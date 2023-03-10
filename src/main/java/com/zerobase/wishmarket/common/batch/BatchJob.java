package com.zerobase.wishmarket.common.batch;

import com.zerobase.wishmarket.domain.funding.service.FundingService;
import com.zerobase.wishmarket.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final FundingService fundingService;

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

    //기간이 만료된 펀딩 체크
    @Bean
    public Job JobToCheckFunding() {
        return jobBuilderFactory.get("JobToCheckFunding")
            .start(CheckFundingStep())
            .on("FAILED")
            .end()
            .from(CheckFundingStep())
            .on("*")
            .end()
            .end()
            .build();
    }

    @Bean
    public Step CheckFundingStep() {
        return stepBuilderFactory.get("CheckFundingStep")
            .tasklet((contribution, chunkContext) -> {
                log.info("===== 기간이 만료된 펀딩 체크 배치 시작====");
                fundingService.checkFundingExpired();
                log.info("===== 기간이 만료된 펀딩 체크 배치 종료====");
                return RepeatStatus.FINISHED;
            })
            .build();
    }


}
