package cz.gattserver.android.photogallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import java.io.File;

import cz.gattserver.android.R;
import cz.gattserver.android.interfaces.PhotoTO;

public class PhotoArrayAdapter extends ArrayAdapter<PhotoTO> {

    public interface OnCheckedChangeListener {
        void onCheckedChanged(CompoundButton buttonView, PhotoTO item, boolean isChecked);
    }

    public interface OnCheckBoxCreateListener {
        void onCreate(CompoundButton buttonView, PhotoTO item);
    }

    private int resource;
    private OnCheckedChangeListener checkBoxListener;
    private OnCheckBoxCreateListener createListener;

    public PhotoArrayAdapter(@NonNull Context context, int resource, OnCheckedChangeListener checkBoxListener, OnCheckBoxCreateListener createListener) {
        super(context, resource);
        this.resource = resource;
        this.checkBoxListener = checkBoxListener;
        this.createListener = createListener;
    }

    @Override
    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createViewFromResource(LayoutInflater.from(getContext()), position, convertView, parent, resource);
    }

    @NonNull
    private View createViewFromResource(@NonNull LayoutInflater inflater, int position, @Nullable View convertView, @NonNull ViewGroup parent, int resource) {

        final View view;

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        final PhotoTO item = getItem(position);
        if (item != null) {
            CheckBox checkBox = view.findViewById(R.id.chk);
            checkBox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> checkBoxListener.onCheckedChanged(buttonView, item, isChecked));
            createListener.onCreate(checkBox, item);

            ImageView imageView = view.findViewById(R.id.itemImage);
            File image = new File(item.getData());
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            float min = 128f;
            if (h > w) {
                float r = min / h;
                h = (int) min;
                w = (int) (r * w);
            } else {
                float r = min / w;
                w = (int) min;
                h = (int) (r * h);
            }

            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            imageView.setImageBitmap(bitmap);
        }

        return view;
    }
}