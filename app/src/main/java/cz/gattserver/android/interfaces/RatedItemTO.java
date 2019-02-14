package cz.gattserver.android.interfaces;

public class RatedItemTO extends ItemTO {

    private String rating;

    public RatedItemTO(String rating, String name, String id) {
        super(name, id);
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return rating + " " + name;
    }
}