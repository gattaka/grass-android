package cz.gattserver.android.common;

import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

class CustomHeaderViewListAdapter extends HeaderViewListAdapter {

    public CustomHeaderViewListAdapter(ArrayList<ListView.FixedViewInfo> headerViewInfos, ArrayList<ListView.FixedViewInfo> footerViewInfos, ListAdapter adapter) {
        super(headerViewInfos, footerViewInfos, adapter);
    }

    @Override
    public int getHeadersCount(){
        //return getCount() == 0 ? 0 : super.getHeadersCount();
        return super.getHeadersCount();
    }

    @Override
    public boolean isEnabled(int position){
        try {
            return position > 0 || getHeadersCount() > 0 ? super.isEnabled(position) : false;
        } catch (IndexOutOfBoundsException e) {
            // nedaří se mi zjistit, proč se tohle občas stane
            return false;
        }
    }


}
