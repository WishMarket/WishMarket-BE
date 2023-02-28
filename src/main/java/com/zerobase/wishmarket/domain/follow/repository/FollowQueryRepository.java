package com.zerobase.wishmarket.domain.follow.repository;

import static com.zerobase.wishmarket.domain.follow.model.entity.QFollow.follow;
import static com.zerobase.wishmarket.domain.user.model.entity.QUserEntity.userEntity;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.wishmarket.domain.follow.model.dto.UserFollowersResponse;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FollowQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<UserFollowersResponse> findByFollows(Long userId, Pageable pageable) {

        List<UserFollowersResponse> content = getUserFollowers(userId, pageable);
        JPAQuery<Long> countQuery = getCount(userId);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }

    private List<UserFollowersResponse> getUserFollowers(Long userId, Pageable pageable){
        List<UserEntity> userEntityList = jpaQueryFactory.selectFrom(userEntity)
            .leftJoin(userEntity.followeeList, follow)
            .fetchJoin()
            .where(follow.follower.userId.eq(userId))
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        return userEntityList.stream()
            .map(UserFollowersResponse::from)
            .collect(Collectors.toList());
    }

    private JPAQuery<Long> getCount(Long userId){
        return jpaQueryFactory.select(userEntity.count())
            .from(userEntity)
            .leftJoin(userEntity.followeeList, follow)

            .where(follow.follower.userId.eq(userId));
    }
}
