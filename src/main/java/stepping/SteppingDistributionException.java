package stepping;

public class SteppingDistributionException extends SteppingSystemException {
    private final String subjectType;

    SteppingDistributionException(String subjectType, Exception e) {
        super(e);
        this.subjectType = subjectType;
    }

    SteppingDistributionException(String subjectType, String message, Exception e) {
        super(message, e);
        this.subjectType = subjectType;
    }

    public String getSubjectType() {
        return subjectType;
    }
}
