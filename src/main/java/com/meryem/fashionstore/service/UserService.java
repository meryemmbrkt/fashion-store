package com.meryem.fashionstore.service;

import com.meryem.fashionstore.dto.*;
import com.meryem.fashionstore.entity.User;
import com.meryem.fashionstore.exception.EmailAlreadyExistsException;
import com.meryem.fashionstore.exception.ResourceNotFoundException;
import com.meryem.fashionstore.mapper.UserMapper;
import com.meryem.fashionstore.repository.UserRepository;
import com.meryem.fashionstore.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final SecurityUtil securityUtil;

    @Transactional(readOnly = true)
    public PagedUserResponse getAllUsers(UserFilterRequest filter) {
        if (filter == null) {
            filter = new UserFilterRequest(null, null, 0, 10);
        }

        Pageable pageable = PageRequest.of(
                filter.page(),
                filter.size(),
                Sort.by("id").ascending()
        );

        Page<User> userPage;

        boolean hasEmail = filter.email() != null && !filter.email().isBlank();
        boolean hasRole = filter.role() != null;

        if (hasEmail && hasRole) {
            userPage = userRepository.findByEmailContainingIgnoreCaseAndRole(
                    filter.email(), filter.role(), pageable);
        } else if (hasEmail) {
            userPage = userRepository.findByEmailContainingIgnoreCase(
                    filter.email(), pageable);
        } else if (hasRole) {
            userPage = userRepository.findByRole(filter.role(), pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        List<UserResponse> users = userPage.getContent().stream()
                .map(userMapper::toResponse)
                .toList();

        return new PagedUserResponse(
                users,
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.getNumber(),
                userPage.getSize()
        );
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

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Kullanıcı bulunamadı: " + email
                ));
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateDto userDto) {
        securityUtil.checkUserAccess(id);

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
        securityUtil.checkUserAccess(id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı!"));
        userRepository.delete(user);
    }
}
