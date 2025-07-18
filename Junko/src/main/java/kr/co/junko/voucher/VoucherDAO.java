package kr.co.junko.voucher;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.EntryDetailDTO;
import kr.co.junko.dto.VoucherDTO;

@Mapper
public interface VoucherDAO {

    public boolean voucherInsert(VoucherDTO dto);

    public int voucherUpdate(VoucherDTO dto);

    public int voucherDel(int entry_idx);

    public List<VoucherDTO> voucherList(String entry_type, String status, String keyword, String sort, String order, int offset, int size);

    public String voucherStatus(int entry_idx);

    public int voucherTotal(String entry_type, String status, String keyword);

    public void insertEntryDetail(EntryDetailDTO detail);

}
