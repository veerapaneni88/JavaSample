package us.tx.state.dfps.service.casemanagement.service;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import org.springframework.web.bind.annotation.RequestBody;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.workload.dto.ApprovalPersonPrintDto;

@Component
public interface CPSInvCnlsnService {

	//PPM 69915 - Alcohol Substance Tracker

	/**
	 *
	 * Method Description:This Method is used to retireve the information of all
	 * the substances for a PArent/Child for a given stage ID
	 *
	 *
	 * @param idStage Stage id
	 * @return @CpsInvSubstancesRes
	 */
	CpsInvSubstancesRes getSubstancesByStageId(Long idStage);


	/**
	 *
	 * Method Description:This Method is used to save the information of all
	 * 	 * the substances for a PArent/Child for a given stage ID
	 *
	 * @param @CpsInvSubstanceReq
	 * @return @CpsInvSubstancesRes
	 */
	CpsInvSubstancesRes saveSubstances(CpsInvSubstanceReq cpsInvSubstanceReq);


	/**
	 * 
	 * Method Description:This Method is used to retireve the information of all
	 * the closure notices for a given EventId.
	 * 
	 * @param commonHelperReq
	 * @return @
	 */
	CpsInvNoticesRes getClosureNotices(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Description: This method is used to save/update/delete the
	 * information of closure notices.
	 * 
	 * @param cpsInvNoticesClosureReq
	 */
	CpsInvNoticesRes saveClosureNotices(CpsInvNoticesClosureReq cpsInvNoticesClosureReq);

	/**
	 * 
	 * Method Description:This Method is used to retireve the information of all
	 * the closure notices for a given EventId.
	 * 
	 * @param commonHelperReq
	 * @return @
	 */
	CpsInvNoticesRes getARClosureNotices(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Name: getCompletedAssessmentsExists Method Description:This Method
	 * is used to check completed safety and risk assessment exists for default
	 * Household.
	 * 
	 * @param commonHelperReq
	 * @return @
	 */
	CommonBooleanRes getCompletedAssessmentsExists(CommonHelperReq commonHelperReq);

	/**
	 * Method Name: savePrintPerson Method Description: This Method saves the
	 * selected Staff member as the Printer Person to create the print task on
	 * final Approval.
	 * 
	 * @param cnclsnPrintStaffReq
	 * @return @
	 */
	CommonStringRes savePrintPerson(CnclsnPrintStaffReq cnclsnPrintStaffReq);

	/**
	 * Method Name: getPrintPerson Method Description: Method fetches the Saved
	 * Print Person for conclusion Event.
	 * 
	 * @param commonHelperReq
	 * @return @
	 */
	ApprovalPersonPrintDto getPrintPerson(CommonHelperReq commonHelperReq);

	/**
	 * Method Name: getMostRecentFDTCSubtype Method Description: Method
	 * signature for service for checking most recent fdtc sub type.
	 * 
	 * @param idCase
	 * @param idStage
	 * @return CpsInvCnclsnRes
	 */
	HashMap getMostRecentFDTCSubtype(Long idCase, Long idStage);

	/**
	 * Method Name: fetchAllegQuestionAnswers Method Description: This method
	 * gets ID, DOB, and DOD for victim on an allegation.
	 * 
	 * @param idStage
	 * @return boolean
	 */
	public Boolean fetchAllegQuestionAnswers(Long idStage);

	/**
	 * Method Name: fetchAllegQuestionYAnswers Method Description: This method
	 * returns if all questions have been answered.
	 * 
	 * @param cpsInvCnclsnReq
	 * @return CommonBooleanRes
	 */
	public CommonBooleanRes fetchAllegQuestionYAnswers(CpsInvCnclsnReq cpsInvCnclsnReq);

	/**
	 * Method Name: validateRlqshCstdyAndPrsnCharQuestion Method Description:
	 * This method returns true if victim relinquish custody is answered yes and
	 * person characterstics are selected.
	 * 
	 * @param cpsInvCnlsnReq
	 * @return Boolean
	 */
	public Boolean validateRlqshCstdyAndPrsnCharQuestion(CpsInvCnclsnReq cpsInvCnlsnReq);

	/**
	 * Method Name: isDispositionMissing Method Description:Retrieve true if any
	 * of allegation(s) for the input stage is missing a disposition
	 * 
	 * @param cpsInvCnclsnReq
	 * @return CommonBooleanRes @
	 */
	public CommonBooleanRes isDispositionMissing(CpsInvCnclsnReq cpsInvCnclsnReq);

	/**
	 * Method Name: validateInvStageForClosure Method Description: Method
	 * signature for service to validate the investigation Stage before Closure.
	 * 
	 * @param invStageClosureReq
	 * @return CpsInvCnclsnRes
	 */
	public CpsInvCnclsnRes validateInvStageForClosure(CpsInvCnclsnReq invStageClosureReq);

	/**
	 * artf228543
	 * Method Name: hasAllegedPerpetratorWithAgeLessThanTen
	 * Method Description: This method checks if the given stage has any alleged perpetrator
	 * with age less than ten and disposition is not Admin Closure.
	 *
	 * @param cpsInvCnclsnReq
	 * @return CommonBooleanRes
	 */
	public CommonBooleanRes hasAllegedPerpetratorWithAgeLessThanTen(@RequestBody CpsInvCnclsnReq cpsInvCnclsnReq);

	}
