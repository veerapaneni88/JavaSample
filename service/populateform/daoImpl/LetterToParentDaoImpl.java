package us.tx.state.dfps.service.populateform.daoImpl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ConclusionNotifctnInfo;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.service.casemanagement.daoimpl.CPSInvCnlsnDaoImpl;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.cpsinv.dto.StagePersonValueDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;
import us.tx.state.dfps.service.populateform.dao.LetterToParentDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Makes
 * database calls and returns results to services May 29, 2018- 10:23:28 AM ©
 * 2017 Texas Department of Family and Protective Services
 * ********Change History**********
 * 01/02/2023 thompswa artf238090 PPM 73576 add getAllegationsList,getConclusionNotifctnInfoList.
 */
@Repository
public class LetterToParentDaoImpl implements LetterToParentDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	CaseUtils caseUtils;

	@Value("${LetterToParentDaoImpl.victimIndicator}")
	private String victimIndicatorSql;

	@Value("${LetterToParentDaoImpl.getPerpsInStage}")
	private String getPerpsInStageSql;

	@Value("${LetterToParentDaoImpl.getParentInfo}")
	private String getParentInfoSql;

	@Value("${LetterToParentDaoImpl.getDispositions}")
	private String getDispositionsSql;

	@Value("${LetterToParentDaoImpl.getAllegTypes}")
	private String getAllegTypesSql;

	/**
	 * Method Name: victimIndicator Method Description: Victim Indicator, 'Y',
	 * if victim age < 3 years. (DAM: CLSCE6D)
	 * 
	 * @param idStage
	 * @return String
	 */
	@Override
	public String victimIndicator(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(victimIndicatorSql).setParameter("idStage",
				idStage);
		// For some stages the query returning null, if there
		// is no record present table for the passing stageId - Modified the
		// code for warranty defect 11089
		Character victimId = (Character) query.uniqueResult();
		return !ObjectUtils.isEmpty(victimId) ? victimId.toString():null;		
	}

	/**
	 * Method Name: getPerpsInStage Method Description: Gets ids of perps to
	 * check for ruled out allegations (DAM: CLSCE8D)
	 * 
	 * @param idPerson
	 * @param idAllegationStage
	 * @return List<FacilityAllegationInfoDto>
	 */
	@Override
	public List<FacilityAllegationInfoDto> getPerpsInStage(Long idPerson, Long idAllegationStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPerpsInStageSql
						.replace("PARENTAL_REL_INT", TypeConvUtil.getQuotedSqlString(ServiceConstants.PARENTAL_REL_INT_ARRAY)))
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("cdAllegDisposition", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setParameter("idAllegationStage", idAllegationStage)
				.setResultTransformer(Transformers.aliasToBean(FacilityAllegationInfoDto.class));
		return (List<FacilityAllegationInfoDto>) query.list();
	}

	/**
	 * Method Name: getParentInfo Method Description: Returns data for an adult
	 * or child parent (DAM: CSECE4D)
	 * 
	 * @param idPerson
	 * @param idStage
	 * @return List<StagePersonValueDto>
	 */
	@Override
	public List<StagePersonValueDto> getParentInfo(Long idPerson, Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getParentInfoSql
						.replace("PARENTAL_REL_INT", TypeConvUtil.getQuotedSqlString(ServiceConstants.PARENTAL_REL_INT_ARRAY)))
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("dtStagePersLink", StandardBasicTypes.DATE)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG)
				.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("stagePersNotes", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("indCaringAdult", StandardBasicTypes.STRING)
				.addScalar("cdMaritalStatus", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.setParameter("idPerson", idPerson).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(StagePersonValueDto.class));
		return (List<StagePersonValueDto>) query.list();
	}

	/**
	 * Method Name: getDispositions Method Description: Returns a list of
	 * dispositions (DAM: CLSSA8D)
	 *
	 * @param idAllegationStage
	 * @param b
	 * @return List<FacilityAllegationInfoDto>
	 */
	@Override
	public List<FacilityAllegationInfoDto> getDispositions(List<String> dispositions, boolean b) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getDispositionsSql)
				.addScalar("cdAllegDisposition", StandardBasicTypes.STRING)
				.addScalar("decodeAllegDisp", StandardBasicTypes.STRING)
				.setParameter("dispType", b ? CodesConstant.CCIVALDS : CodesConstant.CCIVADSP)
				.setParameterList("dispositions", dispositions)
				.setResultTransformer(Transformers.aliasToBean(FacilityAllegationInfoDto.class));
		return (List<FacilityAllegationInfoDto>) query.list();
	}

	/**
	 * Method Name: getAllegTypes Method Description: Get distinct list of
	 * allegation records for a stage. (DAM: CLSSA9D)
	 *
	 * @param idAllegationStage
	 * @param b
	 * @return List<FacilityAllegationInfoDto>
	 */
	public List<FacilityAllegationInfoDto> getAllegTypes(Long idAllegationStage, boolean b) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAllegTypesSql)
				.addScalar("idAllegationStage", StandardBasicTypes.LONG)
				.addScalar("cdAllegType", StandardBasicTypes.STRING)
				.addScalar("decodeAllegType", StandardBasicTypes.STRING)
				.setParameter("allgType", b ? CodesConstant.CCLICALT : CodesConstant.CABTPSP)
				.setParameter("idAllegationStage", idAllegationStage)
				.setResultTransformer(Transformers.aliasToBean(FacilityAllegationInfoDto.class));
		return (List<FacilityAllegationInfoDto>) query.list();
	}

	/**
	 *
	 * Method Name: getConclusionNotifctnInfoList Method Description: fetch event and
	 * ConclusionNotifctnInfoList based on stage id and person id and event cd_type
	 *
	 * @param idStage
	 * @param idPerson
	 * @return anonymous ConclusionNotifctnInfoList
	 */
	public List<ConclusionNotifctnInfo> getConclusionNotifctnInfoList(Long idStage, Long idPerson) {
		Event event = caseUtils.getEvent(idStage, ServiceConstants.INV_CONCLUSION_TASK_CODE);
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ConclusionNotifctnInfo.class);
		criteria.add(Restrictions.eq("idEvent", BigDecimal.valueOf(event.getIdEvent())))
				.add(Restrictions.eq("idPerson", BigDecimal.valueOf(idPerson)));
		List<ConclusionNotifctnInfo> conclusionNotifctnInfoList =  (List<ConclusionNotifctnInfo>) criteria.list();
		return conclusionNotifctnInfoList;
	}
}
