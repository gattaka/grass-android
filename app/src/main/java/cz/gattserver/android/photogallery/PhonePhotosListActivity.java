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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private List<PhotoTO> fetchItems(int offset, int count, boolean idOnly) {
        List<PhotoTO> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            ContentResolver cr = this.getContentResolver();
            String[] columns;
            if (idOnly) {
                columns = new String[]{MediaStore.Images.ImageColumns._ID};
            } else {
                columns = new String[]{
                        MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.TITLE,
                        MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.MIME_TYPE,
                        MediaStore.Images.ImageColumns.SIZE};
            }
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);

            int skipped = 0;
            int processed = 0;
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    if (skipped < offset) {
                        skipped++;
                        continue;
                    }
                    int id = cursor.getInt(0);
                    if (idOnly) {
                        result.add(new PhotoTO(id));
                    } else {
                        String title = cursor.getString(1);
                        String data = cursor.getString(2);
                        String mime = cursor.getString(3);
                        int size = cursor.getInt(4);
                        result.add(new PhotoTO(id, title, data, mime, size));
                    }
                    processed++;
                    if (processed == count)
                        break;
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return result;
    }

    private void fetch() {
        List<PhotoTO> result = fetchItems(PAGE_SIZE * currentPage, PAGE_SIZE, false);
        adapter.addAll(result);
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
                List<PhotoTO> items = fetchItems(0, totalCount, false);
                for (PhotoTO item : items) {
                    choosenPhotos.put(item.getId(), item);
                }

                int itemCount = listView.getCount();
                for (int i = 0; i < itemCount; i++)
                    listView.setItemChecked(i, true);
            }
        });

        final Button deselectAllBtn = findViewById(R.id.phonePhotosListDeselectAllButton);
        deselectAllBtn.setText("Nevybrat nic");
        deselectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosenPhotos.clear();

                int itemCount = listView.getCount();
                for (int i = 0; i < itemCount; i++)
                    listView.setItemChecked(i, false);
            }
        });

        listView = findViewById(R.id.phonePhotosList);
        listView.addFooterView(progressBar = new ProgressBar(this));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = listView.getPositionForView(view);
                PhotoTO photo = (PhotoTO) parent.getItemAtPosition(pos);
                Toast.makeText(PhonePhotosListActivity.this, "Photo ID[" + photo.getId() + "] Name[" + photo.getTitle() + "]", Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new PhotoArrayAdapter(this, R.layout.photo_listview_row, new PhotoArrayAdapter.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, PhotoTO item, boolean isChecked) {
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
        }, new PhotoArrayAdapter.OnCheckBoxCreateListener() {
            @Override
            public void onCreate(CompoundButton buttonView, PhotoTO item) {
                buttonView.setChecked(choosenPhotos.containsKey(item.getId()));
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
