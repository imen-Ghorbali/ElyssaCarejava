package tn.esprit.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;

public class OpenAIService {

    private static final String API_KEY = "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" ;// remplace par ta clé OpenRouter
    private static final String ENDPOINT = "https://openrouter.ai/api/v1/chat/completions";
    private final OkHttpClient client;

    public OpenAIService() {
        this.client = new OkHttpClient();
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new IllegalStateException("Clé API OpenRouter manquante.");
        }
    }

    public String generateDescription(String title) throws IOException {
        String prompt = "Génère une courte description professionnelle en français d'un événement ayant pour titre : \"" + title + "\".";

        // Préparer le corps JSON
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);

        JsonArray messagesArray = new JsonArray();
        messagesArray.add(message);

        JsonObject payload = new JsonObject();
        payload.addProperty("model", "mistralai/mistral-7b-instruct"); // modèle gratuit
        payload.add("messages", messagesArray);
        payload.addProperty("max_tokens", 100);
        payload.addProperty("temperature", 0.7);

        String bodyJson = payload.toString();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        Request request = new Request.Builder()
                .url(ENDPOINT)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("HTTP-Referer", "https://tonsite.com") // ou "http://localhost" si projet local
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(bodyJson, JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error details";
                throw new IOException("API request failed: " + response.code() + " - " + errorBody);
            }

            String json = response.body().string();
            JsonObject jsonObject = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
            JsonArray choices = jsonObject.getAsJsonArray("choices");

            if (choices == null || choices.size() == 0) {
                throw new IOException("No choices in response");
            }

            JsonObject choice = choices.get(0).getAsJsonObject();
            return choice.getAsJsonObject("message").get("content").getAsString();
        }
    }
}
