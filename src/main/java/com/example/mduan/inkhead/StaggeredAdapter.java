package com.example.mduan.inkhead;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.parse.ParseObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Abhinav on 4/23/2015.
 */
public class StaggeredAdapter extends ArrayAdapter<ParseObject>{
    private static final String TAG = "StaggeredAdapter";

    private final LayoutInflater mLayoutInflater;
    private final Random mRandom;
    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();
    private LruCache mMemoryCache;

    public StaggeredAdapter(Context context, int textViewResourceId, ArrayList<ParseObject> objects) {
        super(context, textViewResourceId, objects);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mRandom = new Random();
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
        final int cacheSize = maxMemory/8;
        Log.d(TAG, "Initializing cache with " + cacheSize + "kb of memory");
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //return size in kb
                return value.getByteCount()/1024;
            }
        };
    }

    @Override
    public View getView(final int position, View convertView,
                        final ViewGroup parent) {

        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_image_view,
                    parent, false);
            vh = new ViewHolder();
            vh.imgView = (DynamicHeightImageView) convertView
                    .findViewById(R.id.imgView);
            vh.textView = (TextView) convertView.findViewById(R.id.text_short_description);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.imgView.setHeightRatio(getPositionRatio(position));
        vh.textView.setText(getShortDesc(position));

        /*
            okay, so first check if imageurl is found in LRU cache, and set bitmap if it is, if not then fetch url,
        */
        if(mMemoryCache.get(getMediumURL(position))==null){
            new DownloadImageTask(vh.imgView).execute(getMediumURL(position));
        }
        else
            vh.imgView.setImageBitmap((Bitmap)mMemoryCache.get(getMediumURL(position)));
//        getImage();
//        new DownloadImageTask(vh.imgView).execute(getMediumURL(position));

        return convertView;
    }

    static class ViewHolder {
        DynamicHeightImageView imgView;
        TextView textView;
    }

    private void getImage(){

    }
    public String getShortDesc(int position){
        return getItem(position).getString("shortDescription");
    }

    public String getMediumURL(int position){
        return getItem(position).getString("URL").replace("https","http").replace(".jpg", "m.jpg");
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        String mUrl;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            mUrl = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(mUrl).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }


        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            mMemoryCache.put(mUrl, result);
        }
    }

    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        // if not yet done generate and stash the columns height
        // in our real world scenario this will be determined by
        // some match based on the known height and width of the image
        // and maybe a helpful way to get the column height!
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
            Log.d(TAG, "getPositionRatio:" + position + " ratio:" + ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5
        // the width
    }
}