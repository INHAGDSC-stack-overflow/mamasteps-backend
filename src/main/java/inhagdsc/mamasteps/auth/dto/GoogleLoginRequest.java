package inhagdsc.mamasteps.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GoogleLoginRequest {

    @NotNull
    private String name;

    @NotNull
    @Email
    private String email;
    @NotNull
    private String id;
}
