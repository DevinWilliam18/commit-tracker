package com.app.service.impl;

import com.app.dao.CommitDao;
import com.app.model.GitlabWebhook;
import com.app.model.NotionSync;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotionSyncServiceImplTest {

    @Mock
    private CommitDao commitDao;

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @InjectMocks
    private NotionSyncServiceImpl notionSyncServiceImpl;

    private NotionSync notionSync;

    private GitlabWebhook gitlabWebhook;

    @BeforeEach
    private void initData(){
        notionSync = new NotionSync();
        notionSync.setId("");
        notionSync.setPlatform("");
        notionSync.setTitle("");
        notionSync.setTopic("");
        notionSync.setDifficulty("");
        notionSync.setPath("");
        notionSync.setSyncedToNotion(false);
        notionSync.setCreatedAt(ZonedDateTime.now(ZoneId.of("Asia/Jakarta")).toInstant());
        notionSync.setUpdatedAt(ZonedDateTime.now(ZoneId.of("Asia/Jakarta")).toInstant());

        gitlabWebhook.setRef("test");
        gitlabWebhook.setCommit("");
        gitlabWebhook.setPusher("devinwilliam00");
    }

    @Test
    void getSynedToNotionFalse() {
        when(commitDao.findBySyncedToNotionFalse()).thenReturn(new ArrayList<>(Arrays.asList(notionSync)));

        assertEquals(1,notionSyncServiceImpl.getSynedToNotionFalse().size());
        verify(commitDao).findBySyncedToNotionFalse();

    }

//    @Test
//    void syncToNotion() {
//
//    }
//
//    @Test
//    void pushToNotion() {
//
//    }
//
//    @Test
//    void dataToNotion() {
//
//    }
//
    @Test
    void shouldConvertMessageSuccessfully() {

        String commit = gitlabWebhook.getCommit();

        String regex = "(.*?)\\[(.*?)\\]\\[(.*?)\\]\\[(.*?)\\]\\[(.*?)\\]";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(commit);

        String title = "", topic = "", platform = "", difficulty = "", path = "";

        if (matcher.matches()){
            title = matcher.group(1);
            topic = matcher.group(2);
            platform = matcher.group(3);
            difficulty = matcher.group(4);
            path = matcher.group(5);
        }

        assertEquals(notionSync.getTitle(), title);
        assertEquals(notionSync.getTopic(), topic);
        assertEquals(notionSync.getPlatform(), platform);
        assertEquals(notionSync.getDifficulty(), difficulty);
        assertEquals(notionSync.getPath(), path);

    }
}