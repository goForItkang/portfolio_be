package com.pj.portfoliosite.portfoliosite.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class OAuthUtil {

    @Value("${oauth.github.client-id}")
    private String githubClientId;

    @Value("${oauth.github.client-secret}")
    private String githubClientSecret;

    @Value("${oauth.github.redirect-uri}")
    private String githubRedirectUri;

    @Value("${oauth.google.client-id}")
    private String googleClientId;

    @Value("${oauth.google.client-secret}")
    private String googleClientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String googleRedirectUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getGitHubAuthUrl() {
        return "https://github.com/login/oauth/authorize" +
                "?client_id=" + githubClientId +
                "&redirect_uri=" + githubRedirectUri +
                "&scope=user:email";
    }

    public String getGoogleAuthUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + googleClientId +
                "&redirect_uri=" + googleRedirectUri +
                "&response_type=code" +
                "&scope=openid email profile";
    }

    public String getGitHubAccessToken(String code) {
        try {
            String tokenUrl = "https://github.com/login/oauth/access_token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", githubClientId);
            params.add("client_secret", githubClientSecret);
            params.add("code", code);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Accept", "application/json");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("GitHub 토큰 요청 실패", e);
        }
    }

    public String getGoogleAccessToken(String code) {
        try {
            String tokenUrl = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", googleClientId);
            params.add("client_secret", googleClientSecret);
            params.add("code", code);
            params.add("grant_type", "authorization_code");
            params.add("redirect_uri", googleRedirectUri);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Google 토큰 요청 실패", e);
        }
    }

    public Map<String, String> getGitHubUserInfo(String accessToken) {
        try {
            String userUrl = "https://api.github.com/user";
            String emailUrl = "https://api.github.com/user/emails";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "token " + accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> userResponse = restTemplate.exchange(userUrl, HttpMethod.GET, entity, String.class);
            JsonNode userNode = objectMapper.readTree(userResponse.getBody());

            ResponseEntity<String> emailResponse = restTemplate.exchange(emailUrl, HttpMethod.GET, entity, String.class);
            JsonNode emailNode = objectMapper.readTree(emailResponse.getBody());

            String primaryEmail = "";
            for (JsonNode email : emailNode) {
                if (email.get("primary").asBoolean()) {
                    primaryEmail = email.get("email").asText();
                    break;
                }
            }

            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("email", primaryEmail);
            userInfo.put("name", userNode.get("name") != null ? userNode.get("name").asText() : userNode.get("login").asText());
            userInfo.put("profile", userNode.get("avatar_url").asText());
            userInfo.put("provider", "GITHUB");

            return userInfo;
        } catch (Exception e) {
            throw new RuntimeException("GitHub 사용자 정보 요청 실패", e);
        }
    }

    public Map<String, String> getGoogleUserInfo(String accessToken) {
        try {
            String userUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(userUrl, HttpMethod.GET, entity, String.class);
            JsonNode userNode = objectMapper.readTree(response.getBody());

            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("email", userNode.get("email").asText());
            userInfo.put("name", userNode.get("name").asText());
            userInfo.put("profile", userNode.get("picture").asText());
            userInfo.put("provider", "GOOGLE");

            return userInfo;
        } catch (Exception e) {
            throw new RuntimeException("Google 사용자 정보 요청 실패", e);
        }
    }
}