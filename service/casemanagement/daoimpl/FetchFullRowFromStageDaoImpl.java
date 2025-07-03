package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.casemanagement.dao.FetchFullRowFromStageDao;
import us.tx.state.dfps.service.casepackage.dto.RetrieveFullRowStageInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveFullRowStageOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 22, 2018- 10:41:32 PM © 2017 Texas Department of
 * Family and Protective Services
 */

// Cint40dDaoImpl
@Repository
public class FetchFullRowFromStageDaoImpl implements FetchFullRowFromStageDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FetchFullRowFromStageDaoImpl.strCINT40DCURSORQuery}")
	private String strCINT40DCURSORQuery;

	private static final Logger log = Logger.getLogger(FetchFullRowFromStageDaoImpl.class);

	/**
	 * Method Name: retrieveStage Method Description:This Method is used to
	 * retrieve the Stage information
	 * 
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 * @
	 */
	@Override
	public RetrieveFullRowStageOutputDto retrieveStage(RetrieveFullRowStageInputDto retrieveFullRowStageInputDto) {
		
		log.debug("Entering method retrieveStage in FetchFullRowFromStageDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCINT40DCURSORQuery)
				.addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("cdStageClassification", StandardBasicTypes.STRING)
				.addScalar("cdStageCnty", StandardBasicTypes.STRING)
				.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageInitialPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("cdStageRegion", StandardBasicTypes.STRING)
				.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING)
				.addScalar("cdStageType", StandardBasicTypes.STRING)
				.addScalar("dtDtStageClose", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtDtStageStart", StandardBasicTypes.TIMESTAMP).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idSituation", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idUnit", StandardBasicTypes.LONG).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("txtStagePriorityCmnts", StandardBasicTypes.STRING)
				.addScalar("cdClientAdvised", StandardBasicTypes.STRING).addScalar("indEcs", StandardBasicTypes.STRING)
				.addScalar("indEcsVer", StandardBasicTypes.STRING)
				.addScalar("txtStageClosureCmnts", StandardBasicTypes.STRING)
				.addScalar("indStageClose", StandardBasicTypes.STRING)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtDtClientAdvised", StandardBasicTypes.TIMESTAMP)
				.addScalar("indOpenCaseFoundAtIntake", StandardBasicTypes.STRING)
				.addScalar("indIntakeFormallyScreened", StandardBasicTypes.STRING)
				.setParameter("idStage", retrieveFullRowStageInputDto.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(RetrieveFullRowStageOutputDto.class)));

		return  (RetrieveFullRowStageOutputDto) sQLQuery1.uniqueResult();

	}

}
