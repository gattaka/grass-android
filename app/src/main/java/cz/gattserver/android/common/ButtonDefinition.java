package cz.gattserver.android.common;

import android.view.View;

public class ButtonDefinition {

    private String caption;
    private View.OnClickListener clickListener;

    public ButtonDefinition(String caption, View.OnClickListener clickListener) {
        this.caption = caption;
        this.clickListener = clickListener;
    }

    public String getCaption() {
        return caption;
    }

    public View.OnClickListener getClickListener() {
        return clickListener;
    }

}
