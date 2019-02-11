package cz.gattserver.mobile;

public class RecipeTO {
    private String name;
    private String id;

    RecipeTO(String name, String id) {
        this.name = name;
        this.id = id;
    }

    String getName() {
        return name;
    }

    String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}