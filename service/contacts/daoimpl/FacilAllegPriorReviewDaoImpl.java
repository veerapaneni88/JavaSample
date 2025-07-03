package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.FacilAllegPriorReview;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.contacts.dao.FacilAllegPriorReviewDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.FacilityAllegationPriorDto;

@Repository
public class FacilAllegPriorReviewDaoImpl implements FacilAllegPriorReviewDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 * Method Name: adFacilityAllegationPriorReview Method Description:this
	 * method inserts and delete in FACIL_ALLEG_PRIOR_REVIEW Table.
	 * 
	 * @param facilityAllegationPriorDto
	 * @return long @
	 */
	@Override

	public long adFacilityAllegationPriorReview(FacilityAllegationPriorDto facilityAllegationPriorDto) {
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(facilityAllegationPriorDto.getArchInputStruct().getCreqFuncCd())) {
			FacilAllegPriorReview allegPriorReview = new FacilAllegPriorReview();
			allegPriorReview.setIdAllegation(facilityAllegationPriorDto.getUlIdAllegation());
			Stage stage = new Stage();
			stage.setIdStage(facilityAllegationPriorDto.getUlIdReviewStage());
			allegPriorReview.setStage(stage);
			allegPriorReview.setIdReviewVictim(facilityAllegationPriorDto.getUlIdReviewVictim());
			allegPriorReview.setIdReviewAllegedPerp(facilityAllegationPriorDto.getUlIdReviewAllegedPerp());
			allegPriorReview.setCdReviewAllegType(facilityAllegationPriorDto.getCdReviewAllegType());
			allegPriorReview.setCdReviewAllegDisp(facilityAllegationPriorDto.getCdReviewAllegDisp());
			if (isValid(facilityAllegationPriorDto.getCdReviewAllegDispSupr())) {
				allegPriorReview.setCdReviewAllegDispSupr(facilityAllegationPriorDto.getCdReviewAllegDispSupr());
			} else {
				allegPriorReview.setCdReviewAllegDispSupr(ServiceConstants.EMPTY_STRING);
			}
			allegPriorReview.setCdReviewAllegClss(facilityAllegationPriorDto.getCdReviewAllegClss());
			if (isValid(facilityAllegationPriorDto.getCdReviewAllegClssSupr())) {
				allegPriorReview.setCdReviewAllegClssSupr(facilityAllegationPriorDto.getCdReviewAllegClssSupr());
			} else {
				allegPriorReview.setCdReviewAllegClssSupr(ServiceConstants.EMPTY_STRING);
			}
			sessionFactory.getCurrentSession().save(allegPriorReview);

		} else if (ServiceConstants.REQ_FUNC_CD_DELETE
				.equals(facilityAllegationPriorDto.getArchInputStruct().getCreqFuncCd())) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FacilAllegPriorReview.class);
			criteria.add(Restrictions.eq("stage.idStage", facilityAllegationPriorDto.getUlIdReviewStage()));

			List<FacilAllegPriorReview> allegPriorReviews = criteria.list();
			for (FacilAllegPriorReview priorReview : allegPriorReviews) {
				sessionFactory.getCurrentSession().delete(priorReview);
			}
		} else {
			throw new DataNotFoundException(messageSource.getMessage("ArcErrBadFuncCd", null, Locale.US));
		}
		return ServiceConstants.Zero;

	}

	/**
	 * Checks to see if a given string is valid. This includes checking that the
	 * string is not null or empty.
	 *
	 * @param value
	 *            - the string that is being evaluated
	 * @return boolean - whether the string is valid
	 */
	private boolean isValid(String value) {
		if (value == null) {
			return false;
		}
		String trimmedString = value.trim();
		return (trimmedString.length() > 0);
	}

}
