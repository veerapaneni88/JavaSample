package us.tx.state.dfps.service.investigation.service;

import java.util.List;

import us.tx.state.dfps.service.common.request.FacilityInvCnclsnReq;
import us.tx.state.dfps.service.common.response.FacilityInvCnclsnRes;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityInvProviderDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Ejb Service Name:
 * FacilityInvCnclsnBean. Class Description: This is Service Interface class and
 * methods are implemented in FacilityInvCnclsnServiceImpl class. Nov 14, 2017 -
 * 3:18:29 PM
 */
public interface FacilityInvCnclsnService {

	/**
	 * Method Description: This method will retrieve the facility Investigation
	 * Resource Details for the given InvstRsrcLink ID.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	public FacilityInvCnclsnRes getFacilityInvstRsrcLink(FacilityInvCnclsnReq facilityInvCnclsnReq);

	/**
	 * Method Description: This method will retrieve the facility associated
	 * with the allegation for the given Stage ID.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	public FacilityInvCnclsnRes getAllegedFacilitiesDtls(FacilityInvCnclsnReq facilityInvCnclsnReq);

	/**
	 * Method Description: This method will save the facility to the link table
	 * as part of investigation.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	public FacilityInvCnclsnRes getsaveFacility(FacilityInvCnclsnReq facilityInvCnclsnReq);

	/**
	 * Method Description: This method will assign and update the facility
	 * Overall Dispo as part of investigation.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	public FacilityInvCnclsnRes assignAndUpdateFacilOverallDispo(FacilityInvCnclsnReq facilityInvCnclsnReq);

	/**
	 * Method Description: This method will delete the facility to the link
	 * table as part of investigation.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	public FacilityInvCnclsnRes deleteFacility(FacilityInvCnclsnReq facilityInvCnclsnReq);

	/**
	 * Method Description: This method will verify any one of allegation is tied
	 * to one facility
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	public FacilityInvCnclsnRes facilityTiedToAllegation(FacilityInvCnclsnReq facilityInvCnclsnReq);

	/**
	 * Method Description: This method will verify any one of allegation is tied
	 * to one facility
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	public FacilityInvCnclsnRes nameTiedToFacWithAllegOfCRC(FacilityInvCnclsnReq facilityInvCnclsnReq);

	/**
	 * Method Name: comparePersonAndFacilityAddress Method Description:This
	 * method will compare the Person and Facility Address and return the
	 * boolean value
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameters to fetch the
	 *            facility address and the victim addresses.
	 * @return FacilityInvCnclsnRes - This dto will hold the boolean indicator
	 *         to indicate if the victim address and facility address match.
	 */
	public FacilityInvCnclsnRes comparePersonAndFacilityAddress(FacilityInvCnclsnReq facilityInvCnclsnReq);

	/**
	 * Method Name: getCurrentStagePriority Method Description:This method is
	 * used to retrieve the current priority of the stage.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameter for retrieving the
	 *            current priority of the stage.
	 * @return FacilityInvCnclsnRes -This dto will hold the list of providers in
	 *         the Facility Investigation conclusion .
	 */
	public FacilityInvCnclsnRes getCurrentStagePriority(FacilityInvCnclsnReq facilityInvCnclsnReq);

	/**
	 * Method Name: getApprovalStatusInfo Method Description:This method is used
	 * to get the approval status of the Facility Investigation Conclusion.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameters for retrieving the
	 *            approval status info.
	 * @return FacilityInvCnclsnRes - This dto will hold the approval status
	 *         details for the Facility Investigation Conclusion.
	 */
	public FacilityInvCnclsnRes getApprovalStatusInfo(FacilityInvCnclsnReq facilityInvCnclsnReq);

	/**
	 * Method Name: getProgramAdmins Method Description:This method is used to
	 * get the Program Admins for the New EMR Investigation conclusion.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameters for retrieving the
	 *            Program Admin map .
	 * @return FacilityInvCnclsnRes - This dto will hold the Program Admins map
	 *         for the New EMR Investigation conclusion.
	 */
	public FacilityInvCnclsnRes getProgramAdminsDtls(FacilityInvCnclsnReq facilityInvCnclsnReq);

