package us.tx.state.dfps.service.fce.service;

import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.fce.dto.EligibilityDeterminationFceDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This Class
 * is used for Adoption Assistance and FosterCare
 * EligibilityDeterminationService Mar 15, 2018- 10:25:50 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface EligibilityDeterminationService {

	/**
	 * Method Name: fetchEligDetermination Method Description: This method is
	 * used for reads the EligibilityDetermination details
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param idLastUpdatePerson
	 * @return EligibilityDeterminationFceDto @
	 */
	public EligibilityDeterminationFceDto fetchEligDetermination(Long idStage, Long idEvent, Long idLastUpdatePerson);

	/**
	 * Method Name: determineEligibility Method Description:Determines the
	 * Eligibility
	 * 
	 * @param eligibilityDeterminationFceDto
	 * @return CommonHelperRes @
	 */
	public CommonHelperRes determineEligibility(EligibilityDeterminationFceDto eligibilityDeterminationFceDto);

	/**
	 * Method Name: confirmEligibility Method Description: This method is used
	 * for Confirms the Eligibility
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param idLastUpdatePerson
	 * @return CommonHelperRes @
	 */
	public CommonHelperRes confirmEligibility(Long idStage, Long idEvent, Long idLastUpdatePerson);

	/**
	 * Method Name: save Method Description: This method is used for saves the
	 * eligibility determination details.
	 * 
	 * @param eligibilityDeterminationFceDto
	 * @return CommonHelperRes @
	 */
	public CommonHelperRes saveEligibility(EligibilityDeterminationFceDto eligibilityDeterminationFceDto);

	/**
	 * Method Name: hasDOBChangedForCertPers Method Description: This method is
	 * used for Checks if certified persons DOB has changed
	 * 
	 * @param idFceEligibility
	 * @return Boolean @
	 */
	public Boolean hasDOBChangedForCertPers(Long idFceEligibility);
	/**
	 * Commented code will be used for adaption assistance.
	 */
	/* *//**
			 * Method Name: getEligDeterminationInfo Method Description: This
			 * method returns Adoption Assistance Determination information
			 * including - Event
			 * 
			 * @param idStage
			 * @param idAppEvent
			 * @return AaeApplAndDetermDBDto @
			 */
	/*
	 * public AaeApplAndDetermDBDto getEligDeterminationInfo(Long idStage, Long
	 * idAppEvent) ;
	 * 
	 *//**
		 * Method Name: updateEventAndCreateEligDeterm Method Description: This
		 * method update the Event to PEND and will create a new Adoption
		 * Assistance Eligibility Determination This is called when the
		 * Application is submitted to Eligibility Specialist
		 * 
		 * @param idEvent
		 * @param idLastUpdatePerson
		 * @return Long @
		 */
	/*
	 * public Long updateEventAndCreateEligDeterm(Long idEvent, Long
	 * idLastUpdatePerson) ;
	 * 
	 *//**
		 * Method Name: saveEligDeterminationInfo Method Description:This method
		 * update Adoption Assistance Eligibility Determination and the event.
		 * 
		 * @param aaeApplAndDetermDBDto
		 * @return Long @
		 */
	/*
	 * public Long saveEligDeterminationInfo(AaeApplAndDetermDBDto
	 * aaeApplAndDetermDBDto) ;
	 * 
	 *//**
		 * Method Name: saveEligDetermAndCompTodo Method Description:This method
		 * saves Adoption Assistance Determination and completes Adoption
		 * Assistance Eligibility Determination Todo as complete.
		 * 
		 * @param aaeApplAndDetermDBDto
		 * @return Long @
		 */
	/*
	 * public Long saveEligDetermAndCompTodo(AaeApplAndDetermDBDto
	 * aaeApplAndDetermDBDto) ;
	 * 
	 *//**
		 * Method Name: saveEligDeterminationValueBean Method Description:This
		 * method saves AAE Determination value bean.
		 * 
		 * @param eligibilityDeterminationDto
		 * @return Long @
		 */
	/*
	 * public Long saveEligDeterminationValueBean(EligibilityDeterminationDto
	 * eligibilityDeterminationDto) ;
	 * 
	 *//**
		 * Method Name: determinePrelimDetermin Method Description:This method
		 * determines Preliminary Determination and the saves Adoption
		 * Assistance Eligibility Determination
		 * 
		 * @param aaeApplAndDetermDBDto
		 * @return Long @
		 */
	/*
	 * public Long determinePrelimDetermin(AaeApplAndDetermDBDto
	 * aaeApplAndDetermDBDto) ;
	 * 
	 *//**
		 * Method Name: calculatePrelimEligDeterm Method Description:This method
		 * returns preliminary eligibility determination value in form of
		 * AaeEligDetermMessgDto
		 * 
		 * @param aaeApplAndDetermDBDto
		 * @param aaeEligDetermMessgDto
		 * @return AaeEligDetermMessgDto @
		 */
	/*
	 * public AaeEligDetermMessgDto
	 * calculatePrelimEligDeterm(AaeApplAndDetermDBDto
	 * aaeApplAndDetermDBDto,AaeEligDetermMessgDto aaeEligDetermMessgDto) ;
	 * 
	 *//**
		 * Method Name: determineFinalDetermin Method Description:This method
		 * determines Final Determination and the saves AAE Determination and
		 * returns the determination message Dto
		 * 
		 * @param aaeApplAndDetermDBDto
		 * @return AaeEligDetermMessgDto @
		 */
	/*
	 * public AaeEligDetermMessgDto determineFinalDetermin(AaeApplAndDetermDBDto
	 * aaeApplAndDetermDBDto) ;
	 * 
	 *//**
		 * Method Name: calculateFinalEligDeterm Method Description:This method
		 * returns final eligibility determination value in form of
		 * AaeEligDetermMessgValueBean.This will perform the
		 * calculatePrelimEligDeterm eligibility determination and based in that
		 * will perform the final eligibility determination.
		 * 
		 * @param aaeApplAndDetermDBDto
		 * @return AaeEligDetermMessgDto @
		 */
	/*
	 * public AaeEligDetermMessgDto
	 * calculateFinalEligDeterm(AaeApplAndDetermDBDto aaeApplAndDetermDBDto) ;
	 * 
	 *//**
		 * Method Name: fetchFinalEligDetermOutcome Method Description:This
		 * method returns the Final Eligibility determination Messages in form
		 * of AaeEligDetermMessgValueBean. Is used for display only.
		 * 
		 * @param aaeApplAndDetermDBDto
		 * @return AaeEligDetermMessgDto @
		 */
	/*
	 * public AaeEligDetermMessgDto
	 * fetchFinalEligDetermOutcome(AaeApplAndDetermDBDto aaeApplAndDetermDBDto)
	 * ;
	 * 
	 *//**
		 * Method Name: validateSiblAppl Method Description: *Method
		 * Description:This method determines if the sibling selected is an
		 * applicable child or not.
		 * 
		 * @param idSiblingApplPerson
		 * @param idStage
		 * @return List<Long> @
		 */
	/*
	 * public List<Long> validateSiblAppl(Long idSiblingApplPerson, Long
	 * idStage) ;
	 * 
	 *//**
		 * Method Name: validateFinalEligDetem Method Description:This method
		 * checks for validation errors. 1. Checks if the Date of consummation
		 * exists 2. Active ADO placement exists 3. Sibling is placed in same
		 * resource as the child
		 * 
		 * @param aaeApplAndDetermDBDto
		 * @return List<Long> @
		 */
	/*
	 * public List<Long> validateFinalEligDetem(AaeApplAndDetermDBDto
	 * aaeApplAndDetermDBDto) ;
	 * 
	 *//**
		 * Method Name: completeEligDetermAssignedTodo Method Description:This
		 * method completes Adoption Assistance Eligibility Determination Todo
		 * if any
		 * 
		 * @param idEvent
		 * @return Long @
		 *//*
		 * public Long completeEligDetermAssignedTodo(Long idEvent) ;
		 */
}
