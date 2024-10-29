package com.scaler.angelonejartest;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class Controller {

    @PostMapping("/marketData")
    public ResponseEntity<JSONObject> getMarketData(
            @RequestBody LoginRequestDTO loginRequestDto,
            @RequestParam String privateKey) {

        SmartConnect smartConnect = new SmartConnect();
        smartConnect.setApiKey(privateKey);

        User user;
        try {
            user = smartConnect.generateSession(
                    loginRequestDto.getClientcode(),
                    loginRequestDto.getPassword(),
                    loginRequestDto.getTotp());

            smartConnect.setAccessToken(user.getAccessToken());
            smartConnect.setUserId(user.getUserId());

            // Fetch market data
            JSONObject response = fetchMarketData(smartConnect);

            // Log the response
            System.out.println("Market Data Response: " + response.toString());

            return ResponseEntity.ok(response);
        } catch (SmartAPIException | IOException e) {
            e.printStackTrace(); // Log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JSONObject().put("error", "An error occurred while fetching market data."));
        }
    }


    public JSONObject fetchMarketData(SmartConnect smartConnect) throws SmartAPIException, IOException {
        JSONObject payload = new JSONObject();
        payload.put("mode", "FULL"); // Change this mode if needed

        JSONObject exchangeTokens = new JSONObject();
        JSONArray nseTokens = new JSONArray();
        nseTokens.put("3045"); // Ensure this token is valid for what you want to fetch
        exchangeTokens.put("NSE", nseTokens);
        payload.put("exchangeTokens", exchangeTokens);

        // Get the market data
        JSONObject response = smartConnect.marketData(payload);
        return response;
    }
}
