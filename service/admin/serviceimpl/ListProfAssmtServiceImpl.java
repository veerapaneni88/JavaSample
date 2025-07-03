package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.EventIdDao;
import us.tx.state.dfps.service.admin.dao.EventStagePersonLinkInsUpdDao;
import us.tx.state.dfps.service.admin.dao.RiskAssmtNarrOrIraDao;
import us.tx.state.dfps.service.admin.dto.EventIdOutDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdOutDto;
import us.tx.state.dfps.service.admin.dto.RiskAssmtNarrOrIraInDto;
import us.tx.state.dfps.service.admin.dto.RiskAssmtNarrOrIraOutDto;
import us.tx.state.dfps.service.admin.service.ListProfAssmtService;
import us.tx.state.dfps.service.casepackage.dto.MdclMentalAssmntDtlDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.ListProfAssmtReq;
import us.tx.state.dfps.service.common.response.ListProfAssmtRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.investigation.dao.ListProfessionalMedicalAssessmentDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;

/**
 * 
 * @author abajis
 * 
 *         Class Name: ListProfAssmtServiceImpl Description Class name: CINV29S
 *         ListProfAssmtServiceImpl Description:Used to call Prof Assmt table,
 *         Narr table, Person tabl
 * 
 */

@Service
@Transactional
public class ListProfAssmtServiceImpl implements ListProfAssmtService {
	@Autowired
	ListProfessionalMedicalAssessmentDao listProfessionalMedicalAssessmentDao;

	@Autowired
	EventIdDao eventIdDao;

	@Autowired
	EventStagePersonLinkInsUpdDao eventStagePersonLinkInsUpdDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	RiskAssmtNarrOrIraDao riskAssmtNarrOrIraDao;

	MessageSource messageSource;

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ListProfAssmtRes getListProfAssmtService(ListProfAssmtReq listProfAssmtReq)

