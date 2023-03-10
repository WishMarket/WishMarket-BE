package com.zerobase.wishmarket.domain.funding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zerobase.wishmarket.domain.alarm.service.AlarmService;
import com.zerobase.wishmarket.domain.funding.exception.FundingErrorCode;
import com.zerobase.wishmarket.domain.funding.exception.FundingException;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingJoinResponse;
import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.entity.FundingParticipation;
import com.zerobase.wishmarket.domain.funding.model.form.FundingJoinInputForm;
import com.zerobase.wishmarket.domain.funding.model.type.FundedStatusType;
import com.zerobase.wishmarket.domain.funding.model.type.FundingStatusType;
import com.zerobase.wishmarket.domain.funding.repository.FundingParticipationRepository;
import com.zerobase.wishmarket.domain.funding.repository.FundingRepository;
import com.zerobase.wishmarket.domain.point.service.PointService;
import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FundingJoinTest {

    @Mock
    private FundingRepository fundingRepository;

    @Mock
    private FundingParticipationRepository fundingParticipationRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;


    @Mock
    private PointService pointService;

    @Mock
    private AlarmService alarmService;

    @InjectMocks
    private FundingService fundingService;


    //????????? ??? ????????? ??????
    @DisplayName("?????? ?????? ??????")
    @Test
    void participationSuccessTest() {

        //given
        UserEntity user = UserEntity.builder()
            .userId(1L)
            .name("user")
            .userStatusType(UserStatusType.ACTIVE)
            .pointPrice(1000L)
            .build();

        UserEntity targetUser = UserEntity.builder()
            .userId(2L)
            .name("targetUser")
            .userRegistrationType(UserRegistrationType.EMAIL)
            .build();

        Product product = Product.builder()
            .productId(1L)
            .name("??????")
            .price(100000L)
            .build();


        Funding funding = Funding.builder()
            .id(1L)
            .user(user)
            .targetUser(targetUser)
            .product(product)
            .targetPrice(product.getPrice())
            .fundedPrice(0L)
            .fundingStatusType(FundingStatusType.ING)
            .fundedStatusType(FundedStatusType.ING)
            .participationCount(1L)
            .build();


        FundingJoinInputForm fundingJoinInputForm = FundingJoinInputForm.builder()
            .fundingId(1L)
            .fundedPrice(100L)
            .fundedAt(LocalDateTime.now())
            .build();

        given(userRepository.findByUserId(anyLong()))
            .willReturn(Optional.of(user));

        given(fundingRepository.findById(anyLong()))
            .willReturn(Optional.of(funding));


        ArgumentCaptor<FundingParticipation> captor = ArgumentCaptor.forClass(FundingParticipation.class);


        // when
        fundingService.joinFunding(user.getUserId(),fundingJoinInputForm);

        //then
        verify(fundingParticipationRepository, times(1)).save(captor.capture());
        assertEquals(funding.getId(), captor.getValue().getFunding().getId()); // ????????? ????????? ????????? ????????? ??????????????? ??????
        assertEquals(user.getUserId(), captor.getValue().getUser().getUserId()); // ????????? ????????? ????????? ?????????????????? ????????? ????????? ????????? ??????


    }


    //????????? ????????? ??????
    @DisplayName("?????? ????????? ???????????? ?????? ?????? ??????")
    @Test
    void joinFundingClosedFundingTest() {
        //given
        UserEntity user = UserEntity.builder()
            .userId(1L)
            .name("user")
            .userStatusType(UserStatusType.ACTIVE)
            .pointPrice(1000L)
            .build();

        UserEntity targetUser = UserEntity.builder()
            .userId(2L)
            .name("targetUser")
            .userRegistrationType(UserRegistrationType.EMAIL)
            .build();

        Product product = Product.builder()
            .productId(1L)
            .name("??????")
            .price(100000L)
            .build();


        Funding funding = Funding.builder()
            .id(1L)
            .user(user)
            .targetUser(targetUser)
            .product(product)
            .targetPrice(product.getPrice())
            .fundedPrice(0L)
            .fundingStatusType(FundingStatusType.SUCCESS)
            .fundedStatusType(FundedStatusType.ING)
            .build();


        FundingJoinInputForm fundingJoinInputForm = FundingJoinInputForm.builder()
            .fundingId(1L)
            .fundedPrice(100L)
            .fundedAt(LocalDateTime.now())
            .build();

        given(userRepository.findByUserId(anyLong()))
            .willReturn(Optional.of(user));

        given(fundingRepository.findById(anyLong()))
            .willReturn(Optional.of(funding));


        //when
        FundingException exception = assertThrows(FundingException.class,
            () -> fundingService.joinFunding(user.getUserId(), fundingJoinInputForm));

        //then
        assertEquals(FundingErrorCode.FUNDING_ALREADY_END, exception.getErrorCode());

    }



    //??????????????? ?????? ???????????? ?????????????????? ??? ??????
    @DisplayName("????????? ??????????????? ?????? ???????????? ?????? ???????????? ??? ??????")
    @Test
    void joinFundingTooMuchPointTest(){
        //given
        UserEntity user = UserEntity.builder()
            .userId(1L)
            .name("user")
            .userStatusType(UserStatusType.ACTIVE)
            .pointPrice(1000L)
            .build();

        UserEntity targetUser = UserEntity.builder()
            .userId(2L)
            .name("targetUser")
            .userRegistrationType(UserRegistrationType.EMAIL)
            .build();

        Product product = Product.builder()
            .productId(1L)
            .name("??????")
            .price(100L)
            .build();


        Funding funding = Funding.builder()
            .id(1L)
            .user(user)
            .targetUser(targetUser)
            .product(product)
            .targetPrice(product.getPrice())
            .fundedPrice(0L)
            .fundingStatusType(FundingStatusType.ING)
            .fundedStatusType(FundedStatusType.ING)
            .build();



        FundingJoinInputForm fundingJoinInputForm = FundingJoinInputForm.builder()
            .fundingId(1L)
            .fundedPrice(1000L)
            .fundedAt(LocalDateTime.now())
            .build();

        given(userRepository.findByUserId(anyLong()))
            .willReturn(Optional.of(user));

        given(fundingRepository.findById(anyLong()))
            .willReturn(Optional.of(funding));



        //when
        FundingException exception = assertThrows(FundingException.class,
            () -> fundingService.joinFunding(user.getUserId(), fundingJoinInputForm));

        //then
        assertEquals(FundingErrorCode.FUNDING_TOO_MUCH_POINT, exception.getErrorCode());

    }


    //??????????????? ?????? ??? ?????????????????? ????????? ????????? ??? ??????
    @DisplayName("????????? ??????????????? ?????? ??? ?????????????????? ????????? ????????? ??? ??????")
    @Test
    void joinFundingMyFundingToSuccessFundingTest(){
        //given
        UserEntity user = UserEntity.builder()
            .userId(1L)
            .name("user")
            .userStatusType(UserStatusType.ACTIVE)
            .pointPrice(1000L)
            .build();

        UserEntity targetUser = UserEntity.builder()
            .userId(2L)
            .name("targetUser")
            .userRegistrationType(UserRegistrationType.EMAIL)
            .build();

        Product product = Product.builder()
            .productId(1L)
            .name("??????")
            .price(1000L)
            .build();


        Funding funding = Funding.builder()
            .id(1L)
            .user(user)
            .targetUser(targetUser)
            .product(product)
            .targetPrice(product.getPrice())
            .fundedPrice(0L)
            .fundingStatusType(FundingStatusType.ING)
            .fundedStatusType(FundedStatusType.ING)
            .participationCount(1L)
            .build();



        FundingJoinInputForm fundingJoinInputForm = FundingJoinInputForm.builder()
            .fundingId(1L)
            .fundedPrice(1000L)
            .fundedAt(LocalDateTime.now())
            .build();

        given(userRepository.findByUserId(anyLong()))
            .willReturn(Optional.of(user));

        given(fundingRepository.findById(anyLong()))
            .willReturn(Optional.of(funding));



        //when
        fundingService.joinFunding(user.getUserId(), fundingJoinInputForm);


        //then
        assertEquals(FundingStatusType.SUCCESS, funding.getFundingStatusType());

    }


    @Test
    void funding_join_duplication_test(){

        //given
        UserEntity user1 = UserEntity.builder()
            .userId(1L)
            .name("user1")
            .userStatusType(UserStatusType.ACTIVE)
            .pointPrice(99999L)
            .build();


        UserEntity targetUser = UserEntity.builder()
            .userId(3L)
            .name("targetUser")
            .userStatusType(UserStatusType.ACTIVE)
            .build();


        Product product = Product.builder()
            .productId(5L)
            .build();

        Funding funding = Funding.builder()
            .id(1L)
            .user(user1)
            .targetUser(targetUser)
            .product(product)
            .participationCount(1L)
            .fundedPrice(100L)
            .targetPrice(100000L)
            .fundedPrice(0L)
            .build();


        FundingParticipation participation = FundingParticipation.builder()
            .funding(funding)
            .user(user1)
            .price(100L)
            .fundedAt(LocalDateTime.of(2022,02,22,12,10))
            .build();

        FundingJoinInputForm fundingJoinInputForm = FundingJoinInputForm.builder()
            .fundingId(1L)
            .fundedPrice(100L)
            .fundedAt(LocalDateTime.now())
            .build();


        given(userRepository.findByUserId(anyLong()))
            .willReturn(Optional.of(user1));

        given(fundingRepository.findById(anyLong()))
            .willReturn(Optional.of(funding));

        given(fundingParticipationRepository.findByFundingAndUser(any(),any()))
            .willReturn(Optional.of(participation));


        //when
        FundingJoinResponse result = fundingService.joinFunding(user1.getUserId(),fundingJoinInputForm);

        //then
        assertEquals(participation.getPrice(),funding.getFundedPrice()+fundingJoinInputForm.getFundedPrice());
        assertEquals(result.getParticipationCount(),1);

    }


}