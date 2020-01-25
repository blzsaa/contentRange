package com.example.contentrange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class ContentRangeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentRangeApplication.class, args);
    }

}
