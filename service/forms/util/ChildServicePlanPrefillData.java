package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.admin.dto.CapsPlacemntDto;
import us.tx.state.dfps.service.childplan.dto.ChildParticipantRowDODto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanDetailsDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanDetailsOutputDto;
import us.tx.state.dfps.service.childplan.dto.ConcurrentGoalDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * 
 * @param parentDtoobj
 *            Method Description:This class implements returnPrefillData
 *            operation defined in DocumentServiceUtil Interface to populate the
 *            prefill data for below forms Form Name
 *            :cvs01o00(PlacementAuthorization Foster Care/Residential Care -
 *            2085FC) Form Name:cvs02o00(Placement Authorization Kinship or
 *            Other Non-Foster Caregiver -2085KO) Form Name :cvs03o00(Placement
 *            Authorization Legal Risk - 2085LR)
 */
@Component
public class ChildServicePlanPrefillData extends DocumentServiceUtil {

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

		ChildPlanDetailsOutputDto childPlanDetailsOutputDto = (ChildPlanDetailsOutputDto) parentDtoobj;

		if (null == childPlanDetailsOutputDto.getEventDtoList()) {
			childPlanDetailsOutputDto.setEventDtoList(new ArrayList<EventDto>());
		}
		if (null == childPlanDetailsOutputDto.getGenericCaseInfoDto()) {
			childPlanDetailsOutputDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}
		if (null == childPlanDetailsOutputDto.getPersonDto()) {
			childPlanDetailsOutputDto.setPersonDto(new PersonDto());
		}
		if (null == childPlanDetailsOutputDto.getCapsPlacemntDto()) {
			childPlanDetailsOutputDto.setCapsPlacemntDto(new CapsPlacemntDto());
		}
		if (null == childPlanDetailsOutputDto.getConcurrentDtoList()) {
			childPlanDetailsOutputDto.setConcurrentDtoList(new ArrayList<ConcurrentGoalDto>());
		}
		if (null == childPlanDetailsOutputDto.getChildPlanDetailsDto()) {
			childPlanDetailsOutputDto.setChildPlanDetailsDto(new ChildPlanDetailsDto());
		}
		if (null == childPlanDetailsOutputDto.getEventChildList()) {
			childPlanDetailsOutputDto.setEventChildList(new EventDto());
		}
		if (null == childPlanDetailsOutputDto.getPersonList()) {
			childPlanDetailsOutputDto.setPersonList(new PersonDto());
		}
		if (null == childPlanDetailsOutputDto.getEventList()) {
			childPlanDetailsOutputDto.setEventList(new EventDto());
		}
		if (null == childPlanDetailsOutputDto.getChildServiceParcipantList()) {
			childPlanDetailsOutputDto.setChildServiceParcipantList(new ArrayList<ChildParticipantRowDODto>());
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		if ("csc19o00".equalsIgnoreCase(childPlanDetailsOutputDto.getFormName())) {
			preFillData = impactChildPlanPrefill(childPlanDetailsOutputDto);
		} else if ("csc41o00".equalsIgnoreCase(childPlanDetailsOutputDto.getFormName())) {
			preFillData = initialChildPlanHistoryPrefill(childPlanDetailsOutputDto);
		}
		return preFillData;

	}

