package com.whut.emall.ai.service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;

@Service
public class LLMService {
    @Resource ChatModel chatModel;
    
    public String docsToPrompt(List<Document> docs, Double minConf) {
        if (docs==null || docs.isEmpty()) return "";
        final double conf = minConf==null ? 0. : minConf;
        docs = docs.stream().filter(doc -> doc.getScore()>=conf).toList();
        StringBuilder builder = new StringBuilder();
        for (int i=0 ; i<docs.size() ; ++i) {
            var doc = docs.get(i);
            builder.append(String.format(
                "[参考%d(置信度:%f),分块序号:%d]标题：%s\n%s\n\n",
                i+1, doc.getScore(), ((Double)doc.getMetadata().get("chunkIndex")).intValue(), doc.getMetadata().get("docTitle"), doc.getText()
            ));
        }
        return builder.toString();
    }

    public Flux<String> streamSimilarChar(String question, List<Document> docs, Object ...tools) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("你是一个AI问答助理，根据可能的参考资料回答用户问题。若根据现有资料无法回答，回复“根据知识库内容无法回答”。资料：\n");
        promptBuilder.append(docsToPrompt(docs, 0.5));
        String prompt = promptBuilder.toString();
        ChatClient client = ChatClient.builder(chatModel).build();
        var spec = client.prompt().system(prompt).user(question);
        for (var tool: tools) {
            spec.tools(tool);
        }
        return spec.stream().content();
    }

    public <T> T customPromptStructCall(String prompt, String question, Class<T> responseType, Object ...tools) {
        ChatClient client = ChatClient.builder(chatModel).build();
        var spec = client.prompt().system(prompt).user(question);
        for (var tool: tools) {
            spec.tools(tool);
        }
        return spec.call().entity(responseType);
    }
}
