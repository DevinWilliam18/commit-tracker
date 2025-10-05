package com.app.dao;

import com.app.model.NotionSync;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommitDaoTest {

    @Autowired
    private CommitDao commitDao;

    @Test
    void findBySyncedToNotionFalse() {
        NotionSync notionSync1 = new NotionSync();
        notionSync1.setId("test123");
        notionSync1.setNotionDatabaseId("1234asad");
        notionSync1.setPlatform("Leetcode");
        notionSync1.setTitle("Coin Change");
        notionSync1.setTopic("DP");
        notionSync1.setDifficulty("Hard");
        notionSync1.setPath("https://leetcode.com");
        notionSync1.setSyncedToNotion(false);
        notionSync1.setCreatedAt(ZonedDateTime.now(ZoneId.of("Asia/Jakarta")).toInstant());
        notionSync1.setUpdatedAt(ZonedDateTime.now(ZoneId.of("Asia/Jakarta")).toInstant());

        NotionSync notionSync2 = new NotionSync();
        notionSync1.setId("test323");
        notionSync1.setNotionDatabaseId("1200asad");
        notionSync1.setPlatform("Hackerrank");
        notionSync1.setTitle("Coin Change");
        notionSync1.setTopic("DP");
        notionSync1.setDifficulty("Hard");
        notionSync1.setPath("https://leetcode.com");
        notionSync1.setSyncedToNotion(false);
        notionSync1.setCreatedAt(ZonedDateTime.now(ZoneId.of("Asia/Jakarta")).toInstant());
        notionSync1.setUpdatedAt(ZonedDateTime.now(ZoneId.of("Asia/Jakarta")).toInstant());

        commitDao.saveAll(Arrays.asList(notionSync1, notionSync2));

        assertThat(commitDao.findBySyncedToNotionFalse()).isNotNull();

    }
}