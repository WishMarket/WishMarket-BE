package com.zerobase.wishmarket.domain.funding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zerobase.wishmarket.domain.funding.exception.FundingErrorCode;
import com.zerobase.wishmarket.domain.funding.exception.FundingException;
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

    @InjectMocks
    private FundingService fundingService;


    //참여가 잘 성공한 경우
    @DisplayName("펀딩 참여 성공")
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
            .name("상품")
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
        assertEquals(funding.getId(), captor.getValue().getFunding().getId()); // 참여한 펀딩과 기존의 펀딩이 일치하는지 확인
        assertEquals(user.getUserId(), captor.getValue().getUser().getUserId()); // 참여한 펀딩의 유저가 펀딩참여자에 기록된 유저가 맞는지 확인


    }


    //종료된 펀딩인 경우
    @DisplayName("이미 종료된 펀딩이라 펀딩 참여 실패")
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
            .name("상품")
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


    //이미 참여한 펀딩인 경우
    @DisplayName("이미 참여한 펀딩이라 펀딩 참여 실패")
    @Test
    void joinFundingAlreadyParticipationFundingTest() {
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
            .name("상품")
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
            .build();

        FundingParticipation participation = FundingParticipation.builder()
            .id(1L)
            .funding(funding)
            .user(user)
            .price(1000L)
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

        given(fundingParticipationRepository.findByFundingAndUser(funding,user))
            .willReturn(Optional.of(participation));


        //when
        FundingException exception = assertThrows(FundingException.class,
            () -> fundingService.joinFunding(user.getUserId(), fundingJoinInputForm));

        //then
        assertEquals(FundingErrorCode.FUNDING_ALREADY_PARTICIPATION, exception.getErrorCode());

    }

    //참여하는데 포인트가 부족한 경우


    //참여하는데 펀딩 포인트가 상품가격보다 큰 경우
    @DisplayName("펀딩에 참여하는데 펀딩 포인트가 상품 가격보다 큰 경우")
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
            .name("상품")
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


    //참여하는데 내가 준 펀딩금액으로 펀딩이 성공이 된 경우
    @DisplayName("펀딩에 참여하는데 내가 준 펀딩금액으로 펀딩이 성공이 된 경우")
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
            .name("상품")
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




}