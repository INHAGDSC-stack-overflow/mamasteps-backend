package inhagdsc.mamasteps.auth.service;

import inhagdsc.mamasteps.auth.dto.LoginReponse;
import inhagdsc.mamasteps.auth.dto.LoginRequest;
import inhagdsc.mamasteps.auth.dto.SignupRequest;
import inhagdsc.mamasteps.auth.dto.SignupResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AuthService {

    public SignupResponse signup(MultipartFile profileImage, SignupRequest request);
    public LoginReponse login(LoginRequest request);
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
