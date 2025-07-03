package us.tx.state.dfps.service.casemanagement.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceUpdateStageDao;
import us.tx.state.dfps.service.casepackage.dto.StagePersonLinkUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.StagePersonLinkUpdateOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceUpdateStageDaoImpl Feb 7, 2018- 5:52:22 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class CaseMaintenanceUpdateStageDaoImpl implements CaseMaintenanceUpdateStageDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMaintenanceUpdateStageDaoImpl.strQuery1}")
	private transient String strQuery1;

	private static final Logger log = Logger.getLogger(CaseMaintenanceUpdateStageDaoImpl.class);

	/**
	 * Method Name: updateStagePersonLink Method Description:This Method is used
	 * to update stage person link DAM:Caudf0d
	 * 
	 * @param stagePersonLinkUpdateInDto
	 * @param stagePersonLinkUpdateOutDto
	 * @
	 */
	@Override
	public void updateStagePersonLink(StagePersonLinkUpdateInDto stagePersonLinkUpdateInDto,
			StagePersonLinkUpdateOutDto stagePersonLinkUpdateOutDto) {
		log.debug("Entering method updateStagePersonLink in CaseMaintenanceUpdateStageDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery1)
				.setParameter("idStage", stagePersonLinkUpdateInDto.getIdStage()));
		sQLQuery1.executeUpdate();

		log.debug("Exiting method updateStagePersonLink in CaseMaintenanceUpdateStageDaoImpl");
	}

}
