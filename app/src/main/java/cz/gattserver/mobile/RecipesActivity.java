package cz.gattserver.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

public class RecipesActivity extends GrassActivity {

    private static String msg = "GrassAPP: ";
    private static int PAGE_SIZE = 10;

    private ArrayAdapter<RecipeTO> adapter;

    private static class RecipeTO {
        private String name;
        private String id;

        public RecipeTO(String name, String id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class FetchItemsTask extends URLTask {

        private GrassActivity grassActivity;
        private ArrayAdapter<RecipeTO> adapter;

        public FetchItemsTask(GrassActivity grassActivity, ArrayAdapter<RecipeTO> adapter) {
            this.grassActivity = grassActivity;
            this.adapter = adapter;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    final JSONObject jsonObject = jsonArray.getJSONObject(i);
                    adapter.add(new RecipeTO(jsonObject.getString("name"), jsonObject.getString("id")));
                }
            } catch (JSONException e) {
                Log.e(msg, "JSONArray", e);
            }
        }
    }

    private static class LazyLoaderTask extends URLTask {

        private GrassActivity grassActivity;
        private ArrayAdapter<RecipeTO> adapter;
        private ListView listView;
        private ProgressBar progressBar;
        private int totalCount = 0;
        private int currentPage = 0;

        public LazyLoaderTask(GrassActivity grassActivity, ArrayAdapter<RecipeTO> adapter) {
            this.grassActivity = grassActivity;
            this.adapter = adapter;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            totalCount = Integer.parseInt(result);

            // create the ListView
            listView = new ListView(grassActivity);

            // add a ProgressBar as ListView's footer to indicate data load
            listView.addFooterView(progressBar = new ProgressBar(grassActivity));

            // create an adapter
            adapter = new ArrayAdapter<RecipeTO>(grassActivity, android.R.layout.simple_list_item_1);

            // plug the adapter to the ListView
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    RecipeTO recipe = adapter.getItem(position);
                    Intent intent = new Intent(grassActivity, RecipeActivity.class);
                    intent.putExtra("id", recipe.getId());
                    grassActivity.startActivity(intent);
                }
            });

            // set the ListView as the activity's content
            //LinearLayout layout = grassActivity.findViewById(R.id.recipesLayout);
            //layout.addView(listView);
            grassActivity.setContentView(listView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            // CRUCIAL PART !! Add the LazyLoader as the onScrollListener for the ListView.
            listView.setOnScrollListener(new LazyLoader() {

                // This method is called when the user is nearing the end of the ListView
                // and the ListView is ready to add more items.
                @Override
                public void loadMore(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    load();
                }
            });
        }

        private void load() {
            Log.d(msg, "load() event");
            // The ListView needs more data. So Fetch !!
            if (totalCount > currentPage * PAGE_SIZE) {
                FetchItemsTask fetchTask = new FetchItemsTask(grassActivity, adapter);
                // http://www.gattserver.cz/ws/recipes/list?pageSize=10&page=0
                fetchTask.execute(Config.RECIPES_LIST_RESOURCE + "?pageSize=" + PAGE_SIZE + "&page=" + currentPage);
                currentPage++;
            } else {
                listView.removeFooterView(progressBar);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        setTitle("Recepty");

        LazyLoaderTask lazyLoaderTask = new LazyLoaderTask(this, adapter);
        lazyLoaderTask.execute(Config.RECIPES_COUNT_RESOURCE);

        Log.d(msg, "The onCreate() event");
    }

}
