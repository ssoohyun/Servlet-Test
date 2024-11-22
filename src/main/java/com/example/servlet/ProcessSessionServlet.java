package com.example.servlet;

import com.google.gson.Gson;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

@WebServlet("/api/process-session")
public class ProcessSessionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();
        String sessionId = session.getId();
        HashMap<String, String> sessionData = (HashMap<String, String>) session.getAttribute(sessionId);

        // JSON 요청 데이터 읽기
        StringBuilder sb = new StringBuilder();
        request.getReader().lines().forEach(sb::append);
        String requestData = sb.toString();

        // JSON 파싱
        JSONObject json = new JSONObject(requestData);
        String action = json.getString("action");

        if ("getSession".equals(action)) {

            if (sessionData == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // 클라이언트로 세션 데이터 반환
            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(sessionData));

        } else if ("saveSession".equals(action)) {
            String encryptedData = json.getString("encryptedData");

            // Base64 디코딩
            String decryptedJson;
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(encryptedData); // Base64 디코딩
                decryptedJson = new String(decodedBytes, StandardCharsets.UTF_8); // 디코딩된 JSON 문자열
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\":\"failure\", \"message\":\"Invalid encrypted data\"}");
                return;
            }

            // JSON 파싱
            JSONObject decryptedData;
            try {
                decryptedData = new JSONObject(decryptedJson); // JSON 파싱
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\":\"failure\", \"message\":\"Invalid JSON format\"}");
                return;
            }

            // 추출한 데이터
            String clientSessionId = decryptedData.getString("sessionId");
            String clientUuid = decryptedData.getString("uuid");

            if (sessionData != null) {
                String uuid = sessionData.get("uuid");

                if (sessionId.equals(clientSessionId) && uuid.equals(clientUuid)) {
                    // Step 5: 세션에 저장
                    session.setAttribute(clientUuid, encryptedData); // UUID를 키로 저장
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"status\":\"success\", \"message\":\"Data saved successfully\"}");
                } else {
                    // 검증 실패
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"status\":\"failure\", \"message\":\"UUID or sessionId mismatch\"}");
                }
            } else {
                // 세션 데이터 없음
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\":\"failure\", \"message\":\"Session data not found\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"failure\", \"message\":\"Invalid action\"}");
        }
    }
}
