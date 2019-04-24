package cz.gattserver.android.photogallery;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.ImageItemArrayAdapter;
import cz.gattserver.android.common.LoginUtils;
import cz.gattserver.android.common.URLGetTask;
import cz.gattserver.android.common.OnSuccessAction;
import cz.gattserver.android.common.URLTaskInfoBundle;
import cz.gattserver.android.common.URLTaskParamTO;
import cz.gattserver.android.interfaces.ImageItemTO;
import cz.gattserver.android.lazyloader.LazyLoaderScrollListener;

public class PhotogalleryActivity extends GrassActivity {

    private String id;
    private int totalCount = 0;
    private int currentSlideshow = 0;

    private ListView listView;
    private ProgressBar progressBar;
    private ArrayAdapter<ImageItemTO> adapter;

    private static class PhotogalleryInitAction implements OnSuccessAction<PhotogalleryActivity> {

        @Override
        public void run(PhotogalleryActivity instance, URLTaskInfoBundle bundle) {
            if (bundle.isSuccess()) {
                if (bundle.getResponseCode() == 200) {
                    instance.init(bundle.getResultAsStringUTF());
                } else {
                    new AlertDialog.Builder(instance)
                            .setTitle("Chyba")
                            .setMessage("Galerie není dostupná nebo porušená")
                            .setIcon(android.R.drawable.ic_dialog_alert).show();
                }
            } else {
                new AlertDialog.Builder(instance)
                        .setTitle("Chyba")
                        .setMessage("Nezdařilo se načíst detail galerie")
                        .setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        }
    }

    private static class PhotogalleryFetchMiniatureAction implements OnSuccessAction<PhotogalleryActivity> {

        @Override
        public void run(PhotogalleryActivity instance, URLTaskInfoBundle bundle) {
            instance.addMiniature(bundle);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photogallery);

        hideActionBar();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        URLGetTask<PhotogalleryActivity> fetchTask = new URLGetTask<>(this, new PhotogalleryInitAction());

        // http://gattserver.cz/ws/pg/gallery?id=383
        fetchTask.execute(new URLTaskParamTO(Config.PG_DETAIL_RESOURCE + "?id=" + id, LoginUtils.getSessionid(this)));

        Log.d("PhotogalleryActivity", "The onCreate() event");
    }

    public void addMiniature(URLTaskInfoBundle bundle) {
        if (bundle == null || !bundle.isSuccess())
            return;
        byte[] image = bundle.getResult();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        adapter.add(new ImageItemTO(bundle.getTaskParamTO().getParams()[1], bundle.getTaskParamTO().getParams()[2], bitmap));
    }

    public void init(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            TextView nameText = findViewById(R.id.photogalleryName);
            nameText.setText(jsonObject.getString("name"));

            JSONArray photos = jsonObject.getJSONArray("files");
            totalCount = photos.length();

            final String[] photoNames = new String[photos.length()];
            for (int i = 0; i < photos.length(); i++)
                photoNames[i] = photos.getString(i);

            listView = findViewById(R.id.photogalleryListView);
            listView.addFooterView(progressBar = new ProgressBar(this));
            adapter = new ImageItemArrayAdapter(this, R.layout.image_item_listview_row);
            listView.setAdapter(adapter);
            listView.setOnScrollListener(new LazyLoaderScrollListener(1) {
                @Override
                public void loadMore(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    Log.d("LazyLoaderCountTask", "currentSlideshow: " + currentSlideshow + ", totalCount: " + totalCount);
                    if (totalCount > currentSlideshow) {
                        URLGetTask<PhotogalleryActivity> fetchTask = new URLGetTask<>(PhotogalleryActivity.this, new PhotogalleryFetchMiniatureAction());
                        fetchTask.execute(new URLTaskParamTO(Config.PHOTO_SLIDESHOW_RESOURCE + "?id=" + id + "&fileName=" + URLEncoder.encode(photoNames[currentSlideshow]),
                                LoginUtils.getSessionid(PhotogalleryActivity.this)));
                        currentSlideshow++;
                    }
                    if (totalCount <= currentSlideshow) {
                        listView.removeFooterView(progressBar);
                    }
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (adapter.getCount() <= position)
                        return;
                    ImageItemTO item = adapter.getItem(position);
                    if (item == null)
                        return;
                    Intent intent = new Intent(PhotogalleryActivity.this, PhotoActivity.class);
                    intent.putExtra("id", PhotogalleryActivity.this.id);
                    intent.putExtra("name", item.getName());
                    startActivity(intent);
                }
            });

        } catch (JSONException e) {
            Log.e("PhotogalleryActivity", "JSONObject", e);
        }
    }

}
