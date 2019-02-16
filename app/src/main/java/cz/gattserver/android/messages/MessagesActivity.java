package cz.gattserver.android.messages;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.interfaces.ItemTO;
import cz.gattserver.android.lazyloader.LazyListActivity;
import cz.gattserver.android.recipes.RecipeActivity;

public class MessagesActivity extends GrassActivity {

    private ListView listView;
    private ArrayAdapter<ItemTO> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        setTitle("Zprávy");

        listView = new ListView(this);
        adapter = new ArrayAdapter<ItemTO>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO
            }
        });


        // public static final String INBOX = "content://sms/inbox";
        // public static final String SENT = "content://sms/sent";
        // public static final String DRAFT = "content://sms/draft";
        for (String directory : new String[]{"content://sms/inbox", "content://sms/sent", "content://sms/draft"}) {
            Cursor cursor = getContentResolver().query(Uri.parse(directory), null, null, null, null);
            if (cursor.moveToFirst()) { // must check the result to prevent exception
                do {
                    String msgData = "";
                    for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                        msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                    }
                    adapter.add(new ItemTO(msgData, String.valueOf(adapter.getCount())));
                    // use msgData
                } while (cursor.moveToNext());
            } else {
                // empty box, no SMS
            }
        }
        setContentView(listView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }


}
