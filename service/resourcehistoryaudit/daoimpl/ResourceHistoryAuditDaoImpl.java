/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *June 4, 2018- 2:16:35 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.resourcehistoryaudit.daoimpl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.ResourceHistoryAudit;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.resource.dto.ResourceHistoryAuditInDto;
import us.tx.state.dfps.service.resourcehistoryaudit.dao.ResourceHistoryAuditDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * is used to perform CRUD operations on ResourceHistory June 25, 2018- 2:16:35
 * PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class ResourceHistoryAuditDaoImpl implements ResourceHistoryAuditDao {

	@Autowired
	private SessionFactory sessionFactory;

	protected static final Date MAX_DATE = new Date(Long.MAX_VALUE);
	protected static final Date maxDate = ServiceConstants.GENERIC_END_DATE;

	/**
	 * 
	 * Method Name: getResourceHistoryAuditByIdRescAndHmeStatus Method
	 * Description: DAM NAME: CLSS82D; This DAM will select a full row from the
	 * Resource History Audit table given an Id_Resource & facility type.
	 * 
	 * @param resourceHistoryAuditInDto
	 * @return ResourceHistoryAuditDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceHistoryAudit> getResourceHistoryAuditByIdRescAndHmeStatus(
			ResourceHistoryAuditInDto resourceHistoryAuditInDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceHistoryAudit.class);
		criteria.add(Restrictions.eq("idAudResource", resourceHistoryAuditInDto.getIdAudResource()));
		criteria.add(Restrictions.eq("cdRshsAudFaHomeStatus", resourceHistoryAuditInDto.getCdRshsAudFaHomeStatus()));
		criteria.add(Restrictions.eq("dtRshsAudEnd", maxDate));
		criteria.addOrder(Order.desc("idResourceHistoryAud"));
		return (List<ResourceHistoryAudit>) criteria.list();
	}
}
