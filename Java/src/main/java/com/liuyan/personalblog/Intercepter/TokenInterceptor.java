package com.liuyan.personalblog.Intercepter;

import com.liuyan.personalblog.Utils.CurrentHolder;
import com.liuyan.personalblog.Utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Token");
        if(token != null){

            try {
                Claims claims = JwtUtils.validateToken(token);
                Integer id = Integer.valueOf(claims.get("id").toString());
                System.out.println(id);
                CurrentHolder.setCurrentId(id);
            } catch (JwtException e) {
                response.setStatus(401);
                return false;
            }
        }
        else {
            response.setStatus(401);
            return false;
        }
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        CurrentHolder.remove();
    }
}
