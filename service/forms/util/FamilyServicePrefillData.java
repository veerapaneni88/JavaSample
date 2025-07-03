/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 18, 2018- 9:53:00 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.dto.WorkerDetailDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FamilyAssmtFactDto;
import us.tx.state.dfps.service.forms.dto.FamilyChildNameGaolDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanEvaItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanEvalDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;
import us.tx.state.dfps.service.forms.dto.FdgmFamilyDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.dto.RiskAreaLookUpDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Family
 * Service Plan / Evaluation Mar 18, 2018- 9:53:00 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class FamilyServicePrefillData extends DocumentServiceUtil {

	@Autowired
	LookupDao lookupDao;

	/**
	 * Method Description: This method is used to prefill the data from the
	 * different Dao by passing Dao output Dtos and bookmark and form group
	 * bookmark Dto as objects as input request
	 * 
	 * @param parentDtoobj
	 * @param bookmarkDtoObj
	 * @return PreFillData
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		FdgmFamilyDto fdgmFamilyDto = (FdgmFamilyDto) parentDtoobj;

		if (ObjectUtils.isEmpty(fdgmFamilyDto.getGenericCaseInfoDto())) {
			fdgmFamilyDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}
		if (ObjectUtils.isEmpty(fdgmFamilyDto.getStagePersonDto())) {
			fdgmFamilyDto.setStagePersonDto(new StagePersonDto());
		}
		if (ObjectUtils.isEmpty(fdgmFamilyDto.getWorkerDetailDto())) {
			fdgmFamilyDto.setWorkerDetailDto(new WorkerDetailDto());
		}
		if (ObjectUtils.isEmpty(fdgmFamilyDto.getFamilyPlanEvalDto())) {
			fdgmFamilyDto.setFamilyPlanEvalDto(new FamilyPlanEvalDto());
		}
		if (ObjectUtils.isEmpty(fdgmFamilyDto.getFamilyPlanDto())) {
			fdgmFamilyDto.setFamilyPlanDto(new FamilyPlanDto());
		}
		if (ObjectUtils.isEmpty(fdgmFamilyDto.getEventDto())) {
			fdgmFamilyDto.setEventDto(new EventDto());
		}
		if (ObjectUtils.isEmpty(fdgmFamilyDto.getFamilyPlanItemGoalList())) {
			fdgmFamilyDto.setFamilyPlanItemGoalList(new ArrayList<FamilyPlanItemDto>());
		}
		if (ObjectUtils.isEmpty(fdgmFamilyDto.getFamilyPlanEvalItemList())) {
			fdgmFamilyDto.setFamilyPlanEvalItemList(new ArrayList<FamilyPlanEvaItemDto>());
		}
		if (ObjectUtils.isEmpty(fdgmFamilyDto.getTaskForSpecRiskAreaList())) {
			fdgmFamilyDto.setTaskForSpecRiskAreaList(new ArrayList<FamilyPlanItemDto>());
		}
		if (ObjectUtils.isEmpty(fdgmFamilyDto.getFamilyPlanItemLists())) {
			fdgmFamilyDto.setFamilyPlanItemLists(new ArrayList<FamilyPlanItemDto>());
		}
		if (ObjectUtils.isEmpty(fdgmFamilyDto.getFamilyChildNameGaolDtoList())) {
			fdgmFamilyDto.setFamilyChildNameGaolDtoList(new ArrayList<FamilyChildNameGaolDto>());
		}
		if (ObjectUtils.isEmpty(fdgmFamilyDto.getFamilyAssmtFactDtoList())) {
			fdgmFamilyDto.setFamilyAssmtFactDtoList(new ArrayList<FamilyAssmtFactDto>());
		}
		if (ObjectUtils.isEmpty(fdgmFamilyDto.getRiskAreaLookUpDtoList())) {
			fdgmFamilyDto.setRiskAreaLookUpDtoList(new ArrayList<RiskAreaLookUpDto>());
		}

		// Independent Groups
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// cfsd0805
		if (!ObjectUtils.isEmpty(fdgmFamilyDto.getRiskAreaLookUpDtoList())) {
			for (RiskAreaLookUpDto riskAreaLookUpDto : fdgmFamilyDto.getRiskAreaLookUpDtoList()) {
				List<FormDataGroupDto> formDatacfsd0805List = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDatacfsd0805Para = createFormDataGroup(
						FormGroupsConstants.TMPLAT_AREA_CONCERN_GOALS, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkcfsd0805List = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkcfsd0805 = createBookmark(BookmarkConstants.AREA_OF_CONCERN,
						riskAreaLookUpDto.getTxtArea());
				bookmarkcfsd0805List.add(bookmarkcfsd0805);
				List<FormDataGroupDto> allParentList = new ArrayList<FormDataGroupDto>();
				// cfsd0805 -----> cfsd0822
				if (!ObjectUtils.isEmpty(fdgmFamilyDto.getFamilyPlanItemGoalList())) {
					for (FamilyPlanItemDto familyPlanItemDto : fdgmFamilyDto.getFamilyPlanItemGoalList()) {
						if (!ObjectUtils.isEmpty(familyPlanItemDto)
								&& StringUtils.isEmpty(familyPlanItemDto.getCdInitialLevelConcern())
								&& riskAreaLookUpDto.getCdArea().equals(familyPlanItemDto.getCdAreaConcern())) {
							List<FormDataGroupDto> formDatacfsd0822List = new ArrayList<FormDataGroupDto>();
							FormDataGroupDto formDatacfsd0822Para = createFormDataGroup(
									FormGroupsConstants.TMPLAT_NOT_ADDRESSED,
									FormGroupsConstants.TMPLAT_AREA_CONCERN_GOALS);
							formDatacfsd0822List.add(formDatacfsd0822Para);
							allParentList.addAll(formDatacfsd0822List);
						}
					}
				}
				// cfsd0805 -----> cfsd0806
				if (!ObjectUtils.isEmpty(fdgmFamilyDto.getFamilyPlanItemGoalList())) {
					for (FamilyPlanItemDto familyPlanItemDto : fdgmFamilyDto.getFamilyPlanItemGoalList()) {
						if (!ObjectUtils.isEmpty(familyPlanItemDto)
								&& !StringUtils.isEmpty(familyPlanItemDto.getCdInitialLevelConcern())
								&& riskAreaLookUpDto.getCdArea().equals(familyPlanItemDto.getCdAreaConcern())) {
							List<FormDataGroupDto> formDatacfsd0806List = new ArrayList<FormDataGroupDto>();
							FormDataGroupDto formDatacfsd0806Para = createFormDataGroup(
									FormGroupsConstants.TMPLAT_FAMILY_ITEM,
									FormGroupsConstants.TMPLAT_AREA_CONCERN_GOALS);
							List<BookmarkDto> bookmarkcfsd0806List = new ArrayList<BookmarkDto>();
							BookmarkDto bookmarkcfsd0806 = createBookmark(BookmarkConstants.ITEM_GOAL,
									familyPlanItemDto.getTxtItemGoals());
							bookmarkcfsd0806List.add(bookmarkcfsd0806);
							formDatacfsd0806Para.setBookmarkDtoList(bookmarkcfsd0806List);
							List<FormDataGroupDto> allParentList06 = new ArrayList<FormDataGroupDto>();
							// cfsd0805 -----> cfsd0806 ------> cfsd0813
							if (!ObjectUtils.isEmpty(familyPlanItemDto)
									&& !StringUtils.isEmpty(familyPlanItemDto.getCdCurrentLevelConcern())) {
								for (FamilyPlanItemDto familyPlanItemDto2 : fdgmFamilyDto.getFamilyPlanItemGoalList()) {
									if (riskAreaLookUpDto.getCdArea().equals(familyPlanItemDto2.getCdAreaConcern())
											&& !StringUtils.isEmpty(familyPlanItemDto2.getCdCurrentLevelConcern())) {

										List<FormDataGroupDto> formDatacfsd0813List = new ArrayList<FormDataGroupDto>();
										FormDataGroupDto formDatacfsd0813Para = createFormDataGroup(
												FormGroupsConstants.TMPLAT_CURRENT_LEVEL,
												FormGroupsConstants.TMPLAT_FAMILY_ITEM);
										List<BookmarkDto> bookmarkcfsd0813List = new ArrayList<BookmarkDto>();
										BookmarkDto bookmarkcfsd0813 = createBookmark(
												BookmarkConstants.CURRENT_LEVEL_CONCERN,
												lookupDao.decode(ServiceConstants.CRISKSOC,
														familyPlanItemDto2.getCdCurrentLevelConcern()));
										bookmarkcfsd0813List.add(bookmarkcfsd0813);
										formDatacfsd0813Para.setBookmarkDtoList(bookmarkcfsd0813List);
										formDatacfsd0813List.add(formDatacfsd0813Para);
										allParentList06.addAll(formDatacfsd0813List);
									}
								}

							}
							// cfsd0805 -----> cfsd0806 ------> cfsd0812
							if (!ObjectUtils.isEmpty(familyPlanItemDto)
									&& StringUtils.isEmpty(familyPlanItemDto.getCdCurrentLevelConcern())) {
								for (FamilyPlanItemDto familyPlanItemDto2 : fdgmFamilyDto.getFamilyPlanItemGoalList()) {
									if (riskAreaLookUpDto.getCdArea().equals(familyPlanItemDto2.getCdAreaConcern())) {

										List<FormDataGroupDto> formDatacfsd0812List = new ArrayList<FormDataGroupDto>();
										FormDataGroupDto formDatacfsd0812Para = createFormDataGroup(
												FormGroupsConstants.TMPLAT_INITIAL_LEVEL,
												FormGroupsConstants.TMPLAT_FAMILY_ITEM);
										List<BookmarkDto> bookmarkcfsd0812List = new ArrayList<BookmarkDto>();
										BookmarkDto bookmarkcfsd0812 = createBookmark(
												BookmarkConstants.CURRENT_LEVEL_CONCERN,
												lookupDao.decode(ServiceConstants.CRISKSOC,
														familyPlanItemDto2.getCdInitialLevelConcern()));
										bookmarkcfsd0812List.add(bookmarkcfsd0812);
										formDatacfsd0812Para.setBookmarkDtoList(bookmarkcfsd0812List);
										formDatacfsd0812List.add(formDatacfsd0812Para);
										allParentList06.addAll(formDatacfsd0812List);

									}
								}

							}
							formDatacfsd0806Para.setFormDataGroupList(allParentList06);
							formDatacfsd0806List.add(formDatacfsd0806Para);
							allParentList.addAll(formDatacfsd0806List);
						}
					}
				}
				formDatacfsd0805Para.setFormDataGroupList(allParentList);
				formDatacfsd0805Para.setBookmarkDtoList(bookmarkcfsd0805List);
				formDatacfsd0805List.add(formDatacfsd0805Para);
				formDataGroupList.addAll(formDatacfsd0805List);
			}
		}

		// cfsd0807 CSVC43D
		if (!ObjectUtils.isEmpty(fdgmFamilyDto.getFamilyPlanEvalDto().getIdFamilyPlanEvaluation())
				&& fdgmFamilyDto.getFamilyPlanEvalDto().getIdFamilyPlanEvaluation() > 0) {
			List<FormDataGroupDto> formDatacfsd0807List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDatacfsd0807Para = createFormDataGroup(FormGroupsConstants.TMPLAT_EVALUATION,
					FormConstants.EMPTY_STRING);
			// cfsd0807 -----> cfsd0808 CSVC43D
			if (!ObjectUtils.isEmpty(fdgmFamilyDto.getRiskAreaLookUpDtoList())) {
				for (RiskAreaLookUpDto riskAreaLookUpDto : fdgmFamilyDto.getRiskAreaLookUpDtoList()) {
					List<FormDataGroupDto> formDatacfsd0808List = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDatacfsd0808Para = createFormDataGroup(
							FormGroupsConstants.TMPLAT_AREA_CONCERN_EVALUATION, FormGroupsConstants.TMPLAT_EVALUATION);
					List<BookmarkDto> bookmarkcfsd0808List = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkcfsd0808 = createBookmark(BookmarkConstants.AREA_OF_CONCERN,
							riskAreaLookUpDto.getTxtArea());
					bookmarkcfsd0808List.add(bookmarkcfsd0808);
					formDatacfsd0808Para.setBookmarkDtoList(bookmarkcfsd0808List);
					// cfsd0807 -----> cfsd0808 -----> cfsd0809 CSVC43D
					if (!ObjectUtils.isEmpty(fdgmFamilyDto.getFamilyPlanEvalItemList())) {
						for (FamilyPlanEvaItemDto familyPlanEvaItemDto : fdgmFamilyDto.getFamilyPlanEvalItemList()) {
							List<FormDataGroupDto> formDatacfsd0809List = new ArrayList<FormDataGroupDto>();
							FormDataGroupDto formDatacfsd0809Para = createFormDataGroup(
									FormGroupsConstants.TMPLAT_EVALUATION_ITEM,
									FormGroupsConstants.TMPLAT_AREA_CONCERN_EVALUATION);
							List<BookmarkDto> bookmarkcfsd0809List = new ArrayList<BookmarkDto>();
							BookmarkDto bookmarkDtComp = createBookmark(BookmarkConstants.AS_OF_DATE,
									familyPlanEvaItemDto.getDtCompleted());
							bookmarkcfsd0809List.add(bookmarkDtComp);
							BookmarkDto bookmarktxtItemEval = createBookmark(BookmarkConstants.EVALUATION_ITEM,
									familyPlanEvaItemDto.getTxtItemEvaluation());
							bookmarkcfsd0809List.add(bookmarktxtItemEval);
							formDatacfsd0809Para.setBookmarkDtoList(bookmarkcfsd0809List);
							formDatacfsd0809List.add(formDatacfsd0809Para);
							formDatacfsd0808Para.setFormDataGroupList(formDatacfsd0809List);
						}
					}
					formDatacfsd0808List.add(formDatacfsd0808Para);
					formDatacfsd0807Para.setFormDataGroupList(formDatacfsd0808List);
				}
			}
			formDatacfsd0807List.add(formDatacfsd0807Para);
			formDataGroupList.addAll(formDatacfsd0807List);
		}

		if (!ObjectUtils.isEmpty(fdgmFamilyDto.getRiskAreaLookUpDtoList())) {
			List<FormDataGroupDto> formDatacfsd0810List = new ArrayList<FormDataGroupDto>();
			for (RiskAreaLookUpDto riskAreaLookUpDto : fdgmFamilyDto.getRiskAreaLookUpDtoList()) {

				// get the list of family items for the risk area lookup
				List<FamilyPlanItemDto> templFamilyItemList = new ArrayList<FamilyPlanItemDto>();
				templFamilyItemList = fdgmFamilyDto.getFamilyPlanItemLists().stream()
						.filter(family -> family.getCdArea().equals(riskAreaLookUpDto.getCdArea()))
						.collect(Collectors.toList());

				List<FormDataGroupDto> allParentList = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDatacfsd0810Para = createFormDataGroup(
						FormGroupsConstants.TMPLAT_AREA_CONCERN_TASKS, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkcfsd0810List = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkcfsd0810 = createBookmark(BookmarkConstants.AREA_OF_CONCERN,
						riskAreaLookUpDto.getTxtArea());
				bookmarkcfsd0810List.add(bookmarkcfsd0810);
				formDatacfsd0810Para.setBookmarkDtoList(bookmarkcfsd0810List);

				if (ObjectUtils.isEmpty(templFamilyItemList)) {
					FormDataGroupDto formDatacfsd0823Para = createFormDataGroup(
							FormGroupsConstants.TMPLAT_TASK_NOT_ADDRESSED,
							FormGroupsConstants.TMPLAT_AREA_CONCERN_TASKS);
					allParentList.add(formDatacfsd0823Para);
				} else {
					FormDataGroupDto formDatacfsd0818Para = createFormDataGroup(
							FormGroupsConstants.TMPLAT_TASK_TABLE_HEADER,
							FormGroupsConstants.TMPLAT_AREA_CONCERN_TASKS);
					List<FormDataGroupDto> formDatacfsd0812List = new ArrayList<FormDataGroupDto>();

					// cfsd0810 -----> cfsd0818 -----> cfsd0811 CSVC44D
					for (FamilyPlanItemDto familyPlanItemDto : templFamilyItemList) {
						FormDataGroupDto formDataTaskServices = createFormDataGroup(
								FormGroupsConstants.TMPLAT_TASK_SERVICES, FormGroupsConstants.TMPLAT_TASK_TABLE_HEADER);
						List<FormDataGroupDto> formDatacfsd0811List = new ArrayList<FormDataGroupDto>();
						if (!ObjectUtils.isEmpty(familyPlanItemDto.getIdFamilyPlanItem())
								&& 0l != familyPlanItemDto.getIdFamilyPlanItem()) {

							List<BookmarkDto> bookmarkcfsd0811List = new ArrayList<BookmarkDto>();
							FormDataGroupDto formDataTaskItem = createFormDataGroup(
									FormGroupsConstants.TMPLAT_TASK_ITEM, FormGroupsConstants.TMPLAT_TASK_SERVICES);
							BookmarkDto bookmarkcfsd0811 = createBookmark(BookmarkConstants.TASK_ITEM,
									familyPlanItemDto.getTxtTask());
							bookmarkcfsd0811List.add(bookmarkcfsd0811);
							formDataTaskItem.setBookmarkDtoList(bookmarkcfsd0811List);
							formDatacfsd0811List.add(formDataTaskItem);

							// cfsd0810 -----> cfsd0818 ----->
							// cfsd0811 -----> cfsd0814 CSVC44D

							FormDataGroupDto formDatacfsd0814Para = null;
							if (!ObjectUtils.isEmpty(familyPlanItemDto.getDtCompleted())) {
								formDatacfsd0814Para = createFormDataGroup(FormGroupsConstants.TMPLAT_TASK_COMPLETE_YES,
										FormGroupsConstants.TMPLAT_TASK_SERVICES);
							} else {
								formDatacfsd0814Para = createFormDataGroup(FormGroupsConstants.TMPLAT_TASK_COMPLETE_NO,
										FormGroupsConstants.TMPLAT_TASK_SERVICES);
							}

							formDatacfsd0811List.add(formDatacfsd0814Para);

							// cfsd0810 -----> cfsd0818 ----->
							// cfsd0811 -----> cfsd0820 CSVC44D

							if ("N".equalsIgnoreCase(familyPlanItemDto.getIndCourtOrdered())) {
								FormDataGroupDto formDatacfsd0820Para = createFormDataGroup(
										FormGroupsConstants.TMPLAT_TASK_COURT_ORDERED_NO,
										FormGroupsConstants.TMPLAT_TASK_SERVICES);
								formDatacfsd0811List.add(formDatacfsd0820Para);

							}

							// cfsd0810 -----> cfsd0818 ----->
							// cfsd0811 -----> cfsd0819 CSVC44D

							else if ("Y".equalsIgnoreCase(familyPlanItemDto.getIndCourtOrdered())) {
								FormDataGroupDto formDatacfsd0819Para = createFormDataGroup(
										FormGroupsConstants.TMPLAT_TASK_COURT_ORDERED_YES,
										FormGroupsConstants.TMPLAT_TASK_SERVICES);
								formDatacfsd0811List.add(formDatacfsd0819Para);

							}
						}
						formDataTaskServices.setFormDataGroupList(formDatacfsd0811List);
						formDatacfsd0812List.add(formDataTaskServices);
					}

					formDatacfsd0818Para.setFormDataGroupList(formDatacfsd0812List);

					allParentList.add(formDatacfsd0818Para);
				}

				//
				formDatacfsd0810Para.setFormDataGroupList(allParentList);
				formDatacfsd0810List.add(formDatacfsd0810Para);
			}
			formDataGroupList.addAll(formDatacfsd0810List);

		}

		// cfsd0803 CSVC41D
		if (!ObjectUtils.isEmpty(fdgmFamilyDto.getFamilyPlanDto().getCdPlanType())
				&& "FPP".equalsIgnoreCase(fdgmFamilyDto.getFamilyPlanDto().getCdPlanType())) {
			List<FormDataGroupDto> formDatacfsd0803List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDatacfsd0803Para = createFormDataGroup(FormGroupsConstants.TMPLAT_PURPOSE_OF_PLAN_1,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkcfsd0803List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkcfsd0803 = createBookmark(BookmarkConstants.COVER_DT_NEXT_REVIEW,
					fdgmFamilyDto.getFamilyPlanDto().getDtNextReview());
			bookmarkcfsd0803List.add(bookmarkcfsd0803);
			formDatacfsd0803Para.setBookmarkDtoList(bookmarkcfsd0803List);
			formDatacfsd0803List.add(formDatacfsd0803Para);
			formDataGroupList.addAll(formDatacfsd0803List);
		}

		// cfsd0816 CSVC41D
		if (!ObjectUtils.isEmpty(fdgmFamilyDto.getFamilyPlanDto().getCdPlanType())
				&& "PSC".equalsIgnoreCase(fdgmFamilyDto.getFamilyPlanDto().getCdPlanType())) {
			List<FormDataGroupDto> formDatacfsd0816List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDatacfsd0816Para = createFormDataGroup(FormGroupsConstants.TMPLAT_COVER_SHEET_HEADER_1,
					FormConstants.EMPTY_STRING);
			formDatacfsd0816List.add(formDatacfsd0816Para);
			formDataGroupList.addAll(formDatacfsd0816List);
		}

		// cfsd0817 CSVC41D
		if (!ObjectUtils.isEmpty(fdgmFamilyDto.getFamilyPlanDto().getCdPlanType())
				&& "FPP".equalsIgnoreCase(fdgmFamilyDto.getFamilyPlanDto().getCdPlanType())) {
			List<FormDataGroupDto> formDatacfsd0817List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDatacfsd0817Para = createFormDataGroup(FormGroupsConstants.TMPLAT_PURPOSE_OF_PLAN_1,
					FormConstants.EMPTY_STRING);
			formDatacfsd0817List.add(formDatacfsd0817Para);
			formDataGroupList.addAll(formDatacfsd0817List);
		}

		// cfsd0804 CSVC41D
		if (!ObjectUtils.isEmpty(fdgmFamilyDto.getFamilyPlanDto().getCdPlanType())
				&& "PSC".equalsIgnoreCase(fdgmFamilyDto.getFamilyPlanDto().getCdPlanType())) {
			FormDataGroupDto formDatacfsd0804Para = createFormDataGroup(FormGroupsConstants.TMPLAT_PURPOSE_OF_PLAN_2,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> allParentList = new ArrayList<FormDataGroupDto>();
			// cfsd0804 -----> cfsd0821 CSVC41D
			FormDataGroupDto formDatacfsd0821Para = createFormDataGroup(
					FormGroupsConstants.TMPLAT_PERMANENCY_GOAL_COMMENTS, FormGroupsConstants.TMPLAT_PURPOSE_OF_PLAN_2);
			List<BookmarkDto> bookmarkcfsd0821List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkcfsd0821 = createBookmark(BookmarkConstants.PERMANENCY_GOAL_COMMENTS,
					fdgmFamilyDto.getFamilyPlanDto().getTxtPermGoalComments());
			bookmarkcfsd0821List.add(bookmarkcfsd0821);
			formDatacfsd0821Para.setBookmarkDtoList(bookmarkcfsd0821List);
			allParentList.add(formDatacfsd0821Para);
			// cfsd0804 -----> cfsd0821 CLSCB5D
			List<FormDataGroupDto> childGoalsGroupList = new ArrayList<FormDataGroupDto>();
			for (FamilyChildNameGaolDto familyChildNameGaolDto : fdgmFamilyDto.getFamilyChildNameGaolDtoList()) {

				FormDataGroupDto childGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILDREN_GOALS,
						FormGroupsConstants.TMPLAT_PURPOSE_OF_PLAN_2);
				List<BookmarkDto> childPlanBookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkDtGaol = createBookmark(BookmarkConstants.PERMANENCY_GOAL_DATE,
						DateUtils.stringDt(familyChildNameGaolDto.getDtFamPlanPermGoalTarget()));
				childPlanBookmarkList.add(bookmarkDtGaol);
				if (!ObjectUtils.isEmpty(familyChildNameGaolDto.getCdFamPlanPermGoal())) {
					BookmarkDto bookmarkPermGaol = createBookmark(BookmarkConstants.PERMANENCY_GOAL,
							lookupDao.decode(ServiceConstants.CCPPRMGL, familyChildNameGaolDto.getCdFamPlanPermGoal()));
					childPlanBookmarkList.add(bookmarkPermGaol);
				}
				if (!ObjectUtils.isEmpty(familyChildNameGaolDto.getCdNameSuffix())) {
					BookmarkDto bookmarkChildSuff = createBookmark(BookmarkConstants.CHILD_NAME_SFX,
							lookupDao.decode(ServiceConstants.CSUFFIX2, familyChildNameGaolDto.getCdNameSuffix()));
					childPlanBookmarkList.add(bookmarkChildSuff);
				}
				BookmarkDto bookmarkChild1Nm = createBookmark(BookmarkConstants.CHILD_NAME_FIRST,
						familyChildNameGaolDto.getNmNameFirst());
				childPlanBookmarkList.add(bookmarkChild1Nm);
				BookmarkDto bookmarkChildMid = createBookmark(BookmarkConstants.CHILD_NAME_MIDDLE,
						familyChildNameGaolDto.getNmNameMiddle());
				childPlanBookmarkList.add(bookmarkChildMid);
				BookmarkDto bookmarkChildLast = createBookmark(BookmarkConstants.CHILD_NAME_LAST,
						familyChildNameGaolDto.getNmNameLast());
				childPlanBookmarkList.add(bookmarkChildLast);
				childGroup.setBookmarkDtoList(childPlanBookmarkList);
				childGoalsGroupList.add(childGroup);

			}
			allParentList.addAll(childGoalsGroupList);
			formDatacfsd0804Para.setFormDataGroupList(allParentList);

			formDataGroupList.add(formDatacfsd0804Para);
		}

		// cfsd0801 CLSC23D
		for (FamilyAssmtFactDto familyAssmtFactDto : fdgmFamilyDto.getFamilyAssmtFactDtoList()) {
			if (!ObjectUtils.isEmpty(familyAssmtFactDto.getCdStagePersType())
					&& "AD".equalsIgnoreCase(familyAssmtFactDto.getCdStagePersType())) {
				List<FormDataGroupDto> formDatacfsd0801List = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDatacfsd0801Para = createFormDataGroup(FormGroupsConstants.TMPLAT_PARENT_NAME,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkcfsd0801List = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkcfNmSuff = createBookmark(BookmarkConstants.PARENT_NAME_SUFFIX,
						familyAssmtFactDto.getCdNameSuffix());
				bookmarkcfsd0801List.add(bookmarkcfNmSuff);
				BookmarkDto bookmarkComma = createBookmark(BookmarkConstants.PARENT_ROLE_COMMA,
						familyAssmtFactDto.getComma());
				bookmarkcfsd0801List.add(bookmarkComma);
				BookmarkDto bookmarkNmFirst = createBookmark(BookmarkConstants.PARENT_NAME_FIRST,
						familyAssmtFactDto.getNmNameFirst());
				bookmarkcfsd0801List.add(bookmarkNmFirst);
				BookmarkDto bookmarkNmLast = createBookmark(BookmarkConstants.PARENT_NAME_LAST,
						familyAssmtFactDto.getNmNameLast());
				bookmarkcfsd0801List.add(bookmarkNmLast);
				BookmarkDto bookmarkNmMid = createBookmark(BookmarkConstants.PARENT_NAME_MIDDLE,
						familyAssmtFactDto.getNmNameMiddle());
				bookmarkcfsd0801List.add(bookmarkNmMid);
				formDatacfsd0801Para.setBookmarkDtoList(bookmarkcfsd0801List);
				formDatacfsd0801List.add(formDatacfsd0801Para);
				formDataGroupList.addAll(formDatacfsd0801List);
			}
		}

		// cfsd0802 CLSC23D
		for (FamilyAssmtFactDto familyAssmtFactDto : fdgmFamilyDto.getFamilyAssmtFactDtoList()) {
			if (!ObjectUtils.isEmpty(familyAssmtFactDto.getCdStagePersType())
					&& "CH".equalsIgnoreCase(familyAssmtFactDto.getCdStagePersType())) {
				List<FormDataGroupDto> formDatacfsd0801List = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDatacfsd0801Para = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILDREN_NAME,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkcfsd0801List = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkcfNmSuff = createBookmark(BookmarkConstants.CHILDREN_NAME_SUFFIX,
						familyAssmtFactDto.getCdNameSuffix());
				bookmarkcfsd0801List.add(bookmarkcfNmSuff);
				BookmarkDto bookmarkComma = createBookmark(BookmarkConstants.CHILD_ROLE_COMMA,
						familyAssmtFactDto.getComma());
				bookmarkcfsd0801List.add(bookmarkComma);
				BookmarkDto bookmarkNmFirst = createBookmark(BookmarkConstants.CHILDREN_NAME_FIRST,
						familyAssmtFactDto.getNmNameFirst());
				bookmarkcfsd0801List.add(bookmarkNmFirst);
				BookmarkDto bookmarkNmLast = createBookmark(BookmarkConstants.CHILDREN_NAME_LAST,
						familyAssmtFactDto.getNmNameLast());
				bookmarkcfsd0801List.add(bookmarkNmLast);
				BookmarkDto bookmarkNmMid = createBookmark(BookmarkConstants.CHILDREN_NAME_MIDDLE,
						familyAssmtFactDto.getNmNameMiddle());
				bookmarkcfsd0801List.add(bookmarkNmMid);
				formDatacfsd0801Para.setBookmarkDtoList(bookmarkcfsd0801List);
				formDatacfsd0801List.add(formDatacfsd0801Para);
				formDataGroupList.addAll(formDatacfsd0801List);
			}
		}

		/**
		 * The prefill data for the Bookmark List
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		BookmarkDto bookmarkTitleCaseNm = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				fdgmFamilyDto.getGenericCaseInfoDto().getNmCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNm);

		BookmarkDto bookmarkCover = createBookmark(BookmarkConstants.COVER_DT_NEXT_REVIEW,
				fdgmFamilyDto.getFamilyPlanDto().getTxtNextReviewMMYYYY());
		bookmarkNonFrmGrpList.add(bookmarkCover);

		BookmarkDto bookmarkTitleCaseNbr = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				fdgmFamilyDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNbr);

		BookmarkDto bookmarkWorkerPhnNbr = createBookmark(BookmarkConstants.COVER_WORKER_PHONE,
				fdgmFamilyDto.getWorkerDetailDto().getNbrPersonPhone());
		bookmarkNonFrmGrpList.add(bookmarkWorkerPhnNbr);

		BookmarkDto bookmarkWorkerPhnExt = createBookmark(BookmarkConstants.COVER_WORKER_PHONE_EXTENSION,
				fdgmFamilyDto.getWorkerDetailDto().getNbrPersonPhoneExtension());
		bookmarkNonFrmGrpList.add(bookmarkWorkerPhnExt);

		if (!ObjectUtils.isEmpty(fdgmFamilyDto.getWorkerDetailDto().getCdNameSuffix())) {
			BookmarkDto bookmarkWorkerSuff = createBookmark(BookmarkConstants.COVER_WORKER_NAME_SUFFIX,
					lookupDao.decode(ServiceConstants.CSUFFIX2, fdgmFamilyDto.getWorkerDetailDto().getCdNameSuffix()));
			bookmarkNonFrmGrpList.add(bookmarkWorkerSuff);
		}

		BookmarkDto bookmarkWorkerName1st = createBookmark(BookmarkConstants.COVER_WORKER_NAME_FIRST,
				fdgmFamilyDto.getWorkerDetailDto().getNmNameFirst());
		bookmarkNonFrmGrpList.add(bookmarkWorkerName1st);

		BookmarkDto bookmarkWorkerSurname = createBookmark(BookmarkConstants.COVER_WORKER_NAME_LAST,
				fdgmFamilyDto.getWorkerDetailDto().getNmNameLast());
		bookmarkNonFrmGrpList.add(bookmarkWorkerSurname);

		BookmarkDto bookmarkWorkerNameMid = createBookmark(BookmarkConstants.COVER_WORKER_NAME_MIDDLE,
				fdgmFamilyDto.getWorkerDetailDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameMid);

		BookmarkDto bookmarkDtComp = createBookmark(BookmarkConstants.COVER_DT_COMPLETED,
				DateUtils.stringDt(fdgmFamilyDto.getFamilyPlanDto().getDtCompleted()));
		bookmarkNonFrmGrpList.add(bookmarkDtComp);

		BookmarkDto bookmarkParentPart = createBookmark(BookmarkConstants.COVER_PARENT_NO_PARTIC,
				fdgmFamilyDto.getFamilyPlanDto().getTxtNotParticipate());
		bookmarkNonFrmGrpList.add(bookmarkParentPart);

		BookmarkDto bookmarkPart1Res = createBookmark(BookmarkConstants.PART_ONE_REASONS,
				fdgmFamilyDto.getFamilyPlanDto().getTxtRsnCpsInvlvmnt());
		bookmarkNonFrmGrpList.add(bookmarkPart1Res);

		BookmarkDto bookmarkPart1Strength = createBookmark(BookmarkConstants.PART_ONE_STRENGTHS,
				fdgmFamilyDto.getFamilyPlanDto().getTxtStrngthsRsrcs());
		bookmarkNonFrmGrpList.add(bookmarkPart1Strength);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		preFillData.setFormDataGroupList(formDataGroupList);
		return preFillData;
	}

}
