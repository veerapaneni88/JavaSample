package us.tx.state.dfps.service.admin.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.ApproverUpdateDao;
import us.tx.state.dfps.service.admin.dto.ApprovalUpdateDto;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Execute the
 * DAM that changes the status of all Approvers records on the Approvers Table
 * from PEND to INVD. Aug 8, 2017- 10:47:03 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Repository
public class ApproverUpdateDaoImpl implements ApproverUpdateDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ApproverUpdateDaoImpl.updateApproverStatus}")
	private String updateApproverStatus;

	private static final Logger log = Logger.getLogger(ApproverUpdateDaoImpl.class);

	/**
	 * Description:This method updates status of all approvers
	 * 
	 * @param approvalUpdateDto
	 * @return rowCount @
	 */
	@Override
	public int updateApproverStatus(ApprovalUpdateDto approvalUpdateDto) {
		log.debug("Entering method updateApproverStatus in ApproverUpdateDaoImpl");

		int rowCount = 0;
		if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(approvalUpdateDto.getReqFuncCd())) {
			rowCount = sessionFactory.getCurrentSession().createSQLQuery(updateApproverStatus)
					.setParameter("hI_ulIdApproval", approvalUpdateDto.getUlIdApproval()).executeUpdate();
		}

		log.debug("Exiting method updateApproverStatus in ApproverUpdateDaoImpl");
		return rowCount;
	}
}
