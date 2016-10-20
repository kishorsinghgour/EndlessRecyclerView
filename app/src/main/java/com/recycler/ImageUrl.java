package com.recycler;

import java.io.Serializable;

public class ImageUrl implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    public String getUrl() {
        return url;
    }

    private String url;

    public ImageUrl(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }
}
