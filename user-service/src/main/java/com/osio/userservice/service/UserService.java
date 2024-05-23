package com.osio.userservice.service;

import com.osio.userservice.dto.SaveDTO;
import com.osio.userservice.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    // 사용자 회원가입
    User saveUser(SaveDTO request);

    // 사용자 삭제
    void deleteUserByUserId(long id);

    // 사용자 찾기
    Optional<User> findByUserId(long id);

    // 사용자 수정
    User updateUser(User user);

    // 사용자 리스트 조회
    List<User> getAllUser();

}