	/**
	 * Method Description: This method will will return the boolean as true if
	 * the linked facility type is Community Provider.
	 *
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	public FacilityInvCnclsnRes getLinkedFacilityCommunityProvider(FacilityInvCnclsnReq facilityInvCnclsnReq);

	/**
	 * Method Description: This method will invokes the dao method
	 * getPersMedicaidMissingInd which returns a map of person id keys and an
	 * indicator (Y or N) for each person indicating if Medicaid identifier is
	 * missing. This methods return true if there is one value in the map that
	 * equals to 'Y' indicating that there is at least one person without an
	 * active Medicaid identifier
	 *
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	public FacilityInvCnclsnRes getMedicaidIdentifierMissing(FacilityInvCnclsnReq facilityInvCnclsnReq);

	/**
	 * Method Name: getReportableConductExists Method Description:This method is
	 * used to check if Reportable Conduct exists for the current stage in the
	 * list of Facilities in the Facility Investigation Conclusion.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameters for checking if a
	 *            reportable conduct exists.
	 * @return FacilityInvCnclsnRes - This dto will hold the boolean indicator
	 *         for reportable conduct exists.
	 */
	public FacilityInvCnclsnRes getReportableConductExists(FacilityInvCnclsnReq facilityInvCnclsnReq);

	/**
	 * Method Name: getFacilityInvCnclsn Method Description:This method is used
	 * to get the Facility Investigation conclusion details for the stage.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameter values for retrieving
	 *            the Facility Conclusion details.
	 * @return FacilityInvCnclsnRes - This dto will hold the values of Facility
	 *         Conclusion details.
	 */
	public FacilityInvCnclsnRes getFacilityInvCnclsn(FacilityInvCnclsnReq facilityInvCnclsnReq);

	/**
	 * Method Name: getInvestigatedFacilityList Method Description:This method
	 * is invoked to retrieve the list of Providers in the Facility
	 * Investigation conclusion.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameter for retrieving the
	 *            list of providers.
	 * @return facilityInvCnclsnRes - This dto will hold the list of providers
	 *         in the Facility Investigation conclusion .
	 */
	public List<FacilityInvProviderDto> getInvestigatedFacilitiesList(Long idStage);

	/**
	 * Method Name: getCommunityProviderExists Method Description:This method is
	 * used to check if a community provider exists in the list of Providers in
	 * the Facility Investigation conclusion.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameters to check if a
	 *            community provider exists as part of the Facility
	 *            Investigation conclusion.
	 * @return facilityInvCnclsnRes - This dto will hold the boolean indicator
	 *         whether a community provider exists or not.
	 */
	public boolean checkCommunityProviderExists(FacilityInvCnclsnReq facilityInvCnclsnReq);

	/**
	 * Method Name: saveFacilityInvCnclsn Method Description:This method is used
	 * to save the Facility Investigation conclusion details.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the Facility Investigation conclusion
	 *            details to be saved.
	 * @return FacilityInvCnclsnRes - This dto will hold the response of the
	 *         save of Facility Conclusion , whether the data was saved or some
	 *         error occurred.
	 * @throws Exception
	 */
	public FacilityInvCnclsnRes saveFacilityInvCnclsn(FacilityInvCnclsnReq facilityInvCnclsnReq) throws Exception;

	/**
	 * Method Name: saveAndSubmitFacilityInvCnclsn Method Description:This
	 * method is used to Save and Submit the Facility Investigation Conclusion
	 * details. It first checks if there are validation errors , and if any
	 * return the list of error codes to the business delegate to be displayed
	 * on the screen. If no validation errors , then the method saves the
	 * Facility Investigation Conclusion details and calls the Validation
	 * service(CINV59s) to check if there is any errors before completing the
	 * Conclusion. If any the list of error codes are returned to the business
	 * delegate.Else the conclusion ic completed.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes
	 * @throws Exception
	 */
	public FacilityInvCnclsnRes saveAndSubmitFacilityInvCnclsn(FacilityInvCnclsnReq facilityInvCnclsnReq)
			throws Exception;

	/**
	 * Method Name: saveAndCloseFacilityInvCnclsn Method Description:This method
	 * is used to save the facility conclusion and close the stage.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the details to saved .
	 * @return FacilityInvCnclsnRes - This dto will hold the response of the
	 *         save and submit of Facility Conclusion , whether the data was
	 *         saved or some error occurred.
	 * @throws Exception
	 */
	public FacilityInvCnclsnRes saveAndCloseFacilityInvCnclsn(FacilityInvCnclsnReq facilityInvCnclsnReq)
			throws Exception;

	/**
	 *Method Name:	checkErrorDisplayAbuseForm
	 *Method Description:
	 *@param idEvent
	 *@param idStage
	 *@return
	 */
	public FacilityInvCnclsnRes checkErrorDisplayAbuseForm(FacilityInvCnclsnReq facilityInvCnclsnRes);

}
