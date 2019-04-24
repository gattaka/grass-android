package cz.gattserver.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cz.gattserver.android.books.BooksActivity;
import cz.gattserver.android.campgames.CampgamesActivity;
import cz.gattserver.android.common.ButtonDefinition;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.OnSuccessAction;
import cz.gattserver.android.common.URLGetTask;
import cz.gattserver.android.common.URLTaskInfoBundle;
import cz.gattserver.android.drinks.DrinksActivity;
import cz.gattserver.android.messages.MessagesActivity;
import cz.gattserver.android.photogallery.PhotoMenuActivity;
import cz.gattserver.android.recipes.RecipesActivity;
import cz.gattserver.android.songs.SongsActivity;
import cz.gattserver.android.login.LoginActivity;

public class MainActivity extends GrassActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("GRASS Mobile");

        List<ButtonDefinition> buttonDefinitions = new ArrayList<>();

        createLoginComponents();

        buttonDefinitions.add(new ButtonDefinition("Fotky", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PhotoMenuActivity.class);
                startActivity(intent);
            }
        }));
        buttonDefinitions.add(new ButtonDefinition("Recepty", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecipesActivity.class);
                startActivity(intent);
            }
        }));
        buttonDefinitions.add(new ButtonDefinition("Zpěvník", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SongsActivity.class);
                startActivity(intent);
            }
        }));
        buttonDefinitions.add(new ButtonDefinition("Nápoje", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DrinksActivity.class);
                startActivity(intent);
            }
        }));
        buttonDefinitions.add(new ButtonDefinition("Táborové hry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CampgamesActivity.class);
                startActivity(intent);
            }
        }));
        buttonDefinitions.add(new ButtonDefinition("Knihy", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BooksActivity.class);
                startActivity(intent);
            }
        }));
        buttonDefinitions.add(new ButtonDefinition("Zprávy", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MessagesActivity.class);
                startActivity(intent);
            }
        }));

        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        for (ButtonDefinition bdef : buttonDefinitions) {
            Button btn = new Button(this);
            btn.setText(bdef.getCaption());
            btn.setOnClickListener(bdef.getClickListener());
            mainLayout.addView(btn);
        }

        Log.d("GrassAPP", "The onCreate() event");
    }

    private void createLoginComponents() {
        final LinearLayout loginLayout = findViewById(R.id.loginLayout);
        loginLayout.removeAllViews();

        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        String sessionid = settings.getString(LoginActivity.VAR_NAME, null);

        if (sessionid == null || sessionid.isEmpty()) {
            Button btn = new Button(MainActivity.this);
            btn.setText("Přihlásit se");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    // new Intent(LoginActivity.class.getName())
                    startActivityForResult(intent, 0);
                }
            });
            loginLayout.addView(btn);
        } else {
            URLGetTask<MainActivity> loggedInTask = new URLGetTask<>(this, new OnSuccessAction<MainActivity>() {
                @Override
                public void run(MainActivity urlTaskClient, URLTaskInfoBundle bundle) {
                    Button btn = new Button(MainActivity.this);
                    btn.setText("Odhlásit se (" + bundle.getResultAsStringUTF() + ")");
                    // TODO
                    // btn.setOnClickListener(bdef.getClickListener());
                    loginLayout.addView(btn);
                }
            });
            loggedInTask.execute(Config.LOGGED, sessionid);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            createLoginComponents();
        }
    }
}
