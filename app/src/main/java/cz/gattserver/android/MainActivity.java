package cz.gattserver.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cz.gattserver.android.campgames.CampgamesActivity;
import cz.gattserver.android.common.ButtonDefinition;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.drinks.DrinksActivity;
import cz.gattserver.android.messages.MessagesActivity;
import cz.gattserver.android.photogallery.PhotogalleriesActivity;
import cz.gattserver.android.recipes.RecipesActivity;
import cz.gattserver.android.songs.SongsActivity;

public class MainActivity extends GrassActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("GRASS Mobile");

        List<ButtonDefinition> buttonDefinitions = new ArrayList<>();
        buttonDefinitions.add(new ButtonDefinition("Fotogalerie", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PhotogalleriesActivity.class);
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
}
