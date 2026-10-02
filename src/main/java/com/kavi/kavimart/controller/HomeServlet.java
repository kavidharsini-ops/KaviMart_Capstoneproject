package com.kavi.kavimart.controller;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Sends the site root to the public catalog. */
public class HomeServlet extends HttpServlet {
  /** {@inheritDoc} */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.sendRedirect(request.getContextPath() + "/catalog");
  }
}
