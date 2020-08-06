package com.imperva.stepping;

import java.util.HashMap;

public class TestingResults {

    private HashMap<String, Data> results = new HashMap<>();

    TestingResults(HashMap<String, Data> results) {
        this.results = results;
    }

    public Data get(String subject) {
        return results.get(subject);
    }
}
