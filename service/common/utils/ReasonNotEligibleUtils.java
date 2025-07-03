package us.tx.state.dfps.service.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.FceEligibility;
import us.tx.state.dfps.common.domain.FceReasonNotEligible;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.fce.dto.FceReasonNotEligibleDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ReasonNotEligibleUtils Nov 8, 2017- 6:21:41 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class ReasonNotEligibleUtils {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Method Name: findReasonsNotEligible Method Description:
	 * findReasonsNotEligible
	 * 
	 * @param idFceEligibility
	 * @return List<FceReasonNotEligibleDto>
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public List<FceReasonNotEligibleDto> findReasonsNotEligible(Long idFceEligibility) throws DataNotFoundException {
		List<FceReasonNotEligibleDto> fceReasonNotEligibleDtoList = new ArrayList<>();

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FceReasonNotEligible.class);

		criteria.add(Restrictions.eq("fceEligibility.idFceEligibility", idFceEligibility));
		criteria.addOrder(Order.asc("cdReasonNotEligible"));
		List<FceReasonNotEligible> fceReasonNotEligibleList = criteria.list();

		if (!TypeConvUtil.isNullOrEmpty(fceReasonNotEligibleList)) {
			for (FceReasonNotEligible fceReasonNotEligible : fceReasonNotEligibleList) {

				FceReasonNotEligibleDto fceReasonNotEligibleDto = new FceReasonNotEligibleDto();
				fceReasonNotEligibleDto.setCdReasonNotEligible(fceReasonNotEligible.getCdReasonNotEligible());
				fceReasonNotEligibleDto.setDtLastUpdate(fceReasonNotEligible.getDtLastUpdate());
				if (!TypeConvUtil.isNullOrEmpty(fceReasonNotEligible.getFceEligibility())) {
					fceReasonNotEligibleDto
							.setIdFceEligibility(fceReasonNotEligible.getFceEligibility().getIdFceEligibility());
				}
				fceReasonNotEligibleDto.setIdFceReasonNotEligible(fceReasonNotEligible.getIdFceReasonNotEligible());
				fceReasonNotEligibleDtoList.add(fceReasonNotEligibleDto);
			}
		}

		return fceReasonNotEligibleDtoList;
	}

	public void createReasonsNotEligible(List<String> reasonNotEligibleCodes, long idFceEligibility)
			throws DataNotFoundException {

		if (null != reasonNotEligibleCodes) {
			reasonNotEligibleCodes.stream().forEach(cdReasonNotEligible -> {
				FceReasonNotEligible fceReasonNotEligibleDB = new FceReasonNotEligible();
				FceEligibility fceEligibility = new FceEligibility();
				fceEligibility.setIdFceEligibility(idFceEligibility);
				fceReasonNotEligibleDB.setCdReasonNotEligible(cdReasonNotEligible);
				fceReasonNotEligibleDB.setFceEligibility(fceEligibility);
				sessionFactory.getCurrentSession().save(fceReasonNotEligibleDB);
			}

			);
		}
	}

}
