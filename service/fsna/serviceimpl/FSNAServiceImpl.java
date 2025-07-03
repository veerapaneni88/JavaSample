package us.tx.state.dfps.service.fsna.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CpsFsna;
import us.tx.state.dfps.common.domain.CpsFsnaRspn;
import us.tx.state.dfps.common.domain.FamilyPlan;
import us.tx.state.dfps.common.domain.FamilyPlanEval;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.common.dto.UserProfileDto;
import us.tx.state.dfps.familyplan.request.FamilyPlanDtlEvalReq;
import us.tx.state.dfps.fsna.dto.CpsFsnaDomainLookupDto;
import us.tx.state.dfps.fsna.dto.CpsFsnaDto;
import us.tx.state.dfps.fsna.dto.CpsFsnaPrtyStrngthNeedDto;
import us.tx.state.dfps.fsna.dto.CpsFsnaRspnDto;
import us.tx.state.dfps.fsna.dto.CpsMonthlyEvalDto;
import us.tx.state.dfps.service.SDM.dao.SDMRiskAssessmentDao;
import us.tx.state.dfps.service.SDM.dao.SDMSafetyAssessmentDao;
import us.tx.state.dfps.service.SDM.service.SDMRiskAssessmentService;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonOutDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.casepackage.dao.PcspListPlacmtDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.EventPersonLinkDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.CpsMonthlyEvalReq;
import us.tx.state.dfps.service.common.request.EventReq;
import us.tx.state.dfps.service.common.request.FSNAAssessmentDtlGetReq;
import us.tx.state.dfps.service.common.request.PersonListReq;
import us.tx.state.dfps.service.common.request.SdmFsnaReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.FSNAAssessmentDtlGetRes;
import us.tx.state.dfps.service.common.response.FSNAValidationRes;
import us.tx.state.dfps.service.common.response.SDMRiskReassessmentRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.contact.dto.ContactListSearchDto;
import us.tx.state.dfps.service.contacts.dao.ContactSearchDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.familyplan.dao.FamilyPlanDao;
import us.tx.state.dfps.service.familyplan.dao.FamilyPlanDtlEvalDao;
import us.tx.state.dfps.service.familyplan.service.FamilyPlanService;
import us.tx.state.dfps.service.forms.dto.FamilyPlanDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CpsMonthlyEvalPrefillData;
import us.tx.state.dfps.service.forms.util.SDMFamilyStrengthsAndNeedsAssessmentFBSSPrefillData;
import us.tx.state.dfps.service.fsna.dao.FSNADao;
import us.tx.state.dfps.service.fsna.service.FSNAService;
import us.tx.state.dfps.service.investigation.dao.PcspDao;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentDto;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssessmentDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonInfoDto;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.person.service.PersonListService;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntDto;
import us.tx.state.dfps.service.riskreasmnt.dao.SDMRiskReassessmentDao;
import us.tx.state.dfps.service.riskreasmnt.service.SDMRiskReassessmentService;
import us.tx.state.dfps.service.workload.dao.ContactDao;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.EventListDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: May 3, 2018 - 12:59:59 PM
 */
@Service
public class FSNAServiceImpl implements FSNAService {

	@Autowired
	private FSNADao fSNADao;

	@Autowired
	PersonListService personListService;

	@Autowired
	PostEventService postEventService;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	ApprovalCommonService approvalService;

	@Autowired
	SDMSafetyAssessmentDao sdmSafetyAssessmentDao;

	@Autowired
	EventPersonLinkDao eventPersonLinkDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	ApprovalCommonService approvalCommonService;

	@Autowired
	SDMFamilyStrengthsAndNeedsAssessmentFBSSPrefillData sDMFamilyStrengthsAndNeedsAssessmentFBSSPrefillData;

	@Autowired
	CpsMonthlyEvalPrefillData cpsMonthlyEvalPrefillData;

	@Autowired
	PersonDao personDao;

	@Autowired
	CaseUtils caseUtils;

	@Autowired
	StageDao stageDao;

	@Autowired
	PcspDao pcspDao;

	@Autowired
	PcspListPlacmtDao pcspListPlacmtDao;

	@Autowired
	ContactSearchDao contactSearchDao;

	@Autowired
	ContactDao contactDao;

	@Autowired
	FamilyPlanDtlEvalDao familyPlanDtlEvalDao;

	@Autowired
	FamilyPlanDao familyPlanDao;

	@Autowired
	SDMRiskAssessmentDao sdmRiskAssessmentDao;

	@Autowired
	SDMRiskReassessmentDao sdmRiskReassessmentDao;

	@Autowired
	SDMRiskReassessmentService sdmRiskReassessmentService;
	
	@Autowired
	SDMRiskAssessmentService sdmRiskAssessmentService;

	@Autowired
	FamilyPlanService familyPlanService;

	public static final String PARENT_OR_CAREGIVER = "PRNT";
	public static final String CHILD = "CHLD";
	public static final String STRENGTH = "STRN";
	public static final String NEED = "NEED";
	public static final String LIST_INDEX_START = "<li>";
	public static final String LIST_INDEX_END = "</li>";
	public static final String PATTERN_CHECK = "; and";
	public static final String INITIAL_SAFETY_EVENT_DESCR = "FSNA - Initial";
	public static final String REASSESS_SAFETY_EVENT_DESCR = "FSNA - Reassessment";
	public static final int MSG_FSNA_PRIM_PROC_ST = 56934;
	public static final int MSG_FSNA_PRIM_SEC_ST = 56935;
	public static final int MSG_FSNA_ASMT_PRIOR = 56932;

