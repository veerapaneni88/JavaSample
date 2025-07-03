package us.tx.state.dfps.service.person.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.admin.dto.NameDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceDto;
import us.tx.state.dfps.service.common.request.AddressDtlReq;
import us.tx.state.dfps.service.common.request.CatCharReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.CriminalHistoryNarrReq;
import us.tx.state.dfps.service.common.request.CriminalHistoryReq;
import us.tx.state.dfps.service.common.request.DuplicatePersonsReq;
import us.tx.state.dfps.service.common.request.GetPersCharsDtlReq;
import us.tx.state.dfps.service.common.request.IsPersonReq;
import us.tx.state.dfps.service.common.request.NameHistoryDetailReq;
import us.tx.state.dfps.service.common.request.PersonCharacteristicsReq;
import us.tx.state.dfps.service.common.request.PersonCharsReq;
import us.tx.state.dfps.service.common.request.PersonDtlReq;
import us.tx.state.dfps.service.common.request.PersonMergeSplitReq;
import us.tx.state.dfps.service.common.request.PotentialDupReq;
import us.tx.state.dfps.service.common.request.PrsnSrchListpInitReq;
import us.tx.state.dfps.service.common.request.SaveNameHistoryDtlReq;
import us.tx.state.dfps.service.common.request.UpdtPersPotentialDupReq;
import us.tx.state.dfps.service.common.request.UpdtPersonDtlReq;
import us.tx.state.dfps.service.common.response.AddressDtlRes;
import us.tx.state.dfps.service.common.response.CatCharRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.DuplicatePersonsRes;
import us.tx.state.dfps.service.common.response.GetPersCharsDtlRes;
import us.tx.state.dfps.service.common.response.GetPersonCharsRes;
import us.tx.state.dfps.service.common.response.IsPersonRes;
import us.tx.state.dfps.service.common.response.NameHistoryDetailRes;
import us.tx.state.dfps.service.common.response.PersonCharacteristicsRes;
import us.tx.state.dfps.service.common.response.PersonCharsRes;
import us.tx.state.dfps.service.common.response.PersonDtlRes;
import us.tx.state.dfps.service.common.response.PersonExtRes;
import us.tx.state.dfps.service.common.response.PotentialDupRes;
import us.tx.state.dfps.service.common.response.PrsnSrchListpInitRes;
import us.tx.state.dfps.service.common.response.SaveNameHistoryRes;
import us.tx.state.dfps.service.common.response.UpdtPersPotentialDupRes;
import us.tx.state.dfps.service.common.response.UpdtPersonDtlRes;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.EducationHistoryDto;
import us.tx.state.dfps.service.person.dto.EducationalNeedDto;
import us.tx.state.dfps.service.person.dto.IncomeAndResourceDto;
import us.tx.state.dfps.service.person.dto.PersCharsDto;
import us.tx.state.dfps.service.person.dto.PersonCategoryDto;
import us.tx.state.dfps.service.person.dto.PersonEmailValueDto;
import us.tx.state.dfps.service.person.dto.PersonMergeSplitDto;
import us.tx.state.dfps.service.person.dto.SystemAccessDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCFC37S, CCFC38S
 * Class Description: This interface is use for retrieving Person Details and
 * Updating all column of PersonDtl table Mar 26, 2017 - 8:02:46 PM
 */
public interface PersonDtlService {

	/**
	 * 
	 * Method Name: getPersonDtl Method Description:This method is use to
	 * retrieve person detail by giving person_id or Stage_id and Stage Pers
	 * Role Tuxedo Service Name: CCFC37S
	 * 
	 * @param personDtlReq
	 * @return
	 */
	public PersonDtlRes getPersonDtl(PersonDtlReq personDtlReq);

	/**
	 * 
	 * Method Name: updatePersonDtl Method Description:This method is for
	 * updating all column in Person Dtl table Tuxedo Service Name: CCFC38S
	 * 
	 * @param updatePersonDtlReq
	 * @return
	 */
	public UpdtPersonDtlRes updatePersonDtl(UpdtPersonDtlReq updatePersonDtlReq);

