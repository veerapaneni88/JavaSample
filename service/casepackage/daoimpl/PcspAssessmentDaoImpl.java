package us.tx.state.dfps.service.casepackage.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.PcspAsmnt;
import us.tx.state.dfps.common.domain.PcspAsmntLookup;
import us.tx.state.dfps.common.domain.PcspPlcmnt;
import us.tx.state.dfps.common.domain.PcspPrsnLink;
import us.tx.state.dfps.common.domain.PcspRspns;
import us.tx.state.dfps.common.domain.PcspRspnsLookup;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.casepackage.dao.PcspAssessmentDao;
import us.tx.state.dfps.service.casepackage.dto.PCSPAssessmentDto;
import us.tx.state.dfps.service.casepackage.dto.PCSPAssessmentQuestionDto;
import us.tx.state.dfps.service.casepackage.dto.PCSPPersonDto;
import us.tx.state.dfps.service.casepackage.dto.PcspAsmntLkupDto;
import us.tx.state.dfps.service.casepackage.dto.PcspPrsnLinkDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PCSPAssessmentReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.EventDetailsNotFoundException;
import us.tx.state.dfps.service.exception.PcspAssessmentNotFoundException;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

@Repository
public class PcspAssessmentDaoImpl implements PcspAssessmentDao {

	@Value("${PcspAssessmentDaoImpl.getPersonChildOhmDtls}")
	private transient String getPersonChildOhmDtlsSql;

	@Value("${PcspAssessmentDaoImpl.getPrimaryCaregiverDtls}")
	private transient String getPrimaryCaregiverDtlsSql;

	@Value("${PcspAssessmentDaoImpl.getPersonDtls}")
	private transient String getPersonDtlsSql;

	@Value("${PcspAssessmentDaoImpl.getPlacementOpen}")
	private transient String getPlacementOpenSql;

	@Value("${PcspAssessmentDaoImpl.getLegacyPlacementOpen}")
	private transient String getLegacyPlacementOpenSql;

	@Value("${PcspAssessmentDaoImpl.getPlacementEndDate}")
	private transient String getPlacementEndDateSql;

	@Value("${PcspAssessmentDaoImpl.getLegacyPlacementEndDate}")
	private transient String getLegacyPlacementEndDateSql;

	@Value("${PcspAssessmentDaoImpl.getSubStageStartDate}")
	private transient String getSubStageStartDateSql;

	@Value("${PcspAssessmentDaoImpl.getresponseSaved}")
	private transient String getresponseSavedSql;

	@Value("${PcspAssessmentDaoImpl.getpcspAssessment}")
	private transient String getpcspAssessmentSql;

	@Value("${PcspAssessmentDaoImpl.getpcspPrsnLink}")
	private transient String getpcspPrsnLinkSql;

	@Value("${PcspAssessmentDaoImpl.getPcspSectionDtls}")
	private transient String getPcspSectionDtlsSql;

	@Value("${PcspAssessmentDaoImpl.getIntStgStDate}")
	private transient String getIntStgStDateSql;

	@Value("${PcspAssessmentDaoImpl.getpcspPrsnLinkOhm}")
	private transient String getpcspPrsnLinkOhmSql;

	@Value("${PcspAssessmentDaoImpl.getQueryPageData}")
	private transient String getQueryPageDataSql;

	@Value("${PcspAssessmentDaoImpl.deletePcspPrsnLink}")
	private transient String deletePcspPrsnLinkSql;

	@Value("${PcspAssessmentDaoImpl.placementExist}")
	private transient String placementExistSql;

	@Value("${PcspAssessmentDaoImpl.pcspAsmtFrmLkUp}")
	private transient String pcspAsmtFrmLkUpSql;

	@Value("${PcspAssessmentDaoImpl.prntAsmtId}")
	private transient String prntAsmtIdSql;

	@Value("${PcspAssessmentDaoImpl.showLegalWarning}")
	private transient String showLegalWarningSql;

	@Value("${PcspAssessmentDaoImpl.addressExists}")
	private transient String addressExistsSql;

	@Value("${PcspAssessmentDaoImpl.phoneExists}")
	private transient String phoneExistsSql;

	@Autowired
	private SessionFactory sessionFactory;

	public PcspAssessmentDaoImpl() {

	}

