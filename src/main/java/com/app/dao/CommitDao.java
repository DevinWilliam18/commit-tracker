package com.app.dao;

import com.app.model.NotionSync;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommitDao extends MongoRepository<NotionSync, String> {
    List<NotionSync> findBySyncedToNotionFalse();

}
