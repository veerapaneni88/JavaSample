package us.tx.state.dfps.service.sscc.service;

import java.util.List;

import us.tx.state.dfps.service.casepackage.dto.SSCCTimelineDto;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtMedCnsntrDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtOptCircumDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for SSCC Placement Options and Circumstances Screen. Aug 10, 2018- 6:22:56 PM
 * © 2017 Texas Department of Family and Protective Services
 */
public interface SSCCPlcmtOptCircumService {

	/**
	 * Method Name: readSSCCPlcmtOptCirum Method Description: Method signarure
	 * for readSSCCPlcmtOptCircum.
	 * 
	 * @param ssccPlcmtHeaderDto
	 * @return SSCCPlcmtOptCircumDto
	 */
	SSCCPlcmtOptCircumDto readSSCCPlcmtOptCirum(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: setUpSSCCPlcmtOptCircum Method Description:Method signature
	 * for setUpSSCCPlcmtOptCircum
	 * 
	 * @param ssccPlcmtOptCircumDto
	 */
	public void setUpSSCCPlcmtOptCircum(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: readInitRsrcPers Method Description: Method signature for
	 * readInitRsrcPers.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	SSCCPlcmtOptCircumDto readInitRsrcPers(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: filterLivArrSILResourceSvc Method Description: Method
	 * Signature for filterLivArrSILResourceSvc
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @param personAge
	 * @return
	 */
	List<String> filterLivArrSILResourceSvc(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto, int personAge);

	/**
	 * Method Name: silInSSCCValidation Method Description: Method Signature for
	 * silInSSCCValidation
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @param validate
	 * @return
	 */
	List<String> silInSSCCValidation(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto, boolean validate);

	/**
	 * Method Name: fetchSSCCTimeLine Method Description: Method signature for
	 * fetchSSCCTimeLine
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	List<SSCCTimelineDto> fetchSSCCTimeLine(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: saveAndContinue Method Description: Method Signature for
	 * Save and Continue Service
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	SSCCPlcmtOptCircumDto saveAndContinue(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: setResource Method Description: Method Signature for Set
	 * Resource Service method.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @param selectedIdResource
	 * @return
	 */
	SSCCPlcmtOptCircumDto setResource(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto, Long selectedIdResource);

	/**
	 * Method Name: setSelTypeAndData Method Description: Method Signature for
	 * setSelTypeAndData Method. for setting up the Med Consenter Details.
	 * 
	 * @param ssccPlcmtMedCnsntrDto
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	SSCCPlcmtMedCnsntrDto setSelTypeAndData(SSCCPlcmtMedCnsntrDto ssccPlcmtMedCnsntrDto,
			SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: save Method Description: Method Signature for Saving
	 * SSCCPlcmtOptionCircum Page.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	SSCCPlcmtOptCircumDto save(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: cancelAndRestart Method Description: This method deletes all
	 * the child tables when sscc header is deleted.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 */
	SSCCPlcmtOptCircumDto cancelAndRestart(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: saveOnValidate Method Description: Method Signature for Save
	 * On Validate
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @param checkErrWrn
	 * @return
	 */
	SSCCPlcmtOptCircumDto saveOnValidate(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto, boolean checkErrWrn);

	/**
	 * Method Name: approveWithOutSave Method Description: Method signature for
	 * approvewithoutsave service.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	SSCCPlcmtOptCircumDto approveWithOutSave(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: reject Method Description: Method Signature for Reject
	 * Service of SSCCPlcamentOptCircum Page.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	SSCCPlcmtOptCircumDto reject(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: saveToPlaceInfo Method Description: Method Signature for
	 * SaveToPlaceInfo Service.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	SSCCPlcmtOptCircumDto saveToPlaceInfo(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: checkNarrativeExist Method Description: Method Signature to
	 * check if narrative exists.
	 * 
	 * @param idSSCCPlcmtHdr
	 * @param nbrVersion
	 * @return
	 */
	Boolean checkNarrativeExist(Long idSSCCPlcmtHdr, Long nbrVersion);

	/**
	 * Method Name: rescind Method Description: Method Signature for Rescind
	 * Service of SSCC Placement Opt Circum Page.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	SSCCPlcmtOptCircumDto rescind(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: setPerson Method Description: Method signature for Set
	 * Person Service.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @param selectedPerson
	 * @return
	 */
	SSCCPlcmtOptCircumDto setPerson(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto, PersonListDto selectedPerson);

	/**
	 * Method Name: saveAndTransmit Method Description: Method signature for
	 * SaveandTransmitRequest on SSCC Plcmt Opt Page.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	SSCCPlcmtOptCircumDto saveAndTransmit(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: updateAndNotify Method Description: Method Signature for
	 * Update and Notify Service on SSCC Plcmt Opt Circum page.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	SSCCPlcmtOptCircumDto updateAndNotify(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: setUpSectionToEdit Method Description: Method Signature for
	 * hanlding Edit request on SSCC Plcmt Opt circum page.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	SSCCPlcmtOptCircumDto setUpSectionToEdit(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: cancelUpdate Method Description: Method Signature for Cancel
	 * Update Service of SSCC Plcmt Opt Circum page.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	SSCCPlcmtOptCircumDto cancelUpdate(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: acknowledgeOnly Method Description: Method signature for
	 * AcknowledgeOnly Service of SSCC Plcmt Opt circum page.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	SSCCPlcmtOptCircumDto acknowledgeOnly(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: approveWithMod Method Description: Method Signature to
	 * Approve with Mod Service
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	SSCCPlcmtOptCircumDto approveWithMod(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: finalizeEval Method Description: Method signature to
	 * Finalize Evaluation Service to Save the Final Evaluation by DFPS User on
	 * SSCC plcmt Opt circum page.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	SSCCPlcmtOptCircumDto finalizeEval(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: cancelEval Method Description:Method signature for Cancel
	 * Evaluation Service.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	SSCCPlcmtOptCircumDto cancelEval(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

}
