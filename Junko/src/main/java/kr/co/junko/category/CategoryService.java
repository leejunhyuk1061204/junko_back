package kr.co.junko.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

    public List<CategoryDTO> cateTree() {
		List<CategoryDTO> flatList = dao.cateList();
		Map<Integer, CategoryDTO> map = new HashMap<>();

		// 먼저 모든 카테고리를 Map에 저장
		for (CategoryDTO cate : flatList) {
			map.put(cate.getCategory_idx(), cate);
		}

		// 트리 구조 만들기
		List<CategoryDTO> rootList = new ArrayList<>();
		for (CategoryDTO cate : flatList) {
			Integer parentIdx = cate.getCategory_parent();
			if (parentIdx == null) {
				// 루트 카테고리
				rootList.add(cate);
			} else {
				CategoryDTO parent = map.get(parentIdx);
				if (parent != null) {
					if (parent.getChildren() == null) parent.setChildren(new ArrayList<>());
					parent.getChildren().add(cate);
				}
			}
		}

		return rootList;
	}

	public ArrayList<Integer> childCategoryIdx(int category_idx) {
		ArrayList<Integer> result = new ArrayList<>();
		collectChildren(category_idx, result);
		log.info("최종 하위 카테고리: {}", result);
		return result;
	}

	private void collectChildren(int category_idx, ArrayList<Integer> acc) {
		ArrayList<CategoryDTO> children = dao.childCategoryIdx(category_idx);
		log.info("현재 category_idx: {}, children.size={}", category_idx, children.size());

		for (CategoryDTO child : children) {
			acc.add(child.getCategory_idx());
			collectChildren(child.getCategory_idx(), acc); // 재귀 호출
		}
	}

	public List<String> catePath(int categoryIdx) {
	    List<String> path = new ArrayList<>();
	    buildPath(categoryIdx, path);
	    Collections.reverse(path); // 상위부터 보여주기 위해 역순 정렬
	    return path;
	}

	private void buildPath(int categoryIdx, List<String> path) {
	    CategoryDTO category = dao.catePath(categoryIdx);
	    if (category != null) {
	        path.add(category.getCategory_name());
	        
	        if (category.getCategory_parent() != null) {
	            buildPath(category.getCategory_parent(), path); // 재귀 호출
	        }
	    }
	}
	
	public List<Integer> getCategoryWithChildren(int category_idx) {
	    ArrayList<Integer> result = new ArrayList<>();
	    result.add(category_idx); // 본인 포함
	    collectChildren(category_idx, result); // 하위 카테고리 재귀 호출
	    return result;
	}

}
