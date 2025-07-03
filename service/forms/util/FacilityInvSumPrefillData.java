package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.notiftolawenforcement.dto.FacilInvDtlDto;
import us.tx.state.dfps.notiftolawenforcement.dto.MultiAddressDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityInvSumDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.AllegationWithVicDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;
import us.tx.state.dfps.xmlstructs.outputstructs.AllegationStageVictimDto;
import us.tx.state.dfps.xmlstructs.outputstructs.FacilAllegPersonDto;
import us.tx.state.dfps.xmlstructs.outputstructs.FacilInvstInfoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * FacilityInvSumPrefillData will implement returnPrefillData operation defined
 * in DocumentServiceUtil Interface to populate the prefill data for form
 * CFIV05O00.Form CFIV1300 Mar 16, 2018- 5:14:11 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class FacilityInvSumPrefillData extends DocumentServiceUtil {

	public FacilityInvSumPrefillData() {
		super();
	}

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

		FacilityInvSumDto facilityInvSumDto = (FacilityInvSumDto) parentDtoobj;

		if (ObjectUtils.isEmpty(facilityInvSumDto.getFacilityAllegationInfoDto())) {
			facilityInvSumDto.setFacilityAllegationInfoDto(new FacilityAllegationInfoDto());
		}
		if (ObjectUtils.isEmpty(facilityInvSumDto.getGenCaseInfoDto())) {
			facilityInvSumDto.setGenCaseInfoDto(new GenericCaseInfoDto());
		}
		if (ObjectUtils.isEmpty(facilityInvSumDto.getFacilityAllegationInfoDto())) {
			facilityInvSumDto.setFacilityAllegationInfoDto(new FacilityAllegationInfoDto());
		}
		if (ObjectUtils.isEmpty(facilityInvSumDto.getAllegationWithVicDtoList())) {
			facilityInvSumDto.setAllegationWithVicDtoList(new ArrayList<AllegationWithVicDto>());
		}
		if (ObjectUtils.isEmpty(facilityInvSumDto.getFacilInvDtlDto())) {
			facilityInvSumDto.setFacilInvDtlDto(new FacilInvDtlDto());
		}
		if (ObjectUtils.isEmpty(facilityInvSumDto.getCaseInfoDtoprincipal())) {
			facilityInvSumDto.setCaseInfoDtoprincipal(new ArrayList<CaseInfoDto>());
		}
		if (ObjectUtils.isEmpty(facilityInvSumDto.getCaseInfoDtocollateral())) {
			facilityInvSumDto.setCaseInfoDtocollateral(new ArrayList<CaseInfoDto>());
		}
		if (ObjectUtils.isEmpty(facilityInvSumDto.getStagePersonDto())) {
			facilityInvSumDto.setStagePersonDto(new StagePersonDto());
		}
		if (ObjectUtils.isEmpty(facilityInvSumDto.getEmployeePersPhNameDtoWorker())) {
			facilityInvSumDto.setEmployeePersPhNameDtoWorker(new EmployeePersPhNameDto());
		}
		if (ObjectUtils.isEmpty(facilityInvSumDto.getEmployeePersPhNameDtoWorker())) {
			facilityInvSumDto.setEmployeePersPhNameDtoSupv(new EmployeePersPhNameDto());
		}
		if (ObjectUtils.isEmpty(facilityInvSumDto.getFacilAllegPersonDto())) {
			facilityInvSumDto.setFacilAllegPersonDto(new FacilAllegPersonDto());
		}
		if (ObjectUtils.isEmpty(facilityInvSumDto.getFacilInvstInfoDto())) {
			facilityInvSumDto.setFacilInvstInfoDto(new FacilInvstInfoDto());
		}
		if (ObjectUtils.isEmpty(facilityInvSumDto.getMultiAddressDtoList())) {
			facilityInvSumDto.setMultiAddressDtoList(new ArrayList<MultiAddressDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// set the prefill data for group cfzco00

		if (FormConstants.EMPTY_STRING.equals(facilityInvSumDto.getEmployeePersPhNameDtoWorker().getCdNameSuffix())) {
			FormDataGroupDto tempCommaChildFrmDataGrpDtoWorker = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempCommaChildFrmDataGrpDtoWorker);
		}

		if (FormConstants.EMPTY_STRING.equals(facilityInvSumDto.getEmployeePersPhNameDtoSupv().getCdNameSuffix())) {
			FormDataGroupDto tempCommaChildFrmDataGrpDtoSupv = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_2,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempCommaChildFrmDataGrpDtoSupv);
		}

		// set the prefill data for group cfiv1301

		for (AllegationWithVicDto allegationWithVicDto : facilityInvSumDto.getAllegationWithVicDtoList()) {
			FormDataGroupDto tempAllegationFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkAllegationList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkAllegDisposition = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_DISP,
					allegationWithVicDto.getaCdAllegDisposition(), CodesConstant.CDISPSTN);
			BookmarkDto bookmarkAllegType = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_ALLEG,
					allegationWithVicDto.getaCdAllegType(), CodesConstant.CFACALGT);
			BookmarkDto bookmarkFacilAllegClss = createBookmarkWithCodesTable(BookmarkConstants.CD_FACIL_ALLEG_CLASS,
					allegationWithVicDto.getfCdFacilAllegClss(), CodesConstant.CFACCLSS);
			BookmarkDto bookmarkFacilAllegSrc = createBookmark(BookmarkConstants.ALLEG_DTL_AP,
					allegationWithVicDto.getcNmPersonFull());
			BookmarkDto bookmarkPersonFull = createBookmark(BookmarkConstants.ALLEG_DTL_VICTIM,
					allegationWithVicDto.getB_nmPersonFull());
			bookmarkAllegationList.add(bookmarkAllegDisposition);
			bookmarkAllegationList.add(bookmarkAllegType);
			bookmarkAllegationList.add(bookmarkFacilAllegClss);
			bookmarkAllegationList.add(bookmarkFacilAllegSrc);
			bookmarkAllegationList.add(bookmarkPersonFull);

			tempAllegationFrmDataGrpDto.setBookmarkDtoList(bookmarkAllegationList);
			formDataGroupList.add(tempAllegationFrmDataGrpDto);

		}

		// set the prefill data for group cfiv1304 //CLSC16D

		for (AllegationStageVictimDto allegationStageVictimDto : facilityInvSumDto.getFacilAllegPersonDto()
				.getAllegFacilPersonDto().getAllegationStageVictimDtoList()) {
			FormDataGroupDto facilAllegPersonDtoFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FINDING,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkFacilAllegPersonList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkDtDtFacilAllegInvstgtr = createBookmark(BookmarkConstants.FINDING_INV_DATE,
					DateUtils.stringDt(allegationStageVictimDto.getDtFacilAllegInvstgtr()));
			bookmarkFacilAllegPersonList.add(bookmarkDtDtFacilAllegInvstgtr);
			BookmarkDto bookmarkDtDtFacilAllegSuprReply = createBookmark(BookmarkConstants.FINDING_SUP_DATE,
					DateUtils.stringDt(allegationStageVictimDto.getDtFacilAllegSuprReply()));
			bookmarkFacilAllegPersonList.add(bookmarkDtDtFacilAllegSuprReply);

			BookmarkDto bookmarkFacilAllegClssSupr = createBookmarkWithCodesTable(BookmarkConstants.FINDING_SUP_CLASS,
					allegationStageVictimDto.getCdFacilAllegClssSupr(), CodesConstant.CFACCLSS);
			bookmarkFacilAllegPersonList.add(bookmarkFacilAllegClssSupr);
			BookmarkDto bookmarkFacilAllegDispSupr = createBookmarkWithCodesTable(
					BookmarkConstants.FINDING_SUP_CONFIRMATION, allegationStageVictimDto.getCdFacilAllegDispSupr(),
					CodesConstant.CDISPSTN);
			bookmarkFacilAllegPersonList.add(bookmarkFacilAllegDispSupr);
			BookmarkDto bookmarkFacilAllegSrc = createBookmarkWithCodesTable(
					BookmarkConstants.FINDING_INV_SOURCE_INJURY, allegationStageVictimDto.getCdFacilAllegSrc(),
					CodesConstant.CSRCOINJ);
			bookmarkFacilAllegPersonList.add(bookmarkFacilAllegSrc);
			BookmarkDto bookmarkFacilAllegSrcSupr = createBookmarkWithCodesTable(
					BookmarkConstants.FINDING_SUP_SOURCE_INJURY, allegationStageVictimDto.getCdFacilAllegSrcSupr(),
					CodesConstant.CSRCOINJ);
			bookmarkFacilAllegPersonList.add(bookmarkFacilAllegSrcSupr);
			BookmarkDto bookmarkCdNameSuffix = createBookmarkWithCodesTable(
					BookmarkConstants.FINDING_VICTIM_NAME_SUFFIX, allegationStageVictimDto.getCdNameSuffix(),
					CodesConstant.CSUFFIX2);
			bookmarkFacilAllegPersonList.add(bookmarkCdNameSuffix);
			BookmarkDto bookmarkFacilAllegInvClass = createBookmarkWithCodesTable(BookmarkConstants.FINDING_INV_CLASS,
					allegationStageVictimDto.getFacilAllegInvClass(), CodesConstant.CFACCLSS);
			bookmarkFacilAllegPersonList.add(bookmarkFacilAllegInvClass);

			BookmarkDto bookmarkNmNameFirstd = createBookmark(BookmarkConstants.FINDING_VICTIM_NAME_FIRST,
					allegationStageVictimDto.getNameFirst());
			bookmarkFacilAllegPersonList.add(bookmarkNmNameFirstd);
			BookmarkDto bookmarkNmNameLastd = createBookmark(BookmarkConstants.FINDING_VICTIM_NAME_LAST,
					allegationStageVictimDto.getNameLast());
			bookmarkFacilAllegPersonList.add(bookmarkNmNameLastd);
			BookmarkDto bookmarkNmNameMiddled = createBookmark(BookmarkConstants.FINDING_VICTIM_NAME_MIDDLE,
					allegationStageVictimDto.getNameMiddle());
			bookmarkFacilAllegPersonList.add(bookmarkNmNameMiddled);

			facilAllegPersonDtoFrmDataGrpDto.setBookmarkDtoList(bookmarkFacilAllegPersonList);
			formDataGroupList.add(facilAllegPersonDtoFrmDataGrpDto);

			List<FormDataGroupDto> facilAllegPersonDtoFrmDataGrpList = new ArrayList<FormDataGroupDto>();

			// set the prefill data for group cfiv1306 - sub group of cfiv1304

			/*
			 * for (AllegationWithVicDto allegationWithVicDto :
			 * facilityInvSumDto.getAllegationWithVicDtoList()) { if
			 * (allegationWithVicDto.getaIdAllegation() ==
			 * allegationStageVictimDto.getIdAllegation()) {
			 */
			FormDataGroupDto tempInvConfrmFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_INV_CONFIRM,
					FormGroupsConstants.TMPLAT_FINDING);
			List<BookmarkDto> bookmarkFacilAllegPersonfindingList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkAllegDisposition = createBookmarkWithCodesTable(BookmarkConstants.INV_CONFIRM_CONFIRM,
					allegationStageVictimDto.getCdAllegDisposition(), CodesConstant.CDISPSTN);
			bookmarkFacilAllegPersonfindingList.add(bookmarkAllegDisposition);

			tempInvConfrmFrmDataGrpDto.setBookmarkDtoList(bookmarkFacilAllegPersonfindingList);
			facilAllegPersonDtoFrmDataGrpList.add(tempInvConfrmFrmDataGrpDto);
			/*
			 * } }
			 */
			facilAllegPersonDtoFrmDataGrpDto.setFormDataGroupList(facilAllegPersonDtoFrmDataGrpList);

			// set the prefill data for group cfzco00 - sub group of cfiv1304

			if (FormConstants.EMPTY_STRING.equals(allegationStageVictimDto.getCdNameSuffix())) {
				FormDataGroupDto tempCommaChildFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_B,
						FormGroupsConstants.TMPLAT_FINDING);
				facilAllegPersonDtoFrmDataGrpList.add(tempCommaChildFrmDataGrpDto);
			}

		}

		// set the prefill data for group cfiv1305

		for (CaseInfoDto caseInfoDtoprincipalType : facilityInvSumDto.getCaseInfoDtoprincipal()) {
			List<BookmarkDto> bookmarkCaseInfoDtoprincipalList = new ArrayList<BookmarkDto>();
			FormDataGroupDto tempPrincipalFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRINCIPAL_1,
					FormConstants.EMPTY_STRING);
			BookmarkDto bookmarkIndPersonDobApprox = createBookmark(BookmarkConstants.PRINCIPAL_DTL_DOBAPPX,
					caseInfoDtoprincipalType.getIndPersonDobApprox());
			BookmarkDto bookmarkCdPersonSex = createBookmark(BookmarkConstants.PRINCIPAL_DTL_SEX,
					caseInfoDtoprincipalType.getCdPersonSex());
			BookmarkDto bookmarkDtPersonBirth = createBookmark(BookmarkConstants.PRINCIPAL_DTL_DOB,
					DateUtils.stringDt(caseInfoDtoprincipalType.getDtPersonBirth()));
			BookmarkDto bookmarkNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.PRINCIPAL_DTL_NAME_SUFFIX,
					caseInfoDtoprincipalType.getCdNameSuffix(), CodesConstant.CSUFFIX2);
			BookmarkDto bookmarkCdStagePersRelInt = createBookmarkWithCodesTable(BookmarkConstants.PRINCIPAL_DTL_RELINT,
					caseInfoDtoprincipalType.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
			BookmarkDto bookmarkCdStagePersRole = createBookmarkWithCodesTable(BookmarkConstants.PRINCIPAL_DTL_ROLE,
					caseInfoDtoprincipalType.getCdStagePersRole(), CodesConstant.CINVROLE);
			BookmarkDto bookmarkCdStagePersType = createBookmarkWithCodesTable(BookmarkConstants.PRINCIPAL_DTL_TYPE,
					caseInfoDtoprincipalType.getCdStagePersType(), CodesConstant.CPRSNTYP);
			BookmarkDto bookmarkNameFirst = createBookmark(BookmarkConstants.PRINCIPAL_DTL_NAME_FIRST,
					caseInfoDtoprincipalType.getNmNameFirst());
			BookmarkDto bookmarkNameLast = createBookmark(BookmarkConstants.PRINCIPAL_DTL_NAME_LAST,
					caseInfoDtoprincipalType.getNmNameLast());
			BookmarkDto bookmarkNameMiddle = createBookmark(BookmarkConstants.PRINCIPAL_DTL_NAME_MIDDLE,
					caseInfoDtoprincipalType.getNmNameMiddle());

			bookmarkCaseInfoDtoprincipalList.add(bookmarkIndPersonDobApprox);
			bookmarkCaseInfoDtoprincipalList.add(bookmarkCdPersonSex);
			bookmarkCaseInfoDtoprincipalList.add(bookmarkDtPersonBirth);
			bookmarkCaseInfoDtoprincipalList.add(bookmarkNameSuffix);
			bookmarkCaseInfoDtoprincipalList.add(bookmarkCdStagePersRelInt);
			bookmarkCaseInfoDtoprincipalList.add(bookmarkCdStagePersRole);
			bookmarkCaseInfoDtoprincipalList.add(bookmarkCdStagePersType);
			bookmarkCaseInfoDtoprincipalList.add(bookmarkNameFirst);
			bookmarkCaseInfoDtoprincipalList.add(bookmarkNameLast);
			bookmarkCaseInfoDtoprincipalList.add(bookmarkNameMiddle);

			tempPrincipalFrmDataGrpDto.setBookmarkDtoList(bookmarkCaseInfoDtoprincipalList);
			formDataGroupList.add(tempPrincipalFrmDataGrpDto);

			// set the prefill data for group cfzco00 - sub group of cfiv1305

			List<FormDataGroupDto> principalFrmDataGrpFrmDataGrpList = new ArrayList<FormDataGroupDto>();
			if (FormConstants.EMPTY_STRING
					.equals(facilityInvSumDto.getEmployeePersPhNameDtoWorker().getCdNameSuffix())) {
				FormDataGroupDto tempCommaChildFrmDataGrpDtoWorker = createFormDataGroup(
						FormGroupsConstants.TMPLAT_COMMA, FormGroupsConstants.TMPLAT_PRINCIPAL_1);
				principalFrmDataGrpFrmDataGrpList.add(tempCommaChildFrmDataGrpDtoWorker);
			}

			tempPrincipalFrmDataGrpDto.setFormDataGroupList(principalFrmDataGrpFrmDataGrpList);

		}

		// set the prefill data for group cfiv1307

		for (CaseInfoDto caseInfoDtoCollateralType : facilityInvSumDto.getCaseInfoDtocollateral()) {
			FormDataGroupDto tempCollateralFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRINCIPAL_2,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkCaseInfoDtoCollateralList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkIndPersonDobApprox = createBookmark(BookmarkConstants.COLLATERAL_DTL_DOBAPPX,
					caseInfoDtoCollateralType.getIndPersonDobApprox());
			BookmarkDto bookmarkCdPersonSex = createBookmark(BookmarkConstants.COLLATERAL_DTL_SEX,
					caseInfoDtoCollateralType.getCdPersonSex());
			BookmarkDto bookmarkDtPersonBirth = createBookmark(BookmarkConstants.COLLATERAL_DTL_DOB,
					DateUtils.stringDt(caseInfoDtoCollateralType.getDtPersonBirth()));
			BookmarkDto bookmarkCdNameSuffixc = createBookmarkWithCodesTable(
					BookmarkConstants.COLLATERAL_DTL_NAME_SUFFIX, caseInfoDtoCollateralType.getCdNameSuffix(),
					CodesConstant.CSUFFIX2);
			BookmarkDto bookmarkCdStagePersRelInt = createBookmarkWithCodesTable(
					BookmarkConstants.COLLATERAL_DTL_RELINT, caseInfoDtoCollateralType.getCdStagePersRelInt(),
					CodesConstant.CRPTRINT);
			BookmarkDto bookmarkCdStagePersRole = createBookmarkWithCodesTable(BookmarkConstants.COLLATERAL_DTL_ROLE,
					caseInfoDtoCollateralType.getCdStagePersRole(), CodesConstant.CINVROLE);
			BookmarkDto bookmarkCdStagePersType = createBookmarkWithCodesTable(BookmarkConstants.COLLATERAL_DTL_TYPE,
					caseInfoDtoCollateralType.getCdStagePersType(), CodesConstant.CPRSNTYP);
			BookmarkDto bookmarkNmNameFirstc = createBookmark(BookmarkConstants.COLLATERAL_DTL_NAME_FIRST,
					caseInfoDtoCollateralType.getNmNameFirst());
			BookmarkDto bookmarkNmNameLastc = createBookmark(BookmarkConstants.COLLATERAL_DTL_NAME_LAST,
					caseInfoDtoCollateralType.getNmNameLast());
			BookmarkDto bookmarkNmNameMiddlec = createBookmark(BookmarkConstants.COLLATERAL_DTL_NAME_MIDDLE,
					caseInfoDtoCollateralType.getNmNameMiddle());

			bookmarkCaseInfoDtoCollateralList.add(bookmarkIndPersonDobApprox);
			bookmarkCaseInfoDtoCollateralList.add(bookmarkCdPersonSex);
			bookmarkCaseInfoDtoCollateralList.add(bookmarkDtPersonBirth);
			bookmarkCaseInfoDtoCollateralList.add(bookmarkCdNameSuffixc);
			bookmarkCaseInfoDtoCollateralList.add(bookmarkCdStagePersRelInt);
			bookmarkCaseInfoDtoCollateralList.add(bookmarkCdStagePersRole);
			bookmarkCaseInfoDtoCollateralList.add(bookmarkCdStagePersType);
			bookmarkCaseInfoDtoCollateralList.add(bookmarkNmNameFirstc);
			bookmarkCaseInfoDtoCollateralList.add(bookmarkNmNameLastc);
			bookmarkCaseInfoDtoCollateralList.add(bookmarkNmNameMiddlec);

			tempCollateralFrmDataGrpDto.setBookmarkDtoList(bookmarkCaseInfoDtoCollateralList);
			formDataGroupList.add(tempCollateralFrmDataGrpDto);

			// set the prefill data for group cfzco00 - sub group of cfiv1307

			List<FormDataGroupDto> collateralFrmDataGrpFrmDataGrpList = new ArrayList<FormDataGroupDto>();
			if (FormConstants.EMPTY_STRING
					.equals(facilityInvSumDto.getEmployeePersPhNameDtoWorker().getCdNameSuffix())) {
				FormDataGroupDto tempCommaChildFrmDataGrpDtoSupv = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
						FormGroupsConstants.TMPLAT_PRINCIPAL_2);
				collateralFrmDataGrpFrmDataGrpList.add(tempCommaChildFrmDataGrpDtoSupv);
			}

			tempCollateralFrmDataGrpDto.setFormDataGroupList(collateralFrmDataGrpFrmDataGrpList);
		}
		// set the prefill data for group cfiv1302 //CLSC16D

		for (AllegationStageVictimDto allegationStageVictimDto : facilityInvSumDto.getFacilAllegPersonDto()
				.getAllegFacilPersonDto().getAllegationStageVictimDtoList()) {
			FormDataGroupDto facilAllegPersonDtoFrmDataGrpDto2 = createFormDataGroup(
					FormGroupsConstants.TMPLAT_ALLEG_DETAIL, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkFacilAllegPersonList2 = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkFacilAllegAbOffGr = createBookmark(BookmarkConstants.ALLEG_DETAIL_ON_GROUND,
					allegationStageVictimDto.getIndFacilAllegAbOffGr());
			bookmarkFacilAllegPersonList2.add(bookmarkFacilAllegAbOffGr);
			BookmarkDto bookmarkFacilAllegSupvd = createBookmark(BookmarkConstants.ALLEG_DETAIL_SUPERVISED,
					allegationStageVictimDto.getIndFacilAllegSupvd());
			bookmarkFacilAllegPersonList2.add(bookmarkFacilAllegSupvd);

			BookmarkDto bookmarkSzCdFacilAllegClssSupr = createBookmarkWithCodesTable(
					BookmarkConstants.ALLEG_DETAIL_LOCATION, allegationStageVictimDto.getCdFacilAllegClssSupr(),
					CodesConstant.CAROCOMP);
			bookmarkFacilAllegPersonList2.add(bookmarkSzCdFacilAllegClssSupr);
			BookmarkDto bookmarkSzCdNameSuffix = createBookmarkWithCodesTable(
					BookmarkConstants.ALLEG_DETAIL_VICTIM_NM_SUFFIX, allegationStageVictimDto.getCdNameSuffix(),
					CodesConstant.CSUFFIX2);
			bookmarkFacilAllegPersonList2.add(bookmarkSzCdNameSuffix);

			BookmarkDto bookmarkSzNbrFacilAllegMHMR = createBookmark(BookmarkConstants.ALLEG_DETAIL_MHMR,
					allegationStageVictimDto.getFacilAllegMHMR());
			bookmarkFacilAllegPersonList2.add(bookmarkSzNbrFacilAllegMHMR);
			BookmarkDto bookmarkSzNmNameFirst = createBookmark(BookmarkConstants.ALLEG_DETAIL_VICTIM_NM_FIRST,
					allegationStageVictimDto.getNameFirst());
			bookmarkFacilAllegPersonList2.add(bookmarkSzNmNameFirst);
			BookmarkDto bookmarkSzNmNameLast = createBookmark(BookmarkConstants.ALLEG_DETAIL_VICTIM_NM_LAST,
					allegationStageVictimDto.getNameLast());
			bookmarkFacilAllegPersonList2.add(bookmarkSzNmNameLast);
			BookmarkDto bookmarkSzNmNameMiddle = createBookmark(BookmarkConstants.ALLEG_DETAIL_VICTIM_NM_MIDDLE,
					allegationStageVictimDto.getNameMiddle());
			bookmarkFacilAllegPersonList2.add(bookmarkSzNmNameMiddle);

			facilAllegPersonDtoFrmDataGrpDto2.setBookmarkDtoList(bookmarkFacilAllegPersonList2);
			formDataGroupList.add(facilAllegPersonDtoFrmDataGrpDto2);

			// set the prefill data for group cfzco00 //CLSC16D
			List<FormDataGroupDto> facilAllegPersonDtoFrmDataGrpList2 = new ArrayList<FormDataGroupDto>();
			if (FormConstants.EMPTY_STRING.equals(allegationStageVictimDto.getCdNameSuffix())) {
				FormDataGroupDto tempCommaChildFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
						FormGroupsConstants.TMPLAT_ALLEG_DETAIL);
				facilAllegPersonDtoFrmDataGrpList2.add(tempCommaChildFrmDataGrpDto);
			}
			facilAllegPersonDtoFrmDataGrpDto2.setFormDataGroupList(facilAllegPersonDtoFrmDataGrpList2);

		}

		// set the prefill data for group cfiv1303 //CLSC17D
		FormDataGroupDto tempFacilityAllegationInfoFrmDataGrpDto = createFormDataGroup(
				FormGroupsConstants.TMPLAT_INJURY, FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkFacilityAllegationInfoList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkFacilInjuryBody = createBookmarkWithCodesTable(BookmarkConstants.INJURY_AREA,
				facilityInvSumDto.getFacilityAllegationInfoDto().getCdFacilInjuryBody(), CodesConstant.CBDYAREA);
		BookmarkDto bookmarkFacilInjuryCause = createBookmarkWithCodesTable(BookmarkConstants.INJURY_CAUSE,
				facilityInvSumDto.getFacilityAllegationInfoDto().getCdFacilInjuryCause(), CodesConstant.CCAUSINJ);
		BookmarkDto bookmarkFacilInjurySide = createBookmarkWithCodesTable(BookmarkConstants.INJURY_SIDE,
				facilityInvSumDto.getFacilityAllegationInfoDto().getCdFacilInjurySide(), CodesConstant.CSDOBODY);
		BookmarkDto bookmarkFacilInjury = createBookmarkWithCodesTable(BookmarkConstants.INJURY_ALLEGATION,
				facilityInvSumDto.getFacilityAllegationInfoDto().getCdFacilInjury(), CodesConstant.CTYPEINJ);
		BookmarkDto bookmarkPersonSuffix = createBookmarkWithCodesTable(BookmarkConstants.INJURY_VICTIM_NAME_SUFFIX,
				facilityInvSumDto.getFacilityAllegationInfoDto().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
		BookmarkDto bookmarkNmPersonFirst = createBookmark(BookmarkConstants.INJURY_VICTIM_NAME_FIRST,
				facilityInvSumDto.getFacilityAllegationInfoDto().getNmPersonFirst());
		BookmarkDto bookmarkNmPersonLast = createBookmark(BookmarkConstants.INJURY_VICTIM_NAME_LAST,
				facilityInvSumDto.getFacilityAllegationInfoDto().getNmPersonLast());
		BookmarkDto bookmarkNmPersonMiddle = createBookmark(BookmarkConstants.INJURY_VICTIM_NAME_MIDDLE,
				facilityInvSumDto.getFacilityAllegationInfoDto().getNmPersonMiddle());

		bookmarkFacilityAllegationInfoList.add(bookmarkFacilInjuryBody);
		bookmarkFacilityAllegationInfoList.add(bookmarkFacilInjuryCause);
		bookmarkFacilityAllegationInfoList.add(bookmarkFacilInjurySide);
		bookmarkFacilityAllegationInfoList.add(bookmarkFacilInjury);
		bookmarkFacilityAllegationInfoList.add(bookmarkPersonSuffix);
		bookmarkFacilityAllegationInfoList.add(bookmarkNmPersonFirst);
		bookmarkFacilityAllegationInfoList.add(bookmarkNmPersonLast);
		bookmarkFacilityAllegationInfoList.add(bookmarkNmPersonMiddle);

		tempFacilityAllegationInfoFrmDataGrpDto.setBookmarkDtoList(bookmarkFacilityAllegationInfoList);
		formDataGroupList.add(tempFacilityAllegationInfoFrmDataGrpDto);

		// set the prefill data for group cfzco00 - sub group of cfiv1303

		List<FormDataGroupDto> personSuffixFrmDataGrpList = new ArrayList<FormDataGroupDto>();
		if (FormConstants.EMPTY_STRING.equals(facilityInvSumDto.getFacilityAllegationInfoDto().getCdPersonSuffix())) {
			FormDataGroupDto tempCommaChildFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormGroupsConstants.TMPLAT_INJURY);
			personSuffixFrmDataGrpList.add(tempCommaChildFrmDataGrpDto);
		}

		// set the prefill data for group 1308 //CLSC16D
		if (!ObjectUtils.isEmpty(facilityInvSumDto.getFacilAllegPersonDto().getAllegFacilPersonDto()
				.getAllegationStageVictimDtoList().get(0).getIdAllegation())
				&& !ObjectUtils.isEmpty(facilityInvSumDto.getFacilityAllegationInfoDto().getIdAllegation())) {
			if (facilityInvSumDto.getFacilAllegPersonDto().getAllegFacilPersonDto().getAllegationStageVictimDtoList()
					.get(0).getIdAllegation() == facilityInvSumDto.getFacilityAllegationInfoDto().getIdAllegation()
							.intValue()) {
				FormDataGroupDto tempCommaChildFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SERIOUSNESS, FormGroupsConstants.TMPLAT_INJURY);
				personSuffixFrmDataGrpList.add(tempCommaChildFrmDataGrpDto);
			}
		}

		tempFacilityAllegationInfoFrmDataGrpDto.setFormDataGroupList(personSuffixFrmDataGrpList);

		// set the prefill data for group cfiv1310

		if (ServiceConstants.JUNE_7_IMPACT_DATE.after(facilityInvSumDto.getGenCaseInfoDto().getDtStageStart())) {
			FormDataGroupDto dtStageStartFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_PRIOR_JUNE,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(dtStageStartFrmDataGrpDto);

			// set the prefill data for group cfiv1311 - sub group of cfiv1310

			List<FormDataGroupDto> facilityInvDtlFrmDataGrpList = new ArrayList<FormDataGroupDto>();

			FormDataGroupDto facilityInvDtlFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_JUNE,
					FormConstants.EMPTY_STRING);

			List<BookmarkDto> bookmarkFacilityInvDtlList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkAddrFacilInvstAffZipInv = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_ZIP,
					facilityInvSumDto.getFacilInvDtlDto().getAddrFacilInvstZip());
			bookmarkFacilityInvDtlList.add(bookmarkAddrFacilInvstAffZipInv);
			BookmarkDto bookmarkAddrFacilInvstAffCityInv = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_CITY,
					facilityInvSumDto.getFacilInvDtlDto().getAddrFacilInvstCity());
			bookmarkFacilityInvDtlList.add(bookmarkAddrFacilInvstAffCityInv);
			BookmarkDto bookmarkAddrFacilInvstAffCntyInv = createBookmarkWithCodesTable(
					BookmarkConstants.SUM_FAC_ADDR_LINE_COUNTY,
					facilityInvSumDto.getFacilInvDtlDto().getAddrFacilInvstCnty(), CodesConstant.CCOUNT);
			bookmarkFacilityInvDtlList.add(bookmarkAddrFacilInvstAffCntyInv);
			BookmarkDto bookmarkAddrFacilInvstCityInv = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_CITY,
					facilityInvSumDto.getFacilInvDtlDto().getAddrFacilInvstCity());
			bookmarkFacilityInvDtlList.add(bookmarkAddrFacilInvstCityInv);
			BookmarkDto bookmarkAddrFacilInvstStateInv = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_STATE,
					facilityInvSumDto.getFacilInvDtlDto().getAddrFacilInvstState());
			bookmarkFacilityInvDtlList.add(bookmarkAddrFacilInvstStateInv);
			BookmarkDto bookmarkAddrFacilInvstStr1Inv = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_1,
					facilityInvSumDto.getFacilInvDtlDto().getAddrFacilInvstStr1());
			bookmarkFacilityInvDtlList.add(bookmarkAddrFacilInvstStr1Inv);
			BookmarkDto bookmarkAddrFacilInvstStr2Inv = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_2,
					facilityInvSumDto.getFacilInvDtlDto().getAddrFacilInvstStr2());
			bookmarkFacilityInvDtlList.add(bookmarkAddrFacilInvstStr2Inv);
			BookmarkDto bookmarkNmFacilinvstAffInv = createBookmark(BookmarkConstants.SUM_FAC_AFFILIATED,
					facilityInvSumDto.getFacilInvDtlDto().getNmFacilinvstAff());
			bookmarkFacilityInvDtlList.add(bookmarkNmFacilinvstAffInv);
			BookmarkDto bookmarkNmFacilinvstFacilityInv = createBookmark(BookmarkConstants.SUM_FAC_NAME,
					facilityInvSumDto.getFacilInvDtlDto().getNmFacilinvstFacility());
			bookmarkFacilityInvDtlList.add(bookmarkNmFacilinvstFacilityInv);

			facilityInvDtlFrmDataGrpDto.setBookmarkDtoList(bookmarkFacilityInvDtlList);
			facilityInvDtlFrmDataGrpList.add(facilityInvDtlFrmDataGrpDto);
			dtStageStartFrmDataGrpDto.setFormDataGroupList(facilityInvDtlFrmDataGrpList);

			// set the prefill data for group cfiv1314 - sub group of cfiv1311

			List<FormDataGroupDto> facilityInvstInfoFrmDataGrpList = new ArrayList<FormDataGroupDto>();

			FormDataGroupDto facilityInvstInfoFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_PRIOR_MHMR_CODE, FormConstants.EMPTY_STRING);

			List<BookmarkDto> bookmarkFacilityInvstInfoList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkCode = createBookmark(BookmarkConstants.MHMR_COMP_CODE,
					facilityInvSumDto.getFacilInvstInfoDto().getSzCdMhmrCompCode());
			bookmarkFacilityInvstInfoList.add(bookmarkCode);

			facilityInvstInfoFrmDataGrpDto.setBookmarkDtoList(bookmarkFacilityInvstInfoList);
			facilityInvstInfoFrmDataGrpList.add(facilityInvstInfoFrmDataGrpDto);
			facilityInvDtlFrmDataGrpDto.setFormDataGroupList(facilityInvstInfoFrmDataGrpList);
		}

		// set the prefill data for group cfiv1312

		if ((facilityInvSumDto.getGenCaseInfoDto().getDtStageStart().after(ServiceConstants.JUNE_7_IMPACT_DATE))) {

			FormDataGroupDto dtStagePostFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_POST_JUNE,
					FormConstants.EMPTY_STRING);

			formDataGroupList.add(dtStagePostFrmDataGrpDto);

			// set the prefill data for group cfiv1313 - sub group of cfiv1312

			List<FormDataGroupDto> facilityInvDtlFrmDataGrpList = new ArrayList<FormDataGroupDto>();

			FormDataGroupDto facilityInvDtlFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_POST_JUNE,
					FormGroupsConstants.TMPLAT_DISP_POST_JUNE);

			List<BookmarkDto> bookmarkAddressList = new ArrayList<BookmarkDto>();

			for (MultiAddressDto multiAddressDtoListElem : facilityInvSumDto.getMultiAddressDtoList()) {

				BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_CITY_POST,
						multiAddressDtoListElem.getaAddrRsrcAddrCity());
				bookmarkAddressList.add(bookmarkAddrCity);
				BookmarkDto bookmarkAddrCntyPost = createBookmarkWithCodesTable(
						BookmarkConstants.SUM_FAC_ADDR_LINE_COUNTY_POST, multiAddressDtoListElem.getaCdRsrcAddrCounty(),
						CodesConstant.CCOUNT);
				bookmarkAddressList.add(bookmarkAddrCntyPost);
				BookmarkDto bookmarkAddrSt1 = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_1_POST,
						multiAddressDtoListElem.getaAddrRsrcAddrStLn1());
				bookmarkAddressList.add(bookmarkAddrSt1);
				BookmarkDto bookmarkAddrSt2 = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_2_POST,
						multiAddressDtoListElem.getaAddrRsrcAddrStLn2());
				bookmarkAddressList.add(bookmarkAddrSt2);

				BookmarkDto bookmarkAddrState = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_STATE_POST,
						multiAddressDtoListElem.getaCdRsrcAddrState());
				bookmarkAddressList.add(bookmarkAddrState);
				BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_ZIP_POST,
						multiAddressDtoListElem.getaAddrRsrcAddrZip());
				bookmarkAddressList.add(bookmarkAddrZip);
				BookmarkDto bookmarkCode = createBookmark(BookmarkConstants.MHMR_COMP_CODE_POST,
						multiAddressDtoListElem.getaFilCdMhmrCode());
				bookmarkAddressList.add(bookmarkCode);
				BookmarkDto bookmarkNmResource = createBookmark(BookmarkConstants.SUM_FAC_NAME_POST,
						multiAddressDtoListElem.getaCpNmResource());
				bookmarkAddressList.add(bookmarkNmResource);

				facilityInvDtlFrmDataGrpDto.setBookmarkDtoList(bookmarkAddressList);
				facilityInvDtlFrmDataGrpList.add(facilityInvDtlFrmDataGrpDto);
				dtStagePostFrmDataGrpDto.setFormDataGroupList(facilityInvDtlFrmDataGrpList);

			}

		}

		// set the prefill data for group 1309

		if (FormConstants.EMPTY_STRING.equals(facilityInvSumDto.getDtContactOccured())) {
			FormDataGroupDto tempPriorReview = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_REVIEW,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempPriorReview);
		}

		// bookmarks without groups
		List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();

		// bookmarks for CSEC01D

		BookmarkDto bookmarkAddrMailCodeCity = createBookmark(BookmarkConstants.SUM_OFFICE_ADDR_CITY,
				facilityInvSumDto.getEmployeePersPhNameDtoWorker().getAddrMailCodeCity());
		bookmarkNonFormGrpList.add(bookmarkAddrMailCodeCity);
		BookmarkDto bookmarkAddrMailCodeCounty = createBookmarkWithCodesTable(BookmarkConstants.SUM_OFFICE_ADDR_COUNTY,
				facilityInvSumDto.getEmployeePersPhNameDtoWorker().getAddrMailCodeCounty(), CodesConstant.CCOUNT);
		bookmarkNonFormGrpList.add(bookmarkAddrMailCodeCounty);
		BookmarkDto bookmarkAddrMailCodeStLn1 = createBookmark(BookmarkConstants.SUM_OFFICE_ADDR_LINE_1,
				facilityInvSumDto.getEmployeePersPhNameDtoWorker().getAddrMailCodeStLn1());
		bookmarkNonFormGrpList.add(bookmarkAddrMailCodeStLn1);
		BookmarkDto bookmarkAddrMailCodeStLn2 = createBookmark(BookmarkConstants.SUM_OFFICE_ADDR_LINE_2,
				facilityInvSumDto.getEmployeePersPhNameDtoWorker().getAddrMailCodeStLn2());
		bookmarkNonFormGrpList.add(bookmarkAddrMailCodeStLn2);
		BookmarkDto bookmarkAddrMailCodeZip = createBookmark(BookmarkConstants.SUM_OFFICE_ADDR_ZIP,
				facilityInvSumDto.getEmployeePersPhNameDtoWorker().getAddrMailCodeZip());
		bookmarkNonFormGrpList.add(bookmarkAddrMailCodeZip);
		BookmarkDto bookmarkCdNameSuffixWorker = createBookmarkWithCodesTable(BookmarkConstants.SUM_WORKER_NAME_SUFFIX,
				facilityInvSumDto.getEmployeePersPhNameDtoWorker().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFormGrpList.add(bookmarkCdNameSuffixWorker);
		BookmarkDto bookmarkNmNameFirst = createBookmark(BookmarkConstants.SUM_WORKER_NAME_FIRST,
				facilityInvSumDto.getEmployeePersPhNameDtoWorker().getNmNameFirst());
		bookmarkNonFormGrpList.add(bookmarkNmNameFirst);
		BookmarkDto bookmarkNmNameLast = createBookmark(BookmarkConstants.SUM_WORKER_NAME_LAST,
				facilityInvSumDto.getEmployeePersPhNameDtoWorker().getNmNameLast());
		bookmarkNonFormGrpList.add(bookmarkNmNameLast);
		BookmarkDto bookmarkNmNameMiddle = createBookmark(BookmarkConstants.SUM_WORKER_NAME_MIDDLE,
				facilityInvSumDto.getEmployeePersPhNameDtoWorker().getNmNameMiddle());
		bookmarkNonFormGrpList.add(bookmarkNmNameMiddle);
		BookmarkDto bookmarkCdNameSuffixSupv = createBookmarkWithCodesTable(
				BookmarkConstants.SUM_SUPERVISOR_NAME_SUFFIX,
				facilityInvSumDto.getEmployeePersPhNameDtoSupv().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFormGrpList.add(bookmarkCdNameSuffixSupv);
		BookmarkDto bookmarkNmNameFirstSupv = createBookmark(BookmarkConstants.SUM_SUPERVISOR_NAME_FIRST,
				facilityInvSumDto.getEmployeePersPhNameDtoSupv().getNmNameFirst());
		bookmarkNonFormGrpList.add(bookmarkNmNameFirstSupv);
		BookmarkDto bookmarkNmNameLastSupv = createBookmark(BookmarkConstants.SUM_SUPERVISOR_NAME_LAST,
				facilityInvSumDto.getEmployeePersPhNameDtoSupv().getNmNameLast());
		bookmarkNonFormGrpList.add(bookmarkNmNameLastSupv);
		BookmarkDto bookmarkNmNameMiddleSupv = createBookmark(BookmarkConstants.SUM_SUPERVISOR_NAME_MIDDLE,
				facilityInvSumDto.getEmployeePersPhNameDtoSupv().getNmNameMiddle());
		bookmarkNonFormGrpList.add(bookmarkNmNameMiddleSupv);

		// bookmarks for CSEC02D
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				facilityInvSumDto.getGenCaseInfoDto().getNmCase());
		bookmarkNonFormGrpList.add(bookmarkNmCase);
		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				facilityInvSumDto.getGenCaseInfoDto().getIdCase());
		bookmarkNonFormGrpList.add(bookmarkIdCase);

		// bookmarks for CSES39D
		BookmarkDto bookmarkDtFacilInvstBegun = createBookmark(BookmarkConstants.SUM_DT_INVEST_BEGUN,
				DateUtils.stringDt(facilityInvSumDto.getFacilInvDtlDto().getDtFacilInvstBegun()));
		bookmarkNonFormGrpList.add(bookmarkDtFacilInvstBegun);
		BookmarkDto bookmarkDtFacilInvstComplt = createBookmark(BookmarkConstants.SUM_DT_INVEST_COMPLETED,
				DateUtils.stringDt(facilityInvSumDto.getFacilInvDtlDto().getDtFacilInvstComplt()));
		bookmarkNonFormGrpList.add(bookmarkDtFacilInvstComplt);
		BookmarkDto bookmarkDtFacilInvstIncident = createBookmark(BookmarkConstants.SUM_DT_INCIDENT_OCC,
				DateUtils.stringDt(facilityInvSumDto.getFacilInvDtlDto().getDtFacilInvstIncident()));
		bookmarkNonFormGrpList.add(bookmarkDtFacilInvstIncident);
		BookmarkDto bookmarkDtFacilInvstIntake = createBookmark(BookmarkConstants.SUM_DT_INTAKE_REC,
				DateUtils.stringDt(facilityInvSumDto.getFacilInvDtlDto().getDtFacilInvstIntake()));
		bookmarkNonFormGrpList.add(bookmarkDtFacilInvstIntake);
		BookmarkDto bookmarkAddrFacilInvstAffZip = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_ZIP,
				facilityInvSumDto.getFacilInvDtlDto().getAddrFacilInvstZip());
		bookmarkNonFormGrpList.add(bookmarkAddrFacilInvstAffZip);
		BookmarkDto bookmarkAddrFacilInvstAffCity = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_CITY,
				facilityInvSumDto.getFacilInvDtlDto().getAddrFacilInvstCity());
		bookmarkNonFormGrpList.add(bookmarkAddrFacilInvstAffCity);
		BookmarkDto bookmarkAddrFacilInvstAffCnty = createBookmarkWithCodesTable(
				BookmarkConstants.SUM_FAC_ADDR_LINE_COUNTY,
				facilityInvSumDto.getFacilInvDtlDto().getAddrFacilInvstCnty(), CodesConstant.CCOUNT);
		bookmarkNonFormGrpList.add(bookmarkAddrFacilInvstAffCnty);
		BookmarkDto bookmarkAddrFacilInvstState = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_STATE,
				facilityInvSumDto.getFacilInvDtlDto().getAddrFacilInvstState());
		bookmarkNonFormGrpList.add(bookmarkAddrFacilInvstState);
		BookmarkDto bookmarkAddrFacilInvstStr1 = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_1,
				facilityInvSumDto.getFacilInvDtlDto().getAddrFacilInvstStr1());
		bookmarkNonFormGrpList.add(bookmarkAddrFacilInvstStr1);
		BookmarkDto bookmarkAddrFacilInvstStr2 = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_2,
				facilityInvSumDto.getFacilInvDtlDto().getAddrFacilInvstStr2());
		bookmarkNonFormGrpList.add(bookmarkAddrFacilInvstStr2);
		BookmarkDto bookmarkNmFacilinvstAff = createBookmark(BookmarkConstants.SUM_FAC_AFFILIATED,
				facilityInvSumDto.getFacilInvDtlDto().getNmFacilinvstAff());
		bookmarkNonFormGrpList.add(bookmarkNmFacilinvstAff);
		BookmarkDto bookmarkNmFacilinvstFacility = createBookmark(BookmarkConstants.SUM_FAC_NAME,
				facilityInvSumDto.getFacilInvDtlDto().getNmFacilinvstFacility());
		bookmarkNonFormGrpList.add(bookmarkNmFacilinvstFacility);
		if(!ObjectUtils.isEmpty(facilityInvSumDto.getFacilInvDtlDto().getDtFacilInvstBegun()))
		{
		BookmarkDto bookmarkFacilInvstBegun = createBookmark(BookmarkConstants.SUM_TM_INVEST_BEGUN,
				DateUtils.getTime(facilityInvSumDto.getFacilInvDtlDto().getDtFacilInvstBegun()));
		bookmarkNonFormGrpList.add(bookmarkFacilInvstBegun);
		}
		if (!ObjectUtils.isEmpty(facilityInvSumDto.getFacilInvDtlDto().getDtFacilInvstIncident())) 
		{
			BookmarkDto bookmarkFacilInvstIncident = createBookmark(BookmarkConstants.SUM_TM_INCIDENT_OCC,
					DateUtils.getTime(facilityInvSumDto.getFacilInvDtlDto().getDtFacilInvstIncident()));
			bookmarkNonFormGrpList.add(bookmarkFacilInvstIncident);
		}
		if (!ObjectUtils.isEmpty(facilityInvSumDto.getFacilInvDtlDto().getDtFacilInvstIntake())) 
		{
		BookmarkDto bookmarkFacilInvstIntake = createBookmark(BookmarkConstants.SUM_TM_INTAKE_REC,
				DateUtils.getTime(facilityInvSumDto.getFacilInvDtlDto().getDtFacilInvstIntake()));
		bookmarkNonFormGrpList.add(bookmarkFacilInvstIntake);
		}

		// bookmarks for CINV17D
		BookmarkDto bookmarkMhmrCompCode = createBookmark(BookmarkConstants.MHMR_COMP_CODE,
				facilityInvSumDto.getFacilInvstInfoDto().getSzCdMhmrCompCode());
		bookmarkNonFormGrpList.add(bookmarkMhmrCompCode);

		// bookmarks for CINVB8D
		if (!ObjectUtils.isEmpty(facilityInvSumDto.getDtContactOccured())) {
			BookmarkDto bookmarkDtContactOccured = createBookmark(BookmarkConstants.DATE_CONTACT_OCCURRED,
					DateUtils.stringDt(facilityInvSumDto.getDtContactOccured()));
			bookmarkNonFormGrpList.add(bookmarkDtContactOccured);
			BookmarkDto bookmarkContactOccured = createBookmark(BookmarkConstants.TIME_REQUEST_REVIEW,
					DateUtils.getTime(facilityInvSumDto.getDtContactOccured()));
			bookmarkNonFormGrpList.add(bookmarkContactOccured);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);
		return preFillData;
	}

}
