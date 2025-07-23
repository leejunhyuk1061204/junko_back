package kr.co.junko.receiptPayment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.ReceiptPaymentDTO;

@Mapper
public interface ReceiptPaymentDAO {

    int receiptInsert(ReceiptPaymentDTO dto);

    int paymentInsert(ReceiptPaymentDTO dto);

    int receiptUpdate(ReceiptPaymentDTO dto);

    int receiptDel(int rp_idx);

    List<ReceiptPaymentDTO> receiptList(String string);

    List<ReceiptPaymentDTO> paymentList(String string);

    ReceiptPaymentDTO detailReceipt(int rp_idx);

    ReceiptPaymentDTO detailPayment(int rp_idx);

    void updateVoucherStatus(Map<String, Object> map);

    ReceiptPaymentDTO detailReceiptPayment(int rp_idx);

}