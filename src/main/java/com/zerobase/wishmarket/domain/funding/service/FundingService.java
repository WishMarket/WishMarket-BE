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
import com.zerobase.wishmarket.domain.funding.model.dto.FundingListFriendResponse;
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
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import com.zerobase.wishmarket.domain.review.model.entity.Review;
import com.zerobase.wishmarket.domain.review.repository.ReviewRepository;
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

    private final int FUNDING_NAMELIST_SIZE = 20;


    @Transactional
    public FundingStartResponse startFunding(Long userId,
        FundingStartInputForm fundingStartInputForm) {

        Product product = productRepository.findById(fundingStartInputForm.getProductId())
            .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        UserEntity targetUser = userRepository.findByUserId(fundingStartInputForm.getTargetId())
            .orElseThrow(() -> new FundingException(FundingErrorCode.FUNDING_TARGET_NOT_FOUND));

        //????????? ???????????? ????????? ????????? ???????????? ??????(?????????)
        Long userFundedPrice = fundingStartInputForm.getFundedPrice();

        //????????? ??????
        try {
            pointService.usePoint(userId, userFundedPrice);
        } catch (Exception e) {
            throw new PointException(PointErrorCode.NOT_ENOUGH_POINT);
        }

        //??????????????? ??????(?????????)??? ?????? ???????????? ?????? ??????
        if (userFundedPrice > product.getPrice()) {
            throw new FundingException(FundingErrorCode.FUNDING_TOO_MUCH_POINT);
        }

        FundingStatusType fundingStatusType = FundingStatusType.ING;
        FundedStatusType fundedStatusType = FundedStatusType.ING;
        //?????? ?????? ?????? (?????? ?????? ???????????? ????????? ????????? ??? ????????? ????????? ??? ????????? ????????????)
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

        if (savedFunding.getFundingStatusType() == FundingStatusType.SUCCESS) {
            alarmService.addFundingAlarm(savedFunding);
        }

        return FundingStartResponse.of(savedFunding);

    }

    //?????? ??????
    @Transactional
    public FundingJoinResponse joinFunding(Long userId, FundingJoinInputForm fundingJoinInputForm) {

        //?????? ??????
        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        //?????? ??????
        Funding funding = fundingRepository.findById(fundingJoinInputForm.getFundingId())
            .orElseThrow(() -> new FundingException(FundingErrorCode.FUNDING_NOT_FOUND));

        //????????? ???????????? ??????
        if ((funding.getFundingStatusType() == FundingStatusType.SUCCESS) | (
            funding.getFundingStatusType() == FundingStatusType.FAIL)) {
            throw new FundingException(FundingErrorCode.FUNDING_ALREADY_END);
        }

        //?????? ?????? ?????? ??????
        Long userFundedPrice = fundingJoinInputForm.getFundedPrice();

        //????????? ??????
        try {
            pointService.usePoint(userId, userFundedPrice);
        } catch (Exception e) {
            throw new PointException(PointErrorCode.NOT_ENOUGH_POINT);
        }

        //??????????????? ??????(?????????)??? ?????? ?????? ???????????? ?????? ??????
        if (userFundedPrice > (funding.getTargetPrice() - funding.getFundedPrice())) {
            throw new FundingException(FundingErrorCode.FUNDING_TOO_MUCH_POINT);
        }

        //?????? ????????? ????????? ??????, ??? ??????
        Optional<FundingParticipation> checkParticipation = fundingParticipationRepository.findByFundingAndUser(
            funding, user);
        if (checkParticipation.isPresent()) {
            FundingParticipation p = checkParticipation.get();
            p.setPriceUpdate(userFundedPrice);
            p.setFundedAt(fundingJoinInputForm.getFundedAt());

            //?????? ?????? ????????????
            funding.setFundedPrice(userFundedPrice);

            //?????? ???????????? ??????
            if (funding.getTargetPrice().longValue() == funding.getFundedPrice().longValue()) {
                funding.setFundingStatusType(FundingStatusType.SUCCESS);
                funding.setFundedStatusType(FundedStatusType.BEFORE_RECEIPT);
                alarmService.addFundingAlarm(funding);
            }

            return FundingJoinResponse.of(funding);

        }

        //?????? ?????? ????????? ??????
        FundingParticipation participation = FundingParticipation.builder()
            .funding(funding)
            .user(user)
            .price(userFundedPrice)
            .fundedAt(fundingJoinInputForm.getFundedAt())
            .build();

        fundingParticipationRepository.save(participation);

        //?????? ?????? ????????????
        funding.setFundedPrice(userFundedPrice);

        //?????? ???????????? ????????????
        funding.participationPlus();

        //?????? ???????????? ??????
        if (funding.getTargetPrice().longValue() == funding.getFundedPrice().longValue()) {
            funding.setFundingStatusType(FundingStatusType.SUCCESS);
            funding.setFundedStatusType(FundedStatusType.BEFORE_RECEIPT);
            alarmService.addFundingAlarm(funding);
        }

        return FundingJoinResponse.of(funding);
    }


    //?????? ?????? ??????
    @Transactional
    public void checkFundingExpired() {
        List<Funding> fundingList = fundingRepository.findAll();

        //????????? ??????
        if (!fundingList.isEmpty()) {
            fundingList.stream()
                .filter(funding -> funding.getFundingStatusType()
                    == FundingStatusType.ING) //???????????? ??????????????????
                .filter(fundingIng -> fundingIng.getEndDate()
                    .isBefore(LocalDateTime.now())) //??? ????????? ?????? ???????????? ?????? ?????? ????????????

                .forEach(fundingFail -> {
                    fundingFail.setFundingStatusType(FundingStatusType.FAIL);//?????? ????????? '??????'??? ??????
                    alarmService.addFundingAlarm(fundingFail);//???????????????

                    //?????? ????????????
                    List<FundingParticipation> participationList = fundingFail.getParticipationList();
                    for (FundingParticipation participation : participationList) {
                        pointService.refundPoint(participation.getUser().getUserId(),
                            participation.getPrice());
                    }
                });
        }

    }
    //??? ??? ?????? ??????, (?????? ???)


    @Transactional
    public void receptionFunding(Long userId, FundingReceptionForm form) {

        // ?????? Id
        Funding funding = fundingRepository.findById(form.getFundingId())
            .orElseThrow(() -> new FundingException(FUNDING_NOT_FOUND));

        if (funding.getFundedStatusType() != FundedStatusType.BEFORE_RECEIPT) {
            throw new FundingException(CANNOT_BE_RECEIVED_PRODUCT);
        }

        Product product = productRepository.findById(form.getProductId())
            .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

        // ?????? ?????? ??????
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // ?????? ?????? ??????
        OrderEntity order = OrderEntity.builder()
            .fundingId(funding.getId())
            .productId(product.getProductId())
            .address(form.getAddress())
            .userId(user.getUserId())
            .detailAddress(form.getDetailAddress())
            .build();

        // ?????? ??????
        Review review = Review.builder()
            .productId(product.getProductId())
            .userId(user.getUserId())
            .userName(user.getName())
            .comment(form.getComment())
            .isRecommend(form.getIsLike())
            .fundingId(form.getFundingId()) //????????? ??????????????? ??????
            .build();

        // ????????? ?????? ??????
        if (form.getIsLike()) {
            product.plusProductLikes();
        }

        funding.setFundedStatusType(FundedStatusType.COMPLETION);

        fundingRepository.save(funding);
        orderRepository.save(order);
        reviewRepository.save(review);

        log.info("##????????? ???????????? ?????? ?????????????????????.##");
    }


    //?????? ?????? (?????? ??????????????? ?????? ?????? ????????? - ??????)
    public List<FundingListGiveResponse> getFundingListGive(Long userId) {

        //?????? ??????
        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        List<FundingParticipation> participationList =
            fundingParticipationRepository.findAllByUser(user);

        List<FundingListGiveResponse> fundingListGiveResponses = new ArrayList<>();

        for (FundingParticipation participation : participationList) {
            List<String> participantsNameList = new ArrayList<>();

            Funding funding = participation.getFunding();

            for (FundingParticipation p : funding.getParticipationList()) {
                //????????? ????????? 20????????????
                if (participantsNameList.size() <= FUNDING_NAMELIST_SIZE) {
                    participantsNameList.add(p.getUser().getName());
                }
            }

            fundingListGiveResponses.add(
                FundingListGiveResponse.from(participation, funding, participantsNameList));
        }

        return fundingListGiveResponses;
    }

    //??????(?????? ????????? ????????????) ?????? ??????
    public List<FundingListFriendResponse> getFundingListFriend(Long userId, Long friendId) {
        //?????? ??????
        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        //??????(?????? ??????) ??????
        UserEntity friend = userRepository.findByUserId(friendId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        //????????? ????????? ?????? ?????? ?????? ??????
        List<FundingParticipation> participationList =
            fundingParticipationRepository.findAllByUser(friend);

        List<FundingListFriendResponse> fundingListFriendResponses = new ArrayList<>();

        for (FundingParticipation participation : participationList) {
            List<String> participantsNameList = new ArrayList<>();

            Funding funding = participation.getFunding();

            //???????????? ????????? ??????
            if (funding.getFundingStatusType() != FundingStatusType.ING) {
                continue;
            }

            for (FundingParticipation p : funding.getParticipationList()) {
                //????????? ????????? 20????????????
                if (participantsNameList.size() <= FUNDING_NAMELIST_SIZE) {
                    participantsNameList.add(p.getUser().getName());
                }
            }

            //????????? ????????? ????????? ?????? ????????? ??????, ?????? ??????
            Long myFundingPrice = 0L;

            Optional<FundingParticipation> myParticipation = fundingParticipationRepository.findByFundingAndUser(
                funding, user);
            if (myParticipation.isPresent()) {
                myFundingPrice = myParticipation.get().getPrice();
            }

            fundingListFriendResponses.add(
                FundingListFriendResponse.from(participation, funding, myFundingPrice,
                    participantsNameList));
        }

        return fundingListFriendResponses;

    }

    //??????(?????? ????????? ?????????) ?????? ??????
    public List<FundingListFriendResponse> getTargetFundingListFriend(Long userId, Long friendId) {

        //?????? ??????
        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        //??????(?????? ??????) ??????
        UserEntity friend = userRepository.findByUserId(friendId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        List<FundingListFriendResponse> fundingListFriendResponses = new ArrayList<>();

        //????????? ????????? ?????? ?????? ??????
        List<Funding> fundingList = fundingRepository.findAllByTargetUserAndFundingStatusType(
            friend, FundingStatusType.ING);

        for (Funding funding : fundingList) {
            List<String> participantsNameList = new ArrayList<>();

            for (FundingParticipation participation : funding.getParticipationList()) {
                //????????? ????????? 20????????????
                if (participantsNameList.size() <= FUNDING_NAMELIST_SIZE) {
                    participantsNameList.add(participation.getUser().getName());
                }
            }

            //????????? ????????? ????????? ?????? ????????? ??????, ?????? ??????
            Long myFundingPrice = 0L;

            Optional<FundingParticipation> myParticipation = fundingParticipationRepository.findByFundingAndUser(
                funding, user);

            if (myParticipation.isPresent()) {
                myFundingPrice = myParticipation.get().getPrice();
            }

            fundingListFriendResponses.add(
                FundingListFriendResponse.from(funding, myFundingPrice,
                    participantsNameList));


        }

        return fundingListFriendResponses;

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

            Optional<Review> optionalReview = reviewRepository.findByUserIdAndFundingId(
                user.getUserId(),
                funding.getId()); ///fundingId??????

            List<String> participationList = funding.getParticipationList().stream()
                .map(fundingParticipation -> fundingParticipation.getUser().getName())
                .collect(Collectors.toList());

            // ????????? ???????????? completion
            if (optionalReview.isPresent()) {
                Review review = optionalReview.get();

                fundingMyGiftListResponses.add(
                    FundingMyGiftListResponse.from(funding, review.getComment(), participationList)
                );
            } else {
                fundingMyGiftListResponses.add(
                    FundingMyGiftListResponse.from(funding, "", participationList)
                );
            }
        }
        return fundingMyGiftListResponses;
    }


    //?????? ????????????
    public FundingDetailResponse getFundingDetail(Long userId, Long fundingId) {

        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        Funding funding = fundingRepository.findById(fundingId)
            .orElseThrow(() -> new FundingException(FUNDING_NOT_FOUND));

        Long userFundedPrice = 0L;

        //????????? ????????? ??????????????? ????????? ?????? ??????, ???????????? 0??? ??????
        Optional<FundingParticipation> participation = fundingParticipationRepository.findByFundingAndUser(
            funding, user);
        if (participation.isPresent()) {
            userFundedPrice = participation.get().getPrice();
        }

        //?????? ????????? ????????? ?????? ?????? ??????
        List<String> participantsNameList = new ArrayList<>();

        for (FundingParticipation p : funding.getParticipationList()) {
            //????????? ????????? 20????????????
            if (participantsNameList.size() <= FUNDING_NAMELIST_SIZE) {
                participantsNameList.add(p.getUser().getName());
            }
        }

        return FundingDetailResponse.from(funding, participantsNameList, userFundedPrice);

    }

    //???????????? ??????
    //?????? ?????????, ??????????????? ?????? ?????? ??????,
    //??????????????????
    public List<FundingDetailResponse> getFundingMain(Long userId) {

        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        List<FundingDetailResponse> detailResponseList = new ArrayList<>();

        //????????? ???????????? 11??? ??????
        List<UserEntity> influenceUserList = userAuthRepository.findAllByInfluenceIsTrueRandomEleven();

        //??????????????? ????????? ?????? ??????
        List<Funding> influenceFundingList = new ArrayList<>();

        //???????????? ????????? ????????? ??????????????? ??????
        for (UserEntity influenceUser : influenceUserList) {
            //??????????????? ???????????? ?????? ?????? ??? ????????? ??????
            List<Funding> influenceUserFundingList = fundingRepository.findAllByTargetUserAndFundingStatusType(
                influenceUser, FundingStatusType.ING);
            int ranNum = (int) (Math.random() * (influenceUserFundingList.size()));

            influenceFundingList.add(influenceUserFundingList.get(ranNum));
        }

        for (Funding funding : influenceFundingList) {

            //????????? ????????? ??????
            Long userFundedPrice = 0L;

            //????????? ????????? ??????????????? ????????? ?????? ??????, ???????????? 0??? ??????
            Optional<FundingParticipation> participation = fundingParticipationRepository.findByFundingAndUser(
                funding, user);
            if (participation.isPresent()) {
                userFundedPrice = participation.get().getPrice();
            }

            //?????? ????????? ????????? ?????? ?????? ??????
            List<String> participantsNameList = new ArrayList<>();

            for (FundingParticipation p : funding.getParticipationList()) {
                //????????? ????????? 20????????????
                if (participantsNameList.size() <= FUNDING_NAMELIST_SIZE) {
                    participantsNameList.add(p.getUser().getName());
                }
            }

            detailResponseList.add(
                FundingDetailResponse.from(funding, participantsNameList, userFundedPrice));
        }

        return detailResponseList;
    }

    //??????????????? ?????? ??????
    //?????? ?????????, ??????????????? ?????? ?????? ??????,
    //??????????????????
    public List<FundingDetailResponse> getFundingMain() {

        List<FundingDetailResponse> detailResponseList = new ArrayList<>();

        //????????? ???????????? 11??? ??????
        List<UserEntity> influenceUserList = userAuthRepository.findAllByInfluenceIsTrueRandomEleven();

        //??????????????? ????????? ?????? ??????
        List<Funding> influenceFundingList = new ArrayList<>();

        //???????????? ????????? ????????? ??????????????? ??????
        for (UserEntity influenceUser : influenceUserList) {

            //??????????????? ???????????? ?????? ?????? ??? ????????? ??????
            List<Funding> influenceUserFundingList = fundingRepository.findAllByTargetUserAndFundingStatusType(
                influenceUser, FundingStatusType.ING);
            int ranNum = (int) (Math.random() * (influenceUserFundingList.size()));

            influenceFundingList.add(influenceUserFundingList.get(ranNum));
        }

        for (Funding funding : influenceFundingList) {

            //?????? ????????? ????????? ?????? ?????? ??????
            List<String> participantsNameList = new ArrayList<>();

            for (FundingParticipation p : funding.getParticipationList()) {
                //????????? ????????? 20????????????
                if (participantsNameList.size() <= FUNDING_NAMELIST_SIZE) {
                    participantsNameList.add(p.getUser().getName());
                }
            }

            detailResponseList.add(FundingDetailResponse.from(funding, participantsNameList, 0L));
        }

        return detailResponseList;
    }


}
