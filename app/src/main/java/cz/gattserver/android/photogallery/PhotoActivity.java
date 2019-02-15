package cz.gattserver.android.photogallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ZoomControls;

import java.net.URLEncoder;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.URLTask;

public class PhotoActivity extends GrassActivity {

    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView imageView;

    private static class PhotoActivityInitAction implements URLTask.OnSuccessAction<PhotoActivity> {
        @Override
        public void run(PhotoActivity urlTaskClient, URLTask.URLTaskInfoBundle bundle) {
            urlTaskClient.init(bundle.getResult());
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
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
        fetchTask.execute(Config.PHOTO_DETAIL_RESOURCE + "?id=" + id + "&fileName=" + URLEncoder.encode(name));

        Log.d("PhotoActivity", "The onCreate() event");
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    public void init(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }
}
