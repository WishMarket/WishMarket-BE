package com.zerobase.wishmarket.domain.follow.service;

import static com.zerobase.wishmarket.domain.follow.exception.FollowErrorCode.ALREADY_FOLLOWING_USER;
import static com.zerobase.wishmarket.domain.follow.exception.FollowErrorCode.CANNOT_FOLLOW_YOURSELF;
import static com.zerobase.wishmarket.domain.follow.exception.FollowErrorCode.CANNOT_UNFOLLOW_YOURSELF;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.zerobase.wishmarket.domain.follow.exception.FollowException;
import com.zerobase.wishmarket.domain.follow.model.dto.UserFollowersResponse;
import com.zerobase.wishmarket.domain.follow.model.dto.UserSearchResponse;
import com.zerobase.wishmarket.domain.follow.model.entity.Follow;
import com.zerobase.wishmarket.domain.follow.repository.FollowQueryRepository;
import com.zerobase.wishmarket.domain.follow.repository.FollowRepository;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.dto.InfluencerResponse;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserAuthRepository userAuthRepository;
    private final FollowQueryRepository followQueryRepository;


    @Transactional
    public boolean followUser(Long userId, Long followId) {
        // userId : follow??? ?????? ?????? (following)
        // followId : follow??? ?????? ?????? (follower)

        // ?????? ????????? ????????? ??????
        if (Objects.equals(userId, followId)) {
            throw new FollowException(CANNOT_FOLLOW_YOURSELF);
        }

        // ?????? ?????? ????????? ??????
        UserEntity preyUser = userAuthRepository.findByUserIdAndUserStatusType(followId, UserStatusType.ACTIVE)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        UserEntity hunterUser = userAuthRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        //follow ?????? ????????? ????????? ????????? ??????
        for (Follow follow : preyUser.getFolloweeList()) {
            // ??? ???????????? ?????? ????????????(????????? ?????? ???????????????)??? ????????? Error
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
        // ?????? ????????? ????????? ??????
        if (Objects.equals(userId, followId)) {
            throw new FollowException(CANNOT_UNFOLLOW_YOURSELF);
        }

        // ?????? ?????? ????????? ??????
        UserEntity preyUser = userAuthRepository.findByUserIdAndUserStatusType(followId, UserStatusType.ACTIVE)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        UserEntity hunterUser = userAuthRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        //followId ??? ?????? ????????? ?????? ??????
        for (Follow follow : preyUser.getFolloweeList()) {
            // ???????????? ????????????(????????? ?????? ???????????????)??? ?????????
            if (follow.getFollower().getUserId().equals(hunterUser.getUserId())) {
                preyUser.getFolloweeList().remove(follow); // ???????????? ?????? ?????? ????????? ???????????? ?????? ??????
                hunterUser.getFollowerList().remove(follow);  // ???????????? ?????? ?????? ????????? ???????????? ?????? ??????

                hunterUser.hasUnFollowing(); // ????????? ????????? ??????????????? ?????? ??? ??????
                preyUser.hasUnFollowed(); // ???????????? ????????? ????????? ?????? ?????? ??? ??????

                followRepository.delete(follow);
                return true;
            }
        }

        return false;
    }

    // https://bcp0109.tistory.com/304
    // 1 + N ?????? https://incheol-jung.gitbook.io/docs/q-and-a/spring/n+1
    public Page<UserFollowersResponse> getMyFollowerList(Long userId, Pageable pageable) {
        // ?????? ???????????? ?????? ID
        Page<UserFollowersResponse> byFollows = followQueryRepository.findByFollows(userId, pageable);
        System.out.println(byFollows.getTotalPages());

        System.out.println("??? ?????? : " + byFollows.getTotalElements());
        System.out.println(byFollows.getSize());
        if(byFollows.getTotalElements() % byFollows.getSize() != 0){
            System.out.println("????????? ????????? ?????????");
        }
        return followQueryRepository.findByFollows(userId, pageable);
    }

    // ??????, ???????????? ????????? ??????
    // ??????, ???????????? ????????? ?????? => ???????????? ??????
    public List<UserSearchResponse> searchUser(Long userId, String keyword, String type) {
        Pageable limit = PageRequest.of(0, 100);

        keyword = keyword.trim();

        UserEntity loginUser = userAuthRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // ?????? ???????????? ?????? ID
        List<Long> myFriendList = loginUser.getFollowerList().stream()
            .map(follow -> follow.getFollowee().getUserId())
            .collect(Collectors.toList());

        switch (type.toLowerCase()) {
            case "email":
                log.info("????????? ?????? : " + keyword);

                return userAuthRepository.findByEmailContainsIgnoreCase(keyword, limit)
                    .stream()
                    .filter(userEntity -> !Objects.equals(userEntity.getUserId(), userId))
                    .map(userEntity -> UserSearchResponse.of(userEntity, myFriendList.contains(userEntity.getUserId())))
                    .collect(Collectors.toList());

            case "name":
                log.info("?????? ?????? : " + keyword);

                return userAuthRepository.findByNameContainsIgnoreCase(keyword, limit)
                    .stream()
                    .filter(userEntity -> !Objects.equals(userEntity.getUserId(), userId))
                    .map(userEntity -> UserSearchResponse.of(userEntity, myFriendList.contains(userEntity.getUserId())))
                    .collect(Collectors.toList());

            case "nickname":
                log.info("????????? ?????? : " + keyword);

                return userAuthRepository.findByNickNameContainsIgnoreCase(keyword, limit)
                    .stream()
                    .filter(userEntity -> !Objects.equals(userEntity.getUserId(), userId))
                    .map(userEntity -> UserSearchResponse.of(userEntity, myFriendList.contains(userEntity.getUserId())))
                    .collect(Collectors.toList());

        }
        return null;
    }

    public List<InfluencerResponse> getInfluencerList(Long userId){

        UserEntity loginUser = userAuthRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // ?????? ???????????? ?????? ID
        List<Long> myFriendList = loginUser.getFollowerList().stream()
            .map(follow -> follow.getFollowee().getUserId())
            .collect(Collectors.toList());


        return userAuthRepository.findAllByInfluenceIsTrueRandom().stream()
            .filter(userEntity -> !Objects.equals(userEntity.getUserId(), userId))
            .map(userEntity -> InfluencerResponse.of(userEntity, myFriendList.contains(userEntity.getUserId())))
            .collect(Collectors.toList());
    }
}
