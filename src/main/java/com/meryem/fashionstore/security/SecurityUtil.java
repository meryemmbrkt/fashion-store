package com.meryem.fashionstore.security;

import com.meryem.fashionstore.entity.Store;
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

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth.getName() == null
                || auth.getName().equals("anonymousUser")) {
            throw new ResourceNotFoundException("Oturum bulunamadı, lütfen giriş yapın!");
        }

        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + email));
    }

    public void checkUserAccess(Long userId) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("Bu işlem için yetkiniz yok!");
        }
    }

    public void checkStoreOwnership(Store store) {
        User currentUser = getCurrentUser();
        if (!store.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Bu mağaza üzerinde işlem yapma yetkiniz yok!");
        }
    }
}