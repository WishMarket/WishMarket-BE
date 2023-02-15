package com.zerobase.wishmarket.domain.follow.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zerobase.wishmarket.domain.follow.exception.FollowErrorCode;
import com.zerobase.wishmarket.domain.follow.exception.FollowException;
import com.zerobase.wishmarket.domain.follow.model.entity.Follow;
import com.zerobase.wishmarket.domain.follow.model.entity.FollowInfo;
import com.zerobase.wishmarket.domain.follow.repository.FollowRepository;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.catalina.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private FollowService followService;


    @DisplayName("팔로우 성공")
    @Test
    void followUser() {

        //given
        UserEntity preyUser = UserEntity.builder()
            .userId(2L)
            .name("preyUser")
            .userStatusType(UserStatusType.ACTIVE)
            .build();

        UserEntity hunterUser = UserEntity.builder()
            .userId(1L)
            .name("hunterUser")
            .build();

        given(userAuthRepository.findByUserIdAndUserStatusType(anyLong(), any()))
            .willReturn(Optional.of(preyUser));

        given(userAuthRepository.findById(anyLong()))
            .willReturn(Optional.of(hunterUser));

        ArgumentCaptor<Follow> captor = ArgumentCaptor.forClass(Follow.class);

        // when
        followService.followUser(1L, 2L);

        //then
        verify(followRepository, times(1)).save(captor.capture());
        assertEquals(preyUser, captor.getValue().getFollowee()); // 팔로우 받은 사람
        assertEquals(hunterUser, captor.getValue().getFollower()); // 팔로우한 사람
    }

    @DisplayName("실패 : 자신을 팔로우하려고 했을 때")
    @Test
    void cannot_follow_yourself() {
        //given
        Long userId = 1L;
        Long followId = 1L;

        //when

        FollowException followException = assertThrows(FollowException.class,
            () -> followService.followUser(userId, followId));

        //then
        assertEquals(FollowErrorCode.CANNOT_FOLLOW_YOURSELF, followException.getErrorCode());
    }

    @Test
    void already_followed_user() {
        //given

        UserEntity preyUser1 = UserEntity.builder()
            .userId(2L)
            .name("preyUser")
            .userStatusType(UserStatusType.ACTIVE)
            .build();

        UserEntity hunterUser1 = UserEntity.builder()
            .userId(1L)
            .name("hunterUser")
            .build();

        Follow follow1 = Follow.builder()
            .follower(hunterUser1)
            .followee(preyUser1)
            .build();

        UserEntity preyUser = UserEntity.builder()
            .userId(2L)
            .name("preyUser")
            .followeeList(Collections.singletonList(follow1))
            .userStatusType(UserStatusType.ACTIVE)
            .build();

        UserEntity hunterUser = UserEntity.builder()
            .userId(1L)
            .name("hunterUser")
            .build();

        given(userAuthRepository.findByUserIdAndUserStatusType(anyLong(), any()))
            .willReturn(Optional.of(preyUser));

        given(userAuthRepository.findById(anyLong()))
            .willReturn(Optional.of(hunterUser));

        //when
        FollowException followException = assertThrows(FollowException.class,
            () -> followService.followUser(hunterUser.getUserId(), preyUser.getUserId()));

        assertEquals(FollowErrorCode.ALREADY_FOLLOWING_USER, followException.getErrorCode());
        //then
    }

    @DisplayName("언팔로우 성공")
    @Test
    void unFollowUser() {
        //given
        UserEntity preyUser = UserEntity.builder()
            .userId(2L)
            .name("preyUser")
            .userStatusType(UserStatusType.ACTIVE)
            .followeeList(generateFolloweeList())
            .followInfo(FollowInfo.builder()
                .followerCount((long) generateFolloweeList().size())
                .followCount(1L)
                .build())
            .build();

        UserEntity hunterUser = UserEntity.builder()
            .userId(1L)
            .name("hunterUser")
            .followerList(generateFollowerList()) //내가 팔로우를 하는 유저들의 리스트
            .followInfo(FollowInfo.builder()
                .followCount((long) generateFollowerList().size())
                .followerCount(1L)
                .build())
            .build();

        given(userAuthRepository.findByUserIdAndUserStatusType(anyLong(), any()))
            .willReturn(Optional.of(preyUser));

        given(userAuthRepository.findById(anyLong()))
            .willReturn(Optional.of(hunterUser));

        ArgumentCaptor<Follow> captor = ArgumentCaptor.forClass(Follow.class);

        // when
        followService.unFollowUser(1L, 2L);

        //then
        assertEquals(generateFolloweeList().size() - 1, preyUser.getFolloweeList().size()); // 팔로우 받은 사람
    }

    public List<Follow> generateFollowerList() {
        List<Follow> followList = new ArrayList<>();

        for (int i = 2; i < 10; i++) {

            followList.add(Follow.builder()
                .followee(UserEntity.builder().userId((long) i).build()) // 팔로우 받는 사람
                .follower(UserEntity.builder().userId(1L).build()) // 팔로우를 하는 사람
                .build());

        }

        return followList;
    }

    public List<Follow> generateFolloweeList() {
        List<Follow> followeeList = new ArrayList<>();

        for (int i = 1; i < 10; i++) {

            followeeList.add(Follow.builder()
                .followee(UserEntity.builder().userId(2L).build()) // 팔로우 받는 사람
                .follower(UserEntity.builder().userId((long) i).build()) // 팔로우를 하는 사람
                .build());

        }

        return followeeList;
    }

    public List<UserEntity> prepareFollowedUserList() {
        List<UserEntity> userList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            userList.add(UserEntity.builder()
                .userId((long) i)
                .name("test" + i)
                .email("test" + i + "@naver.com")
                .userStatusType(UserStatusType.ACTIVE)
                .build());
        }

        return userList;
    }
}