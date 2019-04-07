package cz.gattserver.android.photogallery;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.OnSuccessAction;
import cz.gattserver.android.common.URLTaskInfoBundle;
import cz.gattserver.android.common.URLMultipartTask;
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

                    Log.d("PhotoUploadActivity", "id: " + cursor.getType(0));
                    Log.d("PhotoUploadActivity", "title: " + cursor.getType(1));
                    Log.d("PhotoUploadActivity", "data: " + cursor.getType(2));
                    Log.d("PhotoUploadActivity", "mime: " + cursor.getType(3));
                    Log.d("PhotoUploadActivity", "size: " + cursor.getType(4));

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

            final Button sendBtn = new Button(this);
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(PhotoUploadActivity.this)
                            .setTitle("Odeslání fotek")
                            .setMessage("Opravdu odeslat fotky?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    URLMultipartTask<PhotoUploadActivity> uploadTask = new URLMultipartTask<>(PhotoUploadActivity.this,
                                            new OnSuccessAction<PhotoUploadActivity>() {
                                                @Override
                                                public void run(PhotoUploadActivity urlTaskClient, URLTaskInfoBundle result) {
                                                    if (result.isSuccess()) {
                                                        if (result.getResponseCode() == 200) {
                                                            Toast.makeText(urlTaskClient, "Fotky byly úspěšně nahrány", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            new AlertDialog.Builder(PhotoUploadActivity.this)
                                                                    .setTitle("Chyba")
                                                                    .setMessage("Fotky se nezdařilo odeslat -- server code: " + result.getResponseCode())
                                                                    .setIcon(android.R.drawable.ic_dialog_alert).show();
                                                        }
                                                    } else {
                                                        new AlertDialog.Builder(PhotoUploadActivity.this)
                                                                .setTitle("Chyba")
                                                                .setMessage("Fotky se nezdařilo odeslat: " + result.getError().getLocalizedMessage())
                                                                .setIcon(android.R.drawable.ic_dialog_alert).show();
                                                    }
                                                }
                                            });
                                    SimpleDateFormat sdf = new SimpleDateFormat("d_M_yyyy_HH_mm");
                                    List<String> params = new ArrayList<>();
                                    params.add(Config.PG_CREATE);
                                    params.add("PG_import_" + sdf.format(new Date()));
                                    for (PhotoTO p : choosenPhotos.values()) {
                                        params.add(p.getData());
                                        params.add(new File(p.getData()).getName());
                                    }
                                    String[] arrParams = new String[params.size()];
                                    arrParams = params.toArray(arrParams);
                                    uploadTask.execute(arrParams);
                                    constructTable();
                                }
                            }).setNegativeButton(android.R.string.no, null).show();
                }
            });
            final String btnCaptionPrefix = "Odeslat na server";
            sendBtn.setGravity(Gravity.CENTER);
            sendBtn.setEnabled(false);
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
