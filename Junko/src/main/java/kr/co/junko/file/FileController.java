package kr.co.junko.file;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.FileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class FileController {

	private final FileService service;
	Map<String, Object> result = null;
	
	@GetMapping(value="/file/search/{order_idx}")
	public Map<String, Object>fileSearchByOrderIdx(@PathVariable int order_idx){
		log.info("order_idx : "+order_idx);
		result = new HashMap<String, Object>();
		FileDTO file = service.fileSearchByOrderIdx(order_idx);
		if(file != null) {
			result.put("file", file);
			result.put("success", true);
		} else {
			result.put("success", false);
		}
		return result;
	}
	
	private final Path fileStorageLocation = Paths.get("C:/uploads/pdf"); // 파일 저장 경로

    @GetMapping("/pdf/preview/{fileName}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) throws Exception {
        Path filePath = fileStorageLocation.resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            throw new Exception("파일을 찾을 수 없습니다.");
        }
    }
	
	
}
