package com.app.controller;

import com.app.Exception.WrongCharacterPositionException;
import com.app.model.GitlabWebhook;
import com.app.service.NotionSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> handleWebhook(@RequestBody GitlabWebhook gitlabWebhook){

        try{
            notionSyncService.syncToNotion(gitlabWebhook);
            return ResponseEntity.ok("Success");
        }catch (DataAccessException dae){
            return new ResponseEntity<>(dae.getMessage().toString(), HttpStatus.CONFLICT);
        }catch (WrongCharacterPositionException wcpe){
            return new ResponseEntity<>(wcpe.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
