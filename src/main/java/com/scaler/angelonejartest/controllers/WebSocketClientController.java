package com.scaler.angelonejartest.controllers;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.scaler.angelonejartest.service.WebSocketClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketClientController {

    @Autowired
    private WebSocketClientService webSocketClientService;

    @GetMapping("/connect")
    public String connectWebSocket(
            @RequestParam String apiKey,
            @RequestParam String clientCode,
            @RequestParam String password,
            @RequestParam String twoFA) {

        try {
            webSocketClientService.connect(apiKey, clientCode, password, twoFA);
            return "WebSocket connection established!";
        } catch (Exception | SmartAPIException e) {
            e.printStackTrace();
            return "Failed to establish WebSocket connection: " + e.getMessage();
        }
    }

    @GetMapping("/disconnect")
    public String disconnectWebSocket() {
        webSocketClientService.disconnect();
        return "WebSocket connection closed!";
    }
}
