package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dto.PersonRaceOutDto;
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
import us.tx.state.dfps.service.icpforms.dto.IcpPersonDto;
import us.tx.state.dfps.service.icpforms.dto.IcpReqDto;
import us.tx.state.dfps.service.icpforms.dto.IcpRsrcEntityDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:SubcareCasePrefillData will implement returnPrefillData operation
 * defined in DocumentServiceUtil Interface to populate the prefill data for
 * Form icp01o00 Apr 9, 2018- 10:53:14 AM © 2017 Texas Department of Family and
 * Protective Services
 */
@Component
public class IcpReqPrefillData extends DocumentServiceUtil {

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
		IcpReqDto icpReqDto = (IcpReqDto) parentDtoobj;

		if (ObjectUtils.isEmpty(icpReqDto.getIcpRsrcEntityDtoList())) {
			icpReqDto.setIcpRsrcEntityDtoList(new ArrayList<IcpRsrcEntityDto>());
		}
		if (ObjectUtils.isEmpty(icpReqDto.getPersonRaceOutDtoList())) {
			icpReqDto.setPersonRaceOutDtoList(new ArrayList<PersonRaceOutDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// ********************Ethnicity: Hispanic Origin:
		// ****************************************** //

		// parent group icp0109
		if (ServiceConstants.HS.equals(icpReqDto.getIcpPersonDtoF5().getCdEthnicity())) {
			FormDataGroupDto tempHispYesGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HISPANIC_YES,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempHispYesGroupDto);
		}

		// parent group icp0110
		if (ServiceConstants.NH.equals(icpReqDto.getIcpPersonDtoF5().getCdEthnicity())) {
			FormDataGroupDto tempHispNoGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HISPANIC_NO,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempHispNoGroupDto);
		}

