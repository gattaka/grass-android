package cz.gattserver.android.articles;

import android.content.Intent;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.LoginUtils;
import cz.gattserver.android.common.URLTaskParamTO;
import cz.gattserver.android.interfaces.ItemTO;
import cz.gattserver.android.lazyloader.FilteredLazyListActivity;
import cz.gattserver.android.photogallery.PhotogalleryActivity;

public class ArticlesActivity extends FilteredLazyListActivity<ItemTO> {

    public ArticlesActivity() {
        super(R.layout.activity_articles, "Články");
    }

    @Override
    protected ItemTO constructTO(JSONObject jsonObject) throws JSONException {
        return new ItemTO(jsonObject.getString("name"), jsonObject.getString("contentID"));
    }

    @Override
    protected void onItemClick(ItemTO item) {
        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra("id", item.getId());
        startActivity(intent);
    }

    @Override
    protected ViewGroup getLayout() {
        return findViewById(R.id.articlesLayout);
    }

    @Override
    protected URLTaskParamTO createCountTaskParamTO(String filter) {
        return new URLTaskParamTO(Config.ARTICLES_COUNT_RESOURCE + "?filter=" + filter, LoginUtils.getSessionid(this));
    }

    @Override
    protected URLTaskParamTO createFetchTaskParamTO(String filter, int pageSize, int page) {
        return new URLTaskParamTO(Config.ARTICLES_LIST_RESOURCE + "?pageSize=" + pageSize + "&page=" + page + "&filter=" + filter, LoginUtils.getSessionid(this));
    }

}
