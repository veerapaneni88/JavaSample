/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Data Access Implementation Class for select FSNA and select FSNA Evaluation Class
 *Jul 6, 2018- 10:26:15 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.familyplanfsna.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.familyplanfsna.dao.FPSelectFSNADao;
import us.tx.state.dfps.service.familyplanfsna.dto.SelectFSNADto;
import us.tx.state.dfps.service.familyplanfsna.dto.SelectFSNAValidationDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Data Access
 * Implementation Class for select FSNA and select FSNA Evaluation Class> Jul 6,
 * 2018- 10:26:15 AM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class FPSelectFSNADaoImpl implements FPSelectFSNADao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${fpSelectFSNADao.selectFSNAFPR}")
	private String selectFSNAFPRSql;

	@Value("${fpSelectFSNADao.selectFSNAFREandFSU}")
	private String selectFSNAFREandFSUSql;

	@Value("${fpSelectFSNADao.selectFSNAValidation}")
	private String selectFSNAValidationSql;

	public static final String FPR = "FPR";
	public static final String FRE = "FRE";
	public static final String FSU = "FSU";

	/**
	 * Method Name: Method to fetch select FSNA and select FSNA Evaluation List
	 * for FPR, FRE and FSu Stages
	 * 
	 * @param List<SelectFSNADto>
	 * @return idStage, cdStage
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SelectFSNADto> getFPFsnaList(Long idStage, String cdStage) {
		List<SelectFSNADto> fsnaFprList = null;
		if (FPR.equals(cdStage)) {
			SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectFSNAFPRSql)
					.addScalar("assessmentType", StandardBasicTypes.STRING)
					.addScalar("assessmentDesc", StandardBasicTypes.STRING)
					.addScalar("primaryParentName", StandardBasicTypes.STRING)
					.addScalar("secondaryParentName", StandardBasicTypes.STRING)
					.addScalar("createdPersonName", StandardBasicTypes.STRING)
					.addScalar("primaryParentId", StandardBasicTypes.LONG)
					.addScalar("secondaryParentId", StandardBasicTypes.LONG)
					.addScalar("createdPersonId", StandardBasicTypes.LONG).addScalar("stageId", StandardBasicTypes.LONG)
					.addScalar("caseId", StandardBasicTypes.LONG)
					.addScalar(ServiceConstants.IDEVENT, StandardBasicTypes.LONG)
					.setParameter(ServiceConstants.idStage, idStage)
					.setResultTransformer(Transformers.aliasToBean(SelectFSNADto.class)));
			fsnaFprList = (List<SelectFSNADto>) sQLQuery.list();
		} else if (FRE.equals(cdStage) || FSU.equals(cdStage)) {
			SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectFSNAFREandFSUSql)
					.addScalar("assessmentType", StandardBasicTypes.STRING)
					.addScalar("assessmentDesc", StandardBasicTypes.STRING)
					.addScalar("primaryParentName", StandardBasicTypes.STRING)
					.addScalar("secondaryParentName", StandardBasicTypes.STRING)
					.addScalar("createdPersonName", StandardBasicTypes.STRING)
					.addScalar("primaryParentId", StandardBasicTypes.LONG)
					.addScalar("secondaryParentId", StandardBasicTypes.LONG)
					.addScalar("createdPersonId", StandardBasicTypes.LONG).addScalar("stageId", StandardBasicTypes.LONG)
					.addScalar("caseId", StandardBasicTypes.LONG)
					.addScalar(ServiceConstants.IDEVENT, StandardBasicTypes.LONG)
					.setParameter(ServiceConstants.idStage, idStage)
					.setResultTransformer(Transformers.aliasToBean(SelectFSNADto.class)));
			fsnaFprList = (List<SelectFSNADto>) sQLQuery.list();
		}
		return fsnaFprList;
	}

	/**
	 * Method Name: Method to fetch select FSNA and select FSNA Evaluation
	 * Validation View for FSU, FPR and FRE Stages
	 * 
	 * @param List<SelectFSNAValidationDto>
	 * @return idStage
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SelectFSNAValidationDto> getFSNAValidation(Long idStage) {
		List<SelectFSNAValidationDto> fsnaValidationList = null;
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectFSNAValidationSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("dtEventLastUpdate", StandardBasicTypes.DATE).setParameter(ServiceConstants.idStage, idStage)
				.setResultTransformer(Transformers.aliasToBean(SelectFSNAValidationDto.class)));
		fsnaValidationList = (List<SelectFSNAValidationDto>) sQLQuery.list();
		return fsnaValidationList;
	}

}
