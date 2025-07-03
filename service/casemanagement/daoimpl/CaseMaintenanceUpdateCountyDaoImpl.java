package us.tx.state.dfps.service.casemanagement.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceUpdateCountyDao;
import us.tx.state.dfps.service.casepackage.dto.CaseCountyUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.CaseCountyUpdateOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceUpdateCountyDaoImpl Feb 7, 2018- 5:51:57 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class CaseMaintenanceUpdateCountyDaoImpl implements CaseMaintenanceUpdateCountyDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMaintenanceUpdateCountyDaoImpl.strQuery1}")
	private transient String strQuery1;

	private static final Logger log = Logger.getLogger(CaseMaintenanceUpdateCountyDaoImpl.class);

	/**
	 * Method Name: updateCounty Method Description:This Method is used to
	 * update the county DAM: Ccmn38dD
	 * 
	 * @param caseCountyUpdateInDto
	 * @param caseCountyUpdateOutDto
	 * @
	 */
	@Override
	public void updateCounty(CaseCountyUpdateInDto caseCountyUpdateInDto,
			CaseCountyUpdateOutDto caseCountyUpdateOutDto) {
		log.debug("Entering method updateCounty in Ccmn38dDaoImpl");
		switch (caseCountyUpdateInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery1)
					.setParameter("hI_szCdStageCnty", caseCountyUpdateInDto.getCdStageCnty())
					.setParameter("hI_ulIdCase", caseCountyUpdateInDto.getIdCase()));
			sQLQuery1.executeUpdate();

			break;
		}

		log.debug("Exiting method updateCounty in Ccmn38dDaoImpl");
	}

}
