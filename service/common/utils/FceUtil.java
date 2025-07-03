package us.tx.state.dfps.service.common.utils;

import java.util.Date;
import java.util.Locale;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.FceApplication;
import us.tx.state.dfps.common.domain.FceEligibility;
import us.tx.state.dfps.common.domain.FcePerson;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.fce.IncomeExpenditureDto;
import us.tx.state.dfps.service.fce.LegacyApplicationDto;
import us.tx.state.dfps.service.fce.dto.EligibilityDeterminationFceDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fostercarereview.dto.FosterCareReviewDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

@Repository
public class FceUtil {

	@Autowired
	static MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	public ReviewUtils reviewUtils;

	@Autowired
	public EventService eventService;

	@Value("${FceUtil.verifyOpenStage}")
	private String verifyOpenStageSql;

	/**
	 * Method Name: verifyOpenStage Method Description: verify the stage is open
	 * 
	 * @param idStage
	 * 
	 * @throws NoSuchMessageException
	 */
	public void verifyOpenStage(Long idStage) {

		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(verifyOpenStageSql)
				.addScalar("indStageClose", StandardBasicTypes.STRING).setParameter("idStage", idStage);
		String indStageClose = (String) sQLQuery.uniqueResult();

		if (isTrue(indStageClose)) {
			throw new ServiceLayerException(messageSource.getMessage("MSG_SYS_STAGE_CLOSED", null, Locale.US));

		}

	}

	/**
	 * Method Name: isTrue Method Description: null check.
	 * 
	 * @param indStageClose
	 * @return
	 */
	private boolean isTrue(String indStageClose) {
		return ((indStageClose != null) && (indStageClose.equals("Y") || indStageClose.equals("1")));
	}

	/**
	 * Method Name: verifyCanSave Method Description: verify to save the bean
	 * 
	 * @param legacyApplicationDto
	 * 
	 * @throws NoSuchMessageException
	 */
	public void verifyCanSave(LegacyApplicationDto legacyApplicationDto, IncomeExpenditureDto incomeExpenditureDto) {
		Long idStage = null;
		Long idLastUpdatePerson = null;
		if (legacyApplicationDto != null) {
			idStage = legacyApplicationDto.getIdStage();
			idLastUpdatePerson = legacyApplicationDto.getIdLastUpdatePerson();
		} else if (incomeExpenditureDto != null) {
			idStage = incomeExpenditureDto.getIdStage();
			idLastUpdatePerson = incomeExpenditureDto.getFceApplicationDto().getIdLastUpdatePerson();
		}

		verifyNonZero("idStage", idStage);
		verifyOpenStage(idStage);
		verifyNonZero("idLastUpdatePerson", idLastUpdatePerson);

	}

	/**
	 * Method Name: verifyNonZero Method Description: verify for non-zero.
	 * 
	 * @param string
	 * @param value
	 */
	public static void verifyNonZero(String string, Long value) {
		if (TypeConvUtil.isNullOrEmpty(value)) {
			throw new DataNotFoundException(
					messageSource.getMessage("common." + string + ".mandatory", null, Locale.US));
		}

	}

	/**
	 * Method Name: verifyCanSave Method Description: verifyCanSave eligibility
	 * details
	 * 
	 * @param eligibilityDeterminationDBDto
	 * 
	 * @throws NoSuchMessageException
	 */
	public void verifyCanSaveEligibilityDtl(EligibilityDeterminationFceDto eligibilityDeterminationDBDto) {
		Long idStage = null;// eligibilityDeterminationDBDto.getIdStage();
		verifyNonZero("idStage", idStage);
		verifyOpenStage(idStage);
		// FceUtil.verifyNonZero(
		// "idLastUpdatePerson",eligibilityDeterminationDBDto.getIdLastUpdatePerson()
		// );

	}

	public void verifyCanSave(FosterCareReviewDto fosterCareReviewDto) throws NoSuchMessageException {
		long idStage = fosterCareReviewDto.getIdStage();
		verifyNonZero("idStage", idStage);
		verifyOpenStage(idStage);
		verifyNonZero("idLastUpdatePerson", fosterCareReviewDto.getIdLastUpdatePerson());

	}

	public void syncFceEligibilityStatus(FceEligibility fceEligibilityLocal) throws Exception {
		long idPerson = fceEligibilityLocal.getIdPerson().longValue();
		long idFcePerson = fceEligibilityLocal.getIdFcePerson().longValue();
		Long idEligibilityEvent = fceEligibilityLocal.getIdEligibilityEvent();
		FcePerson fcePersonLocal = this.findFcePerson(idFcePerson);

		Person personDB = this.findPerson(idPerson);
		reviewUtils.updateBirthday(fcePersonLocal, personDB);

		EventDto eventDto = eventService.getEvent(idEligibilityEvent);
		String cdEventStatus = eventDto.getCdEventStatus();

		// CodesTables.CEVTSTAT_NEW
		if ((cdEventStatus.equals("NEW"))) {
		}
	}

	public FcePerson findFcePerson(long idFcePerson) {
		FcePerson fcePerson = (FcePerson) sessionFactory.getCurrentSession().get(FcePerson.class, idFcePerson);

		if (fcePerson != null) {
			return fcePerson;
		} else {
			throw new ServiceLayerException(messageSource.getMessage(ServiceConstants.DATA_NOT_FOUND, null, Locale.US));
		}
	}

	public Person findPerson(long personId) {
		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, personId);
		if (person != null) {
			return person;
		} else {
			throw new ServiceLayerException(messageSource.getMessage(ServiceConstants.DATA_NOT_FOUND, null, Locale.US));

		}
	}

	/**
	 * Method Name: updateBirthday Method Description: This method is used to
	 * update person birthdate
	 * 
	 * @param FcePerson
	 * @param PersonDto
	 */
	public void updateBirthday(FcePerson fcePersonLocal, PersonDto person) {

		fcePersonLocal.setDtBirth(person.getDtPersonBirth());
		fcePersonLocal.setNbrAge(person.getNbrPersonAge());
		fcePersonLocal.setIndDobApprox(person.getIndPersonDobApprox().toString());
	}

	/**
	 * Method Name: calculateAge Method Description: This method is used for
	 * calculating age
	 * 
	 * @param PersonDto
	 */

	public long calculateAge(PersonDto personDto) {
		Date birthDate = personDto.getDtPersonBirth();
		if (birthDate != null) {
			return DateUtils.getAge(birthDate);
		}

		return ServiceConstants.ZERO_VAL;
	}

	/**
	 * Method Name: dateLastUpdateCheck Method Description: This method is used
	 * for Date last update check
	 * 
	 * @param fceEligibilityDto
	 * @param fceApplication
	 * @param IncomeExpenditureDto
	 */
	public void dateLastUpdateCheck(FceEligibilityDto fceEligibilityDto, FceApplication fceApplication,
			IncomeExpenditureDto incomeExpenditureDto) {

		Date applicationDateLastUpdate = fceApplication.getDtLastUpdate();
		Date eligibilityDateLastUpdate = fceEligibilityDto.getDtLastUpdate();

		if ((applicationDateLastUpdate.compareTo(incomeExpenditureDto.getFceApplicationDto().getDtLastUpdate()) != 0)
				|| (eligibilityDateLastUpdate
						.compareTo(incomeExpenditureDto.getFceEligibilityDto().getDtLastUpdate()) != 0)) {
			throw new ServiceLayerException("Time Mismatch error", new Long(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH),
					null);
		}
	}

}