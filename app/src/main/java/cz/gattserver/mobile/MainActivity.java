package cz.gattserver.mobile;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String msg = "GrassAPP: ";

    private void customizeActionBar() {
        String title = "GRASS Mobile";

        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }

        android.support.v7.app.ActionBar ab7 = getSupportActionBar();
        if (ab7 != null) {
            ab7.setTitle(title);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customizeActionBar();

        List<ButtonDefinition> buttonDefinitions = new ArrayList<>();
        buttonDefinitions.add(new ButtonDefinition("Fotogalerie", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }));
        buttonDefinitions.add(new ButtonDefinition("Recepty", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecipesActivity.class);
                intent.putExtra("GRASS EXTRA_MESSAGE", "GRASS message");
                startActivity(intent);
            }
        }));
        buttonDefinitions.add(new ButtonDefinition("Zpěvník", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }));
        buttonDefinitions.add(new ButtonDefinition("Nápoje", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }));
        buttonDefinitions.add(new ButtonDefinition("Táborové hry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }));
        buttonDefinitions.add(new ButtonDefinition("SMS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }));

        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        for (ButtonDefinition bdef : buttonDefinitions) {
            Button btn = new Button(this);
            btn.setText(bdef.getCaption());
            btn.setOnClickListener(bdef.getClickListener());
            mainLayout.addView(btn);
        }

        Log.d(msg, "The onCreate() event");
    }
}
