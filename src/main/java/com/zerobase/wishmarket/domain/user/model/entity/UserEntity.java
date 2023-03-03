package com.zerobase.wishmarket.domain.user.model.entity;

import com.zerobase.wishmarket.domain.follow.model.entity.Follow;
import com.zerobase.wishmarket.domain.follow.model.entity.FollowInfo;
import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.entity.FundingParticipation;
import com.zerobase.wishmarket.domain.user.model.dto.SignUpForm;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserRolesType;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.entity.BaseEntity;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
@Entity
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    private String nickName;

    private String email;

    private String password; // 암호화

    private Long pointPrice;

    private String phone;

    private String profileImage; // 프로필 이미지 경로

    @Enumerated(EnumType.STRING)
    private UserRegistrationType userRegistrationType;

    @Enumerated(EnumType.STRING)
    private UserRolesType userRoleType;

    @Enumerated(EnumType.STRING)
    private UserStatusType userStatusType;

    private boolean influence;

    // 1 : 1 Mapping
    // 주소
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private DeliveryAddress deliveryAddress;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Funding> fundingList = new ArrayList<>();

    @OneToMany(mappedBy = "targetUser", fetch = FetchType.LAZY)
    private List<Funding> fundingTargetList = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(nullable = true)
    private FollowInfo followInfo;

    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Follow> followerList = new ArrayList<>(); //내가 팔로우를 하는 유저들의 리스트

    @OneToMany(mappedBy = "followee", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Follow> followeeList = new ArrayList<>(); //나를 팔로우 하는 유저들의 리스트

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FundingParticipation> participationList = new ArrayList<>();

    // 회원 가입 시 가입 정보 입력
    public static UserEntity of(SignUpForm form, UserRegistrationType userRegistrationType,
                                UserStatusType userStatusType, FollowInfo followInfo) {

        return UserEntity.builder()
                .name(form.getName())
                .email(form.getEmail())
                .nickName(form.getNickName())
                .password(form.getPassword())
                .phone("")
                .profileImage("https://wishmarket-s3.s3.ap-northeast-2.amazonaws.com/profile_images/default-profile-img.png")
                .pointPrice(0L)
                .userRoleType(UserRolesType.USER)
                .userRegistrationType(userRegistrationType)
                .userStatusType(userStatusType)
                .followInfo(followInfo)
                .build();

    }

    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setProfileImage(String profileImageUrl) {
        this.profileImage = profileImageUrl;
    }

    public void setUserStatusType(UserStatusType userStatusType) {
        this.userStatusType = userStatusType;
    }

    public UserEntity update(String name, String profileImage) {
        this.name = name;
        this.profileImage = profileImage;
        return this;
    }

    public void hasFollowed() {
        this.followInfo.followerCountPlus();
    }

    public void hasUnFollowed() {
        this.followInfo.followerCountMinus();
    }

    public void hasFollowing() {
        this.followInfo.followCountPlus();
    }

    public void hasUnFollowing() {
        this.followInfo.followCountMinus();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void increasePointPrice(Long point) {
        this.pointPrice += point;
    }

    public void usePointPrice(Long usePoint) {
        this.pointPrice -= usePoint;
    }

}
