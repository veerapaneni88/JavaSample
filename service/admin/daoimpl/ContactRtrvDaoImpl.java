package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Contact;
import us.tx.state.dfps.service.admin.dao.ContactRtrvDao;
import us.tx.state.dfps.service.admin.dto.ContactRtrvInDto;
import us.tx.state.dfps.service.admin.dto.ContactRtrvOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Retrieve the
 * Date the first Request for Review contact occurred given a specific id_stage.
 * Sep 27, 2017- 11:24:12 AM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class ContactRtrvDaoImpl implements ContactRtrvDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Method Name: getFirstEREVContactDateForStage Method Description:Performs
	 * a Select Count to determine if any Request for Review Contacts have been
	 * recorded.
	 * 
	 * @param contactRtrvInDto
	 * @return ContactRtrvOutDto
	 */
	@Override
	public ContactRtrvOutDto getFirstEREVContactDateForStage(ContactRtrvInDto contactRtrvInDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Contact.class);

		criteria.add(Restrictions.eq("cdContactType", ServiceConstants.EREV));
		criteria.add(Restrictions.eq("stage.idStage", contactRtrvInDto.getUlIdStage()));
		criteria.setProjection(Projections.min("dtContactOccurred"));

		Date contactOccured = (Date) criteria.uniqueResult();
		ContactRtrvOutDto contactRtrvOutDto = new ContactRtrvOutDto();
		contactRtrvOutDto.setDtDTContactOccurred(contactOccured);
		return contactRtrvOutDto;
	}

}
