package com.zerobase.wishmarket.domain.follow.repository;

import com.zerobase.wishmarket.domain.follow.model.entity.FollowInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowInfoRepository extends JpaRepository<FollowInfo, Long> {

}
