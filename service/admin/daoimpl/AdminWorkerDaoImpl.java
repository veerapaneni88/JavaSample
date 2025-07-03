package us.tx.state.dfps.service.admin.daoimpl;

import java.math.BigDecimal;
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

import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.service.admin.dao.AdminWorkerDao;
import us.tx.state.dfps.service.admin.dto.AdminWorkerInpDto;
import us.tx.state.dfps.service.admin.dto.AdminWorkerOutpDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonGenderSpanishDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:AdminWorkerDaoImpl Aug 9, 2017- 1:21:28 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class AdminWorkerDaoImpl implements AdminWorkerDao {
	@Autowired
	MessageSource messageSource;
	@Autowired
	LookupDao lookupDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${AdminWorkerDaoImpl.getWorkloadOnRole}")
	private String getWorkloadOnRole;

	@Value("${AdminWorkerDaoImpl.getWorkload}")
	private String getWorkload;

	@Value("${AdminWorkerDaoImpl.getInvStageOpenEvent}")
	private String getInvStageOpenEvent;

	@Value("${AdminWorkerDaoImpl.getPersonInRole}")
	private String getPersonInRole;

	private static final Logger log = Logger.getLogger(AdminWorkerDaoImpl.class);

	/**
	 * 
	 * Method Name: getWorkLoad Method Description: Get person work details
	 * 
	 * @param pInputDataRec
	 * @return List<AdminWorkerOutpDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AdminWorkerOutpDto> getWorkLoad(AdminWorkerInpDto pInputDataRec) {
		log.debug("Entering method getWorkLoad in AdminWorkerDaoImpl");
		List<AdminWorkerOutpDto> liCinv51doDto;
		if (pInputDataRec.getCdStagePersRole().equalsIgnoreCase(ServiceConstants.PRIMARY_ROLE)
				|| (pInputDataRec.getCdStagePersRole().equalsIgnoreCase(ServiceConstants.SECONDARY_ROLE))) {
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getWorkloadOnRole)
					.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
					.setParameter("idStage", pInputDataRec.getIdStage())
					.setParameter("cdStagePersRole", pInputDataRec.getCdStagePersRole())
					.setResultTransformer(Transformers.aliasToBean(AdminWorkerOutpDto.class)));
			liCinv51doDto = (List<AdminWorkerOutpDto>) sQLQuery1.list();
		} else {
			SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getWorkload)
					.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
					.setParameter("idStage", pInputDataRec.getIdStage())
					.setParameter("cdStagePersRole", pInputDataRec.getCdStagePersRole())
					.setResultTransformer(Transformers.aliasToBean(AdminWorkerOutpDto.class)));
			liCinv51doDto = (List<AdminWorkerOutpDto>) sQLQuery2.list();

		}

		if (TypeConvUtil.isNullOrEmpty(liCinv51doDto)) {
			throw new DataNotFoundException(messageSource.getMessage("person.not.found.role", null, Locale.US));
		}
		log.debug("Exiting method cinv51dQUERYdam in AdminWorkerDaoImpl");
		return liCinv51doDto;
	}

	@Override
	public AdminWorkerOutpDto getPersonInRole(AdminWorkerInpDto adminWorkerInpDto) {

		Criteria criteria = null;
		String szCdStagePersRole = adminWorkerInpDto.getCdStagePersRole();
		Long idPerson = null;

		if (ServiceConstants.CROLEALL_SE.equals(szCdStagePersRole)
				|| ServiceConstants.CROLEALL_PR.equals(szCdStagePersRole)) {

			SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(getPersonInRole);
			query.setParameter("idStage", adminWorkerInpDto.getIdStage());
			query.setParameter("cdStagePersonRole", adminWorkerInpDto.getCdStagePersRole());
			BigDecimal idWorkloadPerson = (BigDecimal) query.uniqueResult();
			if (!TypeConvUtil.isNullOrEmpty(idWorkloadPerson)) {
				idPerson = idWorkloadPerson.longValue();
			}

		} else {

			criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class)
					.add(Restrictions.eq("idStage", adminWorkerInpDto.getIdStage()))
					.add(Restrictions.eq("cdStagePersRole", adminWorkerInpDto.getCdStagePersRole()));
			StagePersonLink stagePersonLink = (StagePersonLink) criteria.uniqueResult();
			if (!TypeConvUtil.isNullOrEmpty(stagePersonLink)) {
				idPerson = stagePersonLink.getIdPerson();
			}

		}

		AdminWorkerOutpDto resultCinv51doDto = new AdminWorkerOutpDto();

		if (idPerson != null) {
			// Both statements select exactly one column, so just get the first
			// one.
			resultCinv51doDto.setIdTodoPersAssigned(idPerson);
		}

		return resultCinv51doDto;

	}

	/**
	 * Method Name: getInvStageOpenEvent Method Description: This method
	 * retrieves Event associated with Opening of Investigation Stage.
	 * 
	 * @param idCase
	 * @return EventDto
	 */
	@Override
	public Event getInvStageOpenEvent(long idCase) {
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getInvStageOpenEvent)
				.setResultTransformer(Transformers.aliasToBean(EventDto.class)));
		query.setLong("idCase", idCase);
		query.addScalar("idEvent", StandardBasicTypes.LONG);
		query.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		query.addScalar("idStage", StandardBasicTypes.LONG);
		query.addScalar("cdEventType", StandardBasicTypes.STRING);
		query.addScalar("idCase", StandardBasicTypes.LONG);
		query.addScalar("idPerson", StandardBasicTypes.LONG);
		query.addScalar("cdTask", StandardBasicTypes.STRING);
		query.addScalar("eventDescr", StandardBasicTypes.STRING);
		query.addScalar("dtEventOccurred", StandardBasicTypes.DATE);
		query.addScalar("cdEventStatus", StandardBasicTypes.STRING);
		query.addScalar("dtEventCreated", StandardBasicTypes.DATE);
		query.addScalar("dtEventModified", StandardBasicTypes.DATE);

		EventDto eventDto = (EventDto) query.uniqueResult();
		Event event = new Event();
		org.springframework.beans.BeanUtils.copyProperties(eventDto, event);
		Stage stage = new Stage();
		Person person = new Person();
		stage.setIdStage(eventDto.getIdStage());
		person.setIdPerson(eventDto.getIdPerson());
		event.setStage(stage);
		event.setPerson(person);

		return event;
	}

	/**
	 * Method Name: retrieveStageInfo Method Description:This method retrieves
	 * information from Stage table using idStage.
	 * 
	 * @param idARStage
	 * @return Stage
	 */
	public Stage retrieveStageInfo(long idARStage) {
		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, idARStage);
		return stage;
	}

	@Override
	public AdminWorkerOutpDto getVictim(AdminWorkerInpDto adminWorkerInpDto) {
		Criteria criteria = null;
		Long idPerson = null;
		AdminWorkerOutpDto adminWorkerOutpDto = new AdminWorkerOutpDto();
		criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class)
				.add(Restrictions.eq("idStage", adminWorkerInpDto.getIdStage()));
		List<StagePersonLink> stagePersonLinkList = criteria.list();

		if (!ObjectUtils.isEmpty(stagePersonLinkList)) {
			for (StagePersonLink stagePersonLink : stagePersonLinkList) {
				if (ServiceConstants.STAGE_PERS_ROLE_VC.equals(stagePersonLink.getCdStagePersRole())||
						ServiceConstants.STAGE_PERS_ROLE_VP.equals(stagePersonLink.getCdStagePersRole()) ||
						ServiceConstants.STAGE_PERS_ROLE_CL.equals(stagePersonLink.getCdStagePersRole())) {

					/*Person person = personDao.getPerson(stagePersonLink.getIdPerson());
					StageDto stageDto = stageDao.getStageById(adminWorkerInpDto.getIdStage());

					if(person !=null && person.getNmPersonFull().equalsIgnoreCase(stageDto.getNmStage())){
						adminWorkerOutpDto.setIdTodoPersAssigned(stagePersonLink.getIdPerson());
						break;
                    }*/
					if(ServiceConstants.PERSON_TYPE_PRINCIPAL.equalsIgnoreCase(stagePersonLink.getCdStagePersType())
							&& ServiceConstants.SELF.equalsIgnoreCase(stagePersonLink.getCdStagePersRelInt())){
						adminWorkerOutpDto.setIdTodoPersAssigned(stagePersonLink.getIdPerson());
						break;
					}
				}
			}
		}
		return adminWorkerOutpDto;
	}

}
