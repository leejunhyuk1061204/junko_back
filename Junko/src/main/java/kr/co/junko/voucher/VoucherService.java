package kr.co.junko.voucher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.EntryDetailDTO;
import kr.co.junko.dto.VoucherDTO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VoucherService {

    @Autowired VoucherDAO dao;

    Map<String, Object> result = null;

    public int voucherInsert(VoucherDTO dto) {
        // 전표 먼저 insert
        boolean inserted = dao.voucherInsert(dto);
        if (!inserted) return 0;

        int entry_idx = dto.getEntry_idx(); // selectKey로 셋팅된 값

        // 분개 리스트 등록
        for (EntryDetailDTO detail : dto.getEntry_details()) {
            detail.setEntry_idx(entry_idx);
            dao.insertEntryDetail(detail);
        }

        return entry_idx; 
    }

    public boolean voucherUpdate(VoucherDTO dto) {
        String status = dao.voucherStatus(dto.getEntry_idx());
        if (!"작성중".equals(status)) return false;
        
        int fixedIdx = dto.getEntry_idx();
        
        int row = dao.voucherUpdate(dto);
        
        if (row > 0) {
            dto.setEntry_idx(fixedIdx);
            
            // 기존 분개 삭제
            dao.delEntryDetailEntryIdx(dto.getEntry_idx());

            // 새 분개 자동 생성
            defaultEntryDetails(dto);
            
            for (EntryDetailDTO detail : dto.getEntry_details()) {
                dao.insertEntryDetail(detail);
            }
            
            return true;
        }
        
        return false;
    }

    public boolean voucherDel(int entry_idx) {
        String status = dao.voucherStatus(entry_idx);
        if (!"작성중".equals(status)) return false;
        int row = dao.voucherDel(entry_idx);
        return row > 0;
    }

    public List<VoucherDTO> voucherList(String entry_type, String status, String keyword, String custom_name, String custom_owner, String from, String to, String sort, String order, int offset, int size) {
        return dao.voucherList(entry_type, status, keyword, custom_name, custom_owner, from, to, sort, order, offset, size);
    }

    public int voucherTotal(String entry_type, String status, String keyword, String custom_name, String custom_owner, String from, String to) {
        return dao.voucherTotal(entry_type, status, keyword, custom_name, custom_owner, from, to);
    }

    public void defaultEntryDetails(VoucherDTO dto) {
    List<EntryDetailDTO> list = new ArrayList<>();
    int amount = dto.getAmount();
    int entry_idx = dto.getEntry_idx();

    // 전표 유형에 따라 분개를 생성
    // 1: 현금, 2: 매출, 3: 재고 자산, 4: 환불 비용, 5: 외상매출금, 6: 외상매입금
    switch (dto.getEntry_type()) {
        case "매출": // 물건을 팔아서 돈이 생긴 상황
            list.add(createDetail(1, "차변", amount, entry_idx)); // 현금 수금
            list.add(createDetail(2, "대변", amount, entry_idx)); // 매출 발생
            break;
        case "매입": // 물건을 사서 재고가 들어온 상황
            list.add(createDetail(3, "차변", amount, entry_idx)); // 재고 자산
            list.add(createDetail(1, "대변", amount, entry_idx)); // 현금 지급
            break;
        case "환불": // 환불 처리해서 돈이 나간 상황
            list.add(createDetail(4, "차변", amount, entry_idx)); // 환불 비용
            list.add(createDetail(1, "대변", amount, entry_idx)); // 현금 지급
            break;
        case "수금": // 고객으로부터 실제 입금된 경우
            list.add(createDetail(1, "차변", amount, entry_idx)); // 현금 증가
            list.add(createDetail(5, "대변", amount, entry_idx)); // 외상매출금 감소
            break;

        case "지급": // 거래처에게 실제 지급한 경우
            list.add(createDetail(6, "차변", amount, entry_idx)); // 외상매입금 감소
            list.add(createDetail(1, "대변", amount, entry_idx)); // 현금 감소
            break;

        default:
            throw new IllegalArgumentException("지원하지 않는 전표 유형: " + dto.getEntry_type());
    }

    dto.setEntry_details(list);

}

    private EntryDetailDTO createDetail(int as_idx, String type, int amount, int entry_idx) {
        EntryDetailDTO dto = new EntryDetailDTO();
        dto.setAs_idx(as_idx);
        dto.setType(type);
        dto.setAmount(amount);
        dto.setEntry_idx(entry_idx);
        dto.setDel_yn(0);
        return dto;
    }

    public VoucherDTO voucherDetail(int entry_idx) {
        VoucherDTO dto = dao.voucherDetail(entry_idx);
        if (dto != null) {
            dto.setEntry_details(dao.entryDetailList(entry_idx));
        }
        return dto;
    }

    public boolean voucherStatusUpdate(int entry_idx, String status) {
        String current = dao.voucherStatus(entry_idx);
        if (!"작성중".equals(current)) return false;

        if ("확정".equals(status)) {
            VoucherDTO dto = dao.voucherDetail(entry_idx);

            if ("수금".equals(dto.getEntry_type()) || "지급".equals(dto.getEntry_type())) {
                boolean exists = dao.checkReceiptPayment(entry_idx);
                if (!exists) {
                    Map<String, Object> insertMap = new HashMap<String, Object>();
                    insertMap.put("type", dto.getEntry_type());
                    insertMap.put("entry_idx", dto.getEntry_idx());
                    insertMap.put("custom_idx", dto.getCustom_idx());
                    insertMap.put("amount", dto.getAmount());
                    insertMap.put("method", "기타"); // 기본값, 프론트에서 수정 가능
                    insertMap.put("transaction_date", dto.getEntry_date());
                    insertMap.put("note", "[전표확정 자동생성]");
                    insertMap.put("status", "확정");
                    insertMap.put("user_idx", dto.getUser_idx());

                    dao.insertReceiptPayment(insertMap);
                }
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("entry_idx", entry_idx);
        map.put("status", status);

        return dao.voucherStatusUpdate(map) > 0;
    }

	public List<EntryDetailDTO> entryDetailList(int entry_idx) {
		return dao.entryDetailList(entry_idx);
	}

	public String selectAsNameByIdx(int as_idx) {
		return dao.selectAsNameByIdx(as_idx);
	}

	public String selectVoucherType(int entry_idx) {
	    VoucherDTO dto = dao.voucherDetail(entry_idx);
	    return dto != null ? dto.getEntry_type() : "";
	}

	public int selectVoucherAmount(int entry_idx) {
	    VoucherDTO dto = dao.voucherDetail(entry_idx);
	    return dto != null ? dto.getAmount() : 0;
	}

}