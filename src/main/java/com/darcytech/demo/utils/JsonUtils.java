package com.darcytech.demo.utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtils {

    private static final ObjectMapper om = createObjectMapper();

    public static String toJson(Object obj) {
        try {
            return om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("cannot serialize to json " + obj);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        return fromJson(json, typeReference.getType());
    }

    public static <T> T fromJson(String json, Type type) {
        return fromJson(json, om.constructType(type));
    }

    public static <T> T fromJson(String json, JavaType type) {
        try {
            return om.readValue(json, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot parse json: " + json, e);
        }
    }

    public static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, false)
                .configure(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM, false)
                .registerModule(new JavaTimeModule());
        objectMapper.configOverride(LocalDate.class).setFormat(new JsonFormat.Value().withPattern("yyyy-MM-dd"));
        objectMapper.configOverride(LocalDateTime.class).setFormat(new JsonFormat.Value().withPattern("yyyy-MM-dd HH:mm:ss"));
        objectMapper.configOverride(Date.class).setFormat(new JsonFormat.Value().withPattern("yyyy-MM-dd HH:mm:ss").withTimeZone(TimeZone.getDefault()));
        objectMapper.configOverride(Timestamp.class).setFormat(new JsonFormat.Value().withPattern("yyyy-MM-dd HH:mm:ss").withTimeZone(TimeZone.getDefault()));
        objectMapper.configOverride(java.sql.Date.class).setFormat(new JsonFormat.Value().withPattern("yyyy-MM-dd").withTimeZone(TimeZone.getDefault()));
        objectMapper.enableDefaultTyping();
        return objectMapper;
    }

}