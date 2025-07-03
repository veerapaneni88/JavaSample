package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.dto.ServPlanEvalRecDto;
import us.tx.state.dfps.common.dto.ServicePlanDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FamilyAssmtFactDto;
import us.tx.state.dfps.service.forms.dto.FamilyServicePlanDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> May 3, 2018- 9:18:02 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class FamilyServicePlanPrefillData extends DocumentServiceUtil {

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		FamilyServicePlanDto prefillDto = (FamilyServicePlanDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonGroupList = new ArrayList<BookmarkDto>();

		// initialize null DTOs
		if (ObjectUtils.isEmpty(prefillDto.getEmployeePersPhNameDto())) {
			prefillDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getEventDto())) {
			prefillDto.setEventDto(new EventDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getFamilyAssmtList())) {
			prefillDto.setFamilyAssmtList(new ArrayList<FamilyAssmtFactDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto())) {
			prefillDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getServicePlanDto())) {
			prefillDto.setServicePlanDto(new ServicePlanDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getServicePlanGoalsList())) {
			prefillDto.setServicePlanGoalsList(new ArrayList<ServPlanEvalRecDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getServicePlanItemsList())) {
			prefillDto.setServicePlanItemsList(new ArrayList<ServPlanEvalRecDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getServicePlanProblemsList())) {
			prefillDto.setServicePlanProblemsList(new ArrayList<ServPlanEvalRecDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getStagePersonDto())) {
			prefillDto.setStagePersonDto(new StagePersonDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getPermanencyGoalsList())) {
			prefillDto.setPermanencyGoalsList(new ArrayList<PersonDto>());
		}

		if (ServiceConstants.CPLNTYPE_PSC.equals(prefillDto.getServicePlanDto().getCdSvcPlanType())) {
			// parent group cfsd0506
			FormDataGroupDto planPurpose2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PURPOSE_OF_PLAN_2,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> planPurpose2GroupList = new ArrayList<FormDataGroupDto>();

			// sub group cfsd0508
			for (PersonDto permGoalDto : prefillDto.getPermanencyGoalsList()) {
				FormDataGroupDto childrenGoalsGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILDREN_GOALS,
						FormGroupsConstants.TMPLAT_PURPOSE_OF_PLAN_2);
				List<BookmarkDto> bookmarkChildrenGoalsList = new ArrayList<BookmarkDto>();
				List<FormDataGroupDto> childrenGoalsGroupList = new ArrayList<FormDataGroupDto>();
				BookmarkDto bookmarkTargetDate = createBookmark(BookmarkConstants.TARGET_DATE,
						DateUtils.stringDt(permGoalDto.getDtcspPermGoalTarget()));
				bookmarkChildrenGoalsList.add(bookmarkTargetDate);
				BookmarkDto bookmarkPermGoal = createBookmarkWithCodesTable(BookmarkConstants.PERMANENCY_GOAL,
						permGoalDto.getCspPlanPermGoal(), CodesConstant.CCPPRMGL);
				bookmarkChildrenGoalsList.add(bookmarkPermGoal);
				BookmarkDto bookmarkChildNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.CHILD_NAME_SUFFIX,
						permGoalDto.getCdPersonSuffix(), CodesConstant.CSUFFIX2);
				bookmarkChildrenGoalsList.add(bookmarkChildNameSuffix);
				BookmarkDto bookmarkChildNameFirst = createBookmark(BookmarkConstants.CHILD_NAME_FIRST,
						permGoalDto.getNmPersonFirst());
				bookmarkChildrenGoalsList.add(bookmarkChildNameFirst);
				BookmarkDto bookmarkChildNameLast = createBookmark(BookmarkConstants.CHILD_NAME_LAST,
						permGoalDto.getNmPersonLast());
				bookmarkChildrenGoalsList.add(bookmarkChildNameLast);
				BookmarkDto bookmarkChildNameMiddle = createBookmark(BookmarkConstants.CHILD_NAME_MIDDLE,
						permGoalDto.getNmPersonMiddle());
				bookmarkChildrenGoalsList.add(bookmarkChildNameMiddle);
				childrenGoalsGroupDto.setBookmarkDtoList(bookmarkChildrenGoalsList);

				// sub sub group cfzco00
				if (StringUtils.isNotBlank(permGoalDto.getCdPersonSuffix())) {
					FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
							FormGroupsConstants.TMPLAT_CHILDREN_GOALS);
					childrenGoalsGroupList.add(commaGroupDto);
				}

				childrenGoalsGroupDto.setFormDataGroupList(childrenGoalsGroupList);
				planPurpose2GroupList.add(childrenGoalsGroupDto);
			}

			planPurpose2GroupDto.setFormDataGroupList(planPurpose2GroupList);
			formDataGroupList.add(planPurpose2GroupDto);

			// parent group cfsd0509
			FormDataGroupDto coverSheetHeader1GroupDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_COVER_SHEET_HEADER_1, FormConstants.EMPTY_STRING);
			formDataGroupList.add(coverSheetHeader1GroupDto);
		}

		else if (ServiceConstants.FPP.equals(prefillDto.getServicePlanDto().getCdSvcPlanType())) {
			// parent group cfsd0507
			FormDataGroupDto planPurpose1GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PURPOSE_OF_PLAN_1,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(planPurpose1GroupDto);

			// parent group cfsd0510
			FormDataGroupDto coverSheetHeader2GroupDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_COVER_SHEET_HEADER_2, FormConstants.EMPTY_STRING);
			formDataGroupList.add(coverSheetHeader2GroupDto);
		}

		// parent group cfzco00
		if (StringUtils.isNotBlank(prefillDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
			FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(commaGroupDto);
		}

		for (FamilyAssmtFactDto familyAssmtDto : prefillDto.getFamilyAssmtList()) {
			// parent group cfsd0501
			if (ServiceConstants.ADULT_TYPE.equals(familyAssmtDto.getCdStagePersType())) {
				FormDataGroupDto parentNameGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PARENT_NAME,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkParentNameList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkParentNameSuffix = createBookmarkWithCodesTable(
						BookmarkConstants.PARENT_NAME_SUFFIX, familyAssmtDto.getCdNameSuffix(), CodesConstant.CSUFFIX2);
				bookmarkParentNameList.add(bookmarkParentNameSuffix);
				BookmarkDto bookmarkParentRoleComma = createBookmark(BookmarkConstants.PARENT_ROLE_COMMA,
						familyAssmtDto.getComma());
				bookmarkParentNameList.add(bookmarkParentRoleComma);
				BookmarkDto bookmarkParentNameFirst = createBookmark(BookmarkConstants.PARENT_NAME_FIRST,
						familyAssmtDto.getNmNameFirst());
				bookmarkParentNameList.add(bookmarkParentNameFirst);
				BookmarkDto bookmarkParentNameLast = createBookmark(BookmarkConstants.PARENT_NAME_LAST,
						familyAssmtDto.getNmNameLast());
				bookmarkParentNameList.add(bookmarkParentNameLast);
				BookmarkDto bookmarkParentNameMiddle = createBookmark(BookmarkConstants.PARENT_NAME_MIDDLE,
						familyAssmtDto.getNmNameMiddle());
				bookmarkParentNameList.add(bookmarkParentNameMiddle);
				parentNameGroupDto.setBookmarkDtoList(bookmarkParentNameList);
				formDataGroupList.add(parentNameGroupDto);
			}

			// parent group cfsd0502
			else if (ServiceConstants.CHILD_TYPE.equals(familyAssmtDto.getCdStagePersType())) {
				FormDataGroupDto childNameGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILDREN_NAME,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkChildNameList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkChildNameSuffix = createBookmarkWithCodesTable(
						BookmarkConstants.CHILDREN_NAME_SUFFIX, familyAssmtDto.getCdNameSuffix(),
						CodesConstant.CSUFFIX2);
				bookmarkChildNameList.add(bookmarkChildNameSuffix);
				BookmarkDto bookmarkChildRoleComma = createBookmark(BookmarkConstants.CHILD_ROLE_COMMA,
						familyAssmtDto.getComma());
				bookmarkChildNameList.add(bookmarkChildRoleComma);
				BookmarkDto bookmarkChildNameFirst = createBookmark(BookmarkConstants.CHILDREN_NAME_FIRST,
						familyAssmtDto.getNmNameFirst());
				bookmarkChildNameList.add(bookmarkChildNameFirst);
				BookmarkDto bookmarkChildNameLast = createBookmark(BookmarkConstants.CHILDREN_NAME_LAST,
						familyAssmtDto.getNmNameLast());
				bookmarkChildNameList.add(bookmarkChildNameLast);
				BookmarkDto bookmarkChildNameMiddle = createBookmark(BookmarkConstants.CHILDREN_NAME_MIDDLE,
						familyAssmtDto.getNmNameMiddle());
				bookmarkChildNameList.add(bookmarkChildNameMiddle);
				childNameGroupDto.setBookmarkDtoList(bookmarkChildNameList);
				formDataGroupList.add(childNameGroupDto);
			}
		}

		// parent group cfsd0503
		for (ServPlanEvalRecDto problemDto : prefillDto.getServicePlanProblemsList()) {
			FormDataGroupDto problemsGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PROBLEMS,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkProblemsList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkProblem = createBookmark(BookmarkConstants.PROBLEMS_PROBLEM,
					problemDto.getTxtSvcPlanProblem());
			bookmarkProblemsList.add(bookmarkProblem);
			problemsGroupDto.setBookmarkDtoList(bookmarkProblemsList);
			formDataGroupList.add(problemsGroupDto);
		}

		// parent group cfsd0504
		for (ServPlanEvalRecDto goalDto : prefillDto.getServicePlanGoalsList()) {
			FormDataGroupDto goalsGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_GOALS,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkGoalsList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkGoal = createBookmark(BookmarkConstants.GOALS_GOAL, goalDto.getTxtSvcPlanGoal());
			bookmarkGoalsList.add(bookmarkGoal);
			goalsGroupDto.setBookmarkDtoList(bookmarkGoalsList);
			formDataGroupList.add(goalsGroupDto);
		}

		// parent group cfsd0505
		for (ServPlanEvalRecDto itemDto : prefillDto.getServicePlanItemsList()) {
			FormDataGroupDto tasksGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_TASKS,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> tasksGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkTasksList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkTaskCpsService = createBookmarkWithCodesTable(BookmarkConstants.TASK_CPS_SERVICE,
					itemDto.getCdSvcPlanSvc(), CodesConstant.CSVPLSVC);
			bookmarkTasksList.add(bookmarkTaskCpsService);
			BookmarkDto bookmarkTaskFamilyTask = createBookmarkWithCodesTable(BookmarkConstants.TASK_FAMILY_TASK,
					itemDto.getCdSvcPlanTask(), CodesConstant.CSVPLTSK);
			bookmarkTasksList.add(bookmarkTaskFamilyTask);
			BookmarkDto bookmarkTaskGoalDesc = createBookmark(BookmarkConstants.TASK_GOAL_DESCRIPTION,
					itemDto.getTxtSvcPlanGoal());
			bookmarkTasksList.add(bookmarkTaskGoalDesc);
			BookmarkDto bookmarkTaskMethod = createBookmark(BookmarkConstants.TASK_METHOD,
					itemDto.getTxtSvcPlanMethodEval());
			bookmarkTasksList.add(bookmarkTaskMethod);
			BookmarkDto bookmarkTaskCpsServiceDesc = createBookmark(BookmarkConstants.TASK_CPS_SERVICE_DESCRIPTION,
					itemDto.getTxtSvcPlanSvc());
			bookmarkTasksList.add(bookmarkTaskCpsServiceDesc);
			BookmarkDto bookmarkTaskCpsServiceDt = createBookmark(BookmarkConstants.TASK_CPS_SERVICE_DT,
					itemDto.getTxtSvcPlanSvcFreq());
			bookmarkTasksList.add(bookmarkTaskCpsServiceDt);
			BookmarkDto bookmarkTaskFamilyTaskDesc = createBookmark(BookmarkConstants.TASK_FAMILY_TASK_DESCRIPTION,
					itemDto.getTxtSvcPlanTask());
			bookmarkTasksList.add(bookmarkTaskFamilyTaskDesc);
			BookmarkDto bookmarkTaskFamilyTaskDt = createBookmark(BookmarkConstants.TASK_FAMILY_TASK_DT,
					itemDto.getTxtSvcPlanTaskFreq());
			bookmarkTasksList.add(bookmarkTaskFamilyTaskDt);
			tasksGroupDto.setBookmarkDtoList(bookmarkTasksList);

			// sub group cfzz0301
			if (ServiceConstants.Y.equals(itemDto.getIndSvcPlnTaskCrtOrdr())) {
				FormDataGroupDto familyTaskCourtGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_FAMILY_TASK_COURT, FormGroupsConstants.TMPLAT_TASKS);
				tasksGroupList.add(familyTaskCourtGroupDto);
			}

			// sub group cfzz0301
			if (ServiceConstants.Y.equals(itemDto.getIndSvcPlnSvcCrtOrdr())) {
				FormDataGroupDto cpsServiceCourtGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CPS_SERVICE_COURT, FormGroupsConstants.TMPLAT_TASKS);
				tasksGroupList.add(cpsServiceCourtGroupDto);
			}

			tasksGroupDto.setFormDataGroupList(tasksGroupList);
			formDataGroupList.add(tasksGroupDto);
		}

		// Non group bookmarks

		// CSEC02D
		BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				prefillDto.getGenericCaseInfoDto().getNmCase());
		bookmarkNonGroupList.add(bookmarkTitleCaseName);
		BookmarkDto bookmarkCoverDtNextReview = createBookmark(BookmarkConstants.COVER_DT_NEXT_REVIEW,
				prefillDto.getGenericCaseInfoDto().getTmScrTmGeneric1());
		bookmarkNonGroupList.add(bookmarkCoverDtNextReview);
		BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				prefillDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonGroupList.add(bookmarkTitleCaseNumber);

		// CSVC04D
		BookmarkDto bookmarkCoverDtCompleted = createBookmark(BookmarkConstants.COVER_DT_COMPLETED,
				DateUtils.stringDt(prefillDto.getServicePlanDto().getDtSvcPlanComplt()));
		bookmarkNonGroupList.add(bookmarkCoverDtCompleted);
		BookmarkDto bookmarkCoverPlanDt = createBookmark(BookmarkConstants.COVER_PLAN_DT,
				DateUtils.stringDt(prefillDto.getServicePlanDto().getDtSvcPlanGivenClients()));
		bookmarkNonGroupList.add(bookmarkCoverPlanDt);
		BookmarkDto bookmarkCoverDtPart = createBookmark(BookmarkConstants.COVER_DT_PART,
				DateUtils.stringDt(prefillDto.getServicePlanDto().getDtSvcPlanPartcp()));
		bookmarkNonGroupList.add(bookmarkCoverDtPart);
		BookmarkDto bookmarkCoverParentNoPartic = createBookmark(BookmarkConstants.COVER_PARENT_NO_PARTIC,
				prefillDto.getServicePlanDto().getTxtSvcPlanPartcp());
		bookmarkNonGroupList.add(bookmarkCoverParentNoPartic);
		BookmarkDto bookmarkPartOneReasons = createBookmark(BookmarkConstants.PART_ONE_REASONS,
				prefillDto.getServicePlanDto().getTxtSvcPlanRsnInvlvmnt());
		bookmarkNonGroupList.add(bookmarkPartOneReasons);
		BookmarkDto bookmarkPartOneStrengths = createBookmark(BookmarkConstants.PART_ONE_STRENGTHS,
				prefillDto.getServicePlanDto().getTxtSvcPlnStrnthsRsrcs());
		bookmarkNonGroupList.add(bookmarkPartOneStrengths);

		// CSEC01D
		BookmarkDto bookmarkCoverWorkerPhone = createBookmark(BookmarkConstants.COVER_WORKER_PHONE,
				TypeConvUtil.formatPhone(prefillDto.getEmployeePersPhNameDto().getNbrPhone()));
		bookmarkNonGroupList.add(bookmarkCoverWorkerPhone);
		BookmarkDto bookmarkCoverWorkerPhoneExt = createBookmark(BookmarkConstants.COVER_WORKER_PHONE_EXTENSION,
				prefillDto.getEmployeePersPhNameDto().getNbrPhoneExtension());
		bookmarkNonGroupList.add(bookmarkCoverWorkerPhoneExt);
		BookmarkDto bookmarkCoverWorkerNmSuffix = createBookmarkWithCodesTable(
				BookmarkConstants.COVER_WORKER_NAME_SUFFIX, prefillDto.getEmployeePersPhNameDto().getCdNameSuffix(),
				CodesConstant.CSUFFIX2);
		bookmarkNonGroupList.add(bookmarkCoverWorkerNmSuffix);
		BookmarkDto bookmarkCoverWorkerNmFirst = createBookmark(BookmarkConstants.COVER_WORKER_NAME_FIRST,
				prefillDto.getEmployeePersPhNameDto().getNmNameFirst());
		bookmarkNonGroupList.add(bookmarkCoverWorkerNmFirst);
		BookmarkDto bookmarkCoverWorkerNmLast = createBookmark(BookmarkConstants.COVER_WORKER_NAME_LAST,
				prefillDto.getEmployeePersPhNameDto().getNmNameLast());
		bookmarkNonGroupList.add(bookmarkCoverWorkerNmLast);
		BookmarkDto bookmarkCoverWorkerNmMiddle = createBookmark(BookmarkConstants.COVER_WORKER_NAME_MIDDLE,
				prefillDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bookmarkNonGroupList.add(bookmarkCoverWorkerNmMiddle);

		PreFillDataServiceDto prefillData = new PreFillDataServiceDto();
		prefillData.setBookmarkDtoList(bookmarkNonGroupList);
		prefillData.setFormDataGroupList(formDataGroupList);

		return prefillData;
	}

}