	{

		ListProfAssmtRes listProfAssmtRes = new ListProfAssmtRes();
		if (!ObjectUtils.isEmpty(listProfAssmtReq) && !ObjectUtils.isEmpty(listProfAssmtReq.getUserLogonId())) {
			listProfAssmtRes.setUserLogonId(listProfAssmtReq.getUserLogonId());
		}
		// retrieve stage information detail CINT21D

		StageDto stageDto = stageDao.getStageById(listProfAssmtReq.getIdStage());
		listProfAssmtRes.setStageDto(stageDto);
		
		// Retrieve narrative flag.
		if(!ObjectUtils.isEmpty(listProfAssmtReq.getIdEvent())){
			
		}

		if (!TypeConvUtil.isNullOrEmpty(listProfAssmtReq.getPageMode())
				&& listProfAssmtReq.getPageMode().equalsIgnoreCase(ServiceConstants.WINDOW_MODE_INQUIRE)) {
			if (!TypeConvUtil.isNullOrEmpty(listProfAssmtReq.getEventIdInDto())
					&& listProfAssmtReq.getEventIdInDto().getIdEvent() != 0) {

				// retrieve event related with the assessment -CCMN45D
				List<EventIdOutDto> event = eventIdDao.getEventDetailList(listProfAssmtReq.getEventIdInDto());
				listProfAssmtRes.setEventIdOutDto(event);

			}
			// retrieve medical mental assessment information of a person -
			// CINV45D

			MdclMentalAssmntDtlDto mdclMentalAssmntDtlDto = listProfessionalMedicalAssessmentDao
					.getProfessionalAssesmentByEventId(listProfAssmtReq.getEventIdInDto().getIdEvent());
			TodoDto todoDto = listProfessionalMedicalAssessmentDao
					.getTodoDtl(listProfAssmtReq.getEventIdInDto().getIdEvent());
			mdclMentalAssmntDtlDto.setDtApptScheduled(todoDto.getDtTodoTaskDue());

			if (ObjectUtils.isEmpty(mdclMentalAssmntDtlDto.getCdTask()))
				mdclMentalAssmntDtlDto.setCdTask(listProfAssmtReq.getCdTask());

			listProfAssmtRes.setMdclMentalAssmntDtlDto(mdclMentalAssmntDtlDto);
			// CSYS13D

			if (!TypeConvUtil.isNullOrEmpty(listProfAssmtReq.getIdEvent())) {
				if(listProfessionalMedicalAssessmentDao.checkNarrExists(listProfAssmtReq.getIdEvent()))
				listProfAssmtRes.setScrTxtNarrStatus(ServiceConstants.TXT_NARR_EXISTS);
			}

		} else if (!TypeConvUtil.isNullOrEmpty(listProfAssmtReq.getPageMode())
				&& listProfAssmtReq.getPageMode().equalsIgnoreCase(ServiceConstants.WINDOW_MODE_MODIFY)
				&& !TypeConvUtil.isNullOrEmpty(listProfAssmtReq.getEventIdInDto())
				&& (listProfAssmtReq.getEventIdInDto().getIdEvent() != 0)) {
			// retrieve event related with the assessment -CCMN45D
			List<EventIdOutDto> event = eventIdDao.getEventDetailList(listProfAssmtReq.getEventIdInDto());
			listProfAssmtRes.setEventIdOutDto(event);
			// retrieve medical mental assessment information of a person -
			// CINV45D
			MdclMentalAssmntDtlDto mdclMentalAssmntDtlDto = null;

			if (!TypeConvUtil.isNullOrEmpty((listProfAssmtReq.getEventIdInDto().getIdEvent()))) {
				mdclMentalAssmntDtlDto = listProfessionalMedicalAssessmentDao
						.getProfessionalAssesmentByEventId(listProfAssmtReq.getEventIdInDto().getIdEvent());
				TodoDto todoDto = listProfessionalMedicalAssessmentDao
						.getTodoDtl(listProfAssmtReq.getEventIdInDto().getIdEvent());
				if (!ObjectUtils.isEmpty(todoDto)) {
					mdclMentalAssmntDtlDto.setDtApptScheduled(todoDto.getDtTodoTaskDue());
				}
				listProfAssmtRes.setMdclMentalAssmntDtlDto(mdclMentalAssmntDtlDto);
			}

			// CCMN87D
			EventStagePersonLinkInsUpdInDto eventStagePersonLinkInsUpdInDto = listProfAssmtReq
					.getEventStagePersonLinkInsUpdInDto();
			if (!TypeConvUtil.isNullOrEmpty(eventStagePersonLinkInsUpdInDto)) {
				List<EventStagePersonLinkInsUpdOutDto> EventStagePersonLinkInsUpList = eventStagePersonLinkInsUpdDao
						.getEventAndStatusDtls(eventStagePersonLinkInsUpdInDto);
				listProfAssmtRes.setEventStagePersonLinkInsUpList(EventStagePersonLinkInsUpList);
			}
			// CINV47D
			List<PersonDto> personDto = null;
			if (!TypeConvUtil.isNullOrEmpty(listProfAssmtReq.getCdStagePersType())) {
				personDto = listProfessionalMedicalAssessmentDao.getPersonDetails(listProfAssmtReq.getCdStagePersType(),
						listProfAssmtReq.getIdStage());
			}
			listProfAssmtRes.setPersonDto(personDto);

			// CSYS13D
			if (!TypeConvUtil.isNullOrEmpty(listProfAssmtReq.getIdEvent())) {
				if(listProfessionalMedicalAssessmentDao.checkNarrExists(listProfAssmtReq.getIdEvent()))
				listProfAssmtRes.setScrTxtNarrStatus(ServiceConstants.TXT_NARR_EXISTS);
			}
			
		} else {
			// CINV47D
			List<PersonDto> personDto = null;
			if (!TypeConvUtil.isNullOrEmpty(listProfAssmtReq.getCdStagePersType())) {
				personDto = listProfessionalMedicalAssessmentDao.getPersonDetails(listProfAssmtReq.getCdStagePersType(),
						listProfAssmtReq.getIdStage());
			}

			listProfAssmtRes.setPersonDto(personDto);
			// event stage and person related info CCMN87D
			EventStagePersonLinkInsUpdInDto eventStagePersonLinkInsUpdInDto = listProfAssmtReq
					.getEventStagePersonLinkInsUpdInDto();

			List<EventStagePersonLinkInsUpdOutDto> EventStagePersonLinkInsUpList = null;

			if (!TypeConvUtil.isNullOrEmpty(eventStagePersonLinkInsUpdInDto))
				EventStagePersonLinkInsUpList = eventStagePersonLinkInsUpdDao
						.getEventAndStatusDtls(eventStagePersonLinkInsUpdInDto);

			listProfAssmtRes.setEventStagePersonLinkInsUpList(EventStagePersonLinkInsUpList);
		}
		return listProfAssmtRes;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.admin.service.ListProfAssmtService#getTodoDtl(
	 * java.lang.Long)
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public TodoDto getTodoDtl(Long idEvent) {
		TodoDto todoDto = listProfessionalMedicalAssessmentDao.getTodoDtl(idEvent);
		return todoDto;
	}

}
