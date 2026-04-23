package com.bili.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bili.api.mapper")
public class BiliApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BiliApiApplication.class, args);
    }
}
