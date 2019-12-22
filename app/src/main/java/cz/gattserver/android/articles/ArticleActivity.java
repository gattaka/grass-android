package cz.gattserver.android.articles;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import org.json.JSONArray;
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
                            String contentHTML = jsonObject.getString("outputHTML");

                            String outputHTML = "<!DOCTYPE html>\n" +
                                    "<html>\n" +
                                    " <head>\n" +
                                    "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                                    "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=11\" />\n" +
                                    "  <title>Gattserver</title>\n" +
                                    "  <style type=\"text/css\">\n" +
                                    "\tbody {\n" +
                                    "\t\tfont: 400 14px/1.55 Helvetica, Arial, \"lucida grande\", tahoma, verdana, arial, sans-serif\n" +
                                    "\t}\n" +
                                    "\t\n" +
                                    "\t.articles-h1 {\n" +
                                    "\t\tfont-size: 15pt;\n" +
                                    "\t\tmargin-bottom: 10px !important;\n" +
                                    "\t\tborder-bottom: 1px #BBB solid\n" +
                                    "\t}\n" +
                                    "\t\n" +
                                    "\t.articles-basic-monospaced {\n" +
                                    "\t\tfont-family: monospace;\n" +
                                    "\t\tfont-weight: normal;\n" +
                                    "\t\tbackground: rgba(255, 255, 255, 0.34);\n" +
                                    "\t\tborder-color: rgba(68, 68, 68, 0.38);\n" +
                                    "\t\tborder-style: dashed;\n" +
                                    "\t\tborder-width: 1px;\n" +
                                    "\t\tpadding: 1px 4px 2px 4px;\n" +
                                    "\t\tline-height: 24px;\n" +
                                    "\t\t-moz-border-radius: 2px;\n" +
                                    "\t\tborder-radius: 4px;\t\t\n" +
                                    "\t}\n" +
                                    "\t\n" +
                                    "\t.barier {\n" +
                                    "\t\twhite-space: pre;\n" +
                                    "\t\tborder: dashed 1px;\n" +
                                    "\t\tpadding: 5px;\n" +
                                    "\t\tfont-size: 9pt;\n" +
                                    "\t\tbackground: hsl(48, 53%, 95%);\n" +
                                    "\t}\n" +
                                    "\t\n" +
                                    "\t.barier, .numberedtext, .code {\n" +
                                    "\t\twidth: max-content;\n" +
                                    "\t}\t";

                            // TODO
                            JSONArray pluginCSSResources = jsonObject.getJSONArray("pluginCSSResources");

                            outputHTML += "</style>\n";

                            // TODO
                            JSONArray pluginJSResources = jsonObject.getJSONArray("pluginJSResources");

                            outputHTML += " </head>\n" +
                                    " <body scroll=\"auto\" class=\" v-generated-body\">";

                            contentHTML = contentHTML.replaceAll("</textarea>","</div>");
                            contentHTML = contentHTML.replaceAll("<textarea","<div class=\"code\" ");

                            outputHTML += contentHTML;
                            outputHTML += "</body>\n" +
                                    "</html>";

                            WebView webView = new WebView(instance);
                            webView.loadDataWithBaseURL("file:///android_asset/", outputHTML, "text/html", "UTF-8", null);
                            /*
                             String encodedHTML = Base64.encodeToString(outputHTML.getBytes(),
                             Base64.NO_PADDING);
                             webView.loadData(encodedHTML, "text/html; charset=UTF-8", "base64");
                             WebSettings webSettings = webView.getSettings();
                             */
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
    }
}
