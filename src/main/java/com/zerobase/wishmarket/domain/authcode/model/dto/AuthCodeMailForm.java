package com.zerobase.wishmarket.domain.authcode.model.dto;

import javax.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthCodeMailForm {


    private String name;

    @Email
    private String email;

    private String type;
}
