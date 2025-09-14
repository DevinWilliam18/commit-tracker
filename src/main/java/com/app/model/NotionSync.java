package com.app.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Document("commit")
@Data
public class NotionSync {

    @Id
    private String id;

    String notionDatabaseId;

    private String platform;

    private String title;

    private String topic;

    private String difficulty;

    private String path;

    private boolean syncedToNotion;

    private Timestamp createdAt;

    private Timestamp updatedAt;

}
