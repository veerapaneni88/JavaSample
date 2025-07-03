package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.AllegationHistory;
import us.tx.state.dfps.service.admin.dto.PersonVictimDiDto;
import us.tx.state.dfps.service.admin.dto.PersonVictimDoDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.dao.VictimRoleDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.lookup.dao.LookupDao;

@Repository
public class VictimRoleDaoImpl implements VictimRoleDao {

	@Autowired
	MessageSource messageSource;
	@Autowired
	LookupDao lookupDao;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 * Method Name: getIndVictimRole Method Description:this method fetches
	 * idAllegHistory from ALLEGATION_HISTORY table.
	 * 
	 * @param personVictimDiDto
	 * @return PersonVictimDoDto @
	 */
	public PersonVictimDoDto getIndVictimRole(PersonVictimDiDto personVictimDiDto) {
		PersonVictimDoDto personVictimDoDto = new PersonVictimDoDto();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AllegationHistory.class);
		criteria.add(Restrictions.eq("idAllegationStage", personVictimDiDto.getUlIdAllegationStage()));
		criteria.add(Restrictions.eq("idVictim", personVictimDiDto.getUlIdPerson()));
		if (TypeConvUtil.isNullOrEmpty(criteria.list())) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		int noOfRows = criteria.list().size();

		boolean rowsExist = noOfRows > ServiceConstants.Zero;
		personVictimDoDto.setBindVictimRole(rowsExist ? ServiceConstants.TRUE : ServiceConstants.FALSE);
		return personVictimDoDto;

	}
}
