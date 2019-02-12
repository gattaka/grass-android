package cz.gattserver.android;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public abstract class GrassActivity extends AppCompatActivity {

    protected void setTitle(String title) {
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }

        android.support.v7.app.ActionBar ab7 = getSupportActionBar();
        if (ab7 != null) {
            ab7.setTitle(title);
        }
    }

    protected void hideActionBar() {
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.hide();
        }

        android.support.v7.app.ActionBar ab7 = getSupportActionBar();
        if (ab7 != null) {
            ab7.hide();
        }
    }
}
