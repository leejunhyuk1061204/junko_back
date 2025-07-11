package kr.co.junko.msg;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.junko.dto.MsgDTO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MsgService {

	@Autowired MsgDAO dao;

	public boolean msgSend(MsgDTO dto) {
		int row = dao.msgSend(dto);
		return row>0;
	}

	public List<MsgDTO> msgReceived(int user_idx) {
		return dao.msgReceived(user_idx);
	}

	public List<MsgDTO> msgSent(int user_idx) {
		return dao.msgSent(user_idx);
	}

	public boolean msgDel(int msg_idx, int sender_idx, int receiver_idx, String role) {
		int row = 0;
		
		if ("sender".equals(role)) {
			row = dao.senderDel(msg_idx, sender_idx);
		} else if ("receiver".equals(role)) {
			row = dao.receiverDel(msg_idx, receiver_idx);
		}
		
		return row>0;
	}

	public boolean msgRead(int msg_idx) {
		int row = dao.msgRead(msg_idx);
		return row > 0;
	}

	public List<Map<String, Object>> userAuto(String keyword) {
		return dao.userAuto(keyword);
	}

	public boolean msgImportant(int msg_idx, int user_idx, boolean important_yn) {
		int row = important_yn ? dao.yImportant(msg_idx, user_idx) : dao.nImportant(msg_idx, user_idx);
		return row > 0;
	}

	public List<MsgDTO> msgImportantView(int user_idx) {
		return dao.msgImportantView(user_idx);
	}

	public void oldMsgDel() {
		dao.oldMsgDel();
	}

	public int msgUnreadCnt(int user_idx) {
		return dao.msgUnreadCnt(user_idx);
	}
	
}
