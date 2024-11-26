<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Index Page</title>
    <link rel="icon" href="data:," />

    <style>
        table {
            border-collapse: collapse;
            width: 50%;
            margin: 20px 0px;
        }
        table, th, td {
            border: 1px solid black;
        }
        th, td {
            padding: 10px;
            text-align: center;
        }
    </style>
</head>
<body>
    <h1>인덱스 페이지</h1>
    <p>${message}</p>
    <table>
        <tr>
            <th>세션 스토리지 값</th>
            <th>sessionId</th>
            <th>uuid</th>
        </tr>
        <tr>
            <td>sessionId를 키로 저장</td>
            <td id="serverSessionId"></td>
            <td id="serverUuid"></td>
        </tr>
        <tr>
            <td>uuid를 키로 저장</td>
            <td id="clientSessionId"></td>
            <td id="clientUuid"></td>
        </tr>
    </table>
    <button onclick="location.href='/main.do'">메인 페이지로 가기</button>
    <script>
        // 세션 데이터 가져오기
        async function getData() {
            document.getElementById("serverSessionId").innerText = "";
            document.getElementById("serverUuid").innerText = "";
            document.getElementById("clientSessionId").innerText = "";
            document.getElementById("clientUuid").innerText = "";

            try {
                const response = await fetch('/api/process-data', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        action: 'getData' // 세션 데이터 요청
                    })
                });

                if (!response.ok) {
                    throw new Error('Failed to get session data');
                } else {
                    alert('세션 데이터 가져오기 성공');
                }

                const sessionData = await response.json();
                console.log('Session Data:', sessionData);

                document.getElementById("serverSessionId").innerText = sessionData.sessionId;
                document.getElementById("serverUuid").innerText = sessionData.uuid;

                // 암호화된 데이터를 생성 (Base64 인코딩)
                const encryptedSessionId = btoa(sessionData.sessionId);
                const encryptedUuid = btoa(sessionData.uuid);

                const encryptedData = JSON.stringify({
                    sessionId: encryptedSessionId,
                    uuid: encryptedUuid
                });
                console.log('Encrypted Data:', encryptedData);

                // 암호화된 데이터를 서버에 저장
                saveData(encryptedData);
            } catch (error) {
                console.error('Error getting session data:', error);
            }
        }

        // 암호화된 데이터 저장하기
        async function saveData(encryptedData) {
            try {
                const response = await fetch('/api/process-data', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        action: 'saveData', // 데이터 저장 요청
                        encryptedData: encryptedData
                    })
                });

                const result = await response.json();
                console.log('Server Response:', result);

                if (response.ok) {
                    alert('세션 스토리지에 uuid 키 저장 완료');
                    document.getElementById("clientSessionId").innerText = atob(result.sessionId);
                    document.getElementById("clientUuid").innerText = atob(result.uuid);
                } else {
                    alert(`세션 스토리지에 uuid 키 저장 실패: ${result.message}`);
                }
            } catch (error) {
                console.error('Error saving session data:', error);
            }
        }

        // 페이지 로드 시 세션 데이터 가져오기
        window.onload = getData;
    </script>
</body>
</html>