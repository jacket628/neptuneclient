package com.amazonaws.demo.exception;

/**
 * @author ：z7
 * @project_name ：protocol-transition-system
 * @class_name：ResultEnum
 * @descriptive ：TODO 获取枚举key、value键值对信息
 * @create_date ：2022/2/14 10:14
 */
public interface IEnum {

    /**
     * 获取枚举的key
     */
    Integer getKey();

    /**
     * 获取枚举的下标
     */
    String getValue();

    /**
     * 将参数反序列化为枚举
     *
     * @param param 当前值
     * @param clazz 枚举类型
     */
    static <T extends Enum<T> & IEnum> T parse(Integer param, Class<T> clazz) {
        if (param == null || clazz == null) {
            return null;
        }
        T[] enums = clazz.getEnumConstants();
        for (T t : enums) {
            Integer key = t.getKey();
            if (key.equals(param)) {
                return t;
            }
        }
        return null;
    }
}
