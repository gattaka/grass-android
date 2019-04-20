package cz.gattserver.android.photogallery;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.gattserver.android.R;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.interfaces.PhotoTO;

public class PhotoUploadActivity extends GrassActivity {

    private Map<Integer, PhotoTO> choosenPhotos = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoupload);
        setTitle("Fotky");

        constructTable();
    }

    private void constructTable() {
        TableLayout tableLayout = findViewById(R.id.photoTableLayout);
        tableLayout.removeAllViews();

        final List<PhotoTO> photos = new ArrayList<>();
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

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0);
                    String title = cursor.getString(1);
                    String data = cursor.getString(2);
                    String mime = cursor.getString(3);
                    int size = cursor.getInt(4);
                    photos.add(new PhotoTO(id, title, data, mime, size));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }


        if (photos.isEmpty()) {

            TableRow row = new TableRow(this);
            tableLayout.addView(row);

            TextView bodyTextView = new TextView(this);
            bodyTextView.setText("Nebyly nalezeny žádné fotky");
            bodyTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT));
            bodyTextView.setGravity(Gravity.CENTER);
            row.addView(bodyTextView);

        } else {

            final String btnCaptionPrefix = "Odeslat na server";
            final Button sendBtn = PhotosUploadButtonFactory.createUploadButton(PhotoUploadActivity.this, choosenPhotos);
            tableLayout.addView(sendBtn);

            for (final PhotoTO m : photos) {
                TableRow row = new TableRow(this);
                tableLayout.addView(row);

                CheckBox checkBox = new CheckBox(this);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            choosenPhotos.put(m.getId(), m);
                        } else {
                            choosenPhotos.remove(m.getId());
                        }
                        if (choosenPhotos.isEmpty()) {
                            sendBtn.setText(btnCaptionPrefix);
                            sendBtn.setEnabled(false);
                        } else {
                            sendBtn.setText(btnCaptionPrefix + " (" + choosenPhotos.size() + ")");
                            sendBtn.setEnabled(true);
                        }
                    }
                });
                checkBox.setChecked(true);
                row.addView(checkBox);

                ImageView imageView = new ImageView(this);
                File image = new File(m.getData());
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                bitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, true);
                imageView.setImageBitmap(bitmap);
                row.addView(imageView);
            }
        }
    }
}
