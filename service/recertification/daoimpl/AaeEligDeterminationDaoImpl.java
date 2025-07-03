package us.tx.state.dfps.service.recertification.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.AdptEligApplication;
import us.tx.state.dfps.common.domain.AdptEligDeterm;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.EligibilityDeterminationDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.recertification.dao.AaeEligDeterminationDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Performs
 * some of the database operations for Adoption Assistance Eligibility Module.
 * Oct 10, 2017- 10:33:03 AM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class AaeEligDeterminationDaoImpl implements AaeEligDeterminationDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${AaeEligDeterminationDaoImpl.selectAdptAsstEligDetermForApplsql}")
	private transient String selectAdptAsstEligDetermForApplsql;

	@Value("${AaeEligDeterminationDaoImpl.selectAdptAsstEligDetermFromEvent}")
	private transient String selectAdptAsstEligDetermFromEvent;

	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: selectAdptAsstEligDetermForAppl Method Description:This
	 * method fetches data from ADPT_ELIG_DETERM table using idAdptEligDeterm
	 * 
	 * @param idAdptEligApplication
	 * @return EligibilityDeterminationValueBeanDto @
	 */
	@Override
	public EligibilityDeterminationDto selectAdptAsstEligDetermForAppl(Long idAdptEligApplication) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(selectAdptAsstEligDetermForApplsql)
				.addScalar("idAdptEligDeterm", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idAdptEligApplication", StandardBasicTypes.LONG)
				.addScalar("nbrChildFfyAge", StandardBasicTypes.LONG)
				.addScalar("ind60MonthsCvs", StandardBasicTypes.STRING)
				.addScalar("indApplicableAge", StandardBasicTypes.STRING)
				.addScalar("indWithSibling", StandardBasicTypes.STRING)
				.addScalar("indMedDisSsi", StandardBasicTypes.STRING)
				.addScalar("indLivWminorParent", StandardBasicTypes.STRING)
				.addScalar("ind4ePriorAdo", StandardBasicTypes.STRING)
				.addScalar("indAfdcEligible", StandardBasicTypes.STRING)
				.addScalar("indSsiEligible", StandardBasicTypes.STRING)
				.addScalar("ind4eMinorParent", StandardBasicTypes.STRING)
				.addScalar("dtAdptPlcmtStart", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtAgreement", StandardBasicTypes.DATE)
				.addScalar("dtAdptAsstAgreement", StandardBasicTypes.DATE)
				.addScalar("nbrChildAgreementAge", StandardBasicTypes.LONG)
				.addScalar("indSpecialNeedOutcm", StandardBasicTypes.STRING)
				.addScalar("indCtznshpOutcm", StandardBasicTypes.STRING)
				.addScalar("indRsnbleEffortOutcm", StandardBasicTypes.STRING)
				.addScalar("indDfpsMngngCvsOutcm", StandardBasicTypes.STRING)
				.addScalar("indApplicableChildOutcm", StandardBasicTypes.STRING)
				.addScalar("indPlcmntReqMetOutcm", StandardBasicTypes.STRING)
				.addScalar("indAdptAgreementOutcm", StandardBasicTypes.STRING)
				.addScalar("indAddtIVEReqOutcm", StandardBasicTypes.STRING)
				.addScalar("indChildQualify", StandardBasicTypes.STRING)
				.addScalar("indAsstDisqualified", StandardBasicTypes.STRING)
				.addScalar("idAdoPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("idInitialDetermPerson", StandardBasicTypes.LONG)
				.addScalar("dtInitialDeterm", StandardBasicTypes.DATE)
				.addScalar("cdInitialDeterm", StandardBasicTypes.STRING)
				.addScalar("cdFinalDeterm", StandardBasicTypes.STRING)
				.addScalar("idFinalDetermPerson", StandardBasicTypes.LONG)
				.addScalar("dtFinalDeterm", StandardBasicTypes.DATE)
				.addScalar("idDetermSibPerson", StandardBasicTypes.LONG)
				.addScalar("dtAdoptionConsummated", StandardBasicTypes.DATE)
				.addScalar("nbrEligDetermOutcm", StandardBasicTypes.LONG)
				.setParameter("idAdptEligApplication", idAdptEligApplication)
				.setResultTransformer(Transformers.aliasToBean(EligibilityDeterminationDto.class));

		List<EligibilityDeterminationDto> eligibilityDeterminationValueBeanDto = new ArrayList<EligibilityDeterminationDto>();
		eligibilityDeterminationValueBeanDto = sqlQuery.list();
		if (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationValueBeanDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("common.eligibilityDeterminationValueBeanDto.mandatory", null, Locale.US));
		}

		return eligibilityDeterminationValueBeanDto.get(ServiceConstants.Zero);
	}

	@Override
	public Long updateAdptAsstEligDeterm(EligibilityDeterminationDto eligibilityDeterminationDto) {
		AdptEligDeterm adptEligDeterm = (AdptEligDeterm) sessionFactory.getCurrentSession().get(AdptEligDeterm.class,
				eligibilityDeterminationDto.getIdAdptEligDeterm());
		if (!TypeConvUtil.isNullOrEmpty(adptEligDeterm)) {
			adptEligDeterm.setDtLastUpdate(eligibilityDeterminationDto.getDtLastUpdate());

			if (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.getNbrChildFfyAge())) {
				adptEligDeterm.setNbrChildFfyAge(eligibilityDeterminationDto.getNbrChildFfyAge().byteValue());
			}
			adptEligDeterm.setInd60MonthsCvs(toCharcater(eligibilityDeterminationDto.getInd60MonthsCvs()));
			adptEligDeterm.setIndApplicableAge(toCharcater(eligibilityDeterminationDto.getIndApplicableAge()));
			adptEligDeterm.setIndWithSibling(toCharcater(eligibilityDeterminationDto.getIndWithSibling()));
			adptEligDeterm.setIndMedDisSsi(toCharcater(eligibilityDeterminationDto.getIndMedDisSsi()));
			adptEligDeterm.setIndLivWminorParent(toCharcater(eligibilityDeterminationDto.getIndLivWminorParent()));
			adptEligDeterm.setInd4ePriorAdo(toCharcater(eligibilityDeterminationDto.getInd4ePriorAdo()));
			adptEligDeterm.setIndAfdcEligible(toCharcater(eligibilityDeterminationDto.getIndAfdcEligible()));
			adptEligDeterm.setIndSsiEligible(toCharcater(eligibilityDeterminationDto.getIndSsiEligible()));
			adptEligDeterm.setInd4eMinorParent(toCharcater(eligibilityDeterminationDto.getInd4eMinorParent()));
			adptEligDeterm.setDtAdptPlcmtStart(eligibilityDeterminationDto.getDtAdptPlcmtStart());
			adptEligDeterm.setDtPlcmtAgreement(eligibilityDeterminationDto.getDtPlcmtAgreement());
			adptEligDeterm.setDtAdptAsstAgreement(eligibilityDeterminationDto.getDtAdptAsstAgreement());
			if (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.getNbrChildAgreementAge())) {
				adptEligDeterm
						.setNbrChildAgreementAge(eligibilityDeterminationDto.getNbrChildAgreementAge().byteValue());
			}

			adptEligDeterm.setIndSpecialNeedOutcm(toCharcater(eligibilityDeterminationDto.getIndSpecialNeedOutcm()));
			adptEligDeterm.setIndCtznshpOutcm(toCharcater(eligibilityDeterminationDto.getIndCtznshpOutcm()));
			adptEligDeterm.setIndRsnbleEffortOutcm(toCharcater(eligibilityDeterminationDto.getIndRsnbleEffortOutcm()));
			adptEligDeterm.setIndDfpsMngngCvsOutcm(toCharcater(eligibilityDeterminationDto.getIndDfpsMngngCvsOutcm()));
			adptEligDeterm
					.setIndApplicableChildOutcm(toCharcater(eligibilityDeterminationDto.getIndApplicableChildOutcm()));
			adptEligDeterm.setIndPlcmntReqMetOutcm(toCharcater(eligibilityDeterminationDto.getIndPlcmntReqMetOutcm()));
			adptEligDeterm
					.setIndAdptAgreementOutcm(toCharcater(eligibilityDeterminationDto.getIndAdptAgreementOutcm()));
			adptEligDeterm.setIndAddtIveReqOutcm(toCharcater(eligibilityDeterminationDto.getIndAddtIVEReqOutcm()));
			adptEligDeterm.setIndChildQualify(toCharcater(eligibilityDeterminationDto.getIndChildQualify()));
			adptEligDeterm.setIndAsstDisqualified(toCharcater(eligibilityDeterminationDto.getIndAsstDisqualified()));
			if (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.getIdAdoPlcmtEvent())) {
				Event event = (Event) sessionFactory.getCurrentSession().get(Event.class,
						eligibilityDeterminationDto.getIdAdoPlcmtEvent());
				if (!TypeConvUtil.isNullOrEmpty(event)) {
					adptEligDeterm.setEvent(event);
				}
			}
			if (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.getIdInitialDetermPerson())) {
				Person personByIdInitialDetermPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
						eligibilityDeterminationDto.getIdInitialDetermPerson());
				if (!TypeConvUtil.isNullOrEmpty(personByIdInitialDetermPerson)) {
					adptEligDeterm.setPersonByIdInitialDetermPerson(personByIdInitialDetermPerson);
				}
			}

			adptEligDeterm.setDtInitialDeterm(eligibilityDeterminationDto.getDtInitialDeterm());
			adptEligDeterm.setCdInitialDeterm(eligibilityDeterminationDto.getCdInitialDeterm());
			adptEligDeterm.setCdFinalDeterm(eligibilityDeterminationDto.getCdFinalDeterm());
			if (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.getIdFinalDetermPerson())) {
				Person personByIdFinalDetermPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
						eligibilityDeterminationDto.getIdFinalDetermPerson());
				if (!TypeConvUtil.isNullOrEmpty(personByIdFinalDetermPerson)) {
					adptEligDeterm.setPersonByIdFinalDetermPerson(personByIdFinalDetermPerson);
				}
			}

			adptEligDeterm.setDtFinalDeterm(eligibilityDeterminationDto.getDtFinalDeterm());
			if (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.getIdDetermSibPerson())) {
				Person personByIdDetermSibPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
						eligibilityDeterminationDto.getIdDetermSibPerson());
				if (!TypeConvUtil.isNullOrEmpty(personByIdDetermSibPerson)) {
					adptEligDeterm.setPersonByIdDetermSibPerson(personByIdDetermSibPerson);
				}
			}

			adptEligDeterm.setDtAdoptionConsummated(eligibilityDeterminationDto.getDtAdoptionConsummated());
			if (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.getNbrEligDetermOutcm())) {
				adptEligDeterm.setNbrEligDetermOutcm(eligibilityDeterminationDto.getNbrEligDetermOutcm().byteValue());
			}
			adptEligDeterm
					.setIndApplicableChildOutcm(toCharcater(eligibilityDeterminationDto.getIndApplicableChildOutcm()));
			sessionFactory.getCurrentSession().saveOrUpdate(adptEligDeterm);
			return ServiceConstants.ONE_LONG;
		}

		return ServiceConstants.ZERO_VAL;
	}

	@Override
	public EligibilityDeterminationDto selectAdptAsstEligDetermFromEvent(Long idAppEvent) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectAdptAsstEligDetermFromEvent)
				.addScalar("idAdptEligDeterm", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idAdptEligApplication", StandardBasicTypes.LONG)
				.addScalar("nbrChildFfyAge", StandardBasicTypes.LONG)
				.addScalar("ind60MonthsCvs", StandardBasicTypes.STRING)
				.addScalar("indApplicableAge", StandardBasicTypes.STRING)
				.addScalar("indWithSibling", StandardBasicTypes.STRING)
				.addScalar("indMedDisSsi", StandardBasicTypes.STRING)
				.addScalar("indLivWminorParent", StandardBasicTypes.STRING)
				.addScalar("ind4ePriorAdo", StandardBasicTypes.STRING)
				.addScalar("indAfdcEligible", StandardBasicTypes.STRING)
				.addScalar("indSsiEligible", StandardBasicTypes.STRING)
				.addScalar("ind4eMinorParent", StandardBasicTypes.STRING)
				.addScalar("dtAdptPlcmtStart", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtAgreement", StandardBasicTypes.DATE)
				.addScalar("dtAdptAsstAgreement", StandardBasicTypes.DATE)
				.addScalar("nbrChildAgreementAge", StandardBasicTypes.LONG)
				.addScalar("indSpecialNeedOutcm", StandardBasicTypes.STRING)
				.addScalar("indCtznshpOutcm", StandardBasicTypes.STRING)
				.addScalar("indRsnbleEffortOutcm", StandardBasicTypes.STRING)
				.addScalar("indDfpsMngngCvsOutcm", StandardBasicTypes.STRING)
				.addScalar("indApplicableChildOutcm", StandardBasicTypes.STRING)
				.addScalar("indPlcmntReqMetOutcm", StandardBasicTypes.STRING)
				.addScalar("indAdptAgreementOutcm", StandardBasicTypes.STRING)
				.addScalar("indAddtIVEReqOutcm", StandardBasicTypes.STRING)
				.addScalar("indChildQualify", StandardBasicTypes.STRING)
				.addScalar("indAsstDisqualified", StandardBasicTypes.STRING)
				.addScalar("idAdoPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("idInitialDetermPerson", StandardBasicTypes.LONG)
				.addScalar("dtInitialDeterm", StandardBasicTypes.DATE)
				.addScalar("cdInitialDeterm", StandardBasicTypes.STRING)
				.addScalar("cdFinalDeterm", StandardBasicTypes.STRING)
				.addScalar("idFinalDetermPerson", StandardBasicTypes.LONG)
				.addScalar("dtFinalDeterm", StandardBasicTypes.DATE)
				.addScalar("idDetermSibPerson", StandardBasicTypes.LONG)
				.addScalar("dtAdoptionConsummated", StandardBasicTypes.DATE)
				.addScalar("nbrEligDetermOutcm", StandardBasicTypes.LONG).setParameter("idEvent", idAppEvent)
				.setResultTransformer(Transformers.aliasToBean(EligibilityDeterminationDto.class));

		List<EligibilityDeterminationDto> determinationDtoList = query.list();
		if (TypeConvUtil.isNullOrEmpty(determinationDtoList)) {

			throw new DataNotFoundException(
					messageSource.getMessage("eligibilityDeterminationDBDtoList.notFound", null, Locale.US));

		}

		return determinationDtoList.get(ServiceConstants.Zero);
	}

	@Override
	public Long insertAdptAsstEligDeterm(EligibilityDeterminationDto eligibilityDeterminationDto) {
		AdptEligDeterm adptEligDeterm = new AdptEligDeterm();
		adptEligDeterm.setDtLastUpdate(eligibilityDeterminationDto.getDtLastUpdate());
		if (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.getIdAdptEligApplication())) {
			AdptEligApplication adptEligApplication = (AdptEligApplication) sessionFactory.getCurrentSession()
					.get(AdptEligApplication.class, eligibilityDeterminationDto.getIdAdptEligApplication());
			if (!TypeConvUtil.isNullOrEmpty(adptEligApplication)) {
				adptEligDeterm.setAdptEligApplication(adptEligApplication);
			}

		}
		if (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.getNbrChildFfyAge())) {
			adptEligDeterm.setNbrChildFfyAge(eligibilityDeterminationDto.getNbrChildFfyAge().byteValue());
		}
		adptEligDeterm.setInd60MonthsCvs(toCharcater(eligibilityDeterminationDto.getInd60MonthsCvs()));
		adptEligDeterm.setIndApplicableAge(toCharcater(eligibilityDeterminationDto.getIndApplicableAge()));
		adptEligDeterm.setIndWithSibling(toCharcater(eligibilityDeterminationDto.getIndWithSibling()));
		adptEligDeterm.setIndMedDisSsi(toCharcater(eligibilityDeterminationDto.getIndMedDisSsi()));
		adptEligDeterm.setIndLivWminorParent(toCharcater(eligibilityDeterminationDto.getIndLivWminorParent()));
		adptEligDeterm.setInd4ePriorAdo(toCharcater(eligibilityDeterminationDto.getInd4ePriorAdo()));
		adptEligDeterm.setIndAfdcEligible(toCharcater(eligibilityDeterminationDto.getIndAfdcEligible()));
		adptEligDeterm.setIndSsiEligible(toCharcater(eligibilityDeterminationDto.getIndSsiEligible()));
		adptEligDeterm.setInd4eMinorParent(toCharcater(eligibilityDeterminationDto.getInd4eMinorParent()));
		adptEligDeterm.setDtAdptPlcmtStart(eligibilityDeterminationDto.getDtAdptPlcmtStart());
		adptEligDeterm.setDtPlcmtAgreement(eligibilityDeterminationDto.getDtPlcmtAgreement());
		adptEligDeterm.setDtAdptAsstAgreement(eligibilityDeterminationDto.getDtAdptAsstAgreement());
		if (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.getNbrChildAgreementAge())) {
			adptEligDeterm.setNbrChildAgreementAge(eligibilityDeterminationDto.getNbrChildAgreementAge().byteValue());
		}

		adptEligDeterm.setIndSpecialNeedOutcm(toCharcater(eligibilityDeterminationDto.getIndSpecialNeedOutcm()));
		adptEligDeterm.setIndCtznshpOutcm(toCharcater(eligibilityDeterminationDto.getIndCtznshpOutcm()));
		adptEligDeterm.setIndRsnbleEffortOutcm(toCharcater(eligibilityDeterminationDto.getIndRsnbleEffortOutcm()));
		adptEligDeterm.setIndDfpsMngngCvsOutcm(toCharcater(eligibilityDeterminationDto.getIndDfpsMngngCvsOutcm()));
		adptEligDeterm
				.setIndApplicableChildOutcm(toCharcater(eligibilityDeterminationDto.getIndApplicableChildOutcm()));
		adptEligDeterm.setIndPlcmntReqMetOutcm(toCharcater(eligibilityDeterminationDto.getIndPlcmntReqMetOutcm()));
		adptEligDeterm.setIndAdptAgreementOutcm(toCharcater(eligibilityDeterminationDto.getIndAdptAgreementOutcm()));
		adptEligDeterm.setIndAddtIveReqOutcm(toCharcater(eligibilityDeterminationDto.getIndAddtIVEReqOutcm()));
		adptEligDeterm.setIndChildQualify(toCharcater(eligibilityDeterminationDto.getIndChildQualify()));
		adptEligDeterm.setIndAsstDisqualified(toCharcater(eligibilityDeterminationDto.getIndAsstDisqualified()));
		if (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.getIdAdoPlcmtEvent())) {
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class,
					eligibilityDeterminationDto.getIdAdoPlcmtEvent());
			if (!TypeConvUtil.isNullOrEmpty(event)) {
				adptEligDeterm.setEvent(event);
			}
		}
		if (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.getIdInitialDetermPerson())) {
			Person personByIdInitialDetermPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
					eligibilityDeterminationDto.getIdInitialDetermPerson());
			if (!TypeConvUtil.isNullOrEmpty(personByIdInitialDetermPerson)) {
				adptEligDeterm.setPersonByIdInitialDetermPerson(personByIdInitialDetermPerson);
			}
		}

		adptEligDeterm.setDtInitialDeterm(eligibilityDeterminationDto.getDtInitialDeterm());
		adptEligDeterm.setCdInitialDeterm(eligibilityDeterminationDto.getCdInitialDeterm());
		adptEligDeterm.setCdFinalDeterm(eligibilityDeterminationDto.getCdFinalDeterm());
		if (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.getIdFinalDetermPerson())) {
			Person personByIdFinalDetermPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
					eligibilityDeterminationDto.getIdFinalDetermPerson());
			if (!TypeConvUtil.isNullOrEmpty(personByIdFinalDetermPerson)) {
				adptEligDeterm.setPersonByIdFinalDetermPerson(personByIdFinalDetermPerson);
			}
		}

		adptEligDeterm.setDtFinalDeterm(eligibilityDeterminationDto.getDtFinalDeterm());
		adptEligDeterm.setIdCreatedPerson(eligibilityDeterminationDto.getIdLastUpdatePerson());
		adptEligDeterm.setDtCreated(new Date());
		adptEligDeterm.setIdLastUpdatePerson(eligibilityDeterminationDto.getIdLastUpdatePerson());
		if (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.getIdDetermSibPerson())) {
			Person personByIdDetermSibPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
					eligibilityDeterminationDto.getIdDetermSibPerson());
			if (!TypeConvUtil.isNullOrEmpty(personByIdDetermSibPerson)) {
				adptEligDeterm.setPersonByIdDetermSibPerson(personByIdDetermSibPerson);
			}
		}

		adptEligDeterm.setDtAdoptionConsummated(eligibilityDeterminationDto.getDtAdoptionConsummated());
		if (!TypeConvUtil.isNullOrEmpty(eligibilityDeterminationDto.getNbrEligDetermOutcm())) {
			adptEligDeterm.setNbrEligDetermOutcm(eligibilityDeterminationDto.getNbrEligDetermOutcm().byteValue());
		}

		return (Long) sessionFactory.getCurrentSession().save(adptEligDeterm);
	}

	private Character toCharcater(String value) {
		if (TypeConvUtil.isNullOrEmpty(value) || TypeConvUtil.isNullOrEmpty(value.trim())) {
			return null;
		}
		return value.trim().charAt(ServiceConstants.Zero);
	}

}
