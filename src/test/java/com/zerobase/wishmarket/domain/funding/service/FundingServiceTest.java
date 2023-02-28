package com.zerobase.wishmarket.domain.funding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zerobase.wishmarket.domain.alarm.service.AlarmService;
import com.zerobase.wishmarket.domain.funding.exception.FundingErrorCode;
import com.zerobase.wishmarket.domain.funding.exception.FundingException;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingListGiveResponse;
import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.entity.FundingParticipation;
import com.zerobase.wishmarket.domain.funding.model.entity.OrderEntity;
import com.zerobase.wishmarket.domain.funding.model.form.FundingReceptionForm;
import com.zerobase.wishmarket.domain.funding.model.form.FundingStartInputForm;
import com.zerobase.wishmarket.domain.funding.model.type.FundedStatusType;
import com.zerobase.wishmarket.domain.funding.model.type.FundingStatusType;
import com.zerobase.wishmarket.domain.funding.repository.FundingParticipationRepository;
import com.zerobase.wishmarket.domain.funding.repository.FundingRepository;
import com.zerobase.wishmarket.domain.funding.repository.OrderRepository;
import com.zerobase.wishmarket.domain.point.service.PointService;
import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.model.entity.ProductLikes;
import com.zerobase.wishmarket.domain.product.model.entity.Review;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import com.zerobase.wishmarket.domain.product.repository.ReviewRepository;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FundingServiceTest {

    @Mock
    private FundingRepository fundingRepository;

    @Mock
    private FundingParticipationRepository fundingParticipationRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ReviewRepository reviewRepository;



    @Mock
    private PointService pointService;

    @Mock
    private AlarmService alarmService;

    @InjectMocks
    private FundingService fundingService;


    //펀딩 성공
    @DisplayName("펀딩 시작 성공")
    @Test
    void fundingStartTest() {

    }


    //펀딩 금액이 상품 가격보다 많은 경우
    @DisplayName("펀딩 금액이 상품가격보다 높아 실패하는 경우")
    @Test
    void fundingStart_TOO_MUCH_POINT_Test() {

        //given
        UserEntity user = UserEntity.builder()
            .userId(1L)
            .name("user")
            .userStatusType(UserStatusType.ACTIVE)
            .pointPrice(99999L)
            .build();

        UserEntity targetUser = UserEntity.builder()
            .userId(2L)
            .name("targetUser")
            .userStatusType(UserStatusType.ACTIVE)
            .build();

        Product product = Product.builder()
            .productId(999L)
            .name("상품")
            .price(1000L)
            .build();

        given(userRepository.findByUserId(anyLong()))
            .willReturn(Optional.of(user));

        given(productRepository.findById(anyLong()))
            .willReturn(Optional.of(product));

        FundingStartInputForm fundingStartInputForm = FundingStartInputForm.builder()
            .productId(999L)
            .targetId(targetUser.getUserId())
            .fundedPrice(9999L)
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusMonths(1))
            .build();

        // when
        FundingException exception = assertThrows(FundingException.class,
            () -> fundingService.startFunding(user.getUserId(), fundingStartInputForm));

        //then
        assertEquals(FundingErrorCode.FUNDING_TOO_MUCH_POINT, exception.getErrorCode());
    }


    //펀딩 스케줄러를 통해 기간이 만료가 된 펀딩 상태값 테스트

    @DisplayName("펀딩 스케줄러를 통해 기간이 만료가 된 펀딩 상태값 테스트")
    @Test
    void fundingSchedulerTest() {
        //given
        UserEntity user = UserEntity.builder()
            .userId(1L)
            .name("user")
            .userStatusType(UserStatusType.ACTIVE)
            .pointPrice(99999L)
            .build();

        UserEntity targetUser = UserEntity.builder()
            .userId(2L)
            .name("targetUser")
            .userStatusType(UserStatusType.ACTIVE)
            .build();

        Product product = Product.builder()
            .productId(999L)
            .name("상품")
            .price(1000L)
            .build();

        Funding funding = Funding.builder()
            .id(1L)
            .user(user)
            .targetUser(targetUser)
            .product(product)
            .fundedPrice(10L)
            .fundingStatusType(FundingStatusType.ING)
            .fundedStatusType(FundedStatusType.ING)
            .startDate(LocalDateTime.of(2023, 2, 25, 07, 10))
            .endDate(LocalDateTime.now().minusHours(1))
            .build();

        List<Funding> fundingList = new ArrayList<>();
        fundingList.add(funding);

        given(fundingRepository.findAll())
            .willReturn(fundingList);

        // when
        fundingService.checkFundingExpired();

        //then
        assertEquals(FundingStatusType.FAIL, funding.getFundingStatusType());
    }

    @Test
    void success_funding_reception() {
        //given
        FundingReceptionForm form = FundingReceptionForm.builder()
            .fundingId(1L)
            .productId(5L)
            .address("test Address")
            .detailAddress("test DetailAddress")
            .comment("test Comment")
            .isLike(false)
            .build();

        Funding funding = Funding.builder()
            .id(1L)
            .fundedStatusType(FundedStatusType.BEFORE_RECEIPT)
            .build();

        Product product = Product.builder()
            .productId(5L)
            .build();
        given(fundingRepository.findById(anyLong()))
            .willReturn(Optional.of(funding));

        given(productRepository.findById(anyLong()))
            .willReturn(Optional.of(product));

        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(UserEntity.builder()
                .userId(1L)
                .name("test User")
                .build()));

        ProductLikes productLikes = ProductLikes.builder().likes(0).build();

        ArgumentCaptor<Funding> captor1 = ArgumentCaptor.forClass(Funding.class);
        ArgumentCaptor<OrderEntity> captor2 = ArgumentCaptor.forClass(OrderEntity.class);
        ArgumentCaptor<Review> captor3 = ArgumentCaptor.forClass(Review.class);

        //when
        fundingService.receptionFunding(1L, form);

        //then
        verify(fundingRepository, times(1)).save(captor1.capture());
        verify(orderRepository, times(1)).save(captor2.capture());
        verify(reviewRepository, times(1)).save(captor3.capture());

    }

    @Test
    void funding_history_give_test() {
        //given
        UserEntity user1 = UserEntity.builder()
            .userId(1L)
            .name("user1")
            .userStatusType(UserStatusType.ACTIVE)
            .pointPrice(99999L)
            .build();

        UserEntity user2 = UserEntity.builder()
            .userId(2L)
            .name("user2")
            .userStatusType(UserStatusType.ACTIVE)
            .build();

        UserEntity targetUser = UserEntity.builder()
            .userId(3L)
            .name("targetUser")
            .userStatusType(UserStatusType.ACTIVE)
            .build();


        FundingParticipation participation1 = FundingParticipation.builder()
            .user(user1)
            .build();

        FundingParticipation participation2 = FundingParticipation.builder()
            .user(user2)
            .build();

        List<FundingParticipation> participationList = new ArrayList<>();
        participationList.add(participation1);
        participationList.add(participation2);

        Product product = Product.builder()
            .productId(5L)
            .build();

        Funding funding = Funding.builder()
            .user(user1)
            .targetUser(targetUser)
            .product(product)
            .participationList(participationList)
            .build();


        //펀딩 다시 주입
        participation1 = FundingParticipation.builder()
            .funding(funding)
            .user(user1)
            .build();

        participation2 = FundingParticipation.builder()
            .funding(funding)
            .user(user2)
            .build();

        participationList = new ArrayList<>();
        participationList.add(participation1);
        participationList.add(participation2);


        List<String> nameList = new ArrayList<>();
        nameList.add(user1.getName());
        nameList.add(user2.getName());


        given(userRepository.findByUserId(anyLong()))
            .willReturn(Optional.of(user1));

        given(fundingParticipationRepository.findAllByUser(user1))
            .willReturn(participationList);

        //when
        List<FundingListGiveResponse> result = fundingService.getFundingListGive(user1.getUserId());

        //then
        assertNotNull(result.get(0).getParticipants());
        assertEquals(result.get(0).getParticipants(),nameList);
        assertEquals(result.get(0).getProductId(),product.getProductId());

    }


}