package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.dto.WorkerDetailDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.disasterplan.dto.DisasterPlanDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.subcare.dto.ResourceAddressDto;
import us.tx.state.dfps.service.subcare.dto.ResourcePhoneDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:DisasterPlanFormPrefillData will implemented returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for form CFA19O00. Feb 9, 2018- 2:04:05 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class DisasterPlanFormPrefillData extends DocumentServiceUtil {

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		DisasterPlanDto disasterPlanDto = (DisasterPlanDto) parentDtoobj;

		if (null == disasterPlanDto.getGenericCaseInfoDto()) {
			disasterPlanDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}

		if (null == disasterPlanDto.getResourceAddress()) {
			disasterPlanDto.setResourceAddress(new ResourceAddressDto());
		}
		if (null == disasterPlanDto.getResourcePhoneList()) {
			disasterPlanDto.setResourcePhoneList(new ArrayList<ResourcePhoneDto>());
		}
		if (null == disasterPlanDto.getWorkerDetailDto()) {
			disasterPlanDto.setWorkerDetailDto(new WorkerDetailDto());
		}

		if (null == disasterPlanDto.getWorkerSupvDetailDto()) {
			disasterPlanDto.setWorkerSupvDetailDto(new WorkerDetailDto());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		/**
		 * Checking the nameSuffix not equal to null. if its not null, we set
		 * the preill data for group cfzco00
		 */

		if (null != disasterPlanDto.getWorkerDetailDto().getCdNameSuffix()) {
			FormDataGroupDto tempComma0FrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_0,
					FormConstants.EMPTY_STRING);

			formDataGroupList.add(tempComma0FrmDataGrpDto);

		}

		/**
		 * Checking the nameSuffix not equal to null. if its not null, we set
		 * the preill data for group cfzco01
		 */

		if (null != disasterPlanDto.getWorkerSupvDetailDto().getCdNameSuffix()) {
			FormDataGroupDto tempComma1FrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_1,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempComma1FrmDataGrpDto);

		}

		/**
		 * Checking the nbrPersonPhoneExtension not equal to null. if its not
		 * null, we set the prefill data for group cfzz1901
		 */

		if (null != disasterPlanDto.getWorkerDetailDto().getNbrPersonPhoneExtension()) {
			FormDataGroupDto tempWrkExtFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKER_EXT,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempWrkBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto workerPhonExt = createBookmark(BookmarkConstants.WORKER_PHONE_EXTENSION,
					TypeConvUtil.formatPhone(disasterPlanDto.getWorkerDetailDto().getNbrPersonPhoneExtension()));
			tempWrkBookmarkList.add(workerPhonExt);
			tempWrkExtFrmDataGrpDto.setBookmarkDtoList(tempWrkBookmarkList);
			formDataGroupList.add(tempWrkExtFrmDataGrpDto);

		}

		/**
		 * Checking the idName is greater than zero. If true, we set the prefill
		 * data for group cfa19a00
		 */

		if (null != disasterPlanDto.getWorkerDetailDto().getIdName()) {
			FormDataGroupDto tempRsrcIdFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RESOURCE_ID,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempWrkBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto resourceId = createBookmark(BookmarkConstants.RESOURCE_ID,
					disasterPlanDto.getWorkerDetailDto().getIdName());
			tempWrkBookmarkList.add(resourceId);
			tempRsrcIdFrmDataGrpDto.setBookmarkDtoList(tempWrkBookmarkList);
			formDataGroupList.add(tempRsrcIdFrmDataGrpDto);

		}

		/**
		 * Checking the nbrPersonPhoneExtension not equal to null. if its not
		 * null, we set the prefill data for group cfzz1902
		 */

		if (null != disasterPlanDto.getWorkerDetailDto().getNbrPersonPhoneExtension()) {
			FormDataGroupDto tempSupvExtFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SUPERVISOR_EXT,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempSupvBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto supvPhonExt = createBookmark(BookmarkConstants.SUPERVISOR_PHONE_EXTENSION,
					TypeConvUtil.formatPhone(disasterPlanDto.getWorkerDetailDto().getNbrPersonPhoneExtension()));
			tempSupvBookmarkList.add(supvPhonExt);
			tempSupvExtFrmDataGrpDto.setBookmarkDtoList(tempSupvBookmarkList);
			formDataGroupList.add(tempSupvExtFrmDataGrpDto);

		}

		/**
		 * Checking the nameSuffix not equal to null. if its not null, we set
		 * the preill data for group cfa19a10
		 */

		if (disasterPlanDto.getResourceAddress().getIdResource() > 0) {
			FormDataGroupDto tempRsrcFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RSRC_PRIM_ADDRESS,
					FormConstants.EMPTY_STRING);

			// Set prefill data for the subgroup - cfa19a11
			if (null != disasterPlanDto.getResourceAddress().getAddrRsrcAddrStLn2()) {
				FormDataGroupDto tempAddResFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_RSRC_PRIM_ADDRESS_2, FormGroupsConstants.TMPLAT_RSRC_PRIM_ADDRESS);
				List<BookmarkDto> tempAddrResBookMarkList = new ArrayList<BookmarkDto>();
				BookmarkDto bookMarkAddAddrRes = createBookmark(BookmarkConstants.RSRC_PRIM_ADDRESS_LINE_2,
						disasterPlanDto.getResourceAddress().getAddrRsrcAddrStLn2());
				tempAddrResBookMarkList.add(bookMarkAddAddrRes);
				tempAddResFrmDataGrpDto.setBookmarkDtoList(tempAddrResBookMarkList);

			}

			List<BookmarkDto> tempRsrcBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto bookMarkAddRsrcAddrAttn = createBookmark(BookmarkConstants.RSRC_PRIM_ADDR_ATTN,
					disasterPlanDto.getResourceAddress().getAddrRsrcAddrAttn());
			BookmarkDto bookMarkAddRsrcAddrCity = createBookmark(BookmarkConstants.RSRC_PRIM_ADDRESS_CITY,
					disasterPlanDto.getResourceAddress().getAddrRsrcAddrCity());
			BookmarkDto bookMarkAddRsrcStLn1 = createBookmark(BookmarkConstants.RSRC_PRIM_ADDRESS_LINE_1,
					disasterPlanDto.getResourceAddress().getAddrRsrcAddrStLn1());
			BookmarkDto bookMarkAddRsrcStLn2 = createBookmark(BookmarkConstants.RSRC_PRIM_ADDRESS_LINE_2,
					disasterPlanDto.getResourceAddress().getAddrRsrcAddrStLn2());
			BookmarkDto bookMarkAddRsrcZip = createBookmark(BookmarkConstants.RSRC_PRIM_ADDRESS_ZIP,
					disasterPlanDto.getResourceAddress().getAddrRsrcAddrZip());

			BookmarkDto bookMarkAddCdFacilityCounty = createBookmarkWithCodesTable(BookmarkConstants.RSRC_PRIM_COUNTY,
					disasterPlanDto.getResourceAddress().getCdRsrcAddrCounty(), CodesConstant.CCOUNT);
			BookmarkDto bookMarkAddCdFacilityState = createBookmarkWithCodesTable(
					BookmarkConstants.RSRC_PRIM_ADDRESS_STATE,
					disasterPlanDto.getResourceAddress().getCdRsrcAddrState(), CodesConstant.CSTATE);
			tempRsrcBookMarkList.add(bookMarkAddRsrcAddrAttn);
			tempRsrcBookMarkList.add(bookMarkAddRsrcAddrCity);
			tempRsrcBookMarkList.add(bookMarkAddRsrcStLn1);
			tempRsrcBookMarkList.add(bookMarkAddRsrcStLn2);
			tempRsrcBookMarkList.add(bookMarkAddRsrcZip);
			tempRsrcBookMarkList.add(bookMarkAddCdFacilityCounty);
			tempRsrcBookMarkList.add(bookMarkAddCdFacilityState);
			tempRsrcFrmDataGrpDto.setBookmarkDtoList(tempRsrcBookMarkList);

			formDataGroupList.add(tempRsrcFrmDataGrpDto);

		}

		/**
		 * Checking the idResource equal to null. if its null, we set the
		 * prefill data for group cfa19a12
		 */

		if (null == disasterPlanDto.getResourceAddress().getIdResource()) {
			FormDataGroupDto tempRsrcAddrFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_RSRC_PRIM_ADDRESS_INPUT, FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempRsrcBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto bookMarkAddRsrcAddress = createBookmark(BookmarkConstants.UE_GROUPID,
					disasterPlanDto.getResourceAddress().getIdRsrcAddress());
			tempRsrcBookmarkList.add(bookMarkAddRsrcAddress);
			tempRsrcAddrFrmDataGrpDto.setBookmarkDtoList(tempRsrcBookmarkList);
			formDataGroupList.add(tempRsrcAddrFrmDataGrpDto);

		}

		/**
		 * Checking the idResource equal to null. if its null, we set the
		 * prefill data for group cfa19a14
		 */

		if (!disasterPlanDto.getResourceAddress().getTxtRsrcAddrComments().equals(FormConstants.NULL_STRING)) {
			FormDataGroupDto tempRsrcSchDistFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_RSRC_SCH_DIST, FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempRsrcBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto bookMarkAddRsrcSchDist = createBookmark(BookmarkConstants.RSRC_SCH_DIST,
					disasterPlanDto.getResourceAddress().getTxtRsrcAddrComments());
			tempRsrcBookmarkList.add(bookMarkAddRsrcSchDist);
			tempRsrcSchDistFrmDataGrpDto.setBookmarkDtoList(tempRsrcBookmarkList);
			formDataGroupList.add(tempRsrcSchDistFrmDataGrpDto);
		}

		/**
		 * Checking the idResource equal to 0. if its 0, we set the prefill data
		 * for group cfa19a15
		 */

		if (disasterPlanDto.getResourceAddress().getIdResource() != 0) {
			FormDataGroupDto tempRsrcAddrFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HOME_NAME,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempRsrcBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto bookMarkAddHomeName = createBookmark(BookmarkConstants.HOME_NAME,
					disasterPlanDto.getResourceAddress().getNmResource());
			tempRsrcBookmarkList.add(bookMarkAddHomeName);
			tempRsrcAddrFrmDataGrpDto.setBookmarkDtoList(tempRsrcBookmarkList);
			formDataGroupList.add(tempRsrcAddrFrmDataGrpDto);

		}

		/**
		 * Checking the TxtRsrcAddrComments equal to null. if its null, we set
		 * the prefill data for group cfa19a13
		 */

		if (disasterPlanDto.getResourceAddress().getTxtRsrcAddrComments().equals(FormConstants.NULL_STRING)) {
			FormDataGroupDto tempRsrcAddrFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_RSRC_SCH_DIST_INPUT, FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempRsrcBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto bookMarkAddRsrcAddress = createBookmark(BookmarkConstants.UE_GROUPID,
					disasterPlanDto.getResourceAddress().getIdRsrcAddress());
			tempRsrcBookmarkList.add(bookMarkAddRsrcAddress);
			tempRsrcAddrFrmDataGrpDto.setBookmarkDtoList(tempRsrcBookmarkList);
			formDataGroupList.add(tempRsrcAddrFrmDataGrpDto);

		}

		/**
		 * Checking the cdPersAddrLinkType equals EV. if equals, we set the
		 * prefill data for group cfa19a20
		 */

		if (null != disasterPlanDto.getPersonAddressDto()) {
			FormDataGroupDto tempPersEvacAddrFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_PERS_EVAC_ADDRESS, FormConstants.EMPTY_STRING);

			// Set prefill data for the subgroup - cfa19a21
			if (null != disasterPlanDto.getPersonAddressDto().getAddrPersAddrStLn2()) {
				FormDataGroupDto tempAddResFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PERS_EVAC_ADDRESS_2, FormGroupsConstants.TMPLAT_PERS_EVAC_ADDRESS);
				List<BookmarkDto> tempAddrResBookMarkList = new ArrayList<BookmarkDto>();
				BookmarkDto bookMarkAddAddrRes = createBookmark(BookmarkConstants.PERS_EVAC_ADDRESS_LINE_2,
						disasterPlanDto.getPersonAddressDto().getAddrPersAddrStLn2());
				tempAddrResBookMarkList.add(bookMarkAddAddrRes);
				tempAddResFrmDataGrpDto.setBookmarkDtoList(tempAddrResBookMarkList);

			}

			List<BookmarkDto> tempPersEvacAddrBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto bookMarkAddAddressZip = createBookmark(BookmarkConstants.PERS_EVAC_ADDRESS_ZIP,
					disasterPlanDto.getPersonAddressDto().getAddrPersonAddrZip());

			BookmarkDto bookMarkAddAddressCity = createBookmark(BookmarkConstants.PERS_EVAC_ADDRESS_CITY,
					disasterPlanDto.getPersonAddressDto().getAddrPersonAddrCity());

			BookmarkDto bookMarkAddAddressLn1 = createBookmark(BookmarkConstants.PERS_EVAC_ADDRESS_LINE_1,
					disasterPlanDto.getPersonAddressDto().getAddrPersAddrStLn1());

			BookmarkDto bookMarkAddAddressCounty = createBookmark(BookmarkConstants.PERS_RESIDENCE_COUNTY,
					disasterPlanDto.getPersonAddressDto().getCdPersonAddrCounty());

			BookmarkDto bookMarkAddAddresState = createBookmark(BookmarkConstants.PERS_EVAC_ADDRESS_STATE,
					disasterPlanDto.getPersonAddressDto().getCdPersonAddrState());
			tempPersEvacAddrBookmarkList.add(bookMarkAddAddressZip);
			tempPersEvacAddrBookmarkList.add(bookMarkAddAddressCity);
			tempPersEvacAddrBookmarkList.add(bookMarkAddAddressLn1);
			tempPersEvacAddrBookmarkList.add(bookMarkAddAddressCounty);
			tempPersEvacAddrBookmarkList.add(bookMarkAddAddresState);
			tempPersEvacAddrFrmDataGrpDto.setBookmarkDtoList(tempPersEvacAddrBookmarkList);
			formDataGroupList.add(tempPersEvacAddrFrmDataGrpDto);

		}

		/**
		 * Checking the idPerson equal to null. 1f its null, we set the prefill
		 * data for group cfa19a22
		 */

		if (null != disasterPlanDto.getPersonAddressDto() && disasterPlanDto.getPersonAddressDto().getIdPerson() == 0) {
			FormDataGroupDto tempIdPersonFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_PERS_EVAC_ADDRESS_INPUT, FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempIdPersonBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto bookMarkAddIdPerson = createBookmark(BookmarkConstants.UE_GROUPID,
					disasterPlanDto.getPersonAddressDto().getIdPerson());
			tempIdPersonBookmarkList.add(bookMarkAddIdPerson);
			tempIdPersonFrmDataGrpDto.setBookmarkDtoList(tempIdPersonBookmarkList);
			formDataGroupList.add(tempIdPersonFrmDataGrpDto);

		}

		/**
		 * Checking the idPerson not equal to null. if its not null, we set the
		 * prefill data for group cfa19a23
		 */

		if (null != disasterPlanDto.getPersonAddressDto()) {
			FormDataGroupDto tempFamNameFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FAMILY_NAME,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempFamNameBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto bookMarkAddIdPerson = createBookmark(BookmarkConstants.UE_GROUPID,
					disasterPlanDto.getPersonAddressDto().getIdPerson());
			tempFamNameBookmarkList.add(bookMarkAddIdPerson);
			tempFamNameFrmDataGrpDto.setBookmarkDtoList(tempFamNameBookmarkList);
			formDataGroupList.add(tempFamNameFrmDataGrpDto);

		}

		/**
		 * Checking the cdFacilPhoneType equal to 01 or idRsrcPhone not equal
		 * null. if true, we set the prefill data for group cfa19a30
		 */

		for (ResourcePhoneDto resourcePhoneDto : disasterPlanDto.getResourcePhoneList()) {
			if (ServiceConstants.PRIMARY_ADDRESS_TYPE.equals(resourcePhoneDto.getCdRsrcPhoneType())
					&& 0 != resourcePhoneDto.getIdRsrcPhone()) {
				FormDataGroupDto tempRsrcPhoneFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_HOME_RSRC_PHONE, FormConstants.EMPTY_STRING);
				List<BookmarkDto> tempRsrcPhoneBookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto bookMarkAddFacilPhoneExtension = createBookmark(BookmarkConstants.HOME_RSRC_PHONE,
						TypeConvUtil.formatPhone(resourcePhoneDto.getNbrRsrcPhone()));
				tempRsrcPhoneBookmarkList.add(bookMarkAddFacilPhoneExtension);
				tempRsrcPhoneFrmDataGrpDto.setBookmarkDtoList(tempRsrcPhoneBookmarkList);
				formDataGroupList.add(tempRsrcPhoneFrmDataGrpDto);

			}
		}

		/**
		 * Checking the cdFacilPhoneType equal to 01 or idRsrcPhone not equal
		 * null. if true, we set the prefill data for group cfa19a31
		 */

		for (ResourcePhoneDto resourcePhoneDto : disasterPlanDto.getResourcePhoneList()) {
			if (resourcePhoneDto.getIdRsrcPhone() == 0) {
				FormDataGroupDto tempRsrcPhoneFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_HOME_RSRC_PHONE_INPUT, FormConstants.EMPTY_STRING);
				List<BookmarkDto> tempRsrcPhoneBookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto bookMarkAddIdRsrcPhone = createBookmark(BookmarkConstants.UE_GROUPID,
						TypeConvUtil.formatPhone(resourcePhoneDto.getIdRsrcPhone().toString()));
				tempRsrcPhoneBookmarkList.add(bookMarkAddIdRsrcPhone);
				tempRsrcPhoneFrmDataGrpDto.setBookmarkDtoList(tempRsrcPhoneBookmarkList);
				formDataGroupList.add(tempRsrcPhoneFrmDataGrpDto);

			}
		}

		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		// Populate nmStage value from DAM CSEC02D
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.STAGE_NAME,
				disasterPlanDto.getGenericCaseInfoDto().getNmStage());
		bookmarkNonFrmGrpList.add(bookmarkNmCase);

		// Populate idCase value from DAM CSEC02D
		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.CASE_ID,
				disasterPlanDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkIdCase);

		// Populate idCase value from DAM CSEC01D
		BookmarkDto bookmarkWorkerPhone = createBookmark(BookmarkConstants.WORKER_PHONE,
				TypeConvUtil.formatPhone(disasterPlanDto.getWorkerDetailDto().getNbrPersonPhone()));
		bookmarkNonFrmGrpList.add(bookmarkWorkerPhone);

		// Populate idCase value from DAM CSEC01D
		BookmarkDto bookmarkWorkerNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.WORKER_NAME_SUFFIX,
				disasterPlanDto.getWorkerDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameSuffix);

		// Populate idCase value from DAM CSEC01D
		BookmarkDto bookmarkWorkerNameFirst = createBookmark(BookmarkConstants.WORKER_NAME_FIRST,
				disasterPlanDto.getWorkerDetailDto().getNmNameFirst());
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameFirst);

		// Populate idCase value from DAM CSEC01D
		BookmarkDto bookmarkWorkerNameLast = createBookmark(BookmarkConstants.WORKER_NAME_LAST,
				disasterPlanDto.getWorkerDetailDto().getNmNameLast());
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameLast);

		// Populate idCase value from DAM CSEC01D
		BookmarkDto bookmarkWorkerNameMiddle = createBookmark(BookmarkConstants.WORKER_NAME_MIDDLE,
				disasterPlanDto.getWorkerDetailDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameMiddle);

		// Populate idCase value from DAM CSEC01D
		BookmarkDto bookmarkSupvPhone = createBookmark(BookmarkConstants.SUPERVISOR_PHONE,
				TypeConvUtil.formatPhone(disasterPlanDto.getWorkerSupvDetailDto().getNbrPersonPhone()));
		bookmarkNonFrmGrpList.add(bookmarkSupvPhone);

		// Populate idCase value from DAM CSEC01D
		BookmarkDto bookmarkSupvNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.SUPERVISOR_NAME_SUFFIX,
				disasterPlanDto.getWorkerSupvDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFrmGrpList.add(bookmarkSupvNameSuffix);

		// Populate idCase value from DAM CSEC01D
		BookmarkDto bookmarkSupvNameFirst = createBookmark(BookmarkConstants.SUPERVISOR_NAME_FIRST,
				disasterPlanDto.getWorkerSupvDetailDto().getNmNameFirst());
		bookmarkNonFrmGrpList.add(bookmarkSupvNameFirst);

		// Populate idCase value from DAM CSEC01D
		BookmarkDto bookmarkSupvNameLast = createBookmark(BookmarkConstants.SUPERVISOR_NAME_LAST,
				disasterPlanDto.getWorkerSupvDetailDto().getNmNameLast());
		bookmarkNonFrmGrpList.add(bookmarkSupvNameLast);

		// Populate idCase value from DAM CSEC01D
		BookmarkDto bookmarkSupvNameMiddle = createBookmark(BookmarkConstants.SUPERVISOR_NAME_MIDDLE,
				disasterPlanDto.getWorkerSupvDetailDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(bookmarkSupvNameMiddle);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;

	}

}