	/**
	 * This method will retrieve the person children ohm details from
	 * stage_person_link table for the stage
	 * 
	 * @param pcspAssessmentReq
	 * @param stageId
	 * @param personType
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PCSPPersonDto> getPersonChildOhmDtls(PCSPAssessmentReq pcspAssessmentReq, Long stageId,
			String personType) {
		List<PCSPPersonDto> pcspPersonDtoList = new ArrayList<PCSPPersonDto>();
		pcspPersonDtoList = (List<PCSPPersonDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getPersonChildOhmDtlsSql).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("dtPersonBirth", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPersonDeath", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("stgPrsnType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING).setParameter("idStage", stageId)
				.setParameter("idPcspAsmnt", pcspAssessmentReq.getPcspAssessmentDto().getIdPcspAsmnt())
				.setParameter("cdPcspPrsnType", personType)
				.setResultTransformer(Transformers.aliasToBean(PCSPPersonDto.class)).list();
		return pcspPersonDtoList;
	}

	/**
	 * This method will retrieve the primary care giver details from
	 * stage_person_link table for the stage
	 * 
	 * @param pcspAssessmentReq
	 * @param stageId
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PCSPPersonDto> getPrimaryCaregiverDtls(PCSPAssessmentReq pcspAssessmentReq, Long stageId) {
		List<PCSPPersonDto> pcspPersonDtoList = new ArrayList<PCSPPersonDto>();
		pcspPersonDtoList = (List<PCSPPersonDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getPrimaryCaregiverDtlsSql).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("dtPersonBirth", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPersonDeath", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("stgPrsnType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING).setParameter("idStage", stageId)
				.setParameter("idPcspAsmnt", pcspAssessmentReq.getPcspAssessmentDto().getIdPcspAsmnt())
				.setResultTransformer(Transformers.aliasToBean(PCSPPersonDto.class)).list();
		return pcspPersonDtoList;
	}

	/**
	 * This method will retrieve the person details from stage_person_link table
	 * for the stage
	 * 
	 * @param stageId
	 * @return List<PCSPAssmtValueDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PCSPPersonDto> getPersonDtls(Long stageId) {
		List<PCSPPersonDto> pcspPersonDtoList = new ArrayList<PCSPPersonDto>();
		pcspPersonDtoList = (List<PCSPPersonDto>) sessionFactory.getCurrentSession().createSQLQuery(getPersonDtlsSql)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("dtPersonBirth", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPersonDeath", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("stgPrsnType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING).setParameter("idStage", stageId)
				.setResultTransformer(Transformers.aliasToBean(PCSPPersonDto.class)).list();
		return pcspPersonDtoList;
	}

	/**
	 * This Method is used to check if the placement is open
	 * 
	 * @param personId
	 * @return Boolean @
	 */
	@Override
	public Boolean getPlacementOpen(Long personId) {
		Boolean isPlacementOpen = Boolean.FALSE;
		BigDecimal recExists = (BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(getPlacementOpenSql)
				.setParameter("idPerson", personId).uniqueResult();
		if (!TypeConvUtil.isNullOrEmptyBdDecm(recExists)) {
			isPlacementOpen = (recExists.longValue() > 0) ? true : false;
		}
		return isPlacementOpen;
	}

	/**
	 * This Method is used to check if the legacy placement is open
	 * 
	 * @param personId
	 * @return Boolean @
	 */
	@Override
	public Boolean getLegacyPlacementOpen(Long personId) {
		Boolean isPlacementOpen = Boolean.FALSE;
		BigDecimal recExists = (BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(getLegacyPlacementOpenSql)
				.setParameter("idPerson", personId).uniqueResult();
		if (!TypeConvUtil.isNullOrEmptyBdDecm(recExists)) {
			isPlacementOpen = (recExists.longValue() > 0) ? true : false;
		}
		return isPlacementOpen;
	}

	/**
	 * This Method is used to get the placement end date.
	 * 
	 * @param personId
	 * @return Date @
	 */
	@Override
	public Date getPlacementEndDate(Long personId) {
		PCSPPersonDto pcspPersonDto = new PCSPPersonDto();
		pcspPersonDto = (PCSPPersonDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPlacementEndDateSql).setParameter("idPerson", personId))
						.addScalar("dtEnd", StandardBasicTypes.TIMESTAMP)
						.setResultTransformer(Transformers.aliasToBean(PCSPPersonDto.class)).uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(pcspPersonDto) && !TypeConvUtil.isNullOrEmpty(pcspPersonDto.getDtEnd())) {
			return pcspPersonDto.getDtEnd();
		} else {
			return null;
		}
	}

