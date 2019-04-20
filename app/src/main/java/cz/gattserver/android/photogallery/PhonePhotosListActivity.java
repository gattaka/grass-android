package cz.gattserver.android.photogallery;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

import cz.gattserver.android.R;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.interfaces.PhotoTO;
import cz.gattserver.android.lazyloader.LazyLoaderScrollListener;

public class PhonePhotosListActivity extends GrassActivity {

    private static int PAGE_SIZE = 5;

    private int currentPage = 0;
    private Integer totalCount;

    private ListView listView;
    private ProgressBar progressBar;
    private ArrayAdapter<PhotoTO> adapter;

    private Map<Integer, PhotoTO> choosenPhotos = new HashMap<>();
    private Map<Integer, CheckBox> checkBoxMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_photos);
        setTitle("Fotky");

        count();
        if (totalCount == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Chyba")
                    .setMessage("Nezdařilo se získat počet fotek")
                    .setIcon(android.R.drawable.ic_dialog_alert).show();
        } else {
            createComponents();
        }

        Log.d("PhonePhotosListActivity", "The onCreate() event");
    }

    private void count() {
        Cursor cursor = null;
        try {
            ContentResolver cr = this.getContentResolver();
            String[] columns = new String[]{
                    MediaStore.Images.ImageColumns._ID};
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
            if (cursor != null)
                totalCount = cursor.getCount();
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private void fetch() {
        Cursor cursor = null;
        try {
            ContentResolver cr = this.getContentResolver();
            String[] columns = new String[]{
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.TITLE,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.MIME_TYPE,
                    MediaStore.Images.ImageColumns.SIZE};
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);

            int offset = 0;
            int processed = 0;
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    if (offset < PAGE_SIZE * currentPage) {
                        offset++;
                        continue;
                    }
                    int id = cursor.getInt(0);
                    String title = cursor.getString(1);
                    String data = cursor.getString(2);
                    String mime = cursor.getString(3);
                    int size = cursor.getInt(4);
                    adapter.add(new PhotoTO(id, title, data, mime, size));
                    Log.d("PhonePhotosListActivity", "Přidávám fotku id: " + id);
                    processed++;
                    if (processed == PAGE_SIZE)
                        break;
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private void createComponents() {
        final String btnCaptionPrefix = "Odeslat na server";
        final Button sendBtn = findViewById(R.id.phonePhotosListSendButton);
        sendBtn.setText(btnCaptionPrefix);
        PhotosUploadButtonFactory.setUploadButtonListener(sendBtn, this, choosenPhotos);

        final Button selectAllBtn = findViewById(R.id.phonePhotosListSelectAllButton);
        selectAllBtn.setText("Vybrat vše");
        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Map.Entry<Integer, CheckBox> entry : checkBoxMap.entrySet()) {
                    entry.getValue().setChecked(true);
                }
            }
        });

        final Button deselectAllBtn = findViewById(R.id.phonePhotosListDeselectAllButton);
        deselectAllBtn.setText("Nevybrat nic");
        deselectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Map.Entry<Integer, CheckBox> entry : checkBoxMap.entrySet()) {
                    entry.getValue().setChecked(false);
                }
            }
        });

        listView = findViewById(R.id.phonePhotosList);
        listView.addFooterView(progressBar = new ProgressBar(this));
        adapter = new PhotoArrayAdapter(this, R.layout.photo_listview_row, new PhotoArrayAdapter.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(PhotoTO item, boolean isChecked) {
                if (isChecked) {
                    choosenPhotos.put(item.getId(), item);
                } else {
                    choosenPhotos.remove(item.getId());
                }
                if (choosenPhotos.isEmpty()) {
                    sendBtn.setText(btnCaptionPrefix);
                    sendBtn.setEnabled(false);
                } else {
                    sendBtn.setText(btnCaptionPrefix + " (" + choosenPhotos.size() + ")");
                    sendBtn.setEnabled(true);
                }
            }
        }, new PhotoArrayAdapter.OnCheckboxCreationListener() {

            @Override
            public void onCreation(PhotoTO item, CheckBox checkBox) {
                checkBoxMap.put(item.getId(), checkBox);
            }
        });
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new LazyLoaderScrollListener() {
            @Override
            public void loadMore(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("PhonePhotosListActivity", "currentPage: " + currentPage + ", totalCount: " + totalCount);
                if (totalCount > currentPage * PAGE_SIZE) {
                    fetch();
                    currentPage++;
                }
                if (totalCount <= currentPage * PAGE_SIZE) {
                    listView.removeFooterView(progressBar);
                }
            }
        });
    }

}
