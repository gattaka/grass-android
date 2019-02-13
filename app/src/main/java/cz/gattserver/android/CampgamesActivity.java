package cz.gattserver.android;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.interfaces.CampgameTO;
import cz.gattserver.android.lazyloader.LazyListActivity;

public class CampgamesActivity extends LazyListActivity<CampgameTO> {

    public CampgamesActivity() {
        super(R.layout.activity_campgames, "Táborové hry", Config.CAMPGAMES_COUNT_RESOURCE);
    }

    @Override
    protected CampgameTO constructTO(JSONObject jsonObject) throws JSONException {
        return new CampgameTO(jsonObject.getString("name"), jsonObject.getString("id"));
    }

    @Override
    protected void onItemClick(CampgameTO item) {
        Intent intent = new Intent(this, CampgameActivity.class);
        intent.putExtra("id", item.getId());
        startActivity(intent);
    }

    @Override
    protected String createFetchURL(int pageSize, int page) {
        return Config.CAMPGAMES_LIST_RESOURCE + "?pageSize=" + pageSize + "&page=" + page;
    }
}
