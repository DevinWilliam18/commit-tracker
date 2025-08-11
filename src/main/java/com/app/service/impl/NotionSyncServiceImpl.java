package com.app.service.impl;

import com.app.dao.CommitDao;
import com.app.model.GitlabWebhook;
import com.app.model.NotionSync;
import com.app.service.NotionSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
public class NotionSyncServiceImpl implements NotionSyncService {

    private String parentParams = "parent";
    private String propertiesParams = "properties";
    private String titleParams = "title";

    private String platformParams = "Platform";

    private String topicParams = "Topic";

    private String difficultyParams = "Difficulty";

    private String commitDateParams = "Commit Date";

    @Value("${notion.url.add-row}")
    private String ADD_ROW_URL;

    @Value("${notion.security.token}")
    private String NOTION_TOKEN;

    @Value("${notion.database.id}")
    private String NOTION_DATABASE_ID;

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
    public void syncToNotion(GitlabWebhook gitlabWebhook) {

        //separate the commit message into some portions
        log.info("test");
        NotionSync sync = convertMessage(gitlabWebhook);

        //store it on DB
        sync.setSyncedToNotion(true);
        commitDao.save(sync);

        pushToNotion(sync);

        //find the existing data and change the syncedToNotion status to be 'true'
        // List<NotionSync> unsyncedCommits = getSynedToNotionFalse();


    }

    @Override
    public ResponseEntity<String> pushToNotion(NotionSync notionSync) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(NOTION_TOKEN);
        headers.set("Notion-Version", "2022-06-28");

        //set auth key
        Map<String, Object> mappingsToNotion = dataToNotion(notionSync);


        HttpEntity<Map<String, Object>> request = new HttpEntity<>(mappingsToNotion, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(ADD_ROW_URL, request, String.class);
        return response;
    }

    @Override
    public Map<String, Object> dataToNotion(NotionSync notionSync) {


        Map<String, Object> mappings = new HashMap<>();
        Map<String, Map<String, String>> options = new HashMap<>();

        log.info("{}", notionSync.getTitle());
        log.info("{}", notionSync.getTopic());
        log.info("{}", notionSync.getDifficulty());
        log.info("{}", notionSync.getPlatform());
        log.info("{}", notionSync.getCreatedAt().toString());
        log.info("testing");
        mappings.put(parentParams, Map.of("database_id", NOTION_DATABASE_ID));

        Map<String, Object> properties = new HashMap<>();

        properties.put(titleParams, Map.of("title", Arrays.asList(Map.of("text", Map.of("content", notionSync.getTitle())))));

        properties.put(platformParams, Map.of("select", Map.of("name", notionSync.getPlatform())));

        properties.put(topicParams, Map.of("select", Map.of("name", notionSync.getTopic())));

        properties.put(difficultyParams, Map.of("select", Map.of("name", notionSync.getDifficulty())));

        properties.put(commitDateParams, Map.of("date", Map.of("start", notionSync.getCreatedAt().toString())));

        mappings.put(propertiesParams, properties);

        return mappings;
    }

    @Override
    public NotionSync convertMessage(GitlabWebhook gitlabWebhook) {
        NotionSync sync = new NotionSync();
        String input = gitlabWebhook.getCommit();
        long time = System.currentTimeMillis();
        log.info("commit: {}", gitlabWebhook.getCommit());
        String regex = "(.*?)\\[(.*?)\\]\\[(.*?)\\]\\[(.*?)\\]";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        log.info("matcher.matches(): {}", matcher.matches());

        if (matcher.matches()){
            log.info("{}",matcher.group(1));
            sync.setTitle(matcher.group(1));
            sync.setTopic(matcher.group(2));
            sync.setPlatform(matcher.group(3));
            sync.setDifficulty(matcher.group(4));
        }

        //set time
        sync.setCreatedAt(new Timestamp(time));

        return sync;

    }

}
