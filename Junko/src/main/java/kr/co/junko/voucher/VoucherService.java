package kr.co.junko.voucher;

import java.util.ArrayList;
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

    public boolean voucherInsert(VoucherDTO dto) {
        // 전표 먼저 insert
        boolean inserted = dao.voucherInsert(dto);
        if (!inserted) return false;

        int entry_idx = dto.getEntry_idx(); // selectKey로 셋팅된 값

        // 분개 리스트 등록
        for (EntryDetailDTO detail : dto.getEntry_details()) {
            detail.setEntry_idx(entry_idx);
            dao.insertEntryDetail(detail);
        }

        return true;
    }

    public boolean voucherUpdate(VoucherDTO dto) {
        String status = dao.voucherStatus(dto.getEntry_idx());
        if (!"작성중".equals(status)) return false;
        int row = dao.voucherUpdate(dto);
        return row > 0;
    }

    public boolean voucherDel(int entry_idx) {
        String status = dao.voucherStatus(entry_idx);
        if (!"작성중".equals(status)) return false;
        int row = dao.voucherDel(entry_idx);
        return row > 0;
    }

    public List<VoucherDTO> voucherList(String entry_type, String status, String keyword, String sort, String order, int offset, int size) {
        return dao.voucherList(entry_type, status, keyword, sort, order, offset, size);
    }

    public int voucherTotal(String entry_type, String status, String keyword) {
        return dao.voucherTotal(entry_type, status, keyword);
    }

    public void defaultEntryDetails(VoucherDTO dto) {
    List<EntryDetailDTO> list = new ArrayList<>();
    int amount = dto.getAmount();
    int entry_idx = dto.getEntry_idx();

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

}