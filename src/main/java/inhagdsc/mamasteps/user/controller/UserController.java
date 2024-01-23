package inhagdsc.mamasteps.user.controller;

import inhagdsc.mamasteps.user.dto.UserResponse;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getMyInfo(@AuthenticationPrincipal User user) {
        return userService.getUserInfo(user.getId());
    }
}
