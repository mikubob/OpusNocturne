package com.xuan.common.interceptor;

import com.nimbusds.jwt.JWTClaimsSet;
import com.xuan.common.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取令牌
        String token = request.getHeader(jwtUtils.getHeader());
        
        // 检查令牌是否存在
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing authorization token");
            return false;
        }
        
        // 移除Bearer前缀（如果有）
        String tokenPrefix = jwtUtils.getTokenPrefix();
        if (token.startsWith(tokenPrefix)) {
            token = token.substring(tokenPrefix.length());
        }
        
        try {
            // 解析并验证令牌
            JWTClaimsSet claimsSet = jwtUtils.getAllClaimsFromToken(token);
            
            // 将用户信息存储到请求上下文中
            request.setAttribute("userClaims", claimsSet);
            request.setAttribute("username", claimsSet.getSubject());
            
            return true;
        } catch (Exception e) {
            // 令牌无效或过期
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token: " + e.getMessage());
            return false;
        }
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 后处理，无需实现
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 完成后处理，无需实现
    }
}
