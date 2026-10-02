<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Access denied | KaviMart</title>
  <style>
    body { margin:0; min-height:100vh; display:flex; align-items:center; justify-content:center;
           background:#faf7f0; color:#2b2b26; font-family: Georgia, "Times New Roman", serif; text-align:center; }
    .box { max-width:480px; padding:40px 24px; }
    .code { font-size:72px; margin:0; color:#d4714a; }
    h1 { font-size:28px; margin:8px 0 12px; }
    p { font-family: Arial, sans-serif; color:#5b5b52; line-height:1.5; }
    a { display:inline-block; margin-top:20px; padding:10px 22px; background:#d4714a; color:#fff;
        text-decoration:none; border-radius:4px; font-family: Arial, sans-serif; font-weight:bold; }
  </style>
</head>
<body>
  <div class="box">
    <p class="code">403</p>
    <h1>You don't have access to this page</h1>
    <p>Your account doesn't have permission to view this area.</p>
    <a href="${pageContext.request.contextPath}/catalog">Back to the shop</a>
  </div>
</body>
</html>
