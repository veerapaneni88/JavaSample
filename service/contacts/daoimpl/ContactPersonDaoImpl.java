package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.contacts.dao.ContactPersonDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactPersonDiDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactPersonDetailsDoDto;
import us.tx.state.dfps.xmlstructs.outputstructs.PersonDetailsDoArrayDto;
import us.tx.state.dfps.xmlstructs.outputstructs.PersonDetailsEventDoDto;

@Repository
public class ContactPersonDaoImpl implements ContactPersonDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ContactPersonDaoImpl.getPersonDetailsForEvent}")
	private String getPersonDetailsForEvent;

	/**
	 * 
	 * Method Name: getPersonDetailsForEvent Method Description:gets the person
	 * name & id
	 * 
	 * @param contactPersonDiDto
	 * @return @
	 */
	@Override
	public ContactPersonDetailsDoDto getPersonDetailsForEvent(ContactPersonDiDto contactPersonDiDto) {

		List<PersonDetailsEventDoDto> PersonDetailsEventDoDtoList = sessionFactory.getCurrentSession()
				.createSQLQuery(getPersonDetailsForEvent).addScalar("szNmPersonFull", StandardBasicTypes.STRING)
				.addScalar("ulIdPerson", StandardBasicTypes.LONG)
				.setParameter("idEvent", contactPersonDiDto.getUlIdEvent())
				.setResultTransformer(Transformers.aliasToBean(PersonDetailsEventDoDto.class)).list();
		PersonDetailsDoArrayDto personDetailsDoArrayDto = new PersonDetailsDoArrayDto();
		for (PersonDetailsEventDoDto personDetailsEventDoDto : PersonDetailsEventDoDtoList) {

			if (personDetailsDoArrayDto.getPersonDetailsDoDtoList().size() >= 50)
				throw new DataNotFoundException("List Size Excedeed");
			personDetailsDoArrayDto.getPersonDetailsDoDtoList().add(personDetailsEventDoDto);
		}
		if (personDetailsDoArrayDto.getPersonDetailsDoDtoList().size() == 0) {
			StringBuilder message = new StringBuilder(ServiceConstants.SQL_NOT_FOUND_1403);
			throw new DataNotFoundException(message.toString());
		}
		ContactPersonDetailsDoDto contactPersonDetailsDoDto = new ContactPersonDetailsDoDto();
		contactPersonDetailsDoDto.setPersonDetailsDoARRAYDto(personDetailsDoArrayDto);
		return contactPersonDetailsDoDto;
	}

}