	/**
	 * Method Name: impactChildPlanPrefill Method Description: Creates prefill
	 * data for form CSC19O00
	 * 
	 * @param childPlanDetailsOutputDto
	 * @return PreFillDataServiceDto
	 */
	private PreFillDataServiceDto impactChildPlanPrefill(ChildPlanDetailsOutputDto childPlanDetailsOutputDto) {
		
		// The formDataGroup List containing all Parent
		List<FormDataGroupDto> allParentFormGroup = new ArrayList<FormDataGroupDto>();
		/**
		 * Populating the independent BookMarks into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList=populateIndependentBookmarks(childPlanDetailsOutputDto);

		/**
		 * Populating the Blob Data into prefill data
		 */
		List<BlobDataDto> blobDataDtoForCallNarrative = populateBlobDataNarrativeBookmarks(childPlanDetailsOutputDto);
			
		// SubGroups
		String planType=childPlanDetailsOutputDto.getPersonDto().getCdCspPlanType();
		switch(planType){
		
		case "IPN":
			/***
			 * START**********csc19o01
			 * -->csc19o58-->csc19o63-->csc19o64-->csc19o30-->csc19o03*********** --> csc19o17IPN
			 *************************************************************************************************/	
			allParentFormGroup.add(populateCommonPlanTypeBookmarks(childPlanDetailsOutputDto));
			//csc19o08->csc19o17
			allParentFormGroup.add(populateIPNPlanTypeAdditionalBookmarks(childPlanDetailsOutputDto));	
			//csc19o18
			allParentFormGroup.add(populateCSCEighteen(childPlanDetailsOutputDto));	
			
			break;		
		case "IPL":
			/***
			 * START**********csc19o10 --> csc19o38 --> csc19o35 --> csc19o36 -->
			 * csc19o37
			 ************************************************************************************************************/
			allParentFormGroup.add(populatePALPostDiscahrgeObjTypeBookmarks(childPlanDetailsOutputDto));
			// code is reused here since the same bookmarks are to be loaded csc19o01			
			FormDataGroupDto formDataGroupDtoIPPAL=populateCommonPlanTypeBookmarks(childPlanDetailsOutputDto);
			List<FormDataGroupDto> allFormDataGroupListIP=formDataGroupDtoIPPAL.getFormDataGroupList();
			// csc19o01->csc19o02
			allFormDataGroupListIP.add(populatePALDateBookmarks(childPlanDetailsOutputDto));
			formDataGroupDtoIPPAL.setFormDataGroupList(allFormDataGroupListIP);
			// csc19o15->csc19o02
			allParentFormGroup.add(formDataGroupDtoIPPAL);		
			
			//csc19o08->csc19o17
			allParentFormGroup.add(populateIPNPlanTypeAdditionalBookmarks(childPlanDetailsOutputDto));	
			//csc19o18
			allParentFormGroup.add(populateCSCEighteen(childPlanDetailsOutputDto));
			//csc19o20
			allParentFormGroup.add(populatePALPlans(childPlanDetailsOutputDto));			
			break;
		case "IPT":
			/***
			 * START**********csc19o09 --> csc19o33 --> csc19o61 --> csc19o34 -->
			 ************************************************************************************************************************/
			allParentFormGroup.add(populateIPPlanTypeBookmarks(childPlanDetailsOutputDto));
			// code is reused here since the same bookmarks are to be loaded csc19o01
			allParentFormGroup.add(populateCommonPlanTypeBookmarks(childPlanDetailsOutputDto));	
			//csc19o08->csc19o17
			allParentFormGroup.add(populateIPNPlanTypeAdditionalBookmarks(childPlanDetailsOutputDto));	
			//csc19o18
			allParentFormGroup.add(populateCSCEighteen(childPlanDetailsOutputDto));
			//csc19o19
			allParentFormGroup.add(populateCSCNineteen(childPlanDetailsOutputDto));			
			break;
		case "IPP":
			// code is reused here since the same bookmarks are to be loaded
			// csc19o01
			FormDataGroupDto formDataGroupDtoIPPPAL=populateCommonPlanTypeBookmarks(childPlanDetailsOutputDto);
			List<FormDataGroupDto> allFormDataGroupListIPP=formDataGroupDtoIPPPAL.getFormDataGroupList();
			// csc19o01->csc19o02
			allFormDataGroupListIPP.add(populatePALDateBookmarks(childPlanDetailsOutputDto));
			formDataGroupDtoIPPPAL.setFormDataGroupList(allFormDataGroupListIPP);
			// csc19o15->csc19o02
			allParentFormGroup.add(formDataGroupDtoIPPPAL);		
			//csc19o08->csc19o17
			allParentFormGroup.add(populateIPNPlanTypeAdditionalBookmarks(childPlanDetailsOutputDto));	
			/***
			 * START**********csc19o09 --> csc19o33 --> csc19o61 --> csc19o34 -->
			 ************************************************************************************************************************/
			allParentFormGroup.add(populateIPPlanTypeBookmarks(childPlanDetailsOutputDto));
			/***
			 * START**********csc19o10 --> csc19o38 --> csc19o35 --> csc19o36 -->
			 * csc19o37
			 ************************************************************************************************************/
			allParentFormGroup.add(populatePALPostDiscahrgeObjTypeBookmarks(childPlanDetailsOutputDto));
			//csc19o18
			allParentFormGroup.add(populateCSCEighteen(childPlanDetailsOutputDto));
			//csc19o19
			allParentFormGroup.add(populateCSCNineteen(childPlanDetailsOutputDto));	
			//csc19o20
			allParentFormGroup.add(populatePALPlans(childPlanDetailsOutputDto));
			break;
		case "RVW":
			/***
			 * START************csc19o04
			 * -->csc19o69-->csc19o68-->csc19o59-->csc19o05-->csc19o30-->csc19o07-->csc19o03
			 **************************************************************************************/
			// csc19o04
			allParentFormGroup.add(populateCommonReviewPlanTypeBookmarks(childPlanDetailsOutputDto));
			//csc19o08,csc19o16
			allParentFormGroup.add(populateBasicObjForReview(childPlanDetailsOutputDto));	
			//csc19o18
			allParentFormGroup.add(populateCSCEighteen(childPlanDetailsOutputDto));
			break;
		case "RVL":
			/***
			 * START************csc19o04
			 * -->csc19o02-->csc19o69-->csc19o68-->csc19o59-->csc19o05-->csc19o30-->csc19o07-->csc19o03*******RVL
			 *******************************************************************************/
			// csc19o04			
			FormDataGroupDto formDataGroupDtoRVLPAL=populateCommonReviewPlanTypeBookmarks(childPlanDetailsOutputDto);
			List<FormDataGroupDto> allFormDataGroupListRVL=formDataGroupDtoRVLPAL.getFormDataGroupList();
			// csc19o04->csc19o02
			allFormDataGroupListRVL.add(populatePALDateBookmarks(childPlanDetailsOutputDto));
			formDataGroupDtoRVLPAL.setFormDataGroupList(allFormDataGroupListRVL);
			// csc19o15->csc19o02
			allParentFormGroup.add(formDataGroupDtoRVLPAL);	
			//csc19o08,csc19o16
			allParentFormGroup.add(populateBasicObjForReview(childPlanDetailsOutputDto));
			/***
			 * START**********csc19o10 --> csc19o38 --> csc19o35 --> csc19o36 -->
			 * csc19o37
			 ************************************************************************************************************/
			allParentFormGroup.add(populatePALPostDiscahrgeObjTypeBookmarks(childPlanDetailsOutputDto));
			//csc19o18
			allParentFormGroup.add(populateCSCEighteen(childPlanDetailsOutputDto));
			//csc19o20
			allParentFormGroup.add(populatePALPlans(childPlanDetailsOutputDto));
			break;
		case "RVP":
			/***
			 * START************csc19o04
			 * -->csc19o02-->csc19o69-->csc19o68-->csc19o59-->csc19o05-->csc19o30-->csc19o07-->csc19o03*******RVP
			 *******************************************************************************/
			// csc19o04
			FormDataGroupDto formDataGroupDtoRVPPAL=populateCommonReviewPlanTypeBookmarks(childPlanDetailsOutputDto);
			List<FormDataGroupDto> allFormDataGroupListRVP=formDataGroupDtoRVPPAL.getFormDataGroupList();
			// csc19o04->csc19o02
			allFormDataGroupListRVP.add(populatePALDateBookmarks(childPlanDetailsOutputDto));
			formDataGroupDtoRVPPAL.setFormDataGroupList(allFormDataGroupListRVP);			
			allParentFormGroup.add(formDataGroupDtoRVPPAL);	
			// csc19o08,csc19o16
			allParentFormGroup.add(populateBasicObjForReview(childPlanDetailsOutputDto));
			/***
			 * START**********csc19o09 --> csc19o33 --> csc19o61 --> csc19o34 -->
			 ************************************************************************************************************************/
			allParentFormGroup.add(populateIPPlanTypeBookmarks(childPlanDetailsOutputDto));
			/***
			 * START**********csc19o10 --> csc19o38 --> csc19o35 --> csc19o36 -->
			 * csc19o37
			 ************************************************************************************************************/
			allParentFormGroup.add(populatePALPostDiscahrgeObjTypeBookmarks(childPlanDetailsOutputDto));
			//csc19o18
			allParentFormGroup.add(populateCSCEighteen(childPlanDetailsOutputDto));
			//csc19o19
			allParentFormGroup.add(populateCSCNineteen(childPlanDetailsOutputDto));	
			//csc19o20
			allParentFormGroup.add(populatePALPlans(childPlanDetailsOutputDto));
			break;
		case "RVT":
			/***
			 * START************csc19o04
			 * -->csc19o69-->csc19o68-->csc19o59-->csc19o05-->csc19o30-->csc19o07-->csc19o03***************************RVT
			 ***********************************************************/
			// csc19o04
			allParentFormGroup.add(populateCommonReviewPlanTypeBookmarks(childPlanDetailsOutputDto));
			//csc19o08,csc19o16
			allParentFormGroup.add(populateBasicObjForReview(childPlanDetailsOutputDto));
			/***
			 * START**********csc19o09 --> csc19o33 --> csc19o61 --> csc19o34 -->
			 ************************************************************************************************************************/
			allParentFormGroup.add(populateIPPlanTypeBookmarks(childPlanDetailsOutputDto));
			//csc19o18
			allParentFormGroup.add(populateCSCEighteen(childPlanDetailsOutputDto));
			//csc19o19
			allParentFormGroup.add(populateCSCNineteen(childPlanDetailsOutputDto));	
			break;
		case "FRV":
			/***
			 * START**********csc19o15 --> csc19o74 --> csc19o73 --> csc19o60
			 * -->csc19o05-->csc19o30-->csc19o07-->csc19o03
			 *******************************************************************************/
			allParentFormGroup.add(populateCommonFacilityPlanTypeBookmarks(childPlanDetailsOutputDto));			
			break;	
		
		
		case "FRP":
			/***
			 * START**********csc19o15 --> csc19o74 --> csc19o73 -->
			 * csc19o60 -->csc19o05-->csc19o30-->csc19o07-->csc19o03*****FRP
			 **************************************************************************/
			FormDataGroupDto formDataGroupDtoRWPAL=populateCommonFacilityPlanTypeBookmarks(childPlanDetailsOutputDto);
			List<FormDataGroupDto> allFormDataGroupList=formDataGroupDtoRWPAL.getFormDataGroupList();
			allFormDataGroupList.add(populatePALDateBookmarks(childPlanDetailsOutputDto));
			formDataGroupDtoRWPAL.setFormDataGroupList(allFormDataGroupList);
			// csc19o15->csc19o02
			allParentFormGroup.add(formDataGroupDtoRWPAL);
			/***
			 * START**********csc19o10 --> csc19o38 --> csc19o35 --> csc19o36 -->
			 * csc19o37
			 ************************************************************************************************************/
			allParentFormGroup.add(populatePALPostDiscahrgeObjTypeBookmarks(childPlanDetailsOutputDto));
			//csc19o20
			allParentFormGroup.add(populatePALPlans(childPlanDetailsOutputDto));
			break;
		}

		/*** START***********cfzco00 ***********************************************************************************************************************************************************************/
		if (StringUtils.isNotBlank(childPlanDetailsOutputDto.getPersonDto().getCdPersonSuffix())) {
			FormDataGroupDto formDataGroupTmplatComma = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			allParentFormGroup.add(formDataGroupTmplatComma);
			
		}
	
		
		/***** START************csc19o31 ***********************************************************************************************************************************************************************/
		if (!"FRV".equalsIgnoreCase(planType)
				&& !"FRP".equalsIgnoreCase(planType)
				&& "Y".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersonLivArr())) {
			FormDataGroupDto formDataGroupDtoTmpNotReview = createFormDataGroup(FormGroupsConstants.TMPLAT_NOT_REVIEW,
					FormConstants.EMPTY_STRING);
			List<BlobDataDto> blobDataDtoTmpltNotReviewList = new ArrayList<BlobDataDto>();

			// Populate BlobData IPS_NARR_REI TName-CP_REI_NARR
			BlobDataDto blobDataDtoIpsNarrRei = createBlobData(BookmarkConstants.IPS_NARR_REI, "CP_REI_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataDtoTmpltNotReviewList.add(blobDataDtoIpsNarrRei);

			// Populate BlobData IPS_NARR_HRI TName-CP_HRI_NARR
			BlobDataDto blobDataDtoIpsNarrHri = createBlobData(BookmarkConstants.IPS_NARR_HRI, "CP_HRI_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataDtoTmpltNotReviewList.add(blobDataDtoIpsNarrHri);
			formDataGroupDtoTmpNotReview.setBlobDataDtoList(blobDataDtoTmpltNotReviewList);
			allParentFormGroup.add(formDataGroupDtoTmpNotReview);
			
			//csc19o32
			FormDataGroupDto formDataGroupDtoTmpNotReviewPlans = createFormDataGroup(
					FormGroupsConstants.TMPLAT_NOT_REVIEW_PLANS, FormConstants.EMPTY_STRING);
			List<BlobDataDto> blobDataDtoTmpltNotReviewPlansList = new ArrayList<BlobDataDto>();

			// Populate BlobData OBJ_NARR_HRB TName-CP_HRB_NARR
			BlobDataDto blobDataDtoObjNarrHrb = createBlobData(BookmarkConstants.OBJ_NARR_HRB, "CP_HRB_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataDtoTmpltNotReviewPlansList.add(blobDataDtoObjNarrHrb);

			// Populate BlobData OBJ_NARR_RIP TName-CP_RIP_NARR
			BlobDataDto blobDataDtoObjNarrRip = createBlobData(BookmarkConstants.OBJ_NARR_RIP, "CP_RIP_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataDtoTmpltNotReviewPlansList.add(blobDataDtoObjNarrRip);
			formDataGroupDtoTmpNotReviewPlans.setBlobDataDtoList(blobDataDtoTmpltNotReviewPlansList);
			allParentFormGroup.add(formDataGroupDtoTmpNotReviewPlans);
			
		}
		
		/***** START************csc19o40 ***********************************************************************************************************************************************************************/
		if ("N".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersonLivArr())) {
			FormDataGroupDto formDataGroupDtoTmpApaOld = createFormDataGroup(FormGroupsConstants.TMPLAT_APA_OLD,
					FormConstants.EMPTY_STRING);
			allParentFormGroup.add(formDataGroupDtoTmpApaOld);
			/***** START************csc19o42 ***********************************************************************************************************************************************************************/

			FormDataGroupDto formDataGroupDtoTmpVisOld = createFormDataGroup(FormGroupsConstants.TMPLAT_VIS_OLD,
					FormConstants.EMPTY_STRING);
			allParentFormGroup.add(formDataGroupDtoTmpVisOld);	
			
			/***** START************csc19o50 ***********************************************************************************************************************************************************************/
			
				FormDataGroupDto formDataGroupDtoTmpAppOld = createFormDataGroup(FormGroupsConstants.TMPLAT_APP_OLD,
						FormConstants.EMPTY_STRING);
				allParentFormGroup.add(formDataGroupDtoTmpAppOld);
				/***** START************csc19o52 ***********************************************************************************************************************************************************************/
				
					FormDataGroupDto formDataGroupDtoTmpVplnOld = createFormDataGroup(FormGroupsConstants.TMPLAT_VPLN_OLD,
							FormConstants.EMPTY_STRING);
					allParentFormGroup.add(formDataGroupDtoTmpVplnOld);
					
			//csc19o55
					FormDataGroupDto formDataGroupDtoTmpltSupOld = createFormDataGroup(FormGroupsConstants.TMPLAT_SUP_OLD,
					FormConstants.EMPTY_STRING);
			allParentFormGroup.add(formDataGroupDtoTmpltSupOld);
			
			
		}
		/***** END************csc19o40 *************************************************************************************************************************************************************************/
		/***** START************csc19o41 ***********************************************************************************************************************************************************************/
		else if ("Y".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersonLivArr())) {
			FormDataGroupDto formDataGroupDtoTmpApaNew = createFormDataGroup(FormGroupsConstants.TMPLAT_APA_NEW,
					FormConstants.EMPTY_STRING);
			allParentFormGroup.add(formDataGroupDtoTmpApaNew);
			/***** START************csc19o43 ***********************************************************************************************************************************************************************/

			FormDataGroupDto formDataGroupDtoTmpVisNew = createFormDataGroup(FormGroupsConstants.TMPLAT_VIS_NEW,
					FormConstants.EMPTY_STRING);
			allParentFormGroup.add(formDataGroupDtoTmpVisNew);
		/***** START************csc19o44 ***********************************************************************************************************************************************************************/
			FormDataGroupDto formDataGroupDtoTmpDip = createFormDataGroup(FormGroupsConstants.TMPLAT_DIP,
					FormConstants.EMPTY_STRING);
			List<BlobDataDto> blobDataDtoForCallNarrativeDIP = new ArrayList<BlobDataDto>();
			// populate the IPS_NARR_DIP (TName - CP_DIP_NARR)
			BlobDataDto blobDataIpsNarrDip = createBlobData(BookmarkConstants.IPS_NARR_DIP, "CP_DIP_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataDtoForCallNarrativeDIP.add(blobDataIpsNarrDip);
			formDataGroupDtoTmpDip.setBlobDataDtoList(blobDataDtoForCallNarrativeDIP);
			allParentFormGroup.add(formDataGroupDtoTmpDip);
			/***** START************csc19o49 ***********************************************************************************************************************************************************************/
			FormDataGroupDto formDataGroupDtoTmpNewPlns = createFormDataGroup(FormGroupsConstants.TMPLAT_NEW_PLNS,
					FormConstants.EMPTY_STRING);

			List<BlobDataDto> blobDataDtoTmpNwPlnsList = new ArrayList<BlobDataDto>();
			// Populate BlobData PLN_NARR_REP TName-CP_REP_NARR
			BlobDataDto blobDataDtoPlnNarrRep = createBlobData(BookmarkConstants.PLN_NARR_REP, "CP_REP_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataDtoTmpNwPlnsList.add(blobDataDtoPlnNarrRep);

			// Populate BlobData PLN_NARR_PSP TName-CP_PSP_NARR
			BlobDataDto blobDataDtoPlnNarrPsp = createBlobData(BookmarkConstants.PLN_NARR_PSP, "CP_PSP_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataDtoTmpNwPlnsList.add(blobDataDtoPlnNarrPsp);

			formDataGroupDtoTmpNewPlns.setBlobDataDtoList(blobDataDtoTmpNwPlnsList);
			allParentFormGroup.add(formDataGroupDtoTmpNewPlns);
			/***** START************csc19o51 ***********************************************************************************************************************************************************************/
			
				FormDataGroupDto formDataGroupDtoTmpAppNew = createFormDataGroup(FormGroupsConstants.TMPLAT_APP_NEW,
						FormConstants.EMPTY_STRING);
				allParentFormGroup.add(formDataGroupDtoTmpAppNew);
				//csc19o53
				FormDataGroupDto formDataGroupDtoTmpVplnOld = createFormDataGroup(FormGroupsConstants.TMPLAT_VPLN_NEW,
						FormConstants.EMPTY_STRING);
				allParentFormGroup.add(formDataGroupDtoTmpVplnOld);
				//csc19o54
				FormDataGroupDto formDataGroupDtoTmpDpl = createFormDataGroup(FormGroupsConstants.TMPLAT_DPL,
						FormConstants.EMPTY_STRING);

				List<BlobDataDto> blobDataDtoTmpDplList = new ArrayList<BlobDataDto>();
				// Populate BlobData OBJ_NARR_DPL TName-CP_DPL_NARR
				BlobDataDto blobDataDtoObjNarrDpl = createBlobData(BookmarkConstants.OBJ_NARR_DPL, "CP_DPL_NARR",
						childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
				blobDataDtoTmpDplList.add(blobDataDtoObjNarrDpl);

				formDataGroupDtoTmpDpl.setBlobDataDtoList(blobDataDtoTmpDplList);
				allParentFormGroup.add(formDataGroupDtoTmpDpl);
				//csc19o56
				FormDataGroupDto formDataGroupDtoTmpltSupNew = createFormDataGroup(FormGroupsConstants.TMPLAT_SUP_NEW,
						FormConstants.EMPTY_STRING);
				allParentFormGroup.add(formDataGroupDtoTmpltSupNew);
				//csc19o57
				FormDataGroupDto formDataGroupDtoTmpOthrInfo = createFormDataGroup(FormGroupsConstants.TMPLAT_OTHER_INFO,
						FormConstants.EMPTY_STRING);

				List<BookmarkDto> bookmarkDtoTmplatOtherInfoList = new ArrayList<BookmarkDto>();
				// Populate bookmark CHILD_PLAN_INFO_NOT_AVAIL
				BookmarkDto bookmarkChildPlanInfoNotAvail = createBookmark(BookmarkConstants.CHILD_PLAN_INFO_NOT_AVAIL,
						childPlanDetailsOutputDto.getPersonDto().getTxtInfoNotAvailable());
				bookmarkDtoTmplatOtherInfoList.add(bookmarkChildPlanInfoNotAvail);

				// Populate bookmark CHILD_PLAN_OTHER_ASSMT
				BookmarkDto bookmarkChildPlanOthrAssmt = createBookmark(BookmarkConstants.CHILD_PLAN_OTHER_ASSMT,
						childPlanDetailsOutputDto.getPersonDto().getTxtOtherAssmt());
				bookmarkDtoTmplatOtherInfoList.add(bookmarkChildPlanOthrAssmt);

				formDataGroupDtoTmpOthrInfo.setBookmarkDtoList(bookmarkDtoTmplatOtherInfoList);
				allParentFormGroup.add(formDataGroupDtoTmpOthrInfo);
				//csc19o39
				FormDataGroupDto formDataGroupDtoTmpNewPlnsGT = createFormDataGroup(FormGroupsConstants.TMPLAT_NEW_GT,
						FormConstants.EMPTY_STRING);

				List<BlobDataDto> blobDataDtoTmpNwPlnsListGT = new ArrayList<BlobDataDto>();
				// Populate BlobData OBJ_NARR_PSY TName-CP_PSY_NARR
				BlobDataDto blobDataDtoPlnNarrRepGT = createBlobData(BookmarkConstants.OBJ_NARR_PSY, "CP_PSY_NARR",
						childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
				blobDataDtoTmpNwPlnsListGT.add(blobDataDtoPlnNarrRepGT);

				// Populate BlobData OBJ_NARR_REC TName-CP_REC_NARR
				BlobDataDto blobDataDtoPlnNarrPspGT = createBlobData(BookmarkConstants.OBJ_NARR_REC, "CP_REC_NARR",
						childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
				blobDataDtoTmpNwPlnsListGT.add(blobDataDtoPlnNarrPspGT);

				formDataGroupDtoTmpNewPlnsGT.setBlobDataDtoList(blobDataDtoTmpNwPlnsListGT);
				allParentFormGroup.add(formDataGroupDtoTmpNewPlnsGT);
			
		}
			
	
		/***** START**********csc19100-->csc19101 **************************************************************************************************************************************************************/
		FormDataGroupDto formDataTmpDispPlcmtSumm = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_PLCMT_SUMM,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkIdEventList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkDtoIdEvent = createBookmark(BookmarkConstants.ID_EVENT,
				childPlanDetailsOutputDto.getEventList().getIdEvent());
		bookmarkIdEventList.add(bookmarkDtoIdEvent);
		formDataTmpDispPlcmtSumm.setBookmarkDtoList(bookmarkIdEventList);
		// csc19101
		List<FormDataGroupDto> formDataTmpPlcmtSummList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto formDataTmpPlcmtSumm = createFormDataGroup(FormGroupsConstants.TMPLAT_PLCMT_SUMM,
				FormGroupsConstants.TMPLAT_DISP_PLCMT_SUMM);
		formDataTmpPlcmtSummList.add(formDataTmpPlcmtSumm);
		formDataTmpDispPlcmtSumm.setFormDataGroupList(formDataTmpPlcmtSummList);

		allParentFormGroup.add(formDataTmpDispPlcmtSumm);
		
		/***** END**********csc19100-->csc19101 ****************************************************************************************************************************************************************/
		/***** START************csc19o14 ***********************************************************************************************************************************************************************/
		
		for (ChildParticipantRowDODto childParticipnt : childPlanDetailsOutputDto.getChildServiceParcipantList()) {
			FormDataGroupDto formDataTmpPartInfoRptGp = createFormDataGroup(FormGroupsConstants.TMPLAT_PART_INFO_RPT_GP,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkTmpPartInfoRptGp = new ArrayList<BookmarkDto>();
			// Populate bookmark PART_INFO_DATE_NOTIFICATION
			BookmarkDto bookmarkPartInfoDtNotification = createBookmark(BookmarkConstants.PART_INFO_DATE_NOTIFICATION,
					DateUtils.stringDt(childParticipnt.getDtDtCspDateNotified()));
			bookmarkTmpPartInfoRptGp.add(bookmarkPartInfoDtNotification);

			// Populate bookmark PART_INFO_DATE_COPY_GIVEN
			BookmarkDto bookmarkPartInfoDtCopyGivn = createBookmark(BookmarkConstants.PART_INFO_DATE_COPY_GIVEN,
					DateUtils.stringDt(childParticipnt.getDtDtCspPartCopyGiven()));
			bookmarkTmpPartInfoRptGp.add(bookmarkPartInfoDtCopyGivn);

			// Populate bookmark PART_INFO_DATE_PARTICIPATION
			BookmarkDto bookmarkPartInfoDtParticipatn = createBookmark(BookmarkConstants.PART_INFO_DATE_PARTICIPATION,
					DateUtils.stringDt(childParticipnt.getDtDtCspPartParticipate()));
			bookmarkTmpPartInfoRptGp.add(bookmarkPartInfoDtParticipatn);

			// Populate bookmark PART_INFO_TYPE_NOTIFICATION Ctable-CPPTNOPE
			BookmarkDto bookmarkPartInfoTypeNotification = createBookmarkWithCodesTable(BookmarkConstants.PART_INFO_TYPE_NOTIFICATION,
					childParticipnt.getSzCdCspPartNotifType(),CodesConstant.CPPTNOPE);
			bookmarkTmpPartInfoRptGp.add(bookmarkPartInfoTypeNotification);

			// Populate bookmark PART_INFO_NAME_FULL
			BookmarkDto bookmarkPartInfoNmFull = createBookmark(BookmarkConstants.PART_INFO_NAME_FULL,
					childParticipnt.getSzNmCspPartFull());
			bookmarkTmpPartInfoRptGp.add(bookmarkPartInfoNmFull);

			// Populate bookmark PART_INFO_RELATIONSHIP
			BookmarkDto bookmarkPartInfoRelationship = createBookmark(BookmarkConstants.PART_INFO_RELATIONSHIP,
					childParticipnt.getSzSdsCspPartRelationship());
			bookmarkTmpPartInfoRptGp.add(bookmarkPartInfoRelationship);
			formDataTmpPartInfoRptGp.setBookmarkDtoList(bookmarkTmpPartInfoRptGp);
			allParentFormGroup.add(formDataTmpPartInfoRptGp);
		}
		
		
		/***** END************csc19o14 *************************************************************************************************************************************************************************/
		// Setting the BookMarkDtoList,BlobDataDtoList and FormDataGroupList
		// into PrefillData
		// Setting the BookMarkDtoList,BlobDataDtoList and FormDataGroupList
		// into PrefillData
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(allParentFormGroup);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		preFillData.setBlobDataDtoList(blobDataDtoForCallNarrative);
		return preFillData;
	}

	/**
	 * Method Name: initialChildPlanHistoryPrefill Method Description: Creates
	 * prefill data for form CSC41O00
	 * 
	 * @param childPlanDetailsOutputDto
	 * @return PreFillDataServiceDto
	 */
	private PreFillDataServiceDto initialChildPlanHistoryPrefill(ChildPlanDetailsOutputDto childPlanDetailsOutputDto) {
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonGroupList = new ArrayList<BookmarkDto>();

		// parent group csc41o01
		if (StringUtils.isNotBlank(childPlanDetailsOutputDto.getPersonDto().getCdPersonSuffix())) {
			FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(commaGroupDto);
		}

		// parent group csc41o02
		FormDataGroupDto initPlcmtSummGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_INIT_PLCMT_SUMM,
				FormConstants.EMPTY_STRING);
		List<FormDataGroupDto> initPlcmtSummGroupList = new ArrayList<FormDataGroupDto>();
		List<BlobDataDto> blobInitPlcmtSummList = new ArrayList<BlobDataDto>();
		BlobDataDto blobIpsNarrIsh = createBlobData(BookmarkConstants.IPS_NARR_BLOB_ISH, BookmarkConstants.CP_ISH_NARR,
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().toString());
		blobInitPlcmtSummList.add(blobIpsNarrIsh);
		BlobDataDto blobIpsNarrIch = createBlobData(BookmarkConstants.IPS_NARR_BLOB_ICH, BookmarkConstants.CP_ICH_NARR,
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().toString());
		blobInitPlcmtSummList.add(blobIpsNarrIch);
		initPlcmtSummGroupDto.setBlobDataDtoList(blobInitPlcmtSummList);

		// sub group csc41o03
		FormDataGroupDto sprDtPlanCompGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SPR_DATE_PLAN_COMPLETED,
				FormGroupsConstants.TMPLAT_INIT_PLCMT_SUMM);
		List<BookmarkDto> bookmarkSprDtPlanCompList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkSprDtPlanComp = createBookmark(BookmarkConstants.SPR_DATE_PLAN_COMPLETE,
				DateUtils.stringDt(childPlanDetailsOutputDto.getChildPlanDetailsDto().getDtCspPlanCompleted()));
		bookmarkSprDtPlanCompList.add(bookmarkSprDtPlanComp);
		sprDtPlanCompGroupDto.setBookmarkDtoList(bookmarkSprDtPlanCompList);
		initPlcmtSummGroupList.add(sprDtPlanCompGroupDto);

		// sub group csc41o04
		FormDataGroupDto cpWorkerCompGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CP_WORKER_WHO_COMPLETED,
				FormGroupsConstants.TMPLAT_INIT_PLCMT_SUMM);
		List<FormDataGroupDto> cpWorkerCompGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkCpWorkerCompList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkWorkerSuffix = createBookmarkWithCodesTable(BookmarkConstants.CP_WORKER_SUFFIX_COMP,
				childPlanDetailsOutputDto.getPersonList().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
		bookmarkCpWorkerCompList.add(bookmarkWorkerSuffix);
		BookmarkDto bookmarkWorkerFirst = createBookmark(BookmarkConstants.CP_WORKER_FIRST_COMP,
				childPlanDetailsOutputDto.getPersonList().getNmPersonFirst());
		bookmarkCpWorkerCompList.add(bookmarkWorkerFirst);
		BookmarkDto bookmarkWorkerLast = createBookmark(BookmarkConstants.CP_WORKER_LAST_COMP,
				childPlanDetailsOutputDto.getPersonList().getNmPersonLast());
		bookmarkCpWorkerCompList.add(bookmarkWorkerLast);
		BookmarkDto bookmarkWorkerMiddle = createBookmark(BookmarkConstants.CP_WORKER_MIDDLE_COMP,
				childPlanDetailsOutputDto.getPersonList().getNmPersonMiddle());
		bookmarkCpWorkerCompList.add(bookmarkWorkerMiddle);
		cpWorkerCompGroupDto.setBookmarkDtoList(bookmarkCpWorkerCompList);

		// sub sub group csc41o05
		if (StringUtils.isNotBlank(childPlanDetailsOutputDto.getPersonList().getCdPersonSuffix())) {
			FormDataGroupDto commaJGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_J,
					FormGroupsConstants.TMPLAT_CP_WORKER_WHO_COMPLETED);
			cpWorkerCompGroupList.add(commaJGroupDto);
		}

		for (EventDto eventDto : childPlanDetailsOutputDto.getEventDtoList()) {
			if (eventDto.getIdEvent().equals(childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent())
					&& DateUtils.isBefore(eventDto.getDtEventOccurred(), DateUtils.date(2013, 8, 25))) {
				// sub group csc41o06
				FormDataGroupDto dispInitialGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_INITIAL,
						FormGroupsConstants.TMPLAT_INIT_PLCMT_SUMM);
				initPlcmtSummGroupList.add(dispInitialGroupDto);

				// sub group csc41o07
				FormDataGroupDto dispInitial2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_INITIAL2,
						FormGroupsConstants.TMPLAT_INIT_PLCMT_SUMM);
				initPlcmtSummGroupList.add(dispInitial2GroupDto);
			}
		}

		cpWorkerCompGroupDto.setFormDataGroupList(cpWorkerCompGroupList);
		initPlcmtSummGroupList.add(cpWorkerCompGroupDto);

		initPlcmtSummGroupDto.setFormDataGroupList(initPlcmtSummGroupList);
		formDataGroupList.add(initPlcmtSummGroupDto);

		// Non group bookmarks

		// CSEC02D
		BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				childPlanDetailsOutputDto.getGenericCaseInfoDto().getNmCase());
		bookmarkNonGroupList.add(bookmarkTitleCaseName);
		BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				childPlanDetailsOutputDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonGroupList.add(bookmarkTitleCaseNumber);

		// CSES27D
		BookmarkDto bookmarkChildDtBirth = createBookmark(BookmarkConstants.TITLE_CHILD_BIRTH_DT,
				DateUtils.stringDt(childPlanDetailsOutputDto.getPersonDto().getDtPersonBirth()));
		bookmarkNonGroupList.add(bookmarkChildDtBirth);
		BookmarkDto bookmarkChildPlanType = createBookmarkWithCodesTable(BookmarkConstants.TITLE_CHILD_PLAN_TYPE,
				childPlanDetailsOutputDto.getPersonDto().getCdCspPlanType(), CodesConstant.CCPPLNTP);
		bookmarkNonGroupList.add(bookmarkChildPlanType);
		BookmarkDto bookmarkChildNmSuffix = createBookmarkWithCodesTable(BookmarkConstants.TITLE_CHILD_NAME_SUFFIX,
				childPlanDetailsOutputDto.getPersonDto().getCdPersonSuffix(), CodesConstant.CSUFFIX);
		bookmarkNonGroupList.add(bookmarkChildNmSuffix);
		BookmarkDto bookmarkChildNmFirst = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_FIRST,
				childPlanDetailsOutputDto.getPersonDto().getNmPersonFirst());
		bookmarkNonGroupList.add(bookmarkChildNmFirst);
		BookmarkDto bookmarkChildNmLast = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_LAST,
				childPlanDetailsOutputDto.getPersonDto().getNmPersonLast());
		bookmarkNonGroupList.add(bookmarkChildNmLast);
		BookmarkDto bookmarkChildNmMiddle = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_MIDDLE,
				childPlanDetailsOutputDto.getPersonDto().getNmPersonMiddle());
		bookmarkNonGroupList.add(bookmarkChildNmMiddle);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonGroupList);

		return preFillData;
	}
	
	/**
	 *Method Name:	populateIndependentBookmarks
	 *Method Description:populate independent bookmarks
	 *@param childPlanDetailsOutputDto
	 *@return
	 */
	public List<BookmarkDto> populateIndependentBookmarks(ChildPlanDetailsOutputDto childPlanDetailsOutputDto){
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		// Populate the TITLE_CASE_NAME from CSEC02D
		BookmarkDto bookmarkCaseNm = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				childPlanDetailsOutputDto.getGenericCaseInfoDto().getNmCase());
		bookmarkNonFrmGrpList.add(bookmarkCaseNm);

		// populate the TITLE_CASE_NUMBER from CSEC02D
		BookmarkDto bookmarkTitleCaseNum = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				childPlanDetailsOutputDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNum);

		// populate the TITLE_CHILD_BIRTH_DT from CSES27D
		BookmarkDto bookmarkTitleChildBirthDt = createBookmark(BookmarkConstants.TITLE_CHILD_BIRTH_DT,
				DateUtils.stringDt(childPlanDetailsOutputDto.getPersonDto().getDtPersonBirth()));
		bookmarkNonFrmGrpList.add(bookmarkTitleChildBirthDt);

		// populate the TITLE_CHILD_PLAN_TYPE from CSES27D (CCPPLNTP)
		BookmarkDto bookmarkTitleChildPlanType = createBookmarkWithCodesTable(BookmarkConstants.TITLE_CHILD_PLAN_TYPE, childPlanDetailsOutputDto.getPersonDto().getCdCspPlanType(),
				CodesConstant.CCPPLNTP);
		bookmarkNonFrmGrpList.add(bookmarkTitleChildPlanType);

		// populate the TITLE_CHILD_NAME_SUFFIX from CSES27D (CSUFFIX2)
		BookmarkDto bookmarkTitleChildNmSuffix = createBookmarkWithCodesTable(BookmarkConstants.TITLE_CHILD_NAME_SUFFIX,
				childPlanDetailsOutputDto.getPersonDto().getCdPersonSuffix(),CodesConstant.CSUFFIX2);
		bookmarkNonFrmGrpList.add(bookmarkTitleChildNmSuffix);

		// populate the TITLE_CHILD_NAME_FIRST from CSES27D
		BookmarkDto bookmarkTitleChildNmFirst = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_FIRST,
				childPlanDetailsOutputDto.getPersonDto().getNmPersonFirst());
		bookmarkNonFrmGrpList.add(bookmarkTitleChildNmFirst);

		// populate the TITLE_CHILD_NAME_LAST from CSES27D
		BookmarkDto bookmarkTitleChildNmLast = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_LAST,
				childPlanDetailsOutputDto.getPersonDto().getNmPersonLast());
		bookmarkNonFrmGrpList.add(bookmarkTitleChildNmLast);

		// populate the TITLE_CHILD_NAME_MIDDLE from CSES27D
		BookmarkDto bookmarkTitleChildNmMiddle = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_MIDDLE,
				childPlanDetailsOutputDto.getPersonDto().getNmPersonMiddle());
		bookmarkNonFrmGrpList.add(bookmarkTitleChildNmMiddle);

		// populate the CHILD_PLAN_PARTICIP_COMMENT from CSES27D
		BookmarkDto bookmarkTitleChildPlanParticip = createBookmark(BookmarkConstants.CHILD_PLAN_PARTICIP_COMMENT,
				childPlanDetailsOutputDto.getPersonDto().getTxtCspParticpatnComment());
		bookmarkNonFrmGrpList.add(bookmarkTitleChildPlanParticip);

		// populate the CHILD_PLAN_INFO_NOT_AVAIL from CSES27D
		BookmarkDto bookmarkTitleChildPlanInfoNotAvail = createBookmark(BookmarkConstants.CHILD_PLAN_INFO_NOT_AVAIL,
				childPlanDetailsOutputDto.getPersonDto().getTxtInfoNotAvailable());
		bookmarkNonFrmGrpList.add(bookmarkTitleChildPlanInfoNotAvail);

		// populate the CHILD_PLAN_OTHER_ASSMT from CSES27D
		BookmarkDto bookmarkTitleChildPlanOtherAssnt = createBookmark(BookmarkConstants.CHILD_PLAN_OTHER_ASSMT,
				childPlanDetailsOutputDto.getPersonDto().getTxtOtherAssmt());
		bookmarkNonFrmGrpList.add(bookmarkTitleChildPlanOtherAssnt);
		
		return bookmarkNonFrmGrpList;
	}
	
	/**
	 *Method Name:	populateBlobDataNarrativeBookmarks
	 *Method Description:populate blobs
	 *@param childPlanDetailsOutputDto
	 *@return
	 */
	public List<BlobDataDto> populateBlobDataNarrativeBookmarks(ChildPlanDetailsOutputDto childPlanDetailsOutputDto){
		List<BlobDataDto> blobDataDtoForCallNarrative = new ArrayList<BlobDataDto>();

		// populate the OBJ_NARR_PSY (TName - CP_PSY_NARR)
		BlobDataDto blobDataObjNarrPsy = createBlobData(BookmarkConstants.OBJ_NARR_PSY, "CP_PSY_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataObjNarrPsy);

		// populate the OBJ_NARR_TRV (TName - CP_TRV_NARR)
		BlobDataDto blobDataObjNarrTrv = createBlobData(BookmarkConstants.OBJ_NARR_TRV, "CP_TRV_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataObjNarrTrv);

		// populate the IPS_NARR_PLP (TName - CP_PLP_NARR)
		BlobDataDto blobDataIpsNarrPlp = createBlobData(BookmarkConstants.IPS_NARR_PLP, "CP_PLP_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataIpsNarrPlp);

		// populate the IPS_NARR_PLS (TName - CP_PLS_NARR)
		BlobDataDto blobDataIpsNarrPls = createBlobData(BookmarkConstants.IPS_NARR_PLS, "CP_PLS_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataIpsNarrPls);

		// populate the OBJ_NARR_DSC (TName - CP_DSC_NARR)
		BlobDataDto blobDataObjNarrDsc = createBlobData(BookmarkConstants.OBJ_NARR_DSC, "CP_DSC_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataObjNarrDsc);

		// populate the OBJ_NARR_PER (TName - CP_PER_NARR)
		BlobDataDto blobDataObjNarrPer = createBlobData(BookmarkConstants.OBJ_NARR_PER, "CP_PER_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataObjNarrPer);

		// populate the OBJ_NARR_SUP (TName - CP_SUP_NARR)
		BlobDataDto blobDataObjNarrSup = createBlobData(BookmarkConstants.OBJ_NARR_SUP, "CP_SUP_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataObjNarrSup);

		// populate the NEED_TASK_SVC_NARR_SSC (TName - CP_SSC_NARR)
		BlobDataDto blobDataNeedTaskSvcNarrSSC = createBlobData(BookmarkConstants.NEED_TASK_SVC_NARR_SSC, "CP_SSC_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataNeedTaskSvcNarrSSC);

		// populate the IPS_NARR_APP (TName - CP_APP_NARR)
		BlobDataDto blobDataIPSNarrApp = createBlobData(BookmarkConstants.IPS_NARR_APP, "CP_APP_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataIPSNarrApp);
		
		// populate the VIS_NARR_PFC (TName - CP_PFC_NARR)
		BlobDataDto blobDataIPSNarrPFC = createBlobData(BookmarkConstants.VIS_NARR_PFC, "CP_PFC_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataIPSNarrPFC);

		// populate the OBJ_NARR_PCH (TName - CP_PCH_NARR)
		BlobDataDto blobDataObjNarrPCH = createBlobData(BookmarkConstants.OBJ_NARR_PCH, "CP_PCH_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataObjNarrPCH);

		// populate the IPS_NARR_CHG (TName - CP_CHG_NARR)
		BlobDataDto blobDataIPSNarrCHG = createBlobData(BookmarkConstants.IPS_NARR_CHG, "CP_CHG_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataIPSNarrCHG);

		// populate the IPS_NARR_BLOB_CPL (TName - CP_CPL_NARR)
		BlobDataDto blobDataCPCplNarr = createBlobData(BookmarkConstants.IPS_NARR_BLOB_CPL, "CP_CPL_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataCPCplNarr);

		// populate the IPS_NARR_VIS (TName - CP_VIS_NARR)
		BlobDataDto blobDataIPSNarrVis = createBlobData(BookmarkConstants.IPS_NARR_VIS, "CP_VIS_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataIPSNarrVis);

		// populate the OBJ_NARR_REC (TName - CP_REC_NARR)
		BlobDataDto blobDataObjNarrRec = createBlobData(BookmarkConstants.OBJ_NARR_REC, "CP_REC_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataObjNarrRec);

		// populate the PLN_NARR_PSP (TName - CP_PSP_NARR)
		BlobDataDto blobDataPlnNarrPsp = createBlobData(BookmarkConstants.PLN_NARR_PSP, "CP_PSP_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataPlnNarrPsp);

		// populate the PLN_NARR_REP (TName - CP_REP_NARR)
		BlobDataDto blobDataPlnNarrRep = createBlobData(BookmarkConstants.PLN_NARR_REP, "CP_REP_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataPlnNarrRep);

		// populate the OBJ_NARR_DPL (TName - CP_DPL_NARR)
		BlobDataDto blobDataObjNarrDpl = createBlobData(BookmarkConstants.OBJ_NARR_DPL, "CP_DPL_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataObjNarrDpl);

		// populate the IPS_NARR_DIP (TName - CP_DIP_NARR)
		BlobDataDto blobDataIpsNarrDip = createBlobData(BookmarkConstants.IPS_NARR_DIP, "CP_DIP_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoForCallNarrative.add(blobDataIpsNarrDip);
		return blobDataDtoForCallNarrative;
	}
	
	
	/**
	 *Method Name:	populateIPNPlanTypeBookmarks
	 *Method Description:populate for IPN plan type
	 *@param childPlanDetailsOutputDto
	 *@return
	 */
	public FormDataGroupDto populateCommonPlanTypeBookmarks(ChildPlanDetailsOutputDto childPlanDetailsOutputDto){
		// csc19o01
				
					FormDataGroupDto formdatagroupTmplatDetFactr = createFormDataGroup(
							FormGroupsConstants.TMPLAT_INIT_PLCMT_SUMM, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkInitPlacemntSumList = new ArrayList<BookmarkDto>();

					// populate the IPS_DATE_OF_NEXT_REVIEW CSES27D
					BookmarkDto bookMarkDtoDtCspNextReview = createBookmark(BookmarkConstants.IPS_DATE_OF_NEXT_REVIEW,
							DateUtils.stringDt(childPlanDetailsOutputDto.getPersonDto().getDtCspNextReview()));
					bookmarkInitPlacemntSumList.add(bookMarkDtoDtCspNextReview);

					// populate the IPS_PROJCTD_PERMANENCY_DATE CSES27D
					BookmarkDto bookMarkDtoIPSProjctdPermDt = createBookmark(BookmarkConstants.IPS_PROJCTD_PERMANENCY_DATE,
							DateUtils.stringDt(childPlanDetailsOutputDto.getPersonDto().getDtcspPermGoalTarget()));
					bookmarkInitPlacemntSumList.add(bookMarkDtoIPSProjctdPermDt);

					// populate the IPS_PERMANENCY_GOAL(Cdtble CCPPRMGL) CSES27D
					BookmarkDto bookMarkIPSPermanencyGoal = createBookmarkWithCodesTable(BookmarkConstants.IPS_PERMANENCY_GOAL,
							childPlanDetailsOutputDto.getPersonDto().getCspPlanPermGoal(),CodesConstant.CCPPRMGL);
					bookmarkInitPlacemntSumList.add(bookMarkIPSPermanencyGoal);

					// populate the IPS_CHILD_RELIGION(Cdtble CRELIGNS) CSES27D
					BookmarkDto bookMarkIPSChildReligion = createBookmarkWithCodesTable(BookmarkConstants.IPS_CHILD_RELIGION,
							childPlanDetailsOutputDto.getPersonDto().getCdPersonReligion(),CodesConstant.CRELIGNS);
					bookmarkInitPlacemntSumList.add(bookMarkIPSChildReligion);

					// populate the IPS_DISCREPANCY CSES27D
					BookmarkDto bookMarkIPSDiscrepancy = createBookmark(BookmarkConstants.IPS_DISCREPANCY,
							childPlanDetailsOutputDto.getPersonDto().getTxtCspLosDiscrepancy());
					bookmarkInitPlacemntSumList.add(bookMarkIPSDiscrepancy);

					// populate the IPS_ESTIMATED_LENGTH_OF_STAY CSES27D
					BookmarkDto bookMarkIPSLengthOfStay = createBookmark(BookmarkConstants.IPS_ESTIMATED_LENGTH_OF_STAY,
							childPlanDetailsOutputDto.getPersonDto().getTxtCspLengthOfStay());
					bookmarkInitPlacemntSumList.add(bookMarkIPSLengthOfStay);

					// populate the blob IPS_NARR_BLOB_ISH(TName-CP_ISH_NARR) CSES27D
					List<BlobDataDto> blobDataDtoInitPlcmntSumList = new ArrayList<BlobDataDto>();
					BlobDataDto blobDataIPSNarrBlobIsh = createBlobData(BookmarkConstants.IPS_NARR_BLOB_ISH, "CP_ISH_NARR",
							childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
					blobDataDtoInitPlcmntSumList.add(blobDataIPSNarrBlobIsh);

					// populate the blob IPS_NARR_BLOB_ICH(TName-CP_ICH_NARR) CSES27D
					BlobDataDto blobDataIPSNarrBlobIch = createBlobData(BookmarkConstants.IPS_NARR_BLOB_ICH, "CP_ICH_NARR",
							childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
					blobDataDtoInitPlcmntSumList.add(blobDataIPSNarrBlobIch);

					// populate the blob IPS_NARR_BLOB_CPL(TName-CP_CPL_NARR) CSES27D
					BlobDataDto blobDataIPSNarrBlobCpl = createBlobData(BookmarkConstants.IPS_NARR_BLOB_CPL, "CP_CPL_NARR",
							childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
					blobDataDtoInitPlcmntSumList.add(blobDataIPSNarrBlobCpl);

					formdatagroupTmplatDetFactr.setBookmarkDtoList(bookmarkInitPlacemntSumList);
					formdatagroupTmplatDetFactr.setBlobDataDtoList(blobDataDtoInitPlcmntSumList);

					List<FormDataGroupDto> allFormDataGroupDto = new ArrayList<FormDataGroupDto>();

					// csc19o58
					if ( "Y".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersonLivArr())) {
						List<FormDataGroupDto> formDataGroupTempltTxt1List = new ArrayList<FormDataGroupDto>();
						FormDataGroupDto formDataGroupTmpltTxt1 = createFormDataGroup(FormGroupsConstants.TMPLAT_TXT_1,
								FormGroupsConstants.TMPLAT_INIT_PLCMT_SUMM);
						formDataGroupTempltTxt1List.add(formDataGroupTmpltTxt1);
						allFormDataGroupDto.addAll(formDataGroupTempltTxt1List);
					}

					// csc19o63
					if ( "N".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersGuardCnsrv())) {
						List<FormDataGroupDto> formDataGroupTmpltOldConGoal1List = new ArrayList<FormDataGroupDto>();
						FormDataGroupDto formDataGroupTmpltOldConGoal1 = createFormDataGroup(
								FormGroupsConstants.TMPLAT_OLD_CON_GOAL1, FormGroupsConstants.TMPLAT_INIT_PLCMT_SUMM);

						// populate the blob IPS_NARR_BLOB_CPL(Tname-CP_CPL_NARR)CSES27D
						List<BlobDataDto> blobdataNarrBlobCPLList = new ArrayList<BlobDataDto>();
						BlobDataDto blobdataNarrBlobCPL = createBlobData(BookmarkConstants.IPS_NARR_BLOB_CPL, "CP_CPL_NARR",
								childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());

						blobdataNarrBlobCPLList.add(blobdataNarrBlobCPL);
						formDataGroupTmpltOldConGoal1.setBlobDataDtoList(blobdataNarrBlobCPLList);
						formDataGroupTmpltOldConGoal1List.add(formDataGroupTmpltOldConGoal1);
						allFormDataGroupDto.addAll(formDataGroupTmpltOldConGoal1List);
					}// csc19o64
					else if ("Y".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersGuardCnsrv())) {
						List<FormDataGroupDto> formDataGroupTmpltNewConGoalOneList = new ArrayList<FormDataGroupDto>();
						FormDataGroupDto formDataTmpltNewConGoalOne = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NEW_CON_GOAL1, FormGroupsConstants.TMPLAT_INIT_PLCMT_SUMM);

						// csc19o64-->csc19o65
						if ( "Y".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getIndNoConGoal())) {
							List<FormDataGroupDto> formDataGroupTmpNoConGoalOneList = new ArrayList<FormDataGroupDto>();
							FormDataGroupDto formDataGroupTmpNoConGoalOne = createFormDataGroup(
									FormGroupsConstants.TMPLAT_NO_CON_GOAL1, FormGroupsConstants.TMPLAT_NEW_CON_GOAL1);
							List<BookmarkDto> bookmarkDtoCpTxtNoConGoal1List = new ArrayList<BookmarkDto>();
							BookmarkDto bookmarkDtoCpTxtNoConGoal1 = createBookmark(BookmarkConstants.CP_TXT_NO_CON_GOAL1,
									childPlanDetailsOutputDto.getPersonDto().getTxtNoConsGoal());
							bookmarkDtoCpTxtNoConGoal1List.add(bookmarkDtoCpTxtNoConGoal1);
							formDataGroupTmpNoConGoalOne.setBookmarkDtoList(bookmarkDtoCpTxtNoConGoal1List);
							formDataGroupTmpNoConGoalOneList.add(formDataGroupTmpNoConGoalOne);
							formDataTmpltNewConGoalOne.setFormDataGroupList(formDataGroupTmpNoConGoalOneList);
						}// csc19o64-->csc19o66
						else if ( "N".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getIndNoConGoal())) {
							List<FormDataGroupDto> formDataGroupTempConGoal1List = new ArrayList<FormDataGroupDto>();
							FormDataGroupDto formDataGroupTempConGoal1 = createFormDataGroup(
									FormGroupsConstants.TMPLAT_CON_GOAL1, FormGroupsConstants.TMPLAT_NEW_CON_GOAL1);

							// csc19o64-->csc19o66-->csc19o67
							List<FormDataGroupDto> formDataTmplatConGoalList = new ArrayList<FormDataGroupDto>();
							FormDataGroupDto formDataTmplatConGoal = createFormDataGroup(
									FormGroupsConstants.TMPLAT_CON_GOAL_LIST1, FormGroupsConstants.TMPLAT_CON_GOAL1);
							// populate the bookmark CP_CD_CON_GOAL1
							List<BookmarkDto> bookmarkCdConGoal1List = new ArrayList<BookmarkDto>();
							List<ConcurrentGoalDto> ConcurrentGoalDtoList = childPlanDetailsOutputDto.getConcurrentDtoList();
							for (ConcurrentGoalDto fetchFullDto : ConcurrentGoalDtoList) {
								BookmarkDto bookmarkCdConGoal1 = createBookmarkWithCodesTable(BookmarkConstants.CP_CD_CON_GOAL1,
										fetchFullDto.getCdConcurrentGoal(),CodesConstant.CCPPRMGL);
								bookmarkCdConGoal1List.add(bookmarkCdConGoal1);
							}

							formDataTmplatConGoal.setBookmarkDtoList(bookmarkCdConGoal1List);
							formDataTmplatConGoalList.add(formDataTmplatConGoal);
							formDataGroupTempConGoal1.setFormDataGroupList(formDataTmplatConGoalList);

							formDataGroupTempConGoal1List.add(formDataGroupTempConGoal1);
							formDataTmpltNewConGoalOne.setFormDataGroupList(formDataGroupTempConGoal1List);
						}
						formDataGroupTmpltNewConGoalOneList.add(formDataTmpltNewConGoalOne);
						allFormDataGroupDto.addAll(formDataGroupTmpltNewConGoalOneList);
					}

					// csc19o30
					List<FormDataGroupDto> formDataGroupSprDtPlanCompletedList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataGroupDtoSprDtPlanCompleted = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SPR_DATE_PLAN_COMPLETED, FormGroupsConstants.TMPLAT_INIT_PLCMT_SUMM);
					// populate the bookmark SPR_DATE_PLAN_COMPLETE
					List<BookmarkDto> bookmarkDtPlanCompltList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkDtPlanComplt = createBookmark(BookmarkConstants.SPR_DATE_PLAN_COMPLETE,
							DateUtils.stringDt(childPlanDetailsOutputDto.getChildPlanDetailsDto().getDtCspPlanCompleted()));
					bookmarkDtPlanCompltList.add(bookmarkDtPlanComplt);
					formDataGroupDtoSprDtPlanCompleted.setBookmarkDtoList(bookmarkDtPlanCompltList);
					formDataGroupSprDtPlanCompletedList.add(formDataGroupDtoSprDtPlanCompleted);
					allFormDataGroupDto.addAll(formDataGroupSprDtPlanCompletedList);

					// csc19o03
					List<FormDataGroupDto> formDataGroupTempltWorkrCompletdList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataGroupTempltWorkrCompletd = createFormDataGroup(
							FormGroupsConstants.TMPLAT_CP_WORKER_WHO_COMPLETED, FormGroupsConstants.TMPLAT_INIT_PLCMT_SUMM);
					List<BookmarkDto> bookmarkDtoTempltWorkrCompletdList = new ArrayList<BookmarkDto>();
					
					// populate the bookmark CP_WORKER_SUFFIX_COMP CodeTableNm CSUFFIX2
					BookmarkDto bookmarkCPWrkrSuffix = createBookmarkWithCodesTable(BookmarkConstants.CP_WORKER_SUFFIX_COMP,
							childPlanDetailsOutputDto.getPersonList().getCdPersonSuffix(),CodesConstant.CSUFFIX2);
					bookmarkDtoTempltWorkrCompletdList.add(bookmarkCPWrkrSuffix);

					// populate the bookmark CP_WORKER_FIRST_COMP
					BookmarkDto bookmarkCPWrkrFirst = createBookmark(BookmarkConstants.CP_WORKER_FIRST_COMP,
							childPlanDetailsOutputDto.getPersonList().getNmPersonFirst());
					bookmarkDtoTempltWorkrCompletdList.add(bookmarkCPWrkrFirst);

					// populate the bookmark CP_WORKER_LAST_COMP
					BookmarkDto bookmarkCPWrkrLast = createBookmark(BookmarkConstants.CP_WORKER_LAST_COMP,
							childPlanDetailsOutputDto.getPersonList().getNmPersonLast());
					bookmarkDtoTempltWorkrCompletdList.add(bookmarkCPWrkrLast);

					// populate the bookmark CP_WORKER_MIDDLE_COMP
					BookmarkDto bookmarkCPWrkrMiddle = createBookmark(BookmarkConstants.CP_WORKER_MIDDLE_COMP,
							childPlanDetailsOutputDto.getPersonList().getNmPersonMiddle());
					bookmarkDtoTempltWorkrCompletdList.add(bookmarkCPWrkrMiddle);
					formDataGroupTempltWorkrCompletd.setBookmarkDtoList(bookmarkDtoTempltWorkrCompletdList);

					// csc19o03-->cfzco00
					if (StringUtils.isNotBlank(childPlanDetailsOutputDto.getPersonList().getCdPersonSuffix())) {
						List<FormDataGroupDto> formDatagroupTempltBList = new ArrayList<FormDataGroupDto>();
						FormDataGroupDto formDataGroupTempltCommaB = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_B,
								FormGroupsConstants.TMPLAT_CP_WORKER_WHO_COMPLETED);
						formDatagroupTempltBList.add(formDataGroupTempltCommaB);
						formDataGroupTempltWorkrCompletd.setFormDataGroupList(formDatagroupTempltBList);
					}
					
					formDataGroupTempltWorkrCompletdList.add(formDataGroupTempltWorkrCompletd);
					allFormDataGroupDto.addAll(formDataGroupTempltWorkrCompletdList);
					formdatagroupTmplatDetFactr.setFormDataGroupList(allFormDataGroupDto);
					return 	formdatagroupTmplatDetFactr;		
				

	}
	
	/**
	 *Method Name:	populateRVWPlanTypeBookmarks
	 *Method Description:populate for RVW plan type
	 *@param childPlanDetailsOutputDto
	 *@return
	 */
	public FormDataGroupDto populateCommonReviewPlanTypeBookmarks(ChildPlanDetailsOutputDto childPlanDetailsOutputDto){
		FormDataGroupDto formDataGroupTmplatSvcPlanRev1 = createFormDataGroup(
				FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_1, FormConstants.EMPTY_STRING);

		List<BookmarkDto> bookmarkDtoSvcPlanReviewList = new ArrayList<BookmarkDto>();
		// populate Bookmark SPR_DATE_OF_NEXT_REVIEW CSES27D
		BookmarkDto bookmarkSprDtOfNextReview = createBookmark(BookmarkConstants.SPR_DATE_OF_NEXT_REVIEW,
				DateUtils.stringDt(childPlanDetailsOutputDto.getPersonDto().getDtCspNextReview()));
		bookmarkDtoSvcPlanReviewList.add(bookmarkSprDtOfNextReview);

		// populate Bookmark SPR_PROJECTED_PERMANENCY_DATE CSES27D
		BookmarkDto bookmarkSprProjectedPermanencyDt = createBookmark(
				BookmarkConstants.SPR_PROJECTED_PERMANENCY_DATE,
				DateUtils.stringDt(childPlanDetailsOutputDto.getPersonDto().getDtcspPermGoalTarget()));
		bookmarkDtoSvcPlanReviewList.add(bookmarkSprProjectedPermanencyDt);

		// populate Bookmark SPR_PERMANENCY_GOAL Ctable-CCPPRMGL CSES27D
		BookmarkDto bookmarkSprPermanencyGoal = createBookmarkWithCodesTable(BookmarkConstants.SPR_PERMANENCY_GOAL,
				childPlanDetailsOutputDto.getPersonDto().getCspPlanPermGoal(),CodesConstant.CCPPRMGL);
		bookmarkDtoSvcPlanReviewList.add(bookmarkSprPermanencyGoal);

		// populate Bookmark SPR_DISCREPANCY_LENGTH_OF_STAY CSES27D
		BookmarkDto bookmarkSprDiscrepancyLenOfStay = createBookmark(
				BookmarkConstants.SPR_DISCREPANCY_LENGTH_OF_STAY,
				childPlanDetailsOutputDto.getPersonDto().getTxtCspLosDiscrepancy());
		bookmarkDtoSvcPlanReviewList.add(bookmarkSprDiscrepancyLenOfStay);

		// populate Bookmark SPR_EST_LENGTH_OF_STAY CSES27D
		BookmarkDto bookmarkSprEstLenOfStay = createBookmark(BookmarkConstants.SPR_EST_LENGTH_OF_STAY,
				childPlanDetailsOutputDto.getPersonDto().getTxtCspLengthOfStay());
		bookmarkDtoSvcPlanReviewList.add(bookmarkSprEstLenOfStay);

		// populate Blobdata IPS_NARR_BLOB_CPL TName-CP_CPL_NARR CSES27D
		List<BlobDataDto> blobdataNarrBlobCPLList = new ArrayList<BlobDataDto>();
		BlobDataDto blobdataNarrBlobCPL = createBlobData(BookmarkConstants.IPS_NARR_BLOB_CPL, "CP_CPL_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobdataNarrBlobCPLList.add(blobdataNarrBlobCPL);

		formDataGroupTmplatSvcPlanRev1.setBlobDataDtoList(blobdataNarrBlobCPLList);
		formDataGroupTmplatSvcPlanRev1.setBookmarkDtoList(bookmarkDtoSvcPlanReviewList);

		List<FormDataGroupDto> allFormDataGroupListTwo = new ArrayList<FormDataGroupDto>();
		// csc19o69
		if ( "Y".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersGuardCnsrv())) {
			List<FormDataGroupDto> formDataGroupTmplatNewConGoal2List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGroupTmplatNewConGoal2 = createFormDataGroup(
					FormGroupsConstants.TMPLAT_NEW_CON_GOAL2, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_1);
			// csc19o69-->csc19o70
			if ( "Y".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getIndNoConGoal())) {

				List<FormDataGroupDto> formDataGroupTmplatNoConGoal2List = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDataGroupTmplatNoConGoal2 = createFormDataGroup(
						FormGroupsConstants.TMPLAT_NO_CON_GOAL2, FormGroupsConstants.TMPLAT_NEW_CON_GOAL2);
				List<BookmarkDto> bookmarkDtoCPTxtNoConGoal2List = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkDtoCPTxtNoConGoal2 = createBookmark(BookmarkConstants.CP_TXT_NO_CON_GOAL2,
						childPlanDetailsOutputDto.getPersonDto().getTxtNoConsGoal());
				bookmarkDtoCPTxtNoConGoal2List.add(bookmarkDtoCPTxtNoConGoal2);
				formDataGroupTmplatNoConGoal2.setBookmarkDtoList(bookmarkDtoCPTxtNoConGoal2List);
				formDataGroupTmplatNoConGoal2List.add(formDataGroupTmplatNoConGoal2);
				formDataGroupTmplatNewConGoal2.setFormDataGroupList(formDataGroupTmplatNoConGoal2List);
			}
			// csc19o69-->csc19o71
			else if ("N".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getIndNoConGoal())) {
				List<FormDataGroupDto> formDataGroupTmplatConGoal2List = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDataGroupTmplatConGoal2 = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CON_GOAL2, FormGroupsConstants.TMPLAT_NEW_CON_GOAL2);
				// csc19o69-->csc19o71-->csc19o72
				List<FormDataGroupDto> formDataGroupTmplatConGlList2 = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDataGroupTmplatConGl2 = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CON_GOAL_LIST2, FormGroupsConstants.TMPLAT_CON_GOAL2);

				// populate the bookmark CP_CD_CON_GOAL1
				List<BookmarkDto> bookmarkCdConGoal1List = new ArrayList<BookmarkDto>();
				List<ConcurrentGoalDto> fetchFullConcurrentDtoList = childPlanDetailsOutputDto
						.getConcurrentDtoList();
				for (ConcurrentGoalDto fetchFullDto : fetchFullConcurrentDtoList) {
					BookmarkDto bookmarkCdConGoal1 = createBookmarkWithCodesTable(BookmarkConstants.CP_CD_CON_GOAL2,
							fetchFullDto.getCdConcurrentGoal(),CodesConstant.CCPPRMGL);
					bookmarkCdConGoal1List.add(bookmarkCdConGoal1);
					formDataGroupTmplatConGl2.setBookmarkDtoList(bookmarkCdConGoal1List);
				}

				formDataGroupTmplatConGlList2.add(formDataGroupTmplatConGl2);
				formDataGroupTmplatConGoal2.setFormDataGroupList(formDataGroupTmplatConGlList2);
				formDataGroupTmplatConGoal2List.add(formDataGroupTmplatConGoal2);
				formDataGroupTmplatNewConGoal2.setFormDataGroupList(formDataGroupTmplatConGoal2List);
			}
			formDataGroupTmplatNewConGoal2List.add(formDataGroupTmplatNewConGoal2);
			allFormDataGroupListTwo.addAll(formDataGroupTmplatNewConGoal2List);
		}
		// csc19o68
		else if ("N".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersGuardCnsrv())) {
			List<FormDataGroupDto> formDataGrpTmpltOldConGoal2List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGrpTmpltOldConGoal2 = createFormDataGroup(
					FormGroupsConstants.TMPLAT_OLD_CON_GOAL2, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_1);

			// populate Blobdata IPS_NARR_BLOB_CPL TName-CP_CPL_NARR CSES27D
			List<BlobDataDto> blobdataNarrBlobList = new ArrayList<BlobDataDto>();
			BlobDataDto blobdataNarrBlbCPL = createBlobData(BookmarkConstants.IPS_NARR_BLOB_CPL, "CP_CPL_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobdataNarrBlobList.add(blobdataNarrBlbCPL);

			// setting the blobList to FormDataGroup
			formDataGrpTmpltOldConGoal2.setBlobDataDtoList(blobdataNarrBlobList);
			formDataGrpTmpltOldConGoal2List.add(formDataGrpTmpltOldConGoal2);

			// setting the FormDataList to AllForDataGroupListTwo
			allFormDataGroupListTwo.addAll(formDataGrpTmpltOldConGoal2List);
		}

		// csc19o59
		if ("Y".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersonLivArr())) {
			List<FormDataGroupDto> formDataGrpTmpltTxt2List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGrpTmpltTxt2 = createFormDataGroup(FormGroupsConstants.TMPLAT_TXT_2,
					FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_1);
			formDataGrpTmpltTxt2List.add(formDataGrpTmpltTxt2);
			allFormDataGroupListTwo.addAll(formDataGrpTmpltTxt2List);
		}

		// csc19o05
		List<FormDataGroupDto> formDataGrpTmpltSprDtOfLastPlanList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto formDataGrpTmpltSprDtOfLastPlan = createFormDataGroup(
				FormGroupsConstants.TMPLAT_SPR_DATE_OF_LAST_PLAN, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_1);
		// Populating bookmark SPR_DATE_OF_LAST_PLAN CSEC14D
		List<BookmarkDto> bookmarkSprDtOfLastPlanList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkSprDtOfLastPlan = createBookmark(BookmarkConstants.SPR_DATE_OF_LAST_PLAN,
				DateUtils.stringDt(childPlanDetailsOutputDto.getChildPlanDetailsDto().getDateOfLastPlan()));

		bookmarkSprDtOfLastPlanList.add(bookmarkSprDtOfLastPlan);
		formDataGrpTmpltSprDtOfLastPlan.setBookmarkDtoList(bookmarkSprDtOfLastPlanList);
		formDataGrpTmpltSprDtOfLastPlanList.add(formDataGrpTmpltSprDtOfLastPlan);
		allFormDataGroupListTwo.addAll(formDataGrpTmpltSprDtOfLastPlanList);

		// csc19o30
		List<FormDataGroupDto> formDataGrpTmpltSprDtOfLastPlanComList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto formDataGrpTmpltSprDtOfLastPlanComp = createFormDataGroup(
				FormGroupsConstants.TMPLAT_SPR_DATE_PLAN_COMPLETED, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_1);
		// Populating bookmark SPR_DATE_PLAN_COMPLETE CSEC14D
		List<BookmarkDto> bookmarkSprDtOfLastPlanCompList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkSprDtOfLastPlanCompReviewOne = createBookmark(BookmarkConstants.SPR_DATE_PLAN_COMPLETE,
				DateUtils.stringDt(childPlanDetailsOutputDto.getChildPlanDetailsDto().getDtCspPlanCompleted()));

		bookmarkSprDtOfLastPlanCompList.add(bookmarkSprDtOfLastPlanCompReviewOne);
		formDataGrpTmpltSprDtOfLastPlanComp.setBookmarkDtoList(bookmarkSprDtOfLastPlanCompList);
		formDataGrpTmpltSprDtOfLastPlanComList.add(formDataGrpTmpltSprDtOfLastPlanComp);
		allFormDataGroupListTwo.addAll(formDataGrpTmpltSprDtOfLastPlanComList);

		// csc19o07
		List<FormDataGroupDto> formDataGrpTmplatSprCurPlcmtList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto formDataGrpTmplatSprCurPlcmt = createFormDataGroup(
				FormGroupsConstants.TMPLAT_SPR_CURRENT_PLCMT, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_1);

		List<BookmarkDto> bookmarkTmpltSprCurrPlacmtList = new ArrayList<BookmarkDto>();

		// Populating bookmark SPR_PLCMT_LIVING_ARRANGEMENT CTName-CPLLAFRM
		// CSES28D
		BookmarkDto bookmarkDtoPlcmtLivArr = createBookmarkWithCodesTable(BookmarkConstants.SPR_PLCMT_LIVING_ARRANGEMENT,
				childPlanDetailsOutputDto.getCapsPlacemntDto().getCdPlcmtLivArr(),CodesConstant.CPLLAFRM);
		bookmarkTmpltSprCurrPlacmtList.add(bookmarkDtoPlcmtLivArr);

		// Populating bookmark SPR_PLCMT_FACILITY_TYPE CTName-CFACTYP2
		// CSES28D
		BookmarkDto bookmarkDtoPlcmtFacType = createBookmarkWithCodesTable(BookmarkConstants.SPR_PLCMT_FACILITY_TYPE,
				childPlanDetailsOutputDto.getCapsPlacemntDto().getCdRsrcFacilType(),CodesConstant.CFACTYP2);
		bookmarkTmpltSprCurrPlacmtList.add(bookmarkDtoPlcmtFacType);

		// Populating bookmark SPR_PLCMT_RESIDENCE_NAME
		BookmarkDto bookmarkDtoPlcmtResNm = createBookmark(BookmarkConstants.SPR_PLCMT_RESIDENCE_NAME,
				childPlanDetailsOutputDto.getCapsPlacemntDto().getNmPlcmtFacil());
		bookmarkTmpltSprCurrPlacmtList.add(bookmarkDtoPlcmtResNm);

		// Populating bookmark SPR_PLCMT_PERSON_NAME
		BookmarkDto bookmarkDtoPlcmtPersnNm = createBookmark(BookmarkConstants.SPR_PLCMT_PERSON_NAME,
				childPlanDetailsOutputDto.getCapsPlacemntDto().getNmPlcmtPersonFull());
		bookmarkTmpltSprCurrPlacmtList.add(bookmarkDtoPlcmtPersnNm);

		formDataGrpTmplatSprCurPlcmt.setBookmarkDtoList(bookmarkTmpltSprCurrPlacmtList);
		formDataGrpTmplatSprCurPlcmtList.add(formDataGrpTmplatSprCurPlcmt);
		allFormDataGroupListTwo.addAll(formDataGrpTmplatSprCurPlcmtList);

		// csc19o03
		List<FormDataGroupDto> formDataGroupTmplatCpWrkrCompletdList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto formDataGroupTmplatCpWrkrCompletd = createFormDataGroup(
				FormGroupsConstants.TMPLAT_CP_WORKER_WHO_COMPLETED, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_1);

		List<BookmarkDto> bookmarkDtoTmpatCpWrkrCompletdList = new ArrayList<BookmarkDto>();

		// Populating CP_WORKER_SUFFIX_COMP CtbleNm - CSUFFIX2 CSEC74D
		BookmarkDto bookmarkDtoCpWrkrSuffixComp = createBookmarkWithCodesTable(BookmarkConstants.CP_WORKER_SUFFIX_COMP,
				childPlanDetailsOutputDto.getPersonList().getCdPersonSuffix(),CodesConstant.CSUFFIX2);
		bookmarkDtoTmpatCpWrkrCompletdList.add(bookmarkDtoCpWrkrSuffixComp);

		// Populating CP_WORKER_FIRST_COMP CSEC74D
		BookmarkDto bookmarkDtoCpWrkrFirstComp = createBookmark(BookmarkConstants.CP_WORKER_FIRST_COMP,
				childPlanDetailsOutputDto.getPersonList().getNmPersonFirst());
		bookmarkDtoTmpatCpWrkrCompletdList.add(bookmarkDtoCpWrkrFirstComp);

		// Populating CP_WORKER_LAST_COMP CSEC74D
		BookmarkDto bookmarkDtoCpWrkrLastComp = createBookmark(BookmarkConstants.CP_WORKER_LAST_COMP,
				childPlanDetailsOutputDto.getPersonList().getNmPersonLast());
		bookmarkDtoTmpatCpWrkrCompletdList.add(bookmarkDtoCpWrkrLastComp);
		// Populating CP_WORKER_MIDDLE_COMP CSEC74D

		formDataGroupTmplatCpWrkrCompletd.setBookmarkDtoList(bookmarkDtoTmpatCpWrkrCompletdList);
		// csc19o03-->cfzco00
		List<FormDataGroupDto> formDataGroupTemplatCommaDList = new ArrayList<FormDataGroupDto>();
		if (StringUtils.isNotBlank(childPlanDetailsOutputDto.getPersonList().getCdPersonSuffix())) {
			FormDataGroupDto formDataGroupTemplatCommaD = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_D,
					FormGroupsConstants.TMPLAT_CP_WORKER_WHO_COMPLETED);
			formDataGroupTemplatCommaDList.add(formDataGroupTemplatCommaD);
		}
		formDataGroupTmplatCpWrkrCompletd.setFormDataGroupList(formDataGroupTemplatCommaDList);

		formDataGroupTmplatCpWrkrCompletdList.add(formDataGroupTmplatCpWrkrCompletd);
		allFormDataGroupListTwo.addAll(formDataGroupTmplatCpWrkrCompletdList);
		formDataGroupTmplatSvcPlanRev1.setFormDataGroupList(allFormDataGroupListTwo);
		return formDataGroupTmplatSvcPlanRev1;
	}
	
	/**
	 *Method Name:	populateIPLPlanTypeBookmarks
	 *Method Description:populate for IPL plan type
	 *@param childPlanDetailsOutputDto
	 *@return
	 */
	public FormDataGroupDto populatePALPostDiscahrgeObjTypeBookmarks(ChildPlanDetailsOutputDto childPlanDetailsOutputDto){

		FormDataGroupDto formDataGroupTmplatPal = createFormDataGroup(FormGroupsConstants.TMPLAT_PAL,
				FormConstants.EMPTY_STRING);

		// Populate BlobData PAL_NARR_PAL TName-CP_PAL_NARR
		List<BlobDataDto> blobdataNarrPalList = new ArrayList<BlobDataDto>();
		BlobDataDto blobdataNarrPal = createBlobData(BookmarkConstants.PAL_NARR_PAL, "CP_PAL_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobdataNarrPalList.add(blobdataNarrPal);
		// Populate BlobData PAL_NARR_PDO TName-CP_PDO_NARR
		BlobDataDto blobdataNarrPdo = createBlobData(BookmarkConstants.PAL_NARR_PDO, "CP_PDO_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobdataNarrPalList.add(blobdataNarrPdo);

		formDataGroupTmplatPal.setBlobDataDtoList(blobdataNarrPalList);
		List<FormDataGroupDto> allFormDataGroupListThree = new ArrayList<FormDataGroupDto>();
		// csc19o38  // csc19o36
		if ("Y".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersonLivArr())) {
			List<FormDataGroupDto> formDataGroupTemplatPadNwList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGroupTemplatPadNw = createFormDataGroup(FormGroupsConstants.TMPLAT_PAD_NEW,
					FormGroupsConstants.TMPLAT_PAL);
			formDataGroupTemplatPadNwList.add(formDataGroupTemplatPadNw);
			allFormDataGroupListThree.addAll(formDataGroupTemplatPadNwList);
			
			List<FormDataGroupDto> formDataGroupTemplatPalNwList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGroupTemplatPalNw = createFormDataGroup(FormGroupsConstants.TMPLAT_PAL_NEW,
					FormGroupsConstants.TMPLAT_PAL);
			formDataGroupTemplatPalNwList.add(formDataGroupTemplatPalNw);
			allFormDataGroupListThree.addAll(formDataGroupTemplatPalNwList);
		}
		// csc19o35 // csc19o37
		else if ( "N".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersonLivArr())) {
			List<FormDataGroupDto> formDataGroupTemplatPalOldList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGroupTemplatPalOld = createFormDataGroup(FormGroupsConstants.TMPLAT_PAL_OLD,
					FormGroupsConstants.TMPLAT_PAL);
			formDataGroupTemplatPalOldList.add(formDataGroupTemplatPalOld);
			allFormDataGroupListThree.addAll(formDataGroupTemplatPalOldList);
			
			List<FormDataGroupDto> formDataGroupTemplatPadOldList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGroupTemplatPadOld = createFormDataGroup(FormGroupsConstants.TMPLAT_PAD_OLD,
					FormGroupsConstants.TMPLAT_PAL);
			formDataGroupTemplatPadOldList.add(formDataGroupTemplatPadOld);
			allFormDataGroupListThree.addAll(formDataGroupTemplatPadOldList);
		}
		formDataGroupTmplatPal.setFormDataGroupList(allFormDataGroupListThree);
		
		return formDataGroupTmplatPal;
	}
	
	/**
	 *Method Name:	populateIPTPlanTypeBookmarks
	 *Method Description:populate for IPT plan type
	 *@param childPlanDetailsOutputDto
	 *@return
	 */
	public FormDataGroupDto populateIPPlanTypeBookmarks(ChildPlanDetailsOutputDto childPlanDetailsOutputDto){

		FormDataGroupDto formDataGrpTmpltTherapyNeeds = createFormDataGroup(FormGroupsConstants.TMPLAT_THERAP_NEEDS,
				FormConstants.EMPTY_STRING);

		List<BlobDataDto> blobdataTherNarrPalList = new ArrayList<BlobDataDto>();

		// Populate blobdata THERAP_NARR_FAM TName-CP_FAM_NARR
		BlobDataDto blobdataTherNarrFam = createBlobData(BookmarkConstants.THERAP_NARR_FAM, "CP_FAM_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobdataTherNarrPalList.add(blobdataTherNarrFam);

		// Populate blobdata THERAP_NARR_TSM TName-CP_TSM_NARR
		BlobDataDto blobdataTherNarrTsm = createBlobData(BookmarkConstants.THERAP_NARR_TSM, "CP_TSM_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobdataTherNarrPalList.add(blobdataTherNarrTsm);

		// Populate blobdata THERAP_NARR_TSW TName-CP_TSW_NARR
		BlobDataDto blobdataTherNarrTsw = createBlobData(BookmarkConstants.THERAP_NARR_TSW, "CP_TSW_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobdataTherNarrPalList.add(blobdataTherNarrTsw);

		// Populate blobdata THERAP_NARR_TRM TName-CP_TRM_NARR
		BlobDataDto blobdataTherNarrTrm = createBlobData(BookmarkConstants.THERAP_NARR_TRM, "CP_TRM_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobdataTherNarrPalList.add(blobdataTherNarrTrm);
		formDataGrpTmpltTherapyNeeds.setBlobDataDtoList(blobdataTherNarrPalList);

		List<FormDataGroupDto> allFormDataGroupListFour = new ArrayList<FormDataGroupDto>();
		// csc19o33
		if ( "N".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersonLivArr())) {
			List<FormDataGroupDto> formDataTempGoldGTList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTempGoldGT = createFormDataGroup(FormGroupsConstants.TMPLAT_OLD_GT,
					FormGroupsConstants.TMPLAT_THERAP_NEEDS);

			List<BlobDataDto> blobdataObjNarrRecList = new ArrayList<BlobDataDto>();
			// Populating BlobData OBJ_NARR_REC TName-CP_REC_NARR
			BlobDataDto blobdataObjNarrRec = createBlobData(BookmarkConstants.OBJ_NARR_REC, "CP_REC_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobdataObjNarrRecList.add(blobdataObjNarrRec);
			// Populating BlobData OBJ_NARR_PSY TName-CP_PSY_NARR
			BlobDataDto blobdataObjNarrPsy = createBlobData(BookmarkConstants.OBJ_NARR_PSY, "CP_PSY_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobdataObjNarrRecList.add(blobdataObjNarrPsy);
			formDataTempGoldGT.setBlobDataDtoList(blobdataObjNarrRecList);
			formDataTempGoldGTList.add(formDataTempGoldGT);
			allFormDataGroupListFour.addAll(formDataTempGoldGTList);

		}// csc19o34		
		else if ("Y".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersonLivArr())) {
		List<FormDataGroupDto> formDataTempTSWMList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto formDataTempTSWM = createFormDataGroup(FormGroupsConstants.TMPLAT_TSWM,
				FormGroupsConstants.TMPLAT_THERAP_NEEDS);
		// Populating BlobData THERAP_NARR_TSM TName-CP_TSM_NARR
		List<BlobDataDto> blobdataThrpNarrTsmList = new ArrayList<BlobDataDto>();
		BlobDataDto blobdataPlanNarrTsm = createBlobData(BookmarkConstants.THERAP_NARR_TSM, "CP_TSM_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobdataThrpNarrTsmList.add(blobdataPlanNarrTsm);
		// Populating BlobData THERAP_NARR_TSW TName-CP_TSW_NARR
		BlobDataDto blobdataPlanNarrTsw = createBlobData(BookmarkConstants.THERAP_NARR_TSW, "CP_TSW_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobdataThrpNarrTsmList.add(blobdataPlanNarrTsw);
		formDataTempTSWM.setBlobDataDtoList(blobdataThrpNarrTsmList);
		formDataTempTSWMList.add(formDataTempTSWM);
		allFormDataGroupListFour.addAll(formDataTempTSWMList);
		}
		// csc19o61
		 if ("N".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersGuardCnsrv())) {
			List<FormDataGroupDto> formDataTempOldFam1List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTempOldFam1 = createFormDataGroup(FormGroupsConstants.TMPLAT_OLD_FAM1,
					FormGroupsConstants.TMPLAT_THERAP_NEEDS);
			// Populating BlobData PLN_NARR_FMP TName-CP_FMP_NARR
			List<BlobDataDto> blobdataPlanNarrFmpList = new ArrayList<BlobDataDto>();
			BlobDataDto blobdataPlanNarrFmp = createBlobData(BookmarkConstants.PLN_NARR_FMP, "CP_FMP_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobdataPlanNarrFmpList.add(blobdataPlanNarrFmp);
			formDataTempOldFam1.setBlobDataDtoList(blobdataPlanNarrFmpList);
			formDataTempOldFam1List.add(formDataTempOldFam1);
			allFormDataGroupListFour.addAll(formDataTempOldFam1List);
		}
		
		formDataGrpTmpltTherapyNeeds.setFormDataGroupList(allFormDataGroupListFour);
		
		return formDataGrpTmpltTherapyNeeds;	
	}
	
	/**
	 *Method Name:	populateIPNPlanTypeAdditionalBookmarks
	 *Method Description:additional IPN bookmarks population
	 *@param childPlanDetailsOutputDto
	 *@return
	 */
	public FormDataGroupDto populateIPNPlanTypeAdditionalBookmarks(ChildPlanDetailsOutputDto childPlanDetailsOutputDto){
		//csc19o08
		FormDataGroupDto formDataTempObjNeed = createFormDataGroup(FormGroupsConstants.TMPLAT_OBJ_NEEDS,
				FormConstants.EMPTY_STRING);
		formDataTempObjNeed.setBlobDataDtoList(populateCSCEight(childPlanDetailsOutputDto));
		// csc19o17		
		formDataTempObjNeed.setFormDataGroupList(populateCSCSeventeen(childPlanDetailsOutputDto));
		return formDataTempObjNeed;
	}
	/**
	 *Method Name:	populateFRVPlanTypeBookmarks
	 *Method Description:populate form data for FRV type plan
	 *@param childPlanDetailsOutputDto
	 *@return
	 */
	public FormDataGroupDto populateCommonFacilityPlanTypeBookmarks(ChildPlanDetailsOutputDto childPlanDetailsOutputDto){


		FormDataGroupDto formDataTmpltSvcPlanReview2 = createFormDataGroup(
				FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_2, FormConstants.EMPTY_STRING);

		List<BookmarkDto> bookmarkTmpltSvcPlanReviewList = new ArrayList<BookmarkDto>();

		// Populate Bookmark SPR_DATE_OF_NEXT_REVIEW
		BookmarkDto bookmarkSprDtOfNxtReview = createBookmark(BookmarkConstants.SPR_DATE_OF_NEXT_REVIEW,
				DateUtils.stringDt(childPlanDetailsOutputDto.getPersonDto().getDtCspNextReview()));
		bookmarkTmpltSvcPlanReviewList.add(bookmarkSprDtOfNxtReview);

		// Populate Bookmark SPR_PROJECTED_PERMANENCY_DATE
		BookmarkDto bookmarkSprDtProjctedPermanency = createBookmark(
				BookmarkConstants.SPR_PROJECTED_PERMANENCY_DATE,
				DateUtils.stringDt(childPlanDetailsOutputDto.getPersonDto().getDtcspPermGoalTarget()));
		bookmarkTmpltSvcPlanReviewList.add(bookmarkSprDtProjctedPermanency);

		// Populate Bookmark SPR_PERMANENCY_GOAL CTname CCPPRMGL
		BookmarkDto bookmarkSprPermGoal = createBookmarkWithCodesTable(BookmarkConstants.SPR_PERMANENCY_GOAL,
				childPlanDetailsOutputDto.getPersonDto().getCspPlanPermGoal(),CodesConstant.CCPPRMGL);
		bookmarkTmpltSvcPlanReviewList.add(bookmarkSprPermGoal);

		// Populate Bookmark SPR_DISCREPANCY_LENGTH_OF_STAY
		BookmarkDto bookmarkSprLenOfStay = createBookmark(BookmarkConstants.SPR_DISCREPANCY_LENGTH_OF_STAY,
				childPlanDetailsOutputDto.getPersonDto().getTxtCspLosDiscrepancy());
		bookmarkTmpltSvcPlanReviewList.add(bookmarkSprLenOfStay);

		// Populate Bookmark SPR_EST_LENGTH_OF_STAY
		BookmarkDto bookmarkSprExtLenStay = createBookmark(BookmarkConstants.SPR_EST_LENGTH_OF_STAY,
				childPlanDetailsOutputDto.getPersonDto().getTxtCspLengthOfStay());
		bookmarkTmpltSvcPlanReviewList.add(bookmarkSprExtLenStay);

		// Populate BlobData IPS_NARR_BLOB_CPL TName CP_CPL_NARR
		List<BlobDataDto> blobDataIPSNarrBlobCplList = new ArrayList<BlobDataDto>();
		BlobDataDto blobDataIPSNarrBlobCpl = createBlobData(BookmarkConstants.IPS_NARR_BLOB_CPL, "CP_CPL_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataIPSNarrBlobCplList.add(blobDataIPSNarrBlobCpl);

		formDataTmpltSvcPlanReview2.setBookmarkDtoList(bookmarkTmpltSvcPlanReviewList);
		formDataTmpltSvcPlanReview2.setBlobDataDtoList(blobDataIPSNarrBlobCplList);

		List<FormDataGroupDto> allFormDataGroupListFive = new ArrayList<FormDataGroupDto>();
		// csc19o74
		if ("Y".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersGuardCnsrv())) {
			List<FormDataGroupDto> formDataGroupTempNwConGoal3List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGroupTempNwConGoal3 = createFormDataGroup(
					FormGroupsConstants.TMPLAT_NEW_CON_GOAL3, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_2);

			// csc19o74-->csc19o75
			if ("Y".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getIndNoConGoal())) {
				List<FormDataGroupDto> formDataGroupTempNoConGoal3List = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDataGroupTempNoConGoal3 = createFormDataGroup(
						FormGroupsConstants.TMPLAT_NO_CON_GOAL3, FormGroupsConstants.TMPLAT_NEW_CON_GOAL3);

				// Populate the Bookmark CP_TXT_NO_CON_GOAL3 CSES27D
				List<BookmarkDto> bookmarkCpTxtNoConGoal3List = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkCpTxtNoConGoal3 = createBookmark(BookmarkConstants.CP_TXT_NO_CON_GOAL3,
						childPlanDetailsOutputDto.getPersonDto().getTxtNoConsGoal());
				bookmarkCpTxtNoConGoal3List.add(bookmarkCpTxtNoConGoal3);

				formDataGroupTempNoConGoal3.setBookmarkDtoList(bookmarkCpTxtNoConGoal3List);
				formDataGroupTempNoConGoal3List.add(formDataGroupTempNoConGoal3);
				formDataGroupTempNwConGoal3.setFormDataGroupList(formDataGroupTempNoConGoal3List);
			}
			// csc19o74-->csc19o76
			else if ("N".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getIndNoConGoal())) {
				List<FormDataGroupDto> formDataGroupTempConGoal3List = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDataGroupTempConGoal3 = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CON_GOAL3, FormGroupsConstants.TMPLAT_NEW_CON_GOAL3);
				// csc19o74-->csc19o76-->csc19o77
				List<FormDataGroupDto> formDataGroupTemConGoal3List = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDataGroupTemConGoal3 = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CON_GOAL_LIST3, FormGroupsConstants.TMPLAT_CON_GOAL3);
				List<ConcurrentGoalDto> fetchFullConcurrentDtoList = childPlanDetailsOutputDto
						.getConcurrentDtoList();
				for (ConcurrentGoalDto fetchFullDto : fetchFullConcurrentDtoList) {
					List<BookmarkDto> bookmarkCdConGoal3List = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkCdConGoal3 = createBookmarkWithCodesTable(BookmarkConstants.CP_CD_CON_GOAL3,
							fetchFullDto.getCdConcurrentGoal(),CodesConstant.CCPPRMGL);
					bookmarkCdConGoal3List.add(bookmarkCdConGoal3);
					formDataGroupTemConGoal3.setBookmarkDtoList(bookmarkCdConGoal3List);
				}
				formDataGroupTemConGoal3List.add(formDataGroupTemConGoal3);
				formDataGroupTempConGoal3.setFormDataGroupList(formDataGroupTemConGoal3List);
				formDataGroupTempConGoal3List.add(formDataGroupTempConGoal3);
				formDataGroupTempNwConGoal3.setFormDataGroupList(formDataGroupTempConGoal3List);
			}
			formDataGroupTempNwConGoal3List.add(formDataGroupTempNwConGoal3);
			allFormDataGroupListFive.addAll(formDataGroupTempNwConGoal3List);
		}
		// csc19o73
		else if ( "N".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersGuardCnsrv())) {
			List<FormDataGroupDto> formDataTmplatOldConGoal3List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatOldConGoal3 = createFormDataGroup(
					FormGroupsConstants.TMPLAT_OLD_CON_GOAL3, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_2);

			List<BlobDataDto> blobDataIPSNarrBloCplList = new ArrayList<BlobDataDto>();
			BlobDataDto blobDataIPSNarrBloCpl = createBlobData(BookmarkConstants.IPS_NARR_BLOB_CPL, "CP_CPL_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataIPSNarrBloCplList.add(blobDataIPSNarrBloCpl);

			formDataTmplatOldConGoal3.setBlobDataDtoList(blobDataIPSNarrBloCplList);
			formDataTmplatOldConGoal3List.add(formDataTmplatOldConGoal3);
			allFormDataGroupListFive.addAll(formDataTmplatOldConGoal3List);
		}

		// csc19o60
		if ( "Y".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersonLivArr())) {
			List<FormDataGroupDto> formDataTmplatTxt3List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataTmplatTxt3 = createFormDataGroup(FormGroupsConstants.TMPLAT_TXT_3,
					FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_2);
			formDataTmplatTxt3List.add(formDataTmplatTxt3);
			allFormDataGroupListFive.addAll(formDataTmplatTxt3List);
		}

		// csc19o05
		List<FormDataGroupDto> formDataTmpltSprDtOfLastPlanList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto formDataTmpltSprDtOfLastPlan = createFormDataGroup(
				FormGroupsConstants.TMPLAT_SPR_DATE_OF_LAST_PLAN, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_2);
		// Populate BookmarkDto SPR_DATE_OF_LAST_PLAN
		List<BookmarkDto> bookmarkSprDtOfLastPlanList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkSprDtOfLastPlan = createBookmark(BookmarkConstants.SPR_DATE_OF_LAST_PLAN,
				DateUtils.stringDt(childPlanDetailsOutputDto.getChildPlanDetailsDto().getDateOfLastPlan()));
		bookmarkSprDtOfLastPlanList.add(bookmarkSprDtOfLastPlan);

		formDataTmpltSprDtOfLastPlan.setBookmarkDtoList(bookmarkSprDtOfLastPlanList);
		formDataTmpltSprDtOfLastPlanList.add(formDataTmpltSprDtOfLastPlan);
		allFormDataGroupListFive.addAll(formDataTmpltSprDtOfLastPlanList);

		// csc19o30
		List<FormDataGroupDto> formDataTmpltSprDtPlanCompList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto formDataTmpltSprDtOfLastPlanComp = createFormDataGroup(
				FormGroupsConstants.TMPLAT_SPR_DATE_PLAN_COMPLETED, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_2);

		List<BookmarkDto> bookmarkSprDtOfLastPlanCompList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkSprDtOfLastCompPlan = createBookmark(BookmarkConstants.SPR_DATE_PLAN_COMPLETE,
				DateUtils.stringDt(childPlanDetailsOutputDto.getChildPlanDetailsDto().getDtCspPlanCompleted()));
		bookmarkSprDtOfLastPlanCompList.add(bookmarkSprDtOfLastCompPlan);
		formDataTmpltSprDtOfLastPlanComp.setBookmarkDtoList(bookmarkSprDtOfLastPlanCompList);
		formDataTmpltSprDtPlanCompList.add(formDataTmpltSprDtOfLastPlanComp);
		allFormDataGroupListFive.addAll(formDataTmpltSprDtPlanCompList);

		// csc19o07
		List<FormDataGroupDto> formDataTmpltSprCurrPlcmntList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto formDataTmpltSprCurrPlcmnt = createFormDataGroup(
				FormGroupsConstants.TMPLAT_SPR_CURRENT_PLCMT, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_2);

		List<BookmarkDto> bookMarkTmpltSprCurrPlcmtList = new ArrayList<BookmarkDto>();

		// Populate BookmarkDto SPR_PLCMT_LIVING_ARRANGEMENT
		BookmarkDto bookmarkSprPlcmntLivingArr = createBookmarkWithCodesTable(BookmarkConstants.SPR_PLCMT_LIVING_ARRANGEMENT,
				childPlanDetailsOutputDto.getCapsPlacemntDto().getCdPlcmtLivArr(),CodesConstant.CPLLAFRM);
		bookMarkTmpltSprCurrPlcmtList.add(bookmarkSprPlcmntLivingArr);

		// Populate BookmarkDto SPR_PLCMT_FACILITY_TYPE
		BookmarkDto bookmarkSprPlcmntFacilType = createBookmarkWithCodesTable(BookmarkConstants.SPR_PLCMT_FACILITY_TYPE,
				childPlanDetailsOutputDto.getCapsPlacemntDto().getCdRsrcFacilType(),CodesConstant.CFACTYP2);
		bookMarkTmpltSprCurrPlcmtList.add(bookmarkSprPlcmntFacilType);

		// Populate BookmarkDto SPR_PLCMT_RESIDENCE_NAME 
		BookmarkDto bookmarkSprPlcmntResdNm = createBookmark(BookmarkConstants.SPR_PLCMT_RESIDENCE_NAME,
				childPlanDetailsOutputDto.getCapsPlacemntDto().getNmPlcmtFacil());
		bookMarkTmpltSprCurrPlcmtList.add(bookmarkSprPlcmntResdNm);

		// Populate BookmarkDto SPR_PLCMT_PERSON_NAME
		BookmarkDto bookmarkSprPlcmntPersonNm = createBookmark(BookmarkConstants.SPR_PLCMT_PERSON_NAME,
				childPlanDetailsOutputDto.getCapsPlacemntDto().getNmPlcmtPersonFull());
		bookMarkTmpltSprCurrPlcmtList.add(bookmarkSprPlcmntPersonNm);

		

		formDataTmpltSprCurrPlcmnt.setBookmarkDtoList(bookMarkTmpltSprCurrPlcmtList);

		// Populate BlobData IPS_NARR_BLOB_CPL TName-CP_CPL_NARR
		/*List<BlobDataDto> blobDataIPSNarrBloCplList = new ArrayList<BlobDataDto>();
		BlobDataDto blobDataIPSNarrBloCpl = createBlobData(BookmarkConstants.IPS_NARR_BLOB_CPL, "CP_CPL_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataIPSNarrBloCplList.add(blobDataIPSNarrBloCpl);

		formDataTmpltSprCurrPlcmnt.setBlobDataDtoList(blobDataIPSNarrBloCplList);*/
		formDataTmpltSprCurrPlcmntList.add(formDataTmpltSprCurrPlcmnt);
		allFormDataGroupListFive.addAll(formDataTmpltSprCurrPlcmntList);

		// csc19o03
		List<FormDataGroupDto> formDataGroupTempltWorkrCompletdList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto formDataGroupTempltWorkrCompletd = createFormDataGroup(
				FormGroupsConstants.TMPLAT_CP_WORKER_WHO_COMPLETED, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_2);
		List<BookmarkDto> bookmarkDtoTempltWorkrCompletdList = new ArrayList<BookmarkDto>();

		// populate the bookmark CP_WORKER_SUFFIX_COMP CodeTableNm CSUFFIX2
		BookmarkDto bookmarkCPWrkrSuffix = createBookmarkWithCodesTable(BookmarkConstants.CP_WORKER_SUFFIX_COMP,
				childPlanDetailsOutputDto.getPersonList().getCdPersonSuffix(),CodesConstant.CSUFFIX2);
		bookmarkDtoTempltWorkrCompletdList.add(bookmarkCPWrkrSuffix);

		// populate the bookmark CP_WORKER_FIRST_COMP
		BookmarkDto bookmarkCPWrkrFirst = createBookmark(BookmarkConstants.CP_WORKER_FIRST_COMP,
				childPlanDetailsOutputDto.getPersonList().getNmPersonFirst());
		bookmarkDtoTempltWorkrCompletdList.add(bookmarkCPWrkrFirst);

		// populate the bookmark CP_WORKER_LAST_COMP
		BookmarkDto bookmarkCPWrkrLast = createBookmark(BookmarkConstants.CP_WORKER_LAST_COMP,
				childPlanDetailsOutputDto.getPersonList().getNmPersonLast());
		bookmarkDtoTempltWorkrCompletdList.add(bookmarkCPWrkrLast);

		// populate the bookmark CP_WORKER_MIDDLE_COMP
		BookmarkDto bookmarkCPWrkrMiddle = createBookmark(BookmarkConstants.CP_WORKER_MIDDLE_COMP,
				childPlanDetailsOutputDto.getPersonList().getNmPersonMiddle());
		bookmarkDtoTempltWorkrCompletdList.add(bookmarkCPWrkrMiddle);
		formDataGroupTempltWorkrCompletd.setBookmarkDtoList(bookmarkDtoTempltWorkrCompletdList);

		// csc19o03 --> cfzco00
		if (StringUtils.isNotBlank(childPlanDetailsOutputDto.getPersonList().getCdPersonSuffix())) {
			List<FormDataGroupDto> formDatagroupTempltBList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGroupTempltCommaB = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_K,
					FormGroupsConstants.TMPLAT_CP_WORKER_WHO_COMPLETED);
			formDatagroupTempltBList.add(formDataGroupTempltCommaB);
			formDataGroupTempltWorkrCompletd.setFormDataGroupList(formDatagroupTempltBList);
		}

		formDataGroupTempltWorkrCompletdList.add(formDataGroupTempltWorkrCompletd);
		allFormDataGroupListFive.addAll(formDataGroupTempltWorkrCompletdList);
		formDataTmpltSvcPlanReview2.setFormDataGroupList(allFormDataGroupListFive);
		return formDataTmpltSvcPlanReview2;
			
	}
	
	public FormDataGroupDto populatePALDateBookmarks(ChildPlanDetailsOutputDto childPlanDetailsOutputDto){
		FormDataGroupDto formDataGroupTmpPalDtInitlTrans = createFormDataGroup(
				FormGroupsConstants.TMPLAT_PAL_DATE_INITIAL_TRANS, FormGroupsConstants.TMPLAT_INIT_PLCMT_SUMM);
		List<BookmarkDto> bookmarkPalDtIntlTransList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkPalDtIntlTrans = createBookmark(BookmarkConstants.PAL_DATE_INITIAL_TRANS,
				childPlanDetailsOutputDto.getPersonDto().getDtInitialTransitionPlan());
		bookmarkPalDtIntlTransList.add(bookmarkPalDtIntlTrans);
		formDataGroupTmpPalDtInitlTrans.setBookmarkDtoList(bookmarkPalDtIntlTransList);
		return formDataGroupTmpPalDtInitlTrans;
	}
	
	public List<FormDataGroupDto> populateCSCSeventeen(ChildPlanDetailsOutputDto childPlanDetailsOutputDto){
		
		List<FormDataGroupDto> formDataTempInitialHeadingList = new ArrayList<FormDataGroupDto>();
		
			FormDataGroupDto formDataTempInitialHeading = createFormDataGroup(
					FormGroupsConstants.TMPLAT_INITIAL_HEADING, FormGroupsConstants.TMPLAT_OBJ_NEEDS);
			formDataTempInitialHeadingList.add(formDataTempInitialHeading);
			return formDataTempInitialHeadingList;
	}
	public List<BlobDataDto> populateCSCEight(ChildPlanDetailsOutputDto childPlanDetailsOutputDto){
		List<BlobDataDto> blobDataTempObjNeedsList = new ArrayList<BlobDataDto>();

		// Populating Blobdata OBJ_NARR_SEN TName-CP_SEN_NARR
		BlobDataDto blobdataobjNarrSen = createBlobData(BookmarkConstants.OBJ_NARR_SEN, "CP_SEN_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataTempObjNeedsList.add(blobdataobjNarrSen);

		// Populating Blobdata OBJ_NARR_IBP TName-CP_IBP_NARR
		BlobDataDto blobdataObjNarrIbp = createBlobData(BookmarkConstants.OBJ_NARR_IBP, "CP_IBP_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataTempObjNeedsList.add(blobdataObjNarrIbp);

		// Populating Blobdata OBJ_NARR_DVN TName-CP_DVN_NARR
		BlobDataDto blobdataObjNarrDvn = createBlobData(BookmarkConstants.OBJ_NARR_DVN, "CP_DVN_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataTempObjNeedsList.add(blobdataObjNarrDvn);

		// Populating Blobdata OBJ_NARR_REC TName-CP_REC_NARR
		BlobDataDto blobdataobjNarrRec = createBlobData(BookmarkConstants.OBJ_NARR_REC, "CP_REC_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataTempObjNeedsList.add(blobdataobjNarrRec);

		// Populating Blobdata OBJ_NARR_EDN TName-CP_EDN_NARR
		BlobDataDto blobdataObjNarrEdn = createBlobData(BookmarkConstants.OBJ_NARR_EDN, "CP_EDN_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataTempObjNeedsList.add(blobdataObjNarrEdn);

		// Populating Blobdata OBJ_NARR_PHY TName-CP_PHY_NARR
		BlobDataDto blobdataPlanObjNarrPhy = createBlobData(BookmarkConstants.OBJ_NARR_PHY, "CP_PHY_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataTempObjNeedsList.add(blobdataPlanObjNarrPhy);

		// Populating Blobdata OBJ_NARR_MDN TName-CP_MDN_NARR
		BlobDataDto blobdataObjNarrMdn = createBlobData(BookmarkConstants.OBJ_NARR_MDN, "CP_MDN_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataTempObjNeedsList.add(blobdataObjNarrMdn);
		return blobDataTempObjNeedsList;
	
	}
	
	public List<FormDataGroupDto> populateCSCSixteen(ChildPlanDetailsOutputDto childPlanDetailsOutputDto){
		
		List<FormDataGroupDto> formDataTempInitialHeadingList = new ArrayList<FormDataGroupDto>();
		
			FormDataGroupDto formDataTempRVWHeading = createFormDataGroup(
					FormGroupsConstants.TMPLAT_REVIEW_HEADING, FormGroupsConstants.TMPLAT_OBJ_NEEDS);
			formDataTempInitialHeadingList.add(formDataTempRVWHeading);
			return formDataTempInitialHeadingList;
	}
	public FormDataGroupDto populateBasicObjForReview(ChildPlanDetailsOutputDto childPlanDetailsOutputDto){
			
		//csc19o08
				FormDataGroupDto formDataTempObjNeed = createFormDataGroup(FormGroupsConstants.TMPLAT_OBJ_NEEDS,
						FormConstants.EMPTY_STRING);
				formDataTempObjNeed.setBlobDataDtoList(populateCSCEight(childPlanDetailsOutputDto));
				// csc19o17		
				formDataTempObjNeed.setFormDataGroupList(populateCSCSixteen(childPlanDetailsOutputDto));
				return formDataTempObjNeed;
	}
	
	public FormDataGroupDto populateCSCEighteen(ChildPlanDetailsOutputDto childPlanDetailsOutputDto){

		FormDataGroupDto formDataGroupTmpObjctPlanDto = createFormDataGroup(FormGroupsConstants.TMPLAT_OBJ_PLANS,
				FormConstants.EMPTY_STRING);
		List<BlobDataDto> blobDataDtoTmplatObjPlansList = new ArrayList<BlobDataDto>();

		// Populate BlobData PLN_NARR_SEP TName-CP_SEP_NARR
		BlobDataDto blobDataDtoPlnNarrSep = createBlobData(BookmarkConstants.PLN_NARR_SEP, "CP_SEP_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoTmplatObjPlansList.add(blobDataDtoPlnNarrSep);
		// Populate BlobData PLN_NARR_PHP TName-CP_PHP_NARR
		BlobDataDto blobDataDtoPlnNarrPhp = createBlobData(BookmarkConstants.PLN_NARR_PHP, "CP_PHP_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoTmplatObjPlansList.add(blobDataDtoPlnNarrPhp);
		// Populate BlobData PLN_NARR_DVP TName-CP_DVP_NARR
		BlobDataDto blobDataDtoPlnNarrDvp = createBlobData(BookmarkConstants.PLN_NARR_DVP, "CP_DVP_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoTmplatObjPlansList.add(blobDataDtoPlnNarrDvp);
		// Populate BlobData PLN_NARR_MDP TName-CP_MDP_NARR
		BlobDataDto blobDataDtoPlnNarrMdp = createBlobData(BookmarkConstants.PLN_NARR_MDP, "CP_MDP_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoTmplatObjPlansList.add(blobDataDtoPlnNarrMdp);
		// Populate BlobData PLN_NARR_EDP TName-CP_EDP_NARR
		BlobDataDto blobDataDtoPlnNarrEdp = createBlobData(BookmarkConstants.PLN_NARR_EDP, "CP_EDP_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoTmplatObjPlansList.add(blobDataDtoPlnNarrEdp);

		formDataGroupTmpObjctPlanDto.setBlobDataDtoList(blobDataDtoTmplatObjPlansList);		
		return formDataGroupTmpObjctPlanDto;
	
	}
	
	public FormDataGroupDto populateCSCNineteen(ChildPlanDetailsOutputDto childPlanDetailsOutputDto){

		FormDataGroupDto formDataGroupDtoTmplatTherap = createFormDataGroup(FormGroupsConstants.TMPLAT_THERAP_PLANS,
				FormConstants.EMPTY_STRING);

		List<BlobDataDto> blobDataDtoTmplatObjPlansRVWList = new ArrayList<BlobDataDto>();

		// Populate BlobData PLN_NARR_FMP TName-CP_FMP_NARR
		BlobDataDto blobDataDtoPlnNarrFmp = createBlobData(BookmarkConstants.PLN_NARR_FMP, "CP_FMP_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoTmplatObjPlansRVWList.add(blobDataDtoPlnNarrFmp);
		// Populate BlobData PLN_NARR_TOP TName-CP_TOP_NARR
		BlobDataDto blobDataDtoPlnNarrTop = createBlobData(BookmarkConstants.PLN_NARR_TOP, "CP_TOP_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoTmplatObjPlansRVWList.add(blobDataDtoPlnNarrTop);
		// Populate BlobData PLN_NARR_TMP TName-CP_TMP_NARR
		BlobDataDto blobDataDtoPlnNarrTmp = createBlobData(BookmarkConstants.PLN_NARR_TMP, "CP_TMP_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoTmplatObjPlansRVWList.add(blobDataDtoPlnNarrTmp);
		formDataGroupDtoTmplatTherap.setBlobDataDtoList(blobDataDtoTmplatObjPlansRVWList);

		List<FormDataGroupDto> allFormDataGroupListTwentySeven = new ArrayList<FormDataGroupDto>();
		// csc19o45
		if (childPlanDetailsOutputDto.getPersonDto().getCdPersonLivArr() != null
				&& "N".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersonLivArr())) {
			List<FormDataGroupDto> formDataGroupTmplatOldPlnsList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGroupTmplatOldPlns = createFormDataGroup(FormGroupsConstants.TMPLAT_OLD_PLNS,
					FormGroupsConstants.TMPLAT_THERAP_PLANS);
			List<BlobDataDto> blobDataDtoTmplatOldPlansList = new ArrayList<BlobDataDto>();
			// Populate BlobData PLN_NARR_REP TName-CP_REP_NARR
			BlobDataDto blobDataDtoPlnNarrRep = createBlobData(BookmarkConstants.PLN_NARR_REP, "CP_REP_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataDtoTmplatOldPlansList.add(blobDataDtoPlnNarrRep);
			// Populate BlobData PLN_NARR_PSP TName-CP_PSP_NARR
			BlobDataDto blobDataDtoPlnNarrPsp = createBlobData(BookmarkConstants.PLN_NARR_PSP, "CP_PSP_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataDtoTmplatOldPlansList.add(blobDataDtoPlnNarrPsp);
			formDataGroupTmplatOldPlns.setBlobDataDtoList(blobDataDtoTmplatOldPlansList);
			formDataGroupTmplatOldPlnsList.add(formDataGroupTmplatOldPlns);
			allFormDataGroupListTwentySeven.addAll(formDataGroupTmplatOldPlnsList);
		}
		// csc19o62
		if (childPlanDetailsOutputDto.getPersonDto().getCdPersGuardCnsrv() != null
				&& "N".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersGuardCnsrv())) {
			List<FormDataGroupDto> formDataGroupTmplatOldFamPlan1List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGroupTmplatTmplatOldFamPlan1 = createFormDataGroup(
					FormGroupsConstants.TMPLAT_OLD_FAM_PLAN1, FormGroupsConstants.TMPLAT_THERAP_PLANS);

			// Populate BlobData THERAP_NARR_FAM TName-CP_FAM_NARR
			List<BlobDataDto> blobDataDtoTherapNarrFamList = new ArrayList<BlobDataDto>();
			BlobDataDto blobDataDtoTherapNarrFam = createBlobData(BookmarkConstants.THERAP_NARR_FAM, "CP_FAM_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataDtoTherapNarrFamList.add(blobDataDtoTherapNarrFam);
			formDataGroupTmplatTmplatOldFamPlan1.setBlobDataDtoList(blobDataDtoTherapNarrFamList);
			formDataGroupTmplatOldFamPlan1List.add(formDataGroupTmplatTmplatOldFamPlan1);
			allFormDataGroupListTwentySeven.addAll(formDataGroupTmplatOldFamPlan1List);
		}

		// csc19o46
		if (childPlanDetailsOutputDto.getPersonDto().getCdPersonLivArr() != null
				&& "Y".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersonLivArr())) {
			List<FormDataGroupDto> formDataGroupTmplatTopmList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGroupTmplatTopm = createFormDataGroup(FormGroupsConstants.TMPLAT_TOPM,
					FormGroupsConstants.TMPLAT_THERAP_PLANS);

			List<BlobDataDto> blobDataDtoTmplatTopmList = new ArrayList<BlobDataDto>();
			// Populate BlobData PLN_NARR_TMP TName-CP_TMP_NARR
			BlobDataDto blobDataDtoTmplatTopm = createBlobData(BookmarkConstants.PLN_NARR_TMP, "CP_TMP_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataDtoTmplatTopmList.add(blobDataDtoTmplatTopm);
			// Populate BlobData PLN_NARR_TOP TName-CP_TOP_NARR
			BlobDataDto blobDataDtoPlanNarrTop = createBlobData(BookmarkConstants.PLN_NARR_TOP, "CP_TOP_NARR",
					childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataDtoTmplatTopmList.add(blobDataDtoPlanNarrTop);
			formDataGroupTmplatTopm.setBlobDataDtoList(blobDataDtoTmplatTopmList);
			formDataGroupTmplatTopmList.add(formDataGroupTmplatTopm);
			allFormDataGroupListTwentySeven.addAll(formDataGroupTmplatTopmList);
		}
		formDataGroupDtoTmplatTherap.setFormDataGroupList(allFormDataGroupListTwentySeven);
		return formDataGroupDtoTmplatTherap;	
	}
	
	public FormDataGroupDto populatePALPlans(ChildPlanDetailsOutputDto childPlanDetailsOutputDto){

		FormDataGroupDto formDataGroupTmpltPalPlansIPL = createFormDataGroup(FormGroupsConstants.TMPLAT_PAL_PLANS,
				FormConstants.EMPTY_STRING);

		List<BlobDataDto> blobDataDtoPlnNarrPapList = new ArrayList<BlobDataDto>();
		// Populate BlobData PLN_NARR_PAP TName-CP_PAP_NARR
		BlobDataDto blobDataDtoPlnNarrPap = createBlobData(BookmarkConstants.PLN_NARR_PAP, "CP_PAP_NARR",
				childPlanDetailsOutputDto.getPersonDto().getIdChildPlanEvent().intValue());
		blobDataDtoPlnNarrPapList.add(blobDataDtoPlnNarrPap);
		formDataGroupTmpltPalPlansIPL.setBlobDataDtoList(blobDataDtoPlnNarrPapList);

		List<FormDataGroupDto> allFormDataGroupListTwentyEight = new ArrayList<FormDataGroupDto>();

		// csc19o48
		if ( "Y".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersonLivArr())) {
			List<FormDataGroupDto> formDataGroupTmplatPalPlnNewList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGroupTmplatPalPlnNew = createFormDataGroup(
					FormGroupsConstants.TMPLAT_PAL_PLN_NEW, FormGroupsConstants.TMPLAT_PAL_PLANS);
			formDataGroupTmplatPalPlnNewList.add(formDataGroupTmplatPalPlnNew);
			allFormDataGroupListTwentyEight.addAll(formDataGroupTmplatPalPlnNewList);
		}

		// csc19o47
		if ( "N".equalsIgnoreCase(childPlanDetailsOutputDto.getPersonDto().getCdPersonLivArr())) {
			List<FormDataGroupDto> formDataGroupTmplatPalPlnOldList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGroupTmplatPalPlnOld = createFormDataGroup(
					FormGroupsConstants.TMPLAT_PAL_PLN_OLD, FormGroupsConstants.TMPLAT_PAL_PLANS);
			formDataGroupTmplatPalPlnOldList.add(formDataGroupTmplatPalPlnOld);
			allFormDataGroupListTwentyEight.addAll(formDataGroupTmplatPalPlnOldList);
		}
		formDataGroupTmpltPalPlansIPL.setFormDataGroupList(allFormDataGroupListTwentyEight);
		return formDataGroupTmpltPalPlansIPL;
	}
}
