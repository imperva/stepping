package stepping;

public class DistributionException extends RuntimeException {
    private final String subjectType;

    DistributionException(String subjectType, Exception e) {
        super(e);
        this.subjectType = subjectType;
    }

    DistributionException(String subjectType, String message, Exception e) {
        super(message, e);
        this.subjectType = subjectType;
    }

    public String getSubjectType() {
        return subjectType;
    }
}
