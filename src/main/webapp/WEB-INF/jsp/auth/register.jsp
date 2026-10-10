<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>
<section class="auth-layout">
  <div class="auth-copy">
    <span class="eyebrow">A marketplace with a little more meaning</span>
    <h1>Meet your next<br>favorite thing.</h1>
    <p>Shop small-batch goods from independent makers, or bring your own work to a community that cares.</p>
  </div>
  <form class="form-card" method="post" action="${pageContext.request.contextPath}/auth/register">
    <span class="eyebrow">Get started</span>
    <h2>Create your account</h2>

    <c:if test="${not empty errorMessage}">
      <div class="notice error">
        <c:out value="${errorMessage}" />
      </div>
    </c:if>

    <label>Your name
      <input type="text" name="name" value="<c:out value='${name}' />" autocomplete="name" required maxlength="100">
    </label>
    <label>Email address
      <input type="email" name="email" value="<c:out value='${email}' />" autocomplete="email" required maxlength="150">
    </label>
    <label>Password
      <input type="password" name="password" autocomplete="new-password" required minlength="8">
      <small>Use at least 8 characters.</small>
    </label>
    <label>How will you use KaviMart?
      <select name="role" required>
        <option value="BUYER" <c:if test="${role == 'BUYER'}">selected</c:if>>I want to shop</option>
        <option value="SELLER" <c:if test="${role == 'SELLER'}">selected</c:if>>I want to sell</option>
      </select>
    </label>
    <button class="button button-full" type="submit">Create account <span>&rarr;</span></button>
    <p class="form-foot">Already have an account? <a href="${pageContext.request.contextPath}/auth/login">Sign in</a></p>
  </form>
</section>
<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>