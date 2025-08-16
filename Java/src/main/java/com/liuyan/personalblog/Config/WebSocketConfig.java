package com.liuyan.personalblog.Config;

import com.liuyan.personalblog.Controller.ChatWebSocketHandler;
import com.liuyan.personalblog.Intercepter.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ChatWebSocketHandler chatWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/chat/{username}")
                .addInterceptors(new AuthHandshakeInterceptor())
                .setAllowedOrigins("*");
    }

    public static class AuthHandshakeInterceptor implements HandshakeInterceptor {

        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            // 从请求参数中获取token
            String query = request.getURI().getQuery();
            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    if (param.startsWith("token=")) {
                        String token = param.substring(6);
                        try {
                            // 验证token
                            var claims = com.liuyan.personalblog.Utils.JwtUtils.validateToken(token);
                            // 将用户ID存入attributes，在WebSocket会话中使用
                            attributes.put("userId", claims.get("id"));
                            attributes.put("username", claims.get("username"));
                            return true;
                        } catch (Exception e) {
                            // Token验证失败
                            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                            return false;
                        }
                    }
                }
            }
            // 没有提供token，拒绝连接
            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return false;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Exception exception) {
            // 握手后不需要特殊处理
        }
    }
}
