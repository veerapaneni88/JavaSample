
package us.tx.state.dfps.service.childplan.daoimpl;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ChildPlan;
import us.tx.state.dfps.common.domain.ChildPlanParticip;
import us.tx.state.dfps.common.domain.CpAdoptnDtl;
import us.tx.state.dfps.common.domain.CpAdtnlSctnDtl;
import us.tx.state.dfps.common.domain.CpBhvrMgmt;
import us.tx.state.dfps.common.domain.CpChildFmlyTeamDtl;
import us.tx.state.dfps.common.domain.CpEductnDtl;
import us.tx.state.dfps.common.domain.CpEmtnlThrptcDtl;
import us.tx.state.dfps.common.domain.CpHlthCareSumm;
import us.tx.state.dfps.common.domain.CpInformation;
import us.tx.state.dfps.common.domain.CpIntlctlDvlpmntl;
import us.tx.state.dfps.common.domain.CpLglGrdnshp;
import us.tx.state.dfps.common.domain.CpLstGoal;
import us.tx.state.dfps.common.domain.CpPsychMedctnDtl;
import us.tx.state.dfps.common.domain.CpQrtpPtmParticipant;
import us.tx.state.dfps.common.domain.CpSoclRecrtnalDtl;
import us.tx.state.dfps.common.domain.CpSprvsnDtl;
import us.tx.state.dfps.common.domain.CpSvcsHghRiskBhvr;
import us.tx.state.dfps.common.domain.CpTranstnAdultAbvDtl;
import us.tx.state.dfps.common.domain.CpTranstnAdultBlwDtl;
import us.tx.state.dfps.common.domain.CpTrtmntSrvcDtl;
import us.tx.state.dfps.common.domain.CpVisitCntctFmly;
import us.tx.state.dfps.common.domain.CpQrtpPrmnTmMtng;
import us.tx.state.dfps.common.domain.CpYouthPregntPrntg;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.childplan.dao.ChildPlanDtlDao;
import us.tx.state.dfps.service.childplan.dto.*;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.request.ChildPlanDtlReq;
import us.tx.state.dfps.service.common.response.ChildPlanDtlRes;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * will interact with the database to save, save&submit, delete child plan
 * detail information and execute the stored procedure to fetch the data. May 4,
 * 2018- 10:26:42 AM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class ChildPlanDtlDaoImpl implements ChildPlanDtlDao {
	private static final String AND = " and ";
	private static final String CAMA = ",";
	private static final String AUTISM_SPECTRUM_DISORDER = "Autism Spectrum Disorder";
	private static final String INTELLECTUAL_DISABILITY = "Intellectual Disability";
	private static final String PRIMARY_MEDICAL_NEEDS = "Primary Medical Needs";
	private static final String EMOTIONAL_DISORDERS = "Emotional Disorders";
	public static final String TOPICS_CCID = "CCID";
	public static final String TOPICS_CEDU = "CEDU";
	public static final String TOPICS_CETP = "CETP";
	public static final String TOPICS_CBMG = "CBMG";
	public static final String TOPICS_CHSR = "CHSR";
	public static final String TOPICS_CSUP = "CSUP";
	public static final String TOPICS_CCSR = "CCSR";
	public static final String TOPICS_CU13 = "CU13";
	public static final String TOPICS_CU14 = "CU14";
	public static final String TOPICS_CCTS = "CCTS";

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	PostEventService postEventService;

	@Autowired
	CodesDao codesDao;

	@Value("${ChildPlanDtlDaoImpl.getPrefillReadOnlyInfo}")
	public String getPrefillReadOnlyInfoSql;

	@Value("${ChildPlanDtlDaoImpl.getPrefillEditableInfo}")
	public String getPrefillEditableInfoSql;

	@Value("${ChildPlanDtlDaoImpl.youthOwnConsenter}")
	private String youthOwnConsenterSql;

	@Value("${ChildPlanDtlDaoImpl.getApprovedChildPlanId}")
	private String getApprovedChildPlanIdSql;

	//PPM 70054(FCL) - IMPACT Date Requirement for RCYFC Notification
	@Value("${ChildPlanDtlDaoImpl.getInitialBillOfRightsDateByStageId}")
	private String getInitialBillOfRightsDateByStageIdSql;

	@Value("${ChildPlanDtlDaoImpl.getReviewBillOfRightsDateByStageId}")
	private String getReviewBillOfRightsDateByStageIdSql;

	@Value("${ChildPlanDtlDaoImpl.getQrtpRecommendationsByStageId}")
	private String getQrtpRecommendationsByStageIdSql;

	@Value("${ChildPlanDtlDaoImpl.getSvcPkgDetails}")
	private String getSvcPkgDetails;


	@Autowired LookupDao lookupDao;
	
	private static final Logger LOG = Logger.getLogger(ChildPlanDtlDaoImpl.class);

	/**
	 * Method Name: getPriorAdptn Method Description: This method is used to
	 * retrieve prior adoption section.
	 * 
	 * @param idChildEvent
	 * @return List<ChildPlanPriorAdpInfoDto>
	 */
	@SuppressWarnings("unchecked")
	public List<ChildPlanPriorAdpInfoDto> getPriorAdptn(Long idChildPlanEvent) {
		List<ChildPlanPriorAdpInfoDto> childPlanPriorAdpInfoDtoList = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpAdoptnDtl.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpAdoptnDtl> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			childPlanPriorAdpInfoDtoList = new ArrayList<>(list.size());

			for (CpAdoptnDtl cpAdoptnDtl : list) {
				ChildPlanPriorAdpInfoDto childPlanPriorAdpInfoDto = new ChildPlanPriorAdpInfoDto();
				DozerBeanMapper mapper = new DozerBeanMapper();
				List<String> mappingFiles = Arrays.asList("globalMapping.xml",
						"dozermappingforChildPlanAdoptionDtl.xml");
				mapper.setMappingFiles(mappingFiles);
				mapper.map(cpAdoptnDtl, childPlanPriorAdpInfoDto);
				childPlanPriorAdpInfoDtoList.add(childPlanPriorAdpInfoDto);
			}
		}
		return childPlanPriorAdpInfoDtoList;
	}

	/**
	 * Method Name: getLegalGuardianship Method Description: This method is used
	 * to retrieve Legal Guardianship section.
	 * 
	 * @param idChildPlanEvent
	 * @return List<ChildPlanLegalGrdnshpDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ChildPlanLegalGrdnshpDto> getLegalGuardianship(Long idChildPlanEvent) {
		List<ChildPlanLegalGrdnshpDto> childPlanLegalGrdnshpDtoList = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpLglGrdnshp.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpLglGrdnshp> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			childPlanLegalGrdnshpDtoList = new ArrayList<>(list.size());
			for (CpLglGrdnshp cpLglGrdnshp : list) {
				ChildPlanLegalGrdnshpDto childPlanLegalGrdnshpDto = new ChildPlanLegalGrdnshpDto();
				DozerBeanMapper mapper = new DozerBeanMapper();
				List<String> mappingFiles = Arrays.asList("globalMapping.xml",
						"dozermappingforChildPlanLegalGuardian.xml");
				mapper.setMappingFiles(mappingFiles);
				mapper.map(cpLglGrdnshp, childPlanLegalGrdnshpDto);
				childPlanLegalGrdnshpDtoList.add(childPlanLegalGrdnshpDto);
			}
		}
		return childPlanLegalGrdnshpDtoList;
	}

	/**
	 * Method Name: getPlanVisitation Method Description: This method is used to
	 * retrieve child plan visitation section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanVisitationCnctFmlyDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanVisitationCnctFmlyDto getPlanVisitation(Long idChildPlanEvent) {
		ChildPlanVisitationCnctFmlyDto visitationCnctFmlyDto = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpVisitCntctFmly.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpVisitCntctFmly> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			visitationCnctFmlyDto = new ChildPlanVisitationCnctFmlyDto();
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml",
					"dozermappingforChildPlanVisitationCnctFmly.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), visitationCnctFmlyDto);
		}
		return visitationCnctFmlyDto;
	}

	/**
	 * Method Name: getQrtpPermanencyMtng Method Description: This method is used to
	 * retrieve QRTP Permanency section.
	 *
	 * @param idChildPlanEvent
	 * @return ChildPlanQrtpPrmnncyMeetingDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanQrtpPrmnncyMeetingDto getQrtpPermanencyMtng(Long idChildPlanEvent) {
		ChildPlanQrtpPrmnncyMeetingDto permanencyQrtpMeeting = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpQrtpPrmnTmMtng.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpQrtpPrmnTmMtng> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			permanencyQrtpMeeting = new ChildPlanQrtpPrmnncyMeetingDto();
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml",
					"dozermappingforChildPlanQrtpPtm.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), permanencyQrtpMeeting);
			permanencyQrtpMeeting.setChildPlanQrtpPtmParticipDtoList(getChildPlanQrtpPartcpts(permanencyQrtpMeeting.getIdCpQrtpPtm()));
		}
		return permanencyQrtpMeeting;
	}

	/**
	 * Method Name: getChildPlanQrtpPartcpts Method Description: This method
	 * retrieve record from the CP_QRTP_PARTICIP_LIST table
	 *
	 * @param idCpQrtpPtm
	 * @return List<ChildPlanQrtpPtmParticipDto>
	 */
	@SuppressWarnings("unchecked")
	private List<ChildPlanQrtpPtmParticipDto> getChildPlanQrtpPartcpts(Long idCpQrtpPtm) {
		List<ChildPlanQrtpPtmParticipDto> childPlanParticipDtoList = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpQrtpPtmParticipant.class);
		cr.add(Restrictions.eq("cpQrtpPrmnTmMtng.idCpQrtpPtm", idCpQrtpPtm));
		List<CpQrtpPtmParticipant> list = cr.list();
		if (!ObjectUtils.isEmpty(list)) {
			childPlanParticipDtoList = new ArrayList<>(list.size());

			for (CpQrtpPtmParticipant cpQrtpPtmParticipant : list) {
				ChildPlanQrtpPtmParticipDto childPlanParticipDto = new ChildPlanQrtpPtmParticipDto();
				DozerBeanMapper mapper = new DozerBeanMapper();
				List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanQrtpPtmParticip.xml");
				mapper.setMappingFiles(mappingFiles);
				mapper.map(cpQrtpPtmParticipant, childPlanParticipDto);
				childPlanParticipDtoList.add(childPlanParticipDto);
			}
		}
		return childPlanParticipDtoList;
	}

	/**
	 * Method Name: getIntellectualDevelop Method Description: This method is
	 * used to retrieve Intellectual development section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanIntellectualDevelopDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanIntellectualDevelopDto getIntellectualDevelop(Long idChildPlanEvent) {
		ChildPlanIntellectualDevelopDto intellectualDevelopDto = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpIntlctlDvlpmntl.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpIntlctlDvlpmntl> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			intellectualDevelopDto = new ChildPlanIntellectualDevelopDto();
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml",
					"dozermappingforChildPlanIntellectualDevelop.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), intellectualDevelopDto);
			intellectualDevelopDto.setChildPlanGoalDtoList(getGoalsByTopic(idChildPlanEvent, TOPICS_CCID));
		}
		return intellectualDevelopDto;
	}

	/**
	 * Method Name: getChildEducation Method Description:This method is used to
	 * retrieve education section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanEducationDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanEducationDto getChildEducation(Long idChildPlanEvent) {
		ChildPlanEducationDto childPlanEducationDto = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpEductnDtl.class);

		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpEductnDtl> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			childPlanEducationDto = new ChildPlanEducationDto();
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozerbeanmappingforChildEducation.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), childPlanEducationDto);
			childPlanEducationDto.setChildPlanGoalDtoList(getGoalsByTopic(idChildPlanEvent, TOPICS_CEDU));
		}
		return childPlanEducationDto;
	}

	/**
	 * Method Name: getGoalsByTopic
	 * 
	 * @param idChildPlanEvent
	 * @param cedu2
	 * @return List<ChildPlanGoalDto>
	 */
	@SuppressWarnings("unchecked")
	private List<ChildPlanGoalDto> getGoalsByTopic(Long idChildPlanEvent, String cedu) {
		List<ChildPlanGoalDto> childPlanGoalDtoList = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpLstGoal.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent))
				.add(Restrictions.eq("cdChildPlanTopics", cedu));
		List<CpLstGoal> list = cr.list();
		if (!ObjectUtils.isEmpty(list)) {
			childPlanGoalDtoList = new ArrayList<>(list.size());
			for (CpLstGoal cpLstGoal : list) {
				ChildPlanGoalDto childPlanGoalDto = new ChildPlanGoalDto();
				DozerBeanMapper mapper = new DozerBeanMapper();
				List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozerbeanmappingforChildPlanGoals.xml");
				mapper.setMappingFiles(mappingFiles);
				mapper.map(cpLstGoal, childPlanGoalDto);
				childPlanGoalDtoList.add(childPlanGoalDto);
			}
		}
		return childPlanGoalDtoList;
	}

	/**
	 * Method Name: getEmtnlThrptcDtl Method Description: This method is used to
	 * retrieve child's emotional, psysic, Thrpt section.
	 * 
	 * @param idChildPlanEvent
	 * @returnChildPlanEmtnlThrptcDtlDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanEmtnlThrptcDtlDto getEmtnlThrptcDtl(Long idChildPlanEvent) {
		ChildPlanEmtnlThrptcDtlDto emtnlThrptcDtlDto = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpEmtnlThrptcDtl.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpEmtnlThrptcDtl> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			emtnlThrptcDtlDto = new ChildPlanEmtnlThrptcDtlDto();
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml",
					"dozermappingforChildPlanEmtnlThrptcDtl.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), emtnlThrptcDtlDto);
			emtnlThrptcDtlDto.setChildPlanGoalDtoList(getGoalsByTopic(idChildPlanEvent, TOPICS_CETP));
		}
		return emtnlThrptcDtlDto;
	}

	/**
	 * Method Name: getBehaviorMgnt Method Description: This method is used to
	 * retrieve Behavior management section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPalnBehaviorMgntDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPalnBehaviorMgntDto getBehaviorMgnt(Long idChildPlanEvent) {
		ChildPalnBehaviorMgntDto behaviorMgntDto = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpBhvrMgmt.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpEmtnlThrptcDtl> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			behaviorMgntDto = new ChildPalnBehaviorMgntDto();
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPalnBehaviorMgnt.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), behaviorMgntDto);
			behaviorMgntDto.setChildPlanGoalDtoList(getGoalsByTopic(idChildPlanEvent, TOPICS_CBMG));
		}
		return behaviorMgntDto;
	}

	/**
	 * Method Name: getYouthParenting Method Description: This method is used to
	 * retrieve youth parenting section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanYouthParentingDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanYouthParentingDto getYouthParenting(Long idChildPlanEvent) {
		ChildPlanYouthParentingDto youthParentingDto = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpYouthPregntPrntg.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpYouthPregntPrntg> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			youthParentingDto = new ChildPlanYouthParentingDto();
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml",
					"dozermappingforChildPlanYouthParenting.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), youthParentingDto);
		}
		return youthParentingDto;
	}

	/**
	 * Method Name: getHealthCareSummary Method Description: This method is used
	 * to retrieve health care summary section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanHealthCareSummaryDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanHealthCareSummaryDto getHealthCareSummary(Long idChildPlanEvent, Long idStage) {
		ChildPlanHealthCareSummaryDto healthCareSummaryDto = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpHlthCareSumm.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpHlthCareSumm> list = cr.list();
		if (!ObjectUtils.isEmpty(list)) {
			healthCareSummaryDto = new ChildPlanHealthCareSummaryDto();
			CpHlthCareSumm cpHlthCareSumm = list.get(0);
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml",
					"dozermappingforChildPlanHealthCareSummary.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(cpHlthCareSumm, healthCareSummaryDto);

			healthCareSummaryDto=getPsychMedctnDtls(cpHlthCareSumm.getIdCpHlthCareSumm(),healthCareSummaryDto);

			healthCareSummaryDto.setChildPlanGoalDtoList(getGoalsByTopic(idChildPlanEvent, TOPICS_CHSR));
			//PROD defect #13285 formatting of phone number removed in CPOS
			healthCareSummaryDto.setTxtPhoneNbrDent(healthCareSummaryDto.getTxtPhoneNbrDent());
			healthCareSummaryDto.setNbrPhysicianPhone(healthCareSummaryDto.getNbrPhysicianPhone());
			healthCareSummaryDto.setNbrHospPhone(healthCareSummaryDto.getNbrHospPhone());
			healthCareSummaryDto.setTxtPhoneNbrTxMedChkp(healthCareSummaryDto.getTxtPhoneNbrTxMedChkp());
			healthCareSummaryDto.setNbrVisnPhone(healthCareSummaryDto.getNbrVisnPhone());
			healthCareSummaryDto.setNbrHearngScrngPhone(healthCareSummaryDto.getNbrHearngScrngPhone());
		}
		healthCareSummaryDto = isYouthOwnConsenter(idStage, healthCareSummaryDto);
		
		
		return healthCareSummaryDto;
	}

	/**
	 * 
	 * Method Name: isYouthOwnConsenter Method Description:The method is used to
	 * retreive consent information
	 * 
	 * @param idStage
	 * @param childPlanHealthCareSummaryDto
	 * @return ChildPlanHealthCareSummaryDto
	 */
	public ChildPlanHealthCareSummaryDto isYouthOwnConsenter(Long idStage,
			ChildPlanHealthCareSummaryDto childPlanHealthCareSummaryDto) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(youthOwnConsenterSql)
				.addScalar("commonRes", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CommonStringRes.class));

		CommonStringRes indYouthOwnConsenter = (CommonStringRes) query.uniqueResult();
		if (ServiceConstants.Y.equalsIgnoreCase(indYouthOwnConsenter.getCommonRes())) {
			if (ObjectUtils.isEmpty(childPlanHealthCareSummaryDto)) {
				childPlanHealthCareSummaryDto = new ChildPlanHealthCareSummaryDto();
			}
			childPlanHealthCareSummaryDto.setIndYouthOwnConsenter(indYouthOwnConsenter.getCommonRes());
		}
		return childPlanHealthCareSummaryDto;
	}

	/**
	 * 
	 * Method Name: getPsychMedctnDtls Method Description:This method is used to
	 * retrieve the psycology and medical details for the health care summary
	 * section
	 * 
	 * @param idHlthCareSumm
	 * @return List<ChidPlanPsychMedctnDtlDto>
	 */
	@SuppressWarnings("unchecked")
	private ChildPlanHealthCareSummaryDto getPsychMedctnDtls(Long idHlthCareSumm,ChildPlanHealthCareSummaryDto healthCareSummaryDto) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpPsychMedctnDtl.class);
		cr.add(Restrictions.eq("cpHlthCareSumm.idCpHlthCareSumm", idHlthCareSumm));
		List<ChidPlanPsychMedctnDtlDto> chidPlanPsychMedctnDtlDtoList = new ArrayList<>();
		List<ChidPlanPsychMedctnDtlDto> chidPlanNonPsychMedctnDtlDtoList = new ArrayList<>();
		List<CpPsychMedctnDtl> cpPsychMedctnDtlList = cr.list();
		if (!ObjectUtils.isEmpty(cpPsychMedctnDtlList)) {
			/*chidPlanPsychMedctnDtlDtoList = new ArrayList<>(cpPsychMedctnDtlList.size());*/
			List<String> mappingFilesSub = Arrays.asList("globalMapping.xml",
					"dozermappingforChidPlanPsychMedctnDtl.xml");
			DozerBeanMapper mapper = new DozerBeanMapper();
			mapper.setMappingFiles(mappingFilesSub);
			for (CpPsychMedctnDtl cpPsychMedctnDtl : cpPsychMedctnDtlList) {
				ChidPlanPsychMedctnDtlDto chidPlanPsychMedctnDtlDto = new ChidPlanPsychMedctnDtlDto();
				mapper.map(cpPsychMedctnDtl, chidPlanPsychMedctnDtlDto);
				if(ServiceConstants.PSY.equals(chidPlanPsychMedctnDtlDto.getCdMedctnType())){
					chidPlanPsychMedctnDtlDtoList.add(chidPlanPsychMedctnDtlDto);
				}else {
					chidPlanNonPsychMedctnDtlDtoList.add(chidPlanPsychMedctnDtlDto);
				}
				
			}
		}	
		healthCareSummaryDto.setChidPlanPsychMedctnDtlDtoList(chidPlanPsychMedctnDtlDtoList);
		healthCareSummaryDto.setChidPlanNonPsychMedctnDtlDtoList(chidPlanNonPsychMedctnDtlDtoList);
		return healthCareSummaryDto;
	}

	/**
	 * Method Name: getSupervision Method Description:This method is used to
	 * retrieve supervision section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanSupervisionDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanSupervisionDto getSupervision(Long idChildPlanEvent) {
		ChildPlanSupervisionDto supervisionDto = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpSprvsnDtl.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpSprvsnDtl> list = cr.list();
		if (!ObjectUtils.isEmpty(list)) {
			supervisionDto = new ChildPlanSupervisionDto();
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanSupervision.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), supervisionDto);
			supervisionDto.setChildPlanGoalDtoList(getGoalsByTopic(idChildPlanEvent, TOPICS_CSUP));
		}
		return supervisionDto;
	}

	/**
	 * Method Name: getSocialRecreational Method Description:This method is used
	 * to retrieve social recreational section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanSocialRecreationalDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanSocialRecreationalDto getSocialRecreational(Long idChildPlanEvent) {
		ChildPlanSocialRecreationalDto socialRecreationalDto = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpSoclRecrtnalDtl.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpSoclRecrtnalDtl> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			socialRecreationalDto = new ChildPlanSocialRecreationalDto();
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml",
					"dozermappingforChildPlanSocialRecreational.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), socialRecreationalDto);
			socialRecreationalDto.setChildPlanGoalDtoList(getGoalsByTopic(idChildPlanEvent, TOPICS_CCSR));
		}
		return socialRecreationalDto;
	}

	/**
	 * Method Name: getTransAdtltBlwDtl
	 * 
	 * Method Description:This method is used to retrieve transition adulthood
	 * below thirteen section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanTransAdtltBlwDtlDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanTransAdtltBlwDtlDto getTransAdtltBlwDtl(Long idChildPlanEvent) {
		ChildPlanTransAdtltBlwDtlDto transAdtltBlwDtlDto = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpTranstnAdultBlwDtl.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpTranstnAdultBlwDtl> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			transAdtltBlwDtlDto = new ChildPlanTransAdtltBlwDtlDto();
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml",
					"dozermappingforChildPlanTransAdtltBlwDtl.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), transAdtltBlwDtlDto);
			transAdtltBlwDtlDto.setChildPlanGoalDtoList(getGoalsByTopic(idChildPlanEvent, TOPICS_CU13));
		}
		return transAdtltBlwDtlDto;
	}

	/**
	 * Method Name: getTransAdltAbvDtl
	 * 
	 * Method Description: This method is used to retrieve transition adulthood
	 * above fourteen section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanTransAdltAbvDtlDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanTransAdltAbvDtlDto getTransAdltAbvDtl(Long idChildPlanEvent) {
		ChildPlanTransAdltAbvDtlDto transAdltAbvDtlDto = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpTranstnAdultAbvDtl.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpTranstnAdultAbvDtl> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			transAdltAbvDtlDto = new ChildPlanTransAdltAbvDtlDto();
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml",
					"dozermappingforChildPlanTransAdltAbvDtl.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), transAdltAbvDtlDto);
			transAdltAbvDtlDto.setChildPlanGoalDtoList(getGoalsByTopic(idChildPlanEvent, TOPICS_CU14));
		}
		return transAdltAbvDtlDto;
	}

	/**
	 * Method Name: getHighRiskServices Method Description: This method is used
	 * to retrieve high risk services section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanHighRiskServicesDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanHighRiskServicesDto getHighRiskServices(Long idChildPlanEvent) {
		ChildPlanHighRiskServicesDto highRiskServicesDto = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpSvcsHghRiskBhvr.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpSvcsHghRiskBhvr> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			highRiskServicesDto = new ChildPlanHighRiskServicesDto();
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml",
					"dozermappingforChildPlanHighRiskServices.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), highRiskServicesDto);
		}
		return highRiskServicesDto;
	}

	/**
	 * Method Name: getTreatmentService Method Description: This method is used
	 * to retrieve treatment services section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanTreatmentServiceDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanTreatmentServiceDto getTreatmentService(Long idChildPlanEvent) {
		ChildPlanTreatmentServiceDto treatmentServiceDto = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpTrtmntSrvcDtl.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpTrtmntSrvcDtl> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			treatmentServiceDto = new ChildPlanTreatmentServiceDto();
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml",
					"dozermappingforChildPlanTreatmentService.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), treatmentServiceDto);
			treatmentServiceDto.setChildPlanGoalDtoList(getGoalsByTopic(idChildPlanEvent, TOPICS_CCTS));
		}
		treatmentServiceDto = setTreatmentServiceType(treatmentServiceDto, idChildPlanEvent);

		return treatmentServiceDto;
	}

	/**
	 * 
	 * Method Name: setTreatmentServiceType Method Description: This method set
	 * the treatment service type for a child plan
	 * 
	 * @param treatmentServiceDto
	 * @param idChildPlanEvent
	 * @return ChildPlanTreatmentServiceDto
	 */
	private ChildPlanTreatmentServiceDto setTreatmentServiceType(ChildPlanTreatmentServiceDto treatmentServiceDto,
			Long idChildPlanEvent) {
		StringBuilder str = new StringBuilder();
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(ChildPlan.class);
		cr.add(Restrictions.eq("idChildPlanEvent", idChildPlanEvent));
		ChildPlan childPlan = (ChildPlan) cr.uniqueResult();

		if (!ObjectUtils.isEmpty(childPlan)) {
			if (ServiceConstants.Y.equalsIgnoreCase(childPlan.getIndTrtmntSvcsTypeEmdo())) {
				str.append(EMOTIONAL_DISORDERS);
			}
			if (ServiceConstants.Y.equalsIgnoreCase(childPlan.getIndTrtmntSvcsTypePmn())) {
				if (!ObjectUtils.isEmpty(str) && str.length() > 0) {
					str.append(CAMA);
				}
				str.append(PRIMARY_MEDICAL_NEEDS);
			}
			if (ServiceConstants.Y.equalsIgnoreCase(childPlan.getIndTrtmntSvcsTypeId())) {
				if (!ObjectUtils.isEmpty(str) && str.length() > 0) {
					str.append(CAMA);
				}
				str.append(INTELLECTUAL_DISABILITY);
			}
			if (ServiceConstants.Y.equalsIgnoreCase(childPlan.getIndTrtmntSvcsTypeAsd())) {
				if (!ObjectUtils.isEmpty(str) && str.length() > 0) {
					str.append(CAMA);
				}
				str.append(AUTISM_SPECTRUM_DISORDER);
			}

		}
		if (!ObjectUtils.isEmpty(str) && str.length() > 0) {
			if (ObjectUtils.isEmpty(treatmentServiceDto)) {
				treatmentServiceDto = new ChildPlanTreatmentServiceDto();
			}
			if (str.lastIndexOf(CAMA) > 0) {
				str.replace(str.lastIndexOf(CAMA), str.lastIndexOf(CAMA) + 1, AND);
			}
			treatmentServiceDto.setTxtTrtSrvcTypes(str.toString());
		}
		return treatmentServiceDto;
	}

	/**
	 * Method Name: getFmlyTeamPrtctpn Method Description: This method is used
	 * to retrieve family team participation section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanFmlyTeamPrtctpnDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanFmlyTeamPrtctpnDto getFmlyTeamPrtctpn(Long idChildPlanEvent) {
		ChildPlanFmlyTeamPrtctpnDto fmlyTeamPrtctpnDto = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpChildFmlyTeamDtl.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpTrtmntSrvcDtl> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			fmlyTeamPrtctpnDto = new ChildPlanFmlyTeamPrtctpnDto();
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml",
					"dozermappingforChildPlanFmlyTeamPrtctpn.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), fmlyTeamPrtctpnDto);
			fmlyTeamPrtctpnDto.setChildPlanPartcptDevDtoList(getChildPlanPartcpts(idChildPlanEvent));
			//PROD defect #13285 formatting of phone number removed in CPOS
			fmlyTeamPrtctpnDto.setNbrAtrnyPhone(fmlyTeamPrtctpnDto.getNbrAtrnyPhone());
			fmlyTeamPrtctpnDto.setNbrGrdnPhone(fmlyTeamPrtctpnDto.getNbrGrdnPhone());
			fmlyTeamPrtctpnDto.setNbrCswrkrPhone(fmlyTeamPrtctpnDto.getNbrCswrkrPhone());
		}
		
		return fmlyTeamPrtctpnDto;
	}

	/**
	 * Method Name: getChildPlanPartcpts Method Description: This method
	 * retrieve record from the child_plan_particp table
	 * 
	 * @param idChildPlanEvent
	 * @return List<ChildPlanParticipDto>
	 */
	@SuppressWarnings("unchecked")
	private List<ChildPlanParticipDto> getChildPlanPartcpts(Long idChildPlanEvent) {
		List<ChildPlanParticipDto> childPlanParticipDtoList = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(ChildPlanParticip.class);
		cr.add(Restrictions.eq("event.idEvent", idChildPlanEvent));
		List<ChildPlanParticip> list = cr.list();
		if (!ObjectUtils.isEmpty(list)) {
			childPlanParticipDtoList = new ArrayList<>(list.size());

			for (ChildPlanParticip childPlanParticip : list) {
				ChildPlanParticipDto childPlanParticipDto = new ChildPlanParticipDto();
				DozerBeanMapper mapper = new DozerBeanMapper();
				List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanParticip.xml");
				mapper.setMappingFiles(mappingFiles);
				mapper.map(childPlanParticip, childPlanParticipDto);
				childPlanParticipDtoList.add(childPlanParticipDto);
			}
		}
		return childPlanParticipDtoList;
	}

	/**
	 * Method Name: getAdtnlSctnDtl Method Description: This method is used to
	 * retrieve additional section details in child plan page
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanAdtnlSctnDtlDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanAdtnlSctnDtlDto getAdtnlSctnDtl(Long idChildPlanEvent) {
		ChildPlanAdtnlSctnDtlDto adtnlSctnDtlDto = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpAdtnlSctnDtl.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<CpTrtmntSrvcDtl> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			adtnlSctnDtlDto = new ChildPlanAdtnlSctnDtlDto();
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanAdtnlSctnDtl.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), adtnlSctnDtlDto);
		}
		return adtnlSctnDtlDto;
	}

	/**
	 * Method Name: getChildPlanInformation Method Description: This method is
	 * used to retrieve child plan information.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanInformationDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanInformationDto getChildPlanInformation(Long idChildPlanEvent) {
		ChildPlanInformationDto childPlanInformationDto = null;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpInformation.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		List<ChildPlanInformationDto> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			childPlanInformationDto = new ChildPlanInformationDto();
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml",
					"dozerbeanmappingforChildPlanInformation.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), childPlanInformationDto);
		}
		return childPlanInformationDto;
	}

	/**
	 * Method Name: getChildPlanDtlInfo Method Description:
	 * 
	 * @param childPlanOfServiceDtlDto
	 * @return ChildPlanOfServiceDtlDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanOfServiceDtlDto getChildPlanDtlInfo(Long idChildPlanEvent,
			ChildPlanOfServiceDtlDto childPlanOfServiceDtlDto) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(ChildPlan.class);
		cr.add(Restrictions.eq("idChildPlanEvent", idChildPlanEvent));
		List<ChildPlan> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozerbeanmappingforChildPlan.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(list.get(0), childPlanOfServiceDtlDto);
		}
		return childPlanOfServiceDtlDto;
	}

	/**
	 * Method Name: saveLegalGuardian Method Description: This method is used to
	 * save legal guardian section in child plan.
	 * 
	 * @param childPlanLegalGrdnshpDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveLegalGuardian(ChildPlanLegalGrdnshpDto childPlanLegalGrdnshpDto, String szUserId) {

		ChildPlanDtlRes childPlanDtlRes = null;
		if (!ObjectUtils.isEmpty(childPlanLegalGrdnshpDto.getIdCpLglGrdnshp())
				&& Boolean.TRUE.equals(childPlanLegalGrdnshpDto.getIndDelFlag())) {
			List<Long> deleteIds = new ArrayList<>();
			deleteIds.add(childPlanLegalGrdnshpDto.getIdCpLglGrdnshp());
			return deleteLegalGuardianship(deleteIds);
		}
		CpLglGrdnshp cpLglGrdnshp = null;
		if (!ObjectUtils.isEmpty(childPlanLegalGrdnshpDto.getIdCpLglGrdnshp())) {
			cpLglGrdnshp = (CpLglGrdnshp) sessionFactory.getCurrentSession().get(CpLglGrdnshp.class,
					childPlanLegalGrdnshpDto.getIdCpLglGrdnshp());
		}
		if (ObjectUtils.isEmpty(cpLglGrdnshp)) {
			cpLglGrdnshp = new CpLglGrdnshp();
		} else if (!ObjectUtils.isEmpty(childPlanLegalGrdnshpDto.getDtLastUpdate())
				&& childPlanLegalGrdnshpDto.getDtLastUpdate().getTime() != cpLglGrdnshp.getDtLastUpdate().getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;
		}

		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanLegalGuardian.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanLegalGrdnshpDto, cpLglGrdnshp);
		if (ObjectUtils.isEmpty(childPlanLegalGrdnshpDto.getIdCpLglGrdnshp())
				|| childPlanLegalGrdnshpDto.getIdCpLglGrdnshp() == ServiceConstants.ZERO) {
			cpLglGrdnshp.setDtCreated(new Date());
			cpLglGrdnshp.setDtLastUpdate(new Date());
			cpLglGrdnshp.setIdLastUpdatePerson(new Long(szUserId));
			cpLglGrdnshp.setIdCreatedPerson(new Long(szUserId));
		} else {
			cpLglGrdnshp.setDtLastUpdate(new Date());
			cpLglGrdnshp.setIdLastUpdatePerson(new Long(szUserId));
			cpLglGrdnshp.setIdCreatedPerson(new Long(szUserId));
		}
		sessionFactory.getCurrentSession().saveOrUpdate(cpLglGrdnshp);

		return childPlanDtlRes;
	}

	/**
	 * Method Name: savePriorAdoption Method Description: This method is used to
	 * save prior adoption section in child plan.
	 * 
	 * @param childPlanLegalGrdnshpDtoList
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes savePriorAdoption(ChildPlanPriorAdpInfoDto childPlanPriorAdpInfoDto, String szUserId) {

		ChildPlanDtlRes childPlanDtlRes = null;
		if (!ObjectUtils.isEmpty(childPlanPriorAdpInfoDto.getIdCpAdoptnDtl())
				&& Boolean.TRUE.equals(childPlanPriorAdpInfoDto.getIndDelFlag())) {
			List<Long> deleteIds = new ArrayList<>();
			deleteIds.add(childPlanPriorAdpInfoDto.getIdCpAdoptnDtl());
			return deleteAdoption(deleteIds);
		}

		CpAdoptnDtl cpAdoptnDtl = null;
		if (!ObjectUtils.isEmpty(childPlanPriorAdpInfoDto.getIdCpAdoptnDtl())) {

			cpAdoptnDtl = (CpAdoptnDtl) sessionFactory.getCurrentSession().get(CpAdoptnDtl.class,
					childPlanPriorAdpInfoDto.getIdCpAdoptnDtl());
		}

		if (ObjectUtils.isEmpty(cpAdoptnDtl)) {
			cpAdoptnDtl = new CpAdoptnDtl();
		} else if (!ObjectUtils.isEmpty(childPlanPriorAdpInfoDto.getDtLastUpdate())
				&& childPlanPriorAdpInfoDto.getDtLastUpdate().getTime() != cpAdoptnDtl.getDtLastUpdate().getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}

		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanAdoptionDtl.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanPriorAdpInfoDto, cpAdoptnDtl);
		if (ObjectUtils.isEmpty(childPlanPriorAdpInfoDto.getIdCpAdoptnDtl())
				|| childPlanPriorAdpInfoDto.getIdCpAdoptnDtl() == ServiceConstants.ZERO) {
			cpAdoptnDtl.setDtCreated(new Date());
			cpAdoptnDtl.setDtLastUpdate(new Date());
			cpAdoptnDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpAdoptnDtl.setIdCreatedPerson(new Long(szUserId));
			sessionFactory.getCurrentSession().save(cpAdoptnDtl);
		} else {
			cpAdoptnDtl.setDtLastUpdate(new Date());
			cpAdoptnDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpAdoptnDtl.setIdCreatedPerson(new Long(szUserId));
			sessionFactory.getCurrentSession().update(cpAdoptnDtl);
		}

		return childPlanDtlRes;
	}

	/**
	 * Method Name: savePlanVisitation Method Description: This method is used
	 * to save child visitation plan section in child plan.
	 * 
	 * @param childPlanVisitationCnctFmlyDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes savePlanVisitation(ChildPlanVisitationCnctFmlyDto childPlanVisitationCnctFmlyDto,
			String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpVisitCntctFmly cpVisitCntctFmly = null;
		if (!ObjectUtils.isEmpty(childPlanVisitationCnctFmlyDto.getIdCpVisitCntctFmly())) {
			cpVisitCntctFmly = (CpVisitCntctFmly) sessionFactory.getCurrentSession().get(CpVisitCntctFmly.class,
					childPlanVisitationCnctFmlyDto.getIdCpVisitCntctFmly());
		}
		else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpVisitCntctFmly.class);
			List<CpVisitCntctFmly> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", childPlanVisitationCnctFmlyDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) cpVisitCntctFmly = res.get(0);
		}
		if (ObjectUtils.isEmpty(cpVisitCntctFmly)) {
			cpVisitCntctFmly = new CpVisitCntctFmly();
		} else if (!ObjectUtils.isEmpty(childPlanVisitationCnctFmlyDto.getDtLastUpdate())
				&& childPlanVisitationCnctFmlyDto.getDtLastUpdate().getTime() != cpVisitCntctFmly.getDtLastUpdate()
						.getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml",
				"dozermappingforChildPlanVisitationCnctFmly.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanVisitationCnctFmlyDto, cpVisitCntctFmly);
		if (ObjectUtils.isEmpty(childPlanVisitationCnctFmlyDto.getIdCpVisitCntctFmly())
				|| childPlanVisitationCnctFmlyDto.getIdCpVisitCntctFmly() == ServiceConstants.ZERO) {
			cpVisitCntctFmly.setDtCreated(new Date());
			cpVisitCntctFmly.setDtLastUpdate(new Date());
			cpVisitCntctFmly.setIdLastUpdatePerson(new Long(szUserId));
			cpVisitCntctFmly.setIdCreatedPerson(new Long(szUserId));
		} else {
			cpVisitCntctFmly.setDtLastUpdate(new Date());
			cpVisitCntctFmly.setIdLastUpdatePerson(new Long(szUserId));
			cpVisitCntctFmly.setIdCreatedPerson(new Long(szUserId));
		}
		sessionFactory.getCurrentSession().saveOrUpdate(cpVisitCntctFmly);
		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveQrtpPermanencyMtng Method Description: This method is used
	 * to save child QRTP PTM section in child plan.
	 *
	 * @param childPlanQrtpPrmnncyMeetingDto
	 * @return ChildPlanDtlRes
	 */

	@Override
	public ChildPlanDtlRes saveQrtpPermanencyMtng(ChildPlanQrtpPrmnncyMeetingDto childPlanQrtpPrmnncyMeetingDto,
											  String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;

		if (ObjectUtils.isEmpty(childPlanQrtpPrmnncyMeetingDto.getIndCurrentQrtp())){
			childPlanQrtpPrmnncyMeetingDto.setIndCurrentQrtp(" ");
		}

		CpQrtpPrmnTmMtng cpQrtpPrmnTmMtng = null;
		if (!ObjectUtils.isEmpty(childPlanQrtpPrmnncyMeetingDto.getIdCpQrtpPtm())) {
			cpQrtpPrmnTmMtng = (CpQrtpPrmnTmMtng) sessionFactory.getCurrentSession().get(CpQrtpPrmnTmMtng.class,
					childPlanQrtpPrmnncyMeetingDto.getIdCpQrtpPtm());
		}
		else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpQrtpPrmnTmMtng.class);
			List<CpQrtpPrmnTmMtng> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", childPlanQrtpPrmnncyMeetingDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) cpQrtpPrmnTmMtng = res.get(0);
		}
		if (ObjectUtils.isEmpty(cpQrtpPrmnTmMtng)) {
			cpQrtpPrmnTmMtng = new CpQrtpPrmnTmMtng();
		} else if (!ObjectUtils.isEmpty(childPlanQrtpPrmnncyMeetingDto.getDtLastUpdate())
				&& childPlanQrtpPrmnncyMeetingDto.getDtLastUpdate().getTime() != cpQrtpPrmnTmMtng.getDtLastUpdate()
				.getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml",
				"dozermappingforChildPlanQrtpPtm.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanQrtpPrmnncyMeetingDto, cpQrtpPrmnTmMtng);
		if (ObjectUtils.isEmpty(childPlanQrtpPrmnncyMeetingDto.getIdCpQrtpPtm())
				|| childPlanQrtpPrmnncyMeetingDto.getIdCpQrtpPtm().equals(ServiceConstants.ZERO)) {
			cpQrtpPrmnTmMtng.setDtCreated(new Date());
			cpQrtpPrmnTmMtng.setDtLastUpdate(new Date());
			cpQrtpPrmnTmMtng.setIdLastUpdatePerson(Long.parseLong(szUserId));
			cpQrtpPrmnTmMtng.setIdCreatedPerson(Long.parseLong(szUserId));
		} else {
			cpQrtpPrmnTmMtng.setDtLastUpdate(new Date());
			cpQrtpPrmnTmMtng.setIdLastUpdatePerson(Long.parseLong(szUserId));
			cpQrtpPrmnTmMtng.setIdCreatedPerson(Long.parseLong(szUserId));
		}
		sessionFactory.getCurrentSession().saveOrUpdate(cpQrtpPrmnTmMtng);
		sessionFactory.getCurrentSession().flush();

		Long idCpQrtpPtm = null;
		if(ObjectUtils.isEmpty(childPlanQrtpPrmnncyMeetingDto.getIdCpQrtpPtm())){
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpQrtpPrmnTmMtng.class);
			List<CpQrtpPrmnTmMtng> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", childPlanQrtpPrmnncyMeetingDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) idCpQrtpPtm = res.get(0).getIdCpQrtpPtm();
		}else{
			idCpQrtpPtm = childPlanQrtpPrmnncyMeetingDto.getIdCpQrtpPtm();
		}

		// save qrtp participants list
		List<ChildPlanQrtpPtmParticipDto> childPlanQrtpParticipDtoList = childPlanQrtpPrmnncyMeetingDto
				.getChildPlanQrtpPtmParticipDtoList();
		if (!ObjectUtils.isEmpty(childPlanQrtpParticipDtoList) && ServiceConstants.BOOLEAN_TRUE.equalsIgnoreCase(childPlanQrtpPrmnncyMeetingDto.getIndCurrentQrtp())) {
			for (ChildPlanQrtpPtmParticipDto childPlanQrtpParticipDto : childPlanQrtpParticipDtoList) {
				if(!isBlankRow(childPlanQrtpParticipDto)){
					childPlanQrtpParticipDto.setIdCpQrtpPtm(idCpQrtpPtm);
					saveChildPlanQrtpParticip(childPlanQrtpParticipDto, szUserId);
				}
			}
		} else if (!ObjectUtils.isEmpty(childPlanQrtpParticipDtoList) && ServiceConstants.BOOLEAN_FALSE.equalsIgnoreCase(childPlanQrtpPrmnncyMeetingDto.getIndCurrentQrtp())) {
			deleteQrtpPtmParticipantsByPtmId(idCpQrtpPtm);
		}

		return childPlanDtlRes;
	}

	/**
	 * Method Name: deleteQrtpPtmParticipantsByPtmId Method Description: This method will delete
	 * the QRTP PTM participant details by cpQrtpPtm Id.
	 *
	 * @param idCpQrtpPtm
	 * @return ChildPlanDtlRes
	 */

	private ChildPlanDtlRes deleteQrtpPtmParticipantsByPtmId(Long idCpQrtpPtm) {
		ChildPlanDtlRes childPlanDtlRes = null;
			List<CpQrtpPtmParticipant> cpQrtpPtmParticipants = sessionFactory.getCurrentSession()
					.getNamedQuery("CpQrtpPtmParticipants.findByQrtpPtmId").setParameter("id",idCpQrtpPtm).list();
			if (!ObjectUtils.isEmpty(cpQrtpPtmParticipants)) {
				sessionFactory.getCurrentSession().getNamedQuery("CpQrtpPtmParticipants.deleteByQrtpPtmId")
				.setParameter("id", idCpQrtpPtm).executeUpdate();
			} else {
				childPlanDtlRes = new ChildPlanDtlRes();
				ErrorDto erorrDto = new ErrorDto();
				erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				childPlanDtlRes.setErrorDto(erorrDto);
				return childPlanDtlRes;
			}
		return childPlanDtlRes;
	}

	private boolean isBlankRow(ChildPlanQrtpPtmParticipDto dto) {
		return ObjectUtils.isEmpty(dto.getNmQrtpPartFull()) && ObjectUtils.isEmpty(dto.getSdsQrtpPartRelationship())
				&& ObjectUtils.isEmpty(dto.getCdQrtpPartNotifType()) && ObjectUtils.isEmpty(dto.getCdAttendance())
				&& ObjectUtils.isEmpty(dto.getDtQrtpPartParticipate());
	}

	/**
	 * Method Name: saveChildPlanQrtpParticip Method Description: This method will
	 * save or update the data in child plan QRTP PTM Participant table.
	 *
	 * @param childPlanQrtpParticipDto
	 * @return ChildPlanDtlRes
	 */
	private ChildPlanDtlRes saveChildPlanQrtpParticip(ChildPlanQrtpPtmParticipDto childPlanQrtpParticipDto, String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpQrtpPtmParticipant qrtpPtmParticip = null;
		if (!ObjectUtils.isEmpty(childPlanQrtpParticipDto.getIdCpQrtpParticip())) {
			qrtpPtmParticip = (CpQrtpPtmParticipant) sessionFactory.getCurrentSession().get(CpQrtpPtmParticipant.class,
					childPlanQrtpParticipDto.getIdCpQrtpParticip());
		}
		if (ObjectUtils.isEmpty(qrtpPtmParticip)) {
			qrtpPtmParticip = new CpQrtpPtmParticipant();
		} else if (!ObjectUtils.isEmpty(childPlanQrtpParticipDto.getDtLastUpdate())
				&& childPlanQrtpParticipDto.getDtLastUpdate().getTime() != qrtpPtmParticip.getDtLastUpdate().getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanQrtpPtmParticip.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanQrtpParticipDto, qrtpPtmParticip);
		if (ObjectUtils.isEmpty(childPlanQrtpParticipDto.getIdCpQrtpParticip())
				|| childPlanQrtpParticipDto.getIdCpQrtpParticip().equals(ServiceConstants.ZERO)) {
			qrtpPtmParticip.setDtCreated(new Date());
			qrtpPtmParticip.setDtLastUpdate(new Date());
			qrtpPtmParticip.setIdLastUpdatePerson(Long.parseLong(szUserId));
			qrtpPtmParticip.setIdCreatedPerson(Long.parseLong(szUserId));
		} else {
			qrtpPtmParticip.setDtLastUpdate(new Date());
			qrtpPtmParticip.setIdLastUpdatePerson(Long.parseLong(szUserId));
			qrtpPtmParticip.setIdCreatedPerson(childPlanQrtpParticipDto.getIdCreatedPerson());
		}
		sessionFactory.getCurrentSession().saveOrUpdate(qrtpPtmParticip);

		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveIntellectualDevelop Method Description: This method is
	 * used to save intellectual development section in child plan.
	 * 
	 * @param childPlanIntellectualDevelopDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveIntellectualDevelop(ChildPlanIntellectualDevelopDto childPlanIntellectualDevelopDto,
			String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpIntlctlDvlpmntl cpIntlctlDvlpmntl = null;
		if (!ObjectUtils.isEmpty(childPlanIntellectualDevelopDto.getIdCpIntlctlDvlpmntl())) {
			cpIntlctlDvlpmntl = (CpIntlctlDvlpmntl) sessionFactory.getCurrentSession().get(CpIntlctlDvlpmntl.class,
					childPlanIntellectualDevelopDto.getIdCpIntlctlDvlpmntl());
		}
		else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpIntlctlDvlpmntl.class);
			List<CpIntlctlDvlpmntl> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", childPlanIntellectualDevelopDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) cpIntlctlDvlpmntl = res.get(0);
		}
		if (ObjectUtils.isEmpty(cpIntlctlDvlpmntl)) {
			cpIntlctlDvlpmntl = new CpIntlctlDvlpmntl();
		} else if (!ObjectUtils.isEmpty(childPlanIntellectualDevelopDto.getDtLastUpdate())
				&& childPlanIntellectualDevelopDto.getDtLastUpdate().getTime() != cpIntlctlDvlpmntl.getDtLastUpdate()
						.getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml",
				"dozermappingforChildPlanIntellectualDevelop.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanIntellectualDevelopDto, cpIntlctlDvlpmntl);
		if (ObjectUtils.isEmpty(childPlanIntellectualDevelopDto.getIdCpIntlctlDvlpmntl())
				|| childPlanIntellectualDevelopDto.getIdCpIntlctlDvlpmntl() == ServiceConstants.ZERO) {
			cpIntlctlDvlpmntl.setDtCreated(new Date());
			cpIntlctlDvlpmntl.setDtLastUpdate(new Date());
			cpIntlctlDvlpmntl.setIdLastUpdatePerson(new Long(szUserId));
			cpIntlctlDvlpmntl.setIdCreatedPerson(new Long(szUserId));
		} else {
			cpIntlctlDvlpmntl.setDtLastUpdate(new Date());
			cpIntlctlDvlpmntl.setIdLastUpdatePerson(new Long(szUserId));
			cpIntlctlDvlpmntl.setIdCreatedPerson(new Long(szUserId));
		}
		sessionFactory.getCurrentSession().saveOrUpdate(cpIntlctlDvlpmntl);
		List<ChildPlanGoalDto> childPlanGoalsList = childPlanIntellectualDevelopDto.getChildPlanGoalDtoList();
		if (!ObjectUtils.isEmpty(childPlanGoalsList)) {
			for (ChildPlanGoalDto childPlanGoalDto : childPlanGoalsList) {
				childPlanGoalDto.setIdChildPlanEvent(childPlanIntellectualDevelopDto.getIdChildPlanEvent());
				saveGoals(childPlanGoalDto, TOPICS_CCID, szUserId);
			}
		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveEducation Method Description: This method is used to sae
	 * the education section in child plan.
	 * 
	 * @param childPlanEducationDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveEducation(ChildPlanEducationDto childPlanEducationDto, String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpEductnDtl cpEductnDtl = null;
		if (!ObjectUtils.isEmpty(childPlanEducationDto.getIdEductnDtl())) {
			cpEductnDtl = (CpEductnDtl) sessionFactory.getCurrentSession().get(CpEductnDtl.class,
					childPlanEducationDto.getIdEductnDtl());
		}
		else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpEductnDtl.class);
			List<CpEductnDtl> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", childPlanEducationDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) cpEductnDtl = res.get(0);
		}
		if (ObjectUtils.isEmpty(cpEductnDtl)) {
			cpEductnDtl = new CpEductnDtl();
		} else if (!ObjectUtils.isEmpty(childPlanEducationDto.getDtLastUpdate())
				&& !ObjectUtils.isEmpty(cpEductnDtl.getDtLastUpdate())
				&& childPlanEducationDto.getDtLastUpdate().getTime() != cpEductnDtl.getDtLastUpdate().getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozerbeanmappingforChildEducation.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanEducationDto, cpEductnDtl);
		if (ObjectUtils.isEmpty(childPlanEducationDto.getIdEductnDtl())
				|| childPlanEducationDto.getIdEductnDtl() == ServiceConstants.ZERO) {
			cpEductnDtl.setDtCreated(new Date());
			cpEductnDtl.setDtLastUpdate(new Date());
			cpEductnDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpEductnDtl.setIdCreatedPerson(new Long(szUserId));
		} else {
			cpEductnDtl.setDtLastUpdate(new Date());
			cpEductnDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpEductnDtl.setIdCreatedPerson(new Long(szUserId));
		}
		sessionFactory.getCurrentSession().saveOrUpdate(cpEductnDtl);
		List<ChildPlanGoalDto> childPlanGoalsList = childPlanEducationDto.getChildPlanGoalDtoList();
		if (!ObjectUtils.isEmpty(childPlanGoalsList)) {
			for (ChildPlanGoalDto childPlanGoalDto : childPlanGoalsList) {
				childPlanGoalDto.setIdChildPlanEvent(childPlanEducationDto.getIdChildPlanEvent());
				saveGoals(childPlanGoalDto, TOPICS_CEDU, szUserId);
			}
		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveEmtnlThrptcDtl Method Description: This method is used
	 * to save emotional, physic, thrpt section in child plan.
	 * 
	 * @param childPlanEmtnlThrptcDtlDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveEmtnlThrptcDtl(ChildPlanEmtnlThrptcDtlDto childPlanEmtnlThrptcDtlDto, String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpEmtnlThrptcDtl cpEmtnlThrptcDtl = null;
		if (!ObjectUtils.isEmpty(childPlanEmtnlThrptcDtlDto.getIdEmtnlTpDtl())) {
			cpEmtnlThrptcDtl = (CpEmtnlThrptcDtl) sessionFactory.getCurrentSession().get(CpEmtnlThrptcDtl.class,
					childPlanEmtnlThrptcDtlDto.getIdEmtnlTpDtl());
		}
		else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpEmtnlThrptcDtl.class);
			List<CpEmtnlThrptcDtl> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", childPlanEmtnlThrptcDtlDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) cpEmtnlThrptcDtl = res.get(0);
		}
		if (ObjectUtils.isEmpty(cpEmtnlThrptcDtl)) {
			cpEmtnlThrptcDtl = new CpEmtnlThrptcDtl();
		} else if (!ObjectUtils.isEmpty(childPlanEmtnlThrptcDtlDto.getDtLastUpdate()) && childPlanEmtnlThrptcDtlDto
				.getDtLastUpdate().getTime() != cpEmtnlThrptcDtl.getDtLastUpdate().getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanEmtnlThrptcDtl.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanEmtnlThrptcDtlDto, cpEmtnlThrptcDtl);
		if (ObjectUtils.isEmpty(childPlanEmtnlThrptcDtlDto.getIdEmtnlTpDtl())
				|| childPlanEmtnlThrptcDtlDto.getIdEmtnlTpDtl() == ServiceConstants.ZERO) {
			cpEmtnlThrptcDtl.setDtCreated(new Date());
			cpEmtnlThrptcDtl.setDtLastUpdate(new Date());
			cpEmtnlThrptcDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpEmtnlThrptcDtl.setIdCreatedPerson(new Long(szUserId));
		} else {
			cpEmtnlThrptcDtl.setDtLastUpdate(new Date());
			cpEmtnlThrptcDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpEmtnlThrptcDtl.setIdCreatedPerson(new Long(szUserId));
		}
		if (!ObjectUtils.isEmpty(cpEmtnlThrptcDtl.getTxtPsychlgclRecmndtn())
				&& cpEmtnlThrptcDtl.getTxtPsychlgclRecmndtn().length() > 4000) {
			cpEmtnlThrptcDtl.setTxtPsychlgclRecmndtn(cpEmtnlThrptcDtl.getTxtPsychlgclRecmndtn().substring(0, 4000));
		}
		sessionFactory.getCurrentSession().saveOrUpdate(cpEmtnlThrptcDtl);
		List<ChildPlanGoalDto> childPlanGoalsList = childPlanEmtnlThrptcDtlDto.getChildPlanGoalDtoList();
		if (!ObjectUtils.isEmpty(childPlanGoalsList)) {
			for (ChildPlanGoalDto childPlanGoalDto : childPlanGoalsList) {
				childPlanGoalDto.setIdChildPlanEvent(childPlanEmtnlThrptcDtlDto.getIdChildPlanEvent());
				saveGoals(childPlanGoalDto, TOPICS_CETP, szUserId);
			}
		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveBehaviorMgnt Method Description: This method is used to
	 * save behavior management section
	 * 
	 * @param childPalnBehaviorMgntDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveBehaviorMgnt(ChildPalnBehaviorMgntDto childPalnBehaviorMgntDto, String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpBhvrMgmt cpBhvrMgmt = null;
		if (!ObjectUtils.isEmpty(childPalnBehaviorMgntDto.getIdBhvrMgmt())) {
			cpBhvrMgmt = (CpBhvrMgmt) sessionFactory.getCurrentSession().get(CpBhvrMgmt.class,
					childPalnBehaviorMgntDto.getIdBhvrMgmt());
		}
		else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpBhvrMgmt.class);
			List<CpBhvrMgmt> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", childPalnBehaviorMgntDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) cpBhvrMgmt = res.get(0);
		}
		if (ObjectUtils.isEmpty(cpBhvrMgmt)) {
			cpBhvrMgmt = new CpBhvrMgmt();
		} else if (!ObjectUtils.isEmpty(childPalnBehaviorMgntDto.getDtLastUpdate())
				&& !ObjectUtils.isEmpty(cpBhvrMgmt.getDtLastUpdate())
				&& childPalnBehaviorMgntDto.getDtLastUpdate().getTime() != cpBhvrMgmt.getDtLastUpdate().getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPalnBehaviorMgnt.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPalnBehaviorMgntDto, cpBhvrMgmt);
		if (ObjectUtils.isEmpty(childPalnBehaviorMgntDto.getIdBhvrMgmt())
				|| childPalnBehaviorMgntDto.getIdBhvrMgmt() == ServiceConstants.ZERO) {
			cpBhvrMgmt.setDtCreated(new Date());
			cpBhvrMgmt.setDtLastUpdate(new Date());
			cpBhvrMgmt.setIdLastUpdatePerson(new Long(szUserId));
			cpBhvrMgmt.setIdCreatedPerson(new Long(szUserId));
			sessionFactory.getCurrentSession().save(cpBhvrMgmt);
		} else {
			cpBhvrMgmt.setDtLastUpdate(new Date());
			cpBhvrMgmt.setIdLastUpdatePerson(new Long(szUserId));
			cpBhvrMgmt.setIdCreatedPerson(new Long(szUserId));
			sessionFactory.getCurrentSession().update(cpBhvrMgmt);
		}

		List<ChildPlanGoalDto> childPlanGoalsList = childPalnBehaviorMgntDto.getChildPlanGoalDtoList();
		if (!ObjectUtils.isEmpty(childPlanGoalsList)) {
			for (ChildPlanGoalDto childPlanGoalDto : childPlanGoalsList) {
				childPlanGoalDto.setIdChildPlanEvent(childPalnBehaviorMgntDto.getIdChildPlanEvent());
				saveGoals(childPlanGoalDto, TOPICS_CBMG, szUserId);
			}
		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveYouthParenting Method Description: This method is used
	 * for saving youth parenting section in child plan.
	 * 
	 * @param childPlanYouthParentingDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveYouthParenting(ChildPlanYouthParentingDto childPlanYouthParentingDto, String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpYouthPregntPrntg cpYouthPregntPrntg = null;
		if (!ObjectUtils.isEmpty(childPlanYouthParentingDto.getIdYouthPregntPrntg())) {
			cpYouthPregntPrntg = (CpYouthPregntPrntg) sessionFactory.getCurrentSession().get(CpYouthPregntPrntg.class,
					childPlanYouthParentingDto.getIdYouthPregntPrntg());
		}
		else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpYouthPregntPrntg.class);
			List<CpYouthPregntPrntg> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", childPlanYouthParentingDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) cpYouthPregntPrntg = res.get(0);
		}
		if (ObjectUtils.isEmpty(cpYouthPregntPrntg)) {
			cpYouthPregntPrntg = new CpYouthPregntPrntg();
		} else if (!ObjectUtils.isEmpty(childPlanYouthParentingDto.getDtLastUpdate())
				&& !ObjectUtils.isEmpty(cpYouthPregntPrntg.getDtLastUpdate()) && childPlanYouthParentingDto
						.getDtLastUpdate().getTime() != cpYouthPregntPrntg.getDtLastUpdate().getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanYouthParenting.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanYouthParentingDto, cpYouthPregntPrntg);
		if (ObjectUtils.isEmpty(childPlanYouthParentingDto.getIdYouthPregntPrntg())
				|| childPlanYouthParentingDto.getIdYouthPregntPrntg() == ServiceConstants.ZERO) {
			cpYouthPregntPrntg.setDtCreated(new Date());
			cpYouthPregntPrntg.setDtLastUpdate(new Date());
			cpYouthPregntPrntg.setIdLastUpdatePerson(new Long(szUserId));
			cpYouthPregntPrntg.setIdCreatedPerson(new Long(szUserId));
		} else {
			cpYouthPregntPrntg.setDtLastUpdate(new Date());
			cpYouthPregntPrntg.setIdLastUpdatePerson(new Long(szUserId));
			cpYouthPregntPrntg.setIdCreatedPerson(new Long(szUserId));
		}
		sessionFactory.getCurrentSession().saveOrUpdate(cpYouthPregntPrntg);
		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveHealthCareSummary Method Description: This method is
	 * used to save the health care summary details in child plan.
	 * 
	 * @param childPlanHealthCareSummaryDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveHealthCareSummary(ChildPlanHealthCareSummaryDto childPlanHealthCareSummaryDto,
			String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpHlthCareSumm cpHlthCareSumm = null;
		if (!ObjectUtils.isEmpty(childPlanHealthCareSummaryDto.getIdHlthCareSumm())) {
			cpHlthCareSumm = (CpHlthCareSumm) sessionFactory.getCurrentSession().get(CpHlthCareSumm.class,
					childPlanHealthCareSummaryDto.getIdHlthCareSumm());
		}
		else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpHlthCareSumm.class);
			List<CpHlthCareSumm> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", childPlanHealthCareSummaryDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) cpHlthCareSumm = res.get(0);
		}
		if (ObjectUtils.isEmpty(cpHlthCareSumm)) {
			cpHlthCareSumm = new CpHlthCareSumm();
		} else if (!ObjectUtils.isEmpty(childPlanHealthCareSummaryDto.getDtLastUpdate())
				&& childPlanHealthCareSummaryDto.getDtLastUpdate().getTime() != cpHlthCareSumm.getDtLastUpdate()
						.getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanHealthCareSummary.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanHealthCareSummaryDto, cpHlthCareSumm);
		if (ObjectUtils.isEmpty(childPlanHealthCareSummaryDto.getIdHlthCareSumm())
				|| childPlanHealthCareSummaryDto.getIdHlthCareSumm() == ServiceConstants.ZERO) {
			cpHlthCareSumm.setDtCreated(new Date());
			cpHlthCareSumm.setDtLastUpdate(new Date());
			cpHlthCareSumm.setIdLastUpdatePerson(new Long(szUserId));
			cpHlthCareSumm.setIdCreatedPerson(new Long(szUserId));
		} else {
			cpHlthCareSumm.setDtLastUpdate(new Date());
			cpHlthCareSumm.setIdLastUpdatePerson(new Long(szUserId));
			cpHlthCareSumm.setIdCreatedPerson(new Long(szUserId));
		}
		//PROD defect #13285 formatting of phone number removed in CPOS
		cpHlthCareSumm.setNbrHospPhone(cpHlthCareSumm.getNbrHospPhone());
		cpHlthCareSumm.setNbrHearngScrngPhone(cpHlthCareSumm.getNbrHearngScrngPhone());
		cpHlthCareSumm.setNbrPhysicnPhone(cpHlthCareSumm.getNbrPhysicnPhone());
		cpHlthCareSumm.setNbrVisnPhone(cpHlthCareSumm.getNbrVisnPhone());
		sessionFactory.getCurrentSession().saveOrUpdate(cpHlthCareSumm);
		// psych save includes non psy as well merged in the serviceimpl
		List<ChidPlanPsychMedctnDtlDto> chidPlanPsychMedctnDtlDtoList = childPlanHealthCareSummaryDto
				.getChidPlanPsychMedctnDtlDtoList();
		if (!ObjectUtils.isEmpty(chidPlanPsychMedctnDtlDtoList)) {
			for (ChidPlanPsychMedctnDtlDto chidPlanPsychMedctnDtlDto : chidPlanPsychMedctnDtlDtoList) {
				chidPlanPsychMedctnDtlDto.setIdCPHlthCareSumm(cpHlthCareSumm.getIdCpHlthCareSumm());
				savePsychMedctDtls(chidPlanPsychMedctnDtlDto, szUserId);
			}
		}
		List<ChildPlanGoalDto> childPlanGoalsList = childPlanHealthCareSummaryDto.getChildPlanGoalDtoList();
		if (!ObjectUtils.isEmpty(childPlanGoalsList)) {
			for (ChildPlanGoalDto childPlanGoalDto : childPlanGoalsList) {
				childPlanGoalDto.setIdChildPlanEvent(childPlanHealthCareSummaryDto.getIdChildPlanEvent());
				saveGoals(childPlanGoalDto, TOPICS_CHSR, szUserId);
			}
		}
		return childPlanDtlRes;

	}

	/**
	 * Method Name: savePsychMedctDtls Method Description:
	 * 
	 * @param childPlanHealthCareSummaryDto
	 * @param cpHlthCareSumm
	 * @param mapper
	 * @return ChildPlanDtlRes
	 */
	private ChildPlanDtlRes savePsychMedctDtls(ChidPlanPsychMedctnDtlDto chidPlanPsychMedctnDtlDto, String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpPsychMedctnDtl cpPsychMedctnDtl = null;
		if (!ObjectUtils.isEmpty(chidPlanPsychMedctnDtlDto.getIdPsychMedctnDtl())) {
			cpPsychMedctnDtl = (CpPsychMedctnDtl) sessionFactory.getCurrentSession().get(CpPsychMedctnDtl.class,
					chidPlanPsychMedctnDtlDto.getIdPsychMedctnDtl());
		}
		if (ObjectUtils.isEmpty(cpPsychMedctnDtl)) {
			cpPsychMedctnDtl = new CpPsychMedctnDtl();
		} else if (!ObjectUtils.isEmpty(chidPlanPsychMedctnDtlDto.getDtLastUpdate()) && chidPlanPsychMedctnDtlDto
				.getDtLastUpdate().getTime() != cpPsychMedctnDtl.getDtLastUpdate().getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChidPlanPsychMedctnDtl.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(chidPlanPsychMedctnDtlDto, cpPsychMedctnDtl);
		if (ObjectUtils.isEmpty(chidPlanPsychMedctnDtlDto.getIdPsychMedctnDtl())
				|| chidPlanPsychMedctnDtlDto.getIdPsychMedctnDtl() == ServiceConstants.ZERO) {
			cpPsychMedctnDtl.setDtCreated(new Date());
			cpPsychMedctnDtl.setDtLastUpdate(new Date());
			cpPsychMedctnDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpPsychMedctnDtl.setIdCreatedPerson(new Long(szUserId));
			sessionFactory.getCurrentSession().save(cpPsychMedctnDtl);
		} else {
			cpPsychMedctnDtl.setDtLastUpdate(new Date());
			cpPsychMedctnDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpPsychMedctnDtl.setIdCreatedPerson(new Long(szUserId));
			sessionFactory.getCurrentSession().update(cpPsychMedctnDtl);
		}

		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveGoals Method Description:
	 * 
	 * @param childPlanHealthCareSummaryDto
	 * @param cpLstGoal
	 * @return ChildPlanDtlRes
	 */
	private ChildPlanDtlRes saveGoals(ChildPlanGoalDto childPlanGoalDto, String topic, String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		if (!ObjectUtils.isEmpty(childPlanGoalDto.getIdLstGoals())
				&& Boolean.TRUE.equals(childPlanGoalDto.getIndDelFlag())) {
			List<Long> deleteIds = new ArrayList<>();
			deleteIds.add(childPlanGoalDto.getIdLstGoals());
			deleteGoals(deleteIds);
		} else {
			CpLstGoal cpLstGoal = null;
			if (!ObjectUtils.isEmpty(childPlanGoalDto.getIdLstGoals())) {
				cpLstGoal = (CpLstGoal) sessionFactory.getCurrentSession().get(CpLstGoal.class,
						childPlanGoalDto.getIdLstGoals());
			}
			if (ObjectUtils.isEmpty(cpLstGoal)) {
				cpLstGoal = new CpLstGoal();
			} else if (!ObjectUtils.isEmpty(childPlanGoalDto.getDtLastUpdate())
					&& childPlanGoalDto.getDtLastUpdate().getTime() != cpLstGoal.getDtLastUpdate().getTime()) {
				childPlanDtlRes = new ChildPlanDtlRes();
				ErrorDto erorrDto = new ErrorDto();
				erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				childPlanDtlRes.setErrorDto(erorrDto);
				return childPlanDtlRes;

			}
			DozerBeanMapper mapper = new DozerBeanMapper();
			List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozerbeanmappingforChildPlanGoals.xml");
			mapper.setMappingFiles(mappingFiles);
			mapper.map(childPlanGoalDto, cpLstGoal);
			cpLstGoal.setCdChildPlanTopics(topic);
			if (ObjectUtils.isEmpty(childPlanGoalDto.getIdLstGoals())
					|| childPlanGoalDto.getIdLstGoals() == ServiceConstants.ZERO) {
				cpLstGoal.setDtCreated(new Date());
				cpLstGoal.setDtLastUpdate(new Date());
				cpLstGoal.setIdLastUpdatePerson(new Long(szUserId));
				cpLstGoal.setIdCreatedPerson(new Long(szUserId));
			} else {
				cpLstGoal.setDtLastUpdate(new Date());
				cpLstGoal.setIdLastUpdatePerson(new Long(szUserId));
				cpLstGoal.setIdCreatedPerson(new Long(szUserId));
			}
			sessionFactory.getCurrentSession().saveOrUpdate(cpLstGoal);
		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveSupervision Method Description: This method is used to
	 * save supervision section in child plan.
	 * 
	 * @param childPlanSupervisionDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveSupervision(ChildPlanSupervisionDto childPlanSupervisionDto, String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpSprvsnDtl cpSprvsnDtl = null;
		if (!ObjectUtils.isEmpty(childPlanSupervisionDto.getIdCpSprvsnDtl())) {
			cpSprvsnDtl = (CpSprvsnDtl) sessionFactory.getCurrentSession().get(CpSprvsnDtl.class,
					childPlanSupervisionDto.getIdCpSprvsnDtl());
		}
		else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpSprvsnDtl.class);
			List<CpSprvsnDtl> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", childPlanSupervisionDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) cpSprvsnDtl = res.get(0);
		}
		if (ObjectUtils.isEmpty(cpSprvsnDtl)) {
			cpSprvsnDtl = new CpSprvsnDtl();
		} else if (!ObjectUtils.isEmpty(childPlanSupervisionDto.getDtLastUpdate())
				&& childPlanSupervisionDto.getDtLastUpdate().getTime() != cpSprvsnDtl.getDtLastUpdate().getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanSupervision.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanSupervisionDto, cpSprvsnDtl);
		if (ObjectUtils.isEmpty(childPlanSupervisionDto.getIdCpSprvsnDtl())
				|| childPlanSupervisionDto.getIdCpSprvsnDtl() == ServiceConstants.ZERO) {
			cpSprvsnDtl.setDtCreated(new Date());
			cpSprvsnDtl.setDtLastUpdate(new Date());
			cpSprvsnDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpSprvsnDtl.setIdCreatedPerson(new Long(szUserId));
		} else {
			cpSprvsnDtl.setDtLastUpdate(new Date());
			cpSprvsnDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpSprvsnDtl.setIdCreatedPerson(new Long(szUserId));
		}
		sessionFactory.getCurrentSession().saveOrUpdate(cpSprvsnDtl);
		List<ChildPlanGoalDto> childPlanGoalsList = childPlanSupervisionDto.getChildPlanGoalDtoList();
		if (!ObjectUtils.isEmpty(childPlanGoalsList)) {
			for (ChildPlanGoalDto childPlanGoalDto : childPlanGoalsList) {
				childPlanGoalDto.setIdChildPlanEvent(childPlanSupervisionDto.getIdChildPlanEvent());
				saveGoals(childPlanGoalDto, TOPICS_CSUP, szUserId);
			}
		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveSocialRecreational Method Description: This method is
	 * used to save social recreational section.
	 * 
	 * @param childPlanSocialRecreationalDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveSocialRecreational(ChildPlanSocialRecreationalDto childPlanSocialRecreationalDto,
			String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpSoclRecrtnalDtl cpSoclRecrtnalDtl = null;
		if (!ObjectUtils.isEmpty(childPlanSocialRecreationalDto.getIdSoclRecrtnalDtl())) {
			cpSoclRecrtnalDtl = (CpSoclRecrtnalDtl) sessionFactory.getCurrentSession().get(CpSoclRecrtnalDtl.class,
					childPlanSocialRecreationalDto.getIdSoclRecrtnalDtl());
		}
		else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpSoclRecrtnalDtl.class);
			List<CpSoclRecrtnalDtl> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", childPlanSocialRecreationalDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) cpSoclRecrtnalDtl = res.get(0);
		}
		if (ObjectUtils.isEmpty(cpSoclRecrtnalDtl)) {
			cpSoclRecrtnalDtl = new CpSoclRecrtnalDtl();
		} else if (!ObjectUtils.isEmpty(childPlanSocialRecreationalDto.getDtLastUpdate())
				&& childPlanSocialRecreationalDto.getDtLastUpdate().getTime() != cpSoclRecrtnalDtl.getDtLastUpdate()
						.getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml",
				"dozermappingforChildPlanSocialRecreational.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanSocialRecreationalDto, cpSoclRecrtnalDtl);
		if (ObjectUtils.isEmpty(childPlanSocialRecreationalDto.getIdSoclRecrtnalDtl())
				|| childPlanSocialRecreationalDto.getIdSoclRecrtnalDtl() == ServiceConstants.ZERO) {
			cpSoclRecrtnalDtl.setDtCreated(new Date());
			cpSoclRecrtnalDtl.setDtLastUpdate(new Date());
			cpSoclRecrtnalDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpSoclRecrtnalDtl.setIdCreatedPerson(new Long(szUserId));
		} else {
			cpSoclRecrtnalDtl.setDtLastUpdate(new Date());
			cpSoclRecrtnalDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpSoclRecrtnalDtl.setIdCreatedPerson(new Long(szUserId));
		}
		sessionFactory.getCurrentSession().saveOrUpdate(cpSoclRecrtnalDtl);
		List<ChildPlanGoalDto> childPlanGoalsList = childPlanSocialRecreationalDto.getChildPlanGoalDtoList();
		if (!ObjectUtils.isEmpty(childPlanGoalsList)) {
			for (ChildPlanGoalDto childPlanGoalDto : childPlanGoalsList) {
				childPlanGoalDto.setIdChildPlanEvent(childPlanSocialRecreationalDto.getIdChildPlanEvent());
				saveGoals(childPlanGoalDto, TOPICS_CCSR, szUserId);
			}
		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveTransAdulthoodBelowThirteen Method Description: This
	 * method is used to save Transition of adulthood below thirteen section.
	 * 
	 * @param transAdulthoodBelowThirteenDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveTransAdulthoodBelowThirteen(ChildPlanTransAdtltBlwDtlDto transAdulthoodBelowThirteenDto,
			String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpTranstnAdultBlwDtl cpTranstnAdultBlwDtl = null;
		if (!ObjectUtils.isEmpty(transAdulthoodBelowThirteenDto.getIdTtsAdultBlwDtl())) {
			cpTranstnAdultBlwDtl = (CpTranstnAdultBlwDtl) sessionFactory.getCurrentSession()
					.get(CpTranstnAdultBlwDtl.class, transAdulthoodBelowThirteenDto.getIdTtsAdultBlwDtl());
		}
		else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpTranstnAdultBlwDtl.class);
			List<CpTranstnAdultBlwDtl> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", transAdulthoodBelowThirteenDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) cpTranstnAdultBlwDtl = res.get(0);
		}
		if (ObjectUtils.isEmpty(cpTranstnAdultBlwDtl)) {
			cpTranstnAdultBlwDtl = new CpTranstnAdultBlwDtl();
		} else if (!ObjectUtils.isEmpty(transAdulthoodBelowThirteenDto.getDtLastUpdate())
				&& transAdulthoodBelowThirteenDto.getDtLastUpdate().getTime() != cpTranstnAdultBlwDtl.getDtLastUpdate()
						.getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanTransAdtltBlwDtl.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(transAdulthoodBelowThirteenDto, cpTranstnAdultBlwDtl);
		if (ObjectUtils.isEmpty(transAdulthoodBelowThirteenDto.getIdTtsAdultBlwDtl())
				|| transAdulthoodBelowThirteenDto.getIdTtsAdultBlwDtl() == ServiceConstants.ZERO) {
			cpTranstnAdultBlwDtl.setDtCreated(new Date());
			cpTranstnAdultBlwDtl.setDtLastUpdate(new Date());
			cpTranstnAdultBlwDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpTranstnAdultBlwDtl.setIdCreatedPerson(new Long(szUserId));
		} else {
			cpTranstnAdultBlwDtl.setDtLastUpdate(new Date());
			cpTranstnAdultBlwDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpTranstnAdultBlwDtl.setIdCreatedPerson(new Long(szUserId));
		}
		sessionFactory.getCurrentSession().saveOrUpdate(cpTranstnAdultBlwDtl);
		List<ChildPlanGoalDto> childPlanGoalsList = transAdulthoodBelowThirteenDto.getChildPlanGoalDtoList();
		if (!ObjectUtils.isEmpty(childPlanGoalsList)) {
			for (ChildPlanGoalDto childPlanGoalDto : childPlanGoalsList) {
				childPlanGoalDto.setIdChildPlanEvent(transAdulthoodBelowThirteenDto.getIdChildPlanEvent());
				saveGoals(childPlanGoalDto, TOPICS_CU13, szUserId);
			}
		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveTransAdulthoodAboveFourteen Method Description: This
	 * method is used to save Transition of adulthood above fourteen section.
	 * 
	 * @param transAdulthoodAboveFourteenDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveTransAdulthoodAboveFourteen(ChildPlanTransAdltAbvDtlDto transAdulthoodAboveFourteenDto,
			String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpTranstnAdultAbvDtl cpTranstnAdultAbvDtl = null;
		if (!ObjectUtils.isEmpty(transAdulthoodAboveFourteenDto.getIdTtsAdultAbvDtl())) {
			cpTranstnAdultAbvDtl = (CpTranstnAdultAbvDtl) sessionFactory.getCurrentSession()
					.get(CpTranstnAdultAbvDtl.class, transAdulthoodAboveFourteenDto.getIdTtsAdultAbvDtl());
		}
		else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpTranstnAdultAbvDtl.class);
			List<CpTranstnAdultAbvDtl> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", transAdulthoodAboveFourteenDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) cpTranstnAdultAbvDtl = res.get(0);
		}
		if (ObjectUtils.isEmpty(cpTranstnAdultAbvDtl)) {
			cpTranstnAdultAbvDtl = new CpTranstnAdultAbvDtl();
		} else if (!ObjectUtils.isEmpty(transAdulthoodAboveFourteenDto.getDtLastUpdate())
				&& transAdulthoodAboveFourteenDto.getDtLastUpdate().getTime() != cpTranstnAdultAbvDtl.getDtLastUpdate()
						.getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanTransAdltAbvDtl.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(transAdulthoodAboveFourteenDto, cpTranstnAdultAbvDtl);
		if (ObjectUtils.isEmpty(transAdulthoodAboveFourteenDto.getIdTtsAdultAbvDtl())
				|| transAdulthoodAboveFourteenDto.getIdTtsAdultAbvDtl() == ServiceConstants.ZERO) {
			cpTranstnAdultAbvDtl.setDtCreated(new Date());
			cpTranstnAdultAbvDtl.setDtLastUpdate(new Date());
			cpTranstnAdultAbvDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpTranstnAdultAbvDtl.setIdCreatedPerson(new Long(szUserId));
		} else {
			cpTranstnAdultAbvDtl.setDtLastUpdate(new Date());
			cpTranstnAdultAbvDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpTranstnAdultAbvDtl.setIdCreatedPerson(new Long(szUserId));
		}
		sessionFactory.getCurrentSession().saveOrUpdate(cpTranstnAdultAbvDtl);
		List<ChildPlanGoalDto> childPlanGoalsList = transAdulthoodAboveFourteenDto.getChildPlanGoalDtoList();
		if (!ObjectUtils.isEmpty(childPlanGoalsList)) {
			for (ChildPlanGoalDto childPlanGoalDto : childPlanGoalsList) {
				childPlanGoalDto.setIdChildPlanEvent(transAdulthoodAboveFourteenDto.getIdChildPlanEvent());
				saveGoals(childPlanGoalDto, TOPICS_CU14, szUserId);
			}
		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveHighRiskServices Method Description: This method is used
	 * to save the high risk services section.
	 * 
	 * @param childPlanHighRiskServicesDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveHighRiskServices(ChildPlanHighRiskServicesDto childPlanHighRiskServicesDto,
			String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpSvcsHghRiskBhvr cpSvcsHghRiskBhvr = null;
		if (!ObjectUtils.isEmpty(childPlanHighRiskServicesDto.getIdSvcsHghRiskBhvr())) {
			cpSvcsHghRiskBhvr = (CpSvcsHghRiskBhvr) sessionFactory.getCurrentSession().get(CpSvcsHghRiskBhvr.class,
					childPlanHighRiskServicesDto.getIdSvcsHghRiskBhvr());
		}
		else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpSvcsHghRiskBhvr.class);
			List<CpSvcsHghRiskBhvr> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", childPlanHighRiskServicesDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) cpSvcsHghRiskBhvr = res.get(0);
		}
		if (ObjectUtils.isEmpty(cpSvcsHghRiskBhvr)) {
			cpSvcsHghRiskBhvr = new CpSvcsHghRiskBhvr();
		} else if (!ObjectUtils.isEmpty(childPlanHighRiskServicesDto.getDtLastUpdate()) && childPlanHighRiskServicesDto
				.getDtLastUpdate().getTime() != cpSvcsHghRiskBhvr.getDtLastUpdate().getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanHighRiskServices.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanHighRiskServicesDto, cpSvcsHghRiskBhvr);
		if (ObjectUtils.isEmpty(childPlanHighRiskServicesDto.getIdSvcsHghRiskBhvr())
				|| childPlanHighRiskServicesDto.getIdSvcsHghRiskBhvr() == ServiceConstants.ZERO) {
			cpSvcsHghRiskBhvr.setDtCreated(new Date());
			cpSvcsHghRiskBhvr.setDtLastUpdate(new Date());
			cpSvcsHghRiskBhvr.setIdLastUpdatePerson(new Long(szUserId));
			cpSvcsHghRiskBhvr.setIdCreatedPerson(new Long(szUserId));
		} else {
			cpSvcsHghRiskBhvr.setDtLastUpdate(new Date());
			cpSvcsHghRiskBhvr.setIdLastUpdatePerson(new Long(szUserId));
			cpSvcsHghRiskBhvr.setIdCreatedPerson(new Long(szUserId));
		}
		sessionFactory.getCurrentSession().saveOrUpdate(cpSvcsHghRiskBhvr);
		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveTreatmentService Method Description: This method is used
	 * to save child plan treatment services section.
	 * 
	 * @param childPlanTreatmentServiceDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveTreatmentService(ChildPlanTreatmentServiceDto childPlanTreatmentServiceDto,
			String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpTrtmntSrvcDtl cpTrtmntSrvcDtl = null;
		if (!ObjectUtils.isEmpty(childPlanTreatmentServiceDto.getIdTrtmntSrvcDtl())) {
			cpTrtmntSrvcDtl = (CpTrtmntSrvcDtl) sessionFactory.getCurrentSession().get(CpTrtmntSrvcDtl.class,
					childPlanTreatmentServiceDto.getIdTrtmntSrvcDtl());
		}
		else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpTrtmntSrvcDtl.class);
			List<CpTrtmntSrvcDtl> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", childPlanTreatmentServiceDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) cpTrtmntSrvcDtl = res.get(0);
		}
		if (ObjectUtils.isEmpty(cpTrtmntSrvcDtl)) {
			cpTrtmntSrvcDtl = new CpTrtmntSrvcDtl();
		} else if (!ObjectUtils.isEmpty(childPlanTreatmentServiceDto.getDtLastUpdate()) && childPlanTreatmentServiceDto
				.getDtLastUpdate().getTime() != cpTrtmntSrvcDtl.getDtLastUpdate().getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanTreatmentService.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanTreatmentServiceDto, cpTrtmntSrvcDtl);
		if (ObjectUtils.isEmpty(childPlanTreatmentServiceDto.getIdTrtmntSrvcDtl())
				|| childPlanTreatmentServiceDto.getIdTrtmntSrvcDtl() == ServiceConstants.ZERO) {
			cpTrtmntSrvcDtl.setDtCreated(new Date());
			cpTrtmntSrvcDtl.setDtLastUpdate(new Date());
			cpTrtmntSrvcDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpTrtmntSrvcDtl.setIdCreatedPerson(new Long(szUserId));
		} else {
			cpTrtmntSrvcDtl.setDtLastUpdate(new Date());
			cpTrtmntSrvcDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpTrtmntSrvcDtl.setIdCreatedPerson(new Long(szUserId));
		}
		sessionFactory.getCurrentSession().saveOrUpdate(cpTrtmntSrvcDtl);
		List<ChildPlanGoalDto> childPlanGoalsList = childPlanTreatmentServiceDto.getChildPlanGoalDtoList();
		if (!ObjectUtils.isEmpty(childPlanGoalsList)) {
			for (ChildPlanGoalDto childPlanGoalDto : childPlanGoalsList) {
				childPlanGoalDto.setIdChildPlanEvent(childPlanTreatmentServiceDto.getIdChildPlanEvent());
				saveGoals(childPlanGoalDto, TOPICS_CCTS, szUserId);
			}
		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveFmlyTeamPrtctpn Method Description: This method is used
	 * to save child plan family team participation section.
	 * 
	 * @param childPlanFmlyTeamPrtctpnDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveFmlyTeamPrtctpn(ChildPlanFmlyTeamPrtctpnDto childPlanFmlyTeamPrtctpnDto,
			String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpChildFmlyTeamDtl cpChildFmlyTeamDtl = null;
		if (!ObjectUtils.isEmpty(childPlanFmlyTeamPrtctpnDto.getIdChildFmlyTeamDtl())) {
			cpChildFmlyTeamDtl = (CpChildFmlyTeamDtl) sessionFactory.getCurrentSession().get(CpChildFmlyTeamDtl.class,
					childPlanFmlyTeamPrtctpnDto.getIdChildFmlyTeamDtl());
		}
		else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpChildFmlyTeamDtl.class);
			List<CpChildFmlyTeamDtl> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", childPlanFmlyTeamPrtctpnDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) cpChildFmlyTeamDtl = res.get(0);
		}
		if (ObjectUtils.isEmpty(cpChildFmlyTeamDtl)) {
			cpChildFmlyTeamDtl = new CpChildFmlyTeamDtl();
		} else if (!ObjectUtils.isEmpty(childPlanFmlyTeamPrtctpnDto.getDtLastUpdate()) && childPlanFmlyTeamPrtctpnDto
				.getDtLastUpdate().getTime() != cpChildFmlyTeamDtl.getDtLastUpdate().getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanFmlyTeamPrtctpn.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanFmlyTeamPrtctpnDto, cpChildFmlyTeamDtl);
		if (ObjectUtils.isEmpty(childPlanFmlyTeamPrtctpnDto.getIdChildFmlyTeamDtl())
				|| childPlanFmlyTeamPrtctpnDto.getIdChildFmlyTeamDtl() == ServiceConstants.ZERO) {
			cpChildFmlyTeamDtl.setDtCreated(new Date());
			cpChildFmlyTeamDtl.setDtLastUpdate(new Date());
			cpChildFmlyTeamDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpChildFmlyTeamDtl.setIdCreatedPerson(new Long(szUserId));
		} else {
			cpChildFmlyTeamDtl.setDtLastUpdate(new Date());
			cpChildFmlyTeamDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpChildFmlyTeamDtl.setIdCreatedPerson(new Long(szUserId));
		}

		List<ChildPlanParticipDto> childPlanParticipDtoList = childPlanFmlyTeamPrtctpnDto
				.getChildPlanPartcptDevDtoList();
		if (!ObjectUtils.isEmpty(childPlanParticipDtoList)) {
			for (ChildPlanParticipDto childPlanParticipDto : childPlanParticipDtoList) {
				childPlanParticipDto.setIdChildPlanEvent(childPlanFmlyTeamPrtctpnDto.getIdChildPlanEvent());
				saveChildPlanParticip(childPlanParticipDto, szUserId);
			}
		}
		//PROD defect #13285 formatting of phone number removed in CPOS
		cpChildFmlyTeamDtl.setNbrAtrnyPhone(cpChildFmlyTeamDtl.getNbrAtrnyPhone());
		cpChildFmlyTeamDtl.setNbrCswrkrPhone(cpChildFmlyTeamDtl.getNbrCswrkrPhone());
		cpChildFmlyTeamDtl.setNbrGrdnPhone(cpChildFmlyTeamDtl.getNbrGrdnPhone());
		sessionFactory.getCurrentSession().saveOrUpdate(cpChildFmlyTeamDtl);
		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveChildPlanParticip Method Description: This method will
	 * save or update the data in child plan Participant table.
	 * 
	 * @param childPlanParticipDto
	 * @return ChildPlanDtlRes
	 */
	private ChildPlanDtlRes saveChildPlanParticip(ChildPlanParticipDto childPlanParticipDto, String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		ChildPlanParticip childPlanParticip = null;
		if (!ObjectUtils.isEmpty(childPlanParticipDto.getIdChildPlanPart())) {
			childPlanParticip = (ChildPlanParticip) sessionFactory.getCurrentSession().get(ChildPlanParticip.class,
					childPlanParticipDto.getIdChildPlanPart());
		}
		if (ObjectUtils.isEmpty(childPlanParticip)) {
			childPlanParticip = new ChildPlanParticip();
		} else if (!ObjectUtils.isEmpty(childPlanParticipDto.getDtLastUpdate())
				&& childPlanParticipDto.getDtLastUpdate().getTime() != childPlanParticip.getDtLastUpdate().getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanParticip.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanParticipDto, childPlanParticip);
		if (ObjectUtils.isEmpty(childPlanParticipDto.getIdChildPlanPart())
				|| childPlanParticipDto.getIdChildPlanPart() == ServiceConstants.ZERO) {
			childPlanParticip.setDtCreated(new Date());
			childPlanParticip.setDtLastUpdate(new Date());
			childPlanParticip.setIdLastUpdatePerson(new Long(szUserId));
			childPlanParticip.setIdCreatedPerson(new Long(szUserId));
		} else {
			childPlanParticip.setDtLastUpdate(new Date());
			childPlanParticip.setIdLastUpdatePerson(new Long(szUserId));
			childPlanParticip.setIdCreatedPerson(childPlanParticipDto.getIdCreatedPerson());
		}
		sessionFactory.getCurrentSession().saveOrUpdate(childPlanParticip);

		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveAdtnlSctnDtl. Method Description: This method will save
	 * or update the data in child plan additional section daetail table.
	 * 
	 * @param childPlanAdtnlSctnDtlDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveAdtnlSctnDtl(ChildPlanAdtnlSctnDtlDto childPlanAdtnlSctnDtlDto, String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpAdtnlSctnDtl cpAdtnlSctnDtl = null;
		if (!ObjectUtils.isEmpty(childPlanAdtnlSctnDtlDto.getIdCpAdtnlSctnDtls())) {
			cpAdtnlSctnDtl = (CpAdtnlSctnDtl) sessionFactory.getCurrentSession().get(CpAdtnlSctnDtl.class,
					childPlanAdtnlSctnDtlDto.getIdCpAdtnlSctnDtls());
		}
		if (ObjectUtils.isEmpty(cpAdtnlSctnDtl)) {
			cpAdtnlSctnDtl = new CpAdtnlSctnDtl();
		} else if (!ObjectUtils.isEmpty(childPlanAdtnlSctnDtlDto.getDtLastUpdate())
				&& childPlanAdtnlSctnDtlDto.getDtLastUpdate().getTime() != cpAdtnlSctnDtl.getDtLastUpdate().getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;
		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozermappingforChildPlanAdtnlSctnDtl.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanAdtnlSctnDtlDto, cpAdtnlSctnDtl);
		if (ObjectUtils.isEmpty(childPlanAdtnlSctnDtlDto.getIdCpAdtnlSctnDtls())
				|| childPlanAdtnlSctnDtlDto.getIdCpAdtnlSctnDtls() == ServiceConstants.ZERO) {
			cpAdtnlSctnDtl.setDtCreated(new Date());
			cpAdtnlSctnDtl.setDtLastUpdate(new Date());
			cpAdtnlSctnDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpAdtnlSctnDtl.setIdCreatedPerson(new Long(szUserId));
		} else {
			cpAdtnlSctnDtl.setDtLastUpdate(new Date());
			cpAdtnlSctnDtl.setIdLastUpdatePerson(new Long(szUserId));
			cpAdtnlSctnDtl.setIdCreatedPerson(new Long(szUserId));
		}
		sessionFactory.getCurrentSession().saveOrUpdate(cpAdtnlSctnDtl);
		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveChildPlanInfo Method Description: This method will save
	 * or update the data in child plan inforamtion table.
	 * 
	 * 
	 * @param childPlanInformationDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveChildPlanInfo(ChildPlanInformationDto childPlanInformationDto, String szUserId) {
		ChildPlanDtlRes childPlanDtlRes = null;
		CpInformation cpInformation = null;
		if (!ObjectUtils.isEmpty(childPlanInformationDto.getIdCpInformation())) {
			cpInformation = (CpInformation) sessionFactory.getCurrentSession().get(CpInformation.class,
					childPlanInformationDto.getIdCpInformation());
		}
		else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpInformation.class);
			List<CpInformation> res = cr.add(Restrictions.eq("childPlan.idChildPlanEvent", childPlanInformationDto.getIdChildPlanEvent())).list();
			if(!CollectionUtils.isEmpty(res)) cpInformation = res.get(0);
		}
		if (ObjectUtils.isEmpty(cpInformation)) {
			cpInformation = new CpInformation();
		} else if (!ObjectUtils.isEmpty(childPlanInformationDto.getDtLastUpdate())
				&& childPlanInformationDto.getDtLastUpdate().getTime() != cpInformation.getDtLastUpdate().getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;

		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozerbeanmappingforChildPlanInformation.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanInformationDto, cpInformation);
		if (ObjectUtils.isEmpty(childPlanInformationDto.getIdCpInformation())
				|| childPlanInformationDto.getIdCpInformation() == ServiceConstants.ZERO) {
			cpInformation.setDtCreated(new Date());
			cpInformation.setDtLastUpdate(new Date());
			cpInformation.setIdLastUpdatePerson(new Long(szUserId));
			cpInformation.setIdCreatedPerson(new Long(szUserId));
		} else {
			cpInformation.setDtLastUpdate(new Date());
			cpInformation.setIdLastUpdatePerson(new Long(szUserId));
			cpInformation.setIdCreatedPerson(new Long(szUserId));
		}
		sessionFactory.getCurrentSession().saveOrUpdate(cpInformation);
		return childPlanDtlRes;
	}

	/**
	 * Method Name: createAndReturnEventid Method Description:This method will
	 * create a new event for new child plan or update the event table for
	 * existing child plan and return the idEvent.
	 * 
	 * @param childPlanDtlReq
	 * @return Long
	 */
	public Long createAndReturnEventid(ChildPlanDtlReq childPlanDtlReq,boolean conservatorshipCreated) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		Date date = new Date(System.currentTimeMillis());
		ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
		postEventIPDto.setCdTask(ServiceConstants.CHILD_PLAN_TASK_CODE_SUB);
		postEventIPDto
				.setIdPerson(Long.valueOf(childPlanDtlReq.getUserId()));
		postEventIPDto
				.setIdStage(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdStage());
		postEventIPDto.setDtEventOccurred(date);
		postEventIPDto.setUserId(childPlanDtlReq.getUserId());
		if (ObjectUtils
				.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdChildPlanEvent())
				|| ServiceConstants.ZERO.equals(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto()
						.getIdChildPlanEvent())) {
			archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
			postEventIPDto.setCdEventStatus(CodesConstant.CEVTSTAT_PROC);
		}
		if (!ObjectUtils
				.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdChildPlanEvent())
				&& ServiceConstants.ZERO != childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto()
						.getIdChildPlanEvent()) {
			archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
			postEventIPDto.setIdEvent(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdChildPlanEvent());

			if (ServiceConstants.EVENTSTATUS_NEW.equalsIgnoreCase(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getCdEventStatus())) {
				if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getSaveAndSubmit())
						&& Boolean.TRUE.equals(childPlanDtlReq.getChildPlanOfServiceDto().getSaveAndSubmit())) {
					postEventIPDto.setCdEventStatus(CodesConstant.CEVTSTAT_COMP);
				} else {
					postEventIPDto.setCdEventStatus(CodesConstant.CEVTSTAT_PROC);
				}
			}

		} else {
			postEventIPDto.setDtEventOccurred(date);
		}
		postEventIPDto.setTsLastUpdate(date);

		postEventIPDto.setCdEventType(ServiceConstants.CEVNTTYP_CSP);
		postEventIPDto.setEventDescr(lookupDao.decode(CodesConstant.CEVNTTYP, ServiceConstants.CEVNTTYP_CSP));
		//added the code as the P2 child plan was not writing to event person link table
		if(!conservatorshipCreated){
		List<PostEventDto> postEventDtos = new ArrayList<>();
		PostEventDto postEventDto = new PostEventDto();
		postEventDto.setIdPerson(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdPerson());
		postEventDto.setCdScrDataAction(archInputDto.getReqFuncCd());			
		postEventDtos.add(postEventDto);
		postEventIPDto.setPostEventDto(postEventDtos);
		}
		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);
		return postEventOPDto.getIdEvent();
	}

	/**
	 * Method Name: saveChildPlanDtl. Method Description:This internal method is
	 * call to save or update the data in child plan table.
	 * 
	 * @param childPlanOfServiceDtlDto
	 * @param idUser
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes saveChildPlanDtl(ChildPlanOfServiceDtlDto childPlanOfServiceDtlDto, String idUser,
			Boolean externalUser) {
		ChildPlanDtlRes childPlanDtlRes = null;
		ChildPlan childPlan = null;
		if (!ObjectUtils.isEmpty(childPlanOfServiceDtlDto.getIdChildPlanEvent())) {
			childPlan = (ChildPlan) sessionFactory.getCurrentSession().get(ChildPlan.class,
					childPlanOfServiceDtlDto.getIdChildPlanEvent());
		}
		if (ObjectUtils.isEmpty(childPlan)) {
			childPlan = new ChildPlan();
		} else if (!ObjectUtils.isEmpty(childPlanOfServiceDtlDto.getDtLastUpdate())
				&& childPlanOfServiceDtlDto.getDtLastUpdate().getTime() != childPlan.getDtLastUpdate().getTime()) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			childPlanDtlRes.setErrorDto(erorrDto);
			return childPlanDtlRes;
		}
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<String> mappingFiles = Arrays.asList("globalMapping.xml", "dozerbeanmappingforChildPlan.xml");
		mapper.setMappingFiles(mappingFiles);
		mapper.map(childPlanOfServiceDtlDto, childPlan);
		childPlan.setDtLastUpdate(new Date());
		childPlan.setDtCreated(new Date());
		childPlan.setIdLastUpdatePerson(new Long(idUser));
		if (externalUser) {
			childPlan.setIdLastUpdateExtrnlPerson(new Long(idUser));
			childPlan.setDtLastUpdateExtrnl(new Date());
		} else {
			childPlan.setIdLastUpdateCpsPerson(new Long(idUser));
			childPlan.setDtLastUpdateCps(new Date());
		}

		sessionFactory.getCurrentSession().saveOrUpdate(childPlan);
		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().refresh(childPlan);
		return childPlanDtlRes;
	}

	/**
	 * Method Name: getPrefillReadOnlyInfo. Method Description:This internal
	 * method is call to populate the prefill read only information to childplan
	 * detail dto details after store procedure call.
	 * 
	 * @param childPlanOfServiceDtlDto
	 * @return ChildPlanOfServiceDto
	 */
	@Override
	public ChildPlanOfServiceDto getPrefillReadOnlyInfo(ChildPlanOfServiceDto childPlanOfServiceDto) {
		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		Connection connection;
		CallableStatement callStatement = null;
		ChildPlanOfServiceDtlDto childPlanOfServiceDtlDto = childPlanOfServiceDto.getChildPlanOfServiceDtlDto();
		ChildPlanHealthCareSummaryDto childPlanHealthCareSummaryDto = childPlanOfServiceDto
				.getChildPlanHealthCareSummaryDto();
		if (ObjectUtils.isEmpty(childPlanHealthCareSummaryDto)) {
			childPlanHealthCareSummaryDto = new ChildPlanHealthCareSummaryDto();
		}

		ChildPlanYouthParentingDto childPlanYouthParentingDto = childPlanOfServiceDto.getChildPlanYouthParentingDto();
		if (ObjectUtils.isEmpty(childPlanYouthParentingDto)) {
			childPlanYouthParentingDto = new ChildPlanYouthParentingDto();
		}

		ChildPlanInformationDto childPlanInformationDto = childPlanOfServiceDto.getChildPlanInformationDto();
		if (ObjectUtils.isEmpty(childPlanInformationDto)) {
			childPlanInformationDto = new ChildPlanInformationDto();
		}
		ChildPlanEducationDto childPlanEducationDto = childPlanOfServiceDto.getChildPlanEducationDto();
		if (ObjectUtils.isEmpty(childPlanEducationDto)) {
			childPlanEducationDto = new ChildPlanEducationDto();
		}
		ChildPlanEmtnlThrptcDtlDto childPlanEmtnlThrptcDtlDto = childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto();
		if (ObjectUtils.isEmpty(childPlanEmtnlThrptcDtlDto)) {
			childPlanEmtnlThrptcDtlDto = new ChildPlanEmtnlThrptcDtlDto();
		}

		ChildPlanHighRiskServicesDto childPlanHighRiskServicesDto = childPlanOfServiceDto
				.getChildPlanHighRiskServicesDto();
		if (ObjectUtils.isEmpty(childPlanHighRiskServicesDto)) {
			childPlanHighRiskServicesDto = new ChildPlanHighRiskServicesDto();
		}

		try {
			connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			String errorMsg = ServiceConstants.EMPTY_STRING;
			callStatement = connection.prepareCall(getPrefillReadOnlyInfoSql);

			callStatement.setLong(1, childPlanOfServiceDtlDto.getIdCase());
			callStatement.setLong(2, childPlanOfServiceDtlDto.getIdStage());
			callStatement.registerOutParameter(3, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(4, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(5, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(6, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(7, java.sql.Types.DATE);
			callStatement.registerOutParameter(8, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(9, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(10, java.sql.Types.VARCHAR);

			callStatement.registerOutParameter(11, java.sql.Types.NUMERIC);
			callStatement.registerOutParameter(12, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(13, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(14, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(15, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(16, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(17, java.sql.Types.DATE);
			callStatement.registerOutParameter(18, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(19, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(20, java.sql.Types.VARCHAR);

			callStatement.registerOutParameter(21, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(22, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(23, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(24, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(25, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(26, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(27, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(28, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(29, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(30, java.sql.Types.VARCHAR);

			callStatement.registerOutParameter(31, java.sql.Types.DATE);
			callStatement.registerOutParameter(32, java.sql.Types.DATE);
			callStatement.registerOutParameter(33, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(34, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(35, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(36, java.sql.Types.DATE);
			callStatement.registerOutParameter(37, java.sql.Types.DATE);
			callStatement.registerOutParameter(38, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(39, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(40, java.sql.Types.VARCHAR);

			callStatement.registerOutParameter(41, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(42, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(43, java.sql.Types.DATE);
			callStatement.registerOutParameter(44, java.sql.Types.DATE);
			callStatement.registerOutParameter(45, java.sql.Types.INTEGER);
			callStatement.registerOutParameter(46, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(47, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(48, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(49, java.sql.Types.NUMERIC);
			callStatement.registerOutParameter(50, java.sql.Types.NUMERIC);

			// input Child plan Event id
			if (!ObjectUtils.isEmpty(childPlanOfServiceDtlDto.getIdChildPlanEvent())){
				callStatement.setLong(51, childPlanOfServiceDtlDto.getIdChildPlanEvent());
			}else{
				callStatement.setLong(51, 0l);
			}
				
			callStatement.registerOutParameter(52, java.sql.Types.ARRAY, "TYPE_TXT_SX_AGGRSV_IDNTFD");

			callStatement.executeUpdate();

			errorMsg = callStatement.getString(46);
			if (!ObjectUtils.isEmpty(errorMsg)) {
				throw new SQLException("Error occured in stored proc :" + errorMsg);
			}

			childPlanInformationDto.setNmDfpsPrmryWorker(callStatement.getString(3));
			childPlanInformationDto.setIdDfpsPrmryWorker(callStatement.getLong(50));
			childPlanInformationDto.setNmDfpsSupervisor(callStatement.getString(4));
			childPlanInformationDto.setNmCaseWorkerUnit(callStatement.getString(5));
			childPlanInformationDto.setNmChild(callStatement.getString(6));
			childPlanInformationDto.setDtChildBirth(callStatement.getDate(7));
			childPlanInformationDto.setCdChildEthnicity(callStatement.getString(8));
			childPlanInformationDto.setCdChildGender(callStatement.getString(9));
			childPlanInformationDto.setCdChildRace(callStatement.getString(10));
			childPlanInformationDto.setIdPerson(callStatement.getLong(11));
			childPlanInformationDto.setCdChildLglReg(callStatement.getString(12));
			childPlanInformationDto.setCdChildLglCnty(callStatement.getString(13));

			childPlanOfServiceDtlDto.setCdChildLglStatus(callStatement.getString(14));
			childPlanOfServiceDtlDto.setCdCurrentLvlCare(callStatement.getString(15));

			childPlanOfServiceDtlDto.setDtStartLoc(callStatement.getDate(43));
			if (!ObjectUtils.isEmpty(callStatement.getDate(44))
					&& ServiceConstants.GENERIC_END_DATE.compareTo(callStatement.getDate(44)) != 0) {
				childPlanOfServiceDtlDto.setDtEndLoc(callStatement.getDate(44));
			}

			childPlanOfServiceDtlDto.setCdLvlCareType(callStatement.getString(16));
			childPlanOfServiceDtlDto.setDtPlcmt(callStatement.getDate(17));
			childPlanOfServiceDtlDto.setCdPlcmtTyp(callStatement.getString(18));
			childPlanOfServiceDtlDto.setNmCargvr(callStatement.getString(19));
			childPlanOfServiceDtlDto.setCdChildLglStatusSub(callStatement.getString(20));
			childPlanOfServiceDtlDto.setCdCspPlanType(callStatement.getString(42));
			childPlanOfServiceDtlDto.setIdPerson(callStatement.getLong(45));
			childPlanEducationDto.setNmSchoolDist(callStatement.getString(21));
			childPlanEducationDto.setNmSchool(callStatement.getString(22));
			childPlanEducationDto.setCdGrade(callStatement.getString(23));
			childPlanEducationDto.setIndChildRcvngSvcs(callStatement.getString(24));
			childPlanEducationDto.setIndChildEnrlldSch(callStatement.getString(47));
			childPlanEmtnlThrptcDtlDto.setIndChildCnfrmdSxTrfckng(callStatement.getString(25));
			childPlanEmtnlThrptcDtlDto.setIndChildSuspdSxTrfckng(callStatement.getString(26));
			childPlanEmtnlThrptcDtlDto.setIndChildCnfrmdLbrTrfckng(callStatement.getString(27));
			childPlanEmtnlThrptcDtlDto.setIndChildSuspdLbrTrfckng(callStatement.getString(28));
			childPlanYouthParentingDto.setIndChildCurrPergnt(callStatement.getString(29));
			childPlanYouthParentingDto.setIndChildGender(callStatement.getString(48));

			childPlanHealthCareSummaryDto.setNmPrimaryMedCons(callStatement.getString(30));
			// childPlanHealthCareSummaryDto.setNmClnclPrfsnlTxMedChkp(callStatement.getString(33));
			// childPlanHealthCareSummaryDto.setTxtAddressTxMedChkp(callStatement.getString(34));
			// childPlanHealthCareSummaryDto.setTxtPhoneNbrTxMedChkp(callStatement.getString(35));

			childPlanHighRiskServicesDto.setIndSxAggrsvIdentfd(callStatement.getString(41));
			if (ServiceConstants.INDICATOR_YES1.equals(childPlanHighRiskServicesDto.getIndSxAggrsvIdentfd())) {
				StringBuilder txtSxAggrsvIdentfdStrBuilder = new StringBuilder("");
				String pipe = "||";
				Array txtSxAggrsvIdentfdArray = callStatement.getArray(52);
	            if (txtSxAggrsvIdentfdArray != null) {
	            	List<String> txtSxAggrsvIdentfd = Arrays.asList((String[]) txtSxAggrsvIdentfdArray.getArray());
	            	txtSxAggrsvIdentfd.forEach(el ->{
						if(txtSxAggrsvIdentfdStrBuilder.length() != 0){
							txtSxAggrsvIdentfdStrBuilder.append("\n\n");
						}
						txtSxAggrsvIdentfdStrBuilder.append(el.replace(pipe, "\n").replace(",", "\n"));
			        });
	            }
				childPlanHighRiskServicesDto.setTxtSxAggrsvIdentfd(txtSxAggrsvIdentfdStrBuilder.toString());
			}
			if (ObjectUtils.isEmpty(childPlanOfServiceDto.getIdApprovedChildPlan()))
				childPlanOfServiceDto.setIdApprovedChildPlan(callStatement.getLong(49));
				childPlanOfServiceDto.setChildPlanInformationDto(childPlanInformationDto);
				childPlanOfServiceDto.setChildPlanOfServiceDtlDto(childPlanOfServiceDtlDto);
				childPlanOfServiceDto.setChildPlanEducationDto(childPlanEducationDto);
				childPlanOfServiceDto.setChildPlanEmtnlThrptcDtlDto(childPlanEmtnlThrptcDtlDto);
				childPlanOfServiceDto.setChildPlanYouthParentingDto(childPlanYouthParentingDto);
				childPlanOfServiceDto.setChildPlanHealthCareSummaryDto(childPlanHealthCareSummaryDto);
				childPlanOfServiceDto.setChildPlanHighRiskServicesDto(childPlanHighRiskServicesDto);
		} catch (SQLException sqlExp) {
			DataLayerException dataLayerException = new DataLayerException(sqlExp.getMessage());
			dataLayerException.initCause(sqlExp);
			throw dataLayerException;
		} finally {
			try {
				if (!ObjectUtils.isEmpty(callStatement)) {
					callStatement.close();
				}
			} catch (SQLException e) {
				LOG.error("Failed to close the connectionin getPrefillReadOnlyInfo.");
			}
		}
		return childPlanOfServiceDto;
	}

	/**
	 * Method Name: getPrefillEditableInfo Method Description:This internal
	 * method is call to populate the prefill edit only information to the
	 * childplan detail dto details after store procedure call .
	 * 
	 * @param childPlanOfServiceDto
	 * @return ChildPlanOfServiceDto
	 */
	@Override
	public ChildPlanOfServiceDto getPrefillEditableInfo(ChildPlanOfServiceDto childPlanOfServiceDto) {
		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		Connection connection;
		CallableStatement callStatement = null;
		ChildPlanOfServiceDtlDto childPlanOfServiceDtlDto = childPlanOfServiceDto.getChildPlanOfServiceDtlDto();
		ChildPlanVisitationCnctFmlyDto childPlanVisitationCnctFmlyDto = new ChildPlanVisitationCnctFmlyDto();
		ChildPlanQrtpPrmnncyMeetingDto childPlanQrtpPrmnncyMeetingDto = new ChildPlanQrtpPrmnncyMeetingDto();
		ChildPlanEducationDto childPlanEducationDto = new ChildPlanEducationDto();
		ChildPlanEmtnlThrptcDtlDto childPlanEmtnlThrptcDtlDto = new ChildPlanEmtnlThrptcDtlDto();
		ChildPlanFmlyTeamPrtctpnDto childPlanFmlyTeamPrtctpnDto = new ChildPlanFmlyTeamPrtctpnDto();
		ChildPlanYouthParentingDto childPlanYouthParentingDto = new ChildPlanYouthParentingDto();

		try {
			connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			String errorMsg = ServiceConstants.EMPTY_STRING;
			callStatement = connection.prepareCall(getPrefillEditableInfoSql);
			callStatement.setLong(1, childPlanOfServiceDtlDto.getIdCase());
			callStatement.setLong(2, childPlanOfServiceDtlDto.getIdStage());
			callStatement.registerOutParameter(3, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(4, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(5, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(6, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(7, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(8, java.sql.Types.DATE);
			callStatement.registerOutParameter(9, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(10, java.sql.Types.DATE);
			callStatement.registerOutParameter(11, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(12, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(13, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(14, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(15, java.sql.Types.VARCHAR);
			callStatement.executeUpdate();

			errorMsg = callStatement.getString(15);
			if (!ObjectUtils.isEmpty(errorMsg)) {
				throw new SQLException("Error occured in stored proc :" + errorMsg);
			}

			childPlanOfServiceDtlDto.setNmAgncy(callStatement.getString(3));

			childPlanVisitationCnctFmlyDto.setIndChildSib(callStatement.getString(4));
			//childPlanVisitationCnctFmlyDto.setTxtTypCntctApprvd(callStatement.getString(5));to be removed as no longer prefilled
			childPlanEducationDto.setTxtIepGoals(callStatement.getString(6));

			//childPlanEmtnlThrptcDtlDto.setIndChildCansAsmnt(callStatement.getString(7));

			//childPlanEmtnlThrptcDtlDto.setDtCansAssmt(callStatement.getDate(8));

			//childPlanEmtnlThrptcDtlDto.setNmClnclPrfsnl(callStatement.getString(9));

			Date psychDate = callStatement.getDate(10);
			childPlanEmtnlThrptcDtlDto.setDtPsych(psychDate);
			childPlanEmtnlThrptcDtlDto.setNmClnclPrfsnlPsych(callStatement.getString(11));

			childPlanYouthParentingDto.setIndYouthWithChild(callStatement.getString(12));
			//childPlanYouthParentingDto.setIndChildInDfpsCvs(callStatement.getString(13));not Prefill
			childPlanFmlyTeamPrtctpnDto.setNbrCswrkrPhone(callStatement.getString(14));

			childPlanOfServiceDto.setChildPlanOfServiceDtlDto(childPlanOfServiceDtlDto);
			childPlanOfServiceDto.setChildPlanVisitationCnctFmlyDto(childPlanVisitationCnctFmlyDto);
			childPlanOfServiceDto.setChildPlanQrtpPrmnncyMeetingDto(childPlanQrtpPrmnncyMeetingDto);
			childPlanOfServiceDto.setChildPlanEducationDto(childPlanEducationDto);
			//childPlanOfServiceDto.setChildPlanVisitationCnctFmlyDto(childPlanVisitationCnctFmlyDto);
			childPlanOfServiceDto.setChildPlanEmtnlThrptcDtlDto(childPlanEmtnlThrptcDtlDto);
			childPlanOfServiceDto.setChildPlanYouthParentingDto(childPlanYouthParentingDto);
			childPlanOfServiceDto.setChildPlanFmlyTeamPrtctpnDto(childPlanFmlyTeamPrtctpnDto);

		} catch (SQLException sqlExp) {
			DataLayerException dataLayerException = new DataLayerException(sqlExp.getMessage());
			dataLayerException.initCause(sqlExp);
			throw dataLayerException;
		} finally {
			try {
				if (!ObjectUtils.isEmpty(callStatement)) {
					callStatement.close();
				}
			} catch (SQLException e) {
				LOG.error("Failed to close the connection getPrefillEditableInfo.");
			}
		}
		return childPlanOfServiceDto;
	}

	/**
	 * Method Name: getAllChildPlanDetails Method Description:This internal
	 * method to populate the childplan detail dto details.
	 * 
	 * @param childPlanOfServiceDtlDto
	 * @param idChildPlanEvent
	 * @return ChildPlanOfServiceDto
	 */
	@Override
	public ChildPlanOfServiceDto getAllChildPlanDetails(Long idChildPlanEvent,
			ChildPlanOfServiceDto childPlanOfServiceDto) {
		List<ChildPlanPriorAdpInfoDto> priorAdptn = getPriorAdptn(idChildPlanEvent);

		processPriorAdoption(idChildPlanEvent, childPlanOfServiceDto, priorAdptn);

		List<ChildPlanLegalGrdnshpDto> legalGrardianshipList = getLegalGuardianship(idChildPlanEvent);
		ChildPlanVisitationCnctFmlyDto planVisitation = getPlanVisitation(idChildPlanEvent);
		ChildPlanIntellectualDevelopDto intellectualDevelop = getIntellectualDevelop(idChildPlanEvent);
		ChildPlanEducationDto educationDto = getChildEducation(idChildPlanEvent);
		ChildPlanEmtnlThrptcDtlDto emtnlThrptcDtlDto = getEmtnlThrptcDtl(idChildPlanEvent);
		ChildPlanQrtpPrmnncyMeetingDto qrtpPermanencyMtng = getQrtpPermanencyMtng(idChildPlanEvent);
		ChildPalnBehaviorMgntDto behaviorMgnt = getBehaviorMgnt(idChildPlanEvent);
		ChildPlanYouthParentingDto youthParenting = getYouthParenting(idChildPlanEvent);
		ChildPlanHealthCareSummaryDto healthCareSummary = getHealthCareSummary(idChildPlanEvent,
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIdStage());

		processHealthCareSummary(idChildPlanEvent, childPlanOfServiceDto, healthCareSummary);

		ChildPlanSupervisionDto supervision = getSupervision(idChildPlanEvent);
		ChildPlanSocialRecreationalDto socialRecreational = getSocialRecreational(idChildPlanEvent);
		ChildPlanTransAdltAbvDtlDto transAdltAbvDtl = getTransAdltAbvDtl(idChildPlanEvent);
		ChildPlanTransAdtltBlwDtlDto transAdtltBlwDtl = getTransAdtltBlwDtl(idChildPlanEvent);
		ChildPlanHighRiskServicesDto highRiskServicesDto = getHighRiskServices(idChildPlanEvent);
		ChildPlanTreatmentServiceDto treatmentServiceDto = getTreatmentService(idChildPlanEvent);
		ChildPlanFmlyTeamPrtctpnDto fmlyTeamPrtctpnDto = getFmlyTeamPrtctpn(idChildPlanEvent);

		// For New Using on Add ChildPlan
		if (idChildPlanEvent > 0 && idChildPlanEvent == childPlanOfServiceDto.getIdApprovedChildPlan()) {
			if (!ObjectUtils.isEmpty(legalGrardianshipList)) {
				legalGrardianshipList.stream().forEach(legalGrardianship -> {
					legalGrardianship.setIdCpLglGrdnshp(null);
					legalGrardianship.setIdChildPlanEvent(null);
					legalGrardianship.setDtLastUpdate(null);
					legalGrardianship.setDtCreated(null);
					legalGrardianship.setIdCreatedPerson(null);
					legalGrardianship.setIdLastUpdatedPerson(null);
				});
			}
			childPlanOfServiceDto.setChildPlanLegalGrdnshpDtoList(legalGrardianshipList);
			if (!ObjectUtils.isEmpty(planVisitation)) {
				
				String[] ignorePropertiesArray = { "idChildPlanEvent", "dtLastUpdate", "dtCreated",
						"idLastUpdatePerson", "idCreatedPerson","indChildSib","idCpVisitCntctFmly"};
				BeanUtils.copyProperties(planVisitation,
						childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto(), ignorePropertiesArray);				
			}
			if (!ObjectUtils.isEmpty(qrtpPermanencyMtng)) {

				String[] ignorePropertiesArray = { "idChildPlanEvent", "dtLastUpdate", "dtCreated",
						"idLastUpdatePerson", "idCreatedPerson","indCurrentQrtp","idCpQrtpPtm"};
				BeanUtils.copyProperties(qrtpPermanencyMtng,
						childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto(), ignorePropertiesArray);
			}
			if (!ObjectUtils.isEmpty(intellectualDevelop)) {
				intellectualDevelop.setIdChildPlanEvent(null);
				intellectualDevelop.setIdCpIntlctlDvlpmntl(null);
				List<ChildPlanGoalDto> childPlanGoalDtoList = processForNewGoals(
						intellectualDevelop.getChildPlanGoalDtoList());
				intellectualDevelop.setChildPlanGoalDtoList(childPlanGoalDtoList);
				intellectualDevelop.setDtLastUpdate(null);
				intellectualDevelop.setIdCreatedPerson(null);
				intellectualDevelop.setIdLastUpdatePerson(null);
			}
			childPlanOfServiceDto.setChildPlanIntellectualDevelopDto(intellectualDevelop);
			if (!ObjectUtils.isEmpty(educationDto)) {
				/*educationDto.setIdChildPlanEvent(ServiceConstants.ZERO);
				educationDto.setIdEductnDtl(ServiceConstants.ZERO);
				List<ChildPlanGoalDto> childPlanGoalDtoList = processForNewGoals(
						educationDto.getChildPlanGoalDtoList());
				educationDto.setChildPlanGoalDtoList(childPlanGoalDtoList);
				educationDto.setDtLastUpdate(null);
				educationDto.setIdCreatedPerson(null);
				educationDto.setIdLastUpdatePerson(null);*/
				String[] ignorePropertiesArray = { "idChildPlanEvent", "dtLastUpdate", "dtCreated",
						"idLastUpdatePerson", "idCreatedPerson", "idEductnDtl", "txtIepGoals", "indChildEnrlldSch",
						"indChildRcvngSvcs", "cdGrade", "nmSchool", "nmSchoolDist" };
				BeanUtils.copyProperties(educationDto,
						childPlanOfServiceDto.getChildPlanEducationDto(), ignorePropertiesArray);
				List<ChildPlanGoalDto> childPlanGoalDtoList = processForNewGoals(
						educationDto.getChildPlanGoalDtoList());
				childPlanOfServiceDto.getChildPlanEducationDto().setChildPlanGoalDtoList(childPlanGoalDtoList);
			}
			if (!ObjectUtils.isEmpty(emtnlThrptcDtlDto)) {
				/*emtnlThrptcDtlDto.setIdChildPlanEvent(ServiceConstants.ZERO);
				emtnlThrptcDtlDto.setIdEmtnlTpDtl(ServiceConstants.ZERO);
				List<ChildPlanGoalDto> childPlanGoalDtoList = processForNewGoals(
						emtnlThrptcDtlDto.getChildPlanGoalDtoList());
				emtnlThrptcDtlDto.setChildPlanGoalDtoList(childPlanGoalDtoList);
				emtnlThrptcDtlDto.setDtLastUpdate(null);
				emtnlThrptcDtlDto.setIdCreatedPerson(null);
				emtnlThrptcDtlDto.setIdLastUpdatePerson(null);*/
				String[] ignorePropertiesArray = { "idChildPlanEvent", "dtLastUpdate", "dtCreated",
						"idLastUpdatePerson", "idCreatedPerson", "idEmtnlTpDtl","indChildCnfrmdSxTrfckng","indChildSuspdSxTrfckng","indChildCnfrmdLbrTrfckng","indChildSuspdLbrTrfckng",
						"dtPsych","nmClnclPrfsnlPsych", "indChildCareCordntnSrvc",
						"indChildHmnTrfckngAdvSrvc", "indChildCnslnHmnTrfckngVct", "indChildSxlExptnIdntfn", "selRecntScore",
						"indChildHmnTrfckngSrvc", "ckbCrisIntSrvc", "ckbDrpInCntrSrvc", "ckbEmplSupSrvc", "ckbSubAbuSrvc", "ckbSurPeerSupGrp",
						"ckbOthrSrvc", "ckbNoOthrSrvc", "txtChildOthrSrvcRcvd", "txtSpcfcSvcsForChild"};
				BeanUtils.copyProperties(emtnlThrptcDtlDto,
						childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto(), ignorePropertiesArray);
				List<ChildPlanGoalDto> childPlanGoalDtoList = processForNewGoals(
						emtnlThrptcDtlDto.getChildPlanGoalDtoList());
				childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().setChildPlanGoalDtoList(childPlanGoalDtoList);
			}
			if (!ObjectUtils.isEmpty(behaviorMgnt)) {
				behaviorMgnt.setIdChildPlanEvent(null);
				behaviorMgnt.setIdBhvrMgmt(null);
				List<ChildPlanGoalDto> childPlanGoalDtoList = processForNewGoals(
						behaviorMgnt.getChildPlanGoalDtoList());
				behaviorMgnt.setChildPlanGoalDtoList(childPlanGoalDtoList);
				behaviorMgnt.setDtLastUpdate(null);
				behaviorMgnt.setIdCreatedPerson(null);
				behaviorMgnt.setIdLastUpdatePerson(null);
			}
			childPlanOfServiceDto.setChildPalnBehaviorMgntDto(behaviorMgnt);
			if (!ObjectUtils.isEmpty(youthParenting)) {
				/*youthParenting.setIdChildPlanEvent(ServiceConstants.ZERO);
				youthParenting.setIdYouthPregntPrntg(ServiceConstants.ZERO);
				youthParenting.setDtLastUpdate(null);
				youthParenting.setIdCreatedPerson(null);
				youthParenting.setIdLastUpdatePerson(null);*/				
				String[] ignorePropertiesArray = { "idChildPlanEvent", "dtLastUpdate", "dtCreated",
						"idLastUpdatePerson", "idCreatedPerson", "indChildCurrPergnt","indChildGender","indYouthWithChild","idYouthPregntPrntg"};
				BeanUtils.copyProperties(youthParenting,
						childPlanOfServiceDto.getChildPlanYouthParentingDto(), ignorePropertiesArray);
			}
			if (!ObjectUtils.isEmpty(healthCareSummary)) {
			/*	healthCareSummary.setIdChildPlanEvent(ServiceConstants.ZERO);
				healthCareSummary.setIdHlthCareSumm(ServiceConstants.ZERO);
				List<ChildPlanGoalDto> childPlanGoalDtoList = processForNewGoals(
						healthCareSummary.getChildPlanGoalDtoList());
				healthCareSummary.setChildPlanGoalDtoList(childPlanGoalDtoList);
				healthCareSummary.setDtLastHearngScrng(null);
				healthCareSummary.setDtLastUpdate(null);
				healthCareSummary.setIdCreatedPerson(null);
				healthCareSummary.setIdLastUpdatePerson(null);*/
				
				String[] ignorePropertiesArray = { "idChildPlanEvent", "dtLastUpdate", "dtCreated",
						"idLastUpdatePerson", "idCreatedPerson","idHlthCareSumm","nmPrimaryMedCons" };
				BeanUtils.copyProperties(healthCareSummary,
						childPlanOfServiceDto.getChildPlanHealthCareSummaryDto(), ignorePropertiesArray);				
				
				childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().setChidPlanPsychMedctnDtlDtoList(healthCareSummary.getChidPlanPsychMedctnDtlDtoList());
				childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().setChidPlanNonPsychMedctnDtlDtoList(healthCareSummary.getChidPlanNonPsychMedctnDtlDtoList());				
				List<ChildPlanGoalDto> childPlanGoalDtoList = processForNewGoals(
						healthCareSummary.getChildPlanGoalDtoList());
				childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().setChildPlanGoalDtoList(childPlanGoalDtoList);
			}
			if (!ObjectUtils.isEmpty(supervision)) {
				supervision.setIdChildPlanEvent(null);
				supervision.setIdCpSprvsnDtl(null);
				List<ChildPlanGoalDto> childPlanGoalDtoList = processForNewGoals(supervision.getChildPlanGoalDtoList());
				supervision.setChildPlanGoalDtoList(childPlanGoalDtoList);
				supervision.setDtLastUpdate(null);
				supervision.setIdCreatedPerson(null);
				supervision.setIdLastUpdatePerson(null);
			}
			childPlanOfServiceDto.setChildPlanSupervisionDto(supervision);
			if (!ObjectUtils.isEmpty(socialRecreational)) {
				socialRecreational.setIdChildPlanEvent(null);
				socialRecreational.setIdSoclRecrtnalDtl(null);
				List<ChildPlanGoalDto> childPlanGoalDtoList = processForNewGoals(
						socialRecreational.getChildPlanGoalDtoList());
				socialRecreational.setChildPlanGoalDtoList(childPlanGoalDtoList);
				socialRecreational.setDtLastUpdate(null);
				socialRecreational.setIdCreatedPerson(null);
				socialRecreational.setIdLastUpdatePerson(null);
			}
			childPlanOfServiceDto.setChildPlanSocialRecreationalDto(socialRecreational);
			if (!ObjectUtils.isEmpty(transAdltAbvDtl)) {
				transAdltAbvDtl.setIdChildPlanEvent(null);
				transAdltAbvDtl.setIdTtsAdultAbvDtl(null);
				List<ChildPlanGoalDto> childPlanGoalDtoList = processForNewGoals(
						transAdltAbvDtl.getChildPlanGoalDtoList());
				transAdltAbvDtl.setChildPlanGoalDtoList(childPlanGoalDtoList);
				transAdltAbvDtl.setDtLastUpdate(null);
				transAdltAbvDtl.setIdCreatedPerson(null);
				transAdltAbvDtl.setIdLastUpdatePerson(null);
			}
			childPlanOfServiceDto.setTransAdulthoodAboveFourteenDto(transAdltAbvDtl);
			if (!ObjectUtils.isEmpty(transAdtltBlwDtl)) {
				transAdtltBlwDtl.setIdChildPlanEvent(null);
				transAdtltBlwDtl.setIdTtsAdultBlwDtl(null);
				List<ChildPlanGoalDto> childPlanGoalDtoList = processForNewGoals(
						transAdtltBlwDtl.getChildPlanGoalDtoList());
				transAdtltBlwDtl.setChildPlanGoalDtoList(childPlanGoalDtoList);
				transAdtltBlwDtl.setDtLastUpdate(null);
				transAdtltBlwDtl.setIdCreatedPerson(null);
				transAdtltBlwDtl.setIdLastUpdatePerson(null);
			}
			childPlanOfServiceDto.setTransAdulthoodBelowThirteenDto(transAdtltBlwDtl);
			if (!ObjectUtils.isEmpty(highRiskServicesDto)) {
				/*highRiskServicesDto.setIdChildPlanEvent(ServiceConstants.ZERO);
				highRiskServicesDto.setIdSvcsHghRiskBhvr(ServiceConstants.ZERO);
				highRiskServicesDto.setDtLastUpdate(null);
				highRiskServicesDto.setIdCreatedPerson(null);
				highRiskServicesDto.setIdLastUpdatePerson(null);*/
				String[] ignorePropertiesArray = { "idChildPlanEvent", "dtLastUpdate", "dtCreated",
						"idLastUpdatePerson", "idCreatedPerson","idSvcsHghRiskBhvr","indSxAggrsvIdentfd","txtSxAggrsvIdentfd" };
				BeanUtils.copyProperties(highRiskServicesDto,
						childPlanOfServiceDto.getChildPlanHighRiskServicesDto(), ignorePropertiesArray);

			}
			if (!ObjectUtils.isEmpty(treatmentServiceDto)) {
				treatmentServiceDto.setIdChildPlanEvent(null);
				treatmentServiceDto.setIdTrtmntSrvcDtl(null);
				List<ChildPlanGoalDto> childPlanGoalDtoList = processForNewGoals(
						treatmentServiceDto.getChildPlanGoalDtoList());
				treatmentServiceDto.setChildPlanGoalDtoList(childPlanGoalDtoList);
				treatmentServiceDto.setDtLastUpdate(null);
				treatmentServiceDto.setIdCreatedPerson(null);
				treatmentServiceDto.setIdLastUpdatePerson(null);
			}
			childPlanOfServiceDto.setChildPlanTreatmentServiceDto(treatmentServiceDto);
			if (!ObjectUtils.isEmpty(fmlyTeamPrtctpnDto)) {
				/*fmlyTeamPrtctpnDto.setIdChildPlanEvent(ServiceConstants.ZERO);
				fmlyTeamPrtctpnDto.setIdChildFmlyTeamDtl(ServiceConstants.ZERO);
				List<ChildPlanParticipDto> childPlanParticipDtoList = processForNewParticipants(
						fmlyTeamPrtctpnDto.getChildPlanPartcptDevDtoList());
				fmlyTeamPrtctpnDto.setChildPlanPartcptDevDtoList(childPlanParticipDtoList);
				fmlyTeamPrtctpnDto.setDtCreated(null);
				fmlyTeamPrtctpnDto.setDtLastUpdate(null);
				fmlyTeamPrtctpnDto.setIdCreatedPerson(null);
				fmlyTeamPrtctpnDto.setIdLastUpdatePerson(null);*/
				
				String[] ignorePropertiesArray = { "idChildPlanEvent", "dtLastUpdate", "dtCreated",
						"idLastUpdatePerson", "idCreatedPerson","idChildFmlyTeamDtl","nbrCswrkrPhone" };
				BeanUtils.copyProperties(fmlyTeamPrtctpnDto,
						childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto(), ignorePropertiesArray);				
				List<ChildPlanParticipDto> childPlanParticipDtoList = processForNewParticipants(
						fmlyTeamPrtctpnDto.getChildPlanPartcptDevDtoList());
				childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().setChildPlanPartcptDevDtoList(childPlanParticipDtoList);
			}

		}else{
			childPlanOfServiceDto.setChildPlanLegalGrdnshpDtoList(legalGrardianshipList);
			childPlanOfServiceDto.setChildPlanVisitationCnctFmlyDto(planVisitation);
			childPlanOfServiceDto.setChildPlanQrtpPrmnncyMeetingDto(qrtpPermanencyMtng);
			childPlanOfServiceDto.setChildPlanIntellectualDevelopDto(intellectualDevelop);
			childPlanOfServiceDto.setChildPlanEducationDto(educationDto);
			childPlanOfServiceDto.setChildPlanEmtnlThrptcDtlDto(emtnlThrptcDtlDto);
			childPlanOfServiceDto.setChildPalnBehaviorMgntDto(behaviorMgnt);
			childPlanOfServiceDto.setChildPlanYouthParentingDto(youthParenting);
			childPlanOfServiceDto.setChildPlanHealthCareSummaryDto(healthCareSummary);
			childPlanOfServiceDto.setChildPlanSupervisionDto(supervision);
			childPlanOfServiceDto.setChildPlanSocialRecreationalDto(socialRecreational);
			childPlanOfServiceDto.setTransAdulthoodAboveFourteenDto(transAdltAbvDtl);
			childPlanOfServiceDto.setTransAdulthoodBelowThirteenDto(transAdtltBlwDtl);
			childPlanOfServiceDto.setChildPlanHighRiskServicesDto(highRiskServicesDto);
			childPlanOfServiceDto.setChildPlanTreatmentServiceDto(treatmentServiceDto);
			childPlanOfServiceDto.setChildPlanFmlyTeamPrtctpnDto(fmlyTeamPrtctpnDto);
		}

		
		return childPlanOfServiceDto;
	}

	private void processPriorAdoption(Long idChildPlanEvent, ChildPlanOfServiceDto childPlanOfServiceDto,
			List<ChildPlanPriorAdpInfoDto> priorAdptn) {
		if (!ObjectUtils.isEmpty(priorAdptn)) {
			List<ChildPlanPriorAdpInfoDto> priorAdptnDomestic = priorAdptn.stream()
					.filter(c -> ServiceConstants.DOMESTIC.equals(c.getCdChildAdoptdType()))
					.collect(Collectors.toList());
			List<ChildPlanPriorAdpInfoDto> priorAdptnInternational = priorAdptn.stream()
					.filter(c -> ServiceConstants.INTERNATIONAL.equals(c.getCdChildAdoptdType()))
					.collect(Collectors.toList());
			// for new Using functionality
			if (idChildPlanEvent > 0 && (idChildPlanEvent).equals(childPlanOfServiceDto.getIdApprovedChildPlan()) ) {
				if (!ObjectUtils.isEmpty(priorAdptnDomestic)) {
					priorAdptnDomestic.stream().forEach(c -> {
						c.setIdCpAdoptnDtl(null);
						c.setIdChildPlanEvent(null);
						c.setDtLastUpdate(null);
						c.setDtCreated(null);
						c.setIdCreatedPerson(null);
						c.setIdLastUpdatePerson(null);
					});
				}
				if (!ObjectUtils.isEmpty(priorAdptnInternational)) {
					priorAdptnInternational.stream().forEach(c -> {
						c.setIdCpAdoptnDtl(null);
						c.setIdChildPlanEvent(null);
						c.setDtLastUpdate(null);
						c.setDtCreated(null);
						c.setIdCreatedPerson(null);
						c.setIdLastUpdatePerson(null);
					});
				}
			}
			childPlanOfServiceDto.setChildPlanPriorAdpInfoDomesticDtoList(priorAdptnDomestic);
			childPlanOfServiceDto.setChildPlanPriorAdpInfoInterntlDtoList(priorAdptnInternational);
		}
	}

	private void processHealthCareSummary(Long idChildPlanEvent, ChildPlanOfServiceDto childPlanOfServiceDto,
			ChildPlanHealthCareSummaryDto healthCareSummary) {
		if (!ObjectUtils.isEmpty(healthCareSummary)) {			
			// for New Using Scenarios
			if (idChildPlanEvent > 0 && idChildPlanEvent.equals(childPlanOfServiceDto.getIdApprovedChildPlan())) {
				List<ChidPlanPsychMedctnDtlDto> chidPlanPsychMedctnDtlDtoList = healthCareSummary
						.getChidPlanPsychMedctnDtlDtoList();
				List<ChidPlanPsychMedctnDtlDto> chidPlanNonPsychMedctnDtlDtoList = healthCareSummary
						.getChidPlanNonPsychMedctnDtlDtoList();
				if (!ObjectUtils.isEmpty(chidPlanPsychMedctnDtlDtoList)) {
					chidPlanPsychMedctnDtlDtoList.stream().forEach(c -> {
						c.setIdPsychMedctnDtl(null);
						c.setIdCPHlthCareSumm(null);
						c.setDtLastUpdate(null);
						c.setIdCreatedPerson(null);
						c.setIdLastUpdatePerson(null);
					});
				}
				if (!ObjectUtils.isEmpty(chidPlanNonPsychMedctnDtlDtoList)) {
					chidPlanNonPsychMedctnDtlDtoList.stream().forEach(c -> {
						c.setIdPsychMedctnDtl(null);
						c.setIdCPHlthCareSumm(null);
						c.setDtLastUpdate(null);
						c.setIdCreatedPerson(null);
						c.setIdLastUpdatePerson(null);
					});
				}
				healthCareSummary.setChidPlanPsychMedctnDtlDtoList(chidPlanPsychMedctnDtlDtoList);
				healthCareSummary.setChidPlanNonPsychMedctnDtlDtoList(chidPlanNonPsychMedctnDtlDtoList);
			}
			
		}
	}

	private List<ChildPlanParticipDto> processForNewParticipants(
			List<ChildPlanParticipDto> childPlanPartcptDevDtoList) {
		if (!ObjectUtils.isEmpty(childPlanPartcptDevDtoList)) {
			childPlanPartcptDevDtoList.stream().forEach(participant -> {
				participant.setIdChildPlanEvent(null);
				participant.setIdChildPlanPart(null);
				participant.setDtLastUpdate(null);
				//TODO - Asad save logged in id
				participant.setIdCreatedPerson(null);
				participant.setIdLastUpdatePerson(null);
				participant.setDtCspPartCopyGiven(null);
			});
		}

		return childPlanPartcptDevDtoList;

	}

	private List<ChildPlanGoalDto> processForNewGoals(List<ChildPlanGoalDto> childPlanGoalDtoList) {
		if (!ObjectUtils.isEmpty(childPlanGoalDtoList)) {
			childPlanGoalDtoList.stream().forEach(goal -> {
				goal.setIdChildPlanEvent(null);
				goal.setIdLstGoals(null);
				goal.setDtLastUpdate(null);
				goal.setIdCreatedPerson(null);
				goal.setIdLastUpdatePerson(null);
			});
		}

		return childPlanGoalDtoList;
	}

	/**
	 * Method Name: deleteAdoption. Method Description: This method will delete
	 * the selected domestic/International adoption details
	 * 
	 * @param deleteIds
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes deleteAdoption(List<Long> deleteIds) {
		ChildPlanDtlRes childPlanDtlRes = null;
		for (Long id : deleteIds) {
			CpAdoptnDtl cpAdoptnDtl = (CpAdoptnDtl) sessionFactory.getCurrentSession().get(CpAdoptnDtl.class, id);
			if (!ObjectUtils.isEmpty(cpAdoptnDtl)) {
				sessionFactory.getCurrentSession().delete(cpAdoptnDtl);
			} else {
				childPlanDtlRes = new ChildPlanDtlRes();
				ErrorDto erorrDto = new ErrorDto();
				erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				childPlanDtlRes.setErrorDto(erorrDto);
				return childPlanDtlRes;
			}
		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: deleteLegalGuardianship. Method Description: This method
	 * will delete the selected legal guardianship information.
	 * 
	 * @param deleteIds
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes deleteLegalGuardianship(List<Long> deleteIds) {
		ChildPlanDtlRes childPlanDtlRes = null;
		for (Long id : deleteIds) {
			CpLglGrdnshp cpLglGrdnshp = (CpLglGrdnshp) sessionFactory.getCurrentSession().get(CpLglGrdnshp.class, id);
			if (!ObjectUtils.isEmpty(cpLglGrdnshp)) {
				sessionFactory.getCurrentSession().delete(cpLglGrdnshp);
			} else {
				childPlanDtlRes = new ChildPlanDtlRes();
				ErrorDto erorrDto = new ErrorDto();
				erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				childPlanDtlRes.setErrorDto(erorrDto);
				return childPlanDtlRes;
			}
		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: deleteGoals. Method Description: This method will delete the
	 * selected goals details for the coressponding section.
	 * 
	 * @param deleteIds
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes deleteGoals(List<Long> deleteIds) {
		ChildPlanDtlRes childPlanDtlRes = null;
		for (Long id : deleteIds) {
			CpLstGoal cpLstGoal = (CpLstGoal) sessionFactory.getCurrentSession().get(CpLstGoal.class, id);
			if (!ObjectUtils.isEmpty(cpLstGoal)) {
				sessionFactory.getCurrentSession().delete(cpLstGoal);
			} else {
				childPlanDtlRes = new ChildPlanDtlRes();
				ErrorDto erorrDto = new ErrorDto();
				erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				childPlanDtlRes.setErrorDto(erorrDto);
				return childPlanDtlRes;
			}
		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: deletePyscMdctnHc. Method Description: This method will
	 * delete the selected Non-Psychotropic Medication(s) and Psychotropic
	 * Medication(s) details.
	 * 
	 * @param deleteIds
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes deletePyscMdctnHc(List<Long> deleteIds) {
		ChildPlanDtlRes childPlanDtlRes = null;
		for (Long id : deleteIds) {
			CpPsychMedctnDtl cpPsychMedctnDtl = (CpPsychMedctnDtl) sessionFactory.getCurrentSession()
					.get(CpPsychMedctnDtl.class, id);
			if (!ObjectUtils.isEmpty(cpPsychMedctnDtl)) {
				sessionFactory.getCurrentSession().delete(cpPsychMedctnDtl);
			} else {
				childPlanDtlRes = new ChildPlanDtlRes();
				ErrorDto erorrDto = new ErrorDto();
				erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				childPlanDtlRes.setErrorDto(erorrDto);
				return childPlanDtlRes;
			}
		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: deleteAdoption Method Description: This method will delete
	 * the selected family participant details.
	 *
	 * @param deleteIds
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes deleteCpPartcptnTeam(List<Long> deleteIds) {
		ChildPlanDtlRes childPlanDtlRes = null;
		for (Long id : deleteIds) {
			ChildPlanParticip childPlanParticip = (ChildPlanParticip) sessionFactory.getCurrentSession()
					.get(ChildPlanParticip.class, id);
			if (!ObjectUtils.isEmpty(childPlanParticip)) {
				sessionFactory.getCurrentSession().delete(childPlanParticip);
			} else {
				childPlanDtlRes = new ChildPlanDtlRes();
				ErrorDto erorrDto = new ErrorDto();
				erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				childPlanDtlRes.setErrorDto(erorrDto);
				return childPlanDtlRes;
			}
		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: deleteQrtpPtmParticipants Method Description: This method will delete
	 * the selected QRTP PTM participant details.
	 *
	 * @param deleteIds
	 * @return ChildPlanDtlRes
	 */
	@Override
	public ChildPlanDtlRes deleteQrtpPtmParticipants(List<Long> deleteIds) {
		ChildPlanDtlRes childPlanDtlRes = null;
		for (Long id : deleteIds) {
			CpQrtpPtmParticipant cpQrtpPtmParticipant = (CpQrtpPtmParticipant) sessionFactory.getCurrentSession()
					.get(CpQrtpPtmParticipant.class, id);
			if (!ObjectUtils.isEmpty(cpQrtpPtmParticipant)) {
				sessionFactory.getCurrentSession().delete(cpQrtpPtmParticipant);
			} else {
				childPlanDtlRes = new ChildPlanDtlRes();
				ErrorDto erorrDto = new ErrorDto();
				erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				childPlanDtlRes.setErrorDto(erorrDto);
				return childPlanDtlRes;
			}
		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: chckVictimHasExistingCP Method Description: This Method will
	 * check the victim has existing child plan or not.
	 * 
	 * @param idPerson
	 * @return Boolean
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Boolean chckVictimHasExistingCP(Long idPerson) {
		Boolean existCP = Boolean.TRUE;
		List<ChildPlan> childPlan = (List<ChildPlan>) sessionFactory.getCurrentSession().createCriteria(ChildPlan.class)
				.createAlias("person", "person").add(Restrictions.eq("person.idPerson", idPerson)).list();
		if (ObjectUtils.isEmpty(childPlan))
			existCP = Boolean.FALSE;
		return existCP;
	}

	/**
	 * 
	 * Method Name: getPrePsycnDtls Method Description:This method to get
	 * previous APRV child plans psyc medc details
	 * 
	 * @param idPerson
	 * @return
	 */
	@Override
	public List<ChidPlanPsychMedctnDtlDto> getPrePsycnDtls(Long idPerson, Long idStage) {
		List<ChidPlanPsychMedctnDtlDto> prePsycnDtls = null;
		Long idApprovedChildPlan = getPreviousApprovedChildPlanId(idPerson,  idStage);
		if (!ObjectUtils.isEmpty(idApprovedChildPlan)) {
			ChildPlanHealthCareSummaryDto childPlanHealthCareSummaryDto = getHealthCareSummary(idApprovedChildPlan,
					idStage);
			if (!ObjectUtils.isEmpty(childPlanHealthCareSummaryDto)
					&& !ObjectUtils.isEmpty(childPlanHealthCareSummaryDto.getChidPlanPsychMedctnDtlDtoList())) {
				prePsycnDtls = childPlanHealthCareSummaryDto.getChidPlanPsychMedctnDtlDtoList();
			}
		}

		return prePsycnDtls;
	}

	
	/**
	 * 
	 * Method Name: getPrePsycnDtls Method Description:This method to get
	 * previous APRV child plans psyc medc details
	 * 
	 * @param idPerson
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long getPreviousApprovedChildPlanId(Long idPerson, Long idStage) {		
		Date dtR2Release = codesDao.getAppRelDate(ServiceConstants.R2_REL_CODE);
		Long idApprovedChildPlan = (Long) sessionFactory.getCurrentSession().createSQLQuery(getApprovedChildPlanIdSql)
				.addScalar("idApprovedChildPlan", StandardBasicTypes.LONG).setParameter("idPerson", idPerson).setParameter("idStage", idStage)
				.setParameter("dtR2Release", dtR2Release).list().stream().findAny().orElse(null);

		return idApprovedChildPlan;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void deletePriorAdoptions(Long idChildPlanEvent, String cdChildAdoptdType) {
		List<CpAdoptnDtl> cpAdoptnDtlList = (List<CpAdoptnDtl>) sessionFactory.getCurrentSession().createCriteria(CpAdoptnDtl.class)
				.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent)).add(Restrictions.eq("cdChildAdoptdType", cdChildAdoptdType)).list();
		
		if(!ObjectUtils.isEmpty(cpAdoptnDtlList)) {
			cpAdoptnDtlList.stream().forEach(cpAdoptnDtl->{
				sessionFactory.getCurrentSession().delete(cpAdoptnDtl);
			});
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public void deleteLegalGuardians(Long idChildPlanEvent) {
		List<CpLglGrdnshp> cpLglGrdnshpList = (List<CpLglGrdnshp>) sessionFactory.getCurrentSession().createCriteria(CpLglGrdnshp.class)
				.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent)).list();
		
		if(!ObjectUtils.isEmpty(cpLglGrdnshpList)) {
			cpLglGrdnshpList.stream().forEach(cpLglGrdnshp->{
				sessionFactory.getCurrentSession().delete(cpLglGrdnshp);
			});
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void deleteGoals(Long idChildPlanEvent, String topic) {
		List<CpLstGoal> cpLstGoalList = (List<CpLstGoal>) sessionFactory.getCurrentSession().createCriteria(CpLstGoal.class)
				.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent)).add(Restrictions.eq("cdChildPlanTopics", topic)).list();
		
		if(!ObjectUtils.isEmpty(cpLstGoalList)) {
			cpLstGoalList.stream().forEach(cpLstGoal->{
				sessionFactory.getCurrentSession().delete(cpLstGoal);
			});
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public void deleteTreatmentService(Long idChildPlanEvent, String topic) {
		List<CpTrtmntSrvcDtl> cpTrtmntSrvcList = sessionFactory.getCurrentSession().createCriteria(CpTrtmntSrvcDtl.class)
		.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent)).list();
		
		if(!ObjectUtils.isEmpty(cpTrtmntSrvcList)) {
			cpTrtmntSrvcList.stream().forEach(cpTrtmntSrvc->{
				cpTrtmntSrvc.setIndNaGoal(null);
				cpTrtmntSrvc.setTxtAddtnalTrtmntSrvc(null);
				cpTrtmntSrvc.setTxtIntenseLoc(null);
				cpTrtmntSrvc.setTxtTrnstnalLivingScnt(null);
				cpTrtmntSrvc.setTxtTrtmntCritra(null);
				cpTrtmntSrvc.setTxtTrtmntIntrvntn(null);
				cpTrtmntSrvc.setTxtTrtmntSpclSrvc(null);
				sessionFactory.getCurrentSession().update(cpTrtmntSrvc);
			});
		}
		deleteGoals(idChildPlanEvent, topic);
		
	}

	//PPM 70054(FCL) - IMPACT Date Requirement for RCYFC Notification

	/**
	 * @param idStage
	 * @return
	 */
	@Override
	public Date getChildInitialBORDate(Long idStage){
		Date dtInitialBor = (Date) sessionFactory.getCurrentSession().createSQLQuery(getInitialBillOfRightsDateByStageIdSql)
				.addScalar("dtInitialBor", StandardBasicTypes.DATE)
				.setParameter("idStage", idStage).list().stream().findAny().orElse(null);
		return dtInitialBor;
	}

	/**
	 * @param idStage
	 * @return
	 */
	@Override
	public Date getChildMostRecentBORDate(Long idStage){
		Date dtMostRecentBor = (Date) sessionFactory.getCurrentSession().createSQLQuery(getReviewBillOfRightsDateByStageIdSql)
				.addScalar("dtMostRecentBor", StandardBasicTypes.DATE)
				.setParameter("idStage", idStage).list().stream().findAny().orElse(null);
		return dtMostRecentBor;
	}

	@Override
	public String getQrtpRecommendations(Long idStage) {

		String comments = (String)sessionFactory.getCurrentSession().createSQLQuery(getQrtpRecommendationsByStageIdSql)
				.addScalar("comments", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage)
				.uniqueResult();
		return comments;
	}

	@Override
	public ServicePackageDtlDto getSvcPkgDetails(Long idCase, Long idStage, String svcPkgType){
		ServicePackageDtlDto servicePackageDtlDto = null;
		List<ServicePackageDtlDto> dtoList = sessionFactory.getCurrentSession()
				.createSQLQuery(getSvcPkgDetails).addScalar("svcPkgCd", StandardBasicTypes.STRING)
				.addScalar("dtSvcStart", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("indCansRmd", StandardBasicTypes.STRING)
				.addScalar("dtCansAssmtCompleted", StandardBasicTypes.TIMESTAMP)
				.addScalar("cansRmdCmnts", StandardBasicTypes.STRING)
				.setParameter("caseId", idCase).setParameter("stageId", idStage)
				.setParameter("svcPkgType", svcPkgType)
				.setResultTransformer(Transformers.aliasToBean(ServicePackageDtlDto.class)).list();
		if(!ObjectUtils.isEmpty(dtoList)){
			servicePackageDtlDto = dtoList.get(0);
		}
		return servicePackageDtlDto;
	}

}
