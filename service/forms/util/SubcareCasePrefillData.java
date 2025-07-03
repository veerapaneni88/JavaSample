package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
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
import us.tx.state.dfps.service.subcare.dto.SubcareCaseDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:SubcareCasePrefillData will implement returnPrefillData operation
 * defined in DocumentServiceUtil Interface to populate the prefill data for
 * Form CSC40O00 Apr 9, 2018- 10:53:14 AM © 2017 Texas Department of Family and
 * Protective Services
 */
@Component
public class SubcareCasePrefillData extends DocumentServiceUtil {

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

		SubcareCaseDto subcareCaseDto = (SubcareCaseDto) parentDtoobj;

		if (ObjectUtils.isEmpty(subcareCaseDto.getCaseInfoDtoPrincipal())) {
			subcareCaseDto.setCaseInfoDtoPrincipal(new ArrayList<CaseInfoDto>());
		}
		if (ObjectUtils.isEmpty(subcareCaseDto.getCaseInfoDtoCollateral())) {
			subcareCaseDto.setCaseInfoDtoCollateral(new ArrayList<CaseInfoDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// parent group csc40o01
		FormDataGroupDto removalGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_REMOVAL_REASON,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkRemovalList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkReason = createBookmarkWithCodesTable(BookmarkConstants.RSN_REMOVAL,
				!ObjectUtils.isEmpty(subcareCaseDto.getSubcareChildContactDtoRem())
						? subcareCaseDto.getSubcareChildContactDtoRem().getCdRemovalReason()
						: "",
				CodesConstant.CREMFRHR);
		bookmarkRemovalList.add(bookmarkReason);
		removalGroupDto.setBookmarkDtoList(bookmarkRemovalList);
		formDataGroupList.add(removalGroupDto);

		// parent group csc40o12
		FormDataGroupDto statusGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_LEGAL_STATUS,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(statusGroupDto);

		List<FormDataGroupDto> statusGroupList = new ArrayList<FormDataGroupDto>();
		statusGroupDto.setFormDataGroupList(statusGroupList);

		// sub group csc40o12 :parent group csc40o12
		FormDataGroupDto statusDismissalGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISMISSAL_DATE,
				FormGroupsConstants.TMPLAT_LEGAL_STATUS);
		statusGroupList.add(statusDismissalGroupDto);
		List<BookmarkDto> bookmarkDismissalList = new ArrayList<BookmarkDto>();
		if (!ObjectUtils.isEmpty(subcareCaseDto.getSubcareLegalEnrollDtoTmc())) {
			BookmarkDto bookmarkDismiss = createBookmark(BookmarkConstants.DISMISSAL_DATE,
					subcareCaseDto.getSubcareLegalEnrollDtoTmc().getDtLegalStatTmcDissmiss());
			bookmarkDismissalList.add(bookmarkDismiss);
		}
		statusDismissalGroupDto.setBookmarkDtoList(bookmarkDismissalList);

		// parent group csc40o08
		if (!ObjectUtils.isEmpty(subcareCaseDto.getPlacementActPlannedOutDto())) {
			if (ServiceConstants.CCOR_010.equals(subcareCaseDto.getPlacementActPlannedOutDto().getCdPlcmtType())) {
				FormDataGroupDto personGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PLCMT_PERSON,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPersonDt = createBookmark(BookmarkConstants.PLACEMENT_DATE,
						DateUtils.stringDt(subcareCaseDto.getPlacementActPlannedOutDto().getDtPlcmtStart()));
				bookmarkPersonList.add(bookmarkPersonDt);
				BookmarkDto bookmarkPlcmtLivArr = createBookmarkWithCodesTable(BookmarkConstants.LIVING_ARRANGEMENT,
						subcareCaseDto.getPlacementActPlannedOutDto().getCdPlcmtLivArr(), CodesConstant.CPLLAFRM);
				bookmarkPersonList.add(bookmarkPlcmtLivArr);
				BookmarkDto bookmarkPlcmtFull = createBookmark(BookmarkConstants.PLACEMENT_NAME,
						subcareCaseDto.getPlacementActPlannedOutDto().getNmPlcmtPersonFull());
				bookmarkPersonList.add(bookmarkPlcmtFull);
				BookmarkDto bookmarkPlacementType = createBookmarkWithCodesTable(BookmarkConstants.PLACEMENT_TYPE,
						subcareCaseDto.getPlacementActPlannedOutDto().getCdPlcmtType(), CodesConstant.CPLMNTYP);
				bookmarkPersonList.add(bookmarkPlacementType);
				BookmarkDto bookmarkAdult = createBookmark(BookmarkConstants.PERSON_ID,
						subcareCaseDto.getPlacementActPlannedOutDto().getIdPlcmtAdult());
				bookmarkPersonList.add(bookmarkAdult);
				personGroupDto.setBookmarkDtoList(bookmarkPersonList);
				formDataGroupList.add(personGroupDto);
			}

			// parent group csc40o09

			if (!ServiceConstants.CCOR_010.equals(subcareCaseDto.getPlacementActPlannedOutDto().getCdPlcmtType())) {
				FormDataGroupDto personGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PLCMT_AGENCY,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPersonDt = createBookmark(BookmarkConstants.PLACEMENT_DATE,
						DateUtils.stringDt(subcareCaseDto.getPlacementActPlannedOutDto().getDtPlcmtStart()));
				bookmarkPersonList.add(bookmarkPersonDt);
				BookmarkDto bookmarkPlcmtLivArr = createBookmarkWithCodesTable(BookmarkConstants.LIVING_ARRANGEMENT,
						subcareCaseDto.getPlacementActPlannedOutDto().getCdPlcmtLivArr(), CodesConstant.CPLLAFRM);
				bookmarkPersonList.add(bookmarkPlcmtLivArr);
				BookmarkDto bookmarkPlcmtAgency = createBookmark(BookmarkConstants.AGENCY_NAME,
						subcareCaseDto.getPlacementActPlannedOutDto().getNmPlcmtAgency());
				bookmarkPersonList.add(bookmarkPlcmtAgency);
				BookmarkDto bookmarkFacil = createBookmark(BookmarkConstants.FACILITY_NAME,
						subcareCaseDto.getPlacementActPlannedOutDto().getNmPlcmtFacil());
				bookmarkPersonList.add(bookmarkFacil);
				BookmarkDto bookmarkPlacementType = createBookmarkWithCodesTable(BookmarkConstants.PLACEMENT_TYPE,
						subcareCaseDto.getPlacementActPlannedOutDto().getCdPlcmtType(), CodesConstant.CPLMNTYP);
				bookmarkPersonList.add(bookmarkPlacementType);
				personGroupDto.setBookmarkDtoList(bookmarkPersonList);
				formDataGroupList.add(personGroupDto);

				List<FormDataGroupDto> personGroupList = new ArrayList<FormDataGroupDto>();
				personGroupDto.setFormDataGroupList(personGroupList);

				// sub group csc40o07 :parent group csc40o09
				if (ServiceConstants.ZERO != subcareCaseDto.getPlacementActPlannedOutDto().getIdRsrcFacil()) {
					FormDataGroupDto facilityGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FACILITY_ID,
							FormGroupsConstants.TMPLAT_PLCMT_AGENCY);
					personGroupList.add(facilityGroupDto);

					List<BookmarkDto> bookmarkFacilityList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkFacilId = createBookmark(BookmarkConstants.FACILITY_ID,
							subcareCaseDto.getPlacementActPlannedOutDto().getIdRsrcFacil());
					bookmarkPersonList.add(bookmarkFacilId);
					facilityGroupDto.setBookmarkDtoList(bookmarkFacilityList);
				}

				// sub group csc40o10 :parent group csc40o09

				if (ServiceConstants.ZERO != subcareCaseDto.getPlacementActPlannedOutDto().getIdRsrcAgency()) {
					FormDataGroupDto facilityGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_AGENCY_ID,
							FormGroupsConstants.TMPLAT_PLCMT_AGENCY);
					personGroupList.add(facilityGroupDto);

					List<BookmarkDto> bookmarkFacilityList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkFacilId = createBookmark(BookmarkConstants.AGENCY_ID,
							subcareCaseDto.getPlacementActPlannedOutDto().getIdRsrcAgency());
					bookmarkPersonList.add(bookmarkFacilId);
					facilityGroupDto.setBookmarkDtoList(bookmarkFacilityList);
				}

			}
		}

		String motherDtlsExist=ServiceConstants.N;
		// parent group csc40o04
		for (CaseInfoDto caseInfoDto : subcareCaseDto.getCaseInfoDtoPrincipal()) {
			if (ServiceConstants.FEMALE.equals(caseInfoDto.getCdPersonSex())
					&& ServiceConstants.PLACEMENT_PERSON_TYPE.equals(caseInfoDto.getIndPersCancelHist())
					&& !("PC".equalsIgnoreCase(caseInfoDto.getCdStagePersRole()))) {
				
				motherDtlsExist=ServiceConstants.Y;
				FormDataGroupDto personGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MOTHER,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkZip = createBookmark(BookmarkConstants.ADDR_ZIP,
						caseInfoDto.getAddrPersonAddrZip());
				bookmarkPersonList.add(bookmarkZip);
				BookmarkDto bookmarkPhone = createBookmark(BookmarkConstants.PHONE_NUMBER,
						TypeConvUtil.formatPhone(caseInfoDto.getNbrPersonPhone()));
				bookmarkPersonList.add(bookmarkPhone);
				BookmarkDto bookmarkCity = createBookmark(BookmarkConstants.ADDR_CITY,
						caseInfoDto.getAddrPersonAddrCity());
				bookmarkPersonList.add(bookmarkCity);
				BookmarkDto bookmarkLn1 = createBookmark(BookmarkConstants.ADDR_LN_1,
						caseInfoDto.getAddrPersAddrStLn1());
				bookmarkPersonList.add(bookmarkLn1);
				BookmarkDto bookmarkLn2 = createBookmark(BookmarkConstants.ADDR_LN_2,
						caseInfoDto.getAddrPersAddrStLn2());
				bookmarkPersonList.add(bookmarkLn2);
				BookmarkDto bookmarkState = createBookmark(BookmarkConstants.ADDR_STATE,
						caseInfoDto.getCdPersonAddrState());
				bookmarkPersonList.add(bookmarkState);
				BookmarkDto bookmarkEmail = createBookmark(BookmarkConstants.EMAIL_ADDRESS, caseInfoDto.getTxtEmail());
				bookmarkPersonList.add(bookmarkEmail);
				BookmarkDto bookmarkDtCVSMon = createBookmark(BookmarkConstants.LAST_CVS_MONTHLY_DATE,
						caseInfoDto.getLastContactDate());
				bookmarkPersonList.add(bookmarkDtCVSMon);
				BookmarkDto bookmarkPType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
						caseInfoDto.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
				bookmarkPersonList.add(bookmarkPType);
				BookmarkDto bookmarkRel = createBookmarkWithCodesTable(BookmarkConstants.RELATION,
						caseInfoDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
				bookmarkPersonList.add(bookmarkRel);
				BookmarkDto bookmarkNmFirst = createBookmark(BookmarkConstants.RELATIVE_FIRST_NAME,
						caseInfoDto.getNmNameFirst());
				bookmarkPersonList.add(bookmarkNmFirst);
				BookmarkDto bookmarkNmLast = createBookmark(BookmarkConstants.RELATIVE_LAST_NAME,
						caseInfoDto.getNmNameLast());
				bookmarkPersonList.add(bookmarkNmLast);
				BookmarkDto bookmarkNmMiddle = createBookmark(BookmarkConstants.RELATIVE_MIDDLE_NAME,
						caseInfoDto.getNmNameMiddle());
				bookmarkPersonList.add(bookmarkNmMiddle);
				BookmarkDto bookmarkId = createBookmark(BookmarkConstants.PERSON_NBR, caseInfoDto.getIdPerson());
				bookmarkPersonList.add(bookmarkId);
				personGroupDto.setBookmarkDtoList(bookmarkPersonList);
				formDataGroupList.add(personGroupDto);

			}

		}

		
		if(motherDtlsExist.equals(ServiceConstants.Y))
		{
			FormDataGroupDto personGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MOTHER_HEADER,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(personGroupDto);
		}
		
		String fatherDtlsExist=ServiceConstants.N;
		// parent group csc40o05
		for (CaseInfoDto caseInfoDto : subcareCaseDto.getCaseInfoDtoPrincipal()) {
			if (ServiceConstants.MALE.equals(caseInfoDto.getCdPersonSex())
					&& ServiceConstants.PLACEMENT_PERSON_TYPE.equals(caseInfoDto.getIndPersCancelHist())
					&& !("PC".equalsIgnoreCase(caseInfoDto.getCdStagePersRole()))) {

				fatherDtlsExist=ServiceConstants.Y;
				FormDataGroupDto personGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FATHER,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkZip = createBookmark(BookmarkConstants.ADDR_ZIP,
						caseInfoDto.getAddrPersonAddrZip());
				bookmarkPersonList.add(bookmarkZip);
				BookmarkDto bookmarkPhone = createBookmark(BookmarkConstants.PHONE_NUMBER,
						TypeConvUtil.formatPhone(caseInfoDto.getNbrPersonPhone()));
				bookmarkPersonList.add(bookmarkPhone);
				BookmarkDto bookmarkCity = createBookmark(BookmarkConstants.ADDR_CITY,
						caseInfoDto.getAddrPersonAddrCity());
				bookmarkPersonList.add(bookmarkCity);
				BookmarkDto bookmarkLn1 = createBookmark(BookmarkConstants.ADDR_LN_1,
						caseInfoDto.getAddrPersAddrStLn1());
				bookmarkPersonList.add(bookmarkLn1);
				BookmarkDto bookmarkLn2 = createBookmark(BookmarkConstants.ADDR_LN_2,
						caseInfoDto.getAddrPersAddrStLn2());
				bookmarkPersonList.add(bookmarkLn2);
				BookmarkDto bookmarkState = createBookmark(BookmarkConstants.ADDR_STATE,
						caseInfoDto.getCdPersonAddrState());
				bookmarkPersonList.add(bookmarkState);
				BookmarkDto bookmarkEmail = createBookmark(BookmarkConstants.EMAIL_ADDRESS, caseInfoDto.getTxtEmail());
				bookmarkPersonList.add(bookmarkEmail);
				BookmarkDto bookmarkDtCVSMon = createBookmark(BookmarkConstants.LAST_CVS_MONTHLY_DATE,
						caseInfoDto.getLastContactDate());
				bookmarkPersonList.add(bookmarkDtCVSMon);
				BookmarkDto bookmarkPType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
						caseInfoDto.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
				bookmarkPersonList.add(bookmarkPType);
				BookmarkDto bookmarkRel = createBookmarkWithCodesTable(BookmarkConstants.RELATION,
						caseInfoDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
				bookmarkPersonList.add(bookmarkRel);
				BookmarkDto bookmarkNmFirst = createBookmark(BookmarkConstants.RELATIVE_FIRST_NAME,
						caseInfoDto.getNmNameFirst());
				bookmarkPersonList.add(bookmarkNmFirst);
				BookmarkDto bookmarkNmLast = createBookmark(BookmarkConstants.RELATIVE_LAST_NAME,
						caseInfoDto.getNmNameLast());
				bookmarkPersonList.add(bookmarkNmLast);
				BookmarkDto bookmarkNmMiddle = createBookmark(BookmarkConstants.RELATIVE_MIDDLE_NAME,
						caseInfoDto.getNmNameMiddle());
				bookmarkPersonList.add(bookmarkNmMiddle);
				BookmarkDto bookmarkId = createBookmark(BookmarkConstants.PERSON_NBR, caseInfoDto.getIdPerson());
				bookmarkPersonList.add(bookmarkId);
				personGroupDto.setBookmarkDtoList(bookmarkPersonList);
				formDataGroupList.add(personGroupDto);

			}

		}

		if(fatherDtlsExist.equals(ServiceConstants.Y))
		{
			FormDataGroupDto personGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FATHER_HEADER,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(personGroupDto);
		}
		
		
		if(motherDtlsExist.equals(ServiceConstants.Y)||fatherDtlsExist.equals(ServiceConstants.Y))
		{
			FormDataGroupDto parentHdrGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PARENT_HEADER,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(parentHdrGroupDto);
		}
		
		
		
		String otherPrincipalDtlsExist=ServiceConstants.N;
		// parent group csc40o06
		for (CaseInfoDto caseInfoDto : subcareCaseDto.getCaseInfoDtoPrincipal()) {
			if (!ServiceConstants.STRING_IND_Y.equals(caseInfoDto.getIndStagePersReporter())
					&& ServiceConstants.STRING_IND_N.equals(caseInfoDto.getIndPersCancelHist())
					&& !("PC".equalsIgnoreCase(caseInfoDto.getCdStagePersRole()))) {

				otherPrincipalDtlsExist=ServiceConstants.Y;
				FormDataGroupDto personGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_OTHER_PRINCIPAL,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPhone = createBookmark(BookmarkConstants.OTHER_PRIMARY_PHONE,
						TypeConvUtil.formatPhone(caseInfoDto.getNbrPersonPhone()));
				bookmarkPersonList.add(bookmarkPhone);
				BookmarkDto bookmarkPType = createBookmarkWithCodesTable(BookmarkConstants.OTHER_PHONE_TYPE,
						caseInfoDto.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
				bookmarkPersonList.add(bookmarkPType);
				BookmarkDto bookmarkRel = createBookmarkWithCodesTable(BookmarkConstants.OTHER_RELATIONSHIP,
						caseInfoDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
				bookmarkPersonList.add(bookmarkRel);
				BookmarkDto bookmarkEmail = createBookmark(BookmarkConstants.EMAIL_ADDRESS, caseInfoDto.getTxtEmail());
				bookmarkPersonList.add(bookmarkEmail);
				BookmarkDto bookmarkNmFull = createBookmark(BookmarkConstants.OTHER_NAME_FULL, getFullName(
						caseInfoDto.getNmNameFirst(), caseInfoDto.getNmNameLast(), caseInfoDto.getNmNameMiddle()));
				bookmarkPersonList.add(bookmarkNmFull);
				personGroupDto.setBookmarkDtoList(bookmarkPersonList);
				formDataGroupList.add(personGroupDto);

			}

		}

		// parent group csc40o11
		for (CaseInfoDto caseInfoDto : subcareCaseDto.getCaseInfoDtoCollateral()) {
			if (!ServiceConstants.STRING_IND_Y.equals(caseInfoDto.getIndStagePersReporter())) {

				otherPrincipalDtlsExist=ServiceConstants.Y;
				FormDataGroupDto personGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_OTHER_COLLATERAL,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPhone = createBookmark(BookmarkConstants.OTHER_PRIMARY_PHONE,
						TypeConvUtil.formatPhone(caseInfoDto.getNbrPersonPhone()));
				bookmarkPersonList.add(bookmarkPhone);
				BookmarkDto bookmarkPType = createBookmarkWithCodesTable(BookmarkConstants.OTHER_PHONE_TYPE,
						caseInfoDto.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
				bookmarkPersonList.add(bookmarkPType);
				BookmarkDto bookmarkRel = createBookmarkWithCodesTable(BookmarkConstants.OTHER_RELATIONSHIP,
						caseInfoDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
				bookmarkPersonList.add(bookmarkRel);
				BookmarkDto bookmarkNmFull = createBookmark(BookmarkConstants.OTHER_NAME_FULL, getFullName(
						caseInfoDto.getNmNameFirst(), caseInfoDto.getNmNameLast(), caseInfoDto.getNmNameMiddle()));
				bookmarkPersonList.add(bookmarkNmFull);
				personGroupDto.setBookmarkDtoList(bookmarkPersonList);
				formDataGroupList.add(personGroupDto);

			}

		}
		
		
		if(otherPrincipalDtlsExist.equals(ServiceConstants.Y))
		{
			FormDataGroupDto othersInvolvedHeaderGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_OTHERS_INVOLVED_HEADER,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(othersInvolvedHeaderGroupDto);
			FormDataGroupDto othersInvolvedTableHeaderGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_OTHERS_INVOLVED_TABLE_HEADER,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(othersInvolvedTableHeaderGroupDto);
		}

		// bookmarks without groups
		List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();

		// Populate value from DAM CSEC15D
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				subcareCaseDto.getStagePersonLinkCaseDto().getNmCase());
		bookmarkNonFormGrpList.add(bookmarkNmCase);

		BookmarkDto bookmarkDtpersonBirth = createBookmark(BookmarkConstants.CHILD_DOB,
				DateUtils.stringDt(subcareCaseDto.getStagePersonLinkCaseDto().getDtPersonBirth()));
		bookmarkNonFormGrpList.add(bookmarkDtpersonBirth);

		BookmarkDto bookmarkNmPersonFull = createBookmark(BookmarkConstants.CHILD_NAME_FULL,
				subcareCaseDto.getStagePersonLinkCaseDto().getNmPersonFull());
		bookmarkNonFormGrpList.add(bookmarkNmPersonFull);

		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_CASE_NBR,
				subcareCaseDto.getStagePersonLinkCaseDto().getIdCase());
		bookmarkNonFormGrpList.add(bookmarkIdCase);

		// Warranty Defect - 11113 - Incorrect Mapping of Date - Stage Current Date was mapped instead of 
		// Stage Start Date
		BookmarkDto bookmarkDate = createBookmark(BookmarkConstants.CONSERVATORSHIP_DATE,
				DateUtils.stringDt(subcareCaseDto.getStagePersonLinkCaseDto().getDtStageStart()));
		bookmarkNonFormGrpList.add(bookmarkDate);

		// Populate value from DAM CCMN72D
		if (!ObjectUtils.isEmpty(subcareCaseDto.getPersonIdDtoSsn())) {
			if (!ObjectUtils.isEmpty(subcareCaseDto.getPersonIdDtoSsn().getPersonIdNumber())) {
				BookmarkDto bookmarkPersonIdNumber = createBookmark(BookmarkConstants.CHILD_SSN,
						subcareCaseDto.getPersonIdDtoSsn().getPersonIdNumber());
				bookmarkNonFormGrpList.add(bookmarkPersonIdNumber);
			}
		}
		if (!ObjectUtils.isEmpty(subcareCaseDto.getPersonIdDtoMed())) {
			if (!ObjectUtils.isEmpty(subcareCaseDto.getPersonIdDtoMed().getPersonIdNumber())) {
				BookmarkDto bookmarkPersonIdNumberMed = createBookmark(BookmarkConstants.CHILD_MEDICAID_NBR,
						subcareCaseDto.getPersonIdDtoMed().getPersonIdNumber());
				bookmarkNonFormGrpList.add(bookmarkPersonIdNumberMed);
			}
		}

		if (!ObjectUtils.isEmpty(subcareCaseDto.getPersonIdDtoSsn())) {
			if (!ObjectUtils.isEmpty(subcareCaseDto.getPersonIdDtoSsn().getIdPerson())) {
				BookmarkDto bookmarkPersonId = createBookmark(BookmarkConstants.CHILD_PID,
						subcareCaseDto.getPersonIdDtoSsn().getIdPerson());
				bookmarkNonFormGrpList.add(bookmarkPersonId);
			}
		}

		// Populate value from DAM CSES78D
		if (!ObjectUtils.isEmpty(subcareCaseDto.getSubcareLegalEnrollDtoLeg())) {
			BookmarkDto bookmarkCnty = createBookmarkWithCodesTable(BookmarkConstants.LEGAL_COUNTY,
					subcareCaseDto.getSubcareLegalEnrollDtoLeg().getCdLegalStatCnty(), CodesConstant.CCOUNT);
			bookmarkNonFormGrpList.add(bookmarkCnty);
			BookmarkDto bookmarkStatus = createBookmarkWithCodesTable(BookmarkConstants.LEGAL_STATUS,
					subcareCaseDto.getSubcareLegalEnrollDtoLeg().getCdLegalStatStatus(), CodesConstant.CLEGSTAT);
			bookmarkNonFormGrpList.add(bookmarkStatus);
			BookmarkDto bookmarkCourtNbr = createBookmark(BookmarkConstants.LEGAL_COURT_NUMBER,
					subcareCaseDto.getSubcareLegalEnrollDtoLeg().getTxtLegalStatCourtNbr());
			bookmarkNonFormGrpList.add(bookmarkCourtNbr);
		}

		// Populate value from DAM CLSS64D
		if (!ObjectUtils.isEmpty(subcareCaseDto.getSubcareLegalEnrollDtoMom())) {
			BookmarkDto bookmarkStatusDtM = createBookmark(BookmarkConstants.MO_RIGHTS_TERM,
					subcareCaseDto.getSubcareLegalEnrollDtoMom().getDtLegalStatStatus());
			bookmarkNonFormGrpList.add(bookmarkStatusDtM);
		}
		if (!ObjectUtils.isEmpty(subcareCaseDto.getSubcareLegalEnrollDtoDad())) {
			BookmarkDto bookmarkStatusDtF = createBookmark(BookmarkConstants.FA_RIGHTS_TERM,
					subcareCaseDto.getSubcareLegalEnrollDtoDad().getDtLegalStatStatus());
			bookmarkNonFormGrpList.add(bookmarkStatusDtF);
		}
		if (!ObjectUtils.isEmpty(subcareCaseDto.getSubcareLegalEnrollDtoAll())) {
			BookmarkDto bookmarkStatusDtO = createBookmark(BookmarkConstants.OTHER_RIGHTS_TERM,
					DateUtils.stringDt(subcareCaseDto.getSubcareLegalEnrollDtoAll().getDtLastUpdate()));
			bookmarkNonFormGrpList.add(bookmarkStatusDtO);
		}
		if (!ObjectUtils.isEmpty(subcareCaseDto.getSubcareLegalEnrollDtoTmc())) {
			BookmarkDto bookmarkTMCDismiss = createBookmark(BookmarkConstants.DISMISSAL_DATE,
					DateUtils.stringDt(subcareCaseDto.getSubcareLegalEnrollDtoTmc().getDtLegalStatTmcDissmiss()));
			bookmarkNonFormGrpList.add(bookmarkTMCDismiss);
			BookmarkDto bookmarkCauseNbr = createBookmark(BookmarkConstants.CHILD_CAUSE_NBR,
					subcareCaseDto.getSubcareLegalEnrollDtoTmc().getTxtLegalStatCauseNbr());
			bookmarkNonFormGrpList.add(bookmarkCauseNbr);
			BookmarkDto bookmarkAdditionalContact = createBookmark(BookmarkConstants.ADDITIONAL_CONTACT_INFORMATION,
					subcareCaseDto.getSubcareLegalEnrollDtoTmc().getTxtLegalStatCauseNbr());
			bookmarkNonFormGrpList.add(bookmarkAdditionalContact);
		}

		// Populate value from DAM CSECB5D
		BookmarkDto bookmarkDTLegalActOutcomeDt = createBookmark(BookmarkConstants.LAST_REVIEW_HEARING_DATE,
				DateUtils.stringDt(subcareCaseDto.getHearingDate()));
		bookmarkNonFormGrpList.add(bookmarkDTLegalActOutcomeDt);

		BookmarkDto bookmarkDTNext = createBookmark(BookmarkConstants.NEXT_REVIEW_HEARING_DATE,
				DateUtils.stringDt(subcareCaseDto.getHearingDate()));
		bookmarkNonFormGrpList.add(bookmarkDTNext);

		// Populate value from DAM CSES34D
		if (!ObjectUtils.isEmpty(subcareCaseDto.getPlacementActPlannedOutDto())) {
			BookmarkDto bookmarkDtPlcmtStart = createBookmark(BookmarkConstants.PLACEMENT_DATE,
					DateUtils.stringDt(subcareCaseDto.getPlacementActPlannedOutDto().getDtPlcmtStart()));
			bookmarkNonFormGrpList.add(bookmarkDtPlcmtStart);
			BookmarkDto bookmarkDtPlcmtCity = createBookmark(BookmarkConstants.ADDR_PLCMT_CITY,
					subcareCaseDto.getPlacementActPlannedOutDto().getAddrPlcmtCity());
			bookmarkNonFormGrpList.add(bookmarkDtPlcmtCity);
			BookmarkDto bookmarkDtPlcmtLn1 = createBookmark(BookmarkConstants.ADDR_PLCMT_LN1,
					subcareCaseDto.getPlacementActPlannedOutDto().getAddrPlcmtLn1());
			bookmarkNonFormGrpList.add(bookmarkDtPlcmtLn1);
			BookmarkDto bookmarkDtPlcmtLn2 = createBookmark(BookmarkConstants.ADDR_PLCMT_LN2,
					subcareCaseDto.getPlacementActPlannedOutDto().getAddrPlcmtLn2());
			bookmarkNonFormGrpList.add(bookmarkDtPlcmtLn2);
			BookmarkDto bookmarkDtPlcmtState = createBookmark(BookmarkConstants.ADDR_PLCMT_STATE,
					subcareCaseDto.getPlacementActPlannedOutDto().getAddrPlcmtSt());
			bookmarkNonFormGrpList.add(bookmarkDtPlcmtState);
			BookmarkDto bookmarkDtPlcmtZip = createBookmark(BookmarkConstants.ADDR_PLCMT_ZIP,
					subcareCaseDto.getPlacementActPlannedOutDto().getAddrPlcmtZip());
			bookmarkNonFormGrpList.add(bookmarkDtPlcmtZip);
			BookmarkDto bookmarkDtPlcmtPhone = createBookmark(BookmarkConstants.PLCMT_PHONE,
					TypeConvUtil.formatPhone(subcareCaseDto.getPlacementActPlannedOutDto().getPlcmtTelephone()));
			bookmarkNonFormGrpList.add(bookmarkDtPlcmtPhone);
			BookmarkDto bookmarkDtPlcmtName = createBookmark(BookmarkConstants.PLACEMENT_NAME,
					subcareCaseDto.getPlacementActPlannedOutDto().getNmPlcmtFacil());
			bookmarkNonFormGrpList.add(bookmarkDtPlcmtName);
		}

		// Populate value from DAM CSES35D
		if (!ObjectUtils.isEmpty(subcareCaseDto.getPersonBlocLocDto())) {
			BookmarkDto bookmarkDtPlocStart = createBookmark(BookmarkConstants.LOC_EFFECT_DATE,
					DateUtils.stringDt(subcareCaseDto.getPersonBlocLocDto().getDtPlocStart()));
			bookmarkNonFormGrpList.add(bookmarkDtPlocStart);
			BookmarkDto bookmarkPlocChild = createBookmarkWithCodesTable(BookmarkConstants.LOC,
					subcareCaseDto.getPersonBlocLocDto().getCdPlocChild(), CodesConstant.CREQPLOC);
			bookmarkNonFormGrpList.add(bookmarkPlocChild);
		}

		// Populate value from DAM CSVC46D
		if (!ObjectUtils.isEmpty(subcareCaseDto.getSubcareChildContactDtoChild())) {
			BookmarkDto bookmarkDtApprovDeterm = createBookmark(BookmarkConstants.APRV_CPOS_DATE,
					DateUtils.stringDt(subcareCaseDto.getSubcareChildContactDtoChild().getDtApprovDeterm()));
			bookmarkNonFormGrpList.add(bookmarkDtApprovDeterm);
			BookmarkDto bookmarkDtNext = createBookmark(BookmarkConstants.NEXT_CPOS_DATE,
					DateUtils.stringDt(subcareCaseDto.getSubcareChildContactDtoChild().getDtCspNxtReview()));
			bookmarkNonFormGrpList.add(bookmarkDtNext);
			BookmarkDto bookmarkPermGoal = createBookmarkWithCodesTable(BookmarkConstants.PERMANENCY_PLAN,
					subcareCaseDto.getSubcareChildContactDtoChild().getCdCspPlanPermGoal(), CodesConstant.CCPPRMGL);
			bookmarkNonFormGrpList.add(bookmarkPermGoal);
		}
		if (!ObjectUtils.isEmpty(subcareCaseDto.getConcurrentGoals())) {

			for (String temp : subcareCaseDto.getConcurrentGoals()) {
				FormDataGroupDto concurrentPlans = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CONCURRENT_PERMANENCY_PLAN, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkConList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkConGoal = createBookmarkWithCodesTable(BookmarkConstants.CONCURRENT_PERMANENCY_PLAN,
						temp, CodesConstant.CCPPRMGL);
				bookmarkConList.add(bookmarkConGoal);
				concurrentPlans.setBookmarkDtoList(bookmarkConList);
				formDataGroupList.add(concurrentPlans);
			}
		}
		// Populate value from DAM CSECB7D
		if (!ObjectUtils.isEmpty(subcareCaseDto.getPptDate())) {
			BookmarkDto bookmarkDTPptDate = createBookmark(BookmarkConstants.LAST_PPT_DATE,
					DateUtils.stringDt(subcareCaseDto.getPptDate()));
			bookmarkNonFormGrpList.add(bookmarkDTPptDate);
		}
		// Populate value from DAM CSECB2D
		if (!ObjectUtils.isEmpty(subcareCaseDto.getPlcmntDate())) {
			BookmarkDto bookmarkDTPlcmntDate = createBookmark(BookmarkConstants.LAST_VISIT_PLCMT_DATE,
					DateUtils.stringDt(subcareCaseDto.getPlcmntDate()));
			bookmarkNonFormGrpList.add(bookmarkDTPlcmntDate);
		}

		// Populate value from DAM CSECB1D
		if (!ObjectUtils.isEmpty(subcareCaseDto.getTcmDate())) {
			BookmarkDto bookmarkDTTcmDate = createBookmark(BookmarkConstants.LAST_TCM_CONTACT_DATE,
					DateUtils.stringDt(subcareCaseDto.getTcmDate()));
			bookmarkNonFormGrpList.add(bookmarkDTTcmDate);
		}
		// Populate value from DAM CSVC47D
		if (!ObjectUtils.isEmpty(subcareCaseDto.getSubcareChildContactDtoGmth())) {
			BookmarkDto bookmarkDTGmthDate = createBookmark(BookmarkConstants.APRVD_NARR_DATE,
					DateUtils.stringDt(subcareCaseDto.getSubcareChildContactDtoGmth().getDtContactOccured()));
			bookmarkNonFormGrpList.add(bookmarkDTGmthDate);
		}

		// Populate value from DAM CSECB3D
		if (!ObjectUtils.isEmpty(subcareCaseDto.getApptDate())) {
			BookmarkDto bookmarkDTApptDate = createBookmark(BookmarkConstants.LAST_MEDICAL_DATE,
					DateUtils.stringDt(subcareCaseDto.getApptDate()));
			bookmarkNonFormGrpList.add(bookmarkDTApptDate);
		}
		if (!ObjectUtils.isEmpty(subcareCaseDto.getApptDateDental())) {
			BookmarkDto bookmarkDTApptDateDental = createBookmark(BookmarkConstants.LAST_DENTAL_DATE,
					DateUtils.stringDt(subcareCaseDto.getApptDateDental()));
			bookmarkNonFormGrpList.add(bookmarkDTApptDateDental);
		}
		if (!ObjectUtils.isEmpty(subcareCaseDto.getApptDatePsych())) {
			BookmarkDto bookmarkDTApptDatePsych = createBookmark(BookmarkConstants.LAST_PSYCH_DATE,
					DateUtils.stringDt(subcareCaseDto.getApptDatePsych()));
			bookmarkNonFormGrpList.add(bookmarkDTApptDatePsych);
		}
		// Populate value from DAM CSECB8D
		if (!ObjectUtils.isEmpty(subcareCaseDto.getChildFpos())) {
			BookmarkDto bookmarkDTChildFpos = createBookmark(BookmarkConstants.APRV_FPOS_DATE,
					DateUtils.stringDt(subcareCaseDto.getChildFpos()));
			bookmarkNonFormGrpList.add(bookmarkDTChildFpos);
		}

		// Populate value from DAM CSES33D
		if (!ObjectUtils.isEmpty(subcareCaseDto.getSubcareLegalEnrollDtoSchool())) {
			BookmarkDto bookmarkDTEnrollGrade = createBookmarkWithCodesTable(BookmarkConstants.GRADE_LEVEL,
					subcareCaseDto.getSubcareLegalEnrollDtoSchool().getCdHistEnrollGrade(), CodesConstant.CSCHGRAD);
			bookmarkNonFormGrpList.add(bookmarkDTEnrollGrade);
			BookmarkDto bookmarkNeed = createBookmark(BookmarkConstants.CEDUCNED_SPE,
					subcareCaseDto.getSubcareLegalEnrollDtoSchool().getCdEdNeed());
			bookmarkNonFormGrpList.add(bookmarkNeed);
			BookmarkDto bookmarkSchool = createBookmark(BookmarkConstants.SCHOOL_NAME,
					subcareCaseDto.getSubcareLegalEnrollDtoSchool().getNmHistSchool());
			bookmarkNonFormGrpList.add(bookmarkSchool);
			BookmarkDto bookmarkDistrict = createBookmark(BookmarkConstants.SCHOOL_DISTRICT,
					subcareCaseDto.getSubcareLegalEnrollDtoSchool().getNmHistSchDist());
			bookmarkNonFormGrpList.add(bookmarkDistrict);
			BookmarkDto bookmarkDtARD = createBookmark(BookmarkConstants.LAST_ARD_DATE,
					subcareCaseDto.getSubcareLegalEnrollDtoSchool().getDtLastArdiep());
			bookmarkNonFormGrpList.add(bookmarkDtARD);
			BookmarkDto bookmarkDtEnrolled = createBookmark(BookmarkConstants.ENROLLED_DATE,
					subcareCaseDto.getSubcareLegalEnrollDtoSchool().getDtHistEnrollDate());
			bookmarkNonFormGrpList.add(bookmarkDtEnrolled);
			String indicator;
			if (!ObjectUtils.isEmpty(subcareCaseDto.getIndicator())
					&& "Y".equalsIgnoreCase(subcareCaseDto.getIndicator())) {
				indicator = BookmarkConstants.YES;
			} else
				indicator = BookmarkConstants.NO;
			BookmarkDto bookmarkPortfolio = createBookmark(BookmarkConstants.EDUCATION_PORTFOLIO, indicator);
			bookmarkNonFormGrpList.add(bookmarkPortfolio);
			BookmarkDto bookmarkSect504 = createBookmark(BookmarkConstants.SECTION_504,
					subcareCaseDto.getSubcareLegalEnrollDtoSchool().getTxtSpecialAccmdtns());
			bookmarkNonFormGrpList.add(bookmarkSect504);
		}

		if (!ObjectUtils.isEmpty(subcareCaseDto.getSchoolPrograms())) {
			for (String temp : subcareCaseDto.getSchoolPrograms()) {
				FormDataGroupDto schoolProgramsDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SCHOOL_PROGRAMS,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSchoolProgList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkDTEnrollGrade = createBookmarkWithCodesTable(BookmarkConstants.SCHOOL_PROGRAMS,
						temp, CodesConstant.CEDUCNED);
				bookmarkSchoolProgList.add(bookmarkDTEnrollGrade);
				schoolProgramsDto.setBookmarkDtoList(bookmarkSchoolProgList);
				formDataGroupList.add(schoolProgramsDto);
			}
		}

		String todoListDtls=ServiceConstants.N;
		if (!ObjectUtils.isEmpty(subcareCaseDto.getTodoList())) {
			for (CaseInfoDto caseInfoDto : subcareCaseDto.getTodoList()) {
				todoListDtls=ServiceConstants.Y;
				FormDataGroupDto todoList = createFormDataGroup(FormGroupsConstants.TMPLAT_TODO_LIST,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkTypeList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkType = createBookmarkWithCodesTable(BookmarkConstants.TYPE,
						caseInfoDto.getCdTodoType(), CodesConstant.CTODOTYP);
				bookmarkTypeList.add(bookmarkType);
				BookmarkDto bookmarkDescription = createBookmark(BookmarkConstants.DESCRIPTION,
						caseInfoDto.getTxtTodoDesc());
				bookmarkTypeList.add(bookmarkDescription);
				todoList.setBookmarkDtoList(bookmarkTypeList);
				formDataGroupList.add(todoList);
			}
		}
		
		if(todoListDtls.equals(ServiceConstants.Y))
		{
			FormDataGroupDto todoListHeaderGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_TODO_LIST_HEADER,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(todoListHeaderGroupDto);
			FormDataGroupDto todoListTableHeaderGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_TODO_LIST_TABLE_HEADER,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(todoListTableHeaderGroupDto);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);

		return preFillData;
	}

	private String getFullName(String first, String last, String middle) {
		String fullName = "";
		if (!ObjectUtils.isEmpty(last)) {
			fullName = fullName + last + ",";
		}
		if (!ObjectUtils.isEmpty(first)) {
			fullName = fullName + first;
		}
		if (!ObjectUtils.isEmpty(middle)) {
			fullName = fullName + " " + middle.charAt(0);
		}

		return fullName;
	}

}
