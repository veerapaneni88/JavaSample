package us.tx.state.dfps.service.subcontractor.service;

import us.tx.state.dfps.service.common.request.RsrcNameReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;

public interface RsrcNameRtrvService {

	/**
	 * 
	 * Method Name: getRsrcName
	 *
	 * Method Description: Interface for calling the service that returns a
	 * Resource Name if the Resource ID that it is passed exists in Resource
	 * Directory.
	 *
	 * @param pInputMsg
	 * @return CommonStringRes @
	 *
	 */
	public CommonStringRes getRsrcName(RsrcNameReq pInputMsg);
}
