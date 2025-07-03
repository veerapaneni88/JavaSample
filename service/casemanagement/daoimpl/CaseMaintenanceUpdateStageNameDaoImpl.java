package us.tx.state.dfps.service.casemanagement.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceUpdateStageNameDao;
import us.tx.state.dfps.service.casepackage.dto.SaveStageInDto;
import us.tx.state.dfps.service.casepackage.dto.SaveStageOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceUpdateStageNameDaoImpl Feb 7, 2018- 5:52:29 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class CaseMaintenanceUpdateStageNameDaoImpl implements CaseMaintenanceUpdateStageNameDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMaintenanceUpdateStageNameDaoImpl.strQuery1}")
	private transient String strQuery1;

	private static final Logger log = Logger.getLogger(CaseMaintenanceUpdateStageNameDaoImpl.class);

	/**
	 * Method Name: saveStage Method Description:This Method is used to save the
	 * stage dtl DAM:ccmnd8d
	 * 
	 * @param saveStageInDto
	 * @param saveStageOutDto
	 * @return @
	 */
	@Override
	public void saveStage(SaveStageInDto saveStageInDto, SaveStageOutDto saveStageOutDto) {
		log.debug("Entering method saveStage in CaseMaintenanceUpdateStageNameDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery1)
				.setParameter("hI_ulIdStage", saveStageInDto.getIdStage())
				.setParameter("hI_szNmCase", saveStageInDto.getNmCase()));
		sQLQuery1.executeUpdate();

		log.debug("Exiting method saveStage in CaseMaintenanceUpdateStageNameDaoImpl");
	}

}
