package us.tx.state.dfps.service.casemanagement.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceUpdateStageCountyDao;
import us.tx.state.dfps.service.casepackage.dto.StageCountyUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.StageCountyUpdateOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceUpdateStageCountyDaoImpl Feb 7, 2018- 5:52:13 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class CaseMaintenanceUpdateStageCountyDaoImpl implements CaseMaintenanceUpdateStageCountyDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMaintenanceUpdateStageCountyDaoImpl.strQuery1}")
	private transient String strQuery1;

	private static final Logger log = Logger.getLogger(CaseMaintenanceUpdateStageCountyDaoImpl.class);

	/**
	 * Method Name: updateStageCounty Method Description:This Method is used to
	 * update Stage County
	 * 
	 * @param stageCountyUpdateInDto
	 * @param stageCountyUpdateOutDto
	 * @return @
	 */
	@Override
	public void updateStageCounty(StageCountyUpdateInDto stageCountyUpdateInDto,
			StageCountyUpdateOutDto stageCountyUpdateOutDto) {
		log.debug("Entering method updateStageCounty in CaseMaintenanceUpdateStageCountyDaoImpl");
		switch (stageCountyUpdateInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery1)
					.setParameter("hI_szCdStageCnty", stageCountyUpdateInDto.getSzCdStageCnty())
					.setParameter("hI_ulIdStage", stageCountyUpdateInDto.getUlIdStage()));
			sQLQuery1.executeUpdate();

			break;
		}

		log.debug("Exiting method updateStageCounty in CaseMaintenanceUpdateStageCountyDaoImpl");
	}

}
