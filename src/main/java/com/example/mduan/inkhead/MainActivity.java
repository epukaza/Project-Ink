package com.example.mduan.inkhead;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements AbsListView.OnScrollListener, AbsListView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private StaggeredGridView mGridView;
    private StaggeredAdapter mAdapter;

    private String TAG = "hello";
    private boolean mHasRequestedMore = false;
    private ArrayList<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enable Local Datastore.
        //Parse.enableLocalDatastore(this);
        Parse.initialize(this, "lPxykPJYeGFvC4wntofRpIfurIxBsD5F863NZKOM", "x3ofUyzEhn2m7CY4F875HPr0wDGQiYQwHkB1VpRB");
        mData = generateData();
        mGridView = (StaggeredGridView) findViewById(R.id.grid_view);
        mAdapter = new StaggeredAdapter(this,R.layout.list_item_image_view, generateData());
        mGridView.setAdapter(mAdapter);
        mGridView.setOnScrollListener(this);
        mGridView.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Toast.makeText(this, "Item Clicked: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
        Log.d(TAG, "onScrollStateChanged:" + scrollState);
    }

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        Log.d(TAG, "onScroll firstVisibleItem:" + firstVisibleItem +
                " visibleItemCount:" + visibleItemCount +
                " totalItemCount:" + totalItemCount);
        // our handling
        if (!mHasRequestedMore) {
            int lastInScreen = firstVisibleItem + visibleItemCount;
            if (lastInScreen >= totalItemCount) {
                Log.d(TAG, "onScroll lastInScreen - so load more");
                mHasRequestedMore = true;
                onLoadMoreItems();
            }
        }
    }

    private void onLoadMoreItems() {
        final ArrayList<String> sampleData = generateData();
        for (String data : sampleData) {
            mAdapter.add(data);
        }
        // stash all the data in our backing store
        mData.addAll(sampleData);
        // notify the adapter that we can update now
        mAdapter.notifyDataSetChanged();
        mHasRequestedMore = false;
    }
    private ArrayList<String> generateData() {

        List<ParseObject> parseUrls = null;
        ParseQuery<ParseObject> imageUrls = new ParseQuery<ParseObject>("testImages");
        ArrayList<String> listData = new ArrayList<String>();

        imageUrls.whereExists("URL");
        try {
            parseUrls = imageUrls.find();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "Could not connect to Parse!");
        }

        String temp;
        for(ParseObject p : parseUrls){
            listData.add(p.getString("URL").replace("https", "http").replace(".jpg", "m.jpg"));
        }

        /*
        listData.add("http://i.imgur.com/axsMCaZm.jpg");
        listData.add("http://i.imgur.com/ellWkvRm.jpg");
        listData.add("http://i.imgur.com/FPJRWm.jpg");
        listData.add("http://i.imgur.com/u39aGbHm.jpg");
        listData.add("http://i.imgur.com/xCVfzgdm.jpg");
        listData.add("http://i.imgur.com/V5ZfbTBm.jpg");
        listData.add("http://i.imgur.com/tUJxHXjm.jpg");
        listData.add("http://i.imgur.com/yioKkAom.jpg");
        listData.add("http://i.imgur.com/IjtraNpm.jpg");
        */

/*        ArrayList<String> listData = new ArrayList<String>();
        listData.add("http://i62.tinypic.com/2iitkhx.jpg");
        listData.add("http://i61.tinypic.com/w0omeb.jpg");
        listData.add("http://i60.tinypic.com/w9iu1d.jpg");
        listData.add("http://i60.tinypic.com/iw6kh2.jpg");
        listData.add("http://i57.tinypic.com/ru08c8.jpg");
        listData.add("http://i60.tinypic.com/k12r10.jpg");
        listData.add("http://i58.tinypic.com/2e3daug.jpg");
        listData.add("http://i59.tinypic.com/2igznfr.jpg");
*/
        return listData;
    }
}
