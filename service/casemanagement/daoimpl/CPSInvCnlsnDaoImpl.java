package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.common.dto.ClosureNoticeListDto;
import us.tx.state.dfps.common.dto.CpsInvNoticesQueryResultDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.common.dto.VictimListDto;
import us.tx.state.dfps.service.admin.dto.CpsInvCnclsnValidationDto;
import us.tx.state.dfps.service.casemanagement.dao.CPSInvCnlsnDao;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CpsInvNoticesClosureReq;
import us.tx.state.dfps.service.common.response.CpsInvNoticesRes;
import us.tx.state.dfps.service.common.response.CpsInvSubstancesRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.fcl.dao.SexualVictimizationHistoryDao;
import us.tx.state.dfps.service.investigation.dto.SubstanceTrackerDto;
import us.tx.state.dfps.service.person.dao.PersonDao;

import us.tx.state.dfps.service.person.dao.PersonDetailDao;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.service.servicedlvryclosure.dto.ServiceDlvryClosureDto;

@Repository
public class CPSInvCnlsnDaoImpl implements CPSInvCnlsnDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CaseSummaryDao caseSummaryDao;

	@Autowired
	private PersonDetailDao personDetailDao;

	@Autowired
	private PersonDao personDao;
	
	@Autowired
	private SexualVictimizationHistoryDao sexualVictimizationHistoryDao;

	@Value("${CPSInvCnlsnDaoImpl.getClosureNoticesSql}")
	private String getClosureNoticesSql;

	@Value("${CPSInvCnlsnDaoImpl.deleteVictimsSql}")
	private String deleteVictimsSql;

	@Value("${CPSInvCnlsnDaoImpl.schoolPersonnelSql}")
	private String schoolPersonnelSql;

	@Value("${CPSInvCnlsnDaoImpl.updateChildSexLaborTrafficSql}")
	private String updateChildSexLaborTraffic;

	@Value("${CPSInvCnlsnDaoImpl.completedAssessmentCountSql}")
	private String completedAssessmentCount;

	@Value("${CPSInvCnlsnDaoImpl.fetchAllegQuestionAnswers}")
	private String fetchAllegQuestionAnswersSql;

	@Value("${CPSInvCnlsnDaoImpl.getPrintPersonByEvent}")
	private String getPrintPersonByEvent;

	@Value("${CPSInvCnlsnDaoImpl.getPrintPersonByApprover}")
	private String getPrintPersonByApprover;

	@Value("${CPSInvCnlsnDaoImpl.fetchAllegQuestionYAnswers}")
	private String fetchAllegQuestionYAnswersSql;

	@Value("${CPSInvCnlsnDaoImpl.validateRlqshCstdyAndPrsnCharQuestion}")
	private String validateRlqshCstdyAndPrsnCharQuestionSql;

	@Value("${CPSInvCnlsnDaoImpl.getRlnqshAnswrdYVictimIds}")
	private String getRlnqshAnswrdYVictimIdsSql;

	@Value("${CPSInvCnlsnDaoImpl.isEventStatusNew}")
	private String isEventStatusNewSql;

	@Value("${CPSInvCnlsnDaoImpl.isChildSexLaborTrafficking}")
	private String isChildSexLaborTraffickingSql;

	@Value("${CPSInvCnlsnDaoImpl.isChildSexLaborTraffickingFalse}")
	private String isChildSexLaborTraffickingFalseSql;

	@Value("${CPSInvCnlsnDaoImpl.rlnqushQuestionAnsweredY}")
	private String rlnqushQuestionAnsweredYSql;

	@Value("${CPSInvCnlsnDaoImpl.isDispositionMissing}")
	private String isDispositionMissingSql;

	@Value("${CPSInvCnlsnDaoImpl.isRlnqshAnswrd}")
	private String isRlnqshAnswrdSql;

	@Value("${CPSInvCnlsnDaoImpl.getPrsnByCaseAndStage}")
	private String getPrsnByCaseAndStageSql;

	@Value("${CPSInvCnlsnDaoImpl.getInitialSDMSafetyAssmnt}")
	private String getInitialSDMSafetyAssmntSql;

	@Value("${CPSInvCnlsnDaoImpl.getPriorARInitialSafetyAssmntCount}")
	private String getPriorARInitialSafetyAssmntCountSql;
	
	@Value("${CPSInvCnlsnDaoImpl.getPriorARInitialSafetyAssmntCountNew}")
	private String getPriorARInitialSafetyAssmntCountNewSql;

	@Value("${CPSInvCnlsnDaoImpl.getInCompSDMSafetyAssmntCount}")
	private String getInCompSDMSafetyAssmntCountSql;

	@Value("${CPSInvCnlsnDaoImpl.getRemovalRecordCount}")
	private String getRemovalRecordCountSql;

	@Value("${CPSInvCnlsnDaoImpl.getRemovalRecordCountForHousehold}")
	private String getRemovalRecordCountForHouseholdSql;

	@Value("${CPSInvCnlsnDaoImpl.getRemovalRecordCountFind}")
	private String getRemovalRecordCountFindSql;

	@Value("${CPSInvCnlsnDaoImpl.getPcspCount}")
	private String getPcspCountSql;

	@Value("${CPSInvCnlsnDaoImpl.getPcspCountFind}")
	private String getPcspCountFindSql;

	@Value("${CPSInvCnlsnDaoImpl.getPcspCountForCaseFind}")
	private String getPcspCountFindForCaseSql;

	@Value("${CPSInvCnlsnDaoImpl.getSafetyDecisionAndIdCps}")
	private String getSafetyDecisionAndIdCpsSql;

	@Value("${CPSInvCnlsnDaoImpl.getSafetyDecisionAndIdCpsForHousehold}")
	private String getSafetyDecisionAndIdCpsForHouseholdSql;

	@Value("${CPSInvCnlsnDaoImpl.pcspSelectedCount}")
	private String pcspSelectedCountSql;

	@Value("${CPSInvCnlsnDaoImpl.cspInterventionCount}")
	private String cspInterventionCountSql;

	@Value("${CPSInvCnlsnDaoImpl.sdmRiskAssmComp}")
	private String sdmRiskAssmCompSql;

	@Value("${CPSInvCnlsnDaoImpl.sdmRiskAssmCompForHousehold}")
	private String sdmRiskAssmCompForHouseholdSql;

	@Value("${CPSInvCnlsnDaoImpl.getPersonIdInFDTC}")
	private String getPersonIdInFDTCSql;

	@Value("${CPSInvCnlsnDaoImpl.getMostRecentFDTCSubtype}")
	private String getMostRecentFDTCSubtypeSql;

	@Value("${CPSInvCnlsnDaoImpl.getVictimDataForAllegation}")
	private String getVictimDataForAllegationSql;

	@Value("${CPSInvCnlsnDaoImpl.getDtIntakeForIdStage}")
	private String getDtIntakeForIdStageSql;

	@Value("${CPSInvCnlsnDaoImpl.getSevenDaySafetyAssmntCount}")
	private String getSevenDaySafetyAssmntCountSql;

	@Value("${CPSInvCnlsnDaoImpl.getIdVictimsForAllInINVHql}")
	private String getIdVictimsForAllInINV;

	@Value("${CPSInvCnlsnDaoImpl.getTrfckngExistsForVictimHql}")
	private String getTrfckngExistsForVictim;
	
	@Value("${CPSInvCnlsnDaoImpl.getRTBSXAllegForVictim}")
	private String getRTBSXAllegForVictim;

	@Value("${CpsInvestigationReportDaolmpl.getAllegedPerpetrators}")
	private transient String getAllegedPerpetrators;

	@Value("${CPSInvCnlsnDaoImpl.getSchoolInvNotifctnVctmByPersonId}")
	private String getSchoolInvNotifctnVctmByPersonIdSql;

	private static final Logger logger = Logger.getLogger(CPSInvCnlsnDaoImpl.class.getName());

	private static final String[] PARENTAL_REL_INT = ServiceConstants.PARENTAL_REL_INT_ARRAY;
	private static final List<String> ALGTN_SXTR_LBTR = Arrays.asList(CodesConstant.CCLICALT_SXTR,
			CodesConstant.CCLICALT_LBTR);
	private static final List<String> DISP_RTB_UTD = Arrays.asList(CodesConstant.CDISPSTN_RTB,
			CodesConstant.CDISPSTN_UTD);

	public CPSInvCnlsnDaoImpl() {

	}

	/**
	 *
	 * Method Description: This method is to get the existing records in substance_tracker
	 * for the given person ID.
	 *
	 * @param idStage
	 * @param idPerson
	 * @param indParentChild
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SubstanceTracker> getExsitingSubstanceTrackersbyStageIDPersonID(Long idPerson, Long idStage, String indParentChild) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SubstanceTracker.class);
		criteria.add(Restrictions.eq("idStage", idStage));
		criteria.add(Restrictions.eq("idPerson", idPerson));
		criteria.add(Restrictions.eq("indParentChild", indParentChild));
		List<SubstanceTracker> substanceTrackerList = criteria.list();

		return substanceTrackerList;
	}

	@Override
	public CpsInvSubstancesRes updateSubstanceTracker(List<SubstanceTracker> substanceTrackerList, String operation){
		CpsInvSubstancesRes cpsInvSubstancesRes = new CpsInvSubstancesRes();

		switch (operation) {
			case ServiceConstants.FUNCTION_INSERT:
				for (SubstanceTracker substanceTracker : substanceTrackerList) {
					sessionFactory.getCurrentSession().save(substanceTracker);
				}
				break;
			case ServiceConstants.FUNCTION_DELETE:
				for (SubstanceTracker substanceTracker : substanceTrackerList) {
					sessionFactory.getCurrentSession().delete(substanceTracker);
				}
				break;
			case ServiceConstants.FUNCTION_UPDATE:
				for (SubstanceTracker substanceTracker : substanceTrackerList) {
					sessionFactory.getCurrentSession().update(substanceTracker);
				}
				break;
			default:
				break;
		}

		return cpsInvSubstancesRes;
	}


	/**
	 * 
	 * Method Description:This Method is used to retireve the information of all
	 * the closure notices for a given EventId.
	 * 
	 * @param CommonHelperReq(eventId)
	 * @return cpsInvNoticesRes @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CpsInvNoticesRes getClosureNotices(Long eventId, Long stageId, String stageProgType) {
		CpsInvNoticesRes cpsInvNoticesRes = new CpsInvNoticesRes();
		ClosureNoticeListDto closureNoticeListDto = new ClosureNoticeListDto();
		List<ClosureNoticeListDto> closureNoticeList = new ArrayList<ClosureNoticeListDto>();
		List<ClosureNoticeListDto> parentCaregiverNoticeDto = new ArrayList<ClosureNoticeListDto>();
		List<CpsInvNoticesQueryResultDto> resultList = new ArrayList<CpsInvNoticesQueryResultDto>();
		List<Long> idPersonList;
		resultList = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getClosureNoticesSql)
				.setParameter("idEvent", eventId).setParameter("idStage", stageId))
						.addScalar("idNoticePerson", StandardBasicTypes.LONG)
						.addScalar("nmPersonFull", StandardBasicTypes.STRING)
						.addScalar("cdStagePersType", StandardBasicTypes.STRING)
						.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
						.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
						.addScalar("cdNoticeSelected", StandardBasicTypes.STRING)
						.addScalar("victimFullName", StandardBasicTypes.STRING)
						.addScalar("idVictim", StandardBasicTypes.LONG)
						.addScalar("idSchoolInvNoticeVictim", StandardBasicTypes.LONG)
						.addScalar("idConclusionNoticeInfo", StandardBasicTypes.LONG)
						.addScalar("indInvolvedParent", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(CpsInvNoticesQueryResultDto.class)).list();
		if (null != resultList) {
			for (CpsInvNoticesQueryResultDto dd : resultList) {
				// if role is present , then victim list id present.
				ClosureNoticeListDto parentCaregiver = new ClosureNoticeListDto();
				VictimListDto victimList = new VictimListDto();
				List<VictimListDto> victimListDto = new ArrayList<VictimListDto>();
				if (!TypeConvUtil.isNullOrEmpty(dd.getNmPersonFull())) {
					parentCaregiver.setNmPersonFull(dd.getNmPersonFull());
				}
				if (!TypeConvUtil.isNullOrEmpty(dd.getCdNoticeSelected())) {
					parentCaregiver.setCdNoticeSelected(dd.getCdNoticeSelected());
				}
				if (!TypeConvUtil.isNullOrEmpty(dd.getIdNoticePerson())) {
					parentCaregiver.setIdPerson(dd.getIdNoticePerson());
				}
				if (!TypeConvUtil.isNullOrEmpty(dd.getCdStagePersRelInt())) {
					parentCaregiver.setCdStagePersRelInt(dd.getCdStagePersRelInt());
				}
				if (!TypeConvUtil.isNullOrEmpty(dd.getCdStagePersRole())) {
					parentCaregiver.setCdStagePersRole(dd.getCdStagePersRole());
				}
				if (!TypeConvUtil.isNullOrEmpty(dd.getCdStagePersType())) {
					parentCaregiver.setCdStagePersType(dd.getCdStagePersType());
				}
				if (!TypeConvUtil.isNullOrEmpty(dd.getIdConclusionNoticeInfo())) {
					parentCaregiver.setIdConclusionNoticeInfo(dd.getIdConclusionNoticeInfo());
				}
				parentCaregiver.setIndInvolvedParent(dd.getIndInvolvedParent());
				// check for school investigation
				// PPM 73576, Victim information can be stored by closure letters as well
				if (!TypeConvUtil.isNullOrEmpty(dd.getVictimFullName())
						&& ServiceConstants.CPGRMS_CPS.equalsIgnoreCase(stageProgType)
						&& !TypeConvUtil.isNullOrEmpty(dd.getIdSchoolInvNoticeVictim())
						&& !TypeConvUtil.isNullOrEmpty(dd.getIdVictim())) {
					victimList.setNmVictimFull(dd.getVictimFullName());
					victimList.setIdSchoolInvNoticeVictim(dd.getIdSchoolInvNoticeVictim());
					//In Person merge get the forward person id, in case if we have a closed person id
					Long fwdPersonId = personDetailDao.getForwardPersonInMerge(dd.getIdVictim());
					if (fwdPersonId == 0L)
						fwdPersonId = dd.getIdVictim();
					victimList.setIdVictim(fwdPersonId);
					victimListDto.add(victimList);
				}
				parentCaregiver.setVictimListDto(victimListDto);
				parentCaregiverNoticeDto.add(parentCaregiver);
			}
		}
		idPersonList = parentCaregiverNoticeDto.stream().map(o -> o.getIdPerson()).collect(Collectors.toSet()).stream()
				.collect(Collectors.toList());
		if (ServiceConstants.CSTAGES_AR.equalsIgnoreCase(stageProgType)) {
			for (ClosureNoticeListDto items : parentCaregiverNoticeDto) {
				closureNoticeListDto = setClosureNoticeDto(items);
				closureNoticeList.add(closureNoticeListDto);
			}
			cpsInvNoticesRes.setClosureNoticeListDto(closureNoticeList);
		}
		cpsInvNoticesRes = setResponseValues(idPersonList, parentCaregiverNoticeDto, eventId, stageProgType, stageId);

		return cpsInvNoticesRes;
	}

	/**
	 * 
	 * Method Description:Sets the CpsInvNoticesRes values using values fetched
	 * from Query or database
	 * 
	 * @param idPersonList
	 * @param parentCaregiverNoticeDto
	 * @param eventId
	 * @param stageProgType
	 * @param stageId
	 * @return
	 */
	private CpsInvNoticesRes setResponseValues(List<Long> idPersonList,
			List<ClosureNoticeListDto> parentCaregiverNoticeDto, Long eventId, String stageProgType, Long stageId) {
		CpsInvNoticesRes cpsInvNoticesRes = new CpsInvNoticesRes();
		List<ClosureNoticeListDto> parentCaregiverList = new ArrayList<ClosureNoticeListDto>();
		List<ClosureNoticeListDto> allegedPerpList = new ArrayList<ClosureNoticeListDto>();
		HashMap<Long, List<VictimListDto>> map = new HashMap<Long, List<VictimListDto>>();
		HashMap<Long, ClosureNoticeListDto> map2 = new HashMap<Long, ClosureNoticeListDto>();
		List<ClosureNoticeListDto> closureNoticeList = new ArrayList<ClosureNoticeListDto>();
		Boolean schoolInvestigation = Boolean.FALSE;
		schoolInvestigation = getSchoolPersonnelInitialRole(eventId, stageId);
		for (ClosureNoticeListDto folks : parentCaregiverNoticeDto) {
			List<VictimListDto> victimsList = parentCaregiverNoticeDto.stream()
					.filter(o -> o.getIdPerson().equals(folks.getIdPerson())).collect(Collectors.toList()).stream()
					.flatMap(p -> p.getVictimListDto().stream()).collect(Collectors.toList());
			if (victimsList != null) {
				for (VictimListDto victimListDto : victimsList) {
					victimListDto.setIndVictimChecked(ServiceConstants.STRING_IND_Y);
				}
			}
			map.put(folks.getIdConclusionNoticeInfo(), victimsList);
			map2.put(folks.getIdConclusionNoticeInfo(), folks);
		}
		for (HashMap.Entry<Long, ClosureNoticeListDto> folks : map2.entrySet()) {
			ClosureNoticeListDto parents = new ClosureNoticeListDto();
			if (schoolInvestigation && ArrayUtils.contains(PARENTAL_REL_INT, folks.getValue().getCdStagePersRelInt())) {
				if (null != folks.getValue().getCdNoticeSelected()) {
					parents.setCdNoticeSelected(folks.getValue().getCdNoticeSelected());
				}
				if (!TypeConvUtil.isNullOrEmpty(folks.getValue().getIdPerson())) {
					parents.setIdPerson(folks.getValue().getIdPerson());
				}
				if (map.containsKey(folks.getKey())) {
					parents.setVictimListDto(map.get(folks.getKey()));
				}
				if (!TypeConvUtil.isNullOrEmpty(folks.getValue().getCdStagePersRelInt())) {
					parents.setCdStagePersRelInt(folks.getValue().getCdStagePersRelInt());
				}
				if (!TypeConvUtil.isNullOrEmpty(folks.getValue().getNmPersonFull())) {
					parents.setNmPersonFull(folks.getValue().getNmPersonFull());
				}
				if (!TypeConvUtil.isNullOrEmpty(folks.getValue().getCdStagePersRole())) {
					parents.setCdStagePersRole(folks.getValue().getCdStagePersRole());
				}
				if (!TypeConvUtil.isNullOrEmpty(folks.getValue().getCdStagePersType())) {
					parents.setCdStagePersType(folks.getValue().getCdStagePersType());
				}
				if (!TypeConvUtil.isNullOrEmpty(folks.getValue().getIdPerson())) {
					parents.setIdPerson(folks.getValue().getIdPerson());
				}
				if (!TypeConvUtil.isNullOrEmpty(folks.getValue().getIdConclusionNoticeInfo())) {
					parents.setIdConclusionNoticeInfo(folks.getValue().getIdConclusionNoticeInfo());
				}
				parentCaregiverList.add(parents);
			}
			if (schoolInvestigation && !ArrayUtils.contains(PARENTAL_REL_INT, folks.getValue().getCdStagePersRelInt())
					&& !ServiceConstants.PERSON_OLDEST_VICTIM.equalsIgnoreCase(folks.getValue().getCdStagePersRelInt())
					&& !ServiceConstants.PERSON_VICTIM.equalsIgnoreCase(folks.getValue().getCdStagePersRelInt())) {
				ClosureNoticeListDto allegedPerp = new ClosureNoticeListDto();
				if (!TypeConvUtil.isNullOrEmpty(folks.getValue().getCdNoticeSelected())) {
					allegedPerp.setCdNoticeSelected(folks.getValue().getCdNoticeSelected());
				}
				if (!TypeConvUtil.isNullOrEmpty(folks.getValue().getCdStagePersRelInt())) {
					allegedPerp.setCdStagePersRelInt(folks.getValue().getCdStagePersRelInt());
				}
				if (!TypeConvUtil.isNullOrEmpty(folks.getValue().getNmPersonFull())) {
					allegedPerp.setNmPersonFull(folks.getValue().getNmPersonFull());
				}
				if (!TypeConvUtil.isNullOrEmpty(folks.getValue().getCdStagePersRole())) {
					allegedPerp.setCdStagePersRole(folks.getValue().getCdStagePersRole());
				}
				if (!TypeConvUtil.isNullOrEmpty(folks.getValue().getCdStagePersType())) {
					allegedPerp.setCdStagePersType(folks.getValue().getCdStagePersType());
				}
				if (!TypeConvUtil.isNullOrEmpty(folks.getValue().getIdPerson())) {
					allegedPerp.setIdPerson(folks.getValue().getIdPerson());
				}
				if (!TypeConvUtil.isNullOrEmpty(folks.getValue().getIdConclusionNoticeInfo())) {
					allegedPerp.setIdConclusionNoticeInfo(folks.getValue().getIdConclusionNoticeInfo());
				}
				allegedPerpList.add(allegedPerp);
			}
			if (!schoolInvestigation) {
				ClosureNoticeListDto closureNoticeListDto = new ClosureNoticeListDto();
				closureNoticeListDto = setClosureNoticeDto(folks.getValue());
				if (map.containsKey(folks.getKey())) {
					closureNoticeListDto.setVictimListDto(map.get(folks.getKey()));
				}
				closureNoticeList.add(closureNoticeListDto);
			}
		}
		cpsInvNoticesRes.setClosureNoticeListDto(closureNoticeList);
		cpsInvNoticesRes.setParentCaregiverNoticeDto(parentCaregiverList);
		cpsInvNoticesRes.setAllegedPerpReportNoticeDto(allegedPerpList);
		return cpsInvNoticesRes;
	}

	/**
	 * 
	 * Method Description: Sets the ClosureNoticeListDto using values fetched
	 * from Query or database
	 * 
	 * @param parentCaregiver
	 * @return ClosureNoticeListDto
	 */
	private ClosureNoticeListDto setClosureNoticeDto(ClosureNoticeListDto parentCaregiver) {
		ClosureNoticeListDto closureNoticeDto = new ClosureNoticeListDto();
		if (!TypeConvUtil.isNullOrEmpty(parentCaregiver.getNmPersonFull())) {
			closureNoticeDto.setNmPersonFull(parentCaregiver.getNmPersonFull());
		}
		if (!TypeConvUtil.isNullOrEmpty(parentCaregiver.getCdStagePersRelInt())) {
			closureNoticeDto.setCdStagePersRelInt(parentCaregiver.getCdStagePersRelInt());
		}
		if (!TypeConvUtil.isNullOrEmpty(parentCaregiver.getCdStagePersRole())) {
			closureNoticeDto.setCdStagePersRole(parentCaregiver.getCdStagePersRole());
		}
		if (!TypeConvUtil.isNullOrEmpty(parentCaregiver.getCdStagePersType())) {
			closureNoticeDto.setCdStagePersType(parentCaregiver.getCdStagePersType());
		}
		if (!TypeConvUtil.isNullOrEmpty(parentCaregiver.getCdNoticeSelected())) {
			closureNoticeDto.setCdNoticeSelected(parentCaregiver.getCdNoticeSelected());
		}
		if (!TypeConvUtil.isNullOrEmpty(parentCaregiver.getIdConclusionNoticeInfo())) {
			closureNoticeDto.setIdConclusionNoticeInfo(parentCaregiver.getIdConclusionNoticeInfo());
		}
		if (!TypeConvUtil.isNullOrEmpty(parentCaregiver.getIdPerson())) {
			closureNoticeDto.setIdPerson(parentCaregiver.getIdPerson());
		}
		closureNoticeDto.setIndInvolvedParent(parentCaregiver.getIndInvolvedParent());
		return closureNoticeDto;
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
	public CpsInvNoticesRes saveClosureNotices(CpsInvNoticesClosureReq cpsInvNoticesClosureReq) {
		CpsInvNoticesRes cpsInvNoticesRes = new CpsInvNoticesRes();
		if (null != cpsInvNoticesClosureReq) {
			for (ClosureNoticeListDto noticeListItems : cpsInvNoticesClosureReq.getParentCaregiverNoticeDto()) {
				List<SchoolInvNotifctnVctm> schoolInvVictimList = new ArrayList<SchoolInvNotifctnVctm>();
				ConclusionNotifctnInfo conclusionNotifctnInfo = new ConclusionNotifctnInfo();
				switch (noticeListItems.getReqFuncAction()) {
				case ServiceConstants.REQ_FUNC_CD_ADD:
					conclusionNotifctnInfo = setConclusionNotifctnInfoValues(noticeListItems,
							cpsInvNoticesClosureReq.getIdLogin());
					if (!TypeConvUtil.isNullOrEmpty(noticeListItems.getVictimListDto())) {
						schoolInvVictimList = setSchoolInvVictimListValues(noticeListItems, conclusionNotifctnInfo,
								cpsInvNoticesClosureReq.getIdLogin());
					}
					if (!ObjectUtils.isEmpty(schoolInvVictimList)) {
						conclusionNotifctnInfo.setSchoolInvNotifctnVctmCollection(
								schoolInvVictimList.stream().collect(Collectors.toSet()));
					}
					sessionFactory.getCurrentSession().saveOrUpdate(conclusionNotifctnInfo);
					break;
				case ServiceConstants.REQ_FUNC_CD_UPDATE:
					noticeListItems.setReqFuncAction(ServiceConstants.REQ_FUNC_CD_UPDATE);
					conclusionNotifctnInfo = setConclusionNotifctnInfoValues(noticeListItems,
							cpsInvNoticesClosureReq.getIdLogin());
					// check if need to delete School Inv
					schoolInvVictimList = setSchoolInvVictimListValues(noticeListItems, conclusionNotifctnInfo,
							cpsInvNoticesClosureReq.getIdLogin());
					if (!TypeConvUtil.isNullOrEmpty(schoolInvVictimList)) {
						conclusionNotifctnInfo.setSchoolInvNotifctnVctmCollection(
								schoolInvVictimList.stream().collect(Collectors.toSet()));
					}
					// not using merge as it was not working for this table.
					sessionFactory.getCurrentSession()
							.saveOrUpdate(sessionFactory.getCurrentSession().merge(conclusionNotifctnInfo));
					break;
				case ServiceConstants.REQ_FUNC_CD_DELETE:
					if (!TypeConvUtil.isNullOrEmpty(noticeListItems.getIdConclusionNoticeInfo())) {
						conclusionNotifctnInfo.setIdConclusionNotifctnInfo(noticeListItems.getIdConclusionNoticeInfo());
						// check if the VictimList is Empty or not.
						if (!TypeConvUtil.isNullOrEmpty(noticeListItems.getVictimListDto())) {
							List<VictimListDto> victimList = noticeListItems.getVictimListDto();
							for (VictimListDto victims : victimList) {
								if (!TypeConvUtil.isNullOrEmpty(victims.getIdSchoolInvNoticeVictim())) {
									SchoolInvNotifctnVctm schoolInvNotifctnVctmPrev = (SchoolInvNotifctnVctm) sessionFactory
											.getCurrentSession()
											.load(SchoolInvNotifctnVctm.class, (victims.getIdSchoolInvNoticeVictim()));
									schoolInvVictimList.add(schoolInvNotifctnVctmPrev);
								}
							}
						}
					}
					conclusionNotifctnInfo.setSchoolInvNotifctnVctmCollection(
							schoolInvVictimList.stream().collect(Collectors.toSet()));
					sessionFactory.getCurrentSession().delete(conclusionNotifctnInfo);
					break;
				default:
					break;
				}
			}
		}
		if (sessionFactory.getCurrentSession().getTransaction().wasCommitted()) {
			cpsInvNoticesRes.setSuccessMessage("Successfully Committed.");
		}
		return cpsInvNoticesRes;
	}

	/**
	 * Method Description:Sets the SchoolInvVictimListValues values used in save
	 * process.
	 * 
	 * @param noticeListItems
	 * @param conclusionNotifctnInfo
	 * @return
	 */
	private List<SchoolInvNotifctnVctm> setSchoolInvVictimListValues(ClosureNoticeListDto noticeListItems,
			ConclusionNotifctnInfo conclusionNotifctnInfo, Long idLogin) {
		List<SchoolInvNotifctnVctm> schoolInvVictimList = new ArrayList<SchoolInvNotifctnVctm>();
		Date timeNow = new Date(Calendar.getInstance().getTimeInMillis());
		if (!TypeConvUtil.isNullOrEmpty(noticeListItems.getVictimListDto())) {
			for (VictimListDto victimListItems : noticeListItems.getVictimListDto()) {
				SchoolInvNotifctnVctm schoolInvNotifctnVctm = new SchoolInvNotifctnVctm();
				if (TypeConvUtil.isNullOrEmpty(noticeListItems.getIdConclusionNoticeInfo())
						&& ServiceConstants.STRING_IND_Y.equalsIgnoreCase(victimListItems.getIndVictimChecked())) {
					victimListItems.setReqFuncAction(ServiceConstants.REQ_FUNC_CD_ADD);
					if (!TypeConvUtil.isNullOrEmpty(victimListItems.getIdVictim())) {
						schoolInvNotifctnVctm.setIdVctm(BigDecimal.valueOf(victimListItems.getIdVictim()));
					}
					schoolInvNotifctnVctm.setDtCreated(timeNow);
					if (!TypeConvUtil.isNullOrEmpty(idLogin)) {
						schoolInvNotifctnVctm.setIdCreatedPerson(BigDecimal.valueOf(idLogin));
						schoolInvNotifctnVctm.setIdLastUpdatePerson(BigDecimal.valueOf(idLogin));
					}
					schoolInvNotifctnVctm.setDtLastUpdate(timeNow);
					schoolInvNotifctnVctm.setIdConclusionNotifctnInfo(conclusionNotifctnInfo);
					if (!TypeConvUtil.isNullOrEmpty(schoolInvVictimList))
						schoolInvVictimList.add(schoolInvNotifctnVctm);
				} else {
					// not . if yes then check indicator to delete.
					if (!TypeConvUtil.isNullOrEmpty(noticeListItems.getIdConclusionNoticeInfo())) {
						if (!TypeConvUtil.isNullOrEmpty(victimListItems.getIdSchoolInvNoticeVictim())) {
							if (ServiceConstants.STRING_IND_N.equalsIgnoreCase(victimListItems.getIndVictimChecked())) {
								if (deleteVictims(victimListItems, noticeListItems.getIdConclusionNoticeInfo()) > 0) {
									logger.info("Update Successful, check DB. ");
								}
								schoolInvVictimList = null;
							}
						} else {
							if (ServiceConstants.STRING_IND_Y.equalsIgnoreCase(victimListItems.getIndVictimChecked())) {
								victimListItems.setReqFuncAction(ServiceConstants.REQ_FUNC_CD_ADD);
								if (!TypeConvUtil.isNullOrEmpty(victimListItems.getIdVictim())) {
									schoolInvNotifctnVctm.setIdVctm(BigDecimal.valueOf(victimListItems.getIdVictim()));
								}
								schoolInvNotifctnVctm.setDtCreated(timeNow);
								if (!TypeConvUtil.isNullOrEmpty(idLogin)) {
									schoolInvNotifctnVctm.setIdCreatedPerson(BigDecimal.valueOf(idLogin));
									schoolInvNotifctnVctm.setIdLastUpdatePerson(BigDecimal.valueOf(idLogin));
								}
								schoolInvNotifctnVctm.setDtLastUpdate(timeNow);
								schoolInvNotifctnVctm.setIdConclusionNotifctnInfo(conclusionNotifctnInfo);
								if (!TypeConvUtil.isNullOrEmpty(schoolInvVictimList))
									schoolInvVictimList.add(schoolInvNotifctnVctm);
							}
						}
					}
				}
			}
		}
		return schoolInvVictimList;
	}

	/**
	 * Method Description:Deletes the Victim from the SchoolInvNoticeVictim
	 * table when the checkbox is unchecked for the victim.
	 * 
	 * @param victimListItems
	 * @param idConclusionNoticeInfo
	 */
	private int deleteVictims(VictimListDto victimListItems, Long idConclusionNoticeInfo) {
		int resValue = 0;
		Query res = ((Query) sessionFactory.getCurrentSession().createSQLQuery(deleteVictimsSql)
				.setParameter("idSchoolInvNoticeVictim", victimListItems.getIdSchoolInvNoticeVictim()));
		try {
			resValue = res.executeUpdate();
		} catch (HibernateException e) {
			logger.fatal("Exception occured in deleteVictims method of CPSInvCnlsnDaoImpl Class " + e.getMessage());
		}
		return resValue;
	}

	/**
	 * Method Description:Sets the ConclusionNotifctnInfoValues values used in
	 * save process.
	 * 
	 * @param noticeListItem
	 * @return ConclusionNotifctnInfo
	 */
	private ConclusionNotifctnInfo setConclusionNotifctnInfoValues(ClosureNoticeListDto noticeListItems, Long idLogin) {
		ConclusionNotifctnInfo conclusionNotifctnInfo = new ConclusionNotifctnInfo();
		ConclusionNotifctnInfo conclusionNoticeInfoPrev = new ConclusionNotifctnInfo();

		if (TypeConvUtil.isNullOrEmpty(noticeListItems.getIdConclusionNoticeInfo())) {
			noticeListItems.setReqFuncAction(ServiceConstants.REQ_FUNC_CD_ADD);
		} else {
			noticeListItems.setReqFuncAction(ServiceConstants.REQ_FUNC_CD_UPDATE);
			conclusionNotifctnInfo.setIdConclusionNotifctnInfo(noticeListItems.getIdConclusionNoticeInfo());
			conclusionNoticeInfoPrev = (ConclusionNotifctnInfo) sessionFactory.getCurrentSession()
					.get(ConclusionNotifctnInfo.class, (noticeListItems.getIdConclusionNoticeInfo()));
		}
		if (!TypeConvUtil.isNullOrEmpty(noticeListItems.getReqFuncAction())
				&& (ServiceConstants.REQ_FUNC_CD_ADD.equals(noticeListItems.getReqFuncAction())
						|| ServiceConstants.REQ_FUNC_CD_UPDATE.equals(noticeListItems.getReqFuncAction()))) {
			conclusionNotifctnInfo = checkSetIdPerson(noticeListItems, conclusionNotifctnInfo,
					conclusionNoticeInfoPrev);
			conclusionNotifctnInfo = checkSetCdType(noticeListItems, conclusionNotifctnInfo, conclusionNoticeInfoPrev);
			conclusionNotifctnInfo = checkSetIdEvent(noticeListItems, conclusionNotifctnInfo, conclusionNoticeInfoPrev);
			conclusionNotifctnInfo = checkSetDtCreated(noticeListItems, conclusionNotifctnInfo,
					conclusionNoticeInfoPrev);
			conclusionNotifctnInfo = checkSetIdCreatedPerson(noticeListItems, conclusionNotifctnInfo,
					conclusionNoticeInfoPrev, idLogin);
			conclusionNotifctnInfo = checkSetDtLastUpdate(noticeListItems, conclusionNotifctnInfo,
					conclusionNoticeInfoPrev);
			conclusionNotifctnInfo = checkSetIdLastUpdatePerson(noticeListItems, conclusionNotifctnInfo,
					conclusionNoticeInfoPrev, idLogin);
			conclusionNotifctnInfo = setInvolvedParentIndicator(noticeListItems, conclusionNotifctnInfo);
		}
		return conclusionNotifctnInfo;
	}

	/**
	 * Method Description:Sets the IdLastUpdatePerson value used in save
	 * process.
	 * 
	 * @param noticeListItems
	 * @param conclusionNotifctnInfo
	 * @param conclusionNoticeInfoPrev
	 * @return
	 */
	private ConclusionNotifctnInfo checkSetIdLastUpdatePerson(ClosureNoticeListDto noticeListItems,
			ConclusionNotifctnInfo conclusionNotifctnInfo, ConclusionNotifctnInfo conclusionNoticeInfoPrev,
			Long idLogin) {
		if (!TypeConvUtil.isNullOrEmpty(idLogin)) {
			if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(noticeListItems.getReqFuncAction())
					|| (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(noticeListItems.getReqFuncAction())
							&& !noticeListItems.getCdNoticeSelected()
									.equalsIgnoreCase(TypeConvUtil.isNullOrEmpty(conclusionNoticeInfoPrev.getCdType())
											? "" : conclusionNoticeInfoPrev.getCdType()))) {
				conclusionNotifctnInfo.setIdLastUpdatePerson(BigDecimal.valueOf(idLogin));
			} else {
				conclusionNotifctnInfo.setIdLastUpdatePerson(conclusionNoticeInfoPrev.getIdLastUpdatePerson());
			}
		} else {
			conclusionNotifctnInfo.setIdLastUpdatePerson(conclusionNoticeInfoPrev.getIdLastUpdatePerson());
		}
		return conclusionNotifctnInfo;
	}

	/**
	 * sets IndInvolveddParent on ConclusionNotifctnInfo if the notice selected is AR Family notification letter.
	 * @param noticeListItems
	 * @param conclusionNotifctnInfo
	 * @return
	 */
	private ConclusionNotifctnInfo setInvolvedParentIndicator(ClosureNoticeListDto noticeListItems,
															  ConclusionNotifctnInfo conclusionNotifctnInfo){

		if(ServiceConstants.INVOLVED_PARENT_LETTERS.stream()
				.noneMatch(noticeListItems.getCdNoticeSelected()::equalsIgnoreCase)){
			conclusionNotifctnInfo.setIndInvolveddParent(null);
			return conclusionNotifctnInfo;
		}
		if (TypeConvUtil.isNullOrEmpty(noticeListItems.getIndInvolvedParent())){
			conclusionNotifctnInfo.setIndInvolveddParent(ServiceConstants.STRING_IND_N);
		} else {
			conclusionNotifctnInfo.setIndInvolveddParent(ServiceConstants.STRING_IND_Y);
		}
		return conclusionNotifctnInfo;
	}

	/**
	 * Method Description:Sets the DtLastUpdate value used in save process.
	 * 
	 * @param noticeListItems
	 * @param conclusionNotifctnInfo
	 * @param conclusionNoticeInfoPrev
	 * @return
	 */
	private ConclusionNotifctnInfo checkSetDtLastUpdate(ClosureNoticeListDto noticeListItems,
			ConclusionNotifctnInfo conclusionNotifctnInfo, ConclusionNotifctnInfo conclusionNoticeInfoPrev) {
		Date timeNow = new Date(Calendar.getInstance().getTimeInMillis());
		if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(noticeListItems.getReqFuncAction())
				|| (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(noticeListItems.getReqFuncAction())
						&& !noticeListItems.getCdNoticeSelected()
								.equalsIgnoreCase(TypeConvUtil.isNullOrEmpty(conclusionNoticeInfoPrev.getCdType()) ? ""
										: conclusionNoticeInfoPrev.getCdType()))) {
			conclusionNotifctnInfo.setDtLastUpdate(timeNow);
		} else {
			conclusionNotifctnInfo.setDtLastUpdate(conclusionNoticeInfoPrev.getDtLastUpdate());
		}
		return conclusionNotifctnInfo;
	}

	/**
	 * Method Description:Sets the idCreatedPerson value used in save process.
	 * 
	 * @param noticeListItems
	 * @param conclusionNotifctnInfo
	 * @param conclusionNoticeInfoPrev
	 * @return
	 */
	private ConclusionNotifctnInfo checkSetIdCreatedPerson(ClosureNoticeListDto noticeListItems,
			ConclusionNotifctnInfo conclusionNotifctnInfo, ConclusionNotifctnInfo conclusionNoticeInfoPrev,
			Long idLogin) {
		if (!TypeConvUtil.isNullOrEmpty(idLogin) && !TypeConvUtil.isNullOrEmpty(noticeListItems.getReqFuncAction())) {
			if (noticeListItems.getReqFuncAction().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_ADD)) {
				conclusionNotifctnInfo.setIdCreatedPerson(BigDecimal.valueOf(idLogin));
			} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(noticeListItems.getReqFuncAction())) {
				conclusionNotifctnInfo.setIdCreatedPerson(conclusionNoticeInfoPrev.getIdCreatedPerson());
			}
		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(noticeListItems.getReqFuncAction())) {
			conclusionNotifctnInfo.setIdCreatedPerson(conclusionNoticeInfoPrev.getIdCreatedPerson());
		}
		return conclusionNotifctnInfo;
	}

	/**
	 * Method Description:Sets the dtCreated value used in save process.
	 * 
	 * @param noticeListItems
	 * @param conclusionNotifctnInfo
	 * @param conclusionNoticeInfoPrev
	 * @return
	 */
	private ConclusionNotifctnInfo checkSetDtCreated(ClosureNoticeListDto noticeListItems,
			ConclusionNotifctnInfo conclusionNotifctnInfo, ConclusionNotifctnInfo conclusionNoticeInfoPrev) {
		Date timeNow = new Date(Calendar.getInstance().getTimeInMillis());
		if (!TypeConvUtil.isNullOrEmpty(noticeListItems.getReqFuncAction())) {
			if (noticeListItems.getReqFuncAction().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_ADD)) {
				conclusionNotifctnInfo.setDtCreated(timeNow);
			} else if (noticeListItems.getReqFuncAction().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
				conclusionNotifctnInfo.setDtCreated(conclusionNoticeInfoPrev.getDtCreated());
			}
		}
		return conclusionNotifctnInfo;
	}

	/**
	 * Method Description:Sets the idEvent value used in save process.
	 * 
	 * @param noticeListItems
	 * @param conclusionNotifctnInfo
	 * @param conclusionNoticeInfoPrev
	 * @return
	 */
	private ConclusionNotifctnInfo checkSetIdEvent(ClosureNoticeListDto noticeListItems,
			ConclusionNotifctnInfo conclusionNotifctnInfo, ConclusionNotifctnInfo conclusionNoticeInfoPrev) {
		if (!TypeConvUtil.isNullOrEmpty(noticeListItems.getIdEvent())) {
			conclusionNotifctnInfo.setIdEvent(BigDecimal.valueOf(noticeListItems.getIdEvent()));
		} else {
			conclusionNotifctnInfo.setIdEvent(conclusionNoticeInfoPrev.getIdEvent());
		}
		return conclusionNotifctnInfo;
	}

	/**
	 * Method Description: Sets the cdType value used in save process.
	 * 
	 * @param noticeListItems
	 * @param conclusionNotifctnInfo
	 * @param conclusionNoticeInfoPrev
	 * @return ConclusionNotifctnInfo
	 */
	private ConclusionNotifctnInfo checkSetCdType(ClosureNoticeListDto noticeListItems,
			ConclusionNotifctnInfo conclusionNotifctnInfo, ConclusionNotifctnInfo conclusionNoticeInfoPrev) {
		if (null != noticeListItems.getCdNoticeSelected()) {
			conclusionNotifctnInfo.setCdType(noticeListItems.getCdNoticeSelected());
		} else {
			conclusionNotifctnInfo.setCdType(conclusionNoticeInfoPrev.getCdType());
		}
		return conclusionNotifctnInfo;
	}

	/**
	 * Method Description:Sets the idPerson value used in save process.
	 * 
	 * @param noticeListItems
	 * @param conclusionNotifctnInfo
	 * @param conclusionNoticeInfoPrev
	 */
	private ConclusionNotifctnInfo checkSetIdPerson(ClosureNoticeListDto noticeListItems,
			ConclusionNotifctnInfo conclusionNotifctnInfo, ConclusionNotifctnInfo conclusionNoticeInfoPrev) {
		if (!TypeConvUtil.isNullOrEmpty(noticeListItems.getIdPerson())) {
			conclusionNotifctnInfo.setIdPerson(BigDecimal.valueOf(noticeListItems.getIdPerson()));
		} else {
			conclusionNotifctnInfo.setIdPerson(conclusionNoticeInfoPrev.getIdPerson());
		}
		return conclusionNotifctnInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.casemanagement.dao.CPSInvCnlsnDao#
	 * updateChildSexLaborTrafficking(java.lang.Long)
	 */
	@Override
	public Long updateChildSexLaborTrafficking(Long idCase) {
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateChildSexLaborTraffic)
				.setParameter("idCase", idCase));
		int rowupdated = query.executeUpdate();
		return Long.valueOf(rowupdated);
	}

	/**
	 * 
	 * Method Description: This method is to get the initail role of the School
	 * Personnel in the system.
	 * 
	 * @param idStage
	 * @param idPerson
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Boolean getSchoolPersonnelInitialRole(Long idEvent, Long idStage) {
		Boolean schoolInvestigation = Boolean.FALSE;
		List<CpsInvNoticesQueryResultDto> resultList = new ArrayList<CpsInvNoticesQueryResultDto>();
		resultList = (List<CpsInvNoticesQueryResultDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(schoolPersonnelSql).setParameter("idEvent", idEvent).setParameter("idStage", idStage))
						.addScalar("idNoticePerson", StandardBasicTypes.LONG)
						.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
						.addScalar("cdStagePersType", StandardBasicTypes.STRING)
						.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(CpsInvNoticesQueryResultDto.class)).list();
		if (null != resultList) {
			for (CpsInvNoticesQueryResultDto items : resultList) {
				if (null != items.getCdStagePersRelInt()
						&& ServiceConstants.SCHOOL_PERSONNEL.equalsIgnoreCase(items.getCdStagePersRelInt())
						&& (null != items.getCdStagePersType())
						&& ServiceConstants.PRINCIPAL.equalsIgnoreCase(items.getCdStagePersType())
						&& (null != items.getCdStagePersRole())
						&& ServiceConstants.ALLEGED_PERPETRATOR.equalsIgnoreCase(items.getCdStagePersRole())) {
					schoolInvestigation = Boolean.TRUE;
				}
			}
		}
		return schoolInvestigation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean getCompletedAssessmentCount(Long idCase) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(completedAssessmentCount)
				.setParameter("idCase", idCase).setParameter("eventStatus", ServiceConstants.CEVTSTAT_PROC)
				.setParameter("cdTaskSafety", ServiceConstants.CD_TASK_SA)
				.setParameter("cdTaskRisk", ServiceConstants.CD_TASK_RA)
				.setParameter("relDate", ServiceConstants.ReleaseCode);
		List<Object> countObj = query.list();
		BigDecimal cnt = (BigDecimal) countObj.get(0);
		return cnt.intValue() > 0 ? Boolean.TRUE : Boolean.FALSE;
	}

	/**
	 * Method Name: savePrintPerson Method Description: Method Saves the
	 * Selected Printer Person against the Conclusion Event ID.
	 * 
	 * @param idEvent
	 * @param idPrintPerson
	 * @param idApprover
	 * @return
	 */
	@Override
	public Boolean savePrintPerson(Long idEvent, Long idPrintPerson, Long idApprover) {

		ConclusionEventPrintStaff printStaffDetails = new ConclusionEventPrintStaff();
		Date timeNow = new Date(Calendar.getInstance().getTimeInMillis());

		// Populate entity to save
		printStaffDetails.setIdEvent(idEvent);
		printStaffDetails.setIdApproverPerson(idApprover);
		printStaffDetails.setIdPrintPerson(idPrintPerson);
		printStaffDetails.setIdCreatedPerson(idApprover);
		printStaffDetails.setIdLastUpdatePerson(idApprover);
		printStaffDetails.setDtCreated(timeNow);
		printStaffDetails.setDtLastUpdate(timeNow);

		// Save the entity
		Long insertedID = (Long) sessionFactory.getCurrentSession().save(printStaffDetails);

		return ServiceConstants.Zero_Value < insertedID ? Boolean.TRUE : Boolean.FALSE;
	}

	/**
	 * Method Name: getPrintPerson Method Description: Method gets the Selected
	 * Printer Person against the Conclusion Event ID.
	 * 
	 * @param idEvent
	 * @param idApprover
	 * @return
	 */
	@Override
	public Long getPrintPerson(Long idEvent, Long idApprover) {
		BigDecimal eventId = BigDecimal.valueOf(idEvent);
		Long printPerson = ServiceConstants.ZERO_VAL;
		// First try to get print person by event. This will return person if we
		// already
		// have selected print person for conclusion.
		BigDecimal idPerson = (BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(getPrintPersonByEvent)
				.setParameter("idEvent", eventId).uniqueResult();

		// If not able to find print person by conclusion event ID. Try to get
		// Print
		// person based on approver
		if (ObjectUtils.isEmpty(idPerson)) {
			idPerson = (BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(getPrintPersonByApprover)
					.setParameter("idApprover", idApprover).uniqueResult();
		}

		// If print person is found assign the PersonId, else default value 0
		// will be
		// sent
		if (!ObjectUtils.isEmpty(idPerson)) {
			printPerson = idPerson.longValue();
		}

		return printPerson;
	}

	/**
	 * Method Name: getPersonIdInFDTC Method Description:The method returns the
	 * list of person id/name that have legal actions of type FDTC for a given
	 * case
	 * 
	 * @param caseId
	 * @return HashMap
	 * 
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Long, String> getPersonIdInFDTC(Long caseId) {
		HashMap<Long, String> resultHashMap = new HashMap<Long, String>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersonIdInFDTCSql)
				.addScalar(ServiceConstants.ID_PERSON_CONST, StandardBasicTypes.LONG)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING);
		query.setParameter(ServiceConstants.CASEID, caseId);
		query.setParameter("cdLegalAct", ServiceConstants.CLEGCPS_CFDT)
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class));

		List<PersonDto> personList = query.list();
		if (!ObjectUtils.isEmpty(personList)) {
			for (PersonDto personDto : personList) {
				resultHashMap.put(personDto.getIdPerson(), personDto.getNmPersonFull());
			}
		}
		return resultHashMap;

	}

	/**
	 * Method Name: getMostRecentFDTCSubtype Method Description:Returns the most
	 * recent FDTC subtype and Outcome date for a Person in a given case id
	 * 
	 * @param personId
	 * @return HashMap<String, String>
	 * 
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> getMostRecentFDTCSubtype(Long personId) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		List<String> legalActionSubtype = new ArrayList<String>();
		legalActionSubtype.add(ServiceConstants.CFDT_010);
		legalActionSubtype.add(ServiceConstants.CFDT_020);
		legalActionSubtype.add(ServiceConstants.CFDT_030);
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getMostRecentFDTCSubtypeSql)
				.addScalar("legalActActnSubtype", StandardBasicTypes.STRING)
				.addScalar("idEvent", StandardBasicTypes.LONG);
		query.setParameter(ServiceConstants.ID_PERSON_CONST, personId);
		query.setParameterList("legalActionSubtype", legalActionSubtype);
		query.setParameter("legalAct", ServiceConstants.CLEGCPS_CFDT)
				.setResultTransformer(Transformers.aliasToBean(ServiceDlvryClosureDto.class));
		List<ServiceDlvryClosureDto> serviceDlvryClosure = query.list();
		if (!ObjectUtils.isEmpty(serviceDlvryClosure)) {
			hashMap.put(ServiceConstants.CD_LEGAL_ACT_ACTN_SUBTYPE,
					serviceDlvryClosure.get(0).getLegalActActnSubtype());
			hashMap.put(ServiceConstants.ID_EVENT_STAGE, String.valueOf(serviceDlvryClosure.get(0).getIdEvent()));
		}
		return hashMap;

	}

	/**
	 * Method Name: getVictimDataForAllegation Method Description:This method
	 * gets person info for victim on an allegation.
	 * 
	 * @param ulIdAllegation
	 * @return List<PersonValueDto>
	 */
	@SuppressWarnings("unchecked")
	public List<PersonValueDto> getVictimDataForAllegation(Long ulIdAllegation) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getVictimDataForAllegationSql)
				.addScalar("personId", StandardBasicTypes.LONG).addScalar("dateOfBirth", StandardBasicTypes.DATE)
				.addScalar("dateOfDeath", StandardBasicTypes.DATE)
				.setResultTransformer(Transformers.aliasToBean(PersonValueDto.class));
		query.setParameter("idAlleg", ulIdAllegation);
		List<PersonValueDto> personValueDtoList = query.list();
		if (TypeConvUtil.isNullOrEmpty(personValueDtoList)) {
			throw new DataNotFoundException(messageSource.getMessage("CPSInvCnlsnDaoImpl.notFound", null, Locale.US));

		}

		return personValueDtoList;

	}

	/**
	 * Method Name: getDtIntakeForIdStage Method Description:This method returns
	 * the intake date on the INT stage.
	 * 
	 * @param idStage
	 * @return Date
	 * 
	 */
	public Date getDtIntakeForIdStage(Long idStage) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getDtIntakeForIdStageSql)
				.addScalar("dtIncomingCall", StandardBasicTypes.DATE)
				.setResultTransformer(Transformers.aliasToBean(PersonValueDto.class));
		query.setParameter(ServiceConstants.idStage, idStage);
		PersonValueDto personValueDto = (PersonValueDto) query.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(personValueDto)) {
			throw new DataNotFoundException(messageSource.getMessage("CPSInvCnlsnDaoImpl.notFound", null, Locale.US));

		}
		return personValueDto.getDtIncomingCall();

	}

	/**
	 * Method Name: fetchAllegQuestionAnswers Method Description: This method
	 * gets ID, DOB, and DOD for victim on an allegation.
	 * 
	 * @param idStage
	 * @return Boolean
	 * 
	 */
	public Boolean fetchAllegQuestionAnswers(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(fetchAllegQuestionAnswersSql);

		query.setParameter(ServiceConstants.idStage, idStage);
		BigDecimal count = (BigDecimal) query.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(count)) {
			if (count.longValue() > ServiceConstants.ZERO_VAL) {
				return true;
			}
		}
		return false;

	}

	/**
	 * Method Name: fetchAllegQuestionYAnswers Method Description:This method
	 * returns if all questions have been answered.
	 * 
	 * @param idStage
	 * @return Boolean
	 * 
	 */
	public Boolean fetchAllegQuestionYAnswers(Long idStage) {
		boolean retValue = false;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(fetchAllegQuestionYAnswersSql);
		query.setParameter("idAllStage", idStage);
		BigDecimal count = (BigDecimal) query.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(count)) {
			retValue = false;
		}
		if (count.longValue() > ServiceConstants.ZERO_VAL) {
			retValue = true;
		}

		return retValue;

	}

	/**
	 * Method Name: validateDispAndCFQuestion Method Description:This method
	 * returns true if all the answers to the question match the severity.
	 * 
	 * @param idStage
	 * @return Boolean
	 * 
	 */
	public Boolean validateDispAndCFQuestion(Long idStage) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Allegation.class);
		criteria.add(Restrictions.eq("stage.idStage", idStage));
		criteria.add(Restrictions.eq("cdAllegSeverity", ServiceConstants.ALLEG_SEVERITY));
		criteria.add(Restrictions.eq("indFatality", ServiceConstants.FATAL_NO));
		int count = criteria.list().size();
		if (count > ServiceConstants.ZERO_VAL) {
			return true;
		}
		return false;

	}

	/**
	 * Method Name: validateRlqshCstdyAndPrsnCharQuestion Method
	 * Description:This method checks the relinquish cstdy question and person
	 * char for SB44.
	 * 
	 * @param idStage
	 * @return List<Long>
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Long> validateRlqshCstdyAndPrsnCharQuestion(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(validateRlqshCstdyAndPrsnCharQuestionSql);
		query.setParameter("idAllStage", idStage);
		List<BigDecimal> idVictims = (List<BigDecimal>) query.list();
		List<Long> idPersons = new ArrayList<Long>();
		for (BigDecimal bigDecimal : idVictims) {
			idPersons.add(bigDecimal.longValue());
		}
		return idPersons;

	}

	/**
	 * Method Name: getRlnqshAnswrdYVictimIds Method Description: This method
	 * returns list of victimIds whose relinquish question is answered yes.
	 * 
	 * @param idStage
	 * @return List<Long>
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getRlnqshAnswrdYVictimIds(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getRlnqshAnswrdYVictimIdsSql);
		query.setParameter("idAllStage", idStage);
		List<BigDecimal> idVictims = (List<BigDecimal>) query.list();
		List<Long> idVictimsList = new ArrayList<Long>();
		for (BigDecimal bigDecimal : idVictims) {
			idVictimsList.add(bigDecimal.longValue());
		}
		return idVictimsList;

	}

	/**
	 * Method Name: isEventStatusNew Method Description:This method returns TRUE
	 * if there is at least one event status as 'NEW'
	 * 
	 * @param idStage
	 * @return Boolean
	 * 
	 */
	public Boolean isEventStatusNew(Long idStage) {
		Boolean retVal = false;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(isEventStatusNewSql);
		query.setParameter(ServiceConstants.IDSTAGE, idStage);
		Character eventStatus = (Character) query.uniqueResult();
		if (ServiceConstants.CHAR_IND_Y == eventStatus) {
			retVal = true;
		}
		return retVal;

	}

	/**
	 * Method Name: isChildSexLaborTrafficking Method Description:This method
	 * returns TRUE if the case has answered Child Sex/Labor Trafficking
	 * question in the current stage or there is Allegation of Child Sex/Labor
	 * Trafficking
	 * 
	 * @param idCase
	 * @param idStage
	 * @return Boolean
	 * 
	 */
	public Boolean isChildSexLaborTrafficking(Long idCase, Long idStage) {
		Boolean isChildSexLaborTraffic = false;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(isChildSexLaborTraffickingSql);

		query.setParameter(ServiceConstants.IDCASE, idCase);

		Character eventStatusStr = (Character) query.uniqueResult();
		if (ServiceConstants.CHAR_IND_Y == eventStatusStr) {
			isChildSexLaborTraffic = true;
		}
		if (!isChildSexLaborTraffic) {
			Query queryIf = sessionFactory.getCurrentSession().createSQLQuery(isChildSexLaborTraffickingFalseSql);
			queryIf.setParameter("idInvstStage", idStage);
			eventStatusStr = (Character) queryIf.uniqueResult();
			if (ServiceConstants.CHAR_IND_Y == eventStatusStr) {
				isChildSexLaborTraffic = true;
			}
		}
		return isChildSexLaborTraffic;

	}

	/**
	 * Method Name: rlnqushQuestionAnsweredY Method Description:This method
	 * returns true if atleast one of the relinquish allegation cstdy is
	 * answered yes.
	 * 
	 * @param idStage
	 * @param ulIdVictim
	 * @return Boolean
	 * 
	 */
	public Boolean rlnqushQuestionAnsweredY(Long idStage, Long ulIdVictim) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(rlnqushQuestionAnsweredYSql);

		query.setParameter("idAllStage", idStage);
		query.setParameter("idVictim", ulIdVictim);

		BigDecimal count = (BigDecimal) query.uniqueResult();
		if (count.longValue() > ServiceConstants.ZERO_VAL) {
			return true;
		}
		return false;

	}

	/**
	 * Method Name: isRlnqshAnswrd Method Description:This method returns true
	 * if relinquish question is not answered.
	 * 
	 * @param idStage
	 * @param idCase
	 * @return Boolean
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Boolean isRlnqshAnswrd(Long idStage, Long idCase) {
		Boolean relqshAnswrd = true;

		Query query = sessionFactory.getCurrentSession().createSQLQuery(isRlnqshAnswrdSql)
				.addScalar("idVictim", StandardBasicTypes.LONG)
				.addScalar("idAllegedPreparator", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(CpsInvCnclsnValidationDto.class));

		query.setParameter("idAllStage", idStage);
		query.setParameter(ServiceConstants.IDCASE, idCase);

		List<CpsInvCnclsnValidationDto> cpsInvCnclsnValidationValueDtos = (List<CpsInvCnclsnValidationDto>) query
				.list();
		if (!ObjectUtils.isEmpty(cpsInvCnclsnValidationValueDtos)) {
			for (CpsInvCnclsnValidationDto cpsInvCnclsnValidationValueDto : cpsInvCnclsnValidationValueDtos) {
				Map<Long, PersonValueDto> personMap = getPrsnByCaseAndStage(idStage, idCase);
				PersonValueDto personValueDto = personMap.get(cpsInvCnclsnValidationValueDto.getIdVictim());
				if (personValueDto != null && TypeConvUtil.isNullOrEmpty(personValueDto.getDateOfDeath())
						&& 5 < personValueDto.getAge()) {
					PersonValueDto perpPersonBean = personMap
							.get(cpsInvCnclsnValidationValueDto.getIdAllegedPreparator());
					if (!ObjectUtils.isEmpty(perpPersonBean)
							&& ServiceConstants.MAX_CLD_AGE < perpPersonBean.getAge()) {
						relqshAnswrd = false;
						break;
					}
				}
			}
		}
		return relqshAnswrd;
	}

	/**
	 * Method Name: getPrsnByCaseAndStage Method Description: This Method is
	 * used to get the person by idCase and idStage
	 * 
	 * @param idStage
	 * @param idCase
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<Long, PersonValueDto> getPrsnByCaseAndStage(Long idStage, Long idCase) {
		Map<Long, PersonValueDto> personMap = new HashMap<Long, PersonValueDto>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPrsnByCaseAndStageSql)
				.addScalar("personId", StandardBasicTypes.LONG).addScalar("dateOfBirth", StandardBasicTypes.DATE)
				.addScalar("dateOfDeath", StandardBasicTypes.DATE)
				.setResultTransformer(Transformers.aliasToBean(PersonValueDto.class));
		query.setParameter(ServiceConstants.idStage, idStage);
		query.setParameter(ServiceConstants.IDCASE, idCase);
		List<PersonValueDto> personValueDtos = query.list();
		if (!ObjectUtils.isEmpty(personValueDtos)) {
			for (PersonValueDto personValueDto : personValueDtos) {
				personValueDto.setAge(!StringUtils.isEmpty(personValueDto.getDateOfBirth())
						? DateUtils.getAge(personValueDto.getDateOfBirth()) : ServiceConstants.Zero_INT);
				personMap.put(personValueDto.getPersonId(), personValueDto);
			}
		}
		return personMap;
	}

	/**
	 * Method Name: isDispositionMissing Method Description:Retrieve true if any
	 * of allegation(s) for the input stage is missing a disposition
	 * 
	 * @param stageId
	 * @return Boolean
	 * 
	 */
	public Boolean isDispositionMissing(Long stageId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(isDispositionMissingSql);
		query.setParameter("idAllStage", stageId);
		Character status = (Character) query.uniqueResult();
		if (ServiceConstants.CHAR_IND_Y == status) {
			return true;
		}
		return false;

	}

	/**
	 * Method Name: getInitialSDMSafetyAssmntCount Method Description:Returns
	 * the initial safety assessment count.
	 * 
	 * @param stageId
	 * @return Long
	 *
	 */
	public Long getInitialSDMSafetyAssmntCount(Long stageId) {
		return executeQueryWithStageID(getInitialSDMSafetyAssmntSql, stageId);

	}

	/**
	 * Method Name: getRecordCountUsingStageID Method Description:Method to get
	 * Record count for the SQL query executed in a Stage
	 * 
	 * @param stageId
	 * @return Long
	 */
	private Long executeQueryWithStageID(String sql, Long stageId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);

		query.setParameter(ServiceConstants.idStage, stageId);

		BigDecimal count = (BigDecimal) query.uniqueResult();

		return count.longValue();
	}

	/**
	 * Method Name: getPriorARInitialSafetyAssmntCount Method Description:
	 * Returns the AR initial Safety Assessment count.
	 * 
	 * @param stageId
	 * @return Long
	 *
	 */
	public Long getPriorARInitialSafetyAssmntCount(Long stageId) {
		return executeQueryWithStageID(getPriorARInitialSafetyAssmntCountSql, stageId);

	}
	
	/**
	 * Method Name: getPriorARInitialSafetyAssmntCount Method Description:
	 * Returns the AR initial Safety Assessment count.
	 * 
	 * @param stageId
	 * @return Long
	 *
	 */
	public Long getPriorARInitialSafetyAssmntCountNew(Long stageId) {
		return executeQueryWithStageID(getPriorARInitialSafetyAssmntCountNewSql, stageId);

	}

	/**
	 * Method Name: getRemovalRecordCount Method Description: Returns count of
	 * removal indicated on the Safety Assessment.
	 * 
	 * @param stageId
	 * @return Long
	 *
	 */
	public Long getRemovalRecordCount(Long stageId) {
		Long count = executeQueryWithStageID(getRemovalRecordCountSql, stageId);
		Long removalCount = -1l;
		if (count > 0) {
			removalCount = executeQueryWithStageID(getRemovalRecordCountFindSql, stageId);
		}

		return removalCount;

	}

	/**
	 * Method Name: getRemovalRecordCountForHousehold Method Description: Query
	 * returns count of removals on SDM with Household.
	 * 
	 * @param stageId
	 * @param idHshldPrsn
	 * @return removalCount
	 */
	@Override
	public Long getRemovalRecordCountForHousehold(Long stageId, Long idHshldPrsn) {
		// Get the Prior Stage(A-R) Stage id for current stage.
		Long removalCount = -1l;
		List<Long> stageIdList = new ArrayList<>();
		stageIdList.add(stageId);
		SelectStageDto priorStage = caseSummaryDao.getStage(stageId, ServiceConstants.STAGE_PRIOR);
		if (ServiceConstants.A_R_STAGE.equalsIgnoreCase(priorStage.getCdStage())) {
			stageIdList.add(priorStage.getIdStage());
		}
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getRemovalRecordCountForHouseholdSql);
		query.setParameterList(ServiceConstants.IDSTAGE, stageIdList).setParameter("idHouseHold", idHshldPrsn);
		BigDecimal count = (BigDecimal) query.uniqueResult();
		if (0 < count.longValue()) {
			removalCount = executeQueryWithStageID(getRemovalRecordCountFindSql, stageId);
		}
		return removalCount;
	}

	/**
	 * Method Name: getPcspCount Method Description:Returns count of pcsp
	 * indicated on the Safety Assessment.
	 * 
	 * @param stageId
	 * @return Long
	 *
	 */
	public Long getPcspCount(Long stageId) {
		Long count = executeQueryWithStageID(getPcspCountSql, stageId);
		Long pcspCount = -1l;
		if (count > ServiceConstants.ZERO_VAL) {
			pcspCount = executeQueryWithStageID(getPcspCountFindSql, stageId);
		}

		return pcspCount;

	}

	/**
	 * Method Name: getPcspCountForHousehold Method Description: This method
	 * returns the count of PCSP indicated on the safety Assessment for the case
	 * when conclusion is submitted with houshold.
	 * 
	 * @param stageId
	 * @return pcspCount
	 */
	@Override
	public Long getPcspCountForCase(Long stageId, Long idCase) {
		Long count = executeQueryWithStageID(getPcspCountSql, stageId);
		Long pcspCount = -1l;
		if (count > ServiceConstants.ZERO_VAL) {
			Query query = sessionFactory.getCurrentSession().createSQLQuery(getPcspCountFindForCaseSql);
			query.setParameter(ServiceConstants.IDCASE, idCase);
			BigDecimal resultCount = (BigDecimal) query.uniqueResult();
			pcspCount = resultCount.longValue();
		}
		return pcspCount;
	}

	/**
	 * Method Name: pcspSelectedCount Method Description:Returns safety decision
	 * indicated on the Safety Assessment.
	 * 
	 * @param cpsSaId
	 * @return Long
	 *
	 */
	@Override
	public Long pcspSelectedCount(Long cpsSaId) {
		return executeQueryWithStageID(pcspSelectedCountSql, cpsSaId);

	}

	/**
	 * Method Name: cspInterventionCount Method Description:
	 * 
	 * @param stageId
	 * @return Long
	 *
	 */
	public Long cspInterventionCount(Long stageId) {
		return executeQueryWithStageID(cspInterventionCountSql, stageId);

	}

	/**
	 * Method Name: getInCompSDMSafetyAssmntCount Method Description: Returns
	 * the Incomplete Safety Sssessment count.
	 * 
	 * @param stageId
	 * @return Long @
	 */
	public Long getInCompSDMSafetyAssmntCount(Long stageId) {
		return executeQueryWithStageID(getInCompSDMSafetyAssmntCountSql, stageId);

	}

	/**
	 * Method Name: getSafetyDecisionAndIdCps Method Description:Returns safety
	 * decision indicated on the Safety Assessment.
	 * 
	 * @param stageId
	 * @return Map<Long, String>
	 *
	 */
	public Map<Long, String> getSafetyDecisionAndIdCps(Long stageId) {
		Map<Long, String> personMap = new HashMap<Long, String>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSafetyDecisionAndIdCpsSql)
				.addScalar("cdSafetyDecision", StandardBasicTypes.STRING).addScalar("idCpsSa", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(CpsInvCnclsnValidationDto.class));
		query.setParameter(ServiceConstants.idStage, stageId);

		CpsInvCnclsnValidationDto cpsInvCnclsnValidationValueDto = (CpsInvCnclsnValidationDto) query.uniqueResult();
		if (!ObjectUtils.isEmpty(cpsInvCnclsnValidationValueDto)) {
			personMap.put(cpsInvCnclsnValidationValueDto.getIdCpsSa(),
					cpsInvCnclsnValidationValueDto.getCdSafetyDecision());
		}
		return personMap;

	}

	/**
	 * Method Name: getSafetyDecisionAndIdCpsForHoushold Method Description:
	 * Returns safety decision indicated on Safety assessment for the household
	 * selected on Investigation Conclusion.
	 * 
	 * @param stageId
	 * @param idHshldPrsn
	 * @return
	 */
	@Override
	public Map<Long, String> getSafetyDecisionAndIdCpsForHoushold(Long stageId, Long idHshldPrsn) {
		Map<Long, String> personMap = new HashMap<>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSafetyDecisionAndIdCpsForHouseholdSql)
				.addScalar("cdSafetyDecision", StandardBasicTypes.STRING).addScalar("idCpsSa", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(CpsInvCnclsnValidationDto.class));
		query.setParameter(ServiceConstants.IDSTAGE, stageId).setParameter("idHouseHold", idHshldPrsn);
		CpsInvCnclsnValidationDto cpsInvCnlsnValDto = (CpsInvCnclsnValidationDto) query.uniqueResult();
		if (!ObjectUtils.isEmpty(cpsInvCnlsnValDto)) {
			personMap.put(cpsInvCnlsnValDto.getIdCpsSa(), cpsInvCnlsnValDto.getCdSafetyDecision());
		}
		return personMap;
	}

	/**
	 * Method Name: sdmRiskAssmComp Method Description:Method to check if the
	 * Risk Assessment for the stage is completed
	 * 
	 * @param stageId
	 * @return Boolean
	 *
	 */
	public Boolean sdmRiskAssmComp(Long stageId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sdmRiskAssmCompSql);
		query.setParameter(ServiceConstants.idStage, stageId);
		BigDecimal count = (BigDecimal) query.uniqueResult();
		if (count.longValue() > ServiceConstants.ZERO_VAL) {
			return true;
		}
		return false;
	}

	/**
	 * Method Name: sdmRiskAssmCompForHousehold Method Description: Method to
	 * check if the Risk Assessment for the stage is completed for the Household
	 * selected in Investigation Conclusion page.
	 * 
	 * @param stageId
	 * @param idHshldPrsn
	 * @return Boolean
	 */
	@Override
	public Boolean sdmRiskAssmCompForHousehold(Long stageId, Long idHshldPrsn) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sdmRiskAssmCompForHouseholdSql);
		query.setParameter(ServiceConstants.IDSTAGE, stageId).setParameter("idHouseHold", idHshldPrsn);
		BigDecimal count = (BigDecimal) query.uniqueResult();
		if (count.longValue() > ServiceConstants.ZERO_VAL) {
			return true;
		}
		return false;
	}

	@Override
	public Long getSevenDaySafetyAssmntCount(Long stageId) {
		return executeQueryWithStageID(getSevenDaySafetyAssmntCountSql, stageId);
	}

	/**
	 * Method Name: checkTrfckngRecPend Method Description: The method checks if
	 * Trafficking Record Exist for Victim if an Allegation of RTB/UTD with Type
	 * SXTR/LBTR Exists.
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Allegation checkTrfckngRecPend(Long idStage) {
		// Fetch the VictimIds for the Allegations in Current stage for which
		// Disposition is RTB or UTD.
		Allegation algntn = null;
		List<Allegation> allegations = (List<Allegation>) sessionFactory.getCurrentSession()
				.createQuery(getIdVictimsForAllInINV).setParameter("idStage", idStage)
				.setParameterList("disposition", DISP_RTB_UTD).setParameterList("allegType", ALGTN_SXTR_LBTR).list();
		if (!ObjectUtils.isEmpty(allegations)) {
			algntn = allegations.stream().filter(a -> !isCnfrmdTrfckngRecExist(a.getPersonByIdVictim().getIdPerson(),a.getCdAllegDisposition()))
					.findFirst().orElse(null);
		}
		return algntn;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Allegation> checkTrfckngRecPendList(Long idStage) {
		// Fetch the VictimIds for the Allegations in Current stage for which
		// Disposition is RTB or UTD.
		List<Allegation> allegationList = new ArrayList<Allegation>();
		List<Allegation> allegations = (List<Allegation>) sessionFactory.getCurrentSession()
				.createQuery(getIdVictimsForAllInINV).setParameter("idStage", idStage)
				.setParameterList("disposition", DISP_RTB_UTD).setParameterList("allegType", ALGTN_SXTR_LBTR).list();
		if (!ObjectUtils.isEmpty(allegations)) {
			allegationList = allegations.stream().filter(a -> !isCnfrmdTrfckngRecExist(a.getPersonByIdVictim().getIdPerson(),a.getCdAllegDisposition()))
					.collect(Collectors.toList());
		}
		return allegationList;
	}

	/**
	 * Method Name: isCnfrmdTrfckngRecExist Method Description: private method
	 * to check the Traficking record for passed victim(idPerson)
	 * 
	 * @param idPerson
	 * @return
	 */
	private boolean isCnfrmdTrfckngRecExist(Long idPerson, String cdDisposition) {
		if (CodesConstant.CDISPSTN_RTB.equalsIgnoreCase(cdDisposition)){
			List<BigDecimal> idTrfckLst = (List<BigDecimal>) sessionFactory.getCurrentSession()
					.createQuery(getTrfckngExistsForVictim).setParameter("idPerson", BigDecimal.valueOf(idPerson))
					.setParameter("status", CodesConstant.CTRFSTAT_CONF).list();
			return !ObjectUtils.isEmpty(idTrfckLst);
		}else{ // Defect 11263 - For UTD, record can be in any status
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(TrfckngDtl.class);
			criteria.add(Restrictions.eq("idPerson", BigDecimal.valueOf(idPerson)));
			return criteria.list().size() > 0;
		}
	}
	
	/**
	 * Method Name: checkSxHistIndForAllegs Method Description: This method will
	 * check if there is an SVH incident recorded for PRN(s) if any
	 * allegation(cd_alleg_disposition = 'RTB' and cd_alleg_type = 'SXAB') exists
	 * Artifact id : artf131180
	 * 
	 * @param idStage
	 * @return boolean
	 */
	@Override
	public boolean checkSxHistIndForAllegs(Long idStage) {
		boolean sxHistIndNotRecorded = false;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getRTBSXAllegForVictim).addScalar("id_victim",
				StandardBasicTypes.LONG);
		query.setParameter("idStage", idStage);
		List<Long> personIds = (query.list() == null) ? null : (List<Long>) query.list();
		if (!ObjectUtils.isEmpty(personIds)) {
			sxHistIndNotRecorded = personIds.stream().filter(persondId -> checkSxVctmztnExists(persondId)).findAny()
					.isPresent();
		}

		return sxHistIndNotRecorded;

	}
	
	private boolean checkSxVctmztnExists(Long idPerson) {

		Predicate<ChildSxVctmztn> predicate = childSxVctmztn -> (null == childSxVctmztn
				|| "N".equalsIgnoreCase(childSxVctmztn.getIndChildSxVctmztnHist()));

		ChildSxVctmztn childSxVctmztn = sexualVictimizationHistoryDao.getChildSxVctmztnByPersonId(idPerson);

		return predicate.test(childSxVctmztn);
	}

	/**
	 * artf228543
	 * Method Name: hasAllegedPerpetratorWithAgeLessThanTen
	 * Method Description: This method checks if the given stage has any alleged perpetrator
	 * with age less than ten and disposition is not Admin Closure.
	 *
	 * @param idStage
	 * @param idCase
	 * @return boolean
	 */
	public boolean hasAllegedPerpetratorWithAgeLessThanTen (Long idStage, Long idCase) {
		Long count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAllegedPerpetrators)
				.setParameter("idCase", idCase).setParameter("idStage", idStage)).addScalar("count", StandardBasicTypes.LONG).uniqueResult();
		return count > 0 ? true :false;
	}

	/**
	 * Method Name:updateSubstanceTracker
	 * Method Description: This method updates substance tracker table during person merge process.
	 * If the idClosedPerson has any records in substance tracker table, then they will be updated to idForwardPerson
	 *
	 * @param idStage         stage id of the where the data needs to be updated
	 * @param idClosedPerson  id of the person being closed
	 * @param idForwardPerson id of the person being forwarded
     */
	public void updateSubstanceTrackerForPersonMerge(Long idStage, Long idClosedPerson, Long idForwardPerson){
		//Get Closed Person's substance trackers
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SubstanceTracker.class);
		criteria.add(Restrictions.eq("idPerson", idClosedPerson));
		criteria.add(Restrictions.eq("idStage", idStage));
		List<SubstanceTracker> closedPersonSubstanceTrackers = criteria.list();

		//Get Forward person's substance trackers
		Criteria criteriaForForwardPerson = sessionFactory.getCurrentSession().createCriteria(SubstanceTracker.class);
		criteriaForForwardPerson.add(Restrictions.eq("idPerson", idForwardPerson));
		criteriaForForwardPerson.add(Restrictions.eq("idStage", idStage));
		List<SubstanceTracker> fwdPersonSubstanceTrackers = criteriaForForwardPerson.list();
		List<String> forwardPersonSubstances = fwdPersonSubstanceTrackers.stream()
				.map(SubstanceTracker::getCdSubstance)
				.collect(Collectors.toList());

		closedPersonSubstanceTrackers.forEach(substanceTracker -> {
			if(forwardPersonSubstances.contains(substanceTracker.getCdSubstance())){
				sessionFactory.getCurrentSession().delete(substanceTracker);
			} else {
				substanceTracker.setIdPerson(idForwardPerson);
				sessionFactory.getCurrentSession().save(substanceTracker);
			}
		});
	}

	public void updateVictimOnClosureLetters(Set<Long> idStages, Long idPersonMergeWorker, Long idClosedPerson, Long idForwardPerson) {
		Date timeNow = new Date(Calendar.getInstance().getTimeInMillis());
		//Get closed person school Inv Notifctn Vctm
		List<SchoolInvNotifctnVctm> closedPersonSchoolInvNotifctnVctm = getSchoolInvNotifctnVctms(idStages, idClosedPerson);
		//Get forward person school Inv Notifctn Vctm
		List<SchoolInvNotifctnVctm> forwardPersonSchoolInvNotifctnVctm = getSchoolInvNotifctnVctms(idStages, idForwardPerson);

		List<Long> forwardPersonConclusionIds = forwardPersonSchoolInvNotifctnVctm.stream()
				.map(SchoolInvNotifctnVctm::getIdConclusionNotifctnInfo).map(ConclusionNotifctnInfo::getIdConclusionNotifctnInfo)
				.collect(Collectors.toList());

		closedPersonSchoolInvNotifctnVctm.forEach(closedPrsnSchoolInvVctm -> {
			if (forwardPersonConclusionIds.contains(closedPrsnSchoolInvVctm.getIdConclusionNotifctnInfo().getIdConclusionNotifctnInfo())) {
				sessionFactory.getCurrentSession().delete(closedPrsnSchoolInvVctm);
			} else {
				closedPrsnSchoolInvVctm.setIdVctm(BigDecimal.valueOf(idForwardPerson));
				closedPrsnSchoolInvVctm.setIdLastUpdatePerson(BigDecimal.valueOf(idPersonMergeWorker));
				closedPrsnSchoolInvVctm.setDtLastUpdate(timeNow);
				sessionFactory.getCurrentSession().save(closedPrsnSchoolInvVctm);
			}
		});
	}

	public List<SchoolInvNotifctnVctm> getSchoolInvNotifctnVctms(Set<Long> idStages, Long idPerson) {
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSchoolInvNotifctnVctmByPersonIdSql)
				.setParameterList("stages", idStages)
				.setParameter("idVctm", BigDecimal.valueOf(idPerson)))
				.addEntity("s", SchoolInvNotifctnVctm.class);
		return (List<SchoolInvNotifctnVctm>) query.list();
	}
}
