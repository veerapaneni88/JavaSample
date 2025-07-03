package us.tx.state.dfps.service.riskassessment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.SDM.service.SafetyEvalService;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.RiskAssessmentReq;
import us.tx.state.dfps.service.common.response.InvActionDtlRes;
import us.tx.state.dfps.service.common.response.PrincipalListRes;
import us.tx.state.dfps.service.common.response.RiskAssessmentRes;
import us.tx.state.dfps.service.common.response.RiskFactorRes;
import us.tx.state.dfps.service.common.response.SafetyEvalRes;
import us.tx.state.dfps.service.riskassesment.dto.InvActionDtlDto;
import us.tx.state.dfps.service.riskassessment.service.RiskAssessmentService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is used to handle the REST service requests from the business delegate to
 * fetch, save and delete the SDM Risk Reassessment details. Jun 14, 2018-
 * 3:25:43 PM © 2017 Texas Department of Family and Protective Services
 */
@RequestMapping("/riskassessment/")
@RestController
public class RiskAssessmentController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	RiskAssessmentService riskassessmentService;

	@Autowired
	SafetyEvalService safetyEvalService;

	/**
	 * Method Name: getInvActionDetails Method Description:cinv02s - This method
	 * is used to fetch the details for Inv Action Window
	 * 
	 * @param CommonHelperReq
	 *            - This dto will hold the input parameters to retrieve the Inv
	 *            Action Window details.
	 * @return InvActionDtlRes - This dto will have the Inv Action Window
	 *         details.
	 */
	@RequestMapping(value = "getInvActionDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  InvActionDtlRes getInvActionDetails(@RequestBody CommonHelperReq commonHelperReq) {

		InvActionDtlRes invActionDtlRes = new InvActionDtlRes();
		// This method is used to fetch the details for Inv Action Window
		InvActionDtlDto invActionDtlDto = riskassessmentService.getInvActionDetails(commonHelperReq.getIdStage(),
				commonHelperReq.getIdEvent(), commonHelperReq.getReqFuncCd());

		invActionDtlRes.setInvActionDtlDto(invActionDtlDto);

		return invActionDtlRes;
	}

	/**
	 * Service Name: cinv51s Method Name: getRiskFactorDetails Method
	 * Description: A retrieval service which obtains risk factors for either a
	 * Principal or an Incident type from the RISK FACTORS table. The service
	 * also returns the current time stamp for the ID EVENT on the Event table.
	 *
	 * @param commonHelperReq
	 * @return RiskFactorRes
	 */
	@RequestMapping(value = "getRiskFactorDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RiskFactorRes getRiskFactorDetails(@RequestBody CommonHelperReq commonHelperReq) {

		return riskassessmentService.getRiskFactorDetails(commonHelperReq.getIdPerson(), commonHelperReq.getIdEvent(),
				commonHelperReq.getReqFuncCd());
	}

	/**
	 * Service Name: cinv00s Method Name: getRiskFactorDetails Method
	 * Description: A retrieval service which obtains safety evaluation details
	 *
	 * @param commonHelperReq
	 * @return SafetyEvalRes
	 */
	@RequestMapping(value = "getSafetyEval", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SafetyEvalRes getSafetyEval(@RequestBody CommonHelperReq commonHelperReq) {

		SafetyEvalRes safetyEvalRes = new SafetyEvalRes();

		safetyEvalRes.setSafteyEvalResDto(riskassessmentService.getSafetyEval(commonHelperReq));

		return safetyEvalRes;
	}

	/**
	 * Service Name: cinv36s Method Name: getPrincipalList Method Description: A
	 * retrieval service to fill the Principal list box on the Risk Assessment
	 * window.
	 *
	 * @param RiskAssessmentReq
	 * @return PrincipalListRes
	 */
	@RequestMapping(value = "getPrincipalList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PrincipalListRes getPrincipalList(@RequestBody CommonHelperReq commonHelperReq) {

		PrincipalListRes principalListRes = new PrincipalListRes();

		principalListRes.setPrincipalList(riskassessmentService.getPrincipalList(commonHelperReq));

		return principalListRes;
	}

	/**
	 * Method Name: queryRiskAssmt Method Description:Retrieve the Risk
	 * Assessment details and the data needed to build the Risk Assessment page.
	 *
	 * @param RiskAssessmentReq
	 * @return RiskAssessmentRes
	 */
	@RequestMapping(value = "queryRiskAssmt", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RiskAssessmentRes queryRiskAssmt(@RequestBody RiskAssessmentReq riskAssessmentReq) {

		return safetyEvalService.queryRiskAssmt(riskAssessmentReq);
	}

	/**
	 * Method Name: queryRiskAssmtExists Method Description:Query the Risk
	 * Assessment to check if Risk Assessment already exists.
	 * 
	 * @param RiskAssessmentReq
	 * @return RiskAssessmentRes
	 */
	@RequestMapping(value = "queryRiskAssmtExists", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RiskAssessmentRes queryRiskAssmtExists(@RequestBody RiskAssessmentReq riskAssessmentReq) {

		return riskassessmentService.queryRiskAssmtExists(riskAssessmentReq);
	}

	/**
	 * Method Name: queryPageData Method Description:Query the data needed to
	 * create the Risk Assessment page.
	 *
	 * @param RiskAssessmentReq
	 * @return RiskAssessmentRes
	 */
	@RequestMapping(value = "queryPageData", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RiskAssessmentRes queryPageData(@RequestBody RiskAssessmentReq riskAssessmentReq) {

		return safetyEvalService.queryPageData(riskAssessmentReq);
	}

	/**
	 * Method Name: checkRiskAssmtTaskCode SIR 24696, Check the stage table to
	 * see if INV stage is closed and event table, if it has task code for Risk
	 * Assessment.
	 * 
	 * @param RiskAssessmentReq
	 * @return RiskAssessmentRes
	 */
	@RequestMapping(value = "checkRiskAssmtTaskCode", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RiskAssessmentRes checkRiskAssmtTaskCode(@RequestBody RiskAssessmentReq riskAssessmentReq) {

		return riskassessmentService.checkRiskAssmtTaskCode(riskAssessmentReq);
	}

	/**
	 * Method Name: checkIfRiskAssmtCreatedUsingIRA Query the
	 * IND_RISK_ASSMT_INTRANET column on the RISK_ASSESSMENT table to determine
	 * if the Risk Assessment was created using IRA or IMPACT. has task code for
	 * Risk Assessment.
	 * 
	 * @param RiskAssessmentReq
	 * @return RiskAssessmentRes
	 */
	@RequestMapping(value = "checkIfRiskAssmtCreatedUsingIRA", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RiskAssessmentRes checkIfRiskAssmtCreatedUsingIRA(
			@RequestBody RiskAssessmentReq riskAssessmentReq) {

		return riskassessmentService.checkIfRiskAssmtCreatedUsingIRA(riskAssessmentReq);
	}

	/**
	 * Method Name: getCurrentEventStatus Method Description : Returns the
	 * current Event status
	 * 
	 * @param RiskAssessmentReq
	 * @return RiskAssessmentRes
	 */
	@RequestMapping(value = "getCurrentEventStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RiskAssessmentRes getCurrentEventStatus(@RequestBody RiskAssessmentReq riskAssessmentReq) {

		return riskassessmentService.getCurrentEventStatus(riskAssessmentReq);
	}

}
