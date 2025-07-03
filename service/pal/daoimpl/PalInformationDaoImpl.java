package us.tx.state.dfps.service.pal.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Pal;
import us.tx.state.dfps.common.domain.PalService;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.Unit;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.PlacementValueDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dao.FetchStageDao;
import us.tx.state.dfps.service.admin.dao.SvcAuthEventLinkDao;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkOutDto;
import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.UpdtPalServiceTrainingReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.pal.dao.PalInformationDao;
import us.tx.state.dfps.service.pal.dto.PALSummaryDto;
import us.tx.state.dfps.service.pal.dto.PalInformationDto;
import us.tx.state.dfps.service.pal.dto.PalServiceTrainingDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.workload.dao.StageWorkloadDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.EventStagePersonDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.SVCAuthDetailDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Apr 20, 2018- 4:49:26 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class PalInformationDaoImpl implements PalInformationDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	StageWorkloadDao stageWorkloadDao;

	@Autowired
	WorkLoadDao workLoadDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	FetchStageDao fetchStageDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	SvcAuthEventLinkDao svcAuthEventLinkDao;

	@Autowired
	ServiceAuthorizationDao serviceAuthorizationDao;

	@Value("${PalInformationDao.getActualDischarge}")
	private String getActualDischargeSql;

	@Value("${PalInformationDao.getIdStagePersonLinkData}")
	private String getIdStagePersonLinkDataSql;

	@Value("${PalInformationDao.getPALCoordinatorID}")
	private String getPALCoordinatorIDSql;

	private static final String PAL_UNIT_SPECIALIZATION = "PAL";

	private static final String CD_ACT_PLANNED = "A";
	private static final String SERVICE_AUTH_TASK_CODE = "3520";
	private static final String SERVICE_AUTH_EVENT_TYPE_CODE = "AUT";
	private static final int MSG_SYS_STAGE_CLOSED = 8164;

	/**
	 * Method Name: getPal Method Description: Fetches the data from Pal Table
	 * 
	 * @param idStage
	 */
	@Override
	public PalInformationDto getPal(Long idStage) {

		return (PalInformationDto) sessionFactory.getCurrentSession().createCriteria(Pal.class)
				.setProjection(Projections.projectionList().add(Projections.property("idPalStage"), "idStage")
						.add(Projections.property("dtLastUpdate"), "dtAssessmentLastUpdate")
						.add(Projections.property("cdPalCloseLivArr"), "cdPalCloseLivArr")
						.add(Projections.property("dtPalPostasmtDate"), "dtPostAssessment")
						.add(Projections.property("dtPalPreasmtDate"), "dtPreAssessment")
						.add(Projections.property("indPalIlNoIlsAssmt"), "noIlsAssessment")
						.add(Projections.property("indPalIlNoPoasmtScre"), "postAssessmentNoScore")
						.add(Projections.property("indPalIlNoPrasmtScre"), "preAssessmentNoScore")
						.add(Projections.property("nbrPalPostasmtScore"), "postAssessmentScore")
						.add(Projections.property("nbrPalPreasmtScore"), "preAssessmentScore")
						.add(Projections.property("txtPalIlNoIlsRsn"), "noIlsReason")
						.add(Projections.property("cdNoIlsReason"), "noIlsAssessReason")
						.add(Projections.property("dtTrainingCmpltd"), "dtTraingCompleted")
						.add(Projections.property("txtSummaryComments"), "txtSummaryComments"))
				.add(Restrictions.eq("idPalStage", idStage))
				.setResultTransformer(Transformers.aliasToBean(PalInformationDto.class)).uniqueResult();

	}

	/**
	 * Method Name: retrievePalInformation Method Description: Retrieves the
	 * Summary Information from the Pal Table
	 * 
	 * @param idStage
	 * @param idPerson
	 * @param idEvent
	 * @param palSummaryDto
	 */

	@Override
	public PALSummaryDto retrievePalInformation(Long idStage, Long idPerson, Long idEvent,
			PALSummaryDto palSummaryDto) {

		/*
		 * CSES42D Description - This dam will do a full row retrieval form the
		 * PAL table. It will take as input ID_STAGE and return only one row.
		 */

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Pal.class);
		List<Date> dtLastUpdateList = new ArrayList<>();
		criteria.add(Restrictions.eq("stage.idStage", idStage));
		Pal pal = (Pal) criteria.uniqueResult();

		if (!ObjectUtils.isEmpty(pal)) {
			palSummaryDto.setCdPalCloseLivArr(pal.getCdPalCloseLivArr());
			Date dtLastUpdate = pal.getDtLastUpdate();
			dtLastUpdateList.add(dtLastUpdate);
			palSummaryDto.setSysIndPalIlsAssmt(ServiceConstants.BOOLEAN_TRUE);
		} else {
			palSummaryDto.setSysIndPalIlsAssmt(ServiceConstants.BOOLEAN_FALSE);
		}

		/*
		 * CCMNG2D dam will receive ID_STAGE from the service and return the
		 * associated record from STAGE PERSON LINK table where the staff
		 * person's role is primary (PR). This dam will only return one row.
		 */

		StagePersonLinkDto stagePersonLinkDto = stageWorkloadDao.getStagePersonLinkByStageRole(idStage);
		if (!ObjectUtils.isEmpty(stagePersonLinkDto)) {
			if (!ObjectUtils.isEmpty(stagePersonLinkDto.getIdPerson())
					&& stagePersonLinkDto.getIdPerson().equals(idPerson)) {
				palSummaryDto.setSysIndPrimaryWorker(ServiceConstants.BOOLEAN_TRUE);
			} else {
				palSummaryDto.setSysIndPrimaryWorker(ServiceConstants.BOOLEAN_FALSE);
			}
		}
		/*
		 * Call the Check for Unit Approval Dam - CSES45D Description - This DAM
		 * will retrieve a full row from the UNIT table and will take as input
		 * ID_PERSON and CD UNIT SPECIALIZATION. It will return one row.
		 */

		Criteria unitCriteria = sessionFactory.getCurrentSession().createCriteria(Unit.class);
		unitCriteria.add(Restrictions.eq("idPerson", idPerson));
		unitCriteria.add(Restrictions.eq("cdUnitSpecialization", PAL_UNIT_SPECIALIZATION));
		Unit unit = (Unit) unitCriteria.uniqueResult();
		if (!ObjectUtils.isEmpty(unit)) {
			palSummaryDto.setSysIndPalLeadCoord(ServiceConstants.BOOLEAN_TRUE);
		} else {
			palSummaryDto.setSysIndPalLeadCoord(ServiceConstants.BOOLEAN_FALSE);
		}

		// This method will retrieve the ID PERSON for a given
		// role, for a given stage. It's used to find the primary worker for a
		// given
		// stage. Dam Name: CINV51D

		Long idToDoPerson = workLoadDao.getStagePersonIdByRole(idStage, ServiceConstants.PRIMARY_CHILD_ROLE);
		if (!ObjectUtils.isEmpty(idToDoPerson) && idToDoPerson > ServiceConstants.ZERO) {

			retrievePerson(palSummaryDto, idToDoPerson);
		}

		/*
		 * CSEC51D This DAM will return the most recent row off of the placement
		 * table for the id person passed in. Also cd_plcmt_act_ planned = A and
		 * cd_plcmt_removal_rsn must exist in cdischrg codes table.
		 */
		retreivePlacement(idEvent, palSummaryDto, dtLastUpdateList, idToDoPerson);

		/*
		 * CINT21D Description - This DAM will return one row from the stage
		 * table based upon the id_stage passed into it.
		 */

		StageDto stageDto = stageDao.getStageById(idStage);
		palSummaryDto.setCdStageReasonClosed(stageDto.getCdStageReasonClosed());
		Date dtStageClose = stageDto.getDtStageClose();
		palSummaryDto.setDtStageClose(dtStageClose);

		palSummaryDto.setDtLastUpdateList(dtLastUpdateList);

		/*
		 * Call the Stage Retrieval Dam - CCMN87D Description - Dynamically
		 * builds Select statement and retrieves all events that satisfy the
		 * criteria. Pass in an id stage, the event type, and the task code to
		 * retrieve all possible service auth event for the given stage.
		 */

		List<EventStagePersonDto> eventStagePersonDtoList = stageDao.getEventStagePersonListByAttributes(idStage,
				SERVICE_AUTH_TASK_CODE, SERVICE_AUTH_EVENT_TYPE_CODE);
		if (!ObjectUtils.isEmpty(eventStagePersonDtoList)) {
			retreiveSvcAuth(palSummaryDto, eventStagePersonDtoList);
		}

		return palSummaryDto;
	}

	/**
	 * Method Name: retreivePlaceemnt Method Description:
	 * 
	 * @param idEvent
	 * @param palSummaryDto
	 * @param dtLastUpdateList
	 * @param idToDoPerson
	 */
	private void retreivePlacement(Long idEvent, PALSummaryDto palSummaryDto, List<Date> dtLastUpdateList,
			Long idToDoPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getActualDischargeSql)
				.addScalar("dtPlcmtEnd", StandardBasicTypes.DATE).setParameter("idPlcmtChild", idToDoPerson)
				.setParameter("cdPlcmtActPlanned", CD_ACT_PLANNED)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		PlacementValueDto placementValueDto = (PlacementValueDto) query.uniqueResult();

		if (!ObjectUtils.isEmpty(placementValueDto)) {
			palSummaryDto.setSysIndDischargeDate(ServiceConstants.BOOLEAN_TRUE);
		} else {
			palSummaryDto.setSysIndDischargeDate(ServiceConstants.BOOLEAN_FALSE);
		}
		if (idEvent != 0) {

			// CCMN45D: This DAM will return one row from the event table
			// based upon the id_event passed into it.

			EventDto eventDto = eventDao.getEventByid(idEvent);
			if (!ObjectUtils.isEmpty(eventDto)) {
				palSummaryDto.setCdEventStatus(eventDto.getCdEventStatus());
				palSummaryDto.setCdTask(eventDto.getCdTask());
				dtLastUpdateList.add(eventDto.getDtLastUpdate());
			}
		}
	}

	/**
	 * Method Name: retreiveSvcAuth Method Description:
	 * 
	 * @param palSummaryDto
	 * @param eventStagePersonDtoList
	 */
	private void retreiveSvcAuth(PALSummaryDto palSummaryDto, List<EventStagePersonDto> eventStagePersonDtoList) {
		palSummaryDto.setSysIndPalSvcAuth(ServiceConstants.BOOLEAN_FALSE);
		eventStagePersonDtoList.forEach(eventStagePerson -> {
			if ((CodesConstant.CEVTSTAT_COMP).equals(eventStagePerson.getCdEventStatus())
					|| (CodesConstant.CEVTSTAT_APRV).equals(eventStagePerson.getCdEventStatus())
					|| (CodesConstant.CEVTSTAT_PEND).equals(eventStagePerson.getCdEventStatus())) {

				/*
				 * Call the Stage Retrieval Dam - CSES24D Description - This DAM
				 * will retrieve a row from the SVC_AUTH_EVENT _LINK table based
				 * on ID EVENT.
				 */

				SvcAuthEventLinkInDto svcAuthEventLinkInDto = new SvcAuthEventLinkInDto();
				svcAuthEventLinkInDto.setIdSvcAuthEvent(eventStagePerson.getIdEvent());
				List<SvcAuthEventLinkOutDto> svcAuthEventLinkOutDtoList = svcAuthEventLinkDao
						.getAuthEventLink(svcAuthEventLinkInDto);
				if (!ObjectUtils.isEmpty(svcAuthEventLinkOutDtoList)) {
					retreiveServiceAuthDetails(palSummaryDto, svcAuthEventLinkOutDtoList);
				}
			}
		});
	}

	/**
	 * Method Name: retreiveServiceAuthDetails Method Description:
	 * 
	 * @param palSummaryDto
	 * @param svcAuthEventLinkOutDtoList
	 */
	private void retreiveServiceAuthDetails(PALSummaryDto palSummaryDto,
			List<SvcAuthEventLinkOutDto> svcAuthEventLinkOutDtoList) {
		svcAuthEventLinkOutDtoList.get(0).getIdSvcAuth();

		/*
		 * Call the Stage Retrieval Dam - CLSS24D Description - This DAM selects
		 * all rows from the svc_auth_detail with id_svc_auth as input.
		 */

		Long idSvcAuth = svcAuthEventLinkOutDtoList.get(0).getIdSvcAuth();
		List<SVCAuthDetailDto> svcAuthDetailDtoList = serviceAuthorizationDao.getSVCAuthDetailDtoById(idSvcAuth);
		if (!ObjectUtils.isEmpty(svcAuthDetailDtoList)) {
			svcAuthDetailDtoList.forEach(svcAuthDetailDto -> {
				//Added null condition check for warranty defect 12235
				if (!ObjectUtils.isEmpty(svcAuthDetailDto.getDtSvcAuthDtlTerm())) {
					Double dateDiff = DateUtils.daysDifference(svcAuthDetailDto.getDtSvcAuthDtlTerm(),
							palSummaryDto.getDtSystemDate());
					if (!ObjectUtils.isEmpty(dateDiff) && dateDiff > ServiceConstants.DoubleZero) {
						palSummaryDto.setSysIndPalSvcAuth(ServiceConstants.BOOLEAN_TRUE);
					}
				}
			});

		}
	}

	/**
	 * Method Name: retrievePerson Method Description:
	 * 
	 * @param palSummaryDto
	 * @param idToDoPerson
	 */
	private void retrievePerson(PALSummaryDto palSummaryDto, Long idToDoPerson) {
		// CCMN44D This DAM will return a single row from the
		// person table based upon the ID_PERSON passed in.

		PersonDto personDto = personDao.getPersonById(idToDoPerson);
		if (!ObjectUtils.isEmpty(personDto)) {
			Long age = (long) DateUtils.getAge(personDto.getDtPersonBirth());
			if (age >= 18) {
				palSummaryDto.setSysIndPalOverEighteen(ServiceConstants.BOOLEAN_TRUE);
			} else {
				palSummaryDto.setSysIndPalOverEighteen(ServiceConstants.BOOLEAN_FALSE);
			}
			if (ServiceConstants.MERGED_STATUS.equals(personDto.getCdPersonStatus())) {
				palSummaryDto.setSysIndPalStageMerged(ServiceConstants.BOOLEAN_TRUE);
			} else {
				palSummaryDto.setSysIndPalStageMerged(ServiceConstants.BOOLEAN_FALSE);
			}
		} else {
			palSummaryDto.setSysIndPalOverEighteen(ServiceConstants.BOOLEAN_FALSE);
			palSummaryDto.setSysIndPalStageMerged(ServiceConstants.BOOLEAN_FALSE);
		}
	}

	/**
	 * Method Name: getPalServiceTrainings Method Description: Fetches the
	 * PalServiceTraining Information from the PalService Table
	 * 
	 * @param idStage
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PalServiceTrainingDto> getPalServiceTrainings(Long idStage) {
		List<PalService> palServiceTrainings = (List<PalService>) sessionFactory.getCurrentSession()
				.createCriteria(PalService.class).add(Restrictions.eq("stage.idStage", idStage))
				.addOrder(Order.desc("cdPalServiceCatgory")).addOrder(Order.asc("cdPalServiceType"))
				.addOrder(Order.desc("dtPalServiceDate")).list();
		List<PalServiceTrainingDto> res = new ArrayList<>();
		palServiceTrainings.stream().forEach(o -> {
			PalServiceTrainingDto dto = new PalServiceTrainingDto();
			BeanUtils.copyProperties(o, dto);
			dto.setIdStage(idStage);
			if (!ObjectUtils.isEmpty(o.getIdCreatedPerson())) {
				Person createdPerson = (Person) sessionFactory.getCurrentSession().load(Person.class,
						o.getIdCreatedPerson());
				dto.setNmEntrByFirst(createdPerson.getNmPersonFirst());
				dto.setNmEntrByLast(createdPerson.getNmPersonLast());
				dto.setNmEntrBysuff(createdPerson.getCdPersonSuffix());
			}
			res.add(dto);
		});
		return res;
	}

	/**
	 * Method Name: updtPalService Method Description: Updates the
	 * PalServiceTraining Details
	 * 
	 * @param idStage
	 */

	@Override
	public PalServiceTrainingDto updtPalService(UpdtPalServiceTrainingReq updtPalServiceTrainingReq) {
		PalService palService = getTrasiantPalService(updtPalServiceTrainingReq);
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(updtPalServiceTrainingReq.getReqFuncCd()))
			sessionFactory.getCurrentSession().persist(palService);
		else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(updtPalServiceTrainingReq.getReqFuncCd())) {
			sessionFactory.getCurrentSession().saveOrUpdate(palService);
		} else if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(updtPalServiceTrainingReq.getReqFuncCd()))
			sessionFactory.getCurrentSession().delete(palService);
		PalServiceTrainingDto dto = new PalServiceTrainingDto();
		BeanUtils.copyProperties(palService, dto);
		return dto;
	}

	/**
	 * Method Name: getTrasiantPalService Method Description: Method
	 * Description:
	 * 
	 * @param updtPalServiceTrainingReq
	 * @return PalService
	 */
	private PalService getTrasiantPalService(UpdtPalServiceTrainingReq updtPalServiceTrainingReq) {
		PalService palService;
		PalServiceTrainingDto palServiceTrainingDto = updtPalServiceTrainingReq.getPalServiceTrainingDto();
		if (ObjectUtils.isEmpty(updtPalServiceTrainingReq.getPalServiceTrainingDto().getIdPalService())) {
			palService = new PalService();
			Stage stage = new Stage();
			stage.setIdStage(palServiceTrainingDto.getIdStage());
			palService.setStage(stage);
			palService.setDtCreated(new Date());
			palService.setIdCreatedPerson(palServiceTrainingDto.getIdCreatedPerson());

		} else {
			palService = (PalService) sessionFactory.getCurrentSession().createCriteria(PalService.class)
					.add(Restrictions.eq("idPalService", palServiceTrainingDto.getIdPalService())).uniqueResult();
			Stage stage = (Stage) sessionFactory.getCurrentSession().load(Stage.class,
					palServiceTrainingDto.getIdStage());
			Date dtStageClose = stage.getDtStageClose();
			if ((ServiceConstants.REQ_FUNC_CD_DELETE.equals(updtPalServiceTrainingReq.getReqFuncCd())
					|| ServiceConstants.REQ_FUNC_CD_UPDATE.equals(updtPalServiceTrainingReq.getReqFuncCd()))
					&& ObjectUtils.isEmpty(palService)) {
				throw new DataLayerException("Time Mismatch error",
						Long.valueOf(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH), null);
			}
			if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(updtPalServiceTrainingReq.getReqFuncCd())
					|| ServiceConstants.REQ_FUNC_CD_ADD.equals(updtPalServiceTrainingReq.getReqFuncCd())) {

				if (!ObjectUtils.isEmpty(palServiceTrainingDto.getDtLastUpdate())
						&& palServiceTrainingDto.getDtLastUpdate().compareTo(palService.getDtLastUpdate()) != 0) {
					throw new DataLayerException("Time Mismatch error",
							Long.valueOf(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH), null);
				}

				if (!ObjectUtils.isEmpty(dtStageClose)) {
					throw new DataLayerException("Date Stage Close", Long.valueOf(MSG_SYS_STAGE_CLOSED), null);
				}
			}
		}

		BeanUtils.copyProperties(palServiceTrainingDto, palService);
		palService.setDtLastUpdate(new Date());
		return palService;
	}

	/**
	 * Method Name: updatePal Method Description: This dam updates the PAL
	 * table's living arrangment based upon id stage.
	 * 
	 * @param serviceReqHeaderDto
	 * @param idStage
	 * @param cdPalCloseLivArr
	 */
	@Override
	public void updatePal(ServiceReqHeaderDto serviceReqHeaderDto, Long idStage, String cdPalCloseLivArr) {
		if (serviceReqHeaderDto.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Pal.class);
			criteria.add(Restrictions.eq("idPalStage", idStage));
			Pal pal = (Pal) criteria.uniqueResult();
			pal.setIdPalStage(idStage);
			pal.setCdPalCloseLivArr(cdPalCloseLivArr);
			sessionFactory.getCurrentSession().saveOrUpdate(pal);
		} else {
			throw new DataLayerException(ServiceConstants.ARC_ERR_BAD_FUNC_CD);
		}

	}

	/**
	 * Method Name: getIdStagePersonLinkData Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	public StagePersonLinkDto getIdStagePersonLinkData(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getIdStagePersonLinkDataSql)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("dtEmpTermination", StandardBasicTypes.DATE).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(StagePersonLinkDto.class));
		return (StagePersonLinkDto) query.uniqueResult();
	}

	/**
	 * Method Name: updateEventStatus Method Description: This DAM will update
	 * the CD EVENT STATUS for a row in the EVENT table given CD EVENT TYPE and
	 * the ID STAGE. This DAM will change all events for the stage with the CD
	 * EVENT TYPE specified. It will change the CD EVENT STATUS to the status
	 * specified in the input.
	 * 
	 * @param cdEventType
	 * @param cdEventStatus
	 * @param idStage
	 */
	@Override
	public void updateEventStatus(String cdEventType, String cdEventStatus, Long idStage) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class, "event");
		criteria.add(Restrictions.eq("event.stage.idStage", idStage));
		criteria.add(Restrictions.eq("cdEventType", cdEventType));
		List<Event> eventRec = (List<Event>) criteria.list();
		for (Event event : eventRec) {
			event.setCdEventStatus(cdEventStatus);
			sessionFactory.getCurrentSession().saveOrUpdate(event);
		}

	}

	/**
	 * Method Name: getPALCoordinatorID Method Description: Return the IdPerson
	 * for the PAL Coordinator assigned to a Stage.
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	public Long getPALCoordinatorID(Long idStage) {
		Long palCoordinatorID = 0l;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPALCoordinatorIDSql)
				.addScalar("ID_PERSON", StandardBasicTypes.LONG).setParameter("idStage", idStage);
		// Defect 11106 - Get the coordinator from the list
		if (!ObjectUtils.isEmpty(query.list())){
			List<Long> coordinatorList = query.list();
			palCoordinatorID = coordinatorList.get(0);
		}
		return palCoordinatorID;
	}

	/**
	 * Method Name: updatePALSummary Method Description: Updates the value of
	 * cdPalCloseLivArr as null while reopening a stage
	 * 
	 * @param cReqFunc
	 * @param idStage
	 * @param cdPalCloseLivArr
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PalInformationDto updatePALSummary(String cReqFunc, Long idStage, String cdPalCloseLivArr,
			PalInformationDto palInformationDto) {
		if (cReqFunc.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Pal.class);
			criteria.add(Restrictions.eq("idPalStage", idStage));
			Pal pal = (Pal) criteria.uniqueResult();
			if (!ObjectUtils.isEmpty(palInformationDto.getDtAssessmentLastUpdate())
					&& (pal.getDtLastUpdate().compareTo(palInformationDto.getDtAssessmentLastUpdate()) != 0)) {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				palInformationDto.setErrorDto(errorDto);

			} else {
				if (!ObjectUtils.isEmpty(cdPalCloseLivArr))
					pal.setCdPalCloseLivArr(cdPalCloseLivArr);
				sessionFactory.getCurrentSession().saveOrUpdate(pal);
			}
		}
		return palInformationDto;
	}

	/**
	 * Method Name: saveIlsAssessment Method Description: Updates,Deletes or
	 * Saves the Pal Table based on the reqFunctionCd value
	 * 
	 * @param palInformationDto
	 * @param reqFunctionCd
	 */
	@Override
	public PalInformationDto saveIlsAssessment(PalInformationDto palInformationDto, String reqFunctionCd) {

		Pal pal = new Pal();
		switch (reqFunctionCd) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, palInformationDto.getIdStage());
			pal.setStage(stage);
			pal.setIdPalStage(palInformationDto.getIdStage());
			pal.setDtLastUpdate(!(ObjectUtils.isEmpty(palInformationDto.getDtAssessmentLastUpdate()))
					? palInformationDto.getDtAssessmentLastUpdate() : new Date());
			pal.setCdPalCloseLivArr(palInformationDto.getCdPalCloseLivArr());
			pal.setDtPalPostasmtDate(palInformationDto.getDtPostAssessment());
			pal.setDtPalPreasmtDate(palInformationDto.getDtPreAssessment());
			pal.setIndPalIlNoIlsAssmt(palInformationDto.getNoIlsAssessment());
			pal.setCdNoIlsReason(palInformationDto.getNoIlsAssessReason());
			pal.setIndPalIlNoPoasmtScre(palInformationDto.getPostAssessmentNoScore());
			pal.setIndPalIlNoPrasmtScre(palInformationDto.getPreAssessmentNoScore());
			pal.setNbrPalPostasmtScore(palInformationDto.getPostAssessmentScore());
			pal.setNbrPalPreasmtScore(palInformationDto.getPreAssessmentScore());
			pal.setTxtPalIlNoIlsRsn(palInformationDto.getNoIlsReason());
			pal.setDtTrainingCmpltd(palInformationDto.getDtTraingCompleted());
			pal.setTxtSummaryComments(palInformationDto.getTxtSummaryComments());
			sessionFactory.getCurrentSession().save(pal);
			break;

		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Pal.class);
			criteria.add(Restrictions.eq("stage.idStage", palInformationDto.getIdStage()));
			pal = (Pal) criteria.uniqueResult();
			if (!ObjectUtils.isEmpty(pal) && !ObjectUtils.isEmpty(pal.getDtLastUpdate())
					&& !ObjectUtils.isEmpty(palInformationDto.getDtAssessmentLastUpdate())
					&& pal.getDtLastUpdate().compareTo(palInformationDto.getDtAssessmentLastUpdate()) != 0) {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				palInformationDto.setErrorDto(errorDto);

			} 
			else if(!ObjectUtils.isEmpty(pal) && !ObjectUtils.isEmpty(pal.getStage().getDtStageClose())){
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				palInformationDto.setErrorDto(errorDto);
			}
			else {
				pal.setIdPalStage(palInformationDto.getIdStage());
				pal.setDtLastUpdate(palInformationDto.getDtAssessmentLastUpdate());
				pal.setCdPalCloseLivArr(palInformationDto.getCdPalCloseLivArr());
				pal.setDtPalPostasmtDate(palInformationDto.getDtPostAssessment());
				pal.setDtPalPreasmtDate(palInformationDto.getDtPreAssessment());
				pal.setIndPalIlNoIlsAssmt(palInformationDto.getNoIlsAssessment());
				pal.setCdNoIlsReason(palInformationDto.getNoIlsAssessReason());
				pal.setIndPalIlNoPoasmtScre(palInformationDto.getPostAssessmentNoScore());
				pal.setIndPalIlNoPrasmtScre(palInformationDto.getPreAssessmentNoScore());
				pal.setNbrPalPostasmtScore(palInformationDto.getPostAssessmentScore());
				pal.setNbrPalPreasmtScore(palInformationDto.getPreAssessmentScore());
				pal.setTxtPalIlNoIlsRsn(palInformationDto.getNoIlsReason());
				pal.setDtTrainingCmpltd(palInformationDto.getDtTraingCompleted());
				pal.setTxtSummaryComments(palInformationDto.getTxtSummaryComments());
				sessionFactory.getCurrentSession().saveOrUpdate(pal);
			}
			break;

		case ServiceConstants.REQ_FUNC_CD_DELETE:

			Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(Pal.class);
			criteria1.add(Restrictions.eq("idPalStage", palInformationDto.getIdStage()));
			criteria1.add(Restrictions.eq("dtLastUpdate", palInformationDto.getDtAssessmentLastUpdate()));
			pal = (Pal) criteria1.uniqueResult();
			sessionFactory.getCurrentSession().delete(pal);
			break;

		default:
			break;

		}
		return palInformationDto;
	}
}
