package kr.co.junko.msg;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.junko.dto.MsgDTO;

@Mapper
public interface MsgDAO {

	int msgSend(MsgDTO dto);

	List<MsgDTO> msgReceived(int user_idx);

	List<MsgDTO> msgSent(int user_idx);

	int senderDel(int msg_idx, int sender_idx);

	int receiverDel(int msg_idx, int receiver_idx);

	int msgRead(int msg_idx);

}
