package us.tx.state.dfps.service.pcaeligdetermination.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.PcaEligApplication;
import us.tx.state.dfps.common.domain.PcaEligDeterm;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.pca.dto.PcaEligDeterminationDto;
import us.tx.state.dfps.service.pcaeligdetermination.dao.PcaEligDeterminationDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PcaEligDeterminationDaoImpl Oct 13, 2017- 3:51:02 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class PcaEligDeterminationDaoImpl implements PcaEligDeterminationDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private MessageSource messageSource;

	@Value("${PcaEligDeterminationDaoImpl.selectPcaEligDeterminationFromEvent}")
	private transient String selectPcaEligDeterminationFromEventSql;

	/**
	 * Method Name: selectPcaEligDeterminationFromEvent Method Description:This
	 * method fetches data from Eligibility Determination table using idEvent
	 * 
	 * @param idAppEvent
	 * @return PcaEligDeterminationDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PcaEligDeterminationDto selectPcaEligDeterminationFromEvent(Long idAppEvent) {
		PcaEligDeterminationDto pcaEligDeterminationDto = new PcaEligDeterminationDto();
		List<PcaEligDeterminationDto> pcaEligDeterminationDtoList = new ArrayList<PcaEligDeterminationDto>();
		pcaEligDeterminationDtoList = (List<PcaEligDeterminationDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(selectPcaEligDeterminationFromEventSql)
				.addScalar("idPcaEligDeterm", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPcaEligApplication", StandardBasicTypes.LONG)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
				.addScalar("dtPcaAgreement", StandardBasicTypes.DATE)
				.addScalar("idInitialDetermPerson", StandardBasicTypes.LONG)
				.addScalar("dtInitialDeterm", StandardBasicTypes.DATE)
				.addScalar("cdInitialDeterm", StandardBasicTypes.STRING)
				.addScalar("cdFinalDeterm", StandardBasicTypes.STRING)
				.addScalar("idFinalDetermPerson", StandardBasicTypes.LONG)
				.addScalar("dtFinalDeterm", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtPmcLegalStatus", StandardBasicTypes.DATE)
				.addScalar("indChildQualify", StandardBasicTypes.STRING)
				.addScalar("indPlcmtReqMetOutcome", StandardBasicTypes.STRING)
				.addScalar("indPcaReqMetOutcome", StandardBasicTypes.STRING)
				.addScalar("indCtznshpOutcome", StandardBasicTypes.STRING)
				.addScalar("indAsstDisqualified", StandardBasicTypes.STRING).setParameter("idEvent", idAppEvent)
				.setResultTransformer(Transformers.aliasToBean(PcaEligDeterminationDto.class)).list();
		for (PcaEligDeterminationDto pcaEligDeterminationValueBeanDto : pcaEligDeterminationDtoList) {
			pcaEligDeterminationDto = pcaEligDeterminationValueBeanDto;
		}
		if (TypeConvUtil.isNullOrEmpty(pcaEligDeterminationDtoList)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}

		return pcaEligDeterminationDto;
	}

	/**
	 * Method Name: updateEligibilityDetermination Method Description:This
	 * method updates PCA_ELIG_DETERM table using PcaEligDeterminationDto.
	 * 
	 * @param pcaEligDeterminationDto
	 * @return Long @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long updateEligibilityDetermination(PcaEligDeterminationDto pcaEligDeterminationDto) {
		Long updatedResult = 0l;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PcaEligDeterm.class);
		criteria.add(Restrictions.eq("idPcaEligDeterm", pcaEligDeterminationDto.getIdPcaEligDeterm()));
		List<PcaEligDeterm> pcaEligDeterms = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(pcaEligDeterms)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		for (PcaEligDeterm pcaEligDeterm : pcaEligDeterms) {
			pcaEligDeterm.getEvent().setIdEvent(pcaEligDeterminationDto.getIdPlcmtEvent());
			pcaEligDeterm.setDtPlcmtStart(pcaEligDeterminationDto.getDtPlcmtStart());
			pcaEligDeterm.setDtPcaAgreement(pcaEligDeterminationDto.getDtPcaAgreement());
			Person person = new Person();
			person.setIdPerson(pcaEligDeterminationDto.getIdInitialDetermPerson());
			pcaEligDeterm.setPersonByIdInitialDetermPerson(person);
			pcaEligDeterm.setDtInitialDeterm(pcaEligDeterminationDto.getDtFinalDeterm());
			pcaEligDeterm.setCdInitialDeterm(pcaEligDeterminationDto.getCdInitialDeterm());
			pcaEligDeterm.setCdFinalDeterm(pcaEligDeterminationDto.getCdFinalDeterm());
			Person person1 = new Person();
			person1.setIdPerson(pcaEligDeterminationDto.getIdInitialDetermPerson());
			pcaEligDeterm.setPersonByIdFinalDetermPerson(person1);
			pcaEligDeterm.setDtFinalDeterm(pcaEligDeterminationDto.getDtFinalDeterm());
			pcaEligDeterm.setIdCreatedPerson(pcaEligDeterminationDto.getIdCreatedPerson());
			pcaEligDeterm.setIdLastUpdatePerson(pcaEligDeterminationDto.getIdLastUpdatePerson());
			pcaEligDeterm.setDtPmcLegalStatus(pcaEligDeterminationDto.getDtPmcLegalStatus());
			pcaEligDeterm.setIndChildQualify(pcaEligDeterminationDto.getIndChildQualify().trim().charAt(0));
			pcaEligDeterm.setIndPlcmtReqMetOutcome(pcaEligDeterminationDto.getIndPlcmtReqMetOutcome().trim().charAt(0));
			pcaEligDeterm.setIndPcaReqMetOutcome(pcaEligDeterminationDto.getIndCtznshpOutcome().trim().charAt(0));
			pcaEligDeterm.setIndCtznshpOutcome(pcaEligDeterminationDto.getIndCtznshpOutcome().trim().charAt(0));
			pcaEligDeterm.setIndAsstDisqualified(pcaEligDeterminationDto.getIndAsstDisqualified().trim().charAt(0));
			sessionFactory.getCurrentSession().saveOrUpdate(pcaEligDeterm);
			updatedResult++;

		}

		return updatedResult;
	}

	/**
	 * Method Name: insertPcaEligDetermination Method Description:This method
	 * inserts new record into PCA_ELIG_DETERM table.
	 * 
	 * @param pcaEligDeterminationDto
	 * @return Long @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long insertPcaEligDetermination(PcaEligDeterminationDto pcaEligDeterminationDto) {

		PcaEligDeterm pcaEligDeterm = new PcaEligDeterm();
		pcaEligDeterm.setDtCreated(new Date());
		pcaEligDeterm.setDtLastUpdate(new Date());
		pcaEligDeterm.setEvent(
				(Event) sessionFactory.getCurrentSession().get(Event.class, pcaEligDeterminationDto.getIdPlcmtEvent()));
		pcaEligDeterm.setDtPlcmtStart(pcaEligDeterminationDto.getDtPlcmtStart());
		pcaEligDeterm.setDtPcaAgreement(pcaEligDeterminationDto.getDtPcaAgreement());
		if (!TypeConvUtil.isNullOrEmpty(pcaEligDeterminationDto.getIdInitialDetermPerson())) {
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
					pcaEligDeterminationDto.getIdInitialDetermPerson());
			pcaEligDeterm.setPersonByIdInitialDetermPerson(person);
		}
		if (!TypeConvUtil.isNullOrEmpty(pcaEligDeterminationDto.getIdFinalDetermPerson())) {
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
					pcaEligDeterminationDto.getIdFinalDetermPerson());
			pcaEligDeterm.setPersonByIdFinalDetermPerson(person);
		}
		pcaEligDeterm.setDtInitialDeterm(pcaEligDeterminationDto.getDtFinalDeterm());
		pcaEligDeterm.setCdInitialDeterm(pcaEligDeterminationDto.getCdInitialDeterm());
		pcaEligDeterm.setCdFinalDeterm(pcaEligDeterminationDto.getCdFinalDeterm());
		pcaEligDeterm.setDtFinalDeterm(pcaEligDeterminationDto.getDtFinalDeterm());
		pcaEligDeterm.setIdCreatedPerson(pcaEligDeterminationDto.getIdCreatedPerson());
		pcaEligDeterm.setIdLastUpdatePerson(pcaEligDeterminationDto.getIdLastUpdatePerson());
		pcaEligDeterm.setDtPmcLegalStatus(pcaEligDeterminationDto.getDtPmcLegalStatus());
		if (!TypeConvUtil.isNullOrEmpty(pcaEligDeterminationDto.getIndChildQualify())) {
			pcaEligDeterm.setIndChildQualify(pcaEligDeterminationDto.getIndChildQualify().trim().charAt(0));
		}
		if (!TypeConvUtil.isNullOrEmpty(pcaEligDeterminationDto.getIndPlcmtReqMetOutcome())) {

			pcaEligDeterm.setIndPlcmtReqMetOutcome(pcaEligDeterminationDto.getIndPlcmtReqMetOutcome().trim().charAt(0));
		}
		if (!TypeConvUtil.isNullOrEmpty(pcaEligDeterminationDto.getIndPcaReqMetOutcome())) {
			pcaEligDeterm.setIndPcaReqMetOutcome(pcaEligDeterminationDto.getIndPcaReqMetOutcome().trim().charAt(0));
		}
		if (!TypeConvUtil.isNullOrEmpty(pcaEligDeterminationDto.getIndCtznshpOutcome())) {
			pcaEligDeterm.setIndCtznshpOutcome(pcaEligDeterminationDto.getIndCtznshpOutcome().trim().charAt(0));
		}
		if (!TypeConvUtil.isNullOrEmpty(pcaEligDeterminationDto.getIndAsstDisqualified())) {
			pcaEligDeterm.setIndAsstDisqualified(pcaEligDeterminationDto.getIndAsstDisqualified().trim().charAt(0));
		}

		PcaEligApplication pcaEligApplication = (PcaEligApplication) sessionFactory.getCurrentSession()
				.get(PcaEligApplication.class, pcaEligDeterminationDto.getIdPcaEligApplication());
		pcaEligDeterm.setPcaEligApplication(pcaEligApplication);
		sessionFactory.getCurrentSession().save(pcaEligDeterm);

		return pcaEligDeterm.getIdPcaEligDeterm();
	}

	/**
	 * Method Name: selectPcaEligDetermination Method Description:This method
	 * fetches data from Eligibility Determination table using idPcaEligDeterm
	 * 
	 * @param idPcaEligDeterm
	 * @return PcaEligDeterminationDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PcaEligDeterminationDto selectPcaEligDetermination(Long idPcaEligDeterm) {
		PcaEligDeterminationDto determDto = new PcaEligDeterminationDto();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PcaEligDeterm.class);
		criteria.add(Restrictions.eq("idPcaEligDeterm", idPcaEligDeterm));
		List<PcaEligDeterm> pcaEligDeterms = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(pcaEligDeterms)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		for (PcaEligDeterm pcaEligDeterm : pcaEligDeterms) {
			determDto.setIdPcaEligDeterm(idPcaEligDeterm);
			determDto.setDtLastUpdate(pcaEligDeterm.getDtLastUpdate());
			determDto.setIdPcaEligApplication(pcaEligDeterm.getPcaEligApplication().getIdPcaEligApplication());
			determDto.setIdPlcmtEvent(pcaEligDeterm.getEvent().getIdEvent());
			determDto.setDtPlcmtStart(pcaEligDeterm.getDtPlcmtStart());
			determDto.setDtPcaAgreement(pcaEligDeterm.getDtPcaAgreement());
			determDto.setIdInitialDetermPerson(pcaEligDeterm.getPersonByIdInitialDetermPerson().getIdPerson());
			determDto.setDtInitialDeterm(pcaEligDeterm.getDtInitialDeterm());
			determDto.setCdInitialDeterm(pcaEligDeterm.getCdInitialDeterm());
			determDto.setCdFinalDeterm(pcaEligDeterm.getCdFinalDeterm());
			determDto.setIdFinalDetermPerson(pcaEligDeterm.getPersonByIdFinalDetermPerson().getIdPerson());
			determDto.setDtFinalDeterm(pcaEligDeterm.getDtFinalDeterm());
			determDto.setIdCreatedPerson(pcaEligDeterm.getIdCreatedPerson());
			determDto.setDtCreated(pcaEligDeterm.getDtCreated());
			determDto.setIdLastUpdatePerson(pcaEligDeterm.getIdLastUpdatePerson());
			determDto.setDtPmcLegalStatus(pcaEligDeterm.getDtPmcLegalStatus());
			determDto.setIndChildQualify(pcaEligDeterm.getIndChildQualify().toString());
			determDto.setIndPlcmtReqMetOutcome(pcaEligDeterm.getIndPlcmtReqMetOutcome().toString());
			determDto.setIndPcaReqMetOutcome(pcaEligDeterm.getIndPcaReqMetOutcome().toString());
			determDto.setIndCtznshpOutcome(pcaEligDeterm.getIndCtznshpOutcome().toString());
			determDto.setIndAsstDisqualified(pcaEligDeterm.getIndAsstDisqualified().toString());

		}
		return determDto;
	}

	/**
	 * Method Name: selectEligFromIdPcaApp Method Description:This method
	 * fetches data from PCA_ELIG_DETERM table using idPcaEligApplication
	 * 
	 * @param idPcaEligApplication
	 * @return PcaEligDeterminationDto @
	 */
	@Override
	public PcaEligDeterminationDto selectEligFromIdPcaApp(Long idPcaEligApplication) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PcaEligDeterm.class);
		criteria.add(Restrictions.eq("pcaEligApplication.idPcaEligApplication", idPcaEligApplication));
		PcaEligDeterm pcaEligDeterm = (PcaEligDeterm) criteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(pcaEligDeterm)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		PcaEligDeterminationDto pcaEligDeterminationDto = new PcaEligDeterminationDto();
		pcaEligDeterminationDto.setIdPcaEligDeterm(pcaEligDeterm.getIdPcaEligDeterm());
		pcaEligDeterminationDto.setDtLastUpdate(pcaEligDeterm.getDtLastUpdate());
		pcaEligDeterminationDto
				.setIdPcaEligApplication(pcaEligDeterm.getPcaEligApplication().getIdPcaEligApplication());
		pcaEligDeterminationDto.setIdPlcmtEvent(pcaEligDeterm.getEvent().getIdEvent());
		pcaEligDeterminationDto.setDtPlcmtStart(pcaEligDeterm.getDtPlcmtStart());
		pcaEligDeterminationDto.setDtPcaAgreement(pcaEligDeterm.getDtPcaAgreement());
		pcaEligDeterminationDto
				.setIdInitialDetermPerson(pcaEligDeterm.getPersonByIdInitialDetermPerson().getIdPerson());
		pcaEligDeterminationDto.setDtInitialDeterm(pcaEligDeterm.getDtInitialDeterm());
		pcaEligDeterminationDto.setCdInitialDeterm(pcaEligDeterm.getCdInitialDeterm());
		pcaEligDeterminationDto.setCdFinalDeterm(pcaEligDeterm.getCdFinalDeterm());
		pcaEligDeterminationDto.setIdFinalDetermPerson(pcaEligDeterm.getPersonByIdFinalDetermPerson().getIdPerson());
		pcaEligDeterminationDto.setDtFinalDeterm(pcaEligDeterm.getDtFinalDeterm());
		pcaEligDeterminationDto.setIdCreatedPerson(pcaEligDeterm.getIdCreatedPerson());
		pcaEligDeterminationDto.setDtCreated(pcaEligDeterm.getDtCreated());
		pcaEligDeterminationDto.setIdLastUpdatePerson(pcaEligDeterm.getIdLastUpdatePerson());
		pcaEligDeterminationDto.setDtPmcLegalStatus(pcaEligDeterm.getDtPmcLegalStatus());
		pcaEligDeterminationDto.setIndChildQualify(pcaEligDeterm.getIndChildQualify().toString());
		pcaEligDeterminationDto.setIndPlcmtReqMetOutcome(pcaEligDeterm.getIndPlcmtReqMetOutcome().toString());
		pcaEligDeterminationDto.setIndPcaReqMetOutcome(pcaEligDeterm.getIndPcaReqMetOutcome().toString());
		pcaEligDeterminationDto.setIndCtznshpOutcome(pcaEligDeterm.getIndCtznshpOutcome().toString());
		pcaEligDeterminationDto.setIndAsstDisqualified(pcaEligDeterm.getIndAsstDisqualified().toString());

		return pcaEligDeterminationDto;
	}
}
