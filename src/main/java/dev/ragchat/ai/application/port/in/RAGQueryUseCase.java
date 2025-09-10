package dev.ragchat.ai.application.port.in;

import dev.ragchat.ai.application.port.in.dto.Answer;

public interface RAGQueryUseCase {

	Answer ask(String question, String id);
}
