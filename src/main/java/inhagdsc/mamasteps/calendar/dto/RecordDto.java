package inhagdsc.mamasteps.calendar.dto;

import inhagdsc.mamasteps.calendar.domain.RecordEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecordDto {
    private Long id;
    private Long routeId;
    private LocalDateTime date;
    private int completedTimeSeconds;
    private String createdAt;
    private String updatedAt;

    public RecordEntity toEntity() {
        RecordEntity recordEntity = new RecordEntity();
        recordEntity.setId(id);
        recordEntity.setStartAt(date);
        recordEntity.setCompletedTimeSeconds(completedTimeSeconds);
        recordEntity.setCreatedAt(createdAt);
        recordEntity.setUpdatedAt(updatedAt);
        return recordEntity;
    }
}

