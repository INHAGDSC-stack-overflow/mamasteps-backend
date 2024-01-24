package inhagdsc.mamasteps.auth.service;

import inhagdsc.mamasteps.auth.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;


public interface AuthService {

    SignupResponse signup(MultipartFile profileImage, SignupRequest request);
    LoginReponse login(LoginRequest request);
    RefreshResponse refreshToken(HttpServletRequest request, HttpServletResponse response) ;
}
