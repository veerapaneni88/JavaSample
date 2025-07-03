package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.icpforms.dto.IcpFormsDto;
import us.tx.state.dfps.service.icpforms.dto.IcpcChildDetailsDto;
import us.tx.state.dfps.service.icpforms.dto.IcpcInfoDto;
import us.tx.state.dfps.service.person.dto.SupervisorDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This class
 * is for prefilling data from ICPformsService to populate form ICP20O00> May 7,
 * 2018- 12:39:02 PM © 2017 Texas Department of Family and Protective Services
 */

@Component
public class ICfinancialPlanPrefillData extends DocumentServiceUtil {

	/**
	 * Method Name: returnPrefillData Method Description: prefill data from
	 * ICPformsServiceImpl for Interstate compact financial and Medical form
	 * 
	 * @param parentDtoobj
	 * @return PreFillDataServiceDto
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		IcpFormsDto icpcFormsDto = (IcpFormsDto) parentDtoobj;

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		// Group 23002053
		//Defect# 12758 - Added a condition to set the placement details only for the care type related to resource name
		if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcNameAndAddr())
				&& (ServiceConstants.CPL_30.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
						|| ServiceConstants.CFACTYP2_50.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
						|| ServiceConstants.CPL_60.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
						|| ServiceConstants.CCH_90.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType()))) {
			FormDataGroupDto plcmtRsrc = createFormDataGroup(FormGroupsConstants.TMPLAT_PLCMT_RSRC,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> plcmtGroupbookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto nmResource = createBookmark(BookmarkConstants.PLCMT_RSRC_NAME,
					icpcFormsDto.getIcpcNameAndAddr().getNmResource());
			plcmtGroupbookmarkList.add(nmResource);
			plcmtRsrc.setBookmarkDtoList(plcmtGroupbookmarkList);
			formDataGroupList.add(plcmtRsrc);
		}

		// Group 23002054
		//Defect# 12758 - Added a condition to set the placement details only for the care type related to person
		if (!ObjectUtils.isEmpty(icpcFormsDto.getPersonDetailByIdReqDtoList())
				&& !(ServiceConstants.CPL_30.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
						|| ServiceConstants.CFACTYP2_50.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
						|| ServiceConstants.CPL_60.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
						|| ServiceConstants.CCH_90.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType()))) {
			for (IcpcChildDetailsDto dto : icpcFormsDto.getPersonDetailByIdReqDtoList()) {
				if (ServiceConstants.CCH_60.equals(dto.getCdPersonType())) {
					FormDataGroupDto personGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PLCMT_PERSON,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> personGroupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto firstName = createBookmark(BookmarkConstants.PLCMT_PERSON_FNAME, dto.getNmNameFirst());
					BookmarkDto lastName = createBookmark(BookmarkConstants.PLCMT_PERSON_LNAME, dto.getNmNameLast());
					personGroupbookmarkList.add(firstName);
					personGroupbookmarkList.add(lastName);
					personGroup.setBookmarkDtoList(personGroupbookmarkList);
					formDataGroupList.add(personGroup);
				}
			}
			if (!ObjectUtils.isEmpty(icpcFormsDto.getSupervisorDto())) {
				BookmarkDto nmSupervisor = createBookmark(BookmarkConstants.NM_CASE_WORKER_SUPERVISOR, icpcFormsDto.getSupervisorDto().getNmPersonFull());
				bookmarkNonFrmGrpList.add(nmSupervisor);
			}
			if (!ObjectUtils.isEmpty(icpcFormsDto.getStagePersonDto())) {
				BookmarkDto nmCaseWorker = createBookmark(BookmarkConstants.NM_CASE_WORKER, icpcFormsDto.getStagePersonDto().getNmPersonFull());
				bookmarkNonFrmGrpList.add(nmCaseWorker);
			}
			if (!ObjectUtils.isEmpty(icpcFormsDto.getStagePersonDto())) {
				BookmarkDto currDate = createBookmark(BookmarkConstants.CURR_DT_1, DateUtils.stringDt(new Date()));
				bookmarkNonFrmGrpList.add(currDate);
			}
		}

		// Populating the non form group data into prefill data. !!bookmarks
		if (!ObjectUtils.isEmpty(icpcFormsDto.getStagePersonDto().getNmStage())) {
			BookmarkDto childStage = createBookmark(BookmarkConstants.CHILD_NM_STAGE,
					icpcFormsDto.getStagePersonDto().getNmStage());
			bookmarkNonFrmGrpList.add(childStage);
		}
		if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcDetailsDto())
				&& !ObjectUtils.isEmpty(icpcFormsDto.getIcpcDetailsDto().getIndTitle())) {
			BookmarkDto indTitle = createBookmark(BookmarkConstants.IND_TITLE_IVE,
					icpcFormsDto.getIcpcDetailsDto().getIndTitle());
			bookmarkNonFrmGrpList.add(indTitle);
		}
		if (!ObjectUtils.isEmpty(icpcFormsDto.getStagePersonLinkCaseDto().getDtPersonBirth())) {
			BookmarkDto childDob = createBookmark(BookmarkConstants.CHILD_DOB,
					DateUtils.stringDt(icpcFormsDto.getStagePersonLinkCaseDto().getDtPersonBirth()));
			bookmarkNonFrmGrpList.add(childDob);
		}

		if (!ObjectUtils.isEmpty(icpcFormsDto.getNmResourceList())) {
			for (IcpcInfoDto dto : icpcFormsDto.getNmResourceList()) {
				BookmarkDto nmResource = createBookmark(BookmarkConstants.NM_PLCMT_RESOURCE, dto.getNmResource());
				bookmarkNonFrmGrpList.add(nmResource);
			}
			if (!ObjectUtils.isEmpty(icpcFormsDto.getSupervisorDto())) {
				BookmarkDto nmSupervisor = createBookmark(BookmarkConstants.NM_CASE_WORKER_SUPERVISOR, icpcFormsDto.getSupervisorDto().getNmPersonFull());
				bookmarkNonFrmGrpList.add(nmSupervisor);
			}
			if (!ObjectUtils.isEmpty(icpcFormsDto.getStagePersonDto())) {
				BookmarkDto nmCaseWorker = createBookmark(BookmarkConstants.NM_CASE_WORKER, icpcFormsDto.getStagePersonDto().getNmPersonFull());
				bookmarkNonFrmGrpList.add(nmCaseWorker);
			}
			if (!ObjectUtils.isEmpty(icpcFormsDto.getStagePersonDto())) {
				BookmarkDto currDate = createBookmark(BookmarkConstants.CURR_DT_1, DateUtils.stringDt(new Date()));
				bookmarkNonFrmGrpList.add(currDate);
			}
		}
		
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}

}
