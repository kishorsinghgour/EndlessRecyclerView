package com.recycler;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.recycler.io.HttpDataClient;
import com.recycler.io.HttpDataListener;
import com.recycler.io.Json;
import com.recycler.vo.FlickerResponse;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvEmptyView;
    private RecyclerView mRecyclerView;
    private DataAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List imageUrlList;
    private ProgressDialog dialog;
    private int page = 1;

    private static String FLICKER_API_URL = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=1b26e14428bf2de50614b4a7f55878e7&tags=animal&text=animal&format=json&per_page=10&media=photos&page="; // use your api key here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvEmptyView = (TextView) findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        imageUrlList = new ArrayList<ImageUrl>();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Flicker Test");
        }

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DataAdapter(imageUrlList, mRecyclerView, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        //if data connection is active download more images from flicker
        if (isDataEnabled(this)) {
            loadFlickerData(page, false);
        }

        // this will add more item at bottom of the list when last item is visible
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (isDataEnabled(MainActivity.this)) {
                    imageUrlList.add(null);
                    mAdapter.notifyItemInserted(imageUrlList.size() - 1);
                    loadFlickerData(++page, true);
                }
            }
        });
    }

    private void loadFlickerData(int page, final boolean isProgressViewVisible) {
        if (!isProgressViewVisible) {
            dialog.show();
        }

        HttpDataClient httpDataClient = new HttpDataClient();
        httpDataClient.setOnHttpDataListener(new HttpDataListener() {
            @Override
            public void onHttpData(Object data) throws JSONException {
                if (dialog.isShowing()) dialog.dismiss();

                if (data != null) {

                    String response = data.toString();
                    // removing unnecessary string from response (jsonFlickrApi(....))
                    response = response.substring(14, response.length() - 1);
                    FlickerResponse flickerResponse = Json.parse(response, FlickerResponse.class);
                    FlickerResponse.Photo photo = flickerResponse.getPhotos();
                    List<FlickerResponse.Photo.PhotoInfo> photoInfoList = photo.getPhoto();

                    if (isProgressViewVisible) {
                        imageUrlList.remove(imageUrlList.size() - 1);
                        mAdapter.notifyItemRemoved(imageUrlList.size());
                        //add items one by one
                        int start = imageUrlList.size();
                        int end = start + 10;

                        for (int i = start + 1, j = 0; i <= end; i++, j++) {
                            imageUrlList.add(new ImageUrl(photoInfoList.get(j).getTitle(), getImageUrl(photoInfoList.get(j))));
                            mAdapter.notifyItemInserted(imageUrlList.size());
                        }
                        mAdapter.setLoaded();
                    } else {
                        for (FlickerResponse.Photo.PhotoInfo photoInfo : photoInfoList) {
                            imageUrlList.add(new ImageUrl(photoInfo.getTitle(), getImageUrl(photoInfo)));
                            mAdapter.notifyItemInserted(imageUrlList.size());
                        }
                    }
                }
            }
        });
        httpDataClient.execute(FLICKER_API_URL + page);
    }


    // http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
    private String getImageUrl(FlickerResponse.Photo.PhotoInfo photoInfo) {
        return String.format("http://farm%s.staticflickr.com/%s/%s_%s.jpg", photoInfo.getFarm(), photoInfo.getServer(), photoInfo.getId(), photoInfo.getSecret());
    }

    //check for data connection
    public boolean isDataEnabled(Context context) {
        boolean isConnected = false;
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
        return isConnected;
    }
}
