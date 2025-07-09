package kr.co.junko.returnReceive;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReturnReceiveController {
	
	private final ReturnReceiveService service;

}
