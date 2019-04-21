package cz.gattserver.android.interfaces;

public class PhotoTO {

    private int id;
    private String title;
    private String data;
    private String mime;
    private int size;

    public PhotoTO(int id) {
        this.id = id;
    }

    public PhotoTO(int id, String title, String data, String mime, int size) {
        this.id = id;
        this.title = title;
        this.data = data;
        this.mime = mime;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}