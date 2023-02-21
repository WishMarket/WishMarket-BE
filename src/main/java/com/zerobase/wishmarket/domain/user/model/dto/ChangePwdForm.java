package com.zerobase.wishmarket.domain.user.model.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
public class ChangePwdForm {

    @NotNull
    private String email;

    @NotNull
    private String password;
}
