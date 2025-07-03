package us.tx.state.dfps.service.workload.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Employee;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.HandwrittenData;
import us.tx.state.dfps.common.domain.MedCnsntrFormLog;
import us.tx.state.dfps.common.domain.MedicalConsenter;
import us.tx.state.dfps.common.domain.MedicalConsenterNarr;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.MedicalConsenterFormLogReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.MedicalConsenterRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.handwriting.dao.HandWritingDao;
import us.tx.state.dfps.service.handwriting.dto.HandWritingValueDto;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;
import us.tx.state.dfps.service.person.dto.MedicalConsenterFormLogDto;
import us.tx.state.dfps.service.workload.dao.MedicalConsenterDao;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN19S Class
 * Description: This is used to perform the add, update & delete operations in
 * Medical Concenter table. Apr 3 , 2017 - 3:50:30 PM
 */
@SuppressWarnings("unchecked")
@Repository
public class MedicalConsenterDaoImpl implements MedicalConsenterDao {
	private static final String SSCC = "SSCC";
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${MedicalConcenterDaoImpl.updateMedicalConcenterByAttributes}")
	private String updateMedicalConcenterByAttributesSql;

	@Value("${MedicalConsenterDaoImpl.fetchActiveMedConsRecForPerson}")
	private String fetchActiveMedConsRecForPersonSql;

	@Value("${MedicalConsenterDaoImpl.queryMedicalConsenterList}")
	private String queryMedicalConsenterListSql;

	@Value("${MedicalConsenterDaoImpl.queryMedicalConsenterRecord}")
	private String queryMedicalConsenterRecordSql;

	@Value("${MedicalConsenterDaoImpl.isPersonMedicalConsenter}")
	private String isPersonMedicalConsenterSql;

	@Value("${MedicalConsenterDaoImpl.isPersonMedicalConsenterType}")
	private String isPersonMedicalConsenterTypeSql;

	@Value("${MedicalConsenterDaoImpl.getCorrespStageAodStageSynch}")
	private String getCorrespStageAodStageSynchSql;

	@Value("${MedicalConsenterDaoImpl.getCorrespStageSubStageSynch}")
	private String getCorrespStageSubStageSynchSql;

	@Value("${MedicalConsenterDaoImpl.hasPersonAddrZip}")
	private String hasPersonAddrZipSql;

	@Value("${MedicalConsenterDaoImpl.indToDoExists}")
	private String indToDoExistsSql;

	@Value("${MedicalConsenterDaoImpl.getPrimaryWorker}")
	private String getPrimaryWorkerSql;

	@Value("${MedicalConsenterDaoImpl.checkMCPBExist}")
	private String checkMCPBExistSql;

	@Value("${MedicalConsenterDaoImpl.checkMCCourtAuth}")
	private String checkMCCourtAuthSql;

	@Autowired
	MessageSource messageSource;

	@Autowired
	HandWritingDao handWritingDao;

	/**
	 * Updates end date value in Medical Consenter record given Case id and
	 * Primary Child id.
	 * 
	 * Service Name : CCMN02U, DAM Name : CAUDK8D
	 * 
	 * @param dateEnd
	 * @param idCase
	 * @param idPerson
	 * @
	 */
	@Override
	public void updateMedicalConcenterByAttributes(Date dateEnd, Long idCase, Long idPerson) {
		Query queryMedicalConcenter = sessionFactory.getCurrentSession()
				.createSQLQuery(updateMedicalConcenterByAttributesSql);
		queryMedicalConcenter.setParameter("dateEnd", dateEnd);
		queryMedicalConcenter.setParameter("idCase", idCase);
		queryMedicalConcenter.setParameter("idPerson", idPerson);
		queryMedicalConcenter.executeUpdate();
	}

