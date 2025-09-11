package dev.ragchat.adapter.out;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;

import dev.ragchat.ai.adapter.out.PgVectorStoreAdapter;

class PgVectorStoreAdapterTest {

	private VectorStore vectorStore;
	private PgVectorStoreAdapter sut;

	@BeforeEach
	void setUp() {
		vectorStore = mock(VectorStore.class);
		sut = new PgVectorStoreAdapter(vectorStore);
	}

	@Test
	void 본문_내용이_청크에_포함된다() throws Exception {
		// given
		String md = "# 제목\n\n본문 내용입니다.\n두 번째 줄입니다.";
		ByteArrayResource resource = new ByteArrayResource(md.getBytes(StandardCharsets.UTF_8));

		// when
		sut.embeddingSingleMarkdownFile("sample.md", resource);

		// then
		ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
		verify(vectorStore).add(captor.capture());

		List<Document> captured = captor.getValue();
		assertThat(captured).isNotEmpty();

		for (Document d : captured) {
			Map<String, Object> meta = d.getMetadata();
			assertThat(meta.get("source.name")).isEqualTo("sample.md");
			assertThat(meta.get("source.type")).isEqualTo("markdown");
		}
	}

}