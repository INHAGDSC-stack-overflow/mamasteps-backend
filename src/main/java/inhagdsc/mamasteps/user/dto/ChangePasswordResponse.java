package inhagdsc.mamasteps.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordResponse {

    private String newPassword;
}
