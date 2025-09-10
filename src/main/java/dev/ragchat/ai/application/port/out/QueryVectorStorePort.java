package dev.ragchat.ai.application.port.out;

import java.util.List;

import org.springframework.ai.document.Document;

public interface QueryVectorStorePort {
	List<Document> similaritySearch(String query);
}
