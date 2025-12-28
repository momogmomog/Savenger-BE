package com.momo.savanger.web;

import com.momo.savanger.api.user.OtherUserDto;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.user.UserDto;
import com.momo.savanger.api.user.UserMapper;
import com.momo.savanger.api.user.UserService;
import com.momo.savanger.constants.Endpoints;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping(Endpoints.USER_DETAILS)
    public UserDto getUser(@AuthenticationPrincipal UserDetails userDetails) {
        return this.userMapper.toUserDto((User) userDetails);
    }

    @GetMapping(Endpoints.OTHER_USER_DETAILS)
    public OtherUserDto getOtherUser(@PathVariable String username) {
        return this.userMapper.toOtherUserDto(this.userService.getByUsername(username));
    }
}
