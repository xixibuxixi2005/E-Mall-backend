package com.whut.emall.ai.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whut.emall.ai.entity.KnowledgeDoc;
import com.whut.emall.ai.mapper.KnowledgeDocMapper;
import com.whut.emall.ai.vo.KnowledgeListVO;
import com.whut.emall.ai.vo.KnowledgeVO;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.utils.OSSFileManager;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KnowledgeService extends ServiceImpl<KnowledgeDocMapper, KnowledgeDoc>{
    @Resource OSSFileManager ossFileManager;
    @Resource VectorStore vectorStore;
    
    static final Map<String,String> FILE_TYPES = Map.of(
            MediaType.APPLICATION_PDF_VALUE, "pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx",
            MediaType.TEXT_PLAIN_VALUE, "txt",
            "text/markdown", "md",
            "text/x-markdown", "md",
            "text/x-md", "md"
    );
    static final int CHUNK_SIZE = 500; // 每个chunk的最大字符数

    @Transactional(rollbackFor = Exception.class)
    public KnowledgeVO uploadDoc(MultipartFile file, Integer productId, String category, String title) {
        try {
            if (title==null || title.isEmpty())
                title = file.getOriginalFilename();
            String fileType = FILE_TYPES.get(file.getContentType());
            if (fileType == null) {
                throw ApiException.err(400, "无法解析的文件类型");
            }

            String url = ossFileManager.docsUpload(new MultipartFile[]{file}).get(0);

            String text;

            if (fileType.equals("pdf")) {
                try (PDDocument document = Loader.loadPDF(file.getBytes())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    stripper.setSortByPosition(true);
                    text = stripper.getText(document);
                } catch (Exception e) {
                    throw ApiException.err(e.getLocalizedMessage());
                }
            } else if (fileType.equals("docx")) {
                try (XWPFDocument document = new XWPFDocument(file.getInputStream());
                    XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                    text = extractor.getText();
                } catch (Exception e) {
                    throw ApiException.err(e.getLocalizedMessage());
                }
            }  else if (fileType.equals("md") || fileType.equals("txt")) {
                try {
                    text = new String(file.getBytes(), StandardCharsets.UTF_8);
                    if (fileType.equalsIgnoreCase("md"))
                        text = TextContentRenderer.builder().build().render(
                            Parser.builder().build().parse(text)
                        );
                } catch (Exception e) {
                    throw ApiException.err(e.getLocalizedMessage());
                }
            } else {
                throw ApiException.err(400, "无法解析的文件类型");
            }

            if (text == null || text.isBlank()) {
                throw ApiException.err(400, "提取后的文本为空");
            }

            KnowledgeDoc doc = new KnowledgeDoc();
            doc.setTitle(title);
            doc.setCategory(category);
            doc.setProductId(productId);
            doc.setUrl(url);
            doc.setFileType(fileType);
            doc.setChunkCount(0);
            save(doc);
            doc = getById(doc.getId());

            List<Document> documents = buildChunkDocuments(doc, text);
            vectorStore.add(documents);
            doc.setChunkCount(documents.size());
            updateById(doc);
            return baseMapper.getVo(doc.getId());
        } catch (Exception e) {
            throw e;
        }
    }

    public void deleteDoc(Integer id) {
        KnowledgeDoc doc = getById(id);
        if (doc == null)
            throw ApiException.err(404, "未找到该文档");
        ossFileManager.docsDelete(Arrays.asList(doc.getUrl()));
        vectorStore.delete(new Filter.Expression(
            Filter.ExpressionType.EQ,
            new Filter.Key("docId"),
            new Filter.Value(doc.getId())
        ));
        removeById(id);
    }

    public KnowledgeListVO listDoc(Integer pageNum, Integer pageSize, String keyword, String category) {
        Page<KnowledgeVO> page = getBaseMapper().getVos(new Page<>(pageNum, pageSize), keyword, category);
        return new KnowledgeListVO(page);
    }

    private List<Document> buildChunkDocuments(KnowledgeDoc doc, String text) {
        List<String> chunks = splitTextIntoChunks(text);
        List<Document> documents = new ArrayList<>(chunks.size());
        for (int index = 0; index < chunks.size(); index++) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("docId", doc.getId());
            metadata.put("docTitle", doc.getTitle());
            metadata.put("category", doc.getCategory() == null ? "null" : doc.getCategory());
            metadata.put("productId", doc.getProductId() == null ? "null" : doc.getProductId().toString());
            metadata.put("chunkIndex", index);
            documents.add(new Document(chunks.get(index), metadata));
        }
        return documents;
    }

    private List<String> splitTextIntoChunks(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String normalizedText = text.replace("\r\n", "\n").trim();
        List<String> paragraphs = Arrays.stream(normalizedText.split("\\n{2,}"))
                .map(String::trim)
                .filter(paragraph -> !paragraph.isEmpty())
                .toList();
        if (paragraphs.isEmpty()) {
            return List.of(normalizedText);
        }

        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        for (String paragraph : paragraphs) {
            if (paragraph.length() > CHUNK_SIZE) {
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk.setLength(0);
                }
                chunks.addAll(splitLongParagraph(paragraph, CHUNK_SIZE));
                continue;
            }
            if (currentChunk.length() > 0 && currentChunk.length() + paragraph.length() + 2 > CHUNK_SIZE) {
                chunks.add(currentChunk.toString().trim());
                currentChunk.setLength(0);
            }
            if (currentChunk.length() > 0) {
                currentChunk.append("\n\n");
            }
            currentChunk.append(paragraph);
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }
        return chunks;
    }

    private List<String> splitLongParagraph(String paragraph, int maxLength) {
        List<String> parts = new ArrayList<>();
        String remaining = paragraph.trim();
        while (remaining.length() > maxLength) {
            int splitIndex = remaining.lastIndexOf('\n', maxLength);
            if (splitIndex <= 0) {
                splitIndex = remaining.lastIndexOf(' ', maxLength);
            }
            if (splitIndex <= 0) {
                splitIndex = maxLength;
            }
            parts.add(remaining.substring(0, splitIndex).trim());
            remaining = remaining.substring(splitIndex).trim();
        }
        if (!remaining.isEmpty()) {
            parts.add(remaining);
        }
        return parts;
    }
}
