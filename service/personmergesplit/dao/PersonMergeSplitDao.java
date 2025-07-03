package us.tx.state.dfps.service.personmergesplit.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.person.dto.MergeSplitVldMsgDto;
import us.tx.state.dfps.service.person.dto.PersonMergeSplitDto;
import us.tx.state.dfps.service.person.dto.PersonPotentialDupDto;
import us.tx.state.dfps.service.personmergesplit.dto.CaseValueDto;
import us.tx.state.dfps.service.personmergesplit.dto.PersonMergeSplitValueDto;
import us.tx.state.dfps.service.personmergesplit.dto.PersonMergeUpdateLogDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO level
 * class for Person Merge Split> May 30, 2018- 3:50:06 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PersonMergeSplitDao {

	/**updatePersonPotentialDupInfo
	 * 
	 * Method Name: getPRTForSplit Method Description: get PRT for split
	 * 
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return List<Long>
	 */
	public List<Long> getPRTForSplit(Long idPersMerge, Long idForwardPerson);

	/**
	 * 
	 * Method Name: updatePersonOnPrtForSplit Method Description: update person
	 * PRT for split
	 * 
	 * @param idClosedPerson
	 * @param idPrtPersLink
	 * @param idForwardPerson
	 * @return
	 */
	public void updatePersonOnPrtForSplit(Long idClosedPerson, Long idPrtPersLink, Long idForwardPerson);

	/**
	 * 
	 * Method Name: getPRTConnectionForSplit Method Description: get PRT
	 * Connection for split
	 * 
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return
	 */
	public List<Long> getPRTConnectionForSplit(Long idPersMerge, Long idForwardPerson);

	/**
	 * 
	 * Method Name: updatePRTConnectionOnSplit Method Description: update PRT
	 * Connection on Split
	 * 
	 * @param idClosedPerson
	 * @param idPrtConn
	 * @param idForwardPerson
	 * @return
	 */
	public void updatePRTConnectionOnSplit(Long idClosedPerson, Long idPrtConn, Long idForwardPerson);

	/**
	 * 
	 * Method Name: getPersonMergeInfo Method Description: get Person Merge Info
	 * 
	 * @param idPersonMerge
	 * @return
	 */
	public PersonMergeSplitValueDto getPersonMergeInfo(Long idPersonMerge);

	/**
	 * 
	 * Method Name: getPersonMergeListForForward Method Description:Fetches the
	 * Person Merge records where person exist as forward
	 * 
	 * @param idForwardPerson
	 * @param considerInvalidAlso
	 * @return
	 */
	public List<PersonMerge> getPersonMergeListForForward(long idForwardPerson, boolean considerInvalidAlso);

	/**
	 * 
	 * Method Name: updatePersonMerge Method Description: update Person Merge
	 * 
	 * @param personMerge
	 * @return
	 */
	public void updatePersonMerge(PersonMerge personMerge);

	/**
	 * 
	 * Method Name: getStagesForPersonMergeView Method Description: get stages
	 * for Person Merge View
	 * 
	 * @param idPersonMerge
	 * @return
	 */
	public List<StagePersonValueDto> getStagesForPersonMergeView(long idPersonMerge);

	/**
	 * 
	 * Method Name: deleteNonEmpPersonCategories Method Description: delete non
	 * employee person categories
	 * 
	 * @param idPersonMerge
	 * @return
	 */
	public void deleteNonEmpPersonCategories(long idPersonMerge);

	/**
	 * 
	 * Method Name: savePersonCategory Method Description: save person category
	 * 
	 * @param idPersonMerge
	 * @param cpsnDtctString
	 * @return
	 */
	public void savePersonCategory(long idPersonMerge, String cpsnDtctString);

	/**
	 * 
	 * Method Name: getPcspPrsnLinkForSplit Method Description: get PCSP Person
	 * Link For split
	 * 
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return
	 */
	public List<Integer> getPcspPrsnLinkForSplit(long idPersMerge, long idForwardPerson);

	/**
	 * Method Name: getPcspPrsnLink Method Description: gets the PCSP person link by person
	 * @param idPerson
	 * @return
	 */
	public List<PcspPrsnLink> getPcspPrsnLink(long idPerson);

	/**
	 * 
	 * Method Name: updatePcspPrsnLinkForSplit Method Description: update PCSP
	 * Person Link For Split
	 * 
	 * @param idClosedPerson
	 * @param idPcspPersLink
	 * @param idForwardPerson
	 * @return
	 */
	public void updatePcspPrsnLink(long idClosedPerson, long idPcspPersLink, long idForwardPerson, boolean isMerge);

	/**
	 * 
	 * Method Name: getPcspAsmntForSplit Method Description: Get PCSP Asmnt for
	 * Split
	 * 
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return
	 */
	public List<Integer> getPcspAsmntForSplit(long idPersMerge, long idForwardPerson);

	/**
	 * Method Name: getPcspAsmntByCaregiver Method Description: gets PCSP assessments for the caregiver
	 * @param idPerson
	 * @return
	 */
	public List<PcspAsmnt> getPcspAsmntByCaregiver(long idPerson);

	/**
	 * 
	 * Method Name: updatePcspAsmntForSplit Method Description: Update PCSP
	 * Asmnt for split
	 * 
	 * @param idClosedPerson
	 * @param idPcspAssessment
	 * @param idForwardPerson
	 * @return
	 */
	public void updatePcspAsmntForSplit(long idClosedPerson, long idPcspAssessment, long idForwardPerson);

	/**
	 * 
	 * Method Name: getPcspPlcmntForSplit Method Description: Get PCSP Plcmnt
	 * For Split
	 * 
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return
	 */
	public List<Integer> getPcspPlcmntForSplit(long idPersMerge, long idForwardPerson);

	/**
	 * Method Name:getPcspPlcmntForMerge Method Description: gets PCSP Placements by person id
	 * @param idPerson
	 * @return
	 */
	public List<Integer> getPcspPlcmntForMerge(long idPerson);

	/**
	 * 
	 * Method Name: updatePcspPlcmntForSplit Method Description: Update PCSP
	 * Placement for split
	 * 
	 * @param idClosedPerson
	 * @param idPcspPlacement
	 * @param idForwardPerson
	 * @return
	 */
	public void updatePcspPlcmntForSplit(long idClosedPerson, long idPcspPlacement, long idForwardPerson);

	/**
	 * Method Name: updatePcspPlcmntForSplit Method Description: Update PCSP Placement for merge
	 *
	 * @param idForwardPerson
	 * @param idPcspPlacement
	 * @param idClosedPerson
	 */
	public void updatePcspPlcmntForMerge(long idForwardPerson, long idPcspPlacement, long idClosedPerson);

	/**
	 * 
	 * Method Name: getLegacyChildForSplit Method Description: get legacy child
	 * detailf for split
	 * 
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return
	 */
	public List<Long> getLegacyChildForSplit(long idPersMerge, long idForwardPerson);

	/**
	 * 
	 * Method Name: updateChildSafetyPlcmtForSplit Method Description: Update
	 * Chlid's safety placement for split
	 * 
	 * @param idClosedPerson
	 * @param idLegacyPCSP
	 * @param idForwardPerson
	 * @return
	 */
	public void updateChildSafetyPlcmtForSplit(long idClosedPerson, long idLegacyPCSP, long idForwardPerson);

	/**
	 * 
	 * Method Name: getLegacyCaregiverForSplit Method Description: get legacy
	 * caregiver for split
	 * 
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return
	 */
	public List<Long> getLegacyCaregiverForSplit(long idPersMerge, long idForwardPerson);

	/**
	 * 
	 * Method Name: updateCaregiverSafetyPlcmtForSplit Method Description:
	 * update caregiver safety placement for split
	 * 
	 * @param idClosedPerson
	 * @param idPcspCaregiver
	 * @param idForwardPerson
	 * @return
	 */
	public void updateCaregiverSafetyPlcmtForSplit(long idClosedPerson, long idPcspCaregiver, long idForwardPerson);

	/**
	 * Updates caregiver on the PCSP assessment
	 * @param idForwardPerson
	 * @param idPcspAsmnt
	 * @param idClosedPerson
	 */
	public void updateCaregiverOnPCSPAsmnt(long idForwardPerson, long idPcspAsmnt,long idClosedPerson);

	/**
	 * 
	 * Method Name: savePersonMerge Method Description: save person merge
	 * details
	 * 
	 * @param personMergeSplitDB
	 * @return
	 */
	public long savePersonMerge(PersonMergeSplitDto personMergeSplitDB);

	/**
	 * 
	 * Method Name: getSnapshotTableList Method Description: Get Snapshot Table
	 * list details
	 * 
	 * @param cacnType
	 * @return
	 */
	public ArrayList<SnapshotTblList> getSnapshotTableList(String cacnType);

	/**
	 * 
	 * Method Name: savePersonMergeSnapshot Method Description: save person
	 * merge to snapshot table
	 * 
	 * @param snapshotHeader
	 * @return
	 */
	public void savePersonMergeSnapshot(SnapshotHeader snapshotHeader);

	/**
	 * 
	 * Method Name: savePersonMergeSnapshotDtl Method Description: save person
	 * merge snapshot dtl
	 * 
	 * @param snapshotDtl
	 * @return
	 */
	public void savePersonMergeSnapshotDtl(SnapshotDtl snapshotDtl);

	/**
	 * 
	 * Method Name: getAffectedStagesStaff Method Description: Get affected
	 * stage staff details
	 * 
	 * @param idForwardPerson
	 * @param idClosedPerson
	 * @param idMergePerson
	 * @return
	 */
	public ArrayList<StagePersonLink> getAffectedStagesStaff(long idForwardPerson, long idClosedPerson,
			long idMergePerson);

	/**
	 * 
	 * Method Name: savePersonMergeSplitValidationLog Method Description: save
	 * person merge split validation log
	 * 
	 * @param personMergeSplitDB
	 * @param msgNumber
	 * @param errorMessage
	 * @param cerrType
	 * @param cactnType
	 * @param personMergeForwardStep
	 * @return
	 */
	public void savePersonMergeSplitValidationLog(PersonMergeSplitDto personMergeSplitDB, int msgNumber,
			String errorMessage, String cerrType, String cactnType, int personMergeForwardStep);

	/**
	 * 
	 * Method Name: savePersonMergeSelectField Method Description:his method
	 * insert a row into PERSON_MERGE_SELECT_FIELD. This table contains the
	 * fields and their source (Forward/Closed) selected during Select Forward
	 * Person
	 * 
	 * @param personMergeSelectField
	 * @return
	 */
	public void savePersonMergeSelectField(PersonMergeSelectField personMergeSelectField);

	/**
	 * 
	 * Method Name: savePersonMergeUpdateLog Method Description: save person
	 * merge update log
	 * 
	 * @param idPersonMerge
	 * @param category
	 * @param idPersonMergeWorker
	 * @return
	 */
	public void savePersonMergeUpdateLog(int idPersonMerge, String category, int idPersonMergeWorker);

	/**
	 * 
	 * Method Name: getActivePersonPotentialDupDetail Method Description: get
	 * active person potential duplicate detail
	 * 
	 * @param idClosedPerson
	 * @param idForwardPerson
	 * @return
	 */
	public PersonPotentialDupDto getActivePersonPotentialDupDetail(int idClosedPerson, int idFwdPerson);

	/**
	 * 
	 * Method Name: updatePersonPotentialDupInfo Method Description: Update
	 * Person potential duplicate info
	 * 
	 * @param personPotentialDup
	 * @return
	 */
	public void updatePersonPotentialDupInfo(PersonPotentialDupDto personPotentialDupDto);
	
	/**
	 * 
	 * Method Name: updatePersonPotentialDupRec Method Description: Update
	 * Person potential duplicate record
	 * 
	 * @param personPotentialDup
	 * @return
	 */
	public void updatePersonPotentialDupRec(PersonPotentialDup personPotentialDup);

	/**
	 * 
	 * Method Name: getPersonPotentialDupList Method Description: Get person
	 * potential duplicate list
	 * 
	 * @param idClosedPerson
	 * @return
	 */
	public List<PersonPotentialDup> getPersonPotentialDupList(Long idClosedPerson);

	/**
	 * 
	 * Method Name: updatePersonOnIncomingPersonMPS Method Description: update
	 * person incoming person mps
	 * 
	 * @param fwdPersonId
	 * @param closedPersonId
	 * @return
	 */
	public void updatePersonOnIncomingPersonMPS(Long fwdPersonId, Long closedPersonId);

	/**
	 * 
	 * Method Name: insertMedicaidUpdate Method Description: insert medicaid
	 * update detail to database
	 * 
	 * @param idPerson
	 * @param idStage
	 * @param idCase
	 * @param idEvent
	 * @param cdMedUpdType
	 * @param cdTranType
	 * @return
	 */
	public void insertMedicaidUpdate(Long idPerson, Long idStage, Long idCase, Long idEvent, String cdMedUpdType,
			String cdTranType);

	/**
	 * 
	 * Method Name: updatePersonClosedWithForward Method Description:
	 * 
	 * @param intValue
	 * @param cdEventType
	 * @param idEvent
	 * @param idStage
	 * @param txtEventDescr
	 * @param idForwardPerson
	 * @param idClosedPerson
	 */
	public void updatePersonClosedWithForward(int intValue, String cdEventType, Long idEvent, Long idStage,
			String txtEventDescr, Long idForwardPerson, Long idClosedPerson);

	/**
	 * 
	 * Method Name: updatePersonOnEventPersonLink Method Description: update
	 * person on event person link
	 * 
	 * @param idForwardPerson
	 * @param idClosedPerson
	 * @return
	 */
	public void updatePersonOnEventPersonLink(long idForwardPerson, long idClosedPerson);

	/**
	 * 
	 * Method Name: updatePersonOnStagePersonLink Method Description:update
	 * person on stage person link
	 * 
	 * @param idForwardPerson
	 * @param idClosedPerson
	 * @return
	 */
	public void updatePersonOnStagePersonLink(Long idForwardPerson, Long idClosedPerson);

	/**
	 *
	 * Method Name: updatePersonOnLetterAllegationLink Method Description:update
	 * person on letter allegation link
	 *
	 * @param openStageIds
	 * @param personMergeWorkerId
	 * @param closedPersonId
	 * @param forwardPersonId
	 * @return
	 */
	public void updatePersonOnLetterAllegationLink(Set<Long> openStageIds, Long personMergeWorkerId,
												   Long closedPersonId, Long forwardPersonId);

	/**
	 * Method Name: getPersonMergeUpdateLogListByIdPersonMerge Method
	 * Description: This method gets the Person Merge update log (fields
	 * affected by a merge)
	 * 
	 * @param idPersonMerge
	 * @return List<PersonMergeUpdateLogDto>
	 */
	public List<PersonMergeUpdateLogDto> getPersonMergeUpdateLogListByIdPersonMerge(Long idPersonMerge);

	/**
	 * Method Name: getPersonMergeMessages Method Description:This method gets
	 * all the messages saved during a person merge These include - Information
	 * messages shown to user - Validation messages - Post merge messages
	 * 
	 * @param idPersonMerge
	 * @return List<MergeSplitVldMsgDto>
	 */
	public List<MergeSplitVldMsgDto> getPersonMergeMessages(Long idPersonMerge);

	/**
	 * Method Name: getForwardCaseInCaseMerge Method Description: Fetch forward
	 * case in case of case merge
	 * 
	 * @param caseId
	 * @return CaseValueDto
	 */
	public CaseValueDto getForwardCaseInCaseMerge(Long caseId);

	/**
	 * Method Name: getStagesUpdatedInMerge Method Description:This function
	 * fetches the list of open stages updated in a particular merge. These will
	 * be the open stages for closed person at the time of merge.
	 * 
	 * @param idPersonMerge
	 * @return List<StagePersonValueDto>
	 */
	public List<StagePersonValueDto> getStagesUpdatedInMerge(Long idPersonMerge);

	/**
	 * Method Name: getForwardPersonInMerge Method Description: This method
	 * returns the forward person Id for a person
	 * 
	 * @param ulIdPerson
	 * @return Long
	 */
	public Long getForwardPersonInMerge(Long ulIdPerson);

	/**
	 * Method Name: checkIfMergeListLegacy Method Description: This method
	 * checks if the merge list to be fetched in a legacy stye for a person
	 * forward
	 * 
	 * @param fwdPersonId
	 * @return Boolean
	 */
	public Boolean checkIfMergeListLegacy(Long fwdPersonId);

	/**
	 * Method Name: getPersonMergeHierarchyList Method Description: This method
	 * returns the merge hierarchy list for a forward person.
	 * 
	 * @param fwdPersonId
	 * @param mergeListLegacy
	 * @return List<PersonMergeSplitValueDto>
	 */
	public List<PersonMergeSplitValueDto> getPersonMergeHierarchyList(Long fwdPersonId, Boolean mergeListLegacy);

	/**
	 * Method Name: getPersonMergeSelectFieldList Method Description: This
	 * function fetches the selections made by a user at Select Forward Person
	 * page during a merge.
	 * 
	 * @param idPersonMerge
	 * @return List<PersonMergeUpdateLogDto> @
	 */
	public List<PersonMergeUpdateLogDto> getPersonMergeSelectFieldList(Long idPersonMerge);

	/**
	 * Method Name: getPersonMergeUpdateLogList Method Description: This
	 * function fetches the selections made by a user at Select Forward Person
	 * page during a merge.
	 * 
	 * @param idPersonMerge
	 * @return
	 */
	public List<String> getPersonMergeUpdateLogList(Long idPersonMerge);

	/**
	 * 
	 * Method Name: createSnapshot Method Description: run procedure to create
	 * snapshot
	 * 
	 * @param idSnapshotTable
	 * @param idSnapshotDtl
	 * @param idPerson
	 * @throws SQLException
	 */
	public void createSnapshot(Long idSnapshotTable, Long idSnapshotDtl, Long idPerson) throws SQLException;

	/**
	 * 
	 * Method Name: updatePersonMergeByPersonMergeDto Method Description: Update
	 * Person Merge by personMergeSplitValueDto
	 * 
	 * @param persMergeSplitDto
	 * @return
	 */
	public void updatePersonMergeByPersonMergeDto(PersonMergeSplitValueDto persMergeSplitDto);

	/**
	 * 
	 * Method Name: updatePersonOnStagePersonLink Method Description:This
	 * function updates forward person Id on a specific stage person link
	 * record. It also updates boolean bIndCaregiver = false; int indNmStage =
	 * 0;
	 * 
	 * @param idForwardPerson
	 * @param idClosedPerson
	 * @param idStagePersonLink
	 * @param dataToUpdate
	 */
	public void updatePersonOnStagePersonLink(Long idForwardPerson, Long idStagePersonLink,
			StagePersonValueDto dataToUpdate);
	
	/**
	 * 
	 * Method Name: getSxVctmznIncdntForSplit Method Description: get Sexual victimization Incidents for split
	 * 
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return List<Long>
	 */
	public List<Long> getSxVctmznIncdntForSplit(Long idPersMerge, Long idForwardPerson);


	/**
	 * 
	 * Method Name: updateSxVctmznIncdntForSplit Method Description: Update CHILD_SX_VCTMZTN_INCDNT for split
	 * 
	 * @param idClosedPerson
	 * @param idChildVCTMZTNINCDNT
	 * @param idForwardPerson
	 * @return
	 */
	public void updateSxVctmznIncdntForSplit(long idClosedPerson, long idChildVCTMZTNINCDNT, long idForwardPerson);

	/**
	 *
	 * Method Name: updateSxMutualIncdntForSplit
	 * Method Description: Update CHILD_SX_MUTUAL_INCDNT for split
	 *
	 * @param idClosedPerson
	 * @param idChildMUTUALINCDNT
	 * @param idForwardPerson
	 * @return
	 */
	public void updateSxMutualIncdntForSplit(long idClosedPerson, long idChildMUTUALINCDNT, long idForwardPerson);

	/**
	 *
	 * Method Name: getSxVctmznIncdntForSplit Method Description: get Sexual Mutual Incidents for split
	 *
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return List<Long>
	 */
	public List<Long> getSxMutualIncdntForSplit(Long idPersMerge, Long idForwardPerson);
	
	/**
	 * 
	 * Method Name: getBeforeSplitSxVctmztn Method Description: gets Sexual Vctmztn  History data Before merge
	 * 
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return Object
	 */
	public Object getBeforeSplitSxVctmztn(Long idPersMerge, Long idForwardPerson);

	Object getBeforeSplitSxMutualIncdnt(Long idPersMerge, Long idForwardPerson);
	
	

	/**
	 * 
	 * Method Name: updateChildSxVctmztn Method Description: This method will update the 
	 * ChildSxVctmztn Data 
	 * @param ChildSxVctmztn
	 * 
	 */
	public void updateChildSxVctmztn(ChildSxVctmztn childSxVctmztn);
	
	
	/**	  
	 * Method Name: getClosedPersonBeforeSplitSxVctmztnIncdnt Method Description: gets svh incidents data Before merge
	 * for closed person
	 * 
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return Object
	 */
	public List<Long> getClosedPersonBeforeSplitSxVctmztnIncdnt(Long idPersMerge, Long closedPerson);
	
	/**
	 * Method Name: update table PLACEMENT_TA -> ID_RESPITE_PERSON column if any rows present with closed person id
	 * PPM 65209
	 * @param idFwdPerson
	 * @param idClosedPerson
	 * @param idPersonMergeWorker 
	 */
	public void updatePlacementTAWithFwdPerson(Long idFwdPerson, Long idClosedPerson, Long idPersonMergeWorker);

	/**
	 * Method Name: getClosedPersonBeforeSplitSxMutualIncdnt
	 * Method Description: gets svh incidents data Before merge for closed person
	 *
	 * @param idPersMerge
	 * @param closedPerson
	 * @return Object
	 */
	public List<Long> getClosedPersonBeforeSplitSxMutualIncdnt(Long idPersMerge, Long closedPerson);

	/**
	 * this method is used to update the closed person to forward person if closed person is aggressor in CHILD_SX_VCTMZTN_INCDNT table
	 * @param personMergeSplitDto
	 */
	public boolean updateIfClosedPersonAggressor(PersonMergeSplitDto personMergeSplitDto);

	/**
	 * this method is used to update the closed person to forward person if closed person is mutual in CHILD_SX_MUTUAL_INCDNT table
	 * @param personMergeSplitDto
	 */
	public void updateIfClosedPersonMutual(PersonMergeSplitDto personMergeSplitDto);

	/**
	 * this method is used to update the closed person to forward person if closed person is mutual in CSA_EPISODES_INCDNT table
	 * @param personMergeSplitDto
	 * @return 
	 */
	public boolean updateIfClosedPersonVictim(PersonMergeSplitDto personMergeSplitDto);
	/**
	 * Method Name: update table PLACEMENT_TA -> ID_RESPITE_PERSON column if any rows present with forward person id
	 * PPM 65209
	 * @param personMergeSplitDto
	 */
	public void splitPlacementTAWithFwdPerson(PersonMergeSplitDto personMergeSplitDto);

	public void updateRTBExceptionWithFwdPerson(Long idForwardPerson, Long idClosedPerson, Long idPersonMergeWorker);

	public List<Long> getRtbExceptionsForSplit(Long idPersMerge, Long idFwdPerson);

	public List<Long> getClosedPersonBeforeSplitRtbExceptions(Long idPersMerge, Long idClosedPerson);

	public void updateRtbExceptionsForSplit(Long idClosedPerson, long longValue, Long idFwdPerson);
}
