package com.appunite.debugutils.models;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
public class SearchResponse {

    private List<MovieShortInfo> Search;
    private String Response;
    private String Error;

    public SearchResponse(List<MovieShortInfo> search) {
        Search = search;
    }

    public List<MovieShortInfo> getMoviesList() {
        return Search;
    }


    public List<String> getErrorList() {
        List<String> eList = new ArrayList<>();
        eList.add(Error);
        return eList;
    }

    public String getError() {
        return Error;
    }

    public String getResponse() {
        return Response;
    }
}
