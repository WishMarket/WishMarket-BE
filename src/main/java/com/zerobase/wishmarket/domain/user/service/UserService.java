package com.zerobase.wishmarket.domain.user.service;

import com.zerobase.wishmarket.common.component.S3Component;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.dto.UserInfoResponse;
import com.zerobase.wishmarket.domain.user.model.entity.DeliveryAddress;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.form.ChangePwdForm;
import com.zerobase.wishmarket.domain.user.model.form.UpdateForm;
import com.zerobase.wishmarket.domain.user.model.type.UserPasswordChangeReturnType;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.repository.DeliveryAddressRepository;
import com.zerobase.wishmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Component s3Util;
    private final DeliveryAddressRepository deliveryAddressRepository;

    private static final String PROFILE_IMAGES = "profile_images";

    public UserInfoResponse userDetail(Long userId) {
        Optional<UserEntity> userInfo = userRepository.findByUserId(userId);
        if (!userInfo.isPresent()) {
            throw new UserException(USER_NOT_FOUND);
        } else {
            return UserInfoResponse.from(userInfo.get());
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

    public UserInfoResponse updateUserInfo(Long userId, UpdateForm form) {

        Optional<UserEntity> user = userRepository.findByUserId(userId);
        if (!user.isPresent()) {
            throw new UserException(USER_NOT_FOUND);
        } else {

            if (!form.getPhone().isEmpty()) {
                user.get().setPhone(form.getPhone());
            }

            if (!form.getNickName().isEmpty()) {
                user.get().setNickName(form.getNickName());
            }

            if (!form.getAddress().isEmpty()) {
                Optional<DeliveryAddress> deliveryAddress =
                        deliveryAddressRepository.findByUserEntity(user.get());

                if (!deliveryAddress.isPresent()) {
                    DeliveryAddress newDeliveryAddress = DeliveryAddress.builder()
                            .address(form.getAddress())
                            .userEntity(user.get())
                            .build();
                    user.get().setDeliveryAddress(deliveryAddressRepository.save(newDeliveryAddress));
                } else {
                    deliveryAddress.get().setAddress(form.getAddress());
                    deliveryAddress.get().setDetailAddress(deliveryAddress.get().getDetailAddress());
                    DeliveryAddress updateAddress = deliveryAddressRepository.save(deliveryAddress.get());
                    user.get().setDeliveryAddress(updateAddress);
                }

            }

            if (!form.getDetailAddress().isEmpty()) {
                Optional<DeliveryAddress> deliveryAddress =
                        deliveryAddressRepository.findByUserEntity(user.get());

                if (!deliveryAddress.isPresent()) {
                    DeliveryAddress newDeliveryAddress = DeliveryAddress.builder()
                            .detailAddress(form.getDetailAddress())
                            .userEntity(user.get())
                            .build();
                    user.get().setDeliveryAddress(deliveryAddressRepository.save(newDeliveryAddress));
                } else {
                    deliveryAddress.get().setAddress(deliveryAddress.get().getAddress());
                    deliveryAddress.get().setDetailAddress(form.getDetailAddress());
                    DeliveryAddress updateAddress = deliveryAddressRepository.save(deliveryAddress.get());
                    user.get().setDeliveryAddress(updateAddress);
                }

            }

            UserEntity updateUser = userRepository.save(user.get());

            return UserInfoResponse.from(userRepository.save(updateUser));

        }
    }

    public UserInfoResponse updateUserProfileImage(Long userId, MultipartFile profileImage) {

        Optional<UserEntity> user = userRepository.findByUserId(userId);
        if (!user.isPresent()) {
            throw new UserException(USER_NOT_FOUND);
        } else {

            String imageFileName = "";
            System.out.println(profileImage.isEmpty());
            if (!profileImage.isEmpty()) {
                imageFileName = s3Util.upload(PROFILE_IMAGES, String.valueOf(userId), profileImage);
                user.get().setProfileImage(imageFileName);
            }

            UserEntity updateUser = userRepository.save(user.get());

            return UserInfoResponse.from(userRepository.save(updateUser));

        }
    }
}
