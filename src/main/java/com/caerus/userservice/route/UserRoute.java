package com.caerus.userservice.route;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.caerus.userservice.repository.UserRepository;
import com.caerus.userservice.service.UserProcessor;

@Component
public class UserRoute extends RouteBuilder {
	
	@Value("${spring.kafka.topic.name}")
	private String topicName;
	
	@Value("${spring.kafka.brokers}")
	private String kafkaBroker;
	
	private final UserRepository userRepository;
	
	private final UserProcessor userProcessor;
	
	public UserRoute(UserRepository userRepository, UserProcessor userProcessor) {
		this.userRepository = userRepository;
		this.userProcessor = userProcessor;
	}
	

	@Override
	public void configure() throws Exception {
		from("direct:userSaveAndNotify")
        .routeId("user-save-and-publish-route")
        .log("Received new user: ${body}")
       // .bean(userRepository, "save")   // Save User in DB
        //.log("User saved in DB: ${body}")
        .log("📥 Received user for Kafka publish: ${body}")

        .marshal().json()
        .to("kafka:"+topicName+"?brokers=" +kafkaBroker)
        .log("✅ Message published to Kafka topic "+ topicName + ": ${body}");
	
	}

	
}