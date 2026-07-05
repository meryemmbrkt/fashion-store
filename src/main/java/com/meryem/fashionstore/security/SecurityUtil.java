package com.meryem.fashionstore.security;

import com.meryem.fashionstore.entity.User;
import com.meryem.fashionstore.exception.ResourceNotFoundException;
import com.meryem.fashionstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    /** Login olmuş kullanıcıyı döner */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + email));
    }

    /** URL'deki userId, login olan kullanıcıya ait değilse hata fırlatır */
    public void checkUserAccess(Long userId) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("Bu işlem için yetkiniz yok!");
        }
    }
}