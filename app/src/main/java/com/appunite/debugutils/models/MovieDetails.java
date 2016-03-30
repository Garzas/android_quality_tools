package com.appunite.debugutils.models;

public class MovieDetails {

    private final String Title;
    private final String Year;
    private final String Rated;
    private final String Released;
    private final String Runtime;
    private final String Genre;
    private final String Director;
    private final String Writer;
    private final String Actors;
    private final String Plot;
    private final String Language;
    private final String Country;
    private final String Awards;
    private final String Poster;
    private final String Metascore;
    private final String imdbRating;
    private final String imdbVotes;
    private final String imdbID;
    private final String Type;
    private final String Response;

    public MovieDetails(String title, String year, String rated, String released, String runtime,
                        String genre, String director, String writer, String actors, String plot,
                        String language, String country, String awards, String poster, String metascore,
                        String imdbRating, String imdbVotes, String imdbID, String type, String response) {
        Title = title;
        Year = year;
        Rated = rated;
        Released = released;
        Runtime = runtime;
        Genre = genre;
        Director = director;
        Writer = writer;
        Actors = actors;
        Plot = plot;
        Language = language;
        Country = country;
        Awards = awards;
        Poster = poster;
        Metascore = metascore;
        this.imdbRating = imdbRating;
        this.imdbVotes = imdbVotes;
        this.imdbID = imdbID;
        Type = type;
        Response = response;
    }

    public String getTitle() {
        return Title;
    }

    public String getYear() {
        return Year;
    }

    public String getRated() {
        return Rated;
    }

    public String getReleased() {
        return Released;
    }

    public String getRuntime() {
        return Runtime;
    }

    public String getGenre() {
        return Genre;
    }

    public String getDirector() {
        return Director;
    }

    public String getWriter() {
        return Writer;
    }

    public String getActors() {
        return Actors;
    }

    public String getPlot() {
        return Plot;
    }

    public String getLanguage() {
        return Language;
    }

    public String getCountry() {
        return Country;
    }

    public String getAwards() {
        return Awards;
    }

    public String getPoster() {
        return Poster;
    }

    public String getMetascore() {
        return Metascore;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public String getImdbVotes() {
        return imdbVotes;
    }

    public String getImdbID() {
        return imdbID;
    }

    public String getType() {
        return Type;
    }

    public String getResponse() {
        return Response;
    }
}
