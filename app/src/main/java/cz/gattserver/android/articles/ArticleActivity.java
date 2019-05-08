package cz.gattserver.android.articles;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.OnSuccessAction;
import cz.gattserver.android.common.URLGetTask;
import cz.gattserver.android.common.URLTaskInfoBundle;
import cz.gattserver.android.common.URLTaskParamTO;

public class ArticleActivity extends GrassActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        hideActionBar();

        final LinearLayout mainLayout = findViewById(R.id.articleLayout);

        URLGetTask<ArticleActivity> task = new URLGetTask<>(this, new OnSuccessAction<ArticleActivity>() {
            @Override
            public void run(ArticleActivity instance, URLTaskInfoBundle bundle) {
                if (bundle.isSuccess()) {
                    if (bundle.getResponseCode() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(bundle.getResultAsStringUTF());
                            String outputHTML = jsonObject.getString("outputHTML");

                            WebView webView = new WebView(instance);

                            String encodedHTML = Base64.encodeToString(outputHTML.getBytes(),
                                    Base64.NO_PADDING);
                            webView.loadData(encodedHTML, "text/html; charset=UTF-8", "base64");

                            mainLayout.addView(webView);
                        } catch (JSONException e) {
                            Log.e("ArticleActivity", "JSONObject", e);
                        }
                    } else {
                        new AlertDialog.Builder(instance)
                                .setTitle("Chyba")
                                .setMessage("Článek není dostupný nebo porušený")
                                .setIcon(android.R.drawable.ic_dialog_alert).show();
                    }
                } else {
                    new AlertDialog.Builder(instance)
                            .setTitle("Chyba")
                            .setMessage("Nezdařilo se načíst článek")
                            .setIcon(android.R.drawable.ic_dialog_alert).show();
                }
            }
        });

        task.execute(new URLTaskParamTO(Config.ARTICLES_DETAIL_RESOURCE + "?id=" + id));

        Log.d("DrinksActivity", "The onCreate() event");
    }
}
