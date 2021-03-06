package cz.gattserver.android.interfaces;

public class ItemTO {

    protected String name;
    protected String id;

    public ItemTO(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}