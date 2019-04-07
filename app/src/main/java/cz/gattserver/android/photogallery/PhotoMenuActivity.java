package cz.gattserver.android.photogallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cz.gattserver.android.R;
import cz.gattserver.android.common.ButtonDefinition;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.drinks.BeersActivity;
import cz.gattserver.android.drinks.RumsActivity;
import cz.gattserver.android.drinks.WhiskiesActivity;

public class PhotoMenuActivity extends GrassActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photomenu);

        setTitle("Fotky");

        List<ButtonDefinition> buttonDefinitions = new ArrayList<>();
        buttonDefinitions.add(new ButtonDefinition("Fotogalerie", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotoMenuActivity.this, PhotogalleriesActivity.class);
                startActivity(intent);
            }
        }));
        buttonDefinitions.add(new ButtonDefinition("Upload fotek", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotoMenuActivity.this, PhotoUploadActivity.class);
                startActivity(intent);
            }
        }));

        LinearLayout mainLayout = findViewById(R.id.photomenuLayout);
        for (ButtonDefinition bdef : buttonDefinitions) {
            Button btn = new Button(this);
            btn.setText(bdef.getCaption());
            btn.setOnClickListener(bdef.getClickListener());
            mainLayout.addView(btn);
        }

        Log.d("PhotoMenuActivity", "The onCreate() event");
    }
}
