package dev.ragchat.ai.application;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import dev.ragchat.ai.application.port.in.RAGCommandUseCase;
import dev.ragchat.ai.application.port.out.CommandVectorStorePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RAGCommandService implements RAGCommandUseCase {

	private static final String EXTENSION_MD = "md";
	private final CommandVectorStorePort commandPort;

	@Override
	public void dataEmbedding(String originName, String extension, String contentType, Resource resource) {
		try {
			if (extension.equals(EXTENSION_MD)) {
				commandPort.embeddingSingleMarkdownFile(originName, resource);
			} else {
				log.warn("not supported file type : {}", extension);
			}
		} catch (IOException e) {
			log.warn("file embedding fail : {}", originName);
		}
	}
}
