package dev.ragchat.ai.adapter.in.web;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ragchat.ai.adapter.in.web.dto.QueryRequest;
import dev.ragchat.ai.application.port.in.RAGQueryUseCase;
import dev.ragchat.ai.application.port.in.dto.Answer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

	private final RAGQueryUseCase useCase;

	@PostMapping("/{sessionId}/query")
	public Answer ask(@PathVariable(name = "sessionId") String sessionId, @RequestBody QueryRequest request) {
		sessionId = "temp";
		log.info("Query Request. sessionId={}, request={}", sessionId, request);

		return useCase.ask(request.question(), sessionId);
	}
}
