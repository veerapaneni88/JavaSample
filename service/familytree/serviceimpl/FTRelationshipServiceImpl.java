package us.tx.state.dfps.service.familytree.serviceimpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.StringHelper;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceDto;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseDto;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.FamilyTreeRelationsReq;
import us.tx.state.dfps.service.common.response.FamilyTreeRelationsRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.FTFamilyTreeUtil;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationBean;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationDto;
import us.tx.state.dfps.service.familyTree.bean.FamilyTreeRelationshipDto;
import us.tx.state.dfps.service.familyTree.bean.PersonRelIntDto;
import us.tx.state.dfps.service.familyTree.bean.PersonRelSuggestionDto;
import us.tx.state.dfps.service.familytree.dao.FTRelationshipDao;
import us.tx.state.dfps.service.familytree.dao.FTRelationshipSearchDao;
import us.tx.state.dfps.service.familytree.service.FTRelationshipService;
import us.tx.state.dfps.service.familytree.utils.FTRelationshipSuggestionUtils;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonEthnicityDao;
import us.tx.state.dfps.service.person.dao.PersonRaceDao;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.workload.dao.ApprovalDao;
import us.tx.state.dfps.service.workload.dao.StageProgDao;

/**
 *
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * implements the methods declared in FTRelationshipService Feb 12, 2018-
 * 4:38:23 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class FTRelationshipServiceImpl implements FTRelationshipService {

	@Autowired
	private StageDao stageDao;

	@Autowired
	private CapsCaseDao capsCaseDao;

	@Autowired
	private FTRelationshipSearchDao fTRelationshipSearchDao;

	@Autowired
	private FTRelationshipDao ftRelationshipDao;

	@Autowired
	private PersonDao personDao;

	@Autowired
	private PersonRaceDao personRaceDao;

	@Autowired
	private PersonEthnicityDao personEthnicityDao;

	@Autowired
	private ApprovalDao approvalDao;

	@Autowired
	private StageProgDao stageProgDao;

	@Autowired
	private FTRelationshipSuggestionUtils ftRelationshipSuggestionUtils;
	public static final String SPL_IND_NM_STAGE_VALID = "1";
	private static final Logger LOG = Logger.getLogger("ServiceBusiness-FTRelationshipServiceLog");
	private static final String REPORTER = "(reporter)";
	private static final Long REPORT_TYPE_DETAILED = 2l;

	private static final String NOT_APPLICABLE = "N/A";

	/**
	 *
	 * Method Name: fetchRelationshipInfo Method Description:This method returns
	 * All the information that needs to be displayed on Family Tree
	 * Relationship page including Context Person List, Relationships and
	 * Suggested Relationships.
	 *
	 * @param familyTreeRelationsipDto
	 * @return FamilyTreeRelationshipDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public FamilyTreeRelationshipDto fetchRelationshipInfo(FamilyTreeRelationshipDto familyTreeRelationsipDto) {
		FamilyTreeRelationshipDto familyTreeRelationshipDto = new FamilyTreeRelationshipDto();

		Long idStage = familyTreeRelationsipDto.getIdStage();
		Long idCase = familyTreeRelationsipDto.getIdCase();
		/*
		 * Check if the stage id is not null or 0 before calling the dao
		 * implementation to get the stage details.
		 */
		if (!ObjectUtils.isEmpty(idStage) && 0 != idStage) {
			StageValueBeanDto stageValueBeanDto = stageDao.retrieveStageInfo(idStage);

			if (!ServiceConstants.CSTAGES_ARI.equalsIgnoreCase(stageValueBeanDto.getCdStage())
					&& !ObjectUtils.isEmpty(stageValueBeanDto.getDtStageClose())) {
				familyTreeRelationsipDto.setDtStageClosed(stageValueBeanDto.getDtStageClose());
			}else if(ServiceConstants.CSTAGES_ARI.equalsIgnoreCase(stageValueBeanDto.getCdStage())){
				if(ServiceConstants.GENERIC_END_DATE.equals(stageValueBeanDto.getDtStageClose())){
					familyTreeRelationsipDto.setDtStageClosed(null);
				}else{
					familyTreeRelationsipDto.setDtStageClosed(stageValueBeanDto.getDtStageClose());
				}
			}
		}
		/*
		 * Check if the case id is not null or 0 before calling the dao
		 * implementation to get the case details.
		 */
		else if (!ObjectUtils.isEmpty(idCase) && 0 != idCase) {
			CapsCaseDto capsCaseDto = capsCaseDao.getCaseDetails(idCase);
			if (!ObjectUtils.isEmpty(capsCaseDto.getDtCaseClosed())) {
				familyTreeRelationsipDto.setDtCaseClosed(capsCaseDto.getDtCaseClosed());
			}
		}
		/*
		 * If the stage id is present , then get the stage close date. Else get
		 * the case closed date.
		 */
		Date dtClosed = (!ObjectUtils.isEmpty(idStage) && 0 != idStage) ? familyTreeRelationsipDto.getDtStageClosed()
				: familyTreeRelationsipDto.getDtCaseClosed();
		/*
		 * Calling the method to get the stage person list from the
		 * STAGE_PERSON_LINK table.
		 */
		List<PersonValueDto> contextPersonList = selectContextPersonList(idCase, idStage, dtClosed);
		familyTreeRelationshipDto.setContextPersonList(contextPersonList);
		List<FTPersonRelationDto> relations = fetchRelationships(familyTreeRelationsipDto);
		familyTreeRelationshipDto.setRelations(relations);
		String enableGraph = ftRelationshipDao.selectOnlineParameterValue(ServiceConstants.FAMILY_TREE_GRAPH);
		familyTreeRelationshipDto.setIndDisableGraph(
				ServiceConstants.Y.equals(enableGraph) ? ServiceConstants.FALSEVAL : ServiceConstants.TRUEVAL);
		familyTreeRelationshipDto.getContextPerson()
				.setPersonId(familyTreeRelationsipDto.getContextPerson().getPersonId());
		familyTreeRelationshipDto.setScopeStage(familyTreeRelationsipDto.getScopeStage());
		familyTreeRelationshipDto.setScopeCase(familyTreeRelationsipDto.getScopeCase());
		familyTreeRelationshipDto.setScopeAllDirect(familyTreeRelationsipDto.getScopeAllDirect());
		if (!ObjectUtils.isEmpty(familyTreeRelationsipDto.getIdStage())) {
			familyTreeRelationshipDto.setIdStage(familyTreeRelationsipDto.getIdStage());
		}
		if (!ObjectUtils.isEmpty(familyTreeRelationsipDto.getIdCase())) {
			familyTreeRelationshipDto.setIdCase(familyTreeRelationsipDto.getIdCase());
		}
		if (!ObjectUtils.isEmpty(familyTreeRelationsipDto.getDtStageClosed())) {
			familyTreeRelationshipDto.setDtStageClosed(familyTreeRelationsipDto.getDtStageClosed());
		}
		if (!ObjectUtils.isEmpty(familyTreeRelationsipDto.getDtCaseClosed())) {
			familyTreeRelationshipDto.setDtCaseClosed(familyTreeRelationsipDto.getDtCaseClosed());
		}

		familyTreeRelationshipDto
				.setIndFilterSensitiveRelations(familyTreeRelationsipDto.isIndFilterSensitiveRelations());
		return familyTreeRelationshipDto;
	}

	/**
	 *
	 * Method Name: generateRelSuggestionsRelint Method Description:This
	 * function suggests the relationships based on Rel/Int values.
	 *
	 * @param idStage
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public List<FTPersonRelationDto> generateRelSuggestionsRelint(Long idStage) {
		List<FTPersonRelationDto> sugRelList = new ArrayList<>();
		Map<String, FTPersonRelationDto> relRelintDefMap = ftRelationshipDao.selectRelintRelMappingData();
		List<PersonValueDto> personList = ftRelationshipDao.selectPersonListFromStage(idStage);
		updateClosedPersonWithFwdPerson(personList);
		PersonValueDto ctxPerson = FTFamilyTreeUtil.getDefaultContextPerson(personList);
		Long idContextPerson = ctxPerson.getPersonId();
		if (!ObjectUtils.isEmpty(idContextPerson) && idContextPerson != ServiceConstants.ZERO_VAL) {
			personList = ftRelationshipSuggestionUtils.removeDupRelSuggestions(personList);

			List<FTPersonRelationDto> fTPersonRelationDtoList = fTRelationshipSearchDao
					.selectAllDirectRelationships(idContextPerson);

			List<FTPersonRelationDto> ctxPersonRelations = fTPersonRelationDtoList;

			ctxPersonRelations = ftRelationshipSuggestionUtils.removeInvlidRelations(ctxPersonRelations);
			List<Long> relatedPersonIdList = ftRelationshipSuggestionUtils.createRelatedPersonList(ctxPersonRelations,
					idContextPerson);
			for (PersonValueDto personValueDto : personList) {
				String relInt = personValueDto.getCdStagePersRelInt();
				Long idRelatedPerson = personValueDto.getPersonId();
				if (relatedPersonIdList.contains(idRelatedPerson) == ServiceConstants.FALSEVAL
						&& !TypeConvUtil.isNullOrEmpty(relInt)) {
					FTPersonRelationDto relRelintDefValueBeanDto = relRelintDefMap.get(relInt);
					if (!TypeConvUtil.isNullOrEmpty(relRelintDefValueBeanDto)
							&& (ftRelationshipSuggestionUtils.validateRelintBasedSuggestions(ctxPerson, personValueDto,
									relRelintDefValueBeanDto, ctxPersonRelations))) {

						FTPersonRelationDto relValueBeanDto = new FTPersonRelationDto();
						relValueBeanDto.setIdPerson(idContextPerson);
						relValueBeanDto.setNmContextPerson(ctxPerson.getFullName());
						relValueBeanDto.setNmContextPersonFull(ctxPerson.getFullName());
						relValueBeanDto.setAgeContextPerson(ctxPerson.getAge());
						relValueBeanDto.setCdSexContextPerson(ctxPerson.getSex());
						relValueBeanDto.setIdRelatedPerson(idRelatedPerson);
						relValueBeanDto.setNmRelatedPerson(personValueDto.getFullName());
						relValueBeanDto.setNmRelatedPersonFull(personValueDto.getFullName());
						relValueBeanDto.setAgeRelatedPerson(personValueDto.getAge());
						relValueBeanDto.setCdSexRelatedPerson(personValueDto.getSex());
						relValueBeanDto.setCdOrigin(ServiceConstants.CRELSGOR_RE);

						relValueBeanDto.setCdRelation(relRelintDefValueBeanDto.getCdRelation());
						relValueBeanDto.setCdLineage(relRelintDefValueBeanDto.getCdLineage());
						relValueBeanDto.setCdType(relRelintDefValueBeanDto.getCdType());
						relValueBeanDto.setCdSeparation(relRelintDefValueBeanDto.getCdSeparation());
						if (StringUtil.isValid(relRelintDefValueBeanDto.getCdType()))
							relValueBeanDto.setIndTypeSuggested(true);
						if (StringUtil.isValid(relRelintDefValueBeanDto.getCdLineage()))
							relValueBeanDto.setIndLineageSuggested(true);
						sugRelList.add(relValueBeanDto);
					}
				}
			}
		}
		return sugRelList;
	}

	/**
	 *
	 * Method Name: genSuggestionsBasedonExistingRel Method Description:This
	 * function suggests the relationships based on existing Relationships.
	 *
	 * @param idStage
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public List<FTPersonRelationDto> genSuggestionsBasedonExistingRel(Long idStage) {
		List<FTPersonRelationDto> allSuggestedRelations = new ArrayList<>();
		List<PersonValueDto> personList = ftRelationshipDao.selectContextPersons(ServiceConstants.Zero_Value, idStage);
		List<FTPersonRelationDto> stageRelations = fTRelationshipSearchDao.selectStagePersonListRelations(idStage);
		stageRelations = filterClosedPersonRelations(stageRelations);
		stageRelations = ftRelationshipSuggestionUtils.removeInvlidRelations(stageRelations);
		List<Long> personIdList = ftRelationshipSuggestionUtils.extractAllIdPersons(stageRelations);
		List<FTPersonRelationDto> allRelationsWithinRelated = new ArrayList<>();
		if (personIdList.size() > ServiceConstants.Zero) {
			allRelationsWithinRelated = ftRelationshipDao.selectRelationshipsAmongPersonList(personIdList);
		}
		populatePersonDetails(allRelationsWithinRelated);
		for (PersonValueDto personValueDto : personList) {
			Long idPerson = personValueDto.getPersonId();
			List<Long> relatedPersonIdList = ftRelationshipSuggestionUtils.createRelatedPersonList(stageRelations,
					idPerson);
			List<FTPersonRelationDto> suggestedRelations = ftRelationshipSuggestionUtils.suggestRelationsWithinTheList(
					relatedPersonIdList, allRelationsWithinRelated, allSuggestedRelations, idPerson);
			allSuggestedRelations.addAll(suggestedRelations);
		}
		return allSuggestedRelations;
	}

	/**
	 *
	 * Method Name: populatePersonDetails Method Description:This function
	 * populates Person Details (Name, Age, Gender, DOB and DOD) for all the
	 * Relations.
	 *
	 * @param relations
	 * @return Long
	 */
	private Long populatePersonDetails(List<FTPersonRelationDto> relations) {
		if (CollectionUtils.isNotEmpty(relations)) {
			List<Long> personIdList = ftRelationshipSuggestionUtils.extractAllIdPersons(relations);
			Map<Long, PersonValueDto> personDetailsMap = ftRelationshipDao.selectPersonDetails(personIdList);
			for (FTPersonRelationDto fTPersonRelationDto : relations) {
				PersonValueDto personDetails = (PersonValueDto) personDetailsMap.get(fTPersonRelationDto.getIdPerson());
				if (!TypeConvUtil.isNullOrEmpty(personDetails)
						&& personDetails.getPersonId() != ServiceConstants.Zero_Value) {
					fTPersonRelationDto.setNmContextPerson(personDetails.getFullName());
					fTPersonRelationDto.setNmContextPersonFull(personDetails.getFullName());
					fTPersonRelationDto.setAgeContextPerson(personDetails.getAge());
					fTPersonRelationDto.setCdSexContextPerson(personDetails.getSex());
				}
				PersonValueDto relatedPersonDetails = (PersonValueDto) personDetailsMap
						.get(fTPersonRelationDto.getIdRelatedPerson());
				if (!TypeConvUtil.isNullOrEmpty(relatedPersonDetails)
						&& relatedPersonDetails.getPersonId() != ServiceConstants.ZERO_VAL) {
					fTPersonRelationDto.setNmRelatedPersonFull(relatedPersonDetails.getFullName());
					fTPersonRelationDto.setNmRelatedPerson(relatedPersonDetails.getFullName());
					fTPersonRelationDto.setAgeRelatedPerson(relatedPersonDetails.getAge());
					fTPersonRelationDto.setCdSexRelatedPerson(relatedPersonDetails.getSex());
				}
			}
		}
		return (long) relations.size();
	}

	/**
	 *
	 * Method Name: selectSensitveCasePersons Method Description:This method
	 * returns the list of persons with sensitive case access.
	 *
	 * @param personIdList
	 * @return List<Long>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public List<Long> selectSensitveCasePersons(List<Long> personIdList) {
		LOG.debug("Entering method selectSensitveCasePersons in FTRelationshipService");
		List<Long> personList = ftRelationshipDao.selectSensitveCasePersons(personIdList);
		LOG.debug("Exiting method selectSensitveCasePersons in FTRelationshipService");
		return personList;
	}

	/**
	 *
	 * Method Name: filterClosedPersonRelations Method Description:This method
	 * removes Closed Person Relationships.
	 *
	 * @param fTPersonRelationBeanList
	 * @return List<FTPersonRelationValueDto>
	 */
	@Override
	public List<FTPersonRelationDto> filterClosedPersonRelations(List<FTPersonRelationDto> fTPersonRelationBeanList) {
		List<FTPersonRelationDto> fTPersonRelationValue = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(fTPersonRelationBeanList)) {
			for (FTPersonRelationDto fTPersonRelationValueDto : fTPersonRelationBeanList) {
				if (ServiceConstants.CRELSGOR_EX.equals(fTPersonRelationValueDto.getCdOrigin()))
					continue;
				if (!CodesConstant.CPERSTAT_M.equals(fTPersonRelationValueDto.getCdContextPersonStatus())
						&& !CodesConstant.CPERSTAT_M.equals(fTPersonRelationValueDto.getCdRelatedPersonStatus())) {
					fTPersonRelationValue.add(fTPersonRelationValueDto);
				}
			}
		}
		return fTPersonRelationValue;
	}

	/**
	 *
	 * Method Name: fetchRelationships Method Description:This method returns
	 * all the relationships of the context person with in the current stage or
	 * current case or all based on the input.
	 *
	 * @param familyTreeRelationshipDto
	 * @return List<FTPersonRelationDto> @
	 */
	private List<FTPersonRelationDto> fetchRelationships(FamilyTreeRelationshipDto familyTreeRelationshipDto) {
		List<FTPersonRelationDto> fTPersonRelationBeanList = new ArrayList<>();

		String indScopeStage = familyTreeRelationshipDto.getScopeStage();
		String indScopeCase = familyTreeRelationshipDto.getScopeCase();
		String indScopeAllDirect = familyTreeRelationshipDto.getScopeAllDirect();
		Long idContextPerson = familyTreeRelationshipDto.getContextPerson().getPersonId();
		Long idCase = familyTreeRelationshipDto.getIdCase();
		Long idStage = familyTreeRelationshipDto.getIdStage();
		Date dtStageClosed = familyTreeRelationshipDto.getDtStageClosed();
		Date dtCaseClosed = familyTreeRelationshipDto.getDtCaseClosed();
		Date dtClosed = (ObjectUtils.isEmpty(idStage) || (!ObjectUtils.isEmpty(idStage) && 0 != idStage))
				? dtStageClosed : dtCaseClosed;
		if (ServiceConstants.Y.equals(indScopeStage)) {
			if (!ObjectUtils.isEmpty(idContextPerson) && idContextPerson == ServiceConstants.ALL_SELECTED) {
				if (DateUtils.isNull(dtClosed))
					fTPersonRelationBeanList = fTRelationshipSearchDao.selectAllRelationshipsWithinStage(idStage);
				else
					fTPersonRelationBeanList = fTRelationshipSearchDao
							.selectAllRelWithinClosedCaseOrStage(ServiceConstants.Zero_Value, idStage, dtClosed);
			} else {
				if (DateUtils.isNull(dtClosed))
					fTPersonRelationBeanList = fTRelationshipSearchDao.selectPersonRelationshipsWithinStage(idStage,
							idContextPerson);
				else
					fTPersonRelationBeanList = fTRelationshipSearchDao.selectPersonRelWithinClosedCaseOrStage(
							ServiceConstants.Zero_Value, idStage, idContextPerson, dtClosed);
			}
		} else if (ServiceConstants.Y.equals(indScopeCase)) {
			if (!ObjectUtils.isEmpty(idContextPerson) && idContextPerson == ServiceConstants.ALL_SELECTED) {
				if (DateUtils.isNull(dtClosed))
					fTPersonRelationBeanList = fTRelationshipSearchDao.selectAllRelationshipsWithinCase(idCase);
				else
					fTPersonRelationBeanList = fTRelationshipSearchDao.selectAllRelWithinClosedCaseOrStage(idCase,
							idStage, dtClosed);
			} else {
				if (DateUtils.isNull(dtClosed))
					fTPersonRelationBeanList = fTRelationshipSearchDao.selectPersonRelationshipsWithinCase(idCase,
							idContextPerson);
				else
					fTPersonRelationBeanList = fTRelationshipSearchDao.selectPersonRelWithinClosedCaseOrStage(idCase,
							idStage, idContextPerson, dtClosed);
			}
		} else if (ServiceConstants.Y.equals(indScopeAllDirect)) {
			if (!ObjectUtils.isEmpty(idContextPerson) && idContextPerson == ServiceConstants.ALL_SELECTED) {
				if (ObjectUtils.isEmpty(idStage)
						|| (!ObjectUtils.isEmpty(idStage) && idStage == ServiceConstants.Zero_Value)) {
					if (DateUtils.isNull(dtCaseClosed))
						fTPersonRelationBeanList = fTRelationshipSearchDao.selectCasePersonListRelations(idCase);
					else
						fTPersonRelationBeanList = fTRelationshipSearchDao.selectAllDirectRelInClosedCaseOrStage(idCase,
								idStage, dtCaseClosed);
				} else {
					if (DateUtils.isNull(dtStageClosed))
						fTPersonRelationBeanList = fTRelationshipSearchDao.selectStagePersonListRelations(idStage);
					else
						fTPersonRelationBeanList = fTRelationshipSearchDao.selectAllDirectRelInClosedCaseOrStage(
								ServiceConstants.Zero_Value, idStage, dtStageClosed);
				}
			} else {
				if (DateUtils.isNull(dtClosed)) {
					fTPersonRelationBeanList = fTRelationshipSearchDao.selectAllDirectRelationships(idContextPerson);
				} else
					fTPersonRelationBeanList = fTRelationshipSearchDao
							.selectAllDirectRelPersonClosedCaseorStage(idContextPerson, dtClosed);
			}
		}
		if (!ObjectUtils.isEmpty(idContextPerson) && idContextPerson != ServiceConstants.ALL_SELECTED) {
			ftRelationshipSuggestionUtils.reverseRelations(fTPersonRelationBeanList, idContextPerson);
		}
		fTPersonRelationBeanList = filterRelationships(familyTreeRelationshipDto, fTPersonRelationBeanList);
		return fTPersonRelationBeanList;
	}

	/**
	 *
	 * Method Name: selectContextPersonList Method Description:This method
	 * returns Context Person List for the given Context, Stage/Case status.
	 *
	 * @param idCase
	 * @param idStage
	 * @param dtClosed
	 * @return List<PersonValueDto>
	 */
	private List<PersonValueDto> selectContextPersonList(Long idCase, Long idStage, Date dtClosed) {
		List<PersonValueDto> contextPersonList = null;
		// Open Stage/Case
		if (DateUtils.isNull(dtClosed)) {
			contextPersonList = ftRelationshipDao.selectContextPersons(idCase, idStage);
		}
		// Case context
		else if (ObjectUtils.isEmpty(idStage) || (!ObjectUtils.isEmpty(idStage) && idStage == 0)) {
			contextPersonList = ftRelationshipDao.selectContextPersons(idCase, idStage, dtClosed);
		}
		// Stage Context
		else {
			contextPersonList = ftRelationshipDao.selectContextPersons(idCase, idStage);
		}
		for (PersonValueDto personValueDto : contextPersonList) {
			String fullName = personValueDto.getFullName();
			if (ObjectUtils.isEmpty(fullName) || REPORTER.equals(fullName)) {
				fullName = (new StringBuilder()).append("Unknown (").append(personValueDto.getPersonId()).append(")")
						.toString();
			}
			if (!ObjectUtils.isEmpty(personValueDto.getAge())) {
				fullName = (new StringBuilder()).append(fullName).append("-")
						.append(String.valueOf(personValueDto.getAge())).toString();
			}
			personValueDto.setFullName(fullName);
		}
		return contextPersonList;
	}

	/**
	 *
	 * Method Name: filterRelationships Method Description:This method filters
	 * (removes) relationships based on Sensitive Case Access, Staff, Closed
	 * Person etc.
	 *
	 * @param fTRelationshipDBDto
	 * @param relations
	 * @return List<FTPersonRelationDto>
	 */
	private List<FTPersonRelationDto> filterRelationships(FamilyTreeRelationshipDto fTRelationshipDBDto,
			List<FTPersonRelationDto> relations) {
		Long idStage = fTRelationshipDBDto.getIdStage();
		Date dtStageClosed = fTRelationshipDBDto.getDtStageClosed();
		Date dtCaseClosed = fTRelationshipDBDto.getDtCaseClosed();
		Date dtClosed = (idStage != ServiceConstants.Zero_Value) ? dtStageClosed : dtCaseClosed;
		relations = FTFamilyTreeUtil.removeExternalRelations(relations);
		populateAddlInfoForRelations(relations, fTRelationshipDBDto);
		Boolean filterSensitiveRel = fTRelationshipDBDto.isIndFilterSensitiveRelations();
		if (!ObjectUtils.isEmpty(idStage) && idStage != ServiceConstants.Zero_Value
				&& fTRelationshipDBDto.getIdUser() != ServiceConstants.Zero_Value
				&& !fTRelationshipDBDto.isIndUserHasSensitiveAccess()) {
			StagePersonValueDto splValBean = stageDao.selectStagePersonLink(fTRelationshipDBDto.getIdUser(), idStage);
			if (ServiceConstants.CROLEALL_HP.equals(splValBean.getCdStagePersRole())) {
				filterSensitiveRel = Boolean.TRUE;
			}
		}
		if (filterSensitiveRel && CollectionUtils.isNotEmpty(relations)) {
			List<FTPersonRelationDto> tmpRelations = filterStaffAndSensitiveRecords(relations, idStage);
			if (tmpRelations.size() != relations.size()) {
				relations = tmpRelations;
				fTRelationshipDBDto.setIndFilterSensitiveRelations(ServiceConstants.TRUEVAL);
			}
		}
		if (DateUtils.isNull(dtClosed)) {
			relations = filterClosedPersonRelations(relations);
		} else {
			relations = filterClosedPersonRelationsClosedCaseStage(relations, dtClosed);
		}
		return relations;
	}

	/**
	 *
	 * Method Name: filterStaffAndSensitiveRecords Method Description:This
	 * method removes Staff Relationships and the Relationships of the persons
	 * involved in sensitive cases.
	 *
	 * @param relations
	 * @param idStage
	 * @return List<FTPersonRelationDto>
	 */
	private List<FTPersonRelationDto> filterStaffAndSensitiveRecords(List<FTPersonRelationDto> relations,
			Long idStage) {
		List<FTPersonRelationDto> tmpRelations = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(relations)) {
			for (FTPersonRelationDto fTPersonRelationDto : relations) {
				if (idStage != ServiceConstants.ZERO_VAL) {
					if ((!fTPersonRelationDto.isContextPersonStaffForSensitive()
							|| !fTPersonRelationDto.isContextPersonNotInStagePersonList())
							&& (!fTPersonRelationDto.isRelatedPersonStaffForSensitive()
									|| !fTPersonRelationDto.getRelatedPersonNotInStagePersonList())) {
						tmpRelations.add(fTPersonRelationDto);
					}
				} else {
					if ((!fTPersonRelationDto.isContextPersonStaffForSensitive()
							|| !fTPersonRelationDto.isContextPersonNotInCasePersonList())
							&& (!fTPersonRelationDto.isRelatedPersonStaffForSensitive()
									|| !fTPersonRelationDto.isRelatedPersonNotInCasePersonList())) {
						tmpRelations.add(fTPersonRelationDto);
					}
				}
			}
			List<Long> personIdList = new ArrayList<>();
			for (FTPersonRelationDto fTPersonRelationDto : tmpRelations) {
				Long idContextPerson = fTPersonRelationDto.getIdPerson();
				Long idRelatedPerson = fTPersonRelationDto.getIdRelatedPerson();
				if (fTPersonRelationDto.isContextPersonNotInCasePersonList()) {
					personIdList.add(idContextPerson);
				}
				if (fTPersonRelationDto.isRelatedPersonNotInCasePersonList()) {
					personIdList.add(idRelatedPerson);
				}
			}
			if (CollectionUtils.isNotEmpty(personIdList)) {
				List<Long> sensPersonList = selectSensitveCasePersons(personIdList);
				List<FTPersonRelationDto> tmpRelations2 = new ArrayList<>();
				for (FTPersonRelationDto ftPersonRelationBean : tmpRelations) {
					Long idContextPerson = ftPersonRelationBean.getIdPerson();
					Long idRelatedPerson = ftPersonRelationBean.getIdRelatedPerson();
					if (!sensPersonList.contains(idContextPerson) && !sensPersonList.contains(idRelatedPerson)) {
						tmpRelations2.add(ftPersonRelationBean);
					}
				}
				tmpRelations = tmpRelations2;
			}
		}
		return tmpRelations;
	}

	/**
	 *
	 * Method Name: filterClosedPersonRelationsClosedCaseStage Method
	 * Description:This method removes Closed Person Relationships
	 *
	 * @param relations
	 * @param idStage
	 * @param dtClosed
	 * @return List<FTPersonRelationDto>
	 */
	private List<FTPersonRelationDto> filterClosedPersonRelationsClosedCaseStage(List<FTPersonRelationDto> relations,
			Date dtClosed) {
		if (CollectionUtils.isNotEmpty(relations)) {
			List<Long> personIdList = ftRelationshipSuggestionUtils.extractAllIdPersons(relations);
			List<Long> closedPersonList = ftRelationshipDao.selectPeopleClosedAtGivenDate(personIdList, dtClosed);
			List<FTPersonRelationDto> tmpRelations = new ArrayList<>();
			for (FTPersonRelationDto ftPersonRelationDto : relations) {
				Long idPerson = ftPersonRelationDto.getIdPerson();
				Long idRelatedPerson = ftPersonRelationDto.getIdRelatedPerson();
				if (!closedPersonList.contains(idPerson) && !closedPersonList.contains(idRelatedPerson)) {
					tmpRelations.add(ftPersonRelationDto);
				}
			}
			relations = tmpRelations;
		}
		return relations;
	}

	/**
	 *
	 * Method Name: populateAddlInfoForRelations Method Description:This
	 * function populates following Additional Details about the people with in
	 * the Relationships.
	 *
	 * @param relations
	 * @param fTRelationshipDBDto
	 * @return
	 */
	private Long populateAddlInfoForRelations(List<FTPersonRelationDto> relations,
			FamilyTreeRelationshipDto fTRelationshipDBDto) {
		if (CollectionUtils.isNotEmpty(relations)) {
			markPeopleNotinCurrentStage(relations, fTRelationshipDBDto.getIdStage());
			markPeopleNotinCurrentCase(relations, fTRelationshipDBDto.getIdCase());
			markStaffRecords(relations);
			markStaffRecordsForSensitiveFitlering(relations);
		}
		return fTRelationshipDBDto.getIdCase();
	}

	/**
	 *
	 * Method Name: markPeopleNotinCurrentStage Method Description:This function
	 * marks the people that are not in the current Stage.
	 *
	 * @param relations
	 * @param idStage
	 * @return
	 */
	private void markPeopleNotinCurrentStage(List<FTPersonRelationDto> relations, Long idStage) {
		if (idStage != ServiceConstants.ZERO_VAL && CollectionUtils.isNotEmpty(relations)
				&& relations.size() > ServiceConstants.ZERO_VAL) {
			List<PersonValueDto> personValueDtoList = ftRelationshipDao.selectStagePersonList(idStage);
			List<Long> stagePersonIdList = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(personValueDtoList)) {
				for (PersonValueDto personValueDto : personValueDtoList) {
					stagePersonIdList.add(personValueDto.getPersonId());
				}
			}

			for (FTPersonRelationDto fTPersonRelationDto : relations) {
				Long idContextPerson = fTPersonRelationDto.getIdPerson();
				Long idRelatedPerson = fTPersonRelationDto.getIdRelatedPerson();
				if (!stagePersonIdList.contains(idContextPerson)) {
					fTPersonRelationDto.setContextPersonNotInStagePersonList(ServiceConstants.TRUEVAL);
				}
				if (!stagePersonIdList.contains(idRelatedPerson)) {
					fTPersonRelationDto.setRelatedPersonNotInStagePersonList(ServiceConstants.TRUEVAL);
				}
			}
		}
	}

	/**
	 *
	 * Method Name: markPeopleNotinCurrentCase Method Description:This function
	 * marks the people that are not in the current Case.
	 *
	 * @param relations
	 * @param idCase
	 * @return
	 */
	private void markPeopleNotinCurrentCase(List<FTPersonRelationDto> relations, Long idCase) {
		if (idCase != ServiceConstants.ZERO_VAL && CollectionUtils.isNotEmpty(relations)) {
			List<Long> casePersonIdList = ftRelationshipDao.selectCasePersonList(idCase);
			for (FTPersonRelationDto fTPersonRelationDto : relations) {
				Long idContextPerson = (fTPersonRelationDto.getIdPerson());
				Long idRelatedPerson = (fTPersonRelationDto.getIdRelatedPerson());
				if (!casePersonIdList.contains(idContextPerson)) {
					fTPersonRelationDto.setContextPersonNotInCasePersonList(ServiceConstants.TRUEVAL);
				}
				if (!casePersonIdList.contains(idRelatedPerson)) {
					fTPersonRelationDto.setRelatedPersonNotInCasePersonList(ServiceConstants.TRUEVAL);
				}
			}
		}
	}

	/**
	 *
	 * Method Name: markStaffRecords Method Description:This function marks the
	 * people that are Staff.
	 *
	 * @param relations
	 * @return
	 */
	private void markStaffRecords(List<FTPersonRelationDto> relations) {
		if (CollectionUtils.isNotEmpty(relations)) {
			List<Long> personIdList = ftRelationshipSuggestionUtils.extractAllIdPersons(relations);
			List<Long> staffIdList = ftRelationshipDao.selectStaffRecordsAmongPersonList(personIdList);
			for (FTPersonRelationDto fTPersonRelationDto : relations) {
				Long idContextPerson = fTPersonRelationDto.getIdPerson();
				Long idRelatedPerson = fTPersonRelationDto.getIdRelatedPerson();
				if (staffIdList.contains(idContextPerson)) {
					fTPersonRelationDto.setContextPersonStaff(ServiceConstants.TRUE_VALUE);
				}
				if (staffIdList.contains(idRelatedPerson)) {
					fTPersonRelationDto.setRelatedPersonStaff(ServiceConstants.TRUE_VALUE);
				}
			}
		}
	}

	/**
	 *
	 * Method Name: markStaffRecordsForSensitiveFitlering Method
	 * Description:This function marks the people that are Staff for the purpose
	 * of Family Tree sensitive information filtering.
	 *
	 * For the purpose of suppressing information in the Family Tree
	 * Relationship search results, Staff is defined as a Person with an
	 * Employee record with no external indicator or a Person with an Employee
	 * record with an external indicator but access to IMPACT.
	 *
	 * @param relations
	 * @return
	 */
	private void markStaffRecordsForSensitiveFitlering(List<FTPersonRelationDto> relations) {
		if (CollectionUtils.isNotEmpty(relations)) {
			List<Long> personIdList = ftRelationshipSuggestionUtils.extractAllIdPersons(relations);
			List<Long> staffIdList = ftRelationshipDao.selectStaffRecordsForSensitiveFitlering(personIdList);
			for (FTPersonRelationDto fTPersonRelationValueDto : relations) {
				Long idContextPerson = fTPersonRelationValueDto.getIdPerson();
				Long idRelatedPerson = fTPersonRelationValueDto.getIdRelatedPerson();
				if (staffIdList.contains(idContextPerson)) {
					fTPersonRelationValueDto.setContextPersonStaffForSensitive(ServiceConstants.TRUE_VALUE);
				}
				if (staffIdList.contains(idRelatedPerson)) {
					fTPersonRelationValueDto.setRelatedPersonStaffForSensitive(ServiceConstants.TRUE_VALUE);
				}
			}
		}
	}

	/**
	 * Method Name: getRelationshipDetails Method Description:This method will
	 * be called to fetch the data on "Family Tree Relationship Details" page.
	 * It retrieves Context Person List, Associated Person List based on the
	 * Family Tree Context. It also retrieves single Record of Relationship if
	 * the id is passed.
	 *
	 * @param familyTreeRelationsReq
	 *            - This request object will hold the input parameters for
	 *            retrieving the Family Tree Relationship details.
	 * @return FamilyTreeRelationsRes - This response object will hold the dto
	 *         containing the family tree relationship information.
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public FamilyTreeRelationsRes getRelationshipDetails(FamilyTreeRelationsReq familyTreeRelationsReq) {
		FamilyTreeRelationsRes familyTreeRelationsRes = new FamilyTreeRelationsRes();
		FamilyTreeRelationshipDto familyTreeRelationshipDto = familyTreeRelationsReq.getFamilyTreeRelationshipDto();

		FamilyTreeRelationshipDto newFamilyTreeRelationshipDto = new FamilyTreeRelationshipDto();
		BeanUtils.copyProperties(familyTreeRelationshipDto, newFamilyTreeRelationshipDto);
		Long idPersonRelation = familyTreeRelationshipDto.getFtPersonRelationBean().getIdPersonRelation();
		Long idPersonRelationHistory = familyTreeRelationshipDto.getFtPersonRelationBean().getIdPersonRelationHistory();
		Long idCase = familyTreeRelationshipDto.getIdCase();
		Long idStage = familyTreeRelationshipDto.getIdStage();

		/*
		 * If the idStage is present , then retrieving the stage information to
		 * check if the stage is closed. If the stage is closed then setting the
		 * date closed in the output dto
		 */
		if (!ObjectUtils.isEmpty(idStage) && 0 != idStage) {
			StageValueBeanDto stage = stageDao.retrieveStageInfoList(idStage);
			if (!ObjectUtils.isEmpty(stage.getDtStageClose())) {
				newFamilyTreeRelationshipDto.setDtStageClosed(stage.getDtStageClose());
			}
		}
		/*
		 * If the case id is present , then retrieving the case information to
		 * check if the case is closed. If the case is closed , then setting the
		 * case closed date in the output object.
		 */
		else if (!ObjectUtils.isEmpty(idCase) && 0 != idCase) {
			CapsCaseDto capsCaseDto = capsCaseDao.getCapsCaseByid(idCase);
			if (!ObjectUtils.isEmpty(capsCaseDto.getDtCaseClosed())) {
				newFamilyTreeRelationshipDto.setDtCaseClosed(capsCaseDto.getDtCaseClosed());
			}
		}

		Long idContextPerson;
		if ((!ObjectUtils.isEmpty(idPersonRelation) && 0 != idPersonRelation)
				|| (!ObjectUtils.isEmpty(idPersonRelationHistory) && 0 != idPersonRelationHistory)) {
			/*
			 * Getting the person relation information from the
			 * PERSON_RELATION_HISTORY table or PERSON_HISTORY table based on if
			 * the value for idPersonRelationHistory is present in the request
			 * dto.
			 */
			FTPersonRelationBean ftPersonRelationBean = (!ObjectUtils.isEmpty(idPersonRelationHistory)
					&& idPersonRelationHistory != 0)
							? ftRelationshipDao.selectRelationshipHistory(idPersonRelationHistory)
							: ftRelationshipDao.selectRelationship(idPersonRelation);
			// Setting the relationship information in the output dto.
			newFamilyTreeRelationshipDto.setFtPersonRelationBean(ftPersonRelationBean);
			idContextPerson = ftPersonRelationBean.getIdPerson();

			/*
			 * Calling the dao implementation to get the person details to set
			 * the name of the person creating the relationship in the output
			 * dto
			 */
			Person createdPerson = personDao.getPersonByPersonId(ftPersonRelationBean.getIdCreatedPerson());
			if (!ObjectUtils.isEmpty(createdPerson)) {
				ftPersonRelationBean.setNmCreatedPerson(createdPerson.getNmPersonFull());
			}
			/*
			 * Calling the dao implementation to get the person details to set
			 * the name of the last person who updated the relationship in the
			 * output dto
			 */
			Person lastUpdatePerson = personDao.getPersonByPersonId(ftPersonRelationBean.getIdLastUpdatePerson());

			if (!ObjectUtils.isEmpty(lastUpdatePerson)) {
				ftPersonRelationBean.setNmLastUpdatePerson(lastUpdatePerson.getNmPersonFull());
			}
			/*
			 * Calling the dao implementation to fetch details from the CAPSCASE
			 * and STAGE_PERSON_LINK table of the related cases in which the
			 * person are present.
			 */
			List<Long> relatedOpenCases = ftRelationshipDao.findRelatedOpenCases(ftPersonRelationBean.getIdPerson(),
					ftPersonRelationBean.getIdRelatedPerson());
			relatedOpenCases.remove(familyTreeRelationshipDto.getIdCase());
			newFamilyTreeRelationshipDto.setRelatedOpenCases(relatedOpenCases);
		} else {
			idContextPerson = familyTreeRelationshipDto.getFtPersonRelationBean().getIdPerson();
		}
		/*
		 * Calling the method to populate the person list for the context person
		 * in the relationship.
		 */
		populateContextPersonList(familyTreeRelationshipDto, newFamilyTreeRelationshipDto);
		/*
		 * Calling the method to populate the person details of the context
		 * person and the associated person in the relationship.
		 */
		populateContextAndRelatedPersonDetails(familyTreeRelationshipDto, newFamilyTreeRelationshipDto,
				idContextPerson);
		// Setting the dto in the response object.
		familyTreeRelationsRes.setFamilyTreeRelationshipDto(newFamilyTreeRelationshipDto);
		return familyTreeRelationsRes;
	}

	/**
	 * Method Name: populateContextAndRelatedPersonDetails Method
	 * Description:This method is used to populate the person detail information
	 * for
	 *
	 * @param familyTreeRelationshipDto
	 *            - The dto containing the request parameter values.
	 * @param newFamilyTreeRelationshipDto
	 *            - The dto contaning the response values of the family tree
	 *            relationship.
	 * @param idContextPerson
	 *            - The id of the person for whom the relationship is created.
	 */
	private void populateContextAndRelatedPersonDetails(FamilyTreeRelationshipDto familyTreeRelationshipDto,
			FamilyTreeRelationshipDto newFamilyTreeRelationshipDto, Long idContextPerson) {

		Long idCase = familyTreeRelationshipDto.getIdCase();
		Long idStage = familyTreeRelationshipDto.getIdStage();
		if ((!ObjectUtils.isEmpty(idStage) && 0 != idStage) || (!ObjectUtils.isEmpty(idCase) && 0 != idCase)) {
			/*
			 * If the idContextPerson is 0 then getting the default context
			 * person.
			 */
			if ((!ObjectUtils.isEmpty(idContextPerson) && idContextPerson == 0)
					|| ObjectUtils.isEmpty(idContextPerson)) {
				PersonValueDto contextPerson = getDefaultContextPerson(
						newFamilyTreeRelationshipDto.getContextPersonList());
				if (!ObjectUtils.isEmpty(contextPerson.getPersonId()) && contextPerson.getPersonId() != 0) {
					contextPerson = getPersonDetails(contextPerson.getPersonId(), idStage);
					newFamilyTreeRelationshipDto.setContextPerson(contextPerson);
					newFamilyTreeRelationshipDto.getContextPerson().setPersonId(contextPerson.getPersonId());
					familyTreeRelationshipDto.getContextPerson().setPersonId(contextPerson.getPersonId());
				}

			}
			/*
			 * If the context person is not 0 then getting the context person
			 * details and setting the person as context person in the dto to be
			 * returned as response.
			 */
			else {
				PersonValueDto contextPerson = getPersonDetails(idContextPerson, idStage);
				newFamilyTreeRelationshipDto.setContextPerson(contextPerson);
			}
			/*
			 * If the associated person is not 0 then getting the associated
			 * person details and setting the person as associated person in the
			 * dto to be returned as response.
			 */
			if (!ObjectUtils.isEmpty(newFamilyTreeRelationshipDto.getFtPersonRelationBean().getIdRelatedPerson())
					&& 0 != newFamilyTreeRelationshipDto.getFtPersonRelationBean().getIdRelatedPerson()) {
				PersonValueDto associatedPerson = getPersonDetails(
						newFamilyTreeRelationshipDto.getFtPersonRelationBean().getIdRelatedPerson(), idStage);
				newFamilyTreeRelationshipDto.setAssociatedPerson(associatedPerson);

				addPersonToList(newFamilyTreeRelationshipDto.getAscPersonList(), associatedPerson);
			}
		}
	}

	/**
	 * Method Name: getDefaultContextPerson Method Description:This method is
	 * used to get the default context person.
	 *
	 * @param contextPersonList
	 *            - The person list from STAGE_PERSON_LINK table.
	 * @return contextPerson - The dto with the person details.
	 */
	private PersonValueDto getDefaultContextPerson(List<PersonValueDto> contextPersonList) {
		PersonValueDto contextPerson = new PersonValueDto();
		List<PersonValueDto> personList;
		if (!CollectionUtils.isEmpty(contextPersonList)) {

			personList = contextPersonList.stream()
					.filter(person -> SPL_IND_NM_STAGE_VALID.equals(person.getIndNmStage()))
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(personList) && personList.size() == 1) {
				contextPerson = personList.get(0);
			} else {
				personList = contextPersonList.stream()
						.filter(person -> CodesConstant.CRPTRINT_SL.equals(person.getCdStagePersRelInt()))
						.collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(personList) && personList.size() == 1) {
					contextPerson = personList.get(0);
				} else {
					personList = contextPersonList.stream()
							.filter(person -> CodesConstant.CRPTRINT_OV.equals(person.getCdStagePersRelInt())
									|| CodesConstant.CRPTRINT_RC.equals(person.getCdStagePersRelInt()))
							.collect(Collectors.toList());
					if (!CollectionUtils.isEmpty(personList) && personList.size() == 1) {
						contextPerson = personList.get(0);
					}
				}
			}
		}
		return contextPerson;
	}

	/**
	 * Method Name: populateContextPersonList Method Description:This method is
	 * used to populate the context person list and the associated person list.
	 *
	 * @param familyTreeRelationshipDto
	 *            - The dto contains the input parameters
	 * @param newFamilyTreeRelationshipDto
	 *            - The dto contains the values which will be returned as part
	 *            of response.
	 */
	private void populateContextPersonList(FamilyTreeRelationshipDto familyTreeRelationshipDto,
			FamilyTreeRelationshipDto newFamilyTreeRelationshipDto) {
		Long idCase = familyTreeRelationshipDto.getIdCase();
		Long idStage = familyTreeRelationshipDto.getIdStage();
		FTPersonRelationBean ftPersonRelationBean = newFamilyTreeRelationshipDto.getFtPersonRelationBean();
		Long idPersonRelation = ftPersonRelationBean.getIdPersonRelation();
		Long idPerson;
		Long idRelatedPerson;
		List<PersonValueDto> contextPersonList = new ArrayList();
		List<PersonValueDto> ascPersonList = new ArrayList();
		/*
		 * If the stage id or the case id is not 0 , then getting the person
		 * list from the STAGE_PERSON_LINK table. The same list is used to
		 * populate the context person List and the associated person List.
		 */
		if ((!ObjectUtils.isEmpty(idStage) && 0 != idStage) || (!ObjectUtils.isEmpty(idCase) && 0 != idCase)) {
			idPerson = ftPersonRelationBean.getIdPerson();
			idRelatedPerson = ftPersonRelationBean.getIdRelatedPerson();
			contextPersonList = ftRelationshipDao.selectContextPersons(idCase, idStage);
			ascPersonList = new ArrayList(contextPersonList);
		}
		/*
		 * If the context is not the stage or case then getting the person
		 * details for the context person and associated person and populating
		 * the context person list and associated person list.
		 */
		else {
			if (!ObjectUtils.isEmpty(idPersonRelation) && 0 != idPersonRelation) {
				idPerson = ftPersonRelationBean.getIdPerson();
				idRelatedPerson = ftPersonRelationBean.getIdRelatedPerson();
			} else {
				idPerson = familyTreeRelationshipDto.getContextPerson().getPersonId();
				idRelatedPerson = familyTreeRelationshipDto.getAssociatedPerson().getPersonId();
			}
		}
		/*
		 * If the idPerson and idRelatedPerson is set in the above block then
		 * populate the person List and set it to the output dto.
		 */
		if (!ObjectUtils.isEmpty(idPerson) && 0 != idPerson) {
			PersonValueDto contextPerson = getPersonDetails(idPerson, idStage);
			addPersonToList(contextPersonList, contextPerson);
			newFamilyTreeRelationshipDto.setContextPerson(contextPerson);
		}
		if (!ObjectUtils.isEmpty(idRelatedPerson) && 0 != idRelatedPerson) {
			PersonValueDto relatedPerson = getPersonDetails(idRelatedPerson, idStage);
			addPersonToList(ascPersonList, relatedPerson);
			newFamilyTreeRelationshipDto.setAssociatedPerson(relatedPerson);
		}
		/*
		 * Setting the context person list and associated person list in the
		 * output dto.
		 */
		newFamilyTreeRelationshipDto.setContextPersonList(contextPersonList);
		newFamilyTreeRelationshipDto.setAscPersonList(ascPersonList);
	}

	/**
	 * Method Name: getPersonDetails Method Description:This method retrieves
	 * Person Details, Rel/Int value of Person with in the Stage and Person Race
	 * for the given Person.
	 *
	 * @param idPerson
	 *            - person id.
	 * @param idStage
	 *            - stage id.
	 * @return PersonValueDto - The dto containing the details of the person .
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PersonValueDto getPersonDetails(Long idPerson, Long idStage) {
		// Calling the dao implementation to get the person details.
		PersonValueDto personValueDto = personDao.populatePersonValueDto(idPerson);

		String fullName = personValueDto.getFullName();
		if (StringUtil.isValid(personValueDto.getNameSuffixCode())) {
			fullName += " " + personValueDto.getNameSuffixCode();
		}
		personValueDto.setFullName(fullName);
		personValueDto.setPersonId(idPerson);
		/*
		 * If the stage id is not 0 then get get the person relation from the
		 * STAGE_PERSON_LINK table.
		 */
		if (!ObjectUtils.isEmpty(idStage) && idStage != 0) {
			StagePersonValueDto stagePerson = stageDao.selectStagePersonLink(idPerson, idStage);
			personValueDto.setCdStagePersRelInt(stagePerson.getCdStagePersRelLong());
		}
		StringBuilder personRace = new StringBuilder(ServiceConstants.EMPTY_STRING);
		// Calling the dao implementation to query the PERSON_RACE table to get
		// the Race
		// for a person.
		List<PersonRaceDto> personRaceList = personRaceDao.getPersonRaceByPersonId(idPerson);
		if (!CollectionUtils.isEmpty(personRaceList)) {
			personRaceList.forEach(personRaceValue -> {
				personRace.append(personRaceValue.getCdPersonRace());
				personRace.append(ServiceConstants.COMMA_SPACE);

			});
			/*
			 * If the person has more than 1 race then the race values are
			 * appended to each other by the comma and space. Removing comma and
			 * space from the string after the final value.
			 */
			personRace.delete(personRace.length() - 2, personRace.length());
		}

		personValueDto.setRaceCode(personRace.toString());
		StringBuilder personEthnicity = new StringBuilder(ServiceConstants.EMPTY_STRING);
		// Calling the dao implementation to query the PERSON_ETHNICITY table to
		// get the
		// Ethnicity for a person.
		List<PersonEthnicityDto> personEthnicList = personEthnicityDao.getPersonEthnicityByPersonId(idPerson);
		if (!CollectionUtils.isEmpty(personEthnicList)) {
			personEthnicList.forEach(personEthnicityValue -> {

				personEthnicity.append(personEthnicityValue.getCdPersonEthnicity());
				personEthnicity.append(ServiceConstants.COMMA_SPACE);

			});
			/*
			 * If the person has more than 1 ethnicity then the ethnicity values
			 * are appended to each other by the comma and space. Removing comma
			 * and space from the string after the final value.
			 */
			personEthnicity.delete(personEthnicity.length() - 2, personEthnicity.length());
		}
		personValueDto.setEthnicGroupCode(personEthnicity.toString());

		// returning the dto with the person details.
		return personValueDto;
	}

	/**
	 * Method Name: addPersonToList Method Description: This method first checks
	 * if the given Person is in the List. If not add that person to the List.
	 *
	 * @param personList
	 *            - The list contains the list of person .
	 * @param person
	 *            - The dto contains the person details.
	 */
	private void addPersonToList(List<PersonValueDto> personList, PersonValueDto person) {
		/*
		 * Check if the person list is not empty and if the person is not
		 * already present in the list and then add the person.
		 */
		/*
		 * In Case the stage id and case id is 0 when navigating from the Person
		 * Detail or Staff detail page , then adding only the context person and
		 * the associated person to the list.
		 */
		if ((!CollectionUtils.isEmpty(personList) && !ObjectUtils.isEmpty(person.getPersonId())
				&& !personList.contains(person))
				|| (CollectionUtils.isEmpty(personList) && !ObjectUtils.isEmpty(person.getPersonId())
						&& !personList.contains(person))) {
			personList.add(person);
		}

	}

	/**
	 *
	 * Method Name: updateClosedPersonWithFwdPerson Method Description:This
	 * function updates any closed person details with forward person details.
	 *
	 * Usually Person Merge updates all the person Ids in Stage Person Link
	 * table. But it the stage is closed at the time of Person Merge, then
	 * Person Merge will not update stage person Link. And the stage could be
	 * opened later where we need to suggest relations.
	 *
	 * @param personList
	 * @return Long
	 */
	private Long updateClosedPersonWithFwdPerson(List<PersonValueDto> personList) {
		for (PersonValueDto personValueDto : personList) {
			if (CodesConstant.CPERSTAT_M.equals(personValueDto.getActiveInactiveMergedStatusCode())) {
				Long idFwdPerson = ServiceConstants.ZERO_VAL;
				List<Long> idFwdPersonList = personDao.getForwardPersonInMerge(personValueDto.getPersonId());
				if (CollectionUtils.isNotEmpty(idFwdPersonList))
					idFwdPerson = idFwdPersonList.get(0);
				if (idFwdPerson != ServiceConstants.ZERO_VAL) {
					Person person1 = personDao.getPersonByPersonId(idFwdPerson);
					PersonValueDto fwdPerson = new PersonValueDto();
					fwdPerson.setSex(person1.getCdPersonSex());
					fwdPerson.setDateOfBirth(person1.getDtPersonBirth());
					fwdPerson.setDateOfDeath(person1.getDtPersonDeath());
					fwdPerson.setActiveInactiveMergedStatusCode(person1.getCdPersonStatus());
					fwdPerson.setEthnicGroupCode(person1.getCdPersonEthnicGroup());
					fwdPerson.setFirstName(person1.getNmPersonFirst());
					fwdPerson.setMiddleName(person1.getNmPersonMiddle());
					fwdPerson.setLastName(person1.getNmPersonLast());
					if (!TypeConvUtil.isNullOrEmpty(fwdPerson)) {
						personValueDto.setPersonId(idFwdPerson);
						personValueDto.setSex(fwdPerson.getSex());
						personValueDto.setDateOfDeath(fwdPerson.getDateOfDeath());
						personValueDto.setDateOfBirth(fwdPerson.getDateOfBirth());
						personValueDto.setActiveInactiveMergedStatusCode(fwdPerson.getActiveInactiveMergedStatusCode());
						personValueDto.setEthnicGroupCode(fwdPerson.getEthnicGroupCode());
						personValueDto.setAge(
								FTFamilyTreeUtil.getAge(fwdPerson.getDateOfBirth(), fwdPerson.getDateOfDeath()));
						personValueDto.setFirstName(fwdPerson.getFirstName());
						personValueDto.setMiddleName(fwdPerson.getMiddleName());
						personValueDto.setLastName(fwdPerson.getLastName());
					}
				}
			}
		}
		return (long) personList.size();
	}

	/**
	 *
	 * Method Name: saveSuggestedRelations Method Description:This method
	 * inserts suggested relationship details into the database.
	 *
	 * @param fTRelationshipDBDto
	 * @return String @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String saveSuggestedRelations(FamilyTreeRelationshipDto fTRelationshipDBDto) {
		List<FTPersonRelationDto> suggestedRelations = fTRelationshipDBDto.getSuggestedRelations();
		if (!TypeConvUtil.isNullOrEmpty(suggestedRelations) && suggestedRelations.size() != ServiceConstants.ZERO_VAL) {
			for (FTPersonRelationDto ftPersonRelationDto : suggestedRelations) {
				if (ServiceConstants.CRELSGRS_AD.equals(ftPersonRelationDto.getCdResolution())) {
					ftRelationshipDao.insertRelationship(ftPersonRelationDto);
				}
			}
		}

		return fTRelationshipDBDto.getNmPrimaryWorker();
	}

	/**
	 * Method Name: getExistingRelationships Method Description:This method is
	 * used to call the dao implementation to fetch all the relationships
	 * details between two persons which are not ended and invalidated.
	 *
	 * @param ftPersonRelationBean
	 *            - The dto contains the input values such as idPerson,
	 *            idRelatedPerson based on which the relations is retrieved.
	 * @return List<FTPersonRelationBean> - The list of existing relations.
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public List<FTPersonRelationBean> getExistingRelationships(FTPersonRelationBean ftPersonRelationBean) {

		return ftRelationshipDao.fetchAllDirectRelationships(ftPersonRelationBean);

	}

	/**
	 * Method Name: saverelationship Method Description:This method is used to
	 * call the dao implementation to either create a new relationship or update
	 * the existing relation.
	 *
	 * @param familyTreeRelationshipDto
	 *            - The dto containing the relationship details.
	 * @return Long - The unique id for the created/updated relationship.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long saverelationship(FamilyTreeRelationshipDto familyTreeRelationshipDto) {
		/*
		 * If the idPersonRelation is empty ,then its a new record and so we
		 * insert a new record else we update the existing record.
		 */
		if (ObjectUtils.isEmpty(familyTreeRelationshipDto.getFtPersonRelationBean().getIdPersonRelation())) {
			/*
			 * If it is a new relation and if the user has invalidated existing
			 * relations then updated the existing relations and then create a
			 * new relation
			 */
			if (CollectionUtils.isNotEmpty(familyTreeRelationshipDto.getDuplicatePersonRelnList())) {
				familyTreeRelationshipDto.getDuplicatePersonRelnList().forEach(relation -> {
					/*
					 * If the user has selected Y in the front end for the
					 * invalid , then setting the current date to invalid date.
					 */
					if (ServiceConstants.Y.equals(relation.getIndConflictInv())) {
						relation.setDtInvalid(new Date());
					}
					/*
					 * If the user has selected Y in the front end for the ended
					 * , then setting the current date to date ended.
					 */
					if (ServiceConstants.Y.equals(relation.getIndConflictEnd())) {
						relation.setDtEnded(new Date());
					}
					populateSource(relation);
					relation.setIdLastUpdatePerson(
							familyTreeRelationshipDto.getFtPersonRelationBean().getIdLastUpdatePerson());
					// Calling the dao implementation to invalidate the
					// conflicting relations.
					ftRelationshipDao.invalidateRelation(relation);
				});
			}
			populateSource(familyTreeRelationshipDto.getFtPersonRelationBean());
			// Calling the dao implementation to create a new relation.
			return ftRelationshipDao.insertRelationship(familyTreeRelationshipDto.getFtPersonRelationBean());
		} else {
			// Calling the dao implementation to update a relation.
			return ftRelationshipDao.updateRelationship(familyTreeRelationshipDto.getFtPersonRelationBean());
		}

	}

	/**
	 * Method Name: isRelIntMatches Method Description:This method is used to
	 * call the dao implementation to get the REL_INT_DEF based on the input
	 * relInt.The method then checks if the record retrieved from the db matches
	 * with the input passed from the front-end with respect to cdType,
	 * cdRelation,cdLineage, and cdSeparation ignoring if the column values from
	 * the db record are null.
	 *
	 * @param ftPersonRelationBean
	 *            - The dto contains the values which will used to fetch the
	 *            person relation relInt.
	 * @return boolean - The boolean value to indicate if the relInt matches
	 *         with the one entered on the screen.
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public boolean isRelIntMatches(FTPersonRelationBean ftPersonRelationBean) {
		boolean relIntMatches = true;
		/*
		 * Calling the dao implementation to fetch the records from the
		 * PERSON_RELATION_REL_INT_DEF table based on cdRelInt.
		 */
		List<PersonRelIntDto> personRelIntDtos = ftRelationshipDao.isRelIntMatches(ftPersonRelationBean);
		if (!CollectionUtils.isEmpty(personRelIntDtos)) {
			/*
			 * Iterating over the list retrieved from the db and check if the
			 * cdType,cdLineage,cdSeparation and cdRelation matches.
			 */
			for (PersonRelIntDto personRelIntDto : personRelIntDtos) {
				if (!ftPersonRelationBean.getCdRelation().equals(personRelIntDto.getCdRelation())) {
					relIntMatches = false;
					break;
				}
				if (!ObjectUtils.isEmpty(personRelIntDto.getCdType())) {
					// user should have choosen the Type else error
					relIntMatches = false;
					if (!ftPersonRelationBean.getCdType().equalsIgnoreCase(personRelIntDto.getCdType())) {
						relIntMatches = false;
						break;
					}

				}
				// If lineage does not match error.
				if (!ObjectUtils.isEmpty(personRelIntDto.getCdLineage())) {
					relIntMatches = false;
					if (!ftPersonRelationBean.getCdLineage().equalsIgnoreCase(personRelIntDto.getCdLineage())) {
						relIntMatches = false;
						break;
					}
				}
				// If cdSeparation does not match error.
				if (!ObjectUtils.isEmpty(personRelIntDto.getCdSeparation())) {
					relIntMatches = false;
					if (!ftPersonRelationBean.getCdSeparation().equalsIgnoreCase(personRelIntDto.getCdSeparation())) {
						relIntMatches = false;
						break;
					}
				}
			}
		}
		return relIntMatches;
	}

	/**
	 * Method Name: getPossibleRelation Method Description:This method is used
	 * to call the dao implementation to fetch the possible relation based on
	 * the relation between 2 person.
	 *
	 * @param familyTreeRelationsReq
	 *            - The dto with the input values.
	 * @return PersonRelSuggestionDto - The dto with the values if matched from
	 *         the db.
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PersonRelSuggestionDto getPossibleRelation(FamilyTreeRelationsReq familyTreeRelationsReq) {
		return ftRelationshipDao.getPossibleRelation(familyTreeRelationsReq);
	}

	/**
	 * Method Name: populateSource Method Description:This method is used to
	 * populate the field cdOrigin .
	 *
	 * @param ftPersonRelationBean
	 */
	private void populateSource(FTPersonRelationBean ftPersonRelationBean) {

		String source = ftPersonRelationBean.getCdOrigin();

		if (CodesConstant.CRELSGOR_UA.equals(source)) {
			ftPersonRelationBean.setCdOrigin(CodesConstant.CRELSGOR_MA);
		} else if (CodesConstant.CRELSGOR_UU.equals(source)) {
			ftPersonRelationBean.setCdOrigin(CodesConstant.CRELSGOR_MU);
		} else if (!StringHelper.isValid(source)) {

			ftPersonRelationBean.setCdOrigin(CodesConstant.CRELSGOR_US);
		}
	}

	/**
	 *
	 * Method Name: fetchRelationshipsForGraph Method Description:This method
	 * returns Relationships for Basic/Extended Graphs.
	 *
	 * @param fTRelationshipDBDto
	 * @return FTRelationshipDBDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public FamilyTreeRelationshipDto fetchRelationshipsForGraph(FamilyTreeRelationshipDto familyTreeRelationshipDto) {
		FamilyTreeRelationshipDto fTRelationshipDBDto;
		Long idContextPerson = familyTreeRelationshipDto.getContextPerson().getPersonId();
		Long idStage = familyTreeRelationshipDto.getIdStage();
		fTRelationshipDBDto = fetchRelationshipInfo(familyTreeRelationshipDto);
		List<FTPersonRelationDto> relations = fTRelationshipDBDto.getRelations();
		List<FTPersonRelationDto> allRelations;
		if (ServiceConstants.GRAPH_TYPE_BASIC.equals(familyTreeRelationshipDto.getGraphType())) {
			if (!ObjectUtils.isEmpty(idContextPerson) && idContextPerson != ServiceConstants.ALL_SELECTED) {
				List<Long> relPersonIdList = ftRelationshipSuggestionUtils.createRelatedPersonList(relations,
						idContextPerson);
				List<FTPersonRelationDto> relAmongPersons = fTRelationshipSearchDao
						.selectRelationsAmongPersons(relPersonIdList);
				allRelations = addRelationLists(relations, relAmongPersons);
			} else {
				allRelations = relations;
			}
		} else {
			List<Long> personIdList = ftRelationshipSuggestionUtils.extractAllIdPersons(relations);
			List<FTPersonRelationDto> allDirectRel = fTRelationshipSearchDao.selectAllDirectRelForPersons(personIdList);
			allDirectRel = filterRelationships(fTRelationshipDBDto, allDirectRel);
			allRelations = addRelationLists(relations, allDirectRel);
		}
		allRelations = ftRelationshipSuggestionUtils.removeInvlidRelations(allRelations);
		List<PersonValueDto> notConnectedPersonList = new ArrayList<PersonValueDto>();
		if (!ObjectUtils.isEmpty(idStage) && 0 != idStage) {
			notConnectedPersonList = fetchPeopleNotInRelation(idStage, relations,
					fTRelationshipDBDto.getContextPersonList());
		}
		fTRelationshipDBDto.setContextPersonList(notConnectedPersonList);
		fTRelationshipDBDto.setPcspPersonList(
				setChildSafetyPlacements(allRelations, notConnectedPersonList, familyTreeRelationshipDto.getIdCase()));
		fTRelationshipDBDto.setRelations(allRelations);
		populateRaceEthnicity(allRelations, fTRelationshipDBDto.getContextPersonList());
		String timeoutVal = ftRelationshipDao.selectOnlineParameterValue(ServiceConstants.FAM_TREE_GRPH_TMOUT_SEC);
		fTRelationshipDBDto.setGraphTimeout(!ObjectUtils.isEmpty(timeoutVal) ? Double.parseDouble(timeoutVal)
				: ServiceConstants.FAM_TREE_GRPH_TMOUT_SEC_DEFAULT);

		return fTRelationshipDBDto;
	}

	/**
	 *
	 * Method Name: fetchPeopleNotInRelation Method Description:This method
	 * retrieves Persons that are in the Stage Person List but not in the
	 * current Relationships view.
	 *
	 * @param idStage
	 * @param relations
	 * @param contextPersonList
	 * @return List<PersonValueDto>
	 */
	private List<PersonValueDto> fetchPeopleNotInRelation(Long idStage, List<FTPersonRelationDto> relations,
			List<PersonValueDto> contextPersonList) {
		List<PersonValueDto> notConnectedPersonList = new ArrayList<PersonValueDto>();
		List<Long> personIdList = ftRelationshipSuggestionUtils.extractAllIdPersons(relations);
		Map<String, FTPersonRelationDto> relRelintDefMap = ftRelationshipDao.selectRelintRelMappingData();
		relRelintDefMap.put(ServiceConstants.CRPTRINT_SL, null);
		relRelintDefMap.put(ServiceConstants.CRPTRINT_OV, null);
		for (PersonValueDto personValueDto : contextPersonList) {
			Long idPerson = (personValueDto.getPersonId());
			String relInt = personValueDto.getCdStagePersRelInt();
			if (!personIdList.contains(idPerson) && relRelintDefMap.containsKey(relInt)) {
				notConnectedPersonList.add(personValueDto);
			}
		}
		return notConnectedPersonList;

	}

	/**
	 *
	 * Method Name: setChildSafetyPlacements Method Description:This function
	 * retrieves all the Child Safety Placements for the Persons in the
	 * Relationships as well as for the persons that are not connected.
	 *
	 * @param relations
	 * @param notConnectedPersonList
	 * @param idCase
	 * @return List<PcspValueDto>
	 */
	private List<PcspDto> setChildSafetyPlacements(List<FTPersonRelationDto> relations,
			List<PersonValueDto> notConnectedPersonList, Long idCase) {
		List<PcspDto> pcspPersonList = new ArrayList<>();
		List<Long> personIdList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(relations)) {
			personIdList = ftRelationshipSuggestionUtils.extractAllIdPersons(relations);
		}
		if (CollectionUtils.isNotEmpty(notConnectedPersonList)) {
			for (PersonValueDto personValueDto : notConnectedPersonList) {
				personIdList.add(personValueDto.getPersonId());
			}
		}
		if (CollectionUtils.isNotEmpty(personIdList)) {
			pcspPersonList = ftRelationshipDao.selectPCSPAmongPerons(personIdList, idCase);
		}
		return pcspPersonList;
	}

	/**
	 *
	 * Method Name: populateRaceEthnicity Method Description:This function
	 * populates Race/Ethnicity for all the persons in the Relations.
	 *
	 * @param relations
	 * @param personList
	 */
	private void populateRaceEthnicity(List<FTPersonRelationDto> relations, List<PersonValueDto> personList) {

		List<Long> personIdList1 = ftRelationshipSuggestionUtils.extractAllIdPersons(relations);
		List<Long> personIdList2 = extractIdFromPersons(personList);
		personIdList1.addAll(personIdList2);
		if (CollectionUtils.isNotEmpty(personIdList1)) {
			Map<Long, String> personRaceMap = personEthnicityDao.selectPersonRace(personIdList1);
			Map<Long, String> personEthnMap = personEthnicityDao.selectPersonEthnicity(personIdList1);
			for (FTPersonRelationDto fTPersonRelationDto : relations) {
				Long idPerson = fTPersonRelationDto.getIdPerson();
				Long idRelatedPerson = fTPersonRelationDto.getIdRelatedPerson();
				if (personRaceMap.containsKey(idPerson)) {
					fTPersonRelationDto.setCdRaceContextPerson((String) personRaceMap.get(idPerson));
				}
				if (personRaceMap.containsKey(idRelatedPerson)) {
					fTPersonRelationDto.setCdRaceRelatedPerson((String) personRaceMap.get(idRelatedPerson));
				}
				if (personEthnMap.containsKey(idPerson)) {
					fTPersonRelationDto.setCdEthnicityContextPerson((String) personEthnMap.get(idPerson));
				}
				if (personEthnMap.containsKey(idRelatedPerson)) {
					fTPersonRelationDto.setCdEthnicityRelatedPerson((String) personEthnMap.get(idRelatedPerson));
				}
			}
			for (PersonValueDto personValueDto : personList) {
				Long idPerson = personValueDto.getPersonId();
				if (personRaceMap.containsKey(idPerson)) {
					personValueDto.setRaceCode((String) personRaceMap.get(idPerson));
				}
				if (personEthnMap.containsKey(idPerson)) {
					personValueDto.setEthnicGroupCode((String) personEthnMap.get(idPerson));
				}
			}
		}
	}

	/**
	 *
	 * Method Name: extractIdFromPersons Method Description:This method returns
	 * All Id Persons from the Person ValueBean list.
	 *
	 * @param personList
	 * @return List<Long>
	 */
	public static List<Long> extractIdFromPersons(List<PersonValueDto> personList) {
		List<Long> personIdList = new ArrayList<>();
		for (PersonValueDto personValueDto : personList) {
			Long idPerson = personValueDto.getPersonId();

			if (!personIdList.contains(idPerson)) {
				personIdList.add(idPerson);
			}
		}

		return personIdList;
	}

	/**
	 *
	 * Method Name: addRelationLists Method Description: This method returns the
	 * List of Person Ids that are related to the given idPerson
	 *
	 * @param relations
	 * @param relAmongPersons
	 * @return
	 */
	public static List<FTPersonRelationDto> addRelationLists(List<FTPersonRelationDto> relations1,
			List<FTPersonRelationDto> relAmongPersons) {
		List<FTPersonRelationDto> relations = new ArrayList<>();
		relations.addAll(relations1);
		for (FTPersonRelationDto fTPersonRelationValueDto : relAmongPersons) {
			Boolean duplicate = ServiceConstants.FALSEVAL;
			for (FTPersonRelationDto fTPersonRelationValueDto1 : relations1) {
				if (fTPersonRelationValueDto.getIdPersonRelation() == fTPersonRelationValueDto1.getIdPersonRelation()) {
					duplicate = ServiceConstants.TRUEVAL;
					break;
				}
			}
			if (!duplicate) {
				relations.add(fTPersonRelationValueDto);
			}
		}
		return relations;
	}

	/**
	 * Method Name: fetchRelationshipsForReport Method Description:This method
	 * is used to fetch the family tree relationship details for generating the
	 * report.This method calls the 'fetchRelationshipsForGraph' method of the
	 * service implementation internally to get the family tree relationship
	 * details and performs business logic before returning the response
	 * information.
	 *
	 * @param familyTreeRelationshipDto
	 *            - This dto will hold input parameter values for fetching the
	 *            family tree relationship details.
	 * @return FamilyTreeRelationshipDto - This dto will hold the family tree
	 *         relationship details.
	 */
	@Override
	public FamilyTreeRelationshipDto fetchRelationshipsForReport(FamilyTreeRelationshipDto familyTreeRelationshipDto) {
		FamilyTreeRelationshipDto treeRelationshipDto = new FamilyTreeRelationshipDto();
		BeanUtils.copyProperties(familyTreeRelationshipDto, treeRelationshipDto);
		Long idCase = familyTreeRelationshipDto.getIdCase();
		Long idStage = familyTreeRelationshipDto.getIdStage();
		Long idContextPerson = familyTreeRelationshipDto.getContextPerson().getPersonId();
		// If the report type is detailed
		if (REPORT_TYPE_DETAILED.equals(familyTreeRelationshipDto.getReportType())) {
			treeRelationshipDto = fetchRelationshipsForGraph(familyTreeRelationshipDto);
		}
		if (!ObjectUtils.isEmpty(idCase) && 0 != idCase) {
			CapsCaseDto capsCaseDto = capsCaseDao.getCapsCaseByid(idCase);
			treeRelationshipDto.setNmCase(capsCaseDto.getNmCase());
			treeRelationshipDto.setNmPrimaryWorker(NOT_APPLICABLE);
		}

		if (!ObjectUtils.isEmpty(idStage) && 0 != idStage) {
			StageValueBeanDto stageValueBeanDto = stageDao.retrieveStageInfoList(idStage);
			treeRelationshipDto.setNmStage(stageValueBeanDto.getNmStage());
			treeRelationshipDto.setNmPrimaryWorker(getCaseWorkerName(stageValueBeanDto));
		}
		// Load Context Person Details.
		if (!ObjectUtils.isEmpty(idContextPerson) && 0 != idContextPerson) {
			treeRelationshipDto.setContextPerson(getPersonDetails(idContextPerson, idStage));
		}
		return treeRelationshipDto;
	}

	/**
	 * Method Name: getCaseWorkerName Method Description: This method returns
	 * Primary Case Worker Name for the Open Stage and Historical Primary Case
	 * Worker Name for the Closed Stage.
	 *
	 * @param stageValueBeanDto
	 * @return
	 */
	private String getCaseWorkerName(StageValueBeanDto stageValueBeanDto) {
		String workerName = NOT_APPLICABLE;
		Long idStage = stageValueBeanDto.getIdStage();

		// Open stage
		if (ObjectUtils.isEmpty(stageValueBeanDto.getDtStageClose())) {
			CommonHelperReq commonHelperReq = new CommonHelperReq();
			commonHelperReq.setIdStage(idStage);
			// Primary Worker for the Stage.
			Long idPrimaryWorker = approvalDao.getPrimaryWorkerIdForStage(commonHelperReq).getUlIdPerson();
			if (!ObjectUtils.isEmpty(idPrimaryWorker) && 0 != idPrimaryWorker) {
				Person person = personDao.getPersonByPersonId(idPrimaryWorker);
				workerName = person.getNmPersonFull();
			}
		}
		// Closed stage
		else {
			List<String> personRoles = new ArrayList<>();
			personRoles.add(CodesConstant.CROLEALL_HP);
			List<StagePersonValueDto> stagePersonList = stageProgDao.selectStagePersonLink(idStage.intValue(),
					personRoles);
			if (!CollectionUtils.isEmpty(stagePersonList)) {
				Optional<StagePersonValueDto> maxHPId = stagePersonList.stream()
						.max(Comparator.comparing(StagePersonValueDto::getIdPerson));
				if (maxHPId.isPresent()) {
					Person person = personDao.getPersonByPersonId(maxHPId.get().getIdPerson());
					workerName = person.getNmPersonFull();
				}
			}
		}
		return workerName;
	}

	/**
	 * Method Name: fetchAllDirectPersonRelationShip Method Description:This
	 * method is used to call the dao implementation to fetch all the
	 * relationships details between two persons which are not ended and
	 * invalidated.
	 *
	 * @param ftPersonRelationBean
	 *            - The dto contains the input values such as idPerson,
	 *            idRelatedPerson based on which the relations is retrieved.
	 * @return List<FTPersonRelationBean> - The list of existing relations.
	 */
	@Override
	public List<FTPersonRelationBean> fetchAllDirectPersonRelationShip(FTPersonRelationBean ftPersonRelationBean) {
		return ftRelationshipDao.fetchAllDirectPersonRelationShip(ftPersonRelationBean);
	}

	/**
	 * Method Name: isStaff Method Description: This methods checks whether the
	 * given person is a staff
	 *
	 * @param idPerson
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isStaff(Long personId) {
		LOG.debug("Entering method isStaff in FTRelationshipService");
		Boolean isStaff = false;
		isStaff = ftRelationshipDao.isStaff(personId);
		LOG.debug("Exiting method isStaff in FTRelationshipService");
		return isStaff;

	}

}
