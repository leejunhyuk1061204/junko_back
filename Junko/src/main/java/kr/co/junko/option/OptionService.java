package kr.co.junko.option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.junko.dto.CombinedDTO;
import kr.co.junko.dto.OptionDTO;
import kr.co.junko.dto.ProductOptionDTO;
import kr.co.junko.dto.UsingOptionDTO;

@Service
public class OptionService {

    @Autowired OptionDAO dao;

    Map<String, Object> result = null;

    //옵션 등록
    public boolean optionInsert(OptionDTO dto) {
        int row = dao.optionInsert(dto);
        return row > 0;
    }

    //옵션 수정
    public boolean optionUpdate(OptionDTO dto) {
        int row = dao.optionUpdate(dto);
        return row > 0;
    }

    // 옵션 삭제
    public boolean optionDel(int option_idx) {
        int row = dao.optionDel(option_idx);
        return row > 0;
    }

    // 옵션 사용 등록
    public Integer optionUseReturnIdx(UsingOptionDTO dto) {
        OptionDTO option = dao.selectOptionByIdx(dto.getOption_idx());
        // 옵션이 실제 존재하고 del_yn = 0인지 검증
        if (option == null || option.getDel_yn() != 0) return null;

        dao.optionUse(dto);
        return dao.getLastInsertId();
    }

    // 옵션 사용 삭제
    public boolean optionUseDel(int using_idx) {
        int row = dao.optionUseDel(using_idx);
        return row > 0;
    }

    // 사용 옵션 리스트
    public List<UsingOptionDTO> usingOptionList(int product_idx) {
        return dao.usingOptionList(product_idx);
    }

    // 조합된 옵션 등록
    public boolean combinedInsert(CombinedDTO dto) {
        int row = dao.combinedInsert(dto);
        return row > 0;
    }

    // 자동 조합 생성
    // 상품에 연결된 옵션들을 가져와서 가능한 모든 조합을 생성하고,
    // 각 조합을 CombinedDTO로 저장한 후, ProductOptionDTO에 연결된 상품과 조합을 저장
    // 이때, 조합된 이름은 옵션 값들을 '/'로 구분하여 생성
    public boolean autoCombined(int product_idx) {
        List<OptionDTO> optionList = dao.getOptionsByProduct(product_idx); // 상품에 연결된 옵션 가져오기
        if (optionList == null || optionList.isEmpty()) return false; // 옵션이 없으면 false 반환

        List<List<String>> valueGroups = new ArrayList<>(); // 옵션 값들을 그룹화하여 저장

        for (OptionDTO option : optionList) {
            String valueStr = option.getOption_value(); // 옵션 값 문자열을 가져옴
            String[] splitValues = valueStr.split(","); // 쉼표로 구분된 값을 분리

            List<String> trimmedList = new ArrayList<>(); // 각 값을 트림하여 저장
            for (String val : splitValues) {
                trimmedList.add(val.trim()); // 공백 제거
            }

            valueGroups.add(trimmedList); // 각 옵션의 값들을 리스트로 저장
        }

        List<String> existingNames = dao.selectCombinedProduct(product_idx); // 이미 존재하는 조합 이름들을 가져옴

        List<List<String>> combinations = new ArrayList<>(); // 조합 결과를 저장할 리스트
        generateCombinations(valueGroups, 0, new ArrayList<>(), combinations); // 가능한 모든 조합 생성

        for (List<String> comb : combinations) {
            StringBuilder nameBuilder = new StringBuilder(); // 조합된 이름을 생성하기 위한 StringBuilder
            for (int i = 0; i < comb.size(); i++) {
                nameBuilder.append(comb.get(i)); // 각 옵션 값을 추가
                if (i != comb.size() - 1) nameBuilder.append(" / "); // 마지막 값이 아니면 구분자 추가
            }

            String combinedName = nameBuilder.toString(); // 조합된 이름 생성

            if (existingNames.contains(combinedName)) continue; // 이미 존재하는 조합 이름이면 건너뜀

            CombinedDTO combinedDTO = new CombinedDTO();
            combinedDTO.setCombined_name(combinedName); // 조합된 이름 설정
            dao.insertCombined(combinedDTO); // 여기에 combined_idx가 세팅됨을 전제

            ProductOptionDTO podto = new ProductOptionDTO();
            podto.setProduct_idx(product_idx); // 상품 인덱스 설정
            podto.setCombined_idx(combinedDTO.getCombined_idx()); // 조합 인덱스 설정
            podto.setMin_cnt(1); // 기본값

            dao.insertProductOption(podto);
        }

        return true;
    }

    // 재귀적으로 가능한 모든 조합을 생성하는 메소드
    // depth는 현재 조합의 깊이를 나타내며, current는 현재까지 선택된 옵션 값을 저장하는 리스트
    // result는 최종적으로 생성된 조합들을 저장하는 리스트
    private void generateCombinations(List<List<String>> lists, int depth, List<String> current, List<List<String>> result) {
        // depth가 lists의 크기와 같아지면 현재까지 선택된 조합
        // 모든 옵션 값이 선택된 상태이므로 조합을 저장
        // 현재까지 선택된 값을 result에 추가
        if (depth == lists.size()) { 
            List<String> combination = new ArrayList<>(); // 현재 조합을 저장
            for (String val : current) {
                combination.add(val); // 현재까지 선택된 값을 조합에 추가
            }
            result.add(combination); // 조합 결과에 추가
            return;
        }

        List<String> currentList = lists.get(depth); // 현재 깊이의 옵션 값 리스트 (depth가 몇 번째 옵션인지 확인)
        for (int i = 0; i < currentList.size(); i++) {
            current.add(currentList.get(i)); // 현재 옵션 값을 선택
            generateCombinations(lists, depth + 1, current, result); // 다음 깊이로 재귀 호출
            current.remove(current.size() - 1); // backtracking
        }
    }

    public boolean productOptionDel(int idx) {
        int row = dao.productOptionDel(idx);
        return row > 0;
    }

    public boolean combinedOptionsDel(int combined_idx) {
        // 먼저 연결된 product_option 모두 소프트 삭제
        dao.combinedOptionsDel(combined_idx);

        // 그 후, 해당 combined row 소프트 삭제
        return dao.deleteCombined(combined_idx) > 0;
    }

    public List<CombinedDTO> combinedList() {
        return dao.combinedList();
    }

    public List<CombinedDTO> combinedListProduct(int product_idx) {
        return dao.combinedListProduct(product_idx);
    }

    public OptionDTO getOptionByNameValue(String name, String value) {
        Map<String, Object> param = new HashMap<>();
        param.put("option_name", name);
        param.put("option_value", value);
        return dao.selectOptionByNameValue(param);
    }

    public Integer ensureOptionExists(String name, String value) {
        OptionDTO exist = getOptionByNameValue(name, value);
        if (exist != null) return exist.getOption_idx();

        // 없으면 새로 등록
        OptionDTO newOption = new OptionDTO();
        newOption.setOption_name(name);
        newOption.setOption_value(value);
        dao.optionInsert(newOption); // 이 때 keyProperty="option_idx"로 newOption에 값 세팅
        return newOption.getOption_idx();
    }

	public List<OptionDTO> optionList() {
		return dao.optionList();
	}

	@Transactional
	public boolean deleteAllCombinedByProduct(int product_idx) {
	    int deletedProductOptions = dao.deleteProductOptionsByProduct(product_idx);
	    int deletedCombined = dao.deleteCombinedByProduct(product_idx);
	    return deletedProductOptions >= 0 && deletedCombined >= 0;
	}
	
}
