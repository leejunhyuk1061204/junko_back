package kr.co.junko.sales;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.SalesDTO;
import kr.co.junko.dto.SalesProductDTO;

@Mapper
public interface SalesDAO {

	int salesInsert(SalesDTO dto);

	int salesUpdate(SalesDTO dto);

	List<Map<String, Object>> salesList(Map<String, Object> param);

	int salesListTotalPage(Map<String, Object> param);

	int salesDel(int idx);

	SalesDTO salesDetailByIdx(int idx);

	int salesProductInsert(SalesProductDTO product);

	int salesProductUpdate(SalesProductDTO dto);

	List<Map<String, Object>> salesProductList(Map<String, Object> param);

	int salesProdcutListTotalPage(Map<String, Object> param);

	int salesProductDel(int sales_product_idx);

	int searchPrice(int product_idx);

}
