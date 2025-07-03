package us.tx.state.dfps.service.oncall.service;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.admin.dto.AddOnCallInDto;
import us.tx.state.dfps.service.common.request.OnCallCountyReq;
import us.tx.state.dfps.service.common.request.OnCallSearchReq;
import us.tx.state.dfps.service.common.response.AddOnCallResponse;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.OnCallCountyRes;

public interface OnCallService {

	public AddOnCallResponse addProposedShiftOrBlock(AddOnCallInDto addOnCallInDto);

	public OnCallCountyRes rtrvOnCallCountyDtl(OnCallCountyReq onCallCountyReq);

	/**
	 * Artifact ID: artf151569
	 * Method Name: getRouterPersonOnCall
	 * Method Description: Retrieves the Person with router designation based on the program, county,
	 * start date and end date (including the start time and end time)
	 *
	 * @param onCallSearchReq
	 * @return
	 */
	Person getRouterPersonOnCall(OnCallSearchReq onCallSearchReq);
}
