package kr.co.junko.category;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.CategoryDTO;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class CategoryService {

	@Autowired CategoryDAO dao;
	
	Map<String, Object> result = null;
	
	public boolean cateInsert(CategoryDTO dto) {
		int row = dao.cateInsert(dto);
		return row>0;
	}

	public boolean cateUpdate(CategoryDTO dto) {
		int row = dao.cateUpdate(dto);
		return row>0;
	}

	public ArrayList<CategoryDTO> cateList() {
		return dao.cateList();
	}
	
	public boolean cateDel(CategoryDTO dto) {
		int childCount = dao.childCnt(dto.getCategory_idx());
		if (childCount > 0) {
			log.warn("하위 카테고리가 있어 삭제 불가: {}", dto.getCategory_idx());
			return false; // 하위 카테고리 있으면 삭제 안함.
		}

		int row = dao.cateDel(dto);
		return row > 0;
	}

}
