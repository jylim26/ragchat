package dev.ragchat.ai.application.port.out;

import java.io.IOException;

import org.springframework.core.io.Resource;

public interface CommandVectorStorePort {
	void embeddingSingleMarkdownFile(String originName, Resource resource) throws IOException;
}
