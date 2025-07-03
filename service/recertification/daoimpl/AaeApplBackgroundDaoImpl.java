package us.tx.state.dfps.service.recertification.daoimpl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.AdptAsstAppEventLink;
import us.tx.state.dfps.common.domain.AdptEligApplication;
import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.adoptionasstnc.ApplicationBackgroundDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.AaeApplAndDetermReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.recertification.dao.AaeApplBackgroundDao;
import us.tx.state.dfps.service.workload.dto.AdptAsstAppEventLinkDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Performs
 * some of the database operations Oct 10, 2017- 10:25:57 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class AaeApplBackgroundDaoImpl implements AaeApplBackgroundDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${AaeApplBackgroundDaoImpl.selectLatestAdptAsstEligApplSql}")
	private transient String selectLatestAdptAsstEligApplSql;

	@Autowired
	MessageSource messageSource;

	@Value("${AaeApplBackgroundDaoImpl.selectAdptEligAppUsingIdEventSql}")
	private String selectAdptEligAppUsingIdEventSql;

	@Value("${AaeApplBackgroundDaoImpl.fetchLinkedAdoApplicationEvent}")
	private String fetchLinkedAdoApplicationEvent;

	/*
	 * @Value("${AaeApplBackgroundDaoImpl.selectAdptEligApplicationSql}")
	 * private String selectAdptEligApplicationSql;
	 */

	@Value("${AaeApplBackgroundDaoImpl.selectLatestAdptAsstEligAppl}")
	private String selectLatestAdptAsstEligAppl;

	@Value("${AaeApplBackgroundDaoImpl.fetchIdEventForLatestApplicationForStage}")
	private String fetchIdEventForLatestApplicationForStage;

	private static final Logger log = Logger.getLogger(AaeApplBackgroundDaoImpl.class);

	/**
	 * Method Name: selectLatestAdptAsstEligAppl Method Description:This method
	 * fetches latest AAE Application APRV using idPerson and idStage
	 * 
	 * @param idPerson
	 * @param idStage
	 * @return Long @
	 */
	@Override
	public Long selectLatestAdptAsstEligAppl(Long idPerson, Long idStage) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectLatestAdptAsstEligApplSql)

				.setParameter("idPerson", idPerson).setParameter("idStage", idStage);

		BigDecimal idAdptEligApplication = (BigDecimal) query.uniqueResult();

		if (TypeConvUtil.isNullOrEmpty(idAdptEligApplication)) {
			throw new DataNotFoundException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}
		Long idAdptApplication = idAdptEligApplication.longValue();

		return idAdptApplication;

	}

	/**
	 *
	 * @param idAppEvent
	 * @return ApplicationBackgroundValueDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ApplicationBackgroundDto selectAdptAsstEligAppFromEvent(Long idAppEvent) {
		log.debug("Entering method selectAdptAsstEligAppFromEvent in AaeApplBackgroundDaoImpl");
		// ApplicationBackgroundDto appValueDto = new
		// ApplicationBackgroundDto();
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(selectAdptEligAppUsingIdEventSql)
				.addScalar("idAdptEligApplication", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				// changed from string to long to avoid type mismatch
				.addScalar("nbrChildQualifyAge", StandardBasicTypes.LONG)
				.addScalar("indMngngCvs", StandardBasicTypes.STRING)
				.addScalar("indLcpaMngngCvs", StandardBasicTypes.STRING)
				.addScalar("indOtherMngngCvs", StandardBasicTypes.STRING)
				.addScalar("nmCvsAgency", StandardBasicTypes.STRING)
				.addScalar("txtCvsAgencyAddr", StandardBasicTypes.STRING)
				.addScalar("dtPlcmtPlanned", StandardBasicTypes.DATE)
				.addScalar("dtApplSubmitted", StandardBasicTypes.DATE)
				.addScalar("cdAdptParentAddrState", StandardBasicTypes.STRING)
				.addScalar("indInternationalPlcmt", StandardBasicTypes.STRING)
				.addScalar("indFairHearing", StandardBasicTypes.STRING)
				.addScalar("dtFairHearing", StandardBasicTypes.DATE)
				.addScalar("indAdptHomeStudy", StandardBasicTypes.STRING)
				.addScalar("dtAdptHomeStudy", StandardBasicTypes.DATE)
				.addScalar("indBkgCheck", StandardBasicTypes.STRING).addScalar("indChildSix", StandardBasicTypes.STRING)
				.addScalar("indChildTwoMinority", StandardBasicTypes.STRING)
				.addScalar("indWithSibling", StandardBasicTypes.STRING)
				.addScalar("idQualSibPerson", StandardBasicTypes.LONG)
				.addScalar("indHandicapCond", StandardBasicTypes.STRING)
				.addScalar("indAdptRegExch", StandardBasicTypes.STRING)
				.addScalar("txtNmAdptRegExch", StandardBasicTypes.STRING)
				.addScalar("indEmotBond", StandardBasicTypes.STRING)
				.addScalar("indCtznshpUs", StandardBasicTypes.STRING)
				.addScalar("indCtznshpPerm", StandardBasicTypes.STRING)
				.addScalar("indCtznshpOthQualAlien", StandardBasicTypes.STRING)
				.addScalar("indCtznshpUnknown", StandardBasicTypes.STRING)
				.addScalar("indAdoptedByCtzn", StandardBasicTypes.STRING)
				.addScalar("dtChildEnteredUs", StandardBasicTypes.DATE)
				.addScalar("txtChildEnteredUs", StandardBasicTypes.STRING)
				.addScalar("indDocBirthVerfy", StandardBasicTypes.STRING)
				.addScalar("indDocForm2250", StandardBasicTypes.STRING)
				.addScalar("indDocMedProf", StandardBasicTypes.STRING)
				.addScalar("indDocTermOrdr", StandardBasicTypes.STRING)
				.addScalar("indDocForm2253ab", StandardBasicTypes.STRING)
				.addScalar("indDocCourtOrdr", StandardBasicTypes.STRING)
				.addScalar("indDocHmStudy", StandardBasicTypes.STRING)
				.addScalar("indDocBirthVerfySibl", StandardBasicTypes.STRING)
				.addScalar("indDocPolicyWaiver", StandardBasicTypes.STRING)
				.addScalar("indDocFbiBgchk", StandardBasicTypes.STRING)
				.addScalar("indDocTermOrdrSibl", StandardBasicTypes.STRING)
				.addScalar("indDoctorLetter", StandardBasicTypes.STRING)
				.addScalar("indDocSrvcLvlSh", StandardBasicTypes.STRING)
				.addScalar("indDocImmigrationDocs", StandardBasicTypes.STRING)
				.addScalar("cdWithdrawRsn", StandardBasicTypes.STRING)
				.addScalar("txtWithdrawRsn", StandardBasicTypes.STRING)
				.addScalar("indSsaMedReq", StandardBasicTypes.STRING)
				.addScalar("indDocSsaReq", StandardBasicTypes.STRING)
				.addScalar("indDocMedProfReq", StandardBasicTypes.STRING)
				.addScalar("indDocFromSsa", StandardBasicTypes.STRING)
				.addScalar("indLoctAdptFam", StandardBasicTypes.STRING)
				.addScalar("txtLoctAdptFamEfrt", StandardBasicTypes.STRING)
				.addScalar("indNoAdptPlcRslt", StandardBasicTypes.STRING)
				.addScalar("txtNoAdptPlcRsltRsn", StandardBasicTypes.STRING)
				.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING)
				.addScalar("dtParentalRtsTermAll", StandardBasicTypes.DATE)
				.addScalar("idLegalStatEvent", StandardBasicTypes.LONG).setParameter("idEvent", idAppEvent)
				.setResultTransformer(Transformers.aliasToBean(ApplicationBackgroundDto.class));
		List<ApplicationBackgroundDto> listapplicationBackground = sQLQuery1.list();

		if (TypeConvUtil.isNullOrEmpty(listapplicationBackground)) {
			throw new DataNotFoundException(
					messageSource.getMessage("applicationBackground.not.found", null, Locale.US));
		}
		ApplicationBackgroundDto applicationBackgroundDto = listapplicationBackground.get(0);
		log.debug("Exiting method selectAdptAsstEligAppFromEvent in AaeApplBackgroundDaoImpl");
		return applicationBackgroundDto;
	}

	/**
	 *
	 * @param appValueDto
	 * @return void @
	 */
	@Override
	public Long updateAdptAsstEligApplication(ApplicationBackgroundDto appValueDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AdptEligApplication.class);
		criteria.add(Restrictions.eq("idAdptEligApplication", appValueDto.getIdAdptEligApplication()));
		AdptEligApplication adptEligApplication = (AdptEligApplication) criteria.uniqueResult();
		adptEligApplication.setIndMngngCvs(stringToCharacter(appValueDto.getIndMngngCvs()));
		adptEligApplication.setIndLcpaMngngCvs(stringToCharacter(appValueDto.getIndLcpaMngngCvs()));
		adptEligApplication.setIndOtherMngngCvs(stringToCharacter(appValueDto.getIndOtherMngngCvs()));
		adptEligApplication.setNmCvsAgency(appValueDto.getNmCvsAgency());
		adptEligApplication.setTxtCvsAgencyAddr(appValueDto.getTxtCvsAgencyAddr());
		adptEligApplication.setDtApplSubmitted(appValueDto.getDtPlcmtPlanned());
		adptEligApplication.setDtApplSubmitted(appValueDto.getDtApplSubmitted());
		adptEligApplication.setCdAdptParentAddrState(appValueDto.getCdAdptParentAddrState());
		adptEligApplication.setIndInternationalPlcmt(stringToCharacter(appValueDto.getIndInternationalPlcmt()));
		adptEligApplication.setIndFairHearing(stringToCharacter(appValueDto.getIndFairHearing()));
		adptEligApplication.setDtFairHearing(appValueDto.getDtFairHearing());
		adptEligApplication.setIndAdptHomeStudy(stringToCharacter(appValueDto.getIndAdptHomeStudy()));
		adptEligApplication.setDtAdptHomeStudy(appValueDto.getDtAdptHomeStudy());
		adptEligApplication.setIndBkgCheck(stringToCharacter(appValueDto.getIndBkgCheck()));
		adptEligApplication.setIndChildSix(stringToCharacter(appValueDto.getIndChildSix()));
		adptEligApplication.setIndChildTwoMinority(stringToCharacter(appValueDto.getIndChildTwoMinority()));
		adptEligApplication.setIndWithSibling(stringToCharacter(appValueDto.getIndWithSibling()));
		Person person = (Person) sessionFactory.getCurrentSession().load(Person.class,
				appValueDto.getIdQualSibPerson());
		adptEligApplication.setPersonByIdQualSibPerson(person);
		adptEligApplication.setIndHandicapCond(stringToCharacter(appValueDto.getIndHandicapCond()));
		adptEligApplication.setIndAdptRegExch(stringToCharacter(appValueDto.getIndAdptRegExch()));
		adptEligApplication.setTxtNmAdptRegExch(appValueDto.getTxtNmAdptRegExch());
		adptEligApplication.setIndEmotBond((stringToCharacter(appValueDto.getIndEmotBond())));
		adptEligApplication.setIndCtznshpUs(stringToCharacter(appValueDto.getIndCtznshpUs()));
		adptEligApplication.setIndCtznshpPerm(stringToCharacter(appValueDto.getIndCtznshpPerm()));
		adptEligApplication.setIndCtznshpUnknown(stringToCharacter(appValueDto.getIndCtznshpOthQualAlien()));
		adptEligApplication.setIndCtznshpUnknown(stringToCharacter(appValueDto.getIndCtznshpUnknown()));
		adptEligApplication.setIndAdoptedByCtzn(stringToCharacter(appValueDto.getIndAdoptedByCtzn()));
		adptEligApplication.setDtChildEnteredUs(appValueDto.getDtChildEnteredUs());
		adptEligApplication.setTxtChildEnteredUs(appValueDto.getTxtChildEnteredUs());
		adptEligApplication.setIndDocBirthVerfy(stringToCharacter(appValueDto.getIndDocBirthVerfy()));
		adptEligApplication.setIndDocForm2250(stringToCharacter(appValueDto.getIndDocForm2250()));
		adptEligApplication.setIndDocMedProf(stringToCharacter(appValueDto.getIndDocMedProf()));
		adptEligApplication.setIndDocTermOrdr(stringToCharacter(appValueDto.getIndDocTermOrdr()));
		adptEligApplication.setIndDocForm2253ab(stringToCharacter(appValueDto.getIndDocForm2253ab()));
		adptEligApplication.setIndDocCourtOrdr(stringToCharacter(appValueDto.getIndDocCourtOrdr()));
		adptEligApplication.setIndDocHmStudy(stringToCharacter(appValueDto.getIndDocHmStudy()));
		adptEligApplication.setIndDocBirthVerfySibl(stringToCharacter(appValueDto.getIndDocBirthVerfySibl()));
		adptEligApplication.setIndDocPolicyWaiver(stringToCharacter(appValueDto.getIndDocPolicyWaiver()));
		adptEligApplication.setIndDocFbiBgchk(stringToCharacter(appValueDto.getIndDocFbiBgchk()));
		adptEligApplication.setIndDocTermOrdrSibl(stringToCharacter(appValueDto.getIndDocTermOrdrSibl()));
		adptEligApplication.setIndDoctorLetter(stringToCharacter(appValueDto.getIndDoctorLetter()));
		adptEligApplication.setIndDocSrvcLvlSh(stringToCharacter(appValueDto.getIndDocSrvcLvlSh()));
		adptEligApplication.setIndDocImmigrationDocs(stringToCharacter(appValueDto.getIndDocImmigrationDocs()));
		adptEligApplication.setCdWithdrawRsn(appValueDto.getCdWithdrawRsn());
		adptEligApplication.setTxtWithdrawRsn(appValueDto.getTxtWithdrawRsn());
		adptEligApplication.setIdLastUpdatePerson(appValueDto.getIdLastUpdatePerson());
		adptEligApplication.setIndSsaMedReq(stringToCharacter(appValueDto.getIndSsaMedReq()));
		adptEligApplication.setIndDocSsaReq(stringToCharacter(appValueDto.getIndDocSsaReq()));
		adptEligApplication.setIndDocMedProfReq(stringToCharacter(appValueDto.getIndDocMedProfReq()));
		adptEligApplication.setIndDocFromSsa(stringToCharacter(appValueDto.getIndDocFromSsa()));
		adptEligApplication.setIndLoctAdptFam(stringToCharacter(appValueDto.getIndLoctAdptFam()));
		adptEligApplication.setTxtLoctAdptFamEfrt(appValueDto.getTxtLoctAdptFamEfrt());
		adptEligApplication.setIndNoAdptPlcRslt(stringToCharacter(appValueDto.getIndNoAdptPlcRslt()));
		adptEligApplication.setTxtNoAdptPlcRsltRsn(appValueDto.getTxtNoAdptPlcRsltRsn());
		adptEligApplication.setCdLegalStatStatus(appValueDto.getCdLegalStatStatus());
		adptEligApplication.setDtParentalRtsTermAll(appValueDto.getDtParentalRtsTermAll());
		adptEligApplication.setIdLegalStatEvent(appValueDto.getIdLegalStatEvent());
		adptEligApplication.setIdAdptEligApplication(appValueDto.getIdAdptEligApplication());
		sessionFactory.getCurrentSession().saveOrUpdate(adptEligApplication);
		return adptEligApplication.getIdAdptEligApplication();

	}

	/**
	 *
	 * @param appValueDto
	 * @return Long @
	 */
	@Override
	public Long insertAdptAsstEligApplication(ApplicationBackgroundDto appValueDto) {
		log.debug("Entering method insertAdptAsstEligApplication in AaeApplBackgroundDaoImpl");
		// Criteria criteria =
		// sessionFactory.getCurrentSession().createCriteria(AdptEligApplication.class);
		// AdptEligApplication adptEligApplication = (AdptEligApplication)
		// criteria.uniqueResult();
		// AdptEligApplication adptEligApplication = (AdptEligApplication)
		// sessionFactory.getCurrentSession().load(AdptEligApplication.class,appValueDto.getIdAdptEligApplication());
		AdptEligApplication adptEligApplication = new AdptEligApplication();
		adptEligApplication.setIndMngngCvs(stringToCharacter(appValueDto.getIndMngngCvs()));
		adptEligApplication.setIndLcpaMngngCvs(stringToCharacter(appValueDto.getIndLcpaMngngCvs()));
		adptEligApplication.setIndOtherMngngCvs(stringToCharacter(appValueDto.getIndOtherMngngCvs()));
		adptEligApplication.setNmCvsAgency(appValueDto.getNmCvsAgency());
		adptEligApplication.setTxtCvsAgencyAddr(appValueDto.getTxtCvsAgencyAddr());
		adptEligApplication.setDtApplSubmitted(appValueDto.getDtPlcmtPlanned());
		adptEligApplication.setDtApplSubmitted(appValueDto.getDtApplSubmitted());
		adptEligApplication.setCdAdptParentAddrState(appValueDto.getCdAdptParentAddrState());
		adptEligApplication.setIndInternationalPlcmt(stringToCharacter(appValueDto.getIndInternationalPlcmt()));
		adptEligApplication.setIndFairHearing(stringToCharacter(appValueDto.getIndFairHearing()));
		adptEligApplication.setDtFairHearing(appValueDto.getDtFairHearing());
		adptEligApplication.setIndAdptHomeStudy(stringToCharacter(appValueDto.getIndAdptHomeStudy()));
		adptEligApplication.setDtAdptHomeStudy(appValueDto.getDtAdptHomeStudy());
		adptEligApplication.setIndBkgCheck(stringToCharacter(appValueDto.getIndBkgCheck()));
		adptEligApplication.setIndChildSix(stringToCharacter(appValueDto.getIndChildSix()));
		adptEligApplication.setIndChildTwoMinority(stringToCharacter(appValueDto.getIndChildTwoMinority()));
		adptEligApplication.setIndWithSibling(stringToCharacter(appValueDto.getIndWithSibling()));
		Person person = (Person) sessionFactory.getCurrentSession().load(Person.class,
				appValueDto.getIdQualSibPerson());
		adptEligApplication.setPersonByIdQualSibPerson(person);
		adptEligApplication.setIndHandicapCond(stringToCharacter(appValueDto.getIndHandicapCond()));
		adptEligApplication.setIndAdptRegExch(stringToCharacter(appValueDto.getIndAdptRegExch()));
		adptEligApplication.setTxtNmAdptRegExch(appValueDto.getTxtNmAdptRegExch());
		adptEligApplication.setIndEmotBond((stringToCharacter(appValueDto.getIndEmotBond())));
		adptEligApplication.setIndCtznshpUs(stringToCharacter(appValueDto.getIndCtznshpUs()));
		adptEligApplication.setIndCtznshpPerm(stringToCharacter(appValueDto.getIndCtznshpPerm()));
		adptEligApplication.setIndCtznshpUnknown(stringToCharacter(appValueDto.getIndCtznshpOthQualAlien()));
		adptEligApplication.setIndCtznshpUnknown(stringToCharacter(appValueDto.getIndCtznshpUnknown()));
		adptEligApplication.setIndAdoptedByCtzn(stringToCharacter(appValueDto.getIndAdoptedByCtzn()));
		adptEligApplication.setDtChildEnteredUs(appValueDto.getDtChildEnteredUs());
		adptEligApplication.setTxtChildEnteredUs(appValueDto.getTxtChildEnteredUs());
		adptEligApplication.setIndDocBirthVerfy(stringToCharacter(appValueDto.getIndDocBirthVerfy()));
		adptEligApplication.setIndDocForm2250(stringToCharacter(appValueDto.getIndDocForm2250()));
		adptEligApplication.setIndDocMedProf(stringToCharacter(appValueDto.getIndDocMedProf()));
		adptEligApplication.setIndDocTermOrdr(stringToCharacter(appValueDto.getIndDocTermOrdr()));
		adptEligApplication.setIndDocForm2253ab(stringToCharacter(appValueDto.getIndDocForm2253ab()));
		adptEligApplication.setIndDocCourtOrdr(stringToCharacter(appValueDto.getIndDocCourtOrdr()));
		adptEligApplication.setIndDocHmStudy(stringToCharacter(appValueDto.getIndDocHmStudy()));
		adptEligApplication.setIndDocBirthVerfySibl(stringToCharacter(appValueDto.getIndDocBirthVerfySibl()));
		adptEligApplication.setIndDocPolicyWaiver(stringToCharacter(appValueDto.getIndDocPolicyWaiver()));
		adptEligApplication.setIndDocFbiBgchk(stringToCharacter(appValueDto.getIndDocFbiBgchk()));
		adptEligApplication.setIndDocTermOrdrSibl(stringToCharacter(appValueDto.getIndDocTermOrdrSibl()));
		adptEligApplication.setIndDoctorLetter(stringToCharacter(appValueDto.getIndDoctorLetter()));
		adptEligApplication.setIndDocSrvcLvlSh(stringToCharacter(appValueDto.getIndDocSrvcLvlSh()));
		adptEligApplication.setIndDocImmigrationDocs(stringToCharacter(appValueDto.getIndDocImmigrationDocs()));
		adptEligApplication.setCdWithdrawRsn(appValueDto.getCdWithdrawRsn());
		adptEligApplication.setTxtWithdrawRsn(appValueDto.getTxtWithdrawRsn());
		adptEligApplication.setIdLastUpdatePerson(appValueDto.getIdLastUpdatePerson());
		adptEligApplication.setIndSsaMedReq(stringToCharacter(appValueDto.getIndSsaMedReq()));
		adptEligApplication.setIndDocSsaReq(stringToCharacter(appValueDto.getIndDocSsaReq()));
		adptEligApplication.setIndDocMedProfReq(stringToCharacter(appValueDto.getIndDocMedProfReq()));
		adptEligApplication.setIndDocFromSsa(stringToCharacter(appValueDto.getIndDocFromSsa()));
		adptEligApplication.setIndLoctAdptFam(stringToCharacter(appValueDto.getIndLoctAdptFam()));
		adptEligApplication.setTxtLoctAdptFamEfrt(appValueDto.getTxtLoctAdptFamEfrt());
		adptEligApplication.setIndNoAdptPlcRslt(stringToCharacter(appValueDto.getIndNoAdptPlcRslt()));
		adptEligApplication.setTxtNoAdptPlcRsltRsn(appValueDto.getTxtNoAdptPlcRsltRsn());
		adptEligApplication.setCdLegalStatStatus(appValueDto.getCdLegalStatStatus());
		adptEligApplication.setDtParentalRtsTermAll(appValueDto.getDtParentalRtsTermAll());
		adptEligApplication.setIdLegalStatEvent(appValueDto.getIdLegalStatEvent());
		adptEligApplication.setIdAdptEligApplication(appValueDto.getIdAdptEligApplication());

		// added by Srikant to fix bug with insertion
		// no dtCreated in appValueDto
		adptEligApplication.setDtCreated(appValueDto.getDtCreated());
		adptEligApplication.setDtLastUpdate(appValueDto.getDtLastUpdate());
		adptEligApplication.setIdLastUpdatePerson(appValueDto.getIdLastUpdatePerson());

		// no id created person in dto, not sure about business rule - fixed in
		// dto
		adptEligApplication.setIdCreatedPerson(appValueDto.getIdCreatedPerson());
		adptEligApplication.setPersonByIdPerson(person);

		sessionFactory.getCurrentSession().saveOrUpdate(adptEligApplication);

		log.debug("Exiting method insertAdptAsstEligApplication in AaeApplBackgroundDaoImpl");
		return adptEligApplication.getIdAdptEligApplication();
	}

	private Character stringToCharacter(String value) {
		if (TypeConvUtil.isNullOrEmpty(value) || TypeConvUtil.isNullOrEmpty(value.trim())) {
			return null;
		} else {
			return value.trim().charAt(ServiceConstants.Zero);
		}

	}

	/**
	 *
	 * @param idAdptEligApplication
	 * @param idEvent
	 * @param idCase
	 * @param idCreatedPerson
	 * @return void @
	 */
	@Override
	public Long insertAdptAsstAppEventLink(Long idAdptEligApplication, Long idNewEvent, Long idCase,
			Long idLastUpdatedByPerson) {
		log.debug("Entering method insertAdptAsstAppEventLink in AaeApplBackgroundDaoImpl");
		// AdptAsstAppEventLink AdptAsstAppEventLink =
		// sessionFactory.getCurrentSession().load(AdptAsstAppEventLink.class ,)
		AdptAsstAppEventLink adptAsstAppEventLink = new AdptAsstAppEventLink();
		AdptEligApplication adptEligApplication = (AdptEligApplication) sessionFactory.getCurrentSession()
				.load(AdptEligApplication.class, idAdptEligApplication);
		adptAsstAppEventLink.setAdptEligApplication(adptEligApplication);
		Event event = (Event) sessionFactory.getCurrentSession().load(Event.class, idNewEvent);
		adptAsstAppEventLink.setEvent(event);
		CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().load(CapsCase.class, idCase);
		adptAsstAppEventLink.setCapsCase(capsCase);
		adptAsstAppEventLink.setIdLastUpdatePerson(idLastUpdatedByPerson);
		adptAsstAppEventLink.setIdCreatedPerson(idLastUpdatedByPerson);
		adptAsstAppEventLink.setDtCreated(new Date());
		log.debug("Exiting method insertAdptAsstAppEventLink in AaeApplBackgroundDaoImpl");
		return adptAsstAppEventLink.getIdAdptAsstAppEventLink();
	}

	/**
	 *
	 * @param idAdptEligApplication
	 * @return Long @
	 */
	@SuppressWarnings("unchecked")
	public Long fetchLinkedADOApplEvent(Long idAdptEligApplication) {
		log.debug("Entering method fetchLinkedADOApplEvent in AaeApplBackgroundDaoImpl");
		Long idEvent = 0L;
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(fetchLinkedAdoApplicationEvent).addScalar("idEvent", StandardBasicTypes.LONG)
				.setParameter("idAdptEligApplication", idAdptEligApplication)
				.setResultTransformer(Transformers.aliasToBean(AdptAsstAppEventLinkDto.class));
		List<AdptAsstAppEventLinkDto> adptAsstAppEventLinkDtoList = sQLQuery1.list();

		for (AdptAsstAppEventLinkDto objAdptAsstAppEventLinkDto : adptAsstAppEventLinkDtoList) {
			idEvent = objAdptAsstAppEventLinkDto.getIdEvent();
		}

		if (TypeConvUtil.isNullOrEmpty(adptAsstAppEventLinkDtoList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("adptAsstAppEventLinkDtoList.data.not.found", null, Locale.US));
		}
		log.debug("Exiting method fetchLinkedADOApplEvent in AaeApplBackgroundDaoImpl");
		return idEvent;
	}

	/**
	 *
	 * @param idAdptEligApplication
	 * @return ApplicationBackgroundValueDto @
	 */
	@Override
	public ApplicationBackgroundDto selectAdptAsstEligApplication(Long idAdptEligApplication) {
		log.debug("Entering method selectAdptAsstEligApplication in AaeApplBackgroundDaoImpl");
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AdptEligApplication.class);
		criteria.add(Restrictions.eq("idAdptEligApplication", idAdptEligApplication));
		AdptEligApplication adptEligApplication = (AdptEligApplication) criteria.uniqueResult();

		ApplicationBackgroundDto applicationBackgroundDto = new ApplicationBackgroundDto();

		applicationBackgroundDto.setIdAdptEligApplication(adptEligApplication.getIdAdptEligApplication());
		applicationBackgroundDto.setDtLastUpdate(adptEligApplication.getDtLastUpdate());
		applicationBackgroundDto.setIdPerson(adptEligApplication.getIdCreatedPerson());
		applicationBackgroundDto.setNbrChildQualifyAge(adptEligApplication.getNbrChildQualifyAge().longValue());
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndMngngCvs())) {
			applicationBackgroundDto.setIndMngngCvs(adptEligApplication.getIndMngngCvs().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndLcpaMngngCvs())) {
			applicationBackgroundDto.setIndLcpaMngngCvs(adptEligApplication.getIndLcpaMngngCvs().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndOtherMngngCvs())) {
			applicationBackgroundDto.setIndOtherMngngCvs(adptEligApplication.getIndOtherMngngCvs().toString());
		}
		applicationBackgroundDto.setNmCvsAgency(adptEligApplication.getNmCvsAgency());
		applicationBackgroundDto.setTxtCvsAgencyAddr(adptEligApplication.getTxtCvsAgencyAddr());
		applicationBackgroundDto.setDtApplSubmitted(adptEligApplication.getDtPlcmtPlanned());
		applicationBackgroundDto.setDtApplSubmitted(adptEligApplication.getDtApplSubmitted());
		applicationBackgroundDto.setCdAdptParentAddrState(adptEligApplication.getCdAdptParentAddrState());
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndInternationalPlcmt())) {
			applicationBackgroundDto
					.setIndInternationalPlcmt(adptEligApplication.getIndInternationalPlcmt().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndFairHearing())) {
			applicationBackgroundDto.setIndFairHearing(adptEligApplication.getIndFairHearing().toString());
		}
		applicationBackgroundDto.setDtFairHearing(adptEligApplication.getDtFairHearing());
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndAdptHomeStudy())) {
			applicationBackgroundDto.setIndAdptHomeStudy(adptEligApplication.getIndAdptHomeStudy().toString());
		}
		applicationBackgroundDto.setDtAdptHomeStudy(adptEligApplication.getDtAdptHomeStudy());
		applicationBackgroundDto.setIndBkgCheck(adptEligApplication.getIndBkgCheck().toString());
		applicationBackgroundDto.setIndChildSix(adptEligApplication.getIndChildSix().toString());
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndChildTwoMinority())) {
			applicationBackgroundDto.setIndChildTwoMinority(adptEligApplication.getIndChildTwoMinority().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndWithSibling())) {
			applicationBackgroundDto.setIndWithSibling(adptEligApplication.getIndWithSibling().toString());
		}
		Person person = (Person) sessionFactory.getCurrentSession().load(Person.class,
				adptEligApplication.getPersonByIdQualSibPerson().getIdPerson());
		applicationBackgroundDto.setIdQualSibPerson(person.getIdPerson());
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndHandicapCond())) {
			applicationBackgroundDto.setIndHandicapCond(adptEligApplication.getIndHandicapCond().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndAdptRegExch())) {
			applicationBackgroundDto.setIndAdptRegExch(adptEligApplication.getIndAdptRegExch().toString());
		}
		applicationBackgroundDto.setTxtNmAdptRegExch(adptEligApplication.getTxtNmAdptRegExch());
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndEmotBond())) {
			applicationBackgroundDto.setIndEmotBond((adptEligApplication.getIndEmotBond().toString()));
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndCtznshpUs())) {
			applicationBackgroundDto.setIndCtznshpUs(adptEligApplication.getIndCtznshpUs().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndCtznshpPerm())) {
			applicationBackgroundDto.setIndCtznshpPerm(adptEligApplication.getIndCtznshpPerm().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndCtznshpOthQualAlien())) {
			applicationBackgroundDto.setIndCtznshpUnknown(adptEligApplication.getIndCtznshpOthQualAlien().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndCtznshpUnknown())) {
			applicationBackgroundDto.setIndCtznshpUnknown(adptEligApplication.getIndCtznshpUnknown().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndAdoptedByCtzn())) {
			applicationBackgroundDto.setIndAdoptedByCtzn(adptEligApplication.getIndAdoptedByCtzn().toString());
		}
		applicationBackgroundDto.setDtChildEnteredUs(adptEligApplication.getDtChildEnteredUs());
		applicationBackgroundDto.setTxtChildEnteredUs(adptEligApplication.getTxtChildEnteredUs());
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDocBirthVerfy())) {
			applicationBackgroundDto.setIndDocBirthVerfy(adptEligApplication.getIndDocBirthVerfy().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDocForm2250())) {
			applicationBackgroundDto.setIndDocForm2250(adptEligApplication.getIndDocForm2250().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDocMedProf())) {
			applicationBackgroundDto.setIndDocMedProf(adptEligApplication.getIndDocMedProf().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDocTermOrdr())) {
			applicationBackgroundDto.setIndDocTermOrdr(adptEligApplication.getIndDocTermOrdr().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDocForm2253ab())) {
			applicationBackgroundDto.setIndDocForm2253ab(adptEligApplication.getIndDocForm2253ab().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDocCourtOrdr())) {
			applicationBackgroundDto.setIndDocCourtOrdr(adptEligApplication.getIndDocCourtOrdr().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDocHmStudy())) {
			applicationBackgroundDto.setIndDocHmStudy(adptEligApplication.getIndDocHmStudy().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDocBirthVerfySibl())) {
			applicationBackgroundDto.setIndDocBirthVerfySibl(adptEligApplication.getIndDocBirthVerfySibl().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDocPolicyWaiver())) {
			applicationBackgroundDto.setIndDocPolicyWaiver(adptEligApplication.getIndDocPolicyWaiver().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDocFbiBgchk())) {
			applicationBackgroundDto.setIndDocFbiBgchk(adptEligApplication.getIndDocFbiBgchk().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDocTermOrdrSibl())) {
			applicationBackgroundDto.setIndDocTermOrdrSibl(adptEligApplication.getIndDocTermOrdrSibl().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDoctorLetter())) {
			applicationBackgroundDto.setIndDoctorLetter(adptEligApplication.getIndDoctorLetter().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDocSrvcLvlSh())) {
			applicationBackgroundDto.setIndDocSrvcLvlSh(adptEligApplication.getIndDocSrvcLvlSh().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDocImmigrationDocs())) {
			applicationBackgroundDto
					.setIndDocImmigrationDocs(adptEligApplication.getIndDocImmigrationDocs().toString());
		}
		applicationBackgroundDto.setCdWithdrawRsn(adptEligApplication.getCdWithdrawRsn());
		applicationBackgroundDto.setTxtWithdrawRsn(adptEligApplication.getTxtWithdrawRsn());
		applicationBackgroundDto.setIdLastUpdatePerson(adptEligApplication.getIdLastUpdatePerson());
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndSsaMedReq())) {
			applicationBackgroundDto.setIndSsaMedReq(adptEligApplication.getIndSsaMedReq().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDocSsaReq())) {
			applicationBackgroundDto.setIndDocSsaReq(adptEligApplication.getIndDocSsaReq().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDocMedProfReq())) {
			applicationBackgroundDto.setIndDocMedProfReq(adptEligApplication.getIndDocMedProfReq().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndDocFromSsa())) {
			applicationBackgroundDto.setIndDocFromSsa(adptEligApplication.getIndDocFromSsa().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndLoctAdptFam())) {
			applicationBackgroundDto.setIndLoctAdptFam(adptEligApplication.getIndLoctAdptFam().toString());
		}
		applicationBackgroundDto.setTxtLoctAdptFamEfrt(adptEligApplication.getTxtLoctAdptFamEfrt());
		if (!TypeConvUtil.isNullOrEmpty(adptEligApplication.getIndNoAdptPlcRslt())) {
			applicationBackgroundDto.setIndNoAdptPlcRslt(adptEligApplication.getIndNoAdptPlcRslt().toString());
		}
		applicationBackgroundDto.setTxtNoAdptPlcRsltRsn(adptEligApplication.getTxtNoAdptPlcRsltRsn());
		applicationBackgroundDto.setCdLegalStatStatus(adptEligApplication.getCdLegalStatStatus());
		applicationBackgroundDto.setDtParentalRtsTermAll(adptEligApplication.getDtParentalRtsTermAll());
		applicationBackgroundDto.setIdLegalStatEvent(adptEligApplication.getIdLegalStatEvent());
		applicationBackgroundDto.setIdAdptEligApplication(adptEligApplication.getIdAdptEligApplication());

		if (TypeConvUtil.isNullOrEmpty(adptEligApplication)) {
			throw new DataNotFoundException(
					messageSource.getMessage("adptEligApplication.data.not.found", null, Locale.US));
		}
		log.debug("Exiting method selectAdptAsstEligApplication in AaeApplBackgroundDaoImpl");
		return applicationBackgroundDto;
	}

	@Override
	public Long selectLatestAdptAsstEligAppl(Long idSiblingApplPerson) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(selectLatestAdptAsstEligAppl);
		query.setParameter("idPerson", idSiblingApplPerson);
		BigDecimal idAdptEligApplication = (BigDecimal) query.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(idAdptEligApplication)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		return idAdptEligApplication.longValue();
	}

	/**
	 * Method Name: fetchIdEventForAppl Method Description: Fetches the id event
	 * for the application
	 * 
	 * @param idAdptEligApplication
	 * @return Long @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long fetchIdEventForAppl(Long idAdptEligApplication) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AdptAsstAppEventLink.class);
		criteria.add(Restrictions.eq("idAdptAsstAppEventLink", idAdptEligApplication));
		List<AdptAsstAppEventLink> eventList = criteria.list();
		return eventList.get(ServiceConstants.Zero).getEvent().getIdEvent();
	}

	/**
	 * Method Name: fetchIdEventOfLatestAppl Method Description: Fetches the id
	 * event of the latest Adoption Assistance application for the stage
	 * 
	 * @param idStage
	 * @return Long @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long fetchIdEventOfLatestAppl(Long idStage) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(fetchIdEventForLatestApplicationForStage).addScalar("idStage", StandardBasicTypes.LONG)
				.setParameter("idEventStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(AaeApplAndDetermReq.class));
		List<AaeApplAndDetermReq> results = query.list();
		return results.get(ServiceConstants.Zero).getIdStage();
	}

}
