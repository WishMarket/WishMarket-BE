package com.zerobase.wishmarket.domain.follow.service;

import static com.zerobase.wishmarket.domain.follow.exception.FollowErrorCode.ALREADY_FOLLOWING_USER;
import static com.zerobase.wishmarket.domain.follow.exception.FollowErrorCode.CANNOT_FOLLOW_YOURSELF;
import static com.zerobase.wishmarket.domain.follow.exception.FollowErrorCode.CANNOT_UNFOLLOW_YOURSELF;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.zerobase.wishmarket.domain.follow.exception.FollowException;
import com.zerobase.wishmarket.domain.follow.model.entity.Follow;
import com.zerobase.wishmarket.domain.follow.repository.FollowRepository;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserAuthRepository userAuthRepository;

    @Transactional
    public boolean followUser(Long userId, Long followId) {
        // userId : follow를 하는 사람 (following)
        // followId : follow를 받는 사람 (follower)

        // 자기 자신은 팔로우 불가
        if (Objects.equals(userId, followId)) {
            throw new FollowException(CANNOT_FOLLOW_YOURSELF);
        }

        // 활동 중인 회원만 검색
        UserEntity preyUser = userAuthRepository.findByUserIdAndUserStatusType(followId, UserStatusType.ACTIVE)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        UserEntity hunterUser = userAuthRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        //follow 받는 사람의 팔로워 리스트 검사
        for (Follow follow : preyUser.getFolloweeList()) {
            // 그 리스트에 이미 헌터유저(팔로우 요청 보내는사람)이 있다면 Error
            if (follow.getFollower().getUserId().equals(hunterUser.getUserId())) {
                throw new FollowException(ALREADY_FOLLOWING_USER);
            }
        }

        followRepository.save(Follow.builder()
            .follower(hunterUser)
            .followee(preyUser)
            .build());

        hunterUser.hasFollowing();
        preyUser.hasFollowed();

        return true;
    }

    @Transactional
    public boolean unFollowUser(Long userId, Long followId) {
        // 자기 자신은 팔로우 불가
        if (Objects.equals(userId, followId)) {
            throw new FollowException(CANNOT_UNFOLLOW_YOURSELF);
        }

        // 활동 중인 회원만 검색
        UserEntity preyUser = userAuthRepository.findByUserIdAndUserStatusType(followId, UserStatusType.ACTIVE)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        UserEntity hunterUser = userAuthRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        //follow 받는 사람의 팔로워 리스트 검사
        for (Follow follow : preyUser.getFolloweeList()) {
            // 리스트에 헌터유저(팔로우 요청 보내는사람)이 있다면
            if (follow.getFollower().getUserId().equals(hunterUser.getUserId())) {
                preyUser.getFolloweeList().remove(follow); // 팔로우를 받는 사람 중에서 로그인한 유저 삭제
                hunterUser.getFollowerList().remove(follow);  // 팔로우를 하는 사람 중에서 로그인한 유저 삭제


                hunterUser.hasUnFollowing(); // 로그인 유저가 팔로우하는 사람 수 감소
                preyUser.hasUnFollowed(); // 언팔되는 유저를 팔로우 하는 사람 수 감소

                followRepository.delete(follow);
                return true;
            }
        }

        return false;
    }
}
