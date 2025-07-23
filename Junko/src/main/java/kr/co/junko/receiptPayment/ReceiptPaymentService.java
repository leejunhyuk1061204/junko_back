package kr.co.junko.receiptPayment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.ReceiptPaymentDTO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReceiptPaymentService {

    @Autowired ReceiptPaymentDAO dao;

    Map<String, Object> result = null;

    public boolean receiptInsert(ReceiptPaymentDTO dto) {
        int row = dao.receiptInsert(dto);

        // 전표 연동 상태 확정 처리
        if (row > 0 && dto.getEntry_idx() != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("entry_idx", dto.getEntry_idx());
            map.put("status", "확정");
            dao.updateVoucherStatus(map);
        }

        return row > 0;
        }

    public boolean paymentInsert(ReceiptPaymentDTO dto) {
        int row = dao.paymentInsert(dto);

        // 전표 연동 상태 확정 처리
        if (row > 0 && dto.getEntry_idx() != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("entry_idx", dto.getEntry_idx());
            map.put("status", "확정");
            dao.updateVoucherStatus(map);
        }

        return row > 0;
    }

    public boolean receiptUpdate(ReceiptPaymentDTO dto) {
        int row = dao.receiptUpdate(dto);
        return row > 0;
    }

    public boolean receiptDel(int rp_idx) {
        int row = dao.receiptDel(rp_idx);
        return row > 0;
    }

    public List<ReceiptPaymentDTO> receiptList(String string) {
        return dao.receiptList(string);
    }

    public List<ReceiptPaymentDTO> paymentList(String string) {
        return dao.paymentList(string);
    }

    public ReceiptPaymentDTO detailReceiptPayment(int rp_idx) {
        return dao.detailReceiptPayment(rp_idx);
    }

}
