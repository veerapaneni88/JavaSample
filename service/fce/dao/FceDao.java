package us.tx.state.dfps.service.fce.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.tx.state.dfps.common.domain.FceApplication;
import us.tx.state.dfps.common.domain.FcePerson;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.fce.EligibilityDto;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceDomicilePersonWelfDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.dto.FcePersonDto;
import us.tx.state.dfps.service.fce.dto.FceReasonNotEligibleDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

public interface FceDao {

	public FceApplicationDto getFceApplicationById(Long idFceApplication);

	public FceApplication getFceApplication(Long idFceApplication);

	public String deleteFceApplication(Long idFceApplication);

	public FceApplication save(FceApplication fceApplication);

	public FceApplicationDto findApplicationByApplicationEvent(Long idEvent);

	public void updateFceApplication(FceApplicationDto fceApplicationDto);

	public Long createFcEligibility(Long idPerson, Long idCase, Long idLastUpdatePerson, Long idStage);

	public FceEligibilityDto getFceEligibility(Long idFceEligibility);

	public Long createFceEligibility(Long idCase, Long idLastUpdatePerson, Long idStage, Long idPerson);

	public FceEligibilityDto createFceEligibility(Long idCase, Long idEvent, Long idLastUpdatePerson, Long idStage,
			Long idPerson, boolean forFceReview);

	public FceApplicationDto createFcApplication(String cdApplication, Long idCase, Long idEvent,
			Long idLastUpdatePerson, Long idStage, Long idPerson, String indEligible);

	public EligibilityDto findLegacyEligibility(Long idEligibilityEvent);

	public Double findSsi(Long idPerson);

	public EligibilityDto findLatestLegacyEligibility(Long idCase, Long idPerson);

	public void updateBirthday(FcePersonDto fcePersonDto, PersonDto personDto);

	public CommonHelperRes saveEligibility(FceEligibilityDto fceEligibilityDto, boolean isDeletefceReasonNotEligibles);

	public void deleteFceReasonsNotEligible(Long idFceEligibility);

	public void createFceReasonsNotEligible(List<String> reasonsNotEligibleList, Long idFceEligibility);

	public void updateFceReasonNotEligibles(Long idFceEligibility);

	public void updateFceEligiblility(FceEligibilityDto fceEligibilityDto);

	public List<FceReasonNotEligibleDto> findReasonsNotEligible(Long idFceEligibility);

	public CommonHelperRes updateFcEligibilityAndApp(FceApplicationDto fceApplicationDto,
			FceEligibilityDto fceEligibilityDto);

	public void updateFceDomicilePersonWelf(FceDomicilePersonWelfDto fceDomicilePersonWelfDto);

	public void deleteFceDomicilePersonWelf(FceDomicilePersonWelfDto fceDomicilePersonWelfDto);

	public List<FceDomicilePersonWelfDto> getFceDomicilePersonWelf(FceApplicationDto fceApplicationDto);

	/**
	 * 
	 * Method Name: createFceApplication Method Description:
	 * 
	 * @param fceApplicationDto
	 * @return fceApplication @
	 */
	public Long createFceApplication(FceApplicationDto fceApplicationDto);

	/**
	 * 
	 * Method Name: getCdPersonCitizenship Method Description:
	 * 
	 * @param idPerson
	 * @return CdPersonCitizenship @
	 */
	public String getCdPersonCitizenship(Long idPerson);

	/**
	 * 
	 * Method Name: deleteNonPrinciples Method Description:
	 * 
	 * @param idFceEligibility
	 * @param idStage
	 * @
	 */
	public void deleteNonPrinciples(Long idFceEligibility, Long idStage);

	/**
	 * 
	 * Method Name: createPrinciples Method Description:
	 * 
	 * @param idFceEligibility
	 * @param idStage
	 * @
	 */
	public void createPrinciples(Long idFceEligibility, Long idStage);

	/**
	 * 
	 * Method Name: createIncomeForFcePersons Method Description:
	 * 
	 * @param idFceEligibility
	 * @param idFcePerson
	 * @param idPerson
	 * @
	 */
	public void createIncomeForFcePersons(Long idFceEligibility, Long idFcePerson, Long idPerson);

	/**
	 * 
	 * Method Name: findFcePerson Method Description:
	 * 
	 * @param idFcePerson
	 * @return fcePerson @
	 */
	public FcePersonDto findFcePerson(Long idFcePerson);

	/**
	 * 
	 * Method Name: createBlankRecordsForFcePersons Method Description:
	 * 
	 * @param idFceEligibility
	 * @param childIdFcePerson
	 * @param childIdPerson
	 * @param incomeOrResource
	 * @return @
	 */
	public Long createBlankRecordsForFcePersons(Long idFceEligibility, Long childIdFcePerson, Long childIdPerson,
			boolean incomeOrResource, List<FcePerson> personList);

	/**
	 * 
	 * Method Name: copyIncomeResources Method Description:
	 * 
	 * @param personMap
	 * @param idFceEligibility
	 * @param childIdFcePerson
	 * @return @
	 */
	Long updateIncomeResources(Map<Long, Long> personMap, Long idFceEligibility, Long childIdFcePerson,
			Map<String, Set<Long>> IncomeAndResource);

