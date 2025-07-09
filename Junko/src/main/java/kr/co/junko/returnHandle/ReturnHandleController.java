package kr.co.junko.returnHandle;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReturnHandleController {

	private final ReturnHandleService service;
	
}
