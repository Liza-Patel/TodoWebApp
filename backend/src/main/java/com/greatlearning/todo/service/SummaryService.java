package com.greatlearning.todo.service;

import com.greatlearning.todo.model.Todo;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SummaryService {

    @Value("${cohere.api.key}")
    private String cohereApiKey;

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    @Autowired
    FirebaseTodoService todoService;

    // Updated method: accepts uid to get user-specific todos
    public String summarizeTodosAndSendToSlack(String uid) throws Exception {
        List<Todo> todos = todoService.getTodosByUid(uid).stream()
            .filter(todo -> !todo.isCompleted())
            .collect(Collectors.toList());

        StringBuilder prompt = new StringBuilder("Summarize the following TODOs:\n");
        todos.forEach(todo -> prompt.append("- ").append(todo.getTitle()).append("\n"));

        String summary = callCohere(prompt.toString());
        sendToSlack(summary);

        return summary;
    }

    private String callCohere(String prompt) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
        		.callTimeout(30, java.util.concurrent.TimeUnit.SECONDS)     // total call timeout
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)  // connect timeout
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)     // read timeout
                .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)    // write timeout
                .build();

        JSONObject json = new JSONObject();
        json.put("model", "command"); // or "command-light" for faster/cheaper model
        json.put("prompt", prompt);
        json.put("max_tokens", 500); // Adjust as needed
        json.put("temperature", 0.7);

        Request request = new Request.Builder()
            .url("https://api.cohere.ai/v1/generate")
            .addHeader("Authorization", "Bearer " + cohereApiKey)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(json.toString(), MediaType.parse("application/json")))
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Cohere request failed - Code: " + response.code() + " - Body: " + response.body().string());
            }

            String responseBody = response.body().string();
            JSONObject res = new JSONObject(responseBody);
            return res.getJSONArray("generations").getJSONObject(0).getString("text").trim();
        }
    }

    private void sendToSlack(String message) throws IOException {
    	OkHttpClient client = new OkHttpClient.Builder()
        		.callTimeout(30, java.util.concurrent.TimeUnit.SECONDS)     // total call timeout
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)  // connect timeout
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)     // read timeout
                .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)    // write timeout
                .build();
        JSONObject json = new JSONObject().put("text", message);

        Request request = new Request.Builder()
            .url(slackWebhookUrl)
            .post(RequestBody.create(json.toString(), MediaType.parse("application/json")))
            .build();

        client.newCall(request).execute();
    }
}
