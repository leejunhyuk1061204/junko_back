package kr.co.junko.entryStatus;

import java.sql.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.EntryStatusDTO;

@Mapper
public interface EntryStatusDAO {

    int settlementInsert(EntryStatusDTO dto);

    List<EntryStatusDTO> settlementList(String status, String keyword, int offset, int size, String from, String to);

    int settlementTotal(String status, String keyword, String from, String to);

    int settlementUpdate(EntryStatusDTO dto);

    int settlementDel(int settlement_id);

    EntryStatusDTO settlementDetail(int settlement_id);

    int settlementMulti(EntryStatusDTO dto);

    int sumAmount(int entry_idx);

    int settlementCnt(int entry_idx);

    int voucherAmount(int entry_idx);

    void voucherSettlementUpdate(int entry_idx, String status);

    int selectTotalSettlementAmount(int entry_idx);

    int checkDuplicateSettlement(int entry_idx, Date settlement_day);

    List<EntryStatusDTO> settlementListEntryIdx(int entry_idx);

}
