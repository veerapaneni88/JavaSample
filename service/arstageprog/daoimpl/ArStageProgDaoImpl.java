package us.tx.state.dfps.service.arstageprog.daoimpl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Allegation;
import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.CpsInvstDetail;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.IntakeAllegation;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.PersonEligibility;
import us.tx.state.dfps.common.domain.PersonMerge;
import us.tx.state.dfps.common.domain.PersonMergeViewId;
import us.tx.state.dfps.common.domain.RecRetenType;
import us.tx.state.dfps.common.domain.RecordsRetention;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.StageLink;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.common.domain.Workload;
import us.tx.state.dfps.common.dto.StageClosureValueDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.arstageprog.dao.ArStageProgDao;
import us.tx.state.dfps.service.casepackage.dao.RecordsRetentionDao;
import us.tx.state.dfps.service.casepackage.dto.RecordsRetnDestDtlsDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.PersonEligibilityDto;
import us.tx.state.dfps.service.person.dto.PersonEligibilityValueDto;
import us.tx.state.dfps.service.workload.dto.SVCAuthDetailDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: DaoImpl for
 * functions required for implementing ARStageProg functionality Sep 6, 2017-
 * 7:54:41 PM © 2017 Texas Department of Family and Protective Services
 */

@Repository
public class ArStageProgDaoImpl implements ArStageProgDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${ArStageProgDaoImpl.getAllEligiblePrinciplesArStage}")
	private String getAllEligiblePrinciplesArStagesql;

	@Value("${ArStageProgDaoImpl.getPersonEligibilityRecord}")
	private String getPersonEligibilityRecordSql;

	@Value("${ArStageProgDaoImpl.getEligibilityStartDate}")
	private String getEligibilityStartDateSql;

	@Value("${ArStageProgDaoImpl.getNbrOfDays}")
	private String getNbrOfDays;

	@Value("${ArStageProgDaoImpl.fetchForwardPersonsForStagePersons}")
	private String fetchForwardPersonsForStagePersonsSql;

	@Value("${ArStageProgDaoImpl.fetchForwardPersonsForIntakeAllegationsSql}")
	private String fetchForwardPersonsForIntakeAllegationsSql;
	
	@Autowired
	RecordsRetentionDao recordsRetentionDao;

	/**
	 * Method Name: insertIntoStageLink Method Description:This function inserts
	 * new Record into Stage_Link table. This is basically used to link new
	 * Stage with Old Stage.
	 * 
	 * @param stageValueBeanDto
	 * @param newStageId
	 * @
	 */

	@Override
	public void insertIntoStageLink(StageValueBeanDto stageValueBeanDto, Long newStageId) {

		StageLink stageLink = new StageLink();
		stageLink.setIdStageLink(Long.valueOf(ServiceConstants.ID_STAGE_VALUE));
		stageLink.setIdStage(newStageId);
		stageLink.setIdPriorStage(stageValueBeanDto.getIdStage());
		sessionFactory.getCurrentSession().save(stageLink);

	}

	/**
	 * Method Name: insertIntoEvent Method Description: This method inserts
	 * single Record into Event Table.
	 * 
	 * @param stageValueDto
	 * @param newStageId
	 * @param idCreatedPerson
	 * @return Long
	 */
	@Override
	public Long insertIntoEvent(StageValueBeanDto stageValueDto, Long newStageId, Long idCreatedPerson) {

		String msgStageOpened = null;
		if (ServiceConstants.CSTAGES_AR.equals(stageValueDto.getCdStage())) {
			msgStageOpened = ServiceConstants.STAGE_AR_OPENED_FULLTEXT;
		} else if (ServiceConstants.CSTAGES_FPR.equals(stageValueDto.getCdStage())) {
			msgStageOpened = ServiceConstants.STAGE_FPR_OPENED_FULLTEXT;
		} else {
			MessageFormat messageFormat = new MessageFormat(ServiceConstants.STAGE_OPEN);
			Object[] args = { stageValueDto.getCdStage() };
			msgStageOpened = messageFormat.format(args);
		}

		Event event = new Event();
		event.setCdEventStatus(ServiceConstants.STATUS_COMP);
		event.setCdEventType(ServiceConstants.STATUS_STG);
		event.setDtEventOccurred(new Date());
		event.setDtLastUpdate(new Date());

		Person person = new Person();
		person.setIdPerson(idCreatedPerson);
		event.setPerson(person);

		Stage stage = new Stage();
		stage.setIdStage(newStageId);
		event.setStage(stage);

		event.setTxtEventDescr(msgStageOpened);
		event.setDtEventCreated(new Date());

		return (Long) sessionFactory.getCurrentSession().save(event);
	}

	/**
	 * Method Name: getAllEligiblePrinciplesArStage Method Description:Inserts
	 * Stage Ids into Stage Link table
	 * 
	 * @param idStage
	 * @return List<Integer>
	 * @throws DataNotFoundException
	 */
	@Override
	public List<Integer> getAllEligiblePrinciplesArStage(Long idStage) throws DataNotFoundException {
		List<Integer> getAllEligiblePrinciplesArStageList = new ArrayList<>();
		Query getAllEligiblePrinciplesArStage = sessionFactory.getCurrentSession()
				.createSQLQuery(getAllEligiblePrinciplesArStagesql).addScalar("idPerson", StandardBasicTypes.INTEGER)
				.setParameter("idstage", idStage);

		getAllEligiblePrinciplesArStageList = getAllEligiblePrinciplesArStage.list();

		if (TypeConvUtil.isNullOrEmpty(getAllEligiblePrinciplesArStageList)) {
			throw new DataNotFoundException(messageSource.getMessage("arStage.data", null, Locale.US));
		}

		return getAllEligiblePrinciplesArStageList;
	}

	/**
	 * Method Name: getPersonEligibilityRecord Method Description:Verify was
	 * there any open PERSON_ELIGIBILITY record already exist for each person
	 * 
	 * @param idPerson
	 * @return
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PersonEligibilityDto getPersonEligibilityRecord(Integer idPerson) throws DataNotFoundException {
		List<PersonEligibilityDto> personEligibilityDtoList = new ArrayList<PersonEligibilityDto>();
		personEligibilityDtoList = (List<PersonEligibilityDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getPersonEligibilityRecordSql).addScalar("idPersElig", StandardBasicTypes.LONG)
				.addScalar("idPersEligPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdPersEligEligType", StandardBasicTypes.STRING)
				.addScalar("dtPersEligStart", StandardBasicTypes.DATE)
				.addScalar("dtPersEligEnd", StandardBasicTypes.DATE)
				.addScalar("dtPersEligEaDeny", StandardBasicTypes.DATE)
				.addScalar("cdPersEligPrgStart", StandardBasicTypes.STRING)
				.addScalar("cdPersEligPrgOpen", StandardBasicTypes.STRING)
				.addScalar("cdPersEligPrgClosed", StandardBasicTypes.STRING).setParameter("idPersEligPerson", idPerson)
				.setParameter("cdPersEligEligType", ServiceConstants.CCLIELIG_EA)
				.setResultTransformer(Transformers.aliasToBean(PersonEligibilityDto.class)).list();
		if (!ObjectUtils.isEmpty(personEligibilityDtoList)) {
			return personEligibilityDtoList.get(0);
		}
		return null;
	}

	/**
	 * Method Name: updatePersonEligibility Method Description: Update
	 * PERSON_ELIGIBILITY records with PersonEligibilityProgOpenCode
	 * 
	 * @param personEligibilityValueDto
	 */
	@Override
	public void updatePersonEligibility(PersonEligibilityValueDto personEligibilityValueDto)
			throws DataNotFoundException {
		PersonEligibility personEligibility = (PersonEligibility) sessionFactory.getCurrentSession()
				.get(PersonEligibility.class, personEligibilityValueDto.getPersonEligibilityId());
		if (TypeConvUtil.isNullOrEmpty(personEligibility)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		personEligibility.setCdPersEligPrgOpen(ServiceConstants.CPRGDIST_C);
		personEligibility.setDtLastUpdate(Calendar.getInstance().getTime());
		sessionFactory.getCurrentSession().saveOrUpdate(personEligibility);
	}

	/**
	 * Method Name: getEligibilityStartDate Method Description:Earliest of
	 * (Stage progress date from A-R to FPR and Approved Service Auths begin
	 * date in A-R Stage)
	 * 
	 * @param idStage
	 * @return Date
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Date getEligibilityStartDate(Long idStage) throws DataNotFoundException {
		Date dtEligibilityStart = null;
		List<SVCAuthDetailDto> authDetailDtoList = new ArrayList<SVCAuthDetailDto>();
		authDetailDtoList = (List<SVCAuthDetailDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getEligibilityStartDateSql).addScalar("dtSvcAuthDtlBegin", StandardBasicTypes.DATE)
				.setParameter("idStage", idStage).setParameter("cdEventStatus", ServiceConstants.CEVTSTAT_APRV)
				.setParameter("idStage", idStage).setResultTransformer(Transformers.aliasToBean(SVCAuthDetailDto.class))
				.list();
		dtEligibilityStart = authDetailDtoList.get(0).getDtSvcAuthDtlBegin();
		return dtEligibilityStart;
	}

	/**
	 * 
	 * Method Name: createPersonEligibility Method Description:Create New
	 * PERSON_ELIGIBILITY record.
	 * 
	 * @param idPerson
	 * @param dtPersEligStart
	 * @return
	 * @throws DataNotFoundException
	 */
	@Override
	public Long createPersonEligibility(Long idPerson, Date dtPersEligStart) throws DataNotFoundException {
		Integer nbrOfDaysYear = 365;
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getNbrOfDays)
				.addScalar("leapYear", StandardBasicTypes.INTEGER).setParameter("year", dtPersEligStart);
		Integer leapYear = (Integer) sQLQuery1.uniqueResult();

		if (!TypeConvUtil.isNullOrEmpty(leapYear) && leapYear == 0) {
			nbrOfDaysYear = 366;
		}
		PersonEligibility personEligibility = new PersonEligibility();
		Person person = new Person();
		person.setIdPerson(idPerson);
		personEligibility.setPerson(person);
		personEligibility.setCdPersEligEligType(ServiceConstants.CCLIELIG_EA);
		personEligibility.setDtPersEligStart(dtPersEligStart);
		personEligibility.setDtPersEligEnd(DateUtils.addToDate(dtPersEligStart, 0, 0, nbrOfDaysYear));
		// Defect 15567 artf164195 - removed incorrect Date(12/31/1969) implementation and added util date reference(12/31/4712).
		personEligibility.setDtPersEligEaDeny(ServiceConstants.GENERIC_END_DATE);
		personEligibility.setCdPersEligPrgStart(ServiceConstants.CPRGDIST_C);
		personEligibility.setCdPersEligPrgOpen(ServiceConstants.CPRGDIST_C);
		personEligibility.setDtLastUpdate(Calendar.getInstance().getTime());
		return (Long) sessionFactory.getCurrentSession().save(personEligibility);
	}

	/**
	 * Method Name: closeStage Method Description: This method closes the
	 * specified Stage and also updates the 'reason for stage closing'.
	 * 
	 * @param stageClosureValueDto
	 * @return long
	 */
	@Override
	public long closeStage(StageValueBeanDto stageValueBeanDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Stage.class);
		criteria.add(Restrictions.eq("idStage", stageValueBeanDto.getIdStage()));
		Stage stage = (Stage) criteria.uniqueResult();
		if (stage != null) {
			stage.setDtStageClose(stageValueBeanDto.getDtStageClose());
			stage.setCdStageReasonClosed(stageValueBeanDto.getCdStageReasonClosed());
			sessionFactory.getCurrentSession().saveOrUpdate(stage);
			return stage.getIdStage();
		}
		return 0l;
	}

	/**
	 * Method Name: updateStagePersonLink Method Description: This method
	 * updates StagePersonLink table.
	 * 
	 * @param stagePersonValueDto
	 * @return long
	 */
	@Override
	public long updateStagePersonLink(StagePersonValueDto stagePersonValueDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
		criteria.add(Restrictions.eq("idStagePersonLink", stagePersonValueDto.getIdStagePersonLink()));

		StagePersonLink stage = (StagePersonLink) criteria.uniqueResult();
		if (stage != null) {
			stage.setCdStagePersType(stagePersonValueDto.getCdStagePersType());
			stage.setCdStagePersRole(stagePersonValueDto.getCdStagePersRole());
			stage.setCdStagePersRelInt(stagePersonValueDto.getCdStagePersRelLong());
			stage.setDtLastUpdate(Calendar.getInstance().getTime());
			sessionFactory.getCurrentSession().saveOrUpdate(stage);
		}

		return criteria.list().size();
	}

	/**
	 * 
	 * Method Name: updateStagePersonLink Method Description: This method
	 * updates StagePersonLink table. It will update Type, Role and Rel-Int
	 * 
	 * @param idOldPerson
	 * @param idNewPerson
	 * @param idStage
	 * @param idCase
	 * @param cdRole
	 * @return Long
	 * @throws DataNotFoundException
	 */
	@Override
	public Long updateStagePersonLink(Long idOldPerson, Long idNewPerson, Long idStage, Long idCase, String cdRole)
			throws DataNotFoundException {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
		criteria.add(Restrictions.eq("idPerson", idOldPerson));
		criteria.add(Restrictions.eq("idStage", idStage));
		criteria.add(Restrictions.eq("idCase", idCase));
		criteria.add(Restrictions.eq("cdStagePersRole", cdRole));

		StagePersonLink stagePersonLink = (StagePersonLink) criteria.uniqueResult();
		stagePersonLink.setIdPerson(idNewPerson);
		stagePersonLink.setDtLastUpdate(Calendar.getInstance().getTime());
		
		sessionFactory.getCurrentSession().saveOrUpdate(stagePersonLink);
		
		if (TypeConvUtil.isNullOrEmpty(stagePersonLink)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}

		return stagePersonLink.getIdPerson();
	}

	/**
	 * Method Name: createEvent Method Description: Creates a new Event. This
	 * method uses all the columns in the table.Generic function
	 * 
	 * @param eventValueDto
	 * @return int
	 */
	@Override
	public int createEvent(EventValueDto eventValueDto) {
		int eventId = 0;

		Stage stage = new Stage();
		stage.setIdStage((long) eventValueDto.getIdStage());
		Event event = new Event();
		event.setStage(stage);

		Person person = new Person();
		person.setIdPerson((long) eventValueDto.getIdPerson());
		event.setPerson(person);

		event.setCdEventType(eventValueDto.getCdEventType());
		event.setIdCase((long) eventValueDto.getIdCase());
		event.setCdTask(eventValueDto.getCdEventTask());
		event.setTxtEventDescr(eventValueDto.getEventDescr());
		event.setCdEventStatus(eventValueDto.getCdEventStatus());
		event.setDtEventCreated(Calendar.getInstance().getTime());
		event.setDtLastUpdate(Calendar.getInstance().getTime());
		event.setDtEventOccurred(Calendar.getInstance().getTime());
		Long events = (Long) sessionFactory.getCurrentSession().save(event);
		eventId = events.intValue();

		return eventId;
	}

	/**
	 * Method Name: closeStage Method Description: This method closes the
	 * specified Stage and also updates the 'reason for stage closing'.
	 * 
	 * @param stageClosureValueDto
	 * @return long
	 */
	@Override
	public long closeStage(StageClosureValueDto stageClosureValueDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Stage.class);

		criteria.add(Restrictions.eq("idStage", (long) stageClosureValueDto.getIdStage()));
		Stage stage = (Stage) criteria.uniqueResult();

		if (stage != null) {
			stage.setDtStageClose(stageClosureValueDto.getDtStageClosed());
			stage.setCdStageReasonClosed(stageClosureValueDto.getCdStageCloseRsn());
			sessionFactory.getCurrentSession().saveOrUpdate(stage);
		}

		return stageClosureValueDto.getIdStage();
	}

	/**
	 * Method Name: fetchForwardPersonsForStagePersons Method Description: This
	 * method will fetch the forward persons for all Stage persons
	 * 
	 * @param idStage
	 * @return Map<Integer,Integer>
	 */
	@Override
	public Map<Integer, Integer> fetchForwardPersonsForStagePersons(Long idStage) throws DataNotFoundException {
		Map<Integer, Integer> forwardsMap = new HashMap<Integer, Integer>();

		@SuppressWarnings("unchecked")
		List<PersonMergeViewId> personMergeViewIdList = (List) sessionFactory.getCurrentSession()
				.createSQLQuery(fetchForwardPersonsForStagePersonsSql)
				.addScalar("idPersonInput", StandardBasicTypes.LONG)
				.addScalar("idPersonOutput", StandardBasicTypes.BIG_DECIMAL).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(PersonMergeViewId.class)).list();

		for (PersonMergeViewId personMergeViewId : personMergeViewIdList) {
			int inputId = (int) personMergeViewId.getIdPersonInput();
			int outputId = personMergeViewId.getIdPersonOutput().intValue();
			if (inputId != outputId) {
				if (forwardsMap.containsKey(inputId)) {
					forwardsMap.put(inputId, outputId);
				} else {
					forwardsMap.put(inputId, outputId);
				}
				forwardsMap.put((int) personMergeViewId.getIdPersonInput(),
						personMergeViewId.getIdPersonOutput().intValue());
			}
		}

		return forwardsMap;
	}

	/**
	 * Method Name: fetchIntakeAllegations Method Description: This method
	 * fetches Intake Allegations.
	 * 
	 * @param idIntakeStage
	 * @return List<AllegationDto>
	 */
	@Override
	public List<AllegationDto> fetchIntakeAllegations(Long idIntakeStage) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IntakeAllegation.class);
		criteria.add(Restrictions.eq("idAllegationStage", idIntakeStage));
		List<IntakeAllegation> allegationList = criteria.list();

		if (TypeConvUtil.isNullOrEmpty(allegationList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("fetchIntakeAllegations.notFound", null, Locale.US));
		}
		List<AllegationDto> allegationDtoList = new ArrayList<>();

		criteria = sessionFactory.getCurrentSession().createCriteria(PersonMerge.class);
		BigDecimal personMergeFwdPid;
		for (IntakeAllegation intakeAllegation : allegationList) {
			AllegationDto allegationDto = new AllegationDto();
			allegationDto.setIdAllegation(intakeAllegation.getIdAllegation());

			// 11004 - Check if the victimID is present before fetching the person Entity
			if (!ObjectUtils.isEmpty(intakeAllegation.getPersonByIdVictim())) {
				// ALM 14342 - Check for person merge forward id if applicable
				personMergeFwdPid = (BigDecimal) sessionFactory.getCurrentSession()
					.createSQLQuery(fetchForwardPersonsForIntakeAllegationsSql)
					.setParameter("personByIdPersMergeClosed", intakeAllegation.getPersonByIdVictim().getIdPerson())
					.setMaxResults(1)
					.uniqueResult();
				if(ObjectUtils.isEmpty(personMergeFwdPid)) {
					allegationDto.setIdVictim(intakeAllegation.getPersonByIdVictim().getIdPerson());
				} else { // ALM 14342
					allegationDto.setIdVictim(personMergeFwdPid.longValue());
				}
			}
			if (!ObjectUtils.isEmpty(intakeAllegation.getPersonByIdAllegedPerpetrator())) {
				// ALM 14342 - Check for person merge forward id if applicable
				personMergeFwdPid = (BigDecimal) sessionFactory.getCurrentSession()
					.createSQLQuery(fetchForwardPersonsForIntakeAllegationsSql)
					.setParameter("personByIdPersMergeClosed", intakeAllegation.getPersonByIdAllegedPerpetrator().getIdPerson())
					.setMaxResults(1)
					.uniqueResult();
				if(ObjectUtils.isEmpty(personMergeFwdPid)) {
					allegationDto.setIdAllegedPerpetrator(intakeAllegation.getPersonByIdAllegedPerpetrator().getIdPerson());
				} else { // ALM 14342
					allegationDto.setIdAllegedPerpetrator(personMergeFwdPid.longValue());
				}
			}

			allegationDto.setCdAllegType(intakeAllegation.getCdIntakeAllegType());
			allegationDto.setAllegDuration(intakeAllegation.getTxtIntakeAllegDuration());
			//Defect 5532- To set Allegation Stage once the case gets transferred from A-R to INV.
			allegationDto.setCdAllegIncidentStage(ServiceConstants.INTAKE);
			allegationDtoList.add(allegationDto);

		}

		return allegationDtoList;

	}

	/**
	 * Method Name: insertCPSInvestigationDetail Method Description:This method
	 * inserts the INVESTIGATION Details when INV Stage is created
	 * 
	 * @param eventID
	 * @param caseID
	 * @param stageID
	 * @param idUser
	 * @param inTakeStartDate
	 * @return Long
	 */
	public Long insertCPSInvestigationDetail(Long caseID, Long stageID, Long idUser, Date inTakeStartDate,
			Long idEvent) {
		CpsInvstDetail cpsInvstDetail = new CpsInvstDetail();
		cpsInvstDetail.setIdCase(caseID);
		Stage stage = new Stage();
		stage.setIdStage(stageID);
		cpsInvstDetail.setStage(stage);
		Date date = new Date();
		cpsInvstDetail.setIdEvent(idEvent);
		cpsInvstDetail.setDtLastUpdate(date);
		cpsInvstDetail.setDtCpsInvstDtlIntake(date);
		cpsInvstDetail.setDtCpsInvstDtlAssigned(date);
		sessionFactory.getCurrentSession().save(cpsInvstDetail);
		return idEvent;
	}

	/**
	 * Method Name: updatePriorStageAndIncomingCallDate Method
	 * Description:Updates the Prior Stage and Incoming call date. A trigger
	 * exists for this but will not work for this scenario. Stage link issue.
	 * 
	 * @param idNewStage
	 * @param idCase
	 * @param idPriorStage
	 * @param incomingCallDate
	 * @throws DataNotFoundException
	 * 
	 */

	@Override
	public void updatePriorStageAndIncomingCallDate(Long idNewStage, Long idCase, Long idPriorStage,
			Timestamp incomingCallDate) throws DataNotFoundException {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Workload.class, "workload");

		criteria.add(Restrictions.eq("workload.id.idWkldStage", idNewStage));

		criteria.add(Restrictions.eq("workload.id.idWkldCase", idCase));

		Workload workLoad = (Workload) criteria.uniqueResult();

		if (TypeConvUtil.isNullOrEmpty(workLoad)) {
			// TODO: Debug why Workload has not been created and uncomment below
			// line
			// throw new
			// DataNotFoundException(messageSource.getMessage("arStage.data",
			// null, Locale.US));
			return;
		}
		if (!TypeConvUtil.isNullOrEmpty(incomingCallDate)) {
			workLoad.getId().setDtIncomingCall(incomingCallDate);
		}

		if (idPriorStage > 0) {
			workLoad.getId().setIdPriorStage(idPriorStage);
		}
		sessionFactory.getCurrentSession().saveOrUpdate(workLoad);
	}

	/**
	 * Method Name: createInvestigationAllegations Method Description: Creates
	 * Investigation Allegations For A-R Stage.
	 * 
	 * @param idToStage
	 * @param idCase
	 * @param allegationDtoList
	 * @return Long[]
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("null")
	@Override
	public Long[] createInvestigationAllegations(Long idToStage, Long idCase, List<AllegationDto> allegationDtoList)
			throws DataNotFoundException {
		Long[] reternData = null;
		if ((TypeConvUtil.isNullOrEmpty(allegationDtoList))
				|| ((!TypeConvUtil.isNullOrEmpty(allegationDtoList)) && (allegationDtoList.size() == 0))) {
			return null;
		}
		for (AllegationDto allegationDto : allegationDtoList) {

			Allegation allegation = new Allegation();

			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, allegationDto.getIdVictim());
			/*person.setIdPerson(allegationDto.getIdVictim());*/
			allegation.setPersonByIdVictim(person);

			if (!ObjectUtils.isEmpty(allegationDto.getIdAllegedPerpetrator())){
				Person personobj = (Person) sessionFactory.getCurrentSession().get(Person.class, allegationDto.getIdAllegedPerpetrator());
				// personobj.setIdPerson(allegationDto.getIdAllegedPerpetrator());
				allegation.setPersonByIdAllegedPerpetrator(personobj);
			}

			Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, idToStage);
			// stage.setIdStage(idToStage);
			allegation.setStage(stage);

			allegation.setIdCase(idCase);
			allegation.setCdAllegType(allegationDto.getCdAllegType());
			allegation.setTxtAllegDuration(allegationDto.getAllegDuration());
			allegation.setCdAllegIncidentStage(allegationDto.getCdAllegIncidentStage());
			allegation.setDtLastUpdate(Calendar.getInstance().getTime());

			sessionFactory.getCurrentSession().save(allegation);
		}

		return reternData;
	}

	/**
	 * Method Name: fetchRecordRetentionDate Method Description:This method will
	 * fetch the record retention type month and Final records retention date
	 * 
	 * @return Date
	 * @throws DataNotFoundException
	 */

	@SuppressWarnings("unchecked")
	@Override
	public Date fetchRecordRetentionDate() throws DataNotFoundException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RecRetenType.class);
		criteria.add(Restrictions.eq("cdRecRtnType", ServiceConstants.CRECRETN_TYPE_ARR));
		List<RecRetenType> recRetenType = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(recRetenType)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}

		Date returnDate = DateUtils.addToDate(new Date(), recRetenType.get(0).getNbrRecRtnTypeYear(),
				recRetenType.get(0).getNbrRecRtnTypeMnth(), ServiceConstants.Zero);
		return returnDate;
	}

	/**
	 * Method Name: createRecordRetentionRecord Method Description:lternative
	 * Response Records Retention A-R stage needs new retention code,
	 * REC_RETEN_TYPE row, and retention date calculation modifications Creates
	 * a new record REtention record
	 * 
	 * @param idCase
	 * @param recordRetentionType
	 * @param destroyActualDate
	 * @param eligDate
	 */
	@Override
	public void createRecordRetentionRecord(int idCase, String recordRetentionType, Date destroyActualDate,
			Date eligDate) {
		//Modified the code to create record retention record for Warranty defect 11603
		// Get the Destruction Date - Call to proc_CalcRecRetn procedure -
		// Equivalent of Legacy ccmnj8d
		RecordsRetnDestDtlsDto recordsRetnDestDtlsDto = recordsRetentionDao.getDestructionDate(Long.valueOf(idCase));
		Date destructionDateActual = recordsRetnDestDtlsDto.getDtDestruction();
		RecordsRetention recordsRetnEntity = new RecordsRetention();
		recordsRetnEntity.setCdRecRtnRetenType(recordsRetnDestDtlsDto.getCdRecordRetnType());
		CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class, Long.valueOf(idCase));
		recordsRetnEntity.setCapsCase(capsCase);
		recordsRetnEntity.setDtRecRtnDstryActual(destructionDateActual);
		recordsRetnEntity.setDtRecRtnDstryElig(destructionDateActual);
		recordsRetnEntity.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().save(recordsRetnEntity);
	}

}
