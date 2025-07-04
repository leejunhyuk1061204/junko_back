package kr.co.junko.product;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.ProductDTO;

@Mapper
public interface ProductDAO {

	int productInsert(ProductDTO dto);

	int fileWrite(String ori_filename, String new_filename, int idx, String type);
	
}
