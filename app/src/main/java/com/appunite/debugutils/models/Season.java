package com.appunite.debugutils.models;

import java.util.ArrayList;
import java.util.List;

public class Season {

    private String Title;
    private String Season;
    private List<Episode> Episodes = new ArrayList<>();
    private String Response;

    public Season(String title, String season, List<Episode> episodes, String response) {
        Title = title;
        Season = season;
        Episodes = episodes;
        Response = response;
    }

    public String getTitle() {
        return Title;
    }

    public int getSeason() {
        return Integer.parseInt(Season);
    }

    public List<Episode> getEpisodes() {
        return Episodes;
    }

    public String getResponse() {
        return Response;
    }

    public boolean hasResponse() {
        return Response.equalsIgnoreCase("true");
    }

    @Override
    public String toString() {
        return "Season{" +
                "Title='" + Title + '\'' +
                ", Season='" + Season + '\'' +
                ", Episodes=" + Episodes +
                ", Response='" + Response + '\'' +
                '}';
    }
}
