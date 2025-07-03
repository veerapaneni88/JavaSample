package us.tx.state.dfps.service.prt.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.childplan.dao.ChildServicePlanFormDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PRTActionPlanReq;
import us.tx.state.dfps.service.common.request.PRTActplanFollowUpReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.PRTContactActionPrefillData;
import us.tx.state.dfps.service.forms.util.PRTContactFollowUpPrefillData;
import us.tx.state.dfps.service.prt.service.PRTActPlanFollowUpService;
import us.tx.state.dfps.service.prt.service.PRTActionPlanService;
import us.tx.state.dfps.service.prt.service.PRTContactService;
import us.tx.state.dfps.service.subcare.dto.PRTActPlanFollowUpDto;
import us.tx.state.dfps.service.subcare.dto.PRTActionPlanDto;
import us.tx.state.dfps.service.subcare.dto.PRTContactMainDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Class Description: This
 * method is used to call the DAO to fetch PRT contact Action Plan Response data
 * to return back to the controller July 2, 2018 - 12:44:36 PM © 2017 Texas
 * Department of Family and Protective Services
 * 
 */
@Service
@Transactional
public class PRTContactServiceImpl implements PRTContactService {

	@Autowired
	private PRTActionPlanService pRTActionPlanService;

	@Autowired
	private PRTActPlanFollowUpService pRTActPlanFollowUpService;

	@Autowired
	private ChildServicePlanFormDao childServicePlanFormDao;

	@Autowired
	private PRTContactActionPrefillData pRTContactPrefillData;

	@Autowired
	private PRTContactFollowUpPrefillData pRTContactFollowUpPrefillData;

	/**
	 * Method Description: This service will get forms populated by receiving
	 * BasePRTSessionReq from controller, then retrieving data for
	 * PRTContactActionPlan form, then get them returned to
	 * prefillDataServiceDto
	 *
	 * @param BasePRTSessionReq
	 * @return PreFillDataServiceDto
	 */

	@Override
	public PreFillDataServiceDto getPRTContactActionPopulated(PRTActionPlanReq pRTActionPlanReq) {

		PRTContactMainDto pRTContactMainDto = new PRTContactMainDto();
		PRTActionPlanDto pRTActionPlanDto = pRTActionPlanService.fetchActionPlan(
				pRTActionPlanReq.getIdActionPlanEvent(), pRTActionPlanReq.getIdStage(), pRTActionPlanReq.getIdCase());
		pRTContactMainDto.setpRTActionPlanDto(pRTActionPlanDto);

		if (!ObjectUtils.isEmpty(pRTActionPlanDto.getIdPrtActionPlan())
				&& !ObjectUtils.isEmpty(pRTActionPlanDto.getDtComplete())) {
			PersonDto personDto = childServicePlanFormDao.getPersonDetails(pRTActionPlanDto.getIdLastUpdatePerson());
			StringBuilder prtCompletor = new StringBuilder();
			prtCompletor.append(personDto.getNmPersonFirst());
			prtCompletor.append(ServiceConstants.EMPTY_STRING);
			prtCompletor.append(personDto.getNmPersonLast());
			pRTContactMainDto.setFullName(prtCompletor.toString());
		}
		return pRTContactPrefillData.returnPrefillData(pRTContactMainDto);
	}

	/**
	 * Method Description: This service will get forms populated by receiving
	 * BasePRTSessionReq from controller, then retrieving data for
	 * PRTContactFollowUp form, then get them returned to prefillDataServiceDto
	 *
	 * @param BasePRTSessionReq
	 * @return PreFillDataServiceDto
	 */

	@Override
	public PreFillDataServiceDto getPRTContactFollowUpPopulated(PRTActplanFollowUpReq pRTActplanFollowUpReq) {
		PRTContactMainDto pRTContactMainDto = new PRTContactMainDto();
		PRTActPlanFollowUpDto pRTActPlanFollowUpDto = pRTActPlanFollowUpService.fetchActPlanFollowUp(
				pRTActplanFollowUpReq.getIdActplnFollowupEvent(), pRTActplanFollowUpReq.getIdStage(),
				pRTActplanFollowUpReq.getIdCase());
		pRTContactMainDto.setpRTActPlanFollowUpDto(pRTActPlanFollowUpDto);

		if (!ObjectUtils.isEmpty(pRTActPlanFollowUpDto.getIdPrtActplnFollowup())
				&& !ObjectUtils.isEmpty(pRTActPlanFollowUpDto.getDtComplete())) {
			PersonDto personDto = childServicePlanFormDao
					.getPersonDetails(pRTActPlanFollowUpDto.getIdLastUpdatePerson());
			StringBuilder prtCompletor = new StringBuilder();
			prtCompletor.append(personDto.getNmPersonFirst());
			prtCompletor.append(ServiceConstants.EMPTY_STRING);
			prtCompletor.append(personDto.getNmPersonLast());
			pRTContactMainDto.setFullName(prtCompletor.toString());
		}
		return pRTContactFollowUpPrefillData.returnPrefillData(pRTContactMainDto);
	}

}
