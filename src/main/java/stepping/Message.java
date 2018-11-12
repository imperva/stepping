package stepping;

public class Message {
    private final Data data;
    private final String subjectType;

    public Message(Data data, String subjectType){
        this.data = data;
        this.subjectType = subjectType;
    }

    public Data getData(){
        return this.data;
    }

    public String getSubjectType(){
        return this.subjectType;
    }

}
