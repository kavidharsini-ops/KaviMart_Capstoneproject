package com.kavi.kavimart.controller;

import com.kavi.kavimart.dto.UserResponseDTO;import com.kavi.kavimart.exception.*;import com.kavi.kavimart.util.*;import javax.servlet.*;import javax.servlet.http.*;import java.io.IOException;import java.util.Map;import org.slf4j.Logger;import org.slf4j.LoggerFactory;
/** Shared HTTP helpers; controllers only coordinate request and service calls. */
public abstract class BaseServlet extends HttpServlet {
 protected final Logger log=LoggerFactory.getLogger(getClass());
 protected UserResponseDTO user(HttpServletRequest r){Object u=r.getSession(false)==null?null:r.getSession(false).getAttribute(SessionKeys.USER);return u instanceof UserResponseDTO dto?dto:null;}
 protected <T>T service(ServletContext c,String key,Class<T> type){return type.cast(c.getAttribute(key));}
 protected void error(HttpServletRequest req,HttpServletResponse res,Throwable t)throws IOException,ServletException{if(t instanceof AppException e){if(req.getRequestURI().contains("/api/")){Map<String,String> fields=t instanceof ValidationException v?v.getFields():null;JsonUtil.error(res,e.getStatus(),e.getCode(),e.getMessage(),fields);return;}res.setStatus(e.getStatus());req.setAttribute("errorMessage",e.getMessage());req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req,res);return;}log.error("Request failed",t);if(req.getRequestURI().contains("/api/")){JsonUtil.error(res,500,"INTERNAL_ERROR","The request could not be completed.",null);return;}res.setStatus(500);req.setAttribute("errorMessage","Something went wrong. Please try again.");req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req,res);}
 protected long longParam(HttpServletRequest r,String name){try{return Long.parseLong(r.getParameter(name));}catch(Exception e){throw new ValidationException(Map.of(name,"Enter a valid number."));}}
 protected int intParam(HttpServletRequest r,String name,int fallback){try{return Integer.parseInt(r.getParameter(name));}catch(Exception e){if(r.getParameter(name)==null)return fallback;throw new ValidationException(Map.of(name,"Enter a valid whole number."));}}
 protected void page(HttpServletRequest r,HttpServletResponse s,String view)throws ServletException,IOException{r.getRequestDispatcher("/WEB-INF/jsp/"+view).forward(r,s);}
 protected String path(HttpServletRequest r){return r.getServletPath()+ (r.getPathInfo()==null?"":r.getPathInfo());}
}
