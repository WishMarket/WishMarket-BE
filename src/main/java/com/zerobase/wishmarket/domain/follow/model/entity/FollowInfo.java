package com.zerobase.wishmarket.domain.follow.model.entity;

import com.zerobase.wishmarket.common.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;

@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
@Entity
public class FollowInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long followerCount; // 날 팔로우 하는 사람 수

    @Column(nullable = false)
    private Long followCount; // 내가 팔로우 하는 사람 수


    public void followerCountPlus() {
        this.followerCount += 1L;
    }

    public void followCountPlus() {
        this.followCount += 1L;
    }

    public void followerCountMinus() {
        this.followerCount -= 1L;
    }

    public void followCountMinus() {
        this.followCount -= 1L;
    }

}
