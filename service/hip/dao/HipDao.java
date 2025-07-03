package us.tx.state.dfps.service.hip.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.request.HipGroupDtlReq;
import us.tx.state.dfps.service.common.request.UpdtRecordsReq;
import us.tx.state.dfps.service.common.response.UpdtRecordsRes;
import us.tx.state.dfps.service.hip.dto.HipFileDtlDto;
import us.tx.state.dfps.service.hip.dto.HipGroupDto;
import us.tx.state.dfps.service.hip.dto.HipPersonDto;

public interface HipDao {
	// This service is to get all records for state wide intake
	public List<HipGroupDto> getHipGroups(ServiceReqHeaderDto serviceReqHeaderDto);

	// This service is to get the group details for state wide intake
	public List<HipPersonDto> getHipGroupDetail(HipGroupDtlReq hipGroupDtlReq);

	// This method is to update the HIP group sent to SWI, as we should not send
	// the same record again
	public void updtHipGroup(HipGroupDtlReq hipGroupDtlReq);

	// This service is to get all HIP records for FINDRS to match and non match
	// on the screen, this service is for IMPACT
	public List<HipFileDtlDto> getHipFindrsRecords();

	// This method is to update match and non match in HIP tables , when FINDRS
	// team does their update on the screen.
	public void updtHipFindrsRecords(UpdtRecordsReq updtRecordsReq);

	// This method is check all the records in the file are successfully
	// processed , if processed need to pruge the non match records
	public UpdtRecordsRes chkFileComp(UpdtRecordsReq updtRecordsReq);
}
