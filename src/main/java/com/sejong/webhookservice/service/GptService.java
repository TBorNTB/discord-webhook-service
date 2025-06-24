package com.sejong.webhookservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sejong.webhookservice.controller.dto.OpenAiRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GptService {

    private final WebClient openAiWebClient;

    // 이런식으로 요청한다고 합니다~
//    model: "gpt-4" 사용
//    messages: ChatGPT 스타일의 대화 형식
//     "system" 역할: GPT에게 역할 설명 → 요약 도우미
//  "user" 역할: 실제 유저 질문 → 이슈나 PR 내용 전달
//    temperature: 0.7 → 창의성과 다양성 제어
//
    public Mono<String> summarize(String type, String title, String body) {
        String systemMsg = "너는 GitHub 활동을 요약해서 전송하는 도우미야. 요약은 간결하면서도 중요한 포인트를 포함해야 해.";
        String userMsg = String.format("[%s] %s\n\n%s", type.toUpperCase(), title, body);

        OpenAiRequest request = new OpenAiRequest(
                "gpt-4",
                List.of(
                        new OpenAiRequest.Message("system", systemMsg),
                        new OpenAiRequest.Message("user", userMsg)
                ),
                0.7
        );

        return openAiWebClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(res -> res.get("choices").get(0).get("message").get("content").asText());
    }
    //아래와 같은 형태로 값이 반환된다고 하네요
//    {
//        "choices": [
//        {
//            "message": {
//            "content": "요약된 결과"
//        }
//        }
//  ]
//    }
}