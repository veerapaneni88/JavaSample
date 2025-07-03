package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.notiftolawenforcement.dto.NotifToLawEnforceDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CINV80S
 * Class Description: This class is doing prefill data services for service
 * NotifToLawEnforceService/form name civ38o00. Mar 15, 2018 - 1:32:34 PM © 2017
 * © 2017 Texas Department of Family and Protective Services
 */
@Component
public class NotifToLawEnforcePrefillData extends DocumentServiceUtil {
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		NotifToLawEnforceDto notifToLawEnforceDto = (NotifToLawEnforceDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// civ3801
		if (!ObjectUtils.isEmpty(notifToLawEnforceDto.getEmployeePersPhNameDto())
				&& StringUtils.isNotBlank(notifToLawEnforceDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2())) {
			FormDataGroupDto rrlGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_LN2,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRrlList = new ArrayList<BookmarkDto>();
			BookmarkDto addrStr2 = createBookmark(BookmarkConstants.NOTIFY_ADDR_STREET2,
					notifToLawEnforceDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
			bookmarkRrlList.add(addrStr2);
			rrlGroupDto.setBookmarkDtoList(bookmarkRrlList);
			formDataGroupList.add(rrlGroupDto);
		}

		// parent civ3802
		if (!ObjectUtils.isEmpty(notifToLawEnforceDto.getGenericCaseInfoDto())) {
			if (!ObjectUtils.isEmpty(notifToLawEnforceDto.getGenericCaseInfoDto().getDtStageStart())
					&& notifToLawEnforceDto.getGenericCaseInfoDto().getDtStageStart()
							.before(ServiceConstants.JUNE_7_IMPACT_DATE)) {
				FormDataGroupDto parentGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_PRIOR_JUNE,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> groupDtoList = new ArrayList<FormDataGroupDto>();
				// sub group civ3803
				if (ObjectUtils.isEmpty(notifToLawEnforceDto.getFacilInvstInfoDto())) {
					FormDataGroupDto subGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_JUNE,
							FormGroupsConstants.TMPLAT_DISP_PRIOR_JUNE);

					List<BookmarkDto> bookmarkRrlList = new ArrayList<BookmarkDto>();
					BookmarkDto addrZip = createBookmark(BookmarkConstants.CONF_FACIL_ADDR_ZIP,
							notifToLawEnforceDto.getFacilInvstInfoDto().getSzAddrFacilInvstZip());
					BookmarkDto addrCity = createBookmark(BookmarkConstants.CONF_FACIL_ADDR_CITY,
							notifToLawEnforceDto.getFacilInvstInfoDto().getSzAddrFacilInvstCity());
					BookmarkDto addrState = createBookmarkWithCodesTable(BookmarkConstants.CONF_FACIL_ADDR_STATE,
							notifToLawEnforceDto.getFacilInvstInfoDto().getSzAddrFacilInvstState(),
							CodesConstant.CSTATE);
					BookmarkDto addrStr1 = createBookmark(BookmarkConstants.CONF_FACIL_ADDR_LN_1,
							notifToLawEnforceDto.getFacilInvstInfoDto().getSzAddrFacilInvstStr1());
					BookmarkDto addrStr2 = createBookmark(BookmarkConstants.CONF_FACIL_ADDR_LN_2,
							notifToLawEnforceDto.getFacilInvstInfoDto().getSzAddrFacilInvstStr2());
					bookmarkRrlList.add(addrZip);
					bookmarkRrlList.add(addrCity);
					bookmarkRrlList.add(addrState);
					bookmarkRrlList.add(addrStr1);
					bookmarkRrlList.add(addrStr2);
					subGroupDto.setBookmarkDtoList(bookmarkRrlList);
					groupDtoList.add(subGroupDto);
				}
				parentGroupDto.setFormDataGroupList(groupDtoList);
				formDataGroupList.add(parentGroupDto);
			}
		}
		// civ3804
		if (!ObjectUtils.isEmpty(notifToLawEnforceDto.getGenericCaseInfoDto())) {
			if (!ObjectUtils.isEmpty(notifToLawEnforceDto.getGenericCaseInfoDto().getDtStageStart())
					&& notifToLawEnforceDto.getGenericCaseInfoDto().getDtStageStart()
							.after(ServiceConstants.JUNE_7_IMPACT_DATE)) {
				FormDataGroupDto parentGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_POST_JUNE,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkRrlList = new ArrayList<BookmarkDto>();
				List<FormDataGroupDto> groupDtoList = new ArrayList<FormDataGroupDto>();
				BookmarkDto addrStr2 = createBookmark(BookmarkConstants.UE_GROUPID,
						notifToLawEnforceDto.getGenericCaseInfoDto().getIdStage());
				bookmarkRrlList.add(addrStr2);
				parentGroupDto.setBookmarkDtoList(bookmarkRrlList);
				// sub group civ3805
				if (!ObjectUtils.isEmpty(notifToLawEnforceDto.getMultiAddressDtos())) {
					FormDataGroupDto subGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FAC_NAME,
							FormGroupsConstants.TMPLAT_DISP_POST_JUNE);
					List<BookmarkDto> bookmarkRrlList1 = new ArrayList<BookmarkDto>();
					BookmarkDto groupId = createBookmark(BookmarkConstants.UE_GROUPID,
							notifToLawEnforceDto.getGenericCaseInfoDto().getIdStage());
					BookmarkDto facilName = createBookmark(BookmarkConstants.FACILITY_NAME,
							notifToLawEnforceDto.getMultiAddressDtos().get(0).getaCpNmResource());
					bookmarkRrlList1.add(groupId);
					bookmarkRrlList1.add(facilName);
					subGroupDto.setBookmarkDtoList(bookmarkRrlList1);
					groupDtoList.add(subGroupDto);
				}
				// sub group civ3806
				if (!ObjectUtils.isEmpty(notifToLawEnforceDto.getMultiAddressDtos())) {
					FormDataGroupDto subGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FAC_ADDR1,
							FormGroupsConstants.TMPLAT_DISP_POST_JUNE);
					List<BookmarkDto> bookmarkRrlList1 = new ArrayList<BookmarkDto>();
					BookmarkDto groupId = createBookmark(BookmarkConstants.UE_GROUPID,
							notifToLawEnforceDto.getGenericCaseInfoDto().getIdStage());
					BookmarkDto facilName = createBookmark(BookmarkConstants.FAC_ADDR1,
							notifToLawEnforceDto.getMultiAddressDtos().get(0).getaAddrRsrcAddrStLn1());
					bookmarkRrlList1.add(groupId);
					bookmarkRrlList1.add(facilName);
					subGroupDto.setBookmarkDtoList(bookmarkRrlList1);
					groupDtoList.add(subGroupDto);
				}
				// sub group civ3807
				if (!ObjectUtils.isEmpty(notifToLawEnforceDto.getMultiAddressDtos())) {
					FormDataGroupDto subGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FAC_ADDR2,
							FormGroupsConstants.TMPLAT_DISP_POST_JUNE);
					List<BookmarkDto> bookmarkRrlList1 = new ArrayList<BookmarkDto>();
					BookmarkDto groupId = createBookmark(BookmarkConstants.UE_GROUPID,
							notifToLawEnforceDto.getGenericCaseInfoDto().getIdStage());
					BookmarkDto facilName = createBookmark(BookmarkConstants.FAC_ADDR2,
							notifToLawEnforceDto.getMultiAddressDtos().get(0).getaAddrRsrcAddrStLn2());
					bookmarkRrlList1.add(groupId);
					bookmarkRrlList1.add(facilName);
					subGroupDto.setBookmarkDtoList(bookmarkRrlList1);
					groupDtoList.add(subGroupDto);
				}
				// sub group civ3808
				if (!ObjectUtils.isEmpty(notifToLawEnforceDto.getMultiAddressDtos())) {
					FormDataGroupDto subGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FAC_CSZ,
							FormGroupsConstants.TMPLAT_DISP_POST_JUNE);
					List<BookmarkDto> bookmarkRrlList1 = new ArrayList<BookmarkDto>();
					BookmarkDto groupId = createBookmark(BookmarkConstants.FAC_CITY,
							notifToLawEnforceDto.getMultiAddressDtos().get(0).getaAddrRsrcAddrCity());
					BookmarkDto facilName = createBookmark(BookmarkConstants.FAC_ST,
							notifToLawEnforceDto.getMultiAddressDtos().get(0).getaCdRsrcAddrState());
					BookmarkDto facilZip = createBookmark(BookmarkConstants.FAC_ZIP,
							notifToLawEnforceDto.getMultiAddressDtos().get(0).getaAddrRsrcAddrZip());
					BookmarkDto facilGroupId = createBookmark(BookmarkConstants.UE_GROUPID,
							notifToLawEnforceDto.getGenericCaseInfoDto().getIdStage());
					bookmarkRrlList1.add(groupId);
					bookmarkRrlList1.add(facilName);
					bookmarkRrlList1.add(facilZip);
					bookmarkRrlList1.add(facilGroupId);
					subGroupDto.setBookmarkDtoList(bookmarkRrlList1);
					groupDtoList.add(subGroupDto);
				}

				parentGroupDto.setFormDataGroupList(groupDtoList);
				formDataGroupList.add(parentGroupDto);
			}
		}

		/*
		 * Populating the non form group data into prefill data. !!bookmarks
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		// CSEC01D
		if (!ObjectUtils.isEmpty(notifToLawEnforceDto.getEmployeePersPhNameDto())) {
			BookmarkDto lNbrPhone = createBookmark(BookmarkConstants.NOTIFY_PHONE,
					TypeConvUtil.formatPhone(notifToLawEnforceDto.getEmployeePersPhNameDto().getNbrPhone()));
			BookmarkDto lNbrPhoneExtension = createBookmark(BookmarkConstants.NOTIFY_EXTENSION,
					notifToLawEnforceDto.getEmployeePersPhNameDto().getNbrPhoneExtension());
			BookmarkDto addrMailCodeCity = createBookmark(BookmarkConstants.NOTIFY_ADDR_CITY,
					notifToLawEnforceDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
			BookmarkDto addrMailCodeStLn1 = createBookmark(BookmarkConstants.NOTIFY_ADDR_STREET1,
					notifToLawEnforceDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
			BookmarkDto addrMailCodeStLn2 = createBookmark(BookmarkConstants.NOTIFY_ADDR_STREET2,
					notifToLawEnforceDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
			BookmarkDto addrMailCodeZip = createBookmark(BookmarkConstants.NOTIFY_ADDR_ZIP,
					notifToLawEnforceDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
			BookmarkDto nmNameFirst = createBookmark(BookmarkConstants.NOTIFY_FROM_NM_FIRST,
					notifToLawEnforceDto.getEmployeePersPhNameDto().getNmNameFirst());
			BookmarkDto nmNameLast = createBookmark(BookmarkConstants.NOTIFY_FROM_NM_LAST,
					notifToLawEnforceDto.getEmployeePersPhNameDto().getNmNameLast());
			BookmarkDto nmNameMiddle = createBookmark(BookmarkConstants.NOTIFY_FROM_NM_MIDDLE,
					notifToLawEnforceDto.getEmployeePersPhNameDto().getNmNameMiddle());

			bookmarkNonFrmGrpList.add(lNbrPhone);
			bookmarkNonFrmGrpList.add(lNbrPhoneExtension);
			bookmarkNonFrmGrpList.add(addrMailCodeCity);
			bookmarkNonFrmGrpList.add(addrMailCodeStLn1);
			bookmarkNonFrmGrpList.add(addrMailCodeStLn2);
			bookmarkNonFrmGrpList.add(addrMailCodeZip);
			bookmarkNonFrmGrpList.add(nmNameFirst);
			bookmarkNonFrmGrpList.add(nmNameLast);
			bookmarkNonFrmGrpList.add(nmNameMiddle);
		}
		// CSEC02D
		if (!ObjectUtils.isEmpty(notifToLawEnforceDto.getGenericCaseInfoDto())) {
			BookmarkDto dtSysDtGenericSysdate = createBookmark(BookmarkConstants.NOTIFY_DATE,
					DateUtils.stringDt(notifToLawEnforceDto.getGenericCaseInfoDto().getDtSysDtGenericSysdate()));
			BookmarkDto szNmCase = createBookmark(BookmarkConstants.CONF_CASE_NAME,
					notifToLawEnforceDto.getGenericCaseInfoDto().getNmCase());
			BookmarkDto ulIdCase = createBookmark(BookmarkConstants.CONF_CASE_NUMBER,
					notifToLawEnforceDto.getGenericCaseInfoDto().getIdCase());
			bookmarkNonFrmGrpList.add(dtSysDtGenericSysdate);
			bookmarkNonFrmGrpList.add(szNmCase);
			bookmarkNonFrmGrpList.add(ulIdCase);
		}
		// CINV17D
		if (!ObjectUtils.isEmpty(notifToLawEnforceDto.getFacilInvstInfoDto())) {
			BookmarkDto addrFacilZip = createBookmark(BookmarkConstants.CONF_FACIL_ADDR_ZIP,
					notifToLawEnforceDto.getFacilInvstInfoDto().getSzAddrFacilInvstZip());
			BookmarkDto addrFacilCity = createBookmark(BookmarkConstants.CONF_FACIL_ADDR_CITY,
					notifToLawEnforceDto.getFacilInvstInfoDto().getSzAddrFacilInvstCity());
			BookmarkDto addrFacilState = createBookmark(BookmarkConstants.CONF_FACIL_ADDR_STATE,
					notifToLawEnforceDto.getFacilInvstInfoDto().getSzAddrFacilInvstState());
			BookmarkDto addrFacilStr1 = createBookmark(BookmarkConstants.CONF_FACIL_ADDR_LN_1,
					notifToLawEnforceDto.getFacilInvstInfoDto().getSzAddrFacilInvstStr1());
			BookmarkDto addrFacilStr2 = createBookmark(BookmarkConstants.CONF_FACIL_ADDR_LN_2,
					notifToLawEnforceDto.getFacilInvstInfoDto().getSzAddrFacilInvstAffStr2());
			bookmarkNonFrmGrpList.add(addrFacilZip);
			bookmarkNonFrmGrpList.add(addrFacilCity);
			bookmarkNonFrmGrpList.add(addrFacilState);
			bookmarkNonFrmGrpList.add(addrFacilStr1);
			bookmarkNonFrmGrpList.add(addrFacilStr2);
		}
		// CINT07D
		if (!ObjectUtils.isEmpty(notifToLawEnforceDto.getIncomingDetailStageOutDtoList())) {
			BookmarkDto dtIncomingCall = createBookmark(BookmarkConstants.CONF_DATE_OF_REPORT,
					DateUtils.stringDt(notifToLawEnforceDto.getIncomingDetailStageOutDtoList().getDtIncomingCall()));
			BookmarkDto tmTmIncmgCall = createBookmark(BookmarkConstants.CONF_TIME_OF_REPORT,
					DateUtils.getTime(notifToLawEnforceDto.getIncomingDetailStageOutDtoList().getDtIncomingCall()));
			bookmarkNonFrmGrpList.add(dtIncomingCall);
			bookmarkNonFrmGrpList.add(tmTmIncmgCall);
		}
		// CINT86D PriorStageDto
		if (!ObjectUtils.isEmpty(notifToLawEnforceDto.getPriorStageDto())) {
			BookmarkDto idStage = createBookmark(BookmarkConstants.CONF_INTAKE_NUMBER,
					notifToLawEnforceDto.getPriorStageDto().getIdPriorStage());
			bookmarkNonFrmGrpList.add(idStage);
		}
		// CSES39D
		if (!ObjectUtils.isEmpty(notifToLawEnforceDto.getFacilInvDtlDto())) {
			BookmarkDto apsDate = createBookmark(BookmarkConstants.APS_CONCL_DATE,
					DateUtils.stringDt(notifToLawEnforceDto.getFacilInvDtlDto().getDtFacilInvstComplt()));
			bookmarkNonFrmGrpList.add(apsDate);
		}

		// new ADS changes, prefill allegation type value into editable field
		if (!ObjectUtils.isEmpty(notifToLawEnforceDto.getAllegationValue())) {
			//Fixed Warranty Defect#12004 Issue to fix duplicate bookmark creation
			String allegTypeValue = notifToLawEnforceDto.getAllegationValue().stream().map(a -> a.getCdAllegType()).collect(Collectors.joining(","));
			BookmarkDto allegType = createBookmark(BookmarkConstants.ALLEGATION_TYPES,
					allegTypeValue);
			bookmarkNonFrmGrpList.add(allegType);
		}

		BookmarkDto initialDate = createBookmark(BookmarkConstants.txtContactDate,
				DateUtils.stringDt(notifToLawEnforceDto.getInitialContDate().getDtDTContactOccurred()));
		bookmarkNonFrmGrpList.add(initialDate);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}
}
