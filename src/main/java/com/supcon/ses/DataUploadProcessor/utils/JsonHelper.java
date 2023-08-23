package com.supcon.ses.DataUploadProcessor.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class JsonHelper {

    private final static ObjectMapper mapper = new ObjectMapper();

    public static String writeValue(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static  <T>  T  parseJson(String json,Class<T> cls){
        if (StringUtils.isEmpty(json)){
            return null;
        }
        try {
            return mapper.readValue(json,cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
