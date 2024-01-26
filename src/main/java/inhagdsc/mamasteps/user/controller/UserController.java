package inhagdsc.mamasteps.user.controller;

import inhagdsc.mamasteps.common.ApiResponse;
import inhagdsc.mamasteps.common.code.status.SuccessStatus;
import inhagdsc.mamasteps.user.dto.*;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static inhagdsc.mamasteps.common.code.status.SuccessStatus.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo(@AuthenticationPrincipal User user) { //로그인 시 유저 정보 가져옴
        return ApiResponse.onSuccess(OK, userService.getUserInfo(user.getId())); //따라서 아이디만 넘긴 다음 서비스에서 디비 다시 조회
    }

    @PatchMapping("/me")
    public ApiResponse<UserResponse> updateMyInfo(@RequestBody UserUpdateRequest request, @AuthenticationPrincipal User user) {
        return ApiResponse.onSuccess(OK, userService.updateUserInfo(user.getId(), request));
    }

    @PatchMapping("/password")
    public ApiResponse<ChangePasswordResponse> changePassword(@AuthenticationPrincipal User user, @RequestBody ChangePasswordRequest request) {
        return ApiResponse.onSuccess(OK, userService.changePassword(user.getId(), request));
    }

    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ChangeProfileResponse> updateProfile(@AuthenticationPrincipal User user, @RequestPart("profileImage") MultipartFile profileImage) {
        return ApiResponse.onSuccess(OK, userService.updateProfile(user.getId(), profileImage));
    }
}
