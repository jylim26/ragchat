package dev.ragchat.ai.application.port.in;

import org.springframework.core.io.Resource;

public interface RAGCommandUseCase {

	void dataEmbedding(String originName, String extension, String contentType, Resource resource);
}
