package com.osio.userservice.service;

import com.osio.userservice.dto.SaveDTO;
import com.osio.userservice.entity.User;
import com.osio.userservice.repository.UserJpaRepository;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userJpaRepository;

    @Autowired
    public UserServiceImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User saveUser(SaveDTO request) {
        Optional<User> existingUser = userJpaRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            // 이미 존재하는 이메일 주소인 경우 null 반환
            return null;
        } else {
            // 비밀번호를 Bcrypt로 암호화
            String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
            var user = User.builder()
                    .email(request.getEmail())
                    .password(hashedPassword) // Bcrypt로 암호화된 비밀번호 저장
                    .name(request.getName())
                    .phone(request.getPhone())
                    .address(request.getAddress())
                    .build();
            return userJpaRepository.save(user); // 저장된 사용자 반환
        }
    }


@Override
    @Transactional
    public void deleteUserByUserId(long id) {
    userJpaRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByUserId(long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    @Transactional
    public List<User> getAllUser() {
        return userJpaRepository.findAll();
    }

}
