package com.zerobase.wishmarket.domain.user.model.form;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class UpdateForm {

    private String nickName;

    private String address;
    private String detailAddress;

    private String phone;
    private MultipartFile profileImage;
}
