package us.tx.state.dfps.service.SDM.service;

import us.tx.state.dfps.service.common.request.CommonCaseIdReq;
import us.tx.state.dfps.service.common.response.SDMHoseHoldRes;

public interface SDMService {
	public SDMHoseHoldRes getSDMComplHouseHold(CommonCaseIdReq caseIdReq);

	public SDMHoseHoldRes getSDMHouseHold(CommonCaseIdReq commonCaseIdReq);
}