	/**
	 * 
	 * Method Name: getIndEvaluationConclusion Method Description:
	 * 
	 * @param idFceApplication
	 * @return @
	 */
	public String getIndEvaluationConclusion(Long idFceApplication);

	/**
	 * 
	 * Method Name: createSemaphore Method Description:
	 * 
	 * @param idApplicationEvent
	 * @param fceApplicationTable
	 * @param fceApplicationColumn
	 * @return
	 */
	public Long createSemaphore(Long idApplicationEvent, String fceApplicationTable, String fceApplicationColumn);

	/**
	 * 
	 * Method Name: hasDOBChangedForCertPers Method Description:
	 * 
	 * @param idFceEligibility
	 * @return hasDOBChanged @
	 */
	public boolean hasDOBChangedForCertPers(Long idFceEligibility);

	/**
	 * Method Name: findLatestEligibilityForEligibilityEvent Method Description:
	 * 
	 * @param idEligibilityEvent
	 * @return
	 */
	public FceEligibilityDto findLatestEligibilityForEligibilityEvent(Long idEligibilityEvent);

	/**
	 * 
	 * Method Name: countNewEvents Method Description:
	 * 
	 * @param fceEligibilityTaskCode
	 * @param idStage
	 * @return idEvent @
	 */
	public Long countNewEvents(String fceEligibilityTaskCode, Long idStage);

	/**
	 * 
	 * Method Name: deleteEvent Method Description:
	 * 
	 * @param newEligibilityEvent
	 * @
	 */
	public void deleteEvent(Long newEligibilityEvent);

	/**
	 * 
	 * Method Name: countIncompleteEvents Method Description:
	 * 
	 * @param fceEligibilityTaskCode
	 * @param idStage
	 * @return count @
	 */
	public int countIncompleteEvents(String fceEligibilityTaskCode, Long idStage);

	public FceEligibilityDto findLatestEligibilityForStage(Long idStage);

	public FceEligibilityDto copyEligibility(FceEligibilityDto lastfceEligibilityDto, Long idLastUpdatePerson,
			boolean copyReasonsNotEligible);

	/**
	 * 
	 * Method Name: verifyOpenStage Method Description:
	 * 
	 * @param idStage
	 * @return IdStage @
	 */
	public Long verifyOpenStage(Long idStage);

	public void deleteFceDepCareDeductAdultPerson(Long idFceEligibility);

	public void deleteFceDepCareDeductDependentPerson(Long idFceEligibility);

	public void deleteFcePerson(Long idFceEligibility);

	public void deleteFceReasonNotEligible(Long idFceEligibility);

	public void deleteFceEligibility(Long idFceEligibility);

	FcePersonDto findFcePersonByPrimaryKey(long idFcePerson);

	public Boolean isAutoEligibility(Long idEvent);

	public void deleteFceApplicationForEligibility(Long idEligibilityEvent);

	public List<Long> findEligEventIdByPersonId(Long idPerson);

	public List<Long> saveEligFirstValidation(Long idPerson, Date startDate, Date endDate);

	public List<Long> saveEligSecondValidation(Long idPerson, Date startDate, Date endDate);

	public List<Long> saveEligThirdValidation(Long idPerson, Date startDate, Date endDate);

	public EligibilityDto saveEligFourthValidation(Long idPerson, Date startDate, Date endDate);

	public EligibilityDto saveEligFifthValidation(Long idPerson, Date startDate, Date endDate);

	public EligibilityDto determineEligWasCourtOrd(Long idPerson, Long idEligEvent);

	public List<Long> saveEligSeventhValidation(Long idPerson, Date startDate, Date endDate);

	public Long saveElig(EligibilityDto eligibilityDto);

	public EligibilityDto findEligEventIdByIdEligEvent(Long idEligEvent, Long idPerson, Date startDate, Date endDate,
			Date dtLastUpdate);

	public List<Long> updateEligFirstValidation(Long idEligibilityEvent, Long idPerson, Date startDate,
			Date dtCurrentPlocStart);

	public List<Long> updateEligSecondValidation(Long idEligibilityEvent, Long idPerson, Date endDate,
			Date dtCurrentPlocEnd);

	public EligibilityDto updateEligThirdValidation(Long idPerson, Date startDate, Date dtCurrentPlocStart);

	public EligibilityDto updateEligFourthValidation(Long idPerson, Date endDate, Date dtCurrentPlocEnd);

	public void updateElig(EligibilityDto eligibilityDto);

	public FceEligibilityDto copyFceReviewEligibility(FceEligibilityDto lastfceEligibilityDto, Long idLastUpdatePerson,
			boolean copyReasonsNotEligible);

	FceApplicationDto transformFceApplication(FceApplication fceApplication);
	// List<PersonListDto> getPersonListByStage(Long idStage);

	public List<EligibilityDto> findEligByidStage(Long idStage);

}
