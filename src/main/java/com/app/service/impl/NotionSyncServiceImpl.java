package com.app.service.impl;

import com.app.Exception.WrongCharacterPositionException;
import com.app.dao.CommitDao;
import com.app.model.GitlabWebhook;
import com.app.model.NotionSync;
import com.app.service.NotionSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.management.RuntimeErrorException;
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

    private String pathURL = "URL";

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
    public void syncToNotion(GitlabWebhook gitlabWebhook){
        NotionSync sync = null;
        List<NotionSync> unsyncedCommits = new ArrayList<>();

        try{
            //separate the commit message into some portions
            sync = convertMessage(gitlabWebhook);

            //store it on DB
            sync.setSyncedToNotion(true);

            //check if any unsynced commits stored in database
            unsyncedCommits = getSynedToNotionFalse();

            for (NotionSync notion2: unsyncedCommits) {
                notion2.setSyncedToNotion(true);
            }

            unsyncedCommits.add(sync);

            commitDao.saveAll(unsyncedCommits);

            pushToNotion(unsyncedCommits);
        }catch (DataAccessException dae){
            log.error("ERROR: {}\nat line :{}\n", dae.getMessage(), 84);
            throw dae;
        }catch(NestedRuntimeException nre){
            log.error("ERROR: {}\nat line :{}\n", nre.getMessage(), 86);

            for (NotionSync notion2: unsyncedCommits) {
                notion2.setSyncedToNotion(false);
            }

            commitDao.saveAll(unsyncedCommits);
            throw nre;
        }catch (WrongCharacterPositionException wcpe){
            log.error("ERROR: {}\nat line :{}", wcpe.getMessage(), 80);
            throw wcpe;
        }catch(Exception ex){
            log.error("ERROR: {}", ex);
            throw ex;
        }

        //find the existing data and change the syncedToNotion status to be 'true'
        // List<NotionSync> unsyncedCommits = getSynedToNotionFalse();

    }

    @Override
    public List<ResponseEntity<String>> pushToNotion(List<NotionSync> notionSync) {
        List<ResponseEntity<String>> allResponse = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(NOTION_TOKEN);
        headers.set("Notion-Version", "2022-06-28");

        //set auth key
        List<Map<String, Object>> allMappingsToNotion = dataToNotion(notionSync);

        for (Map<String, Object> mappings: allMappingsToNotion) {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(mappings, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(ADD_ROW_URL, request, String.class);
            allResponse.add(response);
        }

        return (List<ResponseEntity<String>>) allResponse;
    }

    @Override
    public List<Map<String, Object>> dataToNotion(List<NotionSync> notionSync) {

        List<Map<String, Object>> allNotionSync = new ArrayList<>();

        for (NotionSync sync: notionSync) {
            Map<String, Object> mappings = new HashMap<>();
            Map<String, Map<String, String>> options = new HashMap<>();
            Map<String, Object> properties = new HashMap<>();

            mappings.put(parentParams, Map.of("database_id", NOTION_DATABASE_ID));

            properties.put(titleParams, Map.of("title", Arrays.asList(Map.of("text", Map.of("content", sync.getTitle())))));

            properties.put(platformParams, Map.of("select", Map.of("name", sync.getPlatform())));

            properties.put(topicParams, Map.of("select", Map.of("name", sync.getTopic())));

            properties.put(difficultyParams, Map.of("select", Map.of("name", sync.getDifficulty())));

            properties.put(commitDateParams, Map.of("date", Map.of("start", sync.getCreatedAt().toString())));

            properties.put(pathURL, Map.of("url", sync.getPath()));

            mappings.put(propertiesParams, properties);

            allNotionSync.add(mappings);
        }

        return allNotionSync;
    }

    @Override
    public NotionSync convertMessage(GitlabWebhook gitlabWebhook){

        try{
            NotionSync sync = new NotionSync();
            String input = gitlabWebhook.getCommit();
            long time = System.currentTimeMillis();
            log.info("commit: {}", gitlabWebhook.getCommit());
            String regex = "(.*?)\\[(.*?)\\]\\[(.*?)\\]\\[(.*?)\\]\\[(.*?)\\]";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);
            log.info("matcher.matches(): {}", matcher.matches());

            if (matcher.matches()){
                log.info("looping");
                log.info("{}",matcher.group(1));
                sync.setTitle(matcher.group(1));
                sync.setTopic(matcher.group(2));
                sync.setPlatform(matcher.group(3));
                sync.setDifficulty(matcher.group(4));
                sync.setPath(matcher.group(5));
            }

            //set time
            sync.setCreatedAt(new Timestamp(time));
            return sync;
        }catch(NullPointerException npe){
            log.error("ERROR->",npe);
            //check a wrong,missing, or missplaced character posit between desc and topic section
            throw new WrongCharacterPositionException("Wrong character position");
        }catch (Exception e){
            log.error("ERROR->",e);
            throw e;
        }
    }

}
