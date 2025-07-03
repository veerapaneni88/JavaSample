package us.tx.state.dfps.service.investigation.service;

import us.tx.state.dfps.service.common.request.NameChangeReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.LicensingInvCnclusnReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.LicensingInvCnclusnRes;
import us.tx.state.dfps.service.investigation.dto.CvsNotifLogDto;

import java.util.Date;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION interface
 * Description:<LicensingInvstCnclusnService for sending req to
 * LicensingInvstCnclusnServiceImpl class> May 23, 2018- 3:05:39 PM © 2017 Texas
 * Department of Family and Protective Services.
 */
public interface LicensingInvstCnclusnService {

	/**
	 * Save licensing inv conclusion.
	 *
	 * @param licensingInvCnclusnReq
	 *            the licensing inv cnclusn req
	 */
	public void saveLicensingInvConclusion(LicensingInvCnclusnReq licensingInvCnclusnReq);
	
	
	/**
     * Save alleged behavior
     *
     * @param licensingInvCnclusnReq
     *            the licensing inv cnclusn req
     */
    public LicensingInvCnclusnRes saveAllegedBehavior(LicensingInvCnclusnReq licensingInvCnclusnReq);

	/**
	 * Display licensing inv conclusion.
	 *
	 * @param licensingInvCnclusnReq
	 *            the licensing inv cnclusn req
	 * @return the licensing inv cnclusn res
	 */
	public LicensingInvCnclusnRes displayLicensingInvConclusion(LicensingInvCnclusnReq licensingInvCnclusnReq);

	/**
	 * Method Name: getOverallDispositionExists
	 * Method Description: This method is used to rquery the database and see if a stage has an overall disposition
	 * present.
	 * artf128755 - CCI reporter letter
	 *
	 * @param licensingInvCnclusnReq stage to be searched is passed as idStage in DTO.
	 * @return DTO structure containing the result, LicensingInvCnclusnRes.overallDispositionExists will be set to true
	 * or false depending on result.
	 */
	public LicensingInvCnclusnRes getOverallDispositionExists(LicensingInvCnclusnReq licensingInvCnclusnReq);

	/**
	 * Validate and get class info.
	 *
	 * @param licensingInvCnclusnReq
	 *            the licensing inv cnclusn req
	 * @return the licensing inv cnclusn res
	 */
	public LicensingInvCnclusnRes validateAndGetClassInfo(LicensingInvCnclusnReq licensingInvCnclusnReq);

	/**
	 * Save and submit licensing inv conclusion.
	 *
	 * @param licensingInvCnclusnReq
	 *            the licensing inv cnclusn req
	 * @return the licensing inv cnclusn res
	 */
	public LicensingInvCnclusnRes saveAndSubmitLicensingInvConclusion(LicensingInvCnclusnReq licensingInvCnclusnReq);

	/**
	 * This searches the stage for any contacts of the specified type to the specified person.
	 *
	 * @param commonHelperReq The required fields are idStage, idPerson, and stageType.
	 * @return Structure containing hasContactToPerson boolean indicating a contact was found.
	 */
	public CommonHelperRes stageHasContactTypeToPerson(CommonHelperReq commonHelperReq);

	/**
	 * Method Name: updateResourceName
	 * Method Description: This method is used to update case name, stage name if IMPACT Operation Resource ID
	 * 	is updated on Operation Number validation in INV conclusion page
	 * artf233239 -Investigation Conclusion
	 * @param nameChangeReq
	 * @return CommonHelperRes
	 */
	CommonHelperRes updateResourceNameAndCounty(NameChangeReq nameChangeReq);

	/**
	 * Save CVS Notification Log.
	 * @param cvsNotifLogDto
	 * @return Long
	 */
	public Long saveCvsNotifLog( CvsNotifLogDto cvsNotifLogDto) ;
	/**
	 * Method Name: getIntakeStage
	 * Method Description:This method is used to get
	 * the intake date of the Intake stage.
	 *
	 * @param idStage
	 *            - The id of the current stage.
	 * @return LicensingInvCnclusnRes
	 */
	public LicensingInvCnclusnRes getIntakeDate(Long idStage);

	/**
	 * Method Name: hasManualNotification
	 * Method Description: This method is used to check if manual notification is sent for the person
	 * @param caseId
	 * @param personId
	 * @return CommonHelperRes
	 */
	public CommonHelperRes hasManualNotification(Long caseId, Long personId);


	}
