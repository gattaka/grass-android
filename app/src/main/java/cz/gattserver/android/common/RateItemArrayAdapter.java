package cz.gattserver.android.common;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import cz.gattserver.android.R;
import cz.gattserver.android.interfaces.RatedItemTO;

public class RateItemArrayAdapter extends ArrayAdapter<RatedItemTO> {

    private int resource;

    public RateItemArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.resource = resource;
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView,
                 @NonNull ViewGroup parent) {
        return createViewFromResource(LayoutInflater.from(getContext()), position, convertView, parent, resource);
    }

    private @NonNull
    View createViewFromResource(@NonNull LayoutInflater inflater, int position, @Nullable View convertView, @NonNull ViewGroup parent, int resource) {

        final View view;
        final TextView text;

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        final RatedItemTO item = getItem(position);

        TextView ratingTextView = view.findViewById(R.id.itemRating);
        ratingTextView.setText(item.getRating());

        TextView nameTextView = view.findViewById(R.id.itemName);
        nameTextView.setText(item.getName());

        return view;
    }

}
