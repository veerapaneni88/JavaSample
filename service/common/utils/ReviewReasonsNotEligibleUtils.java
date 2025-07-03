package us.tx.state.dfps.service.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.FceReview;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.fce.dto.FceContextDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.dto.FceReviewDto;
import us.tx.state.dfps.service.fostercarereview.dao.FceReviewDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ReasonNotEligibleUtils Nov 8, 2017- 6:21:41 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class ReviewReasonsNotEligibleUtils {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private FceReviewDao fceReviewDao;

	@Autowired
	private IncomeUtil incomeUtil;

	@Autowired
	private ReviewUtils reviewUtils;

	/**
	 * @param fceContextDto
	 * @param fceEligibilityDto
	 * @return
	 * @throws DataNotFoundException
	 */
	public FceEligibilityDto calculateReasonsNotEligible(FceContextDto fceContextDto,
			FceEligibilityDto fceEligibilityDto, FceReviewDto fceReviewDto) throws DataNotFoundException {

		fceEligibilityDto = checkCitizenship(fceContextDto, fceEligibilityDto);
		fceEligibilityDto.setIndParentalDeprivation(fceEligibilityDto.getIndMeetsDpOrNotEs());
		fceEligibilityDto = checkChildIncomeVersusFosterCarePay(fceContextDto, fceEligibilityDto, fceReviewDto);
		return fceEligibilityDto;
	}

	public FceEligibilityDto checkCitizenship(FceContextDto fceContextDto, FceEligibilityDto fceEligibilityDto) {
		fceEligibilityDto.setIndChildQualifiedCitizen(ServiceConstants.STRING_IND_Y);

		String cdPersonCitizenship = fceReviewDao.getById(fceContextDto.getIdFceReview()).getCdPersonCitizenship();
		if (CodesConstant.CCTZNSTA_TMR.equals(cdPersonCitizenship)) {
			fceEligibilityDto.setIndChildQualifiedCitizen(ServiceConstants.STRING_IND_N);
		}
		return fceEligibilityDto;
	}

	public FceEligibilityDto checkChildIncomeVersusFosterCarePay(FceContextDto fceContextDto,
			FceEligibilityDto fceEligibilityDto, FceReviewDto fceReviewDto)

	{
		long idFcePerson = fceEligibilityDto.getIdFcePerson();
		double countableIncome = incomeUtil.calculateCountableIncome(idFcePerson);

		/*
		 * FcePerson fcePerson =
		 * fcePersonDao.getFcepersonById(fceContextDto.getIdFcePerson()); int
		 * age = 0; if (fcePerson != null) { age =
		 * fcePerson.getNbrAge().intValue(); }
		 */

		long idPerson = fceEligibilityDto.getIdPerson();

		FceReview entity = fceReviewDao.getById(fceContextDto.getIdFceReview());

		FceReviewDto dto = reviewUtils.findReview(fceContextDto.getIdFceReview());
		reviewUtils.syncFosterCareRate(fceEligibilityDto, dto, idPerson, true);

		Double fosterCareUnitRateDouble = entity.getAmtFosterCareRate();
		if (fosterCareUnitRateDouble == null) {
			return fceEligibilityDto;
		}
		double fosterCareUnitRate = fosterCareUnitRateDouble.doubleValue();
		int numberOfDaysInMonth = 31;
		double fosterCarePayments = fosterCareUnitRate * numberOfDaysInMonth;

		fceEligibilityDto.setAmtCountableIncome(new Double(countableIncome));
		fceEligibilityDto.setAmtIncomeLimit(new Double(fosterCarePayments));

		fceReviewDto.setIndChildIncomeGtFcPay(ServiceConstants.N);
		if (countableIncome >= fosterCarePayments) {
			fceReviewDto.setIndChildIncomeGtFcPay(ServiceConstants.Y);

		}

		return fceEligibilityDto;
	}

}
