package com.martiancitizen.football;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class WebApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(WebApplication.class);
    private static Database DATABASE;
    private static ConfigurableEnvironment appEnv;

    public static void main(String args[]) {
        try {
            loadDatabase();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            System.exit(1);
        }
        ConfigurableApplicationContext context = SpringApplication.run(WebApplication.class, args);
        appEnv = context.getEnvironment();
    }

    public static Database getDatabase() {
        return DATABASE;
    }

    public static void loadDatabase() throws Exception {
        DATABASE = new Database();
    }

    @Configuration
    public static class Config {


    }
}