	/**
	 * Method Description: This Method will retrieve character details for
	 * person details screen
	 * 
	 * @param persCharsDtlReq
	 * @return
	 */
	public GetPersCharsDtlRes getCharDtls(GetPersCharsDtlReq persCharsDtlReq);

	/**
	 * Method Description: This Method is to update person potential duplicate
	 * record
	 * 
	 * @param updtPersPotentialDupReq
	 * @return
	 */
	public UpdtPersPotentialDupRes savePersPotentialDupInfo(UpdtPersPotentialDupReq updtPersPotentialDupReq);

	/**
	 * 
	 * Method Description: This Method is used to retrieve the Extended person
	 * List.
	 * 
	 * @param personExtReq
	 */
	public PersonExtRes getExtPersonList(PersonDtlReq personExtReq);

	/**
	 * 
	 * Method Name: getDob Method Description:This Method is used to retrieve
	 * the date of Birth from Person ID
	 * 
	 * @param personDtlReq
	 * @return
	 */
	public PersonDtlRes getDob(PersonDtlReq personDtlReq);

	/**
	 * 
	 * Method Description: This Service calls a DAM to update Name information
	 ** about a person on the Name Table and a DAM to update NM PERSON FULL on
	 * the Person Table. Tuxedo Service Name:CINV25S
	 * 
	 * @param nameHistoryDetailReq
	 */
	NameHistoryDetailRes getNameHistoryDtl(NameHistoryDetailReq nameHistoryDetailReq);

	/**
	 * 
	 * Method Description:This Service calls a DAM to update Name information
	 ** about a person on the Name Table and a DAM to update NM PERSON FULL on
	 * the Person Table. Tuxedo Service Name:CINV26S
	 * 
	 * @param updatenameHistoryDtlReq
	 */

	SaveNameHistoryRes updateNameHistoryDtl(SaveNameHistoryDtlReq updatenameHistoryDtlReq);

	/**
	 * 
	 * Method Description:This service is responsible for adding or updating
	 * information from the Person Characteristics window. Tuxedo Service
	 * Name:CINV34S
	 * 
	 * @param personCharReq
	 */
	PersonCharacteristicsRes savePersonChar(PersonCharacteristicsReq personCharReq);

	/**
	 * 
	 * Method Description:To check for Criminal History Record Tuxedo Service
	 * Name:Person Helper (isCriminalHistoryComplete) Tuxedo DAM Name
	 * :(isCriminalHistoryComplete)
	 * 
	 * @param crimHistoryReq
	 */
	boolean isCriminalHistoryComplete(CriminalHistoryReq crimHistoryReq);

	/**
	 * 
	 * Method Description:To check for Criminal History Record Tuxedo Service
	 * Name:updateRecordCheckIndAccptRej Tuxedo DAM Name
	 * :(updateRecordCheckIndAccptRej)
	 * 
	 * @param crimHistoryReq
	 */

	String updateRecordCheckIndAccptRej(CriminalHistoryReq crimHistoryReq);

	/**
	 * 
	 * Method Description:To check for Criminal History Record Tuxedo Service
	 * Name:isCrimHistNarrPresentForRecordCheck Tuxedo DAM Name
	 * :(isCrimHistNarrPresentForRecordCheck)
	 * 
	 * @param crimHistoryReq
	 */
	boolean isCrimHistNarrPresentForRecordCheck(CriminalHistoryReq crimHistoryReq);

	/**
	 * 
	 * Method Description:To check for Criminal History Narritive Record Tuxedo
	 * Service Name:getCriminalHistNarr Tuxedo DAM Name :(getCriminalHistNarr)
	 * 
	 * @param criminalHistoryNarrReq
	 */
	boolean getCriminalHistNarr(CriminalHistoryNarrReq criminalHistoryNarrReq);

	String getPersonFullName(PersonDtlReq personDtlReq);

