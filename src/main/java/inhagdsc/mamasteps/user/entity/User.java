package inhagdsc.mamasteps.user.entity;

import inhagdsc.mamasteps.common.BaseTimeEntity;
import inhagdsc.mamasteps.user.entity.enums.ActivityLevel;
import inhagdsc.mamasteps.user.entity.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Table(name = "users")
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String email; //이메일(아이디)
    private String password; // 비밀번호
    private String name; // 이름
    private Integer age; // 나이
    private LocalDateTime pregnancyStartDate; // 임신 시작일
    private String guardianPhoneNumber; // 보호자 전화번호
    private String profileImageUrl; // 프로필 이미지
    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel; // 활동량


    @Enumerated(EnumType.STRING)
    private Role role;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return role.getAuthorities();
        }


        @Override
        public String getUsername() {
            return email;
        }
        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    public void updateInfo(String name, Integer age, LocalDateTime pregnancyStartDate, ActivityLevel activityLevel) {
        this.name = name;
        this.age = age;
        this.pregnancyStartDate = pregnancyStartDate;
        this.activityLevel = activityLevel;
    }
}

