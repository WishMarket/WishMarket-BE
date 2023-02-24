package com.zerobase.wishmarket.domain.funding.service;

import com.zerobase.wishmarket.domain.funding.exception.FundingErrorCode;
import com.zerobase.wishmarket.domain.funding.exception.FundingException;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingJoinResponse;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingStartResponse;
import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.entity.FundingParticipation;
import com.zerobase.wishmarket.domain.funding.model.form.FundingJoinInputForm;
import com.zerobase.wishmarket.domain.funding.model.form.FundingStartInputForm;
import com.zerobase.wishmarket.domain.funding.model.type.FundedStatusType;
import com.zerobase.wishmarket.domain.funding.model.type.FundingStatusType;
import com.zerobase.wishmarket.domain.funding.repository.FundingParticipationRepository;
import com.zerobase.wishmarket.domain.funding.repository.FundingRepository;
import com.zerobase.wishmarket.domain.point.exception.PointErrorCode;
import com.zerobase.wishmarket.domain.point.exception.PointException;
import com.zerobase.wishmarket.domain.point.service.PointService;
import com.zerobase.wishmarket.domain.product.exception.ProductErrorCode;
import com.zerobase.wishmarket.domain.product.exception.ProductException;
import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import com.zerobase.wishmarket.domain.user.exception.UserErrorCode;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundingService {

    private final FundingRepository fundingRepository;

    private final FundingParticipationRepository fundingParticipationRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final PointService pointService;


    public FundingStartResponse startFunding(Long userId,
        FundingStartInputForm fundingStartInputForm) {

        Product product = productRepository.findById(fundingStartInputForm.getProductId())
            .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        UserEntity targetUser = userRepository.findByUserId(fundingStartInputForm.getTargetId())
            .orElseThrow(() -> new FundingException(FundingErrorCode.FUNDING_TARGET_NOT_FOUND));

        //펀딩을 시작하는 유저가 처음에 펀딩하는 금액(포인트)
        Long userFundedPrice = fundingStartInputForm.getFundedPrice();

        //포인트 사용
        try {
            pointService.usePoint(userId, userFundedPrice);
        } catch (Exception e) {
            throw new PointException(PointErrorCode.NOT_ENOUGH_POINT);
        }

        //펀딩하려는 금액(포인트)이 상품 가격보다 많은 경우
        if (userFundedPrice > product.getPrice()) {
            throw new FundingException(FundingErrorCode.FUNDING_TOO_MUCH_POINT);
        }

        FundingStatusType fundingStatusType = FundingStatusType.ING;
        FundedStatusType fundedStatusType = FundedStatusType.ING;
        //펀딩 성공 체크 ( 펀딩 처음 시작하는 사람이 필요한 총 금액을 한번에 다 할수도 있으므로)
        if (userFundedPrice.longValue() == product.getPrice().longValue()) {
            fundingStatusType = FundingStatusType.SUCCESS;
            fundedStatusType = FundedStatusType.BEFORE_RECEIPT;
        }

        Funding funding = Funding.builder()
            .user(user)
            .targetUser(targetUser)
            .product(product)
            .targetPrice(product.getPrice())
            .fundedPrice(fundingStartInputForm.getFundedPrice())
            .fundedPrice(userFundedPrice)
            .fundingStatusType(fundingStatusType)
            .fundedStatusType(fundedStatusType)
            .startDate(fundingStartInputForm.getStartDate())
            .endDate(fundingStartInputForm.getEndDate())
            .build();

        fundingRepository.save(funding);

        FundingParticipation participation = FundingParticipation.builder()
            .funding(funding)
            .user(user)
            .price(userFundedPrice)
            .fundedAt(LocalDateTime.now())
            .build();

        fundingParticipationRepository.save(participation);

        return FundingStartResponse.of(funding);


    }

    //펀딩 참여
    @Transactional
    public FundingJoinResponse joinFunding(Long userId, FundingJoinInputForm fundingJoinInputForm) {

        //유저 확인
        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        //펀딩 확인
        Funding funding = fundingRepository.findById(fundingJoinInputForm.getFundingId())
            .orElseThrow(() -> new FundingException(FundingErrorCode.FUNDING_NOT_FOUND));

        //종료된 펀딩인지 확인
        if ((funding.getFundingStatusType() == FundingStatusType.SUCCESS) | (
            funding.getFundingStatusType() == FundingStatusType.FAIL)) {
            throw new FundingException(FundingErrorCode.FUNDING_ALREADY_END);
        }

        //이미 참여한 펀딩인지 확인
        fundingParticipationRepository.findByFundingAndUser(funding, user)
            .ifPresent(p -> {
                throw new FundingException(FundingErrorCode.FUNDING_ALREADY_PARTICIPATION);
            });

        //펀딩 참여 금액 확인
        Long userFundedPrice = fundingJoinInputForm.getFundedPrice();

        //포인트 사용
        try {
            pointService.usePoint(userId, userFundedPrice);
        } catch (Exception e) {
            throw new PointException(PointErrorCode.NOT_ENOUGH_POINT);
        }

        //펀딩하려는 금액(포인트)이 남은 펀딩 금액보다 많은 경우
        if (userFundedPrice > (funding.getTargetPrice() - funding.getFundedPrice())) {
            throw new FundingException(FundingErrorCode.FUNDING_TOO_MUCH_POINT);
        }

        //펀딩 참여 리스트 추가
        FundingParticipation participation = FundingParticipation.builder()
            .funding(funding)
            .user(user)
            .price(userFundedPrice)
            .fundedAt(fundingJoinInputForm.getFundedAt())
            .build();

        fundingParticipationRepository.save(participation);

        //펀딩 금액 업데이트
        funding.setFundedPrice(userFundedPrice);

        //펀딩 성공여부 확인
        if (funding.getTargetPrice().longValue() == funding.getFundedPrice().longValue()) {
            funding.setFundingStatusType(FundingStatusType.SUCCESS);
            funding.setFundedStatusType(FundedStatusType.BEFORE_RECEIPT);
            //fundingRepository.save(funding);
        }

        return FundingJoinResponse.of(funding);
    }

    //매일 하루 시작시(00시)  펀딩을 기간에 맞춰 체크해줘야 하는 로직도 필요
    //목표 기간이 지난 펀딩들 상태값을 FAIL로 변경해줘야 함

}
