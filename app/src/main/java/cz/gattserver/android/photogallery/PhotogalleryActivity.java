package cz.gattserver.android.photogallery;

import android.app.AlertDialog;
import android.content.Context;
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
import cz.gattserver.android.common.URLGetTask;
import cz.gattserver.android.common.URLTaskInfoBundle;
import cz.gattserver.android.interfaces.ImageItemTO;
import cz.gattserver.android.lazyloader.LazyListActivity;
import cz.gattserver.android.lazyloader.LazyLoaderScrollListener;

public class PhotogalleryActivity extends GrassActivity {

    private static String msg = "GrassAPP: ";

    private String id;
    private int totalCount = 0;
    private int currentMini = 0;

    private ListView listView;
    private ProgressBar progressBar;
    private ArrayAdapter<ImageItemTO> adapter;

    private static class PhotogalleryInitAction implements URLGetTask.OnSuccessAction<PhotogalleryActivity> {

        @Override
        public void run(PhotogalleryActivity instance, URLTaskInfoBundle bundle) {
            if (bundle.isSuccess()) {
                instance.init(bundle.getResultAsStringUTF());
            } else {
                new AlertDialog.Builder(instance)
                        .setTitle("Chyba")
                        .setMessage("Nezdařilo se načíst detail galerie")
                        .setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        }
    }

    private static class PhotogalleryFetchMiniatureAction implements URLGetTask.OnSuccessAction<PhotogalleryActivity> {

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
        fetchTask.execute(Config.PG_DETAIL_RESOURCE + "?id=" + id);

        Log.d(msg, "The onCreate() event");
    }

    public void addMiniature(URLTaskInfoBundle bundle) {
        if (bundle == null || !bundle.isSuccess())
            return;
        byte[] image = bundle.getResult();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        adapter.add(new ImageItemTO(bundle.getParams()[1], bundle.getParams()[2], bitmap));
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
                    Log.d("LazyLoaderCountTask", "load() event");
                    if (totalCount > currentMini) {
                        URLGetTask<PhotogalleryActivity> fetchTask = new URLGetTask<>(PhotogalleryActivity.this, new PhotogalleryFetchMiniatureAction());
                        // http://gattserver.cz/ws/pg/mini?id=383&fileName=31252889_10211544311583314_2288872652829360128_n.jpg
                        fetchTask.execute(Config.PHOTO_MINIATURE_RESOURCE + "?id=" + id + "&fileName=" + URLEncoder.encode(photoNames[currentMini]), photoNames[currentMini], id);
                        currentMini++;
                    } else {
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
            Log.e(msg, "JSONObject", e);
        }
    }

}
