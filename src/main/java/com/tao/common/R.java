package com.tao.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

//返回结果类

@Data
public class R<T> {

    private Integer code;   //编码
    private String msg;     //信息
    private  T data;        //数据
    private Map map = new HashMap();    //动态数据

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R<T> r = new R<T>();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key,Object value){
        this.map.put(key,value);
        return this;
    }

}
