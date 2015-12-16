package com.test.api;

import com.test.api.config.DataSourceConfig;
import com.test.api.data.DataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component
public class HelloBackEndApplication {

    @Autowired
    public DataGenerator dataGenerator;

    public static void main(String[] args) {
        //SpringApplication.run(HelloBackEndApplication.class, args);
        SpringApplication.run(new Class<?>[]{HelloBackEndApplication.class, DataSourceConfig.class}, args);
    }
}
