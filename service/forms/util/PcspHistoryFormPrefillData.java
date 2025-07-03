package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.pcsphistoryform.dto.PcspHistoryDto;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description:This class is doing prefill data services for
 * PcspHistoryFormServiceImpl Tuxedo name: PCSPHIST Mar 29, 2018- 3:07:08 PM ©
 * 2017 Texas Department of Family and Protective Services
 */

@Component
public class PcspHistoryFormPrefillData extends DocumentServiceUtil {
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		PcspHistoryDto pcspHist = (PcspHistoryDto) parentDtoobj;
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// populate html title
		BookmarkDto http = createBookmark(BookmarkConstants.HTML_TITLE, ServiceConstants.PCSP_TITLE_Detail_History);
		BookmarkDto title = createBookmark(BookmarkConstants.PRINT_TITLE, ServiceConstants.PCSP_TITLE_Detail_History);
		bookmarkNonFrmGrpList.add(http);
		bookmarkNonFrmGrpList.add(title);

		// get caseID and case name
		if (!ObjectUtils.isEmpty(pcspHist.getCaseSummaryDto())) {
			FormDataGroupDto temCase = createFormDataGroup(FormGroupsConstants.TMPLAT_CASE, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRrlList = new ArrayList<BookmarkDto>();
			BookmarkDto nmCase = createBookmark(BookmarkConstants.CASE_NM, pcspHist.getCaseSummaryDto().getNmCase());
			BookmarkDto idCase = createBookmark(BookmarkConstants.CASE_ID, pcspHist.getIdCase());
			bookmarkRrlList.add(nmCase);
			bookmarkRrlList.add(idCase);
			temCase.setBookmarkDtoList(bookmarkRrlList);
			formDataGroupList.add(temCase);
		}

		// PcspDto
		if (ObjectUtils.isEmpty(pcspHist.getPcspDto())) {
			BookmarkDto bookmarkFormMessage = createBookmark(BookmarkConstants.FORM_MESSAGE,
					"No valid PCSP records added to the case.");
			bookmarkNonFrmGrpList.add(bookmarkFormMessage);
		}
		if (!ObjectUtils.isEmpty(pcspHist.getPcspDto())) {
			for (PcspDto pcspDto : pcspHist.getPcspDto()) {
				FormDataGroupDto temCase = createFormDataGroup(FormGroupsConstants.TMPLAT_DETAIL,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkRrlList = new ArrayList<BookmarkDto>();
				List<FormDataGroupDto> groupDtoList = new ArrayList<FormDataGroupDto>();
				BookmarkDto child = createBookmark(BookmarkConstants.DETAIL_CHILD, pcspDto.getChildName());
				BookmarkDto careGiver = createBookmark(BookmarkConstants.DETAIL_CAREGIVER, pcspDto.getCaregiverName());
				BookmarkDto startDate = createBookmark(BookmarkConstants.DETAIL_STARTDATE,
						DateUtils.stringDt(pcspDto.getDtStart()));
				// Warranty Defect Fix - 11138 To prevent the Default PCSP End Date to be printed from Form
				// in case the PCSP detail is not end-dated
				String placementEndDate=TypeConvUtil.formDateFormat(pcspDto.getDtEnd());
				if(!ServiceConstants.STAGE_OPEN_DT.equals(placementEndDate))
				{
				BookmarkDto endDate = createBookmark(BookmarkConstants.DETAIL_ENDDATE,
						DateUtils.stringDt(pcspDto.getDtEnd()));
				bookmarkRrlList.add(endDate);
				}
				BookmarkDto endre = createBookmarkWithCodesTable(BookmarkConstants.DETAIL_ENDREASON,
						pcspDto.getEndReason(), CodesConstant.PCSPEND);
				BookmarkDto goal = createBookmarkWithCodesTable(BookmarkConstants.DETAIL_GOAL, pcspDto.getPcspGoal(),
						CodesConstant.PCSPGOAL);
				BookmarkDto stage = createBookmarkWithCodesTable(BookmarkConstants.DETAIL_CREATEDSTAGE,
						pcspDto.getStageCreated(), CodesConstant.CSTAGES);
				BookmarkDto endestage = createBookmarkWithCodesTable(BookmarkConstants.DETAIL_ENDEDSTAGE,
						pcspDto.getStageEnded(), CodesConstant.CSTAGES);
				bookmarkRrlList.add(child);
				bookmarkRrlList.add(careGiver);
				bookmarkRrlList.add(startDate);				
				bookmarkRrlList.add(endre);
				bookmarkRrlList.add(goal);
				bookmarkRrlList.add(stage);
				bookmarkRrlList.add(endestage);
				int pcspExtnSize = pcspDto.getPcspExtnDtlDtoList().size();
				for (int i = pcspExtnSize - 1; i >= 0; i--) {
					FormDataGroupDto pcspExtnGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PCSP_PLCMT_EXTNS,
							FormGroupsConstants.TMPLAT_DETAIL);
					List<BookmarkDto> bookmarkPcspExtnList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkRenewalDt = createBookmark(BookmarkConstants.PCSP_EXTN_RENEWAL_DT,
							DateUtils.stringDt(pcspDto.getPcspExtnDtlDtoList().get(i).getRenewalDate()));
					bookmarkPcspExtnList.add(bookmarkRenewalDt);
					BookmarkDto bookmarkExtnExpryDt = createBookmark(BookmarkConstants.PCSP_EXTN_EXPRY_DT,
							DateUtils.stringDt(pcspDto.getPcspExtnDtlDtoList().get(i).getExtensionExpiryDate()));
					bookmarkPcspExtnList.add(bookmarkExtnExpryDt);
					BookmarkDto bookmarkCourtOrderToCont = createBookmark(BookmarkConstants.PCSP_EXTN_COURT_ORDER_TO_CONT,
							Optional.ofNullable(pcspDto.getPcspExtnDtlDtoList().get(i).getIndToContPcsp())
									.map(c -> c.equalsIgnoreCase("Y") ? "Yes" : c.equalsIgnoreCase("N") ? "No" : c)
									.orElse(""));
					bookmarkPcspExtnList.add(bookmarkCourtOrderToCont);
					BookmarkDto bookmarkParentAtnyAgreement = createBookmark(BookmarkConstants.PCSP_PARENT_ATTRNY_AGRMNT,
							Optional.ofNullable(pcspDto.getPcspExtnDtlDtoList().get(i).getIndAtrnyParentAgrmnt())
									.map(c -> c.equalsIgnoreCase("Y") ? "Yes" : c.equalsIgnoreCase("N") ? "No" : c)
									.orElse(""));
					bookmarkPcspExtnList.add(bookmarkParentAtnyAgreement);
					BookmarkDto bookmarkExtGoal = createBookmarkWithCodesTable(BookmarkConstants.PCSP_EXTN_CD_GOAL,
							pcspDto.getPcspExtnDtlDtoList().get(i).getCdGoal(), CodesConstant.PCSPGOAL);
					bookmarkPcspExtnList.add(bookmarkExtGoal);
					pcspExtnGroupDto.setBookmarkDtoList(bookmarkPcspExtnList);
					groupDtoList.add(pcspExtnGroupDto);
				}
				if (ServiceConstants.END_RSN_OTHER.equals(pcspDto.getEndReason())) {
					FormDataGroupDto temCase1 = createFormDataGroup(FormGroupsConstants.TMPLAT_DETAIL_OTHER,
							FormGroupsConstants.TMPLAT_DETAIL);
					List<BookmarkDto> bookmarkRrlList1 = new ArrayList<BookmarkDto>();
					BookmarkDto child1 = createBookmark(BookmarkConstants.DETAIL_OTHER_ENDREASON,
							pcspDto.getEndRsnOther());
					bookmarkRrlList1.add(child1);
					temCase1.setBookmarkDtoList(bookmarkRrlList1);
					groupDtoList.add(temCase1);
				}
				temCase.setFormDataGroupList(groupDtoList);
				temCase.setBookmarkDtoList(bookmarkRrlList);
				formDataGroupList.add(temCase);
			}
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}
}