	private static final String CLOSURE_TASK_CODE_FSU = "4110";
	private static final String CLOSURE_TASK_CODE_FRE = "5560";
	private static final String CLOSURE_TASK_CODE_FPR = "7010";
	/**
	 * This is the service to get the FSNA assessment details for display Method
	 * Description:
	 * 
	 * @param getFSNAAssessmentDtlReq
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public FSNAAssessmentDtlGetRes getFSNAAssessmentDtl(FSNAAssessmentDtlGetReq getFSNAAssessmentDtlReq) {
		FSNAAssessmentDtlGetRes fsnaAssessmentDtlGetRes = null;

		// Change stage to FSU when incoming stage is FRE. Domains for both
		// stages are same and mapping is available for FSU
		String cdStage = getFSNAAssessmentDtlReq.getCdStage();
		if (ServiceConstants.FAM_REUN_STG.equalsIgnoreCase(cdStage)) {
			cdStage = ServiceConstants.FAMILY_SUBCARE;
		}

		List<CpsFsnaDomainLookupDto> allDomains = null;
		CpsFsna cpsFsna = null;
		// If the event id is null, need to query for domain details to load the
		// page with blank data
		if (!ObjectUtils.isEmpty(getFSNAAssessmentDtlReq.getIdEvent()) && getFSNAAssessmentDtlReq.getIdEvent() != 0) {
			cpsFsna = fSNADao.getFSNAAsmt(getFSNAAssessmentDtlReq.getIdEvent());
		} else {
			allDomains = fSNADao.queryFsna(cdStage);
		}
		List<PersonListDto> persons = callPersonService(getFSNAAssessmentDtlReq.getIdStage());
		fsnaAssessmentDtlGetRes = formResponse(allDomains, cpsFsna, persons, cdStage);
		fsnaAssessmentDtlGetRes.getCpsFsnaDto().setIdStage(getFSNAAssessmentDtlReq.getIdStage());
		fsnaAssessmentDtlGetRes.getCpsFsnaDto().setIdCase(getFSNAAssessmentDtlReq.getIdCase());
		fsnaAssessmentDtlGetRes.getCpsFsnaDto().setCdTask(getFSNAAssessmentDtlReq.getCdTask());
		fsnaAssessmentDtlGetRes.getCpsFsnaDto().setCdStage(getFSNAAssessmentDtlReq.getCdStage());
		if (!ObjectUtils.isEmpty(getFSNAAssessmentDtlReq.getIdEvent()) && getFSNAAssessmentDtlReq.getIdEvent() != 0) {
			fsnaAssessmentDtlGetRes.getCpsFsnaDto().setIdEvent(getFSNAAssessmentDtlReq.getIdEvent());
			fsnaAssessmentDtlGetRes.getCpsFsnaDto()
					.setCdEventStatus(eventDao.getEventStatus(getFSNAAssessmentDtlReq.getIdEvent()));
			// Get previous assessment danger, worry and goal statements not
			// considering the current assessment. It should not be fetched for
			// Initial Assessments
			if (!StringUtils.isEmpty(cpsFsna.getCdAsgnmntType())
					&& !ServiceConstants.CD_ASSMT_TYPE_INIT.equalsIgnoreCase(cpsFsna.getCdAsgnmntType())) {
				CpsFsnaDto preViousDto = fSNADao.getPerviousStatements(getFSNAAssessmentDtlReq.getIdStage(),
						cpsFsna);
				fsnaAssessmentDtlGetRes.getCpsFsnaDto().setPreviousTxtDngrWorry(preViousDto.getPreviousTxtDngrWorry());
				fsnaAssessmentDtlGetRes.getCpsFsnaDto()
						.setPrevioustxtGoalStatmnts(preViousDto.getPrevioustxtGoalStatmnts());
			}
		} 
		// created by and last updated by person full name
		Long idPersonCreated = null;
		if (ObjectUtils.isEmpty(fsnaAssessmentDtlGetRes.getCpsFsnaDto().getIdCreatedPerson())) {
			idPersonCreated = getFSNAAssessmentDtlReq.getUserProfileDB().getIdUser();
			fsnaAssessmentDtlGetRes.getCpsFsnaDto().setDtCreated(new Date());
		} else
			idPersonCreated = fsnaAssessmentDtlGetRes.getCpsFsnaDto().getIdCreatedPerson();
		fsnaAssessmentDtlGetRes.getCpsFsnaDto()
				.setCreatedBy(personDao.getPersonById(idPersonCreated).getNmPersonFull());
		Long idPersonupdated = null;
		if (ObjectUtils.isEmpty(fsnaAssessmentDtlGetRes.getCpsFsnaDto().getIdLastUpdatePerson())) {
			fsnaAssessmentDtlGetRes.getCpsFsnaDto().setUpdatedBy(ServiceConstants.EMPTY_STR);
		} else {
			idPersonupdated = fsnaAssessmentDtlGetRes.getCpsFsnaDto().getIdLastUpdatePerson();
			fsnaAssessmentDtlGetRes.getCpsFsnaDto()
					.setUpdatedBy(personDao.getPersonById(idPersonupdated).getNmPersonFull());
		}
		return fsnaAssessmentDtlGetRes;
	}

	// This method is to call personService to get person List
	private List<PersonListDto> callPersonService(long idStage) {
		PersonListReq retrievePersonListReq = new PersonListReq();
		retrievePersonListReq.setIdStage(idStage);
		List<PersonListDto> persons = new ArrayList<>();
		personListService.getPersonList(retrievePersonListReq).getRetrievePersonList().stream().forEach(p -> {
			if (CodesConstant.CPRSNALL_PRN.equals(p.getStagePersType())) {
				if ((ObjectUtils.isEmpty(p.getPersonAge()) || p.getPersonAge() == 0)) {
					p.setPersonAge(DateUtils.getAge(p.getDtPersonBirth()));
				}

				persons.add(p);
			}
		});

		persons.stream().sorted(Collections.reverseOrder());

		return persons;
	}

	// this method is to form the response object from
	// List<CpsFsnaDomainLookup>,cpsFsna and List<PersonListDto>
	private FSNAAssessmentDtlGetRes formResponse(List<CpsFsnaDomainLookupDto> allDomains, CpsFsna cpsFsna,
			List<PersonListDto> persons, String cdStage) {

		List<CpsFsnaDomainLookupDto> allDomainDtos = new ArrayList<>();
		List<CpsFsnaPrtyStrngthNeedDto> strensNeeds = new ArrayList<>();
		Set<Long> savedChildAssessed = new HashSet<>();
		CpsFsnaDto cpsFsnaDto = new CpsFsnaDto();
		List<PersonInfoDto> evntPersons = new ArrayList<>();
		// loading the existing assessment
		if (!ObjectUtils.isEmpty(cpsFsna)) {
			BeanUtils.copyProperties(cpsFsna, cpsFsnaDto);
			Set<CpsFsnaRspn> responses = cpsFsna.getResponses();

			evntPersons = eventPersonLinkDao.getPrsnListByEventId(cpsFsna.getEvent().getIdEvent());
			evntPersons.stream().forEach(eventPerson -> savedChildAssessed.add(eventPerson.getIdPerson()));
			// looping though the responses records and map to dto
			if (!ObjectUtils.isEmpty(responses)) {
				// group the responses by idCpsFsnaDomainLookup
				Map<Long, List<CpsFsnaRspn>> differentDomainsMap = responses.stream()
						.collect(Collectors.groupingBy(CpsFsnaRspn::getIdCpsFsnaDomainLookup));
				// get the domain details by idCpsFsnaDomainLookup
				List<CpsFsnaDomainLookupDto> domains = fSNADao.getDominLookUp(cdStage);
				differentDomainsMap.entrySet().forEach(domainEntry -> {
					// loading the domain details for existing record
					CpsFsnaDomainLookupDto cpsFsnaDomainLookupDto = domains.stream()
							.filter(p -> p.getIdCpsFsnaDomainLookup().equals(domainEntry.getKey())).findFirst()
							.orElse(null);
					if (ObjectUtils.isEmpty(cpsFsnaDto.getIdVersion()))
						cpsFsnaDto.setIdVersion(cpsFsnaDomainLookupDto.getNbrVersion());
					String cdFSNADmn = cpsFsnaDomainLookupDto.getCdFsnaDmn();
					cpsFsnaDomainLookupDto.setCdFsnaDmn(cdFSNADmn);
					String[] nmArray = cpsFsnaDomainLookupDto.getNmDefntn().split("\\; and");
					StringBuilder nmDefntn = new StringBuilder();
					// describe is stored as single column
					if (nmArray.length > 0)
						if (cpsFsnaDomainLookupDto.getNmDefntn().contains(PATTERN_CHECK))
							nmDefntn.append(LIST_INDEX_START).append(nmArray[0]).append(PATTERN_CHECK)
									.append(LIST_INDEX_END);
						else
							nmDefntn = nmDefntn.append(LIST_INDEX_START).append(nmArray[0]).append(LIST_INDEX_END);
					if (nmArray.length > 1) {
						nmDefntn = nmDefntn.append(LIST_INDEX_START).append(nmArray[1]).append(LIST_INDEX_END);
					}
					cpsFsnaDomainLookupDto.setNmDefntn(nmDefntn.toString());

					List<CpsFsnaRspnDto> responseDtos = new ArrayList<>();
					// Loading more responses for the Domain ( could be primary/
					// secondary parent/ caregiver or child responses)
					domainEntry.getValue().stream().forEach(rspn -> {
						CpsFsnaRspnDto rspsDto = new CpsFsnaRspnDto();
						BeanUtils.copyProperties(rspn, rspsDto);
						responseDtos.add(rspsDto);
						// if response has strength need record map that to
						// CpsFsnaPrtyStrngthNeedDto
						if (!ObjectUtils.isEmpty(rspn.getCpsFsnaPrtyStrngthNeed())) {
							CpsFsnaPrtyStrngthNeedDto strngthNeedDto = new CpsFsnaPrtyStrngthNeedDto();
							BeanUtils.copyProperties(rspn.getCpsFsnaPrtyStrngthNeed(), strngthNeedDto);
							strngthNeedDto.setIdCpsFsnaRspns(rspsDto.getIdCpsFsnaRspns());
							strngthNeedDto.setNmDomain(cdFSNADmn);
							strngthNeedDto.setIdCpsFsnaDomainLookup(cpsFsnaDomainLookupDto.getIdCpsFsnaDomainLookup());
							strngthNeedDto.setCdStrengthOrNeed(rspn.getCdAnswr());
							// to differentiate strength or need in the jsp
							if (STRENGTH.equals(rspsDto.getCdAnswr())) {
								strngthNeedDto.setCdStrengthOrNeed(STRENGTH);
							} else if (NEED.equals(rspsDto.getCdAnswr())) {
								strngthNeedDto.setCdStrengthOrNeed(NEED);
							}
							strngthNeedDto.setIdRelPerson(rspn.getIdRelPrsn());
							strensNeeds.add(strngthNeedDto);
						}

					});
					// to order primary care giver first and then secondary
					if (!CollectionUtils.isEmpty(responseDtos) && responseDtos.size() > 1) {
						if (!cpsFsnaDto.getIdPrmryCrgvrPrnt().equals(responseDtos.get(0).getIdRelPrsn())
								&& PARENT_OR_CAREGIVER.equalsIgnoreCase(responseDtos.get(0).getCdRelPrsnType())) {
							CpsFsnaRspnDto cpsFsnaResponse = responseDtos.get(0);
							responseDtos.set(0, responseDtos.get(1));
							responseDtos.set(1, cpsFsnaResponse);
						}
					}
					cpsFsnaDomainLookupDto.setFsnaRspns(responseDtos);
					allDomainDtos.add(cpsFsnaDomainLookupDto);

				});
				// The below code is to fetch just the Domains when the page is
				// saved with just Primary/Secondary Caregiver data
				if (CollectionUtils.isEmpty(evntPersons)) {
					domains.stream().filter(dom -> dom.getCdFSNADomainType().equalsIgnoreCase(CHILD)).forEach(cd -> {
						CpsFsnaDomainLookupDto cpsFsnaDomainLookupDto = new CpsFsnaDomainLookupDto();
						cpsFsnaDomainLookupDto = cd;
						String[] nmArray = cpsFsnaDomainLookupDto.getNmDefntn().split("\\; and");
						StringBuilder nmDefntn = new StringBuilder();
						if (nmArray.length > 0)
							if (cpsFsnaDomainLookupDto.getNmDefntn().contains(PATTERN_CHECK))
								nmDefntn.append(LIST_INDEX_START).append(nmArray[0]).append(PATTERN_CHECK)
										.append(LIST_INDEX_END);
							else
								nmDefntn = nmDefntn.append(LIST_INDEX_START).append(nmArray[0]).append(LIST_INDEX_END);
						if (nmArray.length > 1) {
							nmDefntn = nmDefntn.append(LIST_INDEX_START).append(nmArray[1]).append(LIST_INDEX_END);
						}
						cpsFsnaDomainLookupDto.setNmDefntn(nmDefntn.toString());
						allDomainDtos.add(cpsFsnaDomainLookupDto);
					});
				}
			} else {
				// if previously no domains sected to display sections
				allDomains = fSNADao.queryFsna(cdStage);
			}
		}

		if (!ObjectUtils.isEmpty(allDomainDtos)) {
			allDomains = allDomainDtos;
		}

		if (!ObjectUtils.isEmpty(allDomains) && allDomains.size() > 0) {
			cpsFsnaDto.setIdVersion(allDomains.get(0).getNbrVersion());
		}

		Collections.sort(allDomains, new Comparator<CpsFsnaDomainLookupDto>() {
			public int compare(CpsFsnaDomainLookupDto s1, CpsFsnaDomainLookupDto s2) {
				return s1.getNbrSortOrder() > s2.getNbrSortOrder() ? 1 : -1;
			}
		});

		LinkedHashMap<String, List<CpsFsnaDomainLookupDto>> careGiverSectionsMap = new LinkedHashMap<>();
		// grouping domains by domain type for parent or care giver
		allDomains.stream().filter(domain -> PARENT_OR_CAREGIVER.equals(domain.getCdFSNADomainType()))
				.collect(Collectors.groupingBy(CpsFsnaDomainLookupDto::getCdSection)).entrySet().stream()
				.sorted(Map.Entry.<String, List<CpsFsnaDomainLookupDto>>comparingByKey())
				.forEachOrdered(x -> careGiverSectionsMap.put(x.getKey(), x.getValue()));

		LinkedHashMap<String, List<CpsFsnaDomainLookupDto>> childSectionsMap = new LinkedHashMap<>();
		// grouping domains by domain type for child section
		allDomains.stream().filter(domain -> CHILD.equals(domain.getCdFSNADomainType()))
				.collect(Collectors.groupingBy(CpsFsnaDomainLookupDto::getCdSection)).entrySet().stream()
				.sorted(Map.Entry.<String, List<CpsFsnaDomainLookupDto>>comparingByKey())
				.forEachOrdered(x -> childSectionsMap.put(x.getKey(), x.getValue()));

		// map with person as key and List<CpsFsnaPrtyStrngthNeedDto> as value		
		LinkedHashMap<Long, List<CpsFsnaPrtyStrngthNeedDto>> stregthsNeedsMap = new LinkedHashMap<>();
		strensNeeds.stream().collect(Collectors.groupingBy(CpsFsnaPrtyStrngthNeedDto::getIdRelPerson)).entrySet()
				.stream().sorted(Map.Entry.<Long, List<CpsFsnaPrtyStrngthNeedDto>>comparingByKey())
				.forEachOrdered(x -> stregthsNeedsMap.put(x.getKey(), x.getValue()));

		stregthsNeedsMap.entrySet().stream().forEach(value -> {
			value.getValue().sort(Comparator.comparing(CpsFsnaPrtyStrngthNeedDto::getIdRelPerson));
		});

		FSNAAssessmentDtlGetRes FSNAAssessmentDtlGetRes = new FSNAAssessmentDtlGetRes();
		//Sort the Person list before setting it to response.
		persons.sort(Comparator.comparing(PersonListDto::getPersonAge).reversed());
		
		// Sort child response
		if (!ObjectUtils.isEmpty(evntPersons) && ServiceConstants.Zero_INT < evntPersons.size()) {
			List<Long> sortedIdPerson = new ArrayList<>();
			for (PersonListDto p : persons) {
				PersonInfoDto matchingPerson = evntPersons.stream().filter(r -> r.getIdPerson().equals(p.getIdPerson()))
						.findFirst().orElse(null);
				if (!ObjectUtils.isEmpty(matchingPerson)) {
					sortedIdPerson.add(matchingPerson.getIdPerson());
				}
			}

			if (!ObjectUtils.isEmpty(sortedIdPerson) && ServiceConstants.Zero_INT < sortedIdPerson.size()) {
				// Sort child responses with oldest child first order
				childSectionsMap.entrySet().stream().forEach(value -> {
					value.getValue().stream().forEach(rspns -> {
						rspns.getFsnaRspns().sort(Comparator.comparing(v -> sortedIdPerson.indexOf(v.getIdRelPrsn())));
					});
				});
			}
		}
				
		FSNAAssessmentDtlGetRes.setPersons(persons);
		FSNAAssessmentDtlGetRes.setCpsFsnaDto(cpsFsnaDto);
		FSNAAssessmentDtlGetRes.setCareGiverSectionsMap(careGiverSectionsMap);
		FSNAAssessmentDtlGetRes.setChildSectionsMap(childSectionsMap);
		FSNAAssessmentDtlGetRes.setStregthsNeedsMap(stregthsNeedsMap);
		if (strensNeeds.size() >= 1) {
			FSNAAssessmentDtlGetRes.setIndShowStrnNeedSec(Boolean.TRUE);
		}
		FSNAAssessmentDtlGetRes.setSavedChildAssessed(savedChildAssessed);
		return FSNAAssessmentDtlGetRes;
	}

	/**
	 * Method Name: saveCpsFsna Method Description:This method is used to save
	 * the CPS FSNA assessment
	 *
	 * @param appEvent
	 * @param idUpdatePerson
	 * @param evtStatus
	 * @param eventDesc
	 * @
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public FSNAAssessmentDtlGetRes saveCpsFsna(SdmFsnaReq sdmFsnaReq) {
		
		// Invalidate Approval task when saving PEND assessment and not being
		// saved by the Approver
		if (CodesConstant.CEVTSTAT_PEND.equals(sdmFsnaReq.getCpsFsnaDto().getCdEventStatus())
				&& !sdmFsnaReq.getIsApprovalMode()) {
			checkForInvalidateApproval(sdmFsnaReq.getCpsFsnaDto());
		}

		if (ObjectUtils.isEmpty(sdmFsnaReq.getCpsFsnaDto().getIdEvent())
				|| sdmFsnaReq.getCpsFsnaDto().getIdEvent() == 0) {
			sdmFsnaReq.getCpsFsnaDto()
					.setIdEvent(createAndReturnEventId(sdmFsnaReq.getCpsFsnaDto(), sdmFsnaReq.getUserProfile()));
		}
		// Save Checked Person Assessed
		updatePersonAssessed(sdmFsnaReq);

		fSNADao.saveOrUpdateCpsFsna(sdmFsnaReq);
		
		// After saving, invalid pending closure stage task
		String cdStage = sdmFsnaReq.getCpsFsnaDto().getCdStage();
		Long idStage = sdmFsnaReq.getCpsFsnaDto().getIdStage();
		String cdTask = null;
		if (cdStage.equals(CodesConstant.CSTAGES_FSU)) {
			cdTask = CLOSURE_TASK_CODE_FSU;
		} else if (cdStage.equals(CodesConstant.CSTAGES_FRE)) {
			cdTask = CLOSURE_TASK_CODE_FRE;
		} else if (cdStage.equals(CodesConstant.CSTAGES_FPR)) {
			cdTask = CLOSURE_TASK_CODE_FPR;
		}
		List<EventDto> eventDtoList = eventDao.getEventByStageIDAndTaskCode(idStage, cdTask);
		if (!CollectionUtils.isEmpty(eventDtoList)) {
			EventDto eventDto = eventDtoList.get(0);
			if (CodesConstant.CEVTSTAT_PEND.equals(eventDto.getCdEventStatus())) {
				ApprovalCommonInDto pInputMsg = new ApprovalCommonInDto();
				ApprovalCommonOutDto pOutputMsg = new ApprovalCommonOutDto();
				pInputMsg.setIdEvent(eventDto.getIdEvent());
				// Call Service to invalidate approvals
				approvalCommonService.InvalidateAprvl(pInputMsg, pOutputMsg);
			}
		}
		FSNAAssessmentDtlGetRes FSNAAssessmentDtlGetRes = new FSNAAssessmentDtlGetRes();
		FSNAAssessmentDtlGetRes.setCpsFsnaDto(sdmFsnaReq.getCpsFsnaDto());
		return FSNAAssessmentDtlGetRes;

	}

	/**
	 * 
	 * Method Description: This method to update event person Link if a new
	 * child selected needs to be added to event person link , de-sections have
	 * to be removed
	 * 
	 * @param sdmFsnaReq
	 */
	private void updatePersonAssessed(SdmFsnaReq sdmFsnaReq) {

		Set<Long> tobeAddedPersonAssessed = null;
		if (!ObjectUtils.isEmpty(sdmFsnaReq.getSelectedChildAssessed())) {
			tobeAddedPersonAssessed = sdmFsnaReq.getSelectedChildAssessed().stream().collect(Collectors.toSet());
		} else {
			tobeAddedPersonAssessed = new HashSet<>();
		}
		if (!ObjectUtils.isEmpty(sdmFsnaReq.getSavedChildAssessed())) {
			tobeAddedPersonAssessed.removeAll(sdmFsnaReq.getSavedChildAssessed());
		}
		Set<Long> tobeDeletedPersonAssessed = null;
		if (!ObjectUtils.isEmpty(sdmFsnaReq.getSavedChildAssessed())) {
			tobeDeletedPersonAssessed = sdmFsnaReq.getSavedChildAssessed().stream().collect(Collectors.toSet());
		} else {
			tobeDeletedPersonAssessed = new HashSet<>();
		}
		if (!ObjectUtils.isEmpty(sdmFsnaReq.getSelectedChildAssessed())) {
			tobeDeletedPersonAssessed.removeAll(sdmFsnaReq.getSelectedChildAssessed());
		}
		for (Long tobeDeletedPersonId : tobeDeletedPersonAssessed) {
			if (!ObjectUtils.isEmpty(tobeDeletedPersonId)) {
				sdmSafetyAssessmentDao.deleteEventPersonLink(tobeDeletedPersonId.intValue(),
						sdmFsnaReq.getCpsFsnaDto().getIdEvent().intValue());
			}
		}
		for (Long tobeAddedPersonId : tobeAddedPersonAssessed) {
			if (!ObjectUtils.isEmpty(tobeAddedPersonId)) {
				sdmSafetyAssessmentDao.addEventPersonLink(tobeAddedPersonId.intValue(),
						sdmFsnaReq.getCpsFsnaDto().getIdEvent().intValue(),
						sdmFsnaReq.getCpsFsnaDto().getIdCase().intValue());
			}
		}

	}

