package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

@WebServlet("/main.do")
public class MainServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 세션 데이터 가져오기
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        HashMap<String, String> storedSessionData = (HashMap<String, String>) session.getAttribute(sessionId);
        request.setAttribute("serverSessionId", storedSessionData.get("sessionId"));
        request.setAttribute("serverUuid", storedSessionData.get("uuid"));

        // 세션 데이터 확인
        String uuid = storedSessionData.get("uuid");
        HashMap<String, String> storedUuidData = (HashMap<String, String>) session.getAttribute(uuid);
        System.out.println("[" + request.getRequestURI() + "] uuid = " + uuid);
        System.out.println("[" + request.getRequestURI() + "] storedSessionData = " + storedSessionData + " :: storedUuidData = " + storedUuidData);

        request.setAttribute("clientSessionId", null);
        request.setAttribute("clientUuid", null);

        if (storedUuidData != null) {
            String encryptedSessionId = storedUuidData.get("sessionId");
            String decryptedSessionId = new String(Base64.getDecoder().decode(encryptedSessionId), StandardCharsets.UTF_8);
            String encryptedUuid = storedUuidData.get("uuid");
            String decryptedUuid = new String(Base64.getDecoder().decode(encryptedUuid), StandardCharsets.UTF_8);

            request.setAttribute("clientSessionId", decryptedSessionId);
            request.setAttribute("clientUuid", decryptedUuid);

            if (decryptedUuid != null && uuid.equals(decryptedUuid)) {
                request.setAttribute("message", "세션 데이터 확인 완료");
                request.getRequestDispatcher("/WEB-INF/views/main.jsp").forward(request, response);
            } else {
                request.setAttribute("message", "세션 데이터 확인 실패");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("message", "세션 데이터 확인 실패");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