	/**
	 * This Method is used to get the placement end date(to date of decision
	 * validation).
	 * 
	 * @param personId
	 * @return Date @
	 */
	@Override
	public Date getLegacyPlacementEndDate(Long personId) {
		PCSPPersonDto pcspPersonDto = new PCSPPersonDto();
		Date legPlacmtEndDate = null;
		pcspPersonDto = (PCSPPersonDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getLegacyPlacementEndDateSql).setParameter("idPerson", personId))
						.addScalar("dtLegacyPlacementEnd", StandardBasicTypes.TIMESTAMP)
						.setResultTransformer(Transformers.aliasToBean(PCSPPersonDto.class)).uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(pcspPersonDto)) {
			if (!TypeConvUtil.isNullOrEmpty(pcspPersonDto.getDtLegacyPlacementEnd())) {
				legPlacmtEndDate = pcspPersonDto.getDtLegacyPlacementEnd();
			}
		}
		return legPlacmtEndDate;
	}

	/**
	 * This Method is used to get the SUB stage start date of a child.
	 * 
	 * @param personId
	 * @return Date @
	 */
	public Date getSubStageStartDate(Long personId) {
		PCSPPersonDto pcspPersonDto;
		List<PCSPPersonDto> pcspPersonDtoList;
		Date legPlacmtEndDate = null;
		//Defect 11544 - Retreive the list
		pcspPersonDtoList = (List<PCSPPersonDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getSubStageStartDateSql).setParameter("idPerson", personId))
						.addScalar("dtSubStageStart", StandardBasicTypes.TIMESTAMP)
						.setResultTransformer(Transformers.aliasToBean(PCSPPersonDto.class)).list();
		
		if (!ObjectUtils.isEmpty(pcspPersonDtoList)) {
			pcspPersonDto = pcspPersonDtoList.get(0);
			if (!TypeConvUtil.isNullOrEmpty(pcspPersonDto.getDtSubStageStart())) {
				legPlacmtEndDate = pcspPersonDto.getDtSubStageStart();
			}
		}
		return legPlacmtEndDate;
	}

	/**
	 * This Method is used to check if answers are saved for the assessment
	 * 
	 * @param eventId
	 * @return Boolean @
	 */
	@Override
	public Boolean getResponseSaved(Long eventId) {
		Boolean isResponseSaved = Boolean.FALSE;
		BigDecimal recExists = (BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(getresponseSavedSql)
				.setParameter("idEvent", eventId).uniqueResult();
		if (!TypeConvUtil.isNullOrEmptyBdDecm(recExists)) {
			isResponseSaved = (recExists.longValue() > 0) ? true : false;
		}
		return isResponseSaved;
	}

	/**
	 * This is used to retrieve PCSPAssessment dtls based on PCSP Assessment
	 * Event Id and Stage Id It pulls back the sections, questions and responses
	 * 
	 * @param eventId
	 * @return PCSPAssessmentDto @
	 */
	@SuppressWarnings("unchecked")
	public PCSPAssessmentDto getPcspAssessmentDtls(Long eventId) {
		PCSPAssessmentDto pcspAssessmentDto = new PCSPAssessmentDto();
		pcspAssessmentDto = (PCSPAssessmentDto) sessionFactory.getCurrentSession().createSQLQuery(getpcspAssessmentSql)
				.addScalar("idPcspAsmnt", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("vrsn", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("eventDtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("caseName", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtAssessmentCompleted", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdDecsn", StandardBasicTypes.STRING).addScalar("dtDecsn", StandardBasicTypes.TIMESTAMP)
				.addScalar("idPcspAsmntLookup", StandardBasicTypes.LONG)
				.addScalar("idPrmryAsmnt", StandardBasicTypes.LONG).addScalar("cmnts", StandardBasicTypes.STRING)
				.addScalar("cdAsmntTyp", StandardBasicTypes.STRING).addScalar("idPrsncrgvr", StandardBasicTypes.LONG)
				.addScalar("cdPlcmntDecsn", StandardBasicTypes.STRING)
				.addScalar("dtPlcmnt", StandardBasicTypes.TIMESTAMP)
				.addScalar("indPlcdCourtOrder", StandardBasicTypes.STRING)
				.addScalar("idPrsnSprvsr", StandardBasicTypes.LONG).addScalar("idPrsnPd", StandardBasicTypes.LONG)
				.setParameter("idEvent", eventId)
				.setResultTransformer(Transformers.aliasToBean(PCSPAssessmentDto.class)).uniqueResult();
		return pcspAssessmentDto;
	}

	/**
	 * This is used to get the intake start date
	 * 
	 * @param eventId
	 * @return Date @
	 */
	public Date getIntakeStageStartDate(Long caseId) {
		Date intakeStgStDate = null;
		PCSPAssessmentDto pcspPAssessmentDto = (PCSPAssessmentDto) sessionFactory.getCurrentSession()
				.createSQLQuery(getIntStgStDateSql).addScalar("dtIntakeStart", StandardBasicTypes.TIMESTAMP)
				.setParameter("idCase", caseId).setResultTransformer(Transformers.aliasToBean(PCSPAssessmentDto.class))
				.uniqueResult();
		intakeStgStDate = pcspPAssessmentDto.getDtIntakeStart();
		return intakeStgStDate;
	}

	/**
	 * This is used to get Saved children in pcsp_prsn_link
	 * 
	 * @param idPcspAssmt
	 * @return Set<Integer> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PcspPrsnLinkDto> getpcspPrsnLinkChild(Long idPcspAssmt) {
		List<PcspPrsnLinkDto> childOhmSet = new ArrayList<PcspPrsnLinkDto>();
		childOhmSet = (List<PcspPrsnLinkDto>) sessionFactory.getCurrentSession().createSQLQuery(getpcspPrsnLinkSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdPcspPrsnType", StandardBasicTypes.LONG)
				.setParameter("idPcspAsmnt", idPcspAssmt)
				.setResultTransformer(Transformers.aliasToBean(PcspPrsnLinkDto.class)).list();
		return childOhmSet;
	}

	/**
	 * This is used to get Saved ohm in pcsp_prsn_link
	 * 
	 * @param idPcspAssmt
	 * @return Set<Integer> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PcspPrsnLinkDto> getpcspPrsnLinkOhm(Long idPcspAssmt) {
		List<PcspPrsnLinkDto> childOhmSet = new ArrayList<PcspPrsnLinkDto>();
		if (TypeConvUtil.isNullOrEmpty(idPcspAssmt)) {
			idPcspAssmt = 1l;
		}
		childOhmSet = (List<PcspPrsnLinkDto>) sessionFactory.getCurrentSession().createSQLQuery(getpcspPrsnLinkOhmSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdPcspPrsnType", StandardBasicTypes.LONG)
				.setParameter("idPcspAsmnt", idPcspAssmt)
				.setResultTransformer(Transformers.aliasToBean(PcspPrsnLinkDto.class)).list();
		return childOhmSet;
	}

	/**
	 * This Method will retrieve all sections, questions, responses. When
	 * iterating through the results we have to determine what changed (i.e.
	 * sections, questions, responses) in order to determine which objects to
	 * create.
	 * 
	 * @param idPcspAssmt
	 * @param idPcspAsmntlookup
	 * @return PCSPAssessmentDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PCSPAssessmentDto> getPcspSections(Long idPcspAssmt, Long idPcspAsmntlookup) {
		List<PCSPAssessmentDto> pcspAssessmentDto = new ArrayList<PCSPAssessmentDto>();
		pcspAssessmentDto = (List<PCSPAssessmentDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getPcspSectionDtlsSql).addScalar("idPcspSctnQstnLinkLookup", StandardBasicTypes.LONG)
				.addScalar("sctnOrder", StandardBasicTypes.LONG).addScalar("sctnQstnOrder", StandardBasicTypes.LONG)
				.addScalar("cdAnswr", StandardBasicTypes.STRING).addScalar("idPcspRspns", StandardBasicTypes.LONG)
				.addScalar("indCrmnlAbuseHist", StandardBasicTypes.STRING)
				.addScalar("otherDscrptn", StandardBasicTypes.STRING).addScalar("cdResponse", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("qstn", StandardBasicTypes.STRING)
				.addScalar("qstnName", StandardBasicTypes.STRING).addScalar("idPcspQstnLookup", StandardBasicTypes.LONG)
				.addScalar("toolTip", StandardBasicTypes.STRING).addScalar("idPcspRspnsLookup", StandardBasicTypes.LONG)
				.addScalar("rspnsName", StandardBasicTypes.STRING).addScalar("rspns", StandardBasicTypes.STRING)
				.addScalar("idPcspSctnLookup", StandardBasicTypes.LONG).addScalar("sctn", StandardBasicTypes.STRING)
				.addScalar("sctnName", StandardBasicTypes.STRING).setParameter("asmntId", idPcspAssmt)
				.setParameter("asmntLookupId", idPcspAsmntlookup)
				.setResultTransformer(Transformers.aliasToBean(PCSPAssessmentDto.class)).list();
		return pcspAssessmentDto;
	}

	/**
	 * This Method will retrieve all sections, questions, responses. When
	 * iterating through the results we have to determine what changed (i.e.
	 * sections, questions, responses) in order to determine which objects to
	 * create.
	 * 
	 * @param idPcspAsmntlookup
	 * @return PCSPAssessmentDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PCSPAssessmentDto> getQueryPageData(Long idPcspAsmntlookup) {
		List<PCSPAssessmentDto> pcspAssessmentDto = new ArrayList<PCSPAssessmentDto>();
		pcspAssessmentDto = (List<PCSPAssessmentDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getQueryPageDataSql).addScalar("sctnOrder", StandardBasicTypes.LONG)
				.addScalar("sctnQstnOrder", StandardBasicTypes.LONG)
				.addScalar("idPcspSctnQstnLinkLookup", StandardBasicTypes.LONG)
				.addScalar("sctn", StandardBasicTypes.STRING).addScalar("idPcspSctnLookup", StandardBasicTypes.LONG)
				.addScalar("sctnName", StandardBasicTypes.STRING).addScalar("rspnsName", StandardBasicTypes.STRING)
				.addScalar("idPcspAsmntLookup", StandardBasicTypes.LONG).addScalar("qstn", StandardBasicTypes.STRING)
				.addScalar("idPcspQstnLookup", StandardBasicTypes.LONG).addScalar("qstnName", StandardBasicTypes.STRING)
				.addScalar("toolTip", StandardBasicTypes.STRING).addScalar("idPcspRspnsLookup", StandardBasicTypes.LONG)
				.addScalar("rspns", StandardBasicTypes.STRING).addScalar("rspnsName", StandardBasicTypes.STRING)
				.addScalar("idPcspRspnsLookup", StandardBasicTypes.LONG)
				.setParameter("asmntLookupId", idPcspAsmntlookup)
				.setResultTransformer(Transformers.aliasToBean(PCSPAssessmentDto.class)).list();
		return pcspAssessmentDto;
	}

	/**
	 * This Method will delete children Or OHM from PCSP_PRSN_LINK table
	 * 
	 * @param assessmentId
	 * @param idPerson
	 * @return String @
	 */
	@Override
	public String deletePcspPersonLink(Long assessmentId, Long idPerson) {
		PcspPrsnLink pcspPrsnLinkEntity = new PcspPrsnLink();
		String rtnMsg = "";
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PcspPrsnLink.class, "pcspPrsnLnk");
		criteria.createAlias("pcspPrsnLnk.person", "person");
		criteria.createAlias("pcspPrsnLnk.pcspAsmnt", "pcspAsmnt");
		criteria.add(Restrictions.eq("person.idPerson", idPerson))
				.add(Restrictions.eq("pcspAsmnt.idPcspAsmnt", assessmentId));
		pcspPrsnLinkEntity = (PcspPrsnLink) criteria.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(pcspPrsnLinkEntity)) {
			sessionFactory.getCurrentSession().delete(pcspPrsnLinkEntity);
			rtnMsg = ServiceConstants.SUCCESS;
		} else {
			throw new PcspAssessmentNotFoundException(assessmentId, idPerson);
		}
		return rtnMsg;
	}

	/**
	 * This method is used to insert children and Other house hold members
	 * selected in the assessment/addendum into the pcsp_prsn_link table
	 * 
	 * @param assessmentId
	 * @param idPerson
	 * @return String @
	 */
	@Override
	public String savePcspPersonLink(PCSPAssessmentDto pcspAssessmentDto, Long idPerson, String cdPrsnType) {
		PcspPrsnLink pcspPrsnLinkEntity = new PcspPrsnLink();
		String rtnMsg = "";
		Date date = new Date();
		PcspAsmnt pcspAsmnt = new PcspAsmnt();
		Person person = new Person();
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.passeDatTxt(date)))
			pcspPrsnLinkEntity.setDtLastUpdate(date);
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(pcspAssessmentDto.getLoggedInUser())))
			pcspPrsnLinkEntity.setIdLastUpdatePerson(pcspAssessmentDto.getLoggedInUser());
		pcspPrsnLinkEntity.setDtCreated(date);
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(pcspAssessmentDto.getLoggedInUser())))
			pcspPrsnLinkEntity.setIdCreatedPerson(pcspAssessmentDto.getLoggedInUser());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(pcspAssessmentDto.getIdPcspAsmnt())))
			pcspAsmnt.setIdPcspAsmnt(pcspAssessmentDto.getIdPcspAsmnt());
		pcspPrsnLinkEntity.setPcspAsmnt(pcspAsmnt);
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(idPerson)))
			person.setIdPerson(idPerson);
		pcspPrsnLinkEntity.setPerson(person);
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(cdPrsnType)))
			pcspPrsnLinkEntity.setCdPcspPrsnType(cdPrsnType);
		sessionFactory.getCurrentSession().saveOrUpdate(pcspPrsnLinkEntity);
		rtnMsg = ServiceConstants.SUCCESS;
		return rtnMsg;
	}

	/**
	 * This method is used to add new records in PCSP Response table
	 * 
	 * @param pcspAssessmentDto
	 * @return String @
	 */
	@Override
	public String savePcspResponse(PCSPAssessmentDto pcspAssessmentDto, PCSPAssessmentQuestionDto questionDto) {
		PcspRspns pcspRspnsEntity = new PcspRspns();
		PcspAsmnt pcspAsmnt = new PcspAsmnt();
		PcspRspnsLookup pcspRspnsLookup = new PcspRspnsLookup();
		String rtnMsg = "";
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.passeDatTxt(pcspAssessmentDto.getDateLastUpdate())))
			pcspRspnsEntity.setDtLastUpdate(pcspAssessmentDto.getDateLastUpdate());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(pcspAssessmentDto.getLoggedInUser())))
			pcspRspnsEntity.setIdLastUpdatePerson(pcspAssessmentDto.getLoggedInUser());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.passeDatTxt(pcspAssessmentDto.getDtCreated())))
			pcspRspnsEntity.setDtCreated(pcspAssessmentDto.getDtCreated());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(pcspAssessmentDto.getLoggedInUser())))
			pcspRspnsEntity.setIdCreatedPerson(pcspAssessmentDto.getLoggedInUser());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(pcspAssessmentDto.getIdPcspAsmnt())))
			pcspAsmnt.setIdPcspAsmnt(pcspAssessmentDto.getIdPcspAsmnt());
		pcspRspnsEntity.setPcspAsmnt(pcspAsmnt);
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(questionDto.getIdQuestionSectionLink())))
			pcspRspnsEntity.setIdPcspSctnQstnLinkLookup(questionDto.getIdQuestionSectionLink());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(questionDto.getCdResponse()))) {
			pcspRspnsLookup.setIdPcspRspnsLookup(questionDto.getCdResponse());
			pcspRspnsEntity.setPcspRspnsLookup(pcspRspnsLookup);
		}
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(questionDto.getCdAnswr())))
			pcspRspnsEntity.setCdAnswr(questionDto.getCdAnswr());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(questionDto.getIndCrmnlAbuseHist())))
			pcspRspnsEntity.setIndCrmnlAbuseHist(questionDto.getIndCrmnlAbuseHist());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(questionDto.getOtherDscrptn())))
			pcspRspnsEntity.setTxtOtherDscrptn(questionDto.getOtherDscrptn());
		sessionFactory.getCurrentSession().saveOrUpdate(pcspRspnsEntity);
		rtnMsg = ServiceConstants.SUCCESS;
		return rtnMsg;
	}

	/**
	 * This Method is used to update the response table with text description
	 * and the criminal and abuse history for Q4 and Q5 of section 1
	 * 
	 * @param pcspAssessmentQuestionDto
	 * @return String @
	 */
	@Override
	public String updatePcspRespCriminal(PCSPAssessmentQuestionDto pcspAssessmentQuestionDto) {
		PcspRspns pcspRspnsEntity = new PcspRspns();
		String rtnMsg = "";
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PcspRspns.class, "pcspResp");
		criteria.createAlias("pcspResp.pcspAsmnt", "pcspAsmnt");
		criteria.add(Restrictions.eq("idPcspRspns", pcspAssessmentQuestionDto.getIdPcspRspns()))
				.add(Restrictions.eq("pcspAsmnt.idPcspAsmnt", pcspAssessmentQuestionDto.getIdPcspAsmnt()));
		pcspRspnsEntity = (PcspRspns) criteria.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(pcspAssessmentQuestionDto.getOtherDscrptn())))
			pcspRspnsEntity.setTxtOtherDscrptn(pcspAssessmentQuestionDto.getOtherDscrptn());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(pcspAssessmentQuestionDto.getIndCrmnlAbuseHist())))
			pcspRspnsEntity.setIndCrmnlAbuseHist(pcspAssessmentQuestionDto.getIndCrmnlAbuseHist());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(pcspAssessmentQuestionDto.getLoggedInUser())))
			pcspRspnsEntity.setIdLastUpdatePerson(pcspAssessmentQuestionDto.getLoggedInUser());
		sessionFactory.getCurrentSession().saveOrUpdate(pcspRspnsEntity);
		rtnMsg = ServiceConstants.SUCCESS;
		return rtnMsg;
	}

	/**
	 * This Method is used to update the response in PCSP Response table
	 * 
	 * @param pcspAssessmentQuestionDto
	 * @return String @
	 */
	@Override
	public String updatePcspResp(PCSPAssessmentQuestionDto pcspAssessmentQuestionDto) {
		PcspRspns pcspRspnsEntity = new PcspRspns();
		String rtnMsg = "";
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PcspRspns.class, "pcspResp");
		criteria.createAlias("pcspResp.pcspAsmnt", "pcspAsmnt");
		criteria.add(Restrictions.eq("idPcspRspns", pcspAssessmentQuestionDto.getIdPcspRspns()))
				.add(Restrictions.eq("pcspAsmnt.idPcspAsmnt", pcspAssessmentQuestionDto.getIdPcspAsmnt()));
		pcspRspnsEntity = (PcspRspns) criteria.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(pcspAssessmentQuestionDto.getCdAnswr())))
			pcspRspnsEntity.setCdAnswr(pcspAssessmentQuestionDto.getCdAnswr());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(pcspAssessmentQuestionDto.getOtherDscrptn())))
			pcspRspnsEntity.setTxtOtherDscrptn(pcspAssessmentQuestionDto.getOtherDscrptn());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(pcspAssessmentQuestionDto.getIndCrmnlAbuseHist())))
			pcspRspnsEntity.setIndCrmnlAbuseHist(pcspAssessmentQuestionDto.getIndCrmnlAbuseHist());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(pcspAssessmentQuestionDto.getLoggedInUser())))
			pcspRspnsEntity.setIdLastUpdatePerson(pcspAssessmentQuestionDto.getLoggedInUser());
		sessionFactory.getCurrentSession().saveOrUpdate(pcspRspnsEntity);
		rtnMsg = ServiceConstants.SUCCESS;
		return rtnMsg;
	}

	/**
	 * This Method is used to update the assessment in Pcsp Assessment table
	 * 
	 * @param pcspAssessmentDto
	 * @return String @
	 */
	public String updateAssessment(PCSPAssessmentDto pcspAssessmentDto) {
		PcspAsmnt pcspAsmntEntity = new PcspAsmnt();
		String rtnMsg = "";
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(PcspAsmnt.class)
				.add(Restrictions.eq("idPcspAsmnt", pcspAssessmentDto.getIdPcspAsmnt()));
		pcspAsmntEntity = (PcspAsmnt) cr.uniqueResult();
		Calendar DtPlcmnt = Calendar.getInstance();
		Calendar DtDcsn = Calendar.getInstance();
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(pcspAssessmentDto.getCdDecsn())))
			pcspAsmntEntity.setCdDecsn(pcspAssessmentDto.getCdDecsn());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.passeDatTxt(pcspAssessmentDto.getDtDecsn()))) {
			DtDcsn.setTime(pcspAssessmentDto.getDtDecsn());
			// DtDcsn.add(Calendar.DATE, 1);
			pcspAsmntEntity.setDtDecsn(DtDcsn.getTime());
		}
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(pcspAssessmentDto.getCdPlcmntDecsn())))
			pcspAsmntEntity.setCdPlcmntDecsn(pcspAssessmentDto.getCdPlcmntDecsn());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.passeDatTxt(pcspAssessmentDto.getDtPlcmnt()))) {
			DtPlcmnt.setTime(pcspAssessmentDto.getDtPlcmnt());
			// DtPlcmnt.add(Calendar.DATE, 1);
			pcspAsmntEntity.setDtPlcmnt(DtPlcmnt.getTime());
		}
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(pcspAssessmentDto.getIndPlcdCourtOrder())))
			pcspAsmntEntity.setIndPlcdCourtOrder(pcspAssessmentDto.getIndPlcdCourtOrder());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(pcspAssessmentDto.getIdPrsnSprvsr())))
			pcspAsmntEntity.setIdPrsnSprvsr(pcspAssessmentDto.getIdPrsnSprvsr());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(pcspAssessmentDto.getIdPrsnPd())))
			pcspAsmntEntity.setIdPrsnPd(pcspAssessmentDto.getIdPrsnPd());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(pcspAssessmentDto.getCmnts())))
			pcspAsmntEntity.setTxtCmnts(pcspAssessmentDto.getCmnts());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(pcspAssessmentDto.getLoggedInUser())))
			pcspAsmntEntity.setIdLastUpdatePerson(pcspAssessmentDto.getLoggedInUser());
		sessionFactory.getCurrentSession().saveOrUpdate(pcspAsmntEntity);
		rtnMsg = ServiceConstants.SUCCESS;
		return rtnMsg;
	}

	/**
	 * This Method is used to check if placement exists for the primary
	 * caregiver for the same case
	 * 
	 * @param caregiverId
	 * @param caseId
	 * @return Boolean @
	 */
	@Override
	public Boolean getPlacementExists(Long caregiverId, Long caseId) {
		Boolean isPlacementExists = Boolean.FALSE;
		BigDecimal recExists = (BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(placementExistSql)
				.setParameter("idCase", caseId).setParameter("idCaregiver", caregiverId).uniqueResult();
		if (!TypeConvUtil.isNullOrEmptyBdDecm(recExists)) {
			isPlacementExists = (recExists.longValue() > 0) ? true : false;
		}
		return isPlacementExists;
	}

	/**
	 * This Method is used to insert record into PCSP_ASMNT table
	 * 
	 * @param pcspAssessmentDto
	 * @return String @
	 */
	@Override
	public Long savePcspAssessment(PCSPAssessmentDto pcspAssessmentDto, PcspAsmntLkupDto pcspAsmntLkupDto,
			Long idPrimaryAsmt) {
		PcspAsmnt pcspAsmntEntity = new PcspAsmnt();
		Event event = new Event();
		PcspAsmntLookup pcspAsmntLookup = new PcspAsmntLookup();
		Long idPcspAssmt = ServiceConstants.ZERO_VAL;
		Person person = new Person();
		pcspAsmntEntity.setDtLastUpdate(new Date());
		pcspAsmntEntity.setIdLastUpdatePerson(pcspAssessmentDto.getLoggedInUser());
		pcspAsmntEntity.setDtCreated(new Date());
		pcspAsmntEntity.setIdCreatedPerson(pcspAssessmentDto.getLoggedInUser());
		event.setIdEvent(pcspAssessmentDto.getIdEvent());
		pcspAsmntEntity.setEvent(event);
		pcspAsmntLookup.setIdPcspAsmntLookup(pcspAsmntLkupDto.getIdPcspAsmntLookup());
		pcspAsmntEntity.setPcspAsmntLookup(pcspAsmntLookup);
		person.setIdPerson(pcspAssessmentDto.getIdPrsncrgvr());
		pcspAsmntEntity.setPerson(person);
		pcspAsmntEntity.setCdAsmntTyp(pcspAsmntLkupDto.getCdAsmntType());
		if (ServiceConstants.PCSPPRSN_020.equalsIgnoreCase(pcspAsmntLkupDto.getCdAsmntType())) {
			pcspAsmntEntity.setIdPrmryAsmnt(idPrimaryAsmt);
		} else {
			pcspAsmntEntity.setIdPrmryAsmnt(ServiceConstants.NULL_VAL);
		}
		sessionFactory.getCurrentSession().persist(pcspAsmntEntity);
		idPcspAssmt = pcspAsmntEntity.getIdPcspAsmnt();
		return idPcspAssmt;
	}

	/**
	 * This Method is used to get PCSP Assessment lookup details.
	 * 
	 * @param pcspAssessmentDto
	 * @return PcspAsmntLkupDto @
	 */
	@Override
	public PcspAsmntLkupDto getPcspAsmntLkup(PCSPAssessmentDto pcspAssessmentDto) {
		PcspAsmntLkupDto pcspAsmntLkupDto = new PcspAsmntLkupDto();
		pcspAsmntLkupDto = (PcspAsmntLkupDto) sessionFactory.getCurrentSession().createSQLQuery(pcspAsmtFrmLkUpSql)
				.addScalar("idPcspAsmnt", StandardBasicTypes.LONG)
				.addScalar("idPcspAsmntLookup", StandardBasicTypes.LONG).addScalar("vrsn", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("dtEfctv", StandardBasicTypes.TIMESTAMP).addScalar("cdAsmntType", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("createdByName", StandardBasicTypes.STRING).addScalar("updByName", StandardBasicTypes.STRING)
				.addScalar("dtAssmtComp", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtLastUpdEvent", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtEventOccrd", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdEvntStatus", StandardBasicTypes.STRING)
				.setParameter("cdAsmtType", pcspAssessmentDto.getCdAsmntTyp())
				.setParameter("idStage", pcspAssessmentDto.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(PcspAsmntLkupDto.class)).uniqueResult();
		return pcspAsmntLkupDto;
	}

	/**
	 * This Method is used to get the parent assessment Id for the addendum
	 * 
	 * @param idCaregiver
	 * @param idCase
	 * @return Long @
	 */
	@Override
	public Long getPrimaryAsmtId(Long idCaregiver, Long idCase) {
		Long idPrimaryAsmt = 0l;
		BigDecimal assmtId = (BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(prntAsmtIdSql)
				.setParameter("idCase", idCase).setParameter("idPrsnCrgvr", idCaregiver).uniqueResult();
		if (!TypeConvUtil.isNullOrEmptyBdDecm(assmtId)) {
			idPrimaryAsmt = assmtId.longValue();
		}
		return idPrimaryAsmt;
	}

	/**
	 * This Method is used to sets the status of PCSP Assessment Event to
	 * COMP(Complete)
	 * 
	 * @param eventDto
	 * @return Long @
	 */
	public Long updateEvntStatusToComp(EventDto eventDto) {
		Event eventEntity = new Event();
		Long idEvent = 0l;
		Person person = new Person();
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(Event.class)
				.add(Restrictions.eq("idEvent", eventDto.getIdEvent()));
		eventEntity = (Event) cr.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(eventDto.getCdEventStatus())))
			eventEntity.setCdEventStatus(eventDto.getCdEventStatus());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(eventDto.getIdPerson())))
			person.setIdPerson(eventDto.getIdPerson());
		eventEntity.setPerson(person);
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.passeDatTxt(eventDto.getDtLastUpdate())))
			eventEntity.setDtLastUpdate(eventDto.getDtLastUpdate());
		sessionFactory.getCurrentSession().saveOrUpdate(eventEntity);
		idEvent = eventEntity.getIdEvent();
		return idEvent;
	}

	/**
	 * This Method will insert completedDate for the PCSP_ASMNT table when the
	 * assessment is complete.
	 * 
	 * @param pcspAssessmentDto
	 * @return Long @
	 */
	public Long updateAssmtCompDate(PCSPAssessmentDto pcspAssessmentDto) {
		PcspAsmnt pcspAsmntEntity = new PcspAsmnt();
		Long idPcspAssmt = 0l;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(PcspAsmnt.class)
				.add(Restrictions.eq("idPcspAsmnt", pcspAssessmentDto.getIdPcspAsmnt()));
		pcspAsmntEntity = (PcspAsmnt) cr.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(pcspAssessmentDto.getLoggedInUser())))
			pcspAsmntEntity.setIdLastUpdatePerson(pcspAssessmentDto.getLoggedInUser());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.passeDatTxt(new Date())))
			pcspAsmntEntity.setDtCmplt(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(pcspAsmntEntity);
		idPcspAssmt = pcspAsmntEntity.getIdPcspAsmnt();
		return idPcspAssmt;
	}

	/**
	 * This method is used to insert records to placements tables when the
	 * assessment is complete.
	 * 
	 * @param pcspAssessmentDto
	 * @return Long @
	 */
	public Long savePcspPlacmt(PCSPAssessmentDto pcspAssessmentDto, PCSPPersonDto pcspPersonDto, Long eventId) {
		Long idPcspPlcmnt = 0l;
		PcspPlcmnt pcspPlcmntEntity = new PcspPlcmnt();
		Date date = new Date();
		PcspAsmnt pcspAsmnt = new PcspAsmnt();
		Person person = new Person();
		Date maxDate = ServiceConstants.GENERIC_END_DATE;
		Criteria pcspAsmntCriteria = sessionFactory.getCurrentSession().createCriteria(PcspAsmnt.class);
		pcspAsmntCriteria.add(Restrictions.eq("idPcspAsmnt", pcspAssessmentDto.getIdPcspAsmnt()));
		pcspAsmnt = (PcspAsmnt) pcspAsmntCriteria.uniqueResult();
		Criteria personCriteria = sessionFactory.getCurrentSession().createCriteria(Person.class);
		personCriteria.add(Restrictions.eq("idPerson", pcspPersonDto.getIdPerson()));
		person = (Person) personCriteria.uniqueResult();
		pcspPlcmntEntity.setPerson(person);
		pcspPlcmntEntity.setPcspAsmnt(pcspAsmnt);
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(pcspAssessmentDto.getLoggedInUser())))
			pcspPlcmntEntity.setIdCreatedPerson(pcspAssessmentDto.getLoggedInUser());
		pcspPlcmntEntity.setIdLastUpdatePerson(pcspAssessmentDto.getLoggedInUser());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(pcspPersonDto.getIdPerson())))
			person.setIdPerson(pcspPersonDto.getIdPerson());
		pcspPlcmntEntity.setPerson(person);
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(pcspAssessmentDto.getIdPcspAsmnt())))
			pcspAsmnt.setIdPcspAsmnt(pcspAssessmentDto.getIdPcspAsmnt());
		pcspPlcmntEntity.setPcspAsmnt(pcspAsmnt);
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(eventId)))
			pcspPlcmntEntity.setIdEvent(eventId);
		if (!TypeConvUtil.isNullOrEmpty(pcspAssessmentDto.getDtPlcmnt()))
			pcspPlcmntEntity.setDtStart(pcspAssessmentDto.getDtPlcmnt());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(pcspAssessmentDto.getIdStage())))
			pcspPlcmntEntity.setIdStageInitd(pcspAssessmentDto.getIdStage());
		pcspPlcmntEntity.setDtEnd(maxDate);
		pcspPlcmntEntity.setDtCreated(date);
		pcspPlcmntEntity.setDtLastUpdate(date);
		sessionFactory.getCurrentSession().persist(pcspPlcmntEntity);
		idPcspPlcmnt = pcspPlcmntEntity.getIdPcspPlcmnt();
		return idPcspPlcmnt;
	}

	/**
	 * This Method is used to delete all the pcsp assessment related information
	 * 
	 * @param assessmentId
	 * @param idPerson
	 * @return String @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String deletePcspAssmntDetials(PCSPAssessmentDto pcspAssessmentDto) {
		PcspAsmnt pcspAsmntEntity = new PcspAsmnt();
		Event eventEntity = new Event();
		String rtnMsg = "";
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(PcspAsmnt.class)
				.add(Restrictions.eq("idPcspAsmnt", pcspAssessmentDto.getIdPcspAsmnt()));
		pcspAsmntEntity = (PcspAsmnt) cr.uniqueResult();
		Criteria crEvent = sessionFactory.getCurrentSession().createCriteria(Event.class)
				.add(Restrictions.eq("idEvent", pcspAssessmentDto.getIdEvent()));
		eventEntity = (Event) crEvent.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(pcspAsmntEntity)) {
			sessionFactory.getCurrentSession().delete(pcspAsmntEntity);
		} else {
			throw new PcspAssessmentNotFoundException(pcspAsmntEntity.getIdPcspAsmnt());
		}
		if (!TypeConvUtil.isNullOrEmpty(eventEntity)) {
			sessionFactory.getCurrentSession().delete(eventEntity);
		} else {
			throw new EventDetailsNotFoundException(eventEntity.getIdEvent());
		}
		rtnMsg = ServiceConstants.SUCCESS;
		return rtnMsg;
	}

	/**
	 * This Method is used to check if the Child selected has an active Legal
	 * Status.
	 * 
	 * @param idPerson
	 * @return Boolean @
	 */
	@Override
	public Boolean getshowLegalWarning(Long idPerson) {
		PCSPPersonDto pcspPersonDto = new PCSPPersonDto();
		Boolean isShowLeagalWarning = Boolean.FALSE;
		pcspPersonDto = (PCSPPersonDto) sessionFactory.getCurrentSession().createSQLQuery(showLegalWarningSql)
				.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING)
				.addScalar("cdDischargeRsn", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PCSPPersonDto.class)).uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(pcspPersonDto)) {
			if (!TypeConvUtil.isNullOrEmpty(pcspPersonDto.getCdLegalStatStatus())) {
				if (!TypeConvUtil.isNullOrEmpty(pcspPersonDto.getCdDischargeRsn())) {
					isShowLeagalWarning = Boolean.FALSE;
				} else {
					isShowLeagalWarning = Boolean.TRUE;
				}
			}
		}
		return isShowLeagalWarning;
	}

	/**
	 * This Method is used to check whether primary address exists for this
	 * person.
	 * 
	 * @param idPerson
	 * @return Boolean @
	 */
	@Override
	public Boolean getaddressExists(Long idPerson) {
		Boolean isAddressExists = Boolean.FALSE;
		BigDecimal recExists = (BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(addressExistsSql)
				.setParameter("idPerson", idPerson).uniqueResult();
		if (!TypeConvUtil.isNullOrEmptyBdDecm(recExists)) {
			isAddressExists = (recExists.longValue() > 0) ? true : false;
		}
		return isAddressExists;
	}

	/**
	 * This Method is used to check whether primary phone number exists for this
	 * person.
	 * 
	 * @param idPerson
	 * @return Boolean @
	 */
	@Override
	public Boolean getphoneExists(Long idPerson) {
		Boolean isPhoneExists = Boolean.FALSE;
		BigDecimal recExists = (BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(phoneExistsSql)
				.setParameter("idPerson", idPerson).uniqueResult();
		if (!TypeConvUtil.isNullOrEmptyBdDecm(recExists)) {
			isPhoneExists = (recExists.longValue() > 0) ? true : false;
		}
		return isPhoneExists;
	}
}
