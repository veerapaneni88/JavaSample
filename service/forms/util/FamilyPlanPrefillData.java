/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 13, 2018- 11:34:22 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dto.EventIdOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FamilyAssmtFactDto;
import us.tx.state.dfps.service.forms.dto.FamilyChildNameGaolDto;
import us.tx.state.dfps.service.forms.dto.FamilyDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanEvaItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanEvalDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanGoalDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanParticipantsDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanTaskDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;
import us.tx.state.dfps.service.common.util.FormattingUtils;
import us.tx.state.dfps.service.exception.ServiceLayerException;
/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Family Group
 * Conf. Agreement and Family Service Plan (Eval) Mar 13, 2018- 11:34:22 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Component
public class FamilyPlanPrefillData extends DocumentServiceUtil {

	@Autowired
	LookupDao lookupDao;
	
	private static final String DATE_FORMAT = "yyyy-MM-dd";

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
		FamilyDto familyDto = (FamilyDto) parentDtoobj;

		if (ObjectUtils.isEmpty(familyDto.getFamilyPlanDto())) {
			familyDto.setFamilyPlanDto(new FamilyPlanDto());
		}
		if (ObjectUtils.isEmpty(familyDto.getFamilyPlanEvalDto())) {
			familyDto.setFamilyPlanEvalDto(new FamilyPlanEvalDto());
		}
		if (ObjectUtils.isEmpty(familyDto.getStageCaseDtlDto())) {
			familyDto.setStageCaseDtlDto(new StageCaseDtlDto());
		}
		if (ObjectUtils.isEmpty(familyDto.getStagePersonDto())) {
			familyDto.setStagePersonDto(new StagePersonDto());
		}
		if (ObjectUtils.isEmpty(familyDto.getEmployeePersPhNameDto())) {
			familyDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (ObjectUtils.isEmpty(familyDto.getEventDto())) {
			familyDto.setEventDto(new EventDto());
		}
		if (ObjectUtils.isEmpty(familyDto.getEventList())) {
			familyDto.setEventList(new ArrayList<EventIdOutDto>());
		}
		if (ObjectUtils.isEmpty(familyDto.getEvalItemList())) {
			familyDto.setEvalItemList(new ArrayList<FamilyPlanEvaItemDto>());
		}
		if (ObjectUtils.isEmpty(familyDto.getAssessmentList())) {
			familyDto.setAssessmentList(new ArrayList<FamilyAssmtFactDto>());
		}
		if (ObjectUtils.isEmpty(familyDto.getGoalsList())) {
			familyDto.setGoalsList(new ArrayList<FamilyChildNameGaolDto>());
		}
		if (ObjectUtils.isEmpty(familyDto.getParticipantsList())) {
			familyDto.setParticipantsList(new ArrayList<FamilyPlanParticipantsDto>());
		}
		if (ObjectUtils.isEmpty(familyDto.getTaskList())) {
			familyDto.setTaskList(new ArrayList<FamilyPlanTaskDto>());
		}
		if (ObjectUtils.isEmpty(familyDto.getItemList())) {
			familyDto.setItemList(new ArrayList<FamilyPlanItemDto>());
		}
		if (ObjectUtils.isEmpty(familyDto.getDateCompletedList())) {
			familyDto.setDateCompletedList(new ArrayList<FamilyPlanDto>());
		}
		if (ObjectUtils.isEmpty(familyDto.getInitConcernsList())) {
			familyDto.setInitConcernsList(new ArrayList<FamilyPlanDto>());
		}
		if (ObjectUtils.isEmpty(familyDto.getFamilyPlanList())) {
			familyDto.setFamilyPlanList(new ArrayList<FamilyPlanGoalDto>());
		}
		if (ObjectUtils.isEmpty(familyDto.getDateCompList())) {
			familyDto.setDateCompList(new ArrayList<FamilyPlanEvalDto>());
		}

		// Independent Groups
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// FSUStgTp CSEC02D
		if (!ObjectUtils.isEmpty(familyDto.getStageCaseDtlDto().getCdStage())
				&& "FSU".equalsIgnoreCase(familyDto.getStageCaseDtlDto().getCdStage())) {
			List<FormDataGroupDto> formDataTmplatFSUParaList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatFSUPara = createFormDataGroup(FormGroupsConstants.TMPLAT_FSU_PARAGRAPH,
					FormConstants.EMPTY_STRING);
			formDataTmplatFSUParaList.add(formDataTmplatFSUPara);
			formDataGroupList.addAll(formDataTmplatFSUParaList);
		}

		// FPPStgTp CSEC02D
		if (!ObjectUtils.isEmpty(familyDto.getStageCaseDtlDto().getCdStage())
				&& !("FSU".equalsIgnoreCase(familyDto.getStageCaseDtlDto().getCdStage()))) {
			List<FormDataGroupDto> formDataTmplatFPRFSUParaList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatFPRFSUPara = createFormDataGroup(
					FormGroupsConstants.TMPLAT_FPR_FRE_PARAGRAPH, FormConstants.EMPTY_STRING);
			formDataTmplatFPRFSUParaList.add(formDataTmplatFPRFSUPara);
			formDataGroupList.addAll(formDataTmplatFPRFSUParaList);

		}

		// EvalProg CSVC43D		
		FormDataGroupDto formDataTmplatEvalProg = createFormDataGroup(FormGroupsConstants.TMPLAT_EVAL_PROGRESS,
				FormConstants.EMPTY_STRING);
		// EvalCmp2
		if (!ObjectUtils.isEmpty(familyDto.getDateCompList())) {
			List<FormDataGroupDto> formDataTmplatEvalCmp2List = new ArrayList<FormDataGroupDto>();
			for (FamilyPlanEvalDto familyPlanEvalDto : familyDto.getDateCompList()) {
				FormDataGroupDto formDataTmplatEvalCmp2Prog = createFormDataGroup(
						FormGroupsConstants.TMPLAT_EVALUATION_AS_OF_DATE, FormGroupsConstants.TMPLAT_EVAL_PROGRESS);
				List<BookmarkDto> bookmarkEvalCmp2List = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkEvalDate = createBookmark(BookmarkConstants.EVAL_AS_OF_DATE,
						DateUtils.stringDt(familyPlanEvalDto.getDtCompleted()));
				bookmarkEvalCmp2List.add(bookmarkEvalDate);
				// EvalItem CSVC40D
				if (!ObjectUtils.isEmpty(familyDto.getEvalItemList())) {
					List<FormDataGroupDto> formDataTmplatEvalItemList = new ArrayList<FormDataGroupDto>();
					for (FamilyPlanEvaItemDto familyPlanEvaItemDto : familyDto.getEvalItemList()) {
						if (!ObjectUtils.isEmpty(familyPlanEvaItemDto.getTxtItemEvaluation())) {
							
							FormDataGroupDto formDataTmplatEvalItemProg = createFormDataGroup(
									FormGroupsConstants.TMPLAT_EVALUATION_ITEM,
									FormGroupsConstants.TMPLAT_EVALUATION_AS_OF_DATE);
							List<BookmarkDto> bookmarkEvalItemList = new ArrayList<BookmarkDto>();
							BookmarkDto bookmarkEvalItemc = createBookmark(BookmarkConstants.EVALUATION_ITEM,
									familyPlanEvaItemDto.getTxtItemEvaluation());
							bookmarkEvalItemList.add(bookmarkEvalItemc);
							formDataTmplatEvalItemProg.setBookmarkDtoList(bookmarkEvalItemList);
							formDataTmplatEvalItemList.add(formDataTmplatEvalItemProg);
							
						}
					}
					formDataTmplatEvalCmp2Prog.setFormDataGroupList(formDataTmplatEvalItemList);
				}
				formDataTmplatEvalCmp2Prog.setBookmarkDtoList(bookmarkEvalCmp2List);
				formDataTmplatEvalCmp2List.add(formDataTmplatEvalCmp2Prog);
				formDataTmplatEvalProg.setFormDataGroupList(formDataTmplatEvalCmp2List);
			}
		}
		//formDataTmplatEvalProgList.add();
		formDataGroupList.add(formDataTmplatEvalProg);

		// EvalHdg CSVC43D
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanEvalDto().getIdEvent())
				&& familyDto.getFamilyPlanEvalDto().getIdEvent() > 0) {
			List<FormDataGroupDto> formDataTmplatHDGEvalList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatHDGEvalProg = createFormDataGroup(FormGroupsConstants.TMPLAT_HDG_EVALUATION,
					FormConstants.EMPTY_STRING);
			formDataTmplatHDGEvalList.add(formDataTmplatHDGEvalProg);
			formDataGroupList.addAll(formDataTmplatHDGEvalList);
		}

		// IsAnEval CSVC43D
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanEvalDto().getIdFamilyPlanEvaluation())
				&& familyDto.getFamilyPlanEvalDto().getIdFamilyPlanEvaluation() > 0) {
			List<FormDataGroupDto> formDataTmplatEvalDatesList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatEvalDatesProg = createFormDataGroup(FormGroupsConstants.TMPLAT_EVAL_DATES,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkFamPlanEvalList = new ArrayList<BookmarkDto>();	
			// Warranty Defect - 11974 - Incorrect Date Prefill Bookmark Name
			BookmarkDto bookmarkDtEvalComp = createBookmark(BookmarkConstants.DT_EVAL_COMPLETED,
					DateUtils.stringDt(familyDto.getFamilyPlanEvalDto().getDtPlanCompleted()));
			bookmarkFamPlanEvalList.add(bookmarkDtEvalComp);
			// Warranty Defect - 11974 - Incorrect Date Prefill Bookmark Name
			BookmarkDto bookmarkOrgPlanComp = createBookmark(BookmarkConstants.DT_ORIG_PLAN_COMPLETED,
					DateUtils.stringDt(familyDto.getFamilyPlanDto().getDtCompleted()));
			bookmarkFamPlanEvalList.add(bookmarkOrgPlanComp);
			// Warranty Defect - 11974 - Incorrect Date Prefill Bookmark Name
			BookmarkDto bookmarkNextDue = createBookmark(BookmarkConstants.DT_NEXT_DUE,
					familyDto.getFamilyPlanEvalDto().getTxtNextDueMMYYY());
			bookmarkFamPlanEvalList.add(bookmarkNextDue);
			formDataTmplatEvalDatesProg.setBookmarkDtoList(bookmarkFamPlanEvalList);
			formDataTmplatEvalDatesList.add(formDataTmplatEvalDatesProg);
			formDataGroupList.addAll(formDataTmplatEvalDatesList);
		}

		// FGMe3050 CSVC43D
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanEvalDto().getIdEvent())
				&& !ObjectUtils.isEmpty(familyDto.getFamilyPlanEvalDto().getCdFgdmConference())
				&& familyDto.getFamilyPlanEvalDto().getIdEvent() > 0
				&& !familyDto.getFamilyPlanEvalDto().getCdFgdmConference().equals(ServiceConstants.STRING_TEN)
				&& !familyDto.getFamilyPlanEvalDto().getCdFgdmConference().equals(ServiceConstants.STRING_TWENTY)
				&& !familyDto.getFamilyPlanEvalDto().getCdFgdmConference().equals(ServiceConstants.STRING_FORTY)) {
			List<FormDataGroupDto> formDataTmplatFGRC30List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatFGRC30Prog = createFormDataGroup(
					FormGroupsConstants.TMPLAT_FGDM_FGRC30_FTMR50, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkFGMe3050List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkCelebration = createBookmark(BookmarkConstants.CELEBRATION,
					familyDto.getFamilyPlanEvalDto().getTxtCelebration());
			bookmarkFGMe3050List.add(bookmarkCelebration);
			BookmarkDto bookmarkOtherParts = createBookmark(BookmarkConstants.OTHER_PARTICIPANTS_3050,
					familyDto.getFamilyPlanEvalDto().getTxtOtherParticipants());
			bookmarkFGMe3050List.add(bookmarkOtherParts);
			BookmarkDto bookmarkPurposeReconf = createBookmark(BookmarkConstants.PURPOSE_RECONFERENCE,
					familyDto.getFamilyPlanEvalDto().getTxtPurposeReconfernce());
			bookmarkFGMe3050List.add(bookmarkPurposeReconf);
			formDataTmplatFGRC30Prog.setBookmarkDtoList(bookmarkFGMe3050List);
			formDataTmplatFGRC30List.add(formDataTmplatFGRC30Prog);
			formDataGroupList.addAll(formDataTmplatFGRC30List);
		}

		// FGDMpg1 CSVC43D
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanEvalDto().getIdEvent())
				&& !ObjectUtils.isEmpty(familyDto.getFamilyPlanEvalDto().getCdFgdmConference())
				&& !familyDto.getFamilyPlanEvalDto().getIdEvent().equals(ServiceConstants.ZERO)
				&& !familyDto.getFamilyPlanEvalDto().getCdFgdmConference().equals(ServiceConstants.TEN.toString())) {
			List<FormDataGroupDto> formDataTmplatSignaturesList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatSignaturesProg = createFormDataGroup(
					FormGroupsConstants.TMPLAT_FGDM_SIGNATURES, FormConstants.EMPTY_STRING);
			// FGDMpgb1
			if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanDto().getCdPlanType())
					&& "FPP".equalsIgnoreCase(familyDto.getFamilyPlanDto().getCdPlanType())) {
				List<FormDataGroupDto> formDataTmplatFamPRESList1 = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDataTmplatFamPRESProg1 = createFormDataGroup(
						FormGroupsConstants.TMPLAT_FAM_PRES_PGBK, FormGroupsConstants.TMPLAT_FGDM_SIGNATURES);
				formDataTmplatFamPRESList1.add(formDataTmplatFamPRESProg1);
				formDataTmplatSignaturesProg.setFormDataGroupList(formDataTmplatFamPRESList1);
			}
			formDataTmplatSignaturesList.add(formDataTmplatSignaturesProg);
			formDataGroupList.addAll(formDataTmplatSignaturesList);
		}

		// FGDMshdg CSVC43D
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanEvalDto().getIdEvent())
				&& !ObjectUtils.isEmpty(familyDto.getFamilyPlanEvalDto().getCdFgdmConference())
				&& familyDto.getFamilyPlanEvalDto().getIdEvent() > 0
				// Warranty Defect - 11974 - Logic Change to display the correct error
				&& !familyDto.getFamilyPlanEvalDto().getCdFgdmConference().equals(ServiceConstants.STRING_TEN)) {
			List<FormDataGroupDto> formDataTmplatFGDMList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatFGDMProg = createFormDataGroup(FormGroupsConstants.TMPLAT_HDG_FGDM,
					FormConstants.EMPTY_STRING);
			formDataTmplatFGDMList.add(formDataTmplatFGDMProg);
			formDataGroupList.addAll(formDataTmplatFGDMList);
		}

		// FGMe2040 CSVC43D
		/*
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanEvalDto().getIdEvent())
				&& !ObjectUtils.isEmpty(familyDto.getFamilyPlanEvalDto().getCdFgdmConference())
				&& familyDto.getFamilyPlanEvalDto().getIdEvent() > 0
				// Warranty Defect - 11974 - Logic Change to display the correct error
				&& !familyDto.getFamilyPlanEvalDto().getCdFgdmConference().equals(ServiceConstants.STRING_TEN)
				&& !familyDto.getFamilyPlanEvalDto().getCdFgdmConference().equals(ServiceConstants.STRING_THIRTY)
				&& !familyDto.getFamilyPlanEvalDto().getCdFgdmConference().equals(ServiceConstants.STRING_FIFTY)) {
			List<FormDataGroupDto> formDataTmplatFGC20List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatFGC20Prog = createFormDataGroup(FormGroupsConstants.TMPLAT_FGDM_FGC20_FTM40,
					FormConstants.EMPTY_STRING);
			formDataTmplatFGC20List.add(formDataTmplatFGC20Prog);
			formDataGroupList.addAll(formDataTmplatFGC20List);
		}
		*/
		

		// SUBhdg CSVC41D
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanDto().getCdPlanType())
				&& "PSC".equalsIgnoreCase(familyDto.getFamilyPlanDto().getCdPlanType())) {
			List<FormDataGroupDto> formDataTmplatSUBList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatSUBProg = createFormDataGroup(FormGroupsConstants.TMPLAT_HDG_SUB_CARE,
					FormConstants.EMPTY_STRING);
			formDataTmplatSUBList.add(formDataTmplatSUBProg);
			formDataGroupList.addAll(formDataTmplatSUBList);
		}

		// FPRhdg CSVC41D
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanDto().getCdPlanType())
				&& "FPP".equalsIgnoreCase(familyDto.getFamilyPlanDto().getCdPlanType())) {
			List<FormDataGroupDto> formDataTmplatHDGFamList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatHDGFamProg = createFormDataGroup(FormGroupsConstants.TMPLAT_HDG_FAMILY_PRES,
					FormConstants.EMPTY_STRING);
			formDataTmplatHDGFamList.add(formDataTmplatHDGFamProg);
			formDataGroupList.addAll(formDataTmplatHDGFamList);
		}

		// PermGoal CSVC41D
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanDto().getCdPlanType())
				&& "PSC".equalsIgnoreCase(familyDto.getFamilyPlanDto().getCdPlanType())) {
			List<FormDataGroupDto> formDataTmplatPremGoalsList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatPremGoalsProg = createFormDataGroup(
					FormGroupsConstants.TMPLAT_FSU_PERM_GOALS, FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> formDataTmplatChldGoalList = new ArrayList<FormDataGroupDto>();
			// ChldGoal
			if (!ObjectUtils.isEmpty(familyDto.getGoalsList())) {
				for (FamilyChildNameGaolDto familyChildNameGaolDto : familyDto.getGoalsList()) {
					if (!ObjectUtils.isEmpty(familyChildNameGaolDto.getCdFamPlanPermGoal())) {

						FormDataGroupDto formDataTmplatChldGoalProg = createFormDataGroup(
								FormGroupsConstants.TMPLAT_CHILDREN_GOALS, FormGroupsConstants.TMPLAT_FSU_PERM_GOALS);
						List<BookmarkDto> bookmarkChldGoalList = new ArrayList<BookmarkDto>();
						BookmarkDto bookmarkPermGoalDate = createBookmark(BookmarkConstants.PERMANENCY_GOAL_DATE,
								DateUtils.stringDt(familyChildNameGaolDto.getDtFamPlanPermGoalTarget()));
						bookmarkChldGoalList.add(bookmarkPermGoalDate);
						if (!ObjectUtils.isEmpty(familyChildNameGaolDto.getCdFamPlanPermGoal())) {
							BookmarkDto bookmarkPermGaol = createBookmark(BookmarkConstants.PERMANENCY_GOAL, lookupDao
									.decode(ServiceConstants.CCPPRMGL, familyChildNameGaolDto.getCdFamPlanPermGoal()));
							bookmarkChldGoalList.add(bookmarkPermGaol);
						}
						if (!ObjectUtils.isEmpty(familyChildNameGaolDto.getCdNameSuffix())) {
							BookmarkDto bookmarkChildSuff = createBookmark(BookmarkConstants.CHILD_NAME_SFX, lookupDao
									.decode(ServiceConstants.CSUFFIX2, familyChildNameGaolDto.getCdNameSuffix()));
							bookmarkChldGoalList.add(bookmarkChildSuff);
						}
						BookmarkDto bookmarkChild1Name = createBookmark(BookmarkConstants.CHILD_NAME_1ST,
								familyChildNameGaolDto.getNmNameFirst());
						bookmarkChldGoalList.add(bookmarkChild1Name);
						BookmarkDto bookmarkChildSurname = createBookmark(BookmarkConstants.CHILD_SURNAME,
								familyChildNameGaolDto.getNmNameLast());
						bookmarkChldGoalList.add(bookmarkChildSurname);
						BookmarkDto bookmarkChildMid = createBookmark(BookmarkConstants.CHILD_NAME_MID,
								familyChildNameGaolDto.getNmNameMiddle());
						bookmarkChldGoalList.add(bookmarkChildMid);
						formDataTmplatChldGoalProg.setBookmarkDtoList(bookmarkChldGoalList);
						formDataTmplatChldGoalList.add(formDataTmplatChldGoalProg);

					}
				}
			}
			formDataTmplatPremGoalsProg.setFormDataGroupList(formDataTmplatChldGoalList);
			formDataTmplatPremGoalsList.add(formDataTmplatPremGoalsProg);
			formDataGroupList.addAll(formDataTmplatPremGoalsList);
		}

		// IsAPlan CSVC41D
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanDto().getIdEvalEvent())
				&& (familyDto.getFamilyPlanDto().getIdEvalEvent()) == 0) {
			List<FormDataGroupDto> formDataTmplatPlanDatesList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatPlanDatesProg = createFormDataGroup(FormGroupsConstants.TMPLAT_PLAN_DATES,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkIsAPlanList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkPlanComp = createBookmark(BookmarkConstants.DT_PLAN_COMPLETED,
					familyDto.getFamilyPlanDto().getDtCompleted());
			bookmarkIsAPlanList.add(bookmarkPlanComp);
			BookmarkDto bookmarkNextReview = createBookmark(BookmarkConstants.DT_NEXT_REVIEW,
					familyDto.getFamilyPlanDto().getTxtNextReviewMMYYYY());
			bookmarkIsAPlanList.add(bookmarkNextReview);
			formDataTmplatPlanDatesProg.setBookmarkDtoList(bookmarkIsAPlanList);
			formDataTmplatPlanDatesList.add(formDataTmplatPlanDatesProg);
			formDataGroupList.addAll(formDataTmplatPlanDatesList);
		}

		// FGMp2040 CSVC41D
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanDto().getIdEvent())
				&& !ObjectUtils.isEmpty(familyDto.getFamilyPlanDto().getCdFgdmConference())
				&& familyDto.getFamilyPlanDto().getIdEvent() == 0
						&& !familyDto.getFamilyPlanEvalDto().getCdFgdmConference().equals(ServiceConstants.STRING_TEN)
						&& !familyDto.getFamilyPlanEvalDto().getCdFgdmConference().equals(ServiceConstants.STRING_THIRTY)
						&& !familyDto.getFamilyPlanEvalDto().getCdFgdmConference().equals(ServiceConstants.STRING_FIFTY)) {
			List<FormDataGroupDto> formDataTmplatFGC20pList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatFGC20pProg = createFormDataGroup(FormGroupsConstants.TMPLAT_FGDM_FGC20_FTM40,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkFGMp2040List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkOtherParts = createBookmark(BookmarkConstants.OTHER_PARTICIPANTS_2040,
					familyDto.getFamilyPlanDto().getTxtOtherParticipants());
			bookmarkFGMp2040List.add(bookmarkOtherParts);
			formDataTmplatFGC20pProg.setBookmarkDtoList(bookmarkFGMp2040List);
			formDataTmplatFGC20pList.add(formDataTmplatFGC20pProg);
			formDataGroupList.addAll(formDataTmplatFGC20pList);
		}

		// RsnInv CSVC41D
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanDto())) {
			List<FormDataGroupDto> formDataTmplatReasonsList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatReasonsProg = createFormDataGroup(
					FormGroupsConstants.TMPLAT_REASONS_FOR_INVOLVEMENT, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRsnInvList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkReason = createBookmark(BookmarkConstants.REASON_CPS_INVOLVEMENT,
					formatTextValue(familyDto.getFamilyPlanDto().getTxtRsnCpsInvlvmnt()));
			bookmarkRsnInvList.add(bookmarkReason);
			formDataTmplatReasonsProg.setBookmarkDtoList(bookmarkRsnInvList);
			formDataTmplatReasonsList.add(formDataTmplatReasonsProg);
			formDataGroupList.addAll(formDataTmplatReasonsList);
		}

		// HopeDream
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanDto())) {
			List<FormDataGroupDto> formDataTmplatHopeList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatHopeProg = createFormDataGroup(FormGroupsConstants.TMPLAT_HOPES_AND_DREAMS,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkHopeDreamList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkHopeDream = createBookmark(BookmarkConstants.HOPES_AND_DREAMS,
					formatTextValue(familyDto.getFamilyPlanDto().getTxtHopesDreams()));
			bookmarkHopeDreamList.add(bookmarkHopeDream);
			formDataTmplatHopeProg.setBookmarkDtoList(bookmarkHopeDreamList);
			formDataTmplatHopeList.add(formDataTmplatHopeProg);
			formDataGroupList.addAll(formDataTmplatHopeList);
		}

		// StrnSupp
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanDto())) {
			List<FormDataGroupDto> formDataTmplatStrengthList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatStrengthProg = createFormDataGroup(
					FormGroupsConstants.TMPLAT_STRENGTHS_AND_SUPPORTS, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkStrnSuppList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkStrnSupp = createBookmark(BookmarkConstants.STRENGTHS_AND_SUPPORTS,
					formatTextValue(familyDto.getFamilyPlanDto().getTxtStrngthsRsrcs()));
			bookmarkStrnSuppList.add(bookmarkStrnSupp);
			formDataTmplatStrengthProg.setBookmarkDtoList(bookmarkStrnSuppList);
			formDataTmplatStrengthList.add(formDataTmplatStrengthProg);
			formDataGroupList.addAll(formDataTmplatStrengthList);
		}

		// CommSupp
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanDto())) {
			List<FormDataGroupDto> formDataTmplatCommsList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatCommsProg = createFormDataGroup(
					FormGroupsConstants.TMPLAT_COMMUNITY_SUPPORTS, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkCommSuppList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkCommSupp = createBookmark(BookmarkConstants.COMMUNITY_SUPPORTS,
					formatTextValue(familyDto.getFamilyPlanDto().getTxtCommunitySupports()));
			bookmarkCommSuppList.add(bookmarkCommSupp);
			formDataTmplatCommsProg.setBookmarkDtoList(bookmarkCommSuppList);
			formDataTmplatCommsList.add(formDataTmplatCommsProg);
			formDataGroupList.addAll(formDataTmplatCommsList);
		}

		// EducResp
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanDto())) {
			List<FormDataGroupDto> formDataTmplatEducationList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatEducationProg = createFormDataGroup(FormGroupsConstants.TMPLAT_EDUC_RESP,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkEducRespList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkEducResp = createBookmark(BookmarkConstants.EDUCATION_RESPONSIBILITY,
					formatTextValue(familyDto.getFamilyPlanDto().getTxtRespChildsEducation()));
			bookmarkEducRespList.add(bookmarkEducResp);
			formDataTmplatEducationProg.setBookmarkDtoList(bookmarkEducRespList);
			formDataTmplatEducationList.add(formDataTmplatEducationProg);
			formDataGroupList.addAll(formDataTmplatEducationList);
		}

		// PrntNoPt
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanDto())) {
			List<FormDataGroupDto> formDataTmplatParentList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatParentProg = createFormDataGroup(FormGroupsConstants.TMPLAT_PARENT_NO_PART,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkPrntNoPtList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkPrntNoPt = createBookmark(BookmarkConstants.PARENT_NO_PARTICIPATE,
					formatTextValue(familyDto.getFamilyPlanDto().getTxtNotParticipate()));
			bookmarkPrntNoPtList.add(bookmarkPrntNoPt);
			formDataTmplatParentProg.setBookmarkDtoList(bookmarkPrntNoPtList);
			formDataTmplatParentList.add(formDataTmplatParentProg);
			formDataGroupList.addAll(formDataTmplatParentList);
		}

		// EvalNCl

		List<FormDataGroupDto> formDataEvalNClList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto formDataEvalNClProg = createFormDataGroup(FormGroupsConstants.TMPLAT_EVAL_NEW_CONCERNS_LIST,
				FormConstants.EMPTY_STRING);
		// EvalCmp1 CSVC58D
		if (!ObjectUtils.isEmpty(familyDto.getDateCompList())) {
			List<FormDataGroupDto> formDataEvalCmp1List = new ArrayList<FormDataGroupDto>();
			for (FamilyPlanEvalDto familyPlanEvalDto : familyDto.getDateCompList()) {
				FormDataGroupDto formDataEvalCmp1Prog = createFormDataGroup(
						FormGroupsConstants.TMPLAT_DATE_EVAL_COMPLETED,
						FormGroupsConstants.TMPLAT_EVAL_NEW_CONCERNS_LIST);
				List<BookmarkDto> bookmarkEvalCmp1List = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkNewConc = createBookmark(BookmarkConstants.NEW_CONCERN_DATE,
						DateUtils.stringDt(familyPlanEvalDto.getDtCompleted()));
				bookmarkEvalCmp1List.add(bookmarkNewConc);
				// NewConcL
				List<FormDataGroupDto> formDataNewConcLList = new ArrayList<FormDataGroupDto>();
				if (!ObjectUtils.isEmpty(familyDto.getEvalItemList())) {					
					for (FamilyPlanEvaItemDto familyPlanEvaItemDto : familyDto.getEvalItemList()) {
						if (!ObjectUtils.isEmpty(familyPlanEvaItemDto.getTxtNewConcerns())) {
							
							FormDataGroupDto formDataNewConcLProg = createFormDataGroup(
									FormGroupsConstants.TMPLAT_NEW_CONCERNS_LIST,
									FormGroupsConstants.TMPLAT_DATE_EVAL_COMPLETED);
							List<BookmarkDto> bookmarkNewConcLList = new ArrayList<BookmarkDto>();
							BookmarkDto bookmarkNewConcL = createBookmark(BookmarkConstants.NEW_CONCERN,
									formatTextValue(familyPlanEvaItemDto.getTxtNewConcerns()));
							bookmarkNewConcLList.add(bookmarkNewConcL);
							formDataNewConcLProg.setBookmarkDtoList(bookmarkNewConcLList);
							formDataNewConcLList.add(formDataNewConcLProg);
							
						}
					}
					formDataEvalCmp1Prog.setFormDataGroupList(formDataNewConcLList);
				}
				formDataEvalCmp1Prog.setBookmarkDtoList(bookmarkEvalCmp1List);
				formDataEvalCmp1List.add(formDataEvalCmp1Prog);
				formDataEvalNClProg.setFormDataGroupList(formDataEvalCmp1List);
			}
		}
		//formDataEvalNClList.add(formDataEvalNClProg);
		formDataGroupList.add(formDataEvalNClProg);

		// FGDMpg2
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanDto().getIdEvalEvent())
				&& !ObjectUtils.isEmpty(familyDto.getFamilyPlanDto().getCdFgdmConference())
				&& familyDto.getFamilyPlanDto().getIdEvalEvent().equals(ServiceConstants.ZERO)
				&& !familyDto.getFamilyPlanDto().getCdFgdmConference().equals(ServiceConstants.TEN.toString())) {
			List<FormDataGroupDto> formDataTmplatFGDMSigsList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatFGDMSigsProg = createFormDataGroup(
					FormGroupsConstants.TMPLAT_FGDM_SIGNATURES, FormConstants.EMPTY_STRING);
			// FGDMpgb2
			if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanDto().getCdPlanType())
					&& "FPP".equalsIgnoreCase(familyDto.getFamilyPlanDto().getCdPlanType())) {
				List<FormDataGroupDto> formDataTmplatFamPRESList2 = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDataTmplatFamPRESProg2 = createFormDataGroup(
						FormGroupsConstants.TMPLAT_FAM_PRES_PGBK, FormGroupsConstants.TMPLAT_FGDM_SIGNATURES);
				formDataTmplatFamPRESList2.add(formDataTmplatFamPRESProg2);
				formDataTmplatFGDMSigsProg.setFormDataGroupList(formDataTmplatFamPRESList2);
			}
			formDataTmplatFGDMSigsList.add(formDataTmplatFGDMSigsProg);
			formDataGroupList.addAll(formDataTmplatFGDMSigsList);
		}

		// FGDMshdg
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanDto().getIdEvalEvent())
				&& !ObjectUtils.isEmpty(familyDto.getFamilyPlanDto().getCdFgdmConference())
				&& familyDto.getFamilyPlanDto().getIdEvalEvent().equals(ServiceConstants.ZERO) && !familyDto
						.getFamilyPlanDto().getCdFgdmConference().equals(ServiceConstants.TEN_VALUE_VAL.toString())) {
			List<FormDataGroupDto> formDataTmplatFGDMshdgList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatFGDMshdgProg = createFormDataGroup(FormGroupsConstants.TMPLAT_HDG_FGDM,
					FormConstants.EMPTY_STRING);
			formDataTmplatFGDMshdgList.add(formDataTmplatFGDMshdgProg);
			formDataGroupList.addAll(formDataTmplatFGDMshdgList);
		}

		// FGMp3050
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanDto().getIdEvent())
				&& !ObjectUtils.isEmpty(familyDto.getFamilyPlanDto().getCdFgdmConference())
				&& !familyDto.getFamilyPlanDto().getIdEvent().equals(ServiceConstants.ZERO)
				&& !familyDto.getFamilyPlanDto().getCdFgdmConference().equals(ServiceConstants.CWVRQSTS_10)
				&& !familyDto.getFamilyPlanDto().getCdFgdmConference().equals(ServiceConstants.CSSCCSTA_20)
				&& !familyDto.getFamilyPlanDto().getCdFgdmConference().equals(ServiceConstants.CSSCCTBL_40)) {
			List<FormDataGroupDto> formDataTmplatFGRC30pList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatFGRC30pProg = createFormDataGroup(
					FormGroupsConstants.TMPLAT_FGDM_FGRC30_FTMR50, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkFGM30pList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkCelebrations = createBookmark(BookmarkConstants.CELEBRATION,
					formatTextValue(familyDto.getFamilyPlanDto().getTxtCelebration()));
			bookmarkFGM30pList.add(bookmarkCelebrations);
			BookmarkDto bookmarkOtherParts = createBookmark(BookmarkConstants.OTHER_PARTICIPANTS_3050,
					formatTextValue(familyDto.getFamilyPlanDto().getTxtOtherParticipants()));
			bookmarkFGM30pList.add(bookmarkOtherParts);
			BookmarkDto bookmarkPurpose = createBookmark(BookmarkConstants.PURPOSE_RECONFERENCE,
					formatTextValue(familyDto.getFamilyPlanDto().getTxtPurposeReconference()));
			bookmarkFGM30pList.add(bookmarkPurpose);
			formDataTmplatFGRC30pProg.setBookmarkDtoList(bookmarkFGM30pList);
			formDataTmplatFGRC30pList.add(formDataTmplatFGRC30pProg);
			formDataGroupList.addAll(formDataTmplatFGRC30pList);
		}

		/*// NewConcL
		if (!ObjectUtils.isEmpty(familyDto.getEvalItemList())) {
			for (FamilyPlanEvaItemDto familyPlanEvaItemDto : familyDto.getEvalItemList()) {
				if (!ObjectUtils.isEmpty(familyPlanEvaItemDto.getTxtNewConcerns())) {
					List<FormDataGroupDto> formDataTmplatNewConcList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataTmplatNewConcProg = createFormDataGroup(
							FormGroupsConstants.TMPLAT_NEW_CONCERNS_LIST, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkNewConcList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkNewConc = createBookmark(BookmarkConstants.NEW_CONCERN,
							familyPlanEvaItemDto.getTxtNewConcerns());
					bookmarkNewConcList.add(bookmarkNewConc);
					formDataTmplatNewConcProg.setBookmarkDtoList(bookmarkNewConcList);
					formDataTmplatNewConcList.add(formDataTmplatNewConcProg);
					formDataGroupList.addAll(formDataTmplatNewConcList);
				}
			}
		}*/

		// PRNadult CLSC23D
		if (!ObjectUtils.isEmpty(familyDto.getAssessmentList())) {
			boolean indFirst = false;
			int counter = 0;
			for (FamilyAssmtFactDto familyAssmtFactDto : familyDto.getAssessmentList()) {
				if (!ObjectUtils.isEmpty(familyAssmtFactDto.getCdStagePersType())
						&& "AD".equalsIgnoreCase(familyAssmtFactDto.getCdStagePersType())) {
					if (counter >= 1) {
						indFirst = true;
					}
					List<FormDataGroupDto> formDataTmplatPRNadultList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataTmplatPRNadultProg = createFormDataGroup(
							FormGroupsConstants.TMPLAT_PARENT_NAME, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkPRNadultList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkPrnt1Name = createBookmark(BookmarkConstants.PARENT_NM_1ST,
							familyAssmtFactDto.getNmNameFirst());
					bookmarkPRNadultList.add(bookmarkPrnt1Name);
					if (!ObjectUtils.isEmpty(familyAssmtFactDto.getCdNameSuffix())) {
						BookmarkDto bookmarkPrntSuff = createBookmark(BookmarkConstants.PARENT_NM_SFX,
								lookupDao.decode(ServiceConstants.CSUFFIX2, familyAssmtFactDto.getCdNameSuffix()));
						bookmarkPRNadultList.add(bookmarkPrntSuff);
					}
					BookmarkDto bookmarkPrntLast = createBookmark(BookmarkConstants.PARENT_SURNAME,
							familyAssmtFactDto.getNmNameLast());
					bookmarkPRNadultList.add(bookmarkPrntLast);
					if (indFirst) {
						BookmarkDto bookmarkComma = createBookmark(BookmarkConstants.PRNADULT_ROLE_COMMA,
								familyAssmtFactDto.getComma());
						bookmarkPRNadultList.add(bookmarkComma);
					}
					BookmarkDto bookmarkChildPrntLast = createBookmark(BookmarkConstants.PARENT_NM_MID,
							familyAssmtFactDto.getNmNameMiddle());
					bookmarkPRNadultList.add(bookmarkChildPrntLast);
					formDataTmplatPRNadultProg.setBookmarkDtoList(bookmarkPRNadultList);
					formDataTmplatPRNadultList.add(formDataTmplatPRNadultProg);
					formDataGroupList.addAll(formDataTmplatPRNadultList);
					counter++;
				}
			}
		}

		// PRNchild CLSC23D
		if (!ObjectUtils.isEmpty(familyDto.getAssessmentList())) {
			boolean indFirst = false;
			int counter = 0;
			for (FamilyAssmtFactDto familyAssmtFactDto : familyDto.getAssessmentList()) {
				if (!ObjectUtils.isEmpty(familyAssmtFactDto.getCdStagePersType())
						&& "CH".equalsIgnoreCase(familyAssmtFactDto.getCdStagePersType())) {
					if (counter >= 1) {
						indFirst = true;
					}
					List<FormDataGroupDto> formDataTmplatPRNChildList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataTmplatPRNChildProg = createFormDataGroup(
							FormGroupsConstants.TMPLAT_CHILDREN_NAME, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkPRNChildList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkPrnt1Name = createBookmark(BookmarkConstants.CHILDREN_NM_1ST,
							familyAssmtFactDto.getNmNameFirst());
					bookmarkPRNChildList.add(bookmarkPrnt1Name);
					if (!ObjectUtils.isEmpty(familyAssmtFactDto.getCdNameSuffix())) {
						BookmarkDto bookmarkPrntSuff = createBookmark(BookmarkConstants.CHILDREN_NM_SFX,
								lookupDao.decode(ServiceConstants.CSUFFIX2, familyAssmtFactDto.getCdNameSuffix()));
						bookmarkPRNChildList.add(bookmarkPrntSuff);
					}
					BookmarkDto bookmarkPrntLast = createBookmark(BookmarkConstants.CHILDREN_SURNAME,
							familyAssmtFactDto.getNmNameLast());
					bookmarkPRNChildList.add(bookmarkPrntLast);
					if (indFirst) {
						BookmarkDto bookmarkComma = createBookmark(BookmarkConstants.CHILDREN_ROLE_COMMA,
								familyAssmtFactDto.getComma());
						bookmarkPRNChildList.add(bookmarkComma);
					}
					BookmarkDto bookmarkChildPrntLast = createBookmark(BookmarkConstants.CHILDREN_NM_MID,
							familyAssmtFactDto.getNmNameMiddle());
					bookmarkPRNChildList.add(bookmarkChildPrntLast);
					formDataTmplatPRNChildProg.setBookmarkDtoList(bookmarkPRNChildList);
					formDataTmplatPRNChildList.add(formDataTmplatPRNChildProg);
					formDataGroupList.addAll(formDataTmplatPRNChildList);
					counter++;
				}
			}
		}

		// Particpn
		if (!ObjectUtils.isEmpty(familyDto.getParticipantsList())) {
			for (FamilyPlanParticipantsDto familyPlanParticipantsDto : familyDto.getParticipantsList()) {
				if (!ObjectUtils.isEmpty(familyPlanParticipantsDto.getIndFamPlanPart())
						&& "Y".equalsIgnoreCase(familyPlanParticipantsDto.getIndFamPlanPart())) {
					List<FormDataGroupDto> formDataTmplatParticpnList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataTmplatParticpnProg = createFormDataGroup(
							FormGroupsConstants.TMPLAT_PARTICIPANTS, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkParticpnList = new ArrayList<BookmarkDto>();
					if (!ObjectUtils.isEmpty(familyPlanParticipantsDto.getCdNameSuffix())) {
						BookmarkDto bookmarkPartSuff = createBookmark(BookmarkConstants.PARTICIPANT_NAME_SFX, lookupDao
								.decode(ServiceConstants.CSUFFIX2, familyPlanParticipantsDto.getCdNameSuffix()));
						bookmarkParticpnList.add(bookmarkPartSuff);
					}
					if (!ObjectUtils.isEmpty(familyPlanParticipantsDto.getCdStagePersRelInt())) {
						BookmarkDto bookmarkPartRel = createBookmark(BookmarkConstants.PARTICIPANT_RELATIONSHIP,
								lookupDao.decode(ServiceConstants.CRPTRINT,
										familyPlanParticipantsDto.getCdStagePersRelInt()));
						bookmarkParticpnList.add(bookmarkPartRel);
					}
					BookmarkDto bookmarkPart1Name = createBookmark(BookmarkConstants.PARTICIPANT_NAME_1ST,
							familyPlanParticipantsDto.getNmNameFirst());
					bookmarkParticpnList.add(bookmarkPart1Name);
					BookmarkDto bookmarkPartSurname = createBookmark(BookmarkConstants.PARTICIPANT_SURNAME,
							familyPlanParticipantsDto.getNmNameLast());
					bookmarkParticpnList.add(bookmarkPartSurname);
					BookmarkDto bookmarkPartMid = createBookmark(BookmarkConstants.PARTICIPANT_NAME_MID,
							familyPlanParticipantsDto.getNmNameMiddle());
					bookmarkParticpnList.add(bookmarkPartMid);
					formDataTmplatParticpnProg.setBookmarkDtoList(bookmarkParticpnList);
					formDataTmplatParticpnList.add(formDataTmplatParticpnProg);
					formDataGroupList.addAll(formDataTmplatParticpnList);
				}
			}
		}

		// TaskSvcL CSVC52D
		if (!ObjectUtils.isEmpty(familyDto.getTaskList())) {
			for (FamilyPlanTaskDto familyPlanTaskDto : familyDto.getTaskList()) {
				FormDataGroupDto formDataTmplatTaskSvcLProg = createFormDataGroup(FormGroupsConstants.TMPLAT_TASKS,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkTaskSvcLList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkTaskCreate = createBookmark(BookmarkConstants.TASK_CREATED,
						DateUtils.stringDt(familyPlanTaskDto.getDtCreated()));
				bookmarkTaskSvcLList.add(bookmarkTaskCreate);
				BookmarkDto bookmarkTaskAssign = createBookmark(BookmarkConstants.TXT_TASK_ASSIGNED_TO,
						formatTextValue(familyPlanTaskDto.getTxtAssignedTo()));
				bookmarkTaskSvcLList.add(bookmarkTaskAssign);
				BookmarkDto bookmarkCourt = createBookmark(BookmarkConstants.TXT_COURT_ORDERED_TO,
						formatTextValue(familyPlanTaskDto.getTxtCourtOrderedTo()));
				bookmarkTaskSvcLList.add(bookmarkCourt);
				BookmarkDto bookmarkTaskComp = createBookmark(BookmarkConstants.TXT_TASK_COMPLETED,
						familyPlanTaskDto.getTxtDtCompleted());
				bookmarkTaskSvcLList.add(bookmarkTaskComp);
				BookmarkDto bookmarkTaskTxt = createBookmark(BookmarkConstants.TASK_TEXT,
						formatTextValue(familyPlanTaskDto.getTxtTask()));
				bookmarkTaskSvcLList.add(bookmarkTaskTxt);
				formDataTmplatTaskSvcLProg.setBookmarkDtoList(bookmarkTaskSvcLList);
				formDataGroupList.add(formDataTmplatTaskSvcLProg);

			}
		}

		// PlanGoal CSVC56D
		List<FormDataGroupDto> formDataTmplatPlanGoalList = new ArrayList<FormDataGroupDto>();
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanList())) {
			for (FamilyPlanGoalDto familyPlanGoalDto : familyDto.getFamilyPlanList()) {				
				FormDataGroupDto formDataTmplatPlanGoalProg = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SVC_PLAN_GOALS, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPlanGoalList = new ArrayList<BookmarkDto>();
				// Warranty Defect Fix - 12185/12391  - To Replace the Carriage Return /Line Feed with Break Tag
				BookmarkDto bookmarkPlanGoal = createBookmark(BookmarkConstants.SVC_PLAN_GOAL,
						formatTextValue(familyPlanGoalDto.getTxtGoal()));
				bookmarkPlanGoalList.add(bookmarkPlanGoal);
				formDataTmplatPlanGoalProg.setBookmarkDtoList(bookmarkPlanGoalList);
				formDataTmplatPlanGoalList.add(formDataTmplatPlanGoalProg);				
			}
			formDataGroupList.addAll(formDataTmplatPlanGoalList);
		}

		// InitCnV2 CSESC8D
		if (!ObjectUtils.isEmpty(familyDto.getEventDto().getIndInitConcernsVer())
				&& (familyDto.getEventDto().getIndInitConcernsVer().equals(ServiceConstants.TWO_LONG))) {
			/*List<FormDataGroupDto> formDataTmplatInitCnV2List = new ArrayList<FormDataGroupDto>();*/
			FormDataGroupDto formDataTmplatInitCnV2Prog = createFormDataGroup(
					FormGroupsConstants.TMPLAT_INITIAL_CONCERNS_IND2, FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> formDataTmplatInitCon2List = new ArrayList<FormDataGroupDto>();
			// InitCon2 CSVC54D
			if (!ObjectUtils.isEmpty(familyDto.getItemList())) {
				for (FamilyPlanItemDto familyPlanItemDto : familyDto.getItemList()) {
					
					FormDataGroupDto formDataTmplatInitCon2Prog = createFormDataGroup(
							FormGroupsConstants.TMPLAT_INITIAL_CONCERNS_LIST,
							FormGroupsConstants.TMPLAT_INITIAL_CONCERNS_IND2);
					List<BookmarkDto> bookmarkInitCon2List = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkConcDate = createBookmark(BookmarkConstants.INITIAL_CONCERN_DATE,
							familyPlanItemDto.getDtInitiallyAddressed());
					bookmarkInitCon2List.add(bookmarkConcDate);
					BookmarkDto bookmarkConcTxT = createBookmark(BookmarkConstants.INITIAL_CONCERN_TEXT,
							formatTextValue(familyPlanItemDto.getTxtInitialConcerns()));
					bookmarkInitCon2List.add(bookmarkConcTxT);
					formDataTmplatInitCon2Prog.setBookmarkDtoList(bookmarkInitCon2List);
					formDataTmplatInitCon2List.add(formDataTmplatInitCon2Prog);
					
				}
			}
			formDataTmplatInitCnV2Prog.setFormDataGroupList(formDataTmplatInitCon2List);
			//formDataTmplatInitCnV2List.add(formDataTmplatInitCnV2Prog);
			formDataGroupList.add(formDataTmplatInitCnV2Prog);
		}

		// InitCnV3 CSESC8D
		if (!ObjectUtils.isEmpty(familyDto.getEventDto().getIndInitConcernsVer())
				&& familyDto.getEventDto().getIndInitConcernsVer().equals(ServiceConstants.THREE_VAL)) {
			List<FormDataGroupDto> formDataTmplatInitCnV3List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatInitCnV3Prog = createFormDataGroup(
					FormGroupsConstants.TMPLAT_INITIAL_CONCERNS_IND3, FormConstants.EMPTY_STRING);
			// InitDate CSVC53D
			// Warranty Defect Fix - 12042 - To fix the Initial Concern issue display
			List<FormDataGroupDto> formDataTmplatInitDateList = new ArrayList<FormDataGroupDto>();
			if (!ObjectUtils.isEmpty(familyDto.getDateCompList())) {
				for (FamilyPlanEvalDto familyPlanDto : familyDto.getDateCompList()) {
					
					boolean isInitialConcernExist=false;
					FormDataGroupDto formDataTmplatInitDateProg = createFormDataGroup(
							FormGroupsConstants.TMPLAT_DATE_COMPLETED_PLAN_EVAL,
							FormGroupsConstants.TMPLAT_INITIAL_CONCERNS_IND3);
					List<BookmarkDto> bookmarkInitDateList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkIntConcDate = createBookmark(BookmarkConstants.INIT_CONCERN_DATE,
							DateUtils.stringDt(familyPlanDto.getDtCompleted()));
					bookmarkInitDateList.add(bookmarkIntConcDate);
					List<FormDataGroupDto> formDataTmplatInitConList = new ArrayList<FormDataGroupDto>();
					for (FamilyPlanItemDto familyPlanItemDto : familyDto.getItemList()) {	
					
					Date dtFamilyPlanEvalCompleted = new Date();
					Date dtFamilyDtCompleted=new Date();
					Date dtInitialConcern = new Date();					
					try {
						dtFamilyDtCompleted= new SimpleDateFormat(DATE_FORMAT).parse(familyPlanDto.getDtCompleted().toString());
						dtFamilyPlanEvalCompleted = new SimpleDateFormat(DATE_FORMAT).parse(familyDto.getFamilyPlanEvalDto().getDtPlanCompleted().toString());
						dtInitialConcern = new SimpleDateFormat(DATE_FORMAT).parse(familyPlanItemDto.getDtInitiallyAddressed().toString());
					} catch (ParseException e) {
						ServiceLayerException serviceLayerException = new ServiceLayerException(e.getMessage());
						serviceLayerException.initCause(e);
						throw serviceLayerException;
					}				
					
					if((dtInitialConcern.after(dtFamilyPlanEvalCompleted) || dtInitialConcern.equals(dtFamilyPlanEvalCompleted)) && dtFamilyDtCompleted.equals(dtFamilyPlanEvalCompleted))
					{	
						isInitialConcernExist=true;					
					// InitCon CSVC55D
					
					if (!ObjectUtils.isEmpty(familyDto.getItemList())) {
													
							FormDataGroupDto formDataTmplatInitConProg = createFormDataGroup(
									FormGroupsConstants.TMPLAT_INIT_CONCERNS_PLAN_EVAL,
									FormGroupsConstants.TMPLAT_DATE_COMPLETED_PLAN_EVAL);
							List<BookmarkDto> bookmarkInitConList = new ArrayList<BookmarkDto>();
							BookmarkDto bookmarkInitCon = createBookmark(BookmarkConstants.INIT_CONCERN_3,
									formatTextValue(familyPlanItemDto.getTxtInitialConcerns()));
							bookmarkInitConList.add(bookmarkInitCon);
							formDataTmplatInitConProg.setBookmarkDtoList(bookmarkInitConList);
							formDataTmplatInitConList.add(formDataTmplatInitConProg);
							
							
						}
					    formDataTmplatInitDateProg.setBookmarkDtoList(bookmarkInitDateList);
						formDataTmplatInitDateProg.setFormDataGroupList(formDataTmplatInitConList);	
																	
					}					
					}					
					
					if(isInitialConcernExist)
					{
						formDataTmplatInitDateList.add(formDataTmplatInitDateProg);	
					}
					formDataTmplatInitCnV3Prog.setFormDataGroupList(formDataTmplatInitDateList);
					
				}
			}
			formDataTmplatInitCnV3List.add(formDataTmplatInitCnV3Prog);
			formDataGroupList.addAll(formDataTmplatInitCnV3List);
		}

		/**
		 * The prefill data for the Bookmark List
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkTitleCaseNm = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				familyDto.getStageCaseDtlDto().getNmCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNm);

		BookmarkDto bookmarkTitleCaseNbr = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				familyDto.getStageCaseDtlDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNbr);
		if (!ObjectUtils.isEmpty(familyDto.getEmployeePersPhNameDto().getNbrPhone())) {
			String phNumber = FormattingUtils.formatPhone(familyDto.getEmployeePersPhNameDto().getNbrPhone());
		BookmarkDto bookmarkWorkerPhnNbr = createBookmark(BookmarkConstants.WORKER_PHONE,
				phNumber);
		bookmarkNonFrmGrpList.add(bookmarkWorkerPhnNbr);
		}
		
		if (!ObjectUtils.isEmpty(familyDto.getEmployeePersPhNameDto().getNbrPhoneExtension())) {
			String phNumber = FormattingUtils.formatPhone(familyDto.getEmployeePersPhNameDto().getNbrPhoneExtension());
			BookmarkDto bookmarkWorkerPhnExt = createBookmark(BookmarkConstants.WORKER_PHONE_EXT, phNumber);
			bookmarkNonFrmGrpList.add(bookmarkWorkerPhnExt);
		}
		
		if (!ObjectUtils.isEmpty(familyDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
			BookmarkDto bookmarkWorkerSuff = createBookmark(BookmarkConstants.WORKER_NAME_SFX, lookupDao
					.decode(ServiceConstants.CSUFFIX2, familyDto.getEmployeePersPhNameDto().getCdNameSuffix()));
			bookmarkNonFrmGrpList.add(bookmarkWorkerSuff);
		}

		BookmarkDto bookmarkWorkerName1st = createBookmark(BookmarkConstants.WORKER_NAME_1ST,
				familyDto.getEmployeePersPhNameDto().getNmNameFirst());
		bookmarkNonFrmGrpList.add(bookmarkWorkerName1st);

		BookmarkDto bookmarkWorkerSurname = createBookmark(BookmarkConstants.WORKER_SURNAME,
				familyDto.getEmployeePersPhNameDto().getNmNameLast());
		bookmarkNonFrmGrpList.add(bookmarkWorkerSurname);

		BookmarkDto bookmarkWorkerNameMid = createBookmark(BookmarkConstants.WORKER_NAME_MID,
				familyDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameMid);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		preFillData.setFormDataGroupList(formDataGroupList);
		return preFillData;
	}
	
	// Warranty Defect Fix - 12185/12391  - To Replace the Carriage Return /Line Feed with Break Tag
			public String formatTextValue(String txtToFormat) {		
				String[] txtConcurrently = null;
				if (!ObjectUtils.isEmpty(txtToFormat)) {
					txtConcurrently = txtToFormat.split("\r\n");
				}
				StringBuffer txtConcrBuf = new StringBuffer();
				if (!ObjectUtils.isEmpty(txtConcurrently)) {
					for (String txtConcr : txtConcurrently) {
						txtConcrBuf.append(txtConcr);
						txtConcrBuf.append("<br/>");
					}
				}
				return txtConcrBuf.toString();
			}
}