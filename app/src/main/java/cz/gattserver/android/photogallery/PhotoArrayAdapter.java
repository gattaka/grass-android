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
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

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
    public View getView(int position, @Nullable View convertView,
                        @NonNull ViewGroup parent) {
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
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkBoxListener.onCheckedChanged(buttonView, item, isChecked);
                }
            });
            createListener.onCreate(checkBox, item);

            ImageView imageView = view.findViewById(R.id.itemImage);
            File image = new File(item.getData());
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, true);
            imageView.setImageBitmap(bitmap);

            TextView textView = view.findViewById(R.id.name);
            textView.setText(String.valueOf(item.getId()));
        }

        return view;
    }

}
