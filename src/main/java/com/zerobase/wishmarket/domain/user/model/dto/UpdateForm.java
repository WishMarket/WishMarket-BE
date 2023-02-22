package com.zerobase.wishmarket.domain.user.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateForm {

    private String nickName;

    private String baseAddress;
    private String detailAddress;

    private String phone;
    private String profileImageUrl;
}
