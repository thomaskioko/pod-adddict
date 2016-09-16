package com.thomaskioko.podadddict.app.api.model.responses;

import com.thomaskioko.podadddict.app.api.model.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Kioko
 */

public class ItunesLookUpResponse {
    private Integer resultCount;
    private List<Result> results = new ArrayList<>();

    /**
     * @return The resultCount
     */
    public Integer getResultCount() {
        return resultCount;
    }

    /**
     * @param resultCount The resultCount
     */
    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }

    /**
     * @return The results
     */
    public List<Result> getResults() {
        return results;
    }

    /**
     * @param results The results
     */
    public void setResults(List<Result> results) {
        this.results = results;
    }

}
