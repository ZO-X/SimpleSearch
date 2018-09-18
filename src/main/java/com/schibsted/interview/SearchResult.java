package com.schibsted.interview;

public class SearchResult {

    private final String fileName;
    private final Integer score;

    public SearchResult(String fileName, Integer score) {
        this.fileName = fileName;
        this.score = score;
    }

    public String getFileName() {
        return fileName;
    }

    public Integer getScore() {
        return score;
    }
}
