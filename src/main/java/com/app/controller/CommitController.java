package com.app.controller;

import com.app.model.GitlabWebhook;
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
    public ResponseEntity<String> handleWebhook(@RequestBody GitlabWebhook gitlabWebhook){

        try{
            notionSyncService.syncToNotion(gitlabWebhook);

            return ResponseEntity.ok().build();

        }catch (Exception ex){
            return ResponseEntity.badRequest().build();
        }
    }

}
