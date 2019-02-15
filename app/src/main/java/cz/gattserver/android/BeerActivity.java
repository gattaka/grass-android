package cz.gattserver.android;

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

import cz.gattserver.android.common.FormatUtils;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.URLTask;

public class BeerActivity extends GrassActivity {

    private String msg = "GrassAPP: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer);

        hideActionBar();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        URLTask<BeerActivity> fetchTask = new URLTask<>(this, new URLTask.OnSuccessAction<BeerActivity>() {
            @Override
            public void run(BeerActivity urlTaskClient, URLTask.URLTaskInfoBundle bundle) {
                urlTaskClient.init(bundle.getResultAsStringUTF());
            }
        });

        fetchTask.execute(Config.DRINKS_BEER_DETAIL_RESOURCE + "?id=" + id);

        Log.d(msg, "The onCreate() event");
    }

    private String dashIfNullByJSON(JSONObject jsonObject, String key) throws JSONException {
        String value = jsonObject.getString(key);
        return value == null || "null".equals(value) ? "-" : value;
    }

    public void init(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            TextView nameText = findViewById(R.id.beerName);
            nameText.setText(jsonObject.getString("name"));

            TextView ratingText = findViewById(R.id.beerRating);
            ratingText.setText(FormatUtils.formatRatingStars(Double.parseDouble(jsonObject.getString("rating"))));

            TextView countryText = findViewById(R.id.beerCountry);
            countryText.setText(dashIfNullByJSON(jsonObject, "country"));

            TextView breweryText = findViewById(R.id.beerBrewery);
            breweryText.setText(dashIfNullByJSON(jsonObject, "brewery"));

            TextView degreesText = findViewById(R.id.beerDegrees);
            degreesText.setText(getString(R.string.beer_degrees_format, dashIfNullByJSON(jsonObject, "degrees")));

            TextView alcoholText = findViewById(R.id.beerAlcohol);
            alcoholText.setText(getString(R.string.drink_alcohol_format, dashIfNullByJSON(jsonObject, "alcohol")));

            TextView ibuText = findViewById(R.id.beerIBU);
            ibuText.setText(dashIfNullByJSON(jsonObject, "ibu"));

            TextView categoryText = findViewById(R.id.beerCategory);
            categoryText.setText(dashIfNullByJSON(jsonObject, "category"));

            TextView hopsText = findViewById(R.id.beerHops);
            hopsText.setText(dashIfNullByJSON(jsonObject, "hops"));

            TextView maltTypeText = findViewById(R.id.beerMaltType);
            maltTypeText.setText(dashIfNullByJSON(jsonObject, "maltType"));

            TextView maltsText = findViewById(R.id.beerMalts);
            maltsText.setText(dashIfNullByJSON(jsonObject, "malts"));

            TextView descriptionText = findViewById(R.id.beerDescription);
            descriptionText.setText(jsonObject.getString("description").replaceAll("<br/>", "\n").replaceAll("<br>", "\n"));

            byte[] decodedString = Base64.decode(jsonObject.getString("image"), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ImageView beerImage = findViewById(R.id.beerImage);
            beerImage.setImageBitmap(decodedByte);

        } catch (JSONException e) {
            Log.e(msg, "JSONObject", e);
        }
    }
}
