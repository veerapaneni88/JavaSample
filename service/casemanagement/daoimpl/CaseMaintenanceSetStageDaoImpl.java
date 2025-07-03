package us.tx.state.dfps.service.casemanagement.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceSetStageDao;
import us.tx.state.dfps.service.casepackage.dto.UpdateStagePersonLinkInDto;
import us.tx.state.dfps.service.casepackage.dto.UpdateStagePersonLinkOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceSetStageDaoImpl Feb 7, 2018- 5:51:44 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class CaseMaintenanceSetStageDaoImpl implements CaseMaintenanceSetStageDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMaintenanceSetStageDaoImpl.strQuery1}")
	private transient String strQuery1;

	private static final Logger log = Logger.getLogger(CaseMaintenanceSetStageDaoImpl.class);

	/**
	 * Method Name:setStage Method Description:update the stage person link DAM:
	 * Caudf1d
	 * 
	 * @param updateStagePersonLinkInDto
	 * @param updateStagePersonLinkOutDto
	 * @return @
	 */
	@Override
	public void setStage(UpdateStagePersonLinkInDto updateStagePersonLinkInDto,
			UpdateStagePersonLinkOutDto updateStagePersonLinkOutDto) {
		log.debug("Entering method setStage in CaseMaintenanceSetStageDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery1)
				.setParameter("hI_ulIdStage", updateStagePersonLinkInDto.getUlIdStage())
				.setParameter("hI_ulIdPerson", updateStagePersonLinkInDto.getUlIdPerson()));
		sQLQuery1.executeUpdate();

		log.debug("Exiting method setStage in CaseMaintenanceSetStageDaoImpl");
	}

}
