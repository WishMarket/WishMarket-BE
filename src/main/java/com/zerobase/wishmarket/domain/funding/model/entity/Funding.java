package com.zerobase.wishmarket.domain.funding.model.entity;

import com.zerobase.wishmarket.domain.funding.model.type.FundedStatusType;
import com.zerobase.wishmarket.domain.funding.model.type.FundingStatusType;
import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.common.entity.BaseEntity;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
public class Funding extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "funding_id")
    private Long id;

    // N : 1
    // 펀딩을 시작한 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // N : 1
    // 펀딩을 받는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private UserEntity targetUser;

    // N : 1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "funding", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FundingParticipation> participationList;

    //펀딩에 참여한 사람 수
    private Long participationCount;

    // 목표 상품 가격
    private Long targetPrice;

    // 누적 펀딩된 가격
    private Long fundedPrice;

    // 내가 친구들한테 준 펀딩 상태
    @Enumerated(EnumType.STRING)
    private FundingStatusType fundingStatusType;

    // 받은 펀딩 상태
    @Enumerated(EnumType.STRING)
    private FundedStatusType fundedStatusType;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    public void participationPlus() {
        this.participationCount = this.participationCount + 1;
    }

    // 펀딩 시작유저 탈퇴 시 null 변경
    public void setStartUserWithdrawal(){
        this.user = null;
    }

    // 펀딩 대상유저 탈퇴 시 null 변경
    public void setTargetUserWithdrawal(){
        this.targetUser = null;
    }

    //펀딩된 누적 금액 업데이트
    public void setFundedPrice(Long fundPrice) {
        this.fundedPrice = this.fundedPrice + fundPrice;
    }

    //내가 친구들한테 준 펀딩 상태 set
    public void setFundingStatusType(FundingStatusType fundingStatusType) {
        this.fundingStatusType = fundingStatusType;
    }

    //받은 펀딩 상태 set
    public void setFundedStatusType(FundedStatusType fundedStatusType) {
        this.fundedStatusType = fundedStatusType;
    }


}