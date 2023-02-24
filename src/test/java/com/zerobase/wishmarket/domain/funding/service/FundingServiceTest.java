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
import com.zerobase.wishmarket.domain.funding.model.form.FundingStartInputForm;
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
    private PointService pointService;

    @InjectMocks
    private FundingService fundingService;



    //펀딩 성공
    @DisplayName("펀딩 시작 성공")
    @Test
    void fundingStartTest() {

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

        given(userRepository.findByUserId(anyLong()))
            .willReturn(Optional.of(user));

        given(productRepository.findById(anyLong()))
            .willReturn(Optional.of(product));

        FundingStartInputForm fundingStartInputForm = FundingStartInputForm.builder()
            .productId(999L)
            .targetId(targetUser.getUserId())
            .fundedPrice(1000L)
            .productId(1L)
            .targetId(targetUser.getUserId())
            .fundedPrice(100L)
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusMonths(1))
            .build();

        ArgumentCaptor<Funding> captor = ArgumentCaptor.forClass(Funding.class);

        // when
        fundingService.startFunding(user.getUserId(), fundingStartInputForm);

        //then
        verify(fundingRepository, times(1)).save(captor.capture());
        assertEquals(user, captor.getValue().getUser()); // 펀딩을 시작한 사람
        assertEquals(product, captor.getValue().getProduct()); // 펀딩상품 확인
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


}