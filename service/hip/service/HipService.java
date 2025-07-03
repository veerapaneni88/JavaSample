package us.tx.state.dfps.service.hip.service;

import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.request.HipGroupDtlReq;
import us.tx.state.dfps.service.common.request.UpdtRecordsReq;
import us.tx.state.dfps.service.common.response.FindrsRecordsRes;
import us.tx.state.dfps.service.common.response.HipGroupDtlRes;
import us.tx.state.dfps.service.common.response.HipGroupsRes;
import us.tx.state.dfps.service.common.response.UpdtRecordsRes;

public interface HipService {

	// This service is to get all records for state wide intake
	public HipGroupsRes getHipGroups(ServiceReqHeaderDto serviceReqHeaderDto);

	// This service is to get the group details for state wide intake
	public HipGroupDtlRes getHipGroupDetail(HipGroupDtlReq hipGroupDtlReq);

	// This service is to get all HIP records for FINDRS to match and non match
	// on the screen, this service is for IMPACT
	public FindrsRecordsRes getHipFindrsRecords();

	// This service is to update match and non match in HIP tables , when FINDRS
	// team does their update on the screen.
	public UpdtRecordsRes updtHipFindrsRecords(UpdtRecordsReq updtRecordsReq);
}
