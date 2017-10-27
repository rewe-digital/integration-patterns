package com.rewedigital.examples.msintegration.productinformation.infrastructure;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ObjectMapperConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final DateFormat df = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ssz");
        objectMapper.setDateFormat(df);
        return objectMapper;
    }
}
