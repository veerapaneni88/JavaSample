package us.tx.state.dfps.service.casemanagement.serviceimpl;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import us.tx.state.dfps.common.domain.Allegation;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.SubstanceTracker;
import us.tx.state.dfps.common.dto.ClosureNoticeListDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.common.dto.VictimListDto;
import us.tx.state.dfps.service.SDM.dao.SDMSafetyAssessmentDao;
import us.tx.state.dfps.service.admin.dto.CpsInvConclValiDto;
import us.tx.state.dfps.service.admin.dto.CpsInvConclValoDto;
import us.tx.state.dfps.service.admin.service.CpsInvConclValService;
import us.tx.state.dfps.service.approval.service.ApprovalStatusService;
import us.tx.state.dfps.service.casemanagement.dao.CPSInvCnlsnDao;
import us.tx.state.dfps.service.casemanagement.dao.PCSPPlcmntDao;
import us.tx.state.dfps.service.casemanagement.service.CPSInvCnlsnService;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dao.PcspListPlacmtDao;
import us.tx.state.dfps.service.casepackage.service.PcspService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dao.CnsrvtrshpRemovalDao;
import us.tx.state.dfps.service.contacts.dao.PersonMPSDao;
import us.tx.state.dfps.service.investigation.dao.AllegtnDao;
import us.tx.state.dfps.service.investigation.dto.SubstanceTrackerDto;
import us.tx.state.dfps.service.person.dao.CriminalHistoryDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.personutility.dao.PersonUtilityDao;
import us.tx.state.dfps.service.workload.dto.ApprovalPersonPrintDto;
import us.tx.state.dfps.service.workload.dto.SecondaryApprovalDto;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
@Transactional
public class CPSInvCnlsnServiceImpl implements CPSInvCnlsnService {

	@Autowired
	CPSInvCnlsnDao cnlsnDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	AllegtnDao allegationDao;

	@Autowired
	SDMSafetyAssessmentDao safetyAssessmentDao;

	@Autowired
	PCSPPlcmntDao pcspPlcmntDao;

	@Autowired
	ApprovalStatusService approvalStatusService;

	@Autowired
	CpsInvConclValService cpsInvConclValService;

	@Autowired
	CriminalHistoryDao crimHistDao;

	@Autowired
	PersonMPSDao personMpsDao;

	@Autowired
	PcspService pcspService;

	@Autowired
	PcspListPlacmtDao pcspListPlcmntDao;

	@Autowired
	CnsrvtrshpRemovalDao cnsrvtrshpRmvlDao;

	@Autowired
	PersonUtilityDao personUtilityDao;

	@Autowired
	CaseSummaryDao caseSummaryDao;


	private static final Logger log = Logger.getLogger(CPSInvCnlsnServiceImpl.class);

	private static final String[] PARENTAL_REL_INT = ServiceConstants.PARENTAL_REL_INT_ARRAY;

	//PPM 69915


	/***
	 * This method is used to save the substance information
	 * in substance_tracker table
	 *
	 * @param CpsInvSubstanceReq
	 * @return cpsInvSubstancesRes
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CpsInvSubstancesRes saveSubstances(CpsInvSubstanceReq cpsInvSubstanceReq) {
		CpsInvSubstancesRes cpsInvSubstancesRes = new CpsInvSubstancesRes();
		SubstanceTracker substanceTracker = new SubstanceTracker();
		List<SubstanceTracker> existingSubstanceTrackerList;

		if (!TypeConvUtil.isNullOrEmpty(cpsInvSubstanceReq)) {
			List<SubstanceTrackerDto> childSubstanceTrackerDtoReqList = cpsInvSubstanceReq.getChildSubstanceTrackerDtoList();
			List<SubstanceTrackerDto> parentSubstanceTrackerDtoReqList = cpsInvSubstanceReq.getParentSubstanceTrackerDtoList();
			if(childSubstanceTrackerDtoReqList != null) {
				for (SubstanceTrackerDto substanceTrackerDto : childSubstanceTrackerDtoReqList) {
					// get the existing list of drugs/substances
					existingSubstanceTrackerList = cnlsnDao.getExsitingSubstanceTrackersbyStageIDPersonID
							(substanceTrackerDto.getIdPerson(), substanceTrackerDto.getIdStage(), substanceTrackerDto.getIndParentChild());
					saveSubstanceforChildren(substanceTrackerDto, existingSubstanceTrackerList);
				}
			}

			if(parentSubstanceTrackerDtoReqList != null) {
				for (SubstanceTrackerDto substanceTrackerDto : parentSubstanceTrackerDtoReqList) {
					// get the existing list of drugs/substances
					existingSubstanceTrackerList = cnlsnDao.getExsitingSubstanceTrackersbyStageIDPersonID
							(substanceTrackerDto.getIdPerson(), substanceTrackerDto.getIdStage(), substanceTrackerDto.getIndParentChild());
					saveSubstanceforParentCareGiver(substanceTrackerDto, existingSubstanceTrackerList);
				}
			}
			cpsInvSubstancesRes.setChildSubstanceTrackerDtoList(childSubstanceTrackerDtoReqList);
			cpsInvSubstancesRes.setParentSubstanceTrackerDtoList(parentSubstanceTrackerDtoReqList);
		}
		cpsInvSubstancesRes.setSuccessMessage(ServiceConstants.SUCCESS);

		return cpsInvSubstancesRes;
	}


	private void saveSubstanceforChildren(SubstanceTrackerDto substanceTrackerDto,
										  List<SubstanceTracker> existingSubstanceTrackerList) {
		//Look at the current list of drugs/substances

		List<String> existingSubstancesList = existingSubstanceTrackerList.stream()
				.map(drug -> drug.getCdSubstance()).collect(Collectors.toList());


		//Figure out the difference

		List<SubstanceTracker> substanceListToDelete = existingSubstanceTrackerList.stream()
				.filter(es -> !(substanceTrackerDto.getPersonSubstances()).contains(es.getCdSubstance()))
				.collect(Collectors.toList());

		if (substanceListToDelete.size() > 0) {
			cnlsnDao.updateSubstanceTracker(substanceListToDelete, ServiceConstants.FUNCTION_DELETE);
		}


		List<String> substanceListToAdd = substanceTrackerDto.getPersonSubstances().stream()
				.filter(element -> !existingSubstancesList.contains(element))
				.collect(Collectors.toList());

		List<SubstanceTracker> insertList = new ArrayList<SubstanceTracker>();
		for (String substance : substanceListToAdd) {
			SubstanceTracker substanceTrackerTemp = new SubstanceTracker();
			substanceTrackerTemp.setCdSubstance(substance);
			if (ServiceConstants.OTHER_DRUG.equalsIgnoreCase(substance))
				substanceTrackerTemp.setOtherDrugs(substanceTrackerDto.getOtherDrugs());
			substanceTrackerTemp.setCreatedDate(new Date(Calendar.getInstance().getTimeInMillis()));
			substanceTrackerTemp.setCreatedPersonId(substanceTrackerDto.getIdCreatedPerson());
			substanceTrackerTemp.setLastUpdatedPersonId(substanceTrackerDto.getIdLastUpdatePerson());
			substanceTrackerTemp.setIdPerson(substanceTrackerDto.getIdPerson());
			substanceTrackerTemp.setIdStage(substanceTrackerDto.getIdStage());
			substanceTrackerTemp.setIndParentChild(substanceTrackerDto.getIndParentChild());
			insertList.add(substanceTrackerTemp);
		}

		if (insertList.size() > 0) {
			cnlsnDao.updateSubstanceTracker(insertList, ServiceConstants.FUNCTION_INSERT);
		}


		Optional<SubstanceTracker> otherDrug = existingSubstanceTrackerList.stream()
				.filter(drug -> ServiceConstants.OTHER_DRUG.equalsIgnoreCase(drug.getCdSubstance())).findAny();
		List<SubstanceTracker> updateList = new ArrayList<SubstanceTracker>();

		if (!StringUtils.isEmpty(substanceTrackerDto.getOtherDrugs())
		&& otherDrug.isPresent() && !otherDrug.get()
				.getOtherDrugs().equalsIgnoreCase(substanceTrackerDto.getOtherDrugs())) {
			otherDrug.get().setOtherDrugs(substanceTrackerDto.getOtherDrugs());
			otherDrug.get().setLastUpdatedPersonId(substanceTrackerDto.getIdLastUpdatePerson());
			otherDrug.get().setDtLastUpdate(new Date(Calendar.getInstance().getTimeInMillis()));
			updateList.add(otherDrug.get());
			cnlsnDao.updateSubstanceTracker(updateList, ServiceConstants.FUNCTION_UPDATE);
		}
	}

	/**
	 * This method is used to get the existing information
	 * from substance_tracker table
	 *
	 * @param idStage Stage id where the data is stored
	 * @return Response with lists for Parent and children
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CpsInvSubstancesRes getSubstancesByStageId(Long idStage) {
		CpsInvSubstancesRes cpsInvSubstancesRes = new CpsInvSubstancesRes();
		//get the List of PRNs for the given Stage id

		List<String> allowedParents= Arrays.asList("AB","AP","GU","PA","PB","PD","PG","ST");

		List<StagePersonValueDto> stagePersonValueDtoList = personDao.getPersonDtlList(idStage);

		Date intakeDate= caseSummaryDao.getIntIntakeDate(idStage);
		List<StagePersonValueDto> childStagePersonValueDtoList = stagePersonValueDtoList.stream().filter(person ->
						!ObjectUtils.isEmpty(person.getDtPersonBirth()) && !ObjectUtils.isEmpty(intakeDate)
								&& DateUtils.daysDifference(intakeDate,person.getDtPersonBirth()) < 366)
				.collect(Collectors.toList());

		List<StagePersonValueDto> parentStagePersonValueDtoList = stagePersonValueDtoList
				.stream()
				.filter(person -> !StringUtils.isEmpty(person.getCdStagePersRelInt())
						&& allowedParents.contains(person.getCdStagePersRelInt().toUpperCase()))
				.collect(Collectors.toList());

		cpsInvSubstancesRes.setChildSubstanceTrackerDtoList(getChildSubstanceList(idStage, childStagePersonValueDtoList));
		cpsInvSubstancesRes.setParentSubstanceTrackerDtoList(getParentSubstanceList(idStage, parentStagePersonValueDtoList));
		return cpsInvSubstancesRes;
	}

	private List<SubstanceTrackerDto> getParentSubstanceList(Long idStage, List<StagePersonValueDto> parentStagePersonValueDtoList) {
		List<SubstanceTrackerDto> parentSubstanceTrackerDtoList = new ArrayList<>();
		for (StagePersonValueDto person : parentStagePersonValueDtoList) {
			Long idPerson = person.getIdPerson();
			SubstanceTrackerDto substanceTrackerDto;
			List<SubstanceTracker> existingSubstanceTrackerList = cnlsnDao.getExsitingSubstanceTrackersbyStageIDPersonID
					(idPerson, idStage, ServiceConstants.IND_PARENT);

			if (existingSubstanceTrackerList != null && existingSubstanceTrackerList.size() > 0) {

				substanceTrackerDto = new SubstanceTrackerDto(idStage, idPerson, ServiceConstants.IND_PARENT,
						person.getNmPersonFull(), person.getCdStagePersType(),
						person.getCdStagePersRelInt(), person.getCdStagePersRole(),
						null, null, true);
			} else {
				substanceTrackerDto = new SubstanceTrackerDto(idStage, idPerson, ServiceConstants.IND_PARENT,
						person.getNmPersonFull(),person.getCdStagePersType(),
						person.getCdStagePersRelInt(),	person.getCdStagePersRole(),
						null, null, false);
			}

			parentSubstanceTrackerDtoList.add(substanceTrackerDto);
		}
		return parentSubstanceTrackerDtoList;
	}

	private List<SubstanceTrackerDto> getChildSubstanceList(Long idStage, List<StagePersonValueDto> childStagePersonValueDtoList) {
		List<SubstanceTrackerDto> childSubstanceTrackerDtoList = new ArrayList<>();
		for (StagePersonValueDto person : childStagePersonValueDtoList) {
			Long idPerson = person.getIdPerson();
			SubstanceTrackerDto substanceTrackerDto;
			List<SubstanceTracker> existingSubstanceTrackerList = cnlsnDao.getExsitingSubstanceTrackersbyStageIDPersonID
					(idPerson, idStage, ServiceConstants.IND_CHILD);

			if (!CollectionUtils.isEmpty(existingSubstanceTrackerList)) {
				List<String> substancesList = existingSubstanceTrackerList.stream().map(drug ->
						drug.getCdSubstance()).collect(Collectors.toList());

				Optional<SubstanceTracker> otherDrug = existingSubstanceTrackerList.stream()
						.filter(drug -> ServiceConstants.OTHER_DRUG.equalsIgnoreCase(drug.getCdSubstance())).findAny();
				String otherDrugText= otherDrug.isPresent()?otherDrug.get().getOtherDrugs():"";

				substanceTrackerDto = new SubstanceTrackerDto(idStage, idPerson, ServiceConstants.IND_CHILD,
						person.getNmPersonFull(), person.getCdStagePersType(),
						person.getCdStagePersRelInt(), person.getCdStagePersRole(),
						substancesList, otherDrugText, false);
			} else {
				substanceTrackerDto = new SubstanceTrackerDto(idStage, idPerson, ServiceConstants.IND_CHILD,
						person.getNmPersonFull(), person.getCdStagePersType(),
						person.getCdStagePersRelInt(),
						person.getCdStagePersRole(), null, null, false);
			}

			childSubstanceTrackerDtoList.add(substanceTrackerDto);
		}
		return childSubstanceTrackerDtoList;
	}


	private void saveSubstanceforParentCareGiver(SubstanceTrackerDto substanceTrackerDto,
												 List<SubstanceTracker> existingSubstanceTrackerList) {

		//Parent logic
		/**
		 * substanceTrackerDto.indParentDrugUsage is true and existingSubstanceTrackerList exists then don't do anything
		 * substanceTrackerDto.indParentDrugUsage is false and existingSubstanceTrackerList exists then delete
		 * substanceTrackerDto.indParentDrugUsage is true and existingSubstanceTrackerList doesn't exist then add
		 */
		List<SubstanceTracker> insertList = new ArrayList<SubstanceTracker>();

