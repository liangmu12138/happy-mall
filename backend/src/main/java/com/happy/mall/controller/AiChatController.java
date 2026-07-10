package com.happy.mall.controller;

import com.happy.mall.common.Result;
import com.happy.mall.entity.ChatMessage;
import com.happy.mall.service.AiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI聊天控制器
 */
@Tag(name = "AI聊天接口")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;

    /**
     * 发送消息并获取AI回复
     * POST /api/ai/chat
     */
    @Operation(summary = "发送消息")
    @PostMapping("/chat")
    public Result<?> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return Result.error("消息不能为空");
        }

        ChatMessage reply = aiChatService.generateResponse(message);
        return Result.success(reply);
    }

    /**
     * 获取欢迎消息
     * GET /api/ai/welcome
     */
    @Operation(summary = "获取欢迎消息")
    @GetMapping("/welcome")
    public Result<?> getWelcomeMessage() {
        ChatMessage welcome = aiChatService.getWelcomeMessage();
        return Result.success(welcome);
    }

    /**
     * 生成资料描述
     * POST /api/ai/generate/material
     */
    @Operation(summary = "生成资料描述")
    @PostMapping("/generate/material")
    public Result<?> generateMaterialDescription(@RequestBody Map<String, String> request) {
        String title = request.get("title");
        String category = request.get("category");
        String subject = request.get("subject");
        String content = request.get("content");

        String description = aiChatService.generateMaterialDescription(title, category, subject, content);
        return Result.success(Map.of("description", description));
    }

    /**
     * 生成课程评价
     * POST /api/ai/generate/review
     */
    @Operation(summary = "生成课程评价")
    @PostMapping("/generate/review")
    public Result<?> generateReviewContent(@RequestBody Map<String, Object> request) {
        String courseName = (String) request.get("courseName");
        String teacherName = (String) request.get("teacherName");
        int rating = request.get("rating") != null ? Integer.parseInt(request.get("rating").toString()) : 5;
        int difficulty = request.get("difficulty") != null ? Integer.parseInt(request.get("difficulty").toString()) : 3;
        String tips = (String) request.get("tips");

        String content = aiChatService.generateReviewContent(courseName, teacherName, rating, difficulty, tips);
        return Result.success(Map.of("content", content));
    }

    /**
     * 生成学习搭子帖子
     * POST /api/ai/generate/buddy
     */
    @Operation(summary = "生成学习搭子帖子")
    @PostMapping("/generate/buddy")
    public Result<?> generateBuddyContent(@RequestBody Map<String, Object> request) {
        String type = (String) request.get("type");
        String title = (String) request.get("title");
        String target = (String) request.get("target");
        String location = (String) request.get("location");
        String timeInfo = (String) request.get("timeInfo");
        int maxMembers = request.get("maxMembers") != null ? Integer.parseInt(request.get("maxMembers").toString()) : 2;

        String content = aiChatService.generateBuddyContent(type, title, target, location, timeInfo, maxMembers);
        return Result.success(Map.of("content", content));
    }
}
