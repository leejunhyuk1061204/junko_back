package kr.co.junko.product;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.ProductDTO;

@Mapper
public interface ProductDAO {

	int productInsert(ProductDTO dto);

	int fileWrite(String ori_filename, String new_filename, int idx, String type);
	
	int productUpdate(ProductDTO dto);

	int softDelProductImg(int product_idx);

	int softDelProduct(int product_idx);

    List<ProductDTO> productList(int start, int size, String search, int category, String sort);

    int productTotalCnt(String search, int category);
}
