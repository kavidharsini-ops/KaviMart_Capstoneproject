package com.kavi.kavimart.controller;

import com.kavi.kavimart.dto.UserResponseDTO;import com.kavi.kavimart.service.ReviewService;import com.kavi.kavimart.util.SessionKeys;import javax.servlet.ServletException;import javax.servlet.http.*;import java.io.IOException;
/** Web controller for verified-purchase review submission. */
public class ReviewServlet extends BaseServlet {
 protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{try{UserResponseDTO user=user(req);ReviewService reviews=service(getServletContext(),SessionKeys.REVIEW_SERVICE,ReviewService.class);long productId=longParam(req,"productId");reviews.create(user.getId(),productId,intParam(req,"rating",0),req.getParameter("comment"));res.sendRedirect(req.getContextPath()+"/products/"+productId+"#reviews");}catch(Throwable t){error(req,res,t);}}
}
