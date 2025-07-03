package us.tx.state.dfps.service.resourcedetail.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.ResourceEmail;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.resourcedetail.dao.ResourceEmailInsUpdDelDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Update
 * Resource Email Details Aug 8, 2017- 6:33:38 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class ResourceEmailInsUpdDelDaoImpl implements ResourceEmailInsUpdDelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(ResourceEmailInsUpdDelDaoImpl.class);

	/**
	 * Method Name: handleResourceEmail Method Description: Handle Add, Update
	 * and Delete operation for Email detail (resource)
	 * 
	 * @param String
	 * @param ResourceEmail
	 * @return Resource ID @
	 */
	@Override
	public Long handleResourceEmail(String actionType, ResourceEmail resourceEmail) {
		log.debug("Entering method handleResourceEmail in ResourceEmailInsUpdDelDaoImpl");

		switch (actionType) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			sessionFactory.getCurrentSession().save(resourceEmail);
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			sessionFactory.getCurrentSession().saveOrUpdate(resourceEmail);
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			sessionFactory.getCurrentSession().delete(resourceEmail);
			break;
		}
		log.debug("Exiting method handleResourceEmail in ResourceEmailInsUpdDelDaoImpl");
		return resourceEmail.getIdResource();
	}
}
