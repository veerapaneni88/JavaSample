package us.tx.state.dfps.service.riskassessment.daoimpl;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.InvstActionQuestion;
import us.tx.state.dfps.common.domain.RiskArea;
import us.tx.state.dfps.common.domain.RiskAssessment;
import us.tx.state.dfps.common.domain.RiskCategory;
import us.tx.state.dfps.common.domain.RiskFactors;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.admin.dto.RiskAssessmentFactorDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.cpsinv.dto.RiskAssmtValueDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.riskassesment.dto.RiskAssmtDtlDto;
import us.tx.state.dfps.service.riskassesment.dto.RiskFactorsDto;
import us.tx.state.dfps.service.riskassessment.dao.RiskAssessmentDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:DAO Impl for
 * fetching stage details Aug 6, 2017- 4:06:37 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class RiskAssessmentDaoImpl implements RiskAssessmentDao {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(RiskAssessmentDaoImpl.class);

	@Value("${RiskAssessmentDaoImpl.getRiskAssessmentFactorDtls}")
	private transient String getRiskAssessmentFactorDtlsSql;

	@Value("${RiskAssessmentDaoImpl.getRiskFactorsDtls}")
	private transient String getRiskFactorsDtlsSql;

	@Value("${RiskAssessmentDaoImpl.populateRiskData}")
	private String populateRiskDatasql;

	@Value("${RiskAssessmentDaoImpl.queryRiskAssmt}")
	private String queryRiskAssmtsql;

	@Value("${RiskAssessmentDaoImpl.queryRiskAssmtExists}")
	private String queryRiskAssmtExistssql;

	@Value("${RiskAssessmentDaoImpl.queryPageData}")
	private String queryPageDatasql;

	@Value("${RiskAssessmentDaoImpl.checkRiskAssmtTaskCode}")
	private String checkRiskAssmtTaskCodesql;

	@Value("${RiskAssessmentDaoImpl.queryCreatedUsingIRAData}")
	private String queryCreatedUsingIRADatasql;

	/**
	 * 
	 * Method Name: getRiskAssessmentFactorDtls Method Description: This method
	 * retrieves data from RISK_ASSESSMENT, RISK_FACTORS Tables. CINV14D
	 * 
	 * @param idStage
	 * @return List<RiskAssessmentFactorOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskAssessmentFactorDto> getRiskAssessmentFactorDtls(Long idStage) {

		log.debug("Entering method getStageDtls in RiskAssessmentFactorDaoImpl");
		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getRiskAssessmentFactorDtlsSql)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdRiskAssmtApAccess", StandardBasicTypes.STRING)
				.addScalar("cdRiskAssmtPurpose", StandardBasicTypes.STRING)
				.addScalar("cdRiskAssmtRiskFind", StandardBasicTypes.STRING)
				.addScalar("indRiskAssmtIntranet", StandardBasicTypes.STRING)
				.addScalar("cdRiskFactorCateg", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(RiskAssessmentFactorDto.class));

		List<RiskAssessmentFactorDto> riskAssessmentFactorDtoList = (List<RiskAssessmentFactorDto>) sQLQuery.list();
		log.debug("Exiting method getStageDtls in RiskAssessmentFactorDaoImpl");
		return riskAssessmentFactorDtoList;
	}

	/**
	 * 
	 * Method Name: getRiskFactorsDtls Method Description: This method retrieves
	 * data from RiskFactors Table. CSEC76D
	 * 
	 * @param idEvent
	 * @return List<RiskFactorDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskFactorsDto> getRiskFactorsDtls(Long idEvent) {

		log.debug("Entering method getRiskFactorsDtls in RiskAssessmentDaoImpl");
		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getRiskFactorsDtlsSql)
				.addScalar("idRiskFactor", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("cdRiskFactorResponse", StandardBasicTypes.STRING)
				.addScalar("cdRiskFactorCateg", StandardBasicTypes.STRING)
				.addScalar("txtRiskFactorComment", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(RiskFactorsDto.class));

		List<RiskFactorsDto> riskAssessmentFactorDtoList = (List<RiskFactorsDto>) sQLQuery.list();
		if (ObjectUtils.isEmpty(riskAssessmentFactorDtoList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("RiskAssessmentFactor.not.found.idEvent", null, Locale.US));
		}
		log.debug("Exiting method getRiskFactorsDtls in RiskAssessmentFactorDaoImpl");
		return riskAssessmentFactorDtoList;
	}

	/**
	 * Method Name: populateRiskData Method Description:
	 * 
	 * @param idCase
	 * @param idStage
	 * @return RiskAssmtValueDto
	 */
	@Override
	public RiskAssmtValueDto populateRiskData(long idCase, long idStage) {
		RiskAssmtValueDto riskAssmtValueDto = new RiskAssmtValueDto();
		Query query1 = sessionFactory.getCurrentSession().createSQLQuery(populateRiskDatasql)
				.addScalar("factorCode", StandardBasicTypes.STRING).addScalar("nbrFactorOrder", StandardBasicTypes.LONG)
				.addScalar("categoryCode", StandardBasicTypes.STRING).addScalar("areaCode", StandardBasicTypes.STRING)
				.addScalar("nbrAreaOrder", StandardBasicTypes.LONG)
				.addScalar("nbrCategoryOrder", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(RiskAssmtValueDto.class));

		riskAssmtValueDto = (RiskAssmtValueDto) query1.list().get(0);

		return riskAssmtValueDto;
	}

	/**
	 * Method Name: addRiskAssmtDetails Method Description:
	 * 
	 * @param riskAssmtValueBean
	 * @return result
	 */
	@Override
	public long addRiskAssmtDetails(RiskAssmtValueDto riskAssmtValueDto) {

		RiskAssessment riskAssessment = new RiskAssessment();
		riskAssessment.setIdEvent(riskAssmtValueDto.getIdEvent());
		Stage stage = new Stage();
		stage.setIdStage(riskAssmtValueDto.getIdStage());
		riskAssessment.setStage(stage);
		riskAssessment.setIdCase(riskAssmtValueDto.getIdCase());
		if (riskAssmtValueDto.getPurpose() != null) {
			riskAssessment.setCdRiskAssmtPurpose(riskAssmtValueDto.getPurpose());
		}
		if (riskAssmtValueDto.getFinding() != null) {
			riskAssessment.setCdRiskAssmtRiskFind(riskAssmtValueDto.getFinding());
		}

		riskAssessment.setIndRiskAssmtIntranet("M");
		riskAssessment.setNbrVersion((byte) 2);
		if (riskAssmtValueDto.getNeglectSummary() != null) {
			riskAssessment.setTxtAbuseNeglSummary(riskAssmtValueDto.getNeglectSummary());
		}
		if (riskAssmtValueDto.getAbuseNeglHistEffect() != null) {
			riskAssessment.setTxtAbuseNeglHstryEffect(riskAssmtValueDto.getAbuseNeglHistEffect());
		}
		if (riskAssmtValueDto.getIndAbuseNeglComp() != null) {
			riskAssessment.setIndAbuseNeglSearchComp(riskAssmtValueDto.getIndAbuseNeglComp());
		}
		if (riskAssmtValueDto.getIndAbuseNeglFound() != null) {
			riskAssessment.setIndAbuseNeglHistryFound(riskAssmtValueDto.getIndAbuseNeglFound());
		}
		if (riskAssmtValueDto.getFindingRationale() != null) {
			riskAssessment.setTxtFindingRationale(riskAssmtValueDto.getFindingRationale());
		}
		if (riskAssmtValueDto.getFactorsControlled() != null) {
			riskAssessment.setTxtFactorsControlled(riskAssmtValueDto.getFactorsControlled());
		}

		addRiskAssmtDtls(riskAssmtValueDto, riskAssessment);
		long result = (long) sessionFactory.getCurrentSession().save(riskAssessment);
		return result;

	}

	/**
	 * Method Name: addRiskAssmtDtls Method Description:
	 * 
	 * @param riskAssmtValueDto
	 * @param riskAssessment
	 */
	private void addRiskAssmtDtls(RiskAssmtValueDto riskAssmtValueDto, RiskAssessment riskAssessment) {
		riskAssessment.setDtLastUpdate(new Date());
		if (null != riskAssmtValueDto.getIndAbuseNeglPrevINV()) {
			riskAssessment.setIndAbuseNeglPrevInv(riskAssmtValueDto.getIndAbuseNeglPrevINV());
		}
		if (riskAssmtValueDto.getCriminalHistEffect() != null) {
			riskAssessment.setTxtCriminalHstryEffect(riskAssmtValueDto.getCriminalHistEffect());
		}
		if (riskAssmtValueDto.getAbuseNeglExtent() != null) {
			riskAssessment.setTxtExtentAbuseNegl(riskAssmtValueDto.getAbuseNeglExtent());
		}
		if (riskAssmtValueDto.getAbuseNeglCircumstance() != null) {
			riskAssessment.setTxtCircumstanceAbuseNegl(riskAssmtValueDto.getAbuseNeglCircumstance());
		}
		if (riskAssmtValueDto.getChildFunction() != null) {
			riskAssessment.setTxtChildFunction(riskAssmtValueDto.getChildFunction());
		}
		if (riskAssmtValueDto.getParentDailyFunction() != null) {
			riskAssessment.setTxtParentDailyFunction(riskAssmtValueDto.getParentDailyFunction());
		}
		if (riskAssmtValueDto.getParentPractices() != null) {
			riskAssessment.setTxtParentPractices(riskAssmtValueDto.getParentPractices());
		}
		if (riskAssmtValueDto.getParentDiscipline() != null) {
			riskAssessment.setTxtParentDiscipline(riskAssmtValueDto.getParentDiscipline());
		}
	}

	/**
	 * Method Name: addAreaDetails Method Description:
	 * 
	 * @param idRiskEvent
	 * @param formFactorBean
	 * @return long
	 */
	@Override
	public long addAreaDetails(long idRiskEvent, RiskAssmtValueDto formFactorBean) {
		RiskArea riskArea = new RiskArea();
		int newRiskAreaId = 0;
		riskArea.setIdRiskArea(newRiskAreaId);
		if (idRiskEvent != 0) {
			Event event = new Event();
			event.setIdEvent(idRiskEvent);
			riskArea.setEvent(event);
		}
		Stage stage = new Stage();
		stage.setIdStage(formFactorBean.getIdStage());
		riskArea.setStage(stage);
		riskArea.setIdCase(formFactorBean.getIdCase());
		if (formFactorBean.getAreaCode() != null) {
			riskArea.setCdRiskArea(formFactorBean.getAreaCode());
		}
		if (formFactorBean.getAreaScaleOfConcern() != null) {
			riskArea.setCdRiskAreaConcernScale(formFactorBean.getAreaScaleOfConcern());
		}
		if (formFactorBean.getAreaTxtScaleConcern() != null) {
			riskArea.setTxtConcernScale(formFactorBean.getAreaTxtScaleConcern());
		}
		riskArea.setDtLastUpdate(new Date());
		long result = (long) sessionFactory.getCurrentSession().save(riskArea);
		return result;
	}

	/**
	 * Method Name: addCategoryDetails Method Description:
	 * 
	 * @param idRiskEvent
	 * @param newRiskAreaId
	 * @param formFactorBean
	 * @return long
	 */
	@Override
	public long addCategoryDetails(long idRiskEvent, long newRiskAreaId, RiskAssmtValueDto formFactorBean) {
		int newRiskCategoryId = 0;
		RiskCategory riskCategory = new RiskCategory();
		riskCategory.setIdRiskCategory(newRiskCategoryId);
		riskCategory.setIdRiskCategArea(newRiskAreaId);
		Event event = new Event();
		event.setIdEvent(idRiskEvent);
		riskCategory.setEvent(event);
		Stage stage = new Stage();
		stage.setIdStage((long) formFactorBean.getIdStage());
		riskCategory.setStage(stage);
		riskCategory.setIdCase((long) formFactorBean.getIdCase());
		if (formFactorBean.getCategoryScaleOfConcern() != null) {
			riskCategory.setCdRiskCategConcernScale(formFactorBean.getCategoryScaleOfConcern());
		}
		riskCategory.setCdRiskCateg(formFactorBean.getCategoryCode());
		riskCategory.setDtLastUpdate(formFactorBean.getCategoryDateLastUpdate());
		long result = (long) sessionFactory.getCurrentSession().save(riskCategory);
		return result;
	}

	/**
	 * Method Name: addFactorDetails Method Description:
	 * 
	 * @param idRiskEvent
	 * @param newRiskAreaId
	 * @param newRiskCategoryId
	 * @param formFactorBean
	 * @return long
	 */
	@Override
	public long addFactorDetails(long idRiskEvent, long newRiskAreaId, long newRiskCategoryId,
			RiskAssmtValueDto formFactorBean) {
		RiskFactors riskFactors = new RiskFactors();
		if (ServiceConstants.SERVER_IMPACT) {
			riskFactors.setIdRiskFactor(0);
		}
		riskFactors.setIdPerson(0);
		riskFactors.setIdEvent(idRiskEvent);
		Stage stage = new Stage();
		stage.setIdStage((long) formFactorBean.getIdStage());
		riskFactors.setStage(stage);
		riskFactors.setCdRiskFactor(formFactorBean.getFactorCode());
		riskFactors.setIdCase((long) formFactorBean.getIdCase());
		if (formFactorBean.getFactorResponse() != null) {
			riskFactors.setCdRiskFactorResponse(formFactorBean.getFactorResponse());
		}
		riskFactors.setCdRiskFactorCateg(formFactorBean.getCategoryCode());
		riskFactors.setIdRiskFactorArea((long) newRiskAreaId);
		riskFactors.setIdRiskFactorCateg((long) newRiskCategoryId);
		riskFactors.setDtLastUpdate(new Date());

		long result = (long) sessionFactory.getCurrentSession().save(riskFactors);
		return result;
	}

	/**
	 * Method Name: getInvstActionQuestion Method Description:CINV04D - This DAM
	 * retrieves the InvstActionQuestion based on the eventId
	 * 
	 * @param idEvent
	 * @return List<InvstActionQuestion>
	 */
	@SuppressWarnings("unchecked")
	public List<InvstActionQuestion> getInvstActionQuestions(long idEvent) {
		Criteria criteria = (Criteria) sessionFactory.getCurrentSession().createCriteria(InvstActionQuestion.class);
		Event event = new Event();
		event.setIdEvent(idEvent);
		criteria.add(Restrictions.eq("event", event));
		criteria.addOrder(Order.asc("cdInvstActionQuest"));
		List<InvstActionQuestion> invstActionQuestionList = criteria.list();
		return invstActionQuestionList;

	}

	/**
	 * Method Name: getRiskFactor Method Description:CINV65D - This DAM
	 * retrieves the Risk Factors for the given idEvent and idPerson
	 * 
	 * @param idEvent
	 * @param idPerson
	 * @return List<RiskFactors>
	 */
	@SuppressWarnings("unchecked")
	public List<RiskFactors> getRiskFactor(Long idEvent, Long idPerson) {
		Criteria criteria = (Criteria) sessionFactory.getCurrentSession().createCriteria(RiskFactors.class);
		criteria.add(Restrictions.eq("idEvent", idEvent));
		criteria.add(Restrictions.eq("idPerson", idPerson));
		List<RiskFactors> riskFactorsList = criteria.list();
		return riskFactorsList;
	}

	/**
	 * Method Name: getRiskAssessment Method Description:CINV64D - This DAM
	 * retrieves the Risk Assessment for the given idEvent
	 * 
	 * @param idEvent
	 * @return List<RiskAssessment>
	 */
	@SuppressWarnings("unchecked")
	public List<RiskAssessment> getRiskAssessment(Long idEvent) {
		Criteria criteria = (Criteria) sessionFactory.getCurrentSession().createCriteria(RiskAssessment.class);
		criteria.add(Restrictions.eq("idEvent", idEvent));
		List<RiskAssessment> riskAssessmentList = criteria.list();
		return riskAssessmentList;
	}

	/**
	 * 
	 * Method Name: queryRiskAssmt Method Description: This method retrieves
	 * data risk assessment details
	 * 
	 * @param idStage
	 * @param idCase
	 * @param idEvent
	 * @param nbrVersion
	 * @return List<RiskAssmtDtlDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskAssmtDtlDto> queryRiskAssmt(Long idStage, Long idCase, Long idEvent, Long nbrVersion) {

		log.debug("Entering method queryRiskAssmt in RiskAssessmentDaoImpl");
		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryRiskAssmtsql)
				.addScalar("idRiskFactor", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("cdRiskFactor", StandardBasicTypes.STRING)
				.addScalar("cdRiskFactorResponse", StandardBasicTypes.STRING)
				.addScalar("txtRiskFactorComment", StandardBasicTypes.STRING)
				.addScalar("dtFactorLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idRiskCategory", StandardBasicTypes.LONG)
				.addScalar("cdRiskCateg", StandardBasicTypes.STRING)
				.addScalar("cdRiskCategConcernScale", StandardBasicTypes.STRING)
				.addScalar("dtCategLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idRiskArea", StandardBasicTypes.LONG).addScalar("cdRiskArea", StandardBasicTypes.STRING)
				.addScalar("cdRiskAreaConcernScale", StandardBasicTypes.STRING)
				.addScalar("dtAreaLastUpdate", StandardBasicTypes.DATE)
				.addScalar("txtConcernScale", StandardBasicTypes.STRING)
				.addScalar("cdRiskAssmtPurpose", StandardBasicTypes.STRING)
				.addScalar("cdRiskAssmtRiskFind", StandardBasicTypes.STRING)
				.addScalar("dtAssmtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("nbrVersion", StandardBasicTypes.LONG)
				.addScalar("txtAbuseNeglSummary", StandardBasicTypes.STRING)
				.addScalar("txtAbuseNeglHstryEffect", StandardBasicTypes.STRING)
				.addScalar("indAbuseNeglSearchComp", StandardBasicTypes.STRING)
				.addScalar("indAbuseNeglHistryFound", StandardBasicTypes.STRING)
				.addScalar("txtFindingRationale", StandardBasicTypes.STRING)
				.addScalar("txtFactorsControlled", StandardBasicTypes.STRING)
				.addScalar("indAbuseNeglPrevInv", StandardBasicTypes.STRING)
				.addScalar("txtCriminalHstryEffect", StandardBasicTypes.STRING)
				.addScalar("txtExtentAbuseNegl", StandardBasicTypes.STRING)
				.addScalar("txtCircumstanceAbuseNgel", StandardBasicTypes.STRING)
				.addScalar("txtChildFunction", StandardBasicTypes.STRING)
				.addScalar("txtParentDailyFunction", StandardBasicTypes.STRING)
				.addScalar("txtParentPractices", StandardBasicTypes.STRING)
				.addScalar("txtParentDiscipline", StandardBasicTypes.STRING)
				.addScalar("txtFactor", StandardBasicTypes.STRING).addScalar("nbrFactorOrder", StandardBasicTypes.LONG)
				.addScalar("txtCategory", StandardBasicTypes.STRING)
				.addScalar("nbrCategoryOrder", StandardBasicTypes.LONG).addScalar("txtArea", StandardBasicTypes.STRING)
				.addScalar("nbrArea", StandardBasicTypes.LONG).addScalar("dtEventLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setParameter("idCase", idCase).setParameter("idEvent", idEvent).setParameter("nbrVersion", nbrVersion)
				.setResultTransformer(Transformers.aliasToBean(RiskAssmtDtlDto.class));

		List<RiskAssmtDtlDto> riskAssmtDtlDtoList = (List<RiskAssmtDtlDto>) sQLQuery.list();
		log.debug("Exiting method queryRiskAssmt in RiskAssessmentDaoImpl");
		return riskAssmtDtlDtoList;
	}

	/**
	 * Method Name: queryRiskAssmtExists Method Description: Query the Risk
	 * Assessment to check if Risk Assessment already exists
	 * 
	 * @param idStage
	 * @param idCase
	 * @return RiskAssmtDtlDto
	 */
	@SuppressWarnings("unchecked")
	public RiskAssmtDtlDto queryRiskAssmtExists(Long idStage, Long idCase) {

		log.debug("Entering method queryRiskAssmtExists in RiskAssessmentDaoImpl");
		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryRiskAssmtExistssql)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("nbrVersion", StandardBasicTypes.LONG)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setParameter("idCase", idCase).setResultTransformer(Transformers.aliasToBean(RiskAssmtDtlDto.class));

		RiskAssmtDtlDto riskAssmtDtlDto = (RiskAssmtDtlDto) sQLQuery.uniqueResult();
		log.debug("Exiting method queryRiskAssmtExists in RiskAssessmentDaoImpl");
		return riskAssmtDtlDto;
	}

	/**
	 * Method Name: queryPageData Method Description: Query the data needed to
	 * create the Risk Assessment page.
	 * 
	 * @param nbrVersion
	 * @return List<RiskAssmtDtlDto>
	 */
	@SuppressWarnings("unchecked")
	public List<RiskAssmtDtlDto> queryPageData(Long nbrVersion) {

		log.debug("Entering method queryPageData in RiskAssessmentDaoImpl");
		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryPageDatasql)
				.addScalar("cdRiskArea", StandardBasicTypes.STRING).addScalar("txtArea", StandardBasicTypes.STRING)
				.addScalar("nbrArea", StandardBasicTypes.LONG).addScalar("cdRiskCateg", StandardBasicTypes.STRING)
				.addScalar("txtCategory", StandardBasicTypes.STRING)
				.addScalar("nbrCategoryOrder", StandardBasicTypes.LONG)
				.addScalar("cdRiskFactor", StandardBasicTypes.STRING).addScalar("txtFactor", StandardBasicTypes.STRING)
				.addScalar("nbrFactorOrder", StandardBasicTypes.LONG).setParameter("nbrVersion", nbrVersion)
				.setResultTransformer(Transformers.aliasToBean(RiskAssmtDtlDto.class));

		List<RiskAssmtDtlDto> riskAssmtDtlDtoList = (List<RiskAssmtDtlDto>) sQLQuery.list();
		log.debug("Exiting method queryPageData in RiskAssessmentDaoImpl");
		return riskAssmtDtlDtoList;
	}

	/**
	 * Method Name: checkRiskAssmtTaskCode SIR 24696, Check the stage table to
	 * see if INV stage is closed and event table, if it has task code for Risk
	 * Assessment.
	 * 
	 * @param idStage
	 * @param idCase
	 * @return RiskAssmtDtlDto
	 */
	public RiskAssmtDtlDto checkRiskAssmtTaskCode(Long idStage, Long idCase, Long idEvent) {
		RiskAssmtDtlDto riskAssmtDtlDto = null;
		log.debug("Entering method checkRiskAssmtTaskCode in RiskAssessmentDaoImpl");
		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(checkRiskAssmtTaskCodesql)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("cdTask", StandardBasicTypes.STRING)
				.addScalar("indStageClose", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setParameter("idCase", idCase).setResultTransformer(Transformers.aliasToBean(RiskAssmtDtlDto.class));

		List<RiskAssmtDtlDto> riskAssmtDtlDtoList = (List<RiskAssmtDtlDto>) sQLQuery.list();
		if (!CollectionUtils.isEmpty(riskAssmtDtlDtoList)) {
			riskAssmtDtlDto = riskAssmtDtlDtoList.get(0);
		}
		log.debug("Exiting method checkRiskAssmtTaskCode in RiskAssessmentDaoImpl");
		return riskAssmtDtlDto;
	}

	/**
	 * Method Name: checkRiskAssmtTaskCode Query the IND_RISK_ASSMT_INTRANET
	 * column on the RISK_ASSESSMENT table to determine if the Risk Assessment
	 * was created using IRA or IMPACT. has task code for Risk Assessment.
	 * 
	 * @param idStage
	 * @param idCase
	 * @return RiskAssmtDtlDto
	 */
	public RiskAssmtDtlDto checkIfRiskAssmtCreatedUsingIRA(Long idStage, Long idCase) {

		log.debug("Entering method checkIfRiskAssmtCreatedUsingIRA in RiskAssessmentDaoImpl");
		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryCreatedUsingIRADatasql)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("cdTask", StandardBasicTypes.STRING)
				.addScalar("indRiskAssmtIntranet", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setParameter("idCase", idCase).setResultTransformer(Transformers.aliasToBean(RiskAssmtDtlDto.class));

		RiskAssmtDtlDto riskAssmtDtlDto = (RiskAssmtDtlDto) sQLQuery.uniqueResult();
		log.debug("Exiting method checkIfRiskAssmtCreatedUsingIRA in RiskAssessmentDaoImpl");
		return riskAssmtDtlDto;
	}

}
