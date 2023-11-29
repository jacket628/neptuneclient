package com.amazonaws.demo.controller;

import com.amazonaws.demo.bean.GraphClient;
import com.amazonaws.demo.dto.NeptuneInsertDto;
import com.amazonaws.demo.exception.Result;
import com.amazonaws.util.Md5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

/**
 * 登陆管理
 */
@RestController
@Slf4j
public class TestController {
    @GetMapping("/health")
    public Result healthCheck() {
        return Result.success(null);
    }

    @Autowired
    @Qualifier("writeClient")
    GraphClient writeClient;
    @Autowired
    @Qualifier("readClient")
    GraphClient readClient;

    @GetMapping("/graph/query")
    public Result testQuery() {
        GraphTraversalSource g = traversal().withRemote(readClient.getConnection());

        StringBuilder sb = new StringBuilder();
        try {
            List<Map<Object, Object>> results = g.V().limit(10).valueMap(true).toList();
            for (Map<Object, Object> result : results) {
                //Do nothing
                log.info("query result:{}", result.toString());
                sb.append(result.toString()+" ");
            }
        } catch (Exception e) {
            log.warn("Error processing query: {}", e.getMessage());
        }

        return Result.success(sb.toString());
    }

    @GetMapping("/graph/delete")
    public Result testDelete() {
        GraphTraversalSource g = traversal().withRemote(writeClient.getConnection());

        try {
            g.V().drop().iterate();
            //g.V().has("testNode", "1").drop().iterate();
        } catch (Exception e) {
            log.error("Error processing delete: {}", e.getMessage());
        }

        return Result.success("delete success");
    }

    @GetMapping("/graph/add")
    public Result testAdd() {
        int txCount = 100;
        for (int i = 0; i < txCount; i++) {
            Transaction tx = traversal().withRemote(writeClient.getConnection()).tx();
            GraphTraversalSource g = tx.begin();

            try {
                String id1 = UUID.randomUUID().toString();
                String id2 = UUID.randomUUID().toString();

                g.addV("testNode").property(T.id, id1).iterate();
                g.addV("testNode").property(T.id, id2).iterate();
                g.addE("testEdge").from(__.V(id1)).to(__.V(id2)).iterate();

                tx.commit();
                log.info("Tx complete: {}, id:{}, id2:{}", i, id1, id2);
            } catch (Exception e) {
                log.error("Error processing add: {}", e.getMessage());
                tx.rollback();
            }
        }

        return Result.success("add success");
    }


    @PostMapping(value = "/graph/batchInsert")
    @ResponseBody
    public String batchInsertNeptune(@RequestBody List<NeptuneInsertDto> param) throws Exception {
        batchInsertRecords(param);
        return "成功";
    }

    private void batchInsertRecords(List<NeptuneInsertDto> dtoList) {
        log.info("batchInsertNeptune start....");
        GraphTraversalSource g = traversal().withRemote(writeClient.getConnection());

        GraphTraversal<Vertex, Vertex> addVE = g.V();
        for (NeptuneInsertDto n : dtoList) {
            String fromId =  n.getFrom() + "_" + n.getChain();
            String toId = n.getTo() + "_" + n.getChain() ;

            String edgeName = fromId + toId + n.getChain();
            String edgeId = Md5Utils.md5AsBase64(edgeName.getBytes());

            addVE.V(fromId).fold().coalesce(unfold(),addV(n.getChain()).property(T.id,fromId).property("address", n.getFrom())).as("from1")
                    .map(V(toId).fold().coalesce(unfold(),addV(n.getChain()).property(T.id,toId).property("address", n.getTo())))
                    .coalesce(inE("transfer_" + n.getChain()).where(outV().as("from1")),
                            addE("transfer_" + n.getChain()).from("from1").property(T.id, edgeId)).iterate();;

        }

        log.info("batchInsertNeptune end");
    }



}
