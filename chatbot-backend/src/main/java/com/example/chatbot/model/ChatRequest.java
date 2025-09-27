package com.example.chatbot.model;

public class ChatRequest {
    private String message;

    // Default constructor
    public ChatRequest() {}

    public ChatRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}