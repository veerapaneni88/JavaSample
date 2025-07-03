package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.dao.ContactOccuredDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactStageDiDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactStageDoDto;

@Repository
public class ContactOccuredDaoImpl implements ContactOccuredDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	SessionFactory sessionFactory;

	@Value("${ContactOccuredDaoImpl.csys15dQueryDao}")
	private transient String csys15dQueryDao;

	/**
	 *
	 * Method Name: csys15dQueryDao Method Description:fetches the date contact
	 * occurred from contact
	 *
	 * @param csys15di
	 * @return @
	 */
	@Override
	public ContactStageDoDto csys15dQueryDao(ContactStageDiDto csys15di) {
		// Warranty Defect - 12250 - Null Pointer Check
		ContactStageDoDto contactStageDoDto=new ContactStageDoDto();
		SQLQuery sqLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(csys15dQueryDao)
				.addScalar("dtDTContactOccurred", StandardBasicTypes.DATE)
				.setParameter("contactIds", csys15di.getUlIdStage())
				.setParameter("idContactStage", csys15di.getUlIdStage())
				.setResultTransformer(Transformers.aliasToBean(ContactStageDoDto.class)));
		List<ContactStageDoDto> contactStageDoDtoList = (List<ContactStageDoDto>) sqLQuery1.list();

		for(ContactStageDoDto contact:contactStageDoDtoList)
		{
			contact.setDtDTContactOccurred(DateUtils.toCastorDate(contact.getDtDTContactOccurred()));
		}

		if (!ObjectUtils.isEmpty(contactStageDoDtoList)) {
			contactStageDoDto=contactStageDoDtoList.get(0);
		}

		return contactStageDoDto;
	}


	/**
	 * 
	 * Method Name: csys15dQueryDao Method Description:fetches the date contact
	 * occurred from contact
	 * 
	 * @param csys15di
	 * @return @
	 */
	@Override
	public ContactStageDoDto consys15dQueryDao(ContactStageDiDto csys15di) {
		ContactStageDoDto contactStageDoDto=new ContactStageDoDto();
		SQLQuery sqLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(csys15dQueryDao)
				.addScalar("dtDTContactOccurred", StandardBasicTypes.DATE)
				.setParameter("contactIds", csys15di.getUlIdStage())
				.setParameter("idContactStage", csys15di.getUlIdStage())
				.setResultTransformer(Transformers.aliasToBean(ContactStageDoDto.class)));
		List<ContactStageDoDto> contactStageDoDtoList = (List<ContactStageDoDto>) sqLQuery1.list();

		if (!ObjectUtils.isEmpty(contactStageDoDtoList)) {			
			contactStageDoDto=contactStageDoDtoList.get(0);
		}

     	return contactStageDoDto;
	}

}
