package inhagdsc.mamasteps.user.entity;

import inhagdsc.mamasteps.common.BaseTimeEntity;
import inhagdsc.mamasteps.user.entity.enums.DayOfWeek;
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
    @Column(name = "preference_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek; // 월요일, 화요일 등

    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;
}
