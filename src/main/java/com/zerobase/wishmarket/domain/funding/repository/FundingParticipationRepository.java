package com.zerobase.wishmarket.domain.funding.repository;

import com.zerobase.wishmarket.domain.funding.model.entity.FundingParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingParticipationRepository extends JpaRepository<FundingParticipation, Long> {

}
