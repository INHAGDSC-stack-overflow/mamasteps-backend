package inhagdsc.mamasteps.user.entity;

import inhagdsc.mamasteps.common.BaseTimeEntity;
import inhagdsc.mamasteps.user.entity.enums.DayOfWeek;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class WalkPreference extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "walk_preference_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private String startTime;

    private String endTime;

    public void setUser(User user) {
        this.user = user;
    }
}
