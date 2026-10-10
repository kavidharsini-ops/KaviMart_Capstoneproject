package com.kavi.kavimart.controller;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kavi.kavimart.dto.UserResponseDTO;
import com.kavi.kavimart.service.UserService;
import com.kavi.kavimart.util.SessionKeys;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

class AuthServletTest {

  @Test
  void loginRotatesSessionAndRedirectsByRole() throws Exception {
    ServletContext context = mock(ServletContext.class);
    UserService users = mock(UserService.class);
    when(context.getAttribute(SessionKeys.USER_SERVICE)).thenReturn(users);
    when(users.login("seller@example.com", "longPassword"))
        .thenReturn(new UserResponseDTO(2, "Seller", "seller@example.com", "SELLER", true));

    ServletConfig config = mock(ServletConfig.class);
    when(config.getServletContext()).thenReturn(context);

    AuthServlet servlet = new AuthServlet();
    servlet.init(config);

    HttpServletRequest req = mock(HttpServletRequest.class);
    HttpServletResponse res = mock(HttpServletResponse.class);
    HttpSession session = mock(HttpSession.class);

    when(req.getServletPath()).thenReturn("/auth");
    when(req.getPathInfo()).thenReturn("/login");
    when(req.getParameter("email")).thenReturn("seller@example.com");
    when(req.getParameter("password")).thenReturn("longPassword");
    when(req.getSession(true)).thenReturn(session);
    when(req.getContextPath()).thenReturn("");

    servlet.doPost(req, res);

    verify(req).changeSessionId();
    verify(session).setAttribute(
        eq(SessionKeys.USER),
        argThat(value -> value instanceof UserResponseDTO dto && dto.getRole().equals("SELLER"))
    );
    verify(res).sendRedirect("/seller/dashboard");
  }
}
