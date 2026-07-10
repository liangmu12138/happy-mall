package com.happy.mall.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 聊天消息实体
 */
@Data
public class ChatMessage {

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送者：user-用户，assistant-AI助手
     */
    private String role;

    /**
     * 时间戳
     */
    private String timestamp;

    public ChatMessage() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public ChatMessage(String content, String role) {
        this.content = content;
        this.role = role;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
