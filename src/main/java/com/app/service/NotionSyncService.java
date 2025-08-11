package com.app.service;

import com.app.model.GitlabWebhook;
import com.app.model.NotionSync;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface NotionSyncService {

    List<NotionSync> getSynedToNotionFalse();

    void syncToNotion(GitlabWebhook gitlabWebhook);

    ResponseEntity<String> pushToNotion(NotionSync notionSync);

    Map<String, Object> dataToNotion(NotionSync notionSync);

    NotionSync convertMessage(GitlabWebhook gitlabWebhook);

}
