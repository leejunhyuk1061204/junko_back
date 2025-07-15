package kr.co.junko.dto;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AccountingEntrySearchDTO {
	private String keyword;     // 검색 키워드 (거래처명, 유형 등)
	private LocalDate startDate;
	private LocalDate endDate;
	private String custom_name;
	private String customer_name;
	private int page;
	private int limit;
	private String customer;
	private String entry_type;
	private String status;
	
}
