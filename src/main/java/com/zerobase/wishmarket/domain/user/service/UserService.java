package com.zerobase.wishmarket.domain.user.service;


import com.zerobase.wishmarket.common.util.S3Util;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.dto.ChangePwdForm;
import com.zerobase.wishmarket.domain.user.model.dto.UpdateForm;
import com.zerobase.wishmarket.domain.user.model.dto.UserDto;
import com.zerobase.wishmarket.domain.user.model.entity.DeliveryAddress;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserPasswordChangeReturnType;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Util s3Util;
    private static final String PROFILE_IMAGES = "profile_images";

    public UserDto userDetail(Long userId) {
        Optional<UserEntity> userInfo = userRepository.findByUserId(userId);
        if (!userInfo.isPresent()) {
            throw new UserException(USER_NOT_FOUND);
        } else {
            return UserDto.from(userInfo.get());
        }
    }

    public UserPasswordChangeReturnType passwordChange(ChangePwdForm form) {

        String encodePassword = passwordEncoder.encode(form.getPassword());
        Optional<UserEntity> user = userRepository.findByEmailAndUserRegistrationType(form.getEmail(),
                UserRegistrationType.EMAIL);

        if (!user.isPresent()) {
            return UserPasswordChangeReturnType.CHANGE_PASSWORD_FAIL;
        } else {
            UserEntity userInfo = user.get();
            userInfo.setPassword(encodePassword);
            userRepository.save(userInfo);
            return UserPasswordChangeReturnType.CHANGE_PASSWORD_SUCCESS;
        }
    }

    public UserDto userInfoUpdate(Long userId, UpdateForm form) {

        Optional<UserEntity> user = userRepository.findByUserId(userId);
        if (!user.isPresent()) {
            throw new UserException(USER_NOT_FOUND);
        } else {

            DeliveryAddress updateAddress = DeliveryAddress.builder()
                    .baseAddress(form.getBaseAddress())
                    .detailAddress(form.getDetailAddress())
                    .build();

            String imageFileName = "";
            if(form.getProfileImage() != null) {
                imageFileName = s3Util.upload(PROFILE_IMAGES, String.valueOf(userId), form.getProfileImage());
            }

            UserEntity updateUser = UserEntity.builder()
                    .nickName(form.getNickName())
                    .deliveryAddress(updateAddress)
                    .phone(form.getPhone())
                    .profileImage(imageFileName)
                    .build();

            return UserDto.from(userRepository.save(updateUser));
        }
    }
}
