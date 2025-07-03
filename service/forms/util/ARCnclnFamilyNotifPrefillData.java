package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.arinvconclusion.dto.ARCnclnFamilyDto;
import us.tx.state.dfps.common.domain.ConclusionNotifctnInfo;
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
import us.tx.state.dfps.service.workload.dto.StageProgDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Prefill
 * class for ARCnclnFamilyNotifService> Apr 10, 2018- 9:38:26 PM © 2017 Texas
 * Department of Family and Protective Services
 * ********Change History**********
 * 01/09/2023 thompswa artf238090 : Add ind Involved Parent; ADMIN_CLOSE_RSN; code_type CCLOSAR replaces CCOR.
 * 05/11/2023 chennp artf244449: leaving only Sincerely (removing Thank you)  for all AR Family notification letters
 */

@Component
public class ARCnclnFamilyNotifPrefillData extends DocumentServiceUtil {

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		ARCnclnFamilyDto arDto = (ARCnclnFamilyDto) parentDtoobj;
		// Initializing null DTOs
		if (ObjectUtils.isEmpty(arDto.getConclusionNotifctnInfo())) {
			arDto.setConclusionNotifctnInfo(new ConclusionNotifctnInfo());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		// Director
		BookmarkDto headerTitle = createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_TITLE,
				ServiceConstants.UNIT_REGION_001, CodesConstant.CBRDTTLE);
		BookmarkDto headerName = createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_NAME,
				ServiceConstants.UNIT_REGION_001, CodesConstant.CBRDNAME);
		bookmarkNonFrmGrpList.add(headerTitle);
		bookmarkNonFrmGrpList.add(headerName);

		// Addressee
		BookmarkDto addFirst = createBookmark(BookmarkConstants.ADDRESSEE_NAME_FIRST,
				arDto.getPerson().getNmPersonFirst());
		BookmarkDto addMiddle = createBookmark(BookmarkConstants.ADDRESSEE_NAME_MIDDLE,
				arDto.getPerson().getNmPersonMiddle());
		BookmarkDto addLast = createBookmark(BookmarkConstants.ADDRESSEE_NAME_LAST,
				arDto.getPerson().getNmPersonLast());
		bookmarkNonFrmGrpList.add(addFirst);
		bookmarkNonFrmGrpList.add(addMiddle);
		bookmarkNonFrmGrpList.add(addLast);

		// added TMPLAT_ADDRESSEE_SUFFIX directly to formdatagrouplist without
		// adding bookmart in legacy
		if (!ObjectUtils.isEmpty(arDto.getPerson()) && !ObjectUtils.isEmpty(arDto.getPerson().getCdPersonSuffix())) {
			FormDataGroupDto tmpSuffix = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDRESSEE_SUFFIX,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tmpSuffix);
			BookmarkDto suffix = createBookmarkWithCodesTable(BookmarkConstants.ADDRESSEE_NAME_SUFFIX,
					arDto.getPerson().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
			bookmarkNonFrmGrpList.add(suffix);
		}

		// Addressee Address
		if (!ObjectUtils.isEmpty(arDto.getAddressValueDto())) {
			FormDataGroupDto addressee = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDRESSEE_ADDRESS,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> groupDtoList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto line1 = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_LINE_1,
					arDto.getAddressValueDto().getStreetLn1());
			bookmarkList.add(line1);

			if (!ObjectUtils.isEmpty(arDto.getAddressValueDto())
					&& !ObjectUtils.isEmpty(arDto.getAddressValueDto().getStreetLn2())) {
				FormDataGroupDto addressee2 = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDRESSEE_LINE_2,
						addressee.getFormDataGroupBookmark());
				List<BookmarkDto> bookmarkList2 = new ArrayList<BookmarkDto>();
				BookmarkDto line2 = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_LINE_2,
						arDto.getAddressValueDto().getStreetLn2());
				bookmarkList2.add(line2);
				addressee2.setBookmarkDtoList(bookmarkList2);
				groupDtoList.add(addressee2);
				addressee.setFormDataGroupList(groupDtoList);
			}
			BookmarkDto city = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_CITY,
					arDto.getAddressValueDto().getCity());
			BookmarkDto state = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_STATE,
					arDto.getAddressValueDto().getState());
			BookmarkDto zip = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_ZIP,
					arDto.getAddressValueDto().getZip());
			bookmarkList.add(city);
			bookmarkList.add(state);
			bookmarkList.add(zip);
			addressee.setBookmarkDtoList(bookmarkList);
			formDataGroupList.add(addressee);
		}

		// Date
		BookmarkDto date = createBookmark(BookmarkConstants.NOTIFICATION_DATE,
				DateUtils.stringDt(new java.util.Date()));
		bookmarkNonFrmGrpList.add(date);

		// Case Number
		BookmarkDto careid = createBookmark(BookmarkConstants.CASE_NUMBER, arDto.getIdCase());
		bookmarkNonFrmGrpList.add(careid);

		// Family Member
		BookmarkDto fname = createBookmark(BookmarkConstants.FAMILY_MEMBER_FNAME, arDto.getPerson().getNmPersonFirst());
		BookmarkDto mname = createBookmark(BookmarkConstants.FAMILY_MEMBER_MNAME,
				arDto.getPerson().getNmPersonMiddle());
		BookmarkDto lname = createBookmark(BookmarkConstants.FAMILY_MEMBER_LNAME, arDto.getPerson().getNmPersonLast());
		bookmarkNonFrmGrpList.add(fname);
		bookmarkNonFrmGrpList.add(mname);
		bookmarkNonFrmGrpList.add(lname);

		if (!ObjectUtils.isEmpty(arDto.getPerson()) && !ObjectUtils.isEmpty(arDto.getPerson().getCdPersonSuffix())) {
			FormDataGroupDto tmpSuffix = createFormDataGroup(FormGroupsConstants.TMPLAT_FAMILY_MEMBER_SUFFIX,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkList2 = new ArrayList<BookmarkDto>();
			BookmarkDto suffix = createBookmarkWithCodesTable(BookmarkConstants.FAMILY_MEMBER_SUFFIX,
					arDto.getPerson().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
			bookmarkList2.add(suffix);
			tmpSuffix.setBookmarkDtoList(bookmarkList2);
			formDataGroupList.add(tmpSuffix);
		}

		// Involved parent artf238090
		if(ServiceConstants.Y.equals(arDto.getConclusionNotifctnInfo().getIndInvolveddParent())) {
			FormDataGroupDto involvedParentGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_INVOLVEDPARENT,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(involvedParentGroup);
			arDto.setClosureReason(ServiceConstants.EMPTY_STRING); // Closure reason not used for Involved Parent
		}

		// Stage start date
		if (CodesConstant.CCLOSAR_010.equals(arDto.getClosureReason())) {
			FormDataGroupDto servicesCompletedGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_F1,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkList2 = new ArrayList<BookmarkDto>();
			if (!ObjectUtils.isEmpty(arDto.getStageDto())
					&& !ObjectUtils.isEmpty(arDto.getStageDto().getDtStageStart())) {
				BookmarkDto suffix = createBookmark(BookmarkConstants.STAGE_START_DATE,
						DateUtils.stringDt(arDto.getStageDto().getDtStageStart()));
				bookmarkList2.add(suffix);
				servicesCompletedGroup.setBookmarkDtoList(bookmarkList2);
			}
			formDataGroupList.add(servicesCompletedGroup);
		}
		if (CodesConstant.CCLOSAR_020.equals(arDto.getClosureReason())
				|| CodesConstant.CCLOSAR_030.equals(arDto.getClosureReason())
				|| CodesConstant.CCLOSAR_040.equals(arDto.getClosureReason())
				|| ServiceConstants.ADMIN_CLOSE_RSN.contains(arDto.getClosureReason())) {
			FormDataGroupDto f2group = createFormDataGroup(FormGroupsConstants.TMPLAT_F2, FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> groupDtoList = new ArrayList<FormDataGroupDto>();
			// created new group without adding new group into f2group,
			// following legacy format
			if (CodesConstant.CCLOSAR_020.equals(arDto.getClosureReason())) {
				FormDataGroupDto g2group = createFormDataGroup(FormGroupsConstants.TMPLAT_NO_SAFETY_FACTORS,
						FormGroupsConstants.TMPLAT_F2);
				groupDtoList.add(g2group);
				f2group.setFormDataGroupList(groupDtoList);
			}
			if (CodesConstant.CCLOSAR_030.equals(arDto.getClosureReason())) {
				FormDataGroupDto g2group = createFormDataGroup(FormGroupsConstants.TMPLAT_FAMILY_DECLINED_SERVICES,
						FormGroupsConstants.TMPLAT_F2);
				groupDtoList.add(g2group);
				f2group.setFormDataGroupList(groupDtoList);
			}
			if (CodesConstant.CCLOSAR_040.equals(arDto.getClosureReason())) {
				FormDataGroupDto g2group = createFormDataGroup(FormGroupsConstants.TMPLAT_UNABLE_TO_LOCATE,
						FormGroupsConstants.TMPLAT_F2);
				groupDtoList.add(g2group);
				f2group.setFormDataGroupList(groupDtoList);
			}
			if (ServiceConstants.ADMIN_CLOSE_RSN.contains(arDto.getClosureReason())) {
				FormDataGroupDto g2group = createFormDataGroup(FormGroupsConstants.TMPLAT_ADMIN_CLOSURE,
						FormGroupsConstants.TMPLAT_F2);
				groupDtoList.add(g2group);
				f2group.setFormDataGroupList(groupDtoList);
			}

			// The additional questions comment is on all but the UTL
			if ( ServiceConstants.ADMIN_CLOSE_RSN.contains(arDto.getClosureReason())) {
				FormDataGroupDto g2group = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDITIONAL_QUESTIONS,
						FormGroupsConstants.TMPLAT_F2);
				groupDtoList.add(g2group);
				f2group.setFormDataGroupList(groupDtoList);
			}
			formDataGroupList.add(f2group);
		}

		if (CodesConstant.CCLOSAR_060.equals(arDto.getClosureReason())) {
			FormDataGroupDto fprFbssGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_F3,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkList2 = new ArrayList<BookmarkDto>();
			if (!ObjectUtils.isEmpty(arDto.getStageDto())
					&& !ObjectUtils.isEmpty(arDto.getStageDto().getDtStageStart())) {
				BookmarkDto suffix = createBookmark(BookmarkConstants.STAGE_START_DATE,
						DateUtils.stringDt(arDto.getStageDto().getDtStageStart()));
				bookmarkList2.add(suffix);
				fprFbssGroup.setBookmarkDtoList(bookmarkList2);
			}
			formDataGroupList.add(fprFbssGroup);
		}
		if (CodesConstant.CCLOSAR_090.equals(arDto.getClosureReason())) {
			FormDataGroupDto childFatalityGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_F4,
					FormConstants.EMPTY_STRING);
			if (!ObjectUtils.isEmpty(arDto.getStageDto())
					&& !ObjectUtils.isEmpty(arDto.getStageDto().getDtStageStart())) {
				List<BookmarkDto> bookmarkList2 = new ArrayList<BookmarkDto>();
				BookmarkDto suffix = createBookmark(BookmarkConstants.STAGE_START_DATE,
						DateUtils.stringDt(arDto.getStageDto().getDtStageStart()));
				bookmarkList2.add(suffix);
				childFatalityGroup.setBookmarkDtoList(bookmarkList2);
			}
			formDataGroupList.add(childFatalityGroup);
		}
		// artf238090 text for right to records
		if (Arrays.asList(CodesConstant.CCLOSAR_010, CodesConstant.CCLOSAR_020
				, CodesConstant.CCLOSAR_030).contains(arDto.getClosureReason()))
			formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_RIGHT, FormConstants.EMPTY_STRING));

		// artf244449- PPM 73576 - Adding Sincerely for all AR Family letters
		formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_SINCERELY, FormConstants.EMPTY_STRING));
		// Worker Information
		if(!ObjectUtils.isEmpty(arDto.getEmployeePersPhNameDto()))
		{
		if(!ObjectUtils.isEmpty(arDto.getEmployeePersPhNameDto().getNmNameFirst()))
		{
		BookmarkDto wfname = createBookmark(BookmarkConstants.WORKER_NAME_FIRST, arDto.getEmployeePersPhNameDto().getNmNameFirst());
		bookmarkNonFrmGrpList.add(wfname);
		}
		if(!ObjectUtils.isEmpty(arDto.getEmployeePersPhNameDto().getNmNameMiddle()))
		{
		BookmarkDto wmname = createBookmark(BookmarkConstants.WORKER_NAME_MIDDLE,
				arDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(wmname);
		}
		if(!ObjectUtils.isEmpty(arDto.getEmployeePersPhNameDto().getNmNameLast()))
		{
		BookmarkDto wlname = createBookmark(BookmarkConstants.WORKER_NAME_LAST, arDto.getEmployeePersPhNameDto().getNmNameLast());
		bookmarkNonFrmGrpList.add(wlname);
		}
		
		// phone
		if (!ObjectUtils.isEmpty(arDto.getEmployeePersPhNameDto())
			&& !ObjectUtils.isEmpty(arDto.getEmployeePersPhNameDto().getNbrPhone())) {
		BookmarkDto phone = createBookmark(BookmarkConstants.WORKER_PHONE,
							TypeConvUtil.formatPhone(arDto.getEmployeePersPhNameDto().getNbrPhone()));
		bookmarkNonFrmGrpList.add(phone);
		}		
		
		}

		if (!ObjectUtils.isEmpty(arDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
			FormDataGroupDto tmpSuffix = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_WORKER,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tmpSuffix);
			BookmarkDto suffix = createBookmarkWithCodesTable(BookmarkConstants.WORKER_NAME_SUFFIX,
					arDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
			bookmarkNonFrmGrpList.add(suffix);
		}

		// Worker Title
		if (!ObjectUtils.isEmpty(arDto.getEmployeePersPhNameDto())
				&& !ObjectUtils.isEmpty(arDto.getEmployeePersPhNameDto().getTxtEmployeeClass())) {
			FormDataGroupDto workerTitleGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_TITLE,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkList2 = new ArrayList<BookmarkDto>();
			BookmarkDto title = createBookmark(BookmarkConstants.WORKER_TITLE,
					arDto.getEmployeePersPhNameDto().getTxtEmployeeClass());
			bookmarkList2.add(title);
			workerTitleGroup.setBookmarkDtoList(bookmarkList2);
			formDataGroupList.add(workerTitleGroup);
		}

		// Address
		if (!ObjectUtils.isEmpty(arDto.getEmployeePersPhNameDto())) {
			FormDataGroupDto workaddrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKADDR,
					FormConstants.EMPTY_STRING);
			
			List<FormDataGroupDto> workaddrLine2GroupList=new ArrayList<FormDataGroupDto>();
			if (!ObjectUtils.isEmpty(arDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2())) {
				FormDataGroupDto workaddrLine2Group = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKADDR_LINE2,
						FormGroupsConstants.TMPLAT_WORKADDR);
				List<BookmarkDto> bookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto ln2 = createBookmark(BookmarkConstants.WORKER_ADDRESS_LINE2,
						arDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
				bookmarkList.add(ln2);
				workaddrLine2Group.setBookmarkDtoList(bookmarkList);
				workaddrLine2GroupList.add(workaddrLine2Group);
			}
			workaddrGroup.setFormDataGroupList(workaddrLine2GroupList);
			List<BookmarkDto> bookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto workaddrLine1 = createBookmark(BookmarkConstants.WORKER_ADDRESS_LINE1,
					arDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
			BookmarkDto city2 = createBookmark(BookmarkConstants.WORKER_ADDRESS_CITY,
					arDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
			BookmarkDto zip2 = createBookmark(BookmarkConstants.WORKER_ADDRESS_ZIP,
					arDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
			bookmarkList.add(workaddrLine1);
			bookmarkList.add(city2);
			bookmarkList.add(zip2);
			workaddrGroup.setBookmarkDtoList(bookmarkList);
			formDataGroupList.add(workaddrGroup);
		}

		// Email
		if (!ObjectUtils.isEmpty(arDto.getEmployeePersonDto())
				&& !ObjectUtils.isEmpty(arDto.getEmployeePersonDto().getTxtEmployeeEmailAddress())) {
			FormDataGroupDto emailGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKER_EMAIL,
					FormGroupsConstants.TMPLAT_WORKADDR);
			List<BookmarkDto> bookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto email = createBookmark(BookmarkConstants.WORKER_EMAIL,
					arDto.getEmployeePersonDto().getTxtEmployeeEmailAddress());	

			bookmarkList.add(email);
			emailGroup.setBookmarkDtoList(bookmarkList);
			formDataGroupList.add(emailGroup);
		}

		
		
		
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}
}
