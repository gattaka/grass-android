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

public class BeerActivity extends GrassActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer);

        hideActionBar();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        URLGetTask<BeerActivity> fetchTask = new URLGetTask<>(this, new OnSuccessAction<BeerActivity>() {
            @Override
            public void run(BeerActivity urlTaskClient, URLTaskInfoBundle bundle) {
                urlTaskClient.init(bundle.getResultAsStringUTF());
            }
        });

        fetchTask.execute(new URLTaskParamTO(Config.DRINKS_BEER_DETAIL_RESOURCE + "?id=" + id));

        Log.d("BeerActivity", "The onCreate() event");
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

            TextView breweryText = findViewById(R.id.beerBrewery);
            breweryText.setText(dashIfNullByJSON(jsonObject, "brewery"));

            TextView degreesText = findViewById(R.id.beerDegrees);
            degreesText.setText(getString(R.string.beer_degrees_format, dashIfNullByJSON(jsonObject, "degrees")));

            TextView alcoholText = findViewById(R.id.drinkAlcohol);
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

            TextView descriptionText = findViewById(R.id.drinkDescription);
            descriptionText.setText(jsonObject.getString("description").replaceAll("<br/>", "\n").replaceAll("<br>", "\n"));

            byte[] decodedString = Base64.decode(jsonObject.getString("image"), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ImageView drinkImage = findViewById(R.id.drinkImage);
            drinkImage.setImageBitmap(decodedByte);

        } catch (JSONException e) {
            Log.e("BeerActivity", "JSONObject", e);
        }
    }
}
