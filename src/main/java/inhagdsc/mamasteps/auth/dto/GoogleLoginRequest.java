package inhagdsc.mamasteps.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GoogleLoginRequest {

    private String name;
    private String email;
    private String id;
}
