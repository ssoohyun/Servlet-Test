<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Error Page</title>
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
    <h1>에러 페이지</h1>
    <p>{message}</p>
    <table>
        <tr>
            <th>세션 스토리지 값</th>
            <th>sessionId</th>
            <th>uuid</th>
        </tr>
        <tr>
            <td>sessionId를 키로 저장</td>
            <td>${serverSessionId}</td>
            <td>${serverUuid}</td>
        </tr>
        <tr>
            <td>uuid를 키로 저장</td>
            <td>${clientSessionId}</td>
            <td>${clientUuid}</td>
        </tr>
    </table>
    <button onclick="location.href='/index.do'">인덱스 페이지로 돌아가기</button>
</body>
</html>
