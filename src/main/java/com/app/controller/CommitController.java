package com.app.controller;

import com.app.model.GithubWebhook;
import com.app.service.NotionSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gitlab-webhook")
public class CommitController {

    @Autowired
    private NotionSyncService notionSyncService;

    @PostMapping("/commit-parser")
    public ResponseEntity<String> handleWebhook(@RequestBody GithubWebhook githubWebhook){

        notionSyncService.syncToNotion();

        return ResponseEntity.ok().build();
    }

}
