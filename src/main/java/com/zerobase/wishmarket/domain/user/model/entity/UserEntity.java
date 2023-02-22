package com.zerobase.wishmarket.domain.user.model.entity;

import com.zerobase.wishmarket.domain.follow.model.entity.Follow;
import com.zerobase.wishmarket.domain.follow.model.entity.FollowInfo;
import com.zerobase.wishmarket.domain.user.model.dto.SignUpForm;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserRolesType;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.entity.BaseEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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

    // 1 : 1 Mapping
    // 주소
    @OneToOne(fetch = FetchType.LAZY)
    private DeliveryAddress deliveryAddress;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private FollowInfo followInfo;

    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Follow> followerList = new ArrayList<>(); //내가 팔로우를 하는 유저들의 리스트

    @OneToMany(mappedBy = "followee", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Follow> followeeList = new ArrayList<>(); //나를 팔로우 하는 유저들의 리스트

    // 회원 가입 시 가입 정보 입력
    public static UserEntity of(SignUpForm form, UserRegistrationType userRegistrationType,
        UserStatusType userStatusType, FollowInfo followInfo) {

        return UserEntity.builder()
            .name(form.getName())
            .email(form.getEmail())
            .nickName(form.getNickName())
            .password(form.getPassword())
            .userRegistrationType(userRegistrationType)
            .userStatusType(userStatusType)
            .followInfo(followInfo)
            .build();

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

    public void increasePointPrice() {
        this.pointPrice += 10000L;
    }

    public void usePointPrice(Long usePoint) {
        this.pointPrice -= usePoint;
    }

}
