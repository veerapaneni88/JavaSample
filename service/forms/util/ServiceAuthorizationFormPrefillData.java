package us.tx.state.dfps.service.forms.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.approval.dto.ApprovalFormDataDto;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.admin.dto.AddrPersonLinkPhoneOutDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkOutDto;
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
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.ClientInfoServiceAuthDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.DayCareDetailsDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.DayCareFacilServiceAuthDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.KinshipGroupInfoDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.MedicaidServiceAuthDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.OldestVictimNameDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.SelectForwardPersonDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.ServiceAuthFormDataDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.SVCAuthDetailDto;
import us.tx.state.dfps.service.workload.dto.SVCAuthDetailRecDto;
import us.tx.state.dfps.service.workload.dto.ServiceAuthorizationDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * Authorization form used by CPS to refer clients for paid services under PRS
 * contracts. Mar 1, 2018- 1:52:54 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class ServiceAuthorizationFormPrefillData extends DocumentServiceUtil {

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		ServiceAuthFormDataDto serviceAuthFormDataDto = (ServiceAuthFormDataDto) parentDtoobj;

		if (null == serviceAuthFormDataDto.getEventValueDto()) {
			serviceAuthFormDataDto.setEventValueDto(new EventDto());
		}
		if (null == serviceAuthFormDataDto.getGenericCaseInfoDto()) {
			serviceAuthFormDataDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}
		if (null == serviceAuthFormDataDto.getDayCareDetailsDtoList()) {
			serviceAuthFormDataDto.setDayCareDetailsDtoList(new ArrayList<DayCareDetailsDto>());
		}
		if (null == serviceAuthFormDataDto.getResourceDto()) {
			serviceAuthFormDataDto.setResourceDto(new ResourceDto());
		}
		if (null == serviceAuthFormDataDto.getOldestVictimNameDto()) {
			serviceAuthFormDataDto.setOldestVictimNameDto(new OldestVictimNameDto());
		}
		if (null == serviceAuthFormDataDto.getPersMergeFwdList()) {
			serviceAuthFormDataDto.setPersMergeFwdList(new ArrayList<SelectForwardPersonDto>());
		}
		if (null == serviceAuthFormDataDto.getClienDto()) {
			serviceAuthFormDataDto.setClienDto(new ClientInfoServiceAuthDto());
		}

		if (null == serviceAuthFormDataDto.getNameDetailDto()) {
			serviceAuthFormDataDto.setNameDetailDto(new NameDetailDto());
		}

		if (null == serviceAuthFormDataDto.getEmployeePersPhNameDto()) {
			serviceAuthFormDataDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}

		if (null == serviceAuthFormDataDto.getKinshipList()) {
			serviceAuthFormDataDto.setKinshipList(new ArrayList<KinshipGroupInfoDto>());
		}

		if (null == serviceAuthFormDataDto.getAddr()) {
			serviceAuthFormDataDto.setAddr(new ArrayList<AddrPersonLinkPhoneOutDto>());
		}

		if (null == serviceAuthFormDataDto.getAddr()) {
			serviceAuthFormDataDto.setAddr(new ArrayList<AddrPersonLinkPhoneOutDto>());
		}

		if (null == serviceAuthFormDataDto.getSvcAuthEventLinkOutDtoList()) {
			serviceAuthFormDataDto.setSvcAuthEventLinkOutDtoList(new ArrayList<SvcAuthEventLinkOutDto>());
		}

		if (null == serviceAuthFormDataDto.getSvcAuthDetailDtoList()) {
			serviceAuthFormDataDto.setSvcAuthDetailDtoList(new ArrayList<SVCAuthDetailDto>());
		}
		if (null == serviceAuthFormDataDto.getDayCareFaciListFinal()) {
			serviceAuthFormDataDto.setDayCareFaciListFinal(new ArrayList<DayCareFacilServiceAuthDto>());
		}

		if (null == serviceAuthFormDataDto.getStagePersonDto()) {
			serviceAuthFormDataDto.setStagePersonDto(new StagePersonDto());
		}

		if (null == serviceAuthFormDataDto.getServiceAuthorizationDto()) {
			serviceAuthFormDataDto.setServiceAuthorizationDto(new ServiceAuthorizationDto());
		}

		if (ObjectUtils.isEmpty(serviceAuthFormDataDto.getApprovalFormDataDto())) {
			serviceAuthFormDataDto.setApprovalFormDataDto(new ApprovalFormDataDto());
		}
		
		if (ObjectUtils.isEmpty(serviceAuthFormDataDto.getHistoyEmployeePersPhNameDto())) {
			serviceAuthFormDataDto.setHistoyEmployeePersPhNameDto(new EmployeePersPhNameDto ());
		}
		
		/**********************************************
		 * adding default bookmarks for the non-group fields
		 *******************************/
		/**
		 * Description: Populating the non form group data into prefill data
		 * GroupName: None BookMark: Condition: None
		 */
		List<BookmarkDto> bookmarkDtoDefaultDtoList = new ArrayList<BookmarkDto>();

		if (null != serviceAuthFormDataDto.getGenericCaseInfoDto().getDtSysDtGenericSysdate()) {
			BookmarkDto bkCasePrintDate = createBookmark(BookmarkConstants.CASE_PRINTDATE,
					DateUtils.stringDt(serviceAuthFormDataDto.getGenericCaseInfoDto().getDtSysDtGenericSysdate()));
			bookmarkDtoDefaultDtoList.add(bkCasePrintDate);
		}

		if (null != serviceAuthFormDataDto.getGenericCaseInfoDto().getNmCase()) {
			BookmarkDto bkCaseName = createBookmark(BookmarkConstants.CASE_NAME,
					serviceAuthFormDataDto.getGenericCaseInfoDto().getNmCase());
			bookmarkDtoDefaultDtoList.add(bkCaseName);
		} else {
			BookmarkDto bkCaseName = createBookmark(BookmarkConstants.CASE_NAME, "");
			bookmarkDtoDefaultDtoList.add(bkCaseName);
		}

		if (null != serviceAuthFormDataDto.getGenericCaseInfoDto().getIdCase()) {
			BookmarkDto bkCaseNum = createBookmark(BookmarkConstants.CASE_NUMBER,
					serviceAuthFormDataDto.getGenericCaseInfoDto().getIdCase());
			bookmarkDtoDefaultDtoList.add(bkCaseNum);
		} else {
			BookmarkDto bkCaseNum = createBookmark(BookmarkConstants.CASE_NUMBER, "");
			bookmarkDtoDefaultDtoList.add(bkCaseNum);
		}

		/**********************************************
		 * adding Groups ccn01o81
		 ****************************************************************************/
		/**
		 * Description: TMPLAT_CASEWORKER GroupName: ccn01o81 SubGroups:
		 * ccn01o82, ccn01o83 BookMark: TMPLAT_CASEWORKER Condition: None
		 */

		List<FormDataGroupDto> fdParentGroupList = new ArrayList<FormDataGroupDto>();

		FormDataGroupDto fdPrCaseWorker = createFormDataGroup(FormGroupsConstants.TMPLAT_CASEWORKER,
				FormConstants.EMPTY_STRING);

		List<FormDataGroupDto> allSubGroupCaseWorkerList = new ArrayList<FormDataGroupDto>();

		List<BookmarkDto> bkCaseWorkerList = new ArrayList<BookmarkDto>();

		BookmarkDto bkCaseWorkerPhone = createBookmark(BookmarkConstants.CASEWORKER_PHONE,
				serviceAuthFormDataDto.getEmployeePersPhNameDto().getNbrPhone());
		bkCaseWorkerList.add(bkCaseWorkerPhone);

		BookmarkDto bkCaseWorkerCity = createBookmark(BookmarkConstants.CASEWORKER_CITY,
				serviceAuthFormDataDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
		bkCaseWorkerList.add(bkCaseWorkerCity);

		BookmarkDto bkCaseWorkerAddrLnOne = createBookmark(BookmarkConstants.CASEWORKER_ADDR1,
				serviceAuthFormDataDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
		bkCaseWorkerList.add(bkCaseWorkerAddrLnOne);

		BookmarkDto bkCaseWorkerAddrLnTwo = createBookmark(BookmarkConstants.CASEWORKER_ADDR2,
				serviceAuthFormDataDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
		bkCaseWorkerList.add(bkCaseWorkerAddrLnTwo);

		BookmarkDto bkCaseWorkerAddrZip = createBookmark(BookmarkConstants.CASEWORKER_ZIP,
				serviceAuthFormDataDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
		bkCaseWorkerList.add(bkCaseWorkerAddrZip);

		BookmarkDto bkCaseWorkerBjn = createBookmark(BookmarkConstants.CASEWORKER_BJN,
				serviceAuthFormDataDto.getEmployeePersPhNameDto().getBjnJob());
		bkCaseWorkerList.add(bkCaseWorkerBjn);

		BookmarkDto bkCaseWorkerFName = createBookmark(BookmarkConstants.CASEWORKER_FNAME,
				serviceAuthFormDataDto.getEmployeePersPhNameDto().getNmNameFirst());
		bkCaseWorkerList.add(bkCaseWorkerFName);

		BookmarkDto bkCaseWorkerLName = createBookmark(BookmarkConstants.CASEWORKER_LNAME,
				serviceAuthFormDataDto.getEmployeePersPhNameDto().getNmNameLast());
		bkCaseWorkerList.add(bkCaseWorkerLName);

		BookmarkDto bkCaseWorkerMiddle = createBookmark(BookmarkConstants.CASEWORKER_MNAME,
				serviceAuthFormDataDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bkCaseWorkerList.add(bkCaseWorkerMiddle);

		fdPrCaseWorker.setBookmarkDtoList(bkCaseWorkerList);

		/**
		 * Description: TMPLAT_CASEWORKER_EXT GroupName: ccn01o83 SubGroups:
		 * None BookMark: TMPLAT_CASEWORKER_EXT Condition: lNbrPhoneExtension !=
		 * null
		 */
		if (null != serviceAuthFormDataDto.getEmployeePersPhNameDto().getNbrPhoneExtension()) {
			FormDataGroupDto fdSbCaseWorkerExtn = createFormDataGroup(FormGroupsConstants.TMPLAT_CASEWORKER_EXT,
					FormGroupsConstants.TMPLAT_CASEWORKER);

			List<BookmarkDto> bkCaseWorkerExtnList = new ArrayList<BookmarkDto>();

			BookmarkDto bkCaseWorkerExtn = createBookmark(BookmarkConstants.CASEWORKER_EXT_NUM,
					serviceAuthFormDataDto.getEmployeePersPhNameDto().getNbrPhoneExtension());

			bkCaseWorkerExtnList.add(bkCaseWorkerExtn);
			fdSbCaseWorkerExtn.setBookmarkDtoList(bkCaseWorkerExtnList);

			// adding the subgroup as a list
			allSubGroupCaseWorkerList.add(fdSbCaseWorkerExtn);

		}

		/**
		 * Description: TMPLAT_CASEWORKER_SUFFIX GroupName: ccn01o82 SubGroups:
		 * None BookMark: TMPLAT_CASEWORKER_SUFFIX Condition: CdNameSuffix !=
		 * null
		 */

		if (null != serviceAuthFormDataDto.getEmployeePersPhNameDto().getCdNameSuffix()) {
			FormDataGroupDto fdSbCaseWorkerSuffix = createFormDataGroup(FormGroupsConstants.TMPLAT_CASEWORKER_SUFFIX,
					FormGroupsConstants.TMPLAT_CASEWORKER);

			List<BookmarkDto> bkCaseWorkerSuffixList = new ArrayList<BookmarkDto>();

			BookmarkDto bkCaseWorkerSuffix = createBookmarkWithCodesTable(BookmarkConstants.CASEWORKER_SUFFIX_DECODE,
					serviceAuthFormDataDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);

			bkCaseWorkerSuffixList.add(bkCaseWorkerSuffix);
			fdSbCaseWorkerSuffix.setBookmarkDtoList(bkCaseWorkerSuffixList);

			// adding the subgroup as a list
			allSubGroupCaseWorkerList.add(fdSbCaseWorkerSuffix);
		}
		// adding the subgroups to the primary group.
		fdPrCaseWorker.setFormDataGroupList(allSubGroupCaseWorkerList);

		// adding the ccn01o81 Primary group to the list of parentForm Group
		fdParentGroupList.add(fdPrCaseWorker);

		/**********************************************
		 * adding Groups ccn01o03
		 ****************************************************************************/
		/**
		 * Description: TMPLAT_CONTRACT GroupName: ccn01o03 SubGroups: ccn01o02,
		 * ccn01o04, ccn01o05 BookMark: TMPLAT_CONTRACT Condition: None
		 */
		FormDataGroupDto fdPrTmplatContract = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTRACT,
				FormConstants.EMPTY_STRING);

		List<FormDataGroupDto> allSubGroupTmplatContractList = new ArrayList<FormDataGroupDto>();

		List<BookmarkDto> bkTmplatContractList = new ArrayList<BookmarkDto>();

		BookmarkDto bkTmplatContractId = createBookmark(BookmarkConstants.CONTRACT_ID,
				serviceAuthFormDataDto.getServiceAuthorizationDto().getIdContract());
		BookmarkDto bkTmplatContractNumber = createBookmark(BookmarkConstants.CONTRACT_NUMBER,
				serviceAuthFormDataDto.getTxScorContractNumber());
		bkTmplatContractList.add(bkTmplatContractId);
		bkTmplatContractList.add(bkTmplatContractNumber);
		fdPrTmplatContract.setBookmarkDtoList(bkTmplatContractList);

		/**
		 * Description: TMPLAT_CONTRACT_PROVIDER GroupName: ccn01o02
		 * ParentGroup:ccn01o03 SubGroups: BookMark: TMPLAT_CONTRACT_PROVIDER
		 * Condition: None
		 */

		FormDataGroupDto fdSbTmplatContractProvider = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTRACT_PROVIDER,
				FormGroupsConstants.TMPLAT_CONTRACT);

		List<BookmarkDto> bkSbTmplatContractProviderList = new ArrayList<BookmarkDto>();

		BookmarkDto bkTmplatContractProviderZip = createBookmark(BookmarkConstants.CONTRACT_PROVIDER_ZIP,
				serviceAuthFormDataDto.getResourceDto().getAddrRsrcZip());
		bkSbTmplatContractProviderList.add(bkTmplatContractProviderZip);

		BookmarkDto bkTmplatContractProviderCity = createBookmark(BookmarkConstants.CONTRACT_PROVIDER_CITY,
				serviceAuthFormDataDto.getResourceDto().getAddrRsrcCity());
		bkSbTmplatContractProviderList.add(bkTmplatContractProviderCity);

		BookmarkDto bkTmplatContractProviderAddrOne = createBookmark(BookmarkConstants.CONTRACT_PROVIDER_ADDR1,
				serviceAuthFormDataDto.getResourceDto().getAddrRsrcStLn1());
		bkSbTmplatContractProviderList.add(bkTmplatContractProviderAddrOne);

		BookmarkDto bkTmplatContractProviderAddrTwo = createBookmark(BookmarkConstants.CONTRACT_PROVIDER_ADDR2,
				serviceAuthFormDataDto.getResourceDto().getAddrRsrcStLn2());
		bkSbTmplatContractProviderList.add(bkTmplatContractProviderAddrTwo);

		BookmarkDto bkTmplatContractProviderState = createBookmark(BookmarkConstants.CONTRACT_PROVIDER_STATE,
				serviceAuthFormDataDto.getResourceDto().getCdRsrcState());
		bkSbTmplatContractProviderList.add(bkTmplatContractProviderState);

		BookmarkDto bkTmplatContractProviderName = createBookmark(BookmarkConstants.CONTRACT_PROVIDER_NAME,
				serviceAuthFormDataDto.getResourceDto().getNmResource());
		bkSbTmplatContractProviderList.add(bkTmplatContractProviderName);

		fdSbTmplatContractProvider.setBookmarkDtoList(bkSbTmplatContractProviderList);

		// adding subGroup ccn01o02 to ParentGroup list ccn01o03
		allSubGroupTmplatContractList.add(fdSbTmplatContractProvider);

		/**
		 * Description: TMPLAT_CONTRACT_NUMBER ParentGroup:ccn01o03 GroupName:
		 * ccn01o05 SubGroups: BookMark: TMPLAT_CONTRACT_NUMBER Condition: None
		 */

		/*
		 * FormDataGroupDto fdSbTmplatContractNumber =
		 * createFormDataGroup(FormGroupsConstants.TMPLAT_CONTRACT_NUMBER,
		 * FormGroupsConstants.TMPLAT_CONTRACT);
		 * 
		 * List<BookmarkDto> bkSbTmplatContractNumberList = new
		 * ArrayList<BookmarkDto>();
		 * 
		 * BookmarkDto bkTmplatContractNumber =
		 * createBookmark(BookmarkConstants.CONTRACT_NUMBER,
		 * serviceAuthFormDataDto.getGenericCaseInfoDto().
		 * getTxtStagePriorityCmnts());
		 * bkSbTmplatContractNumberList.add(bkTmplatContractNumber);
		 * 
		 * fdSbTmplatContractNumber.setBookmarkDtoList(
		 * bkSbTmplatContractNumberList);
		 * 
		 * //adding the subgroup ccn01o05 to parent ParentGroup:ccn01o03
		 * subgroups list
		 * allSubGroupTmplatContractList.add(fdSbTmplatContractNumber);
		 */

		/**
		 * Description: TMPLAT_CONTRACT_AUTH Situation field has SvcAuth
		 * ParentGroup:ccn01o03 GroupName: ccn01o04 SubGroups: BookMark:
		 * TMPLAT_CONTRACT_AUTH Condition: None
		 */
		// Modified the code to check the condition to display the service auth
		// Id in form for warranty defect 12001
		if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getSvcAuthDetailDtoList())) {
			for (SVCAuthDetailDto svcAuthDetail : serviceAuthFormDataDto.getSvcAuthDetailDtoList()) {
				if (ServiceConstants.DAY_CARE_SVC_AUTH.contains(svcAuthDetail.getCdSvcAuthDtlSvc())) {
					FormDataGroupDto fdSbTmplatContractAuth = createFormDataGroup(
							FormGroupsConstants.TMPLAT_CONTRACT_AUTH, FormGroupsConstants.TMPLAT_CONTRACT);

					List<BookmarkDto> bkSbTmplatContractAuthList = new ArrayList<BookmarkDto>();

					BookmarkDto bkTmplatContractAuth = createBookmark(BookmarkConstants.CONTRACT_AUTH_ID,
							serviceAuthFormDataDto.getGenericCaseInfoDto().getIdSituation());
					bkSbTmplatContractAuthList.add(bkTmplatContractAuth);

					fdSbTmplatContractAuth.setBookmarkDtoList(bkSbTmplatContractAuthList);
					// adding the subgroup ccn01o04 to parent
					// ParentGroup:ccn01o03 subgroups list
					allSubGroupTmplatContractList.add(fdSbTmplatContractAuth);
				}
				break;
			}
		}
		// adding all subGroups to Parent
		fdPrTmplatContract.setFormDataGroupList(allSubGroupTmplatContractList);

		// adding the parent to final parent list
		fdParentGroupList.add(fdPrTmplatContract);
		/**********************************************
		 * adding Groups ccn01o60
		 ****************************************************************************/
		/**
		 * Description: TMPLAT_SECTIONA Situation field has SvcAuth ParentGroup:
		 * ccn01o60 GroupName: ccn01o60 SubGroups: ccn01o61, ccn01o62(ccn01o63)
		 * BookMark: TMPLAT_SECTIONA Condition: szCdStageReasonClosed != 40
		 */
		
		boolean indIsDayCare = !ObjectUtils.isEmpty(serviceAuthFormDataDto.getDayCareDetailsDtoList()) 
						&&  ServiceConstants.Zero < serviceAuthFormDataDto.getDayCareDetailsDtoList().size();


		if (!indIsDayCare) {
		FormDataGroupDto fdPrTmplatSectionA = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTIONA,
				FormConstants.EMPTY_STRING);

		List<FormDataGroupDto> allSubGroupTmplatSectionAList = new ArrayList<FormDataGroupDto>();
		/**
		 * Description: TMPLAT_SECTIONA_PREF ParentGroup: ccn01o60
		 * GroupName: ccn01o61 SubGroups: BookMark: TMPLAT_SECTIONA_PREF
		 * Condition: None
		 */
		FormDataGroupDto fdSbTmplatSectionAPref = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTIONA_PREF,
				FormGroupsConstants.TMPLAT_SECTIONA);

		List<BookmarkDto> bkSbTmplatSectionAPrefList = new ArrayList<BookmarkDto>();

		BookmarkDto bkTmplatSectionAPref = createBookmark(BookmarkConstants.SECTIONA_PREF_SUBCONT,
				serviceAuthFormDataDto.getServiceAuthorizationDto().getSvcAuthSecProvdr());
		bkSbTmplatSectionAPrefList.add(bkTmplatSectionAPref);

		fdSbTmplatSectionAPref.setBookmarkDtoList(bkSbTmplatSectionAPrefList);
		// adding subgroup ccn01o61 to parent ccn01o60
		allSubGroupTmplatSectionAList.add(fdSbTmplatSectionAPref);

		/**
		 * Description: TMPLAT_SECTIONA_SERVICE ParentGroup: ccn01o60
		 * GroupName: ccn01o62 SubGroups: ccn01o63 BookMark:
		 * TMPLAT_SECTIONA_SERVICE Condition: None
		 */

		// the bewlow tow list is for subgroup ccn01o63
		List<BookmarkDto> bkSbTmplatSectionAServiceSuffixList = new ArrayList<BookmarkDto>();		

		if (null != serviceAuthFormDataDto.getSVCDetailRec()) {
			BigDecimal nbrUnits = null;

			serviceAuthFormDataDto.getSVCDetailRec().sort(Comparator.comparing(SVCAuthDetailRecDto::getIdSvcAuthDtl));
			
			int snoSecASvc = 1;
			for (SVCAuthDetailRecDto svcAuthDtl : serviceAuthFormDataDto.getSVCDetailRec()) {
				FormDataGroupDto fdSbTmplatSectionAService = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SECTIONA_SERVICE, FormGroupsConstants.TMPLAT_SECTIONA);
				List<BookmarkDto> bkSbTmplatSectionAServiceList = new ArrayList<BookmarkDto>();
				
				if(!ObjectUtils.isEmpty(svcAuthDtl.getDtSvcAuthDtlBegin())) {
				BookmarkDto bkTmplatSectionAServiceBegin = createBookmark(
						BookmarkConstants.SECTIONA_SERVICE_BEGINDATE,
						DateUtils.stringDt(svcAuthDtl.getDtSvcAuthDtlBegin()));
				bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceBegin);
				}

				if(!ObjectUtils.isEmpty(svcAuthDtl.getDtSvcAuthDtlEnd())) {
				BookmarkDto bkTmplatSectionAServiceEnd = createBookmark(BookmarkConstants.SECTIONA_SERVICE_ENDDATE,
						DateUtils.stringDt(svcAuthDtl.getDtSvcAuthDtlEnd()));
				bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceEnd);
				}

				if(!ObjectUtils.isEmpty(svcAuthDtl.getDtSvcAuthDtlTerm())) {
				BookmarkDto bkTmplatSectionAServiceTermDate = createBookmark(
						BookmarkConstants.SECTIONA_SERVICE_TERMDATE,
						DateUtils.stringDt(svcAuthDtl.getDtSvcAuthDtlTerm()));
				bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceTermDate);
				}
				//Modified the code to set the decimal values for Warranty defect 12030
				if (null != svcAuthDtl.getNbrSvcAuthDtlUnitsReq()) {					
					nbrUnits = svcAuthDtl.getNbrSvcAuthDtlUnitsReq();
					nbrUnits = nbrUnits.setScale(ServiceConstants.INT_TWO);
				}
				BookmarkDto bkTmplatSectionAServiceUnits = createBookmark(BookmarkConstants.SECTIONA_SERVICE_UNITS,
						nbrUnits);
				bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceUnits);

				BookmarkDto bkTmplatSectionAServiceAuthType = createBookmark(
						BookmarkConstants.SECTIONA_SERVICE_AUTHTYPE, svcAuthDtl.getCdSvcAuthDtlAuthType());
				bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceAuthType);

				BookmarkDto bkTmplatSectionAServiceCode = createBookmark(BookmarkConstants.SECTIONA_SERVICE_CODE,
						svcAuthDtl.getCdSvcAuthDtlSvc());
				bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceCode);	
				
				// Warranty Defect - 11139 - Added Code to Display the CONCRETE SERVICES section in Form
				if(svcAuthDtl.getCdSvcAuthDtlSvc().equals(ServiceConstants.CONCRETE_SERVICES))
				{
					FormDataGroupDto tmpltConcreteServices = createFormDataGroup(
							FormGroupsConstants.TMPLAT_CONCRETE_SERVICES,FormConstants.EMPTY_STRING);
					fdParentGroupList.add(tmpltConcreteServices);					
				}

				BookmarkDto bkTmplatSectionAServiceDecode = createBookmarkWithCodesTable(
						BookmarkConstants.SECTIONA_SERVICE_DECODE, svcAuthDtl.getCdSvcAuthDtlSvc(),
						CodesConstant.CSVCCODE);
				bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceDecode);

				BookmarkDto bkTmplatSectionAServiceUnitType = createBookmark(
						BookmarkConstants.SECTIONA_SERVICE_UNITTYPE, svcAuthDtl.getCdSvcAuthDtlUnitType());
				bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceUnitType);

				BookmarkDto bkTmplatSectionAServiceSno = createBookmark(BookmarkConstants.SNO_SECA_SVC,
						snoSecASvc++);
				bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceSno);
				
				BookmarkDto bkTmplatSectionAServiceFname = createBookmark(BookmarkConstants.SECTIONA_SERVICE_FNAME,
						svcAuthDtl.getNmPersonFirst());
				bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceFname);

				BookmarkDto bkTmplatSectionAServiceLname = createBookmark(BookmarkConstants.SECTIONA_SERVICE_LNAME,
						svcAuthDtl.getNmPersonLast());
				bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceLname);

				BookmarkDto bkTmplatSectionAServiceMname = createBookmark(BookmarkConstants.SECTIONA_SERVICE_MNAME,
						svcAuthDtl.getNmPersonMiddle());
				bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceMname);

				BookmarkDto bkTmplatSectionAServiceAuthDtl = createBookmark(
						BookmarkConstants.SECTIONA_SERVICE_AUTHDTL, svcAuthDtl.getIdSvcAuthDtl());
				bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceAuthDtl);

				/**
				 * Description: TMPLAT_SECTIONA_SERVICE_SUFFIX ParentGroup:
				 * ccn01o62 GroupName: ccn01o63 SubGroups: None BookMark:
				 * TMPLAT_SECTIONA_SERVICE_SUFFIX Condition: null !=
				 * szCdNameSuffix
				 */
				// Warranty Defect Fix - 11350 - To Avoid Suffix prevented Multiple Times
				List<FormDataGroupDto> subGrpTmplSecAServiceSuffixList = new ArrayList<FormDataGroupDto>();
				if (null != svcAuthDtl.getCdPersonSuffix()) {
					FormDataGroupDto fdSbTmplatSectionAServiceSuffix = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SECTIONA_SERVICE_SUFFIX,
							FormGroupsConstants.TMPLAT_SECTIONA_SERVICE);

					BookmarkDto bkTmplatSectionAServiceSuffix = createBookmarkWithCodesTable(
							BookmarkConstants.SECTIONA_SERVICE_SUFFIX_DECODE,
							svcAuthDtl.getCdPersonSuffix(),
							CodesConstant.CSUFFIX2);
					bkSbTmplatSectionAServiceSuffixList.add(bkTmplatSectionAServiceSuffix);
					//ALM Defect# 13917- Form 2054 not Displaying Clients Correct
					// Code fix to add suffix correctly in Section A names
					// bkTmplatSectionAServiceSuffix was not added to bkSbTmplatSectionAServiceList to display correctly
					bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceSuffix);

					fdSbTmplatSectionAServiceSuffix.setBookmarkDtoList(bkSbTmplatSectionAServiceSuffixList);
					subGrpTmplSecAServiceSuffixList.add(fdSbTmplatSectionAServiceSuffix);
										
					fdSbTmplatSectionAService.setFormDataGroupList(subGrpTmplSecAServiceSuffixList);
				}
				fdSbTmplatSectionAService.setBookmarkDtoList(bkSbTmplatSectionAServiceList);
				allSubGroupTmplatSectionAList.add(fdSbTmplatSectionAService);
			}
			// adding the subgroup ccn01o63 to parent group ccn01o62
		}
		// adding subgroup ccn01o62 to parent ccn01o60
			fdPrTmplatSectionA.setFormDataGroupList(allSubGroupTmplatSectionAList);

		// adding the parent to final parent list
		fdParentGroupList.add(fdPrTmplatSectionA);
		}
		
		/**********************************************
		 * adding Groups ccn01o08
		 ****************************************************************************/

		/**
		 * Description: TMPLAT_SECTIONC ParentGroup: ccn01o08 GroupName:
		 * ccn01o08 SubGroups: ccn01o06 BookMark: TMPLAT_SECTIONC Condition:
		 * CdStageReasonClosed =40
		 */

		if (indIsDayCare) {
			List<FormDataGroupDto> sbGrpTmplatSecCList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto fdPrTmplatSectionC = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTIONC,
					FormConstants.EMPTY_STRING);

			/**
			 * Description: TMPLAT_SECTIONC_SVCAUTH ParentGroup: ccn01o08
			 * GroupName: ccn01o06 SubGroups: None BookMark:
			 * TMPLAT_SECTIONC_SVCAUTH Condition: None
			 */

			FormDataGroupDto fdSbTmplatSectionCSvcAuth = createFormDataGroup(
					FormGroupsConstants.TMPLAT_SECTIONC_SVCAUTH, FormGroupsConstants.TMPLAT_SECTIONC);
			List<BookmarkDto> bkSbTmplatSectionCSvcAuthList = new ArrayList<BookmarkDto>();

			BookmarkDto bkSbTmplatSectionCSvcAuthComments = createBookmark(BookmarkConstants.SECTIONC_SVCAUTH_COMMENTS,
					serviceAuthFormDataDto.getServiceAuthorizationDto().getSvcAuthComments());
			bkSbTmplatSectionCSvcAuthList.add(bkSbTmplatSectionCSvcAuthComments);

			fdSbTmplatSectionCSvcAuth.setBookmarkDtoList(bkSbTmplatSectionCSvcAuthList);

			sbGrpTmplatSecCList.add(fdSbTmplatSectionCSvcAuth);
			fdPrTmplatSectionC.setFormDataGroupList(sbGrpTmplatSecCList);

			// adding the parent to final parent list
			fdParentGroupList.add(fdPrTmplatSectionC);
		}
		/**********************************************
		 * adding Groups ccn01o71
		 ****************************************************************************/

		/**
		 * Description: TMPLAT_SECTIONB_CLIENT ParentGroup: ccn01o71 GroupName:
		 * ccn01o71 SubGroups: ccn01o72 BookMark: TMPLAT_SECTIONB_CLIENT
		 * Condition: None
		 */

		List<FormDataGroupDto> sbGrpTmplatSecBclientList = new ArrayList<FormDataGroupDto>();

		FormDataGroupDto fdPrTmplatSectionBClient = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTIONB_CLIENT,
				FormConstants.EMPTY_STRING);

		List<BookmarkDto> bkPrTmplatSectionBClientList = new ArrayList<BookmarkDto>();

		BookmarkDto bkPrTmplatSecBclientFname = createBookmark(BookmarkConstants.SECTIONB_CLIENT_FNAME,
				serviceAuthFormDataDto.getNameDetailDto().getNmNameFirst());
		bkPrTmplatSectionBClientList.add(bkPrTmplatSecBclientFname);

		BookmarkDto bkPrTmplatSecBclientLname = createBookmark(BookmarkConstants.SECTIONB_CLIENT_LNAME,
				serviceAuthFormDataDto.getNameDetailDto().getNmNameLast());
		bkPrTmplatSectionBClientList.add(bkPrTmplatSecBclientLname);

		BookmarkDto bkPrTmplatSecBclientMname = createBookmark(BookmarkConstants.SECTIONB_CLIENT_MNAME,
				serviceAuthFormDataDto.getNameDetailDto().getNmNameMiddle());
		bkPrTmplatSectionBClientList.add(bkPrTmplatSecBclientMname);

		fdPrTmplatSectionBClient.setBookmarkDtoList(bkPrTmplatSectionBClientList);

		/**
		 * Description: TMPLAT_SECTIONB_CLIENT_SUFFIX ParentGroup: ccn01o71
		 * GroupName: ccn01o72 SubGroups: BookMark:
		 * TMPLAT_SECTIONB_CLIENT_SUFFIX Condition: None
		 */

		if (null != serviceAuthFormDataDto.getNameDetailDto().getCdNameSuffix()) {

			FormDataGroupDto fdSbTmplatSectionBClientSuffix = createFormDataGroup(
					FormGroupsConstants.TMPLAT_SECTIONB_CLIENT_SUFFIX, FormGroupsConstants.TMPLAT_SECTIONB_CLIENT);

			List<BookmarkDto> bkSbTmplatSectionBClientSuffixList = new ArrayList<BookmarkDto>();

			BookmarkDto bkSbTmplatSecBclientSuffixFname = createBookmarkWithCodesTable(
					BookmarkConstants.SECTIONB_CLIENT_SUFFIX_DECODE,
					serviceAuthFormDataDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
			bkSbTmplatSectionBClientSuffixList.add(bkSbTmplatSecBclientSuffixFname);

			fdSbTmplatSectionBClientSuffix.setBookmarkDtoList(bkSbTmplatSectionBClientSuffixList);

			sbGrpTmplatSecBclientList.add(fdSbTmplatSectionBClientSuffix);
			fdPrTmplatSectionBClient.setFormDataGroupList(sbGrpTmplatSecBclientList);
		}

		// adding the parent to final parent list
		fdParentGroupList.add(fdPrTmplatSectionBClient);
		/**********************************************
		 * adding Groups ccn01o40
		 ****************************************************************************/
		/**
		 * Description: TMPLAT_KINSHIP ParentGroup: ccn01o40 GroupName: ccn01o40
		 * SubGroups: ccn01o41 BookMark: TMPLAT_KINSHIP Condition:
		 * CdStageReasonClosed = 68B or 68D
		 */
		if (ServiceConstants.CD_STAGE_REASON_CLOSED_68D
				.equalsIgnoreCase(serviceAuthFormDataDto.getGenericCaseInfoDto().getCdStageReasonClosed())
				|| ServiceConstants.CD_STAGE_REASON_CLOSED_68B
						.equalsIgnoreCase(serviceAuthFormDataDto.getGenericCaseInfoDto().getCdStageReasonClosed())) {

			List<FormDataGroupDto> sbGrpTmplatKinshipList = new ArrayList<FormDataGroupDto>();

			FormDataGroupDto fdPrTmplatKinship = createFormDataGroup(FormGroupsConstants.TMPLAT_KINSHIP,
					FormConstants.EMPTY_STRING);

			/**
			 * Description: TMPLAT_KINSHIP_GROUP ParentGroup: ccn01o40
			 * GroupName: ccn01o41 SubGroups: BookMark: TMPLAT_KINSHIP_GROUP
			 * Condition: CdStageReasonClosed = 68B or 68D
			 */

			for (KinshipGroupInfoDto kins : serviceAuthFormDataDto.getKinshipList()) {

				if (ServiceConstants.CD_SVC_AUTH_SERVICE_68D.equalsIgnoreCase(kins.getCdSvcAuthService())
						|| ServiceConstants.CD_SVC_AUTH_SERVICE_68B.equalsIgnoreCase(kins.getCdSvcAuthService())) {

					FormDataGroupDto fdSbTmplatKinshipGrp = createFormDataGroup(
							FormGroupsConstants.TMPLAT_KINSHIP_GROUP, FormGroupsConstants.TMPLAT_KINSHIP);

					List<BookmarkDto> bkSbTmplatKinshipGrpList = new ArrayList<BookmarkDto>();

					BookmarkDto bkSbTmplatKinshipGrpEndDate = createBookmark(BookmarkConstants.KINSHIP_GROUP_ENDDATE,
							kins.getDtKinshipEnd());
					bkSbTmplatKinshipGrpList.add(bkSbTmplatKinshipGrpEndDate);

					BookmarkDto bkSbTmplatKinshipGrpFName = createBookmark(BookmarkConstants.KINSHIP_GROUP_FNAME,
							kins.getNmNameFirst());
					bkSbTmplatKinshipGrpList.add(bkSbTmplatKinshipGrpFName);

					BookmarkDto bkSbTmplatKinshipGrpLName = createBookmark(BookmarkConstants.KINSHIP_GROUP_LNAME,
							kins.getNmNameLast());
					bkSbTmplatKinshipGrpList.add(bkSbTmplatKinshipGrpLName);

					BookmarkDto bkSbTmplatKinshipGrpMName = createBookmark(BookmarkConstants.KINSHIP_GROUP_MNAME,
							kins.getNmNameMiddle());
					bkSbTmplatKinshipGrpList.add(bkSbTmplatKinshipGrpMName);

					BookmarkDto bkSbTmplatKinshipGrpId = createBookmark(BookmarkConstants.KINSHIP_GROUP_PID,
							kins.getIdPerson());
					bkSbTmplatKinshipGrpList.add(bkSbTmplatKinshipGrpId);

					fdSbTmplatKinshipGrp.setBookmarkDtoList(bkSbTmplatKinshipGrpList);

					sbGrpTmplatKinshipList.add(fdSbTmplatKinshipGrp);

					fdPrTmplatKinship.setFormDataGroupList(sbGrpTmplatKinshipList);
				}
			}

			// adding the parent to final parent list
			fdParentGroupList.add(fdPrTmplatKinship);
		}

		/**********************************************
		 * adding Groups ccn01o84
		 ****************************************************************************/
		/**
		 * Description: TMPLAT_ORIGWORKER ParentGroup: ccn01o84 GroupName:
		 * ccn01o84 SubGroups: ccn01o85 BookMark: TMPLAT_ORIGWORKER Condition:
		 * None
		 */

		List<FormDataGroupDto> sbGrpTmplatOrigWorkerList = new ArrayList<FormDataGroupDto>();

		FormDataGroupDto fdPrTmplatOrigWorker = createFormDataGroup(FormGroupsConstants.TMPLAT_ORIGWORKER,
				FormConstants.EMPTY_STRING);

		List<BookmarkDto> bkPrTmplatOrigWorkerList = new ArrayList<BookmarkDto>();

		BookmarkDto bkPrTmplatOrigWorkerFName = createBookmark(BookmarkConstants.ORIGWORKER_FNAME,
				serviceAuthFormDataDto.getHistoyEmployeePersPhNameDto().getNmNameFirst());
		bkPrTmplatOrigWorkerList.add(bkPrTmplatOrigWorkerFName);

		BookmarkDto bkPrTmplatOrigWorkerLName = createBookmark(BookmarkConstants.ORIGWORKER_LNAME,
				serviceAuthFormDataDto.getHistoyEmployeePersPhNameDto().getNmNameLast());
		bkPrTmplatOrigWorkerList.add(bkPrTmplatOrigWorkerLName);

		BookmarkDto bkPrTmplatOrigWorkerMName = createBookmark(BookmarkConstants.ORIGWORKER_MNAME,
				serviceAuthFormDataDto.getHistoyEmployeePersPhNameDto().getNmNameMiddle());
		bkPrTmplatOrigWorkerList.add(bkPrTmplatOrigWorkerMName);

		BookmarkDto bkPrTmplatOrigWorkerId = createBookmark(BookmarkConstants.ORIGWORKER_PID,
				serviceAuthFormDataDto.getHistoyEmployeePersPhNameDto().getIdPerson());
		bkPrTmplatOrigWorkerList.add(bkPrTmplatOrigWorkerId);

		fdPrTmplatOrigWorker.setBookmarkDtoList(bkPrTmplatOrigWorkerList);

		/**
		 * Description: TMPLAT_ORIGWORKER_SUFFIX ParentGroup: ccn01o84
		 * GroupName: ccn01o85 SubGroups: BookMark: TMPLAT_ORIGWORKER_SUFFIX
		 * Condition: null != CdNameSuffix
		 */

		if (null != serviceAuthFormDataDto.getHistoyEmployeePersPhNameDto().getCdNameSuffix()) {

			FormDataGroupDto fdSbTmplatOrigWorkerSuffix = createFormDataGroup(
					FormGroupsConstants.TMPLAT_ORIGWORKER_SUFFIX, FormGroupsConstants.TMPLAT_ORIGWORKER);

			List<BookmarkDto> bkSbTmplatOrigWorkerSuffixList = new ArrayList<BookmarkDto>();

			BookmarkDto bkSbTmplatOrigWorkerSuffix = createBookmarkWithCodesTable(
					BookmarkConstants.ORIGWORKER_SUFFIX_DECODE,
					serviceAuthFormDataDto.getHistoyEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
			bkSbTmplatOrigWorkerSuffixList.add(bkSbTmplatOrigWorkerSuffix);

			fdSbTmplatOrigWorkerSuffix.setBookmarkDtoList(bkSbTmplatOrigWorkerSuffixList);

			sbGrpTmplatOrigWorkerList.add(fdSbTmplatOrigWorkerSuffix);

			fdPrTmplatOrigWorker.setFormDataGroupList(sbGrpTmplatOrigWorkerList);
		}

		// adding the parent to final parent list
		fdParentGroupList.add(fdPrTmplatOrigWorker);

		/**********************************************
		 * adding Groups ccn01o30
		 ****************************************************************************/

		/**
		 * Description: TMPLAT_DAYCAREGIVER ParentGroup: ccn01o30 GroupName:
		 * ccn01o30 SubGroups: ccn01o34,ccn01o32,ccn01o33 BookMark:
		 * TMPLAT_DAYCAREGIVER Condition: CdPersonType ==030
		 */

		// Modified the code to display caregiver if more than one caregive
		// present in the list for warranty defect 11063.  
		for (DayCareDetailsDto dayCareDetailsDto : serviceAuthFormDataDto.getDayCareDetailsDtoList()) {
			if (ServiceConstants.CAREGIVER.toString().equalsIgnoreCase(dayCareDetailsDto.getCdPersonType())) {
				FormDataGroupDto fdPrTmplatDayCareGiver = createFormDataGroup(FormGroupsConstants.TMPLAT_DAYCAREGIVER,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bkPrTmplatDayCareGiverList = new ArrayList<BookmarkDto>();
				List<FormDataGroupDto> sbGrpDayCareGiverTmplat = new ArrayList<FormDataGroupDto>();

				if (!ObjectUtils.isEmpty(dayCareDetailsDto)) {
					if (null != dayCareDetailsDto.getNbrTelephone()) {
						BookmarkDto bkPrTmplatDayCarePhone = createBookmark(BookmarkConstants.DAYCAREGIVER_TELEPHONE,
								TypeConvUtil.formatPhone(dayCareDetailsDto.getNbrTelephone()));
						bkPrTmplatDayCareGiverList.add(bkPrTmplatDayCarePhone);
					} else {
						BookmarkDto bkPrTmplatDayCarePhone = createBookmark(BookmarkConstants.DAYCAREGIVER_TELEPHONE,
								FormConstants.EMPTY_STRING);
						bkPrTmplatDayCareGiverList.add(bkPrTmplatDayCarePhone);
					}

					BookmarkDto bkPrTmplatDayCareCity = createBookmark(BookmarkConstants.DAYCAREGIVER_CITY,
							dayCareDetailsDto.getAddrCity());
					bkPrTmplatDayCareGiverList.add(bkPrTmplatDayCareCity);

					BookmarkDto bkPrTmplatDayCareAddrOne = createBookmark(BookmarkConstants.DAYCAREGIVER_ADDR1,
							dayCareDetailsDto.getAddrLn1());
					bkPrTmplatDayCareGiverList.add(bkPrTmplatDayCareAddrOne);

					BookmarkDto bkPrTmplatDayCareZip = createBookmark(BookmarkConstants.DAYCAREGIVER_ZIP,
							dayCareDetailsDto.getAddrZip1());
					bkPrTmplatDayCareGiverList.add(bkPrTmplatDayCareZip);

					BookmarkDto bkPrTmplatDayCareState = createBookmark(BookmarkConstants.DAYCAREGIVER_STATE,
							dayCareDetailsDto.getCdAddrState());
					bkPrTmplatDayCareGiverList.add(bkPrTmplatDayCareState);

					if (!ObjectUtils.isEmpty(dayCareDetailsDto.getNbrPersonIdNumber())) {
						BookmarkDto bkPrTmplatDayCareSSN = createBookmark(BookmarkConstants.DAYCAREGIVER_SSN,
								TypeConvUtil.formatSSN(dayCareDetailsDto.getNbrPersonIdNumber()));
						bkPrTmplatDayCareGiverList.add(bkPrTmplatDayCareSSN);
					}

					BookmarkDto bkPrTmplatDayCareFName = createBookmark(BookmarkConstants.DAYCAREGIVER_FNAME,
							dayCareDetailsDto.getNmPersonFirst());
					bkPrTmplatDayCareGiverList.add(bkPrTmplatDayCareFName);

					BookmarkDto bkPrTmplatDayCareLName = createBookmark(BookmarkConstants.DAYCAREGIVER_LNAME,
							dayCareDetailsDto.getNmPersonLast());
					bkPrTmplatDayCareGiverList.add(bkPrTmplatDayCareLName);

					BookmarkDto bkPrTmplatDayCareId = createBookmark(BookmarkConstants.DAYCAREGIVER_PID,
							dayCareDetailsDto.getIdPerson());
					bkPrTmplatDayCareGiverList.add(bkPrTmplatDayCareId);

					fdPrTmplatDayCareGiver.setBookmarkDtoList(bkPrTmplatDayCareGiverList);

					/**
					 * Description: TMPLAT_DAYCAREGIVER_BIRTH ParentGroup:
					 * ccn01o30 GroupName: ccn01o34 SubGroups: BookMark:
					 * TMPLAT_DAYCAREGIVER_BIRTH Condition: None
					 */
					FormDataGroupDto fdSbTmplatDayCareBirth = createFormDataGroup(
							FormGroupsConstants.TMPLAT_DAYCAREGIVER_BIRTH, FormGroupsConstants.TMPLAT_DAYCAREGIVER);

					List<BookmarkDto> bkSbTmplatDayCareBirthList = new ArrayList<BookmarkDto>();

					// assuming the CLSC36D detail required here is stored in
					// CSES77D which
					// is Client Info
					Long idPerson = dayCareDetailsDto.getIdPerson();
					ClientInfoServiceAuthDto currentClientDto = serviceAuthFormDataDto.getClientDtoList().stream()
							.filter(p -> p.getIdPersonId().equals(idPerson)).findFirst().orElse(null);
					if (!ObjectUtils.isEmpty(currentClientDto)) {
						BookmarkDto bkPrTmplatDayCareDob = createBookmark(BookmarkConstants.DAYCAREGIVER_BIRTH_DATE,
								DateUtils.stringDt(currentClientDto.getDtPersonBirth()));
						bkSbTmplatDayCareBirthList.add(bkPrTmplatDayCareDob);

						fdSbTmplatDayCareBirth.setBookmarkDtoList(bkSbTmplatDayCareBirthList);
					}
					sbGrpDayCareGiverTmplat.add(fdSbTmplatDayCareBirth);

					/**
					 * Description: TMPLAT_DAYCAREGIVER_PHONE ParentGroup:
					 * ccn01o30 GroupName: ccn01o32 SubGroups: BookMark:
					 * TMPLAT_DAYCAREGIVER_PHONE Condition: NbrPhoneExtension !=
					 * null
					 */

					if (null != dayCareDetailsDto.getlNbrPhoneExtension()) {
						FormDataGroupDto fdSbTmplatDayCarePh = createFormDataGroup(
								FormGroupsConstants.TMPLAT_DAYCAREGIVER_PHONE, FormGroupsConstants.TMPLAT_DAYCAREGIVER);

						List<BookmarkDto> bkSbTmplatDayCarePhList = new ArrayList<BookmarkDto>();

						// assuming the CLSC36D detail required here is stored
						// in CSES77D
						// which is Client Info
						BookmarkDto bkPrTmplatDayCarePhExtn = createBookmark(BookmarkConstants.DAYCAREGIVER_PHONE_EXT,
								dayCareDetailsDto.getlNbrPhoneExtension());
						bkSbTmplatDayCarePhList.add(bkPrTmplatDayCarePhExtn);
						fdSbTmplatDayCarePh.setBookmarkDtoList(bkSbTmplatDayCarePhList);

						sbGrpDayCareGiverTmplat.add(fdSbTmplatDayCarePh);

					}
					/**
					 * Description: TMPLAT_DAYCAREGIVER_ADDR ParentGroup:
					 * ccn01o30 GroupName: ccn01o33 SubGroups: BookMark:
					 * TMPLAT_DAYCAREGIVER_ADDR Condition: zAddrLn2 != null
					 */

					if (null != dayCareDetailsDto.getAddrLn2()) {

						FormDataGroupDto fdSbTmplatDayCareAddr = createFormDataGroup(
								FormGroupsConstants.TMPLAT_DAYCAREGIVER_ADDR, FormGroupsConstants.TMPLAT_DAYCAREGIVER);

						List<BookmarkDto> bkSbTmplatDayCareAddrList = new ArrayList<BookmarkDto>();

						// assuming the CLSC36D detail required here is stored
						// in CSES77D
						// which is Client Info
						BookmarkDto bkPrTmplatDayCareAddrTwo = createBookmark(BookmarkConstants.DAYCAREGIVER_ADDR_2,
								dayCareDetailsDto.getAddrLn2());
						bkSbTmplatDayCareAddrList.add(bkPrTmplatDayCareAddrTwo);

						fdSbTmplatDayCareAddr.setBookmarkDtoList(bkSbTmplatDayCareAddrList);

						sbGrpDayCareGiverTmplat.add(fdSbTmplatDayCareAddr);

					}

					fdPrTmplatDayCareGiver.setFormDataGroupList(sbGrpDayCareGiverTmplat);
					// adding the parent to final parent list
					fdParentGroupList.add(fdPrTmplatDayCareGiver);
				}
			}
		}

		// commenting out the bracket as according to data, the if condition(per
		// catalog) wil not show this section
		// }
		// }

		/**********************************************
		 * adding Groups ccn01o70
		 ****************************************************************************/
		/**
		 * Description: TMPLAT_SECTIONB ParentGroup: ccn01o70 GroupName:
		 * ccn01o70 SubGroups: ccn01o79, ccn01o50(primary : ccn01o50,ccn01o51),
		 * ccn01o76, ccn01o73(ccn01o74,ccn01o75), ccn01o77(ccn01o78 BookMark:
		 * TMPLAT_SECTIONB Condition:
		 */
		List<FormDataGroupDto> sbGrpsSectionBList = new ArrayList<FormDataGroupDto>();

		
		/**
		 * Description: TMPLAT_SECTIONB_SVCAUTH ParentGroup: ccn01o70
		 * GroupName: ccn01o79 SubGroups: BookMark: TMPLAT_SECTIONB_SVCAUTH
		 * Condition:
		 */
		if (!indIsDayCare) {

			FormDataGroupDto fdPrTmplatSectionB = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTIONB,
					FormConstants.EMPTY_STRING);
			
			FormDataGroupDto fdSbTmplatSectionBSvcAuth = createFormDataGroup(
					FormGroupsConstants.TMPLAT_SECTIONB_SVCAUTH, FormGroupsConstants.TMPLAT_SECTIONB);

			List<BookmarkDto> bkSbTmplatSecBSvcAuthList = new ArrayList<BookmarkDto>();
			//Formated the text for Warranty defect 10906 
			BookmarkDto bkTmplatSecBSvcAuth = createBookmark(BookmarkConstants.SECTIONB_SVCAUTH_COMMENTS,
					TypeConvUtil.formatTextValue(serviceAuthFormDataDto.getServiceAuthorizationDto().getSvcAuthComments()));
			bkSbTmplatSecBSvcAuthList.add(bkTmplatSecBSvcAuth);

			fdSbTmplatSectionBSvcAuth.setBookmarkDtoList(bkSbTmplatSecBSvcAuthList);

			// adding ccn01o79 to parent's ccn01o70 subgroup list
			sbGrpsSectionBList.add(fdSbTmplatSectionBSvcAuth);

			/**
			 * Description: TMPLAT_SECTIONB_CLIENTREL ParentGroup: ccn01o70
			 * GroupName: ccn01o50 SubGroups: ccn01o50,ccn01o51 BookMark:
			 * TMPLAT_SECTIONB_CLIENTREL Condition:
			 */
			
			int snoSecBRel = 1;
			for (ClientInfoServiceAuthDto clientDto : serviceAuthFormDataDto.getClientDtoList()) {
				List<FormDataGroupDto> sbGrpsSectionBClientRelList = new ArrayList<FormDataGroupDto>();
				
				FormDataGroupDto fdSbTmplatSecBClientRel = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SECTIONB_CLIENTREL, FormGroupsConstants.TMPLAT_SECTIONB);

				List<BookmarkDto> bkSbTmplatSecBClientRelList = new ArrayList<BookmarkDto>();

				// assuming the CLSC36D detail required here is stored in
				// CSES77D which is Client Info

				if(!ObjectUtils.isEmpty(clientDto.getDtPersonBirth())) {
				BookmarkDto bkSbTmplatSecBClientRelDob = createBookmark(BookmarkConstants.SECTIONB_CLIENTREL_DOB,
						DateUtils.stringDt(clientDto.getDtPersonBirth()));
				bkSbTmplatSecBClientRelList.add(bkSbTmplatSecBClientRelDob);
				}
				//Modified the correct relationship code constant for warranty defect 11516
				BookmarkDto bkSbTmplatSecBClientRelInt = createBookmarkWithCodesTable(BookmarkConstants.SECTIONB_CLIENTREL_REL,
						clientDto.getCdStagePersRelInt(), CodesConstant.CFAMLREL);
				bkSbTmplatSecBClientRelList.add(bkSbTmplatSecBClientRelInt);

				if(!ObjectUtils.isEmpty(clientDto.getNbrPersonIdNumber())) {
				BookmarkDto bkSbTmplatSecBClientRelSSN = createBookmark(BookmarkConstants.SECTIONB_CLIENTREL_SSN,
						TypeConvUtil.formatSSN(clientDto.getNbrPersonIdNumber()));
				bkSbTmplatSecBClientRelList.add(bkSbTmplatSecBClientRelSSN);
				}
				
				BookmarkDto bkSbTmplatSecBClientRelSno = createBookmark(BookmarkConstants.SNO_SECB_REL,
						snoSecBRel++);
				bkSbTmplatSecBClientRelList.add(bkSbTmplatSecBClientRelSno);

				BookmarkDto bkSbTmplatSecBClientRelFname = createBookmark(BookmarkConstants.SECTIONB_CLIENTREL_FNAME,
						clientDto.getNmNameFirst());
				bkSbTmplatSecBClientRelList.add(bkSbTmplatSecBClientRelFname);

				BookmarkDto bkSbTmplatSecBClientRelLname = createBookmark(BookmarkConstants.SECTIONB_CLIENTREL_LNAME,
						clientDto.getNmNameLast());
				bkSbTmplatSecBClientRelList.add(bkSbTmplatSecBClientRelLname);

				BookmarkDto bkSbTmplatSecBClientRelMname = createBookmark(BookmarkConstants.SECTIONB_CLIENTREL_MNAME,
						clientDto.getNmNameMiddle());
				bkSbTmplatSecBClientRelList.add(bkSbTmplatSecBClientRelMname);

				BookmarkDto bkSbTmplatSecBClientRelId = createBookmark(BookmarkConstants.SECTIONB_CLIENTREL_ID,
						clientDto.getIdPersonId());
				bkSbTmplatSecBClientRelList.add(bkSbTmplatSecBClientRelId);

				fdSbTmplatSecBClientRel.setBookmarkDtoList(bkSbTmplatSecBClientRelList);

				/********** Adding subgroups for 050 ***********************/

				/**
				 * Description: TMPLAT_SECTIONB_CLIENTREL_SUFFIX ParentGroup:
				 * ccn01o50 GroupName: ccn01o52 SubGroups: BookMark:
				 * TMPLAT_SECTIONB_CLIENTREL_SUFFIX Condition:
				 */
				if (!ObjectUtils.isEmpty(clientDto.getCdNameSuffix())) {
					FormDataGroupDto fdSbTmplatSecBClientRelSuffix = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SECTIONB_CLIENTREL_SUFFIX,
							FormGroupsConstants.TMPLAT_SECTIONB_CLIENTREL);

					List<BookmarkDto> bkSbTmplatSecBClientRelSuffixList = new ArrayList<BookmarkDto>();

					BookmarkDto bkSbTmplatSecBClientRelSuffix = createBookmarkWithCodesTable(
							BookmarkConstants.SECTIONB_CLIENTREL_SUFFIX_DECODE, clientDto.getCdNameSuffix(),
							CodesConstant.CSUFFIX2);
					bkSbTmplatSecBClientRelSuffixList.add(bkSbTmplatSecBClientRelSuffix);

					fdSbTmplatSecBClientRelSuffix.setBookmarkDtoList(bkSbTmplatSecBClientRelSuffixList);

					// adding to subgroup list of ccn01o50
					sbGrpsSectionBClientRelList.add(fdSbTmplatSecBClientRelSuffix);

				}

				/**
				 * Description: TMPLAT_SECTIONB_CLIENTREL_MEDIC ParentGroup:
				 * ccn01o50 GroupName: ccn01o51 SubGroups: BookMark:
				 * TMPLAT_SECTIONB_CLIENTREL_MEDIC Condition:
				 */

				FormDataGroupDto fdSbTmplatSecBClientRelMedic = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SECTIONB_CLIENTREL_MEDIC,
						FormGroupsConstants.TMPLAT_SECTIONB_CLIENTREL);

				List<BookmarkDto> bkSbTmplatSecBClientRelMedicList = new ArrayList<BookmarkDto>();

				if (!ObjectUtils.isEmpty(clientDto.getMedicaidServiceAuthDtoList())) {
					for (MedicaidServiceAuthDto medicaid : clientDto.getMedicaidServiceAuthDtoList()) {
						BookmarkDto bkSbTmplatSecBClientRelMedic = createBookmark(
								BookmarkConstants.SECTIONB_CLIENTREL_MEDIC_NUM, medicaid.getNbrPersonIdNumber());
						bkSbTmplatSecBClientRelMedicList.add(bkSbTmplatSecBClientRelMedic);
					}
				}

				fdSbTmplatSecBClientRelMedic.setBookmarkDtoList(bkSbTmplatSecBClientRelMedicList);

				// adding to subgroup list of ccn01o50
				sbGrpsSectionBClientRelList.add(fdSbTmplatSecBClientRelMedic);

				// adding to parent group ccn01o50
				fdSbTmplatSecBClientRel.setFormDataGroupList(sbGrpsSectionBClientRelList);

				// adding ccn01o50 to parent's ccn01o70 subgroup list
				sbGrpsSectionBList.add(fdSbTmplatSecBClientRel);

			}
			/**
			 * Description: TMPLAT_SECTIONB_STAGE ParentGroup: ccn01o70
			 * GroupName: ccn01o76 SubGroups: BookMark: TMPLAT_SECTIONB_STAGE
			 * Condition:
			 */

			FormDataGroupDto fdSbTmplatSecBStage = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTIONB_STAGE,
					FormGroupsConstants.TMPLAT_SECTIONB);

			List<BookmarkDto> bkSbTmplatSecBStageList = new ArrayList<BookmarkDto>();

			BookmarkDto bkSbTmplatSecBStage = createBookmarkWithCodesTable(BookmarkConstants.SECTIONB_STAGE_TYPE,
					serviceAuthFormDataDto.getGenericCaseInfoDto().getCdStage(), CodesConstant.CSTAGES);
			bkSbTmplatSecBStageList.add(bkSbTmplatSecBStage);

			BookmarkDto bkSbTmplatSecBStageName = createBookmark(BookmarkConstants.SECTIONB_STAGE_NAME,
					serviceAuthFormDataDto.getGenericCaseInfoDto().getNmStage());
			bkSbTmplatSecBStageList.add(bkSbTmplatSecBStageName);

			fdSbTmplatSecBStage.setBookmarkDtoList(bkSbTmplatSecBStageList);

			// adding ccn01o76 to parent's ccn01o70 subgroup list
			sbGrpsSectionBList.add(fdSbTmplatSecBStage);

			/**
			 * Description: TMPLAT_SECTIONB_CLIENTLOC ParentGroup: ccn01o70
			 * GroupName: ccn01o73 SubGroups: (ccn01o74,ccn01o75) BookMark:
			 * TMPLAT_SECTIONB_CLIENTLOC Condition:
			 */

			// list for subgroup (ccn01o74,ccn01o75)
			List<FormDataGroupDto> sbGrpsSecBclientLoc = new ArrayList<FormDataGroupDto>();

			FormDataGroupDto fdPrTmplatSecBclientLoc = createFormDataGroup(
					FormGroupsConstants.TMPLAT_SECTIONB_CLIENTLOC, FormGroupsConstants.TMPLAT_SECTIONB);

			List<BookmarkDto> bkSbTmplatSecBclientLocList = new ArrayList<BookmarkDto>();

			for (AddrPersonLinkPhoneOutDto addr : serviceAuthFormDataDto.getAddr()) {

				BookmarkDto bkSbTmplatSecBclientLocZip = createBookmark(BookmarkConstants.SECTIONB_CLIENTLOC_ZIP,
						addr.getAddrZip());
				bkSbTmplatSecBclientLocList.add(bkSbTmplatSecBclientLocZip);

				if(!ObjectUtils.isEmpty(addr.getPhone())) {
				BookmarkDto bkSbTmplatSecBclientLocPh = createBookmark(BookmarkConstants.SECTIONB_CLIENTLOC_PHONE,
						TypeConvUtil.formatPhone(addr.getPhone()));
				bkSbTmplatSecBclientLocList.add(bkSbTmplatSecBclientLocPh);
				}

				BookmarkDto bkSbTmplatSecBclientLocCity = createBookmark(BookmarkConstants.SECTIONB_CLIENTLOC_CITY,
						addr.getAddrCity());
				bkSbTmplatSecBclientLocList.add(bkSbTmplatSecBclientLocCity);

				BookmarkDto bkSbTmplatSecBclientLocLn1 = createBookmark(BookmarkConstants.SECTIONB_CLIENTLOC_ADDR1,
						addr.getAddrPersAddrStLn1());
				bkSbTmplatSecBclientLocList.add(bkSbTmplatSecBclientLocLn1);

				BookmarkDto bkSbTmplatSecBclientLocState = createBookmarkWithCodesTable(
						BookmarkConstants.SECTIONB_CLIENTLOC_STATE, addr.getCdAddrState(), CodesConstant.CSTATE);
				bkSbTmplatSecBclientLocList.add(bkSbTmplatSecBclientLocState);

				/**
				 * Description: TMPLAT_SECTIONB_CLIENTLOC_ADDR ParentGroup:
				 * ccn01o73 GroupName: ccn01o74 SubGroups: BookMark:
				 * TMPLAT_SECTIONB_CLIENTLOC_ADDR Condition: null !=
				 * AddrPersAddrStLn2
				 */

				if (null != addr.getAddrPersAddrStLn2()) {
					FormDataGroupDto fdSbTmplatSecBclientLocAddr2 = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SECTIONB_CLIENTLOC_ADDR,
							FormGroupsConstants.TMPLAT_SECTIONB_CLIENTLOC);

					// for subgroup ccn01o74
					List<BookmarkDto> bkSbTmplatSecBclientLocAddr2List = new ArrayList<BookmarkDto>();

					BookmarkDto bkSbTmplatSecBclientLocAddr2 = createBookmark(
							BookmarkConstants.SECTIONB_CLIENTLOC_ADDR_2, addr.getAddrPersAddrStLn2());
					bkSbTmplatSecBclientLocAddr2List.add(bkSbTmplatSecBclientLocAddr2);

					fdSbTmplatSecBclientLocAddr2.setBookmarkDtoList(bkSbTmplatSecBclientLocAddr2List);
					sbGrpsSecBclientLoc.add(fdSbTmplatSecBclientLocAddr2);
				}

				/**
				 * Description: TMPLAT_SECTIONB_CLIENTLOC_PH ParentGroup:
				 * ccn01o73 GroupName: ccn01o75 SubGroups: BookMark:
				 * TMPLAT_SECTIONB_CLIENTLOC_PH Condition: null !=
				 * NbrPhoneExtension
				 */
				if (null != addr.getPhoneExtension()) {

					FormDataGroupDto fdSbTmplatSecBclientLocPh = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SECTIONB_CLIENTLOC_PH,
							FormGroupsConstants.TMPLAT_SECTIONB_CLIENTLOC);

					// for subgroup ccn01o75
					List<BookmarkDto> bkSbTmplatSecBclientLocPhList = new ArrayList<BookmarkDto>();

					BookmarkDto bkSbTmplatSecBclientLocPhExtn = createBookmark(
							BookmarkConstants.SECTIONB_CLIENTLOC_PH_EXT, addr.getPhoneExtension());
					bkSbTmplatSecBclientLocPhList.add(bkSbTmplatSecBclientLocPhExtn);

					fdSbTmplatSecBclientLocPh.setBookmarkDtoList(bkSbTmplatSecBclientLocPhList);
					sbGrpsSecBclientLoc.add(fdSbTmplatSecBclientLocPh);
				}
				fdPrTmplatSecBclientLoc.setFormDataGroupList(sbGrpsSecBclientLoc);

			}

			fdPrTmplatSecBclientLoc.setBookmarkDtoList(bkSbTmplatSecBclientLocList);

			// adding ccn01o73 to parent's ccn01o70 subgroup list
			sbGrpsSectionBList.add(fdPrTmplatSecBclientLoc);
			/**
			 * Description: TMPLAT_SECTIONB_OV ParentGroup: ccn01o70 GroupName:
			 * ccn01o77 SubGroups: ccn01o78 BookMark: TMPLAT_SECTIONB_OV
			 * Condition:
			 */
			FormDataGroupDto fdPrTmplatSecBOV = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTIONB_OV,
					FormGroupsConstants.TMPLAT_SECTIONB);
			if (ServiceConstants.PERSON_OLDEST_VICTIM
					.equalsIgnoreCase(serviceAuthFormDataDto.getOldestVictimNameDto().getCdStagePersRelInt())) {

				// list for subgroup (ccn01o78)
				List<FormDataGroupDto> sbGrpsSecBOv = new ArrayList<FormDataGroupDto>();

				

				List<BookmarkDto> bkTmplatSecBOvList = new ArrayList<BookmarkDto>();

				BookmarkDto bkSbTmplatSecBOvFName = createBookmark(BookmarkConstants.SECTIONB_OV_FNAME,
						serviceAuthFormDataDto.getOldestVictimNameDto().getNmPersonFirst());
				bkTmplatSecBOvList.add(bkSbTmplatSecBOvFName);

				BookmarkDto bkSbTmplatSecBOvLName = createBookmark(BookmarkConstants.SECTIONB_OV_LNAME,
						serviceAuthFormDataDto.getOldestVictimNameDto().getNmPersonLast());
				bkTmplatSecBOvList.add(bkSbTmplatSecBOvLName);

				BookmarkDto bkSbTmplatSecBOvMName = createBookmark(BookmarkConstants.SECTIONB_OV_MNAME,
						serviceAuthFormDataDto.getOldestVictimNameDto().getNmPersonMiddle());
				bkTmplatSecBOvList.add(bkSbTmplatSecBOvMName);

				fdPrTmplatSecBOV.setBookmarkDtoList(bkTmplatSecBOvList);

				/**
				 * Description: TMPLAT_SECTIONB_OV_SUFFIX ParentGroup: ccn01o77
				 * GroupName: ccn01o78 SubGroups: BookMark:
				 * TMPLAT_SECTIONB_OV_SUFFIX Condition: null != CdNameSuffix
				 */
				if (null != serviceAuthFormDataDto.getOldestVictimNameDto().getCdPersonSuffix()) {

					FormDataGroupDto fdSbTmplatSecBOVSuffix = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SECTIONB_OV_SUFFIX, FormGroupsConstants.TMPLAT_SECTIONB_OV);

					List<BookmarkDto> bkTmplatSecBOvSuffixList = new ArrayList<BookmarkDto>();

					BookmarkDto bkSbTmplatSecBOvSuffix = createBookmarkWithCodesTable(
							BookmarkConstants.SECTIONB_OV_SUFFIX_DECODE,
							serviceAuthFormDataDto.getOldestVictimNameDto().getNmPersonFirst(), CodesConstant.CSUFFIX2);
					bkTmplatSecBOvSuffixList.add(bkSbTmplatSecBOvSuffix);

					fdSbTmplatSecBOVSuffix.setBookmarkDtoList(bkTmplatSecBOvSuffixList);
					sbGrpsSecBOv.add(fdSbTmplatSecBOVSuffix);

				}
				fdPrTmplatSecBOV.setFormDataGroupList(sbGrpsSecBOv);

				// adding list of S
				fdPrTmplatSecBclientLoc.setFormDataGroupList(sbGrpsSecBclientLoc);

			}
			// adding ccn01o77 to parent's ccn01o70 subgroup list
			sbGrpsSectionBList.add(fdPrTmplatSecBOV);

			// adding all subgroups to parent group ccn01o70.
			fdPrTmplatSectionB.setFormDataGroupList(sbGrpsSectionBList);
			
			// add the fdPrTmplatSectionB to the fdPArent groups list
			fdParentGroupList.add(fdPrTmplatSectionB);
		}
		

		/**********************************************
		 * adding Groups ccn01o10
		 ****************************************************************************/
		/**
		 * Description: TMPLAT_DAYCARE ParentGroup: ccn01o10 GroupName: ccn01o10
		 * SubGroups:
		 * ccn01o27,ccn01o28,ccn01o26,ccn01o24,ccn01o11,ccn01o12,ccn01o14,ccn01o20(ccn01o21,ccn01o22,ccn01o23)
		 * BookMark: TMPLAT_DAYCARE Condition: cdPersonType =010
		 */

		

		int snoDayCare = 1;
		for (DayCareDetailsDto dcare : serviceAuthFormDataDto.getDayCareDetailsDtoList()) {
			if (ServiceConstants.DAYCARETYPE.equalsIgnoreCase(dcare.getCdPersonType())) {
				// List to add all subgroups under primary group ccn01o10
				List<FormDataGroupDto> sbGrpTmplatDayCareList = new ArrayList<FormDataGroupDto>();
				
				List<BookmarkDto> bkPrTmplatDayCareList = new ArrayList<BookmarkDto>();

				FormDataGroupDto fdPrTmplatDayCare = createFormDataGroup(FormGroupsConstants.TMPLAT_DAYCARE,
						FormConstants.EMPTY_STRING);
				
				BookmarkDto bkSbDayCareSno = createBookmark(BookmarkConstants.SNO_DAYCARE, snoDayCare++);
				bkPrTmplatDayCareList.add(bkSbDayCareSno);

				if(!ObjectUtils.isEmpty(dcare.getCdDaycareType())) {
					BookmarkDto bkSbTmplatDCareRef = createBookmarkWithCodesTable(BookmarkConstants.DAYCARE_REFTYPE,
							dcare.getCdDaycareType(),CodesConstant.CDCREQTY);
					bkPrTmplatDayCareList.add(bkSbTmplatDCareRef);
				}
				
				if(!ObjectUtils.isEmpty(dcare.getDtBegin())) {
				BookmarkDto bkSbTmplatDCareBegin = createBookmark(BookmarkConstants.DAYCARE_BEGINDATE,
						DateUtils.stringDt(dcare.getDtBegin()));
				bkPrTmplatDayCareList.add(bkSbTmplatDCareBegin);
				}

				if(!ObjectUtils.isEmpty(dcare.getDtBeginProvider())) {
				BookmarkDto bkSbTmplatDCareBeginPro = createBookmark(BookmarkConstants.DAYCARE_BEGIN,
						DateUtils.stringDt(dcare.getDtBeginProvider()));
				bkPrTmplatDayCareList.add(bkSbTmplatDCareBeginPro);
				}

				if(!ObjectUtils.isEmpty(dcare.getDtEnd())) {
				BookmarkDto bkSbTmplatDCareEnd = createBookmark(BookmarkConstants.DAYCARE_ENDDATE,
						DateUtils.stringDt(dcare.getDtEnd()));
				bkPrTmplatDayCareList.add(bkSbTmplatDCareEnd);
				}
				
				if(!ObjectUtils.isEmpty(dcare.getDtSvcTerm())) {
					FormDataGroupDto fdSbTmplatDayCareTermDt = createFormDataGroup(FormGroupsConstants.TMPLAT_DAYCARE_TERMDATE,
							FormGroupsConstants.TMPLAT_DAYCARE);

					List<BookmarkDto> bkSbTmplatDayCareTermDtList = new ArrayList<BookmarkDto>();
					
					BookmarkDto bkSbTmplatDCareEnd = createBookmark(BookmarkConstants.DAYCARE_TERMDATE,
							DateUtils.stringDt(dcare.getDtSvcTerm()));
					bkSbTmplatDayCareTermDtList.add(bkSbTmplatDCareEnd);
					
					fdSbTmplatDayCareTermDt.setBookmarkDtoList(bkSbTmplatDayCareTermDtList);

					// adding the subgroup ccn01o27 to Parent ccn01o10 grp's sb
					// group list.
					sbGrpTmplatDayCareList.add(fdSbTmplatDayCareTermDt);
					
					}

				BookmarkDto bkSbTmplatDCareCity = createBookmark(BookmarkConstants.DAYCARE_CITY, dcare.getAddrCity());
				bkPrTmplatDayCareList.add(bkSbTmplatDCareCity);

				BookmarkDto bkSbTmplatDCareAddr1 = createBookmark(BookmarkConstants.DAYCARE_ADDR1, dcare.getAddrLn1());
				bkPrTmplatDayCareList.add(bkSbTmplatDCareAddr1);

				BookmarkDto bkSbTmplatDCareZip = createBookmark(BookmarkConstants.DAYCARE_ZIP, dcare.getAddrZip1());
				bkPrTmplatDayCareList.add(bkSbTmplatDCareZip);

				/*
				 * BookmarkDto bkSbTmplatDCareState=
				 * createBookmark(BookmarkConstants.DAYCARE_STATE,
				 * dcare.getCdAddrState());
				 * bkPrTmplatDayCareList.add(bkSbTmplatDCareState);
				 */

				BookmarkDto bkSbTmplatDCareState = createBookmarkWithCodesTable(BookmarkConstants.DAYCARE_STATE,
						dcare.getCdAddrState(), CodesConstant.CDCREQTY);
				bkPrTmplatDayCareList.add(bkSbTmplatDCareState);

				BookmarkDto bkSbTmplatDCareSuffix = createBookmarkWithCodesTable(BookmarkConstants.DAYCARE_SUFFIX,
						dcare.getCdPersonSuffix(), CodesConstant.CSUFFIX);
				bkPrTmplatDayCareList.add(bkSbTmplatDCareSuffix);

				BookmarkDto bkSbTmplatDCareReqType = createBookmarkWithCodesTable(BookmarkConstants.DAYCARE_RTYPE,
						dcare.getCdRequestType(), CodesConstant.CSVATYPE);
				bkPrTmplatDayCareList.add(bkSbTmplatDCareReqType);

				BookmarkDto bkSbTmplatDCareSummType = createBookmarkWithCodesTable(BookmarkConstants.DAYCARE_SUMMERTYPE,
						dcare.getCdSummerType(), CodesConstant.CDCSUMTY);
				bkPrTmplatDayCareList.add(bkSbTmplatDCareSummType);

				BookmarkDto bkSbTmplatDCareMaxDays = createBookmarkWithCodesTable(BookmarkConstants.DAYCARE_MAXDAYS,
						dcare.getCdVarSchMaxDays(), CodesConstant.CDCDAYS);
				bkPrTmplatDayCareList.add(bkSbTmplatDCareMaxDays);

				BookmarkDto bkSbTmplatDCareWkDays = createBookmarkWithCodesTable(BookmarkConstants.DAYCARE_WEEKENDTYPE,
						dcare.getCdWeekendType(), CodesConstant.CDCWKNTY);
				bkPrTmplatDayCareList.add(bkSbTmplatDCareWkDays);

				BookmarkDto bkSbTmplatDCareFri = createBookmark(BookmarkConstants.DAYCARE_FRI, dcare.getIndFri());
				bkPrTmplatDayCareList.add(bkSbTmplatDCareFri);

				BookmarkDto bkSbTmplatDCareMon = createBookmark(BookmarkConstants.DAYCARE_MON, dcare.getIndMon());
				bkPrTmplatDayCareList.add(bkSbTmplatDCareMon);

				BookmarkDto bkSbTmplatDCareSat = createBookmark(BookmarkConstants.DAYCARE_SAT, dcare.getIndSat());
				bkPrTmplatDayCareList.add(bkSbTmplatDCareSat);

				BookmarkDto bkSbTmplatDCareSun = createBookmark(BookmarkConstants.DAYCARE_SUN, dcare.getIndSun());
				bkPrTmplatDayCareList.add(bkSbTmplatDCareSun);

				BookmarkDto bkSbTmplatDCarethu = createBookmark(BookmarkConstants.DAYCARE_THU, dcare.getIndThu());
				bkPrTmplatDayCareList.add(bkSbTmplatDCarethu);

				BookmarkDto bkSbTmplatDCareTue = createBookmark(BookmarkConstants.DAYCARE_TUE, dcare.getIndTue());
				bkPrTmplatDayCareList.add(bkSbTmplatDCareTue);

				BookmarkDto bkSbTmplatDCareWed = createBookmark(BookmarkConstants.DAYCARE_WED, dcare.getIndWed());
				bkPrTmplatDayCareList.add(bkSbTmplatDCareWed);

				if(!ObjectUtils.isEmpty(dcare.getNbrPersonIdNumber())) {
				BookmarkDto bkSbTmplatDCareSsn = createBookmark(BookmarkConstants.DAYCARE_SSN,
						TypeConvUtil.formatSSN(dcare.getNbrPersonIdNumber()));
				bkPrTmplatDayCareList.add(bkSbTmplatDCareSsn);
				}

				if(!ObjectUtils.isEmpty(dcare.getNbrTelephone())) {
				BookmarkDto bkSbTmplatDCarePh = createBookmark(BookmarkConstants.DAYCARE_TLPHN,
						TypeConvUtil.formatPhone(dcare.getNbrTelephone()));
				bkPrTmplatDayCareList.add(bkSbTmplatDCarePh);
				}

				BookmarkDto bkSbTmplatDCareName = createBookmark(BookmarkConstants.DAYCARE_NAME, dcare.getNmFclty());
				bkPrTmplatDayCareList.add(bkSbTmplatDCareName);

				BookmarkDto bkSbTmplatDCareFName = createBookmark(BookmarkConstants.DAYCARE_FNAME,
						dcare.getNmPersonFirst());
				bkPrTmplatDayCareList.add(bkSbTmplatDCareFName);

				BookmarkDto bkSbTmplatDCareLName = createBookmark(BookmarkConstants.DAYCARE_LNAME,
						dcare.getNmPersonLast());
				bkPrTmplatDayCareList.add(bkSbTmplatDCareLName);

				BookmarkDto bkSbTmplatDCareMName = createBookmark(BookmarkConstants.DAYCARE_MNAME,
						dcare.getNmPersonMiddle());
				bkPrTmplatDayCareList.add(bkSbTmplatDCareMName);

				BookmarkDto bkSbTmplatDCareTxtCmnts = createBookmark(BookmarkConstants.DAYCARE_COMMENTS,
						dcare.getTxtComments());
				bkPrTmplatDayCareList.add(bkSbTmplatDCareTxtCmnts);

				BookmarkDto bkSbTmplatDCareHours = createBookmark(BookmarkConstants.DAYCARE_HOURS,
						dcare.getTxtHoursNeeded());
				bkPrTmplatDayCareList.add(bkSbTmplatDCareHours);

				BookmarkDto bkSbTmplatDCareId = createBookmark(BookmarkConstants.DAYCARE_ID, dcare.getIdFacility());
				bkPrTmplatDayCareList.add(bkSbTmplatDCareId);

				BookmarkDto bkSbTmplatDCarePId = createBookmark(BookmarkConstants.DAYCARE_PID, dcare.getIdPerson());
				bkPrTmplatDayCareList.add(bkSbTmplatDCarePId);

				fdPrTmplatDayCare.setBookmarkDtoList(bkPrTmplatDayCareList);

				/**
				 * Description: TMPLAT_DAYCARE_DOB ParentGroup: ccn01o10
				 * GroupName: ccn01o27 SubGroups: BookMark: TMPLAT_DAYCARE_DOB
				 * Condition:
				 */

				FormDataGroupDto fdSbTmplatDayCareDob = createFormDataGroup(FormGroupsConstants.TMPLAT_DAYCARE_DOB,
						FormGroupsConstants.TMPLAT_DAYCARE);

				List<BookmarkDto> bkSbTmplatDayCareDobList = new ArrayList<BookmarkDto>();
				
				// Get person detail from ClientDtoList
				ClientInfoServiceAuthDto currentClientDto = serviceAuthFormDataDto.getClientDtoList().stream().filter(p->p.getIdPersonId().equals(dcare.getIdPerson())).findFirst().orElse(null);
				if (!ObjectUtils.isEmpty(currentClientDto)) {
					if (!ObjectUtils.isEmpty(currentClientDto.getDtPersonBirth())) {
						BookmarkDto bkSbTmplatDCareDob = createBookmark(BookmarkConstants.DAYCARE_DOB,
								DateUtils.stringDt(currentClientDto.getDtPersonBirth()));
						bkSbTmplatDayCareDobList.add(bkSbTmplatDCareDob);
					}
				}

				fdSbTmplatDayCareDob.setBookmarkDtoList(bkSbTmplatDayCareDobList);

				// adding the subgroup ccn01o27 to Parent ccn01o10 grp's sb
				// group list.
				sbGrpTmplatDayCareList.add(fdSbTmplatDayCareDob);

				/**
				 * Description: TMPLAT_DAYCARE_SVCTYPE ParentGroup: ccn01o10
				 * GroupName: ccn01o26 SubGroups: BookMark:
				 * TMPLAT_DAYCARE_SVCTYPE Condition:
				 */

				FormDataGroupDto fdSbTmplatDayCareSvcType = createFormDataGroup(
						FormGroupsConstants.TMPLAT_DAYCARE_SVCTYPE, FormGroupsConstants.TMPLAT_DAYCARE);

				List<BookmarkDto> bkSbTmplatDayCareSvcTypeList = new ArrayList<BookmarkDto>();

				for (SVCAuthDetailDto svcAuthDetail : serviceAuthFormDataDto.getSvcAuthDetailDtoList()) {
					if (!ObjectUtils.isEmpty(svcAuthDetail.getIdPerson())
							&& svcAuthDetail.getIdPerson().equals(dcare.getIdPerson())
							&& !ObjectUtils.isEmpty(svcAuthDetail.getCdSvcAuthDtlSvc())) {
					BookmarkDto bkSbTmplatDCareSvcType = createBookmarkWithCodesTable(
							BookmarkConstants.DAYCARE_SVCTYPE_DECODE, svcAuthDetail.getCdSvcAuthDtlSvc(),
							CodesConstant.CSVCCODE);
					bkSbTmplatDayCareSvcTypeList.add(bkSbTmplatDCareSvcType);
					}
				}
				fdSbTmplatDayCareSvcType.setBookmarkDtoList(bkSbTmplatDayCareSvcTypeList);

				// adding the subgroup ccn01o26 to Parent ccn01o10 grp's sb
				// group list.
				sbGrpTmplatDayCareList.add(fdSbTmplatDayCareSvcType);

				/**
				 * Description: TMPLAT_DAYCARE_UNITS ParentGroup: ccn01o10
				 * GroupName: ccn01o24 SubGroups: BookMark: TMPLAT_DAYCARE_UNITS
				 * Condition:
				 */

				FormDataGroupDto fdSbTmplatDayCareUnits = createFormDataGroup(FormGroupsConstants.TMPLAT_DAYCARE_UNITS,
						FormGroupsConstants.TMPLAT_DAYCARE);

				List<BookmarkDto> bkSbTmplatDCareUnits = new ArrayList<BookmarkDto>();

				for (SVCAuthDetailDto svcAuthDetail : serviceAuthFormDataDto.getSvcAuthDetailDtoList()) {
					if (!ObjectUtils.isEmpty(svcAuthDetail.getIdPerson())
							&& svcAuthDetail.getIdPerson().equals(dcare.getIdPerson())
							&& !ObjectUtils.isEmpty(svcAuthDetail.getSvcAuthDtlUnitsReq())) {
						BookmarkDto bkSbTmplatDcareUnits = createBookmark(BookmarkConstants.DAYCARE_UNITS_NUM,
								TypeConvUtil
										.convertToTwoDecimalPlace(svcAuthDetail.getSvcAuthDtlUnitsReq().longValue()));
						bkSbTmplatDCareUnits.add(bkSbTmplatDcareUnits);
					}
				}
				fdSbTmplatDayCareUnits.setBookmarkDtoList(bkSbTmplatDCareUnits);

				// adding the subgroup ccn01o24 to Parent ccn01o10 grp's sb
				// group list.
				sbGrpTmplatDayCareList.add(fdSbTmplatDayCareUnits);

				/**
				 * Description: TMPLAT_DAYCARE_ZIP ParentGroup: ccn01o10
				 * GroupName: ccn01o11 SubGroups: BookMark: TMPLAT_DAYCARE_ZIP
				 * Condition: AddrZip2 != null
				 */
				if (null != dcare.getAddrZip2()) {

					FormDataGroupDto fdSbTmplatDayCareZip = createFormDataGroup(FormGroupsConstants.TMPLAT_DAYCARE_ZIP,
							FormGroupsConstants.TMPLAT_DAYCARE);

					List<BookmarkDto> bkSbTmplatDCareZipList = new ArrayList<BookmarkDto>();

					BookmarkDto bkSbTmplatDcareZip = createBookmark(BookmarkConstants.DAYCARE_ZIP_PLUS4,
							dcare.getAddrZip2());
					bkSbTmplatDCareZipList.add(bkSbTmplatDcareZip);

					fdSbTmplatDayCareZip.setBookmarkDtoList(bkSbTmplatDCareZipList);

					// adding the subgroup ccn01o11 to Parent ccn01o10 grp's sb
					// group list.
					sbGrpTmplatDayCareList.add(fdSbTmplatDayCareZip);
				}

				/**
				 * Description: TMPLAT_DAYCARE_TLPHN ParentGroup: ccn01o10
				 * GroupName: ccn01o12 SubGroups: BookMark: TMPLAT_DAYCARE_TLPHN
				 * Condition: NbrTlphnExt != null
				 */

				if (null != dcare.getNbrTlphnExt()) {
					FormDataGroupDto fdSbTmplatDayCareTlphn = createFormDataGroup(
							FormGroupsConstants.TMPLAT_DAYCARE_TLPHN, FormGroupsConstants.TMPLAT_DAYCARE);

					List<BookmarkDto> bkSbTmplatDCareTlphnList = new ArrayList<BookmarkDto>();

					BookmarkDto bkSbTmplatDcareTlphn = createBookmark(BookmarkConstants.DAYCARE_TLPHN_EXT,
							dcare.getNbrTlphnExt());
					bkSbTmplatDCareTlphnList.add(bkSbTmplatDcareTlphn);

					fdSbTmplatDayCareTlphn.setBookmarkDtoList(bkSbTmplatDCareTlphnList);

					// adding the subgroup ccn01o11 to Parent ccn01o10 grp's sb
					// group list.
					sbGrpTmplatDayCareList.add(fdSbTmplatDayCareTlphn);
				}

				/**
				 * Description: TMPLAT_DAYCARE_ADDR ParentGroup: ccn01o10
				 * GroupName: ccn01o14 SubGroups: BookMark: TMPLAT_DAYCARE_ADDR
				 * Condition: zAddrLn2 != null
				 */
				if (null != dcare.getAddrLn2()) {

					FormDataGroupDto fdSbTmplatDayCareAddr = createFormDataGroup(
							FormGroupsConstants.TMPLAT_DAYCARE_ADDR, FormGroupsConstants.TMPLAT_DAYCARE);

					List<BookmarkDto> bkSbTmplatDCareAddrList = new ArrayList<BookmarkDto>();

					BookmarkDto bkSbTmplatDcareAddr2 = createBookmark(BookmarkConstants.DAYCARE_ADDR_ADDR2,
							dcare.getAddrLn2());
					bkSbTmplatDCareAddrList.add(bkSbTmplatDcareAddr2);

					fdSbTmplatDayCareAddr.setBookmarkDtoList(bkSbTmplatDCareAddrList);

					// adding the subgroup ccn01o11 to Parent ccn01o10 grp's sb
					// group list.
					sbGrpTmplatDayCareList.add(fdSbTmplatDayCareAddr);

				}

				/**
				 * Description: TMPLAT_DAYCARE_DETAIL ParentGroup: ccn01o10
				 * GroupName: ccn01o25 SubGroups: BookMark:
				 * TMPLAT_DAYCARE_DETAIL Condition:
				 */
				FormDataGroupDto fdSbTmplatDayCareDetail = createFormDataGroup(
						FormGroupsConstants.TMPLAT_DAYCARE_DETAIL, FormGroupsConstants.TMPLAT_DAYCARE);

				List<BookmarkDto> bkSbTmplatDCareDetailList = new ArrayList<BookmarkDto>();
				for (SVCAuthDetailDto svcAuthDetail : serviceAuthFormDataDto.getSvcAuthDetailDtoList()) {
					// changes to get the ServiceAuthorizationDetailId one for
					// one person.
					if (null != dcare.getIdPerson() && null != svcAuthDetail.getIdPerson()) {
						if (svcAuthDetail.getIdPerson().equals(dcare.getIdPerson())) {
							BookmarkDto bkSbTmplatDcareDetail = createBookmark(BookmarkConstants.DAYCARE_DETAIL_ID,
									svcAuthDetail.getIdSvcAuthDtl());
							bkSbTmplatDCareDetailList.add(bkSbTmplatDcareDetail);
							fdSbTmplatDayCareDetail.setBookmarkDtoList(bkSbTmplatDCareDetailList);
							// adding the subgroup ccn01o25 to Parent ccn01o10
							// grp's sb group list.
							sbGrpTmplatDayCareList.add(fdSbTmplatDayCareDetail);
						}
					}
				}

				/**
				 * Description: TMPLAT_DAYCARE_TWO ParentGroup: ccn01o10
				 * GroupName: ccn01o20 SubGroups: (ccn01o21,ccn01o22,ccn01o23)
				 * BookMark: TMPLAT_DAYCARE_TWO Condition: IdDaycarePersonLink
				 * != 0
				 */
				List<FormDataGroupDto> subGrpDayCareTwo = new ArrayList<FormDataGroupDto>();

				for (DayCareFacilServiceAuthDto dayCareFacil : serviceAuthFormDataDto.getDayCareFaciListFinal()) {
					if (ServiceConstants.Zero_Value != dayCareFacil.getIdDaycarePersonLink()) {
						FormDataGroupDto fdSbTmplatDayCareTwo = createFormDataGroup(
								FormGroupsConstants.TMPLAT_DAYCARE_TWO, FormGroupsConstants.TMPLAT_DAYCARE);

						List<BookmarkDto> bkSbTmplatDCareTwoList = new ArrayList<BookmarkDto>();

						BookmarkDto bkSbTmplatDCareTwoBegin = createBookmark(BookmarkConstants.DAYCARE_TWO_BEGIN,
								dayCareFacil.getDtBeginProvider());
						bkSbTmplatDCareTwoList.add(bkSbTmplatDCareTwoBegin);

						BookmarkDto bkSbTmplatDCareTwoCity = createBookmark(BookmarkConstants.DAYCARE_TWO_CITY,
								dayCareFacil.getAddrCity());
						bkSbTmplatDCareTwoList.add(bkSbTmplatDCareTwoCity);

						BookmarkDto bkSbTmplatDCareTwoAddr1 = createBookmark(BookmarkConstants.DAYCARE_TWO_ADDR1,
								dayCareFacil.getAddrLn1());
						bkSbTmplatDCareTwoList.add(bkSbTmplatDCareTwoAddr1);

						BookmarkDto bkSbTmplatDCareTwoZip = createBookmark(BookmarkConstants.DAYCARE_TWO_ZIP,
								dayCareFacil.getAddrZip1());
						bkSbTmplatDCareTwoList.add(bkSbTmplatDCareTwoZip);

						BookmarkDto bkSbTmplatDCareTwoState = createBookmark(BookmarkConstants.DAYCARE_TWO_STATE,
								dayCareFacil.getCdAddrState());
						bkSbTmplatDCareTwoList.add(bkSbTmplatDCareTwoState);

						BookmarkDto bkSbTmplatDCareTwoTlphn = createBookmark(BookmarkConstants.DAYCARE_TWO_TLPHN,
								dayCareFacil.getNbrTelephone());
						bkSbTmplatDCareTwoList.add(bkSbTmplatDCareTwoTlphn);

						BookmarkDto bkSbTmplatDCareTwoName = createBookmark(BookmarkConstants.DAYCARE_TWO_NAME,
								dayCareFacil.getNmFclty());
						bkSbTmplatDCareTwoList.add(bkSbTmplatDCareTwoName);

						BookmarkDto bkSbTmplatDCareTwoId = createBookmark(BookmarkConstants.DAYCARE_TWO_ID,
								dayCareFacil.getIdFacility());
						bkSbTmplatDCareTwoList.add(bkSbTmplatDCareTwoId);

						fdSbTmplatDayCareTwo.setBookmarkDtoList(bkSbTmplatDCareTwoList);

						/**
						 * Description: TMPLAT_DAYCARE_TWO_ZIP ParentGroup:
						 * ccn01o20 GroupName: ccn01o21 SubGroups: BookMark:
						 * TMPLAT_DAYCARE_TWO_ZIP Condition: AddrZip2 != null
						 */

						if (null != dayCareFacil.getAddrZip2()) {
							FormDataGroupDto fdSbTmplatDayCareTwoZip = createFormDataGroup(
									FormGroupsConstants.TMPLAT_DAYCARE_TWO_ZIP, FormGroupsConstants.TMPLAT_DAYCARE_TWO);

							List<BookmarkDto> bkSbTmplatDCareTwoZipList = new ArrayList<BookmarkDto>();

							BookmarkDto bkSbTmplatDCareTwoZip2 = createBookmark(BookmarkConstants.DAYCARE_TWO_ZIP_PLUS4,
									dayCareFacil.getAddrZip2());
							bkSbTmplatDCareTwoZipList.add(bkSbTmplatDCareTwoZip2);

							fdSbTmplatDayCareTwoZip.setBookmarkDtoList(bkSbTmplatDCareTwoZipList);

							subGrpDayCareTwo.add(fdSbTmplatDayCareTwoZip);
						}

						/**
						 * Description: TMPLAT_DAYCARE_TWO_ADDR ParentGroup:
						 * ccn01o20 GroupName: ccn01o23 SubGroups: BookMark:
						 * TMPLAT_DAYCARE_TWO_ADDR Condition: AddrLn2 != null
						 */

						if (null != dayCareFacil.getAddrLn2()) {
							FormDataGroupDto fdSbTmplatDayCareTwoAddr2 = createFormDataGroup(
									FormGroupsConstants.TMPLAT_DAYCARE_TWO_ADDR,
									FormGroupsConstants.TMPLAT_DAYCARE_TWO);

							List<BookmarkDto> bkSbTmplatDCareTwoAddr2List = new ArrayList<BookmarkDto>();

							BookmarkDto bkSbTmplatDCareTwoLn2 = createBookmark(BookmarkConstants.DAYCARE_TWO_ADDR_TWO,
									dayCareFacil.getAddrLn2());
							bkSbTmplatDCareTwoAddr2List.add(bkSbTmplatDCareTwoLn2);

							fdSbTmplatDayCareTwoAddr2.setBookmarkDtoList(bkSbTmplatDCareTwoAddr2List);

							subGrpDayCareTwo.add(fdSbTmplatDayCareTwoAddr2);

						}
						/**
						 * Description: TMPLAT_DAYCARE_TWO_TLPHN ParentGroup:
						 * ccn01o20 GroupName: ccn01o22 SubGroups: BookMark:
						 * TMPLAT_DAYCARE_TWO_TLPHN Condition: NbrTlphnExt !=
						 * null
						 */

						if (null != dcare.getNbrTlphnExt()) {
							FormDataGroupDto fdSbTmplatDayCareTwoPhExtn = createFormDataGroup(
									FormGroupsConstants.TMPLAT_DAYCARE_TWO_TLPHN,
									FormGroupsConstants.TMPLAT_DAYCARE_TWO);

							List<BookmarkDto> bkSbTmplatDCareTwoPhExtnList = new ArrayList<BookmarkDto>();

							BookmarkDto bkSbTmplatDCareTwoPhExtn = createBookmark(
									BookmarkConstants.DAYCARE_TWO_ADDR_TWO, dayCareFacil.getNbrTlphnExt());
							bkSbTmplatDCareTwoPhExtnList.add(bkSbTmplatDCareTwoPhExtn);
							fdSbTmplatDayCareTwoPhExtn.setBookmarkDtoList(bkSbTmplatDCareTwoPhExtnList);

							subGrpDayCareTwo.add(fdSbTmplatDayCareTwoPhExtn);

						}

						fdSbTmplatDayCareTwo.setFormDataGroupList(subGrpDayCareTwo);

						// adding the subgroup ccn01o20 to Parent ccn01o10 grp's
						// sb group list.
						sbGrpTmplatDayCareList.add(fdSbTmplatDayCareTwo);
					}
				}

				fdPrTmplatDayCare.setFormDataGroupList(sbGrpTmplatDayCareList);
				fdParentGroupList.add(fdPrTmplatDayCare);
			}

		}

		/**********************************************
		 * adding Groups ccn01o87
		 ****************************************************************************/
		/**
		 * Description: TMPLAT_APPROVER ParentGroup: ccn01o86 GroupName:
		 * ccn01o87 SubGroups: ccn01o87 BookMark: TMPLAT_APPROVER Condition:
		 * None
		 */

		List<FormDataGroupDto> fdTmpltApprovrList = new ArrayList<FormDataGroupDto>();

		FormDataGroupDto fdTmpltApprovr = createFormDataGroup(FormGroupsConstants.TMPLAT_APPROVER,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bkTmpltApprovrList = new ArrayList<BookmarkDto>();
		
		if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getApprovalFormDataDto())) {
			if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getApprovalFormDataDto().getDtApproversDetermination())) {
				BookmarkDto bkTmplatApprvrDt = createBookmark(BookmarkConstants.APPROVER_DATE, DateUtils
						.stringDt(serviceAuthFormDataDto.getApprovalFormDataDto().getDtApproversDetermination()));
				bkTmpltApprovrList.add(bkTmplatApprvrDt);
			}

			BookmarkDto bkPrTmplatApprvrFNm = createBookmark(BookmarkConstants.APPROVER_FNAME,
					serviceAuthFormDataDto.getApprovalFormDataDto().getNmFirst());
			bkTmpltApprovrList.add(bkPrTmplatApprvrFNm);

			BookmarkDto bkPrTmplatApprvrLNm = createBookmark(BookmarkConstants.APPROVER_LNAME,
					serviceAuthFormDataDto.getApprovalFormDataDto().getNmLast());
			bkTmpltApprovrList.add(bkPrTmplatApprvrLNm);

			BookmarkDto bkPrTmplatApprvrMNm = createBookmark(BookmarkConstants.APPROVER_MNAME,
					serviceAuthFormDataDto.getApprovalFormDataDto().getNmMiddle());
			bkTmpltApprovrList.add(bkPrTmplatApprvrMNm);

			BookmarkDto bkPrTmplatApprvrTitle = createBookmark(BookmarkConstants.APPROVER_TITLE,
					serviceAuthFormDataDto.getApprovalFormDataDto().getTxtEmployeeClass());
			bkTmpltApprovrList.add(bkPrTmplatApprvrTitle);

			fdTmpltApprovr.setBookmarkDtoList(bkTmpltApprovrList);
		}

		/**
		 * Description: TMPLAT_ORIGWORKER_SUFFIX ParentGroup: ccn01o86
		 * GroupName: ccn01o87 SubGroups: BookMark: TMPLAT_ORIGWORKER_SUFFIX
		 * Condition: null != CdNameSuffix
		 */

		if (null != serviceAuthFormDataDto.getEmployeePersPhNameDto().getCdNameSuffix()) {

			FormDataGroupDto fdTmplatOrigWorkerSufx = createFormDataGroup(FormGroupsConstants.TMPLAT_APPROVER_SUFFIX,
					FormGroupsConstants.TMPLAT_APPROVER);
			List<BookmarkDto> bkTmpltApprovrSuffixList = new ArrayList<BookmarkDto>();
			BookmarkDto bkPrTmplatApprvrSufxDecode = createBookmarkWithCodesTable(
					BookmarkConstants.APPROVER_SUFFIX_DECODE,
					serviceAuthFormDataDto.getApprovalFormDataDto().getNmSuffix(), CodesConstant.CSUFFIX2);

			bkTmpltApprovrSuffixList.add(bkPrTmplatApprvrSufxDecode);

			fdTmplatOrigWorkerSufx.setBookmarkDtoList(bkTmpltApprovrSuffixList);

			fdTmpltApprovrList.add(fdTmplatOrigWorkerSufx);
			fdTmpltApprovr.setFormDataGroupList(fdTmpltApprovrList);
		}

		// adding the parent to final parent list
		fdParentGroupList.add(fdTmpltApprovr);

		PreFillDataServiceDto preFillDataServiceDto = new PreFillDataServiceDto();
		preFillDataServiceDto.setBookmarkDtoList(bookmarkDtoDefaultDtoList);
		preFillDataServiceDto.setFormDataGroupList(fdParentGroupList);

		return preFillDataServiceDto;
	}

}
