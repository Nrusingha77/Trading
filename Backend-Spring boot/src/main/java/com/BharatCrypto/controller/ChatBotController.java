package com.BharatCrypto.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.BharatCrypto.request.ChatRequest;
import com.BharatCrypto.service.ChatBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
public class ChatBotController {

    @Autowired
    private ChatBotService chatBotService;

    @PostMapping("/ask")
    public ResponseEntity<String> askBot(@RequestBody ChatRequest request) throws Exception {
        String response = chatBotService.getResponse(request.getPrompt());

        // extract final text from Gemini response (handles both JSON and plain text)
        String rawResponse = response;
        String assistantText = "";

        ObjectMapper mapper = new ObjectMapper();
        try {
            // try to parse as JSON first
            JsonNode root = mapper.readTree(rawResponse);
            if (root.has("candidates")) {
                assistantText = root.path("candidates").get(0)
                                    .path("content").path(0)
                                    .path("text").asText("");
            } else if (root.has("outputText")) {
                assistantText = root.path("outputText").asText("");
            } else if (root.has("text")) {
                assistantText = root.path("text").asText("");
            } else {
                // fallback to entire JSON object as string
                assistantText = root.toString();
            }
        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            // response is plain text/markdown — use raw response as-is
            assistantText = rawResponse != null ? rawResponse.trim() : "";
        }

        // return plain text response (frontend will normalize it to a message object)
        return ResponseEntity.ok(assistantText);
    }
}