package us.tx.state.dfps.service.personmergesplit.serviceimpl;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ChildSxMutalIncdnt;
import us.tx.state.dfps.common.domain.ChildSxVctmztn;
import us.tx.state.dfps.common.domain.ChildSxVctmztnIncdnt;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.PersonMerge;
import us.tx.state.dfps.common.domain.PersonMergeSelectField;
import us.tx.state.dfps.common.domain.PersonPotentialDup;
import us.tx.state.dfps.common.domain.SnapshotDtl;
import us.tx.state.dfps.common.domain.SnapshotHeader;
import us.tx.state.dfps.common.domain.SnapshotTblList;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateInDto;
import us.tx.state.dfps.service.admin.service.TodoCreateService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.errorWarning.ErrorListDto;
import us.tx.state.dfps.service.errorWarning.ErrorListGroupDto;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.fcl.dao.MutualNonAggressiveIncidentDao;
import us.tx.state.dfps.service.fcl.dao.SexualVictimizationHistoryDao;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.AllegationDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.MergeSplitVldMsgDto;
import us.tx.state.dfps.service.person.dto.PersonMergeSplitDto;
import us.tx.state.dfps.service.person.dto.PersonPotentialDupDto;
import us.tx.state.dfps.service.personmergesplit.dao.PersonMergeDao;
import us.tx.state.dfps.service.personmergesplit.dao.PersonMergeSplitDao;
import us.tx.state.dfps.service.personmergesplit.dto.CaseValueDto;
import us.tx.state.dfps.service.personmergesplit.dto.PersonAllegationUpdateDto;
import us.tx.state.dfps.service.personmergesplit.dto.PersonMergeSplitReqDto;
import us.tx.state.dfps.service.personmergesplit.dto.PersonMergeSplitValueDto;
import us.tx.state.dfps.service.personmergesplit.dto.PersonMergeUpdateLogDto;
import us.tx.state.dfps.service.personmergesplit.service.PersonMergeSplitService;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.web.person.bean.SelectForwardPersonValueBean;
import us.tx.state.dfps.xmlstructs.inputstructs.MergeSplitToDoDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Service
 * Impl Level class for Person Merge Split> May 30, 2018- 10:29:24 AM © 2017
 * Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class PersonMergeSplitServiceImpl implements PersonMergeSplitService {

	@Autowired
	PersonMergeSplitDao personMergeSplitDao;
	@Autowired
	PersonMergeDao personMergeDao;

	@Autowired
	TodoCreateService todoCreateService;
	@Autowired
	AllegationDao allegationDao;
	@Autowired
	LookupDao lookupDao;

	@Autowired
	PersonDao personDao;
	
	@Autowired
	private SexualVictimizationHistoryDao sexualVictimizationHistoryDao;

	@Autowired
	private MutualNonAggressiveIncidentDao mutualNonAggressiveIncidentDao;

	private static final Logger log = Logger.getLogger(PersonMergeSplitServiceImpl.class);

	/**
	 * 
	 * Method Name: mergePersons Method Description:This function carries out
	 * the person merge. At high level it performs following sequence of steps -
	 * Make before merge snapshots for person forward and person closed - Call
	 * PersonMergeHelper to perform person and stage data updates - Save the
	 * warnings/informations if any thrown as part of validation step - Save the
	 * select forward fields as chosen by user - Save the areas updated by
	 * person merge - Save the after merge snapshots for the person forward
	 * 
	 * @param personMergeSplitDto
	 * @return PersonMergeSplitDto
	 */
	@Transactional(rollbackFor = { Exception.class })
	@Override
	public PersonMergeSplitDto mergePersons(PersonMergeSplitReqDto personMergeSplitDto) {
		log.info("Inside method mergePersons in Class PersonMergeSplitServiceImpl");
		// Save and update records into PERSON_MERGE
		PersonMergeSplitDto persMergeValueBean = new PersonMergeSplitDto();
		int staffWorkerId = personMergeSplitDto.getStaffWorkerId();
		int fwdPersonId = personMergeSplitDto.getSfPersValeBean().getIdPerson().getForwardPerson().getPrimaryKeyData()
				.getIdKey();
		int closedPersonId = personMergeSplitDto.getSfPersValeBean().getIdPerson().getClosedPerson().getPrimaryKeyData()
				.getIdKey();

		// populate merge information into persMergeValueBean
		persMergeValueBean.setIdForwardPerson((long) fwdPersonId);
		persMergeValueBean.setIdClosedPerson((long) closedPersonId);
		persMergeValueBean.setIdPersonMergeWorker((long) staffWorkerId);

		// fetch staff person information
		PersonDto personDto = personDao.getPersonById((long) staffWorkerId);
		persMergeValueBean.setNmPersonMergeWorker(personDto.getNmPersonFull());

		// Create the person merge records and all the indirect merge records
		int idPersonMerge = savePersonMergeRecords(persMergeValueBean);
		persMergeValueBean.setIdPersonMerge((long) idPersonMerge);

		// Prepare before merge snapshot for forward person
		preparePersonMergeSnapshots(persMergeValueBean.getIdForwardPerson(), CodesConstant.CSSPRDTY_B,
				persMergeValueBean);

		// Prepare before merge snapshot for closed person
		preparePersonMergeSnapshots(persMergeValueBean.getIdClosedPerson(), CodesConstant.CSSPRDTY_B,
				persMergeValueBean);

		// Perform merge specific operatio                                                                                                                                                                                                                                                                                                                                 ns
		try {
			personMergeDao.mergePersons(persMergeValueBean, personMergeSplitDto.getSfPersValeBean());
		} catch (Exception e) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(e.getMessage());
			serviceLayerException.initCause(e);
			throw serviceLayerException;
		}

		// Create alert ToDo for workers (PR, SE) for the open stages affected
		// by merge
		createMergeSplitToDo(persMergeValueBean, ServiceConstants.MERGE);

		// Save elect Forward person messages, merge validation messages and
		// post merge messages
		savePersonMergeMessageLog(personMergeSplitDto.getSfPersValeBean(), persMergeValueBean,
				personMergeSplitDto.getWarningList());

		// Save select forward person fields
		try {
			savePersonMergeSelectFields(persMergeValueBean, personMergeSplitDto.getSfPersValeBean());
		} catch (IllegalAccessException e) {
			throw new ServiceLayerException(e.getMessage());
		}

		// Save merge update log (data updated by merge)
		savePersonMergeUpdateLog(persMergeValueBean);

		// Prepare after merge snapshot for person forward
		preparePersonMergeSnapshots(persMergeValueBean.getIdForwardPerson(), CodesConstant.CSSPRDTY_A,
				persMergeValueBean);

		// Update the potential duplicate records
		updatePotentialDupRecords(persMergeValueBean);
		log.info("Outside method mergePersons in Class PersonMergeSplitServiceImpl");
		return persMergeValueBean;
	}

	/**
	 * 
	 * Method Name: splitPRT Method Description:This method is being called
	 * during the Person Split operation to split PRT
	 * 
	 * @param personMergeSplitDB
	 * @return long
	 */
	@Transactional(rollbackFor = { Exception.class })
	@Override
	public void splitPRT(PersonMergeSplitDto personMergeSplitDB) {
		log.info("Inside method splitPRT in Class PersonMergeSplitServiceImpl");
		Long idClosedPerson = personMergeSplitDB.getIdClosedPerson();
		Long idFwdPerson = personMergeSplitDB.getIdForwardPerson();
		Long idPersMerge = personMergeSplitDB.getIdPersonMerge();
		// Split prt person
		List<Long> prtPersonLinkList = personMergeSplitDao.getPRTForSplit(idPersMerge, idFwdPerson);
		prtPersonLinkList.stream().forEach(idPrtPersonLink -> personMergeSplitDao
				.updatePersonOnPrtForSplit(idClosedPerson, idPrtPersonLink, idFwdPerson));

		// Split prt connection person
		List<Long> prtConnSplitList = personMergeSplitDao.getPRTConnectionForSplit(idPersMerge, idFwdPerson);
		prtConnSplitList.stream().forEach(idPrtConnection -> personMergeSplitDao
				.updatePRTConnectionOnSplit(idClosedPerson, idPrtConnection, idFwdPerson));
		log.info("Outside method splitPRT in Class PersonMergeSplitServiceImpl");
	}

	/**
	 * 
	 * Method Name: splitPersonMerge Method Description:This function carries
	 * out the person split
	 * 
	 * @param personMergeSplitDB
	 * @return long
	 */
	@Transactional(rollbackFor = { Exception.class })
	@Override
	public long splitPersonMerge(PersonMergeSplitDto personMergeSplitDB) {
		log.info("Inside method splitPersonMerge in Class PersonMergeSplitServiceImpl");
		// create person merge/split DAO object

		// get person merge record based on primary key (ID_PERSON_MERGE)
		PersonMergeSplitValueDto persSplitValueBean = personMergeSplitDao
				.getPersonMergeInfo(personMergeSplitDB.getIdPersonMerge());

		// we do the following processing if it is not a legacy merge.
		// In selected legacy merges we need to do basic split
		List<PersonMerge> fwdPersonMergeList = null;

		if (!ServiceConstants.STRING_IND_Y.equals(personMergeSplitDB.getIndLegacy())) {
			// Fetch following 2 lists before we start the split
			// List of persons closed into the current forward
			fwdPersonMergeList = personMergeSplitDao
					.getPersonMergeListForForward(persSplitValueBean.getIdForwardPerson().intValue(), false);

			// List of persons closed into the current person being closed
			// we need to pass N to second argument as all records for closed
			// person will be invalid
			List<PersonMerge> closdPersonMergeList = personMergeSplitDao
					.getPersonMergeListForForward(persSplitValueBean.getIdClosedPerson().intValue(), true);

			// Check all the person closed to the forward person,
			// if these persons were also the person closed to the current
			// closed person
			// then validate such merge records for current person closed,
			for (PersonMerge fwdRec : fwdPersonMergeList) {
				boolean bFound = false;

				// If record being split is part of a group then consider only
				// group records
				// of forward person for marking invalid.
				// note: the group id is not set for legacy records.
				if (persSplitValueBean.getIdMergeGroup() != null && !persSplitValueBean.getIdMergeGroup().equals(0l)
						&& !persSplitValueBean.getIdMergeGroup().equals(fwdRec.getIdMergeGroup()))
					continue;

				for (PersonMerge closedRec : closdPersonMergeList) {

					/*
					 * following is good to have there for commenting. We
					 * already skipping records from fwd person // when merge
					 * group is set on record being split, then we need to
					 * consider only those records on closed person which were
					 * marked invalid because of this group.
					 */

					if (fwdRec.getPersonByIdPersMergeClosed().getIdPerson()
							.equals(closedRec.getPersonByIdPersMergeClosed().getIdPerson())) {
						// mark closed person record as valid
						closedRec.setIndPersMergeInvalid(ServiceConstants.STRING_IND_N.trim().charAt(0));

						personMergeSplitDao.updatePersonMerge(closedRec);
						bFound = true;
						break;
					}
				}

				// mark forward person record as invalid
				if (bFound && (fwdRec.getIdPersonMerge() != personMergeSplitDB.getIdPersonMerge().longValue())) {
					fwdRec.setIndPersMergeInvalid(ServiceConstants.STRING_IND_Y.trim().charAt(0));
					personMergeSplitDao.updatePersonMerge(fwdRec);
				}

			} // loop for forward person merge list

		} // end if not legacy

		// Now split the actual record which user has requested
		// update split information to split this record
		persSplitValueBean = personMergeSplitDao.getPersonMergeInfo(personMergeSplitDB.getIdPersonMerge());
		persSplitValueBean.setIdPersonMergeSplitWorker(personMergeSplitDB.getIdPersonMergeSplitWorker());
		persSplitValueBean.setDtPersonMergeSplit(new Date());
		persSplitValueBean.setDtLastUpdate(personMergeSplitDB.getDtLastUpdate());
		persSplitValueBean.setIndPersonMergeInvalid(ServiceConstants.STRING_IND_Y);

		// update split information into the database
		personMergeSplitDao.updatePersonMergeByPersonMergeDto(persSplitValueBean);

		// check if forward person has any closed persons to it
		// if it is only the dummy self merge record for forward person then
		// mark that invalid now
		fwdPersonMergeList = personMergeSplitDao
				.getPersonMergeListForForward(persSplitValueBean.getIdForwardPerson().intValue(), false);
		if (fwdPersonMergeList.size() == 1) {
			PersonMerge fwdRec = fwdPersonMergeList.get(0);
			fwdRec.setIndPersMergeInvalid(ServiceConstants.STRING_IND_Y.trim().charAt(0));
			personMergeSplitDao.updatePersonMerge(fwdRec);
		}

		// update status of closed person to Inactive
		PersonDto personDto = personDao.getPersonById((long) persSplitValueBean.getIdClosedPerson());
		Person person = new Person();
		personDto.setCdPersonStatus(CodesConstant.CPERSTAT_I);
		BeanUtils.copyProperties(personDto, person);
		personDao.updatePerson(person);

		// Update the Person Forward Categories
		boolean bIndFAHome = false; // indicator if person is PRN or COL in a FA
									// Home
		boolean bIndCase = false; // indicator is person is PRN or COL in a case

		// get all the stages for forward person
		List<StagePersonValueDto> personStageList = personMergeSplitDao
				.getStagesForPersonMergeView(persSplitValueBean.getIdForwardPerson().intValue());

		for (StagePersonValueDto stgPersValueBean : personStageList) {
			if (ServiceConstants.CSTAGES_FAD.equals(stgPersValueBean.getCdStage())
					&& (ServiceConstants.CPRSNTYP_PRN.equals(stgPersValueBean.getCdStagePersType())
							|| CodesConstant.CPRSNTYP_COL.equals(stgPersValueBean.getCdStagePersType()))) {
				bIndFAHome = true;
			}

			if (ServiceConstants.CPRSNTYP_PRN.equals(stgPersValueBean.getCdStagePersType())
					|| CodesConstant.CPRSNTYP_COL.equals(stgPersValueBean.getCdStagePersType())) {
				bIndCase = true;
			}
		}

		// Delete all person categories except EMP and FEM.
		// Only if forward person has some category after the merge
		if (bIndFAHome || bIndCase) {
			personMergeSplitDao.deleteNonEmpPersonCategories(persSplitValueBean.getIdForwardPerson().intValue());
		}

		// Add needed person categories
		// Add FAH category if person is PRN or COL in a FA Home
		if (bIndFAHome) {
			personMergeSplitDao.savePersonCategory(persSplitValueBean.getIdForwardPerson().intValue(),
					CodesConstant.CPSNDTCT_FAH);
		}
		// Add CAS category if person is PRN or COL in a stage
		if (bIndCase) {
			personMergeSplitDao.savePersonCategory(persSplitValueBean.getIdForwardPerson().intValue(),
					CodesConstant.CPSNDTCT_CAS);
		}

		// SIR 1026527 - Split PRT
		PersonMergeSplitDto personMergeSplitDto = new PersonMergeSplitDto();
		BeanUtils.copyProperties(persSplitValueBean, personMergeSplitDto);
		splitPRT(personMergeSplitDto);
		splitChildSxVctmztnIncdnt(personMergeSplitDto);
		updateChildSxVctmztnHistory(personMergeSplitDto);
		
		/*update table PLACEMENT_TA -> ID_RESPITE_PERSON column if any rows present with forward person id 
		 * this table does not have ID_PERSON columns as this is child table to PLACEMENT table
		 * but this has a column which is FK to PERSON table so if records present with forward person, we have to update with closed person id*/
		splitPlacementTAWithFwdPerson(personMergeSplitDto);

		splitChildSxMutualIncdnt(personMergeSplitDto);
		updateChildSxMutualIncdnt(personMergeSplitDto);

		// update CHILD_RTB_EXCEPTN table  if records present with forward person, we have to update with closed person id
		splitRTBExceptionWithFwdPerson(personMergeSplitDto);

		// Create alert ToDo for workers (PR, SE) for the open stages affected
		// by split
		createMergeSplitToDo(personMergeSplitDto, ServiceConstants.SPLIT);
		log.info("Outside method splitPersonMerge in Class PersonMergeSplitServiceImpl");
		return 0;
	}

	private void splitRTBExceptionWithFwdPerson(PersonMergeSplitDto personMergeSplitDto) {
		Long idClosedPerson = personMergeSplitDto.getIdClosedPerson();
		Long idFwdPerson = personMergeSplitDto.getIdForwardPerson();
		Long idPersMerge = personMergeSplitDto.getIdPersonMerge();

		// Fetch the List of RTB exceptions ds that needs to be split
		List<Long> rtbExceptionList = personMergeSplitDao.getRtbExceptionsForSplit(idPersMerge, idFwdPerson);
		if (!ObjectUtils.isEmpty(rtbExceptionList)) {
			/**
			 * check after the merge if new records are added
			 * to the forward person Then those records are to be retained with the forward
			 * person only after split.
			 */
			List<Long> closedPersonschRtbExceptions = personMergeSplitDao
					.getClosedPersonBeforeSplitRtbExceptions(idPersMerge, idClosedPerson);
			if (null != closedPersonschRtbExceptions) {
				rtbExceptionList = rtbExceptionList.stream().filter(closedPersonschRtbExceptions::contains)
						.collect(Collectors.toList());
			}


			rtbExceptionList.forEach(idPcspPrsnLink -> personMergeSplitDao
					.updateRtbExceptionsForSplit(idClosedPerson, idPcspPrsnLink.longValue(), idFwdPerson));

		}

	}

	/*ppm 65209	
	 update table PLACEMENT_TA -> ID_RESPITE_PERSON column if any rows present with forward person id 
	 * this table does not have ID_PERSON columns as this is child table to PLACEMENT table
	 * but this has a column which is FK to PERSON table so if records present with forward person, we have to update with closed person id*/
	private void splitPlacementTAWithFwdPerson(PersonMergeSplitDto personMergeSplitDto) {
		personMergeSplitDao.splitPlacementTAWithFwdPerson(personMergeSplitDto);
	}

	/**
	 * 
	 * Method Name: splitPCSP Method Description:This method is being called
	 * during the Person Split operation to split PCSP
	 * 
	 * @param personMergeSplitDB
	 * @return long
	 */
	@Override
	public long splitPCSP(PersonMergeSplitDto personMergeSplitDB) {
		log.info("Inside method splitPCSP in Class PersonMergeSplitServiceImpl");
		int idClosedPerson = personMergeSplitDB.getIdClosedPerson().intValue();
		int idFwdPerson = personMergeSplitDB.getIdForwardPerson().intValue();
		int idPersMerge = personMergeSplitDB.getIdPersonMerge().intValue();
		// Split PCSP Person Link Person
		List<Integer> pcspPrsnLinkList = personMergeSplitDao.getPcspPrsnLinkForSplit(idPersMerge, idFwdPerson);
		pcspPrsnLinkList.stream().forEach(idPcspPrsnLink -> personMergeSplitDao
				.updatePcspPrsnLink(idClosedPerson, idPcspPrsnLink.longValue(), idFwdPerson, false));

		// Split PCSP Assessment Person
		List<Integer> pcspAsmntSplitList = personMergeSplitDao.getPcspAsmntForSplit(idPersMerge, idFwdPerson);
		pcspAsmntSplitList.stream().forEach(
				idPcspAsmnt -> personMergeSplitDao.updatePcspAsmntForSplit(idClosedPerson, idPcspAsmnt, idFwdPerson));

		// Split PCSP Placement Person
		List<Integer> pcspPlcmntSplitList = personMergeSplitDao.getPcspPlcmntForSplit(idPersMerge, idFwdPerson);
		pcspPlcmntSplitList.stream().forEach(idPcspPlcmnt -> personMergeSplitDao
				.updatePcspPlcmntForSplit(idClosedPerson, idPcspPlcmnt, idFwdPerson));

		log.info("Outside method splitPCSP in Class PersonMergeSplitServiceImpl");
		return pcspPlcmntSplitList.size();
	}

	
	/**
	 *
	 * Method Name: splitChildSxMutualIncdnt
	 * Method Description:This method is being called during the Person Split operation to split CHILD_SX_MUTUAL_INCDNT
	 *
	 * @param personMergeSplitDB
	 *
	 */
	@Transactional(rollbackFor = { Exception.class })
	@Override
	public void splitChildSxMutualIncdnt(PersonMergeSplitDto personMergeSplitDB) {

		log.info("Inside method splitChildSxMutualIncdnt in Class PersonMergeSplitServiceImpl");

		Long idClosedPerson = personMergeSplitDB.getIdClosedPerson();
		Long idFwdPerson = personMergeSplitDB.getIdForwardPerson();
		Long idPersMerge = personMergeSplitDB.getIdPersonMerge();

		// Fetch the List of sx mutual incidents ids that needs to be split
		List<Long> sxChildMutualIncdntList = personMergeSplitDao.getSxMutualIncdntForSplit(idPersMerge, idFwdPerson);
		if (!ObjectUtils.isEmpty(sxChildMutualIncdntList)) {
			/**
			 * artf130746.This artifact change is to fix check after the merge if new records are added
			 * to the forward person Then those records are to be retained with the forward
			 * person only after split.
			 */
			List<Long> closedPersonschIncdnts = personMergeSplitDao
					.getClosedPersonBeforeSplitSxMutualIncdnt(idPersMerge, idClosedPerson);
			if (null != closedPersonschIncdnts) {
				sxChildMutualIncdntList = sxChildMutualIncdntList.stream().filter(closedPersonschIncdnts::contains)
						.collect(Collectors.toList());
			} // End of the changes for artf130746

			sxChildMutualIncdntList.forEach(idPcspPrsnLink -> personMergeSplitDao
					.updateSxMutualIncdntForSplit(idClosedPerson, idPcspPrsnLink.longValue(), idFwdPerson));
		}
		log.info("Outside method splitChildSxMutualIncdnt in Class PersonMergeSplitServiceImpl");
	}

	/**
	 * 	 * Method updateChildSxMutualIncdnt method will update sx mutual incidents indicator during person/merge split
	 * operation.
	 * @param personMergeSplitDto
	 */
	private void updateChildSxMutualIncdnt(PersonMergeSplitDto personMergeSplitDto) {

		log.info("Inside method updateChildSxMutualIncdnt in Class PersonMergeSplitServiceImpl");

		Long idFwdPerson = personMergeSplitDto.getIdForwardPerson();
		Long idPersMerge = personMergeSplitDto.getIdPersonMerge();
		ChildSxVctmztn forwardchildSxVctmztn;
		List<ChildSxMutalIncdnt> childSxMutualIncdntList =  mutualNonAggressiveIncidentDao.getMutualNonAggressiveIncidents(idFwdPerson);

		//If there are no incidents after split then update the Sx Mutual Incident indicator to N or null based on the Before Merge data
		if(ObjectUtils.isEmpty(childSxMutualIncdntList)) {
			forwardchildSxVctmztn = sexualVictimizationHistoryDao.getChildSxVctmztnByPersonId(idFwdPerson);

			if( !(ObjectUtils.isEmpty(forwardchildSxVctmztn))   && "Y".equalsIgnoreCase(forwardchildSxVctmztn.getIndChildSxVctmztnHist())) {

				forwardchildSxVctmztn.setIdLastUpdatePerson(personMergeSplitDto.getIdPersonMergeSplitWorker());
				forwardchildSxVctmztn.setDtLastUpdate(new Date());

				//Check if there was a record exists for child for sx mutual incident
				Object ssIBeforechildSxVctmztn = personMergeSplitDao.getBeforeSplitSxMutualIncdnt(idPersMerge, idFwdPerson);
				if(ObjectUtils.isEmpty(ssIBeforechildSxVctmztn)) {
					forwardchildSxVctmztn.setIndChildSxVctmztnHist(null);
				} else {
					forwardchildSxVctmztn.setIndChildSxVctmztnHist("N");
				}
				personMergeSplitDao.updateChildSxVctmztn(forwardchildSxVctmztn);
			}
		}
		log.info("Outside method updateChildSxMutualIncdnt in Class PersonMergeSplitServiceImpl");
	}

	/**
	 *
	 * Method Name: splitChildSxVctmztnIncdnt Method Description:This method is being called
	 * during the Person Split operation to split CHILD_SX_VCTMZTN_INCDNT
	 * 
	 * @param personMergeSplitDB
	 *
	 *	 */
	@Transactional(rollbackFor = { Exception.class })
	@Override
	public void splitChildSxVctmztnIncdnt(PersonMergeSplitDto personMergeSplitDB) {
		log.info("Inside method splitChildSxVctmztnIncdnt in Class PersonMergeSplitServiceImpl");
		Long idClosedPerson = personMergeSplitDB.getIdClosedPerson();
		Long idFwdPerson = personMergeSplitDB.getIdForwardPerson();
		Long idPersMerge = personMergeSplitDB.getIdPersonMerge();

		// Fetch the List of sx incidents ids that needs to be split
		List<Long> sxChildVctmztnIncdntList = personMergeSplitDao.getSxVctmznIncdntForSplit(idPersMerge, idFwdPerson);
		if (!ObjectUtils.isEmpty(sxChildVctmztnIncdntList)) {
			/**
			 * artf130746.This artifact change is to fix check after the merge if new records are added
			 * to the forward person Then those records are to be retained with the forward
			 * person only after split.
			 */
			List<Long> closedPersonschIncdnts = personMergeSplitDao
					.getClosedPersonBeforeSplitSxVctmztnIncdnt(idPersMerge, idClosedPerson);
			if (null != closedPersonschIncdnts) {
				sxChildVctmztnIncdntList = sxChildVctmztnIncdntList.stream().filter(closedPersonschIncdnts::contains)
						.collect(Collectors.toList());
			} // End of the changes for artf130746

			
				sxChildVctmztnIncdntList.forEach(idPcspPrsnLink -> personMergeSplitDao
						.updateSxVctmznIncdntForSplit(idClosedPerson, idPcspPrsnLink.longValue(), idFwdPerson));
			
		}

		log.info("Outside method splitChildSxVctmztnIncdnt in Class PersonMergeSplitServiceImpl");

	}
	
	/**
	 * Method updateChildSxVctmztnHistory method will update sx vctmztn history indicator during person/merge split
	 * operation.
	 * @param personMergeSplitDto
	 */
	private void updateChildSxVctmztnHistory(PersonMergeSplitDto personMergeSplitDto) {
		log.info("Inside method updateChildSxVctmztnHistory in Class PersonMergeSplitServiceImpl");
		Long idFwdPerson = personMergeSplitDto.getIdForwardPerson();
		Long idPersMerge = personMergeSplitDto.getIdPersonMerge();
		ChildSxVctmztn forwardchildSxVctmztn = null;
		List<ChildSxVctmztnIncdnt> childSxVctmztnIncdntList =  sexualVictimizationHistoryDao.fetchSexualVictimHistory(idFwdPerson);
		
		//If there are no incidents after split then update the Sx vctmzt history indicator to N or null based on the Before Merge data
		if(ObjectUtils.isEmpty(childSxVctmztnIncdntList)) {
			 forwardchildSxVctmztn=sexualVictimizationHistoryDao.getChildSxVctmztnByPersonId(idFwdPerson);
			
			 if( !(ObjectUtils.isEmpty(forwardchildSxVctmztn))   && "Y".equalsIgnoreCase(forwardchildSxVctmztn.getIndChildSxVctmztnHist())) {
				 
				 
				 forwardchildSxVctmztn.setIdLastUpdatePerson(personMergeSplitDto.getIdPersonMergeSplitWorker());
				 forwardchildSxVctmztn.setDtLastUpdate(new Date());
				 
				 //Check if there was a record exists for child for sx victmztn
				 Object ssIBeforechildSxVctmztn = personMergeSplitDao.getBeforeSplitSxVctmztn(idPersMerge, idFwdPerson);
				 if(ObjectUtils.isEmpty(ssIBeforechildSxVctmztn)) {
					 forwardchildSxVctmztn.setIndChildSxVctmztnHist(null);
				 } else 
				 {
					 forwardchildSxVctmztn.setIndChildSxVctmztnHist("N");
				 }
		 
				 personMergeSplitDao.updateChildSxVctmztn(forwardchildSxVctmztn);
			 }
		}
		
		log.info("Outside method updateChildSxVctmztnHistory in Class PersonMergeSplitServiceImpl");
		
	}

	/**
	 * 
	 * Method Name: splitPCSP Method Description:This method is being called
	 * during the Person Split operation to split Legacy PCSP
	 * 
	 * @param personMergeSplitDB
	 * @return long
	 */
	@Override
	public long splitLegacyPCSP(PersonMergeSplitDto personMergeSplitDB) {
		log.info("Inside method splitLegacyPCSP in Class PersonMergeSplitServiceImpl");
		int idClosedPerson = personMergeSplitDB.getIdClosedPerson().intValue();
		int idFwdPerson = personMergeSplitDB.getIdForwardPerson().intValue();
		int idPersMerge = personMergeSplitDB.getIdPersonMerge().intValue();
		// Split CHILD ID on CHILD_SAFETY_PLACEMENT
		List<Long> pcspChildSafetyPlcmt = personMergeSplitDao.getLegacyChildForSplit(idPersMerge, idFwdPerson);
		pcspChildSafetyPlcmt.stream().forEach(idChildSafetyPlcmt -> personMergeSplitDao
				.updateChildSafetyPlcmtForSplit(idClosedPerson, idChildSafetyPlcmt, idFwdPerson));

		// Split Legacy PCSP Caregiver Person
		List<Long> pcspCaregiverSplitList = personMergeSplitDao.getLegacyCaregiverForSplit(idPersMerge, idFwdPerson);
		pcspCaregiverSplitList.stream().forEach(idCaregiverPCSP -> personMergeSplitDao
				.updateCaregiverSafetyPlcmtForSplit(idClosedPerson, idCaregiverPCSP, idFwdPerson));

		log.info("Outside method splitLegacyPCSP in Class PersonMergeSplitServiceImpl");
		return pcspCaregiverSplitList.size();
	}

	/**
	 * SIR 1005505 This function saves the data into Person Merge table. - If
	 * the Foward Person has not been involved in a prior Merge, a record is
	 * inserted into PERSON_MERGE - If the Closed Person has been a Forward
	 * Person in a Prior Person Merge, Existing record(s) are updated and New
	 * record(s) are inserted. - A New record inserted reflecting this Person
	 * Merge.
	 * 
	 * @param personMergeSplitDao
	 *            - DAO access object
	 * @param persMergeValueBean
	 *            - contains merge information
	 * 
	 * @return The IdPersonMerge
	 */
	private int savePersonMergeRecords(PersonMergeSplitDto persMergeValueBean) {
		log.info("Inside method savePersonMergeRecords in Class PersonMergeSplitServiceImpl");
		// Fetch following 2 lists before we start the merge
		// List of persons closed into the current forward
		List<PersonMerge> fwdPersonMergeList = personMergeSplitDao
				.getPersonMergeListForForward(persMergeValueBean.getIdForwardPerson(), false);

		// List of persons closed into the current person being closed
		List<PersonMerge> closdPersonMergeList = personMergeSplitDao
				.getPersonMergeListForForward(persMergeValueBean.getIdClosedPerson(), false);

		// Insert a merge record between closed and forward person
		PersonMergeSplitDto currentMergeValueBean = new PersonMergeSplitDto();
		currentMergeValueBean.setIdForwardPerson(persMergeValueBean.getIdForwardPerson());
		currentMergeValueBean.setIdClosedPerson(persMergeValueBean.getIdClosedPerson());
		currentMergeValueBean.setIndPersonMergeInvalid(ServiceConstants.STRING_IND_N);
		currentMergeValueBean.setDtPersonMerge(new Date());
		currentMergeValueBean.setIndDirectMerge(ServiceConstants.STRING_IND_Y);
		currentMergeValueBean.setIdMergeGroup(0l);
		currentMergeValueBean.setIdPersonMergeWorker(persMergeValueBean.getIdPersonMergeWorker());
		currentMergeValueBean.setIdGroupLink(0l);
		Long idPersonMerge = personMergeSplitDao.savePersonMerge(currentMergeValueBean);
		currentMergeValueBean.setIdPersonMerge(idPersonMerge);
		// if person forward is already a person forward in existing merges
		boolean bDummyExists = false;
		// check if dummy record exists where forward person id is merged to
		// self
		if (CollectionUtils.isNotEmpty(fwdPersonMergeList)
				&& fwdPersonMergeList.stream().anyMatch(persMergeBean -> persMergeBean.getPersonByIdPersMergeForward()
						.equals(persMergeBean.getPersonByIdPersMergeClosed()))) {
			bDummyExists = true;
		}

		// if person forward does not have a self merge record, then create one
		if (!bDummyExists) {
			// insert a record in person_merge table indicating self merge
			PersonMergeSplitDto persMergeFwd2FwdValueBean = new PersonMergeSplitDto();
			persMergeFwd2FwdValueBean.setIdForwardPerson(persMergeValueBean.getIdForwardPerson());
			persMergeFwd2FwdValueBean.setIdClosedPerson(persMergeValueBean.getIdForwardPerson());
			persMergeFwd2FwdValueBean.setDtPersonMerge(new Date());
			persMergeFwd2FwdValueBean.setIndPersonMergeInvalid(ServiceConstants.STRING_IND_N);
			persMergeFwd2FwdValueBean.setIndDirectMerge(ServiceConstants.STRING_IND_Y);
			persMergeFwd2FwdValueBean.setIdMergeGroup(currentMergeValueBean.getIdPersonMerge());
			persMergeFwd2FwdValueBean.setIdPersonMergeWorker(persMergeValueBean.getIdPersonMergeWorker());
			persMergeFwd2FwdValueBean.setIdGroupLink(0l);
			personMergeSplitDao.savePersonMerge(persMergeFwd2FwdValueBean);
		}

		// Now each of the persons already closed into current person being
		// closed, need to be moved to current
		// forward person
		closdPersonMergeList.stream().forEach(alreadyClosedPersonMergeBean -> {
			// we need to skip the self merge (dummy) record for person closed
			if (!alreadyClosedPersonMergeBean.getPersonByIdPersMergeForward().getIdPerson()
					.equals(alreadyClosedPersonMergeBean.getPersonByIdPersMergeClosed().getIdPerson())) {
				// add closed person to new forward person (the indirect merge)
				PersonMergeSplitDto indirectPersonMergeValueBean = new PersonMergeSplitDto();
				indirectPersonMergeValueBean.setIdForwardPerson(persMergeValueBean.getIdForwardPerson());
				indirectPersonMergeValueBean
						.setIdClosedPerson(alreadyClosedPersonMergeBean.getPersonByIdPersMergeClosed().getIdPerson());
				indirectPersonMergeValueBean.setDtPersonMerge(new Date());
				indirectPersonMergeValueBean.setIndPersonMergeInvalid(ServiceConstants.STRING_IND_N);
				indirectPersonMergeValueBean.setIndDirectMerge(ServiceConstants.STRING_IND_N);
				indirectPersonMergeValueBean.setIdMergeGroup(currentMergeValueBean.getIdMergeGroup());
				indirectPersonMergeValueBean.setIdPersonMergeWorker(persMergeValueBean.getIdPersonMergeWorker());
				indirectPersonMergeValueBean.setIdGroupLink(0l);
				personMergeSplitDao.savePersonMerge(indirectPersonMergeValueBean);
			}
		});

		// Now mark all the records for closed person (where this person was
		// forward) as Invalid
		closdPersonMergeList.stream().forEach(alreadyClosedPersonMergeBean -> {
			alreadyClosedPersonMergeBean.setIndPersMergeInvalid(ServiceConstants.STRING_IND_Y.trim().charAt(0));
			// set the group link to indicate which merge marked this merge
			// invalid
			alreadyClosedPersonMergeBean.setIdGroupLink(currentMergeValueBean.getIdPersonMerge());
			personMergeSplitDao.updatePersonMerge(alreadyClosedPersonMergeBean);
		});

		log.info("Outside method savePersonMergeRecords in Class PersonMergeSplitServiceImpl");
		return currentMergeValueBean.getIdPersonMerge().intValue();
	}

	/**
	 * This function create snapshots for person data. The function creates meta
	 * data information for the snapshots and then calls a DB procedure to
	 * create the actual snapshots.
	 * 
	 * @param personMergeSplitDao
	 *            - DAO access object
	 * @param idPerson
	 *            - ID of the person for which snapshot is being made
	 * @param cdSnapshotType
	 *            - Before or After merge snapshot
	 * @param persMergeValueBean
	 *            - contains merge information
	 * 
	 * @return
	 */
	private void preparePersonMergeSnapshots(long idPerson, String cdSnapshotType,
			PersonMergeSplitDto persMergeValueBean) {
		log.info("Inside method preparePersonMergeSnapshots in Class PersonMergeSplitServiceImpl");
		// Fetch list of tables for which snapshot to be prepared
		ArrayList<SnapshotTblList> ssTblList = personMergeSplitDao.getSnapshotTableList(CodesConstant.CACTNTYP_100);

		// First make snapshots for forward person
		// Prepare data for an entry into SNAPSHOT table
		SnapshotHeader ssValueBean = new SnapshotHeader();
		ssValueBean.setTxtReferenceSource("PERSON_MERGE");
		ssValueBean.setIdReferenceData(persMergeValueBean.getIdPersonMerge());
		ssValueBean.setCdActionType(CodesConstant.CACTNTYP_100);
		ssValueBean.setCdSnapshotType(cdSnapshotType);
		ssValueBean.setIdCreatedPerson(persMergeValueBean.getIdPersonMergeWorker());
		ssValueBean.setIdLastUpdatePerson(persMergeValueBean.getIdPersonMergeWorker());
		ssValueBean.setIdObject(idPerson);
		ssValueBean.setDtCreated(new Date());
		ssValueBean.setDtLastUpdate(new Date());
		// Insert the record into snapshot table
		personMergeSplitDao.savePersonMergeSnapshot(ssValueBean);

		// for each row in the snapshot table list, create a record into
		// snapshot_dtl table
		// and call store procedure to prepare the snapshots for the table.
		ssTblList.stream().forEach(ssTblValueBean -> {
			// Now prepare the data for an entry into SNAPSHOT_DTL table
			SnapshotDtl ssDtlValueBean = new SnapshotDtl();
			ssDtlValueBean.setSnapshotHeader(ssValueBean);
			ssDtlValueBean.setTxtSnapshotTableName(ssTblValueBean.getTxtSnapshotTableName());
			ssDtlValueBean.setIdCreatedPerson(persMergeValueBean.getIdPersonMergeWorker());
			ssDtlValueBean.setIdLastUpdatePerson(persMergeValueBean.getIdPersonMergeWorker());
			ssDtlValueBean.setDtLastUpdate(new Date());
			ssDtlValueBean.setDtCreated(new Date());
			/** Fix for ALM Defect#13464: person merge  should not update the  ALLEGATION records after stage closure */
			ssDtlValueBean.setIndSourceRecFound(ServiceConstants.Y);
			// End of  fix for ALM#13646
			personMergeSplitDao.savePersonMergeSnapshotDtl(ssDtlValueBean);
			// Call store procedure to prepare snapshot corresponding to source
			// table in ssTblValueBean
			try {
				personMergeSplitDao.createSnapshot(ssTblValueBean.getIdSnapshotTblList(),
						ssDtlValueBean.getIdSnapshotDtl(), idPerson);
			} catch (SQLException e) {
				throw new ServiceLayerException(e.getMessage());
			}
		});

		log.info("Outside method preparePersonMergeSnapshots in Class PersonMergeSplitServiceImpl");

	}

	/**
	 * This function creates the To-Dos for all primary/Secondary workers for
	 * the Open Stages affected by the Person Merge or Split, for both the
	 * Forward and Closed Persons. In the event a worker conducting the Person
	 * Merge/Split is in this list, (s)he is excluded from the To-Do List.
	 * 
	 * 
	 * @param personMergeSplitDao
	 *            - DAO access object
	 * @param persMergeSplitValueBean
	 *            - bean containing merge information
	 * @param mergeOrSplit
	 *            ( M for merge, S for split)
	 * @return
	 */
	private void createMergeSplitToDo(PersonMergeSplitDto persMergeSplitValueBean, String mergeOrSplit) {
		log.info("Inside method createMergeSplitToDo in Class PersonMergeSplitServiceImpl");
		// Get the primary worker for the open stages affected by merge
		ArrayList<StagePersonLink> stageArr = personMergeSplitDao.getAffectedStagesStaff(
				persMergeSplitValueBean.getIdForwardPerson().intValue(),
				persMergeSplitValueBean.getIdClosedPerson().intValue(),
				persMergeSplitValueBean.getIdPersonMergeWorker().intValue());

		// Build ToDo message
		StringBuffer sbShortDesc = new StringBuffer("");
		sbShortDesc.append(persMergeSplitValueBean.getNmClosedPerson());
		if (ServiceConstants.MERGE.equals(mergeOrSplit))
			sbShortDesc.append(ServiceConstants.MERGE_SHORT_DESC);
		else
			sbShortDesc.append(ServiceConstants.SPLIT_SHORT_DESC);

		sbShortDesc.append(persMergeSplitValueBean.getNmForwardPerson());

		StringBuilder sbLongDesc = new StringBuilder("");
		sbLongDesc.append(persMergeSplitValueBean.getNmClosedPerson());
		sbLongDesc.append(" " + persMergeSplitValueBean.getIdClosedPerson() + " ");

		if (ServiceConstants.MERGE.equals(mergeOrSplit))
			sbLongDesc.append(ServiceConstants.MERGED_LONG_DESC);
		else
			sbLongDesc.append(ServiceConstants.SPLIT_LONG_DESC);

		sbLongDesc.append(persMergeSplitValueBean.getNmForwardPerson());
		sbLongDesc.append(" " + persMergeSplitValueBean.getIdForwardPerson());
		sbLongDesc.append(ServiceConstants.BY_STRING);

		if (ServiceConstants.MERGE.equals(mergeOrSplit)) {
			sbLongDesc.append(persMergeSplitValueBean.getNmPersonMergeWorker());
			sbLongDesc.append(" " + persMergeSplitValueBean.getIdPersonMergeWorker());
		} else {
			sbLongDesc.append(persMergeSplitValueBean.getNmPersonMergeSplitWorker());
			sbLongDesc.append(" " + persMergeSplitValueBean.getIdPersonMergeSplitWorker());
		}

		// For each stage/staff, create ToDo
		stageArr.stream().forEach(spBean -> {
			TodoCreateInDto csub40ui = new TodoCreateInDto();

			MergeSplitToDoDto csub40uig00 = new MergeSplitToDoDto();
			csub40ui.setServiceInputDto(new ServiceInputDto());

			if (ServiceConstants.MERGE.equals(mergeOrSplit))
				csub40uig00.setCdTodoCf(ServiceConstants.MERGE_TODO_INFO_CODE);
			else
				csub40uig00.setCdTodoCf(ServiceConstants.SPLIT_TODO_INFO_CODE);

			csub40uig00.setIdTodoCfPersCrea(persMergeSplitValueBean.getIdPersonMergeWorker());
			csub40uig00.setIdTodoCfStage(spBean.getIdStage());
			csub40uig00.setIdTodoCfPersAssgn(spBean.getIdPerson());
			csub40uig00.setIdTodoCfPersWkr(persMergeSplitValueBean.getIdPersonMergeWorker());
			csub40uig00.setTodoCfDesc(sbShortDesc.toString());
			csub40uig00.setTodoCfLongDesc(sbLongDesc.toString());
			csub40ui.setMergeSplitToDoDto(csub40uig00);

			todoCreateService.TodoCommonFunction(csub40ui);
		});

		log.info("Outside method createMergeSplitToDo in Class PersonMergeSplitServiceImpl");
	}

	/**
	 * This function stores list of information messages shown to user at Select
	 * Forward Person page. Then it stores all the warnings shown to user during
	 * person merge validation process Finally it stores some information
	 * messages displayed to user as post merge messages
	 * 
	 * @param personMergeSplitDao
	 *            - DAO access object
	 * @param sfPersValeBean
	 *            (Select forward person data)
	 * @param persMergeValueBean
	 *            - Contains person merge basic information
	 * @param warningList
	 *            (Array list of warnings found in merge validation process)
	 * 
	 * @return
	 */
	private void savePersonMergeMessageLog(SelectForwardPersonValueBean sfPersValeBean,
			PersonMergeSplitDto persMergeValueBean, ArrayList<ErrorListGroupDto> warningList) {
		log.info("Inside method savePersonMergeMessageLog in Class PersonMergeSplitServiceImpl");
		// Store any messages shown to user during Select forward page
		ArrayList<ErrorListDto> messagesToDisplay = sfPersValeBean.getMessages();

		if (CollectionUtils.isNotEmpty(messagesToDisplay)) {
			messagesToDisplay.stream()
					.forEach(errorListMessage -> personMergeSplitDao.savePersonMergeSplitValidationLog(
							persMergeValueBean, errorListMessage.getMsgNumber(), errorListMessage.getErrorMessage(),
							CodesConstant.CERRTYPE_I, CodesConstant.CACTNTYP_100,
							ServiceConstants.PERSON_MERGE_SELECT_FORWARD_STEP));
		}

		// Store any messages found during the validation process.
		if (CollectionUtils.isNotEmpty(warningList)) {
			warningList.stream().forEach(errorMsgGrp -> {
				ArrayList<ErrorListDto> errListMsgList = (ArrayList<ErrorListDto>) errorMsgGrp.getErrorMessageList();
				errListMsgList.stream()
						.forEach(errorListMsg -> personMergeSplitDao.savePersonMergeSplitValidationLog(
								persMergeValueBean, errorListMsg.getMsgNumber(), errorListMsg.getErrorMessage(),
								CodesConstant.CERRTYPE_W, CodesConstant.CACTNTYP_100,
								ServiceConstants.PERSON_MERGE_VALIDATION_STEP));
			});
		}

		// Now store information messages displayed to user as post merge
		// messages
		ArrayList<MergeSplitVldMsgDto> infoList = persMergeValueBean.getPostMergeInfoDataList();
		if (CollectionUtils.isNotEmpty(infoList)) {
			infoList.stream().forEach(infoMsg -> {
				// get the embedded data
				ArrayList dataArr = infoMsg.getMessageDataList();
				String msgText = null;
				if (CollectionUtils.isNotEmpty(dataArr)) {
					String data[] = new String[dataArr.size()];
					for (int k = 0; k < dataArr.size(); k++) {
						data[k] = (String) dataArr.get(k);
					}

					msgText = String.format(lookupDao.getMessageByNumber(Integer.toString(infoMsg.getMessageInt())),
							data);
				} else {
					msgText = lookupDao.getMessageByNumber(Integer.toString(infoMsg.getMessageInt()));
				}
				personMergeSplitDao.savePersonMergeSplitValidationLog(persMergeValueBean, infoMsg.getMessageInt(),
						msgText, CodesConstant.CERRTYPE_I, CodesConstant.CACTNTYP_100,
						ServiceConstants.PERSON_MERGE_POST_MERGE);
			});
		}
		log.info("Outside method savePersonMergeMessageLog in Class PersonMergeSplitServiceImpl");

	}

	/**
	 * This function stores the data selected by user in Select person forward
	 * page.
	 * 
	 * @param personMergeSplitDao
	 *            - DAO access object
	 * @param persMergeValueBean
	 *            - contains merge information
	 * @param sfPersValeBean
	 *            (bean containing select forward person data)
	 * 
	 * @return
	 * @throws IllegalAccessException
	 */
	private void savePersonMergeSelectFields(PersonMergeSplitDto persMergeValueBean,
			SelectForwardPersonValueBean sfPersValeBean) throws IllegalAccessException {
		log.info("Inside method savePersonMergeSelectFields in Class PersonMergeSplitServiceImpl");
		// get all the fields from SelectForwardPersonValueBean using reflection
		Field[] selectForwardPersonfields = SelectForwardPersonValueBean.class.getDeclaredFields();

		// Now for each field, see if value was selected from closed person or
		// from forward person
		// and save the data accordingly in PERSON_MERGE_SELECT_FIELD table
		for (Field selectForwardPersonField : selectForwardPersonfields) {

			if (selectForwardPersonField.getType() == SelectForwardPersonValueBean.MergePersons.class) {
				selectForwardPersonField.setAccessible(true);

				SelectForwardPersonValueBean.MergePersons mergePersonsValueBean = (SelectForwardPersonValueBean.MergePersons) selectForwardPersonField
						.get(sfPersValeBean);

				// For saving to database, we need to use standard predefined
				// field names so we need to do the mapping
				String fieldName = SelectForwardPersonValueBean
						.getfieldNameMappingForDB(selectForwardPersonField.getName());

				if (!ObjectUtils.isEmpty(fieldName)) {
					PersonMergeSelectField selectFieldValueBean = new PersonMergeSelectField();
					selectFieldValueBean.setIdPersonMerge(persMergeValueBean.getIdPersonMerge());
					selectFieldValueBean.setIdCreatedPerson(persMergeValueBean.getIdPersonMergeWorker());
					selectFieldValueBean.setIdLastUpdatePerson(persMergeValueBean.getIdPersonMergeWorker());
					selectFieldValueBean.setIdPersonMergeSelectField(0);
					selectFieldValueBean.setTxtSelectFieldName(fieldName);
					selectFieldValueBean.setDtCreated(new Date());
					selectFieldValueBean.setDtLastUpdate(new Date());

					if ((null != mergePersonsValueBean.getClosedPerson())
							&& mergePersonsValueBean.getClosedPerson().getIsSelected())
						selectFieldValueBean.setCdRole(CodesConstant.CMRGPERS_C);
					else if ((null != mergePersonsValueBean.getForwardPerson())
							&& mergePersonsValueBean.getForwardPerson().getIsSelected())
						selectFieldValueBean.setCdRole(CodesConstant.CMRGPERS_F);
					else // no need to save record
						continue;
					personMergeSplitDao.savePersonMergeSelectField(selectFieldValueBean);
				}
			}
		}
		log.info("Outside method savePersonMergeSelectFields in Class PersonMergeSplitServiceImpl");
	}

	/**
	 * SIR 1005505 This function stores list of categories updated as part of
	 * person merge.
	 * 
	 * @param personMergeSplitDao
	 *            - DAO access object
	 * @param persMergeValueBean
	 *            - contains HashMap of updated categories
	 * 
	 * @return
	 */
	private void savePersonMergeUpdateLog(PersonMergeSplitDto persMergeValueBean) {
		log.info("Inside method savePersonMergeUpdateLog in Class PersonMergeSplitServiceImpl");
		HashMap updateFieldMap = persMergeValueBean.getUpdateLogMap();
		Set s = updateFieldMap.keySet();
		Iterator itr = s.iterator();
		while (itr.hasNext()) {
			String catg = (String) itr.next();
			personMergeSplitDao.savePersonMergeUpdateLog(persMergeValueBean.getIdPersonMerge().intValue(), catg,
					persMergeValueBean.getIdPersonMergeWorker().intValue());
		}
		log.info("Outside method savePersonMergeUpdateLog in Class PersonMergeSplitServiceImpl");
	}

	/**
	 * 
	 * Method Name: updatePotentialDupRecords Method Description:Potential
	 * duplicate processing logic 1. Any Active and Distinct Potential Duplicate
	 * records found for the two Person Merge candidates in which each is listed
	 * are updated. o PERSON_POTENTIAL_DUPLICATE.IND_MERGED = Y o
	 * PERSON_POTENTIAL_DUPLICATE.DT_END = System Date 2. Any Active Potential
	 * Duplicate records in which the Person Closed is listed with another PID
	 * is ended. o PERSON_POTENTIAL_DUPLICATE.DT_END = System Date o
	 * PERSON_POTENTIAL_DUPLICATE.CD_RSN_NOT_MERGED = 'PID Closed in another
	 * Merge' 3. Any Active Potential Duplicate records found in which the
	 * Person Forward is listed with another PID is untouched.
	 * 
	 * @param pMergeSplitValueBean
	 */
	private void updatePotentialDupRecords(PersonMergeSplitDto pMergeSplitValueBean) {
		log.info("Inside method updatePotentialDupRecords in Class PersonMergeSplitServiceImpl");
		// get active potential duplicate record where both persons are involved
		PersonPotentialDupDto personPotentialDupDto = personMergeSplitDao.getActivePersonPotentialDupDetail(
				pMergeSplitValueBean.getIdClosedPerson().intValue(),
				pMergeSplitValueBean.getIdForwardPerson().intValue());

		if (!ObjectUtils.isEmpty(personPotentialDupDto)) {
			personPotentialDupDto.setIndMergeds(ServiceConstants.ARCHITECTURE_CONS_Y);
			personPotentialDupDto.setDtEnd(new Date());
			personMergeSplitDao.updatePersonPotentialDupInfo(personPotentialDupDto);
		}

		// get active potential duplicate records where person closed is list
		List<PersonPotentialDup> potDupList = personMergeSplitDao
				.getPersonPotentialDupList(pMergeSplitValueBean.getIdClosedPerson());

		potDupList.stream().filter(personPotentialDup -> !personPotentialDup.getPersonByIdPerson().getIdPerson()
				.equals(pMergeSplitValueBean.getIdForwardPerson())
				&& !personPotentialDup.getPersonByIdDupPerson().getIdPerson().equals(pMergeSplitValueBean.getIdForwardPerson())
				&& (personPotentialDup.getDtEnd() == null || DateUtils.daysDifference(personPotentialDup.getDtEnd(), DateUtils.getMaxJavaDate()) == 0))
				.forEach(personPotentialDup -> {
					personPotentialDup.setDtEnd(new Date());
					personPotentialDup.setCdRsnNotMerged(CodesConstant.CRSNNOMG_N22);
					personMergeSplitDao.updatePersonPotentialDupRec(personPotentialDup);
				});

		log.info("Outside method updatePotentialDupRecords in Class PersonMergeSplitServiceImpl");

	}

	/**
	 * Method Name: getPersonMergeInfo Method Description: This method returns
	 * Person Merge row based on IdPersonMerge
	 * 
	 * @param idPersonMerge
	 * @param userProfileDto
	 * @return PersonMergeSplitValueDto
	 * 
	 */
	@Transactional(rollbackFor = { Exception.class })
	@Override
	public PersonMergeSplitValueDto getPersonMergeInfo(Long idPersonMerge, boolean hasSensitiveCaseAccessRight) {
		log.info("Inside method getPersonMergeInfo in Class PersonMergeSplitServiceImpl");
		PersonMergeSplitValueDto personMergeSplitValueDto = personMergeSplitDao.getPersonMergeInfo(idPersonMerge);

		if (!ObjectUtils.isEmpty(personMergeSplitValueDto.getIdMergeGroup())
				&& !ServiceConstants.ZERO.equals(personMergeSplitValueDto.getIdMergeGroup())) {
			List<String> fieldCategoryList = personMergeSplitDao.getPersonMergeUpdateLogList(idPersonMerge);
			HashMap<String, String> hashMap = new HashMap<>();
			fieldCategoryList.stream().forEach(fieldCategory -> hashMap.put(fieldCategory, ServiceConstants.TRUE));

			ArrayList<String> updCatgItr = lookupDao.getCategoryListingDecode(ServiceConstants.CPMFLDCT);
			updCatgItr.stream().filter(string -> !hashMap.containsKey(string))
					.forEach(string -> hashMap.put(string, ServiceConstants.FALSE));

			if (!ObjectUtils.isEmpty(personMergeSplitValueDto))
				personMergeSplitValueDto.setUpdateLogMap(hashMap);

			ArrayList<MergeSplitVldMsgDto> mergeSplitVldMsgDtoList = (ArrayList<MergeSplitVldMsgDto>) personMergeSplitDao
					.getPersonMergeMessages(idPersonMerge);

			if (!TypeConvUtil.isNullOrEmpty(personMergeSplitValueDto)) {
				personMergeSplitValueDto.setValidationDataList(mergeSplitVldMsgDtoList);
			}

			for (MergeSplitVldMsgDto mergeSplitVldMsgDto : mergeSplitVldMsgDtoList) {
				if (TypeConvUtil.isNullOrEmpty(mergeSplitVldMsgDto.getStep()) && mergeSplitVldMsgDto.getStep() == 3
						&& mergeSplitVldMsgDto.getMessageInt() == ServiceConstants.MSG_POST_MERGE_ALLEG.intValue()) {
					List<AllegationDto> allegationDtoList = getPersonAllegationsUpdatedInMerge(idPersonMerge,
							personMergeSplitValueDto.getIdForwardPerson(),
							personMergeSplitValueDto.getIdClosedPerson());
					allegationDtoList.stream().forEach(allegationDto -> {
						Long caseId = allegationDto.getIdCase();
						CaseValueDto caseValueDto = personMergeSplitDao.getForwardCaseInCaseMerge(caseId);
						if (!ObjectUtils.isEmpty(caseValueDto.getIdCase())
								&& !ServiceConstants.ZERO.equals(caseValueDto.getIdCase())) {
							allegationDto.setIdFwdCase(caseValueDto.getIdCase());
							allegationDto.setIndSensitiveCase(caseValueDto.getIndCaseSensitive());
						} else
							allegationDto.setIdFwdCase(caseId);
					});

					boolean bSensitiveCaseNoAccess = false;
					if (!hasSensitiveCaseAccessRight && CollectionUtils.isNotEmpty(allegationDtoList)
							&& allegationDtoList.stream()
									.anyMatch(allgnRow -> ServiceConstants.Y.equals(allgnRow.getIndSensitiveCase()))) {
						bSensitiveCaseNoAccess = true;

					}
					if (bSensitiveCaseNoAccess) {
						personMergeSplitValueDto.setbSensitiveCaseNoAccess(true);
						allegationDtoList = null;
					}
					mergeSplitVldMsgDto.setAllegationDataList(allegationDtoList);
					break;
				}
			}

			ArrayList<StagePersonValueDto> stagePersonValueDtoList = (ArrayList<StagePersonValueDto>) personMergeSplitDao
					.getStagesUpdatedInMerge(idPersonMerge);
			personMergeSplitValueDto.setOpenStgUpdList(stagePersonValueDtoList);

			stagePersonValueDtoList.stream().forEach(stagePersonValueDto -> {
				Long caseId = stagePersonValueDto.getIdCase();
				CaseValueDto caseValueDto = personMergeSplitDao.getForwardCaseInCaseMerge(caseId);
				if (!ObjectUtils.isEmpty(caseValueDto.getIdCase())
						&& !ServiceConstants.ZERO.equals(caseValueDto.getIdCase())) {
					stagePersonValueDto.setIdFwdCase(caseValueDto.getIdCase());
					stagePersonValueDto.setIndSensitiveCase(caseValueDto.getIndCaseSensitive());
				} else
					stagePersonValueDto.setIdFwdCase(caseId);
			});

			if (!hasSensitiveCaseAccessRight && CollectionUtils.isNotEmpty(stagePersonValueDtoList)
					&& stagePersonValueDtoList.stream().anyMatch(stagePersonValueDto -> ServiceConstants.Y
							.equals(stagePersonValueDto.getIndSensitiveCase()))) {
				personMergeSplitValueDto.setbSensitiveCaseNoAccess(true);
			}
		}
		log.info("Outside method getPersonMergeInfo in Class PersonMergeSplitServiceImpl");
		return personMergeSplitValueDto;
	}

	/**
	 * Method Name: getPersonMergeHierarchyList Method Description: This method
	 * returns the merge hierarchy list for a forward person.
	 * 
	 * @param ulIdPerson
	 * @return List<PersonMergeSplitValueDto>
	 * 
	 */
	@Override
	public List<PersonMergeSplitValueDto> getPersonMergeHierarchyList(Long ulIdPerson) {
		List<PersonMergeSplitValueDto> personMergeSplitValueDtoList = new ArrayList<>();
		Long fwdPersonId = personMergeSplitDao.getForwardPersonInMerge(ulIdPerson);
		if (ObjectUtils.isEmpty(fwdPersonId) || fwdPersonId.equals(ServiceConstants.ZERO_VAL))
			fwdPersonId = ulIdPerson;
		Boolean mergeListLegacy = personMergeSplitDao.checkIfMergeListLegacy(fwdPersonId);
		personMergeSplitValueDtoList = personMergeSplitDao.getPersonMergeHierarchyList(fwdPersonId, mergeListLegacy);
		return personMergeSplitValueDtoList;
	}

	/**
	 * Method Name: getPersonMergeUpdateLogList Method Description: This method
	 * gets the Person Merge update log (fields affected by a merge)
	 * 
	 * @param idPersonMerge
	 * @return List<PersonMergeUpdateLogDto>
	 * 
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public List<PersonMergeUpdateLogDto> getPersonMergeUpdateLogList(Long idPersonMerge) {
		return personMergeSplitDao.getPersonMergeUpdateLogListByIdPersonMerge(idPersonMerge);
	}

	/**
	 * Method Name: getPersonMergeSelectFieldMap Method Description: This
	 * function fetches the selections made by a user at Select Forward Person
	 * page during a merge.
	 * 
	 * @param idPersonMerge
	 * @return HashMap<String, String>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public HashMap<String, String> getPersonMergeSelectFieldMap(Long idPersonMerge) {

		List<PersonMergeUpdateLogDto> selectFieldList = personMergeSplitDao
				.getPersonMergeSelectFieldList(idPersonMerge);

		HashMap<String, String> selectFieldMap = new HashMap<>();
		selectFieldList.stream().forEach(selectFieldDto -> selectFieldMap.put(selectFieldDto.getTxtSelectFieldName(),
				selectFieldDto.getCdRole()));

		return selectFieldMap;
	}

	/**
	 * Method Name: getPersonAllegationsUpdatedInMerge Method Description:This
	 * method fetches the allegations modified for forward person in a person
	 * merge.
	 * 
	 * @param idPersonMerge
	 * @param idForwardPerson
	 * @param idClosedPerson
	 * @return List<PersonAllegationUpdateDto>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public List<AllegationDto> getPersonAllegationsUpdatedInMerge(Long idPersonMerge, Long idForwardPerson,
			Long idClosedPerson) {

		// Get all the allegations for person closed before the merge
		List<PersonAllegationUpdateDto> closedBeforeList = allegationDao.getPersonAllegationsUpdatedInMerge(
				idPersonMerge, idClosedPerson, ServiceConstants.CD_ACTION_TYPE,
				ServiceConstants.CD_SNAPSHOT_TYPE_BEFORE);

		// Update forward person id on the closed person Id as it happens in a
		// merge
		closedBeforeList.stream()
				.forEach(personAllegationUpdateDto -> {
					if (personAllegationUpdateDto.getIdVictim().equals(idClosedPerson))
						personAllegationUpdateDto.setIdVictim(idForwardPerson);
					if (personAllegationUpdateDto.getIdAllegedPerpetrator().equals(idClosedPerson))
						personAllegationUpdateDto.setIdAllegedPerpetrator(idForwardPerson);
				});

		// Get all the allegations for the forward person before merge
		List<PersonAllegationUpdateDto> forwardBeforeList = allegationDao.getPersonAllegationsUpdatedInMerge(
				idPersonMerge, idForwardPerson, ServiceConstants.CD_ACTION_TYPE,
				ServiceConstants.CD_SNAPSHOT_TYPE_BEFORE);

		// make a total list of all the allegations before merge
		List<PersonAllegationUpdateDto> totalBeforeList = new ArrayList<>();
		totalBeforeList.addAll(closedBeforeList);
		totalBeforeList.addAll(forwardBeforeList);

		// Get all the allegations for the forward person after merge
		List<PersonAllegationUpdateDto> forwardAfterList = allegationDao.getPersonAllegationsUpdatedInMerge(
				idPersonMerge, idForwardPerson, CodesConstant.CACTNTYP_100, ServiceConstants.CD_SNAPSHOT_TYPE_AFTER);

		List<AllegationDto> allegationUpdateDtos = new ArrayList<>();

		// Check if any allegation was delete from "totalBeforeList" list
		Long nbrPair = 0L;
		for (PersonAllegationUpdateDto beforeListDto : totalBeforeList) {
			Boolean bAllgnDeleted = true;
			if (forwardAfterList.stream().anyMatch(forwardAfterListDto -> beforeListDto.getIdAllegation()
					.equals(forwardAfterListDto.getIdAllegation())))
				bAllgnDeleted = false;

			// if deleted allegation found, then add it to a list
			if (bAllgnDeleted) {
				beforeListDto.setPair(++nbrPair);
				AllegationDto beforeListAllDto = new AllegationDto();
				beforeListDto.setAllgnDelMod("Removed");
				BeanUtils.copyProperties(beforeListDto, beforeListAllDto);
				allegationUpdateDtos.add(beforeListAllDto);
				
				// for delete allegation find the corresponding duplicate
				// allegation
				// in the forward person allegation list after the merge
				for (PersonAllegationUpdateDto forwardAfterDto : forwardAfterList) {
					if ((!beforeListDto.getIdAllegation().equals(forwardAfterDto.getIdAllegation()))
							&& (beforeListDto.getIdVictim().equals(forwardAfterDto.getIdVictim()))
							&& (beforeListDto.getIdAllegedPerpetrator()
									.equals(forwardAfterDto.getIdAllegedPerpetrator()))
							&& (beforeListDto.getIdStage().equals(forwardAfterDto.getIdStage()))
							&& (beforeListDto.getCdAllegType().equals(forwardAfterDto.getCdAllegType()))) {
						// fetch the modified allegation data prior to merge.
						// we want to show show allegation looked prior to
						// merge.
						PersonAllegationUpdateDto beforeDto = null;
						if (CollectionUtils.isNotEmpty(totalBeforeList)) {
							Optional<PersonAllegationUpdateDto> optional = totalBeforeList.stream()
									.filter(totalBeforeDto -> forwardAfterDto.getIdAllegation()
											.equals(totalBeforeDto.getIdAllegation()))
									.findFirst();
							if (optional.isPresent()) {
								beforeDto = optional.get();
								if (!ObjectUtils.isEmpty(beforeDto)) {
									AllegationDto beforeAllegationDto = new AllegationDto();
									beforeDto.setPair(nbrPair);
									beforeDto.setAllgnDelMod("Modified");
									BeanUtils.copyProperties(beforeDto, beforeAllegationDto);
									allegationUpdateDtos.add(beforeAllegationDto);
									
									break;
								}
							}
						}
					}
				}
			} // if allegation was deleted

		}
		return allegationUpdateDtos;
	}

}
