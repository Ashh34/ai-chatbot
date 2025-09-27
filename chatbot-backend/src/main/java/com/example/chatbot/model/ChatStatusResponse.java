package com.example.chatbot.model;

public class ChatStatusResponse {
    private int chatsLeft;
    private boolean serviceEnabled;
    private String status;

    public ChatStatusResponse() {}

    public ChatStatusResponse(int chatsLeft, boolean serviceEnabled) {
        this.chatsLeft = chatsLeft;
        this.serviceEnabled = serviceEnabled;
        this.status = serviceEnabled ? "GEMINI_ACTIVE" : "SERVICE_UNAVAILABLE";
    }

    // Getters and setters
    public int getChatsLeft() { return chatsLeft; }
    public void setChatsLeft(int chatsLeft) { this.chatsLeft = chatsLeft; }

    public boolean isServiceEnabled() { return serviceEnabled; }
    public void setServiceEnabled(boolean serviceEnabled) { this.serviceEnabled = serviceEnabled; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}