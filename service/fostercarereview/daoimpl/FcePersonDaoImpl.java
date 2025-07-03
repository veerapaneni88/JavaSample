/**
 *service-ejb-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Nov 15, 2017- 3:18:47 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.fostercarereview.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.FceEligibility;
import us.tx.state.dfps.common.domain.FcePerson;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.fce.dto.FcePersonDto;
import us.tx.state.dfps.service.fostercarereview.dao.FcePersonDao;
import us.tx.state.dfps.service.person.dao.PersonDao;

/**
 * service-ejb-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter
 * the description of class> Nov 15, 2017- 3:18:47 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class FcePersonDaoImpl implements FcePersonDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private PersonDao personDao;

	/**
	 * load FcePerson by id
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public FcePerson getFcepersonById(Long idFcePerson) {

		FcePerson fceperson = (FcePerson) sessionFactory.getCurrentSession().load(FcePerson.class, idFcePerson);
		if (TypeConvUtil.isNullOrEmpty(fceperson)) {
			throw new DataNotFoundException("fcePerson not found");
		}

		return fceperson;

	}

	/**
	 * save id person
	 */
	@Override
	public FcePerson save(Long idFceEligibility, Long idPerson) {

		FcePerson fcePerson = new FcePerson();
		fcePerson.setDtLastUpdate(new Date());
		Person personByIdPerson = personDao.getPerson(idPerson);
		personByIdPerson.setIdPerson(idPerson);
		FceEligibility fceEligibility = new FceEligibility();
		fceEligibility.setIdFceEligibility(idFceEligibility);
		fcePerson.setPerson(personByIdPerson);
		fcePerson.setFceEligibility(fceEligibility);
		sessionFactory.getCurrentSession().save(fcePerson);
		return fcePerson;
	}

	/**
	 * load Map<Long,FcePersonDto> by idFceEligibility
	 */
	@Override
	public Map<Long, FcePersonDto> getFcePersonDtosbyEligibilityId(Long idFceEligibility) {

		Map<Long, FcePersonDto> personsMap = new HashMap<>();
		getFcePersonsbyEligibilityId(idFceEligibility).stream().forEach(o -> {
			FcePersonDto personDto = new FcePersonDto();
			BeanUtils.copyProperties(o, personDto);
			personDto.setIdPerson(o.getPerson().getIdPerson());
			personDto.setNmPersonFull(o.getPerson().getNmPersonFull());
			personDto.setNbrSocialSecurity(o.getPerson().getNbrPersonIdNumber());
			personDto.setAddrPersonStLn1(o.getPerson().getAddrPersonStLn1());
			personDto.setAddrPersonCity(o.getPerson().getAddrPersonCity());
			personDto.setCdPersonState(o.getPerson().getCdPersonState());
			personDto.setAddrPersonZip(o.getPerson().getAddrPersonZip());
			personsMap.put(o.getPerson().getIdPerson(), personDto);
		});
		return personsMap;
	}

	/**
	 * load List<FcePerson> by idFceEligibility
	 */
	@Override
	public List<FcePerson> getFcePersonsbyEligibilityId(Long idFceEligibility) {
		List<FcePerson> personList = new ArrayList<>();
		Criteria personCriteria = sessionFactory.getCurrentSession().createCriteria(FcePerson.class)
				.add(Restrictions.eq("fceEligibility.idFceEligibility", idFceEligibility))
				.addOrder(Order.asc("nbrAge"));
		personList = (List<FcePerson>) personCriteria.list();
		return personList;
	}

}
