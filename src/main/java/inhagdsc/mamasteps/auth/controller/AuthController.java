package inhagdsc.mamasteps.auth.controller;

import inhagdsc.mamasteps.auth.dto.*;
import inhagdsc.mamasteps.auth.service.AuthService;
import inhagdsc.mamasteps.common.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


import static inhagdsc.mamasteps.common.code.status.SuccessStatus.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;

  @PostMapping("/signup")
  public ApiResponse<SignupResponse> signup(@RequestBody SignupRequest request) {
    log.info("signup 호출 {}", request.getEmail());
    return ApiResponse.onSuccess(CREATED, authService.signup(request));
  }

  @PostMapping("/login")
  public ApiResponse<LoginReponse> login(@RequestBody LoginRequest request) {
    log.info("login 호출 {}", request.getEmail());
    return ApiResponse.onSuccess(OK, authService.login(request));
  }

  @PostMapping("/google-login")
  public ApiResponse<GoogleLoginResponse> googleLogin(@RequestBody GoogleLoginRequest request) {
    return ApiResponse.onSuccess(OK, authService.googleLogin(request));
  }

  @GetMapping("/health")
  public ApiResponse<String> health() {
    return ApiResponse.onSuccess(OK, "I'm healthy!");
  }



}
