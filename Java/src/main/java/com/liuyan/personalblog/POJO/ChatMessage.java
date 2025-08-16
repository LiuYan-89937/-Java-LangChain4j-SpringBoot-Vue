package com.liuyan.personalblog.POJO;

public class ChatMessage {
    private String username;
    private String content;
    private long timestamp;
    
    // 构造函数
    public ChatMessage() {}
    
    public ChatMessage(String username, String content, long timestamp) {
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }
    
    // Getter 和 Setter 方法
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}