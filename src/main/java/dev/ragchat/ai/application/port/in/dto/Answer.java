package dev.ragchat.ai.application.port.in.dto;

import java.util.List;

public record Answer(String answer, List<Citation> citations) {
	public record Citation(String sourceName, String preview) {
	}
}
