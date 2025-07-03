/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 9, 2018- 4:13:41 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.sscc.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dto.SSCCPlcmtOptCircumListDto;
import us.tx.state.dfps.service.sscc.dao.SSCCOptCircumListDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This is the
 * implementation class for the methods in SSCCOptCircumListDao Aug 9, 2018-
 * 4:13:41 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class SSCCOptCircumListDaoImpl implements SSCCOptCircumListDao {

	@Value("${SSCCOptCircumListDaoImpl.retrieveSSCCOptCircumListSql}")
	private String retrieveSSCCOptCircumListSql;

	@Autowired
	private SessionFactory sessionFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.sscc.optcircum.dao.SSCCOptCircumListDao#
	 * retrieveSSCCOptCircumList(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SSCCPlcmtOptCircumListDto> retrieveSSCCOptCircumList(Long idStage) {
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(retrieveSSCCOptCircumListSql)
				.addScalar("dtRecordedSSCC", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtRecordedDFPS", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtRecorded", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdSSCCPlcmtType", StandardBasicTypes.STRING)
				.addScalar("cdSSCCOptionType", StandardBasicTypes.STRING)
				.addScalar("ind14DayPlcmt", StandardBasicTypes.BOOLEAN).addScalar("indHold", StandardBasicTypes.BOOLEAN)
				.addScalar("nmPlacement", StandardBasicTypes.STRING).addScalar("cdStatus", StandardBasicTypes.STRING)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idEvalPerson", StandardBasicTypes.LONG).addScalar("dtEval", StandardBasicTypes.TIMESTAMP)
				.addScalar("idSSCCPlcmtHeader", StandardBasicTypes.LONG)
				.addScalar("idSSCCReferral", StandardBasicTypes.LONG).addScalar("nbrVersion", StandardBasicTypes.LONG)
				.addScalar("nmProposedBy", StandardBasicTypes.STRING)
				.addScalar("nmEvaluatedBy", StandardBasicTypes.STRING)
				.addScalar("indEnableRescind", StandardBasicTypes.BOOLEAN).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(SSCCPlcmtOptCircumListDto.class));

		// set perso
		return sQLQuery1.list();
	}

}