	/**
	 * method to Create Event and Return Event Id of the new Event Method
	 * Description:
	 * 
	 * @param cpsFsnaDto
	 * @param userProfile
	 * @param eventStatus
	 * @param assessmentType
	 * @return
	 */
	private Long createAndReturnEventId(CpsFsnaDto cpsFsnaDto, UserProfileDto userProfile) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
		if (ServiceConstants.CSDMASMT_INIT.equals(cpsFsnaDto.getCdAsgnmntType())) {
			postEventIPDto.setEventDescr(INITIAL_SAFETY_EVENT_DESCR);
		} else if (ServiceConstants.CSDMASMT_REAS.equals(cpsFsnaDto.getCdAsgnmntType())) {
			postEventIPDto.setEventDescr(REASSESS_SAFETY_EVENT_DESCR);
		}
		postEventIPDto.setCdTask(cpsFsnaDto.getCdTask());
		postEventIPDto.setIdPerson(userProfile.getIdUser());
		postEventIPDto.setIdStage(cpsFsnaDto.getIdStage());
		postEventIPDto.setIdCase(cpsFsnaDto.getIdCase());
		postEventIPDto.setDtEventOccurred(new Date());
		postEventIPDto.setUserId(userProfile.getIdUserLogon());
		archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		postEventIPDto.setTsLastUpdate(new Date());
		postEventIPDto.setCdEventType(ServiceConstants.CEVNTTYP_ASM);
		postEventIPDto.setCdEventStatus(ServiceConstants.EVENT_PROC);
		postEventIPDto.setDtEventOccurred(new Date());
		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);
		return postEventOPDto.getIdEvent();
	}

	private void checkForInvalidateApproval(CpsFsnaDto cpsFsnaDto) {
		if ((!ObjectUtils.isEmpty(cpsFsnaDto.getIdEvent())) && (cpsFsnaDto.getIdEvent() != 0)) {
			ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
			approvalCommonInDto.setIdEvent(cpsFsnaDto.getIdEvent());
			approvalService.callCcmn05uService(approvalCommonInDto);
		}
	}

	/**
	 * Method Name: deleteSdmFsna
	 * 
	 * @param cpsFsnaDto
	 * @throws DataNotFoundException
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public FSNAAssessmentDtlGetRes deleteSdmFsna(CpsFsnaDto cpsFsnaDto) {

		// ALM # 13575 - Invalidate To-do Task on FSNA delete
		ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
		approvalCommonInDto.setIdEvent(cpsFsnaDto.getIdEvent());
		approvalService.callCcmn05uService(approvalCommonInDto);

		return fSNADao.deleteSdmFsna(cpsFsnaDto.getIdEvent());

	}

	/**
	 * Method Name: completeSdmFsna
	 * 
	 * @param cpsFsnaDto
	 * @throws DataNotFoundException
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public FSNAAssessmentDtlGetRes completeSdmFsna(CpsFsnaDto cpsFsnaDto, UserProfileDto userProfileDB) {

		cpsFsnaDto = fSNADao.completeAssessment(cpsFsnaDto, userProfileDB);
		FSNAAssessmentDtlGetRes fSNAAssessmentDtlGetRes = new FSNAAssessmentDtlGetRes();
		cpsFsnaDto.setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
		fSNAAssessmentDtlGetRes.setCpsFsnaDto(cpsFsnaDto);
		return fSNAAssessmentDtlGetRes;
	}

	/**
	 * Method Name: getFSNAAssmntType is to get the assessment type for given
	 * primary and secondary care giver
	 * 
	 * @param commonHelperReq
	 * 
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonStringRes getFSNAAssmntType(CommonHelperReq commonHelperReq) {

		List<CpsFsnaDto> assessments = fSNADao.getAllAssmnts(commonHelperReq.getIdStage());
		CommonStringRes commonStringRes = new CommonStringRes();
		List<String> responseList = new ArrayList<>();
		// checking for same primary /secorndary in all the assessments except
		// the current assessment
		// if exists and other assessment in PROC status will return error
		// message
		// if exists and other assessment in any other status will return assessment
		// type is Reassessment
		assessments = assessments.stream().filter(
				o -> !ObjectUtils.isEmpty(o.getIdEvent()) && !o.getIdCpsFsna().equals(commonHelperReq.getIdToDo())).collect(Collectors.toList());
		String primaryData = ServiceConstants.EMPTY_STRING;
		String secondaryData = ServiceConstants.EMPTY_STRING;
		String goalStatement = ServiceConstants.EMPTY_STRING;
		String dangerWorryStmt = ServiceConstants.EMPTY_STRING;
		// Primary Parent/Caregiver is passed in the idPerson field. The other
		// assessments are checked to see if the primary parent is already
		// present as either primary or secondary in one of them. If yes, then
		// depending on the status of that assessment, the error message or
		// reassessment is set for the primary. The same thing happens in the
		// next loop for secondary. If the error is found then the loop will be
		// immediately terminated
		for (CpsFsnaDto assessment : assessments) {
			if ((!ObjectUtils.isEmpty(assessment.getIdPrmryCrgvrPrnt())
					&& assessment.getIdPrmryCrgvrPrnt().equals(commonHelperReq.getIdPerson()))
					|| (!ObjectUtils.isEmpty(assessment.getIdSecndryCrgvrPrnt())
							&& assessment.getIdSecndryCrgvrPrnt().equals(commonHelperReq.getIdPerson()))) {
				if (CodesConstant.CEVTSTAT_PROC.equals(assessment.getCdEventStatus())) {
					primaryData = String.valueOf(MSG_FSNA_PRIM_PROC_ST);
					break;
				} else {
					primaryData = CodesConstant.CSDMASMT_REAS;
				}
			}
		}
		//Loop for secondary
		for (CpsFsnaDto assessment : assessments) {
			if (ObjectUtils.isEmpty(commonStringRes.getCommonRes())) {
				if ((!ObjectUtils.isEmpty(assessment.getIdSecndryCrgvrPrnt())
						&& assessment.getIdSecndryCrgvrPrnt().equals(commonHelperReq.getIdPerson2()))
						|| (!ObjectUtils.isEmpty(assessment.getIdPrmryCrgvrPrnt())
								&& assessment.getIdPrmryCrgvrPrnt().equals(commonHelperReq.getIdPerson2()))) {
					if (CodesConstant.CEVTSTAT_PROC.equals(assessment.getCdEventStatus())) {
						secondaryData = String.valueOf(MSG_FSNA_PRIM_SEC_ST);
						break;
					} else {
						secondaryData = CodesConstant.CSDMASMT_REAS;
					}
				}
			}
		}
		// If neither the reassessment nor the error message was set, then the
		// message has to be defaulted to Initial
		if (StringUtils.isEmpty(primaryData)) {
			primaryData = CodesConstant.CSDMASMT_INIT;
		}
		if (StringUtils.isEmpty(secondaryData)) {
			secondaryData = CodesConstant.CSDMASMT_INIT;
		}
		responseList.add(primaryData);
		responseList.add(secondaryData);
		// If the assessment type is being returned as Reassessment in either of
		// Primary of Secondary, then the previous danger worry and goal
		// statement should also be fetched
		if (CodesConstant.CSDMASMT_REAS.equalsIgnoreCase(primaryData)
				|| CodesConstant.CSDMASMT_REAS.equalsIgnoreCase(secondaryData)) {
			CpsFsnaDto lastAssessment = assessments.stream()
					.filter(a -> (!ObjectUtils.isEmpty(commonHelperReq.getIdPerson())
							&& ((a.getIdPrmryCrgvrPrnt().equals(commonHelperReq.getIdPerson()))
									|| (!ObjectUtils.isEmpty(a.getIdSecndryCrgvrPrnt())
											&& a.getIdSecndryCrgvrPrnt().equals(commonHelperReq.getIdPerson()))))
							|| (!ObjectUtils.isEmpty(commonHelperReq.getIdPerson2()) && ((a.getIdPrmryCrgvrPrnt()
									.equals(commonHelperReq.getIdPerson2()))
									|| (!ObjectUtils.isEmpty(a.getIdSecndryCrgvrPrnt())
											&& a.getIdSecndryCrgvrPrnt().equals(commonHelperReq.getIdPerson2())))))
					.findFirst().get();
			goalStatement = lastAssessment.getTxtGoalStatmnts();
			dangerWorryStmt = lastAssessment.getTxtDngrWorry();
			responseList.add(goalStatement);
			responseList.add(dangerWorryStmt);			
		}
		commonStringRes.setCommonResList(responseList);
		return commonStringRes;
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public FSNAAssessmentDtlGetRes checkCareGiverPerson(Long personId, Long stageId) {
		CpsFsnaDto cpsFsnaDto =  fSNADao.getPersonFSNA(personId, stageId);
		FSNAAssessmentDtlGetRes fsnaAssessmentDtlGetRes = new FSNAAssessmentDtlGetRes();
		fsnaAssessmentDtlGetRes.setCpsFsnaDto(cpsFsnaDto);
		return fsnaAssessmentDtlGetRes;
	}

	/**
	 * This method is to get validation errors before save
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FSNAValidationRes validateFSNAAssessment(SdmFsnaReq sdmFsnaReq) {
		List<CpsFsnaDto> assessments = fSNADao.getAllAssmnts(sdmFsnaReq.getCpsFsnaDto().getIdStage());
		Set<ErrorDto> errors = new HashSet<ErrorDto>();
		// loop all the assessments except current assessment
		assessments.stream().filter(o -> !ObjectUtils.isEmpty(o.getIdEvent())
				&& !o.getIdEvent().equals(sdmFsnaReq.getCpsFsnaDto().getIdEvent())).forEach(p -> {
					// Check dtOfAsgnmnt for the current assessment is before
					// any other assessment
					if (!ObjectUtils.isEmpty(p.getDtOfAsgnmnt())
							&& !ObjectUtils.isEmpty(sdmFsnaReq.getCpsFsnaDto().getDtOfAsgnmnt())
							&& sdmFsnaReq.getCpsFsnaDto().getDtOfAsgnmnt().before(p.getDtOfAsgnmnt())) {
						addError(errors, MSG_FSNA_ASMT_PRIOR);
					}
					// Any other PROC assessment having same primary
					// parent/caregiver
					if (!ObjectUtils.isEmpty(p.getIdPrmryCrgvrPrnt())
							&& (p.getIdPrmryCrgvrPrnt().equals(sdmFsnaReq.getCpsFsnaDto().getIdPrmryCrgvrPrnt())
									|| (!ObjectUtils.isEmpty(p.getIdSecndryCrgvrPrnt()) && p.getIdSecndryCrgvrPrnt()
											.equals(sdmFsnaReq.getCpsFsnaDto().getIdPrmryCrgvrPrnt())))
							&& CodesConstant.CEVTSTAT_PROC.equals(p.getCdEventStatus())) {
						addError(errors, MSG_FSNA_PRIM_PROC_ST);
					}

					// Any other PROC assessment having same secondary
					// parent/caregiver
					if (!ObjectUtils.isEmpty(p.getIdSecndryCrgvrPrnt())
							&& (p.getIdSecndryCrgvrPrnt().equals(sdmFsnaReq.getCpsFsnaDto().getIdSecndryCrgvrPrnt())
									|| !ObjectUtils.isEmpty(p.getIdPrmryCrgvrPrnt()) && (p.getIdPrmryCrgvrPrnt()
											.equals(sdmFsnaReq.getCpsFsnaDto().getIdSecndryCrgvrPrnt())))
							&& CodesConstant.CEVTSTAT_PROC.equals(p.getCdEventStatus())) {
						addError(errors, MSG_FSNA_PRIM_SEC_ST);
					}
					
					// Already Initial assessment is present for selected primary/secondary parent.
					if (CodesConstant.CSDMASMT_INIT.equals(sdmFsnaReq.getCpsFsnaDto().getCdAsgnmntType())
							&& CodesConstant.CSDMASMT_INIT.equals(p.getCdAsgnmntType())
							&& ((sdmFsnaReq.getCpsFsnaDto().getIdPrmryCrgvrPrnt().equals(p.getIdPrmryCrgvrPrnt())
									|| sdmFsnaReq.getCpsFsnaDto().getIdPrmryCrgvrPrnt()
											.equals(p.getIdSecndryCrgvrPrnt()))
									|| (!ObjectUtils.isEmpty(sdmFsnaReq.getCpsFsnaDto().getIdSecndryCrgvrPrnt())
											&& (sdmFsnaReq.getCpsFsnaDto().getIdSecndryCrgvrPrnt()
													.equals(p.getIdPrmryCrgvrPrnt())
													|| sdmFsnaReq.getCpsFsnaDto().getIdSecndryCrgvrPrnt()
															.equals(p.getIdSecndryCrgvrPrnt()))))) {
						addError(errors, ServiceConstants.MSG_FSNA_INIT_EXISTS);
					}
				});
		FSNAValidationRes res = new FSNAValidationRes();
		res.setErrors(errors);
		return res;
	}

	/**
	 * 
	 * Method Description: this method is to add error to errors list
	 * 
	 * @param errors
	 * @param error
	 */
	private void addError(Set<ErrorDto> errors, int error) {
		ErrorDto errordto = new ErrorDto();
		errordto.setErrorCode(error);
		errors.add(errordto);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public PreFillDataServiceDto getFSNAFbssForm(FSNAAssessmentDtlGetReq getFSNAAssessmentDtlReq) {
		FSNAAssessmentDtlGetRes fsnaAssessmentDtlGetRes = null;

		// Change stage to FSU when incoming stage is FRE. Domains for both
		// stages are same and mapping is available for FSU
		String cdStage = getFSNAAssessmentDtlReq.getCdStage();
		if (ServiceConstants.FAM_REUN_STG.equalsIgnoreCase(cdStage)) {
			cdStage = ServiceConstants.FAMILY_SUBCARE;
		}

		List<CpsFsnaDomainLookupDto> allDomains = null;
		CpsFsna cpsFsna = null;
		// If the event id is null, need to query for domain details to load the
		// page with blank data
		if (!ObjectUtils.isEmpty(getFSNAAssessmentDtlReq.getIdEvent()) && getFSNAAssessmentDtlReq.getIdEvent() != 0) {
			cpsFsna = fSNADao.getFSNAAsmt(getFSNAAssessmentDtlReq.getIdEvent());
		} else {
			allDomains = fSNADao.queryFsna(cdStage);
		}
		List<PersonListDto> persons = callPersonService(getFSNAAssessmentDtlReq.getIdStage());
		fsnaAssessmentDtlGetRes = formResponse(allDomains, cpsFsna, persons, cdStage);
		fsnaAssessmentDtlGetRes.getCpsFsnaDto().setIdStage(getFSNAAssessmentDtlReq.getIdStage());
		fsnaAssessmentDtlGetRes.getCpsFsnaDto().setIdCase(getFSNAAssessmentDtlReq.getIdCase());
		fsnaAssessmentDtlGetRes.getCpsFsnaDto().setCdTask(getFSNAAssessmentDtlReq.getCdTask());
		fsnaAssessmentDtlGetRes.getCpsFsnaDto().setCdStage(getFSNAAssessmentDtlReq.getCdStage());
		if (!ObjectUtils.isEmpty(getFSNAAssessmentDtlReq.getIdEvent()) && getFSNAAssessmentDtlReq.getIdEvent() != 0) {
			fsnaAssessmentDtlGetRes.getCpsFsnaDto().setIdEvent(getFSNAAssessmentDtlReq.getIdEvent());
			fsnaAssessmentDtlGetRes.getCpsFsnaDto()
					.setCdEventStatus(eventDao.getEventStatus(getFSNAAssessmentDtlReq.getIdEvent()));
			// get previous assessment danger,worry and goal statements not
			// considering the current assessment
			if (!StringUtils.isEmpty(cpsFsna.getCdAsgnmntType())
					&& !ServiceConstants.CD_ASSMT_TYPE_INIT.equalsIgnoreCase(cpsFsna.getCdAsgnmntType())) {
				CpsFsnaDto preViousDto = fSNADao.getPerviousStatements(getFSNAAssessmentDtlReq.getIdStage(), cpsFsna);
				fsnaAssessmentDtlGetRes.getCpsFsnaDto().setPreviousTxtDngrWorry(preViousDto.getPreviousTxtDngrWorry());
				fsnaAssessmentDtlGetRes.getCpsFsnaDto()
						.setPrevioustxtGoalStatmnts(preViousDto.getPrevioustxtGoalStatmnts());
			}
		} 
		// created by and last updated by person full name
		Long idPersonCreated = null;
		if (ObjectUtils.isEmpty(fsnaAssessmentDtlGetRes.getCpsFsnaDto().getIdCreatedPerson())) {
			idPersonCreated = getFSNAAssessmentDtlReq.getIdWorker();
			fsnaAssessmentDtlGetRes.getCpsFsnaDto().setDtCreated(new Date());
		} else
			idPersonCreated = fsnaAssessmentDtlGetRes.getCpsFsnaDto().getIdCreatedPerson();
		fsnaAssessmentDtlGetRes.getCpsFsnaDto()
				.setCreatedBy(personDao.getPersonById(idPersonCreated).getNmPersonFull());
		Long idPersonupdated = null;
		if (ObjectUtils.isEmpty(fsnaAssessmentDtlGetRes.getCpsFsnaDto().getIdLastUpdatePerson())) {
			fsnaAssessmentDtlGetRes.getCpsFsnaDto().setUpdatedBy(ServiceConstants.EMPTY_STR);
		} else {
			idPersonupdated = fsnaAssessmentDtlGetRes.getCpsFsnaDto().getIdLastUpdatePerson();
			fsnaAssessmentDtlGetRes.getCpsFsnaDto()
					.setUpdatedBy(personDao.getPersonById(idPersonupdated).getNmPersonFull());
		}
		return sDMFamilyStrengthsAndNeedsAssessmentFBSSPrefillData.returnPrefillData(fsnaAssessmentDtlGetRes);

	}

	/**
	 * 
	 * Method Description: This service will fetch the details required to
	 * launch the CPS Monthly Evaluation ADS Change
	 * 
	 * @param CpsMonthlyEvalReq
	 * @return PreFillDataServiceDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getCpsMonthlyEvalForm(CpsMonthlyEvalReq cpsMonthlyEvalReq) {

		CpsMonthlyEvalDto cpsMonthlyEvalDto = new CpsMonthlyEvalDto();
		List<FamilyPlan> FamilyPlanList = new ArrayList<FamilyPlan>();
		List<FamilyPlanEval> FamilyPlanEvalList = new ArrayList<FamilyPlanEval>();
		List<SDMSafetyAssessmentDto> SDMSafetyAssmnt = new ArrayList<SDMSafetyAssessmentDto>();
		List<SDMRiskReassessmentRes> SDMRiskReassessment = new ArrayList<SDMRiskReassessmentRes>();
		List<FamilyPlanDto> LegacyFamilyPlanList = new ArrayList<FamilyPlanDto>();

		// Set the Id Event
		cpsMonthlyEvalDto.setIdEvent(cpsMonthlyEvalReq.getIdEvent());

		// Set the Id Stage
		cpsMonthlyEvalDto.setIdStage(cpsMonthlyEvalReq.getIdStage());

		// Set the Case Name
		cpsMonthlyEvalDto.setNmCase(caseUtils.getNmCase(cpsMonthlyEvalReq.getIdCase()));

		// Set the Date FPR Stage Opened
		StageDto stageDto = stageDao.getStageById(cpsMonthlyEvalReq.getIdStage());
		cpsMonthlyEvalDto.setDtStageOpened(stageDto.getDtStageCreated());

		// Set the ContactedBy Name
		cpsMonthlyEvalDto.setNmCaseWorker(cpsMonthlyEvalReq.getNmContact());

		// Set the Date of Evaluation
		cpsMonthlyEvalDto.setDtEvaluation(cpsMonthlyEvalReq.getDtContact());

		// Set the PCSP List
		List<PcspDto> pcsp = pcspListPlacmtDao.getPcspPlacemnts(cpsMonthlyEvalReq.getIdCase());
		cpsMonthlyEvalDto.setPcsp(pcsp);

		// Set the Contact Search List Details
		ContactListSearchDto contactListSearchDto = new ContactListSearchDto();
		contactListSearchDto.setIdStage(cpsMonthlyEvalReq.getIdStage());
		List<ContactDto> contactList = contactSearchDao.searchContactList(contactListSearchDto);
		cpsMonthlyEvalDto.setContact(contactList);

		// Set the SDM FSNA List Details
		//code change for defect 16193
		//this method will retrieve the latest record for primary care givers
		List<CpsFsnaDto> fsna = fSNADao.getLatestAssmntsForPrimCaregiver(cpsMonthlyEvalReq.getIdStage());
		cpsMonthlyEvalDto.setFsna(fsna);
		
		// Fetch the Event Details of FamilyPlan/Family Plan Evaluation
		if(!ObjectUtils.isEmpty(fsna)) {
			List<EventListDto> familyPlanEventList = eventDao.getLatestAprvFamilyPlanEvent(cpsMonthlyEvalReq.getIdStage());
			// Set the Family Plan Related Details
			if (!ObjectUtils.isEmpty(familyPlanEventList)) {
				for (EventListDto familyPlaneventListDto : familyPlanEventList) { 
						// Fetch the New Family Plan Related Details
						FamilyPlanDtlEvalReq familyPlanDtlEvalReq = new FamilyPlanDtlEvalReq();
						familyPlanDtlEvalReq.setIdEvent(familyPlaneventListDto.getIdEvent());
						Object familyPlanDtlEvalRes = familyPlanDao.getFamilyPlanFormDtl(familyPlanDtlEvalReq);
						
						if (familyPlanDtlEvalReq.getFamilyPlanDtlInd().equals(ServiceConstants.TRUEVAL)) {
							FamilyPlanList.add((FamilyPlan) familyPlanDtlEvalRes);
							//FamilyPlanList.get(0);
						} else if (familyPlanDtlEvalReq.getFamilyPlanDtlInd().equals(ServiceConstants.FALSEVAL)) {
							FamilyPlanEvalList.add((FamilyPlanEval) familyPlanDtlEvalRes);
							
						}
		
						// Fetch the Legacy Family Plan Related Details
						FamilyPlanDto familyPlanReq = new FamilyPlanDto();
						familyPlanReq.setIdEvent(familyPlaneventListDto.getIdEvent());
						familyPlanReq.setCdTask(familyPlaneventListDto.getCdTask());
						familyPlanReq.setIdCase(familyPlaneventListDto.getIdCase());
						familyPlanReq.setIdStage(familyPlaneventListDto.getIdStage());
						LegacyFamilyPlanList.add(familyPlanService.fetchFamilyPlanDtl(familyPlanReq));
				}
			}
		}

		// Fetch the Event Details of SDM Safety Assessment
		EventReq sdmSafetyReq = new EventReq();
		sdmSafetyReq.setUlIdCase(cpsMonthlyEvalReq.getIdCase());
		sdmSafetyReq.setUlIdStage(cpsMonthlyEvalReq.getIdStage());
		List<String> sdmSafetyEventType = new ArrayList<String>();
		sdmSafetyEventType.add(ServiceConstants.ASSESS_EVENT_TYPE);
		sdmSafetyReq.setSzCdTask(ServiceConstants.CD_TASK_SA_FPR);
		sdmSafetyReq.setEventType(sdmSafetyEventType);
		List<EventListDto> sdmSafetyEventList = eventDao.getEventDetails(sdmSafetyReq);

		//ALM ID : 13182 copied the qualifing logic from Form Prefill method. 
		// need to determine if the list has any qualified safety assessment in this stage.
		Date lastDayOfEvaluation = getLastDayOfEvaluation(cpsMonthlyEvalDto.getDtEvaluation(), cpsMonthlyEvalDto.getIdEvent());
		
		SDMSafetyAssmnt = getSDMSafetyAssessments(sdmSafetyEventList, lastDayOfEvaluation);
		
		//ALM ID : 13182 if the current stage doesn't have safety assessment get them from previous stage
		if(CollectionUtils.isEmpty(SDMSafetyAssmnt)){
			sdmSafetyEventList = eventDao.getSDMAssmntList(sdmSafetyReq);
			SDMSafetyAssmnt = getSDMSafetyAssessments(sdmSafetyEventList, lastDayOfEvaluation);
		}

		// Fetch the Event Details of Risk Re Assessment
		EventReq riskSafetyReq = new EventReq();
		riskSafetyReq.setUlIdCase(cpsMonthlyEvalReq.getIdCase());
		riskSafetyReq.setUlIdStage(cpsMonthlyEvalReq.getIdStage());
		List<String> riskSafetyEventType = new ArrayList<String>();
		riskSafetyEventType.add(ServiceConstants.ASSESS_EVENT_TYPE);
		riskSafetyReq.setSzCdTask(ServiceConstants.TASK_7480);
		riskSafetyReq.setEventType(riskSafetyEventType);
		List<EventListDto> riskSafetyEventList = eventDao.getEventDetails(riskSafetyReq);

		// Fetch the Risk Re Assessment
		if (!ObjectUtils.isEmpty(riskSafetyEventList)) {
			for (EventListDto eventListDto : riskSafetyEventList) {
				SDMRiskReasmntDto sdmRiskReasmntDto = new SDMRiskReasmntDto();
				sdmRiskReasmntDto.setIdCase(cpsMonthlyEvalReq.getIdCase());
				sdmRiskReasmntDto.setIdStage(eventListDto.getIdStage());
				sdmRiskReasmntDto.setIdEvent(eventListDto.getIdEvent());
				sdmRiskReasmntDto.setCdStage(eventListDto.getCdStage());
				SDMRiskReassessmentRes sdmRiskReassessmentRes = sdmRiskReassessmentService
						.getSDMRiskReassessmentDtls(sdmRiskReasmntDto, ServiceConstants.ZERO);
				//ALM ID : 13182 : add only qualified Risk Reassessments (approved and assessment date is before last day of Evaluation).
				if (sdmRiskReassessmentRes.getSdmRiskReasmntDto().getCdEventStatus().equals(ServiceConstants.EVENTSTATUS_APPROVE)
					&& sdmRiskReassessmentRes.getSdmRiskReasmntDto().getDtAsmnt().before(lastDayOfEvaluation)) {
					SDMRiskReassessment.add(sdmRiskReassessmentRes);
				}
			}
		}

		//ALM ID : 13182 if the current stage doesn't have Risk Reassessments get Risk Assessments from previous stage
		if(CollectionUtils.isEmpty(SDMRiskReassessment)){
			riskSafetyReq.setSzCdTask(ServiceConstants.CD_TASK_RA);
			riskSafetyEventList = eventDao.getSDMAssmntList(riskSafetyReq);
			if (!CollectionUtils.isEmpty(riskSafetyEventList)) {
				for (EventListDto eventListDto : riskSafetyEventList) {
					SDMRiskAssessmentDto sdmRiskAssessmentDto = sdmRiskAssessmentService.getSDMRiskAssessment(eventListDto.getIdStage(), eventListDto.getIdEvent());
					SDMRiskReassessmentRes sdmRiskReassessmentRes = getSDMRiskReassessmentRes(sdmRiskAssessmentDto);
					SDMRiskReassessment.add(sdmRiskReassessmentRes);
				}
			}
		}

		cpsMonthlyEvalDto.setLegacyFamilyPlan(LegacyFamilyPlanList);
		cpsMonthlyEvalDto.setRiskReassessment(SDMRiskReassessment);
		cpsMonthlyEvalDto.setSafetyAssmnt(SDMSafetyAssmnt);
		cpsMonthlyEvalDto.setFamilyPlan(FamilyPlanList);
		cpsMonthlyEvalDto.setFamilyPlanEval(FamilyPlanEvalList);
		
		/**Defect# 12998 fix***/
        ContactDto monthlyEvalContactDto = contactDao.getContactById(cpsMonthlyEvalReq.getIdEvent());
        cpsMonthlyEvalDto.setDtCntctMnthlySummBeg(monthlyEvalContactDto.getDtCntctMnthlySummBeg());
        cpsMonthlyEvalDto.setDtCntctMnthlySummEnd(monthlyEvalContactDto.getDtCntctMnthlySummEnd());
		
		return cpsMonthlyEvalPrefillData.returnPrefillData(cpsMonthlyEvalDto);
	}
	
	//ALM ID : 13182 : need this date to qualify Assessments. got this from prefill method.
	private Date getLastDayOfEvaluation(String evaluationDateStr, Long eventId) {
		String[] strdateSplit = evaluationDateStr.split("/");
		int[] intdateSplit = new int[strdateSplit.length];
		// Creates the integer array.
		for (int i = 0; i < intdateSplit.length; i++) {
			intdateSplit[i] = Integer.parseInt(strdateSplit[i]);
			// Parses the integer for each string.
		}

		// Setting the First Day and Last Day of Month when the Evaluation was recorded
		LocalDate lastDayOfMonth = new LocalDate(intdateSplit[2], intdateSplit[0], ServiceConstants.One_Integer)
				.dayOfMonth().withMaximumValue();
		Date dtlastdayofEval = lastDayOfMonth.toDate();
		
		ContactDto monthlyEvalContactDto = contactDao.getContactById(eventId);
		if (!ObjectUtils.isEmpty(monthlyEvalContactDto.getDtContactApprv())) {
			dtlastdayofEval = monthlyEvalContactDto.getDtContactApprv();
		}
		return dtlastdayofEval;
	}

	// ALM ID : 13182 : Refactoed the logic to add only qualified safety assessments to the list.
	// Set the SDM Safety Assessment List Details
	private List<SDMSafetyAssessmentDto> getSDMSafetyAssessments(List<EventListDto> sdmSafetyEventList, Date lastDayOfEvaluation) {
		List<SDMSafetyAssessmentDto> SDMSafetyAssmnt = new ArrayList<SDMSafetyAssessmentDto>();
		if (!ObjectUtils.isEmpty(sdmSafetyEventList)) {
			for (EventListDto eventListDto : sdmSafetyEventList) {
				if (eventListDto.getCdEventStatus().equals(ServiceConstants.EVENT_STATUS_APRV)
						|| eventListDto.getCdEventStatus().equals(ServiceConstants.EVENT_STAT_COMP)) {
					SDMSafetyAssessmentDto sdmSafetyAssessmentDto = new SDMSafetyAssessmentDto();
					sdmSafetyAssessmentDto = sdmSafetyAssessmentDao.getSDMSafetyAssessment(eventListDto.getIdEvent(),
							eventListDto.getIdStage());
					if(sdmSafetyAssessmentDto.getDtSafetyAssessed().before(lastDayOfEvaluation)) {
						SDMSafetyAssmnt.add(sdmSafetyAssessmentDto);
					}
				}
			}
		}
		return SDMSafetyAssmnt;
	}

	// ALM ID : 13182 : Form uses SDMRiskReassessmentRes bean to display the logic, so
	// converitng SDMRiskAssessmentDto to SDMRiskReassessmentRes.
	private SDMRiskReassessmentRes getSDMRiskReassessmentRes(SDMRiskAssessmentDto sdmRiskAssessmentDto) {
		SDMRiskReassessmentRes riskReassessmentRes = new SDMRiskReassessmentRes();
		SDMRiskReasmntDto sdmRiskReasmntDto = new SDMRiskReasmntDto();
		sdmRiskReasmntDto.setCdEventStatus(sdmRiskAssessmentDto.getEventStatus());
		sdmRiskReasmntDto.setDtAsmnt(sdmRiskAssessmentDto.getCreatedDate());
		sdmRiskReasmntDto.setIdHshldAssessed(sdmRiskAssessmentDto.getIdHouseHoldPerson());
		sdmRiskReasmntDto.setIdStage(sdmRiskAssessmentDto.getStageId());
		List<Long> houseHoldMembers = new ArrayList<Long>();
		if(   sdmRiskAssessmentDto.getHouseHoldNameMap() != null 
		   && !sdmRiskAssessmentDto.getHouseHoldNameMap().isEmpty()) {
			houseHoldMembers = new ArrayList<Long>(sdmRiskAssessmentDto.getHouseHoldNameMap().keySet());
		}
		sdmRiskReasmntDto.setHouseholdMembers(houseHoldMembers);
		//sdmRiskReasmntDto.setRiskScore(sdmRiskAssessmentDto.getScoredRiskLevelCode());
		sdmRiskReasmntDto.setCdOverride(sdmRiskAssessmentDto.getOverrideCode());
		sdmRiskReasmntDto.setCdFinalRiskLevel(sdmRiskAssessmentDto.getFinalRiskLevelCode());
//		sdmRiskReasmntDto.setTxtRecommendation(sdmRiskAssessmentDto.getTxtRecommendation());
		riskReassessmentRes.setSdmRiskReasmntDto(sdmRiskReasmntDto);
		return riskReassessmentRes;
	}

}
