package kr.co.junko.dto;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AccountingEntrySearchDTO {
	private String status;          // 전표 상태
	private String user_id;         // 작성자
	private String keyword;         // 키워드 (제목/내용)
	private LocalDate start_date;   // 시작일
	private LocalDate end_date;     // 종료일
	private String orderBy;         // 정렬 기준 (e.g. reg_date)
	private String orderDir;        // 정렬 방향 (ASC / DESC)
	private int page;               // 페이지 번호
	private int limit;              // 페이지당 개수
}
