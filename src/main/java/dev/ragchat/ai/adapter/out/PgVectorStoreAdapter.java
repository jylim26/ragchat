package dev.ragchat.ai.adapter.out;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import dev.ragchat.ai.application.port.out.QueryVectorStorePort;
import dev.ragchat.ai.application.port.out.CommandVectorStorePort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PgVectorStoreAdapter implements CommandVectorStorePort, QueryVectorStorePort {

	private static final int TOP_K = 3;

	private final VectorStore vectorStore;

	private final TokenTextSplitter splitter = TokenTextSplitter.builder()
		.withChunkSize(800)
		.withMinChunkSizeChars(350)
		.withMinChunkLengthToEmbed(5)
		.withMaxNumChunks(10_000)
		.withKeepSeparator(true)
		.build();

	@Override
	public List<Document> similaritySearch(String query) {
		return vectorStore.similaritySearch(
				SearchRequest.builder()
					.query(query)
					.topK(TOP_K)
					.build());
	}

	@Override
	public void embeddingSingleMarkdownFile(String originName, Resource resource) throws IOException {
		String markdown = resource.getContentAsString(StandardCharsets.UTF_8);

		Map<String, Object> metadata = new HashMap<>();
		metadata.put("source.name", originName);
		metadata.put("source.type", "markdown");

		List<Document> input = List.of(new Document(markdown, metadata));
		List<Document> chunks = splitter.apply(input);

		vectorStore.add(chunks);
	}
}
