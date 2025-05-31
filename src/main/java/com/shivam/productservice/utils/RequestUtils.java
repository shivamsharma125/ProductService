package com.shivam.productservice.utils;

public class RequestUtils {
    public static boolean isInvalidId(Long id){
        return id == null || id <= 0;
    }

    public static boolean isEmptyParam(String param){
        return param == null || param.isBlank();
    }

    public static boolean isNull(Object obj){
        return obj == null;
    }

    public static boolean isNotNull(Object obj){
        return obj != null;
    }
}
