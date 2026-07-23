
import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whut.emall.ai.AIApplication;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(classes = AIApplication.class)
public class UnitTest1 {
    @Resource VectorStore vectorStore;
    final static ObjectMapper mapper = new ObjectMapper();

    @Test
    @SneakyThrows
    public void test() {
        var a = vectorStore.similaritySearch("充电速度");
        var b = a.stream().map(doc -> doc.getMetadata()).toList();
        var c = b.get(0).get("chunkIndex");
        int d = (int) c;
        log.info(c.toString());
    }
}
