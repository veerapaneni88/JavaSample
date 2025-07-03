package us.tx.state.dfps.service.casemanagement.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceSaveNewCaseNameDao;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseUpdateOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceSaveNewCaseNameDaoImpl Feb 7, 2018- 5:51:21 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class CaseMaintenanceSaveNewCaseNameDaoImpl implements CaseMaintenanceSaveNewCaseNameDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMaintenanceSaveNewCaseNameDaoImpl.strQuery1}")
	private transient String strQuery1;

	private static final Logger log = Logger.getLogger(CaseMaintenanceSaveNewCaseNameDaoImpl.class);

	/**
	 * 
	 * Method Name: saveNewCaseName Method Description:This Method is used to
	 * save the new case name DAM: ccmn14d
	 *
	 * @param capsCaseUpdateInDto
	 * @param capsCaseUpdateOutDto
	 * @
	 */
	@Override
	public void saveNewCaseName(CapsCaseUpdateInDto capsCaseUpdateInDto, CapsCaseUpdateOutDto capsCaseUpdateOutDto) {
		log.debug("Entering method saveNewCaseName in CaseMaintenanceSaveNewCaseNameDaoImpl");
		switch (capsCaseUpdateInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery1)
					.setParameter("hI_szNmCase", capsCaseUpdateInDto.getSzNmCase())
					.setParameter("hI_ulIdCase", capsCaseUpdateInDto.getUlIdCase()));
			sQLQuery1.executeUpdate();

			break;
		}

		log.debug("Exiting method saveNewCaseName in CaseMaintenanceSaveNewCaseNameDaoImpl");
	}

}
