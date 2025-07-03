package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.dto.InvActionReportDto;
import us.tx.state.dfps.riskandsafetyassmt.dto.InvstActionQuestionDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.EventStagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:DPSCrimHistResPrefillData will implement returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for Form cfiv0200 May 3, 2018- 10:53:14 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class InvActionReportFormPrefillData extends DocumentServiceUtil {

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		InvActionReportDto invActionReportDto = (InvActionReportDto) parentDtoobj;
		if (ObjectUtils.isEmpty(invActionReportDto.getEventStagePersonDtoList())) {
			invActionReportDto.setEventStagePersonDtoList(new ArrayList<EventStagePersonDto>());
		}

		if (ObjectUtils.isEmpty(invActionReportDto.getGenericCaseInfoDto())) {
			invActionReportDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}

		if (ObjectUtils.isEmpty(invActionReportDto.getInvsActionQuestionDtoList())) {
			invActionReportDto.setInvsActionQuestionDtoList(new ArrayList<InvstActionQuestionDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// create form group cfiv0201 and set prefill data
		for (InvstActionQuestionDto invstActionQuestionDto : invActionReportDto.getInvsActionQuestionDtoList()) {
			FormDataGroupDto tmpInvstActionsFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_INVST_ACTIONS, FormConstants.EMPTY_STRING);
			List<BookmarkDto> invstActionBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto cdInvstActionAns = createBookmarkWithCodesTable(BookmarkConstants.INVST_ACTIONS_ANSWER,
					invstActionQuestionDto.getCdInvstActionAns(), CodesConstant.CINVACAN);

			BookmarkDto cdInvstActionQuest = createBookmarkWithCodesTable(BookmarkConstants.INVST_ACTIONS_QUESTION,
					invstActionQuestionDto.getCdInvstActionQuest(), CodesConstant.CACTIONS);

			BookmarkDto txtInvstActionCmnts = createBookmark(BookmarkConstants.INVST_ACTIONS_COMMENT,
					invstActionQuestionDto.getInvstActionCmnts());
			invstActionBookmarkList.add(cdInvstActionQuest);
			invstActionBookmarkList.add(txtInvstActionCmnts);
			invstActionBookmarkList.add(cdInvstActionAns);
			tmpInvstActionsFrmDataGrpDto.setBookmarkDtoList(invstActionBookmarkList);
			formDataGroupList.add(tmpInvstActionsFrmDataGrpDto);

		}

		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		// populate nmStage value from DAM CSEC02D
		BookmarkDto nmStageBookmark = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				invActionReportDto.getGenericCaseInfoDto().getNmStage());
		bookmarkNonFrmGrpList.add(nmStageBookmark);

		// populate nmStage value from DAM CSEC02D
		BookmarkDto idCaseBookmark = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				invActionReportDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonFrmGrpList.add(idCaseBookmark);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}

}
