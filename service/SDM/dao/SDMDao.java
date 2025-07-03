package us.tx.state.dfps.service.SDM.dao;

import java.util.List;

import us.tx.state.dfps.service.SDM.SDMHouseHoldDto;
import us.tx.state.dfps.service.common.request.CommonCaseIdReq;

public interface SDMDao {
	public List<SDMHouseHoldDto> getSDMComplHouseHold(CommonCaseIdReq caseIdReq,Boolean isStageClosed);

	public List<SDMHouseHoldDto> getSDMHouseHold(CommonCaseIdReq commonCaseIdReq);
}
