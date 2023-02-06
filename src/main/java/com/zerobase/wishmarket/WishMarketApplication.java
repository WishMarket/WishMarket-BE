package com.zerobase.wishmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
@EnableJpaAuditing
@SpringBootApplication
public class WishMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WishMarketApplication.class, args);
    }

}
