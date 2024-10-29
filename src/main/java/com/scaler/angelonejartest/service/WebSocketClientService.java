package com.scaler.angelonejartest.service;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.SessionExpiryHook;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.smartstream.models.*;
import com.angelbroking.smartapi.smartstream.ticker.SmartStreamListener;
import com.angelbroking.smartapi.smartstream.ticker.SmartStreamTicker;
import com.neovisionaries.ws.client.WebSocketException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class WebSocketClientService {

    private SmartStreamTicker smartStreamTicker;
    private SmartConnect smartConnect;
    private Boolean connectionStatus;

    public void connect(String apiKey, String clientCode, String password, String twoFA) throws SmartAPIException, WebSocketException, IOException {

        // Initialize SmartConnect object and generate session
        smartConnect = new SmartConnect();
        smartConnect.setApiKey(apiKey);
        smartConnect.setSessionExpiryHook(new SessionExpiryHook() {
            @Override
            public void sessionExpired() {
                System.out.println("Session expired");
            }
        });

        // Generate session with user credentials
        User user = smartConnect.generateSession(clientCode, password, twoFA);
        smartConnect.setAccessToken(user.getAccessToken());
        smartConnect.setUserId(user.getUserId());

        // Initialize SmartStreamListener to handle WebSocket events
        SmartStreamListener smartStreamListener = new SmartStreamListener() {
            @Override
            public void onLTPArrival(LTP ltp) {
                System.out.println("LTP value: " + ltp.getExchangeType() + " -> " + ltp);
            }

            @Override
            public void onQuoteArrival(Quote quote) {
                // Handle quote arrival
            }

            @Override
            public void onSnapQuoteArrival(SnapQuote snapQuote) {
                // Handle snap quote arrival
            }

            @Override
            public void onDepthArrival(Depth depth) {
                // Handle market depth arrival
            }

            @Override
            public void onConnected() {
                System.out.println("Connected successfully");
            }

            @Override
            public void onDisconnected() {
                System.out.println("Disconnected from WebSocket");
            }

            @Override
            public void onError(SmartStreamError smartStreamError) {
//                System.err.println("Error occurred: " + smartStreamError.getMessage());
            }

            @Override
            public void onPong() {
                System.out.println("Pong received");
            }

            @Override
            public SmartStreamError onErrorCustom() {
                return null;
            }
        };

        // Initialize SmartStreamTicker to manage WebSocket
        String feedToken = user.getFeedToken();
        smartStreamTicker = new SmartStreamTicker(clientCode, feedToken, smartStreamListener);
        smartStreamTicker.connect();
        connectionStatus = smartStreamTicker.isConnectionOpen();

        // Subscribe to tokens
        subscribeToTokens();
    }

    private void subscribeToTokens() {
        if (connectionStatus) {
            Set<TokenID> tokenSet = new HashSet<>();
            tokenSet.add(new TokenID(ExchangeType.NSE_CM, "26000")); // NIFTY
            tokenSet.add(new TokenID(ExchangeType.NSE_CM, "26009")); // NIFTY BANK
            tokenSet.add(new TokenID(ExchangeType.BSE_CM, "19000")); // BSE

            // Subscribe to LTP updates for the given tokens
            smartStreamTicker.subscribe(SmartStreamSubsMode.LTP, tokenSet);
            System.out.println("Subscribed to tokens: " + tokenSet);
        } else {
            System.err.println("WebSocket connection is not open!");
        }
    }

    public void disconnect() {
        if (smartStreamTicker != null && connectionStatus) {
            smartStreamTicker.disconnect();
            System.out.println("Disconnected from WebSocket");
        }
    }
}
