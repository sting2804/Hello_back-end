package com.test.api;

import com.test.api.config.DataSourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
public class HelloBackEndApplication {

    public static void main(String[] args) {
        //SpringApplication.run(HelloBackEndApplication.class, args);
        SpringApplication.run(new Class<?>[]{HelloBackEndApplication.class, DataSourceConfig.class}, args);
    }
}
