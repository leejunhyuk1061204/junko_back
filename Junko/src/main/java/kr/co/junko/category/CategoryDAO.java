package kr.co.junko.category;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.CategoryDTO;

@Mapper
public interface CategoryDAO {

	Integer cateInsert(CategoryDTO dto);

	int cateUpdate(CategoryDTO dto);

	int cateDel(CategoryDTO dto);

	ArrayList<CategoryDTO> cateList();

	int childCnt(int category_idx);

    ArrayList<CategoryDTO> childCategoryIdx(int category_idx);

}
