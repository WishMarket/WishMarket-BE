package com.zerobase.wishmarket.domain.funding.service;

import com.zerobase.wishmarket.domain.funding.exception.FundingErrorCode;
import com.zerobase.wishmarket.domain.funding.exception.FundingException;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingStartDto;
import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.form.FundingStartInputForm;
import com.zerobase.wishmarket.domain.funding.model.type.FundedStatusType;
import com.zerobase.wishmarket.domain.funding.model.type.FundingStatusType;
import com.zerobase.wishmarket.domain.funding.repository.FundingRepository;
import com.zerobase.wishmarket.domain.product.exception.ProductErrorCode;
import com.zerobase.wishmarket.domain.product.exception.ProductException;
import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import com.zerobase.wishmarket.domain.user.exception.UserErrorCode;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundingService {

    private final FundingRepository fundingRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    public FundingStartDto startFunding(Long userId, FundingStartInputForm fundingStartInputForm) {

        Product product = productRepository.findById(fundingStartInputForm.getProductId())
            .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        UserEntity targetUser = userRepository.findByUserId(fundingStartInputForm.getTargetId())
            .orElseThrow(() -> new FundingException(FundingErrorCode.FUNDING_TARGET_NOT_FOUND));

        Funding funding = Funding.builder()
            .user(user)
            .targetUser(targetUser)
            .product(product)
            .targetPrice(product.getPrice())
            .fundedPrice(fundingStartInputForm.getFundedPrice())
            .fundingStatusType(FundingStatusType.ING)
            .fundedStatusType(FundedStatusType.BEFORE_RECEIPT)
            .startDate(fundingStartInputForm.getStartDate())
            .endDate(fundingStartInputForm.getEndDate())
            .build();

        fundingRepository.save(funding);

        return FundingStartDto.of(funding);


    }

}
