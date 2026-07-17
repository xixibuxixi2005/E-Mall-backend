package com.whut.emall.ai.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.ai.entity.CommentSentiment;
import com.whut.emall.ai.service.SentimentService;
import com.whut.emall.ai.vo.BatchSentimentVO;
import com.whut.emall.common.entity.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/sentiment")
@Tag(name = "评论情感分析接口", description = "根据用户输入获取情感信息和关键词")
public class SentimentController {
    @Resource SentimentService sentimentService;

    @Operation(summary = "评论情感分析（单条）", description = "根据用户输入的评论内容，返回情感类型、置信度和关键词")
    @ApiResponse(responseCode = "200", description = "分析成功")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("analyze")
    public ApiResult<CommentSentiment> analyzeComment(@RequestBody CommentDTO dto) {
        return ApiResult.ok("分析成功", sentimentService.analyze(dto.getText()));
    }
    @Operation(summary = "评论情感分析（批量）", description = "根据用户输入的评论内容列表，返回每条评论的情感类型、置信度和关键词")
    @ApiResponse(responseCode = "200", description = "分析成功")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("batch")
    public ApiResult<BatchSentimentVO> analyzeComments(@RequestBody CommentsDTO dto) {
        return ApiResult.ok("分析成功", sentimentService.analyze(dto.getTexts()));
    }
    
    @Data
    static class CommentDTO {
        @NotBlank(message = "评论内容不能为空") String text;
    }
    @Data
    static class CommentsDTO {
        @Size(min = 1, message = "评论内容不能为空") List<String> texts;
    }
}
