package com.app.model;

import lombok.Data;

import java.util.List;


public class GithubWebhook {
    private String ref;
    private String pusher;
    private List<Commit> commits;


}
