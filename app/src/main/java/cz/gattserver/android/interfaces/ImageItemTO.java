package cz.gattserver.android.interfaces;

import android.graphics.Bitmap;

public class ImageItemTO {

    protected String name;
    protected String id;
    protected Bitmap bitmap;

    public ImageItemTO(String name, String id, Bitmap bitmap) {
        this.name = name;
        this.id = id;
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public String toString() {
        return name;
    }
}