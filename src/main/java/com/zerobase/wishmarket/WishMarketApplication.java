package com.zerobase.wishmarket;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableBatchProcessing
@EnableScheduling
@SpringBootApplication
@EnableCaching
public class WishMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WishMarketApplication.class, args);
    }

}
