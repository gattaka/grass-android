package cz.gattserver.android;

public class Config {

    public static final String SERVER_ROOT = "https://www.gattserver.cz";
    //public static final String SERVER_ROOT = "http://10.0.2.2:8180/web";

    public static final String CORE_ROOT = SERVER_ROOT + "/ws/core";
    public static final String LOGGED = CORE_ROOT + "/logged";
    public static final String LOGOUT = CORE_ROOT + "/logout";
    public static final String LOGIN = CORE_ROOT + "/login";

    public static final String ARTICLES_ROOT = SERVER_ROOT + "/ws/articles";
    public static final String ARTICLES_CREATE = ARTICLES_ROOT + "/create";
    public static final String ARTICLES_LIST_RESOURCE = ARTICLES_ROOT + "/list";
    public static final String ARTICLES_COUNT_RESOURCE = ARTICLES_ROOT + "/count";
    public static final String ARTICLES_DETAIL_RESOURCE = ARTICLES_ROOT + "/article";

    public static final String PG_ROOT = SERVER_ROOT + "/ws/pg";
    public static final String PG_CREATE = PG_ROOT + "/create";
    public static final String PG_UPLOAD = PG_ROOT + "/upload";
    public static final String PG_PROCESS = PG_ROOT + "/process";
    public static final String PG_LIST_RESOURCE = PG_ROOT + "/list";
    public static final String PG_COUNT_RESOURCE = PG_ROOT + "/count";
    public static final String PG_DETAIL_RESOURCE = PG_ROOT + "/gallery";
    public static final String PHOTO_SLIDESHOW_RESOURCE = PG_ROOT + "/slideshow";
    public static final String PHOTO_DETAIL_RESOURCE = PG_ROOT + "/photo";

    public static final String BOOKS_ROOT = SERVER_ROOT + "/ws/books";
    public static final String BOOKS_LIST_RESOURCE = BOOKS_ROOT + "/list";
    public static final String BOOKS_COUNT_RESOURCE = BOOKS_ROOT + "/count";
    public static final String BOOK_DETAIL_RESOURCE = BOOKS_ROOT + "/book";

    public static final String RECIPES_ROOT = SERVER_ROOT + "/ws/recipes";
    public static final String RECIPES_LIST_RESOURCE = RECIPES_ROOT + "/list";
    public static final String RECIPES_COUNT_RESOURCE = RECIPES_ROOT + "/count";
    public static final String RECIPE_DETAIL_RESOURCE = RECIPES_ROOT + "/recipe";

    public static final String SONGS_ROOT = SERVER_ROOT + "/ws/songs";
    public static final String SONGS_LIST_RESOURCE = SONGS_ROOT + "/list";
    public static final String SONGS_COUNT_RESOURCE = SONGS_ROOT + "/count";
    public static final String SONG_DETAIL_RESOURCE = SONGS_ROOT + "/song";

    public static final String CAMPGAMES_ROOT = SERVER_ROOT + "/ws/campgames";
    public static final String CAMPGAMES_LIST_RESOURCE = CAMPGAMES_ROOT + "/list";
    public static final String CAMPGAMES_COUNT_RESOURCE = CAMPGAMES_ROOT + "/count";
    public static final String CAMPGAMES_DETAIL_RESOURCE = CAMPGAMES_ROOT + "/campgame";

    public static final String DRINKS_ROOT = SERVER_ROOT + "/ws/drinks";
    public static final String DRINKS_BEER_LIST_RESOURCE = DRINKS_ROOT + "/beer-list";
    public static final String DRINKS_BEER_COUNT_RESOURCE = DRINKS_ROOT + "/beer-count";
    public static final String DRINKS_BEER_DETAIL_RESOURCE = DRINKS_ROOT + "/beer";
    public static final String DRINKS_RUM_LIST_RESOURCE = DRINKS_ROOT + "/rum-list";
    public static final String DRINKS_RUM_COUNT_RESOURCE = DRINKS_ROOT + "/rum-count";
    public static final String DRINKS_RUM_DETAIL_RESOURCE = DRINKS_ROOT + "/rum";
    public static final String DRINKS_WHISKEY_LIST_RESOURCE = DRINKS_ROOT + "/whiskey-list";
    public static final String DRINKS_WHISKEY_COUNT_RESOURCE = DRINKS_ROOT + "/whiskey-count";
    public static final String DRINKS_WHISKEY_DETAIL_RESOURCE = DRINKS_ROOT + "/whiskey";
    public static final String DRINKS_WINE_LIST_RESOURCE = DRINKS_ROOT + "/wine-list";
    public static final String DRINKS_WINE_COUNT_RESOURCE = DRINKS_ROOT + "/wine-count";
    public static final String DRINKS_WINE_DETAIL_RESOURCE = DRINKS_ROOT + "/wine";

    public static final String NOTES_DIR = "notes";

}
