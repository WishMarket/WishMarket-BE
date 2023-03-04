package com.zerobase.wishmarket.domain.user.model.form;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangePwdForm {

    @NotNull
    private String email;

    @NotNull
    private String password;
}
