package inhagdsc.mamasteps.user.entity.enums;

public enum ActivityLevel {
    HIGH("강"),
    MEDIUM("중"),
    LOW("약");

    private final String description;

    ActivityLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
