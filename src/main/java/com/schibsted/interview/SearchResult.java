package com.schibsted.interview;

public class SearchResult {

    private String fileName;
    private Integer score;

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
