package com.whut.emall.ai.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.whut.emall.ai.service.KnowledgeService;
import com.whut.emall.ai.vo.KnowledgeListVO;
import com.whut.emall.ai.vo.KnowledgeVO;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/knowledge")
@Tag(name = "知识文档接口", description = "管理商家的知识问答，提供RAG搜索源")
public class KnowledgeController {
    @Resource KnowledgeService knowledgeService;

    // 支持的文件 MIME 类型（白名单）
    static final Set<String> SUPPORTED_MIME_TYPES = new HashSet<>(Arrays.asList(
            MediaType.APPLICATION_PDF_VALUE,    // application/pdf
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // docx
            "application/msword",
            MediaType.TEXT_PLAIN_VALUE, // text/plain
            "text/markdown",            // md 常见
            "text/x-markdown",          // md 另一种
            "text/x-md"                 // 少数情况
    ));

    @Operation(summary = "知识文档上传（未实现向量化）", description = "支持PDF/DOCX/TXT/MD格式文档上传，存入向量数据库")
    @ApiResponse(responseCode = "200", description = "文档上传并向量化成功")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("upload")
    public ApiResult<KnowledgeVO> uploadDoc(
        @RequestParam MultipartFile file,  // （支持PDF/DOCX/TXT/MD）
        @RequestParam(required = false) Integer productId, // 关联商品ID（若为商品说明书）
        @RequestParam(required = false) String category, // 分类（如 policy / manual / faq）
        @RequestParam(required = false) String title    // 自定义标题（默认取文件名）
    ) {
        if (file.getContentType() == null || !SUPPORTED_MIME_TYPES.contains(file.getContentType()))
            throw ApiException.err(400, "不支持的文件类型，请上传PDF/DOCX/TXT/MD格式文档");
        // TODO:
        // return ApiResult.ok("文档上传并向量化成功", knowledgeService.uploadDoc(file, productId, category, title));
        return new ApiResult<>(501, "文件已上传，但是未实现向量化", knowledgeService.uploadDoc(file, productId, category, title));
    }
    
    @Operation(summary = "知识文档删除", description = "删除已上传的知识文档")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("{docId}")
    public ApiResult<Void> deleteDoc(@PathVariable Integer docId) {
        knowledgeService.deleteDoc(docId);
        return ApiResult.ok("删除成功");
    }
    
    @Operation(summary = "知识文档列表查询", description = "查询知识文档列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("list")
    public ApiResult<KnowledgeListVO> listDoc(
        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
        @RequestParam(required = false, defaultValue = "10") Integer pageSize,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String category
    ) {
        var result = knowledgeService.listDoc(pageNum, pageSize, keyword, category);
        return ApiResult.ok("查询成功", result);
    }
}
