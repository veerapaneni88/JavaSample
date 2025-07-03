package us.tx.state.dfps.service.casepackage.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkUpdIndEmpNewDao;
import us.tx.state.dfps.service.casepackage.dto.StagePersonLinkUpdIndEmpNewInDto;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:updates the
 * employee indicator. Aug 7, 2017- 3:42:54 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Repository
public class StagePersonLinkUpdIndEmpNewDaoImpl implements StagePersonLinkUpdIndEmpNewDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	public static final String NO = "0";

	public static final String IND_EMP_NEW = "0";

	private static final Logger log = Logger.getLogger("ServiceBusiness-StagePersonLinkUpdIndEmpNewDao");

	/**
	 * Method Name: updateEmployeeIndicator Method Description:updates the
	 * employee indicator.
	 * 
	 * @param stagePersonLinkUpdIndEmpNewInDto
	 * @return void @
	 */
	@Override
	public void updateEmployeeIndicator(StagePersonLinkUpdIndEmpNewInDto stagePersonLinkUpdIndEmpNewInDto) {
		log.debug("Entering method updateEmployeeIndicator in StagePersonLinkUpdIndEmpNewDaoImpl");
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
		switch (stagePersonLinkUpdIndEmpNewInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			StagePersonLink stagePersonLink = (StagePersonLink) criteria
					.add(Restrictions.eq("idStage", stagePersonLinkUpdIndEmpNewInDto.getIdStage()))
					.add(Restrictions.eq("idPerson", stagePersonLinkUpdIndEmpNewInDto.getIdPerson())).uniqueResult();
			if (stagePersonLink != null) {
				stagePersonLink.setIndStagePersEmpNew(IND_EMP_NEW);
				sessionFactory.getCurrentSession().saveOrUpdate(stagePersonLink);
			}
			break;
		}
		log.debug("Exiting method updateEmployeeIndicator in StagePersonLinkUpdIndEmpNewDaoImpl");
	}
}
