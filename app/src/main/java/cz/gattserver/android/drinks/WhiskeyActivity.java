package cz.gattserver.android.drinks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.FormatUtils;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.URLGetTask;
import cz.gattserver.android.common.URLTaskInfoBundle;
import cz.gattserver.android.common.OnSuccessAction;
import cz.gattserver.android.common.URLTaskParamTO;

public class WhiskeyActivity extends GrassActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whiskey);

        hideActionBar();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        URLGetTask<WhiskeyActivity> fetchTask = new URLGetTask<>(this, new OnSuccessAction<WhiskeyActivity>() {
            @Override
            public void run(WhiskeyActivity urlTaskClient, URLTaskInfoBundle bundle) {
                urlTaskClient.init(bundle.getResultAsStringUTF());
            }
        });

        fetchTask.execute(new URLTaskParamTO(Config.DRINKS_WHISKEY_DETAIL_RESOURCE + "?id=" + id));

        Log.d("WhiskeyActivity", "The onCreate() event");
    }

    private String dashIfNullByJSON(JSONObject jsonObject, String key) throws JSONException {
        String value = jsonObject.getString(key);
        return value == null || "null".equals(value) ? "-" : value;
    }

    public void init(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            TextView nameText = findViewById(R.id.drinkName);
            nameText.setText(jsonObject.getString("name"));

            TextView ratingText = findViewById(R.id.drinkRating);
            ratingText.setText(FormatUtils.formatRatingStars(Double.parseDouble(jsonObject.getString("rating"))));

            TextView countryText = findViewById(R.id.drinkCountry);
            countryText.setText(dashIfNullByJSON(jsonObject, "country"));

            TextView breweryText = findViewById(R.id.whiskeyYears);
            breweryText.setText(dashIfNullByJSON(jsonObject, "years"));

            TextView degreesText = findViewById(R.id.whiskeyType);
            degreesText.setText(dashIfNullByJSON(jsonObject, "whiskeyType"));

            TextView alcoholText = findViewById(R.id.drinkAlcohol);
            alcoholText.setText(getString(R.string.drink_alcohol_format, dashIfNullByJSON(jsonObject, "alcohol")));

            TextView descriptionText = findViewById(R.id.drinkDescription);
            descriptionText.setText(jsonObject.getString("description").replaceAll("<br/>", "\n").replaceAll("<br>", "\n"));

            byte[] decodedString = Base64.decode(jsonObject.getString("image"), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ImageView drinkImage = findViewById(R.id.drinkImage);
            drinkImage.setImageBitmap(decodedByte);

        } catch (JSONException e) {
            Log.e("WhiskeyActivity", "JSONObject", e);
        }
    }
}
