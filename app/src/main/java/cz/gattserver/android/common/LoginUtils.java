package cz.gattserver.android.common;

import android.content.ContextWrapper;
import android.content.SharedPreferences;

public class LoginUtils {

    public static final String PREFS_NAME = "GRASS_MOBILE_LOGIN";
    public static final String VAR_NAME = "sessionid";

    private LoginUtils() {
    }

    public static String getSessionid(ContextWrapper wrapper) {
        SharedPreferences settings = wrapper.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(VAR_NAME, null);
    }

    public static void saveSessionId(ContextWrapper wrapper, String sessionId) {
        SharedPreferences settings = wrapper.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(VAR_NAME, sessionId);
        editor.commit();
    }
}
