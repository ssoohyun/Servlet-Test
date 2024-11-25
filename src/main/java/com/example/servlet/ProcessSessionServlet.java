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

        // 서버에 저장된 세션 데이터 가져오기
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        HashMap<String, String> storedSessionData = (HashMap<String, String>) session.getAttribute(sessionId);

        // JSON 요청 데이터 읽기
        StringBuilder sb = new StringBuilder();
        request.getReader().lines().forEach(sb::append);
        String requestData = sb.toString();

        // JSON 파싱
        JSONObject json = new JSONObject(requestData);
        String action = json.getString("action");

        if ("getSession".equals(action)) {

            if (storedSessionData == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // 클라이언트로 세션 데이터 반환
            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(storedSessionData));

        } else if ("saveSession".equals(action)) {
            String encryptedData = json.getString("encryptedData");

            // JSON 파싱
            JSONObject encryptedJson = new JSONObject(encryptedData);
            String encryptedSessionId = encryptedJson.getString("sessionId");
            String encryptedUuid = encryptedJson.getString("uuid");

            // Base64 디코딩
            String decryptedSessionId = new String(Base64.getDecoder().decode(encryptedSessionId), StandardCharsets.UTF_8);
            String decryptedUuid = new String(Base64.getDecoder().decode(encryptedUuid), StandardCharsets.UTF_8);
            System.out.println("[" + request.getRequestURI() + "] encryptedData = " + encryptedData + " :: decryptedData = {sessionId: " + decryptedSessionId + ", uuid: " + decryptedUuid + "}");

            if (storedSessionData != null) {
                String uuid = storedSessionData.get("uuid");

                if (sessionId.equals(decryptedSessionId) && uuid.equals(decryptedUuid)) {
                    // 세션에 저장
                    HashMap<String, String> uuidData = new HashMap<>();
                    uuidData.put("sessionId", encryptedSessionId);
                    uuidData.put("uuid", encryptedUuid);

                    session.setAttribute(decryptedUuid, uuidData); // UUID를 키로 저장
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
