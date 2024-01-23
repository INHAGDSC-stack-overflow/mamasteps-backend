package inhagdsc.mamasteps.auth.controller;

import inhagdsc.mamasteps.auth.dto.LoginReponse;
import inhagdsc.mamasteps.auth.dto.LoginRequest;
import inhagdsc.mamasteps.auth.dto.SignupRequest;
import inhagdsc.mamasteps.auth.dto.SignupResponse;
import inhagdsc.mamasteps.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;

  @PostMapping("/signup")
  public ResponseEntity<SignupResponse> signup(@RequestPart("profileImage") MultipartFile profileImage,
                                               @RequestPart("request") SignupRequest request) {
    log.info("signup 호출 {}", request.getEmail());
    return ResponseEntity.ok(authService.signup(profileImage, request));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginReponse> login(@RequestBody LoginRequest request) {
    log.info("login 호출 {}", request.getEmail());
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    authService.refreshToken(request, response);
  }


}
