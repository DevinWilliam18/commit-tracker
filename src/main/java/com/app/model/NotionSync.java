package com.app.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Document(collation = "commit")
@Data
public class NotionSync {

    @Id
    private String id;

    private String platform;

    private String title;

    private String topic;

    private String difficulty;

    private boolean syncedToNotion;

    private Timestamp createdAt;

    private Timestamp updatedAt;

}
