package com.app.service;

import com.app.model.NotionSync;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface NotionSyncService {

    List<NotionSync> getSynedToNotionFalse();

    void syncToNotion();

    void pushToNotion(NotionSync notionSync);

    Map<String, Object> dataToNotion(NotionSync notionSync);

}
