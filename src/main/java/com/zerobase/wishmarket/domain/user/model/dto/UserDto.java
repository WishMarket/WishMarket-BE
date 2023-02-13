package com.zerobase.wishmarket.domain.user.model.dto;

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
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private String nickName;
    private String phone;
    private String profileImage;
    private UserRegistrationType userRegistrationType;
    private UserStatusType userStatusType;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
