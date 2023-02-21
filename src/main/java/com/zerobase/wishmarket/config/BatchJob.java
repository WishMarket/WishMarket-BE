package com.zerobase.wishmarket.config;

import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import com.zerobase.wishmarket.domain.product.service.ProductService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class BatchJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ProductService productService;
    private final ProductRepository productRepository;

    //서버 처음 구동시, 상품 데이터 넣기
    @Bean
    public Job JobToInputProduct() {
        return jobBuilderFactory.get("JobToInputProduct")
            .start(InputProductStep())
            .on("FAILED")
            .end()
            .from(InputProductStep())
            .on("*")
            .end()
            .end()
            .build();
    }

    @Bean
    public Step InputProductStep() {
        return stepBuilderFactory.get("InputProductStep")
            .tasklet((contribution, chunkContext) -> {
                log.debug("===== 상품 데이터 저장 ====");
                Optional<Product> product = productRepository.findById(1L);
                if (product.isPresent()) {  //상품 데이터가 이미 존재한다면 실패
                    contribution.setExitStatus(ExitStatus.FAILED);
                }

                productService.addProduct();
                return RepeatStatus.FINISHED;
            })
            .build();
    }


    //Best 상품 초기화
    @Bean
    public Job JobToUpdateBestProduct() {
        return jobBuilderFactory.get("JobToUpdateBestProduct")
            .start(UpdateBestProductStep())
            .on("FAILED")
            .end()
            .from(UpdateBestProductStep())
            .on("*")
            .end()
            .end()
            .build();
    }

    @Bean
    public Step UpdateBestProductStep() {
        return stepBuilderFactory.get("UpdateBestProductStep")
            .tasklet((contribution, chunkContext) -> {
                log.debug("===== 베스트 상품 업데이트 ====");
                productService.updateBestProducts();
                return RepeatStatus.FINISHED;
            })
            .build();
    }


}
