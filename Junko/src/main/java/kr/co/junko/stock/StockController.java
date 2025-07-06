package kr.co.junko.stock;

import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StockController {

	private final StockService service;
	Map<String, Object>result = null;
	
}
