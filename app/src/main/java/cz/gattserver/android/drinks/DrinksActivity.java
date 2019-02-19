package cz.gattserver.android.drinks;

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

public class DrinksActivity extends GrassActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Nápoje");

        List<ButtonDefinition> buttonDefinitions = new ArrayList<>();
        buttonDefinitions.add(new ButtonDefinition("Pivo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DrinksActivity.this, BeersActivity.class);
                startActivity(intent);
            }
        }));
        buttonDefinitions.add(new ButtonDefinition("Rum", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DrinksActivity.this, RumsActivity.class);
                startActivity(intent);
            }
        }));
        buttonDefinitions.add(new ButtonDefinition("Whiskey", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DrinksActivity.this, WhiskiesActivity.class);
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

        Log.d("DrinksActivity", "The onCreate() event");
    }
}
