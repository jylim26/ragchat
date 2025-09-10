package dev.ragchat.ai.adapter.in.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.ragchat.ai.application.RAGCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

	private final RAGCommandService ragCommandService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, Object>> uploadFile(@RequestPart("file") MultipartFile file) throws Exception {
		String originName = file.getOriginalFilename();
		String extension = StringUtils.getFilenameExtension(originName);
		String contentType = file.getContentType();

		log.info("upload request. originName={}, extension={}, contentType={}", originName, extension, contentType);

		ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
			@Override
			public String getFilename() {
				return originName;
			}
		};

		ragCommandService.dataEmbedding(originName, extension, contentType, resource);

		Map<String, Object> body = new HashMap<>();
		body.put("ok", true);
		body.put("file", originName);
		return ResponseEntity.ok(body);
	}
}
