package us.tx.state.dfps.service.SDM.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.SafetyEvalFactors;
import us.tx.state.dfps.common.domain.SafetyEvaluation;
import us.tx.state.dfps.riskandsafetyassmt.dto.SafetyFactorEvalDto;
import us.tx.state.dfps.riskandsafetyassmt.dto.SafteyEvalResDto;
import us.tx.state.dfps.service.SDM.dao.SafetyEvalDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.SafetyAssessmentReq;
import us.tx.state.dfps.service.common.response.SafetyAssessmentRes;
import us.tx.state.dfps.service.safetyassessment.dto.SafetyAssessmentDto;
import us.tx.state.dfps.service.safetyassessment.dto.SafetyFactorDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service for
 * the Child Safety Eval Form on Safety and Risk Assessment Page. May 9, 2018-
 * 10:31:14 AM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class SafetyEvalDaoImpl implements SafetyEvalDao {

	@Autowired
	SessionFactory sessionFactory;

	@Value("${SafetyEvalDaoImpl.getRecentSafeteyEval}")
	private String getRecentSafetyEvalSql;

	@Value("${SafetyEvalDaoImpl.getCurrentEventId}")
	private String getCurrentEventId;

	@Value("${SafetyEvalDaoImpl.getCurrentEventStatus}")
	private String getCurrentEventStatus;

	@Value("${SafetyEvalDaoImpl.getSubStageOpen}")
	private String getSubStageOpen;

	@Value("${SafetyEvalDaoImpl.retrieveSafetyAssessmentData}")
	private String retrieveSafetyAssessmentData;

	@Value("${SafetyEvalDaoImpl.getQueryPgData}")
	private String getQueryPgData;

	/**
	 * Method Name:getSafetyEvalFactors Method Description: This DAM retrieves
	 * multiptle rows from the Safety Eval Factors table for a given idEvent.
	 * 
	 * @param idEvent
	 * @return SafetyFactorEvalDto
	 */
	@Override
	public List<SafetyFactorEvalDto> getSafetyEvalFactors(Long idEvent) {

		@SuppressWarnings("unchecked")
		List<SafetyFactorEvalDto> safetyFactorsEvalList = sessionFactory.getCurrentSession()
				.createCriteria(SafetyEvalFactors.class)
				.setProjection(Projections.projectionList()
						.add(Projections.property(ServiceConstants.CD_SAFETY_FACTOR), ServiceConstants.CD_SAFETY_FACTOR)
						.add(Projections.property("stage.idStage"), ServiceConstants.IDSTAGE)
						.add(Projections.property(ServiceConstants.DT_LAST_UPDATE), ServiceConstants.DT_LAST_UPDATE)
						.add(Projections.property(ServiceConstants.CD_SAFETY_EVAL_TYPE),
								ServiceConstants.CD_SAFETY_EVAL_TYPE)
						.add(Projections.property(ServiceConstants.ID_SAFETY_FACTOR),
								ServiceConstants.ID_SAFETY_FACTOR))
				.add(Restrictions.eq(ServiceConstants.IDEVENT, idEvent))
				.setResultTransformer(Transformers.aliasToBean(SafetyFactorEvalDto.class)).list();

		return safetyFactorsEvalList;
	}

	/**
	 * Method Name: getSafetyEval Method Description: This DAM retrieves a full
	 * row from the Safety Evaluation Table using ID EVENT.
	 * 
	 * @param idEvent
	 * @return SafteyEvalResDto
	 */
	@Override
	public SafteyEvalResDto getSafetyEval(Long idEvent) {

		SafteyEvalResDto safteyEvalResDto = (SafteyEvalResDto) sessionFactory.getCurrentSession()
				.createCriteria(SafetyEvaluation.class)
				.setProjection(Projections.projectionList()
						.add(Projections.property(ServiceConstants.DT_LAST_UPDATE), ServiceConstants.DT_LAST_UPDATE)
						.add(Projections.property(ServiceConstants.CD_SAFETY_EVAL_LEGAL_ACT),
								ServiceConstants.CD_SAFETY_LEGAL_ACT)
						.add(Projections.property(ServiceConstants.SAFETY_EVALUATION_CMNTS_AR),
								ServiceConstants.SAFETY_EVAL_CMNTS_AR)
						.add(Projections.property(ServiceConstants.SAFETY_EVALUATION_CMNTS_CNC),
								ServiceConstants.SAFETY_EVAL_CMNTS_CNC)
						.add(Projections.property(ServiceConstants.SAFETY_EVALUATION_CMNTS_SOC),
								ServiceConstants.SAFETY_EVAL_CMNTS_SOC))
				.add(Restrictions.eq(ServiceConstants.IDEVENT, idEvent))
				.setResultTransformer(Transformers.aliasToBean(SafteyEvalResDto.class)).uniqueResult();

		return safteyEvalResDto;
	}

	/**
	 * Method Name: getRecentSafetyEval Method Description: cinva2d - This DAM
	 * determines whether the ID EVENT Passed in corresponds to the most recent
	 * Safety Eval
	 * 
	 * @param idEvent
	 * @param idStage
	 * @return Long
	 */

	public Long getRecentSafetyEval(Long idEvent, Long idStage) {

		Long recentIdEvent = 0l;
		recentIdEvent = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getRecentSafetyEvalSql)
				.setParameter("idEvent", idEvent).setParameter("idStage", idStage))
						.addScalar("idEvent", StandardBasicTypes.LONG).uniqueResult();
		return recentIdEvent;
	}

	/**
	 * Method Name: getCurrentEventId Method Description:fetches current EventId
	 * 
	 * @param idEvent
	 * @param idStage
	 * @return idEvent
	 */
	@Override
	public Long getCurrentEventId(Long idStage, Long idCase) {
		Long idEvent = 0L;
		idEvent = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCurrentEventId)
				.setParameter("idStage", idStage).setParameter("idCase", idCase))
						.addScalar("idEvent", StandardBasicTypes.LONG).uniqueResult();
		return idEvent;
	}

	/**
	 * Method Name: retrieveSafetyAssessmentData Method Description:Fetches
	 * existing Safety Assessment Data
	 * 
	 * @param idEvent
	 * @param idStage
	 * @param idCase
	 * @return SafetyAssessmentDto
	 */
	@Override
	public SafetyAssessmentDto retrieveSafetyAssessmentData(Long idStage, Long idEvent, Long idCase) {
		@SuppressWarnings("unchecked")
		SafetyAssessmentDto safetyAsmntDto = new SafetyAssessmentDto();
		List<SafetyAssessmentDto> safetyAssessmentDtoList = new ArrayList<SafetyAssessmentDto>();
		safetyAssessmentDtoList = (List<SafetyAssessmentDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(retrieveSafetyAssessmentData)

				.addScalar("cdSafetyDecision", StandardBasicTypes.STRING)
				.addScalar("indAbuseNeglHistCompl", StandardBasicTypes.BOOLEAN)
				.addScalar("abuseNeglHistNotCompl", StandardBasicTypes.STRING)
				.addScalar("indInsufficientSafety", StandardBasicTypes.BOOLEAN)
				.addScalar("decisionRationale", StandardBasicTypes.STRING)
				.addScalar("indChildPresentDanger", StandardBasicTypes.BOOLEAN)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdSafetyFactor", StandardBasicTypes.STRING)
				.addScalar("dtFactorsLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdSafetyArea", StandardBasicTypes.STRING)
				.addScalar("discussyFactors", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdateSafetyArea", StandardBasicTypes.DATE)
				.addScalar("cdSafetyFactorCategory", StandardBasicTypes.STRING)
				.addScalar("cdSafetyFactorResponse", StandardBasicTypes.STRING)
				.addScalar("area", StandardBasicTypes.STRING).addScalar("txtArea", StandardBasicTypes.STRING)
				.addScalar("category", StandardBasicTypes.STRING).addScalar("txtCategory", StandardBasicTypes.STRING)
				.addScalar("factor", StandardBasicTypes.STRING).addScalar("txtFactor", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage).setParameter("idEvent", idEvent).setParameter("idCase", idCase))
						.setResultTransformer(Transformers.aliasToBean(SafetyAssessmentDto.class)).list();

		List<SafetyFactorDto> safetyFactorsDtoList = new ArrayList<SafetyFactorDto>();
		for (SafetyAssessmentDto safetyDto : safetyAssessmentDtoList) {
			SafetyFactorDto safetyFactorDto = new SafetyFactorDto();
			safetyFactorDto.setArea(safetyDto.getTxtArea());
			safetyFactorDto.setCategory(safetyDto.getTxtCategory());
			safetyFactorDto.setCdSafetyArea(safetyDto.getCdSafetyArea());
			safetyFactorDto.setCdSafetyCategory(safetyDto.getTxtCategory());
			safetyFactorDto.setCdSafetyFactor(safetyDto.getFactor());
			safetyFactorDto.setCdSafetyFactorCategory(safetyDto.getCdSafetyFactorCategory());
			safetyFactorDto.setCdSafetyFactorResponse(safetyDto.getCdSafetyFactorResponse());
			safetyFactorDto.setDiscussyFactors(safetyDto.getDiscussyFactors());
			safetyFactorDto.setDtLastUpdateArea(safetyDto.getDtLastUpdateSafetyArea());
			safetyFactorDto.setDtLastUpdateFactor(safetyDto.getDtFactorsLastUpdate());
			safetyFactorDto.setIdCase(idCase);
			safetyFactorDto.setIdEvent(idEvent);
			safetyFactorDto.setIdPerson(safetyDto.getIdPerson());
			safetyFactorDto.setIdStage(idStage);
			safetyFactorDto.setFactors(safetyDto.getTxtFactor());
			safetyFactorsDtoList.add(safetyFactorDto);
		}
		safetyAsmntDto.setFactors(safetyFactorsDtoList);
		safetyAsmntDto.setCdSafetyDecision(safetyAssessmentDtoList.get(0).getCdSafetyDecision());
		safetyAsmntDto.setDecisionRationale(safetyAssessmentDtoList.get(0).getDecisionRationale());
		safetyAsmntDto.setDtLastUpdate(safetyAssessmentDtoList.get(0).getDtLastUpdate());
		safetyAsmntDto.setIdCase(idCase);
		safetyAsmntDto.setIdEvent(idEvent);
		safetyAsmntDto.setIdPerson(safetyAssessmentDtoList.get(0).getIdPerson());
		safetyAsmntDto.setIdStage(idStage);
		safetyAsmntDto.setIndAbuseNeglHistCompl(safetyAssessmentDtoList.get(0).getIndAbuseNeglHistCompl());
		safetyAsmntDto.setIndInsufficientSafety(safetyAssessmentDtoList.get(0).getIndInsufficientSafety());
		safetyAsmntDto.setIndChildPresentDanger(safetyAssessmentDtoList.get(0).getIndChildPresentDanger());
		safetyAsmntDto.setFactors(safetyFactorsDtoList);

		return safetyAsmntDto;
	}

	/**
	 * Method Name: getCurrentEventStatus Method Description:fetches current
	 * Event Status
	 * 
	 * @param idCase
	 * @param idStage
	 * @return String
	 */
	@Override
	public String getCurrentEventStatus(Long idStage, Long idCase) {
		String eventStatus;
		eventStatus = (String) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCurrentEventStatus)
				.setParameter("idStage", idStage).setParameter("idCase", idCase))
						.addScalar("eventStatus", StandardBasicTypes.STRING).uniqueResult();
		return eventStatus;
	}

	/**
	 * Method Name: getSubStageOpen Method Description:Returns a boolean value
	 * stating if stage is open or not
	 * 
	 * @param idCase
	 * @return boolean
	 */

	@Override
	public boolean getSubStageOpen(Long idCase) {
		boolean subStageOpen = false;
		List<String> cdStageList = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSubStageOpen)
				.setParameter("idCase", idCase)).addScalar("cdStage", StandardBasicTypes.STRING).list();
		if (!CollectionUtils.isEmpty(cdStageList)) {
			subStageOpen = true;
		}
		return subStageOpen;

	}

	/**
	 * Method Name: getQueryPgData Method Description: fetches the Safety
	 * Assessment Page
	 * 
	 * @param safetyAssessmentReq
	 * @return SafetyAssessmentRes
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SafetyAssessmentRes getQueryPgData(SafetyAssessmentReq safetyAssessmentReq) {
		List<SafetyFactorDto> safetyFactorsDto = (List<SafetyFactorDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getQueryPgData).addScalar("area", StandardBasicTypes.STRING)
				.addScalar("cdSafetyArea", StandardBasicTypes.STRING).addScalar("category", StandardBasicTypes.STRING)
				.addScalar("txtCategory", StandardBasicTypes.STRING).addScalar("factors", StandardBasicTypes.STRING)
				.addScalar("discussyFactors", StandardBasicTypes.STRING))
						.setResultTransformer(Transformers.aliasToBean(SafetyFactorDto.class)).list();
		SafetyAssessmentRes safetyAssessmentRes = new SafetyAssessmentRes();
		SafetyAssessmentDto safetyAssemntDto = new SafetyAssessmentDto();
		safetyAssemntDto.setFactors(safetyFactorsDto);
		safetyAssessmentRes.setIdCase(safetyAssessmentReq.getIdCase());
		safetyAssessmentRes.setIdEvent(safetyAssessmentReq.getIdEvent());
		safetyAssessmentRes.setIdStage(safetyAssessmentReq.getIdStage());
		safetyAssessmentRes.setSafetyAssessmentDto(safetyAssemntDto);
		return safetyAssessmentRes;
	}
}