	/**
	 * Method Name: hasCurrentPrimaryAddress Method Description:
	 * 
	 * @param addressReq
	 * @return
	 */
	public AddressDtlRes hasCurrentPrimaryAddress(AddressDtlReq addressReq);

	/**
	 * Method Description:: This method retrieves a list of CriminalHistory ID's
	 * that have REJ Action status, for a give Person ID.
	 * 
	 * @param criminalHistoryNarrReq
	 * @return List
	 */
	public List<Long> getRejectCriminalHistList(CriminalHistoryNarrReq criminalHistoryNarrReq);

	/**
	 * 
	 * Method Name: validatePersonMerge Method Description: validate person
	 * merge details
	 * 
	 * @param personMergeSplitDto
	 * @return
	 */
	public PersonMergeSplitDto validatePersonMerge(PersonMergeSplitDto personMergeSplitDto);

	/**
	 * 
	 * Method Name: getPersonMergeInfo Method Description: get person merge info
	 * 
	 * @param personMergeSplitReq
	 * @return PersonMergeSplitRes
	 */
	public PersonMergeSplitDto getPersonMergeInfo(PersonMergeSplitReq personMergeSplitReq);

	/**
	 * 
	 * Method Name: getPersonAllegationsUpdatedInMerge Method Description: get
	 * person allegations info
	 * 
	 * @param idPersonMerge
	 * @param idForwardPerson
	 * @return PersonMergeSplitRes
	 * @throws SQLException
	 * 
	 *             public List<AllegationWithVicDto>
	 *             getPersonAllegationsUpdatedInMerge(Long idPersonMerge, Long
	 *             idForwardPerson, Long idClosedPerson)
	 */

	/**
	 * Method Description: This method will get the duplicate persons meeting
	 * the criteria. Service Name: Duplicate Alert on Person Detail Screen.
	 * 
	 * @param DuplicatePersonsReq
	 * @return DuplicatePersonsRes
	 */
	DuplicatePersonsRes getDuplicates(DuplicatePersonsReq duplicatePersonsReq);

	CommonHelperRes saveIndAbuseNglctDeathInCare(CommonHelperReq personIndAbuse);

	CommonHelperRes getIndAbuseNglctDeathInCare(CommonHelperReq person);

	PrsnSrchListpInitRes populateAddtnlInfoIntakeSearch(PrsnSrchListpInitReq personSearchList);

	/**
	 * Method Name: isPersonInfoViewable Method Description: Check if the
	 * Person's Information is Viewable or Not
	 * 
	 * @param isPersonReq
	 * @return IsPersonRes
	 */
	public IsPersonRes isPersonInfoViewable(IsPersonReq isPersonReq) throws DataNotFoundException;

	/**
	 * Method Name: getPersonDetail Method Description: To get personDetail from
	 * PersonId
	 * 
	 * @param isPersonReq
	 * @return IsPersonRes
	 */
	public PersonDto getPersonDetail(CommonHelperReq personReq) throws DataNotFoundException;

	/**
	 * 
	 * Method Name: fetchPersonCharDetails Method Description: Provides the list
	 * of characteristics for a person along with person details
	 * 
	 * @param personCharsReq
	 * @return PersonCharsRes
	 */
	public PersonCharsRes fetchPersonCharDetails(PersonCharsReq personCharsReq);

	/**
	 * 
	 * Method Name: fetchPersonChar Method Description: Retrieve characteristics
	 * for given person ID, in given category, with status indicators and dates.
	 * 
	 * @param personCharsReq
	 * @return GetPersonCharsRes
	 */
	public GetPersonCharsRes fetchPersonChar(PersonCharsReq personCharsReq);

	/**
	 * Method Name: getPersonPca Method Description: Get the name of the person
	 * 
	 * @param PersonDtlRes
	 * @return PersonReq
	 */

	public PersonDtlRes getPersonPca(Long idPerson);

