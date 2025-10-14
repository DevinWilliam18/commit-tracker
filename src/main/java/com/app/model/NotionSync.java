package com.app.model;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Document("commit")
@Data
@CompoundIndex(
        name = "title_platform_idx",
        def = "{'title': 1 , 'platform':1}",
        unique = true
)
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

    private Instant createdAt;

    private Instant updatedAt;

}
