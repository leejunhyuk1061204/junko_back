package kr.co.junko.entryStatus;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.EntryStatusDTO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntryStatusService {

    @Autowired EntryStatusDAO dao;

    Map<String, Object> result = null;

    public boolean settlementInsert(EntryStatusDTO dto) {
        // settlement_day가 null이면 오늘 날짜로 기본 설정
        if (dto.getSettlement_day() == null) {
            dto.setSettlement_day(new Date(System.currentTimeMillis()));
        }

        int row = dao.settlementInsert(dto);
        if (row > 0) updateSettlementStatus(dto.getEntry_idx());
        return row > 0;
    }

    public List<EntryStatusDTO> settlementList(String status, String keyword, int offset, int size) {
        return dao.settlementList(status, keyword, offset, size);
    }

    public int settlementTotal(String status, String keyword) {
        return dao.settlementTotal(status, keyword);
    }

    public boolean settlementUpdate(EntryStatusDTO dto) {
        int row = dao.settlementUpdate(dto);
        if (row > 0) updateSettlementStatus(dto.getEntry_idx());
        return row > 0;
    }

    public boolean settlementDel(int settlement_id) {
        EntryStatusDTO dto = dao.settlementDetail(settlement_id);
        int row = dao.settlementDel(settlement_id);
        if (row > 0) updateSettlementStatus(dto.getEntry_idx());
        return row > 0;
    }

    public EntryStatusDTO settlementDetail(int settlement_id) {
        return dao.settlementDetail(settlement_id);
    }

    public boolean settlementMulti(EntryStatusDTO dto) {
        List<Integer> list = dto.getEntry_idx_list();
        if (list == null || list.isEmpty()) return false;

        if (dto.getSettlement_day() == null) {
            dto.setSettlement_day(Date.valueOf(LocalDate.now()));
        }

        int count = 0;
        for (int entry_idx : list) {
            dto.setEntry_idx(entry_idx);
            count += dao.settlementMulti(dto);
            updateSettlementStatus(entry_idx);
        }

        return count == list.size();
    }

    private void updateSettlementStatus(int entry_idx) {
        int total = dao.sumAmount(entry_idx);
        int count = dao.settlementCnt(entry_idx);
        int voucherAmount = dao.voucherAmount(entry_idx);

        String status = "미정산";
        if (count > 0) {
            if (total < voucherAmount) status = "부분정산";
            else if (total == voucherAmount) status = "정산";
        }

        dao.voucherSettlementUpdate(entry_idx, status);
    }

}
