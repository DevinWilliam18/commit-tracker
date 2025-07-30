package com.app.service.impl;

import com.app.dao.CommitDao;
import com.app.model.NotionSync;
import com.app.service.NotionSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NotionSyncServiceImpl implements NotionSyncService {

    private String parentParams = "parent";
    private String propertiesParams = "properties";
    private String titleParams = "title";

    private String platformParams = "Platform";

    private String topicParams = "Topic";

    private String difficultyParams = "Difficulty";

    private String commitDateParams = "Commit Date";

    private final String CREATE_DATABASE_URL = "";

    private final String ADD_ROW_URL = "";


    @Autowired
    private CommitDao commitDao;

    private final RestTemplate restTemplate;

    @Autowired
    public NotionSyncServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public List<NotionSync> getSynedToNotionFalse() {
        List<NotionSync> data = commitDao.findBySyncedToNotionFalse();

        return data;
    }

    @Override
    public void syncToNotion() {

        List<NotionSync> unsyncedCommits = getSynedToNotionFalse();

        for (NotionSync sync: unsyncedCommits) {
            pushToNotion(sync);
            sync.setSyncedToNotion(true);
            commitDao.save(sync);
        }

    }

    @Override
    public void pushToNotion(NotionSync notionSync) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("");
        //set auth key

        Map<String, Object> mappingsToNotion = dataToNotion(notionSync);


        HttpEntity<Map<String, Object>> request = new HttpEntity<>(mappingsToNotion, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(ADD_ROW_URL, request, String.class);

    }

    @Override
    public Map<String, Object> dataToNotion(NotionSync notionSync) {
        Map<String, Object> mappings = new HashMap<>();

        mappings.put(parentParams, "");

        Map<String, Object> properties = new HashMap<>();
        properties.put(titleParams, "");
        properties.put(platformParams, "");
        properties.put(topicParams, "");
        properties.put(difficultyParams, "");
        properties.put(commitDateParams, "");

        mappings.put(propertiesParams, properties);

        return mappings;
    }
}
