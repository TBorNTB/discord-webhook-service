package com.sejong.webhookservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GitHubIssuePayload {
    private String action;
    private Issue issue;
}
