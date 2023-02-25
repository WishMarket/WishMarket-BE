package com.zerobase.wishmarket.domain.funding.repository;

import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.entity.FundingParticipation;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingParticipationRepository extends JpaRepository<FundingParticipation, Long> {

    Optional<FundingParticipation> findByFundingAndUser(Funding funding, UserEntity user);

}
