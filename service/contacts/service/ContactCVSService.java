package us.tx.state.dfps.service.contacts.service;

import us.tx.state.dfps.service.common.request.CpsInvConclValReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description :MONTHLY
 * EVALUATION FORM Tuxedo Service :CSUB84S Mar 28, 2018- 9:48:14 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ContactCVSService {
	/**
	 * Method Name: getContactCVS Method Description:Populate MONTHLY EVALUATION
	 * FORM
	 * 
	 * @param cpsInvConclValReq
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getContactCVS(CpsInvConclValReq cpsInvConclValReq);
}
