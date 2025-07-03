package us.tx.state.dfps.service.sslccommchklst.service;

import us.tx.state.dfps.service.common.request.SslcCommChklstReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * service method for SSLC Commencement Checklist Mar 15, 2018- 4:56:20 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface SslcCommChklstService {

	/**
	 * Method Name: getChecklistData Method Description: Gets data for checklist
	 * and returns prefill data
	 * 
	 * @param sslcCommChklstReq
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getChecklistData(SslcCommChklstReq sslcCommChklstReq, boolean isApsReferral);

	/**
	 * Method Name: getChecklistData Method Description: Gets data for checklist
	 * and returns prefill data
	 * 
	 * @param sslcCommChklstReq
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getApsReferralData(SslcCommChklstReq sslcCommChklstReq);

}
