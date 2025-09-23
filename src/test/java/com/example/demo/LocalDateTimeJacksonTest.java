package com.example.demo;

import com.example.demo.model.OperationLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class LocalDateTimeJacksonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deserializes space separated pattern yyyy-MM-dd HH:mm:ss")
    void deserializeSpaceSeparated() throws Exception {
        String json = "{\"time\":\"2025-09-17 11:23:44\"}";
        OperationLog log = objectMapper.readValue(json, OperationLog.class);
        assertThat(log.getTime()).isEqualTo(LocalDateTime.of(2025, 9, 17, 11, 23, 44));
    }

    @Test
    @DisplayName("Deserializes ISO_LOCAL_DATE_TIME pattern with 'T'")
    void deserializeIsoT() throws Exception {
        String json = "{\"time\":\"2025-09-17T11:23:44\"}";
        OperationLog log = objectMapper.readValue(json, OperationLog.class);
        assertThat(log.getTime()).isEqualTo(LocalDateTime.of(2025, 9, 17, 11, 23, 44));
    }

    @Test
    @DisplayName("Deserializes date-only yyyy-MM-dd to start of day")
    void deserializeDateOnly() throws Exception {
        String json = "{\"time\":\"2025-09-17\"}";
        OperationLog log = objectMapper.readValue(json, OperationLog.class);
        assertThat(log.getTime()).isEqualTo(LocalDateTime.of(2025, 9, 17, 0, 0, 0));
    }
}

