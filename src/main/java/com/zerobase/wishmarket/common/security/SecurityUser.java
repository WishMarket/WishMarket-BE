package com.zerobase.wishmarket.common.security;

import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class SecurityUser extends User {

    public SecurityUser(UserEntity userEntity) {
        super(String.valueOf(userEntity.getUserId()), userEntity.getPassword(),
            AuthorityUtils.createAuthorityList("ROLE_USER"));
    }
}
