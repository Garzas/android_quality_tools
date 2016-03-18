package com.appunite.debugutils.models;

import com.google.gson.annotations.SerializedName;

public class MoviesRequest {
    @SerializedName("s")
    private final String title;
    @SerializedName("type")
    private final String type;
    @SerializedName("y")
    private final Integer year;


    public MoviesRequest(String title, String type, Integer year) {
        this.title = title;
        this.type = type;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public Integer getYear() {
        return year;
    }
}
