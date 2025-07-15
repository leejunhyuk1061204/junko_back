package kr.co.junko.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.FileDTO;
import kr.co.junko.dto.ProductDTO;
import kr.co.junko.dto.ProductHistoryDTO;

@Mapper
public interface ProductDAO {

	int productInsert(ProductDTO dto);

	int fileWrite(String ori_filename, String new_filename, int idx, String type);
	
	int productUpdate(ProductDTO dto);

	int softDelProductImg(int product_idx);

	int softDelProduct(int product_idx);

    List<ProductDTO> productList(int start, int size, String search, int category, String sort);

    int productTotalCnt(String search, int category);

	void productDocsUpload(String oriName, String newName, int product_idx);

	int productDocsCnt(int product_idx);

	int productDocsDel(int doc_id);

	List<FileDTO> productDocsList(int product_idx);

	String downloadProductDoc(String fileName);

	ProductDTO selectProductIdx(int product_idx);

	int insertProductHistory(ProductHistoryDTO history);

	int LoginUserIdx(String loginId);

    ProductDTO productDetail(int product_idx);

	List<String> selectProductImages(int product_idx);

    void softDelProductImgByFileName(String fileName);
	
	List<Map<String, Object>> productNoptionList(Map<String, Object> param);

	int productNoptionListTotalPage(Map<String, Object> param);

	List<ProductDTO> getProductCateIdx(Map<String, Object> map);

}
