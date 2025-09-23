package com.example.demo.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class JacksonConfig {
    // Primary output pattern
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
    private static final DateTimeFormatter SPACE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Supported input formatters (order matters: most specific first)
    private static final DateTimeFormatter[] SUPPORTED_INPUT_FORMATTERS = new DateTimeFormatter[]{
            SPACE_FORMATTER,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME, // 2025-09-17T11:23:44
            DATE_ONLY_FORMATTER // 2025-09-17
    };

    // Custom deserializer that tries multiple patterns including date-only
    private static final JsonDeserializer<LocalDateTime> MULTI_FORMAT_LOCAL_DATE_TIME_DESERIALIZER = new JsonDeserializer<>() {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getText();
            if (text == null) return null;
            String value = text.trim();
            if (value.isEmpty()) return null;
            for (DateTimeFormatter f : SUPPORTED_INPUT_FORMATTERS) {
                try {
                    if (f == DATE_ONLY_FORMATTER) {
                        LocalDate d = LocalDate.parse(value, f);
                        return d.atStartOfDay();
                    }
                    return LocalDateTime.parse(value, f);
                } catch (DateTimeParseException ignored) {
                }
            }
            throw ctxt.weirdStringException(value, LocalDateTime.class, "Unable to parse LocalDateTime with supported patterns");
        }
    };

    @Bean
    public JavaTimeModule javaTimeModule() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(OUTPUT_FORMATTER));
        module.addDeserializer(LocalDateTime.class, MULTI_FORMAT_LOCAL_DATE_TIME_DESERIALIZER);
        return module;
    }

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder(JavaTimeModule javaTimeModule) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.modules(javaTimeModule);
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        builder.simpleDateFormat(DATETIME_PATTERN); // For legacy java.util.Date if any
        return builder;
    }
}
