package com.example;

import com.example.websocket.SpringContextHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Description: 主程序入口
 */
@SpringBootApplication
public class ImApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImApplication.class, args);

    }

    @Bean
    public SpringContextHelper getSpringContextHelperInstance(){
        return new SpringContextHelper();
    }
}
