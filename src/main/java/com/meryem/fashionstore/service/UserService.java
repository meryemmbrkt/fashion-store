package com.meryem.fashionstore.service;

import com.meryem.fashionstore.dto.UserRequest;
import com.meryem.fashionstore.dto.UserResponse;
import com.meryem.fashionstore.dto.UserUpdateDto;
import com.meryem.fashionstore.entity.Role;
import com.meryem.fashionstore.entity.User;
import com.meryem.fashionstore.exception.EmailAlreadyExistsException;
import com.meryem.fashionstore.exception.ResourceNotFoundException;
import com.meryem.fashionstore.mapper.UserMapper;
import com.meryem.fashionstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse saveUser(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(
                    "Bu e-posta adresi ile daha önce kayıt olunmuş!"
            );
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.CUSTOMER);

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Kullanıcı bulunamadı: " + email
                ));
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı!"));

        if (userDto.getFirstName() != null) {
            user.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            user.setLastName(userDto.getLastName());
        }
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı!"));
        userRepository.delete(user);
    }
}