package com.example.online.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class LoadingUtils {
    private static final Logger LOG = LoggerFactory.getLogger(LoadingUtils.class);

    public static String loadAsString(String filepath){
        try{
            ClassPathResource resource = new ClassPathResource(filepath);
            try(InputStream is = resource.getInputStream()){
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        catch (Exception e){
            LOG.error("Cannot load file from classpath: {}", filepath, e);
            return null;
        }
    }
}
