package com.sejong.webhookservice.controller;

import com.sejong.webhookservice.controller.dto.GitHubIssuePayload;
import com.sejong.webhookservice.service.DiscordWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")
@Slf4j
@RequiredArgsConstructor
public class GitHubWebhookController {

    private final DiscordWebhookService discordService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> onGithubEvent(@RequestBody Map<String, Object> payload,
                                              @RequestHeader("X-GitHub-Event") String eventType) {

        switch (eventType) {
            case "issues" -> handleEvent(payload, "issue", "ISSUE");
            case "pull_request" -> handleEvent(payload, "pull_request", "PR");
            case "pull_request_review_comment" -> handleReview(payload);
        }
        return ResponseEntity.ok().build();
    }

    private void handleEvent(Map<String, Object> payload, String key, String type) {
        Object obj = payload.get(key);
        if (obj instanceof Map<?, ?> content) {
            String title = (String) content.get("title");
            String body = (String) content.get("body");
            String url = (String) content.get("html_url");
            discordService.sendSummarizedAlert(type, title, body, url);
        }
    }

    private void handleReview(Map<String, Object> payload) {
        Map<String, Object> comment = (Map<String, Object>) payload.get("comment");
        String body = (String) comment.get("body");
        String url = (String) comment.get("html_url");
        discordService.sendSummarizedAlert("REVIEW", "Code Review Comment", body, url);
    }
}
