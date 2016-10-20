package com.recycler.io;


import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class HttpDataClient extends AsyncTask<String, Void, String> {

    private HttpDataListener mListener;

    public void setOnHttpDataListener(HttpDataListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected void onPostExecute(String data) {
        try {
            mListener.onHttpData(data);
        } catch (JSONException e) {
            Log.d("Error", e.getMessage());
        }
    }

    @Override
    protected String doInBackground(String... params) {
        try {

            final HttpClient httpClient = new DefaultHttpClient();
            final URI uri = getURI(params);
            HttpResponse response =  getResponse(httpClient, uri);
            return EntityUtils.toString(response.getEntity());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private HttpResponse getResponse(HttpClient httpClient, URI uri) throws IOException {
        HttpGet httpGet = new HttpGet(uri);
        return httpClient.execute(httpGet);

    }



    private URI getURI(String... params) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(params[0]);

        for (int i = 1; i < params.length; i += 2) {
            builder.addParameter(params[i], params[i + 1]);
        }

        return builder.build();
    }


}
