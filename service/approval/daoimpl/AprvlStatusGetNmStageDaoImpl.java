package us.tx.state.dfps.service.approval.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.approval.dto.SaveApprovalStatusNmStageReq;
import us.tx.state.dfps.approval.dto.SaveApprovalStatusNmStageRes;
import us.tx.state.dfps.service.approval.dao.AprvlStatusGetNmStageDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 8,
 * 2018- 3:38:32 PM © 2018 Texas Department of Family and Protective Services
 */
@Repository
public class AprvlStatusGetNmStageDaoImpl implements AprvlStatusGetNmStageDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${AprvlStatusGetNmStageDaoImpl.getnmStage}")
	private String getnmStagesql;

	public static final Logger log = Logger.getLogger(AprvlStatusGetNmStageDaoImpl.class);

	/**
	 * Method Name: getnmStage Method Description: This method retrieves a stage
	 * name from the Stage table based on an ID_stage as an input. DAM NAME:
	 * CCMNJ7D Service Name: CCMN35S
	 * 
	 * @param SaveApprovalStatusNmStageReq
	 * @return SaveApprovalStatusNmStageRes
	 */
	@Override
	public SaveApprovalStatusNmStageRes getNmStage(SaveApprovalStatusNmStageReq saveApprovalStatusNmStageReq) {
		log.debug("Entering method getNmStage in AprvlStatusGetNmStageDaoImpl");

		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getnmStagesql)
				.addScalar("nmStage").setLong("idStage", saveApprovalStatusNmStageReq.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(SaveApprovalStatusNmStageRes.class)));

		SaveApprovalStatusNmStageRes saveApprovalStatusNmStageRes = (SaveApprovalStatusNmStageRes) sqlQuery
				.uniqueResult();

		log.debug("Exiting method getNmStage in AprvlStatusGetNmStageDaoImpl");
		return saveApprovalStatusNmStageRes;
	}
}
