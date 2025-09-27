package com.example.chatbot.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {
    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    private final String apiKey;
    private final boolean geminiEnabled;
    private final OkHttpClient client;

    // Updated API endpoints - try these different versions
    private static final String[] GEMINI_API_URLS = {
            "https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent",
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent",
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.0-pro:generateContent"
    };

    public GeminiService(@Value("${gemini.api.key:}") String apiKey) {
        this.apiKey = apiKey;
        this.geminiEnabled = apiKey != null && !apiKey.trim().isEmpty();
        this.client = new OkHttpClient();

        if (geminiEnabled) {
            testApiKeyOnStartup();
        } else {
            logger.info("Using fallback mode - No Gemini API key provided");
        }
    }

    private void testApiKeyOnStartup() {
        logger.info("🔧 Testing Gemini API key...");
        for (int i = 0; i < GEMINI_API_URLS.length; i++) {
            String apiUrl = GEMINI_API_URLS[i];
            try {
                JSONObject requestBody = createRequestBody("Hello");

                RequestBody body = RequestBody.create(
                        requestBody.toString(),
                        MediaType.parse("application/json")
                );

                Request request = new Request.Builder()
                        .url(apiUrl + "?key=" + apiKey)
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        logger.info("✅ Gemini API working with endpoint: {}", apiUrl);
                        return;
                    } else {
                        logger.warn("❌ Endpoint {} failed: {}", apiUrl, response.code());
                    }
                }
            } catch (Exception e) {
                logger.warn("❌ Endpoint {} error: {}", apiUrl, e.getMessage());
            }
        }
        logger.error("🚫 All Gemini API endpoints failed. Using fallback mode.");
    }

    public String getReply(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Please enter a valid message.";
        }

        try {
            if (geminiEnabled) {
                String response = getGeminiResponse(userMessage);
                logger.debug("🤖 Gemini Response: {}", response);
                return response;
            } else {
                return getFallbackResponse(userMessage);
            }
        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage());
            return getFallbackResponse(userMessage);
        }
    }

    private String getGeminiResponse(String userMessage) {
        // Try each endpoint until one works
        for (String apiUrl : GEMINI_API_URLS) {
            try {
                logger.debug("📨 Trying endpoint: {}", apiUrl);

                JSONObject requestBody = createRequestBody(userMessage);

                RequestBody body = RequestBody.create(
                        requestBody.toString(),
                        MediaType.parse("application/json")
                );

                Request request = new Request.Builder()
                        .url(apiUrl + "?key=" + apiKey)
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String result = extractResponseText(jsonResponse);
                        logger.debug("✅ Gemini response successful from: {}", apiUrl);
                        return result;
                    } else {
                        logger.warn("❌ Endpoint {} failed: {}", apiUrl, response.code());
                    }
                }
            } catch (Exception e) {
                logger.warn("❌ Endpoint {} error: {}", apiUrl, e.getMessage());
            }
        }

        // If all endpoints fail, use fallback
        return getFallbackResponse(userMessage);
    }

    private JSONObject createRequestBody(String userMessage) {
        JSONObject requestBody = new JSONObject();

        JSONObject content = new JSONObject();
        JSONArray parts = new JSONArray();
        JSONObject part = new JSONObject();
        part.put("text", userMessage);
        parts.put(part);
        content.put("parts", parts);

        JSONArray contents = new JSONArray();
        contents.put(content);
        requestBody.put("contents", contents);

        JSONObject generationConfig = new JSONObject();
        generationConfig.put("maxOutputTokens", 500);
        generationConfig.put("temperature", 0.7);
        generationConfig.put("topP", 0.8);
        generationConfig.put("topK", 40);
        requestBody.put("generationConfig", generationConfig);

        return requestBody;
    }

    private String extractResponseText(JSONObject jsonResponse) {
        try {
            if (jsonResponse.has("candidates")) {
                JSONArray candidates = jsonResponse.getJSONArray("candidates");
                if (candidates.length() > 0) {
                    JSONObject candidate = candidates.getJSONObject(0);
                    JSONObject content = candidate.getJSONObject("content");
                    JSONArray parts = content.getJSONArray("parts");
                    if (parts.length() > 0) {
                        return parts.getJSONObject(0).getString("text");
                    }
                }
            }
            return "I received your message but the response format was unexpected.";
        } catch (Exception e) {
            logger.error("Error parsing response: {}", e.getMessage());
            return "I understand your message. How can I help you further?";
        }
    }

    private String getFallbackResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase().trim();

        // Enhanced fallback responses
        if (lowerMessage.contains("capital") && lowerMessage.contains("india")) {
            return "The capital city of India is New Delhi. 🏛️";
        } else if (lowerMessage.contains("capital") && lowerMessage.contains("france")) {
            return "The capital city of France is Paris. 🗼";
        } else if (lowerMessage.contains("capital") && lowerMessage.contains("japan")) {
            return "The capital city of Japan is Tokyo. 🗾";
        } else if (lowerMessage.contains("capital") && lowerMessage.contains("usa") || lowerMessage.contains("america")) {
            return "The capital city of the United States is Washington D.C. 🇺🇸";
        } else if (lowerMessage.contains("capital") && lowerMessage.contains("china")) {
            return "The capital city of China is Beijing. 🐉";
        } else if (lowerMessage.contains("capital") && lowerMessage.contains("uk") || lowerMessage.contains("britain")) {
            return "The capital city of the United Kingdom is London. 🇬🇧";
        } else if (lowerMessage.contains("weather")) {
            return "I don't have real-time weather data, but I can help with other questions!";
        } else if (lowerMessage.matches(".*\\b(hello|hi|hey)\\b.*")) {
            return "Hello! 👋 I'm your AI assistant. " +
                    (geminiEnabled ? "I'm powered by Google Gemini AI!" : "I'm in basic mode.");
        } else if (lowerMessage.contains("?")) {
            return "That's a great question! " +
                    (geminiEnabled ? "Let me think about that..." :
                            "For detailed AI answers, please configure your API key properly.");
        } else if (lowerMessage.matches(".*\\b(thank|thanks)\\b.*")) {
            return "You're welcome! 😊";
        } else if (lowerMessage.matches(".*\\b(bye|goodbye)\\b.*")) {
            return "Goodbye! Have a great day! 👋";
        } else if (lowerMessage.contains("india")) {
            return "India is a beautiful country in South Asia with rich culture and history. " +
                    "It's known for its diversity, delicious food, and ancient civilization. 🇮🇳";
        } else if (lowerMessage.contains("tell me about")) {
            return "I'd love to tell you more! Could you be more specific about what you'd like to know?";
        } else {
            return "I understand you said: \"" + userMessage + "\". " +
                    "How can I help you further?";
        }
    }

    public void reset() {
        logger.info("Chat reset");
    }

    public int chatsLeft() {
        return 9999;
    }

    public boolean isServiceEnabled() {
        return geminiEnabled;
    }
}