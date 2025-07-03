package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.visitationplan.dto.VisitationPlanDetailDto;
import us.tx.state.dfps.visitationplan.dto.VstPlanPartcpntDto;

@Component
/**
 * FamilyPlanVisitationPlanPrefillData Description:
 * FamilyPlanVisitationPlanPrefillData will implement returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for form Jan 04, 2018 - 04:40:29 PM
 */
public class FamilyPlanVisitationPlanPrefillData extends DocumentServiceUtil {

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
		VisitationPlanDetailDto visitationPlanDetailDto = (VisitationPlanDetailDto) parentDtoobj;

		if (null == visitationPlanDetailDto) {
			visitationPlanDetailDto = (new VisitationPlanDetailDto());
		}
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		// Populate the TITLE_CASE_NAME
		BookmarkDto bookmarkTitleCaseNm = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				visitationPlanDetailDto.getNmCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNm);

		// Populate the TITLE_CASE_NBR
		BookmarkDto bookmarkTitleCaseNum = createBookmark(BookmarkConstants.TITLE_CASE_NBR,
				visitationPlanDetailDto.getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNum);

		// Populate the CAUSE_NUMBER
		if (!ObjectUtils.isEmpty(visitationPlanDetailDto.getTxtCauseNbr())) {
			BookmarkDto bookmarkCauseNumber = createBookmark(BookmarkConstants.CAUSE_NUMBER,
					visitationPlanDetailDto.getTxtCauseNbr());
			bookmarkNonFrmGrpList.add(bookmarkCauseNumber);
		}
		// Populate the VISITATION_LIMIT
		BookmarkDto bookmarkVisitnLimit = createBookmark(BookmarkConstants.VISITATION_LIMIT,
				visitationPlanDetailDto.getTxtLmtatns());
		bookmarkNonFrmGrpList.add(bookmarkVisitnLimit);

		// Populate the VISITATION_LENGTH
		BookmarkDto bookmarkVistatnLength = createBookmark(BookmarkConstants.VISITATION_LENGTH,
				visitationPlanDetailDto.getTxtLngth());
		bookmarkNonFrmGrpList.add(bookmarkVistatnLength);

		// Populate the VISITATION_FREQUENCY
		BookmarkDto bookmarkVistatnFreq = createBookmark(BookmarkConstants.VISITATION_FREQUENCY,
				visitationPlanDetailDto.getTxtFreq());
		bookmarkNonFrmGrpList.add(bookmarkVistatnFreq);

		// Populate the VISITATION_DAYS_AND_TIMES
		BookmarkDto bookmarkVisitatnDaysAndTimes = createBookmark(BookmarkConstants.VISITATION_DAYS_AND_TIMES,
				visitationPlanDetailDto.getTxtDayTime());
		bookmarkNonFrmGrpList.add(bookmarkVisitatnDaysAndTimes);

		// Populate the VISITATION_LOC
		BookmarkDto bookmarkVisitatnLoc = createBookmark(BookmarkConstants.VISITATION_LOC,
				visitationPlanDetailDto.getTxtVisitLoctn());
		bookmarkNonFrmGrpList.add(bookmarkVisitatnLoc);

		// Populate the VISIT_SUPERVISION_CONTACTS
		BookmarkDto bookmarkVisitnSupervsnCntct = createBookmark(BookmarkConstants.VISIT_SUPERVISION_CONTACTS,
				visitationPlanDetailDto.getTxtSprvsn());
		bookmarkNonFrmGrpList.add(bookmarkVisitnSupervsnCntct);

		// Populate the ADDTNL_SUPRTV_ADULTS
		BookmarkDto bookmarkAddtnlSuprtvAdults = createBookmark(BookmarkConstants.ADDTNL_SUPRTV_ADULTS,
				visitationPlanDetailDto.getTxtAdtnlSprtvAdults());
		bookmarkNonFrmGrpList.add(bookmarkAddtnlSuprtvAdults);

		// Populate the VISITS_WITH_NO_SUPERVSN
		BookmarkDto bookmarkNoSupervsn = createBookmark(BookmarkConstants.VISITS_WITH_NO_SUPERVSN,
				visitationPlanDetailDto.getTxtActnsRqrdForNoSprvsn());
		bookmarkNonFrmGrpList.add(bookmarkNoSupervsn);

		// Populate the OTHR_CNTCT
		BookmarkDto bookmarkOthrCntct = createBookmark(BookmarkConstants.OTHR_CNTCT,
				visitationPlanDetailDto.getTxtAprvdCntctMthd());
		bookmarkNonFrmGrpList.add(bookmarkOthrCntct);

		// Populate the VISITS_RULES
		BookmarkDto bookmarkVisitRules = createBookmark(BookmarkConstants.VISITS_RULES,
				visitationPlanDetailDto.getTxtVisitRules());
		bookmarkNonFrmGrpList.add(bookmarkVisitRules);

		BookmarkDto bookmarkVisitSuprtServices = createBookmark(BookmarkConstants.VISIT_SUPRT_SERVICES,
				visitationPlanDetailDto.getTxtSrvcsPrvded());
		bookmarkNonFrmGrpList.add(bookmarkVisitSuprtServices);

		// parent group PARTICIPATION_INFORMATION
		List<FormDataGroupDto> parentFormDataGroupDtoList = new ArrayList<FormDataGroupDto>();
		List<FormDataGroupDto> participationInformationGroupDtoList = new ArrayList<FormDataGroupDto>();

		for (VstPlanPartcpntDto vstPlnPrtcntDto : visitationPlanDetailDto.getVisitPlanPartcpntList()) {

			FormDataGroupDto participationInformationGroupDto = createFormDataGroup(
					FormGroupsConstants.PARTICIPATION_INFORMATION, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkParticipationInfList = new ArrayList<BookmarkDto>();
			// Populate the CHILD_NM
			BookmarkDto childNm = createBookmark(BookmarkConstants.CHILD_NM, vstPlnPrtcntDto.getPartcpntName());
			bookmarkParticipationInfList.add(childNm);

			participationInformationGroupDto.setBookmarkDtoList(bookmarkParticipationInfList);
			participationInformationGroupDtoList.add(participationInformationGroupDto);
		}
		parentFormDataGroupDtoList.addAll(participationInformationGroupDtoList);

		// parent group TMPLAT_VISTATN_SCHDL_PRTCPNTS
		List<FormDataGroupDto> tmpltVistnSchdlList = new ArrayList<FormDataGroupDto>();
		for (VstPlanPartcpntDto vstPlnPrtcntDto : visitationPlanDetailDto.getVisitPlanPartcpntList()) {
			FormDataGroupDto ptmpltVistnSchdl = createFormDataGroup(FormGroupsConstants.TMPLAT_VISTATN_SCHDL_PRTCPNTS,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarktmpltVistnSchdlList = new ArrayList<BookmarkDto>();
			// Populate the PRTCPNT_NM
			BookmarkDto prtcpntNm = createBookmark(BookmarkConstants.PRTCPNT_NM, vstPlnPrtcntDto.getPartcpntName());
			bookmarktmpltVistnSchdlList.add(prtcpntNm);

			// Populate the SUPRVSN_STAGE
			BookmarkDto suprvsnStage = createBookmarkWithCodesTable(BookmarkConstants.SUPRVSN_STAGE,
					vstPlnPrtcntDto.getCdSprvsn(), CodesConstant.CVSTSPRV);
			bookmarktmpltVistnSchdlList.add(suprvsnStage);

			ptmpltVistnSchdl.setBookmarkDtoList(bookmarktmpltVistnSchdlList);
			tmpltVistnSchdlList.add(ptmpltVistnSchdl);
		}
		parentFormDataGroupDtoList.addAll(tmpltVistnSchdlList);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		preFillData.setFormDataGroupList(parentFormDataGroupDtoList);
		return preFillData;
	}

}
