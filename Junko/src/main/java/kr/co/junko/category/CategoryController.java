package kr.co.junko.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.junko.dto.CategoryDTO;
import kr.co.junko.dto.ProductDTO;
import kr.co.junko.product.ProductService;
import kr.co.junko.util.Jwt;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Slf4j
public class CategoryController {

	@Autowired CategoryService service;
	@Autowired ProductService productService;
	
	Map<String, Object> result = null;
	
	// 카테고리 추가
	@PostMapping(value="/cate/insert")
	public Map<String, Object>cateInsert(
			@RequestBody CategoryDTO dto
			,@RequestHeader Map<String, String> header){
		
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		
		String loginId = null;
		boolean login = false;
		boolean success = false;

		String token = header.get("authorization");
		Map<String, Object> payload = Jwt.readToken(token);
		loginId = (String) payload.get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			login = true;
			success = service.cateInsert(dto);
		}

		result.put("success", success);
		result.put("loginYN", login);
		result.put("name", dto.getCategory_name());
		result.put("parent", dto.getCategory_parent());

		return result;
	}
	
	// 카테고리 수정
	@PutMapping(value="/cate/update")
	public Map<String, Object> cateUpdate(
			@RequestBody CategoryDTO dto
			,@RequestHeader Map<String, String> header){
		log.info("dto : {}",dto);
		result = new HashMap<String, Object>();
		
		String loginId = null;
		boolean login = false;
		boolean success = false;

		String token = header.get("authorization");
		Map<String, Object> payload = Jwt.readToken(token);
		loginId = (String) payload.get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			login = true;
			success = service.cateUpdate(dto);
		}
		
		result.put("success", success);
		result.put("loginYN", login);
		result.put("name", dto.getCategory_name());
		result.put("parent", dto.getCategory_parent());
		
		return result;
	}
	
	// 카테고리 리스트
	@GetMapping(value="/cate/list")
	public Map<String, Object> cateList(){
		result = new HashMap<String, Object>();
		ArrayList<CategoryDTO> list = service.cateList();
		result.put("list", list);
		return result;
	}
	
	// 카테고리 삭제
	@DeleteMapping(value="/cate/delete")
	public Map<String, Object> cateDel(
			@RequestBody CategoryDTO dto
			,@RequestHeader Map<String, String> header){
		log.info("dto : {}", dto);
		result = new HashMap<String, Object>();
		
		String loginId = null;
		boolean login = false;
		boolean success = false;

		String token = header.get("authorization");
		Map<String, Object> payload = Jwt.readToken(token);
		loginId = (String) payload.get("user_id");

		if (loginId != null && !loginId.isEmpty()) {
			login = true;
			success = service.cateDel(dto);
		}
		
		result.put("success", success);
		result.put("loginYN", login);
		
		if (!success) {
			result.put("message", "하위 카테고리가 있어 삭제할 수 없습니다.");
		} else {
			result.put("name", dto.getCategory_idx());
		}
		
		return result;
	}
	
	// 카테고리 트리
	@GetMapping("/cate/tree")
	public Map<String, Object> cateTree() {
		result = new HashMap<String, Object>();
		
		List<CategoryDTO> treeList = service.cateTree();
		result.put("list", treeList);

		return result;
	}

	// 특정 카테고리의 하위 카테고리 ID 가져오기
	@GetMapping("/cate/childList")
	public Map<String, Object> childCategoryIdx(@RequestParam int category_idx) {
		Map<String, Object> result = new HashMap<String, Object>();

		ArrayList<Integer> childIdx = service.childCategoryIdx(category_idx);
		result.put("childIdx", childIdx);

		return result;
	}
	
	// 카테고리 경로 조회
	@GetMapping("/cate/path")
	public Map<String, Object> catePath(@RequestParam int category_idx){
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<String> path = service.catePath(category_idx);
		result.put("path", path);
		
		return result;
	}
	
	// 카테고리 순서
	@PostMapping("/cate/reorder")
	public Map<String, Object> reorder(@RequestBody List<CategoryDTO> list,
	                                   @RequestHeader Map<String, String> header) {
	    Map<String, Object> result = new HashMap<>();
	    String token = header.get("authorization");
	    String loginId = (String) Jwt.readToken(token).get("user_id");

	    boolean login = loginId != null && !loginId.isEmpty();
	    boolean success = false;

	    if (login) {
	        success = service.reorder(list);
	    }

	    result.put("success", success);
	    result.put("loginYN", login);
	    return result;
	}


}
