/* package com.meryem.fashionstore.mapper;

import com.meryem.fashionstore.dto.UserRequest;
import com.meryem.fashionstore.dto.UserResponse;
import com.meryem.fashionstore.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setRole(request.role());
        return user;
    }



    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }
} */

package com.meryem.fashionstore.mapper;

import com.meryem.fashionstore.dto.UserRequest;
import com.meryem.fashionstore.dto.UserResponse;
import com.meryem.fashionstore.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {


    UserResponse toResponse(User user);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "basket", ignore = true)
    User toEntity(UserRequest request);
}


