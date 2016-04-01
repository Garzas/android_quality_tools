package com.appunite.debugutils.models;

public class Episode {
    private String Title;
    private String Released;
    private String Episode;
    private String imdbRating;
    private String imdbID;


    public Episode(String title, String released, String episode, String imdbRating, String imdbID) {
        Title = title;
        Released = released;
        Episode = episode;
        this.imdbRating = imdbRating;
        this.imdbID = imdbID;
    }

    public String getTitle() {
        return Title;
    }

    public String getReleased() {
        return Released;
    }

    public int getEpisode() {
        return Integer.parseInt(Episode);
    }

    public Double getImdbRating() {
        return Double.parseDouble(imdbRating);
    }

    public String getImdbID() {
        return imdbID;
    }
}
