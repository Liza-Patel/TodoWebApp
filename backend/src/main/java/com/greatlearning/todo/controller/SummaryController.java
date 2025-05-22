package com.greatlearning.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.greatlearning.todo.service.SummaryService;
import com.greatlearning.todo.util.FirebaseAuthUtil;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class SummaryController {

    @Autowired
    SummaryService summaryService;

    @PostMapping("/summarize")
    public ResponseEntity<String> summarize(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from header
            String token = authHeader.replace("Bearer ", "");
            // Verify token and get uid
            String uid = FirebaseAuthUtil.verifyAndGetUid(token);

            // Pass uid to service
            String summary = summaryService.summarizeTodosAndSendToSlack(uid);
            return ResponseEntity.ok("Summary sent to Slack:\n" + summary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
