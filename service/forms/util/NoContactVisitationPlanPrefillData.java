package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.visitationplan.dto.NoCnctVstPlnDetailDto;
import us.tx.state.dfps.visitationplan.dto.VstPlanPartcpntDto;

@Component
/**
 * Name:NoContactVisitationPlanPrefillData
 * Description:NoContactVisitationPlanPrefillData will implement
 * returnPrefillData operation defined in DocumentServiceUtil Interface to
 * populate the prefill data for the form No Contact Visitation Plan.
 * 
 */
public class NoContactVisitationPlanPrefillData extends DocumentServiceUtil {

	private static final String COURT_ORDERED_STRING = "Court Ordered";

	private static final String DFPS_RECOMMENDED_STRING = "DFPS Recommended";

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
		NoCnctVstPlnDetailDto noCnctVstPlnDetailDto = (NoCnctVstPlnDetailDto) parentDtoobj;

		if (null == noCnctVstPlnDetailDto) {
			noCnctVstPlnDetailDto = (new NoCnctVstPlnDetailDto());
		}
		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		// Populate the TITLE_CASE_NAME
		BookmarkDto bookmarkTitleCaseNm = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				noCnctVstPlnDetailDto.getNmCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNm);

		// Populate the TITLE_CASE_NBR
		BookmarkDto bookmarkTitleCaseNum = createBookmark(BookmarkConstants.TITLE_CASE_NBR,
				noCnctVstPlnDetailDto.getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNum);

		// Populate the CAUSE_NUMBER
		BookmarkDto bookmarkCauseNumber = createBookmark(BookmarkConstants.CAUSE_NUMBER,
				noCnctVstPlnDetailDto.getTxtCauseNbr());
		bookmarkNonFrmGrpList.add(bookmarkCauseNumber);

		// Populate the BST_INTRST_CNTCT_VISITATION
		BookmarkDto bookmarkVisitnCntct = createBookmark(BookmarkConstants.BST_INTRST_CNTCT_VISITATION,
				noCnctVstPlnDetailDto.getTxtRsnNoCntct());
		bookmarkNonFrmGrpList.add(bookmarkVisitnCntct);

		// Populate the CNTCT_VISITATION_NEEDS
		BookmarkDto bookmarkVistatnNeeds = createBookmark(BookmarkConstants.CNTCT_VISITATION_NEEDS,
				noCnctVstPlnDetailDto.getTxtCntctBeginCndtn());
		bookmarkNonFrmGrpList.add(bookmarkVistatnNeeds);

		// Populate the SUPRTV_ADULTS
		BookmarkDto bookmarkSuprtvAdults = createBookmark(BookmarkConstants.SUPRTV_ADULTS,
				noCnctVstPlnDetailDto.getTxtSprtvAdults());
		bookmarkNonFrmGrpList.add(bookmarkSuprtvAdults);

		// Populate the ADDTNL_SUPRTV_ADULTS
		BookmarkDto bookmarkAddtnlAdults = createBookmark(BookmarkConstants.ADDTNL_SUPRTV_ADULTS,
				noCnctVstPlnDetailDto.getTxtAdtnlSprtvAdults());
		bookmarkNonFrmGrpList.add(bookmarkAddtnlAdults);

		// parent group PARTICIPATION_INFORMATION
		List<FormDataGroupDto> participationInformationGroupDtoList = new ArrayList<FormDataGroupDto>();

		for (VstPlanPartcpntDto vstPlnPartcpntDto : noCnctVstPlnDetailDto.getNoCnctVstPlanPartcpntList()) {
			FormDataGroupDto participationInformationGroupDto = createFormDataGroup(
					FormGroupsConstants.PARTICIPATION_INFORMATION, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkParticipationInfList = new ArrayList<BookmarkDto>();
			// Populate the CHILD_NM
			BookmarkDto childNm = createBookmark(BookmarkConstants.CHILD_NM, vstPlnPartcpntDto.getPartcpntName());
			bookmarkParticipationInfList.add(childNm);

			// Populate the VISITATION_RESTRICTION
			String indCourtOrdered = vstPlnPartcpntDto.getIndCourtOrdrd();
			String visitationRestriction = null;
			if (ServiceConstants.Y.equals(indCourtOrdered)) {
				visitationRestriction = COURT_ORDERED_STRING;
			} else if (ObjectUtils.isEmpty(indCourtOrdered)) {
				visitationRestriction = DFPS_RECOMMENDED_STRING;
			}
			BookmarkDto childRestrictn = createBookmark(BookmarkConstants.VISITATION_RESTRICTION,
					visitationRestriction);
			bookmarkParticipationInfList.add(childRestrictn);
			participationInformationGroupDto.setBookmarkDtoList(bookmarkParticipationInfList);

			participationInformationGroupDtoList.add(participationInformationGroupDto);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		preFillData.setFormDataGroupList(participationInformationGroupDtoList);
		return preFillData;
	}
}
