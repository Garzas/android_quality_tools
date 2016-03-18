package com.appunite.debugutils.models;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {

    private List<com.appunite.debugutils.models.Search> Search;
    private String Response;
    private String Error;

    public List<Search> getMoviesList() {
        return Search;
    }

    public List<String> getErrorList(){
        List<String> eList= new ArrayList<>();
        eList.add(Error);
        return eList;
    }
    public String getError() {
        return Error;
    }
}
