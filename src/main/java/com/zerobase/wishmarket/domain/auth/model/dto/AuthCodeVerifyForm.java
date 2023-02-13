package com.zerobase.wishmarket.domain.auth.model.dto;

import javax.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthCodeVerifyForm {

    private String name;

    @Email
    private String email;

    private String code;
}
