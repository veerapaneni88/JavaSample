package us.tx.state.dfps.service.riskassessment.service;

import us.tx.state.dfps.riskandsafetyassmt.dto.PrincipalListResDto;
import us.tx.state.dfps.riskandsafetyassmt.dto.SafteyEvalResDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.RiskAssessmentReq;
import us.tx.state.dfps.service.common.response.RiskAssessmentRes;
import us.tx.state.dfps.service.common.response.RiskFactorRes;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.riskassesment.dto.InvActionDtlDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * service populates the Risk Assessment Narrative Form. Mar 15, 2018- 10:29:24
 * PM © 2017 Texas Department of Family and Protective Services
 */
public interface RiskAssessmentService {
	/**
	 * 
	 * Method Name: getRiskAssmntData Method Description: This method is used
	 * for populates the Risk Assessment Narrative Form.
	 * 
	 * @param idStage
	 *            CINV77S
	 * @return RiskAssessmentNarrativeDto @
	 */
	public PreFillDataServiceDto getRiskAssmntData(Long idStage);

	/**
	 * 
	 * Method Name: getRiskAssmntInvData Method Description: This method is used
	 * for populates the Risk Assessment for Ruled-Out Investigation Form.
	 * 
	 * @param idStage
	 *            CINV84S
	 * @return RiskAssessmentNarrativeDto @
	 */
	public PreFillDataServiceDto getRiskAssmntInvData(Long idStage);

	/**
	 * Service Name: cinv02s Method Name: getInvActionDetails Method
	 * Description: This service Retrieves information for the Inv Action Window
	 *
	 * @param idStage
	 * @param idEvent
	 * @return RtrvInvActQuestResDto
	 */
	public InvActionDtlDto getInvActionDetails(Long idStage, Long idEvent, String cdReqFunction);

	/**
	 * Service Name: cinv51s Method Name: getRiskFactorDetails Method
	 * Description: A retrieval service which obtains risk factors for either a
	 * Principal or an Incident type from the RISK FACTORS table. The service
	 * also returns the current time stamp for the ID EVENT on the Event table.
	 *
	 * @param idStage
	 * @param idEvent
	 * @return RiskFactorRtrvResDto
	 */
	public RiskFactorRes getRiskFactorDetails(Long idPerson, Long idEvent, String getData);

	/**
	 * Service Name: cinv00s Method Name: getSafetyEval Method Description: This
	 * service calls 2 DAMS to retrieve the information for the Safety
	 * Evaluation window. The first service gets information from the Safety
	 * Evaluation Table to be used for entry fields. The second DAM is a list
	 * that will retieve the Safety Eval Factor information for the safety
	 * factor list box. The service also retrieves a full row from the event
	 * table, and also the status of the Conclusion event for this stage. IT
	 * also calls a DAM to determine if the ID Event passed in corresponds to
	 * the most recent Safety Evaluation and sets a flag accordingly
	 *
	 * @param idStage
	 * @param idEvent
	 * @return RiskFactorRtrvResDto
	 */
	public SafteyEvalResDto getSafetyEval(CommonHelperReq commonHelperReq);

	/**
	 * Service Name: CINV36S Method Name: getPrincipalList Method Description: A
	 * retrieval service to fill the Principal list box on the Risk Assessment
	 * window.
	 *
	 * @param CommonHelperReq
	 * @return principalListResDto
	 */
	public PrincipalListResDto getPrincipalList(CommonHelperReq commonHelperReq);

	/**
	 * Method Name: queryRiskAssmtExists Method Description:Query the Risk
	 * Assessment to check if Risk Assessment already exists.
	 * 
	 * @param CommonHelperReq
	 * @return RiskAssessmentRes
	 */
	public RiskAssessmentRes queryRiskAssmtExists(RiskAssessmentReq riskAssessmentReq);

	/**
	 * Method Name: checkRiskAssmtTaskCode SIR 24696, Check the stage table to
	 * see if INV stage is closed and event table, if it has task code for Risk
	 * Assessment.
	 * 
	 * @param CommonHelperReq
	 * @return RiskAssessmentRes
	 */
	public RiskAssessmentRes checkRiskAssmtTaskCode(RiskAssessmentReq riskAssessmentReq);

	/**
	 * Method Name: checkIfRiskAssmtCreatedUsingIRA Query the
	 * IND_RISK_ASSMT_INTRANET column on the RISK_ASSESSMENT table to determine
	 * if the Risk Assessment was created using IRA or IMPACT. has task code for
	 * Risk Assessment.
	 * 
	 * @param CommonHelperReq
	 * @return RiskAssessmentRes
	 */
	public RiskAssessmentRes checkIfRiskAssmtCreatedUsingIRA(RiskAssessmentReq riskAssessmentReq);

	/**
	 * Method Name: getCurrentEventStatus Method Description : Returns the
	 * current Event status
	 * 
	 * @param RiskAssessmentReq
	 * @return RiskAssessmentRes
	 */
	public RiskAssessmentRes getCurrentEventStatus(RiskAssessmentReq riskAssessmentReq);

}
