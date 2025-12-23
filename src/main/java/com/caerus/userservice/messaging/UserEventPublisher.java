package com.caerus.userservice.messaging;

import com.caerus.userservice.dto.UserNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserEventPublisher {

    private final KafkaTemplate<String, UserNotificationDto> kafkaTemplate;

    @Value("${spring.kafka.topic.notification-events}")
    private String topic;

    public void publish(UserNotificationDto event){
        kafkaTemplate.send(topic, event.email().toString(), event)
                .whenComplete((result, ex)->{
                    if(ex != null){
                        log.error("Failed to publish user registration event for email id={}",
                                event.email(), ex);
                    } else {
                        log.info("User registration event published to topic={}, offset={}",
                                topic,
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