		// parent group icp0111
		if (ServiceConstants.UT.equals(icpReqDto.getIcpPersonDtoF5().getCdEthnicity())) {
			FormDataGroupDto tempHispUnKnownGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HISPANIC_UNKNOWN,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempHispUnKnownGroupDto);
		}

		// parent group icp0179
		if (ServiceConstants.DC.equals(icpReqDto.getIcpPersonDtoF5().getCdEthnicity())) {
			FormDataGroupDto tempHispDecGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HISPANIC_DECLINED,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempHispDecGroupDto);
		}

		// ********************Ethnicity: Hispanic Origin:
		// ****************************************** //

		// ******************************************ICWA Eligible
		// ****************************************** //

		// parent group icp0112
		if (ServiceConstants.YES.equals(icpReqDto.getIcpPersonDtoF1().getIndEligible())) {
			FormDataGroupDto tempIcwaYesGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ICWA_YES,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempIcwaYesGroupDto);
		}

		// parent group icp0113
		if (ServiceConstants.NO.equals(icpReqDto.getIcpPersonDtoF1().getIndEligible())) {
			FormDataGroupDto tempIcwaNoGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ICWA_NO,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempIcwaNoGroupDto);
		}

		// ******************************************ICWA Eligible
		// ****************************************** //

		// ***********************************************************Race
		// ************************************ //

		for (PersonRaceOutDto personRaceOutDto : icpReqDto.getPersonRaceOutDtoList()) {
			// parent group icp0114
			if (ServiceConstants.AN.equals(personRaceOutDto.getCdPersonRace())) {
				FormDataGroupDto tempAsianGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ASIAN,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(tempAsianGroupDto);
			}

			// parent group icp0115
			if (ServiceConstants.BK.equals(personRaceOutDto.getCdPersonRace())) {
				FormDataGroupDto tempAfricanGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_AFRICAN,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(tempAfricanGroupDto);
			}

			// parent group icp0116
			if (ServiceConstants.WT.equals(personRaceOutDto.getCdPersonRace())) {
				FormDataGroupDto tempWhiteGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WHITE,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(tempWhiteGroupDto);
			}

			// parent group icp0117
			if (ServiceConstants.AA.equals(personRaceOutDto.getCdPersonRace())) {
				FormDataGroupDto tempAmeriIndianGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_AMERI_INDIAN,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(tempAmeriIndianGroupDto);
			}

			// parent group icp0118
			if (ServiceConstants.PRIMARY_ROLE_STAGE_CLOSED.equals(personRaceOutDto.getCdPersonRace())) {
				FormDataGroupDto tempNativeHawaiGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NATIVE_HAWAII,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(tempNativeHawaiGroupDto);
			}

			// parent group icp0180
			if (ServiceConstants.DC.equals(personRaceOutDto.getCdPersonRace())) {
				FormDataGroupDto tempIndDeclGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_IND_DECL,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(tempIndDeclGroupDto);
			}

			// parent group icp0181
			if (ServiceConstants.UD.equals(personRaceOutDto.getCdPersonRace())) {
				FormDataGroupDto tempUnableGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_UNABLE,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(tempUnableGroupDto);
			}

		}

		// ***********************************************************Race
		// ************************************ //

		// ***********************Title IV-E
		// Determination:*****************************************************//

		// parent group icp0119
		if (ServiceConstants.YES.equals(icpReqDto.getIcpPersonDtoF1().getIndTitle())) {
			FormDataGroupDto tempIveYesGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_IVE_YES,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempIveYesGroupDto);
		}

		// parent group icp0120
		if (ServiceConstants.NO.equals(icpReqDto.getIcpPersonDtoF1().getIndTitle())) {
			FormDataGroupDto tempIveNoGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_IVE_NO,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempIveNoGroupDto);
		}

		// parent group icp0121
		if (ServiceConstants.TO_DO_TYPE_1.equals(icpReqDto.getIcpPersonDtoF1().getIndTitle())) {
			FormDataGroupDto tempIvePendGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_IVE_PEND,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempIvePendGroupDto);
		}

		// ***********************Title IV-E
		// Determination:*****************************************************//

		// ***********************************Parent 1: Parent
		// 2:*********************************************//

		for (IcpPersonDto icpPersonDto : icpReqDto.getIcpPersonDto0()) {

			// parent group icp0101
			if (ServiceConstants.CPL_10.equals(icpPersonDto.getCdPersonType())) {
				FormDataGroupDto tempNmPrnt1GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_PARENT_1,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkNmPrnt1List = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkNmNmPrnt1 = createBookmark(BookmarkConstants.NM_PARENT_1,
						icpPersonDto.getNmPersonFull());
				bookmarkNmPrnt1List.add(bookmarkNmNmPrnt1);
				tempNmPrnt1GroupDto.setBookmarkDtoList(bookmarkNmPrnt1List);
				formDataGroupList.add(tempNmPrnt1GroupDto);
			}

			// parent group icp0102
			if (ServiceConstants.CPL_30.equals(icpPersonDto.getCdPersonType())) {
				FormDataGroupDto tempNmPrnt2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_PARENT_2,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkNmPrnt2List = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkNmNmPrnt2 = createBookmark(BookmarkConstants.NM_PARENT_2,
						icpPersonDto.getNmPersonFull());
				bookmarkNmPrnt2List.add(bookmarkNmNmPrnt2);
				tempNmPrnt2GroupDto.setBookmarkDtoList(bookmarkNmPrnt2List);
				formDataGroupList.add(tempNmPrnt2GroupDto);
			}
		}

		// ***********************************Parent 1: Parent
		// 2:*********************************************//

		// *** Name of Agency Or Person Responsible For Planning For
		// Child:*****************************************//

		// parent group icp0103
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpReqDto.getIcpRsrcEntityDtoList()) {
			if (ServiceConstants.CPL_20.equals(icpRsrcEntityDto.getCdType())) {
				FormDataGroupDto tempNmAgncyPlnGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_AGNCY_PLAN,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkNmAgncyPlnList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkNmEntity = createBookmark(BookmarkConstants.NM_AGNCY_PLAN,
						icpRsrcEntityDto.getNmEntity());
				bookmarkNmAgncyPlnList.add(bookmarkNmEntity);
				tempNmAgncyPlnGroupDto.setBookmarkDtoList(bookmarkNmAgncyPlnList);
				formDataGroupList.add(tempNmAgncyPlnGroupDto);

			}
		}

		for (IcpPersonDto icpPersonDto : icpReqDto.getIcpPersonDtoH5()) {

			/*
			 * // parent group icp0159 if
			 * (ServiceConstants.CFACTYP2_50.equals(icpPersonDto.getCdPersonType
			 * ())) { FormDataGroupDto tempCommaAgncyPlanGroupDto =
			 * createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_AGNCY_PLAN,
			 * FormConstants.EMPTY_STRING);
			 * formDataGroupList.add(tempCommaAgncyPlanGroupDto); }
			 */

			// parent group icp0160
			if (ServiceConstants.CFACTYP2_50.equals(icpPersonDto.getCdPersonType())) {
				FormDataGroupDto tempNmPersPlanGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_PERS_PLAN,
						FormConstants.EMPTY_STRING);

				List<BookmarkDto> bookmarkNmPersPlanList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkNmPersPlan = createBookmark(BookmarkConstants.NM_PERS_PLAN,
						icpPersonDto.getNmPersonFull());
				bookmarkNmPersPlanList.add(bookmarkNmPersPlan);
				tempNmPersPlanGroupDto.setBookmarkDtoList(bookmarkNmPersPlanList);
				formDataGroupList.add(tempNmPersPlanGroupDto);
			}

		}
		// *** Name of Agency Or Person Responsible For Planning For
		// Child:*****************************************//

		// PUBLIC Placement Checkbox for icp01o00
		if (ServiceConstants.YES.equals(icpReqDto.getIcpPersonDtoF1().getIndPublicOrPrivate())) {
			FormDataGroupDto publicPlacement = createFormDataGroup(FormGroupsConstants.TMPLAT_PUBLIC_PLACEMENT,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(publicPlacement);
		}

		// PRIVATE Placement Checkbox for icp01o00
		if (ServiceConstants.NO.equals(icpReqDto.getIcpPersonDtoF1().getIndPublicOrPrivate())) {
			FormDataGroupDto privatePlacement = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIVATE_PLACEMENT,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(privatePlacement);
		}

		// SUBSIDY dropdown for icp01o00
		if (CodesConstant.ICPCHSSB_10.equals(icpReqDto.getIcpPersonDtoF1().getCdSubsidy())){
			FormDataGroupDto tempSubsidyIVE = createFormDataGroup(
					FormGroupsConstants.TMPLAT_SUBSIDY_IV_E, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempSubsidyIVE);
		}

		// SUBSIDY dropdown for icp01o00
		if (CodesConstant.ICPCHSSB_20.equals(icpReqDto.getIcpPersonDtoF1().getCdSubsidy())){
			FormDataGroupDto tempSubsidyNonIVE = createFormDataGroup(
					FormGroupsConstants.TMPLAT_SUBSIDY_NON_IV_E, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempSubsidyNonIVE);
		}

		// SUBSIDY dropdown for icp01o00
		if (CodesConstant.ICPCHSSB_30.equals(icpReqDto.getIcpPersonDtoF1().getCdSubsidy())){
			FormDataGroupDto tempFstFmlyCareGroupDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_SUBSIDY_PENDING, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempFstFmlyCareGroupDto);
		}

		// SUBSIDY dropdown for icp01o00
		if (CodesConstant.ICPCHSSB_40.equals(icpReqDto.getIcpPersonDtoF1().getCdSubsidy())){
			FormDataGroupDto tempFstFmlyCareGroupDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_SUBSIDY_NONE, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempFstFmlyCareGroupDto);
		}

		// parent group icp0122
		if (ServiceConstants.CCH_40.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())) {
			FormDataGroupDto tempFstFmlyCareGroupDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_FOSTER_FAMILY_CARE, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempFstFmlyCareGroupDto);
		}

		// parent group icp0123
		if (ServiceConstants.CFACTYP2_50.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())) {
			FormDataGroupDto tempGrpHomeCareGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_GROUP_HOME_CARE,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempGrpHomeCareGroupDto);
		}

		// parent group icp0124
		if (ServiceConstants.CCH_90.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())) {
			FormDataGroupDto tempResTreatCenterGroupDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_RES_TREAT_CENTER, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempResTreatCenterGroupDto);
		}

		// parent group icp0125
		if (ServiceConstants.CPL_30.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())) {
			FormDataGroupDto tempChldCareInstitGroupDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_CHILD_CARE_INSTIT, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempChldCareInstitGroupDto);
		}

		// parent group icp0126
		if (ServiceConstants.CPL_60.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())) {
			FormDataGroupDto tempInstitCareGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_INSTIT_CARE,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempInstitCareGroupDto);
		}

		// parent group icp0127
		if (ServiceConstants.CPL_70.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())) {
			FormDataGroupDto tempPrntTypeGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PARENT_TYPE,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempPrntTypeGroupDto);
		}

		// parent group icp0128
		if (ServiceConstants.CPL_80.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())) {
			FormDataGroupDto tempRelTypeGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RELATIVE_TYPE,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempRelTypeGroupDto);
		}

		// parent group icp0129
		if (ServiceConstants.RETURN_CODE_05.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())) {
			FormDataGroupDto tempAdptPrivateGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADOPTION_PRIVATE,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempAdptPrivateGroupDto);
		}

		// parent group icp0130
		if (ServiceConstants.CPL_03.equals(icpReqDto.getIcpPersonDtoF1()
				.getCdCareType()) || Arrays.asList(ServiceConstants.CPL_30, ServiceConstants.CPL_40)
				.contains(icpReqDto.getIcpPersonDtoF1()
						.getCdSubsidy())) {
			FormDataGroupDto tempAdptNoSubGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADOPTION_NO_SUBSIDY,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempAdptNoSubGroupDto);
		}

		// parent group icp0131
		if (ServiceConstants.CPL_20.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
				|| ServiceConstants.ICPCCRTP_100.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())) {
			FormDataGroupDto tempSendStateGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SENDING_STATE,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempSendStateGroupDto);
		}

		// parent group icp0132
		if (ServiceConstants.CPL_10.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
			|| ServiceConstants.ICPCCRTP_110.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())) {
			FormDataGroupDto tempRecStateGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RECEIVING_STATE,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempRecStateGroupDto);
		}

		// parent group icp0133
		if (ServiceConstants.CFACTYP2_50.equals(icpReqDto.getIcpPersonDtoF1().getCdLegalStatus())) {
			FormDataGroupDto tempSendAgncyCusGroupDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_SEND_AGNCY_CUSTODY, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempSendAgncyCusGroupDto);
		}

		// parent group icp0134
		if (ServiceConstants.CPL_30.equals(icpReqDto.getIcpPersonDtoF1().getCdLegalStatus())) {
			FormDataGroupDto tempPrntRelGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PARENT_REL_CUSTODY,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempPrntRelGroupDto);
		}

		// parent group icp0135
		if (ServiceConstants.CPL_20.equals(icpReqDto.getIcpPersonDtoF1().getCdLegalStatus())) {
			FormDataGroupDto tempCrtJurisGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COURT_JURIS,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempCrtJurisGroupDto);
		}

		// parent group icp0136
		if (ServiceConstants.CPL_40.equals(icpReqDto.getIcpPersonDtoF1().getCdLegalStatus())) {
			FormDataGroupDto tempPrntRightTermGroupDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_PARENT_RIGHT_TERM, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempPrntRightTermGroupDto);
		}

		// parent group icp0137
		if (ServiceConstants.CPL_60.equals(icpReqDto.getIcpPersonDtoF1().getCdLegalStatus())) {
			FormDataGroupDto tempRefMinorGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_REFUGEE_MINOR,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempRefMinorGroupDto);
		}

		// parent group icp0138
		if (ServiceConstants.CPL_30.equals(icpReqDto.getIcpPersonDtoF1().getCdLegalStatus())) {
			FormDataGroupDto tempPrntHmStdyGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PARENT_HM_STUDY,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempPrntHmStdyGroupDto);
		}

		// parent group icp0139
		if (ServiceConstants.CPL_40.equals(icpReqDto.getIcpPersonDtoF1().getCdInitReport())) {
			FormDataGroupDto tempRelHmStdyGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_REL_HM_STUDY,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempRelHmStdyGroupDto);
		}

		// parent group icp0140
		if (ServiceConstants.CPL_10.equals(icpReqDto.getIcpPersonDtoF1().getCdInitReport())) {
			FormDataGroupDto tempAdoptHmStdyGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADOPT_HM_STUDY,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempAdoptHmStdyGroupDto);
		}

		// parent group icp0141
		if (ServiceConstants.CPL_20.equals(icpReqDto.getIcpPersonDtoF1().getCdInitReport())) {
			FormDataGroupDto tempFosterHmStudyGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FOSTER_HM_STUDY,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempFosterHmStudyGroupDto);
		}

		// parent group icp0142
		if (ServiceConstants.CPL_30.equals(icpReqDto.getIcpPersonDtoF1().getCdSprvSrvc())) {
			FormDataGroupDto tempRecAgncySuprGroupDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_RECEIVE_AGNCY_SUPERV, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempRecAgncySuprGroupDto);
		}

		// parent group icp0143
		if (ServiceConstants.CPL_10.equals(icpReqDto.getIcpPersonDtoF1().getCdSprvSrvc())) {
			FormDataGroupDto tempAnotherAgncySupGroupDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_ANOTHER_AGNCY_SUP, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempAnotherAgncySupGroupDto);
		}

		// parent group icp0144
		if (ServiceConstants.CPL_40.equals(icpReqDto.getIcpPersonDtoF1().getCdSprvSrvc())) {
			FormDataGroupDto tempSendAgncySupGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SEND_AGNCY_SUP,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempSendAgncySupGroupDto);
		}

		// parent group icp0145
		if (ServiceConstants.CPL_20.equals(icpReqDto.getIcpPersonDtoF1().getCdSprvSrvc())) {
			FormDataGroupDto tempInstSupGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_INSTITUTE_SUP,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempInstSupGroupDto);
		}

		// parent group icp0146
		if (ServiceConstants.CPL_30.equals(icpReqDto.getIcpPersonDtoF1().getCdSprvRpt())) {
			FormDataGroupDto tempSemiAnSupGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SEMI_ANUAL_SUP,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempSemiAnSupGroupDto);
		}

		// parent group icp0147
		if (ServiceConstants.CPL_20.equals(icpReqDto.getIcpPersonDtoF1().getCdSprvRpt())) {
			FormDataGroupDto tempQrtyGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_QUARTERLY,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempQrtyGroupDto);
		}

		// parent group icp0148
		if (ServiceConstants.CPL_40.equals(icpReqDto.getIcpPersonDtoF1().getCdSprvRpt())) {
			FormDataGroupDto tempUponReqGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_UPON_REQUEST,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempUponReqGroupDto);
		}

		// parent group icp0149
		if (ServiceConstants.CPL_10.equals(icpReqDto.getIcpPersonDtoF1().getCdSprvRpt())) {
			FormDataGroupDto tempMnSupGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MONTHLY_SUP,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempMnSupGroupDto);
		}

		// parent group icp0150
		if (ServiceConstants.CFACTYP2_50.equals(icpReqDto.getIcpPersonDtoF1().getCdSprvRpt())) {
			FormDataGroupDto tempNotAplicSupGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NOT_APLIC_SUP,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempNotAplicSupGroupDto);
		}

		// parent group icp0155
		if (ServiceConstants.CPL_20.equals(icpReqDto.getIcpPersonDtoF1().getCdDecision())) {
			FormDataGroupDto tempPlcmntAprvCondGroupDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_PLCMT_APRV_COND, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempPlcmntAprvCondGroupDto);
		}

		// parent group icp0156
		if (ServiceConstants.CPL_10.equals(icpReqDto.getIcpPersonDtoF1().getCdDecision())) {
			FormDataGroupDto tempPlcmntAprvGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PLCMT_APRV,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempPlcmntAprvGroupDto);
		}

		// parent group icp0157
		if (ServiceConstants.CPL_30.equals(icpReqDto.getIcpPersonDtoF1().getCdDecision())) {
			FormDataGroupDto tempPlcmntDenGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PLCMT_DENIED,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempPlcmntDenGroupDto);
		}

		// parent group icp0178
		if (ServiceConstants.CPL_10.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
				|| ServiceConstants.CPL_20.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
				|| ServiceConstants.CPL_03.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
				|| ServiceConstants.RETURN_CODE_05.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
				|| ServiceConstants.ICPCCRTP_100.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
				|| ServiceConstants.ICPCCRTP_110.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())) {
			FormDataGroupDto tempAdptTypeGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADOPT_TYPE,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempAdptTypeGroupDto);
		}

		// parent group icp0104
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpReqDto.getIcpRsrcEntityDtoList()) {
			if (ServiceConstants.CPL_20.equals(icpRsrcEntityDto.getCdType())
					&& !ServiceConstants.TEXAS.equals(icpRsrcEntityDto.getNmEntity())) {
				FormDataGroupDto tempPlnAgncyPhNoGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PLAN_AGNCY_PHONE, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPlnAgncyPhNoList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPlnAgncyPhNo = createBookmark(BookmarkConstants.PLAN_AGNCY_PHONE,
						TypeConvUtil.formatPhone(icpRsrcEntityDto.getNbrPhone().toString()));
				bookmarkPlnAgncyPhNoList.add(bookmarkPlnAgncyPhNo);
				tempPlnAgncyPhNoGroupDto.setBookmarkDtoList(bookmarkPlnAgncyPhNoList);
				formDataGroupList.add(tempPlnAgncyPhNoGroupDto);
			}
		}

		// parent group icp0105
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpReqDto.getIcpRsrcEntityDtoList()) {
			if (ServiceConstants.CPL_20.equals(icpRsrcEntityDto.getCdType())
					&& !ServiceConstants.TEXAS.equals(icpRsrcEntityDto.getNmEntity())) {
				FormDataGroupDto tempPlnAddGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PLAN_ADD,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPlnAddList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.PLN_AGNCY_ADD_CITY,
						icpRsrcEntityDto.getAddrCity());
				bookmarkPlnAddList.add(bookmarkAddrCity);
				BookmarkDto bookmarkAddrStLn1 = createBookmark(BookmarkConstants.PLN_AGNCY_ADD_LN1,
						!StringUtils.isEmpty(icpRsrcEntityDto.getAddrRsrcStLn1()) ? icpRsrcEntityDto.getAddrRsrcStLn1() : icpRsrcEntityDto.getAddrStLn1());
				bookmarkPlnAddList.add(bookmarkAddrStLn1);
				BookmarkDto bookmarkAddrStLn2 = createBookmark(BookmarkConstants.PLN_AGNCY_ADD_LN2,
						!StringUtils.isEmpty(icpRsrcEntityDto.getAddrRsrcStLn2()) ? icpRsrcEntityDto.getAddrRsrcStLn2() : icpRsrcEntityDto.getAddrStLn2());
				bookmarkPlnAddList.add(bookmarkAddrStLn2);
				BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.PLN_AGNCY_ADD_ZIP,
						icpRsrcEntityDto.getAddrZip());
				bookmarkPlnAddList.add(bookmarkAddrZip);
				BookmarkDto bookmarkState = createBookmark(BookmarkConstants.PLN_AGNCY_ADD_STATE,
						icpRsrcEntityDto.getCdState());
				bookmarkPlnAddList.add(bookmarkState);
				tempPlnAddGroupDto.setBookmarkDtoList(bookmarkPlnAddList);
				formDataGroupList.add(tempPlnAddGroupDto);
			}
		}

		// parent group icp0106
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpReqDto.getIcpRsrcEntityDtoList()) {
			if (ServiceConstants.CPL_10.equals(icpRsrcEntityDto.getCdType())) {
				FormDataGroupDto tempNmAgncyFinGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_AGNCY_FIN,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkNmAgncyFinList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkNmAgncyFin = createBookmark(BookmarkConstants.NM_AGNCY_FINANCE,
						icpRsrcEntityDto.getNmEntity());
				bookmarkNmAgncyFinList.add(bookmarkNmAgncyFin);
				tempNmAgncyFinGroupDto.setBookmarkDtoList(bookmarkNmAgncyFinList);
				formDataGroupList.add(tempNmAgncyFinGroupDto);
			}
		}

		for (IcpPersonDto icpPersonDto : icpReqDto.getIcpPersonDtoH5()) {

			/*
			 * // parent group icp0165 if
			 * (ServiceConstants.CPL_40.equals(icpPersonDto.getCdPersonType()))
			 * { FormDataGroupDto tempCommaGroupDto =
			 * createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
			 * FormConstants.EMPTY_STRING);
			 * formDataGroupList.add(tempCommaGroupDto); }
			 */

			// parent group icp0166
			if (ServiceConstants.CPL_40.equals(icpPersonDto.getCdPersonType())) {
				FormDataGroupDto tempNmPersFinGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_PERS_FIN,
						FormConstants.EMPTY_STRING);

				List<BookmarkDto> bookmarkNmPersFinList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkNmPersFin = createBookmark(BookmarkConstants.NM_PERS_FINANCE,
						icpPersonDto.getNmPersonFull());
				bookmarkNmPersFinList.add(bookmarkNmPersFin);
				tempNmPersFinGroupDto.setBookmarkDtoList(bookmarkNmPersFinList);
				formDataGroupList.add(tempNmPersFinGroupDto);
			}

		}

		// parent group icp0107
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpReqDto.getIcpRsrcEntityDtoList()) {
			if (ServiceConstants.CPL_10.equals(icpRsrcEntityDto.getCdType())
					&& !ServiceConstants.TEXAS.equals(icpRsrcEntityDto.getNmEntity())) {
				
				if(!ObjectUtils.isEmpty(icpRsrcEntityDto.getNbrPhone()))
				{
				FormDataGroupDto tempFinPhGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FIN_PHONE,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkFinPhList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkFinPh = createBookmark(BookmarkConstants.FINANCE_AGNCY_PHONE,
						TypeConvUtil.formatPhone(icpRsrcEntityDto.getNbrPhone().toString()));
				bookmarkFinPhList.add(bookmarkFinPh);
				tempFinPhGroupDto.setBookmarkDtoList(bookmarkFinPhList);
				formDataGroupList.add(tempFinPhGroupDto);
				}
			}
		}

		// parent group icp0108
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpReqDto.getIcpRsrcEntityDtoList()) {
			if (ServiceConstants.CPL_10.equals(icpRsrcEntityDto.getCdType())
					&& !ServiceConstants.TEXAS.equals(icpRsrcEntityDto.getNmEntity())) {
				FormDataGroupDto tempFinAddGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FIN_ADD,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkFinAddList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.FIN_AGNCY_ADD_CITY,
						icpRsrcEntityDto.getAddrCity());
				bookmarkFinAddList.add(bookmarkAddrCity);
				BookmarkDto bookmarkAddrStLn1 = createBookmark(BookmarkConstants.FIN_AGNCY_ADD_LN1,
						!StringUtils.isEmpty(icpRsrcEntityDto.getAddrRsrcStLn1()) ? icpRsrcEntityDto.getAddrRsrcStLn1() : icpRsrcEntityDto.getAddrStLn1());
				bookmarkFinAddList.add(bookmarkAddrStLn1);
				BookmarkDto bookmarkAddrStLn2 = createBookmark(BookmarkConstants.FIN_AGNCY_ADD_LN2,
						!StringUtils.isEmpty(icpRsrcEntityDto.getAddrRsrcStLn2()) ? icpRsrcEntityDto.getAddrRsrcStLn2() : icpRsrcEntityDto.getAddrStLn2());
				bookmarkFinAddList.add(bookmarkAddrStLn2);
				BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.FIN_AGNCY_ADD_ZIP,
						icpRsrcEntityDto.getAddrZip());
				bookmarkFinAddList.add(bookmarkAddrZip);
				BookmarkDto bookmarkState = createBookmark(BookmarkConstants.FIN_AGNCY_ADD_STATE,
						icpRsrcEntityDto.getCdState());
				bookmarkFinAddList.add(bookmarkState);
				tempFinAddGroupDto.setBookmarkDtoList(bookmarkFinAddList);
				formDataGroupList.add(tempFinAddGroupDto);
			}
		}

		// parent group icp0151
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpReqDto.getIcpRsrcEntityDtoList()) {
			if (ServiceConstants.CPL_30.equals(icpRsrcEntityDto.getCdType())
					&& ObjectUtils.isEmpty(icpRsrcEntityDto.getNmEntity())) {
				FormDataGroupDto tempNmUnKnownGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_UNKNOWN,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(tempNmUnKnownGroupDto);
			}
		}

		// parent group icp0158
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpReqDto.getIcpRsrcEntityDtoList()) {
			if (ServiceConstants.CPL_30.equals(icpRsrcEntityDto.getCdType())) {
				FormDataGroupDto tempRecAgncyGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RECEIVE_AGNCY,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkRecAgncyList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.SUPV_AGNCY_ADD_CITY,
						icpRsrcEntityDto.getAddrCity());
				bookmarkRecAgncyList.add(bookmarkAddrCity);
				BookmarkDto bookmarkAddrStLn1 = createBookmark(BookmarkConstants.SUPV_AGNCY_ADD_LN1,
						icpRsrcEntityDto.getAddrRsrcStLn1());
				bookmarkRecAgncyList.add(bookmarkAddrStLn1);
				BookmarkDto bookmarkAddrStLn2 = createBookmark(BookmarkConstants.SUPV_AGNCY_ADD_LN2,
						icpRsrcEntityDto.getAddrRsrcStLn2());
				bookmarkRecAgncyList.add(bookmarkAddrStLn2);
				BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.SUPV_AGNCY_ADD_ZIP,
						icpRsrcEntityDto.getAddrZip());
				bookmarkRecAgncyList.add(bookmarkAddrZip);
				BookmarkDto bookmarkState = createBookmark(BookmarkConstants.SUPV_AGNCY_ADD_STATE,
						icpRsrcEntityDto.getCdState());
				bookmarkRecAgncyList.add(bookmarkState);
				BookmarkDto bookmarkEntity = createBookmark(BookmarkConstants.NM_AGNCY_SUPV,
						icpRsrcEntityDto.getNmEntity());
				bookmarkRecAgncyList.add(bookmarkEntity);
				tempRecAgncyGroupDto.setBookmarkDtoList(bookmarkRecAgncyList);
				formDataGroupList.add(tempRecAgncyGroupDto);
			}
		}

		// parent group icp0161
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpReqDto.getIcpRsrcEntityDtoList()) {
			if (ServiceConstants.YES.equals(icpRsrcEntityDto.getIndDispPersPlcmt())) {
				FormDataGroupDto tempDispPlnPersPhNoGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_DISP_PLAN_PERS_PHONE, FormConstants.EMPTY_STRING);

				List<FormDataGroupDto> tempDispPlnPersPhNoList = new ArrayList<FormDataGroupDto>();

				for (IcpPersonDto icpPersonDto : icpReqDto.getIcpPersonDtoH5()) {
					// sub group icp0162 :parent group icp0161
					if (ServiceConstants.CFACTYP2_50 == icpPersonDto.getCdPersonType()) {
						FormDataGroupDto tempPlnPersPhNoGroupDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_PLAN_PERS_PHONE,
								FormGroupsConstants.TMPLAT_DISP_PLAN_PERS_PHONE);

						List<BookmarkDto> bookmarkPlnPersPhNoList = new ArrayList<BookmarkDto>();
						if (!ObjectUtils.isEmpty(icpPersonDto.getNbrPersPhone())) {
							BookmarkDto bookmarkPersPhone = createBookmark(BookmarkConstants.PLAN_PERS_PHONE,
									TypeConvUtil.formatPhone(icpPersonDto.getNbrPersPhone().toString()));
							bookmarkPlnPersPhNoList.add(bookmarkPersPhone);
						}
						tempPlnPersPhNoGroupDto.setBookmarkDtoList(bookmarkPlnPersPhNoList);
						tempDispPlnPersPhNoList.add(tempPlnPersPhNoGroupDto);

					}
				}
				tempDispPlnPersPhNoGroupDto.setFormDataGroupList(tempDispPlnPersPhNoList);
				formDataGroupList.add(tempDispPlnPersPhNoGroupDto);

			}
		}

		// parent group icp0163
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpReqDto.getIcpRsrcEntityDtoList()) {
			if (ServiceConstants.YES.equals(icpRsrcEntityDto.getIndDispPersPlcmt())) {
				FormDataGroupDto tempDispPlnPersAddGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_DISP_PLAN_PERS_ADD, FormConstants.EMPTY_STRING);

				List<FormDataGroupDto> tempDispPlnPersAddGroupList = new ArrayList<FormDataGroupDto>();

				for (IcpPersonDto icpPersonDto : icpReqDto.getIcpPersonDtoH5()) {
					// sub group icp0164 :parent group icp0163
					if (ServiceConstants.CFACTYP2_50 != icpPersonDto.getCdPersonType()) {
						FormDataGroupDto tempPlnPersAddGroupDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_PLAN_PERS_ADD,
								FormGroupsConstants.TMPLAT_DISP_PLAN_PERS_ADD);

						List<BookmarkDto> bookmarkPlnPersAddList = new ArrayList<BookmarkDto>();
						BookmarkDto bookmarkPersZip = createBookmark(BookmarkConstants.PLN_PERS_ADD_ZIP,
								icpPersonDto.getAddrPersZip());
						bookmarkPlnPersAddList.add(bookmarkPersZip);
						BookmarkDto bookmarkPersCity = createBookmark(BookmarkConstants.PLN_PERS_ADD_CITY,
								icpPersonDto.getAddrPersCity());
						bookmarkPlnPersAddList.add(bookmarkPersCity);
						BookmarkDto bookmarkPersStLn1 = createBookmark(BookmarkConstants.PLN_PERS_ADD_LN1,
								icpPersonDto.getAddrPersStLn1());
						bookmarkPlnPersAddList.add(bookmarkPersStLn1);
						BookmarkDto bookmarkPersStLn2 = createBookmark(BookmarkConstants.PLN_PERS_ADD_LN2,
								icpPersonDto.getAddrPersStLn2());
						bookmarkPlnPersAddList.add(bookmarkPersStLn2);
						BookmarkDto bookmarkPersState = createBookmark(BookmarkConstants.PLN_PERS_ADD_STATE,
								icpPersonDto.getAddrPersState());
						bookmarkPlnPersAddList.add(bookmarkPersState);
						tempPlnPersAddGroupDto.setBookmarkDtoList(bookmarkPlnPersAddList);
						tempDispPlnPersAddGroupList.add(tempPlnPersAddGroupDto);
					}
				}
				tempDispPlnPersAddGroupDto.setFormDataGroupList(tempDispPlnPersAddGroupList);
				formDataGroupList.add(tempDispPlnPersAddGroupDto);
			}
		}

		// parent group icp0167
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpReqDto.getIcpRsrcEntityDtoList()) {
			if (ServiceConstants.YES.equals(icpRsrcEntityDto.getIndDispPersFin())) {
				FormDataGroupDto tempDispFinPersPhGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_DISP_FIN_PERS_PHONE, FormConstants.EMPTY_STRING);

				List<FormDataGroupDto> tempDispFinPersPhGroupList = new ArrayList<FormDataGroupDto>();

				for (IcpPersonDto icpPersonDto : icpReqDto.getIcpPersonDtoH5()) {
					// sub group icp0168 :parent group icp0167
					if (ServiceConstants.CPL_40 != icpPersonDto.getCdPersonType()) {
						FormDataGroupDto tempFinPersPhGroupDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_FIN_PERS_PHONE,
								FormGroupsConstants.TMPLAT_DISP_FIN_PERS_PHONE);

						List<BookmarkDto> bookmarkFinPersPhList = new ArrayList<BookmarkDto>();
						if (!ObjectUtils.isEmpty(icpPersonDto.getNbrPersPhone())) {
							BookmarkDto bookmarkFinPersPh = createBookmark(BookmarkConstants.FINANCE_PERS_PHONE,
									TypeConvUtil.formatPhone(icpPersonDto.getNbrPersPhone().toString()));
							bookmarkFinPersPhList.add(bookmarkFinPersPh);
						}
						tempFinPersPhGroupDto.setBookmarkDtoList(bookmarkFinPersPhList);
						tempDispFinPersPhGroupList.add(tempFinPersPhGroupDto);
					}
				}
				tempDispFinPersPhGroupDto.setFormDataGroupList(tempDispFinPersPhGroupList);
				formDataGroupList.add(tempDispFinPersPhGroupDto);
			}
		}

		// parent group icp0169
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpReqDto.getIcpRsrcEntityDtoList()) {
			if (ServiceConstants.YES.equals(icpRsrcEntityDto.getIndDispPersFin())
					&& !ServiceConstants.TEXAS.equals(icpRsrcEntityDto.getNmEntity())) {
				FormDataGroupDto tempDispFinPersAddGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_DISP_FIN_PERS_ADD, FormConstants.EMPTY_STRING);

				List<FormDataGroupDto> tempDispFinPersAddGroupList = new ArrayList<FormDataGroupDto>();

				for (IcpPersonDto icpPersonDto : icpReqDto.getIcpPersonDtoH5()) {
					// sub group icp0169 :parent group icp0170
					if (ServiceConstants.CPL_40 != icpPersonDto.getCdPersonType()) {
						FormDataGroupDto tempFinPersAddGroupDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_FIN_PERS_ADD, FormGroupsConstants.TMPLAT_DISP_FIN_PERS_ADD);

						List<BookmarkDto> bookmarkFinPersAddList = new ArrayList<BookmarkDto>();
						BookmarkDto bookmarkPersZip = createBookmark(BookmarkConstants.FIN_PERS_ADD_ZIP,
								icpPersonDto.getAddrPersZip());
						bookmarkFinPersAddList.add(bookmarkPersZip);
						BookmarkDto bookmarkPersCity = createBookmark(BookmarkConstants.FIN_PERS_ADD_CITY,
								icpPersonDto.getAddrPersCity());
						bookmarkFinPersAddList.add(bookmarkPersCity);
						BookmarkDto bookmarkPersStLn1 = createBookmark(BookmarkConstants.FIN_PERS_ADD_LN1,
								icpPersonDto.getAddrPersStLn1());
						bookmarkFinPersAddList.add(bookmarkPersStLn1);
						BookmarkDto bookmarkPersStLn2 = createBookmark(BookmarkConstants.FIN_PERS_ADD_LN2,
								icpPersonDto.getAddrPersStLn2());
						bookmarkFinPersAddList.add(bookmarkPersStLn2);
						BookmarkDto bookmarkPersState = createBookmark(BookmarkConstants.FIN_PERS_ADD_STATE,
								icpPersonDto.getAddrPersState());
						bookmarkFinPersAddList.add(bookmarkPersState);
						tempFinPersAddGroupDto.setBookmarkDtoList(bookmarkFinPersAddList);
						tempDispFinPersAddGroupList.add(tempFinPersAddGroupDto);
					}
				}
				tempDispFinPersAddGroupDto.setFormDataGroupList(tempDispFinPersAddGroupList);
				formDataGroupList.add(tempDispFinPersAddGroupDto);
			}
		}

		// parent group icp0182
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpReqDto.getIcpRsrcEntityDtoList()) {
			if (ServiceConstants.CPL_20.equals(icpRsrcEntityDto.getCdType())
					&& ServiceConstants.TEXAS.equals(icpRsrcEntityDto.getNmEntity())) {
				FormDataGroupDto tempDispPlnAddrTxGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_DISP_PLN_ADDR_TX, FormConstants.EMPTY_STRING);

				List<FormDataGroupDto> tempDispPlnAddrTxGroupList = new ArrayList<FormDataGroupDto>();

				// sub group icp0183 :parent group icp0182
				FormDataGroupDto tempAddrPlnTxGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_PLN_TX,
						FormGroupsConstants.TMPLAT_DISP_PLN_ADDR_TX);

				List<BookmarkDto> bookmarkAddrPlnTxList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPersZip = createBookmark(BookmarkConstants.ADDR_PLN_TX_ZIP,
						icpReqDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
				bookmarkAddrPlnTxList.add(bookmarkPersZip);
				BookmarkDto bookmarkPersCity = createBookmark(BookmarkConstants.ADDR_PLN_TX_CITY,
						icpReqDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
				bookmarkAddrPlnTxList.add(bookmarkPersCity);
				BookmarkDto bookmarkPersStLn1 = createBookmark(BookmarkConstants.ADDR_PLN_TX_STLN1,
						icpReqDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
				bookmarkAddrPlnTxList.add(bookmarkPersStLn1);
				BookmarkDto bookmarkPersStLn2 = createBookmark(BookmarkConstants.ADDR_PLN_TX_STLN2,
						icpReqDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
				bookmarkAddrPlnTxList.add(bookmarkPersStLn2);
				tempAddrPlnTxGroupDto.setBookmarkDtoList(bookmarkAddrPlnTxList);
				tempDispPlnAddrTxGroupList.add(tempAddrPlnTxGroupDto);

				tempDispPlnAddrTxGroupDto.setFormDataGroupList(tempDispPlnAddrTxGroupList);
				formDataGroupList.add(tempDispPlnAddrTxGroupDto);
			}
		}

		// parent group icp0184
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpReqDto.getIcpRsrcEntityDtoList()) {
			if (ServiceConstants.CPL_20.equals(icpRsrcEntityDto.getCdType())
					&& ServiceConstants.TEXAS.equals(icpRsrcEntityDto.getNmEntity())) {
				FormDataGroupDto tempDispPlnPhTxGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_DISP_PLN_PHONE_TX, FormConstants.EMPTY_STRING);

				List<FormDataGroupDto> tempDispPlnPhTxGroupList = new ArrayList<FormDataGroupDto>();

				// sub group icp0185 :parent group icp0184
				FormDataGroupDto tempPlnPhTxGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PLAN_PHONE_TX,
						FormGroupsConstants.TMPLAT_DISP_PLN_PHONE_TX);
				List<BookmarkDto> bookmarkPlnPhTxList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPlnPhTx = createBookmark(BookmarkConstants.PLAN_PHONE_TX,
						TypeConvUtil.formatPhone(icpReqDto.getEmployeePersPhNameDto().getNbrPhone()));
				bookmarkPlnPhTxList.add(bookmarkPlnPhTx);

				List<FormDataGroupDto> tempPlnPhTxGroupList = new ArrayList<FormDataGroupDto>();

				// sub group icp0186 :parent group icp0185
				if (StringUtils.isNotBlank(icpReqDto.getEmployeePersPhNameDto().getNbrPhoneExtension())) {
					FormDataGroupDto tempPlnPhExtGroupDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_PLAN_PHONE_EXT, FormGroupsConstants.TMPLAT_PLAN_PHONE_TX);

					List<BookmarkDto> bookmarkPlnPhExtList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkPlnPhExt = createBookmark(BookmarkConstants.PLN_TX_PHONE_EXT,
							TypeConvUtil.formatPhone(icpReqDto.getEmployeePersPhNameDto().getNbrPhoneExtension()));
					bookmarkPlnPhExtList.add(bookmarkPlnPhExt);
					tempPlnPhExtGroupDto.setBookmarkDtoList(bookmarkPlnPhExtList);
					tempPlnPhTxGroupList.add(tempPlnPhExtGroupDto);
				}
				tempPlnPhTxGroupDto.setFormDataGroupList(tempPlnPhTxGroupList);
				tempPlnPhTxGroupDto.setBookmarkDtoList(bookmarkPlnPhTxList);
				tempDispPlnPhTxGroupList.add(tempPlnPhTxGroupDto);
				tempDispPlnPhTxGroupDto.setFormDataGroupList(tempDispPlnPhTxGroupList);
				formDataGroupList.add(tempDispPlnPhTxGroupDto);
			}
		}

		// parent group icp0187
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpReqDto.getIcpRsrcEntityDtoList()) {
			if (ServiceConstants.CPL_10.equals(icpRsrcEntityDto.getCdType())
					&& ServiceConstants.TEXAS.equals(icpRsrcEntityDto.getNmEntity())) {
				FormDataGroupDto tempDispFinPhTxGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_DISP_FIN_PHONE_TX, FormConstants.EMPTY_STRING);

				List<FormDataGroupDto> tempDispFinPhTxGroupList = new ArrayList<FormDataGroupDto>();

				// sub group icp0188 :parent group icp0187
				FormDataGroupDto tempFinPhTxGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FIN_PHONE_TX,
						FormGroupsConstants.TMPLAT_DISP_FIN_PHONE_TX);

				List<BookmarkDto> bookmarkFinPhTxList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkFinPhTx = createBookmark(BookmarkConstants.FIN_PHONE_TX,
						TypeConvUtil.formatPhone(icpReqDto.getEmployeePersPhNameDto().getNbrPhone()));
				bookmarkFinPhTxList.add(bookmarkFinPhTx);
				tempFinPhTxGroupDto.setBookmarkDtoList(bookmarkFinPhTxList);

				List<FormDataGroupDto> tempFinPhExtGroupList = new ArrayList<FormDataGroupDto>();

				if (StringUtils.isNotBlank(icpReqDto.getEmployeePersPhNameDto().getNbrPhoneExtension())) {
					// sub group icp0189 :parent group icp0188
					FormDataGroupDto tempFinPhExtGroupDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_FIN_PHONE_EXT, FormGroupsConstants.TMPLAT_FIN_PHONE_TX);

					List<BookmarkDto> bookmarkFinPhExtList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkFinPhExt = createBookmark(BookmarkConstants.FIN_TX_PHONE_EXT,
							TypeConvUtil.formatPhone(icpReqDto.getEmployeePersPhNameDto().getNbrPhoneExtension()));
					bookmarkFinPhExtList.add(bookmarkFinPhExt);
					tempFinPhExtGroupDto.setBookmarkDtoList(bookmarkFinPhExtList);
					tempFinPhExtGroupList.add(tempFinPhExtGroupDto);
				}
				tempFinPhTxGroupDto.setFormDataGroupList(tempFinPhExtGroupList);
				tempDispFinPhTxGroupList.add(tempFinPhTxGroupDto);
				tempDispFinPhTxGroupDto.setFormDataGroupList(tempDispFinPhTxGroupList);
				formDataGroupList.add(tempDispFinPhTxGroupDto);
			}
		}

		// parent group icp0190
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpReqDto.getIcpRsrcEntityDtoList()) {
			if (ServiceConstants.CPL_10.equals(icpRsrcEntityDto.getCdType())
					&& ServiceConstants.TEXAS.equals(icpRsrcEntityDto.getNmEntity())) {
				FormDataGroupDto tempDispFinAddrTxGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_DISP_FIN_ADDR_TX, FormConstants.EMPTY_STRING);

				List<FormDataGroupDto> tempDispFinAddrTxGroupList = new ArrayList<FormDataGroupDto>();

				// sub group icp0191 :parent group icp0190
				FormDataGroupDto tempAddrFinTxGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_FIN_TX,
						FormGroupsConstants.TMPLAT_DISP_FIN_ADDR_TX);

				List<BookmarkDto> bookmarkAddrFinTxList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPersZip = createBookmark(BookmarkConstants.ADDR_FIN_TX_ZIP,
						icpReqDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
				bookmarkAddrFinTxList.add(bookmarkPersZip);
				BookmarkDto bookmarkPersCity = createBookmark(BookmarkConstants.ADDR_FIN_TX_CITY,
						icpReqDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
				bookmarkAddrFinTxList.add(bookmarkPersCity);
				BookmarkDto bookmarkPersStLn1 = createBookmark(BookmarkConstants.ADDR_FIN_TX_STLN1,
						icpReqDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
				bookmarkAddrFinTxList.add(bookmarkPersStLn1);
				BookmarkDto bookmarkPersStLn2 = createBookmark(BookmarkConstants.ADDR_FIN_TX_STLN2,
						icpReqDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
				bookmarkAddrFinTxList.add(bookmarkPersStLn2);
				tempAddrFinTxGroupDto.setBookmarkDtoList(bookmarkAddrFinTxList);
				tempDispFinAddrTxGroupList.add(tempAddrFinTxGroupDto);
				tempDispFinAddrTxGroupDto.setFormDataGroupList(tempDispFinAddrTxGroupList);
				formDataGroupList.add(tempDispFinAddrTxGroupDto);
			}
		}

		//Defect# 12758 - Added a condition to set the placement details only for the care type related to resources
		if (!ObjectUtils.isEmpty(icpReqDto.getIcpRsrcEntityDto6())
				&& (ServiceConstants.CPL_30.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
						|| ServiceConstants.CFACTYP2_50.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
						|| ServiceConstants.CPL_60.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
						|| ServiceConstants.CCH_90.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType()))) {
			// parent group icp0171
			if (!ObjectUtils.isEmpty(icpReqDto.getIcpRsrcEntityDto6().getNmResource())) {
				FormDataGroupDto tempNmAgncyChdPlacedGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_NM_AGNCY_CHILD_PLACED, FormConstants.EMPTY_STRING);

				List<BookmarkDto> bookmarkNmAgncyChdPlacedList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkNmAgncyChdPlaced = createBookmark(BookmarkConstants.NM_AGNCY_CHILD_PLACED,
						icpReqDto.getIcpRsrcEntityDto6().getNmResource());
				bookmarkNmAgncyChdPlacedList.add(bookmarkNmAgncyChdPlaced);
				tempNmAgncyChdPlacedGroupDto.setBookmarkDtoList(bookmarkNmAgncyChdPlacedList);
				formDataGroupList.add(tempNmAgncyChdPlacedGroupDto);
			}

			// parent group icp0173
			if (!ObjectUtils.isEmpty(icpReqDto.getIcpRsrcEntityDto6().getNmResource())) {
				FormDataGroupDto tempPlcAgncyPhGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PLACED_AGNCY_PHONE, FormConstants.EMPTY_STRING);

				List<BookmarkDto> bookmarkPlcAgncyPhList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPlcAgncyPh = createBookmark(BookmarkConstants.PLACED_AGNCY_PHONE,
						TypeConvUtil.formatPhone(icpReqDto.getIcpRsrcEntityDto6().getNbrRsrcPhone().toString()));
				bookmarkPlcAgncyPhList.add(bookmarkPlcAgncyPh);
				tempPlcAgncyPhGroupDto.setBookmarkDtoList(bookmarkPlcAgncyPhList);
				formDataGroupList.add(tempPlcAgncyPhGroupDto);
			}

			// parent group icp0174
			if (!ObjectUtils.isEmpty(icpReqDto.getIcpRsrcEntityDto6().getNmResource())) {
				FormDataGroupDto tempPlcAgncyAddGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PLC_AGNCY_ADD,
						FormConstants.EMPTY_STRING);

				List<BookmarkDto> bookmarkPlcAgncyAddList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPersRsrcCity = createBookmark(BookmarkConstants.PLC_AGNCY_ADD_CITY,
						icpReqDto.getIcpRsrcEntityDto6().getAddrRsrcCity());
				bookmarkPlcAgncyAddList.add(bookmarkPersRsrcCity);
				BookmarkDto bookmarkPersRsrcState = createBookmark(BookmarkConstants.PLC_AGNCY_ADD_STATE,
						icpReqDto.getIcpRsrcEntityDto6().getAddrRsrcState());
				bookmarkPlcAgncyAddList.add(bookmarkPersRsrcState);
				BookmarkDto bookmarkPersRsrcStLn2 = createBookmark(BookmarkConstants.PLC_AGNCY_ADD_LN2,
						icpReqDto.getIcpRsrcEntityDto6().getAddrRsrcStLn2());
				bookmarkPlcAgncyAddList.add(bookmarkPersRsrcStLn2);
				BookmarkDto bookmarkPersRsrcStLn1 = createBookmark(BookmarkConstants.PLC_AGNCY_ADD_LN1,
						icpReqDto.getIcpRsrcEntityDto6().getAddrRsrcStLn1());
				bookmarkPlcAgncyAddList.add(bookmarkPersRsrcStLn1);
				BookmarkDto bookmarkPersRsrcZip = createBookmark(BookmarkConstants.PLC_AGNCY_ADD_ZIP,
						icpReqDto.getIcpRsrcEntityDto6().getAddrRsrcZip());
				bookmarkPlcAgncyAddList.add(bookmarkPersRsrcZip);
				tempPlcAgncyAddGroupDto.setBookmarkDtoList(bookmarkPlcAgncyAddList);
				formDataGroupList.add(tempPlcAgncyAddGroupDto);
			}
		}

		for (String enclosure : icpReqDto.getEnclosure()) {

			// parent group icp0152
			if (ServiceConstants.CPL_30.equals(enclosure)) {
				FormDataGroupDto tempChldSocialHistGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CHILD_SOCIAL_HIST, FormConstants.EMPTY_STRING);
				formDataGroupList.add(tempChldSocialHistGroupDto);
			}

			// parent group icp0153
			if (ServiceConstants.CPL_42.equals(enclosure)) {
				FormDataGroupDto tempHmStdyPlcmntGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_HM_STUDY_PLCMT, FormConstants.EMPTY_STRING);
				formDataGroupList.add(tempHmStdyPlcmntGroupDto);
			}

			// parent group icp0154
			if (!ServiceConstants.CFACTYP2_35.equals(enclosure) && !ServiceConstants.CPL_42.equals(enclosure)
					&& !ServiceConstants.CPL_30.equals(enclosure)) {
				FormDataGroupDto tempOtherEnclGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_OTHER_ENCLOSURE,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(tempOtherEnclGroupDto);
			}

			// parent group icp0177
			if (ServiceConstants.CFACTYP2_35.equals(enclosure)) {
				FormDataGroupDto tempCourtOrderGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COURT_ORDER,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(tempCourtOrderGroupDto);
			}
		}

		// Error Causing Code

		for (IcpPersonDto icpPersonDto : icpReqDto.getIcpPersonDtoH5()) {

			/*
			 * 
			 * 
			 * 
			 * // parent group icp0165 if
			 * (ServiceConstants.CPL_40.equals(icpPersonDto.getCdPersonType()))
			 * { FormDataGroupDto tempCommaGroupDto =
			 * createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
			 * FormConstants.EMPTY_STRING);
			 * formDataGroupList.add(tempCommaGroupDto); }
			 * 
			 * // parent group icp0166 if
			 * (ServiceConstants.CPL_40.equals(icpPersonDto.getCdPersonType()))
			 * { FormDataGroupDto tempNmPersFinGroupDto =
			 * createFormDataGroup(FormGroupsConstants.TMPLAT_NM_PERS_FIN,
			 * FormConstants.EMPTY_STRING);
			 * 
			 * List<BookmarkDto> bookmarkNmPersFinList = new
			 * ArrayList<BookmarkDto>(); BookmarkDto bookmarkNmPersFin =
			 * createBookmark(BookmarkConstants.NM_PERS_FINANCE,
			 * icpPersonDto.getNmPersonFull());
			 * bookmarkNmPersFinList.add(bookmarkNmPersFin);
			 * tempNmPersFinGroupDto.setBookmarkDtoList(bookmarkNmPersFinList);
			 * formDataGroupList.add(tempNmPersFinGroupDto); }
			 * 
			 */

			// parent group icp0172
			//Defect# 12758 - Added a condition to set the placement details only for the care type related to person
			if (ServiceConstants.CPL_60.equals(icpPersonDto.getCdPersonType())
					&& !(ServiceConstants.CPL_30.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
							|| ServiceConstants.CFACTYP2_50.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
							|| ServiceConstants.CPL_60.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
							|| ServiceConstants.CCH_90.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType()))) {
				FormDataGroupDto tempNmPersChildPlacedGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_NM_PERS_CHILD_PLACED, FormConstants.EMPTY_STRING);

				List<BookmarkDto> bookmarkNmPersChildPlacedList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkNmPersChildPlaced = createBookmark(BookmarkConstants.NM_PERS_CHILD_PLACED,
						icpPersonDto.getNmPersonFull());
				bookmarkNmPersChildPlacedList.add(bookmarkNmPersChildPlaced);
				tempNmPersChildPlacedGroupDto.setBookmarkDtoList(bookmarkNmPersChildPlacedList);
				formDataGroupList.add(tempNmPersChildPlacedGroupDto);

			}

			// parent group icp0175
			//Defect# 12758 - Added a condition to set the placement details only for the care type related to person address
			if (ServiceConstants.CPL_60.equals(icpPersonDto.getCdPersonType())
					&& !(ServiceConstants.CPL_30.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
							|| ServiceConstants.CFACTYP2_50.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
							|| ServiceConstants.CPL_60.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
							|| ServiceConstants.CCH_90.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType()))) {
				FormDataGroupDto tempPlcPersAddGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PLC_PERS_ADD,
						FormConstants.EMPTY_STRING);

				List<BookmarkDto> bookmarkPlcPersAddList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPersRsrcCity = createBookmark(BookmarkConstants.PLC_PERS_ADD_CITY,
						icpPersonDto.getAddrPersCity());
				bookmarkPlcPersAddList.add(bookmarkPersRsrcCity);
				BookmarkDto bookmarkPersRsrcState = createBookmark(BookmarkConstants.PLC_PERS_ADD_STATE,
						icpPersonDto.getAddrPersState());
				bookmarkPlcPersAddList.add(bookmarkPersRsrcState);
				BookmarkDto bookmarkPersRsrcStLn2 = createBookmark(BookmarkConstants.PLC_PERS_ADD_LN2,
						icpPersonDto.getAddrPersStLn2());
				bookmarkPlcPersAddList.add(bookmarkPersRsrcStLn2);
				BookmarkDto bookmarkPersRsrcStLn1 = createBookmark(BookmarkConstants.PLC_PERS_ADD_LN1,
						icpPersonDto.getAddrPersStLn1());
				bookmarkPlcPersAddList.add(bookmarkPersRsrcStLn1);
				BookmarkDto bookmarkPersRsrcZip = createBookmark(BookmarkConstants.PLC_PERS_ADD_ZIP,
						icpPersonDto.getAddrPersZip());
				bookmarkPlcPersAddList.add(bookmarkPersRsrcZip);
				tempPlcPersAddGroupDto.setBookmarkDtoList(bookmarkPlcPersAddList);
				formDataGroupList.add(tempPlcPersAddGroupDto);
			}

			// parent group icp0176
			//Defect# 12758 - Added a condition to set the placement details only for the care type related to person phone number
			if (ServiceConstants.CPL_60.equals(icpPersonDto.getCdPersonType())
					&& !(ServiceConstants.CPL_30.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
							|| ServiceConstants.CFACTYP2_50.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
							|| ServiceConstants.CPL_60.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType())
							|| ServiceConstants.CCH_90.equals(icpReqDto.getIcpPersonDtoF1().getCdCareType()))) {
				FormDataGroupDto tempPlcPersPhGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PLACED_PERS_PHONE, FormConstants.EMPTY_STRING);

				List<BookmarkDto> bookmarkPlcPersPhList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPlcPersPh = createBookmark(BookmarkConstants.PLACED_PERS_PHONE,
						TypeConvUtil.formatPhone(icpPersonDto.getNbrPersPhone()));
				bookmarkPlcPersPhList.add(bookmarkPlcPersPh);
				tempPlcPersPhGroupDto.setBookmarkDtoList(bookmarkPlcPersPhList);
				formDataGroupList.add(tempPlcPersPhGroupDto);
			}
		}

		// Error Code

		// bookmarks without groups
		List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();

		// CSESG1D
		BookmarkDto bookmarkState = createBookmarkWithCodesTable(BookmarkConstants.TO_OTHER_STATE,
				icpReqDto.getIcpRsrcEntityDto1().getCdRecievingState(), CodesConstant.CSTATE);
		bookmarkNonFormGrpList.add(bookmarkState);
		BookmarkDto bookmarkType = createBookmarkWithCodesTable(BookmarkConstants.CD_REQUEST_TYPE,
				icpReqDto.getIcpRsrcEntityDto1().getCdReqType(), CodesConstant.ICPCFMTP);
		bookmarkNonFormGrpList.add(bookmarkType);

		// CSESF1D
		BookmarkDto bookmarkReceivingState = createBookmarkWithCodesTable(BookmarkConstants.CD_RECEIVING_STATE,
				icpReqDto.getIcpPersonDtoF1().getCdReceivingState(), CodesConstant.ICPCST);
		bookmarkNonFormGrpList.add(bookmarkReceivingState);
		BookmarkDto bookmarkSendingState = createBookmarkWithCodesTable(BookmarkConstants.CD_SENDING_STATE,
				icpReqDto.getIcpPersonDtoF1().getCdSendingState(), CodesConstant.ICPCST);
		bookmarkNonFormGrpList.add(bookmarkSendingState);
		BookmarkDto bookmarkTxtPlacemnetRemarks = createBookmark(BookmarkConstants.REMARKS,
				icpReqDto.getIcpPersonDtoF1().getTxtPlacementRemarks());
		bookmarkNonFormGrpList.add(bookmarkTxtPlacemnetRemarks);

		// CSESF5D
		BookmarkDto bookmarkDtPersonBirth = createBookmark(BookmarkConstants.DATE_OF_BIRTH,
				DateUtils.stringDt(icpReqDto.getIcpPersonDtoF5().getDtPersonBirth()));
		bookmarkNonFormGrpList.add(bookmarkDtPersonBirth);
		BookmarkDto bookmarkCdPersonSex = createBookmarkWithCodesTable(BookmarkConstants.CD_SEX,
				icpReqDto.getIcpPersonDtoF5().getCdPersonSex(), CodesConstant.CSEX);
		bookmarkNonFormGrpList.add(bookmarkCdPersonSex);
		BookmarkDto bookmarkPersonIdNumber = createBookmark(BookmarkConstants.SOCIAL_NUMBER,
				icpReqDto.getIcpPersonDtoF5().getNbrPersonId());
		bookmarkNonFormGrpList.add(bookmarkPersonIdNumber);
		BookmarkDto bookmarkNmPersonFull = createBookmark(BookmarkConstants.NM_OF_CHILD,
				icpReqDto.getIcpPersonDtoF5().getNmPersonFull());
		bookmarkNonFormGrpList.add(bookmarkNmPersonFull);

		// Other Type Of Care for icp01o00
		BookmarkDto bookmarkOtherTypeOfCare = createBookmark(BookmarkConstants.OTHER_TYPE_OF_CARE,
				icpReqDto.getIcpPersonDtoF1().getOtrTypeOfCare());
		bookmarkNonFormGrpList.add(bookmarkOtherTypeOfCare);

		// Other Legal status for icp01o00
		BookmarkDto bookmarkOtherLegalStatus = createBookmark(BookmarkConstants.OTHER_LEGAL_STATUS,
				icpReqDto.getIcpPersonDtoF1().getOtrLegalStatus());
		bookmarkNonFormGrpList.add(bookmarkOtherLegalStatus);

		// CSESF6D
		if (!ObjectUtils.isEmpty(icpReqDto.getIcpRsrcEntityDto6())) {
			if (!ObjectUtils.isEmpty(icpReqDto.getIcpRsrcEntityDto6().getAddrRsrcCity())) {
				BookmarkDto bookmarkAddrRsrcCity = createBookmark(BookmarkConstants.PLC_AGNCY_ADD_CITY,
						icpReqDto.getIcpRsrcEntityDto6().getAddrRsrcCity());
				bookmarkNonFormGrpList.add(bookmarkAddrRsrcCity);
			}
			if (!ObjectUtils.isEmpty(icpReqDto.getIcpRsrcEntityDto6().getAddrRsrcStLn1())) {
				BookmarkDto bookmarkAddrRsrcStLn1 = createBookmark(BookmarkConstants.PLC_AGNCY_ADD_LN1,
						icpReqDto.getIcpRsrcEntityDto6().getAddrRsrcStLn1());
				bookmarkNonFormGrpList.add(bookmarkAddrRsrcStLn1);
			}
			if (!ObjectUtils.isEmpty(icpReqDto.getIcpRsrcEntityDto6().getAddrRsrcStLn2())) {
				BookmarkDto bookmarkAddrRsrcStLn2 = createBookmark(BookmarkConstants.PLC_AGNCY_ADD_LN2,
						icpReqDto.getIcpRsrcEntityDto6().getAddrRsrcStLn2());
				bookmarkNonFormGrpList.add(bookmarkAddrRsrcStLn2);
			}
			if (!ObjectUtils.isEmpty(icpReqDto.getIcpRsrcEntityDto6().getAddrRsrcState())) {
				BookmarkDto bookmarkAddrRsrcState = createBookmark(BookmarkConstants.PLC_AGNCY_ADD_STATE,
						icpReqDto.getIcpRsrcEntityDto6().getAddrRsrcState());
				bookmarkNonFormGrpList.add(bookmarkAddrRsrcState);
			}
			if (!ObjectUtils.isEmpty(icpReqDto.getIcpRsrcEntityDto6().getAddrRsrcZip())) {
				BookmarkDto bookmarkAddrRsrcZip = createBookmark(BookmarkConstants.PLC_AGNCY_ADD_ZIP,
						icpReqDto.getIcpRsrcEntityDto6().getAddrRsrcZip());
				bookmarkNonFormGrpList.add(bookmarkAddrRsrcZip);
			}
			if (!ObjectUtils.isEmpty(icpReqDto.getIcpRsrcEntityDto6().getNbrRsrcPhone())) {
				BookmarkDto bookmarkAddrRsrcPhone = createBookmark(BookmarkConstants.PLACED_AGNCY_PHONE,
						icpReqDto.getIcpRsrcEntityDto6().getNbrRsrcPhone());
				bookmarkNonFormGrpList.add(bookmarkAddrRsrcPhone);
			}
			if (!ObjectUtils.isEmpty(icpReqDto.getIcpRsrcEntityDto6().getNmResource())) {
				BookmarkDto bookmarkNmResource = createBookmark(BookmarkConstants.NM_AGNCY_CHILD_PLACED,
						icpReqDto.getIcpRsrcEntityDto6().getNmResource());
				bookmarkNonFormGrpList.add(bookmarkNmResource);
			}
		}

		// CSESF9D
		if (!ObjectUtils.isEmpty(icpReqDto.getIcpcChildDetailsDto())) {
			BookmarkDto bookmarkCdRelation = createBookmark(BookmarkConstants.RELATIVE_RELATION,
					icpReqDto.getIcpcChildDetailsDto().getCdStagePersRelInt());
			bookmarkNonFormGrpList.add(bookmarkCdRelation);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);

		return preFillData;

	}

}
