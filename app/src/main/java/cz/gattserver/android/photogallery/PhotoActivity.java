package cz.gattserver.android.photogallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ZoomControls;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.URLTask;

public class PhotoActivity extends GrassActivity {

    private float currentZoom;
    private ScaleAnimation scaleAnim;

    private static class PhotoActivityInitAction implements URLTask.OnSuccessAction<PhotoActivity> {
        @Override
        public void run(PhotoActivity urlTaskClient, URLTask.URLTaskInfoBundle bundle) {
            urlTaskClient.init(bundle.getResult());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        hideActionBar();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");

        URLTask<PhotoActivity> fetchTask = new URLTask<>(this, new PhotoActivityInitAction());

        // http://localhost:8180/web/ws/pg/photo?id=364&fileName=shocked_kittens_cr.jpg
        fetchTask.execute(Config.PHOTO_DETAIL_RESOURCE + "?id=" + id + "&fileName=" + name);

        Log.d("PhotoActivity", "The onCreate() event");
    }

    public void init(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        final ImageView imageView = findViewById(R.id.itemImage);
        final ZoomControls zc = (ZoomControls) findViewById(R.id.zoomControl);
        imageView.setImageBitmap(bitmap);

        zc.setOnZoomInClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                float oldZoom = currentZoom;
                currentZoom = currentZoom * 1.25f;
                zc.setIsZoomOutEnabled(true);
                if (3.0 < currentZoom) {
                    zc.setIsZoomInEnabled(false);
                }
                scaleAnim = new ScaleAnimation(oldZoom, currentZoom, oldZoom, currentZoom, 0, 0);
                scaleAnim.setFillAfter(true);
                imageView.startAnimation(scaleAnim);
            }
        });
        zc.setOnZoomOutClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                float oldZoom = currentZoom;
                currentZoom = currentZoom / 1.25f;
                zc.setIsZoomInEnabled(true);
                if (0.33 > currentZoom) {
                    zc.setIsZoomOutEnabled(false);
                }
                scaleAnim = new ScaleAnimation(oldZoom, currentZoom, oldZoom, currentZoom, 0, 0);
                scaleAnim.setFillAfter(true);
                imageView.startAnimation(scaleAnim);
            }
        });
    }
}
