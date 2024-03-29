package inhagdsc.mamasteps.user.entity;

import inhagdsc.mamasteps.common.BaseTimeEntity;
import java.time.DayOfWeek;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;


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

    private LocalTime startTime;

    private LocalTime endTime;

    public void setUser(User user) {
        this.user = user;
    }
}