	/**
	 * Method Name: fetchActiveMedConsRecForPerson Method Description: Fetches
	 * the list of records where a person is active medical consenter
	 * 
	 * @param idMedConPerson
	 * @return List<MedicalConsenterDto> @
	 */
	@Override
	public List<MedicalConsenterDto> fetchActiveMedConsRecForPerson(Long idMedConPerson) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(fetchActiveMedConsRecForPersonSql).addScalar("idMedConsenter", StandardBasicTypes.LONG)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idMedConsenterPerson", StandardBasicTypes.LONG)
				.addScalar("cdMedConsenterType", StandardBasicTypes.STRING)
				.addScalar("cdCourtAuth", StandardBasicTypes.STRING).addScalar("cdDfpsDesig", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("dtMedConsStart", StandardBasicTypes.DATE)
				.addScalar("dtMedConsEnd", StandardBasicTypes.DATE).addScalar("dtMedConsFiled", StandardBasicTypes.DATE)
				.addScalar("youthSome", StandardBasicTypes.STRING).addScalar("indCourtAuth", StandardBasicTypes.STRING)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("indSensitiveCase", StandardBasicTypes.STRING)
				.addScalar("dtStageStart", StandardBasicTypes.DATE).addScalar("cdStage", StandardBasicTypes.STRING)
				.setParameter("idMedConPerson", idMedConPerson)
				.setResultTransformer(Transformers.aliasToBean(MedicalConsenterDto.class));

		List<MedicalConsenterDto> medicalConsenterDtoList = sqlQuery.list();
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterDtoList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("person.ActiveMedConsRecForPerson.empty", null, Locale.US));
		}
		return medicalConsenterDtoList;
	}

	/**
	 * Method Name: selectPersonIdFromDao Method Description:Select personId
	 * from case and stage ids in database.
	 * 
	 * @param medicalConsenterDto
	 * @return Long @
	 */
	@Override
	public Long selectPersonIdFromDao(MedicalConsenterDto medicalConsenterDto) {
		Long idPerson = ServiceConstants.ZERO_VAL;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);

		criteria.add(Restrictions.eq("idStage", medicalConsenterDto.getIdStage()));
		criteria.add(Restrictions.eq("idCase", medicalConsenterDto.getIdCase()));
		criteria.add(Restrictions.eq("cdStagePersRole", ServiceConstants.CROLES_PC));

		List<StagePersonLink> stagePersonLinkList = criteria.list();
		if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkList)) {
			idPerson = stagePersonLinkList.get(ServiceConstants.Zero).getIdPerson();
		} else {
			throw new DataNotFoundException(messageSource.getMessage("stagepersonlink.not.found", null, Locale.US));
		}
		return idPerson;
	}

	/**
	 * Method Name: updateMedicalConsenterDetail Method Description:update the
	 * new Medical Consenter to the database.
	 * 
	 * @param medicalConsenterDto
	 * @return MedicalConsenterDto @
	 */
	@Override
	public MedicalConsenterDto updateMedicalConsenterDetail(MedicalConsenterDto medicalConsenterDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MedicalConsenter.class);
		criteria.add(Restrictions.eq("idMedConsenter", medicalConsenterDto.getIdMedConsenter()));
		criteria.add(Restrictions.eq("dtLastUpdate", medicalConsenterDto.getDtLastUpdate()));
		List<MedicalConsenter> medicalConsenterList = criteria.list();
		if (!TypeConvUtil.isNullOrEmpty(medicalConsenterList)) {
			for (MedicalConsenter medicalConsenter : medicalConsenterList) {
				if (!TypeConvUtil.isNullOrEmpty(medicalConsenterDto.getCdMedConsenterType())
						&& !medicalConsenterDto.getCdMedConsenterType().equals(ServiceConstants.EMPTY_STRING)) {
					medicalConsenter.setCdMedConsenterType(medicalConsenterDto.getCdMedConsenterType());
				} else {
					medicalConsenter.setCdMedConsenterType(ServiceConstants.EMPTY_STRING);

				}

				if (!TypeConvUtil.isNullOrEmpty(medicalConsenterDto.getCdCourtAuth())
						&& !medicalConsenterDto.getCdCourtAuth().equals(ServiceConstants.EMPTY_STRING)) {
					medicalConsenter.setCdCourtAuth(medicalConsenterDto.getCdCourtAuth());
				} else {
					medicalConsenter.setCdCourtAuth(ServiceConstants.EMPTY_STRING);
				}

				if (!TypeConvUtil.isNullOrEmpty(medicalConsenterDto.getCdDfpsDesig())
						&& !medicalConsenterDto.getCdDfpsDesig().equals(ServiceConstants.EMPTY_STRING)) {
					medicalConsenter.setCdDfpsDesig(medicalConsenterDto.getCdDfpsDesig());
				} else {
					medicalConsenter.setCdDfpsDesig(ServiceConstants.EMPTY_STRING);

				}

				if (!TypeConvUtil.isNullOrEmpty(medicalConsenterDto.getComments())
						&& !medicalConsenterDto.getComments().equals(ServiceConstants.EMPTY_STRING)) {
					medicalConsenter.setTxtYouthSome(medicalConsenterDto.getComments());

				} else {
					medicalConsenter.setTxtYouthSome(ServiceConstants.EMPTY_STRING);

				}

				if (!TypeConvUtil.isNullOrEmpty(medicalConsenterDto.getIndCourtAuth())
						&& !medicalConsenterDto.getIndCourtAuth().equals(ServiceConstants.EMPTY_STRING)) {
					medicalConsenter.setIndCourtAuth(medicalConsenterDto.getIndCourtAuth());

				} else {
					medicalConsenter.setIndCourtAuth(ServiceConstants.EMPTY_STRING);

				}
				medicalConsenter.setDtMedConsStart(medicalConsenterDto.getDtMedConsStart());
				medicalConsenter.setDtMedConsEnd(medicalConsenterDto.getDtMedConsEnd());
				medicalConsenter.setDtMedConsFiled(medicalConsenterDto.getDtMedConsFiled());
				sessionFactory.getCurrentSession().saveOrUpdate(medicalConsenter);
			}
		}

		return medicalConsenterDto;
	}

	/**
	 * Method Name: addMedicalConsenterDetail Method Description:Saves the new
	 * Medical Consenter to the database.
	 * 
	 * @param medicalConsenterDto
	 * @return MedicalConsenterDto @
	 */
	@Override
	public MedicalConsenterDto addMedicalConsenterDetail(MedicalConsenterDto medicalConsenterDto) {

		MedicalConsenter medicalConsenter = new MedicalConsenter();
		Criteria stageCriteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		stageCriteria.add(Restrictions.eq("idEvent", medicalConsenterDto.getIdEvent()));
		Event event = (Event) stageCriteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(event)) {
			throw new DataNotFoundException(messageSource.getMessage("event.notFound", null, Locale.US));
		}
		medicalConsenter.setEvent(event);
		Criteria personCriteria = sessionFactory.getCurrentSession().createCriteria(Person.class);
		personCriteria.add(Restrictions.eq("idPerson", medicalConsenterDto.getIdPerson()));
		Person person = (Person) personCriteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(person)) {
			throw new DataNotFoundException(messageSource.getMessage("person.notFound", null, Locale.US));
		}
		medicalConsenter.setPersonByIdPerson(person);
		Criteria caseCriteria = sessionFactory.getCurrentSession().createCriteria(CapsCase.class);
		caseCriteria.add(Restrictions.eq("idCase", medicalConsenterDto.getIdCase()));
		CapsCase capsCase = (CapsCase) caseCriteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(capsCase)) {
			throw new DataNotFoundException(messageSource.getMessage("capsCase.notFound", null, Locale.US));
		}
		medicalConsenter.setCapsCase(capsCase);
		Criteria personMedCriteria = sessionFactory.getCurrentSession().createCriteria(Person.class);
		personMedCriteria.add(Restrictions.eq("idPerson", medicalConsenterDto.getIdMedConsenterPerson()));
		Person createdPerson = (Person) personMedCriteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(createdPerson)) {
			throw new DataNotFoundException(messageSource.getMessage("person.notFound", null, Locale.US));
		}
		medicalConsenter.setPersonByIdMedConsenterPerson(createdPerson);
		medicalConsenter.setCdMedConsenterType(medicalConsenterDto.getCdMedConsenterType());
		medicalConsenter.setCdCourtAuth(medicalConsenterDto.getCdCourtAuth());
		if (!TypeConvUtil.isNullOrEmpty(medicalConsenterDto.getCdDfpsDesig())
				&& !medicalConsenterDto.getCdDfpsDesig().equals(ServiceConstants.EMPTY_STRING)) {
			medicalConsenter.setCdDfpsDesig(medicalConsenterDto.getCdDfpsDesig());
		} else {
			medicalConsenter.setCdDfpsDesig(ServiceConstants.EMPTY_STRING);

		}
		medicalConsenter.setDtMedConsStart(medicalConsenterDto.getDtMedConsStart());
		medicalConsenter.setDtMedConsFiled(medicalConsenterDto.getDtMedConsFiled());
		if (!TypeConvUtil.isNullOrEmpty(medicalConsenterDto.getComments())
				&& !medicalConsenterDto.getComments().equals(ServiceConstants.EMPTY_STRING)) {
			medicalConsenter.setTxtYouthSome(medicalConsenterDto.getComments());

		} else {
			medicalConsenter.setTxtYouthSome(ServiceConstants.EMPTY_STRING);

		}
		if (!TypeConvUtil.isNullOrEmpty(medicalConsenterDto.getIndCourtAuth())
				&& !medicalConsenterDto.getIndCourtAuth().equals("")) {
			medicalConsenter.setIndCourtAuth(medicalConsenterDto.getIndCourtAuth());

		} else {
			medicalConsenter.setIndCourtAuth(ServiceConstants.EMPTY_STRING);

		}
		medicalConsenter.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().save(medicalConsenter);
		medicalConsenterDto.setDtLastUpdate(new Date());
		medicalConsenterDto.setIdMedConsenter(medicalConsenter.getIdMedConsenter());

		return medicalConsenterDto;
	}

	/**
	 * Method Name: getStageType Method Description:Method retrieves the type of
	 * input stage.
	 * 
	 * @param idStage
	 * @return String @
	 */
	@Override
	public String getStageType(Long idStage) {

		String stageType = ServiceConstants.EMPTY_STRING;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Stage.class);
		if (idStage > ServiceConstants.Zero) {
			criteria.add(Restrictions.eq("idStage", idStage));
		}
		List<Stage> stageList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(stageList)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}

		stageType = stageList.get(ServiceConstants.Zero).getCdStage();

		return stageType;

	}

	/**
	 * Method Name: getPrimaryWorker Method Description:Fetch Primary Worker for
	 * given stage only when stage is active.
	 * 
	 * @param stageId
	 * @return Long @
	 */
	@Override
	public Long getPrimaryWorker(Long stageId) {
		BigDecimal ulIdPrimaryWorker = BigDecimal.ZERO;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPrimaryWorkerSql);
		query.setParameter("idStage", stageId);
		ulIdPrimaryWorker = (BigDecimal) query.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(ulIdPrimaryWorker)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}

		return ulIdPrimaryWorker.longValue();
	}

	/**
	 * Method Name: queryMedicalConsenterList Method Description:Select list of
	 * Medical Consenter record from case and stage ids in database.
	 * 
	 * @param caseId
	 * @param stageId
	 * @return List<MedicalConsenterDto> @
	 */

	@Override
	public List<MedicalConsenterDto> queryMedicalConsenterList(Long caseId, Long stageId) {
		Long childId = ServiceConstants.ZERO_VAL;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);

		criteria.add(Restrictions.eq("idStage", stageId));
		criteria.add(Restrictions.eq("idCase", caseId));
		criteria.add(Restrictions.eq("cdStagePersRole", ServiceConstants.CROLES_PC));
		List<StagePersonLink> stagePersonLinkList = criteria.list();
		if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkList)) {
			childId = stagePersonLinkList.get(ServiceConstants.Zero).getIdPerson();
		} else {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}

		List<MedicalConsenterDto> medicalConsenterDtoList = new ArrayList<MedicalConsenterDto>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(queryMedicalConsenterListSql)
				.addScalar("idMedConsenter", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idMedConsenterPerson", StandardBasicTypes.LONG)
				.addScalar("cdMedConsenterType", StandardBasicTypes.STRING)
				.addScalar("cdCourtAuth", StandardBasicTypes.STRING).addScalar("cdDfpsDesig", StandardBasicTypes.STRING)
				.addScalar("dtMedConsStart", StandardBasicTypes.DATE).addScalar("dtMedConsEnd", StandardBasicTypes.DATE)
				.addScalar("cdNameFull", StandardBasicTypes.STRING).addScalar("phoneNbr", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(MedicalConsenterDto.class));
		query.setParameter("idCase", caseId);
		query.setParameter("idPerson", childId);
		medicalConsenterDtoList = query.list();

		if (!TypeConvUtil.isNullOrEmpty(medicalConsenterDtoList)) {
			for (MedicalConsenterDto mcBean : medicalConsenterDtoList) {
				Long idMcStage = stageId;
				Long idMcCase = mcBean.getIdCase();
				Long idMedConPer = mcBean.getIdMedConsenterPerson();
				Employee employee = getEmployeeDtls(idMedConPer);
				if (!TypeConvUtil.isNullOrEmpty(employee)) {
					mcBean.setCdPersRelInt(ServiceConstants.SG);
					if(SSCC.equals(employee.getCdEmpBjnEmp())){
						mcBean.setSSCCStaff(true);
					}
				} else {

					Criteria returnCriteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);

					returnCriteria.add(Restrictions.eq("idStage", idMcStage));
					returnCriteria.add(Restrictions.eq("idCase", idMcCase));
					returnCriteria.add(Restrictions.eq("idPerson", idMedConPer));
					StagePersonLink stagePersonLink2 = (StagePersonLink) returnCriteria.uniqueResult();
					if (!TypeConvUtil.isNullOrEmpty(stagePersonLink2)) {
						mcBean.setCdPersRelInt(stagePersonLink2.getCdStagePersRelInt());
					}
				}
			}
		}

		return medicalConsenterDtoList;
	}

	public Employee getEmployeeDtls(Long staffId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Employee.class);
		criteria.add(Restrictions.eq("idPerson", staffId));
		Employee employee = (Employee) criteria.uniqueResult();
		return employee;
	}

	/**
	 * Method Name: queryMedicalConsenterRecord Method Description:Select a
	 * medical consenter detail for the given primary id in database.
	 * 
	 * @param idMedCons
	 * @return MedicalConsenterDto @
	 */

	@Override
	public MedicalConsenterDto queryMedicalConsenterRecord(Long idMedCons) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(queryMedicalConsenterRecordSql)
				.addScalar("idMedConsenter", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idMedConsenterPerson", StandardBasicTypes.LONG)
				.addScalar("cdMedConsenterType", StandardBasicTypes.STRING)
				.addScalar("cdCourtAuth", StandardBasicTypes.STRING).addScalar("cdDfpsDesig", StandardBasicTypes.STRING)
				.addScalar("dtMedConsStart", StandardBasicTypes.DATE).addScalar("dtMedConsEnd", StandardBasicTypes.DATE)
				.addScalar("dtMedConsFiled", StandardBasicTypes.TIMESTAMP)
				.addScalar("youthSome", StandardBasicTypes.STRING).addScalar("nmNameFirst", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("cdNameSuffix", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("indCourtAuth", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(MedicalConsenterDto.class));
		query.setParameter("idMedConsenter", idMedCons);
		List<MedicalConsenterDto> medicalConsenterDtoList = query.list();
		MedicalConsenterDto returnBean = medicalConsenterDtoList.get(ServiceConstants.Zero);

		if (isDFPSStaff(returnBean.getIdMedConsenterPerson())) {
			returnBean.setCdPersRelInt(ServiceConstants.DFPS_STAFF);
		}
		return returnBean;
	}

	/**
	 * Method Name: updateEndDateRecordType Method Description:End Date Type of
	 * another record before saving new Medical Consenter
	 * 
	 * @param requestBean
	 * @return Long @
	 */
	@Override
	public Long updateEndDateRecordType(MedicalConsenterDto requestBean) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MedicalConsenter.class);

		if ((!TypeConvUtil.isNullOrEmpty(requestBean.getIdPerson()))
				&& requestBean.getIdPerson() > ServiceConstants.Zero) {
			criteria.add(Restrictions.eq("personByIdPerson.idPerson", requestBean.getIdPerson()));
		}

		if (!TypeConvUtil.isNullOrEmpty(requestBean.getCdMedConsenterType())
				&& !requestBean.getCdMedConsenterType().equals(ServiceConstants.EMPTY_STRING)) {
			criteria.add(Restrictions.eq("cdMedConsenterType", requestBean.getCdMedConsenterType()));
		}

		criteria.add(Restrictions.or(Restrictions.isNull("dtMedConsEnd"), Restrictions.gt("dtMedConsEnd", new Date())));

		List<MedicalConsenter> medicalConsenterList = criteria.list();
		if (!TypeConvUtil.isNullOrEmpty(medicalConsenterList)) {
			for (MedicalConsenter medicalConsenter : medicalConsenterList) {
				medicalConsenter.setDtMedConsEnd(requestBean.getDtMedConsStart());
				sessionFactory.getCurrentSession().saveOrUpdate(medicalConsenter);
			}
		} else {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}

		return requestBean.getIdPerson();
	}

	/**
	 * Method Name: updateEndDate Method
	 * Description:updateMedicalConsenterEndDate Method Description:Update the
	 * Medical Consenter end date to the database.
	 * 
	 * @param medicalConsenterDto
	 * @return Long @
	 */
	@Override
	public Long updateEndDate(MedicalConsenterDto medicalConsenterDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MedicalConsenter.class);

		if (medicalConsenterDto.getIdPerson() > ServiceConstants.Zero) {
			criteria.add(Restrictions.eq("personByIdPerson.idPerson", medicalConsenterDto.getIdPerson()));
		}

		criteria.add(Restrictions.or(Restrictions.isNull("dtMedConsEnd"), Restrictions.gt("dtMedConsEnd", new Date())));

		List<MedicalConsenter> medicalConsenterList = criteria.list();
		if (!TypeConvUtil.isNullOrEmpty(medicalConsenterList)) {
			for (MedicalConsenter medicalConsenter : medicalConsenterList) {
				medicalConsenter.setDtMedConsEnd(medicalConsenterDto.getDtMedConsEnd());
				sessionFactory.getCurrentSession().saveOrUpdate(medicalConsenter);
			}

		} else {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}

		return medicalConsenterDto.getIdPerson();
	}

	/**
	 * Method Name: isDFPSStaff Method Description:query if a given person id,
	 * there is a record in the employee table, if found record, return true
	 * else false
	 * 
	 * @param staffId
	 * @return Boolean @
	 */
	@Override
	public Boolean isDFPSStaff(Long staffId) {

		Boolean isStaff = ServiceConstants.FALSEVAL;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Employee.class);

		criteria.add(Restrictions.eq("idPerson", staffId));

		Employee employee = (Employee) criteria.uniqueResult();

		if (!TypeConvUtil.isNullOrEmpty(employee)) {
			isStaff = ServiceConstants.TRUEVAL;
		}

		return isStaff;
	}

	/**
	 * Method Name: isPersonMedicalConsenter Method Description:Check if the
	 * person is in Medical Consenter from Person Detail page
	 * 
	 * @param szIdPerson
	 * @param szIdStage
	 * @return Boolean @
	 */

	@Override
	public Boolean isPersonMedicalConsenter(String szIdPerson, String szIdStage) {

		Boolean isMedCons = ServiceConstants.FALSEVAL;
		List<Long> idMedConList = new ArrayList<>();

		Long idMedCons = Long.valueOf(szIdPerson);
		if (!ServiceConstants.EMPTY_STRING.equals(szIdStage)) {
			Long idEventStage = Long.valueOf(szIdStage);

			Query query = sessionFactory.getCurrentSession().createSQLQuery(isPersonMedicalConsenterSql);
			query.setParameter("idEventStage", idEventStage);
			query.setParameter("idMedCons", idMedCons);
			idMedConList = query.list();

		} else {

			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MedicalConsenter.class);

			criteria.add(Restrictions.eq("personByIdMedConsenterPerson.idPerson", idMedCons));
			idMedConList = criteria.list();
		}

		if (!ObjectUtils.isEmpty(idMedConList) && idMedConList.size() > ServiceConstants.Zero) {
			isMedCons = ServiceConstants.TRUEVAL;
		}

		return isMedCons;

	}

	/**
	 * Method Name: getMedicalConsenterIdForEvent Method Description:Get the
	 * medical consenter id based on the medical consenter creation event id.
	 * 
	 * @param ulIdEvent
	 * @return Long @
	 */
	@Override
	public Long getMedicalConsenterIdForEvent(Long ulIdEvent) {
		Long ulIdMedCons = ServiceConstants.ZERO_VAL;
		List<MedicalConsenter> IdMedCon = null;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MedicalConsenter.class);

		criteria.add(Restrictions.eq("event.idEvent", ulIdEvent));
		IdMedCon = criteria.list();

		if (TypeConvUtil.isNullOrEmpty(IdMedCon)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		} else {
			ulIdMedCons = IdMedCon.get(ServiceConstants.Zero).getIdMedConsenter();
		}
		return ulIdMedCons;
	}

	/**
	 * Method Name: isPersonMedicalConsenterType Method Description:check if the
	 * person is already Medical Consenter type.
	 * 
	 * @param idPerson
	 * @param idCase
	 * @param idChild
	 * @return Long @
	 */
	@Override
	public Long isPersonMedicalConsenterType(Long idPerson, Long idCase, Long idChild) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(isPersonMedicalConsenterTypeSql)
				.addScalar("cdMedConsenterType", StandardBasicTypes.STRING)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(MedicalConsenterDto.class));
		query.setParameter("idMedConPerson", idPerson);
		query.setParameter("idPerson", idChild);
		query.setParameter("idCase", idCase);
		List<MedicalConsenterDto> returnBeanList = query.list();
		if (TypeConvUtil.isNullOrEmpty(returnBeanList) || returnBeanList.size() <= ServiceConstants.Zero) {
			// throw new
			// DataNotFoundException(messageSource.getMessage("Common.noRecordFound",
			// null, Locale.US));
			return 0l;
		}
		return returnBeanList.get(ServiceConstants.Zero).getIdEvent();
	}

	/**
	 * Method Name: updateEndDateConsenterRecord Method Description: update end
	 * Date Medical Consenter record specific to stage
	 * 
	 * @param medConsBean
	 * @return Long @
	 */
	@Override
	public Long updateEndDateConsenterRecord(MedicalConsenterDto medConsBean) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MedicalConsenter.class);

		if (medConsBean.getIdPerson() > ServiceConstants.Zero) {
			criteria.add(Restrictions.eq("personByIdPerson.idPerson", medConsBean.getIdPerson()));
		} else {
			criteria.add(Restrictions.eq("personByIdPerson.idPerson", null));
		}

		if (medConsBean.getIdEvent() > ServiceConstants.Zero) {
			criteria.add(Restrictions.eq("event.idEvent", medConsBean.getIdEvent()));
		} else {
			criteria.add(Restrictions.eq("event.idEvent", null));
		}

		criteria.add(Restrictions.or(Restrictions.isNull("dtMedConsEnd"), Restrictions.gt("dtMedConsEnd", new Date())));

		List<MedicalConsenter> medicalConsenterList = criteria.list();
		if (!TypeConvUtil.isNullOrEmpty(medicalConsenterList)) {
			for (MedicalConsenter medicalConsenter : medicalConsenterList) {
				medicalConsenter.setDtMedConsEnd(medConsBean.getDtMedConsEnd());
				sessionFactory.getCurrentSession().saveOrUpdate(medicalConsenter);
			}

		} else {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}

		return medConsBean.getIdPerson();
	}

	/**
	 * Method Name: getCorrespStage Method Description:To retrieve a
	 * corresponding Stage for a given SUB or ADO stages.
	 * 
	 * @param medBean
	 * @return Long @
	 */
	@Override
	public Long getCorrespStage(MedicalConsenterDto medBean) {
		BigDecimal idStageToSynch = BigDecimal.ZERO;
		String stageType = ServiceConstants.EMPTY_STRING;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Stage.class);
		if (medBean.getIdStage() > ServiceConstants.Zero) {
			criteria.add(Restrictions.eq("idStage", medBean.getIdStage()));
		}
		List<Stage> stageList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(stageList)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		} else {
			stageType = stageList.get(ServiceConstants.Zero).getCdStage();
			if (!TypeConvUtil.isNullOrEmpty(stageType) && !(stageType.equals(ServiceConstants.EMPTY_STRING))) {
				if (stageType.equals(ServiceConstants.SUB_CARE)) {
					Query query = sessionFactory.getCurrentSession().createSQLQuery(getCorrespStageAodStageSynchSql);
					query.setParameter("idStage", medBean.getIdStage());
					idStageToSynch = (BigDecimal) query.uniqueResult();
				} else if (stageType.equals(ServiceConstants.ADOPTION)) {
					Query query = sessionFactory.getCurrentSession().createSQLQuery(getCorrespStageSubStageSynchSql);
					query.setParameter("idStage", medBean.getIdStage());
					idStageToSynch = (BigDecimal) query.uniqueResult();
				}

			}

		}
		if (null == idStageToSynch) {
			return ServiceConstants.ZERO_VAL;
		}
		return idStageToSynch.longValue();
	}

	/**
	 * Method Name: hasPersonAddrZip Method Description:Checks if the person
	 * being added as a Medical Censenter have atleast one Zip code in any of
	 * its addresses.
	 * 
	 * @param idPerson
	 * @return Boolean @
	 */
	@Override
	public Boolean hasPersonAddrZip(Long idPerson) {
		Boolean addrZipPresent = ServiceConstants.FALSEVAL;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(hasPersonAddrZipSql);
		query.setParameter("idPerson", idPerson);
		BigDecimal count = (BigDecimal) query.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(count) && count.intValue() > ServiceConstants.Zero) {
			addrZipPresent = ServiceConstants.TRUEVAL;
		}
		return addrZipPresent;
	}

	/**
	 * Method Name: indToDoExists Method Description: Get boolean indicating if
	 * Primary Child for Case has both Primary and Backup Medical Consenters.
	 * 
	 * @param idStage
	 * @param idCase
	 * @param cd_todo_type
	 * @param txt_Todo_Desc
	 * @return Boolean @
	 */
	@Override
	public Boolean indToDoExists(Long idStage, Long idCase, String cd_todo_type, String txt_Todo_Desc) {
		Boolean indToDoExists = ServiceConstants.FALSEVAL;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(indToDoExistsSql);
		query.setParameter("ulIdStage", idStage);
		query.setParameter("ulIdCase", idCase);
		query.setParameter("cd_todo_type", cd_todo_type);
		query.setParameter("txt_Todo_Desc", txt_Todo_Desc);
		BigDecimal count = (BigDecimal) query.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(count) && count.intValue() > ServiceConstants.Zero) {
			indToDoExists = ServiceConstants.TRUEVAL;
		}
		return indToDoExists;
	}

	/**
	 * Method Name:checkMCPBExist Method Description:Method returns true if
	 * Primary child for the given stage has atleast one Primary and one Backup
	 * MC's that are court authorized.
	 * 
	 * @param idStage
	 * @param idChild
	 * @return Boolean @
	 */
	@Override
	public Boolean checkMCPBExist(Long idStage, Long idChild) {
		Boolean indMCExists = ServiceConstants.FALSEVAL;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(checkMCPBExistSql);
		query.setParameter("idEventStage", idStage);
		query.setParameter("idPerson", idChild);
		BigDecimal count = (BigDecimal) query.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(count) && count.intValue() > ServiceConstants.Zero) {
			indMCExists = ServiceConstants.TRUEVAL;
		}
		return indMCExists;
	}

	/**
	 * Method Name: checkMCCourtAuth Method Description:Method returns true if
	 * Primary child for the given stage has atleast one Primary and one Backup
	 * MC's that are court authorized.
	 * 
	 * @param idStage
	 * @param ulIdRelatedStage
	 * @param idChild
	 * @return Boolean @
	 */
	@Override
	public Boolean checkMCCourtAuth(Long idStage, Long ulIdRelatedStage, Long idChild) {
		Boolean indMCCourthAuth = ServiceConstants.FALSEVAL;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(checkMCCourtAuthSql);
		query.setParameter("ulIdStage", idStage);
		query.setParameter("ulIdRelatedStage", idStage);
		query.setParameter("ulIdChild", idChild);
		BigDecimal count = (BigDecimal) query.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(count) && count.intValue() > ServiceConstants.Zero) {
			indMCCourthAuth = ServiceConstants.TRUEVAL;
		}
		return indMCCourthAuth;
	}

	/**
	 * Method Name: isActiveMedCons Method Description:isActiveMedCons returns
	 * count of number of active medical consenters for the person id.
	 * 
	 * @param idPerson
	 * @return Boolean @
	 */
	@Override
	public Boolean isActiveMedCons(Long idPerson) {

		Boolean hasActiveMedCons = ServiceConstants.FALSEVAL;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MedicalConsenter.class);
		criteria.add(Restrictions.or(Restrictions.isNull("dtMedConsEnd"), Restrictions.gt("dtMedConsEnd", new Date())));
		criteria.add(Restrictions.eq("personByIdMedConsenterPerson.idPerson", idPerson));
		criteria.setProjection(Projections.rowCount());
		Long count = (Long) criteria.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(count) && count > ServiceConstants.ZERO_VAL) {
			hasActiveMedCons = ServiceConstants.TRUEVAL;
		}
		return hasActiveMedCons;
	}

	/**
	 * Method Name: getPersonNameSfx Method Description:Method to get the Person
	 * Name Suffix
	 * 
	 * @param medicalConsenterDto
	 * @return MedicalConsenterDto @
	 */
	@Override
	public MedicalConsenterDto getPersonNameSfx(MedicalConsenterDto medicalConsenterDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Person.class);
		criteria.add(Restrictions.eq("idPerson", medicalConsenterDto.getIdMedConsenterPerson()));

		Person person = (Person) criteria.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(person)) {
			medicalConsenterDto.setCdNameSuffix(person.getCdPersonSuffix());
			medicalConsenterDto.setNmNameFirst(person.getNmPersonFirst());
			medicalConsenterDto.setNmNameLast(person.getNmPersonLast());
			medicalConsenterDto.setNmNameMiddle(person.getNmPersonMiddle());
		} else {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));

		}

		return medicalConsenterDto;

	}

	/**
	 * 
	 * Method Name: audMdclConsenterFormLog Method Description: This method is
	 * to save Medical Consenter forms(Designation of Medical Consenter Form
	 * 2085-B English & Spanish) status and doc_type in the Log
	 * table(MED_CNSNTR_FORM_LOG) when the Form launched using launch button in
	 * detail page, If Save and Complete button is clicked in Detail page update
	 * the status as 'COMP', If Delete button clicked delete the record.
	 *
	 * @param medicalConsenterFormLogReq
	 * @return CommonStringRes
	 */
	@Override
	public CommonStringRes audMdclConsenterFormLog(MedicalConsenterFormLogReq medicalConsenterFormLogReq) {
		CommonStringRes commonStringRes = new CommonStringRes();
		List<MedCnsntrFormLog> medCnsntrFormLogs = null;
		switch (medicalConsenterFormLogReq.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			// update
			MedCnsntrFormLog cnsntrFormLog = new MedCnsntrFormLog();
			MedCnsntrFormLog currentCnsntrFormLog = null;
			// If - for Save & Complete click make all records to 'COMP' for the
			// given IdMedConsenter
			if (ServiceConstants.CRPTRINT_SC
					.equals(medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getBtnName())) {
				medCnsntrFormLogs = getMedCnsntrFormLogList(
						medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdMedConsenter());
				for (MedCnsntrFormLog medCnsntrFormLog : medCnsntrFormLogs) {
					medCnsntrFormLog.setCdFormStatus(ServiceConstants.CEVTSTAT_COMP);
					medCnsntrFormLog.setDtLastUpdate(new Date());
					sessionFactory.getCurrentSession().merge(medCnsntrFormLog);
				}
				commonStringRes.setCommonRes(ServiceConstants.FORM_SUCCESS);
			} else if (!ObjectUtils
					.isEmpty(medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdMedCnsntrFormLog())) {
				cnsntrFormLog.setIdMedCnsntrFormLog(
						medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdMedCnsntrFormLog());
				currentCnsntrFormLog = (MedCnsntrFormLog) sessionFactory.getCurrentSession().get(MedCnsntrFormLog.class,
						cnsntrFormLog.getIdMedCnsntrFormLog());
				cnsntrFormLog.setDtCreated(currentCnsntrFormLog.getDtCreated());
				cnsntrFormLog.setIdCreatedPerson(currentCnsntrFormLog.getIdCreatedPerson());
				cnsntrFormLog.setTxtDocType(currentCnsntrFormLog.getTxtDocType());
				cnsntrFormLog.setIdMedConsenter(currentCnsntrFormLog.getIdMedConsenter());
				cnsntrFormLog.setIdLastUpdatePerson(
						medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdLastupdatePerson());
				if (!ServiceConstants.CEVTSTAT_COMP.equals(currentCnsntrFormLog.getCdFormStatus())) {
					cnsntrFormLog.setCdFormStatus(
							medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getCdFormStatus());
				}
				cnsntrFormLog.setDtLastUpdate(new Date());
				sessionFactory.getCurrentSession().merge(cnsntrFormLog);
				commonStringRes.setCommonRes(ServiceConstants.FORM_SUCCESS);
			} else {
				// insert
				MedCnsntrFormLog medCnsntrFormLog = new MedCnsntrFormLog();
				medCnsntrFormLog
						.setCdFormStatus(medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getCdFormStatus());
				medCnsntrFormLog.setDtCreated(new Date());
				medCnsntrFormLog.setDtLastUpdate(new Date());
				medCnsntrFormLog.setIdCreatedPerson(
						medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdCreatedPerson());
				medCnsntrFormLog.setIdLastUpdatePerson(
						medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdLastupdatePerson());
				medCnsntrFormLog
						.setTxtDocType(medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getTxtDocType());

				if (!ObjectUtils
						.isEmpty(medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdMedConsenter())) {
					MedicalConsenter medicalConsenter = new MedicalConsenter();
					medicalConsenter.setIdMedConsenter(
							medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdMedConsenter());
					medCnsntrFormLog.setIdMedConsenter(medicalConsenter);
				}
				sessionFactory.getCurrentSession().saveOrUpdate(medCnsntrFormLog);
				commonStringRes.setCommonRes(ServiceConstants.FORM_SUCCESS);
			}
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			// delete all PROC for given idMedConsenter
			medCnsntrFormLogs = getMedCnsntrFormLogList(
					medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdMedConsenter());
			for (MedCnsntrFormLog medCnsntrFormLog : medCnsntrFormLogs) {
				if (ServiceConstants.EVENTSTATUS_PROCESS.equals(medCnsntrFormLog.getCdFormStatus())) {
					// delete form log
					sessionFactory.getCurrentSession().delete(medCnsntrFormLog);
					// delete form
					Criteria cr = sessionFactory.getCurrentSession().createCriteria(MedicalConsenterNarr.class);
					cr.add(Restrictions.eq("idEvent",
							medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdEvent()));
					MedicalConsenterNarr medicalConsenterNarr = (MedicalConsenterNarr) cr.uniqueResult();
					if (!ObjectUtils.isEmpty(medicalConsenterNarr)) {
						sessionFactory.getCurrentSession().delete(medicalConsenterNarr);
					}
					//delete handwritten
					Criteria handwritten = sessionFactory.getCurrentSession().createCriteria(HandwrittenData.class);
					handwritten.add(Restrictions.eq("idEvent",
							medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdEvent()));
					List<HandwrittenData> handwrittenDatas = (List<HandwrittenData>) handwritten.list();
					if (!CollectionUtils.isEmpty(handwrittenDatas)) {
						handwrittenDatas.forEach(handwrittenData ->{
							sessionFactory.getCurrentSession().delete(handwrittenData);
						});
					}
					commonStringRes.setCommonRes(ServiceConstants.FORM_SUCCESS);
				}
			}
			break;
		}

		return commonStringRes;
	}

	/**
	 * 
	 * Method Name: getMedCnsntrFormLogList Method Description: method to get
	 * MedCnsntrFormLog list for the given idMedConsenter from the
	 * MED_CNSNTR_FORM_LOG table
	 * 
	 * @param idMedConsenter
	 * @return List<MedCnsntrFormLog>
	 */
	private List<MedCnsntrFormLog> getMedCnsntrFormLogList(Long idMedConsenter) {
		List<MedCnsntrFormLog> medCnsntrFormLogs = null;

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(MedCnsntrFormLog.class);
		cr.add(Restrictions.eq("idMedConsenter.idMedConsenter", idMedConsenter));
		medCnsntrFormLogs = cr.list();

		return medCnsntrFormLogs;
	}

	/**
	 * 
	 * Method Name: getMdclConsenterFormLogList Method Description: This method
	 * is to get the list to display forms(Designation of Medical Consenter Form
	 * 2085-B English & Spanish) associated with the Medical Consenters in the
	 * Medical Consenter List page
	 *
	 * @param medicalConsenterFormLogReq
	 * @return MedicalConsenterRes
	 */
	@Override
	public MedicalConsenterRes getMdclConsenterFormLogList(MedicalConsenterFormLogReq medicalConsenterFormLogReq) {

		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		List<MedicalConsenterFormLogDto> medicalConsenterFormLogDtos = new ArrayList<MedicalConsenterFormLogDto>();
		medicalConsenterFormLogDtos = sessionFactory.getCurrentSession().createCriteria(MedCnsntrFormLog.class)
				.setProjection(Projections.projectionList().add(Projections.property("cdFormStatus"), "cdFormStatus")
						.add(Projections.property("idMedCnsntrFormLog"), "idMedCnsntrFormLog")
						.add(Projections.property("idMedConsenter.idMedConsenter"), "idMedConsenter")
						.add(Projections.property("txtDocType"), "txtDocType")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("idLastUpdatePerson"), "idLastupdatePerson")
						.add(Projections.property("idCreatedPerson"), "idCreatedPerson")
						.add(Projections.property("dtCreated"), "dtCreated"))
				.addOrder(Order.desc("dtLastUpdate"))
				.add(Restrictions.in("idMedConsenter", medicalConsenterFormLogReq.getIdMedConsenter()))
				.setResultTransformer(Transformers.aliasToBean(MedicalConsenterFormLogDto.class)).list();

		medicalConsenterRes.setMedicalConsenterFormLogDtos(medicalConsenterFormLogDtos);

		return medicalConsenterRes;

	}

	@Override
	public CommonStringRes updateMdclConsenterFormLog(MedicalConsenterFormLogReq medicalConsenterFormLogReq) {

		CommonStringRes commonStringRes = new CommonStringRes();
		MedCnsntrFormLog medCnsntrFormLog = null;
		MedicalConsenter medicalConsenter = new MedicalConsenter();
		Criteria criteriaMedCnsntrFormLog = sessionFactory.getCurrentSession().createCriteria(MedCnsntrFormLog.class);
		Criteria criteriaMedicalConsenter = sessionFactory.getCurrentSession().createCriteria(MedicalConsenter.class);
		criteriaMedicalConsenter.add(Restrictions.eq("event.idEvent", medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdEvent()));
		medicalConsenter = (MedicalConsenter) criteriaMedicalConsenter.uniqueResult();
		if (!ObjectUtils.isEmpty(medicalConsenter)) {
			criteriaMedCnsntrFormLog.add(Restrictions.eq("idMedConsenter", medicalConsenter));
			criteriaMedCnsntrFormLog.add(Restrictions.eq("txtDocType",
					medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getTxtDocType().toLowerCase()));
			if (!ObjectUtils.isEmpty(criteriaMedCnsntrFormLog.uniqueResult())) {
				medCnsntrFormLog = (MedCnsntrFormLog) criteriaMedCnsntrFormLog.uniqueResult();
			}
		}

		if (ObjectUtils.isEmpty(medCnsntrFormLog)) {
			medCnsntrFormLog = new MedCnsntrFormLog();
			medCnsntrFormLog.setDtCreated(new Date());
			medCnsntrFormLog.setDtLastUpdate(new Date());
			medCnsntrFormLog.setTxtDocType(medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getTxtDocType());
			if (!ObjectUtils.isEmpty(medicalConsenter)) {
				medCnsntrFormLog.setIdMedConsenter(medicalConsenter);
			}
			//Warranty Defect#11833 - To insert id created person
			if(!ObjectUtils.isEmpty(medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdCreatedPerson())){
				medCnsntrFormLog.setIdCreatedPerson(Long.valueOf(medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdCreatedPerson()));
			}
			List<HandWritingValueDto> signatures = handWritingDao.fetchHandwrittenDataForEvent(
					medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdEvent(), ServiceConstants.FALSEVAL);

			if (signatures.size() == 7) {
				medCnsntrFormLog.setCdFormStatus(ServiceConstants.EVENT_COMP);
			} else {
				medCnsntrFormLog.setCdFormStatus(ServiceConstants.EVENT_PROC);
			}
			sessionFactory.getCurrentSession().save(medCnsntrFormLog);

		} else {
			List<HandWritingValueDto> signatures = handWritingDao.fetchHandwrittenDataForEvent(
					medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdEvent(), ServiceConstants.FALSEVAL);

			if (signatures.size() == 7) {
				medCnsntrFormLog.setCdFormStatus(ServiceConstants.EVENT_COMP);
			} else {
				medCnsntrFormLog.setCdFormStatus(ServiceConstants.EVENT_PROC);
			}
			medCnsntrFormLog.setDtLastUpdate(new Date());
			//Warranty Defect#11833 - To insert id created person
			if(!ObjectUtils.isEmpty(medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdLastupdatePerson())){
				medCnsntrFormLog.setIdLastUpdatePerson(Long.valueOf(medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdLastupdatePerson()));
			}
			sessionFactory.getCurrentSession().saveOrUpdate(medCnsntrFormLog);

		}

		return commonStringRes;
	}

	@Override
	public MedCnsntrFormLog getMedCnsntrFormLog(MedicalConsenterFormLogReq medicalConsenterFormLogReq) {
		MedCnsntrFormLog medCnsntrFormLog = new MedCnsntrFormLog();

		List<MedicalConsenter> medicalConsenter = new ArrayList<MedicalConsenter>();
		Criteria criteriaMedCnsntrFormLog = sessionFactory.getCurrentSession().createCriteria(MedCnsntrFormLog.class);
		Criteria criteriaMedicalConsenter = sessionFactory.getCurrentSession().createCriteria(MedicalConsenter.class);
		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
				medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getIdMedConsenter());
		criteriaMedicalConsenter.add(Restrictions.eq("personByIdMedConsenterPerson", person));
		medicalConsenter = (List<MedicalConsenter>) criteriaMedicalConsenter.list();
		if (!ObjectUtils.isEmpty(medicalConsenter)) {
			criteriaMedCnsntrFormLog.add(Restrictions.eq("idMedConsenter", medicalConsenter.get(0)));
			criteriaMedCnsntrFormLog.add(Restrictions.eq("txtDocType",
					medicalConsenterFormLogReq.getMedicalConsenterFormLogDto().getTxtDocType().toLowerCase()));
			medCnsntrFormLog = (MedCnsntrFormLog) criteriaMedCnsntrFormLog.uniqueResult();
		}

		return medCnsntrFormLog;
	}

}
