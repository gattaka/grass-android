package cz.gattserver.android.common;

import android.content.Context;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class CustomListView extends ListView {

    public CustomListView(Context context) {
        super(context);
    }

    protected HeaderViewListAdapter wrapHeaderListAdapterInternal(
            ArrayList<FixedViewInfo> headerViewInfos,
            ArrayList<ListView.FixedViewInfo> footerViewInfos,
            ListAdapter adapter) {
        return new CustomHeaderViewListAdapter(headerViewInfos, footerViewInfos, adapter);
    }
}
