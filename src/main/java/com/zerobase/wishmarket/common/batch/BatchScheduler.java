package com.zerobase.wishmarket.common.batch;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;

    private final BatchJob batchJob;

    //베스트 상품, 매일 자정 2시 업데이트
    //스케줄러 시간 잠시 수정
    @Scheduled(cron = "0 30 22 * * *")
    public void updateBest() {
        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(confMap);

        try {
            log.debug("===== 베스트 상품 업데이트 ====");
            jobLauncher.run(batchJob.JobToUpdateBestProduct(), jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException |
                 org.springframework.batch.core.repository.JobRestartException e) {
            log.error(e.getMessage());
        }


    }

    //1시간마다 실행
    @Scheduled(cron = "0 0 0/1 * * *")
    public void checkFundingFail() {
        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(confMap);

        try {
            log.info("===== 기간이 만료된 펀딩 체크 스케줄러 시작====");
            jobLauncher.run(batchJob.JobToCheckFunding(), jobParameters);
            log.info("===== 기간이 만료된 펀딩 체크 스케줄러 종료====");
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException |
                 org.springframework.batch.core.repository.JobRestartException e) {
            log.error(e.getMessage());
        }


    }


}
