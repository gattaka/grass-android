package cz.gattserver.android.interfaces;

import java.util.Date;

public class NoteTO {

    private String id;
    private String text;
    private Date date;

    public NoteTO(String id, String text, Date date) {
        this.id = id;
        this.text = text;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getPreview() {
        if (!text.contains("\n"))
            return text;
        return text.substring(0, text.indexOf('\n'));
    }

    public Date getDate() {
        return date;
    }

}