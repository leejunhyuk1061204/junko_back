package kr.co.junko.voucher;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.EntryDetailDTO;
import kr.co.junko.dto.VoucherDTO;

@Mapper
public interface VoucherDAO {

    public boolean voucherInsert(VoucherDTO dto);

    public int voucherUpdate(VoucherDTO dto);

    public int voucherDel(int entry_idx);

    public List<VoucherDTO> voucherList(String entry_type, String status, String keyword, String custom_name, String custom_owner, String from, String to, String sort, String order, int offset, int size);

    public String voucherStatus(int entry_idx);

    public int voucherTotal(String entry_type, String status, String keyword, String custom_name, String custom_owner, String from, String to);

    public void insertEntryDetail(EntryDetailDTO detail);

    public VoucherDTO voucherDetail(int entry_idx);

    public List<EntryDetailDTO> entryDetailList(int entry_idx);

    public int voucherStatusUpdate(Map<String, Object> map);

    public boolean checkReceiptPayment(int entry_idx);

    public void insertReceiptPayment(Map<String, Object> insertMap);

	public void delEntryDetailEntryIdx(int entry_idx);

	public String selectAsNameByIdx(int as_idx);

    public List<VoucherDTO> getSettledVouchers();
    
}
