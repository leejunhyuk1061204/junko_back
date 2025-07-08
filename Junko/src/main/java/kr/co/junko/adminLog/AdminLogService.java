package kr.co.junko.adminLog;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminLogService {
	
	private final AdminLogDAO dao;

}
