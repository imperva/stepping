package com.imperva.stepping;

// TODO remove this class and use org.springframework.util.StringUtils instead (already have the dependency)
public class StringUtils {
    public static boolean isEmpty(String s) {
        return s == null || "".equals(s);
    }
}
