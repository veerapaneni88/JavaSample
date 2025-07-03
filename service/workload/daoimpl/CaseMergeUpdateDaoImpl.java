package us.tx.state.dfps.service.workload.daoimpl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.CaseMerge;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.workload.dao.CaseMergeUpdateDao;
import us.tx.state.dfps.service.workload.dto.CaseMergeUpdateDto;
import us.tx.state.dfps.service.workload.dto.EventStageDto;

@Repository
public class CaseMergeUpdateDaoImpl implements CaseMergeUpdateDao {

	@Value("${CaseMergeUpdateDaoImpl.selectMergedCaseSql}")
	private String selectMergedCase;

	@Autowired
	private SessionFactory sessionFactory;

	public CaseMergeUpdateDaoImpl() {
	}

	/**
	 * 
	 * Method Description:excute query of dam-CAUD94D to insert a row in table
	 * CASE_MERGE DAM CAUD94D
	 * 
	 * @param caseMerge
	 * @return CaseMergeOutputDto @
	 */
	public CaseMergeUpdateDto saveCaseMerge(CaseMerge caseMerge) {
		CaseMergeUpdateDto updatedCase = new CaseMergeUpdateDto();
		sessionFactory.getCurrentSession().saveOrUpdate(caseMerge);
		updatedCase.setIdCaseMerge(caseMerge.getIdCaseMerge());
		return updatedCase;
	}

	/**
	 * 
	 * Method Description:excute query of dam-CAUD94D to update a row in table
	 * CASE_MERGE DAM CAUD94D
	 * 
	 * @param caseMerge
	 * @return CaseMergeOutputDto @
	 */
	public CaseMergeUpdateDto updateCaseMerge(CaseMerge caseMerge) {
		CaseMergeUpdateDto updatedCase = new CaseMergeUpdateDto();
		sessionFactory.getCurrentSession().saveOrUpdate(caseMerge);
		updatedCase.setIdCaseMerge(caseMerge.getIdCaseMerge());
		return updatedCase;
	}

	/**
	 * 
	 * Method Description:excute query of dam-CAUD94D to delete a row in table
	 * CASE_MERGE DAM CAUD94D
	 * 
	 * @param caseMerge
	 * @return CaseMergeOutputDto @
	 */
	public CaseMergeUpdateDto deleteCaseMerge(CaseMerge caseMerge) {
		CaseMergeUpdateDto deletedCase = new CaseMergeUpdateDto();
		sessionFactory.getCurrentSession().delete(caseMerge);
		deletedCase.setIdCaseMerge(caseMerge.getIdCaseMerge());
		return deletedCase;
	}

