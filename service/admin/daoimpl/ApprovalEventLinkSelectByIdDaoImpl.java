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

import us.tx.state.dfps.service.admin.dao.ApprovalEventLinkSelectByIdDao;
import us.tx.state.dfps.service.admin.dto.ApprovalEventLinkSelectByIdInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalEventLinkSelectByIdOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Get
 * approval record. Aug 8, 2017- 9:08:39 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Repository
public class ApprovalEventLinkSelectByIdDaoImpl implements ApprovalEventLinkSelectByIdDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ApprovalEventLinkSelectByIdDaoImpl.getIDApproval}")
	private String getIDApproval;

	private static final Logger log = Logger.getLogger(ApprovalEventLinkSelectByIdDaoImpl.class);

	public ApprovalEventLinkSelectByIdDaoImpl() {
		super();
	}

	/**
	 *
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 * @return list Ccmn55doDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ApprovalEventLinkSelectByIdOutDto> getIDApproval(ApprovalEventLinkSelectByIdInDto pInputDataRec) {
		log.debug("Entering method ApprovalEventLinkSelectByIdQUERYdam in ApprovalEventLinkSelectByIdDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getIDApproval)
				.addScalar("idApproval", StandardBasicTypes.LONG)
				.setParameter("hostIdEvent", pInputDataRec.getIdEvent())
				.setResultTransformer(Transformers.aliasToBean(ApprovalEventLinkSelectByIdOutDto.class)));
		List<ApprovalEventLinkSelectByIdOutDto> liCcmn55doDto = (List<ApprovalEventLinkSelectByIdOutDto>) sQLQuery1
				.list();
		/*
		 * if (TypeConvUtil.isNullOrEmpty(liCcmn55doDto) || liCcmn55doDto.size()
		 * == 0) { throw new DataNotFoundException(messageSource.getMessage(
		 * "approvalID.record.notfound", null, Locale.US)); }
		 */
		log.debug("Exiting method ApprovalEventLinkSelectByIdQUERYdam in ApprovalEventLinkSelectByIdDaoImpl");
		return liCcmn55doDto;
	}
}
