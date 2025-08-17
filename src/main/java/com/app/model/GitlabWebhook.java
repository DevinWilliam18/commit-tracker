package com.app.model;

import lombok.Data;

import java.util.List;


@Data
public class GitlabWebhook {
    private String ref;
    private String pusher;
    private String commit;

}
