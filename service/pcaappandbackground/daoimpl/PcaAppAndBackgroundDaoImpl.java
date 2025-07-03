package us.tx.state.dfps.service.pcaappandbackground.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Eligibility;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.FceEligibility;
import us.tx.state.dfps.common.domain.PcaAppEventLink;
import us.tx.state.dfps.common.domain.PcaEligApplication;
import us.tx.state.dfps.common.domain.PcaEligRecert;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PcaAppAndBackgroundReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.pca.dto.PcaAppAndBackgroundDto;
import us.tx.state.dfps.service.pca.dto.PcaEligDeterminationDto;
import us.tx.state.dfps.service.pcaappandbackground.dao.PcaAppAndBackgroundDao;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-ejb-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * class will fetch,save and submit PCA details into respective database details
 * 
 * Nov 14, 2017- 2:31:19 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class PcaAppAndBackgroundDaoImpl implements PcaAppAndBackgroundDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${PcaAppAndBackgroundDaoImpl.fetchPcaAppEvents}")
	private String fetchPcaAppEventsSql;

	@Value("${PcaAppAndBackgroundDaoImpl.selectLatestAppForStage}")
	private String selectLatestApplForStageSql;

	@Value("${PcaAppAndBackgroundDaoImpl.selectLatestApplication}")
	private String selectLatestApplicationSql;

	@Value("${PcaAppAndBackgroundDaoImpl.selectPcaEligAppUsingIdEvent}")
	private String selectPcaEligAppUsingIdEvent;

	@Value("${PcaAppAndBackgroundDaoImpl.findLinkedSubStage}")
	private String findLinkedSubStage;

	@Value("${PcaAppAndBackgroundDaoImpl.selectEligFromIdPcaApp}")
	private String selectEligFromIdPcaApp;

	@Value("${PcaAppAndBackgroundDaoImpl.fetchPriorPlcmts}")
	private String fetchPriorPlcmts;

	@Value("${PcaAppAndBackgroundDaoImpl.selectPcaEligApp}")
	private String selectPcaEligApp;

	@Value("${PcaAppAndBackgroundDaoImpl.selectPrevApplication}")
	private String selectPrevApplication;

	private static final Logger log = Logger.getLogger(PcaAppAndBackgroundDaoImpl.class);

	/**
	 * Method Name: selectPcaEligAppFromEvent Method Description:This method
	 * fetches data from PCA_ELIG_APPLICATION table using idPcaEligApplication
	 * 
	 * @param idAppEvent
	 * @return PcaAppAndBackgroundDto
	 */
	@Override
	public PcaAppAndBackgroundDto selectPcaEligAppFromEvent(Long idAppEvent) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(selectPcaEligAppUsingIdEvent);
		query.setParameter("idEvent", idAppEvent);
		query.addScalar("idPcaEligApplication", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("nbrChildQualifyAge", StandardBasicTypes.LONG)
				.addScalar("cdChildPermGoal", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("indEligFcmp6mthsRel", StandardBasicTypes.STRING)
				.addScalar("indWithSibling", StandardBasicTypes.STRING)
				.addScalar("indHmRemJudicial", StandardBasicTypes.STRING)
				.addScalar("indNoReturnHome", StandardBasicTypes.STRING)
				.addScalar("indRelAttachment", StandardBasicTypes.STRING)
				.addScalar("indChildConsulted", StandardBasicTypes.STRING)
				.addScalar("indFairHearing", StandardBasicTypes.STRING)
				.addScalar("dtFairHearing", StandardBasicTypes.DATE)
				.addScalar("indCtznshpUs", StandardBasicTypes.STRING)
				.addScalar("indCtznshpPerm", StandardBasicTypes.STRING)
				.addScalar("indCtznshpOthQualAlien", StandardBasicTypes.STRING)
				.addScalar("indCtznshpUnknown", StandardBasicTypes.STRING)
				.addScalar("dtChildEnteredUs", StandardBasicTypes.DATE)
				.addScalar("txtChildEnteredUs", StandardBasicTypes.STRING)
				.addScalar("dtApplSubmitted", StandardBasicTypes.DATE)
				.addScalar("indDocBirthVerfy", StandardBasicTypes.STRING)
				.addScalar("indDocSrvcLvlSh", StandardBasicTypes.STRING)
				.addScalar("indDocCourtOrdrSibl", StandardBasicTypes.STRING)
				.addScalar("indDocCourtOrdrMngCnv", StandardBasicTypes.STRING)
				.addScalar("indDocForm2115", StandardBasicTypes.STRING)
				.addScalar("indDocCourtOrdr", StandardBasicTypes.STRING)
				.addScalar("indDocHmStudy", StandardBasicTypes.STRING)
				.addScalar("indDocForm2116", StandardBasicTypes.STRING)
				.addScalar("indDocForm2118", StandardBasicTypes.STRING)
				.addScalar("indDocOther", StandardBasicTypes.STRING)
				.addScalar("indDocFbiBgchk", StandardBasicTypes.STRING)
				.addScalar("indDocBirthVerfySibl", StandardBasicTypes.STRING)
				.addScalar("cdWithdrawRsn", StandardBasicTypes.STRING)
				.addScalar("withdrawRsn", StandardBasicTypes.STRING)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idQualSibPerson", StandardBasicTypes.LONG)
				.addScalar("indEligFcmp6mthsFicKin", StandardBasicTypes.STRING)
				.addScalar("idFceEligibility", StandardBasicTypes.LONG)
				.addScalar("indFceRmvlChildOrdered", StandardBasicTypes.STRING)
				.addScalar("idEligEvent", StandardBasicTypes.LONG)
				.addScalar("cdFceEligActual", StandardBasicTypes.STRING)
				.addScalar("indChildSibling1", StandardBasicTypes.STRING)
				.addScalar("indEligFcmpU6mthsRel", StandardBasicTypes.STRING)
				.addScalar("indEligFcmpU6mthsFicKin", StandardBasicTypes.STRING);

		List<PcaAppAndBackgroundDto> pcaAppAndBackgroundDtos = query
				.setResultTransformer(Transformers.aliasToBean(PcaAppAndBackgroundDto.class)).list();
		PcaAppAndBackgroundDto pcaAppAndBackgroundDto = new PcaAppAndBackgroundDto();
		if (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundDtos)) {
			throw new DataNotFoundException(
					messageSource.getMessage("pcaAppAndBackground.pcaAppAndBackgroundDtos.empty", null, Locale.US));
		} else {
			pcaAppAndBackgroundDto = pcaAppAndBackgroundDtos.get(0);
		}
		return pcaAppAndBackgroundDto;
	}

	/**
	 * Method Name: fetchPcaAppEvents Method Description:
	 * 
	 * @param idPcaEligApplication
	 * @return List<EventValueDto>
	 */
	@Override
	public List<EventValueDto> fetchPcaAppEvents(Long idPcaEligApplication) {

		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchPcaAppEventsSql)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("cdEventType", StandardBasicTypes.STRING).addScalar("eventDescr", StandardBasicTypes.STRING)
				.addScalar("cdEventTask", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
				.addScalar("dtEventModified", StandardBasicTypes.DATE)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.setParameter("idPcaEligApplication", idPcaEligApplication)
				.setResultTransformer(Transformers.aliasToBean(EventValueDto.class));

		List<EventValueDto> eventValueDtoList = sqlQuery.list();

		return eventValueDtoList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.pcaappandbackground.dao.PcaAppAndBackgroundDao#
	 * insertPcaEligApplication(us.tx.state.dfps.service.pca.dto.
	 * PcaAppAndBackgroundDto)
	 */
	@Override
	public Long insertPcaEligApplication(PcaAppAndBackgroundDto pcaAppAndBackgroundDto) {
		PcaEligApplication pcaEligApplication = new PcaEligApplication();
		if (pcaAppAndBackgroundDto.getNbrChildQualifyAge() != null) {
			pcaEligApplication.setNbrChildQualifyAge(pcaAppAndBackgroundDto.getNbrChildQualifyAge().shortValue());
		}
		pcaEligApplication.setCdChildPermGoal(pcaAppAndBackgroundDto.getCdChildPermGoal());

		setPerson(pcaAppAndBackgroundDto, pcaEligApplication);

		setEvent(pcaAppAndBackgroundDto, pcaEligApplication);

		setFceEligibility(pcaAppAndBackgroundDto, pcaEligApplication);

		setEligibility(pcaAppAndBackgroundDto, pcaEligApplication);

		setQualSibPerson(pcaAppAndBackgroundDto, pcaEligApplication);

		if (pcaAppAndBackgroundDto.getIndEligFcmp6mthsRel() != null) {
			pcaEligApplication.setIndEligFcmp6mthsRel(pcaAppAndBackgroundDto.getIndEligFcmp6mthsRel().charAt(0));
		}
		if (pcaAppAndBackgroundDto.getIndWithSibling() != null) {
			pcaEligApplication.setIndWithSibling(pcaAppAndBackgroundDto.getIndWithSibling().charAt(0));
		}
		if (pcaAppAndBackgroundDto.getIndHmRemJudicial() != null) {
			pcaEligApplication.setIndHmRemJudicial(pcaAppAndBackgroundDto.getIndHmRemJudicial().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndNoReturnHome() != null) {
			pcaEligApplication.setIndNoReturnHome(pcaAppAndBackgroundDto.getIndNoReturnHome().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndRelAttachment() != null) {
			pcaEligApplication.setIndRelAttachment(pcaAppAndBackgroundDto.getIndRelAttachment().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndChildConsulted() != null) {
			pcaEligApplication.setIndChildConsulted(pcaAppAndBackgroundDto.getIndChildConsulted().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndFairHearing() != null) {
			pcaEligApplication.setIndFairHearing(pcaAppAndBackgroundDto.getIndFairHearing().charAt(0));
		}

		pcaEligApplication.setDtFairHearing(pcaAppAndBackgroundDto.getDtFairHearing());

		if (pcaAppAndBackgroundDto.getIndCtznshpUs() != null) {
			pcaEligApplication.setIndCtznshpUs(pcaAppAndBackgroundDto.getIndCtznshpUs().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndCtznshpPerm() != null) {
			pcaEligApplication.setIndCtznshpPerm(pcaAppAndBackgroundDto.getIndCtznshpPerm().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndCtznshpOthQualAlien() != null) {
			pcaEligApplication.setIndCtznshpOthQualAlien(pcaAppAndBackgroundDto.getIndCtznshpOthQualAlien().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndCtznshpUnknown() != null) {
			pcaEligApplication.setIndCtznshpUnknown(pcaAppAndBackgroundDto.getIndCtznshpUnknown().charAt(0));
		}

		pcaEligApplication.setDtChildEnteredUs(pcaAppAndBackgroundDto.getDtChildEnteredUs());

		pcaEligApplication.setTxtChildEnteredUs(pcaAppAndBackgroundDto.getTxtChildEnteredUs());

		pcaEligApplication.setDtApplSubmitted(pcaAppAndBackgroundDto.getDtApplSubmitted());

		if (pcaAppAndBackgroundDto.getIndDocBirthVerfy() != null) {
			pcaEligApplication.setIndDocBirthVerfy(pcaAppAndBackgroundDto.getIndDocBirthVerfy().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocSrvcLvlSh() != null) {
			pcaEligApplication.setIndDocSrvcLvlSh(pcaAppAndBackgroundDto.getIndDocSrvcLvlSh().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocCourtOrdrSibl() != null) {
			pcaEligApplication.setIndDocCourtOrdrSibl(pcaAppAndBackgroundDto.getIndDocCourtOrdrSibl().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocCourtOrdrMngCnv() != null) {
			pcaEligApplication.setIndDocCourtOrdrMngCnv(pcaAppAndBackgroundDto.getIndDocCourtOrdrMngCnv().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocForm2115() != null) {
			pcaEligApplication.setIndDocForm2115(pcaAppAndBackgroundDto.getIndDocForm2115().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocCourtOrdr() != null) {
			pcaEligApplication.setIndDocCourtOrdr(pcaAppAndBackgroundDto.getIndDocCourtOrdr().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocHmStudy() != null) {
			pcaEligApplication.setIndDocHmStudy(pcaAppAndBackgroundDto.getIndDocHmStudy().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocForm2116() != null) {
			pcaEligApplication.setIndDocForm2116(pcaAppAndBackgroundDto.getIndDocForm2116().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocForm2118() != null) {
			pcaEligApplication.setIndDocForm2118(pcaAppAndBackgroundDto.getIndDocForm2118().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocOther() != null) {
			pcaEligApplication.setIndDocOther(pcaAppAndBackgroundDto.getIndDocOther().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocFbiBgchk() != null) {
			pcaEligApplication.setIndDocFbiBgchk(pcaAppAndBackgroundDto.getIndDocFbiBgchk().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocBirthVerfySibl() != null) {
			pcaEligApplication.setIndDocBirthVerfySibl(pcaAppAndBackgroundDto.getIndDocBirthVerfySibl().charAt(0));
		}

		pcaEligApplication.setCdWithdrawRsn(pcaAppAndBackgroundDto.getCdWithdrawRsn());
		pcaEligApplication.setTxtWithdrawRsn(pcaAppAndBackgroundDto.getWithdrawRsn());
		pcaEligApplication.setIdCreatedPerson(pcaAppAndBackgroundDto.getIdCreatedPerson());
		pcaEligApplication.setIdLastUpdatePerson(pcaAppAndBackgroundDto.getIdLastUpdatePerson());

		if (pcaAppAndBackgroundDto.getIndEligFcmp6mthsFicKin() != null) {
			pcaEligApplication.setIndEligFcmp6mthsFicKin(pcaAppAndBackgroundDto.getIndEligFcmp6mthsFicKin().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndFceRmvlChildOrdered() != null) {
			pcaEligApplication.setIndFceRmvlChildOrdered(pcaAppAndBackgroundDto.getIndFceRmvlChildOrdered().charAt(0));
		}

		pcaEligApplication.setCdFceEligActual(pcaAppAndBackgroundDto.getCdFceEligActual());
		if (pcaAppAndBackgroundDto.getIndChildSibling1() != null) {
			pcaEligApplication.setIndChildSibling1(pcaAppAndBackgroundDto.getIndChildSibling1().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndEligFcmpU6mthsRel() != null) {
			pcaEligApplication.setIndEligFcmpU6mthsRel(pcaAppAndBackgroundDto.getIndEligFcmpU6mthsRel().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndEligFcmpU6mthsFicKin() != null) {
			pcaEligApplication
					.setIndEligFcmpU6mthsFicKin(pcaAppAndBackgroundDto.getIndEligFcmpU6mthsFicKin().charAt(0));
		}
		pcaEligApplication.setDtCreated(new Date());
		pcaEligApplication.setDtLastUpdate(new Date());

		sessionFactory.getCurrentSession().saveOrUpdate(pcaEligApplication);
		return pcaEligApplication.getIdPcaEligApplication();
	}

	private void setQualSibPerson(PcaAppAndBackgroundDto pcaAppAndBackgroundDto,
			PcaEligApplication pcaEligApplication) {
		Person personByIdQualSibPerson = null;
		if (pcaAppAndBackgroundDto.getIdQualSibPerson() != null && pcaAppAndBackgroundDto.getIdQualSibPerson() > 0) {

			personByIdQualSibPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
					pcaAppAndBackgroundDto.getIdQualSibPerson());
			pcaEligApplication.setPersonByIdQualSibPerson(personByIdQualSibPerson);
		}
	}

	private void setEligibility(PcaAppAndBackgroundDto pcaAppAndBackgroundDto, PcaEligApplication pcaEligApplication) {
		Eligibility eligibility = null;
		if (pcaAppAndBackgroundDto.getIdEligEvent() != null) {
			eligibility = (Eligibility) sessionFactory.getCurrentSession().get(Eligibility.class,
					pcaAppAndBackgroundDto.getIdEligEvent());
			pcaEligApplication.setEligibility(eligibility);
		}
	}

	private void setFceEligibility(PcaAppAndBackgroundDto pcaAppAndBackgroundDto,
			PcaEligApplication pcaEligApplication) {
		FceEligibility fceEligibility = null;
		if (pcaAppAndBackgroundDto.getIdFceEligibility() != null) {
			fceEligibility = (FceEligibility) sessionFactory.getCurrentSession().get(FceEligibility.class,
					pcaAppAndBackgroundDto.getIdFceEligibility());
			pcaEligApplication.setFceEligibility(fceEligibility);
		}
	}

	private void setEvent(PcaAppAndBackgroundDto pcaAppAndBackgroundDto, PcaEligApplication pcaEligApplication) {
		Event event = null;
		if (pcaAppAndBackgroundDto.getIdPlcmtEvent() != null && pcaAppAndBackgroundDto.getIdPerson() > 0) {
			event = (Event) sessionFactory.getCurrentSession().get(Event.class,
					pcaAppAndBackgroundDto.getIdPlcmtEvent());
			pcaEligApplication.setEvent(event);
		}
	}

	private void setPerson(PcaAppAndBackgroundDto pcaAppAndBackgroundDto, PcaEligApplication pcaEligApplication) {
		Person personByIdPerson = null;
		if (pcaAppAndBackgroundDto.getIdPerson() != null && pcaAppAndBackgroundDto.getIdPerson() > 0) {
			personByIdPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
					pcaAppAndBackgroundDto.getIdPerson());
			pcaEligApplication.setPersonByIdPerson(personByIdPerson);
		}
	}

	/**
	 * Method Name: updatePcaEligApplication Method Description:This method
	 * updates PCA_ELIG_APPLICATION table
	 * 
	 * @param appDto
	 * @return Long @
	 */
	@Override
	public Long updatePcaEligApplication(PcaAppAndBackgroundDto pcaAppAndBackgroundDto) {
		PcaEligApplication pcaEligApplication = new PcaEligApplication();

		// set PCA eligibility Application id
		pcaEligApplication.setIdPcaEligApplication(pcaAppAndBackgroundDto.getIdPcaEligApplication());

		if (null != pcaAppAndBackgroundDto.getNbrChildQualifyAge()) {
			pcaEligApplication.setNbrChildQualifyAge(pcaAppAndBackgroundDto.getNbrChildQualifyAge().shortValue());
		}

		pcaEligApplication.setCdChildPermGoal(pcaAppAndBackgroundDto.getCdChildPermGoal());

		Person personByIdPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
				pcaAppAndBackgroundDto.getIdPerson());
		personByIdPerson.getCdPersonSex();
		pcaEligApplication.setPersonByIdPerson(personByIdPerson);

		if (null != pcaAppAndBackgroundDto.getIdPlcmtEvent()) {
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class,
					pcaAppAndBackgroundDto.getIdPlcmtEvent());
			pcaEligApplication.setEvent(event);
		} else {
			pcaEligApplication.setEvent(null);
		}

		if (pcaAppAndBackgroundDto.getIndWithSibling() != null) {
			pcaEligApplication.setIndWithSibling(pcaAppAndBackgroundDto.getIndWithSibling().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndHmRemJudicial() != null) {
			pcaEligApplication.setIndHmRemJudicial(pcaAppAndBackgroundDto.getIndHmRemJudicial().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndNoReturnHome() != null) {
			pcaEligApplication.setIndNoReturnHome(pcaAppAndBackgroundDto.getIndNoReturnHome().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndRelAttachment() != null) {
			pcaEligApplication.setIndRelAttachment(pcaAppAndBackgroundDto.getIndRelAttachment().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndChildConsulted() != null) {
			pcaEligApplication.setIndChildConsulted(pcaAppAndBackgroundDto.getIndChildConsulted().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndFairHearing() != null) {
			pcaEligApplication.setIndFairHearing(pcaAppAndBackgroundDto.getIndFairHearing().charAt(0));
		}

		pcaEligApplication.setDtFairHearing(pcaAppAndBackgroundDto.getDtFairHearing());

		// Citizenship Detail update
		// fix for defect 12739 - INC000004959320 - R2 PCA Application Missing Data when
		// reviewed by Eligibility Specialist
		if (ServiceConstants.YES.equals(pcaAppAndBackgroundDto.getIndCtznshpUs())) {
			pcaEligApplication.setIndCtznshpUs(pcaAppAndBackgroundDto.getIndCtznshpUs().charAt(0));
			pcaEligApplication.setIndCtznshpPerm(ServiceConstants.STRING_IND_N.charAt(0));
			pcaEligApplication.setIndCtznshpOthQualAlien(ServiceConstants.STRING_IND_N.charAt(0));
			pcaEligApplication.setIndCtznshpUnknown(ServiceConstants.STRING_IND_N.charAt(0));
		}

		if (ServiceConstants.YES.equals(pcaAppAndBackgroundDto.getIndCtznshpPerm())) {
			pcaEligApplication.setIndCtznshpPerm(pcaAppAndBackgroundDto.getIndCtznshpPerm().charAt(0));
			pcaEligApplication.setIndCtznshpUs(ServiceConstants.STRING_IND_N.charAt(0));
			pcaEligApplication.setIndCtznshpOthQualAlien(ServiceConstants.STRING_IND_N.charAt(0));
			pcaEligApplication.setIndCtznshpUnknown(ServiceConstants.STRING_IND_N.charAt(0));
		}

		if (ServiceConstants.YES.equals(pcaAppAndBackgroundDto.getIndCtznshpOthQualAlien())) {
			pcaEligApplication.setIndCtznshpOthQualAlien(pcaAppAndBackgroundDto.getIndCtznshpOthQualAlien().charAt(0));
			pcaEligApplication.setIndCtznshpUs(ServiceConstants.STRING_IND_N.charAt(0));
			pcaEligApplication.setIndCtznshpPerm(ServiceConstants.STRING_IND_N.charAt(0));
			pcaEligApplication.setIndCtznshpUnknown(ServiceConstants.STRING_IND_N.charAt(0));
		}

		if (ServiceConstants.YES.equals(pcaAppAndBackgroundDto.getIndCtznshpUnknown())) {
			pcaEligApplication.setIndCtznshpUnknown(pcaAppAndBackgroundDto.getIndCtznshpUnknown().charAt(0));
			pcaEligApplication.setIndCtznshpUs(ServiceConstants.STRING_IND_N.charAt(0));
			pcaEligApplication.setIndCtznshpPerm(ServiceConstants.STRING_IND_N.charAt(0));
			pcaEligApplication.setIndCtznshpOthQualAlien(ServiceConstants.STRING_IND_N.charAt(0));
		}

		pcaEligApplication.setDtChildEnteredUs(pcaAppAndBackgroundDto.getDtChildEnteredUs());
		pcaEligApplication.setTxtChildEnteredUs(pcaAppAndBackgroundDto.getTxtChildEnteredUs());
		pcaEligApplication.setDtApplSubmitted(pcaAppAndBackgroundDto.getDtApplSubmitted());
		if (pcaAppAndBackgroundDto.getIndDocBirthVerfy() != null) {
			pcaEligApplication.setIndDocBirthVerfy(pcaAppAndBackgroundDto.getIndDocBirthVerfy().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocSrvcLvlSh() != null) {
			pcaEligApplication.setIndDocSrvcLvlSh(pcaAppAndBackgroundDto.getIndDocSrvcLvlSh().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocCourtOrdrSibl() != null) {
			pcaEligApplication.setIndDocCourtOrdrSibl(pcaAppAndBackgroundDto.getIndDocCourtOrdrSibl().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocCourtOrdrMngCnv() != null) {
			pcaEligApplication.setIndDocCourtOrdrMngCnv(pcaAppAndBackgroundDto.getIndDocCourtOrdrMngCnv().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocForm2115() != null) {
			pcaEligApplication.setIndDocForm2115(pcaAppAndBackgroundDto.getIndDocForm2115().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocCourtOrdr() != null) {
			pcaEligApplication.setIndDocCourtOrdr(pcaAppAndBackgroundDto.getIndDocCourtOrdr().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocHmStudy() != null) {
			pcaEligApplication.setIndDocHmStudy(pcaAppAndBackgroundDto.getIndDocHmStudy().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocForm2116() != null) {
			pcaEligApplication.setIndDocForm2116(pcaAppAndBackgroundDto.getIndDocForm2116().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocForm2118() != null) {
			pcaEligApplication.setIndDocForm2118(pcaAppAndBackgroundDto.getIndDocForm2118().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocOther() != null) {
			pcaEligApplication.setIndDocOther(pcaAppAndBackgroundDto.getIndDocOther().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocFbiBgchk() != null) {
			pcaEligApplication.setIndDocFbiBgchk(pcaAppAndBackgroundDto.getIndDocFbiBgchk().charAt(0));
		}

		if (pcaAppAndBackgroundDto.getIndDocBirthVerfySibl() != null) {
			pcaEligApplication.setIndDocBirthVerfySibl(pcaAppAndBackgroundDto.getIndDocBirthVerfySibl().charAt(0));
		}

		pcaEligApplication.setCdWithdrawRsn(pcaAppAndBackgroundDto.getCdWithdrawRsn());

		pcaEligApplication.setTxtWithdrawRsn(pcaAppAndBackgroundDto.getWithdrawRsn());
		pcaEligApplication.setIdCreatedPerson(pcaAppAndBackgroundDto.getIdCreatedPerson());
		pcaEligApplication.setIdLastUpdatePerson(pcaAppAndBackgroundDto.getIdLastUpdatePerson());

		if (null != pcaAppAndBackgroundDto.getIdQualSibPerson()) {
			Person personByIdQualSibPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
					pcaAppAndBackgroundDto.getIdQualSibPerson());
			pcaEligApplication.setPersonByIdQualSibPerson(personByIdQualSibPerson);
		} else {
			pcaEligApplication.setPersonByIdQualSibPerson(null);
		}
		if (null != pcaAppAndBackgroundDto.getIdFceEligibility() && pcaAppAndBackgroundDto.getIdFceEligibility() != 0) {
			FceEligibility fceEligibility = (FceEligibility) sessionFactory.getCurrentSession()
					.get(FceEligibility.class, pcaAppAndBackgroundDto.getIdFceEligibility());
			pcaEligApplication.setFceEligibility(fceEligibility);
		}
		if (pcaAppAndBackgroundDto.getIndFceRmvlChildOrdered() != null) {
			pcaEligApplication.setIndFceRmvlChildOrdered(pcaAppAndBackgroundDto.getIndFceRmvlChildOrdered().charAt(0));
		}

		if (null != pcaAppAndBackgroundDto.getIdEligEvent()) {
			Eligibility eligibility = (Eligibility) sessionFactory.getCurrentSession().get(Eligibility.class,
					pcaAppAndBackgroundDto.getIdEligEvent());
			pcaEligApplication.setEligibility(eligibility);
		}

		pcaEligApplication.setCdFceEligActual(pcaAppAndBackgroundDto.getCdFceEligActual());
		if (pcaAppAndBackgroundDto.getIndChildSibling1() != null) {
			pcaEligApplication.setIndChildSibling1(pcaAppAndBackgroundDto.getIndChildSibling1().charAt(0));
		}

		// Placement detail Update
		// fix for defect 12739 - INC000004959320 - R2 PCA Application Missing Data when
		// reviewed by Eligibility Specialist
		if (ServiceConstants.YES.equals(pcaAppAndBackgroundDto.getIndEligFcmp6mthsRel())) {
			pcaEligApplication.setIndEligFcmp6mthsRel(pcaAppAndBackgroundDto.getIndEligFcmp6mthsRel().charAt(0));
			pcaEligApplication.setIndEligFcmp6mthsFicKin(ServiceConstants.STRING_IND_N.charAt(0));
			pcaEligApplication.setIndEligFcmpU6mthsRel(ServiceConstants.STRING_IND_N.charAt(0));
			pcaEligApplication.setIndEligFcmpU6mthsFicKin(ServiceConstants.STRING_IND_N.charAt(0));
		}

		if (ServiceConstants.YES.equals(pcaAppAndBackgroundDto.getIndEligFcmp6mthsFicKin())) {
			pcaEligApplication.setIndEligFcmp6mthsFicKin(pcaAppAndBackgroundDto.getIndEligFcmp6mthsFicKin().charAt(0));
			pcaEligApplication.setIndEligFcmp6mthsRel(ServiceConstants.STRING_IND_N.charAt(0));
			pcaEligApplication.setIndEligFcmpU6mthsRel(ServiceConstants.STRING_IND_N.charAt(0));
			pcaEligApplication.setIndEligFcmpU6mthsFicKin(ServiceConstants.STRING_IND_N.charAt(0));
		}

		if (ServiceConstants.YES.equals(pcaAppAndBackgroundDto.getIndEligFcmpU6mthsRel())) {
			pcaEligApplication.setIndEligFcmpU6mthsRel(pcaAppAndBackgroundDto.getIndEligFcmpU6mthsRel().charAt(0));
			pcaEligApplication.setIndEligFcmp6mthsFicKin(ServiceConstants.STRING_IND_N.charAt(0));
			pcaEligApplication.setIndEligFcmp6mthsRel(ServiceConstants.STRING_IND_N.charAt(0));
			pcaEligApplication.setIndEligFcmpU6mthsFicKin(ServiceConstants.STRING_IND_N.charAt(0));
		}

		if (ServiceConstants.YES.equals(pcaAppAndBackgroundDto.getIndEligFcmpU6mthsFicKin())) {
			pcaEligApplication
					.setIndEligFcmpU6mthsFicKin(pcaAppAndBackgroundDto.getIndEligFcmpU6mthsFicKin().charAt(0));
			pcaEligApplication.setIndEligFcmpU6mthsRel(ServiceConstants.STRING_IND_N.charAt(0));
			pcaEligApplication.setIndEligFcmp6mthsFicKin(ServiceConstants.STRING_IND_N.charAt(0));
			pcaEligApplication.setIndEligFcmp6mthsRel(ServiceConstants.STRING_IND_N.charAt(0));

		}
		pcaEligApplication.setDtCreated(new Date());
		pcaEligApplication.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(pcaEligApplication));
		return pcaEligApplication.getIdPcaEligApplication();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.pcaappandbackground.dao.PcaAppAndBackgroundDao#
	 * fetchPriorPlcmts(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> fetchPriorPlcmts(Long idPlcmtEvent) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchPriorPlcmts)
				.addScalar("idEvent", StandardBasicTypes.LONG).setParameter("idEvent", idPlcmtEvent);

		return (List<Long>) sqlQuery.list();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.pcaappandbackground.dao.PcaAppAndBackgroundDao#
	 * selectLatestApplication(java.lang.Long, java.lang.String[])
	 */
	@Override
	public PcaAppAndBackgroundDto selectLatestApplication(Long idQualSibPerson, String[] statusArray) {
		StringBuilder sql = new StringBuilder(selectLatestApplicationSql);
		if (statusArray.length > 0) {
			sql.append(" AND  EVT.CD_EVENT_STATUS IN (:statusList)");

		}
		sql.append(") ");
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql.toString())
				.addScalar("idPcaEligApplication", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("nbrChildQualifyAge", StandardBasicTypes.LONG)
				.addScalar("cdChildPermGoal", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("indEligFcmp6mthsRel", StandardBasicTypes.STRING)
				.addScalar("indWithSibling", StandardBasicTypes.STRING)
				.addScalar("indHmRemJudicial", StandardBasicTypes.STRING)
				.addScalar("indNoReturnHome", StandardBasicTypes.STRING)
				.addScalar("indRelAttachment", StandardBasicTypes.STRING)
				.addScalar("indChildConsulted", StandardBasicTypes.STRING)
				.addScalar("indFairHearing", StandardBasicTypes.STRING)
				.addScalar("dtFairHearing", StandardBasicTypes.DATE)
				.addScalar("indCtznshpUs", StandardBasicTypes.STRING)
				.addScalar("indCtznshpPerm", StandardBasicTypes.STRING)
				.addScalar("indCtznshpOthQualAlien", StandardBasicTypes.STRING)
				.addScalar("indCtznshpUnknown", StandardBasicTypes.STRING)
				.addScalar("dtChildEnteredUs", StandardBasicTypes.DATE)
				.addScalar("txtChildEnteredUs", StandardBasicTypes.STRING)
				.addScalar("dtApplSubmitted", StandardBasicTypes.DATE)
				.addScalar("indDocBirthVerfy", StandardBasicTypes.STRING)
				.addScalar("indDocSrvcLvlSh", StandardBasicTypes.STRING)
				.addScalar("indDocCourtOrdrSibl", StandardBasicTypes.STRING)
				.addScalar("indDocCourtOrdrMngCnv", StandardBasicTypes.STRING)
				.addScalar("indDocForm2115", StandardBasicTypes.STRING)
				.addScalar("indDocCourtOrdr", StandardBasicTypes.STRING)
				.addScalar("indDocHmStudy", StandardBasicTypes.STRING)
				.addScalar("indDocForm2116", StandardBasicTypes.STRING)
				.addScalar("indDocForm2118", StandardBasicTypes.STRING)
				.addScalar("indDocOther", StandardBasicTypes.STRING)
				.addScalar("indDocFbiBgchk", StandardBasicTypes.STRING)
				.addScalar("indDocBirthVerfySibl", StandardBasicTypes.STRING)
				.addScalar("cdWithdrawRsn", StandardBasicTypes.STRING)
				.addScalar("withdrawRsn", StandardBasicTypes.STRING)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idQualSibPerson", StandardBasicTypes.LONG)
				.addScalar("indEligFcmp6mthsFicKin", StandardBasicTypes.STRING)
				.addScalar("idFceEligibility", StandardBasicTypes.LONG)
				.addScalar("indFceRmvlChildOrdered", StandardBasicTypes.STRING)
				.addScalar("idEligEvent", StandardBasicTypes.LONG)
				.addScalar("cdFceEligActual", StandardBasicTypes.STRING)
				.addScalar("indChildSibling1", StandardBasicTypes.STRING)
				.addScalar("indEligFcmpU6mthsRel", StandardBasicTypes.STRING)
				.addScalar("indEligFcmpU6mthsFicKin", StandardBasicTypes.STRING)
				.setParameter("idPerson", idQualSibPerson)
				.setResultTransformer(Transformers.aliasToBean(PcaAppAndBackgroundDto.class));
		if (statusArray.length > 0) {
			sQLQuery1.setParameterList("statusList", statusArray);

		}
		List<PcaAppAndBackgroundDto> appAndBackgroundDtoList = sQLQuery1.list();

		if (CollectionUtils.isEmpty(appAndBackgroundDtoList)) {
			PcaAppAndBackgroundDto resultDto = new PcaAppAndBackgroundDto();
			resultDto.setIdEligEvent(0L);
			resultDto.setIdPlcmtEvent(0L);
			return resultDto;
		}
		log.debug("Exiting method selectLatestAppForStage in PcaAppAndBackgroundDaoImpl");
		return appAndBackgroundDtoList.get(ServiceConstants.Zero);
	}

	@Override
	public PcaAppAndBackgroundDto selectLatestAppForStage(Long idStage) {
		PcaAppAndBackgroundDto pcaAppAndBackgroundDto = new PcaAppAndBackgroundDto();
		log.debug("Entering method selectLatestAppForStage in PcaAppAndBackgroundDaoImpl");
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectLatestApplForStageSql)
				.addScalar("idPcaEligApplication", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("nbrChildQualifyAge", StandardBasicTypes.LONG)
				.addScalar("cdChildPermGoal", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("indEligFcmp6mthsRel", StandardBasicTypes.STRING)
				.addScalar("indWithSibling", StandardBasicTypes.STRING)
				.addScalar("indHmRemJudicial", StandardBasicTypes.STRING)
				.addScalar("indNoReturnHome", StandardBasicTypes.STRING)
				.addScalar("indRelAttachment", StandardBasicTypes.STRING)
				.addScalar("indChildConsulted", StandardBasicTypes.STRING)
				.addScalar("indFairHearing", StandardBasicTypes.STRING)
				.addScalar("dtFairHearing", StandardBasicTypes.DATE)
				.addScalar("indCtznshpUs", StandardBasicTypes.STRING)
				.addScalar("indCtznshpPerm", StandardBasicTypes.STRING)
				.addScalar("indCtznshpOthQualAlien", StandardBasicTypes.STRING)
				.addScalar("indCtznshpUnknown", StandardBasicTypes.STRING)
				.addScalar("dtChildEnteredUs", StandardBasicTypes.DATE)
				.addScalar("txtChildEnteredUs", StandardBasicTypes.STRING)
				.addScalar("dtApplSubmitted", StandardBasicTypes.DATE)
				.addScalar("indDocBirthVerfy", StandardBasicTypes.STRING)
				.addScalar("indDocSrvcLvlSh", StandardBasicTypes.STRING)
				.addScalar("indDocCourtOrdrSibl", StandardBasicTypes.STRING)
				.addScalar("indDocCourtOrdrMngCnv", StandardBasicTypes.STRING)
				.addScalar("indDocForm2115", StandardBasicTypes.STRING)
				.addScalar("indDocCourtOrdr", StandardBasicTypes.STRING)
				.addScalar("indDocHmStudy", StandardBasicTypes.STRING)
				.addScalar("indDocForm2116", StandardBasicTypes.STRING)
				.addScalar("indDocForm2118", StandardBasicTypes.STRING)
				.addScalar("indDocOther", StandardBasicTypes.STRING)
				.addScalar("indDocFbiBgchk", StandardBasicTypes.STRING)
				.addScalar("indDocBirthVerfySibl", StandardBasicTypes.STRING)
				.addScalar("cdWithdrawRsn", StandardBasicTypes.STRING)
				.addScalar("withdrawRsn", StandardBasicTypes.STRING)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idQualSibPerson", StandardBasicTypes.LONG)
				.addScalar("indEligFcmp6mthsFicKin", StandardBasicTypes.STRING)
				.addScalar("idFceEligibility", StandardBasicTypes.LONG)
				.addScalar("indFceRmvlChildOrdered", StandardBasicTypes.STRING)
				.addScalar("idEligEvent", StandardBasicTypes.LONG)
				.addScalar("cdFceEligActual", StandardBasicTypes.STRING)
				.addScalar("indChildSibling1", StandardBasicTypes.STRING)
				.addScalar("indEligFcmpU6mthsRel", StandardBasicTypes.STRING)
				.addScalar("indEligFcmpU6mthsFicKin", StandardBasicTypes.STRING).setParameter("idEventStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(PcaAppAndBackgroundDto.class));
		List<PcaAppAndBackgroundDto> appAndBackgroundDtoList = sQLQuery1.list();

		if (!TypeConvUtil.isNullOrEmpty(appAndBackgroundDtoList) && !appAndBackgroundDtoList.isEmpty()) {
			pcaAppAndBackgroundDto = appAndBackgroundDtoList.get(ServiceConstants.Zero);
		}
		log.debug("Exiting method selectLatestAppForStage in PcaAppAndBackgroundDaoImpl");
		return pcaAppAndBackgroundDto;
	}

	/**
	 * Method Name: markTodoComplete Method Description:This method marks all
	 * the Todos associate with the given event Complete by setting the end
	 * date.
	 * 
	 * @param idEvent
	 * @return Long @
	 */
	@Override
	public Long markTodoComplete(Long idEvent) {
		Todo todo = (Todo) sessionFactory.getCurrentSession().get(Todo.class, idEvent);
		Long result = ServiceConstants.ZERO_VAL;
		if (TypeConvUtil.isNullOrEmpty(todo)) {
			// Uncomment below once PCA stage coding will start
			/*
			 * throw new DataNotFoundException(
			 * messageSource.getMessage("pcaAppAndBackground.todo.NotFound",
			 * null, Locale.US));
			 */
			result = ServiceConstants.ZERO_VAL;
		} else {
			todo.setDtTodoCompleted(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(todo);
			result = ServiceConstants.ONE_VAL;
		}
		return result;
	}

	/**
	 * 
	 * Method Name: insertPcaAppEventLink Method Description: This method
	 * inserts record into PCA_APP_EVENT_LINK table.
	 * 
	 * @param idPcaEligApplication
	 * @param idPcaEligRecert
	 * @param idEvent
	 * @param idCase
	 * @param idLastUpdatePerson
	 * @return
	 */
	@Override
	public Long insertPcaAppEventLink(Long idPcaEligApplication, Long idPcaEligRecert, Long idEvent, Long idCase,
			Long idLastUpdatePerson) {
		PcaAppEventLink pcaAppEventLink = new PcaAppEventLink();

		PcaEligApplication pcaEligApplication = (PcaEligApplication) sessionFactory.getCurrentSession()
				.get(PcaEligApplication.class, idPcaEligApplication);
		pcaAppEventLink.setPcaEligApplication(pcaEligApplication);

		PcaEligRecert pcaEligRecert = (PcaEligRecert) sessionFactory.getCurrentSession().get(PcaEligRecert.class,
				idPcaEligRecert);
		pcaAppEventLink.setPcaEligRecert(pcaEligRecert);
		if (idEvent != null && idEvent > 0) {
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, idEvent);
			pcaAppEventLink.setEvent(event);
		}

		if (idCase != null && idCase > 0) {
			CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class, idCase);
			pcaAppEventLink.setCapsCase(capsCase);
		}

		pcaAppEventLink.setIdCreatedPerson(idLastUpdatePerson);
		pcaAppEventLink.setIdLastUpdatePerson(idLastUpdatePerson);
		pcaAppEventLink.setDtCreated(new Date());
		pcaAppEventLink.setDtLastUpdate(new Date());

		return (Long) sessionFactory.getCurrentSession().save(pcaAppEventLink);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.pcaappandbackground.dao.PcaAppAndBackgroundDao#
	 * findLinkedSubStage(java.lang.Long, java.lang.String)
	 */
	@Override
	public StageDto findLinkedSubStage(Long idSubStage, String cstagesSub) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(findLinkedSubStage)
				.addScalar("idStage", StandardBasicTypes.LONG).setParameter("idStage", idSubStage)
				.setParameter("cdStage", cstagesSub).setResultTransformer(Transformers.aliasToBean(StageDto.class));
		return (StageDto) query.uniqueResult();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.pcaappandbackground.dao.PcaAppAndBackgroundDao#
	 * selectEligFromIdPcaApp(java.lang.Long)
	 */
	@Override
	public PcaEligDeterminationDto selectEligFromIdPcaApp(Long idPcaEligApplication) {
		PcaEligDeterminationDto pcaEligDeterminationDto = new PcaEligDeterminationDto();
		List<PcaEligDeterminationDto> pcaEligDeterminationDtoList = new ArrayList<PcaEligDeterminationDto>();
		pcaEligDeterminationDtoList = (List<PcaEligDeterminationDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(selectEligFromIdPcaApp)

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
				.addScalar("indAsstDisqualified", StandardBasicTypes.STRING)
				.setParameter("idPcaEligApplication", idPcaEligApplication)
				.setResultTransformer(Transformers.aliasToBean(PcaEligDeterminationDto.class)).list();

		if(pcaEligDeterminationDtoList.size() >= 1){
			pcaEligDeterminationDto = pcaEligDeterminationDtoList.get(0);
		}
		return pcaEligDeterminationDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.pcaappandbackground.dao.PcaAppAndBackgroundDao#
	 * selectPcaEligApplication(us.tx.state.dfps.service.common.request.
	 * PcaAppAndBackgroundReq)
	 */
	@Override
	public PcaAppAndBackgroundDto selectPcaEligApplication(PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(selectPcaEligApp);
		query.setParameter("idPcaEligApplication", pcaAppAndBackgroundReq.getIdPcaEligApplication());
		query.addScalar("idPcaEligApplication", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("nbrChildQualifyAge", StandardBasicTypes.LONG)
				.addScalar("cdChildPermGoal", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("indEligFcmp6mthsRel", StandardBasicTypes.STRING)
				.addScalar("indWithSibling", StandardBasicTypes.STRING)
				.addScalar("indHmRemJudicial", StandardBasicTypes.STRING)
				.addScalar("indNoReturnHome", StandardBasicTypes.STRING)
				.addScalar("indRelAttachment", StandardBasicTypes.STRING)
				.addScalar("indChildConsulted", StandardBasicTypes.STRING)
				.addScalar("indFairHearing", StandardBasicTypes.STRING)
				.addScalar("dtFairHearing", StandardBasicTypes.DATE)
				.addScalar("indCtznshpUs", StandardBasicTypes.STRING)
				.addScalar("indCtznshpPerm", StandardBasicTypes.STRING)
				.addScalar("indCtznshpOthQualAlien", StandardBasicTypes.STRING)
				.addScalar("indCtznshpUnknown", StandardBasicTypes.STRING)
				.addScalar("dtChildEnteredUs", StandardBasicTypes.DATE)
				.addScalar("txtChildEnteredUs", StandardBasicTypes.STRING)
				.addScalar("dtApplSubmitted", StandardBasicTypes.DATE)
				.addScalar("indDocBirthVerfy", StandardBasicTypes.STRING)
				.addScalar("indDocSrvcLvlSh", StandardBasicTypes.STRING)
				.addScalar("indDocCourtOrdrSibl", StandardBasicTypes.STRING)
				.addScalar("indDocCourtOrdrMngCnv", StandardBasicTypes.STRING)
				.addScalar("indDocForm2115", StandardBasicTypes.STRING)
				.addScalar("indDocCourtOrdr", StandardBasicTypes.STRING)
				.addScalar("indDocHmStudy", StandardBasicTypes.STRING)
				.addScalar("indDocForm2116", StandardBasicTypes.STRING)
				.addScalar("indDocForm2118", StandardBasicTypes.STRING)
				.addScalar("indDocOther", StandardBasicTypes.STRING)
				.addScalar("indDocFbiBgchk", StandardBasicTypes.STRING)
				.addScalar("indDocBirthVerfySibl", StandardBasicTypes.STRING)
				.addScalar("cdWithdrawRsn", StandardBasicTypes.STRING)
				.addScalar("withdrawRsn", StandardBasicTypes.STRING)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idQualSibPerson", StandardBasicTypes.LONG)
				.addScalar("indEligFcmp6mthsFicKin", StandardBasicTypes.STRING)
				.addScalar("idFceEligibility", StandardBasicTypes.LONG)
				.addScalar("indFceRmvlChildOrdered", StandardBasicTypes.STRING)
				.addScalar("idEligEvent", StandardBasicTypes.LONG)
				.addScalar("cdFceEligActual", StandardBasicTypes.STRING)
				.addScalar("indChildSibling1", StandardBasicTypes.STRING)
				.addScalar("indEligFcmpU6mthsRel", StandardBasicTypes.STRING)
				.addScalar("indEligFcmpU6mthsFicKin", StandardBasicTypes.STRING);

		return (PcaAppAndBackgroundDto) query
				.setResultTransformer(Transformers.aliasToBean(PcaAppAndBackgroundDto.class)).uniqueResult();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.pcaappandbackground.dao.PcaAppAndBackgroundDao#
	 * selectPrevApplication(us.tx.state.dfps.service.common.request.
	 * PcaAppAndBackgroundReq)
	 */
	@Override
	public PcaAppAndBackgroundDto selectPrevApplication(PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(selectPrevApplication);
		query.addScalar("idPcaEligApplication", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("nbrChildQualifyAge", StandardBasicTypes.LONG)
				.addScalar("cdChildPermGoal", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("indEligFcmp6mthsRel", StandardBasicTypes.STRING)
				.addScalar("indWithSibling", StandardBasicTypes.STRING)
				.addScalar("indHmRemJudicial", StandardBasicTypes.STRING)
				.addScalar("indNoReturnHome", StandardBasicTypes.STRING)
				.addScalar("indRelAttachment", StandardBasicTypes.STRING)
				.addScalar("indChildConsulted", StandardBasicTypes.STRING)
				.addScalar("indFairHearing", StandardBasicTypes.STRING)
				.addScalar("dtFairHearing", StandardBasicTypes.DATE)
				.addScalar("indCtznshpUs", StandardBasicTypes.STRING)
				.addScalar("indCtznshpPerm", StandardBasicTypes.STRING)
				.addScalar("indCtznshpOthQualAlien", StandardBasicTypes.STRING)
				.addScalar("indCtznshpUnknown", StandardBasicTypes.STRING)
				.addScalar("dtChildEnteredUs", StandardBasicTypes.DATE)
				.addScalar("txtChildEnteredUs", StandardBasicTypes.STRING)
				.addScalar("dtApplSubmitted", StandardBasicTypes.DATE)
				.addScalar("indDocBirthVerfy", StandardBasicTypes.STRING)
				.addScalar("indDocSrvcLvlSh", StandardBasicTypes.STRING)
				.addScalar("indDocCourtOrdrSibl", StandardBasicTypes.STRING)
				.addScalar("indDocCourtOrdrMngCnv", StandardBasicTypes.STRING)
				.addScalar("indDocForm2115", StandardBasicTypes.STRING)
				.addScalar("indDocCourtOrdr", StandardBasicTypes.STRING)
				.addScalar("indDocHmStudy", StandardBasicTypes.STRING)
				.addScalar("indDocForm2116", StandardBasicTypes.STRING)
				.addScalar("indDocForm2118", StandardBasicTypes.STRING)
				.addScalar("indDocOther", StandardBasicTypes.STRING)
				.addScalar("indDocFbiBgchk", StandardBasicTypes.STRING)
				.addScalar("indDocBirthVerfySibl", StandardBasicTypes.STRING)
				.addScalar("cdWithdrawRsn", StandardBasicTypes.STRING)
				.addScalar("withdrawRsn", StandardBasicTypes.STRING)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idQualSibPerson", StandardBasicTypes.LONG)
				.addScalar("indEligFcmp6mthsFicKin", StandardBasicTypes.STRING)
				.addScalar("idFceEligibility", StandardBasicTypes.LONG)
				.addScalar("indFceRmvlChildOrdered", StandardBasicTypes.STRING)
				.addScalar("idEligEvent", StandardBasicTypes.LONG)
				.addScalar("cdFceEligActual", StandardBasicTypes.STRING)
				.addScalar("indChildSibling1", StandardBasicTypes.STRING)
				.addScalar("indEligFcmpU6mthsRel", StandardBasicTypes.STRING)
				.addScalar("indEligFcmpU6mthsFicKin", StandardBasicTypes.STRING)
				.setParameter("idEventStage", pcaAppAndBackgroundReq.getIdStage())
				.setParameter("idPcaEligApplication", pcaAppAndBackgroundReq.getIdPcaEligApplication());

		return (PcaAppAndBackgroundDto) query
				.setResultTransformer(Transformers.aliasToBean(PcaAppAndBackgroundDto.class)).uniqueResult();

	}
}
