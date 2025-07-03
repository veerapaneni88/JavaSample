package us.tx.state.dfps.service.fahomestudy.dao;

import us.tx.state.dfps.service.fahomestudy.dto.InquiryDateDto;
import us.tx.state.dfps.service.homedetails.dto.HouseHoldMembersDto;

import java.util.List;

public interface FAHomeStudyDao {

    List<HouseHoldMembersDto> getHouseHoldMembersInfo(Long idStage);

    InquiryDateDto getInquiryDate(Long idStage);
}
