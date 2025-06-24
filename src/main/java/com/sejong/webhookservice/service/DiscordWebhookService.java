package com.sejong.webhookservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiscordWebhookService {

    private final WebClient webClient = WebClient.create();
    private final GptService gptService;

    @Value("${discord.webhook.url}")
    private String webhookUrl;

    public void sendSummarizedAlert(String type, String title, String body, String url) {
        gptService.summarize(type, title, body)
                .map(summary -> {
                    String message = String.format(
                            """
                                    @everyone
                                    📌 **[%s] 알림 요약**
                                    
                                    **%s**
                                    
                                    ```markdown
                                    %s
                                    ```
                                    
                                    👉 [자세히 보기](%s)
                                    """,
                            type.toUpperCase(),
                            title,
                            summary,
                            url
                    );
                    return Map.of("content", message);
                })
                .flatMap(payload ->
                        webClient.post()
                                .uri(webhookUrl)
                                .bodyValue(payload)
                                .retrieve()
                                .bodyToMono(String.class)
                )
                .doOnError(e -> log.error("Discord 전송 실패", e))
                .subscribe();
    }
}
