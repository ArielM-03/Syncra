package com.syncra.syncra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.syncra.syncra.model.User;

@Getter
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String userEmail;
    private String displayName;

    public static UserDTO from(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getUserEmail(),
                user.getDisplayName()
        );
    }
}