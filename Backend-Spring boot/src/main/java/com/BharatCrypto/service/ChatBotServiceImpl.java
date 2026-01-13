package com.BharatCrypto.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatBotServiceImpl implements ChatBotService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public ChatBotServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getResponse(String prompt) throws Exception {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key=" + geminiApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(textPart));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            String response = restTemplate.postForObject(url, entity, String.class);
            JsonNode responseNode = objectMapper.readTree(response);
            
            // SDE4 FIX: Defensive coding to handle empty candidates or API errors
            if (responseNode.has("candidates") && responseNode.get("candidates").size() > 0) {
                JsonNode candidate = responseNode.get("candidates").get(0);
                if (candidate.has("content") && candidate.get("content").has("parts")) {
                     return candidate.get("content").get("parts").get(0).get("text").asText();
                }
            }
            return "I'm sorry, I couldn't process that request at the moment. Please try again.";
        } catch (Exception e) {
            throw new Exception("Failed to get response from Gemini API: " + e.getMessage(), e);
        }
    }
}