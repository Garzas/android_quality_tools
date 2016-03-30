package com.appunite.debugutils.models;

public class MovieShortInfo {
    private String Title;
    private String imdbID;
    private String Type;
    private String Year;
    private String Poster;


    public MovieShortInfo(String title, String imdbID, String type, String year, String poster) {
        Title = title;
        this.imdbID = imdbID;
        Type = type;
        Year = year;
        Poster = poster;
    }

    public String getTitle() {
        return Title;
    }

    public String getImdbID() {
        return imdbID;
    }

    public String getType() {
        return Type;
    }

    public String getYear() {
        return Year;
    }

    public String getPoster() {
        return Poster;
    }
}
