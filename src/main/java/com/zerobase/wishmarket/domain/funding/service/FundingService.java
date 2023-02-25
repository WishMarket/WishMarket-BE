package com.zerobase.wishmarket.domain.funding.service;

import static com.zerobase.wishmarket.domain.funding.exception.FundingErrorCode.CANNOT_BE_RECEIVED_PRODUCT;
import static com.zerobase.wishmarket.domain.funding.exception.FundingErrorCode.FUNDING_NOT_FOUND;
import static com.zerobase.wishmarket.domain.product.exception.ProductErrorCode.PRODUCT_NOT_FOUND;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.zerobase.wishmarket.domain.funding.exception.FundingErrorCode;
import com.zerobase.wishmarket.domain.funding.exception.FundingException;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingJoinResponse;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingStartResponse;
import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.entity.FundingParticipation;
import com.zerobase.wishmarket.domain.funding.model.entity.Order;
import com.zerobase.wishmarket.domain.funding.model.form.FundingReceptionForm;
import com.zerobase.wishmarket.domain.funding.model.form.FundingStartInputForm;
import com.zerobase.wishmarket.domain.funding.model.type.FundedStatusType;
import com.zerobase.wishmarket.domain.funding.model.type.FundingStatusType;
import com.zerobase.wishmarket.domain.funding.repository.FundingParticipationRepository;
import com.zerobase.wishmarket.domain.funding.repository.FundingRepository;
import com.zerobase.wishmarket.domain.funding.repository.OrderRepository;
import com.zerobase.wishmarket.domain.point.exception.PointErrorCode;
import com.zerobase.wishmarket.domain.point.exception.PointException;
import com.zerobase.wishmarket.domain.point.service.PointService;
import com.zerobase.wishmarket.domain.product.exception.ProductException;
import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.model.entity.Review;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import com.zerobase.wishmarket.domain.product.repository.ReviewRepository;
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

    private final ReviewRepository reviewRepository;

    private final OrderRepository orderRepository;

    private final PointService pointService;


    public FundingStartResponse startFunding(Long userId, FundingStartInputForm fundingStartInputForm) {

        Product product = productRepository.findById(fundingStartInputForm.getProductId())
            .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

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

        Funding funding = Funding.builder()
            .user(user)
            .targetUser(targetUser)
            .product(product)
            .targetPrice(product.getPrice())
            .fundedPrice(fundingStartInputForm.getFundedPrice())
            .fundedPrice(userFundedPrice)
            .fundingStatusType(FundingStatusType.ING)
            .fundedStatusType(FundedStatusType.BEFORE_RECEIPT)
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

        //펀딩 성공 체크

        return FundingStartResponse.of(funding);


    }

    //펀딩 참여

    public FundingJoinResponse joinFunding(Long userId, FundingStartInputForm fundingStartInputForm) {

        //유저 확인

        //펀딩 확인

        //참여, 똑같이 금액 확인
        //여기서 금액체크는 남은 펀딩금액이 최대, 그 금액보다 낮게끔 설정

        //펀딩 참여 리스트 추가

        //펀딩 성공여부 확인

        return null;

        //펀딩 성공여부 확인

        //펀딩이 성공이면(금액이 다 차면)
        //펀딩 스테이터스값을 변경 후,

        //그 외 로직 처리, (알람 등)

    }

    //펀딩 성공여부 확인

    //펀딩이 성공이면(금액이 다 차면)
    //펀딩 스테이터스값을 변경 후,

    //그 외 로직 처리, (알람 등)


    @Transactional
    public void receptionFunding(Long userId, FundingReceptionForm form) {

        // 펀딩 Id
        Funding funding = fundingRepository.findById(form.getFundingId())
            .orElseThrow(() -> new FundingException(FUNDING_NOT_FOUND));

        if (funding.getFundedStatusType() != FundedStatusType.BEFORE_RECEIPT) {
            throw new FundingException(CANNOT_BE_RECEIVED_PRODUCT);
        }

        Product product = productRepository.findById(form.getProductId())
            .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

        // 유저 정보 확인
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // 배송 정보 등록
        Order order = Order.builder()
            .fundingId(funding.getId())
            .productId(product.getProductId())
            .address(form.getAddress())
            .userId(user.getUserId())
            .detailAddress(form.getDetailAddress())
            .build();

        // 리뷰 작성
        Review review = Review.builder()
            .productId(product.getProductId())
            .userId(user.getUserId())
            .userName(user.getName())
            .comment(form.getComment())
            .isRecommend(form.getIsLike())
            .build();

        // 좋아요 개수 추가
        if (form.getIsLike()) {
            product.plusProductLikes();
        }

        funding.setFundedStatusType(FundedStatusType.COMPLETION);

        fundingRepository.save(funding);
        orderRepository.save(order);
        reviewRepository.save(review);

    }

}
