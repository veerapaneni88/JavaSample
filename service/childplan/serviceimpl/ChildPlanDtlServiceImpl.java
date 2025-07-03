package us.tx.state.dfps.service.childplan.serviceimpl;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.EventUpdEventStatusDao;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonOutDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.alert.service.AlertService;
import us.tx.state.dfps.service.childplan.dao.ChildPlanDtlDao;
import us.tx.state.dfps.service.childplan.daoimpl.ChildPlanDtlDaoImpl;
import us.tx.state.dfps.service.childplan.dto.ChidPlanPsychMedctnDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPalnBehaviorMgntDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanAdtnlSctnDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanEducationDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanEmtnlThrptcDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanFmlyTeamPrtctpnDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanHealthCareSummaryDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanHighRiskServicesDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanInformationDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanIntellectualDevelopDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanLegalGrdnshpDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanOfServiceDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanOfServiceDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanPriorAdpInfoDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanServiceTypeEnum;
import us.tx.state.dfps.service.childplan.dto.ChildPlanSocialRecreationalDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanSupervisionDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanTransAdltAbvDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanTransAdtltBlwDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanTreatmentServiceDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanVisitationCnctFmlyDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanQrtpPrmnncyMeetingDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanYouthParentingDto;
import us.tx.state.dfps.service.childplan.service.ChildPlanDtlService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.ChildPlanDtlReq;
import us.tx.state.dfps.service.common.response.ChildPlanDtlRes;
import us.tx.state.dfps.service.common.response.CommonIdRes;
import us.tx.state.dfps.service.common.util.CodesTableViewLookupUtils;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ChildPlanOfServicePrefillData;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import us.tx.state.dfps.service.person.service.CriminalHistoryService;
import us.tx.state.dfps.service.workload.dto.EventDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * will make call to dao layer to fetch, save, save&submit, delete and business
 * logic are implemented for ChildPlan Detail Screen. May 4, 2018- 10:30:08 AM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class ChildPlanDtlServiceImpl implements ChildPlanDtlService {

	@Autowired
	ChildPlanDtlDao childPlanDtlDao;

	@Autowired
	ChildPlanOfServicePrefillData childPlanOfServicePrefillData;

	@Autowired
	CriminalHistoryService criminalHistoryService;

	@Autowired
	EventDao eventDao;

	@Autowired
	AlertService alertService;

	@Autowired
	ApprovalCommonService approvalCommonService;
	
	@Autowired
	EventUpdEventStatusDao updateEventStatusDao;

	@Autowired
	private CodesTableViewLookupUtils codesTableViewLookupUtils;

	/**
	 * Method Name: getChildPlanDtl Method Description: This Method is used to
	 * retrieve all the pre-fill editable and pre-fill readable values for
	 * display.
	 * 
	 * @param childPlanDtlReq
	 * @return ChildPlanDtlRes
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public ChildPlanDtlRes getChildPlanDtl(ChildPlanDtlReq childPlanDtlReq){
		boolean copyFromDiffStage = false;
		long eventIdForCopyOfDiffStage=0l;
		ChildPlanDtlRes childPlanDtlRes = new ChildPlanDtlRes();
		ChildPlanOfServiceDto childPlanOfServiceDto = childPlanDtlReq.getChildPlanOfServiceDto();
		Long idChildPlanEvent = childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto()
				.getIdChildPlanEvent();
		String cdEventStatus = eventDao.getEventStatus(idChildPlanEvent);
		if (!ObjectUtils.isEmpty(idChildPlanEvent) && idChildPlanEvent > 0) {
			EventDto eventDto = eventDao.getEventByid(idChildPlanEvent);
			// New using in Child's Service Plan for Case List , copy Child plan
			// from different stage
			if (!eventDto.getIdStage().equals(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIdStage())) {
				eventIdForCopyOfDiffStage=idChildPlanEvent;
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().setIdChildPlanEvent(null);
				copyFromDiffStage = true;
			}
		}
		
		if (ObjectUtils.isEmpty(idChildPlanEvent) || idChildPlanEvent == 0
				|| ServiceConstants.EVENTSTATUS_NEW.equalsIgnoreCase(cdEventStatus) || copyFromDiffStage) {// new
																											// Save
																											// stage
			
			// Get the PE fields to be refreshed from source page till plan is
			// new
			childPlanOfServiceDto = getPrefillEditableInfo(childPlanOfServiceDto);

			// Get the PR fields always to be refreshed from source page till plan is approved
			childPlanOfServiceDto = getPrefillReadOnlyInfo(childPlanOfServiceDto);

			String levelOfCare = childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCurrentLvlCare();			
			if (CodesConstant.CBILPLOC_210.equalsIgnoreCase(levelOfCare)) {
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().setIndChildRcvngSvcs(ServiceConstants.NO);
			} else if (CodesConstant.CBILPLOC_230.equalsIgnoreCase(levelOfCare)
					|| CodesConstant.CBILPLOC_240.equalsIgnoreCase(levelOfCare)
					|| CodesConstant.CBILPLOC_270.equalsIgnoreCase(levelOfCare)) {
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().setIndChildRcvngSvcs(ServiceConstants.YES);
			}			
			// this call is required to get the approved child plan id and prefill	
			if(!copyFromDiffStage){
				childPlanOfServiceDto.setIdApprovedChildPlan(childPlanDtlDao.getPreviousApprovedChildPlanId(
						childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIdPerson(),
						childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIdStage()));
			}else {
				childPlanOfServiceDto.setIdApprovedChildPlan(eventIdForCopyOfDiffStage);
			}
			// new Using Scenario on Add.
			if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getIdApprovedChildPlan()) && childPlanOfServiceDto.getIdApprovedChildPlan() > 0) {
				// get all the values for the previous approved plan and the
				// process.
				Long approvedChildPlanEvent = childPlanOfServiceDto.getIdApprovedChildPlan();
				// get the initial data
				childPlanOfServiceDto = getInitialDataOfPreviousApprovedPlan(childPlanOfServiceDto,
						approvedChildPlanEvent);
				// get the other section wise data for approved previous child
				// plan
				childPlanOfServiceDto = childPlanDtlDao.getAllChildPlanDetails(approvedChildPlanEvent,
						childPlanOfServiceDto);
				//mergePrefillValues(newchildPlanOfServiceDto, childPlanOfServiceDto);
				//childPlanOfServiceDto = newchildPlanOfServiceDto;
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().setCdEventStatus(ServiceConstants.EVENT_STATUS_PROC);
				/*if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto())
						&& !ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto()
								.getChidPlanPsychMedctnDtlDtoList())) {
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().setPrePsycnDtls(childPlanOfServiceDto
							.getChildPlanHealthCareSummaryDto().getChidPlanPsychMedctnDtlDtoList());
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto()
							.setIndPsycMedChange(ServiceConstants.STRING_IND_N);
				}*/

			}else {
				/*childPlanOfServiceDto.getChildPlanHealthCareSummaryDto()
						.setIndYouthOwnConsenter(childPlanDtlDao
								.isYouthOwnConsenter(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIdStage(),
										childPlanOfServiceDto.getChildPlanHealthCareSummaryDto())
								.getIndYouthOwnConsenter());*/
				childPlanDtlDao
				.isYouthOwnConsenter(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIdStage(),
						childPlanOfServiceDto.getChildPlanHealthCareSummaryDto());
			}

			//PPM 70054(FCL) - IMPACT Date Requirement for RCYFC Notification
			setChildBillOfRightsDates(childPlanOfServiceDto);
			// childPlanOfServiceDto =
			// getPrefillReadOnlyInfo(childPlanOfServiceDto);

		} else {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().setCdEventStatus(cdEventStatus);
			childPlanOfServiceDto = getChildPlanInfo(childPlanDtlReq);
			
			// Get the PR fields always to be refreshed from source page till
			// plan is approved
			if (!ServiceConstants.APPROVED.equalsIgnoreCase(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getCdEventStatus())) {
				childPlanOfServiceDto = getPrefillReadOnlyInfo(childPlanOfServiceDto);
				//PPM 70054(FCL) - IMPACT Date Requirement for RCYFC Notification
				setChildBillOfRightsDates(childPlanOfServiceDto);
			}else{
				if (!ObjectUtils.isEmpty(idChildPlanEvent)){					
					ChildPlanInformationDto informationDto = childPlanDtlDao.getChildPlanInformation(idChildPlanEvent);
					childPlanOfServiceDto.setChildPlanInformationDto(informationDto);
				}
				
			}

		}

		if (ServiceConstants.CHILD_PLAN_RVW
				.equals(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCspPlanType())
				&& !ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto())) {
			childPlanOfServiceDto.getChildPlanHealthCareSummaryDto()
					.setPrePsycnDtls(childPlanDtlDao.getPrePsycnDtls(
							childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIdPerson(),
							childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIdStage()));
			String indShowrsnChngPsycMedDiv = ServiceConstants.STRING_IND_N;
			List<ChidPlanPsychMedctnDtlDto> currentList=childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getChidPlanPsychMedctnDtlDtoList();
			List<ChidPlanPsychMedctnDtlDto> previousList=childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getPrePsycnDtls();
			
			if((!ObjectUtils.isEmpty(currentList) && ObjectUtils.isEmpty(previousList)) ||
					(ObjectUtils.isEmpty(currentList) && !ObjectUtils.isEmpty(previousList) ) ||
						 (!ObjectUtils.isEmpty(currentList) && !ObjectUtils.isEmpty(previousList) && currentList.size() != previousList.size() ) ){
				indShowrsnChngPsycMedDiv = ServiceConstants.STRING_IND_Y;
			}else {
				if(!ObjectUtils.isEmpty(currentList) && !ObjectUtils.isEmpty(previousList)){
							indShowrsnChngPsycMedDiv = getIndrsnChngPsycMedDiv(
						childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getChidPlanPsychMedctnDtlDtoList(),
						childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getPrePsycnDtls());
			
				}
				}
			childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().setIndPsycMedChange(indShowrsnChngPsycMedDiv);
			//PPM 69915 - Alcohol Substance Tracker changes

			childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().setIndDgnsMdclCndtn(
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getIndDgnsMdclCndtn());


		}
		
		List<EventDto> eventList = eventDao.getEventByStageIDAndTaskCode(
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIdStage(), ServiceConstants.CLOSE_SUB_STAGE);

		EventDto stageClosureEvent = null;

		if (!ObjectUtils.isEmpty(eventList)) {
			stageClosureEvent = eventList.stream()
					.filter(x -> ServiceConstants.EVENT_STATUS_PEND.equals(x.getCdEventStatus())).findAny()
					.orElse(null);
		}

		if (!ObjectUtils.isEmpty(stageClosureEvent)) {
			childPlanOfServiceDto.setIndStageClosurePending(ServiceConstants.YES);
		}
	   /*artf215077 IMPACT Date Requirement*/
		String decodeDate = codesTableViewLookupUtils.getDecodeVal(ServiceConstants.CRELDATE,ServiceConstants.CRELDATE_MAY_2022_FFPSA_HT);
		SimpleDateFormat dateFormat = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_MMddyyyy);
		Date result;
		try {
			result = dateFormat.parse(decodeDate);
		}catch (ParseException e) {
			throw new DataLayerException(e.getMessage());
		}

		if(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getDtLastUpdate() == null ||
				childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getDtLastUpdate().compareTo(result)>0){
			if(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto() != null) {
				childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().setIndEventStartFfpsa(true);
		}
		}

		//BR15.6. Get Recommended and Selected Service Package details
		if(!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()) && !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto())
			&& !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdCase())
			&& !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdStage())) {
			ServicePackageDtlDto rcmdSvcPkgDtlDto = childPlanDtlDao.getSvcPkgDetails(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdCase(),
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdStage(), ServiceConstants.SERVICE_PACKAGE_RECOMMENDED);
			childPlanOfServiceDto.setRcmdSvcPackageDtlDto(rcmdSvcPkgDtlDto);

			ServicePackageDtlDto selSvcPkgDtlDto = childPlanDtlDao.getSvcPkgDetails(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdCase(),
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdStage(), ServiceConstants.SERVICE_PACKAGE_SELECTED);
			childPlanOfServiceDto.setSelSvcPackageDtlDto(selSvcPkgDtlDto);
		}
		childPlanDtlRes.setChildPlanOfServiceDto(childPlanOfServiceDto);
		return childPlanDtlRes;
	}

	//PPM 70054(FCL) - IMPACT Date Requirement for RCYFC Notification
	/**
	 * @param childPlanOfServiceDto
	 */
	private void setChildBillOfRightsDates(ChildPlanOfServiceDto childPlanOfServiceDto) {
		Long idStage = 0L;
		if(!ObjectUtils.isEmpty(childPlanOfServiceDto) && !ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanOfServiceDtlDto())){
			idStage = childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIdStage();
		}
		childPlanOfServiceDto.getChildPlanOfServiceDtlDto().setDtInitialBor(childPlanDtlDao.getChildInitialBORDate(idStage));
		childPlanOfServiceDto.getChildPlanOfServiceDtlDto().setDtMostRecentBor(childPlanDtlDao.getChildMostRecentBORDate(idStage));

	}

	// This method to compare the if any psyc medication change for RVW child
	// plan
	private String getIndrsnChngPsycMedDiv(List<ChidPlanPsychMedctnDtlDto> currentList,
			List<ChidPlanPsychMedctnDtlDto> previousList) {
		String indShowrsnChngPsycMedDiv = ServiceConstants.STRING_IND_N;
		
		
		
		for(ChidPlanPsychMedctnDtlDto currPhysc:currentList){
			
			String currentMedication=currPhysc.getTxtMedctn();
			if (StringUtils.isNotBlank(currentMedication)){
			
				ChidPlanPsychMedctnDtlDto previousPhyscObj=previousList.stream().filter( previousPhysc-> currentMedication.equals(previousPhysc.getTxtMedctn()) ).findFirst().orElse(null);
			
			if(!ObjectUtils.isEmpty(previousPhyscObj)){
				String dosage=currPhysc.getTxtDosageFreqncy();
				String reason=currPhysc.getTxtRsn();
				String sideEffect=currPhysc.getTxtSideEfct();
			for( int i=0;i<3;i++){
				String tempFieldVal="";
				String previousFiledVal="";
				switch (i) {
				case 0:
					tempFieldVal=dosage;
					previousFiledVal=previousPhyscObj.getTxtDosageFreqncy();
					break;
				case 1:
					tempFieldVal=reason;
					previousFiledVal=previousPhyscObj.getTxtRsn();
					break;
				case 2:
					tempFieldVal=sideEffect;
					previousFiledVal=previousPhyscObj.getTxtSideEfct();
					break;
				}
				//Fix for defect 13180 - Cannot add new CPOS/view existing
				if( ( StringUtils.isNotBlank(tempFieldVal) && StringUtils.isBlank(previousFiledVal) ) ||
						( StringUtils.isBlank(tempFieldVal) && StringUtils.isNotBlank(previousFiledVal) )	||
						(!ObjectUtils.isEmpty(tempFieldVal) && !ObjectUtils.isEmpty(previousFiledVal) && !tempFieldVal.equals(previousFiledVal) )
						){
					indShowrsnChngPsycMedDiv = ServiceConstants.STRING_IND_Y;
					break;
				}
				
				
			}
			}else {
				indShowrsnChngPsycMedDiv = ServiceConstants.STRING_IND_Y;
			}
			
			if(ServiceConstants.STRING_IND_Y.equals(indShowrsnChngPsycMedDiv)) break;
			
			}
			}
			
		return indShowrsnChngPsycMedDiv;
	}

	private void mergePrefillValues(ChildPlanOfServiceDto newchildPlanOfServiceDto,
			ChildPlanOfServiceDto childPlanOfServiceDto) {

		// Copying Prefill Editable fields
		newchildPlanOfServiceDto.getChildPlanOfServiceDtlDto().setCdEventStatus(ServiceConstants.EVENT_STATUS_PROC);
		newchildPlanOfServiceDto.getChildPlanOfServiceDtlDto()
				.setNmAgncy(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getNmAgncy());
		newchildPlanOfServiceDto.getChildPlanOfServiceDtlDto().setIdChildPlanEvent(0L);
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto())) {
			newchildPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto()
					.setIndChildSib(childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto().getIndChildSib());
			newchildPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto().setTxtTypCntctApprvd(
					childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto().getTxtTypCntctApprvd());
		}
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto())) {
			newchildPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto()
					.setIdCpQrtpPtm(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getIdCpQrtpPtm());
		}
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanEducationDto())) {
			newchildPlanOfServiceDto.getChildPlanEducationDto()
					.setTxtIepGoals(childPlanOfServiceDto.getChildPlanEducationDto().getTxtIepGoals());
		}

		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto())) {
			newchildPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto()
					.setIndChildCansAsmnt(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildCansAsmnt());
			newchildPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto()
					.setDtCansAssmt(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getDtCansAssmt());
			newchildPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto()
					.setNmClnclPrfsnl(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getNmClnclPrfsnl());
			newchildPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto()
					.setDtPsych(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getDtPsych());
			newchildPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().setNmClnclPrfsnlPsych(
					childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getNmClnclPrfsnlPsych());
		}

		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanYouthParentingDto())) {
			newchildPlanOfServiceDto.getChildPlanYouthParentingDto()
					.setIndYouthWithChild(childPlanOfServiceDto.getChildPlanYouthParentingDto().getIndYouthWithChild());
			newchildPlanOfServiceDto.getChildPlanYouthParentingDto()
					.setIndChildInDfpsCvs(childPlanOfServiceDto.getChildPlanYouthParentingDto().getIndChildInDfpsCvs());
		}

		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto())) {
			newchildPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto()
					.setNbrCswrkrPhone(childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getNbrCswrkrPhone());
			newchildPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().setChildPlanPartcptDevDtoList(
					childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getChildPlanPartcptDevDtoList());
		}
		// Copying Prefill Readonly fields

		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanInformationDto())) {
			newchildPlanOfServiceDto.getChildPlanInformationDto()
					.setNmDfpsPrmryWorker(childPlanOfServiceDto.getChildPlanInformationDto().getNmDfpsPrmryWorker());
			newchildPlanOfServiceDto.getChildPlanInformationDto()
					.setNmDfpsSupervisor(childPlanOfServiceDto.getChildPlanInformationDto().getNmDfpsSupervisor());
			newchildPlanOfServiceDto.getChildPlanInformationDto()
					.setNmCaseWorkerUnit(childPlanOfServiceDto.getChildPlanInformationDto().getNmCaseWorkerUnit());
			newchildPlanOfServiceDto.getChildPlanInformationDto()
					.setNmChild(childPlanOfServiceDto.getChildPlanInformationDto().getNmChild());
			newchildPlanOfServiceDto.getChildPlanInformationDto()
					.setDtChildBirth(childPlanOfServiceDto.getChildPlanInformationDto().getDtChildBirth());
			newchildPlanOfServiceDto.getChildPlanInformationDto()
					.setDtChildBirth(childPlanOfServiceDto.getChildPlanInformationDto().getDtChildBirth());
			newchildPlanOfServiceDto.getChildPlanInformationDto()
					.setCdChildGender(childPlanOfServiceDto.getChildPlanInformationDto().getCdChildGender());
			newchildPlanOfServiceDto.getChildPlanInformationDto()
					.setCdChildRace(childPlanOfServiceDto.getChildPlanInformationDto().getCdChildRace());
			newchildPlanOfServiceDto.getChildPlanInformationDto()
					.setIdPerson(childPlanOfServiceDto.getChildPlanInformationDto().getIdPerson());
			newchildPlanOfServiceDto.getChildPlanInformationDto()
					.setCdChildLglReg(childPlanOfServiceDto.getChildPlanInformationDto().getCdChildLglReg());
			newchildPlanOfServiceDto.getChildPlanInformationDto()
					.setCdChildLglCnty(childPlanOfServiceDto.getChildPlanInformationDto().getCdChildLglCnty());
		}

		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanOfServiceDtlDto())) {
			newchildPlanOfServiceDto.getChildPlanOfServiceDtlDto()
					.setCdChildLglStatus(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildLglStatus());
			newchildPlanOfServiceDto.getChildPlanOfServiceDtlDto()
					.setCdCurrentLvlCare(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCurrentLvlCare());

			newchildPlanOfServiceDto.getChildPlanOfServiceDtlDto()
					.setDtStartLoc(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getDtStartLoc());
			newchildPlanOfServiceDto.getChildPlanOfServiceDtlDto()
					.setDtEndLoc(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getDtEndLoc());

			newchildPlanOfServiceDto.getChildPlanOfServiceDtlDto()
					.setCdLvlCareType(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdLvlCareType());
			newchildPlanOfServiceDto.getChildPlanOfServiceDtlDto()
					.setDtPlcmt(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getDtPlcmt());
			newchildPlanOfServiceDto.getChildPlanOfServiceDtlDto()
					.setCdPlcmtTyp(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdPlcmtTyp());
			newchildPlanOfServiceDto.getChildPlanOfServiceDtlDto()
					.setNmCargvr(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getNmCargvr());
			newchildPlanOfServiceDto.getChildPlanOfServiceDtlDto().setCdChildLglStatusSub(
					childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildLglStatusSub());
			newchildPlanOfServiceDto.getChildPlanOfServiceDtlDto().setCdCspPlanType(ServiceConstants.CHILD_PLAN_RVW);
			newchildPlanOfServiceDto.getChildPlanOfServiceDtlDto()
					.setIdPerson(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIdPerson());
		}
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanEducationDto())) {
			newchildPlanOfServiceDto.getChildPlanEducationDto()
					.setNmSchoolDist(childPlanOfServiceDto.getChildPlanEducationDto().getNmSchoolDist());
			newchildPlanOfServiceDto.getChildPlanEducationDto()
					.setNmSchool(childPlanOfServiceDto.getChildPlanEducationDto().getNmSchool());
			newchildPlanOfServiceDto.getChildPlanEducationDto()
					.setCdGrade(childPlanOfServiceDto.getChildPlanEducationDto().getCdGrade());
			newchildPlanOfServiceDto.getChildPlanEducationDto()
					.setIndChildRcvngSvcs(childPlanOfServiceDto.getChildPlanEducationDto().getIndChildRcvngSvcs());
			newchildPlanOfServiceDto.getChildPlanEducationDto()
					.setIndChildEnrlldSch(childPlanOfServiceDto.getChildPlanEducationDto().getIndChildEnrlldSch());
		}
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto())) {
			newchildPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().setIndChildCnfrmdSxTrfckng(
					childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildCnfrmdSxTrfckng());
			newchildPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().setIndChildSuspdSxTrfckng(
					childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildSuspdSxTrfckng());
			newchildPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().setIndChildCnfrmdLbrTrfckng(
					childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildCnfrmdLbrTrfckng());
			newchildPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().setIndChildSuspdLbrTrfckng(
					childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildSuspdLbrTrfckng());
		}
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanYouthParentingDto())) {
			newchildPlanOfServiceDto.getChildPlanYouthParentingDto().setIndChildCurrPergnt(
					childPlanOfServiceDto.getChildPlanYouthParentingDto().getIndChildCurrPergnt());
			newchildPlanOfServiceDto.getChildPlanYouthParentingDto()
					.setIndChildGender(childPlanOfServiceDto.getChildPlanYouthParentingDto().getIndChildGender());
		}
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto())) {
			newchildPlanOfServiceDto.getChildPlanHealthCareSummaryDto().setNmPrimaryMedCons(
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getNmPrimaryMedCons());
			newchildPlanOfServiceDto.getChildPlanHealthCareSummaryDto().setNmClnclPrfsnlTxMedChkp(
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getNmClnclPrfsnlTxMedChkp());
			newchildPlanOfServiceDto.getChildPlanHealthCareSummaryDto().setTxtAddressTxMedChkp(
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtAddressTxMedChkp());
			newchildPlanOfServiceDto.getChildPlanHealthCareSummaryDto().setTxtPhoneNbrTxMedChkp(
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtPhoneNbrTxMedChkp());
		}
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanHighRiskServicesDto())) {
			newchildPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndSxAggrsvIdentfd(
					childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSxAggrsvIdentfd());
		}
		newchildPlanOfServiceDto.setIdApprovedChildPlan(childPlanOfServiceDto.getIdApprovedChildPlan());

	}

	/**
	 * Method Name: getChildPlanInfo Method Description: This method is used to
	 * retrieve values from different tables for the display in child plan.
	 * 
	 * @param childPlanDtlReq
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("incomplete-switch")
	private ChildPlanOfServiceDto getChildPlanInfo(ChildPlanDtlReq childPlanDtlReq) {

		ChildPlanOfServiceDto childPlanOfServiceDto = childPlanDtlReq.getChildPlanOfServiceDto();
		ChildPlanOfServiceDtlDto childPlanOfServiceDtlDto = childPlanOfServiceDto.getChildPlanOfServiceDtlDto();
		Long idChildPlanEvent = childPlanOfServiceDtlDto.getIdChildPlanEvent();
		ChildPlanTransAdltAbvDtlDto transAdltAbvDtl = null;
		ChildPlanTransAdtltBlwDtlDto transAdtltBlwDtl = null;
		childPlanOfServiceDtlDto = childPlanDtlDao.getChildPlanDtlInfo(idChildPlanEvent, childPlanOfServiceDtlDto);
		
		switch (childPlanDtlReq.getSectionType()) {

		case INITIAL:
			if (!ObjectUtils.isEmpty(idChildPlanEvent) && idChildPlanEvent > 0) {
				childPlanOfServiceDtlDto.setCdEventStatus(eventDao.getEventStatus(idChildPlanEvent));
			} else {
				childPlanOfServiceDtlDto.setCdEventStatus(ServiceConstants.NEW_EVENT);
			}
			// to get the override fields on load as the design changed , the
			// services are changed too .
			childPlanOfServiceDto = getInitialData(childPlanOfServiceDto, idChildPlanEvent);
			transAdltAbvDtl = childPlanDtlDao.getTransAdltAbvDtl(idChildPlanEvent);
			childPlanOfServiceDto.setTransAdulthoodAboveFourteenDto(transAdltAbvDtl);
			transAdtltBlwDtl = childPlanDtlDao.getTransAdtltBlwDtl(idChildPlanEvent);
			childPlanOfServiceDto.setTransAdulthoodBelowThirteenDto(transAdtltBlwDtl);

			break;
		case ADOPTION:
			childPlanOfServiceDtlDto = childPlanDtlDao.getChildPlanDtlInfo(idChildPlanEvent, childPlanOfServiceDtlDto);
			childPlanOfServiceDto.setChildPlanOfServiceDtlDto(childPlanOfServiceDtlDto);
			List<ChildPlanPriorAdpInfoDto> priorAdptn = childPlanDtlDao.getPriorAdptn(idChildPlanEvent);
			if (!ObjectUtils.isEmpty(priorAdptn)) {
				List<ChildPlanPriorAdpInfoDto> priorAdptnDomestic = priorAdptn.stream()
						.filter(c -> ServiceConstants.DOMESTIC.equals(c.getCdChildAdoptdType()))
						.collect(Collectors.toList());
				List<ChildPlanPriorAdpInfoDto> priorAdptnInternational = priorAdptn.stream()
						.filter(c -> ServiceConstants.INTERNATIONAL.equals(c.getCdChildAdoptdType()))
						.collect(Collectors.toList());
				childPlanOfServiceDto.setChildPlanPriorAdpInfoDomesticDtoList(priorAdptnDomestic);
				childPlanOfServiceDto.setChildPlanPriorAdpInfoInterntlDtoList(priorAdptnInternational);
			}

			List<ChildPlanLegalGrdnshpDto> legalGrardianship = childPlanDtlDao.getLegalGuardianship(idChildPlanEvent);
			childPlanOfServiceDto.setChildPlanLegalGrdnshpDtoList(legalGrardianship);
			break;
		case PLAN_VISITATION:
			ChildPlanVisitationCnctFmlyDto planVisitation = childPlanDtlDao.getPlanVisitation(idChildPlanEvent);
			childPlanOfServiceDto.setChildPlanVisitationCnctFmlyDto(planVisitation);
			break;
		case QRTP_PERMANENCY:
			ChildPlanQrtpPrmnncyMeetingDto qrtpPTMtng = childPlanDtlDao.getQrtpPermanencyMtng(idChildPlanEvent);
			if("PROC".equalsIgnoreCase(eventDao.getEventStatus(idChildPlanEvent)) && !ObjectUtils.isEmpty(qrtpPTMtng)){
				qrtpPTMtng.setTxtAssesmntRecmndatns(getQrtpRecommendations(childPlanDtlReq));
			}
			childPlanOfServiceDto.setChildPlanQrtpPrmnncyMeetingDto(qrtpPTMtng);
			break;
		case INTELLECTUAL_DEVELOP:
			ChildPlanIntellectualDevelopDto intellectualDevelop = childPlanDtlDao
					.getIntellectualDevelop(idChildPlanEvent);
			childPlanOfServiceDto.setChildPlanIntellectualDevelopDto(intellectualDevelop);
			break;
		case EDUCATION:
			ChildPlanEducationDto educationDto = childPlanDtlDao.getChildEducation(idChildPlanEvent);
			childPlanOfServiceDto.setChildPlanEducationDto(educationDto);
			break;
		case EMOTIONAL_PYSC:
			ChildPlanEmtnlThrptcDtlDto emtnlThrptcDtlDto = childPlanDtlDao.getEmtnlThrptcDtl(idChildPlanEvent);
			childPlanOfServiceDto.setChildPlanEmtnlThrptcDtlDto(emtnlThrptcDtlDto);
			break;
		case BEHAVIOR_MNGT:
			ChildPalnBehaviorMgntDto behaviorMgnt = childPlanDtlDao.getBehaviorMgnt(idChildPlanEvent);
			childPlanOfServiceDto.setChildPalnBehaviorMgntDto(behaviorMgnt);
			break;
		case YOUTH_PARENTING:
			ChildPlanYouthParentingDto youthParenting = childPlanDtlDao.getYouthParenting(idChildPlanEvent);
			childPlanOfServiceDto.setChildPlanYouthParentingDto(youthParenting);
			break;
		case HEALTHCARE_SUMMARY:
			ChildPlanHealthCareSummaryDto healthCareSummary = childPlanDtlDao.getHealthCareSummary(idChildPlanEvent,
					childPlanOfServiceDtlDto.getIdStage());			
			childPlanOfServiceDto.setChildPlanHealthCareSummaryDto(healthCareSummary);
			break;
		case SUPERVISION:
			ChildPlanSupervisionDto supervision = childPlanDtlDao.getSupervision(idChildPlanEvent);
			childPlanOfServiceDto.setChildPlanSupervisionDto(supervision);
			break;
		case SOCIAL_RECREATIONAL:
			ChildPlanSocialRecreationalDto socialRecreational = childPlanDtlDao.getSocialRecreational(idChildPlanEvent);
			childPlanOfServiceDto.setChildPlanSocialRecreationalDto(socialRecreational);
			break;
		case TRANS_ADTT_ABV_DTL:
			transAdltAbvDtl = childPlanDtlDao.getTransAdltAbvDtl(idChildPlanEvent);
			childPlanOfServiceDto.setTransAdulthoodAboveFourteenDto(transAdltAbvDtl);
			break;
		case TRANS_ADTT_BLW_DTL:
			transAdtltBlwDtl = childPlanDtlDao.getTransAdtltBlwDtl(idChildPlanEvent);
			childPlanOfServiceDto.setTransAdulthoodBelowThirteenDto(transAdtltBlwDtl);
			break;
		case HIGH_RISK_BEHAVIOR:
			ChildPlanHighRiskServicesDto highRiskServicesDto = childPlanDtlDao.getHighRiskServices(idChildPlanEvent);
			childPlanOfServiceDto.setChildPlanHighRiskServicesDto(highRiskServicesDto);
			break;
		case TREATMENT_SERVICES:
			ChildPlanTreatmentServiceDto treatmentServiceDto = childPlanDtlDao.getTreatmentService(idChildPlanEvent);
			childPlanOfServiceDto.setChildPlanTreatmentServiceDto(treatmentServiceDto);
			break;
		case FMLY_TEAM_PRTCPTN:
			ChildPlanFmlyTeamPrtctpnDto fmlyTeamPrtctpnDto = childPlanDtlDao.getFmlyTeamPrtctpn(idChildPlanEvent);
			if (!ObjectUtils.isEmpty(fmlyTeamPrtctpnDto)) {
				/*fmlyTeamPrtctpnDto.setNbrAtrnyPhone(TypeConvUtil.formatPhone(fmlyTeamPrtctpnDto.getNbrAtrnyPhone()));
				fmlyTeamPrtctpnDto.setNbrCswrkrPhone(TypeConvUtil.formatPhone(fmlyTeamPrtctpnDto.getNbrCswrkrPhone()));
				fmlyTeamPrtctpnDto.setNbrGrdnPhone(TypeConvUtil.formatPhone(fmlyTeamPrtctpnDto.getNbrGrdnPhone()));*/
				childPlanOfServiceDto.setChildPlanFmlyTeamPrtctpnDto(fmlyTeamPrtctpnDto);
			}
			break;

		case ALL:
			childPlanOfServiceDto = getInitialData(childPlanOfServiceDto, idChildPlanEvent);
			childPlanOfServiceDto = childPlanDtlDao.getAllChildPlanDetails(idChildPlanEvent, childPlanOfServiceDto);
			break;

		}
		return childPlanOfServiceDto;
	}

	private String getQrtpRecommendations(ChildPlanDtlReq childPlanDtlReq) {
		return childPlanDtlDao.getQrtpRecommendations(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdStage());
	}

	private ChildPlanOfServiceDto getInitialData(ChildPlanOfServiceDto childPlanOfServiceDto, Long idChildPlanEvent) {
		ChildPlanOfServiceDtlDto childPlanOfServiceDtlDto = childPlanOfServiceDto.getChildPlanOfServiceDtlDto();
		ChildPlanInformationDto informationDto = childPlanDtlDao.getChildPlanInformation(idChildPlanEvent);
		childPlanOfServiceDtlDto = childPlanDtlDao.getChildPlanDtlInfo(idChildPlanEvent, childPlanOfServiceDtlDto);
		ChildPlanAdtnlSctnDtlDto adtnlSctnDtl = childPlanDtlDao.getAdtnlSctnDtl(idChildPlanEvent);

		// For new Using
		/*if (idChildPlanEvent > 0 && childPlanOfServiceDto.getIdApprovedChildPlan().equals(idChildPlanEvent)) {
			if (!ObjectUtils.isEmpty(informationDto)) {
				informationDto.setIdChildPlanEvent(null);
				informationDto.setIdCpInformation(null);
				informationDto.setIdCreatedPerson(null);
				informationDto.setDtCreated(null);
				informationDto.setIdLastUpdatePerson(null);
				informationDto.setDtLastUpdate(null);
			}
			if (!ObjectUtils.isEmpty(childPlanOfServiceDtlDto)) {
				childPlanOfServiceDtlDto.setIdChildPlanEvent(null);
				childPlanOfServiceDtlDto.setDtLastUpdate(null);
				childPlanOfServiceDtlDto.setDtLastUpdateExtrnl(null);
			}
			if (!ObjectUtils.isEmpty(adtnlSctnDtl)) {
				adtnlSctnDtl.setIdChildPlanEvent(null);
				adtnlSctnDtl.setIdCpAdtnlSctnDtls(null);
				adtnlSctnDtl.setDtLastUpdate(null);
				adtnlSctnDtl.setIdLastUpdatePerson(null);
			}
		}*/
		childPlanOfServiceDto.setChildPlanInformationDto(informationDto);
		childPlanOfServiceDto.setChildPlanOfServiceDtlDto(childPlanOfServiceDtlDto);
		childPlanOfServiceDto.setChildPlanAdtnlSctnDtlDto(adtnlSctnDtl);
		return childPlanOfServiceDto;
	}

	private ChildPlanOfServiceDto getInitialDataOfPreviousApprovedPlan(ChildPlanOfServiceDto childPlanOfServiceDto,
			Long idChildPlanEvent) {
		ChildPlanOfServiceDtlDto childPlanOfServiceDtlDto = childPlanOfServiceDto.getChildPlanOfServiceDtlDto();
		String[] ignorePropertiesArray = { "idChildPlanEvent", "dtLastUpdate", "dtLastUpdateExtrnl","idLastUpdateExtrnlPerson",
				"idLastUpdatePerson", "dtLastUpdateCps", "cdPlcmtTyp", "nmCargvr", "nmAgncy", "cdCurrentLvlCare","cdLvlCareType",
				"dtStartLoc", "dtEndLoc", "dtPlcmt", "cdChildLglStatus", "cdChildLglStatusSub", "cdCspPlanType",
				"cdEventStatus","dtCVSRemoval","idPerson","idCase","idStage","indChildRcvngSvcs", "indChildCareCordntnSrvc",
				"indChildHmnTrfckngAdvSrvc", "indChildCnslnHmnTrfckngVct", "indChildSxlExptnIdntfn", "selRecntScore",
				"indChildHmnTrfckngSrvc", "ckbCrisIntSrvc", "ckbDrpInCntrSrvc", "ckbEmplSupSrvc", "ckbSubAbuSrvc", "ckbSurPeerSupGrp",
				"ckbOthrSrvc", "ckbNoOthrSrvc", "txtChildOthrSrvcRcvd", "txtSpcfcSvcsForChild"};
		BeanUtils.copyProperties(childPlanDtlDao.getChildPlanDtlInfo(idChildPlanEvent, new ChildPlanOfServiceDtlDto()),
				childPlanOfServiceDtlDto, ignorePropertiesArray);
		ChildPlanAdtnlSctnDtlDto adtnlSctnDtl = childPlanDtlDao.getAdtnlSctnDtl(idChildPlanEvent);

		if (!ObjectUtils.isEmpty(adtnlSctnDtl)) {
			adtnlSctnDtl.setIdChildPlanEvent(null);
			adtnlSctnDtl.setIdCpAdtnlSctnDtls(null);
			adtnlSctnDtl.setDtLastUpdate(null);
			adtnlSctnDtl.setIdLastUpdatePerson(null);
			adtnlSctnDtl.setIdCreatedPerson(null);
		}

		childPlanOfServiceDto.setChildPlanOfServiceDtlDto(childPlanOfServiceDtlDto);
		childPlanOfServiceDto.setChildPlanAdtnlSctnDtlDto(adtnlSctnDtl);
		return childPlanOfServiceDto;
	}

	/**
	 * Method Name: getPrefillReadOnlyInfo Method Description:
	 * 
	 * @param childPlanOfServiceDto
	 * @return
	 */
	private ChildPlanOfServiceDto getPrefillReadOnlyInfo(ChildPlanOfServiceDto childPlanOfServiceDto) {
		return childPlanDtlDao.getPrefillReadOnlyInfo(childPlanOfServiceDto);
	}

	/**
	 * Method Name: getPrefillEditableInfo Method Description:
	 * 
	 * @param childPlanOfServiceDto
	 * @return
	 */
	private ChildPlanOfServiceDto getPrefillEditableInfo(ChildPlanOfServiceDto childPlanOfServiceDto) {
		childPlanOfServiceDto = childPlanDtlDao.getPrefillEditableInfo(childPlanOfServiceDto);		
		return childPlanOfServiceDto;
	}

	/**
	 * Method Name: saveChildPlanDtl Method Description: This method is used to
	 * save the fields in child plan page.
	 * 
	 * @param childPlanDtlReq
	 * @return ChildPlanDtlRes
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public ChildPlanDtlRes saveChildPlanDtl(ChildPlanDtlReq childPlanDtlReq) {
		ChildPlanDtlRes childPlanDtlRes = null;
		boolean isNewEvent = false;
		// event creation
		//this is coming as empty for new plan 
		String cdEventStatus = childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto()
				.getCdEventStatus();
		
		Long eventId = childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdChildPlanEvent();
		if (!ObjectUtils.isEmpty(eventId) && eventId > ServiceConstants.ZERO && StringUtils.isBlank(cdEventStatus)) {
			cdEventStatus = eventDao.getEventStatus(eventId);
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().setCdEventStatus(cdEventStatus);
		}
		if (ObjectUtils.isEmpty(eventId) || eventId == ServiceConstants.ZERO
				|| (ServiceConstants.EVENTSTATUS_NEW.equalsIgnoreCase(cdEventStatus) && ObjectUtils.isEmpty(eventId) && eventId == ServiceConstants.ZERO ) )  {
			isNewEvent = ServiceConstants.TRUE_VALUE;
		}
		
		if (!childPlanDtlReq.getChildPlanOfServiceDto().isApprovalMode()) {
			if (ServiceConstants.EVENT_STATUS_PEND.equals(cdEventStatus)) {
				ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
				ApprovalCommonOutDto approvalCommonOutDto = new ApprovalCommonOutDto();
				approvalCommonInDto.setIdEvent(eventId);
				// Call Service to invalidate approval
				approvalCommonService.InvalidateAprvl(approvalCommonInDto, approvalCommonOutDto);
				cdEventStatus = ServiceConstants.EVENT_STATUS_COMP;
			}

			List<EventDto> eventList = eventDao.getEventByStageIDAndTaskCode(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdStage(),
					ServiceConstants.CLOSE_SUB_STAGE);			

			if (!ObjectUtils.isEmpty(eventList)) {
				EventDto stageClosureEvent = eventList.stream()
						.filter(x -> ServiceConstants.EVENT_STATUS_PEND.equals(x.getCdEventStatus())).findAny()
						.orElse(null);
				if (!ObjectUtils.isEmpty(stageClosureEvent)) {
					ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
					ApprovalCommonOutDto approvalCommonOutDto = new ApprovalCommonOutDto();
					approvalCommonInDto.setIdEvent(stageClosureEvent.getIdEvent());
					// Call Service to invalidate approval
					approvalCommonService.InvalidateAprvl(approvalCommonInDto, approvalCommonOutDto);				

				}
			}
		}
		if (isNewEvent || (childPlanDtlReq.getChildPlanOfServiceDto().getSaveAndSubmit()
				&& (ObjectUtils.isEmpty(eventId) || eventId == ServiceConstants.ZERO))) {
			// This Method will create an new event and update the existing
			// event record.
			eventId = childPlanDtlDao.createAndReturnEventid(childPlanDtlReq,false);
		}// this is for the case when the child plan is created in NEW state from conservatorship , 
		//but event id is present, so it has to be made to PROC on save or COMP on svae and submit
		else if(ServiceConstants.EVENTSTATUS_NEW.equalsIgnoreCase(cdEventStatus) && !ObjectUtils.isEmpty(eventId) && eventId > ServiceConstants.ZERO){
			// made to true to save other sections
			isNewEvent=true;
			childPlanDtlDao.createAndReturnEventid(childPlanDtlReq,true);
		}

		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().setIdChildPlanEvent(eventId);
			childPlanDtlRes = saveChildPlanDtl(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto(),
					childPlanDtlReq.getUserId(), childPlanDtlReq.getExternalUser());
			if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
				return childPlanDtlRes;
			}
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanPriorAdpInfoDomesticDtoList())
				|| !ObjectUtils.isEmpty(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanPriorAdpInfoInterntlDtoList())) {
			List<ChildPlanPriorAdpInfoDto> priorAdpInfoDto = new ArrayList<>();
			if (!ObjectUtils
					.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanPriorAdpInfoDomesticDtoList())) {
				childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanPriorAdpInfoDomesticDtoList()
						.forEach(f -> f.setCdChildAdoptdType(ServiceConstants.DOMESTIC));
				priorAdpInfoDto
						.addAll(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanPriorAdpInfoDomesticDtoList());
			}

			if (!ObjectUtils
					.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanPriorAdpInfoInterntlDtoList())) {
				childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanPriorAdpInfoInterntlDtoList()
						.forEach(f -> f.setCdChildAdoptdType(ServiceConstants.INTERNATIONAL));
				priorAdpInfoDto
						.addAll(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanPriorAdpInfoInterntlDtoList());
			}

			childPlanDtlRes = savePriorAdoption(priorAdpInfoDto, eventId, childPlanDtlReq.getUserId(), isNewEvent);
			if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
				throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		if (ServiceConstants.ADPTN_NO.equals(
				childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getCdChildAdoptdDmstc())
				|| ServiceConstants.ADPTN_UNABLE_TO_DETERMINE.equals(childPlanDtlReq.getChildPlanOfServiceDto()
						.getChildPlanOfServiceDtlDto().getCdChildAdoptdDmstc())) {
			childPlanDtlDao.deletePriorAdoptions(eventId, ServiceConstants.DOMESTIC);
		}
		if (ServiceConstants.ADPTN_NO.equals(
				childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getCdChildAdoptdIntnl())
				|| ServiceConstants.ADPTN_UNABLE_TO_DETERMINE.equals(childPlanDtlReq.getChildPlanOfServiceDto()
						.getChildPlanOfServiceDtlDto().getCdChildAdoptdIntnl())) {
			childPlanDtlDao.deletePriorAdoptions(eventId, ServiceConstants.INTERNATIONAL);
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanLegalGrdnshpDtoList())) {
			childPlanDtlRes = saveLegalGuardian(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanLegalGrdnshpDtoList(), eventId,
					childPlanDtlReq.getUserId());
			if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
				throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		else if (ServiceConstants.ADPTN_NO.equals(
				childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getCdChildLglGrdnship())
				|| ServiceConstants.ADPTN_UNABLE_TO_DETERMINE.equals(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto()
						.getCdChildLglGrdnship())) {
			childPlanDtlDao.deleteLegalGuardians(eventId);
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanVisitationCnctFmlyDto())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanVisitationCnctFmlyDto().setIdChildPlanEvent(eventId);
			if ((isNewEvent && ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
					.getChildPlanVisitationCnctFmlyDto().getIdCpVisitCntctFmly()))
					|| !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
							.getChildPlanVisitationCnctFmlyDto().getIdCpVisitCntctFmly())) {
				childPlanDtlRes = savePlanVisitation(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanVisitationCnctFmlyDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanQrtpPrmnncyMeetingDto())
		   && !StringUtils.isBlank(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanQrtpPrmnncyMeetingDto().getIndCurrentQrtp())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanQrtpPrmnncyMeetingDto().setIdChildPlanEvent(eventId);
			if ((isNewEvent && ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
					.getChildPlanQrtpPrmnncyMeetingDto().getIdCpQrtpPtm()))
					|| !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
					.getChildPlanQrtpPrmnncyMeetingDto().getIdCpQrtpPtm())
					|| ((ServiceConstants.EVENTSTATUS_NEW.equalsIgnoreCase(cdEventStatus) || ServiceConstants.EVENTSTATUS_PROC.
					equalsIgnoreCase(cdEventStatus) || ServiceConstants.EVENTSTATUS_PENDING.equalsIgnoreCase(cdEventStatus) ||
					ServiceConstants.EVENTSTATUS_COMPLETE.equalsIgnoreCase(cdEventStatus)) && ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
					.getChildPlanQrtpPrmnncyMeetingDto().getIdCpQrtpPtm()))) {
				childPlanDtlRes = saveQrtpPermanencyMtng(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanQrtpPrmnncyMeetingDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanIntellectualDevelopDto())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanIntellectualDevelopDto()
					.setIdChildPlanEvent(eventId);
			if ((isNewEvent && ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
					.getChildPlanIntellectualDevelopDto().getIdCpIntlctlDvlpmntl()))
					|| !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
							.getChildPlanIntellectualDevelopDto().getIdCpIntlctlDvlpmntl())) {

				childPlanDtlRes = saveIntellectualDevelop(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanIntellectualDevelopDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			if (ServiceConstants.Y.equals(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanIntellectualDevelopDto().getIndNaGoal())) {
				childPlanDtlDao.deleteGoals(eventId, ChildPlanDtlDaoImpl.TOPICS_CCID);
			}
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanEducationDto())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanEducationDto().setIdChildPlanEvent(eventId);
			if ((isNewEvent && ObjectUtils
					.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanEducationDto().getIdEductnDtl()))
					|| !ObjectUtils.isEmpty(
							childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanEducationDto().getIdEductnDtl())) {
				childPlanDtlRes = saveEducation(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanEducationDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			if (ServiceConstants.Y.equals(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanEducationDto().getIndNaGoal())) {
				childPlanDtlDao.deleteGoals(eventId, ChildPlanDtlDaoImpl.TOPICS_CEDU);
			}
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanEmtnlThrptcDtlDto())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanEmtnlThrptcDtlDto().setIdChildPlanEvent(eventId);
			if ((isNewEvent && ObjectUtils.isEmpty(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanEmtnlThrptcDtlDto().getIdEmtnlTpDtl()))
					|| !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanEmtnlThrptcDtlDto()
							.getIdEmtnlTpDtl())) {
				childPlanDtlRes = saveEmtnlThrptcDtl(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanEmtnlThrptcDtlDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			if (ServiceConstants.Y.equals(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanEmtnlThrptcDtlDto().getIndNaGoal())) {
				childPlanDtlDao.deleteGoals(eventId, ChildPlanDtlDaoImpl.TOPICS_CETP);
			}
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPalnBehaviorMgntDto())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPalnBehaviorMgntDto().setIdChildPlanEvent(eventId);
			if ((isNewEvent && ObjectUtils
					.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPalnBehaviorMgntDto().getIdBhvrMgmt()))
					|| !ObjectUtils.isEmpty(
							childPlanDtlReq.getChildPlanOfServiceDto().getChildPalnBehaviorMgntDto().getIdBhvrMgmt())) {
				childPlanDtlRes = saveBehaviorMgnt(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPalnBehaviorMgntDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			if (ServiceConstants.Y.equals(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPalnBehaviorMgntDto().getIndNaGoal())) {
				childPlanDtlDao.deleteGoals(eventId, ChildPlanDtlDaoImpl.TOPICS_CBMG);
			}
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanYouthParentingDto())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanYouthParentingDto().setIdChildPlanEvent(eventId);
			if ((isNewEvent && ObjectUtils.isEmpty(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanYouthParentingDto().getIdYouthPregntPrntg()))
					|| !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanYouthParentingDto()
							.getIdYouthPregntPrntg())) {
				childPlanDtlRes = saveYouthParenting(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanYouthParentingDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanHealthCareSummaryDto())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanHealthCareSummaryDto().setIdChildPlanEvent(eventId);

			if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanHealthCareSummaryDto()
					.getChidPlanNonPsychMedctnDtlDtoList())
					|| !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
							.getChildPlanHealthCareSummaryDto().getChidPlanPsychMedctnDtlDtoList())) {
				List<ChidPlanPsychMedctnDtlDto> chidPlanPsychMedctnDtlDto = new ArrayList<>();
				if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanHealthCareSummaryDto()
						.getChidPlanNonPsychMedctnDtlDtoList())) {
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanHealthCareSummaryDto()
							.getChidPlanNonPsychMedctnDtlDtoList()
							.forEach(f -> f.setCdMedctnType(ServiceConstants.NPSY));
					chidPlanPsychMedctnDtlDto.addAll(childPlanDtlReq.getChildPlanOfServiceDto()
							.getChildPlanHealthCareSummaryDto().getChidPlanNonPsychMedctnDtlDtoList());
				}

				if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanHealthCareSummaryDto()
						.getChidPlanPsychMedctnDtlDtoList())) {
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanHealthCareSummaryDto()
							.getChidPlanPsychMedctnDtlDtoList().forEach(f -> f.setCdMedctnType(ServiceConstants.PSY));
					chidPlanPsychMedctnDtlDto.addAll(childPlanDtlReq.getChildPlanOfServiceDto()
							.getChildPlanHealthCareSummaryDto().getChidPlanPsychMedctnDtlDtoList());
				}
				childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanHealthCareSummaryDto()
						.setChidPlanPsychMedctnDtlDtoList(chidPlanPsychMedctnDtlDto);

			}
			if ((isNewEvent && ObjectUtils.isEmpty(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanHealthCareSummaryDto().getIdHlthCareSumm()))
					|| !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
							.getChildPlanHealthCareSummaryDto().getIdHlthCareSumm())) {

				childPlanDtlRes = saveHealthCareSummary(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanHealthCareSummaryDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			if (ServiceConstants.Y.equals(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanHealthCareSummaryDto().getIndNaGoal())) {
				childPlanDtlDao.deleteGoals(eventId, ChildPlanDtlDaoImpl.TOPICS_CHSR);
			}
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanSupervisionDto())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanSupervisionDto().setIdChildPlanEvent(eventId);
			if ((isNewEvent && ObjectUtils.isEmpty(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanSupervisionDto().getIdCpSprvsnDtl()))
					|| !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanSupervisionDto()
							.getIdCpSprvsnDtl())) {

				childPlanDtlRes = saveSupervision(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanSupervisionDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			if (ServiceConstants.Y.equals(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanSupervisionDto().getIndNaGoal())) {
				childPlanDtlDao.deleteGoals(eventId, ChildPlanDtlDaoImpl.TOPICS_CSUP);
			}
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanSocialRecreationalDto())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanSocialRecreationalDto().setIdChildPlanEvent(eventId);
			if ((isNewEvent && ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
					.getChildPlanSocialRecreationalDto().getIdSoclRecrtnalDtl()))
					|| !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
							.getChildPlanSocialRecreationalDto().getIdSoclRecrtnalDtl())) {
				childPlanDtlRes = saveSocialRecreational(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanSocialRecreationalDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			if (ServiceConstants.Y.equals(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanSocialRecreationalDto().getIndNaGoal())) {
				childPlanDtlDao.deleteGoals(eventId, ChildPlanDtlDaoImpl.TOPICS_CCSR);
			}
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getTransAdulthoodAboveFourteenDto())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getTransAdulthoodAboveFourteenDto().setIdChildPlanEvent(eventId);
			if ((isNewEvent && ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
					.getTransAdulthoodAboveFourteenDto().getIdTtsAdultAbvDtl()))
					|| !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
							.getTransAdulthoodAboveFourteenDto().getIdTtsAdultAbvDtl())) {

				childPlanDtlRes = saveTransAdulthoodAboveFourteen(
						childPlanDtlReq.getChildPlanOfServiceDto().getTransAdulthoodAboveFourteenDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			if (ServiceConstants.Y.equals(
					childPlanDtlReq.getChildPlanOfServiceDto().getTransAdulthoodAboveFourteenDto().getIndNaGoal())) {
				childPlanDtlDao.deleteGoals(eventId, ChildPlanDtlDaoImpl.TOPICS_CU14);
			}
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getTransAdulthoodBelowThirteenDto())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getTransAdulthoodBelowThirteenDto().setIdChildPlanEvent(eventId);
			if ((isNewEvent && ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
					.getTransAdulthoodBelowThirteenDto().getIdTtsAdultBlwDtl()))
					|| !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
							.getTransAdulthoodBelowThirteenDto().getIdTtsAdultBlwDtl())) {

				childPlanDtlRes = saveTransAdulthoodBelowThirteen(
						childPlanDtlReq.getChildPlanOfServiceDto().getTransAdulthoodBelowThirteenDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			if (ServiceConstants.Y.equals(
					childPlanDtlReq.getChildPlanOfServiceDto().getTransAdulthoodBelowThirteenDto().getIndNaGoal())) {
				childPlanDtlDao.deleteGoals(eventId, ChildPlanDtlDaoImpl.TOPICS_CU13);
			}
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanHighRiskServicesDto())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanHighRiskServicesDto().setIdChildPlanEvent(eventId);
			if ((isNewEvent && ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
					.getChildPlanHighRiskServicesDto().getIdSvcsHghRiskBhvr()))
					|| !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanHighRiskServicesDto()
							.getIdSvcsHghRiskBhvr())) {

				childPlanDtlRes = saveHighRiskServices(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanHighRiskServicesDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanTreatmentServiceDto())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanTreatmentServiceDto().setIdChildPlanEvent(eventId);
			if ((isNewEvent && ObjectUtils.isEmpty(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanTreatmentServiceDto().getIdTrtmntSrvcDtl()))
					|| !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanTreatmentServiceDto()
							.getIdTrtmntSrvcDtl())) {

				childPlanDtlRes = saveTreatmentService(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanTreatmentServiceDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			if (ServiceConstants.Y.equals(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanTreatmentServiceDto().getIndNaGoal())) {
				childPlanDtlDao.deleteGoals(eventId, ChildPlanDtlDaoImpl.TOPICS_CCTS);
			}
		}
		
		if(ServiceConstants.N.equals(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIndChildRcvngSvcs())){
			childPlanDtlDao.deleteTreatmentService(eventId, ChildPlanDtlDaoImpl.TOPICS_CCTS);
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanFmlyTeamPrtctpnDto())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanFmlyTeamPrtctpnDto().setIdChildPlanEvent(eventId);
			if ((isNewEvent && ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto()
					.getChildPlanFmlyTeamPrtctpnDto().getIdChildFmlyTeamDtl()))
					|| !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanFmlyTeamPrtctpnDto()
							.getIdChildFmlyTeamDtl())) {

				childPlanDtlRes = saveFmlyTeamPrtctpn(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanFmlyTeamPrtctpnDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanAdtnlSctnDtlDto())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanAdtnlSctnDtlDto().setIdChildPlanEvent(eventId);
			if ((isNewEvent && ObjectUtils.isEmpty(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanAdtnlSctnDtlDto().getIdCpAdtnlSctnDtls()))
					|| !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanAdtnlSctnDtlDto()
							.getIdCpAdtnlSctnDtls())) {

				childPlanDtlRes = saveAdtnlSctnDtl(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanAdtnlSctnDtlDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanInformationDto())
				&& !ObjectUtils.isEmpty(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanInformationDto().getIdPerson())) {
			childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanInformationDto().setIdChildPlanEvent(eventId);
			if ((isNewEvent && ObjectUtils.isEmpty(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanInformationDto().getIdCpInformation()))
					|| !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanInformationDto()
							.getIdCpInformation())) {

				childPlanDtlRes = saveChildPlanInfo(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanInformationDto(),
						childPlanDtlReq.getUserId());
				if (!ObjectUtils.isEmpty(childPlanDtlRes) && !ObjectUtils.isEmpty(childPlanDtlRes.getErrorDto())) {
					throw new ServiceLayerException(null, childPlanDtlRes.getErrorDto().getErrorCode(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}
		// for creating alerts

		if (isNewEvent && ServiceConstants.REVIEW.equalsIgnoreCase(
				childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getCdCspPlanType())) {

			if (Boolean.TRUE.equals(childPlanDtlReq.getExternalUser())
					&& !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto())
					&& !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanInformationDto())
					&& !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto())
					&& !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanInformationDto()
							.getIdDfpsPrmryWorker())) {
				alertService.createAlert(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdStage(),
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanInformationDto().getIdDfpsPrmryWorker(),
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdPerson(),
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdCase(),
						ServiceConstants.CHILD_PLAN_REVIEW_INITIATED_EXT, new Date());
			} else if (Boolean.FALSE.equals(childPlanDtlReq.getExternalUser())
					&& !ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto())) {
				alertService.createAlert(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdStage(),
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdPerson(),
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdPerson(),
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdCase(),
						ServiceConstants.CHILD_PLAN_REVIEW_INITIATED_NON_EXT, new Date());
			}

		}

		if (ObjectUtils.isEmpty(childPlanDtlRes)) {
			childPlanDtlRes = new ChildPlanDtlRes();
			ChildPlanOfServiceDto childPlanOfServiceDto = new ChildPlanOfServiceDto();
			ChildPlanOfServiceDtlDto childPlanOfServiceDtlDto = new ChildPlanOfServiceDtlDto();
			childPlanOfServiceDtlDto.setIdChildPlanEvent(eventId);
			childPlanDtlRes.setChildPlanOfServiceDto(childPlanOfServiceDto);
			childPlanDtlRes.getChildPlanOfServiceDto().setChildPlanOfServiceDtlDto(childPlanOfServiceDtlDto);
		}
		return childPlanDtlRes;

	}

	/**
	 * Method Name: saveChildPlanInfo Method Description: This method is used to
	 * save child plan information section.
	 * 
	 * @param childPlanInformationDto
	 */
	private ChildPlanDtlRes saveChildPlanInfo(ChildPlanInformationDto childPlanInformationDto, String szUserId) {
		return childPlanDtlDao.saveChildPlanInfo(childPlanInformationDto, szUserId);

	}

	/**
	 * Method Name: saveAdtnlSctnDtl Method Description:This method is used to
	 * save child plan additional section details.
	 * 
	 * @param childPlanAdtnlSctnDtlDto
	 */
	private ChildPlanDtlRes saveAdtnlSctnDtl(ChildPlanAdtnlSctnDtlDto childPlanAdtnlSctnDtlDto, String szUserId) {
		return childPlanDtlDao.saveAdtnlSctnDtl(childPlanAdtnlSctnDtlDto, szUserId);

	}

	/**
	 * Method Name: saveFmlyTeamPrtctpn Method Description: This method is used
	 * to save child plan family team participation section.
	 * 
	 * @param childPlanFmlyTeamPrtctpnDto
	 */
	private ChildPlanDtlRes saveFmlyTeamPrtctpn(ChildPlanFmlyTeamPrtctpnDto childPlanFmlyTeamPrtctpnDto,
			String szUserId) {
		return childPlanDtlDao.saveFmlyTeamPrtctpn(childPlanFmlyTeamPrtctpnDto, szUserId);

	}

	/**
	 * Method Name: saveTreatmentService Method Description: This method is used
	 * to save child plan treatment services section.
	 * 
	 * @param childPlanTreatmentServiceDto
	 */
	private ChildPlanDtlRes saveTreatmentService(ChildPlanTreatmentServiceDto childPlanTreatmentServiceDto,
			String szUserId) {
		return childPlanDtlDao.saveTreatmentService(childPlanTreatmentServiceDto, szUserId);

	}

	/**
	 * Method Name: saveHighRiskServices Method Description: This method is used
	 * to save the high risk services section.
	 * 
	 * @param childPlanHighRiskServicesDto
	 */
	private ChildPlanDtlRes saveHighRiskServices(ChildPlanHighRiskServicesDto childPlanHighRiskServicesDto,
			String szUserId) {
		return childPlanDtlDao.saveHighRiskServices(childPlanHighRiskServicesDto, szUserId);

	}

	/**
	 * Method Name: saveTransAdulthoodBelowThirteen Method Description: This
	 * method is used to save Transition of adulthood below thirteen section.
	 * 
	 * @param transAdulthoodBelowThirteenDto
	 */
	private ChildPlanDtlRes saveTransAdulthoodBelowThirteen(ChildPlanTransAdtltBlwDtlDto transAdulthoodBelowThirteenDto,
			String szUserId) {
		return childPlanDtlDao.saveTransAdulthoodBelowThirteen(transAdulthoodBelowThirteenDto, szUserId);

	}

	/**
	 * Method Name: saveTransAdulthoodAboveFourteen
	 * 
	 * Method Description: This method is used to save Transition of adulthood
	 * above fourteen section.
	 * 
	 * @param transAdulthoodAboveFourteenDto
	 */
	private ChildPlanDtlRes saveTransAdulthoodAboveFourteen(ChildPlanTransAdltAbvDtlDto transAdulthoodAboveFourteenDto,
			String szUserId) {
		return childPlanDtlDao.saveTransAdulthoodAboveFourteen(transAdulthoodAboveFourteenDto, szUserId);

	}

	/**
	 * Method Name: saveSocialRecreational Method Description: This method is
	 * used to save social recreational section.
	 * 
	 * @param childPlanSocialRecreationalDto
	 */
	private ChildPlanDtlRes saveSocialRecreational(ChildPlanSocialRecreationalDto childPlanSocialRecreationalDto,
			String szUserId) {
		return childPlanDtlDao.saveSocialRecreational(childPlanSocialRecreationalDto, szUserId);

	}

	/**
	 * Method Name: saveSupervision Method Description: This method is used to
	 * save supervision section in child plan.
	 * 
	 * @param childPlanSupervisionDto
	 */
	private ChildPlanDtlRes saveSupervision(ChildPlanSupervisionDto childPlanSupervisionDto, String szUserId) {
		return childPlanDtlDao.saveSupervision(childPlanSupervisionDto, szUserId);

	}

	/**
	 * Method Name: saveHealthCareSummary Method Description: This method is
	 * used to save the health care summary details in child plan.
	 * 
	 * @param childPlanHealthCareSummaryDto
	 */
	private ChildPlanDtlRes saveHealthCareSummary(ChildPlanHealthCareSummaryDto childPlanHealthCareSummaryDto,
			String szUserId) {
		return childPlanDtlDao.saveHealthCareSummary(childPlanHealthCareSummaryDto, szUserId);

	}

	/**
	 * Method Name: saveYouthParenting Method Description: This method is used
	 * for saving youth parenting section in child plan.
	 * 
	 * @param childPlanYouthParentingDto
	 */
	private ChildPlanDtlRes saveYouthParenting(ChildPlanYouthParentingDto childPlanYouthParentingDto, String szUserId) {
		return childPlanDtlDao.saveYouthParenting(childPlanYouthParentingDto, szUserId);

	}

	/**
	 * Method Name: saveBehaviorMgnt Method Description: This method is used to
	 * save behavior management section
	 * 
	 * @param childPalnBehaviorMgntDto
	 */
	private ChildPlanDtlRes saveBehaviorMgnt(ChildPalnBehaviorMgntDto childPalnBehaviorMgntDto, String szUserId) {
		return childPlanDtlDao.saveBehaviorMgnt(childPalnBehaviorMgntDto, szUserId);

	}

	/**
	 * Method Name: saveEmtnlThrptcDtl Method Description: This method is used
	 * to save emotional, physic, thrpt section in child plan.
	 * 
	 * @param childPlanEmtnlThrptcDtlDto
	 */
	private ChildPlanDtlRes saveEmtnlThrptcDtl(ChildPlanEmtnlThrptcDtlDto childPlanEmtnlThrptcDtlDto, String szUserId) {
		return childPlanDtlDao.saveEmtnlThrptcDtl(childPlanEmtnlThrptcDtlDto, szUserId);

	}

	/**
	 * Method Name: saveEducation Method Description: This method is used to sae
	 * the education section in child plan.
	 * 
	 * @param childPlanEducationDto
	 */
	private ChildPlanDtlRes saveEducation(ChildPlanEducationDto childPlanEducationDto, String szUserId) {
		return childPlanDtlDao.saveEducation(childPlanEducationDto, szUserId);

	}

	/**
	 * Method Name: saveIntellectualDevelop Method Description: This method is
	 * used to save intellectual development section in child plan.
	 * 
	 * @param childPlanIntellectualDevelopDto
	 */
	private ChildPlanDtlRes saveIntellectualDevelop(ChildPlanIntellectualDevelopDto childPlanIntellectualDevelopDto,
			String szUserId) {
		return childPlanDtlDao.saveIntellectualDevelop(childPlanIntellectualDevelopDto, szUserId);

	}

	/**
	 * Method Name: savePlanVisitation Method Description: This method is used
	 * to save child visitation plan section in child plan.
	 * 
	 * @param childPlanVisitationCnctFmlyDto
	 */
	private ChildPlanDtlRes savePlanVisitation(ChildPlanVisitationCnctFmlyDto childPlanVisitationCnctFmlyDto,
			String szUserId) {
		return childPlanDtlDao.savePlanVisitation(childPlanVisitationCnctFmlyDto, szUserId);

	}

	/**
	 * Method Name: saveQrtpPermanencyMtng Method Description: This method is used
	 * to save QRTP PTM section in child plan.
	 *
	 * @param childPlanQrtpPrmnncyMeetingDto
	 */
	private ChildPlanDtlRes saveQrtpPermanencyMtng(ChildPlanQrtpPrmnncyMeetingDto childPlanQrtpPrmnncyMeetingDto,
											   String szUserId) {
		return childPlanDtlDao.saveQrtpPermanencyMtng(childPlanQrtpPrmnncyMeetingDto, szUserId);

	}

	/**
	 * Method Name: saveLegalGuardian Method Description: This method is used to
	 * save legal guardian section in child plan.
	 * 
	 * @param childPlanLegalGrdnshpDtoList
	 */
	private ChildPlanDtlRes saveLegalGuardian(List<ChildPlanLegalGrdnshpDto> childPlanLegalGrdnshpDtoList, Long eventId,
			String szUserId) {
		ChildPlanDtlRes res = null;
		for (ChildPlanLegalGrdnshpDto childPlanLegalGrdnshpDto : childPlanLegalGrdnshpDtoList) {

			childPlanLegalGrdnshpDto.setIdChildPlanEvent(eventId);
			res = childPlanDtlDao.saveLegalGuardian(childPlanLegalGrdnshpDto, szUserId);

		}
		return res;
	}

	/**
	 * Method Name: savePriorAdoption Method Description: This method is used to
	 * save prior adoption section in child plan.
	 * 
	 * @param childPlanPriorAdpInfoDtoList
	 * @param isNewEvent
	 */
	private ChildPlanDtlRes savePriorAdoption(List<ChildPlanPriorAdpInfoDto> childPlanPriorAdpInfoDtoList, Long eventId,
			String szUserId, Boolean isNewEvent) {

		ChildPlanDtlRes res = null;
		for (ChildPlanPriorAdpInfoDto childPlanPriorAdpInfoDto : childPlanPriorAdpInfoDtoList) {
			childPlanPriorAdpInfoDto.setIdChildPlanEvent(eventId);
			res = childPlanDtlDao.savePriorAdoption(childPlanPriorAdpInfoDto, szUserId);

		}
		return res;
	}

	/**
	 * Method Name: saveChildPlanDtl Method Description: This method saves or
	 * updates the child_plan table
	 * 
	 * @param childPlanOfServiceDtlDto
	 * @return ChildPlanDtlRes
	 */
	private ChildPlanDtlRes saveChildPlanDtl(ChildPlanOfServiceDtlDto childPlanOfServiceDtlDto, String idUser,
			Boolean externalUser) {
		return childPlanDtlDao.saveChildPlanDtl(childPlanOfServiceDtlDto, idUser, externalUser);
	}

	/**
	 * Method Name: deleteChildPlanDtl Method Description: This is method is
	 * used to delete the child plan data based on inputs.
	 * 
	 * @param childPlanOfServiceDtlDto
	 * @return ChildPlanDtlRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ChildPlanDtlRes deleteChildPlanDtl(ChildPlanDtlReq childPlanDtlReq) {
		ChildPlanDtlRes childPlanDtlRes = null;
		switch (childPlanDtlReq.getDeleteSectionType()) {

		case INTL_ADOPTION:
		case DMST_ADOPTION:
			childPlanDtlRes = childPlanDtlDao.deleteAdoption(childPlanDtlReq.getDeleteIds());
			break;

		case LGL_GRDNSHP:
			childPlanDtlRes = childPlanDtlDao.deleteLegalGuardianship(childPlanDtlReq.getDeleteIds());
			break;

		case LST_GOALS:
			childPlanDtlRes = childPlanDtlDao.deleteGoals(childPlanDtlReq.getDeleteIds());
			break;

		case NON_PYSC_MDCTN_HC:
		case PYSC_MDCTN_HC:
			childPlanDtlRes = childPlanDtlDao.deletePyscMdctnHc(childPlanDtlReq.getDeleteIds());
			break;

		case CP_PARTCPTN_TEAM:
			childPlanDtlRes = childPlanDtlDao.deleteCpPartcptnTeam(childPlanDtlReq.getDeleteIds());
			break;
		case CP_QRTP_PARTICIP_LIST:
			childPlanDtlRes = childPlanDtlDao.deleteQrtpPtmParticipants(childPlanDtlReq.getDeleteIds());
			break;

		}
		return childPlanDtlRes;
	}

	/**
	 * Method Name: saveAndSubmitChildPlanDtl Method Description: This method is
	 * used to save the fields in child plan page detail screen and submit the
	 * child plan for supervisor approval.
	 * 
	 * @param childPlanDtlReq
	 * @return ChildPlanDtlRes
	 */
	@SuppressWarnings("rawtypes")
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public ChildPlanDtlRes saveAndSubmitChildPlanDtl(ChildPlanDtlReq childPlanDtlReq) {
		ChildPlanDtlRes childPlanDtlRes = new ChildPlanDtlRes();
		saveChildPlanDtl(childPlanDtlReq);
		HashMap personDetail = new HashMap();
		/**
		 * To get the idPerson of the criminal history action which is null for
		 * the given id_Stage. If the idPerson is zero or null will display a
		 * warning message.
		 */
		if (CodesConstant.CSTAGES_ADO.equalsIgnoreCase(childPlanDtlReq.getChildPlanOfServiceDto().getCdStage())
				|| CodesConstant.CSTAGES_SUB
						.equalsIgnoreCase(childPlanDtlReq.getChildPlanOfServiceDto().getCdStage())) {
			personDetail = criminalHistoryService.checkCrimHistAction(
					childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdStage());
		}
		if (!ObjectUtils.isEmpty(personDetail) && personDetail.size() > ServiceConstants.Zero_INT) {
			childPlanDtlRes.getChildPlanOfServiceDto().setCriminalHistAction(ServiceConstants.TRUEVAL);
		}
		// To check if any DPS Criminal History check is pending
		childPlanDtlRes.getChildPlanOfServiceDto().setCriminalHistPending(criminalHistoryService.isCrimHistPending(
				childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdStage()));

		return childPlanDtlRes;
	}

	/**
	 * Method Name: getChildPlanDtlForm Method Description: This method gets the
	 * Child Plan Detail for forms
	 * 
	 * @param childPlanDtlReq
	 * @return PreFillDataServiceDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getChildPlanDtlForm(ChildPlanDtlReq childPlanDtlReq) {

		ChildPlanOfServiceDto childPlanOfServiceDto = new ChildPlanOfServiceDto();
		ChildPlanOfServiceDtlDto childPlanOfServiceDtlDto = new ChildPlanOfServiceDtlDto();
		childPlanOfServiceDtlDto.setIdCase(childPlanDtlReq.getIdCase());
		childPlanOfServiceDtlDto.setIdChildPlanEvent(childPlanDtlReq.getIdEvent());
		childPlanOfServiceDtlDto.setIdStage(childPlanDtlReq.getIdStage());
		childPlanOfServiceDtlDto.setNmCase(childPlanDtlReq.getNmCase());
		String nmCase =childPlanDtlReq.getNmCase();
		childPlanOfServiceDto.setChildPlanOfServiceDtlDto(childPlanOfServiceDtlDto);
		childPlanDtlReq.setChildPlanOfServiceDto(childPlanOfServiceDto);

		/*childPlanOfServiceDto = childPlanDtlReq.getChildPlanOfServiceDto();
		Long idChildPlanEvent = childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto()
				.getIdChildPlanEvent();
		String cdEventStatus = eventDao.getEventStatus(idChildPlanEvent);

		childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().setCdEventStatus(cdEventStatus);*/
		// For Forms set childPlanDtlReq.getSectionType() as "ALL"
		childPlanDtlReq.setSectionType(ChildPlanServiceTypeEnum.ALL);		
		ChildPlanDtlRes response	= getChildPlanDtl(childPlanDtlReq);	
		if (!ObjectUtils.isEmpty(response.getChildPlanOfServiceDto()) && 
				!ObjectUtils.isEmpty(response.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto()) ) {
			response.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().setNmCase(nmCase);
		}
		/*if (!ServiceConstants.APPROVED.equalsIgnoreCase(
				childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getCdEventStatus())) {
			childPlanOfServiceDto = getPrefillReadOnlyInfo(childPlanOfServiceDto);
		}

		if (ServiceConstants.CHILD_PLAN_RVW
				.equals(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCspPlanType())) {

			List<ChidPlanPsychMedctnDtlDto> childPlanPsychMedctnDtlList = childPlanDtlDao.getPrePsycnDtls(
					childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIdPerson(),
					childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIdStage());
			if (!ObjectUtils.isEmpty(childPlanPsychMedctnDtlList)) {
				childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().setPrePsycnDtls(childPlanPsychMedctnDtlList);
			}
			String indShowrsnChngPsycMedDiv = ServiceConstants.STRING_IND_N;
			if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto())
					&& !ObjectUtils.isEmpty(
							childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getChidPlanPsychMedctnDtlDtoList())
					&& !ObjectUtils
							.isEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getPrePsycnDtls())) {
				indShowrsnChngPsycMedDiv = getIndrsnChngPsycMedDiv(
						childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getChidPlanPsychMedctnDtlDtoList(),
						childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getPrePsycnDtls());
			}
			if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto())) {
				childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().setIndPsycMedChange(indShowrsnChngPsycMedDiv);
			}
		}
*/
		return childPlanOfServicePrefillData.returnPrefillData(response.getChildPlanOfServiceDto());
	}

	@Override
	public CommonIdRes alertReadyForReview(ChildPlanDtlReq childPlanDtlReq) {
		CommonIdRes resp = new CommonIdRes();
		Long resultId = alertService.createAlert(childPlanDtlReq.getIdStage(),
				childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanInformationDto().getIdDfpsPrmryWorker(),
				childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanInformationDto().getIdPerson(),
				childPlanDtlReq.getIdCase(), ServiceConstants.CHILD_PLAN_REVIEW_COMPL, new Date());

		resp.setResultId(resultId);
		return resp;
	}

}
