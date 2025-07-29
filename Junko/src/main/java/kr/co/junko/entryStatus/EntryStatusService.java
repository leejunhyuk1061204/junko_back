package kr.co.junko.entryStatus;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.EntryStatusDTO;
import kr.co.junko.invoiceTax.InvoiceTaxService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntryStatusService {

    @Autowired EntryStatusDAO dao;
    @Autowired InvoiceTaxService invoiceTaxService;

    Map<String, Object> result = null;

    public boolean settlementInsert(EntryStatusDTO dto) {
        // settlement_day가 null이면 오늘 날짜로 기본 설정
        if (dto.getSettlement_day() == null) {
            dto.setSettlement_day(new Date(System.currentTimeMillis()));
        }

        int voucherAmount = dao.voucherAmount(dto.getEntry_idx());
        int total = dao.selectTotalSettlementAmount(dto.getEntry_idx());
        int amount = dto.getAmount();

        // 초과 정산 방지
        if (amount + total > voucherAmount) {
            log.warn("정산 초과: 입력={}, 누적={}, 전표금액={}", amount, total, voucherAmount);
            return false;
        }

        // 중복 정산 방지
        if (dao.checkDuplicateSettlement(dto.getEntry_idx(), dto.getSettlement_day()) > 0) {
            log.warn("중복 정산 시도: entry_idx={}, settlement_day={}", dto.getEntry_idx(), dto.getSettlement_day());
            return false;
        }

        String status = "미정산";
        if (amount > 0 && amount < voucherAmount) status = "부분정산";
        else if (amount >= voucherAmount) status = "정산";
        dto.setStatus(status);

        int row = dao.settlementInsert(dto);
        if (row > 0) updateSettlementStatus(dto.getEntry_idx());

        return row > 0;
    }

    public List<EntryStatusDTO> settlementList(String status, String keyword, int offset, int size, String from, String to) {
        return dao.settlementList(status, keyword, offset, size, from, to);
    }

    public int settlementTotal(String status, String keyword, String from, String to) {
        return dao.settlementTotal(status, keyword, from, to);
    }

    public boolean settlementUpdate(EntryStatusDTO dto) {
        int voucherAmount = dao.voucherAmount(dto.getEntry_idx());
        int amount = dto.getAmount();

        // 현재 정산 ID 제외한 누적 정산 금액
        int total = dao.selectTotalSettlementAmount(dto.getEntry_idx()) 
                    - dao.settlementDetail(dto.getSettlement_id()).getAmount();

        // 초과 방지
        if (amount + total > voucherAmount) {
            log.warn("정산 수정 시 초과 발생: 수정금액={}, 누적정산={}, 전표금액={}", amount, total, voucherAmount);
            return false;
        }

        String status = "미정산";
        if (amount > 0 && amount < voucherAmount) status = "부분정산";
        else if (amount >= voucherAmount) status = "정산";
        dto.setStatus(status);

        int row = dao.settlementUpdate(dto);
        if (row > 0) updateSettlementStatus(dto.getEntry_idx());

        return row > 0;
    }

    public boolean settlementDel(int settlement_id) {
        EntryStatusDTO dto = dao.settlementDetail(settlement_id);
        if (dto == null) {
            log.warn("삭제 요청: settlement_id {} 는 이미 삭제되었거나 존재하지 않음", settlement_id);
            return false;
        }
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

            int voucherAmount = dao.voucherAmount(entry_idx);
            int amount = dto.getAmount();
            String status = "미정산";
            if (amount > 0 && amount < voucherAmount) status = "부분정산";
            else if (amount >= voucherAmount) status = "정산";
            dto.setStatus(status);

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

        if ("정산".equals(status)) {
            if (!invoiceTaxService.existsInvoiceEntryIdx(entry_idx)) {
                invoiceTaxService.insertAutoInvoice(entry_idx);
            }
        }

        dao.voucherSettlementUpdate(entry_idx, status);
    }

    public int selectTotalSettlementAmount(int entry_idx) {
        return dao.selectTotalSettlementAmount(entry_idx);
    }

    public int voucherAmount(int entry_idx) {
        return dao.voucherAmount(entry_idx);
    }

    public List<EntryStatusDTO> settlementListEntryIdx(int entry_idx) {
        return dao.settlementListEntryIdx(entry_idx);
    }

}
