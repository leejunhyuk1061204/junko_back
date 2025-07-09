package kr.co.junko.returnReceive;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReturnReceiveService {
	
	private final ReturnReceiveDAO dao;

}
