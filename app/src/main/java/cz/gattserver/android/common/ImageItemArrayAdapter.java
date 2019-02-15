package cz.gattserver.android.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cz.gattserver.android.R;
import cz.gattserver.android.interfaces.ImageItemTO;

public class ImageItemArrayAdapter extends ArrayAdapter<ImageItemTO> {

    private int resource;

    public ImageItemArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.resource = resource;
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

        final ImageItemTO item = getItem(position);
        if (item != null) {
            TextView nameTextView = view.findViewById(R.id.itemName);
            nameTextView.setText(item.getName());

            ImageView imageView = view.findViewById(R.id.itemImage);
            imageView.setImageBitmap(item.getBitmap());
        }

        return view;
    }

}