	/**
	 * 
	 * Method Description:excute query of dam-CMSC38D to get all merged cases
	 * for one case
	 * 
	 * @param caseId
	 * @return List<CaseMergeUpdateDto> @
	 */
	@SuppressWarnings("unchecked")
	public List<CaseMergeUpdateDto> searchAllMergedCases(Long idCaseMerge) {
		List<CaseMergeUpdateDto> mergedCaseList = null;
		Query queryCase = sessionFactory.getCurrentSession().createSQLQuery(selectMergedCase)
				.addScalar("idCaseMerge", StandardBasicTypes.LONG).addScalar("idCaseMergeFrom", StandardBasicTypes.LONG)
				.addScalar("idCaseMergeTo", StandardBasicTypes.LONG).addScalar("dtCaseMerge", StandardBasicTypes.DATE)
				.addScalar("dtCaseMergeSplitDate", StandardBasicTypes.DATE)
				.addScalar("idCaseMergePersMrg", StandardBasicTypes.LONG)
				.addScalar("idCaseMergePersSplit", StandardBasicTypes.LONG)
				.addScalar("indCaseMergeInv", StandardBasicTypes.STRING)
				.addScalar("indCaseMergePending", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.setResultTransformer(Transformers.aliasToBean(CaseMergeUpdateDto.class));
		queryCase.setParameter("idCase", idCaseMerge);
		mergedCaseList = queryCase.list();
		return mergedCaseList;
	}

	@Value("${CaseMergeUpdateDaoImpl.selectCaseNameSql}")
	private String selectCaseName;

	/**
	 * 
	 * Method Description:retrieve name of case for ulIdCaseMergeTo DAM-CMSC38D
	 * 
	 * @param idCase
	 * @return @
	 */
	public String searchCaseName(Long idCase) {
		String caseName = null;
		Query queryCase = sessionFactory.getCurrentSession().createQuery(selectCaseName);
		queryCase.setParameter("idCase", idCase);
		return caseName;
	}

	/**
	 * 
	 * Method Description:retrieve name of person for ulIdCaseMergePersSplit or
	 * ulIdCaseMergePersMerge if has value DAM-CMSC38D
	 * 
	 * @param idPerson
	 * @return @
	 */
	@Value("${CaseMergeUpdateDaoImpl.selectPersonNameFullSql}")
	private String selectPersonName;

	public String searchPersonNameFull(Long idPerson) {
		String nmPersonFull = null;
		Query queryCase = sessionFactory.getCurrentSession().createQuery(selectPersonName);
		queryCase.setParameter("idPerson", idPerson);
		nmPersonFull = (String) queryCase.uniqueResult();
		return nmPersonFull;
	}

	/**
	 * 
	 * 
	 * ulIdCaseMergePersMerge if has value DAM-CMSC38D
	 * 
	 * @param idPerson
	 * @return @
	 */
	@Value("${CaseMergeUpdateDaoImpl.selectCaseStageSql}")
	private String selectCaseStage;

	/**
	 * Method Description: This methos gets the records from EVENT & STAGE
	 * tables for the given idCase, event status and event type. LEGACY TUXEDO
	 * MODULE NAME: CLSS86D
	 * 
	 * @param idPerson
	 * @return @
	 */
	public EventStageDto searchCaseStage(Long idCase, String pendStatus, String programCCL) {
		Query queryCase = sessionFactory.getCurrentSession().createSQLQuery(selectCaseStage)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idEventPerson", StandardBasicTypes.LONG)
				.addScalar("idEventStage", StandardBasicTypes.LONG)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("cdEventType", StandardBasicTypes.STRING).addScalar("eventDescr", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("indStageClose", StandardBasicTypes.BOOLEAN)
				.setResultTransformer(Transformers.aliasToBean(EventStageDto.class));
		queryCase.setParameter("idCase", idCase);
		queryCase.setParameter("cdEventType", programCCL);
		queryCase.setParameter("cdEventStatus", pendStatus);
		return (EventStageDto) queryCase.uniqueResult();
	}

	@Value("${CaseMergeUpdateDaoImpl.selectReverseMergeCaseSql}")
	private String selectReverseMergeCaseSql;

	/**
	 * CLSC67D : This DAM will retrieve a full row from the Case Merge table
	 * based on Id Case Merge From.
	 * 
	 * @param caseMergeId
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	public List<CaseMergeUpdateDto> checkForReverseMerge(Long caseMergeId) {
		List<CaseMergeUpdateDto> mergedCaseList = null;
		Query queryCase = sessionFactory.getCurrentSession().createSQLQuery(selectReverseMergeCaseSql)
				.addScalar("idCaseMerge", StandardBasicTypes.LONG).addScalar("idCaseMergeFrom", StandardBasicTypes.LONG)
				.addScalar("idCaseMergeTo", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idSituation", StandardBasicTypes.LONG)
				.addScalar("idCaseMergeStageFrom", StandardBasicTypes.LONG)
				.addScalar("indCaseMergeStageSwap", StandardBasicTypes.STRING)
				.addScalar("idCaseMergePersMrg", StandardBasicTypes.LONG)
				.addScalar("idCaseMergePersSplit", StandardBasicTypes.LONG)
				.addScalar("indCaseMergeInv", StandardBasicTypes.STRING)
				.addScalar("indCaseMergePending", StandardBasicTypes.STRING)
				.addScalar("dtCaseMerge", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCaseMergeSplitDate", StandardBasicTypes.TIMESTAMP)
				.setResultTransformer(Transformers.aliasToBean(CaseMergeUpdateDto.class));
		queryCase.setParameter("idCase", caseMergeId);
		mergedCaseList = (List<CaseMergeUpdateDto>) queryCase.list();
		return mergedCaseList;
	}

	@Value("${CaseMergeUpdateDaoImpl.selectMergeCountsSql}")
	private String selectMergeCountSql;

	/**
	 * CSESA6D : This DAM checks the number of times that a case has been merged
	 * if it has been merged three times then do not let the next merge thru
	 * update ErrorCount
	 * 
	 * @param caseMergeId
	 * @return @
	 */
	public Integer checkForMergeCounts(Long caseMergeId) {
		Integer forMergeCounts = 0;
		Query queryCase = sessionFactory.getCurrentSession().createSQLQuery(selectMergeCountSql);
		queryCase.setParameter("idCaseMergeFrom", caseMergeId);
		if (!TypeConvUtil.isNullOrEmpty(queryCase.uniqueResult()))
			forMergeCounts = Integer.valueOf(queryCase.uniqueResult().toString());
		return forMergeCounts;
	}

	@Value("${CaseMergeUpdateDaoImpl.selectRecDstrySql}")
	private String selectRecDstrySql;

	/**
	 * CSESF2D : This function will check whether the records retention
	 * destruction date of both Case Merge To or Case Merge From is in the
	 * future. Also compare the records retention destruction date of the first
	 * case with the date of case opened of other case, then throw appropriate
	 * error message once users click on the validation button in the Case
	 * Merge/Split Detail window.
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Date> checkForRecDstryDate(Long caseMergeId) {
		Query queryCase = sessionFactory.getCurrentSession().createSQLQuery(selectRecDstrySql)
				.addScalar("caseOpenDate", StandardBasicTypes.TIMESTAMP)
				.addScalar("recDstryDate", StandardBasicTypes.TIMESTAMP).setParameter("idCase", caseMergeId)
				.setParameter("dtRecRtnDstryActual", new Date()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return (Map<String, Date>) queryCase.uniqueResult();
	}
}
