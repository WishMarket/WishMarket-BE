package com.zerobase.wishmarket.domain.user.model.dto;

import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SignUpEmailDto {

    private Long id;
    private String name;
    private String email;
    private String nickName;
    private UserRegistrationType userRegistrationType;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static SignUpEmailDto from(UserEntity userEntity){
        return SignUpEmailDto.builder()
            .id(userEntity.getUserId())
            .name(userEntity.getName())
            .email(userEntity.getEmail())
            .nickName(userEntity.getNickName())
            .userRegistrationType(userEntity.getUserRegistrationType())
            .createdAt(userEntity.getCreatedAt())
            .modifiedAt(userEntity.getModifiedAt())
            .build();
    }
}