	/**
	 * Method Name: getPersonAllegationsUpdatedInMerge Method Description:This
	 * method fetches the allegations modified for forward person in a person
	 * merge.
	 * 
	 * @param idPersonMerge
	 * @param idForwardPerson
	 * @param idClosedPerson
	 * @return List<PersonAllegationUpdateDto>
	 *//*
		 * 
		 * @Transactional(isolation = Isolation.READ_COMMITTED, readOnly =
		 * false, propagation = Propagation.REQUIRED) public
		 * List<AllegationWithVicDto> getPersonAllegationsUpdatedInMerge(Long
		 * idPersonMerge, Long idForwardPerson, Long idClosedPerson);
		 */
	/**
	 * Method Name: fetchPersonPotentialDuplicates Method Description: Returns
	 * list of person Potential Duplicates.
	 * 
	 * @param PotentialDupReq
	 * @return PotentialDupListRes
	 */
	public PotentialDupRes fetchPersonPotentialDuplicates(PotentialDupReq potentialDupListReq);

	/**
	 * Method Name: fetchPersonPotentialDuplicate Method Description: Returns
	 * Person Potential Duplicate detail given Person Potential Duplicate ID
	 * 
	 * @param PotentialDupReq
	 * @return PotentialDupListRes
	 */
	public PotentialDupRes fetchPersonPotentialDuplicate(PotentialDupReq potentialDupReq);

	/**
	 * Method Name: fetchActivePersonPotentialDuplicate Method Description:
	 * Retrieve active potential Duplicate and other information related to a
	 * person.
	 * 
	 * @param personActiveDupReq
	 * @return PersonActiveDupRes
	 */
	public PotentialDupRes fetchActivePersonPotentialDuplicate(PotentialDupReq personActiveDupReq);

	/**
	 * Method Name: getForwardPersonInMerge Method Description:This method
	 * checks if the person is closed person in a merge
	 * 
	 * @param idClosedPerson
	 * @return Long
	 */
	public Long getForwardPersonInMerge(Long idClosedPerson);

	/**
	 * Method Name: isPersonIRReport Method Description:This method checks if
	 * the person is in IR Report
	 * 
	 * @param idPerson
	 * @return Boolean
	 */
	public Boolean isPersonIRReport(Long idPerson);

	/**
	 * 
	 * Method Description:This is the form service for the Extended Person List
	 * form. Tuxedo Service Name: CPER02S
	 * 
	 * @param personDtlReq
	 */
	public PreFillDataServiceDto getExtendedPersonDtl(PersonDtlReq personDtlReq);

	/**
	 * 
	 * Method Name: fetchCatChar Method Description:Provides the list of all
	 * currently valid characteristics for a given category, plus indicators for
	 * AFCARS and diagnosability.
	 * 
	 * @param catCharReq
	 * @return CatCharRes
	 */
	public CatCharRes fetchCatChar(CatCharReq catCharReq);

	/**
	 * Method Name: getPersonCharList Method Description:Get Person
	 * Characteristics for input person id and Category Type
	 * 
	 * @param idPerson
	 * @param cdCharCategory
	 * @return List<PersCharsDto>
	 */
	public List<PersCharsDto> getPersonCharList(long idPerson, String cdCharCategory);

	/**
	 * Method Name: getPersonCharList Method Description:Retrieve
	 * characteristics for given person ID, in given category from snapshot
	 * table (SS_CHARACTERISTICS)
	 * 
	 * @param idPerson
	 * @param cdCharCategory
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @return List<PersCharsDto>
	 */
	public List<PersCharsDto> getPersonCharList(long idPerson, String cdCharCategory, long idReferenceData,
			String cdActionType, String cdSnapshotType);

	/**
	 * 
	 * Method Name: selectAllRelationsOfPerson Method Description:Reads the
	 * person Family Tree relationships from snapshot table (SS_PERSON_RELATION)
	 * 
	 * (For example: This method is used for displaying the Select Forward
	 * person details in post person merge page)
	 * 
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @param idPerson
	 * @param idReferenceData
	 * @return
	 */
	public List<FTPersonRelationDto> selectAllRelationsOfPerson(String cdActionType, String cdSnapshotType,
			Long idPerson, Long idReferenceData);

