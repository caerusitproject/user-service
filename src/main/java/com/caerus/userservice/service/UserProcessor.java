package com.caerus.userservice.service;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.caerus.userservice.model.User;
import com.caerus.userservice.repository.UserRepository;

@Component
public class UserProcessor implements Processor {

    private final UserRepository repository;

    public UserProcessor(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void process(Exchange exchange) {
        User user = exchange.getIn().getBody(User.class);
        User saved = repository.save(user);
        exchange.getMessage().setBody(saved);
    }
}