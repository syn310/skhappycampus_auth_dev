<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
body {
  margin: 0;
  font-size: 28px;
  font-family: Arial, Helvetica, sans-serif;
}

.header {
  position: fixed;
  top: 0;
  z-index: 1;
  width: 100%;
  background-color: #f1f1f1;
}

.header h2 {
  text-align: center;
}

.progress-container {
  width: 100%;
  height: 8px;
  background: #ccc;
}

.progress-bar {
  height: 8px;
  background: #4caf50;
  width: 0%;
}

.content {
  padding: 100px 0;
  margin: 50px auto 0 auto;
  width: 80%;
}
</style>
</head>
<body>

<div class="header">
  <h2>본인인증 오류</h2>
  <div class="progress-container">
    <div class="progress-bar" id="myBar"></div>
  </div>  
</div>

<div class="content">
  <p>본인인증 도중 오류가 발생하였습니다.</p>
  <p>잠시 후 다시 시도해주세요.</p>
</div>

</body>
</html> 