	/**
	 * Method Name: selectAllRelationsOfPerson Method Description:Get All Family
	 * Tree RelationShip for person
	 * 
	 * @param idPerson
	 * @return List<FTPersonRelationDto>
	 */
	public List<FTPersonRelationDto> selectAllRelationsOfPerson(Long idPerson);

	/**
	 * Method Name: getPersonPrimaryEmail Method Description:Fetches the primary
	 * email for the person
	 * 
	 * @param idPerson
	 * @return PersonEmailValueDto
	 * @throws DataNotFoundException
	 */
	public PersonEmailValueDto getPersonPrimaryEmail(Long idPerson);

	/**
	 * Method Name: fetchPersonRaceList Method Description: Get Person Race for
	 * input person id.
	 * 
	 * @param personRaceReq
	 * @return PersonRaceRes
	 * @throws DataNotFoundException
	 */
	public ArrayList<PersonRaceDto> fetchPersonRaceList(long idPerson);

	/**
	 * 
	 * Method Name: getPersonPrimaryEmail Method Description: Reads the current
	 * primary email for a person from snapshot table (SS_PERSON_EMAIL) ( For
	 * example: This method is used for displaying the Select Forward person
	 * details in post person merge page)
	 * 
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @param idPerson
	 * @param idReferenceData
	 * @return
	 */
	public PersonEmailValueDto getPersonPrimaryEmail(String cdActionType, String cdSnapshotType, Long idPerson,
			Long idReferenceData);

	/**
	 * Method Name: getLatestActiveIncmRsrcStartDate Method Description: get
	 * Latest(Date) Active Income & Resource for person id.
	 * 
	 * @param idPerson
	 * @return Date
	 */
	public Date getLatestActiveIncmRsrcStartDate(Long idPerson);

	/**
	 * Method Name: getCurrentEducationHistoryById Method Description: This
	 * method gets current Education for input person id
	 * 
	 * @param idPerson
	 * @return EducationHistoryDto
	 */
	public EducationHistoryDto getCurrentEducationHistoryById(Long idPerson);

	/**
	 * 
	 * Method Name: getCurrentEducationHistory Method Description:Fetches the
	 * person current educational history from snapshot table
	 * (SS_EDUCATIONAL_HISTORY) ( For example: This method is used for
	 * displaying the Select Forward person details in post person merge page)
	 * 
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @param idPerson
	 * @param idReferenceData
	 * @return
	 */
	public EducationHistoryDto getCurrentEducationHistory(String cdActionType, String cdSnapshotType, Long idPerson,
			Long idReferenceData);

	/**
	 * Method Name: getEducationalNeedListForHist Method Description:Fetches the
	 * Get Education Need for a Education History Record from snapshot table
	 * (SS_EDUCATIONAL_NEED)
	 * 
	 * @param idPerson
	 * @param idEduHist
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @return List<EducationalNeedDto>
	 */
	public List<EducationalNeedDto> getEducationalNeedListForHist(Long idPerson, Long idEduHist, Long idReferenceData,
			String cdActionType, String cdSnapshotType);

	/**
	 * Method Name: getEducationalNeedListForHist Method Description: This
	 * method fetches the education need records for a education history record.
	 * 
	 * @param idEduHist
	 * @return ArrayList<EducationalNeedDto>
	 */
	public ArrayList<EducationalNeedDto> getEducationalNeedListForHist(Long idEduHist);

	/**
	 * 
	 * Method Name: getPersonCategoryList Method Description: Get Person
	 * Category List for input person
	 * 
	 * @param personId
	 * @return PersonCategoryDto
	 */
	public List<PersonCategoryDto> getPersonCategoryList(Long personId);

	/**
	 * Method Name: isPrimaryChildInOpenStage Method Description: Checks if the
	 * person is a Primary Child (PC) in an open stage for list of stages
	 * 
	 * @param idPerson
	 * @param chkStages
	 * @return boolean
	 */
	public boolean isPrimaryChildInOpenStage(int idPerson, String[] chkStages);

