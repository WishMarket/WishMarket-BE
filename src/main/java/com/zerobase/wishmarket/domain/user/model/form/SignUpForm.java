package com.zerobase.wishmarket.domain.user.model.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpForm {

    @NotNull
    @Email(message = "올바른 이메일 형식이 아닙니다.")

    private String email;

    @NotNull
    private String name;

    @NotNull
    private String nickName;

    @NotNull
    private String password;

    private String code;
}
