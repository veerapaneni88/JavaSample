package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casemanagement.dao.CpsCheckListDao;
import us.tx.state.dfps.service.casemanagement.dto.CpsCheckListInDto;
import us.tx.state.dfps.service.casemanagement.dto.CpsCheckListOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CSUB64S Aug
 * 21, 2017- 3:58:55 PM © 2017 Texas Department of Family and Protective
 * Services.
 */
@Repository
public class CpsCheckListDaoImpl implements CpsCheckListDao {

	/** The session factory. */
	@Autowired
	private SessionFactory sessionFactory;

	/** The csesc 9 d QUER ydam. */
	@Value("${CpsCheckListDaoImpl.csesc9dQUERYdam}")
	private String csesc9dQUERYdam;

	/**
	 * Select from CPS_CHECKLIST BY ID_STAGE.
	 *
	 * @param pInputDataRec
	 *            the input data rec
	 * @return liCsesc9doDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsCheckListOutDto> csesc9dQUERYdam(CpsCheckListInDto pInputDataRec) {
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(csesc9dQUERYdam)
				.addScalar("idCpsChecklist", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("dtFirstReferral", StandardBasicTypes.DATE)
				.addScalar("indSvcRefChklstNoRef").addScalar("cdFamilyResponse").addScalar("txtChklstComments")
				.addScalar("annualHouseholdIncome", StandardBasicTypes.DOUBLE)
				.addScalar("numberInHousehold", StandardBasicTypes.LONG).addScalar("indIncomeQualification")
				.addScalar("indEligVerifiedByStaff").addScalar("indProblemNeglect").addScalar("indCitizenshipVerify")
				.addScalar("indChildRmvlReturn").addScalar("cdEarlyTermRsn")
				.addScalar("dtEligStart", StandardBasicTypes.DATE).addScalar("dtEligEnd", StandardBasicTypes.DATE)
				.setResultTransformer(Transformers.aliasToBean(CpsCheckListOutDto.class));
		sQLQuery1.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		return (List<CpsCheckListOutDto>) sQLQuery1.list();
	}
}
