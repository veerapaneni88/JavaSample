package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import us.tx.state.dfps.common.dto.FamilyPlanEvalServiceDto;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.dto.ServPlanEvalRecDto;
import us.tx.state.dfps.common.dto.ServicePlanDto;
import us.tx.state.dfps.common.dto.ServicePlanEvalDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:DisasterPlanFormPrefillData will implemented returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for form cfsd0400. March 9, 2018- 2:04:05 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class FamilyPlanEvalFormPrefillData extends DocumentServiceUtil {

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		FamilyPlanEvalServiceDto familyPlanEvalDto = (FamilyPlanEvalServiceDto) parentDtoobj;

		if (null == familyPlanEvalDto.getGenericCaseInfoDto()) {
			familyPlanEvalDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}

		if (null == familyPlanEvalDto.getServicePlanDto()) {
			familyPlanEvalDto.setServicePlanDto(new ServicePlanDto());
		}
		if (null == familyPlanEvalDto.getServicePlanEvalDto()) {
			familyPlanEvalDto.setServicePlanEvalDto(new ServicePlanEvalDto());
		}
		if (null == familyPlanEvalDto.getServicePlanEvalRecList()) {
			familyPlanEvalDto.setServicePlanEvalRecList(new ArrayList<ServPlanEvalRecDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// Create group cfsd0401 and set the prefill data

		for (ServPlanEvalRecDto servicePlanEvalRecDto : familyPlanEvalDto.getServicePlanEvalRecList()) {
			FormDataGroupDto tempEvalFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EVALUATION,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempEvalBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto cdServPlanGoal = createBookmarkWithCodesTable(BookmarkConstants.GOAL_EVAL_DESC,
					servicePlanEvalRecDto.getCdSvcPlanGoal(), CodesConstant.CSVPNGOL);
			tempEvalBookmarkList.add(cdServPlanGoal);

			BookmarkDto cdServPlanTask = createBookmarkWithCodesTable(BookmarkConstants.TASK_EVAL_DESC,
					servicePlanEvalRecDto.getCdSvcPlanTask(), CodesConstant.CSVPLTSK);
			tempEvalBookmarkList.add(cdServPlanTask);

			BookmarkDto txtServPlanEvalGoal = createBookmark(BookmarkConstants.GOAL_EVAL_COMMENT,
					servicePlanEvalRecDto.getTxtSvcPlanEvalGoal());
			tempEvalBookmarkList.add(txtServPlanEvalGoal);

			BookmarkDto txtServPlanTask = createBookmark(BookmarkConstants.TASK_EVAL_COMMENT,
					servicePlanEvalRecDto.getTxtSvcPlanEvalTask());
			tempEvalBookmarkList.add(txtServPlanTask);
			tempEvalFrmDataGrpDto.setBookmarkDtoList(tempEvalBookmarkList);
			formDataGroupList.add(tempEvalFrmDataGrpDto);

		}

		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		// Populate nmStage value from DAM CSEC02D
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				familyPlanEvalDto.getGenericCaseInfoDto().getNmStage());
		bookmarkNonFrmGrpList.add(bookmarkNmCase);

		// Populate idCase value from DAM CSEC02D
		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				familyPlanEvalDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkIdCase);

		// Populate DtSvcPlanNextRevw value from DAM CSVC04D
		BookmarkDto bookmarkDtSvcPlanNextReview = createBookmark(BookmarkConstants.NEXT_EVAL_DATE,
				TypeConvUtil.formDateFormat(familyPlanEvalDto.getServicePlanDto().getDtSvcPlanNextRevw()));
		bookmarkNonFrmGrpList.add(bookmarkDtSvcPlanNextReview);

		// Populate DtSvcPlanEvalDtlCmpl value from DAM CSVC04D
		BookmarkDto bookmarkDtSvcPlanEvalDtlsCmpl = createBookmark(BookmarkConstants.DATE_COMPLETED,
				TypeConvUtil.formDateFormat(familyPlanEvalDto.getServicePlanEvalDto().getDtSvcPlanEvalDtlCmpl()));
		bookmarkNonFrmGrpList.add(bookmarkDtSvcPlanEvalDtlsCmpl);

		// Populate DtSvcPlanEvalDtlCmpl value from DAM CSVC23D
		BookmarkDto bookmarkDtSvcPlanEvalDtlType = createBookmarkWithCodesTable(BookmarkConstants.TYPE_OF_REVIEW,
				familyPlanEvalDto.getServicePlanEvalDto().getCdSvcPlanEvalDtlType(), CodesConstant.CEVALTYP);
		bookmarkNonFrmGrpList.add(bookmarkDtSvcPlanEvalDtlType);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;

	}

}
