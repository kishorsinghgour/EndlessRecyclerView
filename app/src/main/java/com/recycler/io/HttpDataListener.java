package com.recycler.io;

import org.json.JSONException;

/**
 * Created by bvg on 21/01/2016.
 */


public interface HttpDataListener {
    public void onHttpData(Object data) throws JSONException;
}

