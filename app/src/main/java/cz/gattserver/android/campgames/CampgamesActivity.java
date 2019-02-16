package cz.gattserver.android.campgames;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.interfaces.ItemTO;
import cz.gattserver.android.lazyloader.LazyListActivity;

public class CampgamesActivity extends LazyListActivity<ItemTO> {

    public CampgamesActivity() {
        super(R.layout.activity_campgames, "Táborové hry", Config.CAMPGAMES_COUNT_RESOURCE);
    }

    @Override
    protected ItemTO constructTO(JSONObject jsonObject) throws JSONException {
        return new ItemTO(jsonObject.getString("name"), jsonObject.getString("id"));
    }

    @Override
    protected void onItemClick(ItemTO item) {
        Intent intent = new Intent(this, CampgameActivity.class);
        intent.putExtra("id", item.getId());
        startActivity(intent);
    }

    @Override
    protected String createFetchURL(int pageSize, int page) {
        return Config.CAMPGAMES_LIST_RESOURCE + "?pageSize=" + pageSize + "&page=" + page;
    }
}
