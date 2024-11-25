<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Index Page</title>
    <link rel="icon" href="data:," /> <!-- Favicon 없음 설정 -->
</head>
<body>
    <h1>Welcome to Index Page</h1>
    <p>${message}</p>
    <button onclick="location.href='/main.do'">메인 페이지로 가기</button>
    <script>
            // 세션 데이터 가져오기
            async function getSessionData() {
                try {
                    const response = await fetch('/api/process-session', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            action: 'getSession' // 세션 데이터 요청
                        })
                    });

                    if (!response.ok) {
                        throw new Error('Failed to get session data');
                    }

                    const sessionData = await response.json();
                    console.log('Session Data:', sessionData);

                    // 암호화된 데이터를 생성 (Base64 인코딩)
                    const encryptedSessionId = btoa(sessionData.sessionId);
                    const encryptedUuid = btoa(sessionData.uuid);

                    const encryptedData = JSON.stringify({
                        sessionId: encryptedSessionId,
                        uuid: encryptedUuid
                    });
                    console.log('Encrypted Data:', encryptedData);

                    // 암호화된 데이터를 서버에 저장
                    saveSessionData(encryptedData);
                } catch (error) {
                    console.error('Error getting session data:', error);
                }
            }

            // 암호화된 데이터 저장하기
            async function saveSessionData(encryptedData) {
                try {
                    const response = await fetch('/api/process-session', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            action: 'saveSession', // 데이터 저장 요청
                            encryptedData: encryptedData
                        })
                    });

                    const result = await response.json();
                    console.log('Server Response:', result);

                    if (response.ok) {
                        alert('Session data saved successfully!');
                    } else {
                        alert(`Failed to save session data: ${result.message}`);
                    }
                } catch (error) {
                    console.error('Error saving session data:', error);
                }
            }

            // 페이지 로드 시 세션 데이터 가져오기
            window.onload = getSessionData;
    </script>
</body>
</html>