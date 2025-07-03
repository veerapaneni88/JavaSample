package us.tx.state.dfps.service.fostercarereview.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.FceEligibility;
import us.tx.state.dfps.common.domain.FceIncome;
import us.tx.state.dfps.common.domain.FcePerson;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.fce.dto.FceIncomeDto;
import us.tx.state.dfps.service.fostercarereview.dao.FceIncomeDao;

@Repository
public class FceIncomeDaoImpl implements FceIncomeDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Override
	public Long saveFceIncome(FceIncomeDto fceIncomeDto) {
		FceIncome fceIncome = null;
		if (fceIncomeDto.getIdFceIncome() == null) {
			fceIncome = new FceIncome();
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, fceIncomeDto.getIdPerson());
			fceIncome.setPerson(person);
			FcePerson fcePerson = (FcePerson) sessionFactory.getCurrentSession().get(FcePerson.class,
					fceIncomeDto.getIdFcePerson());
			fceIncome.setFcePerson(fcePerson);
			FceEligibility fceEligibility = (FceEligibility) sessionFactory.getCurrentSession()
					.get(FceEligibility.class, fceIncomeDto.getIdFceEligibility());
			fceIncome.setFceEligibility(fceEligibility);
		} else {
			fceIncome = (FceIncome) sessionFactory.getCurrentSession().get(FceIncome.class,
					fceIncomeDto.getIdFceIncome());
		}

		fceIncome.setIdIncRsrc(fceIncomeDto.getIdIncRsrc());
		fceIncome.setDtLastUpdate(fceIncomeDto.getDtLastUpdate());
		fceIncome.setAmtIncome(fceIncomeDto.getAmtIncome());
		fceIncome.setCdType(fceIncomeDto.getCdType());
		fceIncome.setIndIncomeSource(fceIncomeDto.getIndIncomeSource());
		fceIncome.setIndResourceSource(fceIncomeDto.getIndResourceSource());
		fceIncome.setIndCountable(fceIncomeDto.getIndCountable());
		fceIncome.setIndEarned(fceIncomeDto.getIndEarned());
		fceIncome.setIndNotAccessible(fceIncomeDto.getIndNotAccessible());
		fceIncome.setIndChild(fceIncomeDto.getIndChild());
		fceIncome.setIndFamily(fceIncomeDto.getIndFamily());
		fceIncome.setIndNone(fceIncomeDto.getIndNone());
		fceIncome.setTxtComments(fceIncomeDto.getTxtComments());
		fceIncome.setTxtVerificationMethod(fceIncomeDto.getTxtVerificationMethod());
		fceIncome.setTxtSource(fceIncomeDto.getTxtSource());

		Long idFceIncome = fceIncomeDto.getIdFceIncome();
		if (idFceIncome == null) {
			idFceIncome = (Long) sessionFactory.getCurrentSession().save(fceIncome);
		} else {
			sessionFactory.getCurrentSession().update(fceIncome);
		}

		sessionFactory.getCurrentSession().flush();

		return idFceIncome;
	}

	@Override
	public void deleteFceIncome(Long idFceIncome) {
		FceIncome fceIncome = (FceIncome) sessionFactory.getCurrentSession().get(FceIncome.class, idFceIncome);

		deleteFceIncome(fceIncome);

	}

	@Override
	public List<FceIncomeDto> getFceIncomeDtosByIdElig(Long idFceEligibility) {
		List<FceIncomeDto> incomeList = new ArrayList<>();
		getFceIncomesByIdElig(idFceEligibility).stream().forEach(o -> {
			FceIncomeDto incomeDto = new FceIncomeDto();
			BeanUtils.copyProperties(o, incomeDto);
			incomeDto.setIdFceEligibility(o.getFceEligibility().getIdFceEligibility());
			incomeDto.setIdFcePerson(o.getFcePerson().getIdFcePerson());
			incomeDto.setIdPerson(o.getPerson().getIdPerson());
			incomeDto.setNmPersonFirst(o.getPerson().getNmPersonFirst());
			incomeDto.setNmPersonMiddle(o.getPerson().getNmPersonMiddle());
			incomeDto.setNmPersonLast(o.getPerson().getNmPersonLast());
			incomeDto.setNmPersonFull(o.getPerson().getNmPersonFull());
			incomeDto.setCdRelInt(o.getFcePerson().getCdRelInt());
			if (!ObjectUtils.isEmpty(o.getFcePerson().getNbrAge())) {
				incomeDto.setNbrAge(o.getFcePerson().getNbrAge().longValue());
			}
			incomeDto.setDtBirth(o.getFcePerson().getDtBirth());
			incomeDto.setIndCertifiedGroup(o.getFcePerson().getIndCertifiedGroup());
			incomeDto.setIndPersonHmRemoval(o.getFcePerson().getIndPersonHmRemoval());
			incomeList.add(incomeDto);
		});
		return incomeList;
	}

	@Override
	public List<FceIncome> getFceIncomesByIdElig(Long idFceEligibility) {
		List<FceIncome> incomeList = new ArrayList<>();
		Criteria personCriteria = sessionFactory.getCurrentSession().createCriteria(FceIncome.class)
				.add(Restrictions.eq("fceEligibility.idFceEligibility", idFceEligibility));
		// .addOrder(Order.asc("person.nbrPersonAge"));
		incomeList = (List<FceIncome>) personCriteria.list();
		return incomeList;
	}

	@Override
	public void deleteFceIncome(FceIncome fceIncome) {
		sessionFactory.getCurrentSession().delete(fceIncome);

	}

}
