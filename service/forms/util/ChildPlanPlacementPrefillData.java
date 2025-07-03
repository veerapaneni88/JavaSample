package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.PersonAddressDto;
import us.tx.state.dfps.common.web.WebConstants;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.ChildPlanPlacementDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.pca.dto.PlacementDtlDto;
import us.tx.state.dfps.service.placement.dto.ApprovalInfoDto;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Populates
 * prefill string for Child Plan Placement form Mar 26, 2018- 9:52:32 AM © 2017
 * Texas Department of Family and Protective Services
 */
@Component
public class ChildPlanPlacementPrefillData extends DocumentServiceUtil {

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		ChildPlanPlacementDto prefillDto = (ChildPlanPlacementDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		// Initializing null DTOs
		if (ObjectUtils.isEmpty(prefillDto.getApprovalInfoDto())) {
			prefillDto.setApprovalInfoDto(new ApprovalInfoDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getNameDetailDto())) {
			prefillDto.setNameDetailDto(new NameDetailDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getPersonAddressDto())) {
			prefillDto.setPersonAddressDto(new PersonAddressDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getPlacementDtlDto())) {
			prefillDto.setPlacementDtlDto(new PlacementDtlDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getStagePersonLinkCaseDto())) {
			prefillDto.setStagePersonLinkCaseDto(new StagePersonLinkCaseDto());
		}

		// add groups
		// group cfzco00 (x3)
		if (StringUtils.isNotBlank(prefillDto.getStagePersonLinkCaseDto().getCdPersonSuffix())) {
			FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(commaGroupDto);
		}
		if (StringUtils.isNotBlank(prefillDto.getNameDetailDto().getCdNameSuffix())) {
			FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(commaGroupDto);
		}
		if (StringUtils.isNotBlank(prefillDto.getApprovalInfoDto().getCdNameSuffix())) {
			FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(commaGroupDto);
		}

		// group csc37o01
		FormDataGroupDto approvalGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_APPROVAL,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkApprovalList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkApprovalDate = createBookmark(BookmarkConstants.DT_APPROVAL_DATE,
				DateUtils.stringDt(prefillDto.getApprovalInfoDto().getDtApproversDetermination()));
		bookmarkApprovalList.add(bookmarkApprovalDate);
		BookmarkDto bookmarkNmSuffix = createBookmarkWithCodesTable(BookmarkConstants.NM_PLCMT_APRV_SUFFIX,
				prefillDto.getApprovalInfoDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkApprovalList.add(bookmarkNmSuffix);
		BookmarkDto bookmarkNmFirst = createBookmark(BookmarkConstants.NM_PLCMT_APRV_FIRST,
				prefillDto.getApprovalInfoDto().getNmNameFirst());
		bookmarkApprovalList.add(bookmarkNmFirst);
		BookmarkDto bookmarkNmLast = createBookmark(BookmarkConstants.NM_PLCMT_APRV_LAST,
				prefillDto.getApprovalInfoDto().getNmNameLast());
		bookmarkApprovalList.add(bookmarkNmLast);
		BookmarkDto bookmarkNmMiddle = createBookmark(BookmarkConstants.NM_PLCMT_APRV_MIDDLE,
				prefillDto.getApprovalInfoDto().getNmNameMiddle());
		bookmarkApprovalList.add(bookmarkNmMiddle);
		approvalGroupDto.setBookmarkDtoList(bookmarkApprovalList);
		formDataGroupList.add(approvalGroupDto);

		// Non-group bookmarks
		List<BookmarkDto> bookmarkNonFormList = new ArrayList<BookmarkDto>();

		// CSEC15D
		BookmarkDto bookmarkChildDob = createBookmark(BookmarkConstants.TITLE_CHILD_DOB,
				DateUtils.stringDt(prefillDto.getStagePersonLinkCaseDto().getDtPersonBirth()));
		bookmarkNonFormList.add(bookmarkChildDob);
		BookmarkDto bookmarkChildNmSuffix = createBookmarkWithCodesTable(BookmarkConstants.TITLE_CHILD_NAME_SUFFIX,
				prefillDto.getStagePersonLinkCaseDto().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFormList.add(bookmarkChildNmSuffix);
		BookmarkDto bookmarkCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				prefillDto.getStagePersonLinkCaseDto().getNmCase());
		bookmarkNonFormList.add(bookmarkCaseName);
		BookmarkDto bookmarkChildNmFirst = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_FIRST,
				prefillDto.getStagePersonLinkCaseDto().getNmPersonFirst());
		bookmarkNonFormList.add(bookmarkChildNmFirst);
		BookmarkDto bookmarkChildNmLast = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_LAST,
				prefillDto.getStagePersonLinkCaseDto().getNmPersonLast());
		bookmarkNonFormList.add(bookmarkChildNmLast);
		BookmarkDto bookmarkChildNmMiddle = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_MIDDLE,
				prefillDto.getStagePersonLinkCaseDto().getNmPersonMiddle());
		bookmarkNonFormList.add(bookmarkChildNmMiddle);
			BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
					prefillDto.getStagePersonLinkCaseDto().getIdCase());
			bookmarkNonFormList.add(bookmarkIdCase);


		// CSES37D
		BookmarkDto bookmarkIndPlcmtEmerg = createBookmark(BookmarkConstants.IND_PLCMT_EMERG,
				prefillDto.getPlacementDtlDto().getIndPlcmtEmerg());
		bookmarkNonFormList.add(bookmarkIndPlcmtEmerg);

		if(prefillDto.getPlacementDtlDto().getIndT3cPlcmt()!=null) {
			BookmarkDto bookmarkIndT3C = createBookmark(BookmarkConstants.IND_T3C_SERVICE,
					prefillDto.getPlacementDtlDto().getIndT3cPlcmt());
			bookmarkNonFormList.add(bookmarkIndT3C);
	   } else {
			BookmarkDto bookmarkIndT3C = createBookmark(BookmarkConstants.IND_T3C_SERVICE,
					WebConstants.NO);
			bookmarkNonFormList.add(bookmarkIndT3C);
		}
		BookmarkDto bookmarkIndPlcmtContact = createBookmark(BookmarkConstants.IND_PLCMT_CONT_CNTCT,
				prefillDto.getPlacementDtlDto().getIndPlcmtContCntct());
		bookmarkNonFormList.add(bookmarkIndPlcmtContact);
		BookmarkDto bookmarkIndPlcmtEducLog = createBookmark(BookmarkConstants.IND_PLCMT_EDUC_LOG,
				prefillDto.getPlacementDtlDto().getIndPlcmtEducLog());
		bookmarkNonFormList.add(bookmarkIndPlcmtEducLog);
		BookmarkDto bookmarkIndPlcmtSchoolDoc = createBookmark(BookmarkConstants.IND_PLCMT_SCHOOL_DOC,
				prefillDto.getPlacementDtlDto().getIndPlcmtSchoolDoc());
		bookmarkNonFormList.add(bookmarkIndPlcmtSchoolDoc);
		BookmarkDto bookmarkDtPlcmtCaregvrDiscuss = createBookmark(BookmarkConstants.DT_PLCMT_CAREGVR_DISCUSS,
				DateUtils.stringDt(prefillDto.getPlacementDtlDto().getDtPlcmtCaregvrDiscuss()));
		bookmarkNonFormList.add(bookmarkDtPlcmtCaregvrDiscuss);
		BookmarkDto bookmarkDtPlcmtChildDiscuss = createBookmark(BookmarkConstants.DT_PLCMT_CHILD_DISCUSS,
				DateUtils.stringDt(prefillDto.getPlacementDtlDto().getDtPlcmtChildDiscuss()));
		bookmarkNonFormList.add(bookmarkDtPlcmtChildDiscuss);
		BookmarkDto bookmarkDtPlcmtChildPlan = createBookmark(BookmarkConstants.DT_PLCMT_CHILD_PLAN,
				DateUtils.stringDt(prefillDto.getPlacementDtlDto().getDtPlcmtChildPlan()));
		bookmarkNonFormList.add(bookmarkDtPlcmtChildPlan);
		BookmarkDto bookmarkDtPlcmtEducLog = createBookmark(BookmarkConstants.DT_PLCMT_EDUC_LOG,
				DateUtils.stringDt(prefillDto.getPlacementDtlDto().getDtPlcmtEducLog()));
		bookmarkNonFormList.add(bookmarkDtPlcmtEducLog);
		BookmarkDto bookmarkDtPlcmtEnd = createBookmark(BookmarkConstants.DT_PLCMT_END,
				DateUtils.stringDt(prefillDto.getPlacementDtlDto().getDtPlcmtEnd()));
		bookmarkNonFormList.add(bookmarkDtPlcmtEnd);
		BookmarkDto bookmarkDtPlcmtMeddevHistory = createBookmark(BookmarkConstants.DT_PLCMT_MEDDEV_HISTORY,
				DateUtils.stringDt(prefillDto.getPlacementDtlDto().getDtPlcmtMeddevHistory()));
		bookmarkNonFormList.add(bookmarkDtPlcmtMeddevHistory);
		BookmarkDto bookmarkDtPlcmtParentsNotif = createBookmark(BookmarkConstants.DT_PLCMT_PARENTS_NOTIF,
				DateUtils.stringDt(prefillDto.getPlacementDtlDto().getDtPlcmtParentsNotif()));
		bookmarkNonFormList.add(bookmarkDtPlcmtParentsNotif);
		BookmarkDto bookmarkDtPlcmtPreplaceVisit = createBookmark(BookmarkConstants.DT_PLCMT_PREPLACE_VISIT,
				DateUtils.stringDt(prefillDto.getPlacementDtlDto().getDtPlcmtPreplaceVisit()));
		bookmarkNonFormList.add(bookmarkDtPlcmtPreplaceVisit);
		BookmarkDto bookmarkDtPlcmtSchoolRecords = createBookmark(BookmarkConstants.DT_PLCMT_SCHOOL_RECORDS,
				DateUtils.stringDt(prefillDto.getPlacementDtlDto().getDtPlcmtSchoolRecords()));
		bookmarkNonFormList.add(bookmarkDtPlcmtSchoolRecords);
		BookmarkDto bookmarkDtPlcmtStart = createBookmark(BookmarkConstants.DT_PLCMT_START,
				DateUtils.stringDt(prefillDto.getPlacementDtlDto().getDtPlcmtStart()));
		bookmarkNonFormList.add(bookmarkDtPlcmtStart);
		BookmarkDto bookmarkAddrPlcmtCity = createBookmark(BookmarkConstants.ADDR_PLCMT_CITY,
				prefillDto.getPlacementDtlDto().getAddrPlcmtCity());
		bookmarkNonFormList.add(bookmarkAddrPlcmtCity);
		BookmarkDto bookmarkAddrPlcmtCnty = createBookmarkWithCodesTable(BookmarkConstants.ADDR_PLCMT_CNTY,
				prefillDto.getPlacementDtlDto().getAddrPlcmtCnty(), CodesConstant.CCOUNT);
		bookmarkNonFormList.add(bookmarkAddrPlcmtCnty);
		BookmarkDto bookmarkAddrPlcmtLn1 = createBookmark(BookmarkConstants.ADDR_PLCMT_LN1,
				prefillDto.getPlacementDtlDto().getAddrPlcmtLn1());
		bookmarkNonFormList.add(bookmarkAddrPlcmtLn1);
		BookmarkDto bookmarkAddrPlcmtLn2 = createBookmark(BookmarkConstants.ADDR_PLCMT_LN2,
				prefillDto.getPlacementDtlDto().getAddrPlcmtLn2());
		bookmarkNonFormList.add(bookmarkAddrPlcmtLn2);
		BookmarkDto bookmarkAddrPlcmtSt = createBookmarkWithCodesTable(BookmarkConstants.ADDR_PLCMT_ST,
				prefillDto.getPlacementDtlDto().getAddrPlcmtSt(), CodesConstant.CSTATE);
		bookmarkNonFormList.add(bookmarkAddrPlcmtSt);
		BookmarkDto bookmarkAddrPlcmtZip = createBookmark(BookmarkConstants.ADDR_PLCMT_ZIP,
				prefillDto.getPlacementDtlDto().getAddrPlcmtZip());
		bookmarkNonFormList.add(bookmarkAddrPlcmtZip);
		BookmarkDto bookmarkPlcmtInfo1 = createBookmarkWithCodesTable(BookmarkConstants.CD_PLCMT_INFO_1,
				prefillDto.getPlacementDtlDto().getCdPlcmtInfo1(), CodesConstant.CPLCMTIN);
		bookmarkNonFormList.add(bookmarkPlcmtInfo1);
		BookmarkDto bookmarkPlcmtInfo2 = createBookmarkWithCodesTable(BookmarkConstants.CD_PLCMT_INFO_2,
				prefillDto.getPlacementDtlDto().getCdPlcmtInfo2(), CodesConstant.CPLCMTIN);
		bookmarkNonFormList.add(bookmarkPlcmtInfo2);
		BookmarkDto bookmarkPlcmtInfo3 = createBookmarkWithCodesTable(BookmarkConstants.CD_PLCMT_INFO_3,
				prefillDto.getPlacementDtlDto().getCdPlcmtInfo3(), CodesConstant.CPLCMTIN);
		bookmarkNonFormList.add(bookmarkPlcmtInfo3);
		BookmarkDto bookmarkPlcmtInfo4 = createBookmarkWithCodesTable(BookmarkConstants.CD_PLCMT_INFO_4,
				prefillDto.getPlacementDtlDto().getCdPlcmtInfo4(), CodesConstant.CPLCMTIN);
		bookmarkNonFormList.add(bookmarkPlcmtInfo4);
		BookmarkDto bookmarkPlcmtInfo5 = createBookmarkWithCodesTable(BookmarkConstants.CD_PLCMT_INFO_5,
				prefillDto.getPlacementDtlDto().getCdPlcmtInfo5(), CodesConstant.CPLCMTIN);
		bookmarkNonFormList.add(bookmarkPlcmtInfo5);
		BookmarkDto bookmarkPlcmtInfo6 = createBookmarkWithCodesTable(BookmarkConstants.CD_PLCMT_INFO_6,
				prefillDto.getPlacementDtlDto().getCdPlcmtInfo6(), CodesConstant.CPLCMTIN);
		bookmarkNonFormList.add(bookmarkPlcmtInfo6);
		BookmarkDto bookmarkPlcmtInfo7 = createBookmarkWithCodesTable(BookmarkConstants.CD_PLCMT_INFO_7,
				prefillDto.getPlacementDtlDto().getCdPlcmtInfo7(), CodesConstant.CPLCMTIN);
		bookmarkNonFormList.add(bookmarkPlcmtInfo7);
		BookmarkDto bookmarkPlcmtLivArr = createBookmarkWithCodesTable(BookmarkConstants.CD_PLCMT_LIV_ARR,
				prefillDto.getPlacementDtlDto().getCdPlcmtLivArr(), CodesConstant.CPLLAFRM);
		bookmarkNonFormList.add(bookmarkPlcmtLivArr);
		BookmarkDto bookmarkPlcmtRemovalRsn = createBookmarkWithCodesTable(BookmarkConstants.CD_PLCMT_REMOVAL_RSN,
				prefillDto.getPlacementDtlDto().getCdPlcmtRemovalRsn(), CodesConstant.CPLREMRO);
		bookmarkNonFormList.add(bookmarkPlcmtRemovalRsn);
		BookmarkDto bookmarkPlcmtType = createBookmarkWithCodesTable(BookmarkConstants.CD_PLCMT_TYPE,
				prefillDto.getPlacementDtlDto().getCdPlcmtType(), CodesConstant.CPLMNTYP);
		bookmarkNonFormList.add(bookmarkPlcmtType);
		BookmarkDto bookmarkPlcmtPhoneExt = createBookmark(BookmarkConstants.NBR_PLCMT_PHONE_EXT,
				prefillDto.getPlacementDtlDto().getNbrPlcmtPhoneExt());
		bookmarkNonFormList.add(bookmarkPlcmtPhoneExt);
		BookmarkDto bookmarkPlcmtTelephone = createBookmark(BookmarkConstants.NBR_PLCMT_TELEPHONE,
				TypeConvUtil.formatPhone(prefillDto.getPlacementDtlDto().getNbrPlcmtTelephone()));
		bookmarkNonFormList.add(bookmarkPlcmtTelephone);
		BookmarkDto bookmarkPlcmtAgency = createBookmark(BookmarkConstants.NM_PLCMT_AGENCY,
				prefillDto.getPlacementDtlDto().getNmPlcmtAgency());
		bookmarkNonFormList.add(bookmarkPlcmtAgency);
		BookmarkDto bookmarkPlcmtContact = createBookmark(BookmarkConstants.NM_PLCMT_CONTACT,
				prefillDto.getPlacementDtlDto().getNmPlcmtContact());
		bookmarkNonFormList.add(bookmarkPlcmtContact);
		BookmarkDto bookmarkPlcmtFacil = createBookmark(BookmarkConstants.NM_PLCMT_FACIL,
				prefillDto.getPlacementDtlDto().getNmPlcmtFacil());
		bookmarkNonFormList.add(bookmarkPlcmtFacil);
		BookmarkDto bookmarkPlcmtAddrComment = createBookmark(BookmarkConstants.TXT_PLCMT_ADDR_COMMENT,
				prefillDto.getPlacementDtlDto().getTxtPlcmtAddrComment());
		bookmarkNonFormList.add(bookmarkPlcmtAddrComment);
		BookmarkDto bookmarkPlcmtDiscussion = createBookmark(BookmarkConstants.TXT_PLCMT_DISCUSSION,
				prefillDto.getPlacementDtlDto().getTxtPlcmtDiscussion());
		bookmarkNonFormList.add(bookmarkPlcmtDiscussion);
		BookmarkDto bookmarkPlcmtDocuments = createBookmark(BookmarkConstants.TXT_PLCMT_DOCUMENTS,
				prefillDto.getPlacementDtlDto().getTxtPlcmtDocuments());
		bookmarkNonFormList.add(bookmarkPlcmtDocuments);
		BookmarkDto bookmarkTxtPlcmtRemovalRsn = createBookmark(BookmarkConstants.TXT_PLCMT_REMOVAL_RSN,
				prefillDto.getPlacementDtlDto().getTxtPlcmtRemovalRsn());
		bookmarkNonFormList.add(bookmarkTxtPlcmtRemovalRsn);
		if (!ObjectUtils.isEmpty(prefillDto.getPlacementDtlDto().getIdRsrcFacil())) {
			BookmarkDto bookmarkRsrcFacil = createBookmark(BookmarkConstants.ID_RSRC_FACIL,
					prefillDto.getPlacementDtlDto().getIdRsrcFacil());
			bookmarkNonFormList.add(bookmarkRsrcFacil);
		} else {
			BookmarkDto bookmarkRsrcFacil = createBookmark(BookmarkConstants.ID_RSRC_FACIL,
					ServiceConstants.STR_ZERO_VAL);
			bookmarkNonFormList.add(bookmarkRsrcFacil);
		}
		

		// CSEC35D
		BookmarkDto bookmarkPlcmtPersonSuffix = createBookmarkWithCodesTable(BookmarkConstants.NM_PLCMT_PERSON_SUFFIX,
				prefillDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFormList.add(bookmarkPlcmtPersonSuffix);
		BookmarkDto bookmarkPlcmtPersonFirst = createBookmark(BookmarkConstants.NM_PLCMT_PERSON_FIRST,
				prefillDto.getNameDetailDto().getNmNameFirst());
		bookmarkNonFormList.add(bookmarkPlcmtPersonFirst);
		BookmarkDto bookmarkPlcmtPersonLast = createBookmark(BookmarkConstants.NM_PLCMT_PERSON_LAST,
				prefillDto.getNameDetailDto().getNmNameLast());
		bookmarkNonFormList.add(bookmarkPlcmtPersonLast);
		BookmarkDto bookmarkPlcmtPersonMiddle = createBookmark(BookmarkConstants.NM_PLCMT_PERSON_MIDDLE,
				prefillDto.getNameDetailDto().getNmNameMiddle());
		bookmarkNonFormList.add(bookmarkPlcmtPersonMiddle);

		// CSEC34D
		BookmarkDto bookmarkAddrMedicaidZip = createBookmark(BookmarkConstants.ADDR_MEDICAID_ZIP,
				prefillDto.getPersonAddressDto().getAddrPersonAddrZip());
		bookmarkNonFormList.add(bookmarkAddrMedicaidZip);
		BookmarkDto bookmarkAddrMedicaidCity = createBookmark(BookmarkConstants.ADDR_MEDICAID_CITY,
				prefillDto.getPersonAddressDto().getAddrPersonAddrCity());
		bookmarkNonFormList.add(bookmarkAddrMedicaidCity);
		BookmarkDto bookmarkAddrMedicaidAttn = createBookmark(BookmarkConstants.ADDR_MEDICAID_ATTN,
				prefillDto.getPersonAddressDto().getAddrPersonAddrAttn());
		bookmarkNonFormList.add(bookmarkAddrMedicaidAttn);
		BookmarkDto bookmarkAddrMedicaidLn1 = createBookmark(BookmarkConstants.ADDR_MEDICAID_LN1,
				prefillDto.getPersonAddressDto().getAddrPersAddrStLn1());
		bookmarkNonFormList.add(bookmarkAddrMedicaidLn1);
		BookmarkDto bookmarkAddrMedicaidLn2 = createBookmark(BookmarkConstants.ADDR_MEDICAID_LN2,
				prefillDto.getPersonAddressDto().getAddrPersAddrStLn2());
		bookmarkNonFormList.add(bookmarkAddrMedicaidLn2);
		BookmarkDto bookmarkAddrMedicaidCnty = createBookmarkWithCodesTable(BookmarkConstants.ADDR_MEDICAID_CNTY,
				prefillDto.getPersonAddressDto().getCdPersonAddrCounty(), CodesConstant.CCOUNT);
		bookmarkNonFormList.add(bookmarkAddrMedicaidCnty);
		BookmarkDto bookmarkAddrMedicaidSt = createBookmarkWithCodesTable(BookmarkConstants.ADDR_MEDICAID_ST,
				prefillDto.getPersonAddressDto().getCdPersonAddrState(), CodesConstant.CSTATE);
		bookmarkNonFormList.add(bookmarkAddrMedicaidSt);

		// CSEC88D
		BookmarkDto bookmarkRemovalRsnSubtype = createBookmark(BookmarkConstants.CD_PLCMT_REMOVAL_RSN_SUBTYPE,
				prefillDto.getDecodeValue());
		bookmarkNonFormList.add(bookmarkRemovalRsnSubtype);

		// create blobs
		List<BlobDataDto> blobDataList = new ArrayList<BlobDataDto>();
		if (!ObjectUtils.isEmpty(prefillDto.getPlacementDtlDto().getIdPlcmtEvent())) {
			BlobDataDto blobNarrPlcmtDischarge = createBlobData(BookmarkConstants.NARR_PLCMT_DISCHARGE,
					BookmarkConstants.PLCMT_DISCHG_NARR, prefillDto.getPlacementDtlDto().getIdPlcmtEvent().toString());
			blobDataList.add(blobNarrPlcmtDischarge);
			BlobDataDto blobNarrPlcmtIssues = createBlobData(BookmarkConstants.NARR_PLCMT_ISSUES,
					BookmarkConstants.PLCMT_ISSUES_NARR, prefillDto.getPlacementDtlDto().getIdPlcmtEvent().toString());
			blobDataList.add(blobNarrPlcmtIssues);
		}

		// Store prefill in dto and return
		PreFillDataServiceDto prefillData = new PreFillDataServiceDto();
		prefillData.setBlobDataDtoList(blobDataList);
		prefillData.setBookmarkDtoList(bookmarkNonFormList);
		prefillData.setFormDataGroupList(formDataGroupList);
		return prefillData;
	}

}
