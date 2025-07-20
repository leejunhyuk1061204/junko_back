package kr.co.junko.chart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChartService {
	
	private final ChartDAO dao;
	
	public Map<String, Object> chart(Map<String, Object> param) {
		Map<String, Object> list = new HashMap<String, Object>();
		
		list.put("getDaySales", safeList(dao.getDaySales(param)));
		list.put("getRecentOrderStats", safeList(dao.getRecentOrderStats(param)));
		list.put("getPopularProduct", safeList(dao.getPopularProduct(param)));
		list.put("getHighReturnProduct", safeList(dao.getHighReturnProduct(param)));
		list.put("getInventoryTurnoverStats", safeList(dao.getInventoryTurnoverStats(param)));
		list.put("returnProduct", safeList(dao.returnProduct(param)));
		list.put("returnProductThisMonth", safeList(dao.returnProductThisMonth(param)));
		list.put("getDelayedProduct", safeList(dao.getDelayedProduct(param)));
		list.put("getOrderStatus", safeList(dao.getOrderStatus(param)));
		list.put("getProductMarginStats", safeList(dao.getProductMarginStats(param)));
		list.put("getNetProfitStats", safeList(dao.getNetProfitStats(param)));
		list.put("getInOutProduct", safeList(dao.getInOutProduct(param)));
		list.put("getMonthlySalesYoY", safeList(dao.getMonthlySalesYoY(param)));
		list.put("getLowStockProduct", safeList(dao.getLowStockProduct(param)));
		list.put("getSalesThisMonth", safeList(dao.getSalesThisMonth(param)));
		list.put("newOrder", safeList(dao.newOrder(param)));
		list.put("getPendingShipment", safeList(dao.getPendingShipment(param)));
		list.put("getShippedToday", safeList(dao.getShippedToday(param)));
		list.put("getReceiveThisMonth", safeList(dao.getReceiveThisMonth(param)));
		list.put("getSalesByCategory", safeList(dao.getSalesByCategory(param)));
		
		return list;
	}
	
	// NPE 에러 방지 함수
	private List<Map<String, Object>> safeList(List<Map<String, Object>> list) {
	    return (list != null) ? list : new ArrayList<>();
	}

	public void chartExcel(Map<String, Object> param, HttpServletResponse res) {
	    try (Workbook workbook = new XSSFWorkbook()) {
	        // 기존 차트 메서드와 동일한 통계 데이터 조회
	        Map<String, Object> chartData = this.chart(param);

	        // 통계 항목별로 엑셀 시트 생성
	        for (Map.Entry<String, Object> entry : chartData.entrySet()) {
	            String sheetName = entry.getKey(); // 시트 이름 설정
	            List<Map<String, Object>> dataList = (List<Map<String, Object>>) entry.getValue();
	            
	            // 시트 생성
	            Sheet sheet = workbook.createSheet(sheetName);
	            
	            if (!dataList.isEmpty()) {
	                // 1. 첫 번째 행(0번 행)에 헤더 생성
	                Row headerRow = sheet.createRow(0);
	                // 첫번째 맵에서 key 들만 추출해서 엑셀 컬럼명으로 사용
	                Map<String, Object> firstRow = dataList.get(0);
	                int colIndex = 0;
	                for (String key : firstRow.keySet()) {
	                	// 각 컬럼명으로 셀 생성 후 값 세팅
	                    headerRow.createCell(colIndex++).setCellValue(key);
	                }

	                // 2. 실제 데이터 행을 1번 행부터 채우기
	                for (int i = 0; i < dataList.size(); i++) {
	                    Row row = sheet.createRow(i + 1);
	                    Map<String, Object> rowData = dataList.get(i);
	                    int j = 0;
	                    for (Object value : rowData.values()) {
	                    	// 각 데이터 값을 문자열로 변환해 셀에 넣음
	                        row.createCell(j++).setCellValue(value != null ? value.toString() : "");
	                    }
	                }
	            }
	        }
	        // HTTP 응답 헤더 설정
	        res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // 엑셀 MIME 타입 설정 (xlsx 형식)
	        res.setHeader("Content-Disposition", "attachment; filename=\"chart_data.xlsx\""); // 브라우저가 파일 다운로드로 인식하도록 파일명 지정
	        workbook.write(res.getOutputStream());
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	public void chartPdf(Map<String, Object> param, HttpServletResponse res) {
		try {
			Map<String, Object> chartData = this.chart(param);
			
			// PDF 문서 생성
			Document doc = new Document(PageSize.A4.rotate()); // 가로 방향
			res.setContentType("application/pdf");
			res.setHeader("Content-Disposition", "attachment; filename=\"chart_data.pdf\"");
			PdfWriter.getInstance(doc, res.getOutputStream());
			doc.open();
			
			Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD, Color.BLACK);
			Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
			Font cellFont = new Font(Font.HELVETICA, 11);
			
			// 항목별로 테이블 생성
			for (Map.Entry<String, Object> entry : chartData.entrySet()) {
				String tableTitle = entry.getKey();
				List<Map<String, Object>> dataList = (List<Map<String, Object>>) entry.getValue();
				
				if (!dataList.isEmpty()) {
					doc.add(new Paragraph(tableTitle, titleFont));
					doc.add(new Paragraph(" ")); // 줄바꿈
					
					Map<String, Object> firstRow = dataList.get(0);
					PdfPTable table = new PdfPTable(firstRow.size());
					table.setWidthPercentage(100);
					
					// 헤더 생성
					for (String key : firstRow.keySet()) {
						PdfPCell headerCell = new PdfPCell(new Phrase(key, headerFont));
						headerCell.setBackgroundColor(new Color(100, 149, 237));
						headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(headerCell);
					}
					
					// 데이터 행 추가
					for (Map<String, Object> rowData : dataList) {
						for (Object value : rowData.values()) {
							table.addCell(new Phrase(value != null ? value.toString() : "", cellFont));
						}
					}
					doc.add(table);
					doc.add(new Paragraph(" ")); // 테이블 간 간격
				}
			}
			doc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
