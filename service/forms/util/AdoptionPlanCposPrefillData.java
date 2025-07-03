package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ChildPlanItemDto;
import us.tx.state.dfps.service.admin.dto.CapsPlacemntDto;
import us.tx.state.dfps.service.adoptionplan.dto.AdoptionPlanCposDto;
import us.tx.state.dfps.service.childplan.dto.ChildParticipantRowDODto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Generates
 * prefill string to send to forms Apr 16, 2018- 3:56:57 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Component
public class AdoptionPlanCposPrefillData extends DocumentServiceUtil {

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		AdoptionPlanCposDto prefillDto = (AdoptionPlanCposDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// Initialize null DTOs
		if (ObjectUtils.isEmpty(prefillDto.getCapsPlacementDto())) {
			prefillDto.setCapsPlacementDto(new CapsPlacemntDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getChildParticipantList())) {
			prefillDto.setChildParticipantList(new ArrayList<ChildParticipantRowDODto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getChildPlanItemsList())) {
			prefillDto.setChildPlanItemsList(new ArrayList<ChildPlanItemDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getEventDetailsList())) {
			prefillDto.setEventDetailsList(new ArrayList<EventDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getNameDetailDto())) {
			prefillDto.setNameDetailDto(new NameDetailDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getPersonDto())) {
			prefillDto.setPersonDto(new PersonDto());
		}

		// parent group csc18o12
		for (ChildPlanItemDto itemDto : prefillDto.getChildPlanItemsList()) {
			FormDataGroupDto taskGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NEED_TASK_SVC_RPT_GP,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkTaskList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkTaskNeed = createBookmarkWithCodesTable(BookmarkConstants.NEED_TASK_SVC_NEED,
					itemDto.getCdCspItemNeed(), CodesConstant.CCPNEEDS);
			bookmarkTaskList.add(bookmarkTaskNeed);
			BookmarkDto bookmarkTaskMethodEval = createBookmark(BookmarkConstants.NEED_TASK_SVC_METHOD_EVAL,
					itemDto.getTxtCspItemMethodEval());
			bookmarkTaskList.add(bookmarkTaskMethodEval);
			BookmarkDto bookmarkTaskSvcTimeFrame = createBookmark(BookmarkConstants.NEED_TASK_SVC_SVC_TIME_FRAME,
					itemDto.getTxtCspItemSvcFreq());
			bookmarkTaskList.add(bookmarkTaskSvcTimeFrame);
			BookmarkDto bookmarkTaskTimeFrame = createBookmark(BookmarkConstants.NEED_TASK_SVC_TASK_TIME_FRAME,
					itemDto.getTxtCspItemTaskFreq());
			bookmarkTaskList.add(bookmarkTaskTimeFrame);
			BookmarkDto bookmerkTaskService = createBookmark(BookmarkConstants.NEED_TASK_SVC_SERVICE,
					itemDto.getTxtCspService());
			bookmarkTaskList.add(bookmerkTaskService);
			BookmarkDto bookmarkTaskTask = createBookmark(BookmarkConstants.NEED_TASK_SVC_TASK,
					itemDto.getTxtCspTask());
			bookmarkTaskList.add(bookmarkTaskTask);

			taskGroupDto.setBookmarkDtoList(bookmarkTaskList);
			formDataGroupList.add(taskGroupDto);
		}

		// parent group csc18o14
		for (ChildParticipantRowDODto partDto : prefillDto.getChildParticipantList()) {
			FormDataGroupDto formDataTmpPartInfoRptGp = createFormDataGroup(FormGroupsConstants.TMPLAT_PART_INFO_RPT_GP, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkPartList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkPartDtNotif = createBookmark(BookmarkConstants.PART_INFO_DATE_NOTIFICATION,
					DateUtils.stringDt(partDto.getDtDtCspDateNotified()));
			bookmarkPartList.add(bookmarkPartDtNotif);
			BookmarkDto bookmarkPartDtCopyGiven = createBookmark(BookmarkConstants.PART_INFO_DATE_COPY_GIVEN,
					DateUtils.stringDt(partDto.getDtDtCspPartCopyGiven()));
			bookmarkPartList.add(bookmarkPartDtCopyGiven);
			BookmarkDto bookmarkPartDtPart = createBookmark(BookmarkConstants.PART_INFO_DATE_PARTICIPATION,
					DateUtils.stringDt(partDto.getDtDtCspPartParticipate()));
			bookmarkPartList.add(bookmarkPartDtPart);
			BookmarkDto bookmarkPartTypeNotif = createBookmarkWithCodesTable(
					BookmarkConstants.PART_INFO_TYPE_NOTIFICATION, partDto.getSzCdCspPartNotifType(),
					CodesConstant.CPPTNOPE);
			bookmarkPartList.add(bookmarkPartTypeNotif);
			BookmarkDto bookmarkPartNmFull = createBookmark(BookmarkConstants.PART_INFO_NAME_FULL,
					partDto.getSzNmCspPartFull());
			bookmarkPartList.add(bookmarkPartNmFull);
			BookmarkDto bookmarkPartRel = createBookmark(BookmarkConstants.PART_INFO_RELATIONSHIP,
					partDto.getSzSdsCspPartRelationship());
			bookmarkPartList.add(bookmarkPartRel);
			formDataTmpPartInfoRptGp.setBookmarkDtoList(bookmarkPartList);
			formDataGroupList.add(formDataTmpPartInfoRptGp);
		}

		// non group bookmarks
		List<BookmarkDto> bookmarkNonGroupList = new ArrayList<BookmarkDto>();

		// CSES28D
		BookmarkDto bookmarkDtAdoptPlcmt = createBookmark(BookmarkConstants.AP_DATE_OF_ADOPT_PLCMT,
				DateUtils.stringDt(prefillDto.getCapsPlacementDto().getDtPlcmtStart()));
		bookmarkNonGroupList.add(bookmarkDtAdoptPlcmt);
		BookmarkDto bookmarkNmFamily = createBookmark(BookmarkConstants.AP_FAMILY_NAME,
				prefillDto.getCapsPlacementDto().getNmPlcmtFacil());
		bookmarkNonGroupList.add(bookmarkNmFamily);

		// CSES27D
		BookmarkDto bookmarkDtNextReview = createBookmark(BookmarkConstants.AP_DATE_NEXT_REVIEW,
				DateUtils.stringDt(prefillDto.getPersonDto().getDtCspNextReview()));
		bookmarkNonGroupList.add(bookmarkDtNextReview);
		BookmarkDto bookmarkDtAdoptFinal = createBookmark(BookmarkConstants.AP_PROJ_DT_OF_ADOPT_FINALZTN,
				DateUtils.stringDt(prefillDto.getPersonDto().getDtcspPermGoalTarget()));
		bookmarkNonGroupList.add(bookmarkDtAdoptFinal);
		BookmarkDto bookmarkChildDob = createBookmark(BookmarkConstants.AP_CHILD_DATE_OF_BIRTH,
				DateUtils.stringDt(prefillDto.getPersonDto().getDtPersonBirth()));
		bookmarkNonGroupList.add(bookmarkChildDob);
		BookmarkDto bookmarkDtPlan = createBookmark(BookmarkConstants.AP_DATE_OF_PLAN,
				DateUtils.stringDt(prefillDto.getPersonDto().getDtPersonDeath()));
		bookmarkNonGroupList.add(bookmarkDtPlan);
		BookmarkDto bookmarkComment = createBookmark(BookmarkConstants.AP_TXT_CSP_PARTICIP_COMMENT,
				prefillDto.getPersonDto().getTxtCspParticpatnComment());
		bookmarkNonGroupList.add(bookmarkComment);

		// CSEC35D
		BookmarkDto bookmarkNmChild = createBookmark(BookmarkConstants.AP_NAME_OF_CHILD,
				prefillDto.getNameDetailDto().getNmNameFirst());
		bookmarkNonGroupList.add(bookmarkNmChild);

		// non group blobs
		List<BlobDataDto> blobDataList = new ArrayList<BlobDataDto>();
		BlobDataDto blobCpSsf = createBlobData(BookmarkConstants.AP_NARR_CP_SSF, BookmarkConstants.CP_SSF_NARR,
				prefillDto.getPersonDto().getIdChildPlanEvent().toString());
		blobDataList.add(blobCpSsf);
		BlobDataDto blobCpOop = createBlobData(BookmarkConstants.AP_NARR_CP_OOP, BookmarkConstants.CP_OOP_NARR,
				prefillDto.getPersonDto().getIdChildPlanEvent().toString());
		blobDataList.add(blobCpOop);
		BlobDataDto blobCpMdn = createBlobData(BookmarkConstants.AP_NARR_CP_MDN, BookmarkConstants.CP_MDN_NARR,
				prefillDto.getPersonDto().getIdChildPlanEvent().toString());
		blobDataList.add(blobCpMdn);
		BlobDataDto blobCpDvl = createBlobData(BookmarkConstants.AP_NARR_CP_DVL, BookmarkConstants.CP_DVL_NARR,
				prefillDto.getPersonDto().getIdChildPlanEvent().toString());
		blobDataList.add(blobCpDvl);
		BlobDataDto blobCpSae = createBlobData(BookmarkConstants.AP_NARR_CP_SAE, BookmarkConstants.CP_SAE_NARR,
				prefillDto.getPersonDto().getIdChildPlanEvent().toString());
		blobDataList.add(blobCpSae);
		BlobDataDto blobCpCnp = createBlobData(BookmarkConstants.AP_NARR_CP_CNP, BookmarkConstants.CP_CNP_NARR,
				prefillDto.getPersonDto().getIdChildPlanEvent().toString());
		blobDataList.add(blobCpCnp);
		BlobDataDto blobCpPlp = createBlobData(BookmarkConstants.AP_NARR_CP_PLP, BookmarkConstants.CP_PLP_NARR,
				prefillDto.getPersonDto().getIdChildPlanEvent().toString());
		blobDataList.add(blobCpPlp);
		BlobDataDto blobCpApa = createBlobData(BookmarkConstants.AP_NARR_CP_APA, BookmarkConstants.CP_APA_NARR,
				prefillDto.getPersonDto().getIdChildPlanEvent().toString());
		blobDataList.add(blobCpApa);
		BlobDataDto blobCpPer = createBlobData(BookmarkConstants.AP_NARR_CP_PER, BookmarkConstants.CP_PRA_NARR,
				prefillDto.getPersonDto().getIdChildPlanEvent().toString());
		blobDataList.add(blobCpPer);
		BlobDataDto blobCpPls = createBlobData(BookmarkConstants.AP_NARR_CP_PLS, BookmarkConstants.CP_PLS_NARR,
				prefillDto.getPersonDto().getIdChildPlanEvent().toString());
		blobDataList.add(blobCpPls);
		BlobDataDto blobCpWor = createBlobData(BookmarkConstants.AP_NARR_CP_WOR, BookmarkConstants.CP_WOR_NARR,
				prefillDto.getPersonDto().getIdChildPlanEvent().toString());
		blobDataList.add(blobCpWor);
		BlobDataDto blobCpFan = createBlobData(BookmarkConstants.AP_NARR_CP_FAN, BookmarkConstants.CP_FAN_NARR,
				prefillDto.getPersonDto().getIdChildPlanEvent().toString());
		blobDataList.add(blobCpFan);
		BlobDataDto blobCpApr = createBlobData(BookmarkConstants.AP_NARR_CP_APR, BookmarkConstants.CP_APR_NARR,
				prefillDto.getPersonDto().getIdChildPlanEvent().toString());
		blobDataList.add(blobCpApr);
		BlobDataDto blobCpEoc = createBlobData(BookmarkConstants.AP_NARR_CP_EOC, BookmarkConstants.CP_EOC_NARR,
				prefillDto.getPersonDto().getIdChildPlanEvent().toString());
		blobDataList.add(blobCpEoc);

		PreFillDataServiceDto prefillData = new PreFillDataServiceDto();
		prefillData.setBookmarkDtoList(bookmarkNonGroupList);
		prefillData.setBlobDataDtoList(blobDataList);
		prefillData.setFormDataGroupList(formDataGroupList);

		return prefillData;
	}

}
