package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

import static java.util.UUID.randomUUID;

@WebServlet("/index.do")
public class IndexServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 세션 생성
        HttpSession session = request.getSession();
        String sessionId = session.getId();

        // UUID 생성 (16자리)
        // TODO: 암호화된 UUID 생성
        String encryptedUuid = randomUUID().toString().replace("-", "").substring(0, 16);

        // JSON 구조로 세션 저장
        HashMap<String, String> sessionData = new HashMap<>();
        sessionData.put("sessionId", sessionId);
        sessionData.put("uuid", encryptedUuid);

        // 세션 저장
        session.setAttribute(sessionId, sessionData);

        // 세션 확인
        HashMap<String, String> storedSessionData = (HashMap<String, String>) session.getAttribute(sessionId);
        System.out.println("[" + request.getRequestURI() + "] sessionData = " + sessionData + " :: storedSessionData = " + storedSessionData);
        request.setAttribute("serverSessionId", storedSessionData.get("sessionId"));
        request.setAttribute("serverUuid", storedSessionData.get("uuid"));

        if (storedSessionData != null && storedSessionData.containsKey("sessionId") && storedSessionData.containsKey("uuid")) {
            request.setAttribute("message", "세션 데이터 저장 완료");
            request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
        } else {
            request.setAttribute("message", "세션 데이터 저장 실패");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
