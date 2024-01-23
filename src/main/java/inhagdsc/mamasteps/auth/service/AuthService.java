package inhagdsc.mamasteps.auth.service;

import inhagdsc.mamasteps.auth.dto.LoginReponse;
import inhagdsc.mamasteps.auth.dto.LoginRequest;
import inhagdsc.mamasteps.auth.dto.SignupRequest;
import inhagdsc.mamasteps.auth.dto.SignupResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthService {

    public SignupResponse signup(SignupRequest req);
    public LoginReponse login(LoginRequest req);
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
