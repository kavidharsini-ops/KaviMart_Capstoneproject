package com.kavi.kavimart.filter;
import javax.servlet.*;import java.io.IOException;
/** Applies UTF-8 encoding consistently to all requests and responses. */
public class EncodingFilter implements Filter { /** {@inheritDoc} */ public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain)throws IOException,ServletException{request.setCharacterEncoding("UTF-8");response.setCharacterEncoding("UTF-8");chain.doFilter(request,response);} }
