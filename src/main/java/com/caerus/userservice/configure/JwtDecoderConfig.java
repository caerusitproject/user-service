package com.caerus.userservice.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

//@Configuration
public class JwtDecoderConfig {


//    @Bean
//    public JwtDecoder jwtDecoder(Environment env) {
//        String jwkSetUri = env.getProperty("spring.security.oauth2.resourceserver.jwt.jwk-set-uri");
//        if (jwkSetUri == null) {
//            throw new IllegalStateException("Missing property: spring.security.oauth2.resourceserver.jwt.jwk-set-uri");
//        }
//        System.out.println(">>> Loaded JWK Set URI = " + jwkSetUri);
//        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
//    }
}