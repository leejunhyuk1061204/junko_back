package kr.co.junko.option;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.CombinedDTO;
import kr.co.junko.dto.OptionDTO;
import kr.co.junko.dto.ProductOptionDTO;
import kr.co.junko.dto.UsingOptionDTO;

@Mapper
public interface OptionDAO {

    int optionInsert(OptionDTO dto);

    int optionUpdate(OptionDTO dto);

    int optionDel(int option_idx);

    int optionUse(UsingOptionDTO dto);

    OptionDTO selectOptionByIdx(int option_idx);

    int optionUseDel(int using_idx);

    List<UsingOptionDTO> usingOptionList(int product_idx);

    int combinedInsert(CombinedDTO dto);

    List<OptionDTO> getOptionsByProduct(int product_idx);

    int insertCombined(CombinedDTO combinedDTO);

    int insertProductOption(ProductOptionDTO podto);

    List<String> selectCombinedProduct(int product_idx);

    int productOptionDel(int idx);

    int combinedOptionsDel(int combined_idx);

    int deleteCombined(int combined_idx);

    List<CombinedDTO> combinedList();

    List<CombinedDTO> combinedListProduct(int product_idx);
    
    String searchOptionName(int product_option_idx);

	OptionDTO selectOptionByNameValue(Map<String, Object> param);

	List<OptionDTO> optionList();

	Integer getLastInsertId();

	int deleteProductOptionsByProduct(int product_idx);

	int deleteCombinedByProduct(int product_idx);

}
