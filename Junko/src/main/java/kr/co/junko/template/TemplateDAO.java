package kr.co.junko.template;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.TemplateDTO;
import kr.co.junko.dto.TemplateHistoryDTO;
import kr.co.junko.dto.TemplateVarDTO;

@Mapper
public interface TemplateDAO {

	int templateInsert(TemplateDTO dto);

	void insertVariable(TemplateVarDTO varDTO);

	TemplateDTO selectTemplate(int template_idx);

	int templateUpdate(TemplateDTO dto);

	void templateVarDel(int template_idx);

	int templateDel(int template_idx);

	List<TemplateDTO> templateList(Map<String, Object> param);
	
	int templateTotalCnt(Map<String, Object> param);

	TemplateDTO templateDetail(int template_idx);

	List<TemplateVarDTO> templateVarList(int template_idx);

	void insertTemplateHistory(TemplateDTO before);

	List<TemplateHistoryDTO> templateHistory(int template_idx);

	List<TemplateDTO> templateListCategory(String category);

	List<String> templateCategoryList();

}
