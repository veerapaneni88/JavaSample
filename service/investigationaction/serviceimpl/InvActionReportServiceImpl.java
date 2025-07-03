package us.tx.state.dfps.service.investigationaction.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.dto.InvActionReportDto;
import us.tx.state.dfps.riskandsafetyassmt.dto.InvstActionQuestionDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.InvActionReportReq;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.InvActionReportFormPrefillData;
import us.tx.state.dfps.service.investigationaction.dao.InvActionReportDao;
import us.tx.state.dfps.service.investigationaction.service.InvActionReportService;
import us.tx.state.dfps.service.workload.dto.EventStagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:InvActionReportServiceImpl will implemented all operation defined
 * in InvActionReportServiceImpl Interface related InvActionReport module. May
 * 2, 2018- 2:01:28 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class InvActionReportServiceImpl implements InvActionReportService {

	@Autowired
	DisasterPlanDao disasterPlanDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	InvActionReportDao invActionReportDao;

	@Autowired
	InvActionReportFormPrefillData invActionReportFormPrefillData;

	/**
	 * 
	 * Method Name: getInvActionInfo Service Name :CINV37S Method
	 * Description:This is the CINV37S form service to build the Investigation
	 * Actions Report
	 * 
	 * @param invActionReportReq
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getInvActionReportInfo(InvActionReportReq invActionReportReq) {
		InvActionReportDto invActionReportDto = new InvActionReportDto();
		Long idEvent = ServiceConstants.ZERO_VAL;

		// call DAM CCMN87D
		List<EventStagePersonDto> eventStagePersonDtoList = stageDao.getEventStagePersonListByAttributes(
				invActionReportReq.getIdStage(), ServiceConstants.QUESTION_ANSWER_TASK,
				ServiceConstants.STAGE_EVENT_TYPE);
		if (!ObjectUtils.isEmpty(eventStagePersonDtoList)) {
			idEvent = eventStagePersonDtoList.get(0).getIdEvent();
		}

		// call DAM CSEC02D
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(invActionReportReq.getIdStage());

		// call DAM CINV04D
		List<InvstActionQuestionDto> invsActionQuestionDtoList = invActionReportDao.getInvstActionQuestions(idEvent);

		invActionReportDto.setEventStagePersonDtoList(eventStagePersonDtoList);
		invActionReportDto.setGenericCaseInfoDto(genericCaseInfoDto);
		invActionReportDto.setInvsActionQuestionDtoList(invsActionQuestionDtoList);

		return invActionReportFormPrefillData.returnPrefillData(invActionReportDto);
	}

}
