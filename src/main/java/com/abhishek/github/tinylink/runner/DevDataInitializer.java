package com.abhishek.github.tinylink.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!prod")
@Component
public class DevDataInitializer implements CommandLineRunner {

    @Autowired
    DevDataInitializer() {}

    @Override
    public void run(String... args) {
        // Nothing to do for now
    }

}
