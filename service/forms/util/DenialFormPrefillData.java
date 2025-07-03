package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.PersonAddressDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.FormattingUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.pca.dto.DenailLetterDto;
import us.tx.state.dfps.service.pca.dto.PlacementDtlDto;
import us.tx.state.dfps.service.pca.dto.ResourcePlcmntDto;
import us.tx.state.dfps.service.person.dto.SupervisorDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:DenailFormPrefillData will implemented returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for form CFA20o00. Feb 9, 2018- 2:04:05 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class DenialFormPrefillData extends DocumentServiceUtil {

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

		DenailLetterDto denailLetterDto = (DenailLetterDto) parentDtoobj;
		if (null == denailLetterDto.getEmployeePersPhNameDto()) {
			denailLetterDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (null == denailLetterDto.getSupervisorDto()) {
			denailLetterDto.setSupervisorDto(new SupervisorDto());
		}
		if (null == denailLetterDto.getPersonAddressDto()) {
			denailLetterDto.setPersonAddressDto(new PersonAddressDto());
		}
		if (null == denailLetterDto.getPersonDto()) {
			denailLetterDto.setPersonDto(new PersonDto());
		}
		if (null == denailLetterDto.getPlacementDtlDto()) {
			denailLetterDto.setPlacementDtlDto(new PlacementDtlDto());
		}
		if (null == denailLetterDto.getResourcePlcmntDto()) {
			denailLetterDto.setResourcePlcmntDto(new ResourcePlcmntDto());
		}
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		/**
		 * Checking the stage type equal to ADO or PAD. If equals set the
		 * prefill data for group cfa2005 and cfa2003
		 */
		if (ServiceConstants.ADOPTION_STG.equalsIgnoreCase(denailLetterDto.getStageCaseDtlDto().getCdStage())
				|| ServiceConstants.POST_ADOPTION_STG
						.equalsIgnoreCase(denailLetterDto.getStageCaseDtlDto().getCdStage())) {
			FormDataGroupDto tempAdoFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADO,
					FormConstants.EMPTY_STRING);
			FormDataGroupDto tempAdoSPFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADO_SP,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempAdoFrmDataGrpDto);
			formDataGroupList.add(tempAdoSPFrmDataGrpDto);
		}
		/**
		 * Checking the stage type equal to SUB or PCA. If equals set the
		 * prefill data for group cfa2005 and cfa2003
		 */
		else if (ServiceConstants.SUB_CARE_STG.equalsIgnoreCase(denailLetterDto.getStageCaseDtlDto().getCdStage())
				|| ServiceConstants.PCA_STG.equalsIgnoreCase(denailLetterDto.getStageCaseDtlDto().getCdStage())) {
			FormDataGroupDto tempPcaFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PCA,
					FormConstants.EMPTY_STRING);
			FormDataGroupDto tempPcaSPFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PCA_SP,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempPcaFrmDataGrpDto);
			formDataGroupList.add(tempPcaSPFrmDataGrpDto);
		}
		/**
		 * Checking the AddrMailCodeStLn2 value not equal to zero and then set
		 * prefill data for the group - cfa2015
		 */
		if (!ObjectUtils.isEmpty(denailLetterDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2())) {
			FormDataGroupDto tempAddWrkrLn2FrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_ADDR_WORKER_LN2, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkAddWrkrLn2List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkAddWrkrLn2Dto = createBookmark(BookmarkConstants.ADDRESS_WORKER_LINE_2,
					denailLetterDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
			bookmarkAddWrkrLn2List.add(bookmarkAddWrkrLn2Dto);
			tempAddWrkrLn2FrmDataGrpDto.setBookmarkDtoList(bookmarkAddWrkrLn2List);
			formDataGroupList.add(tempAddWrkrLn2FrmDataGrpDto);
		}
		/**
		 * Checking the idperson value equal to zero and set prefill data for
		 * the parent group - cfa2006
		 */
		if (Long.valueOf(FormConstants.STR_ZERO).equals(denailLetterDto.getPersonAddressDto().getIdPerson())) {
			FormDataGroupDto tempResDispFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RES_DISP,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> tempResDispFrmDataGrpList = new ArrayList<FormDataGroupDto>();
			// Set prefill data for the subgroup - cfa2007
			FormDataGroupDto tempAddResFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_RESOURCE,
					FormGroupsConstants.TMPLAT_RES_DISP);
			List<BookmarkDto> bookmarkAddResourceList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkAddResZipDto = createBookmark(BookmarkConstants.ADDR_ZIP_RES,
					denailLetterDto.getResourcePlcmntDto().getAddrRsrcZip());
			BookmarkDto bookmarkAddResCityDto = createBookmark(BookmarkConstants.ADDR_CITY_RES,
					denailLetterDto.getResourcePlcmntDto().getAddrRsrcCity());
			BookmarkDto bookmarkAddResStLn1Dto = createBookmark(BookmarkConstants.ADDR_LN1_RES,
					denailLetterDto.getResourcePlcmntDto().getAddrRsrcStLn1());
			BookmarkDto bookmarkAddResStateDto = createBookmark(BookmarkConstants.ADDR_ST_RES,
					denailLetterDto.getResourcePlcmntDto().getCdRsrcState());
			bookmarkAddResourceList.add(bookmarkAddResZipDto);
			bookmarkAddResourceList.add(bookmarkAddResCityDto);
			bookmarkAddResourceList.add(bookmarkAddResStLn1Dto);
			bookmarkAddResourceList.add(bookmarkAddResStateDto);
			tempAddResFrmDataGrpDto.setBookmarkDtoList(bookmarkAddResourceList);
			/**
			 * Checking the AddrRsrcStLn2 value not equal to empty, then Set
			 * prefill data for the subgroup - cfa2013
			 */
			if (!ObjectUtils.isEmpty(denailLetterDto.getResourcePlcmntDto().getAddrRsrcStLn2())) {
				List<FormDataGroupDto> tempAddResFrmDataGrpList = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto tempAddLn2ResFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_ADDR_LN2_RES, FormGroupsConstants.TMPLAT_ADDR_RESOURCE);
				List<BookmarkDto> bookmarkAddResStLnList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkAddResStLn2Dto = createBookmark(BookmarkConstants.ADDR_LN2_RES,
						denailLetterDto.getResourcePlcmntDto().getAddrRsrcStLn2());
				bookmarkAddResStLnList.add(bookmarkAddResStLn2Dto);
				tempAddLn2ResFrmDataGrpDto.setBookmarkDtoList(bookmarkAddResStLnList);
				tempAddResFrmDataGrpList.add(tempAddLn2ResFrmDataGrpDto);
				tempAddResFrmDataGrpDto.setFormDataGroupList(tempAddResFrmDataGrpList);
			}
			tempResDispFrmDataGrpList.add(tempAddResFrmDataGrpDto);
			tempResDispFrmDataGrpDto.setFormDataGroupList(tempResDispFrmDataGrpList);
			formDataGroupList.add(tempResDispFrmDataGrpDto);
		}
		/**
		 * Checking the idperson value equal to zero and set prefill data for
		 * the parent group - cfa2002
		 */
		if (Long.valueOf(FormConstants.STR_ZERO).equals(denailLetterDto.getPersonAddressDto().getIdPerson())) {
			FormDataGroupDto tempPlcmntDispFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PLCMT_DISP,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> tempPlcmntDispFrmDataGrpList = new ArrayList<FormDataGroupDto>();
			/**
			 * Checking the AddrRsrcStLn1 value equal to empty and AddrPlcmtLn1
			 * not equal empty, then set prefill data for the sub group -
			 * cfa2004
			 */
			if (FormConstants.EMPTY_STRING.equalsIgnoreCase(denailLetterDto.getResourcePlcmntDto().getAddrRsrcStLn1())
					&& !ObjectUtils.isEmpty(denailLetterDto.getResourcePlcmntDto().getAddrPlcmtLn1())) {
				FormDataGroupDto tempAddPlcmntFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_PLCMT,
						FormGroupsConstants.TMPLAT_PLCMT_DISP);
				List<BookmarkDto> bookmarkAddPlcmntList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkAddPlcmntCityDto = createBookmark(BookmarkConstants.ADDR_CITY_PLCMT,
						denailLetterDto.getResourcePlcmntDto().getAddrPlcmtCity());
				BookmarkDto bookmarkAddPlcmntLn1Dto = createBookmark(BookmarkConstants.ADDR_LN1_PLCMT,
						denailLetterDto.getResourcePlcmntDto().getAddrPlcmtLn1());
				BookmarkDto bookmarkAddPlcmntStDto = createBookmark(BookmarkConstants.ADDR_ST_PLCMT,
						denailLetterDto.getResourcePlcmntDto().getAddrPlcmtSt());
				BookmarkDto bookmarkAddPlcmntZipDto = createBookmark(BookmarkConstants.ADDR_ZIP_PLCMT,
						denailLetterDto.getResourcePlcmntDto().getAddrPlcmtZip());
				bookmarkAddPlcmntList.add(bookmarkAddPlcmntCityDto);
				bookmarkAddPlcmntList.add(bookmarkAddPlcmntLn1Dto);
				bookmarkAddPlcmntList.add(bookmarkAddPlcmntStDto);
				bookmarkAddPlcmntList.add(bookmarkAddPlcmntZipDto);
				tempAddPlcmntFrmDataGrpDto.setBookmarkDtoList(bookmarkAddPlcmntList);
				/**
				 * Checking the AddrPlcmtLn2 not equal empty, then set prefill
				 * data for the sub group - cfa2014
				 */
				if (!ObjectUtils.isEmpty(denailLetterDto.getResourcePlcmntDto().getAddrPlcmtLn2())) {
					List<FormDataGroupDto> tempAddPlcmntFrmDataGrpList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto tempAddLn2PlcmntFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_ADDR_LN2_PLCMT, FormGroupsConstants.TMPLAT_ADDR_PLCMT);
					List<BookmarkDto> bookmarkAddPlcmntLnList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkAddPlcmntLn2Dto = createBookmark(BookmarkConstants.ADDR_LN2_PLCMT,
							denailLetterDto.getResourcePlcmntDto().getAddrPlcmtLn2());
					bookmarkAddPlcmntLnList.add(bookmarkAddPlcmntLn2Dto);
					tempAddLn2PlcmntFrmDataGrpDto.setBookmarkDtoList(bookmarkAddPlcmntLnList);
					tempAddPlcmntFrmDataGrpList.add(tempAddLn2PlcmntFrmDataGrpDto);
					tempAddPlcmntFrmDataGrpDto.setFormDataGroupList(tempAddPlcmntFrmDataGrpList);
				}
				tempPlcmntDispFrmDataGrpList.add(tempAddPlcmntFrmDataGrpDto);
			}
			tempPlcmntDispFrmDataGrpDto.setFormDataGroupList(tempPlcmntDispFrmDataGrpList);
			formDataGroupList.add(tempPlcmntDispFrmDataGrpDto);
		}
		/**
		 * Checking the NmResource value equal to empty and NmPlcmntPersonFull
		 * not equal empty, then set prefill data for the group - cfa2010
		 */
		if (!ObjectUtils.isEmpty((denailLetterDto.getResourcePlcmntDto().getNmResource()))
				&& !ObjectUtils.isEmpty(denailLetterDto.getResourcePlcmntDto().getNmPlcmtPersonFull())) {
			FormDataGroupDto tempNmPlcmntFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_PLCMT,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkNmPlcmntList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkNmplcmntPrsnFullDto = createBookmark(BookmarkConstants.ADDR_NAME_PLCMT,
					denailLetterDto.getResourcePlcmntDto().getNmPlcmtPersonFull());
			bookmarkNmPlcmntList.add(bookmarkNmplcmntPrsnFullDto);
			tempNmPlcmntFrmDataGrpDto.setBookmarkDtoList(bookmarkNmPlcmntList);
			formDataGroupList.add(tempNmPlcmntFrmDataGrpDto);
		}

		/**
		 * Checking the NmResource value not equal to empty, then set prefill
		 * data for the group - cfa2011
		 */
		if (!ObjectUtils.isEmpty(denailLetterDto.getResourcePlcmntDto().getNmResource())) {
			FormDataGroupDto tempNmRsrcFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_RSRC,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkNmRsrcList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkNmRsrcDto = createBookmark(BookmarkConstants.ADDR_NAME_RSRC,
					denailLetterDto.getResourcePlcmntDto().getNmResource());
			bookmarkNmRsrcList.add(bookmarkNmRsrcDto);
			tempNmRsrcFrmDataGrpDto.setBookmarkDtoList(bookmarkNmRsrcList);
			formDataGroupList.add(tempNmRsrcFrmDataGrpDto);
		}
		/**
		 * Checking the AddrPersAddrStLn1 value not equal to empty, then set
		 * prefill data for the parent group - cfa2001
		 */
		if (!ObjectUtils.isEmpty(denailLetterDto.getPersonAddressDto().getAddrPersAddrStLn1())) {
			FormDataGroupDto tempAddMedFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_MEDICAID,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> tempAddMedFrmDataGrpList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkAddMedList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkAddZipDto = createBookmark(BookmarkConstants.ADDR_ZIP_MED,
					denailLetterDto.getPersonAddressDto().getAddrPersonAddrZip());
			BookmarkDto bookmarkAddCityDto = createBookmark(BookmarkConstants.ADDR_CITY_MED,
					denailLetterDto.getPersonAddressDto().getAddrPersonAddrCity());
			BookmarkDto bookmarkAddPersStLn1Dto = createBookmark(BookmarkConstants.ADDR_LN1_MED,
					denailLetterDto.getPersonAddressDto().getAddrPersAddrStLn1());
			BookmarkDto bookmarkAddPersStLn2Dto = createBookmark(BookmarkConstants.ADDR_LN2_MED,
					denailLetterDto.getPersonAddressDto().getAddrPersAddrStLn2());
			BookmarkDto bookmarkCdAddStateDto = createBookmark(BookmarkConstants.ADDR_ST_MED,
					denailLetterDto.getPersonAddressDto().getCdPersonAddrState());
			bookmarkAddMedList.add(bookmarkAddZipDto);
			bookmarkAddMedList.add(bookmarkAddCityDto);
			bookmarkAddMedList.add(bookmarkAddPersStLn1Dto);
			bookmarkAddMedList.add(bookmarkAddPersStLn2Dto);
			bookmarkAddMedList.add(bookmarkCdAddStateDto);
			tempAddMedFrmDataGrpDto.setBookmarkDtoList(bookmarkAddMedList);
			/**
			 * Checking the AddrPersAddrStLn2 value not equal to empty, then set
			 * prefill data for the sub group - cfa2012
			 */
			if (!ObjectUtils.isEmpty(denailLetterDto.getPersonAddressDto().getAddrPersAddrStLn2())) {
				FormDataGroupDto tempAddLn2MedFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_ADDR_LN2_MED, FormGroupsConstants.TMPLAT_ADDR_MEDICAID);

				List<BookmarkDto> bookmarkAddStLn2List = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkAddStLn2Dto = createBookmark(BookmarkConstants.ADDR_LN2_MED,
						denailLetterDto.getPersonAddressDto().getAddrPersAddrStLn2());
				bookmarkAddStLn2List.add(bookmarkAddStLn2Dto);
				tempAddLn2MedFrmDataGrpDto.setBookmarkDtoList(bookmarkAddStLn2List);
				tempAddMedFrmDataGrpList.add(tempAddLn2MedFrmDataGrpDto);
			}
			tempAddMedFrmDataGrpDto.setFormDataGroupList(tempAddMedFrmDataGrpList);
			formDataGroupList.add(tempAddMedFrmDataGrpDto);
		}

		// Set prefill data for the parent group - cfa2016
		FormDataGroupDto tempAddPcaDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_PCA,
				FormConstants.EMPTY_STRING);
		List<FormDataGroupDto> tempAddPcaFrmDataGrpList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkAddPcaList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkAddPlcmntCityDto = createBookmark(BookmarkConstants.ADDR_CITY_PCA,
				denailLetterDto.getPlacementDtlDto().getAddrPlcmtCity());
		BookmarkDto bookmarkAddPlcmntLn1Dto = createBookmark(BookmarkConstants.ADDR_LN1_PCA,
				denailLetterDto.getPlacementDtlDto().getAddrPlcmtLn1());
		BookmarkDto bookmarkAddPlcmntStDto = createBookmark(BookmarkConstants.ADDR_ST_PCA,
				denailLetterDto.getPlacementDtlDto().getAddrPlcmtSt());
		BookmarkDto bookmarkAddplcmntZipDto = createBookmark(BookmarkConstants.ADDR_ZIP_PCA,
				denailLetterDto.getPlacementDtlDto().getAddrPlcmtZip());
		BookmarkDto bookmarkNmPlcmntPersFullDto = createBookmark(BookmarkConstants.ADDR_NM_PCA,
				denailLetterDto.getPlacementDtlDto().getNmPlcmtPersonFull());
		bookmarkAddPcaList.add(bookmarkAddPlcmntCityDto);
		bookmarkAddPcaList.add(bookmarkAddPlcmntLn1Dto);
		bookmarkAddPcaList.add(bookmarkAddPlcmntStDto);
		bookmarkAddPcaList.add(bookmarkAddplcmntZipDto);
		bookmarkAddPcaList.add(bookmarkNmPlcmntPersFullDto);
		tempAddPcaDataGrpDto.setBookmarkDtoList(bookmarkAddPcaList);
		/**
		 * Checking the AddrPlcmtLn2 value not equal to empty, then set prefill
		 * data for the sub group - cfa2017
		 */
		if (!ObjectUtils.isEmpty(denailLetterDto.getPlacementDtlDto().getAddrPlcmtLn2())) {
			FormDataGroupDto tempAddLn2PcaFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_LN2_PCA,
					FormGroupsConstants.TMPLAT_ADDR_PCA);
			List<BookmarkDto> bookmarkAddLn2PcaList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkAddPlcmntLn2Dto = createBookmark(BookmarkConstants.ADDR_LN2_PCA,
					denailLetterDto.getPlacementDtlDto().getAddrPlcmtLn2());
			bookmarkAddLn2PcaList.add(bookmarkAddPlcmntLn2Dto);
			tempAddLn2PcaFrmDataGrpDto.setBookmarkDtoList(bookmarkAddLn2PcaList);
			tempAddPcaFrmDataGrpList.add(tempAddLn2PcaFrmDataGrpDto);
		}
		/**
		 * Checking the AddrPlcmtLn2 value equal to empty, then set prefill data
		 * for the sub group - cfa2018
		 */
		if (ObjectUtils.isEmpty(denailLetterDto.getPlacementDtlDto().getNmPlcmtPersonFull())) {
			FormDataGroupDto tempPcaNmPlcmntFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PCA_NM_PLCMT,
					FormGroupsConstants.TMPLAT_ADDR_PCA);
			List<BookmarkDto> bookmarkPcaNmPlcmntList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkNmPlcmntFacilDto = createBookmark(BookmarkConstants.ADDR_NM_PCA_PLCMT,
					denailLetterDto.getPlacementDtlDto().getNmPlcmtFacil());
			bookmarkPcaNmPlcmntList.add(bookmarkNmPlcmntFacilDto);
			tempPcaNmPlcmntFrmDataGrpDto.setBookmarkDtoList(bookmarkPcaNmPlcmntList);
			tempAddPcaFrmDataGrpList.add(tempPcaNmPlcmntFrmDataGrpDto);
		}
		tempAddPcaDataGrpDto.setFormDataGroupList(tempAddPcaFrmDataGrpList);
		formDataGroupList.add(tempAddPcaDataGrpDto);

		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		// Populate SystemDate value from DAM CSEC02D
		BookmarkDto bookmarkSysDate = createBookmark(BookmarkConstants.SYSTEM_DATE,
				FormattingUtils.formatDate(denailLetterDto.getStageCaseDtlDto().getCurrentDate()));
		bookmarkNonFrmGrpList.add(bookmarkSysDate);
		// Populate addressMailCodeCity value from DAM CSEC01D
		BookmarkDto bookmarkAddMailCode = createBookmark(BookmarkConstants.ADDRESS_WORKER_CITY,
				denailLetterDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
		bookmarkNonFrmGrpList.add(bookmarkAddMailCode);
		// Populate addressMailCodeStLn1 value from DAM CSEC01D
		BookmarkDto bookmarkAddMailCodeStLn1 = createBookmark(BookmarkConstants.ADDRESS_WORKER_LINE_1,
				denailLetterDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
		bookmarkNonFrmGrpList.add(bookmarkAddMailCodeStLn1);
		// Populate address mail code Zip from DAM CSEC01D
		BookmarkDto bookmarkAddMailCodeZip = createBookmark(BookmarkConstants.ADDRESS_WORKER_ZIP,
				denailLetterDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
		bookmarkNonFrmGrpList.add(bookmarkAddMailCodeZip);
		// Populate nbrmailcodephone value from DAM CSEC01D
		BookmarkDto bookmarkAddMailCodePhone = createBookmark(BookmarkConstants.ADDRESS_WORKER_PHONE,
				TypeConvUtil.getPhoneWithFormat(denailLetterDto.getEmployeePersPhNameDto().getNbrPhone(),
						denailLetterDto.getEmployeePersPhNameDto().getNbrPhoneExtension()));
		bookmarkNonFrmGrpList.add(bookmarkAddMailCodePhone);
		// Populate nbrMmailcodePhoneExtension value from DAM CSEC01D
		// BookmarkDto bookmarkAddMailCodePhoneExt =
		// createBookmark(BookmarkConstants.ADDRESS_WORKER_EXT,
		// denailLetterDto.getEmployeePersPhNameDto().getNbrPhoneExtension());
		// bookmarkNonFrmGrpList.add(bookmarkAddMailCodePhoneExt);
		// Populate AddressNmWorkerfirst value from DAM CSEC01D
		BookmarkDto bookmarkAddNmWorkerFirst = createBookmark(BookmarkConstants.ADDR_NM_WORKER_FIRST,
				denailLetterDto.getEmployeePersPhNameDto().getNmNameFirst());
		bookmarkNonFrmGrpList.add(bookmarkAddNmWorkerFirst);
		// Populate nmNamefirst value from DAM CSEC01D
		BookmarkDto bookmarkNmNameFirst = createBookmark(BookmarkConstants.NM_FROM_FIRST,
				denailLetterDto.getEmployeePersPhNameDto().getNmNameFirst());
		bookmarkNonFrmGrpList.add(bookmarkNmNameFirst);
		// Populate AddressNmWorkerLast value from DAM CSEC01D
		BookmarkDto bookmarkAddNmWorkerLast = createBookmark(BookmarkConstants.ADDR_NM_WORKER_LAST,
				denailLetterDto.getEmployeePersPhNameDto().getNmNameLast());
		bookmarkNonFrmGrpList.add(bookmarkAddNmWorkerLast);
		// Populate nmNameLast value from DAM CSEC01D
		BookmarkDto bookmarkNmNameLast = createBookmark(BookmarkConstants.NM_FROM_LAST,
				denailLetterDto.getEmployeePersPhNameDto().getNmNameLast());
		bookmarkNonFrmGrpList.add(bookmarkNmNameLast);
		// Populate AddressNmWorkerMiddle value from DAM CSEC01D
		BookmarkDto bookmarkAddNmWorkerMid = createBookmark(BookmarkConstants.ADDR_NM_WORKER_MIDDLE,
				denailLetterDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(bookmarkAddNmWorkerMid);
		// Populate nmNameMiddle value from DAM CSEC01D
		BookmarkDto bookmarkNmNameMid = createBookmark(BookmarkConstants.NM_FROM_MIDDLE,
				denailLetterDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(bookmarkNmNameMid);
		// Populate nmNameOffice value from DAM CSEC01D
		BookmarkDto bookmarkNmNameOffice = createBookmark(BookmarkConstants.ADDR_NM_WORKER_OFF,
				denailLetterDto.getEmployeePersPhNameDto().getNmOfficeName());
		bookmarkNonFrmGrpList.add(bookmarkNmNameOffice);
		// Populate nmResource value from DAM CSES28D
		BookmarkDto bookmarkNmNameResource = createBookmark(BookmarkConstants.ADDR_NAME,
				denailLetterDto.getResourcePlcmntDto().getNmResource());
		bookmarkNonFrmGrpList.add(bookmarkNmNameResource);
		// Populate idPlcmntChild value from DAM CSES28D
		BookmarkDto bookmarkIdPlcmntChild = createBookmark(BookmarkConstants.PLCMT_CHILD,
				denailLetterDto.getResourcePlcmntDto().getIdPersonPlcmtChild());
		bookmarkNonFrmGrpList.add(bookmarkIdPlcmntChild);
		// Populate nmPersonFirst value from DAM CCMN44D
		BookmarkDto bookmarkNmPersFirst = createBookmark(BookmarkConstants.NM_CHILD_FIRST,
				denailLetterDto.getPersonDto().getNmPersonFirst());
		bookmarkNonFrmGrpList.add(bookmarkNmPersFirst);
		// Populate nmPersonLast value from DAM CCMN44D
		BookmarkDto bookmarkNmPersLast = createBookmark(BookmarkConstants.NM_CHILD_LAST,
				denailLetterDto.getPersonDto().getNmPersonLast());
		bookmarkNonFrmGrpList.add(bookmarkNmPersLast);
		// Populate nmPersonMiddle value from DAM CCMN44D
		BookmarkDto bookmarkNmPersMid = createBookmark(BookmarkConstants.NM_CHILD_MIDDLE,
				denailLetterDto.getPersonDto().getNmPersonMiddle());
		bookmarkNonFrmGrpList.add(bookmarkNmPersMid);
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}

}
