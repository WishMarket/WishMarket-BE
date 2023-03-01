package com.zerobase.wishmarket.domain.funding.service;

import static com.zerobase.wishmarket.domain.funding.exception.FundingErrorCode.CANNOT_BE_RECEIVED_PRODUCT;
import static com.zerobase.wishmarket.domain.funding.exception.FundingErrorCode.FUNDING_NOT_FOUND;
import static com.zerobase.wishmarket.domain.product.exception.ProductErrorCode.PRODUCT_NOT_FOUND;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.zerobase.wishmarket.domain.alarm.service.AlarmService;
import com.zerobase.wishmarket.domain.funding.exception.FundingErrorCode;
import com.zerobase.wishmarket.domain.funding.exception.FundingException;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingDetailResponse;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingJoinResponse;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingListGiveResponse;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingMyGiftListResponse;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingStartResponse;
import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.entity.FundingParticipation;
import com.zerobase.wishmarket.domain.funding.model.entity.OrderEntity;
import com.zerobase.wishmarket.domain.funding.model.form.FundingJoinInputForm;
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
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import com.zerobase.wishmarket.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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

    private final UserAuthRepository userAuthRepository;

    private final PointService pointService;

    private final AlarmService alarmService;

    private final int FUNDING_NAMELISE_SIZE = 20;


    @Transactional
    public FundingStartResponse startFunding(Long userId,
        FundingStartInputForm fundingStartInputForm) {

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

        FundingStatusType fundingStatusType = FundingStatusType.ING;
        FundedStatusType fundedStatusType = FundedStatusType.ING;
        //펀딩 성공 체크 (펀딩 처음 시작하는 사람이 필요한 총 금액을 한번에 다 할수도 있으므로)
        if (userFundedPrice.longValue() == product.getPrice().longValue()) {
            fundingStatusType = FundingStatusType.SUCCESS;
            fundedStatusType = FundedStatusType.BEFORE_RECEIPT;
        }

        Funding savedFunding = fundingRepository.save(Funding.builder()
            .user(user)
            .targetUser(targetUser)
            .product(product)
            .targetPrice(product.getPrice())
            .fundedPrice(fundingStartInputForm.getFundedPrice())
            .fundedPrice(userFundedPrice)
            .participationCount(1L)
            .fundingStatusType(fundingStatusType)
            .fundedStatusType(fundedStatusType)
            .startDate(fundingStartInputForm.getStartDate())
            .endDate(fundingStartInputForm.getEndDate())
            .build());

        FundingParticipation participation = FundingParticipation.builder()
            .funding(savedFunding)
            .user(user)
            .price(userFundedPrice)
            .fundedAt(LocalDateTime.now())
            .build();

        fundingParticipationRepository.save(participation);

        alarmService.addAlarm(targetUser.getUserId(), "당신을 위한 펀딩이 시작되었습니다.");
        if (savedFunding.getFundingStatusType() == FundingStatusType.SUCCESS) {
            alarmService.addFundingAlarm(savedFunding);
        }

        return FundingStartResponse.of(savedFunding);

    }

    //펀딩 참여
    @Transactional
    public FundingJoinResponse joinFunding(Long userId, FundingJoinInputForm fundingJoinInputForm) {

        //유저 확인
        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        //펀딩 확인
        Funding funding = fundingRepository.findById(fundingJoinInputForm.getFundingId())
            .orElseThrow(() -> new FundingException(FundingErrorCode.FUNDING_NOT_FOUND));

        //종료된 펀딩인지 확인
        if ((funding.getFundingStatusType() == FundingStatusType.SUCCESS) | (
            funding.getFundingStatusType() == FundingStatusType.FAIL)) {
            throw new FundingException(FundingErrorCode.FUNDING_ALREADY_END);
        }

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

        //이미 참여한 펀딩일 경우, 값 수정
        Optional<FundingParticipation> checkParticipation = fundingParticipationRepository.findByFundingAndUser(
            funding, user);
        if (checkParticipation.isPresent()) {
            FundingParticipation p = checkParticipation.get();
            p.setPriceUpdate(userFundedPrice);
            p.setFundedAt(fundingJoinInputForm.getFundedAt());

            //펀딩 금액 업데이트
            funding.setFundedPrice(userFundedPrice);

            //펀딩 성공여부 확인
            if (funding.getTargetPrice().longValue() == funding.getFundedPrice().longValue()) {
                funding.setFundingStatusType(FundingStatusType.SUCCESS);
                funding.setFundedStatusType(FundedStatusType.BEFORE_RECEIPT);
                alarmService.addFundingAlarm(funding);
            }

            return FundingJoinResponse.of(funding);

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

        //펀딩 참여자수 업데이트
        funding.participationPlus();

        //펀딩 성공여부 확인
        if (funding.getTargetPrice().longValue() == funding.getFundedPrice().longValue()) {
            funding.setFundingStatusType(FundingStatusType.SUCCESS);
            funding.setFundedStatusType(FundedStatusType.BEFORE_RECEIPT);
            alarmService.addFundingAlarm(funding);
        }

        return FundingJoinResponse.of(funding);
    }


    //펀딩 실패 체크
    @Transactional
    public void checkFundingExpired() {
        List<Funding> fundingList = fundingRepository.findAll();

        //스트림 활용
        if (!fundingList.isEmpty()) {
            fundingList.stream()
                .filter(funding -> funding.getFundingStatusType()
                    == FundingStatusType.ING) //진행중인 펀딩들중에서
                .filter(fundingIng -> fundingIng.getEndDate()
                    .isBefore(LocalDateTime.now())) //그 중에서 펀딩 만료일이 지난 펀딩 객체들만

                .forEach(fundingFail -> {
                    fundingFail.setFundingStatusType(FundingStatusType.FAIL);//펀딩 상태값 '실패'로 변경
                    alarmService.addFundingAlarm(fundingFail);//알림보내기

                    //금액 돌려주기
                    List<FundingParticipation> participationList = fundingFail.getParticipationList();
                    for (FundingParticipation participation : participationList) {
                        pointService.refundPoint(participation.getUser().getUserId(),
                            participation.getPrice());
                    }
                });
        }

    }
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
        OrderEntity order = OrderEntity.builder()
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

        log.info("##만료된 펀딩들을 실패 처리하였습니다.##");
    }


    //펀딩 내역 (내가 친구들한테 주는 펀딩 내역들 - 참여)
    public List<FundingListGiveResponse> getFundingListGive(Long userId) {

        //유저 확인
        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));


        List<FundingParticipation> participationList =
            fundingParticipationRepository.findAllByUser(user);

        List<FundingListGiveResponse> fundingListGiveResponses = new ArrayList<>();

        for (FundingParticipation participation : participationList) {
            List<String> participantsNameList = new ArrayList<>();

            Funding funding = participation.getFunding();

            for (FundingParticipation p : funding.getParticipationList()) {
                //참여자 이름은 20명까지만
                if (participantsNameList.size() <= FUNDING_NAMELISE_SIZE) {
                    participantsNameList.add(p.getUser().getName());
                }
            }

            fundingListGiveResponses.add(
                FundingListGiveResponse.from(participation, funding, participantsNameList));
        }

        return fundingListGiveResponses;
    }


    public List<FundingMyGiftListResponse> getMyFundigGifyList(Long userId) {
        PageRequest pageRequest = PageRequest.of(0, 100);
        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        List<Funding> fundingList = fundingRepository.findAllByTargetUser(user, pageRequest)
            .stream()
            .collect(Collectors.toList());

        List<FundingMyGiftListResponse> fundingMyGiftListResponses = new ArrayList<>();
        for (Funding funding : fundingList) {

            Optional<Review> optionalReview = reviewRepository.findByUserIdAndProductId(
                user.getUserId(),
                funding.getProduct().getProductId());

            if (optionalReview.isPresent()) {
                Review review = optionalReview.get();
                List<String> participationList = funding.getParticipationList()
                    .stream().map(fundingParticipation -> fundingParticipation.getUser().getName())
                    .collect(Collectors.toList());

                fundingMyGiftListResponses.add(
                    FundingMyGiftListResponse.from(funding, review.getComment(), participationList)
                );
            }
        }
        return fundingMyGiftListResponses;
    }


    //펀딩 상세조회
    public FundingDetailResponse getFundingDetail(Long userId, Long fundingId) {

        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        Funding funding = fundingRepository.findById(fundingId)
            .orElseThrow(() -> new FundingException(FUNDING_NOT_FOUND));

        Long userFundedPrice = 0L;

        //유저가 참여한 펀딩이라면 펀딩한 금액 표출, 아니라면 0원 표출
        Optional<FundingParticipation> participation = fundingParticipationRepository.findByFundingAndUser(
            funding, user);
        if (participation.isPresent()) {
            userFundedPrice = participation.get().getPrice();
        }

        //해당 펀딩에 참여한 유저 이름 목록
        List<String> participantsNameList = new ArrayList<>();

        for (FundingParticipation p : funding.getParticipationList()) {
            //참여자 이름은 20명까지만
            if (participantsNameList.size() <= FUNDING_NAMELISE_SIZE) {
                participantsNameList.add(p.getUser().getName());
            }
        }

        return FundingDetailResponse.from(funding, participantsNameList, userFundedPrice);

    }

    //메인 페이지, 인플루언서 타겟 펀딩 조회
    //보여주기위함
    public List<FundingDetailResponse> getFundingMain(Long userId) {

        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        List<FundingDetailResponse> detailResponseList = new ArrayList<>();

        //무작위 인기유저 목록
        List<UserEntity> influenceUserList = userAuthRepository.findAllByInfluenceIsTrueRandom();

        //인기유저가 타겟인 펀딩 목록
        List<Funding> influenceFundingList = new ArrayList<>();

        //인기유저 펀딩이 무조건 존재한다고 가정
        for(UserEntity influenceUser : influenceUserList){
            influenceFundingList.add(fundingRepository.findByTargetUser(influenceUser));
        }

        for(Funding funding : influenceFundingList){

            //유저가 참여한 금액
            Long userFundedPrice = 0L;

            //유저가 참여한 펀딩이라면 펀딩한 금액 표출, 아니라면 0원 표출
            Optional<FundingParticipation> participation = fundingParticipationRepository.findByFundingAndUser(
                funding, user);
            if (participation.isPresent()) {
                userFundedPrice = participation.get().getPrice();
            }


            //해당 펀딩에 참여한 유저 이름 목록
            List<String> participantsNameList = new ArrayList<>();

            for (FundingParticipation p : funding.getParticipationList()) {
                //참여자 이름은 20명까지만
                if (participantsNameList.size() <= FUNDING_NAMELISE_SIZE) {
                    participantsNameList.add(p.getUser().getName());
                }
            }

            detailResponseList.add(FundingDetailResponse.from(funding, participantsNameList, userFundedPrice));
        }

        return detailResponseList;
    }

}
