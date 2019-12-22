package cz.gattserver.android.articles;

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
import cz.gattserver.android.photogallery.PhonePhotosListActivity;

public class ArticlesMenuActivity extends GrassActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_menu);

        setTitle("Články");

        List<ButtonDefinition> buttonDefinitions = new ArrayList<>();
        buttonDefinitions.add(new ButtonDefinition("Články", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArticlesMenuActivity.this, ArticlesActivity.class);
                startActivity(intent);
            }
        }));
        buttonDefinitions.add(new ButtonDefinition("Poznámky", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArticlesMenuActivity.this, NotesActivity.class);
                startActivity(intent);
            }
        }));
        buttonDefinitions.add(new ButtonDefinition("Upload SMS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArticlesMenuActivity.this, MessagesActivity.class);
                startActivity(intent);
            }
        }));

        LinearLayout mainLayout = findViewById(R.id.articlesMenuLayout);
        for (ButtonDefinition bdef : buttonDefinitions) {
            Button btn = new Button(this);
            btn.setText(bdef.getCaption());
            btn.setOnClickListener(bdef.getClickListener());
            mainLayout.addView(btn);
        }

        Log.d("ArticlesMenuActivity", "The onCreate() event");
    }
}
