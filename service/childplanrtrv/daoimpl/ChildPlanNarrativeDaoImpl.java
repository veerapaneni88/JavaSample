package us.tx.state.dfps.service.childplanrtrv.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.childplan.dto.ChildPlanNarrativeDto;
import us.tx.state.dfps.service.childplanrtrv.dao.ChildPlanNarrativeDao;
import us.tx.state.dfps.service.common.CodesConstant;

@Repository
public class ChildPlanNarrativeDaoImpl implements ChildPlanNarrativeDao {

	@Autowired
	private SessionFactory sessionFactory;

	public static final String CCPTPTBL = "CCPTPTBL";
	public static final String CCPPLNTP = "CCPPLNTP";

	@Value("${ChildPlanNarrativeDaoImpl.sqlToGetCodeFromCodesTable}")
	private String sqlToGetCodeFromCodesTable;

	@Value("${ChildPlanNarrativeDaoImpl.sqlToGetTableNameFromDecode}")
	private String sqlToGetTableNameFromDecode;

	@Value("${ChildPlanNarrativeDaoImpl.sqlToGetNarrative}")
	private String sqlToGetNarrative;

	@Value("${ChildPlanNarrativeDaoImpl.sqlToInsertNarr}")
	private String sqlToInsertNarr;

	@Value("${ChildPlanNarrativeDaoImpl.getTablesToDeleteFrom}")
	private String getTablesToDeleteFrom;

	@Value("${ChildPlanNarrativeDaoImpl.deleteNarrative}")
	private String deleteNarrative;

	/**
	 * Method Name: insertChildPlanNarrative
	 *
	 * Method Description: Retrieve all Child Plan Narrative given an
	 * ID_CHILD_PLAN_EVENT (old) and insert them into the appropriate table
	 * (determined by the CP_PLAN_TYPE passed in) with the new
	 * ID_CHILD_PLAN_EVENT passed in.
	 * 
	 * @param cdCspPlanType
	 * @param idChildPlanEvent
	 * @param idChildPlanEventNew
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void insertChildPlanNarrative(String cdCspPlanType, Long idChildPlanEvent, Long idChildPlanEventNew) {
		if (!ObjectUtils.isEmpty(cdCspPlanType)) {

			SQLQuery sqlQueryForCode = (SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(sqlToGetCodeFromCodesTable).setParameter("codeType", CodesConstant.CCPPLNTP)
					.setParameter("cdCspPlanType", cdCspPlanType)
					.setResultTransformer(Transformers.aliasToBean(String.class));
			String code = (String) sqlQueryForCode.uniqueResult();

			if (!ObjectUtils.isEmpty(code)) {
				SQLQuery sqlQueryForTableName = (SQLQuery) sessionFactory.getCurrentSession()
						.createSQLQuery(sqlToGetTableNameFromDecode).setParameter("codeType", CodesConstant.CCPTPTBL)
						.setParameter("cdCspPlanType", cdCspPlanType)
						.setResultTransformer(Transformers.aliasToBean(String.class));
				List<String> tableNameList = sqlQueryForTableName.list();
				if (!ObjectUtils.isEmpty(tableNameList)) {
					tableNameList.stream().forEach(tableName -> {
						SQLQuery sqlQueryForGetNarr = (SQLQuery) sessionFactory.getCurrentSession()
								.createSQLQuery(sqlToGetNarrative)
								.addScalar("idDocumentTemplate", StandardBasicTypes.LONG)
								.addScalar("idNewEvent", StandardBasicTypes.LONG)
								.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
								.addScalar("dtNewUsed", StandardBasicTypes.DATE)
								.addScalar("documentBlob", StandardBasicTypes.BLOB)
								.setParameter("idEvent", idChildPlanEvent).setParameter("tableName", tableName)
								.setResultTransformer(Transformers.aliasToBean(ChildPlanNarrativeDto.class));
						ChildPlanNarrativeDto childPlanNarrativeDto = (ChildPlanNarrativeDto) sqlQueryForGetNarr
								.uniqueResult();
						SQLQuery sqlQueryInsertNarr = (SQLQuery) sessionFactory.getCurrentSession()
								.createSQLQuery(sqlToInsertNarr).setParameter("idNewEvent", idChildPlanEventNew)
								.setParameter("idDocumentTemplate", childPlanNarrativeDto.getIdDocumentTemplate())
								.setParameter("dtLastUpdate", childPlanNarrativeDto.getDtLastUpdate())
								.setParameter("dtNewUsed", childPlanNarrativeDto.getDtNewUsed())
								.setParameter("documentBlob", childPlanNarrativeDto.getDocumentBlob())
								.setParameter("tableName", tableName)
								.setResultTransformer(Transformers.aliasToBean(String.class));
						sqlQueryInsertNarr.executeUpdate();

					});
				}
			}
		}
	}

	/**
	 * Method Name: deleteChildPlanNarrative
	 *
	 * Method Description: Delete all records (for a given ID_EVENT) from tables
	 * derived from the difference between 2 Plan Types
	 * 
	 * @param cdCspPlanTypeOld
	 * @param cdCspPlanTypeNew
	 * @param idChildPlanEventNew
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deleteChildPlanNarrative(String cdCspPlanTypeOld, String cdCspPlanTypeNew, Long idChildPlanEventNew) {
		SQLQuery sqlQueryForCode = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(sqlToGetCodeFromCodesTable);

		sqlQueryForCode.setParameter("codeType", CodesConstant.CCPPLNTP).setParameter("cdCspPlanType",
				cdCspPlanTypeOld);
		String cdCspPlanTypeOldCode = (String) sqlQueryForCode.uniqueResult();

		sqlQueryForCode.setParameter("codeType", CodesConstant.CCPPLNTP).setParameter("cdCspPlanType",
				cdCspPlanTypeNew);
		;
		String cdCspPlanTypeNewCode = (String) sqlQueryForCode.uniqueResult();

		if (!ObjectUtils.isEmpty(cdCspPlanTypeOldCode) && !ObjectUtils.isEmpty(cdCspPlanTypeNewCode)) {
			SQLQuery getTablesToDeleteFromSql = (SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(getTablesToDeleteFrom).setParameter("codeType", CodesConstant.CCPTPTBL)
					.setParameter("cdCspPlanTypeOld", cdCspPlanTypeOld)
					.setParameter("cdCspPlanTypeNew", cdCspPlanTypeNew);

			List<String> tableNameList = getTablesToDeleteFromSql.list();
			if (!ObjectUtils.isEmpty(tableNameList)) {
				tableNameList.stream().forEach(tableName -> {
					SQLQuery deleteNarrativeSql = (SQLQuery) sessionFactory.getCurrentSession()
							.createSQLQuery(deleteNarrative).setParameter("idEvent", idChildPlanEventNew)
							.setParameter("tableName", tableName);
					deleteNarrativeSql.executeUpdate();
				});
			}
		}
	}
}
