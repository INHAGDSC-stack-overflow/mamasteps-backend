package inhagdsc.mamasteps.user.controller;

import inhagdsc.mamasteps.common.ApiResponse;
import inhagdsc.mamasteps.user.dto.ChangePasswordRequest;
import inhagdsc.mamasteps.user.dto.ChangePasswordResponse;
import inhagdsc.mamasteps.user.dto.UserResponse;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo(@AuthenticationPrincipal User user) { //로그인 시 유저 정보 가져옴
        return ApiResponse.onSuccess(userService.getUserInfo(user.getId())); //따라서 아이디만 넘긴 다음 서비스에서 디비 다시 조회함
    }

    @PatchMapping("/password")
    public ApiResponse<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest request, @AuthenticationPrincipal User user) {
        return ApiResponse.onSuccess(userService.changePassword(request, user));
    }
}
