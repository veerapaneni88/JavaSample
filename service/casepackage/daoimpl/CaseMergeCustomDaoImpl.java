package us.tx.state.dfps.service.casepackage.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.service.casepackage.dao.CaseMergeCustomDao;
import us.tx.state.dfps.service.casepackage.dto.CaseMergeDetailDto;
import us.tx.state.dfps.service.casepackage.dto.PersonMergeDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

@Repository
public class CaseMergeCustomDaoImpl implements CaseMergeCustomDao {

	@Value("${CaseMerge.getCaseMergeDetails}")
	private transient String getCaseMergeDetailsSql;

	@Value("${CaseMerge.getCaseMergeByIdCaseMergeTo}")
	private String getCaseMergeByIdCaseMergeToSql;

	@Value("${CaseMerge.retrievRecentPersonId}")
	private String retrievRecentPersonId;

	@Autowired
	private SessionFactory sessionFactory;

	public CaseMergeCustomDaoImpl() {

	}

	/**
	 * This call will retrieve a full row using ID CASE as an input.
	 * 
	 * Service name- CCFC39S
	 * 
	 * @param uidcase
	 * @param criteria
	 * @return List<RowCaseMergeDetailDto>
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CaseMergeDetailDto> getCaseMergeDetails(Long uidcase, String criteria) throws DataNotFoundException {

		List<CaseMergeDetailDto> rowCaseMergeDetailList = new ArrayList<CaseMergeDetailDto>();

		String sql = getCaseMergeDetailsSql.replaceAll("TOKENSTARTSWITH", criteria);

		rowCaseMergeDetailList = (List<CaseMergeDetailDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(sql.toString()).setParameter("hi_ulIdCase", uidcase))
						.addScalar("idCaseMerge", StandardBasicTypes.LONG)
						.addScalar("idCaseMergeFrom", StandardBasicTypes.LONG)
						.addScalar("idCaseMergeTo", StandardBasicTypes.LONG)
						.addScalar("idCaseMergePersMrg", StandardBasicTypes.LONG)
						.addScalar("idCaseMergePersSplit", StandardBasicTypes.LONG)
						.addScalar("indCaseMergePending", StandardBasicTypes.STRING)
						.addScalar("dtCaseMerge", StandardBasicTypes.DATE)
						.addScalar("dtCaseMergeSplit", StandardBasicTypes.DATE)
						.addScalar("scrNmCaseMrgFrom", StandardBasicTypes.STRING)
						.addScalar("scrNmCaseMrgTo", StandardBasicTypes.STRING)
						.addScalar("scrNmSplitWorker", StandardBasicTypes.STRING)
						.addScalar("scrMergeWorker", StandardBasicTypes.STRING)
						.addScalar("indCaseMergeInv", StandardBasicTypes.STRING)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(CaseMergeDetailDto.class)).list();

		return rowCaseMergeDetailList;
	}

	/**
	 * This call will retrieve a full row using ID CASE as an input.
	 * 
	 * Service name- CCFC39S
	 * 
	 * @param uidcase
	 * @param criteria
	 * @return List<PersonMergeDto>
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonMergeDto> retrievRecentPersonId(Long idCase) throws DataNotFoundException {

		return (List<PersonMergeDto>) sessionFactory.getCurrentSession().createSQLQuery(retrievRecentPersonId)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdPersonRole", StandardBasicTypes.STRING)
				.addScalar("cdStageType", StandardBasicTypes.STRING).setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(PersonMergeDto.class)).list();
	}

	/**
	 * This DAM will retrieve a full row using ID CASE MERGE TO as an input.
	 * 
	 * Service name- CCMN20S, DAM name - CLSC68D
	 * 
	 * @param ulIdCaseMergeTo
	 * @return
	 * @throws DataNotFoundException
	 */

	@SuppressWarnings("unchecked")
	public List<CaseMergeDetailDto> getCaseMergeByIdCaseMergeTo(Long ulIdCaseMergeTo) throws DataNotFoundException {
		List<CaseMergeDetailDto> rowCaseMergeDetailList = new ArrayList<CaseMergeDetailDto>();

		Query queryCaseMerge = sessionFactory.getCurrentSession().createSQLQuery(getCaseMergeByIdCaseMergeToSql)
				.addScalar("idCaseMerge", StandardBasicTypes.LONG).addScalar("idCaseMergeFrom", StandardBasicTypes.LONG)
				.addScalar("idCaseMergeTo", StandardBasicTypes.LONG)
				.addScalar("idCaseMergePersMrg", StandardBasicTypes.LONG)
				.addScalar("idCaseMergePersSplit", StandardBasicTypes.LONG)
				.addScalar("idCaseMergeSitFrom", StandardBasicTypes.LONG)
				.addScalar("idCaseMergeStageFrom", StandardBasicTypes.LONG)
				.addScalar("indCaseMergePending", StandardBasicTypes.STRING)
				.addScalar("dtCaseMerge", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCaseMergeSplit", StandardBasicTypes.TIMESTAMP)
				.addScalar("indCaseMergeInv", StandardBasicTypes.STRING)
				.addScalar("indCaseMergeStageSwap", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtStageClose", StandardBasicTypes.TIMESTAMP)
				.setResultTransformer(Transformers.aliasToBean(CaseMergeDetailDto.class));

		queryCaseMerge.setParameter("idCaseMergeTo", ulIdCaseMergeTo);

		rowCaseMergeDetailList = queryCaseMerge.list();

		return rowCaseMergeDetailList;
	}

	/**
	 * This will retrieve all case detail passing case id
	 * 
	 * @param getCapsCaseByid
	 * @return CapsCase
	 * @throws DataNotFoundException
	 */
	@Override
	public CapsCase getCapsCaseByid(Long idCase) throws DataNotFoundException {
		CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().load(CapsCase.class, idCase);
		return capsCase;
	}
}
