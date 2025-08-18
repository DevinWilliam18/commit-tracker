package com.app.dao;

import com.app.model.NotionSync;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommitDao extends MongoRepository<NotionSync, String> {
    List<NotionSync> findBySyncedToNotionFalse();

}
