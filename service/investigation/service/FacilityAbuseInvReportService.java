package us.tx.state.dfps.service.investigation.service;

import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * service method (CSUB71S) Apr 30, 2018- 4:38:22 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface FacilityAbuseInvReportService {

	/**
	 * Method Name: getAbuseReport Method Description: Gets information about
	 * abuse report from database and returns prefill string
	 * 
	 * @param req
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getAbuseReport(CommonApplicationReq req);

	/**
	 * Method Name: getStageIdForDataFix Method Description: Return the idStage to Launch the 
	 * APS Abuse and Neglect in Editable Mode
	 *  
	 * @param idStage
	 * @return Long
	 */
	public Long getStageIdForDataFix(Long idStage);
}
