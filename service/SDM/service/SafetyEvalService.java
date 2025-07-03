package us.tx.state.dfps.service.SDM.service;

import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.RiskAssessmentReq;
import us.tx.state.dfps.service.common.request.SafetyAssessmentReq;
import us.tx.state.dfps.service.common.response.RiskAssessmentRes;
import us.tx.state.dfps.service.common.response.SafetyAssessmentRes;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

public interface SafetyEvalService {

	/**
	 * Method Name:getSafetyEvalDetails Method Description: This method is used
	 * to retrieve the information for Child Safety Evaluation letter by passing
	 * IdStage and IdEvent as input request
	 * 
	 * @param commonHelperReq
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getSafetyEvalDetails(CommonHelperReq commonHelperReq);

	/**
	 * Method Name: queryRiskAssmt Method Description: * Retrieve the Risk
	 * Assessment details and the data needed to build the Risk Assessment page.
	 *
	 * @param CommonHelperReq
	 * @return RiskAssessmentRes
	 */
	public RiskAssessmentRes queryRiskAssmt(RiskAssessmentReq riskAssessmentReq);

	/**
	 * Method Name: queryPageData Method Description:Query the data needed to
	 * create the Risk Assessment page.
	 *
	 * @param riskAssessmentReq
	 * @return RiskAssessmentRes
	 */
	public RiskAssessmentRes queryPageData(RiskAssessmentReq riskAssessmentReq);

	/**
	 * Method Name: getCurrentEventId Method Description:returns the current
	 * EventId
	 * 
	 * @param idStage
	 * @param idCase
	 * @return SafetyAssessmentRes
	 */
	public SafetyAssessmentRes getCurrentEventId(Long idStage, Long idCase);

	/**
	 * Method Name: retrieveSafetyAssmtData Method Description:Fetches the
	 * Safety Assessment Data
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param idCase
	 * @return SafetyAssessmentRes
	 */
	public SafetyAssessmentRes retrieveSafetyAssmtData(Long idStage, Long idEvent, Long idCase);

	/**
	 * Method Name: getCurrentEventStatus Method Description:This method fetched
	 * the current event status
	 * 
	 * @param idStage
	 * @param idCase
	 * @return SafetyAssessmentRes
	 */
	public SafetyAssessmentRes getCurrentEventStatus(Long idStage, Long idCase);

	/**
	 * Method Name: getSubStageOpen Method Description:This method checks if the
	 * Sub stage is Open or Close
	 * 
	 * @param idCase
	 * @return SafetyAssessmentRes
	 */
	public SafetyAssessmentRes getSubStageOpen(Long idCase);

	/**
	 * Method Name: getQueryPgData Method Description:Query the data needed to
	 * create the safety Assessment page.
	 * 
	 * @param safetyAssessmentReq
	 * @return SafetyAssessmentRes
	 */
	public SafetyAssessmentRes getQueryPgData(SafetyAssessmentReq safetyAssessmentReq);

}
