package com.liuyan.personalblog.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liuyan.personalblog.POJO.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CHAT_HISTORY_KEY = "chat:history";
    private static final int MAX_HISTORY_SIZE = 100;

    // 存储所有连接的用户
    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从会话属性中获取认证信息
        Map<String, Object> attributes = session.getAttributes();
        String username = (String) attributes.get("username");
        Integer userId = (Integer) attributes.get("userId");

        if (username == null || userId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Authentication required"));
            return;
        }

        // 将用户名和会话关联
        sessions.put(username, session);

        // 发送历史消息给新用户
        sendHistoryMessages(session);

        // 通知所有用户有新用户加入
        broadcastMessage(new ChatMessage("系统", username + " 加入了聊天室", System.currentTimeMillis()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 从会话属性中获取认证信息
        Map<String, Object> attributes = session.getAttributes();
        String username = (String) attributes.get("username");
        Integer userId = (Integer) attributes.get("userId");

        if (username == null || userId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Authentication required"));
            return;
        }

        String payload = message.getPayload();

        // 解析消息
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        chatMessage.setUsername(username);
        chatMessage.setTimestamp(System.currentTimeMillis());

        // 保存消息到Redis
        saveMessageToRedis(chatMessage);

        // 广播消息给所有用户
        broadcastMessage(chatMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 从会话属性中获取认证信息
        Map<String, Object> attributes = session.getAttributes();
        String username = (String) attributes.get("username");

        if (username != null) {
            sessions.remove(username);

            // 通知所有用户有用户离开
            broadcastMessage(new ChatMessage("系统", username + " 离开了聊天室", System.currentTimeMillis()));
        }
    }

    private void broadcastMessage(ChatMessage message) throws Exception {
        String messageJson = objectMapper.writeValueAsString(message);
        TextMessage textMessage = new TextMessage(messageJson);

        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen()) {
                session.sendMessage(textMessage);
            }
        }
    }

    private void saveMessageToRedis(ChatMessage message) throws Exception {
        // 检查redisTemplate是否已注入
        if (redisTemplate == null) {
            System.err.println("RedisTemplate is null, message not saved to Redis");
            return;
        }

        String messageJson = objectMapper.writeValueAsString(message);
        // 将消息添加到Redis列表的开头
        redisTemplate.opsForList().leftPush(CHAT_HISTORY_KEY, messageJson);
        // 限制列表大小，只保留最新的100条消息
        redisTemplate.opsForList().trim(CHAT_HISTORY_KEY, 0, MAX_HISTORY_SIZE - 1);
    }

    private void sendHistoryMessages(WebSocketSession session) throws Exception {
        // 检查redisTemplate是否已注入
        if (redisTemplate == null) {
            System.err.println("RedisTemplate is null, cannot fetch history messages");
            return;
        }

        // 从Redis获取最新的聊天记录
        List<String> messages = redisTemplate.opsForList().range(CHAT_HISTORY_KEY, 0, MAX_HISTORY_SIZE - 1);
        if (messages != null) {
            // 反转消息顺序，从旧到新发送
            for (int i = messages.size() - 1; i >= 0; i--) {
                session.sendMessage(new TextMessage(messages.get(i)));
            }
        }
    }
}
