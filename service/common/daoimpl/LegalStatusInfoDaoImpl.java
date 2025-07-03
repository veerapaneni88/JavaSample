package us.tx.state.dfps.service.common.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.LegalStatus;
import us.tx.state.dfps.service.common.dao.LegalStatusInfoDao;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Nov 6, 2017- 5:47:43 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class LegalStatusInfoDaoImpl implements LegalStatusInfoDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private MessageSource messageSource;

	/**
	 * Method Name: fetchLegalStatusListForChild Method Description: Fetches all
	 * the Legal Statuses
	 * 
	 * @param idChildPerson
	 * @return List<LegalStatusDto> @
	 */
	@Override
	public List<LegalStatusDto> fetchLegalStatusListForChild(Long idChildPerson) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LegalStatus.class);
		criteria.add(Restrictions.eq("person.idPerson", idChildPerson));
		criteria.addOrder(Order.desc("dtLegalStatStatusDt"));
		List<LegalStatus> legalStatusList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(legalStatusList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cses32dDaoImpl.no.legal.record", null, Locale.US));
		}
		List<LegalStatusDto> legalStatusDtoList = new ArrayList<LegalStatusDto>();
		for (LegalStatus legalStatus : legalStatusList) {
			LegalStatusDto legalStatusDto = new LegalStatusDto();
			legalStatusDto.setIdLegalStatEvent(legalStatus.getIdLegalStatEvent());
			if (!TypeConvUtil.isNullOrEmpty(legalStatus.getPerson())) {
				legalStatusDto.setIdPerson(legalStatus.getPerson().getIdPerson());
			}
			legalStatusDto.setDtLastUpdate(legalStatus.getDtLastUpdate());
			legalStatusDto.setIdCase(legalStatus.getIdCase());
			legalStatusDto.setCdLegalStatCnty(legalStatus.getCdLegalStatCnty());
			legalStatusDto.setCdLegalStatStatus(legalStatus.getCdLegalStatStatus());
			legalStatusDto.setDtLegalStatStatusDt(legalStatus.getDtLegalStatStatusDt());
			legalStatusDto.setTxtLegalStatCauseNbr(legalStatus.getTxtLegalStatCauseNbr());
			legalStatusDto.setTxtLegalStatCourtNbr(legalStatus.getTxtLegalStatCourtNbr());
			legalStatusDto.setDtLegalStatTmcDismiss(legalStatus.getDtLegalStatTmcDismiss());
			legalStatusDto.setIndCsupSend(legalStatus.getIndCsupSend());
			legalStatusDto.setCdCourtNbr(legalStatus.getCdCourtNbr());
			legalStatusDto.setCdDischargeRsn(legalStatus.getCdDischargeRsn());
			legalStatusDto.setIdLastUpdatePerson(legalStatus.getIdLastUpdatePerson());
			legalStatusDtoList.add(legalStatusDto);

		}
		return legalStatusDtoList;
	}

}
