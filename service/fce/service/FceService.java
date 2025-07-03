package us.tx.state.dfps.service.fce.service;

import java.util.List;

import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.FceReq;
import us.tx.state.dfps.service.common.request.SaveFceApplicationReq;
import us.tx.state.dfps.service.common.request.TexasZipCountyValidateReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.GetFceApplicationRes;
import us.tx.state.dfps.service.fce.EligibilityDto;
import us.tx.state.dfps.service.fce.dto.FceContextDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.workload.dto.EventDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This is the
 * Foster Care Eligibility Service Primary functions: initializing FCE
 * Application, Eligibility and Review Mar 15, 2018- 11:10:51 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface FceService {

	/**
	 * 
	 * Method Name: hasOpenFosterCareEligibility Method Description: Checks if
	 * the given person has an open FCE
	 * 
	 * @param idPerson
	 * @return @
	 */
	public Boolean hasOpenFosterCareEligibility(Long idPerson);

	/**
	 * 
	 * Method Name: getOpenFceEventForStage Method Description: Retrieves open
	 * FCE event for given stage
	 * 
	 * @param idStage
	 * @return Long @
	 */
	public Long getOpenFceEventForStage(Long idStage);

	/**
	 * Retrieves the latest FC eligibility for given stage
	 * 
	 * @param idPerson
	 * @return @
	 */
	public EligibilityDto fetchLatestEligibility(Long idPerson);

	/**
	 * Retrieves active FCEs for given person
	 * 
	 * @param idPerson
	 * @return @
	 */
	public List<EligibilityDto> fetchActiveFceList(Long idPerson);

	/**
	 * 
	 * Method Name: initializeFceApplication Method Description: This service is
	 * used for Initializes FCE Application
	 * 
	 * @param idStage
	 * @param idAppEvent
	 * @param idLastUpdatePerson
	 * @return FceContextDto @
	 */
	public FceContextDto initializeFceApplication(Long idStage, Long idAppEvent, Long idLastUpdatePerson);

	/**
	 * 
	 * Method Name: initializeFceEligibility Method Description: This service is
	 * used for Initializes FCE Eligibility
	 * 
	 * @param idStage
	 * @param idEligibilityEvent
	 * @param idLastUpdatePerson
	 * @return FceContextDto @
	 */
	public FceContextDto initializeFceEligibility(Long idStage, Long idEligibilityEvent, Long idLastUpdatePerson);

	/**
	 * Initializes FCE Review
	 * 
	 * @param idStage
	 * @param idReviewEvent
	 * @param idLastUpdatePerson
	 * @return @
	 */
	public FceContextDto initializeFceReview(Long idStage, Long idReviewEvent, Long idLastUpdatePerson);

	/**
	 * This is to get the fce application information for App/Background page
	 * load
	 * 
	 * @param fceReq
	 * @return @
	 */
	GetFceApplicationRes getFceApplication(FceReq fceReq);

	/**
	 * 
	 * Method Name: findOrCreateEvent Method Description: This method creates
	 * and retrieves the event data
	 * 
	 * @param eventType
	 * @param idEvent
	 * @param idPerson
	 * @param idStage
	 * @param idChild
	 * @return EventDto @
	 */
	public EventDto findOrCreateEvent(String eventType, Long idEvent, Long idPerson, Long idStage, Long idChild);

	/**
	 * 
	 * Method Name: getDescription Method Description: This method returns the
	 * eventDescription based on taskCode and eventStatus
	 * 
	 * @param taskCode
	 * @param eventStatus
	 * @return eventDescription @
	 */
	public String getDescription(String taskCode, String eventStatus);

	/**
	 * 
	 * Method Name: createEvent Method Description: This method is used for
	 * creating an event
	 * 
	 * @param eventType
	 * @param idPerson
	 * @param idStage
	 * @param idChild
	 * @return @
	 */
	public Long createEvent(String eventType, Long idPerson, Long idStage, Long idChild);

	/**
	 * This is to save the fce application information from App/Background page
	 * 
	 * @param saveFceApplicationReq
	 * @return
	 */
	ServiceResHeaderDto saveFceApplication(SaveFceApplicationReq saveFceApplicationReq);

	/**
	 * 
	 * Method Name: verifyCanSave Method Description: This method is used for
	 * verify the save
	 * 
	 * @param idStage
	 * @param idLastUpdatePerson
	 * @
	 */
	public void verifyCanSave(Long idStage, Long idLastUpdatePerson);

	/**
	 * 
	 * Method Name: syncFceEligibilityStatus Method Description: This method is
	 * used for updating the fceEligibility
	 * 
	 * @param fceEligibilityDto
	 * @
	 */
	public void syncFceEligibilityStatus(FceEligibilityDto fceEligibilityDto);

	/**
	 * 
	 * Method Name: syncFceApplicationStatus Method Description: This method is
	 * used for updating the fceEligibility and fceApplication
	 * 
	 * @param fceEligibilityDto
	 * @
	 */
	public void syncFceApplicationStatus(FceEligibilityDto fceEligibilityDto);

	/**
	 * This service is to check if the given zip and county is valid for texas
	 * 
	 * @param texasZipCountyValidateReq
	 * @return
	 * @throws InvalidRequestException
	 * @
	 */
	public CommonBooleanRes isValidTexasZipAndCounty(TexasZipCountyValidateReq texasZipCountyValidateReq);

	/**
	 * 
	 * Method Name: createPostEvent Method Description: This method is used for
	 * the creating a new event.
	 * 
	 * @param idPerson
	 * @param idStage
	 * @param idChild
	 * @param taskCode
	 * @param eventType
	 * @param eventDescription
	 * @return idEvent @
	 */
	public Long createPostEvent(Long idPerson, Long idStage, Long idChild, String taskCode, String eventType,
			String eventDescription);

	/**
	 * 
	 * Method Name: newUsingFceReview Method Description: New Using FCE Review
	 * 
	 * @param idStage
	 * @param idReviewEvent
	 * @param idLastUpdatePerson
	 * @return FceContextDto
	 */
	public FceContextDto newUsingFceReview(Long idStage, Long idReviewEvent, Long idLastUpdatePerson);

	/**
	 * 
	 * Method Name: createNewApp Method Description: This method called when
	 * newusing is called , the following method will create new app
	 * 
	 * @param fceReq
	 * @return FceApplicationRes
	 */
	GetFceApplicationRes createNewApp(FceReq fceReq);

	/**
	 * 
	 * Method Name: copyOldApptoNewApp Method Description: This method called
	 * after calling the createNewApp to copy the original application data to
	 * new application
	 * 
	 * @param fceReq
	 * @return FceApplicationRes
	 */
	GetFceApplicationRes copyOldApptoNewApp(GetFceApplicationRes fceApplicationRes, FceReq fceReq);

}