	/**
	 * Method Name: fetchActivePrimaryName Method Description:Fetches the active
	 * primary name for a person from NAME table
	 * 
	 * @param idPerson
	 * @return NameDto ;
	 * @throws ParseException
	 */
	public NameDto fetchActivePrimaryName(Long idPerson);

	/**
	 * 
	 * Method Name: fetchActivePrimaryName Method Description:Reads the active
	 * primary name for the person from snapshot. ( For example: This method is
	 * used for displaying the Select Forward person details in post person
	 * merge page)
	 * 
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @param idPerson
	 * @param idReferenceData
	 * @return
	 */
	public NameDto fetchActivePrimaryName(String cdActionType, String cdSnapshotType, Long idPerson,
			Long idReferenceData);

	/**
	 * Method Name: getIncomeAndResourceList Method Description: Get Income and
	 * Resources for input person id.
	 * 
	 * @param idPerson
	 * @param sortBy
	 * @param activeFlag
	 * @return List<IncomeAndResourceDto>
	 */
	public ArrayList<IncomeAndResourceDto> getIncomeAndResourceList(Long idPerson, String sortBy, boolean activeFlag);

	/**
	 * 
	 * Method Name: getIncomeAndResourceList Method Description:Fetches the
	 * person income and resources list from snapshot table
	 * (SS_INCOME_AND_RESOURCES) ( For example: This method is used for
	 * displaying the Select Forward person details in post person merge page)
	 * 
	 * @param activeFlag
	 * @param idPerson
	 * @param sortBy
	 * @param idReferenceData
	 * @param cdSnapshotType
	 * @param cdActionType
	 * @return
	 */
	public ArrayList<IncomeAndResourceDto> getIncomeAndResourceList(Long idPerson, String sortBy, Boolean activeFlag,
			Long idReferenceData, String cdActionType, String cdSnapshotType);

	/**
	 * Method Name: getStaffLegalName Method Description:This method retrieves
	 * staff Legal Name using id_person
	 * 
	 * @param idPerson
	 * @return nameDto
	 */
	public us.tx.state.dfps.service.contact.dto.NameDto getStaffLegalName(Long idPerson);

	/**
	 * Method Name: getExtPersonRoles Method Description:Get the external person
	 * roles for a Person with External Application access
	 * 
	 * @param idPerson
	 * @return String
	 */
	public String getExtPersonRoles(Long idPerson);

	/**
	 * Method Name: getSystemAccessList Method Description: Get the application
	 * access list for external user types
	 * 
	 * @param idPerson
	 * @return List<SystemAccessValueDto>
	 */
	public List<SystemAccessDto> getSystemAccessList(Long idPerson);

	/**
	 * Method Name: getStaffAddress Method Description:This method retrieves
	 * staff mail code address using cd_mail_code
	 * 
	 * @param mailCode
	 * @return String
	 */
	public String getStaffAddress(String mailCode);

	/**
	 * Method Name: saveSystemAccess Method Description: This method saves the
	 * application access for external user types
	 * 
	 * @param systemAccessDto
	 */
	public void saveSystemAccess(SystemAccessDto systemAccessDto, String cdReqFunction);
	
	/**
	 * Method written for defect 6503 artf81655
	 * this method will return full name based on person id 
	 * @param idPerson
	 * @return
	 */
	public String getPersonFullName(Long idPerson);

  /**
   * method to get Child Citizenship by IdStage and relationship type
   *
   * @param idStage
   * @param persRelInt
   * @return Citizenship status value
   */
  String getPersonCitizenshipByStageIdAndRelType(long idStage, String persRelInt);

  /**
   * method to get Oldest victim person id by IdStage and relationship type('OV')
   *
   * @param idStage
   * @return PersonDto
   */
  PersonDto getOldestVictimPersonByStageIdAndRelType(long idStage);
}
