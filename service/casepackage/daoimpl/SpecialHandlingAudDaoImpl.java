package us.tx.state.dfps.service.casepackage.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casepackage.dao.SpecialHandlingAudDao;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingAudInDto;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingAudOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;

@Repository
public class SpecialHandlingAudDaoImpl implements SpecialHandlingAudDao {
	@Autowired
	MessageSource messageSource;

	@Value("${SpecialHandlingAudDaoImpl.specialHandlingAud}")
	private String specialHandlingAud;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger("ServiceBusiness-EmployeeDaoLog");

	/**
	 * Method Name: specialHandlingAud Method Description: save special handling
	 * data in case summary , DAM Ccmng4dD
	 * 
	 * @param specialHandlingAudInDto
	 * @param specialHandlingAudOutDto
	 * @return @
	 */
	@Override
	public void specialHandlingAud(SpecialHandlingAudInDto specialHandlingAudInDto,
			SpecialHandlingAudOutDto specialHandlingAudOutDto) {
		log.debug("Entering method specialHandlingAud in SpecialHandlingAudDaoImpl");
		switch (specialHandlingAudInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(specialHandlingAud)
					.setParameter("indSafetyCheckList", specialHandlingAudInDto.getIndSafetyCheckList())
					.setParameter("txtCaseSensitiveCmnts", specialHandlingAudInDto.getTxtCaseSensitiveCmnts())
					.setParameter("indLitigationHold", specialHandlingAudInDto.getIndLitigationHold())
					.setParameter("txtCaseWorkerSafety", specialHandlingAudInDto.getTxtCaseWorkerSafety())
					.setParameter("indCaseSensitive", specialHandlingAudInDto.getIndCaseSensitive())
					.setParameter("indCaseWorkerSafety", specialHandlingAudInDto.getIndCaseWorkerSafety())
					.setParameter("txtSpecHandling", specialHandlingAudInDto.getTxtSpecHandling())
					.setParameter("txtLitigationHold", specialHandlingAudInDto.getTxtLitigationHold())
					.setParameter("idCase", specialHandlingAudInDto.getIdCase())
					.setParameter("cdCaseSpeclHndlg", specialHandlingAudInDto.getCdCaseSpeclHndlg())
					.setParameter("indCaseAlert", specialHandlingAudInDto.getIndCaseAlert())
					.setParameter("indMediaAttention", specialHandlingAudInDto.getIndMediaAttention())
					.setParameter("txtMediaAttention", specialHandlingAudInDto.getTxtMediaAttention())
					.setParameter("indSelfReport", specialHandlingAudInDto.getIndSelfReport())
					.setParameter("txtSelfReport", specialHandlingAudInDto.getTxtSelfReport()));
			sQLQuery1.executeUpdate();

			break;
		}

		log.debug("Exiting method specialHandlingAud in SpecialHandlingAudDaoImpl");
	}

}
