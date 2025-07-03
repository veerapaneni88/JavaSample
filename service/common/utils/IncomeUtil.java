package us.tx.state.dfps.service.common.utils;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.FceEligibility;
import us.tx.state.dfps.common.domain.FceIncome;
import us.tx.state.dfps.common.domain.FcePerson;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.fce.dto.FceIncomeDto;

@Repository
public class IncomeUtil {

	@Autowired
	private SessionFactory sessionFactory;

	public void saveIncome(List<FceIncomeDto> incomeForChildList) {
		if (incomeForChildList == null) {
			return;
		}

		Iterator<FceIncomeDto> iterator = incomeForChildList.iterator();
		while (iterator.hasNext()) {
			FceIncomeDto fceIncomeDto = (FceIncomeDto) iterator.next();
			saveIncome(fceIncomeDto);
		}

	}

	private void saveIncome(FceIncomeDto fceIncomeDto) {
		if (fceIncomeDto != null && fceIncomeDto.getIdFceIncome() > 0) {
			FceIncome fceIncome = (FceIncome) sessionFactory.getCurrentSession().get(FceIncome.class,
					fceIncomeDto.getIdFceIncome());
			if (fceIncome == null) {
				return;
			}
			setFceEligibility(fceIncomeDto, fceIncome);
			setFcePerson(fceIncomeDto, fceIncome);
			setPerson(fceIncomeDto, fceIncome);

			if (fceIncomeDto.getIdIncRsrc() != null) {
				fceIncome.setIdIncRsrc(fceIncomeDto.getIdIncRsrc());
			}
			if (fceIncomeDto.getAmtIncome() != null) {
				fceIncome.setAmtIncome(fceIncomeDto.getAmtIncome());
			}
			fceIncome.setDtLastUpdate(new Date());
			if (fceIncomeDto.getCdType() != null) {
				fceIncome.setCdType(fceIncomeDto.getCdType());
			}
			if (!StringUtils.isEmpty(fceIncomeDto.getIndChild())) {
				fceIncome.setIndChild(fceIncomeDto.getIndChild());
			}
			if (!StringUtils.isEmpty(fceIncomeDto.getIndCountable())) {
				fceIncome.setIndCountable(fceIncomeDto.getIndCountable());
			}
			if (!StringUtils.isEmpty(fceIncomeDto.getIndEarned())) {
				fceIncome.setIndEarned(fceIncomeDto.getIndEarned());
			}

			if (!StringUtils.isEmpty(fceIncomeDto.getIndFamily())) {
				fceIncome.setIndFamily(fceIncomeDto.getIndFamily());
			}
			if (!StringUtils.isEmpty(fceIncomeDto.getIndIncomeSource())) {
				fceIncome.setIndIncomeSource(fceIncomeDto.getIndIncomeSource());
			}
			if (!StringUtils.isEmpty(fceIncomeDto.getIndNone())) {
				fceIncome.setIndNone(fceIncomeDto.getIndNone());
			}
			if (!StringUtils.isEmpty(fceIncomeDto.getIndNotAccessible())) {
				fceIncome.setIndNotAccessible(fceIncomeDto.getIndNotAccessible());
			}
			if (!StringUtils.isEmpty(fceIncomeDto.getIndResourceSource())) {
				fceIncome.setIndResourceSource(fceIncomeDto.getIndResourceSource());
			}
			if (!StringUtils.isEmpty(fceIncomeDto.getTxtComments())) {
				fceIncome.setTxtComments(fceIncomeDto.getTxtComments());
			}
			if (!StringUtils.isEmpty(fceIncomeDto.getTxtSource())) {
				fceIncome.setTxtSource(fceIncomeDto.getTxtSource());
			}
			if (!StringUtils.isEmpty(fceIncomeDto.getTxtVerificationMethod())) {
				fceIncome.setTxtVerificationMethod(fceIncomeDto.getTxtVerificationMethod());
			}

			sessionFactory.getCurrentSession().saveOrUpdate(fceIncome);

		}

	}

	public double calculateCountableIncome(long idFcePerson) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FceIncome.class);
		ProjectionList proList = Projections.projectionList();
		proList.add(Projections.sum("amtIncome"));
		criteria.setProjection(proList);
		Criterion conjuction = Restrictions.conjunction().add(Restrictions.eq("fcePerson.idFcePerson", idFcePerson))
				.add(Restrictions.eq("indIncomeSource", "Y")).add(Restrictions.eq("indCountable", "Y"));
		criteria.add(conjuction);

		Object returnvalue = criteria.uniqueResult();
		double sumResult = returnvalue != null ? (double) returnvalue : 0.0;
		return sumResult;

	}

	private void setPerson(FceIncomeDto fceIncomeDto, FceIncome fceIncome) {
		Person person = null;
		if (fceIncomeDto.getIdPerson() != null && fceIncomeDto.getIdPerson() > 0) {
			person = (Person) sessionFactory.getCurrentSession().get(Person.class, fceIncomeDto.getIdPerson());
			fceIncome.setPerson(person);
		}
	}

	private void setFcePerson(FceIncomeDto fceIncomeDto, FceIncome fceIncome) {
		FcePerson fcePerson = null;
		if (fceIncomeDto.getIdFcePerson() != null && fceIncomeDto.getIdFcePerson() > 0) {
			fcePerson = (FcePerson) sessionFactory.getCurrentSession().get(FcePerson.class,
					fceIncomeDto.getIdFcePerson());
			fceIncome.setFcePerson(fcePerson);
		}
	}

	private void setFceEligibility(FceIncomeDto fceIncomeDto, FceIncome fceIncome) {
		FceEligibility fceEligibility = null;
		if (fceIncomeDto.getIdFceEligibility() != null && fceIncomeDto.getIdFceEligibility() > 0) {
			fceEligibility = (FceEligibility) sessionFactory.getCurrentSession().get(FceEligibility.class,
					fceIncomeDto.getIdFceEligibility());
			fceIncome.setFceEligibility(fceEligibility);
		}
	}

}
