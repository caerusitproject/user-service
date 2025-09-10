package com.caerus.userservice.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
@Configuration
public class CamelConfig {

    @Bean
    public ProducerTemplate producerTemplate(CamelContext camelContext) {
        return camelContext.createProducerTemplate();
    }
}