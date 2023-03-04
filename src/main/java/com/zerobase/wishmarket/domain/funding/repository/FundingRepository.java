package com.zerobase.wishmarket.domain.funding.repository;

import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.type.FundingStatusType;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundingRepository extends JpaRepository<Funding, Long> {

    Page<Funding> findAllByTargetUser(UserEntity targetUser, Pageable pageable);

    List<Funding> findAllByTargetUserAndFundingStatusType(UserEntity targetUser, FundingStatusType fundingStatusType);
}
