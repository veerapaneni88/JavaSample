package us.tx.state.dfps.service.person.service;

import us.tx.state.dfps.service.common.request.AbcsRecordCheckReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.recordscheck.dto.AbcsRecordsCheckDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class AbcsRecordsCheckService
 * will have all service operation related Record Check Screen forms Mar 14,
 * 2018- 2:17:34 PM © 2017 Texas Department of Family and Protective Services
 */
public interface AbcsRecordsCheckService {

	/**
	 * Method Description: This method is used to retrieve the common
	 * application form. This form fully documents the historical social,
	 * emotional, educational, medical, and family account of the child by
	 * passing IdStage and IdPerson as input request
	 * 
	 * @param commonApplicationReq
	 * @return PreFillDataServiceDto @
	 */
	public CommonFormRes getRecordsCheckNtfcnForm(AbcsRecordCheckReq abcsRecordCheckReq);
	public AbcsRecordsCheckDto getAbcsRecordsCheckDetails(Long idRecCheck);
}
