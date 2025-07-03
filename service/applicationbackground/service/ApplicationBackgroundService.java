package us.tx.state.dfps.service.applicationbackground.service;

import java.rmi.RemoteException;
import java.util.List;

import us.tx.state.dfps.service.admin.dto.AaeEligDetermMessgDto;
import us.tx.state.dfps.service.adoptionasstnc.AaeApplAndDetermDBDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.request.AaeApplAndDetermReq;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ApplicationBackgroundService Nov 6, 2017- 6:20:54 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ApplicationBackgroundService {
	/**
	 * Method Name: fetchApplicationDetails Method Description:This method
	 * retrieves AAE Application & Background Information, Recent Placements and
	 * Child's information including Social Security Number, Date of Birth,
	 * MNedical Number, Age etc..
	 * 
	 * @param idStage
	 * @param idAppEvent
	 * @param isNewString
	 * @return AaeApplAndDetermDBDto @
	 */
	public AaeApplAndDetermDBDto fetchApplicationDetails(Long idStage, Long idAppEvent, Boolean isNewString);

	/**
	 * Method Name: getDetermQualMessages Method Description:This method returns
	 * the Determine qualification message details
	 * 
	 * @param aaeApplAndDetermDBDto
	 * @return AaeEligDetermMessgDto @
	 */
	public AaeEligDetermMessgDto getDetermQualMessages(AaeApplAndDetermDBDto aaeApplAndDetermDBDto);

	/**
	 * Method Name: callPostEvent Method Description:Calls the common
	 * PostEvent() function. PostEvent() calls DAO that AUD the EVENT table and
	 * it's children.
	 * 
	 * @param eventValueDto
	 * @param cReqFuncCd
	 * @param idStagePersonLinkPerson
	 * @param idAdptEligApplication
	 * @return Long @
	 */
	public Long callPostEvent(EventValueDto eventValueDto, String cReqFuncCd, Long idStagePersonLinkPerson,
			Long idAdptEligApplication);

	/**
	 * Method Name: withdrawFromAAEProcess Method Description:This method is
	 * called to withdrawal the AAE process. Changes the vent status to COMP
	 * 
	 * @param eventDto
	 * @param cReqFuncCd
	 * @return Long @
	 */
	public Long withdrawFromAAEProcess(EventValueDto eventDto, String cReqFuncCd);

	/**
	 * Method Name: validateSubmitApplication Method Description:This method
	 * validates the data before submitting the Application to AAES.
	 * 
	 * @param AaeApplAndDetermReq
	 * @return Long[] - message Ids
	 * @throws RemoteException
	 * @
	 */
	public List validateSubmitApplication(AaeApplAndDetermReq aaeApplAndDetermReq);

	/**
	 * This method validates the data before Determining Qualification for the
	 * Application
	 * 
	 * @param AaeApplAndDetermReq
	 * @return Long[] - message Ids
	 * @throws RemoteException
	 * @
	 */
	public List validateApplForDetermQual(AaeApplAndDetermReq aaeApplAndDetermReq);

	/**
	 * Method Name: determineQualification Method Description: Determines the
	 * qualification of the Application. It will generate the qualification
	 * messages and saves info based on calculations.
	 * 
	 * @param aaeApplAndDetermReq
	 * @return AaeEligDetermMessgDto @
	 */
	public AaeEligDetermMessgDto determineQualification(AaeApplAndDetermReq aaeApplAndDetermReq);

	/**
	 * Method Name: fetchLatestApplAndBackgroundInfo Method Description: This
	 * method returns Latest AAE Application And Background information for the
	 * idPerson
	 * 
	 * @param idPerson
	 * @param idStage
	 * @return AaeApplAndDetermDBDto @
	 */
	public AaeApplAndDetermDBDto fetchLatestApplAndBackgroundInfo(Long idPerson, Long idStage);

	/**
	 * Method Name: isAdptAssistApplUnapproved Method Description: This method
	 * checks if there is a latest existing Adoption Application which is in
	 * PEND or COMP status ( not withdrawn )
	 * 
	 * @param idStage
	 * @return Boolean @
	 */
	public Boolean isAdptAssistApplUnapproved(Long idStage);

	public Long saveApplAndBackgroundInfo(AaeApplAndDetermReq aaeApplAndDetermReq);

	// public AaeApplAndDetermRes getApplAndBackgroundInfo(AaeApplAndDetermReq
	// aaeApplAndDetermReq) ;

}
