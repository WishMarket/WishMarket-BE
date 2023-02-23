package com.zerobase.wishmarket.domain.funding.repository;

import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingRepository extends JpaRepository<Funding, Long> {

}