		if (substanceTrackerDto.getIndParentDrugUsage() && CollectionUtils.isEmpty(existingSubstanceTrackerList)) {
			SubstanceTracker substanceTrackerTemp = new SubstanceTracker();
			substanceTrackerTemp.setCreatedDate(new Date(Calendar.getInstance().getTimeInMillis()));
			substanceTrackerTemp.setCreatedPersonId(substanceTrackerDto.getIdCreatedPerson());
			substanceTrackerTemp.setLastUpdatedPersonId(substanceTrackerDto.getIdLastUpdatePerson());
			substanceTrackerTemp.setIdPerson(substanceTrackerDto.getIdPerson());
			substanceTrackerTemp.setIdStage(substanceTrackerDto.getIdStage());
			substanceTrackerTemp.setIndParentChild(substanceTrackerDto.getIndParentChild());
			insertList.add(substanceTrackerTemp);
			cnlsnDao.updateSubstanceTracker(insertList, ServiceConstants.FUNCTION_INSERT);
		} else if(!substanceTrackerDto.getIndParentDrugUsage() && !CollectionUtils.isEmpty(existingSubstanceTrackerList)){
			cnlsnDao.updateSubstanceTracker(existingSubstanceTrackerList, ServiceConstants.FUNCTION_DELETE);
		}
	}
	/**
	 * 
	 * Method Description:This Method is used to retireve the information of all
	 * the closure notices for a given EventId.
	 * 
	 * @param CommonHelperReq(eventId)
	 * @return cpsInvNoticesRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CpsInvNoticesRes getClosureNotices(CommonHelperReq commonHelperReq) {
		CpsInvNoticesRes cpsInvNoticesRes = new CpsInvNoticesRes();
		List<PersonListDto> personListByStage = null;
		List<ClosureNoticeListDto> closureNoticeDtoList = new ArrayList<ClosureNoticeListDto>();
		List<ClosureNoticeListDto> allegedPerpReportNoticeDtoList = new ArrayList<ClosureNoticeListDto>();
		List<ClosureNoticeListDto> parentCaregiverNoticeDtoList = new ArrayList<ClosureNoticeListDto>();
		List<VictimListDto> victimList = new ArrayList<VictimListDto>();
		Boolean schoolInvestigation = Boolean.FALSE;
		commonHelperReq.setStageProgType(ServiceConstants.CPGRMS_CPS);
		if (null != commonHelperReq.getIdEvent() && null != commonHelperReq.getIdStage()
				&& null != commonHelperReq.getStageProgType()) {
			cpsInvNoticesRes = cnlsnDao.getClosureNotices(commonHelperReq.getIdEvent(), commonHelperReq.getIdStage(),
					commonHelperReq.getStageProgType());
		}
		// Getting the Victim list from Allegation.
		schoolInvestigation = cnlsnDao.getSchoolPersonnelInitialRole(commonHelperReq.getIdEvent(),
				commonHelperReq.getIdStage());
		List<Person> allegedVictims = allegationDao.getVictimsByIdStage(commonHelperReq.getIdStage());
		allegedVictims.stream().distinct().forEach(allegedVictim -> {
			VictimListDto victim = new VictimListDto();
			victim.setNmVictimFull(!StringUtils.isEmpty(allegedVictim.getCdPersonSuffix())
					? allegedVictim.getNmPersonFull() + " " + allegedVictim.getCdPersonSuffix()
					: allegedVictim.getNmPersonFull());
			victim.setIdVictim(allegedVictim.getIdPerson());
			victim.setIndVictimChecked(ServiceConstants.STRING_IND_N);
			victimList.add(victim);
		});
		if (null != cpsInvNoticesRes.getClosureNoticeListDto()
				&& null != cpsInvNoticesRes.getAllegedPerpReportNoticeDto()
				&& null != cpsInvNoticesRes.getParentCaregiverNoticeDto()) {
			if (!(cpsInvNoticesRes.getClosureNoticeListDto().size() == ServiceConstants.LONG_ZERO_VAL)
					|| !(cpsInvNoticesRes.getAllegedPerpReportNoticeDto().size() == ServiceConstants.LONG_ZERO_VAL)
					|| !(cpsInvNoticesRes.getParentCaregiverNoticeDto().size() == ServiceConstants.LONG_ZERO_VAL)
					|| ServiceConstants.CSTAGES_AR.equalsIgnoreCase(commonHelperReq.getStageProgType())) {
				personListByStage = personDao.getPersonListByIdStage(commonHelperReq.getIdStage(),
						ServiceConstants.STAFF_TYPE);
				HashMap<Long, PersonListDto> map = new HashMap<Long, PersonListDto>();
				HashMap<Long, ClosureNoticeListDto> map2 = new HashMap<Long, ClosureNoticeListDto>();
				for (PersonListDto persons : personListByStage) {
					map.put(persons.getIdPerson(), persons);
				}
				for (ClosureNoticeListDto peeps : cpsInvNoticesRes.getParentCaregiverNoticeDto()) {
					if (map.containsKey(peeps.getIdPerson())
							&& !TypeConvUtil.isNullOrEmpty(map.get(peeps.getIdPerson()).getCdPersonMaritalStatus())) {
						peeps.setCdPersonMaritalStatus(map.get(peeps.getIdPerson()).getCdPersonMaritalStatus());
					}
					if (map.containsKey(peeps.getIdPerson())
							&& !TypeConvUtil.isNullOrEmpty(map.get(peeps.getIdPerson()).getIndStagePersReporter())) {
						peeps.setIndStagePersReporter(map.get(peeps.getIdPerson()).getIndStagePersReporter());
					}
					map2.put(peeps.getIdPerson(), peeps);
				}
				for (ClosureNoticeListDto peeps : cpsInvNoticesRes.getClosureNoticeListDto()) {
					if (map.containsKey(peeps.getIdPerson())
							&& !TypeConvUtil.isNullOrEmpty(map.get(peeps.getIdPerson()).getCdPersonMaritalStatus())) {
						peeps.setCdPersonMaritalStatus(map.get(peeps.getIdPerson()).getCdPersonMaritalStatus());
					}
					if (map.containsKey(peeps.getIdPerson())
							&& !TypeConvUtil.isNullOrEmpty(map.get(peeps.getIdPerson()).getIndStagePersReporter())) {
						peeps.setIndStagePersReporter(map.get(peeps.getIdPerson()).getIndStagePersReporter());
					}
					map2.put(peeps.getIdPerson(), peeps);
				}
				for (ClosureNoticeListDto peeps : cpsInvNoticesRes.getAllegedPerpReportNoticeDto()) {
					if (map.containsKey(peeps.getIdPerson())
							&& !TypeConvUtil.isNullOrEmpty(map.get(peeps.getIdPerson()).getCdPersonMaritalStatus())) {
						peeps.setCdPersonMaritalStatus(map.get(peeps.getIdPerson()).getCdPersonMaritalStatus());
					}
					if (map.containsKey(peeps.getIdPerson())
							&& !TypeConvUtil.isNullOrEmpty(map.get(peeps.getIdPerson()).getIndStagePersReporter())) {
						peeps.setIndStagePersReporter(map.get(peeps.getIdPerson()).getIndStagePersReporter());
					}
					map2.put(peeps.getIdPerson(), peeps);
				}
				for (HashMap.Entry<Long, PersonListDto> persons : map.entrySet()) {
					if (!map2.containsKey(persons.getKey())) {
						if (schoolInvestigation) {
							ClosureNoticeListDto parentDto = new ClosureNoticeListDto();
							if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getIdPerson())
									&& !TypeConvUtil.isNullOrEmpty(persons.getValue().getStagePersRelInt())) {
								if (ArrayUtils.contains(PARENTAL_REL_INT, persons.getValue().getStagePersRelInt())) {
									parentDto.setIdPerson(persons.getValue().getIdPerson());
									parentDto.setCdStagePersRelInt(persons.getValue().getStagePersRelInt());
									if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getPersonFull())) {
										parentDto.setNmPersonFull(
												!StringUtils.isEmpty(persons.getValue().getPersonSuffix())
														? persons.getValue().getPersonFull() + " "
																+ persons.getValue().getPersonSuffix()
														: persons.getValue().getPersonFull());
									}
									if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getStagePersRole())) {
										parentDto.setCdStagePersRole(persons.getValue().getStagePersRole());
									}
									if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getStagePersType())) {
										parentDto.setCdStagePersType(persons.getValue().getStagePersType());
									}
									if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getCdPersonMaritalStatus())) {
										parentDto.setCdPersonMaritalStatus(
												persons.getValue().getCdPersonMaritalStatus());
									}
									if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getIndStagePersReporter())) {
										parentDto.setIndStagePersReporter(persons.getValue().getIndStagePersReporter());
									}
									if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getDtPersonBirth())) {
										parentDto.setDtPersonBirth(persons.getValue().getDtPersonBirth());
									}
									parentDto.setVictimListDto(victimList);
									parentCaregiverNoticeDtoList.add(parentDto);
								}
							}
						}

						if (schoolInvestigation
								&& !ArrayUtils.contains(PARENTAL_REL_INT, persons.getValue().getStagePersRelInt())) {
							if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getIdPerson())
									&& !TypeConvUtil.isNullOrEmpty(persons.getValue().getStagePersRelInt())) {
								if (!ServiceConstants.PERSON_OLDEST_VICTIM
										.equalsIgnoreCase(persons.getValue().getStagePersRelInt())
										&& !ServiceConstants.PERSON_VICTIM
												.equalsIgnoreCase(persons.getValue().getStagePersRelInt())) {
									ClosureNoticeListDto allegedPerpDto = new ClosureNoticeListDto();

									allegedPerpDto.setIdPerson(persons.getValue().getIdPerson());

									if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getPersonFull())) {
										allegedPerpDto.setNmPersonFull(
												!StringUtils.isEmpty(persons.getValue().getPersonSuffix())
												? persons.getValue().getPersonFull() + " "
														+ persons.getValue().getPersonSuffix()
												: persons.getValue().getPersonFull());
									}
									if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getStagePersRole())) {
										allegedPerpDto.setCdStagePersRole(persons.getValue().getStagePersRole());
									}
									if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getStagePersType())) {
										allegedPerpDto.setCdStagePersType(persons.getValue().getStagePersType());
									}
									if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getStagePersRelInt())) {
										allegedPerpDto.setCdStagePersRelInt(persons.getValue().getStagePersRelInt());
									}
									if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getCdPersonMaritalStatus())) {
										allegedPerpDto.setCdPersonMaritalStatus(
												persons.getValue().getCdPersonMaritalStatus());
									}
									if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getIndStagePersReporter())) {
										allegedPerpDto
												.setIndStagePersReporter(persons.getValue().getIndStagePersReporter());
									}
									if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getDtPersonBirth())) {
										allegedPerpDto.setDtPersonBirth(persons.getValue().getDtPersonBirth());
									}

									allegedPerpReportNoticeDtoList.add(allegedPerpDto);
								}
							}
						} else if (!schoolInvestigation) {
							ClosureNoticeListDto closureNoticeListDto = new ClosureNoticeListDto();
							if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getIdPerson())
									&& !TypeConvUtil.isNullOrEmpty(persons.getValue().getStagePersRelInt())) {
								closureNoticeListDto.setIdPerson(persons.getValue().getIdPerson());
								if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getPersonFull())) {
									closureNoticeListDto.setNmPersonFull(
											!StringUtils.isEmpty(persons.getValue().getPersonSuffix())
											? persons.getValue().getPersonFull() + " "
													+ persons.getValue().getPersonSuffix()
											: persons.getValue().getPersonFull());
								}
								if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getStagePersRole())) {
									closureNoticeListDto.setCdStagePersRole(persons.getValue().getStagePersRole());
								}
								if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getStagePersType())) {
									closureNoticeListDto.setCdStagePersType(persons.getValue().getStagePersType());
								}
								if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getStagePersRelInt())) {
									closureNoticeListDto.setCdStagePersRelInt(persons.getValue().getStagePersRelInt());
								}
								if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getCdPersonMaritalStatus())) {
									closureNoticeListDto
											.setCdPersonMaritalStatus(persons.getValue().getCdPersonMaritalStatus());
								}
								if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getIndStagePersReporter())) {
									closureNoticeListDto
											.setIndStagePersReporter(persons.getValue().getIndStagePersReporter());
								}
								if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getDtPersonBirth())) {
									closureNoticeListDto.setDtPersonBirth(persons.getValue().getDtPersonBirth());
								}
								closureNoticeListDto.setVictimListDto(victimList);
								closureNoticeDtoList.add(closureNoticeListDto);
							}
						}
					}
				}
				cpsInvNoticesRes.getClosureNoticeListDto().addAll(closureNoticeDtoList);
				cpsInvNoticesRes.getAllegedPerpReportNoticeDto().addAll(allegedPerpReportNoticeDtoList);
				cpsInvNoticesRes.getParentCaregiverNoticeDto().addAll(parentCaregiverNoticeDtoList);
				// Set Victim List of each parent giver. Update Ind Checked for
				// those in the database
				cpsInvNoticesRes.getParentCaregiverNoticeDto().forEach(pcgDto -> {
					victimList.forEach(allegedVictim -> {
						VictimListDto personVictim = pcgDto.getVictimListDto().stream().filter(
								savedVictim -> savedVictim.getIdVictim().compareTo(allegedVictim.getIdVictim()) == 0)
								.findAny().orElse(null);
						if (ObjectUtils.isEmpty(personVictim)) {
							pcgDto.getVictimListDto().add(allegedVictim);
						}
					});
				});
				cpsInvNoticesRes.getClosureNoticeListDto().forEach(closureNoticeDto -> {
					victimList.forEach(allegedVictim -> {
						VictimListDto personVictim = closureNoticeDto.getVictimListDto().stream().filter(
										savedVictim -> savedVictim.getIdVictim().compareTo(allegedVictim.getIdVictim()) == 0)
								.findAny().orElse(null);
						if (ObjectUtils.isEmpty(personVictim)) {
							closureNoticeDto.getVictimListDto().add(allegedVictim);
						}
					});
				});
			} else if (cpsInvNoticesRes.getClosureNoticeListDto().size() == ServiceConstants.LONG_ZERO_VAL
					&& cpsInvNoticesRes.getAllegedPerpReportNoticeDto().size() == ServiceConstants.LONG_ZERO_VAL
					&& cpsInvNoticesRes.getParentCaregiverNoticeDto().size() == ServiceConstants.LONG_ZERO_VAL) {
				personListByStage = personDao.getPersonListByIdStage(commonHelperReq.getIdStage(),
						ServiceConstants.STAFF_TYPE);
				for (PersonListDto persons : personListByStage) {
					if (schoolInvestigation && ArrayUtils.contains(PARENTAL_REL_INT, persons.getStagePersRelInt())) {
						ClosureNoticeListDto parentCaregiverNoticeDto = new ClosureNoticeListDto();
						parentCaregiverNoticeDto.setCdStagePersRelInt(persons.getStagePersRelInt());
						parentCaregiverNoticeDto.setNmPersonFull(!StringUtils.isEmpty(persons.getPersonSuffix())
								? persons.getPersonFull() + " " + persons.getPersonSuffix() : persons.getPersonFull());
						parentCaregiverNoticeDto.setCdStagePersRole(persons.getStagePersRole());
						parentCaregiverNoticeDto.setCdStagePersType(persons.getStagePersType());
						parentCaregiverNoticeDto.setIdPerson(persons.getIdPerson());
						parentCaregiverNoticeDto.setCdPersonMaritalStatus(persons.getCdPersonMaritalStatus());
						parentCaregiverNoticeDto.setIndStagePersReporter(persons.getIndStagePersReporter());
						if (!TypeConvUtil.isNullOrEmpty(persons.getDtPersonBirth())) {
							parentCaregiverNoticeDto.setDtPersonBirth(persons.getDtPersonBirth());
						}
						parentCaregiverNoticeDtoList.add(parentCaregiverNoticeDto);
					}
					if (schoolInvestigation && !ArrayUtils.contains(PARENTAL_REL_INT, persons.getStagePersRelInt())
							&& !ServiceConstants.PERSON_OLDEST_VICTIM.equalsIgnoreCase(persons.getStagePersRelInt())
							&& !ServiceConstants.PERSON_VICTIM.equalsIgnoreCase(persons.getStagePersRelInt())) {
						ClosureNoticeListDto allegedPerpReportNoticeDto = new ClosureNoticeListDto();
						allegedPerpReportNoticeDto.setCdStagePersRelInt(persons.getStagePersRelInt());
						allegedPerpReportNoticeDto.setNmPersonFull(!StringUtils.isEmpty(persons.getPersonSuffix())
								? persons.getPersonFull() + " " + persons.getPersonSuffix() : persons.getPersonFull());
						allegedPerpReportNoticeDto.setCdStagePersRole(persons.getStagePersRole());
						allegedPerpReportNoticeDto.setCdStagePersType(persons.getStagePersType());
						allegedPerpReportNoticeDto.setIdPerson(persons.getIdPerson());
						allegedPerpReportNoticeDto.setCdPersonMaritalStatus(persons.getCdPersonMaritalStatus());
						allegedPerpReportNoticeDto.setIndStagePersReporter(persons.getIndStagePersReporter());
						if (!TypeConvUtil.isNullOrEmpty(persons.getDtPersonBirth())) {
							allegedPerpReportNoticeDto.setDtPersonBirth(persons.getDtPersonBirth());
						}
						allegedPerpReportNoticeDtoList.add(allegedPerpReportNoticeDto);
					}
					if (!schoolInvestigation) {
						ClosureNoticeListDto closureNotice = new ClosureNoticeListDto();
						closureNotice.setCdStagePersRelInt(persons.getStagePersRelInt());
						closureNotice.setNmPersonFull(!StringUtils.isEmpty(persons.getPersonSuffix())
								? persons.getPersonFull() + " " + persons.getPersonSuffix() : persons.getPersonFull());
						closureNotice.setCdStagePersRole(persons.getStagePersRole());
						closureNotice.setCdStagePersType(persons.getStagePersType());
						closureNotice.setIdPerson(persons.getIdPerson());
						closureNotice.setCdPersonMaritalStatus(persons.getCdPersonMaritalStatus());
						closureNotice.setIndStagePersReporter(persons.getIndStagePersReporter());
						if (!TypeConvUtil.isNullOrEmpty(persons.getDtPersonBirth())) {
							closureNotice.setDtPersonBirth(persons.getDtPersonBirth());
						}
						closureNotice.setVictimListDto(victimList);
						closureNoticeDtoList.add(closureNotice);
					}
				}
				cpsInvNoticesRes.setClosureNoticeListDto(closureNoticeDtoList);
				cpsInvNoticesRes.setAllegedPerpReportNoticeDto(allegedPerpReportNoticeDtoList);
				cpsInvNoticesRes.setParentCaregiverNoticeDto(parentCaregiverNoticeDtoList);
				cpsInvNoticesRes.getParentCaregiverNoticeDto().forEach(parentCareGiverDto -> {
					List<VictimListDto> allegedVictimsDto = new ArrayList<VictimListDto>(victimList);
					parentCareGiverDto.setVictimListDto(allegedVictimsDto);
				});
			}
		}
		cpsInvNoticesRes.setClosureNoticeListDto(sortCLosureList(cpsInvNoticesRes.getClosureNoticeListDto()));
		return cpsInvNoticesRes;
	}

	/**
	 * 
	 * Method Description: This method is used to save/update/delete the
	 * information of closure notices.
	 * 
	 * @param cpsInvNoticesClosureReq
	 * @return CpsInvNoticesRes(successMessage String)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CpsInvNoticesRes saveClosureNotices(CpsInvNoticesClosureReq cpsInvNoticesClosureReq) {
		if (!ObjectUtils.isEmpty(cpsInvNoticesClosureReq.getParentCaregiverNoticeDto())) {
			for (ClosureNoticeListDto listItems : cpsInvNoticesClosureReq.getParentCaregiverNoticeDto()) {
				if (!TypeConvUtil.isNullOrEmpty((listItems.getIdConclusionNoticeInfo()))) {
					if (!TypeConvUtil.isNullOrEmpty(listItems.getCdNoticeSelected())) {
						cpsInvNoticesClosureReq.setReqFuncCd("U");
						listItems.setReqFuncAction("U");
					} else {
						cpsInvNoticesClosureReq.setReqFuncCd("D");
						listItems.setReqFuncAction("D");
					}
				} else if (!TypeConvUtil.isNullOrEmpty(listItems.getCdNoticeSelected())) {
					cpsInvNoticesClosureReq.setReqFuncCd("A");
					listItems.setReqFuncAction("A");
				} else {
					cpsInvNoticesClosureReq.setReqFuncCd("I");
					listItems.setReqFuncAction("I");
				}
			}
		}
		return cnlsnDao.saveClosureNotices(cpsInvNoticesClosureReq);
	}

	/**
	 * 
	 * Method Description:This Method is used to retireve the information of all
	 * the closure notices for a given EventId.
	 * 
	 * @param CommonHelperReq(eventId)
	 * @return cpsInvNoticesRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CpsInvNoticesRes getARClosureNotices(CommonHelperReq commonHelperReq) {
		CpsInvNoticesRes cpsInvNoticesRes = new CpsInvNoticesRes();
		List<PersonListDto> personListByStage = null;
		commonHelperReq.setStageProgType(ServiceConstants.CSTAGES_AR);
		cpsInvNoticesRes = cnlsnDao.getClosureNotices(commonHelperReq.getIdEvent(), commonHelperReq.getIdStage(),
				commonHelperReq.getStageProgType());
		if (!TypeConvUtil.isNullOrEmpty(cpsInvNoticesRes.getClosureNoticeListDto())) {
			if (!(cpsInvNoticesRes.getClosureNoticeListDto().size() == ServiceConstants.LONG_ZERO_VAL)
					|| ServiceConstants.CSTAGES_AR.equalsIgnoreCase(commonHelperReq.getStageProgType())) {
				personListByStage = personDao.getPersonListByIdStage(commonHelperReq.getIdStage(),
						ServiceConstants.STAFF_TYPE);
				LinkedHashMap<Long, PersonListDto> map = new LinkedHashMap<Long, PersonListDto>();
				LinkedHashMap<Long, ClosureNoticeListDto> map2 = new LinkedHashMap<Long, ClosureNoticeListDto>();
				List<ClosureNoticeListDto> closureNoticeDtoList = new ArrayList<>();
				for (PersonListDto persons : personListByStage) {
					map.put(persons.getIdPerson(), persons);
				}
				for (ClosureNoticeListDto peeps : cpsInvNoticesRes.getClosureNoticeListDto()) {
					map2.put(peeps.getIdPerson(), peeps);
				}
				for (Entry<Long, PersonListDto> persons : map.entrySet()) {
					if (!map2.containsKey(persons.getKey())) {
						ClosureNoticeListDto closureNotice = new ClosureNoticeListDto();
						if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getIdPerson())
								&& !TypeConvUtil.isNullOrEmpty(persons.getValue().getStagePersRelInt())) {
							closureNotice.setIdPerson(persons.getValue().getIdPerson());
							if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getPersonFull())) {
								closureNotice.setNmPersonFull(!StringUtils.isEmpty(persons.getValue().getPersonSuffix())
										? persons.getValue().getPersonFull() + " "
												+ persons.getValue().getPersonSuffix()
										: persons.getValue().getPersonFull());
							}
							if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getStagePersRole())) {
								closureNotice.setCdStagePersRole(persons.getValue().getStagePersRole());
							}
							if (!TypeConvUtil.isNullOrEmpty(persons.getValue().getStagePersType())) {
								closureNotice.setCdStagePersType(persons.getValue().getStagePersType());
							}
							closureNotice.setCdStagePersRelInt(persons.getValue().getStagePersRelInt());
							closureNoticeDtoList.add(closureNotice);
						}
					}
				}
				cpsInvNoticesRes.getClosureNoticeListDto().addAll(closureNoticeDtoList);
			}
		} else if (cpsInvNoticesRes.getClosureNoticeListDto().size() == ServiceConstants.LONG_ZERO_VAL) {
			personListByStage = personDao.getPersonListByIdStage(commonHelperReq.getIdStage(),
					ServiceConstants.STAFF_TYPE);
			List<ClosureNoticeListDto> list1 = new ArrayList<>();
			personListByStage.forEach(o -> {
				ClosureNoticeListDto p = new ClosureNoticeListDto();
				p.setCdStagePersRelInt(o.getStagePersRelInt());
				p.setNmPersonFull(!StringUtils.isEmpty(o.getPersonSuffix())
						? o.getPersonFull() + " " + o.getPersonSuffix() : o.getPersonFull());
				p.setCdStagePersRole(o.getStagePersRole());
				p.setCdStagePersType(o.getStagePersType());
				p.setIdPerson(o.getIdPerson());
				list1.add(p);
			});
			cpsInvNoticesRes.setClosureNoticeListDto(list1);
		}
		return cpsInvNoticesRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CommonBooleanRes getCompletedAssessmentsExists(CommonHelperReq commonHelperReq) {
		CommonBooleanRes response = new CommonBooleanRes();
		Boolean exists = cnlsnDao.getCompletedAssessmentCount(commonHelperReq.getIdCase());
		response.setExists(exists);
		return response;
	}

	/**
	 * Method Description : To Sort the closure Notice List Dto
	 * 
	 * @param closureNoticeListDtoList
	 * @return List<ClosureNoticeListDto>
	 */
	private List<ClosureNoticeListDto> sortCLosureList(List<ClosureNoticeListDto> closureNoticeListDtoList) {

		if (ObjectUtils.isEmpty(closureNoticeListDtoList)) {
			return closureNoticeListDtoList;
		}
		List<ClosureNoticeListDto> reporter = closureNoticeListDtoList.stream()
				.filter(p -> (ServiceConstants.Y).equalsIgnoreCase(p.getIndStagePersReporter()))
				.collect(Collectors.toList());
		List<ClosureNoticeListDto> victim = closureNoticeListDtoList.stream()
				.filter(p -> (ServiceConstants.PRIMARY_CHILD).equalsIgnoreCase(p.getCdStagePersRole()))
				.collect(Collectors.toList());
		List<ClosureNoticeListDto> prn = closureNoticeListDtoList.stream()
				.filter(p -> ((ServiceConstants.PRINCIPAL).equalsIgnoreCase(p.getCdStagePersType())
						&& !(ServiceConstants.Y).equalsIgnoreCase(p.getIndStagePersReporter())
						&& !(ServiceConstants.PRIMARY_CHILD).equalsIgnoreCase(p.getCdStagePersRole())
						&& null != p.getDtPersonBirth()))
				.collect(Collectors.toList());
		List<ClosureNoticeListDto> prnNoBirthDate = closureNoticeListDtoList.stream()
				.filter(p -> ((ServiceConstants.PRINCIPAL).equalsIgnoreCase(p.getCdStagePersType())
						&& !(ServiceConstants.Y).equalsIgnoreCase(p.getIndStagePersReporter())
						&& !(ServiceConstants.PRIMARY_CHILD).equalsIgnoreCase(p.getCdStagePersRole())
						&& null == p.getDtPersonBirth()))
				.collect(Collectors.toList());
		List<ClosureNoticeListDto> col = closureNoticeListDtoList.stream()
				.filter(p -> ((ServiceConstants.COLLATERAL).equalsIgnoreCase(p.getCdStagePersType())
						&& !(ServiceConstants.Y).equalsIgnoreCase(p.getIndStagePersReporter())
						&& !(ServiceConstants.PRIMARY_CHILD).equalsIgnoreCase(p.getCdStagePersRole())
						&& null != p.getNmPersonFull()))
				.collect(Collectors.toList());
		List<ClosureNoticeListDto> colNoName = closureNoticeListDtoList.stream()
				.filter(p -> ((ServiceConstants.PRINCIPAL).equalsIgnoreCase(p.getCdStagePersType())
						&& !(ServiceConstants.Y).equalsIgnoreCase(p.getIndStagePersReporter())
						&& !(ServiceConstants.PRIMARY_CHILD).equalsIgnoreCase(p.getCdStagePersRole())
						&& null == p.getNmPersonFull()))
				.collect(Collectors.toList());

		List<ClosureNoticeListDto> sortedPrn = prn.stream()
				.sorted((a, b) -> (b.getDtPersonBirth()).compareTo((a.getDtPersonBirth())))
				.collect(Collectors.toList());
		List<ClosureNoticeListDto> sortedCol = col.stream()
				.sorted((object1, object2) -> object1.getNmPersonFull().compareTo(object2.getNmPersonFull()))
				.collect(Collectors.toList());

		List<ClosureNoticeListDto> res = new ArrayList<>();
		res.addAll(reporter);
		res.addAll(victim);
		res.addAll(sortedPrn);
		res.addAll(prnNoBirthDate);
		res.addAll(sortedCol);
		res.addAll(colNoName);
		return res;

	}

	/**
	 * Method Name: fetchAllegQuestionAnswers Method Description: This method
	 * gets ID, DOB, and DOD for victim on an allegation.
	 * 
	 * @param idStage
	 * @return boolean
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Boolean fetchAllegQuestionAnswers(Long idStage) {
		boolean result = false;
		result = cnlsnDao.fetchAllegQuestionAnswers(idStage);
		return result;
	}

	/**
	 * Method Name: savePrintPerson Method Description: This method saves the
	 * Print Staff for conclusion event.
	 * 
	 * @param cnclsnPrintStaffReq
	 */
	@Override
	public CommonStringRes savePrintPerson(CnclsnPrintStaffReq cnclsnPrintStaffReq) {
		Long idEvent = cnclsnPrintStaffReq.getIdEvent();
		Long idPrintPerson = cnclsnPrintStaffReq.getIdPrintPerson();
		Long idApprover = cnclsnPrintStaffReq.getIdApprover();
		Boolean indSaveSuccessful = cnlsnDao.savePrintPerson(idEvent, idPrintPerson, idApprover);
		CommonStringRes commonStringRes = new CommonStringRes();
		commonStringRes.setCommonRes(indSaveSuccessful ? ServiceConstants.SUCCESS : ServiceConstants.CONTACT_FAILURE);
		return commonStringRes;
	};

	@Override
	public ApprovalPersonPrintDto getPrintPerson(CommonHelperReq commonHelperReq) {
		ApprovalPersonPrintDto printPerson = new ApprovalPersonPrintDto();
		Long idEvent = commonHelperReq.getIdEvent();
		Long idApprover = commonHelperReq.getIdPerson();
		Long idPerson = ServiceConstants.ZERO_VAL;
		idPerson = cnlsnDao.getPrintPerson(idEvent, idApprover);
		if (ServiceConstants.ZERO_VAL < idPerson) {
			Person person = personDao.getPerson(idPerson);
			printPerson.setNmFullPerson(person.getNmPersonFull());
			printPerson.setIdPerson(idPerson);
		}
		return printPerson;
	}

	/**
	 * Method Name: validateInvStageClosure Method Description: Service Class
	 * method which validates Saved details on Investigation Conclusion Page for
	 * stage closure. This method invokes all the validations included in
	 * CINV15S and CpsInvCnlsnBean Ejb of Legacy application and returns the
	 * applicable error message list.
	 * 
	 * @param invStageClosureReq
	 * @return validationResponse
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
	public CpsInvCnclsnRes validateInvStageForClosure(CpsInvCnclsnReq invStageClosureReq) {
		log.info("validateInvStageForClosure method for CpsInvCnlsnService Started for Investigation Validation ");
		CpsInvCnclsnRes validationResponse = new CpsInvCnclsnRes();
		Long idStage = invStageClosureReq.getCpsInvConclValiDto().getIdStage();
		Long idCase = invStageClosureReq.getCpsInvConclValiDto().getIdCase();
		// check and set the Person In FDTC Flag.
		boolean startsFDTCFlag = false;
		HashMap personInFDTCEdit = getMostRecentFDTCSubtype(idCase, idStage);
		if (!ObjectUtils.isEmpty(personInFDTCEdit)) {
			startsFDTCFlag = true;
		}
		validationResponse.setStartsFDTCFlag(startsFDTCFlag);
		validationResponse.setPersonInFDTCEdit(personInFDTCEdit);
		String cdReasonClosed = invStageClosureReq.getCpsInvConclValiDto().getCdStageReasonClosed();
		List<Long> errorList = new ArrayList<>();
		// Call the cpsInv Conclusion validation service equivalent to cinv15s
		// service of Legacy.
		CpsInvConclValoDto cpsInvConclValoDto = cpsInvConclValService
				.cpsInvConclValidationService(invStageClosureReq.getCpsInvConclValiDto());
		errorList = cpsInvConclValoDto.getUsSysNbrMessageCode();
		validationResponse.setCpsInvConclValoDto(cpsInvConclValoDto);
		// Call the Validate InvStage Closure method on top of above
		// validations. This is equivalent to validations included in
		// CpsInvCnclsnBean EJB of Legacy application.
		errorList = validateInvStageClosure(invStageClosureReq.getCpsInvConclValiDto(), errorList,
				invStageClosureReq.getIdHouseholdPerson());
		// Check all other validations and set the corrosponding Boolean.
		boolean crimHistPending = crimHistDao.isCrimHistPending(idStage);
		boolean eventStatusNew = cnlsnDao.isEventStatusNew(idStage);
		boolean isChildSexLaborTraffic = cnlsnDao.isChildSexLaborTrafficking(idCase, idStage);
		boolean relqshAnswrd = cnlsnDao.isRlnqshAnswrd(idStage, idCase);
		boolean persMpsSearchRqd = personMpsDao.getNbrMPSPersStage(idStage, ServiceConstants.Y);

		// Create Common Helper Request to call the PCSP services.
		List<Long> pcspErrors = new ArrayList<>();
		CommonHelperReq cmnReq = new CommonHelperReq();
		cmnReq.setIdCase(idCase);
		cmnReq.setIdStage(idStage);
		cmnReq.setCdStage(invStageClosureReq.getCdStage());
		cmnReq.setCdStageReasonClosed(cdReasonClosed);
		PCSPAssessmentRes pcspAssessmentRes = pcspService.valPCSPPlcmt(cmnReq);
		if (!ObjectUtils.isEmpty(pcspAssessmentRes) && pcspAssessmentRes.getValPCSPPlcmtErr() > 0) {
			pcspErrors.add(Long.valueOf(pcspAssessmentRes.getValPCSPPlcmtErr()));
		}
		if (pcspListPlcmntDao.hasOpenPCSPAsmntForStage(idStage)) {
			pcspErrors.add(Long.valueOf(ServiceConstants.MSG_PCSP_VAL_ASMNT_COMP_OR_DEL));
		}
		boolean babyMosesError = false;
		if (CodesConstant.CCRSKSDM_106.equals(cdReasonClosed)
				&& !cnsrvtrshpRmvlDao.babyMosesRemovalReasonExists(idStage).getExists()) {
			babyMosesError = true;
		}

		// ADS Change for SXTR/LBTR Allegation.
		Allegation algtn = cnlsnDao.checkTrfckngRecPend(idStage);
		
		//check svh for ALLEG-SXAB/RTB artf131180
		if(cnlsnDao.checkSxHistIndForAllegs(idStage)) {
			addErrorMessageCode(ServiceConstants.MSG_RTB_SXAB_SVH_INCONSISTENT, errorList);
		}
		

		// Check for the errors and set the error list accordingly.
		if (!ObjectUtils.isEmpty(errorList) || persMpsSearchRqd || crimHistPending || eventStatusNew
				|| !isChildSexLaborTraffic || !relqshAnswrd || babyMosesError || pcspErrors.size() > 0
				|| !ObjectUtils.isEmpty(algtn)) {
			if (!StringUtils.isEmpty(cpsInvConclValoDto.getIdPerson()) && cpsInvConclValoDto.getIdPerson() != 0) {
				HashMap personDetail = crimHistDao.checkCrimHistAction(idStage);
				if (!ObjectUtils.isEmpty(personDetail)) {
					validationResponse.setPersonDetail(personDetail);
				}
			}
			if (persMpsSearchRqd) {
				addErrorMessageCode(ServiceConstants.MSG_MPS_PERSON_NOT_SEARCHED, errorList);
			}
			if (personUtilityDao.getPRNRaceEthnicityStat(idStage)) {
				addErrorMessageCode(ServiceConstants.MSG_INV_NO_RAC_STAT, errorList);
			}
			if (startsFDTCFlag) {
				addErrorMessageCode(ServiceConstants.MSG_ERR_FDTC_CLOSURE, errorList);
			}
			if (cnlsnDao.fetchAllegQuestionAnswers(idStage)) {
				addErrorMessageCode(ServiceConstants.MSG_CF_ALLEG_REQ, errorList);
			}
			if (cnlsnDao.validateDispAndCFQuestion(idStage)) {
				addErrorMessageCode(ServiceConstants.MSG_NO_CF_SEVERITY_FTL, errorList);
			}
			if (crimHistPending) {
				addErrorMessageCode(ServiceConstants.MSG_CRML_HIST_CHECK_STAGE_PEND, errorList);
			}
			if (eventStatusNew) {
				addErrorMessageCode(ServiceConstants.MSG_SVC_AUTH_STATUS_NEW, errorList);
			}
			if (!isChildSexLaborTraffic) {
				addErrorMessageCode(ServiceConstants.MSG_CHILD_SXTR_LBTR_REQ, errorList);
			}
			if (!relqshAnswrd) {
				addErrorMessageCode(ServiceConstants.MSG_RELNQSH_CSTDY_MISSED, errorList);
			}
			if (pcspErrors.size() > 0) {
				errorList.addAll(pcspErrors);
			}
			if (babyMosesError) {
				addErrorMessageCode(ServiceConstants.MSG_CLOSE_BABY_MOSES_ERR, errorList);
			}
			
			

			// ADS Change for SXTR/LBTR Allegation.
			if (!ObjectUtils.isEmpty(algtn) && ObjectUtils.isEmpty(validationResponse.getPersonDetail())) {
				HashMap victim = new HashMap<>();
				victim.put(ServiceConstants.ID_PERSON_CONST, algtn.getPersonByIdVictim().getIdPerson());
				victim.put(ServiceConstants.NAMEPERSON, algtn.getPersonByIdVictim().getNmPersonFull());
				validationResponse.setPersonDetail(victim);
				switch (algtn.getCdAllegDisposition()) {
				case CodesConstant.CDISPSTN_RTB:
					addErrorMessageCode(ServiceConstants.MSG_INVRTB_TRAFFICKING, errorList);
					break;
				case CodesConstant.CDISPSTN_UTD:
					addErrorMessageCode(ServiceConstants.MSG_INVUTD_TRAFFICKING, errorList);
					break;
				default:
					break;
				}
			}
		}
		validationResponse.setNbrErrorMsgList(errorList);
		log.info("validateInvStageForClosure method for CpsInvCnlsnService return response " + errorList.size());
		return validationResponse;
	}

	/**
	 * 
	 * Method Name: getMostRecentFDTCSubtype Method Description:The method
	 * returns the most recent FDTC Subtype and Outcome date for a Person in a
	 * given case id It is merged from Ejb methods getPersonIdInFDTC &
	 * getMostRecentFDTCSubtype to return the required value in single call.
	 * 
	 * @param idCase
	 * @param idStage
	 * @return response
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
	public HashMap getMostRecentFDTCSubtype(Long idCase, Long idStage) {
		HashMap hashMapFDTC = new HashMap();
		HashMap personInFDTCEdit = new HashMap();
		String mostRecentFDTCSubType = ServiceConstants.EMPTY_STRING;
		String personFullName = ServiceConstants.EMPTY_STRING;
		Long idFDTCStage = ServiceConstants.ZERO_VAL;
		HashMap<Long, String> personFDTCMap = cnlsnDao.getPersonIdInFDTC(idCase);
		for (Map.Entry<Long, String> entry : personFDTCMap.entrySet()) {
			Long personId = entry.getKey();
			hashMapFDTC = cnlsnDao.getMostRecentFDTCSubtype(personId);
			personFullName = (String) personFDTCMap.get(personId);
			if (!hashMapFDTC.isEmpty()) {
				mostRecentFDTCSubType = (String) hashMapFDTC.get(ServiceConstants.CD_LEGAL_ACT_ACTN_SUBTYPE);
				idFDTCStage = Long.parseLong((String) hashMapFDTC.get(ServiceConstants.ID_EVENT_STAGE));
			}
			if (CodesConstant.CFDT_010.equalsIgnoreCase(mostRecentFDTCSubType)
					|| (CodesConstant.CFDT_030.equalsIgnoreCase(mostRecentFDTCSubType)
							&& !idStage.equals(idFDTCStage))) {
				personInFDTCEdit.put(personId.toString(), personFullName);
			}
		}
		return personInFDTCEdit;

	}

	/**
	 * Method Name: fetchAllegQuestionAnswers Method Description: This method
	 * returns if all questions have been answered.
	 * 
	 * @param cpsInvCnclsnReq
	 * @return CommonBooleanRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
	public CommonBooleanRes fetchAllegQuestionYAnswers(CpsInvCnclsnReq cpsInvCnclsnReq) {
		CommonBooleanRes resp = new CommonBooleanRes();
		resp.setExists(cnlsnDao.fetchAllegQuestionYAnswers(cpsInvCnclsnReq.getIdStage()));
		return resp;

	}

	/**
	 * Method Name: validateRlqshCstdyAndPrsnCharQuestion Method Description:
	 * This method returns the value if for any victim Rlqsh Custody question is
	 * answered as yes and person characterstics are ansered accordingly. This
	 * is combination of the ejb method getRlnqshAnswrdYVictimIds &
	 * validateRlqshCstdyAndPrsnCharQuestion
	 * 
	 * @param cpsInvCnlsnReq
	 * @return returnValue
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false)
	public Boolean validateRlqshCstdyAndPrsnCharQuestion(CpsInvCnclsnReq cpsInvCnlsnReq) {
		Boolean returnValue = Boolean.TRUE;
		List<Long> victimIds = cnlsnDao.getRlnqshAnswrdYVictimIds(cpsInvCnlsnReq.getIdStage());
		if (!ObjectUtils.isEmpty(victimIds)) {
			List<Long> personIds = cnlsnDao.validateRlqshCstdyAndPrsnCharQuestion(cpsInvCnlsnReq.getIdStage());
			if (!ObjectUtils.isEmpty(personIds) && personIds.containsAll(victimIds)) {
				returnValue = Boolean.TRUE;
			} else {
				returnValue = Boolean.FALSE;
			}
		}
		return returnValue;
	}

	/**
	 * Method Name: validateDispAndCFQuestion Method Description: This method
	 * returns true if atleast one of the relinquish allegation cstdy is
	 * answered yes.
	 * 
	 * @param idVictim
	 * @param idStage
	 * @return boolean
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
	public boolean rlnqushQuestionAnsweredY(Long idStage, Long idVictim) {
		boolean result = false;

		result = cnlsnDao.rlnqushQuestionAnsweredY(idStage, idVictim);
		return result;
	}

	/**
	 * Method Name: isDispositionMissing Method Description:Retrieve true if any
	 * of allegation(s) for the input stage is missing a disposition
	 * 
	 * @param cpsInvCnclsnReq
	 * @return CommonBooleanRes
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
	@Override
	public CommonBooleanRes isDispositionMissing(CpsInvCnclsnReq cpsInvCnclsnReq) {
		CommonBooleanRes resp = new CommonBooleanRes();
		resp.setExists(cnlsnDao.isDispositionMissing(cpsInvCnclsnReq.getIdStage()));
		return resp;
	}

	/**
	 * Method Name: validateInvStageClosure Method Description: This method
	 * implements business logic for stage closure validations related to all
	 * the events in a particular stage
	 * 
	 * @param sysNbrMessageCodeArray
	 * @param invStageClosureDto
	 * @param cpsSecondaryApprovalRequired
	 * @param riskAssmValRequired
	 * @return errorList
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
	private List<Long> validateInvStageClosure(CpsInvConclValiDto invStageClosureDto, List<Long> errorList,
			Long idHshldPrsn) {
		Long stageId = invStageClosureDto.getIdStage();
		String editConfig = ServiceConstants.EMPTY_STRING;
		if (!TypeConvUtil.isNullOrEmpty(invStageClosureDto.getDcdEditProcess())) {
			editConfig = invStageClosureDto.getDcdEditProcess();
		}
		// Check if Second Level Approval is require for Cps Investigation Stage
		// Closure. //This peice is available in cpsInvBusinessDelegateUtil in
		// R1.1
		SecondaryApprovalDto secAppDto = new SecondaryApprovalDto();
		secAppDto.setCdStageProgram(CodesConstant.CPRTRLS_CPS);
		secAppDto.setCdStage(CodesConstant.CPURPRSK_INV);
		secAppDto.setCdTaskCode(ServiceConstants.INV_CONCLUSION_TASK_CODE);
		secAppDto.setIdCase(invStageClosureDto.getIdCase().intValue());
		secAppDto.setIdStage(invStageClosureDto.getIdStage().intValue());
		secAppDto.setCdStageReasonClose(invStageClosureDto.getCdStageReasonClosed());
		// Call the method to validate new 2nd Level approval required rules to
		// check if 2nd level approval required.
		boolean secondLvlAppRqrd = approvalStatusService.isSecondLevelApproverRequiredForCPSINV(secAppDto);

		// Check if idHshldPrsn is available in the Req. (for the Phase II pre
		// release cases,
		// idHshldPrsn should be null and post release it should contain the
		// value of household selected during
		// Conclusion.
		cpsSDMSafetyAssmntInvValidations(editConfig, errorList, invStageClosureDto, secondLvlAppRqrd, idHshldPrsn);
		// perform risk assessment related validations
		if (ServiceConstants.Y.equals(invStageClosureDto.getIndSDM())) {
			cpsSDMRiskAssmntInvValidations(editConfig, errorList, stageId, idHshldPrsn);
		}
		return errorList;

	}

	/**
	 * Method Name: cpsSDMRiskAssmntInvValidations Method Description:This
	 * method implements business logic for stage closure validations related to
	 * Risk assessments for Investigation stage and adds the validation messages
	 * in the errorList
	 * 
	 * @param editConfig
	 * @param errorList
	 * @param stageId
	 */
	private void cpsSDMRiskAssmntInvValidations(String editConfig, List<Long> errorList, Long stageId,
			Long idHshldPrsn) {
		boolean isSdmRiskCompltd = false;
		// check if SDM Risk Assessment Complete ,if not add error message to
		// error list
		if (!ObjectUtils.isEmpty(editConfig)
				&& ServiceConstants.CHAR_Y == editConfig.charAt(ServiceConstants.MSG_SDM_RA_REQ_Nbr)) {
			if (TypeConvUtil.isNullOrEmpty(idHshldPrsn)) {
				isSdmRiskCompltd = cnlsnDao.sdmRiskAssmComp(stageId);
			} else {
				isSdmRiskCompltd = cnlsnDao.sdmRiskAssmCompForHousehold(stageId, idHshldPrsn);
			}

			if (!isSdmRiskCompltd) {
				addErrorMessageCode(ServiceConstants.MSG_SDM_RA_REQ, errorList);
			}
		}
	}

	/**
	 * Method Name: cpsSDMRiskAssmntInvValidations Method Description:This
	 * method implements business logic for stage closure validations related to
	 * Investigation stage and adds the validation messages in the errorList
	 * 
	 * @param editConfig
	 * @param errorList
	 * @param stageId
	 */
	private void cpsSDMSafetyAssmntInvValidations(String editConfig, List<Long> errorList,
			CpsInvConclValiDto invStageClosureDto, Boolean cpsSecondaryApprovalRequired, Long idHshldPrsn) {
		Long idStage = invStageClosureDto.getIdStage();
		Long idCase = invStageClosureDto.getIdCase();
		Map<Long, String> safetyDecsnAndIdCpsMap = null;
		String safetyDecision = null;
		Long idCps = 0L;

		Long pcspSelectedCount = 0L;
		Long pcspInterventionSelectedCount = 0L;

		Long initialSDMSafetyAsmntCount = cnlsnDao.getInitialSDMSafetyAssmntCount(idStage);
		Long initialARSafetyAsmntCount = cnlsnDao.getPriorARInitialSafetyAssmntCount(idStage);
		Long initialARSafetyAsmntCountNew = cnlsnDao.getPriorARInitialSafetyAssmntCountNew(idStage);
		Long sevenDaySafetyAsmntCount = cnlsnDao.getSevenDaySafetyAssmntCount(idStage);

		Long inCompSDMSafetyAsmntCount = cnlsnDao.getInCompSDMSafetyAssmntCount(idStage);
		Long removalRecordCount = ServiceConstants.ZERO_VAL;
		Long pcspCount = ServiceConstants.ZERO_VAL;
		boolean hasPlacement = false;
		// Check if the household is present in the conclusion and check removal
		// count accordingly.
		if (TypeConvUtil.isNullOrEmpty(idHshldPrsn)) {
			removalRecordCount = cnlsnDao.getRemovalRecordCount(idStage);
			safetyDecsnAndIdCpsMap = cnlsnDao.getSafetyDecisionAndIdCps(idStage);
			pcspCount = cnlsnDao.getPcspCount(idStage);
			hasPlacement = pcspPlcmntDao.hasPlcmntForStage(idStage);
		} else {
			removalRecordCount = cnlsnDao.getRemovalRecordCountForHousehold(idStage, idHshldPrsn);
			safetyDecsnAndIdCpsMap = cnlsnDao.getSafetyDecisionAndIdCpsForHoushold(idStage, idHshldPrsn);
			pcspCount = cnlsnDao.getPcspCountForCase(idStage, idCase);
			hasPlacement = pcspPlcmntDao.hasPlacementForCase(idCase);
		}

		Set<Map.Entry<Long, String>> set = safetyDecsnAndIdCpsMap.entrySet();
		// Get an iterator
		Iterator<Entry<Long, String>> i = set.iterator();

		while (i.hasNext()) {
			Entry<Long, String> me = (Entry<Long, String>) i.next();
			idCps = ((Long) me.getKey());
			safetyDecision = (String) me.getValue();
		}
		pcspSelectedCount = cnlsnDao.pcspSelectedCount(idCps);
		pcspInterventionSelectedCount = cnlsnDao.cspInterventionCount(idStage);

		if (!ObjectUtils.isEmpty(editConfig)) {
			if (ServiceConstants.CHAR_Y == editConfig.charAt(ServiceConstants.MSG_INV_SDM_SFTY_ASSMNT_COMP_REQ_EDIT)) {
				if (ServiceConstants.ZERO_VAL == initialSDMSafetyAsmntCount
						&& initialARSafetyAsmntCount == ServiceConstants.ZERO_VAL
						&& ServiceConstants.ZERO_VAL == initialARSafetyAsmntCountNew // Defect 10803 - CHeck count of new AR assessments
						&& sevenDaySafetyAsmntCount == ServiceConstants.ZERO_VAL) {
					addErrorMessageCode(ServiceConstants.MSG_INV_SA_INIT_REQ, errorList);
				}
			}

			if (ServiceConstants.CHAR_Y == editConfig.charAt(ServiceConstants.MSG_SDM_SFTY_ASSMNT_COMP_REQ_EDIT)) {
				if (inCompSDMSafetyAsmntCount >= ServiceConstants.NUM_ONE) {
					addErrorMessageCode(ServiceConstants.MSG_COMP_SDM_SA_REQ, errorList);
				}
			}

			if (ServiceConstants.CHAR_Y == editConfig.charAt(ServiceConstants.MSG_CONSRVTRSHP_RMVL_REQ_EDIT)) {
				if (removalRecordCount == ServiceConstants.ZERO_VAL) {
					addErrorMessageCode(ServiceConstants.MSG_SDM_SA_REMOVAL_REQ, errorList);
				}
			}

			if (ServiceConstants.CHAR_Y == editConfig.charAt(ServiceConstants.MSG_PCSP_REQ_EDIT)) {
				if ((pcspCount == ServiceConstants.ZERO_VAL) && (!hasPlacement)) {
					addErrorMessageCode(ServiceConstants.MSG_SDM_SA_PCSP_REQ, errorList);
				}
			}
			if (ServiceConstants.CHAR_Y == editConfig.charAt(ServiceConstants.MSG_INV_SDM_SFTY_ASSMNT_SAFE_WITH_PLAN)) {
				if (ServiceConstants.CSDMDCSN_SAFEWITHPLAN.equalsIgnoreCase(safetyDecision)
						&& pcspSelectedCount > ServiceConstants.ZERO_VAL
						&& ((pcspInterventionSelectedCount == ServiceConstants.ZERO_VAL)
								&& (!pcspPlcmntDao.isPlcmntWithCrgvrAtCls(idStage)))) {

					addErrorMessageCode(ServiceConstants.MSG_SDM_PCSP_RQD, errorList);
				}
			}

			if (ServiceConstants.CHAR_Y == editConfig
					.charAt(ServiceConstants.MSG_CASE_CLOSRE_SDM_SFTY_ASSMNT_SAFE_REQ)) {
				if (safetyDecision == null && !ObjectUtils.isEmpty(cpsSecondaryApprovalRequired)
						&& cpsSecondaryApprovalRequired && sevenDaySafetyAsmntCount == ServiceConstants.NUM_ONE) {
					addErrorMessageCode(ServiceConstants.MSG_SDM_SA_STAGE_CLOSURE_RQRD, errorList);
				}
			}

			if (ServiceConstants.CHAR_Y == editConfig.charAt(ServiceConstants.MSG_INV_SDM_SFTY_ASSMNT_SAFE_REQ)) {
				if (ServiceConstants.CSDMDCSN_SAFEWITHPLAN.equalsIgnoreCase(safetyDecision)
						&& pcspSelectedCount == ServiceConstants.ZERO_VAL) {
					addErrorMessageCode(ServiceConstants.MSG_SDM_SA_SAFE_REQ, errorList);
				}
			}
		}

	}

	/**
	 * Method Name: addUsSysNbrMessageCode Method Description: add nbr number to
	 * NbrMessageCodeList
	 * 
	 * @param msgInvSaInitReq
	 * @param errorList
	 */
	private void addErrorMessageCode(int msgInvSaInitReq, List<Long> errorList) {

		if (errorList.size() >= 100) {
			throw new IndexOutOfBoundsException();
		}
		errorList.add(Long.valueOf(msgInvSaInitReq));

	}
	/**
	 * artf228543
	 * Method Name: hasAllegedPerpetratorWithAgeLessThanTen
	 * Method Description: This method checks if the given stage has any alleged perpetrator
	 * with age less than ten and disposition is not Admin Closure.
	 *
	 * @param cpsInvCnclsnReq
	 * @return CommonBooleanRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
	public CommonBooleanRes hasAllegedPerpetratorWithAgeLessThanTen(CpsInvCnclsnReq cpsInvCnclsnReq) {
		CommonBooleanRes resp = new CommonBooleanRes();
		resp.setExists(cnlsnDao.hasAllegedPerpetratorWithAgeLessThanTen(cpsInvCnclsnReq.getIdStage(), cpsInvCnclsnReq.getIdCase()));
		return resp;

	}

}
