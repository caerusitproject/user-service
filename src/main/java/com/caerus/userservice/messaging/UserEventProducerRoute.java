package com.caerus.userservice.messaging;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class UserEventProducerRoute extends RouteBuilder {

    @Value("${spring.kafka.topic.user-registered}")
    private String topicName;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBroker;

    @Override
    public void configure() {
        errorHandler(deadLetterChannel("log:dead?level=ERROR").maximumRedeliveries(5).redeliveryDelay(2000));

        from("direct:user-events")
                .routeId("user-events-route")
                .marshal().json()
                .log("Sending message to Kafka: ${body}")
                .to("kafka:" + topicName+ "?brokers=" + kafkaBroker)
                .log("Message published to Kafka topic "+ topicName + ": ${body}");
    }
}
