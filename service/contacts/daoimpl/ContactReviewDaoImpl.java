package us.tx.state.dfps.service.contacts.daoimpl;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Contact;
import us.tx.state.dfps.service.contacts.dao.ContactReviewDao;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactsForStageDto;
import us.tx.state.dfps.xmlstructs.outputstructs.NbrContactDto;

@Repository
public class ContactReviewDaoImpl implements ContactReviewDao {

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	/**
	 * 
	 * Method Name: getCountReqRevContactsForStage Method Description:Selects
	 * number of request for review contacts given an id-stage.
	 * 
	 * @param contactsForStageDto
	 * @return NbrContactDto
	 */
	@Override
	public NbrContactDto getCountReqRevContactsForStage(ContactsForStageDto contactsForStageDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Contact.class);
		criteria.add(Restrictions.eq("stage.idStage", contactsForStageDto.getIdStage()));
		criteria.add(Restrictions.eq("cdContactType", "EREV"));
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.count("idEvent"));
		criteria.setProjection(projectionList);
		NbrContactDto nbrContactDto = new NbrContactDto();
		Long nbrContact = (Long) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(nbrContact)) {
			nbrContactDto.setNbrContact(nbrContact.intValue());
		}
		return nbrContactDto;
	}

}
