package dev.ragchat.ai.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import dev.ragchat.ai.application.port.in.RAGQueryUseCase;
import dev.ragchat.ai.application.port.in.dto.Answer;
import dev.ragchat.ai.application.port.out.QueryVectorStorePort;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RAGQueryService implements RAGQueryUseCase {

	private final QueryVectorStorePort vectorStorePort;
	private final ChatClient chatClient;

	@Value("classpath:/prompts/rag-prompt-template.st")
	private Resource ragPromptTemplate;
	private PromptTemplate promptTemplate;

	@PostConstruct
	private void init() {
		this.promptTemplate = new PromptTemplate(ragPromptTemplate);
	}

	@Override
	public Answer ask(String question, String id) {
		List<Document> similarDocuments = vectorStorePort.similaritySearch(question);
		if (similarDocuments == null || similarDocuments.isEmpty()) {
			throw new IllegalArgumentException("유사한 문서가 존재하지 않습니다.");
		}

		String docsBlock = buildDocumentsBlock(similarDocuments);

		Map<String, Object> params = new HashMap<>();
		params.put("input", question);
		params.put("documents", docsBlock);

		String conversationId = id;

		Prompt prompt = promptTemplate.create(params);
		String answerText = chatClient
			.prompt(prompt)
			.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
			.call()
			.content();

		List<Answer.Citation> citations = toCitations(similarDocuments);
		return new Answer(answerText, citations);
	}

	private List<Answer.Citation> toCitations(List<Document> docs) {
		List<Answer.Citation> list = new ArrayList<>();
		for (int i = 0; i < docs.size(); i++) {
			Document doc = docs.get(i);
			Map<String, Object> metadata = doc.getMetadata();
			String sourceName = (String)metadata.get("source.name");
			String prev = doc.getText().substring(0, 160);
			list.add(new Answer.Citation(sourceName, prev));
		}
		return list;
	}

	private String buildDocumentsBlock(List<Document> docs) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < docs.size(); i++) {
			String raw = docs.get(i).getText();
			sb.append("### [").append(i + 1).append("]\n").append(raw).append("\n\n");
		}
		return sb.toString().trim();
	}
}
