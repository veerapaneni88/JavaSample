package us.tx.state.dfps.service.ppm.daoimpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
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
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Ppt;
import us.tx.state.dfps.common.domain.PptNarr;
import us.tx.state.dfps.common.domain.PptParticipant;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PPMInfoReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.forms.dto.PptDetailsOutDto;
import us.tx.state.dfps.service.person.dto.PPTParticipantDto;
import us.tx.state.dfps.service.ppm.dao.PPMDao;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsInDto;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Implementation for PpmDao interface Feb 2, 2018- 6:50:35 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class PPMDaoImpl implements PPMDao {

	@Autowired
	MessageSource messageSource;

	private static Logger log = Logger.getLogger(PPMDaoImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PpmDaoImpl.getPptAddress}")
	private String getPptAddressSql;

	@Value("${PpmDaoImpl.getPptParticipant}")
	private String getPptParticipantSql;

	@Value("${PpmDaoImpl.getPptParticipantList}")
	private String getPptParticipantListSql;

	/**
	 * 
	 * Method Name: getPptAddress (DAM Name : CSES14D) Method Description: This
	 * method retrieves the Participant address data using idPptEvent
	 * 
	 * @param idPptEvent
	 * @return pptDetailsOutDto
	 */
	@Override
	public PptDetailsOutDto getPptAddress(Long idPptEvent) {

		PptDetailsOutDto pptDetailsOutDto = null;
		pptDetailsOutDto = (PptDetailsOutDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPptAddressSql).setParameter("idPptEvent", idPptEvent))
						.addScalar("idPptEvent", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("addrPptCity", StandardBasicTypes.STRING)
						.addScalar("addrPptCnty", StandardBasicTypes.STRING)
						.addScalar("addrPptStLn1", StandardBasicTypes.STRING)
						.addScalar("addrPptStLn2", StandardBasicTypes.STRING)
						.addScalar("addrPptState", StandardBasicTypes.STRING)
						.addScalar("addrPptZip", StandardBasicTypes.STRING)
						.addScalar("dtPptDate", StandardBasicTypes.DATE)
						.addScalar("dtPptDocComp", StandardBasicTypes.DATE)
						.addScalar("nbrPptPhone", StandardBasicTypes.STRING)
						.addScalar("nbrPptPhoneExt", StandardBasicTypes.STRING)
						.addScalar("pptAddrCmnt", StandardBasicTypes.STRING)
						.addScalar("cdChairedType", StandardBasicTypes.STRING)
						.addScalar("cdConferenceType", StandardBasicTypes.STRING)

						.setResultTransformer(Transformers.aliasToBean(PptDetailsOutDto.class)).uniqueResult();

		return pptDetailsOutDto;

	}

	/**
	 * 
	 * Method Name: getParticipantData (DAM Name : CSES40D) Method Description: This
	 * method retrieves the Participant data using idPptPart
	 * 
	 * @param idPptPart
	 * @return pPTParticipantDto
	 */
	@Override
	public PPTParticipantDto getParticipantData(Long idPptPart) {

		PPTParticipantDto pPTParticipantDto = null;
		pPTParticipantDto = (PPTParticipantDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPptParticipantSql).setParameter("idPptPart", idPptPart))
						.addScalar("idPptPart", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("cdPptNotifType", StandardBasicTypes.STRING)
						.addScalar("cdPptPartType", StandardBasicTypes.STRING)
						.addScalar("dtPptDate", StandardBasicTypes.DATE)
						.addScalar("dtPptPartDateNotif", StandardBasicTypes.DATE)
						.addScalar("nmPptPartFull", StandardBasicTypes.STRING)
						.addScalar("sdsPptPartRelationship", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(PPTParticipantDto.class)).uniqueResult();

		return pPTParticipantDto;
	}

	/**
	 * Method Name: getPPTDetails Method Description: Get PPT details
	 * 
	 * @param idPptEvent
	 * @return PptDetailsOutDto
	 */
	@Override
	public PptDetailsOutDto getPPTDetails(Long idPptEvent) {

		PptDetailsOutDto pptDetailsOutDto = null;
		pptDetailsOutDto = (PptDetailsOutDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPptAddressSql).setParameter("idPptEvent", idPptEvent))
						.addScalar("idPptEvent", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("addrPptCity", StandardBasicTypes.STRING)
						.addScalar("addrPptCnty", StandardBasicTypes.STRING)
						.addScalar("addrPptStLn1", StandardBasicTypes.STRING)
						.addScalar("addrPptStLn2", StandardBasicTypes.STRING)
						.addScalar("addrPptState", StandardBasicTypes.STRING)
						.addScalar("addrPptZip", StandardBasicTypes.STRING)
						.addScalar("dtPptDate", StandardBasicTypes.TIMESTAMP)
						.addScalar("dtPptDocComp", StandardBasicTypes.TIMESTAMP)
						.addScalar("nbrPptPhone", StandardBasicTypes.STRING)
						.addScalar("nbrPptPhoneExt", StandardBasicTypes.STRING)
						.addScalar("pptAddrCmnt", StandardBasicTypes.STRING)
						.addScalar("cdChairedType", StandardBasicTypes.STRING)
						.addScalar("cdConferenceType", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(PptDetailsOutDto.class)).uniqueResult();

		if ((!TypeConvUtil.isNullOrEmpty(pptDetailsOutDto))
				&& (!TypeConvUtil.isNullOrEmpty(pptDetailsOutDto.getAddrPptZip()))) {
			String zip = pptDetailsOutDto.getAddrPptZip();
			String[] zipArray = zip.split("-");
			pptDetailsOutDto.setAddrPptZip(zipArray[0]);
			if (zipArray.length > 1)
				pptDetailsOutDto.setExpandedZip(zipArray[1]);
		}

		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, idPptEvent);
		if (!TypeConvUtil.isNullOrEmpty(event)) {
			if (TypeConvUtil.isNullOrEmpty(pptDetailsOutDto))
				pptDetailsOutDto = new PptDetailsOutDto();
			BeanUtils.copyProperties(event, pptDetailsOutDto);

			if (ObjectUtils.isEmpty(pptDetailsOutDto.getIdEventStage()))
				pptDetailsOutDto.setIdEventStage(event.getStage().getIdStage());

			if (ObjectUtils.isEmpty(pptDetailsOutDto.getIdEventPerson()))
				pptDetailsOutDto.setIdEventPerson(event.getPerson().getIdPerson());

		}

		return pptDetailsOutDto;
	}

	/**
	 * 
	 * Method Name: saveOrUpdatePPM Method Description: This method saves/updates
	 * the PPM Information
	 * 
	 * @param pptDetailsOutDto
	 * @return pptDetailsOutDto
	 */
	@Override
	public PptDetailsOutDto saveOrUpdatePPM(PptDetailsOutDto pptDetailsOutDto) {

		Ppt ppt = new Ppt();
		if (!TypeConvUtil.isNullOrEmpty(pptDetailsOutDto)) {
			ppt.setCdConferenceType(pptDetailsOutDto.getCdConferenceType());
			ppt.setCdChairedType(pptDetailsOutDto.getCdChairedType());
			Date dtPptDate = pptDetailsOutDto.getDtPptDate();
			String startTime = pptDetailsOutDto.getStartTime();
			Date pptDate = null;
			try {
				pptDate = DateUtils.getTimestamp(dtPptDate, startTime);
			} catch (ParseException e) {
				pptDate = dtPptDate;
			}
			ppt.setDtPptDate(pptDate);
			ppt.setDtPptDocComp(pptDetailsOutDto.getDtPptDocComp());
			ppt.setDtLastUpdate(new Date());

			ppt.setNbrPptPhone(pptDetailsOutDto.getNbrPptPhone());
			ppt.setNbrPptPhoneExt(pptDetailsOutDto.getNbrPptPhoneExt());

			ppt.setAddrPptStLn1(pptDetailsOutDto.getAddrPptStLn1());
			ppt.setAddrPptStLn2(pptDetailsOutDto.getAddrPptStLn2());
			ppt.setAddrPptCity(pptDetailsOutDto.getAddrPptCity());
			ppt.setAddrPptState(pptDetailsOutDto.getAddrPptState());
			ppt.setAddrPptCnty(pptDetailsOutDto.getAddrPptCnty());
			ppt.setTxtPptAddrCmnt(pptDetailsOutDto.getPptAddrCmnt());

			String zip = pptDetailsOutDto.getAddrPptZip();
			if (!TypeConvUtil.isNullOrEmpty(zip)) {
				String expandedZip = pptDetailsOutDto.getExpandedZip();
				if (!TypeConvUtil.isNullOrEmpty(expandedZip))
					zip = zip + "-" + expandedZip;
				if (zip.length() > 10)
					zip = zip.substring(0, 10);
				ppt.setAddrPptZip(zip);
			}

			ppt.setIdCase(pptDetailsOutDto.getIdCase());
			savePptEvent(pptDetailsOutDto);
			Long idPptEvent = pptDetailsOutDto.getIdPptEvent();
			ppt.setIdPptEvent(idPptEvent);
			sessionFactory.getCurrentSession().saveOrUpdate(ppt);
		}
		return pptDetailsOutDto;
	}

	/**
	 * 
	 * Method Name: getParticipantList Method Description: This method retrieves the
	 * Participant list using idPptEvent
	 * 
	 * @param idPptEvent
	 * @return List<PPTParticipantDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PPTParticipantDto> getParticipantList(Long idPptEvent) {

		List<PPTParticipantDto> participantList = new ArrayList<>();
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getPptParticipantListSql)
				.addScalar("idPptPart", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("cdPptNotifType", StandardBasicTypes.STRING)
				.addScalar("cdPptPartType", StandardBasicTypes.STRING).addScalar("dtPptDate", StandardBasicTypes.DATE)
				.addScalar("dtPptPartDateNotif", StandardBasicTypes.DATE)
				.addScalar("nmPptPartFull", StandardBasicTypes.STRING)
				.addScalar("sdsPptPartRelationship", StandardBasicTypes.STRING).setParameter("idPptEvent", idPptEvent)
				.setResultTransformer(Transformers.aliasToBean(PPTParticipantDto.class));

		participantList = (List<PPTParticipantDto>) query.list();
		return participantList;
	}

	/**
	 * Method Name: deletePPTParticipant Method Description: This method deletes PPT
	 * participant
	 * 
	 * @param idPptPart
	 * @return Boolean
	 */
	@Override
	public Boolean deletePPTParticipant(Long idPptPart) {
		Boolean participantDeleted = Boolean.FALSE;
		PptParticipant pptParticipant = (PptParticipant) sessionFactory.getCurrentSession().load(PptParticipant.class,
				idPptPart);
		if (!ObjectUtils.isEmpty(pptParticipant)) {
			sessionFactory.getCurrentSession().delete(pptParticipant);
			participantDeleted = Boolean.TRUE;
		}
		return participantDeleted;
	}

	/**
	 * Method Name: savePptEvent Method Description: Saves/Updates PPT event in DB.
	 * 
	 * @param PptDetailsOutDto
	 * @return PptDetailsOutDto
	 */
	private PptDetailsOutDto savePptEvent(PptDetailsOutDto pptDetailsOutDto) {

		Event event = null;
		Long idPptEvent = pptDetailsOutDto.getIdPptEvent();
		if (!TypeConvUtil.isNullOrEmpty(idPptEvent) && 0L != (long) idPptEvent) {
			event = (Event) sessionFactory.getCurrentSession().get(Event.class, idPptEvent);
		}
		if (TypeConvUtil.isNullOrEmpty(event)) {
			event = new Event();
			event.setDtEventCreated(new Date());
		}
		event.setDtLastUpdate(new Date());
		event.setDtEventOccurred(new Date());

		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, pptDetailsOutDto.getIdEventStage());

		event.setStage(stage);
		event.setCdEventType(pptDetailsOutDto.getCdEventType());
		event.setIdCase(pptDetailsOutDto.getIdCase());

		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
				pptDetailsOutDto.getIdEventPerson());
		event.setPerson(person);
		event.setCdTask(pptDetailsOutDto.getCdTask());
		event.setTxtEventDescr(pptDetailsOutDto.getTxtEventDescr());
		event.setCdEventStatus(pptDetailsOutDto.getCdEventStatus());

		if (TypeConvUtil.isNullOrEmpty(idPptEvent)) {
			Long eventID = (Long) sessionFactory.getCurrentSession().save(event);
			pptDetailsOutDto.setIdEvent(eventID);
			pptDetailsOutDto.setIdPptEvent(eventID);
		} else {
			sessionFactory.getCurrentSession().update(event);
		}

		return pptDetailsOutDto;
	}

	/**
	 * csys06dQUERYdam - This DAM is used by a service delivery window. Method Name:
	 * getPPTNarrDetails Method Description:
	 * 
	 * @param pInputDataRec
	 * @return ServiceDeliveryRtrvDtlsOutDto
	 */
	@Override
	public ServiceDeliveryRtrvDtlsOutDto getPPTNarrDetails(ServiceDeliveryRtrvDtlsInDto pInputDataRec) {
		PptNarr pptNarr = new PptNarr();
		ServiceDeliveryRtrvDtlsOutDto serviceDeliveryRtrvDtlsOutDto = new ServiceDeliveryRtrvDtlsOutDto();

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PptNarr.class);
		criteria.add(Restrictions.eq("idEvent", pInputDataRec.getIdEvent()));
		pptNarr = (PptNarr) criteria.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(pptNarr)) {
			serviceDeliveryRtrvDtlsOutDto.setIndBLOBExistsInDatabase(ServiceConstants.TRUEVAL);

			if (!TypeConvUtil.isNullOrEmpty(pptNarr.getDtLastUpdate())) {
				serviceDeliveryRtrvDtlsOutDto.setDtLastUpdate(pptNarr.getDtLastUpdate());
			}

			if (!TypeConvUtil.isNullOrEmpty(pptNarr.getIdEvent())) {
				serviceDeliveryRtrvDtlsOutDto.setIdEvent(pptNarr.getIdEvent());
			}

			if (!TypeConvUtil.isNullOrEmpty(pptNarr.getIdDocumentTemplate())) {
				serviceDeliveryRtrvDtlsOutDto.setBlogLength(pptNarr.getIdDocumentTemplate());
			}

		}else{
			serviceDeliveryRtrvDtlsOutDto.setIndBLOBExistsInDatabase(ServiceConstants.FALSEVAL);
		}

		return serviceDeliveryRtrvDtlsOutDto;
	}

	/**
	 * Method Name: insertOrUpdatePPTInfo Method Description: This Method Performs
	 * AUD operations on PPT table based on the IdPptEvent
	 * 
	 * DAM Name: CAUD09D
	 * 
	 * @param pptDetailsOutDto
	 * @param archInputDto
	 * @return ServiceDeliveryRtrvDtlsOutDto
	 */
	@Override
	public ServiceDeliveryRtrvDtlsOutDto insertOrUpdatePPTInfo(PptDetailsOutDto pptDetailsOutDto,
			ServiceReqHeaderDto archInputDto) {
		ServiceDeliveryRtrvDtlsOutDto serviceDeliveryRtrvDtlsOutDto = new ServiceDeliveryRtrvDtlsOutDto();
		Criteria criteria = null;
		pptDetailsOutDto = !ObjectUtils.isEmpty(pptDetailsOutDto) ? pptDetailsOutDto : new PptDetailsOutDto();
		switch (archInputDto.getReqFuncCd()) {

		case (ServiceConstants.REQ_FUNC_CD_ADD):
		case (ServiceConstants.REQ_FUNC_CD_UPDATE):
			Ppt ppt = new Ppt();

			ppt.setIdPptEvent(pptDetailsOutDto.getIdPptEvent());
			ppt.setAddrPptCity(pptDetailsOutDto.getAddrPptCity());
			ppt.setAddrPptCnty(pptDetailsOutDto.getAddrPptCnty());
			ppt.setAddrPptStLn1(pptDetailsOutDto.getAddrPptStLn1());
			ppt.setAddrPptStLn2(pptDetailsOutDto.getAddrPptStLn2());
			ppt.setAddrPptState(pptDetailsOutDto.getAddrPptState());
			ppt.setAddrPptZip(pptDetailsOutDto.getAddrPptZip());
			ppt.setNbrPptPhone(pptDetailsOutDto.getNbrPptPhone());
			ppt.setTxtPptAddrCmnt(pptDetailsOutDto.getPptAddrCmnt());
			ppt.setNbrPptPhoneExt(pptDetailsOutDto.getNbrPptPhoneExt());
			ppt.setCdChairedType(pptDetailsOutDto.getCdChairedType());
			ppt.setCdConferenceType(pptDetailsOutDto.getCdConferenceType());
			ppt.setDtPptDate(pptDetailsOutDto.getDtPptDate());
			ppt.setDtPptDocComp(pptDetailsOutDto.getDtPptDocComp());
			ppt.setIdCase(pptDetailsOutDto.getIdCase());
			// BeanUtils.copyProperties(pptDetailsOutDto, ppt);
			ppt.setDtLastUpdate(new Date());

			sessionFactory.getCurrentSession().saveOrUpdate(ppt);

			break;

		case ServiceConstants.REQ_FUNC_CD_DELETE:
			criteria = sessionFactory.getCurrentSession().createCriteria(Ppt.class);
			criteria.add(Restrictions.eq("idPptEvent", pptDetailsOutDto.getIdPptEvent()));
			ppt = (Ppt) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(ppt)) {
				throw new DataNotFoundException(
						messageSource.getMessage("ppmDaoImpl.IdPptEvent.not.found", null, Locale.US));
			}
			sessionFactory.getCurrentSession().delete(ppt);
			serviceDeliveryRtrvDtlsOutDto.setIdEvent(pptDetailsOutDto.getIdPptEvent());
			break;

		default:
			break;
		}
		return serviceDeliveryRtrvDtlsOutDto;
	}

	/**
	 * Method Name: insertOrUpdatePPTInfo Method Description: This Method Performs
	 * AUD operations on PPT table based on the IdPptEvent
	 * 
	 * DAM Name: CAUD50D
	 * 
	 * @param PPMInfoReq
	 * @param archInputDto
	 * @return ServiceDeliveryRtrvDtlsOutDto
	 */
	@Override
	public ServiceDeliveryRtrvDtlsOutDto updateToDoTable(PPMInfoReq PPMInfoReq, ServiceReqHeaderDto archInputDto) {
		switch (archInputDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_UPDATE:

			Todo todo = new Todo();
			Criteria criteria = null;
			criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
			criteria.add(Restrictions.eq("event.idEvent", PPMInfoReq.getIdEvent()));
			todo = (Todo) criteria.uniqueResult();

			if (!ObjectUtils.isEmpty(todo)) {
				if (!TypeConvUtil.isNullOrEmpty(PPMInfoReq.getIdTodoEvent())) {
					Event event = new Event();
					event.setIdEvent(PPMInfoReq.getIdTodoEvent());
					todo.setEvent(event);
				}

				if (!TypeConvUtil.isNullOrEmpty(PPMInfoReq.getDtTodoCompleted())) {
					todo.setDtTodoCompleted(PPMInfoReq.getDtTodoCompleted());
				}

				if (!TypeConvUtil.isNullOrEmpty(todo.getIdTodo())) {
					todo.setIdTodo(todo.getIdTodo());
				}

				if (!TypeConvUtil.isNullOrEmpty(todo.getDtLastUpdate())) {
					todo.setDtLastUpdate(new Date());
				}

				if (!TypeConvUtil.isNullOrEmpty(todo.getPersonByIdTodoPersAssigned())) {
					todo.setPersonByIdTodoPersAssigned(todo.getPersonByIdTodoPersAssigned());
				}

				sessionFactory.getCurrentSession().update(todo);
			}
		}
		return null;
	}

	/**
	 * Method Name: savePPTParticipant Method Description: Method Saves the PPT
	 * Participant Details.
	 * 
	 * @param pptParticipantDto
	 * @return void
	 */
	@Override
	public void savePPTParticipant(PPTParticipantDto pptParticipantDto) {
		log.info("Method savePPTParticipant of PpmDaoImpl : Execution Started.");
		// Save the PPT Participant.
		PptParticipant pptParticipant = new PptParticipant();
		BeanUtils.copyProperties(pptParticipantDto, pptParticipant);
		pptParticipant
				.setEvent((Event) sessionFactory.getCurrentSession().load(Event.class, pptParticipantDto.getIdEvent()));
		pptParticipant.setDtLastUpdate(new Date());
		pptParticipant.setDtPptPart(pptParticipantDto.getDtPptDate());
		if (!TypeConvUtil.isNullOrEmpty(pptParticipantDto.getIdPerson())) {
			Person person = (Person) sessionFactory.getCurrentSession().load(Person.class,
					pptParticipantDto.getIdPerson());
			pptParticipant.setPerson(person);
		}
		sessionFactory.getCurrentSession().saveOrUpdate(pptParticipant);
		log.info("Method savePPTParticipant of PpmDaoImpl : Returning Response.");
	}

	/**
	 * 
	 * Method Name: populateAddressPpt Method Description: CCMN14D This DAM will
	 * perform a full row retrieval from PPT when the host input variable ID event
	 * matches an element in the table.
	 * 
	 * @param idPptEvent
	 * @return PptDetailsOutDto
	 */
	@Override
	public PptDetailsOutDto populatePptAddress(Long idPptEvent) {

		PptDetailsOutDto pptDetailsOutDto = null;
		pptDetailsOutDto = (PptDetailsOutDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPptAddressSql).setParameter("idPptEvent", idPptEvent))
						.addScalar("idPptEvent", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("addrPptCity", StandardBasicTypes.STRING)
						.addScalar("addrPptCnty", StandardBasicTypes.STRING)
						.addScalar("addrPptStLn1", StandardBasicTypes.STRING)
						.addScalar("addrPptStLn2", StandardBasicTypes.STRING)
						.addScalar("addrPptState", StandardBasicTypes.STRING)
						.addScalar("addrPptZip", StandardBasicTypes.STRING)
						.addScalar("dtPptDate", StandardBasicTypes.TIMESTAMP)
						.addScalar("dtPptDocComp", StandardBasicTypes.TIMESTAMP)
						.addScalar("nbrPptPhone", StandardBasicTypes.STRING)
						.addScalar("nbrPptPhoneExt", StandardBasicTypes.STRING)
						.addScalar("pptAddrCmnt", StandardBasicTypes.STRING)
						.addScalar("cdChairedType", StandardBasicTypes.STRING)
						.addScalar("cdConferenceType", StandardBasicTypes.STRING)

						.setResultTransformer(Transformers.aliasToBean(PptDetailsOutDto.class)).uniqueResult();

		if (!TypeConvUtil.isNullOrEmpty(pptDetailsOutDto)) {
			if (!TypeConvUtil.isNullOrEmpty(pptDetailsOutDto.getAddrPptZip())) {
				String zip = pptDetailsOutDto.getAddrPptZip();
				String[] zipArray = zip.split("-");
				pptDetailsOutDto.setAddrPptZip(zipArray[0]);
				if (zipArray.length > 1)
					pptDetailsOutDto.setExpandedZip(zipArray[1]);
			}
		}

		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, idPptEvent);
		if (!TypeConvUtil.isNullOrEmpty(event)) {
			if (TypeConvUtil.isNullOrEmpty(pptDetailsOutDto))
				pptDetailsOutDto = new PptDetailsOutDto();

			BeanUtils.copyProperties(event, pptDetailsOutDto);

			if (ObjectUtils.isEmpty(pptDetailsOutDto.getIdEventStage()))
				pptDetailsOutDto.setIdEventStage(event.getStage().getIdStage());

			if (ObjectUtils.isEmpty(pptDetailsOutDto.getIdEventPerson()))
				pptDetailsOutDto.setIdEventPerson(event.getPerson().getIdPerson());
		}
		return pptDetailsOutDto;
	}
}
