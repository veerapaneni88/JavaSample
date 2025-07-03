package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import org.springframework.util.CollectionUtils;
import us.tx.state.dfps.common.domain.ContactNarrative;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.dao.ContactNarrativeDao;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactNarrativeStageDiDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactNarrativeStageDoDto;

@Repository
public class ContactNarrativeDaoImpl implements ContactNarrativeDao {
	@Autowired
	MessageSource messageSource;
	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 * Method Name: deleteContactNarrative Method Description:
	 * deleteContactNarrative by idevent
	 * 
	 * @param idEvent
	 * @
	 */
	@Override
	public long deleteContactNarrative(int idEvent) {
		long result = 0;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ContactNarrative.class);

		criteria.add(Restrictions.eq("idEvent", Long.valueOf(idEvent)));
		List<ContactNarrative> contactNarrativeList = criteria.list();
		if (!TypeConvUtil.isNullOrEmpty(contactNarrativeList)) {
			for (ContactNarrative contactNarrative : contactNarrativeList) {
				sessionFactory.getCurrentSession().delete(contactNarrative);
				result++;
			}
			return result;
		}
		if (contactNarrativeList.size() == ServiceConstants.Zero) {
			throw new DataLayerException(ServiceConstants.SQL_NOT_FOUND);
		}
		return result;
	}

	/**
	 * 
	 * Method Name: getNarrativeExists Method Description:fetches from contact
	 * narrative
	 * 
	 * @param csesa9di
	 * @return @
	 */
	@Override
	public ContactNarrativeStageDoDto getNarrativeExists(ContactNarrativeStageDiDto contactNarrativeStageDiDto) {

		ContactNarrativeStageDoDto contactNarrativeStageDoDto = new ContactNarrativeStageDoDto();

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ContactNarrative.class);
		criteria.add(Restrictions.eq("idEvent", contactNarrativeStageDiDto.getUlIdEvent()));
		ContactNarrative contactNarrative = (ContactNarrative) criteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(contactNarrative)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		contactNarrativeStageDoDto.setUlIdEvent(contactNarrative.getIdEvent());
		contactNarrativeStageDoDto.setUlIdCase(contactNarrative.getIdCase());
		contactNarrativeStageDoDto.setUlIdDocumentTemplate(contactNarrative.getIdDocumentTemplate());

		return contactNarrativeStageDoDto;
	}

    @Override
    public List<byte[]> getContactNarrativeDocs(List<Long> idEvents) {
        List<byte[]> narrativeBlobs = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ContactNarrative.class);
        criteria.add(Restrictions.in("idEvent", idEvents));
        List<ContactNarrative> contactNarratives = (List<ContactNarrative>) criteria.list();
        if (!CollectionUtils.isEmpty(contactNarratives)) {
            narrativeBlobs.addAll(
                    contactNarratives.stream().map(ContactNarrative::getNarrative).collect(Collectors.toList()));
        }
        return narrativeBlobs;
    }
}
