package us.tx.state.dfps.service.investigation.service;

import us.tx.state.dfps.service.common.request.LicensingInvCnclusnReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * service method Apr 13, 2018- 12:15:11 PM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface CCIReporterLetterFormService {

	/**
	 * Method Name: getLicensingInvReportDtls Method Description: Gathers data
	 * to generate prefill string
	 * 
	 * @param req
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto retrieveOperationIdentity(LicensingInvCnclusnReq req);

}
