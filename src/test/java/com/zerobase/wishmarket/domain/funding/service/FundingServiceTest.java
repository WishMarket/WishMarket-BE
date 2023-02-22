package com.zerobase.wishmarket.domain.funding.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.form.FundingStartInputForm;
import com.zerobase.wishmarket.domain.funding.repository.FundingRepository;
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
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FundingService fundingService;


    @DisplayName("펀딩 시작 성공")
    @Test
    void fundingStartTest() {

        //given
        UserEntity user = UserEntity.builder()
            .userId(1L)
            .name("user")
            .userStatusType(UserStatusType.ACTIVE)
            .build();

        UserEntity targetUser = UserEntity.builder()
            .userId(2L)
            .name("targetUser")
            .userRegistrationType(UserRegistrationType.EMAIL)
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
            .fundedPrice(1000L)
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


}