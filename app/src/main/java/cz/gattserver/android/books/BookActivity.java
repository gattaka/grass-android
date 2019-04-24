package cz.gattserver.android.books;

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

public class BookActivity extends GrassActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        hideActionBar();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        URLGetTask<BookActivity> fetchTask = new URLGetTask<>(this, new OnSuccessAction<BookActivity>() {
            @Override
            public void run(BookActivity urlTaskClient, URLTaskInfoBundle bundle) {
                urlTaskClient.init(bundle.getResultAsStringUTF());
            }
        });

        fetchTask.execute(new URLTaskParamTO(Config.BOOK_DETAIL_RESOURCE + "?id=" + id));

        Log.d("BookActivity", "The onCreate() event");
    }

    private String dashIfNullByJSON(JSONObject jsonObject, String key) throws JSONException {
        String value = jsonObject.getString(key);
        return value == null || "null".equals(value) ? "-" : value;
    }

    public void init(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            TextView nameText = findViewById(R.id.bookName);
            nameText.setText(jsonObject.getString("name"));

            TextView ratingText = findViewById(R.id.bookRating);
            ratingText.setText(FormatUtils.formatRatingStars(Double.parseDouble(jsonObject.getString("rating"))));

            TextView countryText = findViewById(R.id.bookAuthor);
            countryText.setText(dashIfNullByJSON(jsonObject, "author"));

            TextView releasedText = findViewById(R.id.bookReleased);
            releasedText.setText(dashIfNullByJSON(jsonObject, "year"));

            TextView descriptionText = findViewById(R.id.bookDescription);
            descriptionText.setText(jsonObject.getString("description").replaceAll("<br/>", "\n").replaceAll("<br>", "\n"));

            byte[] decodedString = Base64.decode(jsonObject.getString("image"), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ImageView bookImage = findViewById(R.id.bookImage);
            bookImage.setImageBitmap(decodedByte);

        } catch (JSONException e) {
            Log.e("BookActivity", "JSONObject", e);
        }
    }
}
