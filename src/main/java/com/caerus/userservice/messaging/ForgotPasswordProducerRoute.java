package com.caerus.userservice.messaging;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ForgotPasswordProducerRoute extends RouteBuilder {

    @Value("${spring.kafka.topic.password-reset}")
   private String forgotPasswordTopic;

    @Value("${spring.kafka.bootstrap-servers}")
   private String kafkaBroker;

    @Override
    public void configure() throws Exception {
        errorHandler(deadLetterChannel("log:dead?level=ERROR").maximumRedeliveries(5).redeliveryDelay(2000));

        from("direct:forgot-password-events")
                .routeId("forgot-password-events-route")
                .marshal().json()
                .log("Sending forgot password event to Kafka: ${body}")
                .to("kafka:" + forgotPasswordTopic+ "?brokers=" + kafkaBroker)
                .log("Forgot password event published to Kafka topic " + forgotPasswordTopic);

    }
}
