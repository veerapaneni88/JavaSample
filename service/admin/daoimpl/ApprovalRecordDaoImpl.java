package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.ApprovalRecordDao;
import us.tx.state.dfps.service.admin.dto.ApprovalHostDto;
import us.tx.state.dfps.service.admin.dto.ApprovalRecordDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Get
 * approval record. Aug 8, 2017- 9:08:39 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Repository
public class ApprovalRecordDaoImpl implements ApprovalRecordDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Ccmn55dDaoImpl.getIDApproval}")
	private String getIDApproval;

	private static final Logger log = Logger.getLogger(ApprovalRecordDaoImpl.class);

	/**
	 * Description:Gets approval record.
	 * 
	 * @param pInputDataRec
	 * @return liCcmn55doDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ApprovalHostDto> getIDApproval(ApprovalRecordDto pInputDataRec) {
		log.debug("Entering method ccmn55dQUERYdam in Ccmn55dDaoImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getIDApproval)
				.addScalar("ulIdApproval", StandardBasicTypes.LONG)
				.setParameter("hostulIdEvent", pInputDataRec.getUlIdEvent())

				.setResultTransformer(Transformers.aliasToBean(ApprovalHostDto.class)));
		List<ApprovalHostDto> liCcmn55doDto = (List<ApprovalHostDto>) sQLQuery1.list();

		log.debug("Exiting method ccmn55dQUERYdam in Ccmn55dDaoImpl");
		return liCcmn55doDto;
	}
}
