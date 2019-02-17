package cz.gattserver.android.interfaces;

public class MessageTO {

    private String id;
    private String body;
    private String date;
    private String number;

    public MessageTO(String id, String body, String date, String number) {
        this.id = id;
        this.body = body;
        this.date = date;
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    public String getNumber() {
        return number;
    }
}