package com.example.chatbot.controller;

import com.example.chatbot.model.ChatRequest;
import com.example.chatbot.model.ChatStatusResponse;
import com.example.chatbot.service.GeminiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final GeminiService geminiService;

    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping
    public String chat(@RequestBody ChatRequest request) {
        logger.info("Received chat request: {}", request.getMessage());
        String response = geminiService.getReply(request.getMessage());
        logger.info("Sending response: {}", response);
        return response;
    }

    @PostMapping("/reset")
    public ChatStatusResponse reset() {
        logger.info("Reset endpoint called");
        geminiService.reset();
        ChatStatusResponse response = new ChatStatusResponse(
                geminiService.chatsLeft(),
                geminiService.isServiceEnabled()
        );
        return response;
    }

    @GetMapping("/status")
    public ChatStatusResponse status() {
        ChatStatusResponse response = new ChatStatusResponse(
                geminiService.chatsLeft(),
                geminiService.isServiceEnabled()
        );
        return response;
    }
}