/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jan 23, 2018- 10:47:20 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.*;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;
import us.tx.state.dfps.service.person.dto.PersonPhoneDto;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.dto.PersonLocPersonOutDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 23, 2018- 10:47:20 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class AssessmentRequestPrefillData extends DocumentServiceUtil {

	@Autowired
	LookupDao lookupDao;

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
		ServicePackageFormDto servicePackageFormDto = (ServicePackageFormDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		if (null == servicePackageFormDto.getPersonDto()) {
			servicePackageFormDto.setPersonDto(new PersonDto());
		}
		if (null == servicePackageFormDto.getStageCaseDtlDto()) {
			servicePackageFormDto.setStageCaseDtlDto(new StageCaseDtlDto());
		}
		if (null == servicePackageFormDto.getEmployeePersPhNameDto()) {
			servicePackageFormDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (null == servicePackageFormDto.getPersonPhoneDto()) {
			servicePackageFormDto.setPersonPhoneDto(new PersonPhoneDto());
		}
		if (null == servicePackageFormDto.getStagePersonDto()) {
			servicePackageFormDto.setStagePersonDto(new StagePersonDto());
		}
		if (null == servicePackageFormDto.getPersonLocDto()) {
			servicePackageFormDto.setPersonLocDto(new PersonLocDto());
		}
		if (null == servicePackageFormDto.getPersonLocPersonOutDto()) {
			servicePackageFormDto.setPersonLocPersonOutDto(new PersonLocPersonOutDto());
		}
		if (null == servicePackageFormDto.getPersonDto()) {
			servicePackageFormDto.setPersonDto(new PersonDto());
		}
	    for (LegalStatusPersonMaxStatusDtOutDto Legal : servicePackageFormDto.getLegalStatusPersonMaxList()) {
			if (null == Legal) {
				List<LegalStatusPersonMaxStatusDtOutDto> legal2 = new ArrayList<LegalStatusPersonMaxStatusDtOutDto>();
				servicePackageFormDto.setLegalStatusPersonMaxList(legal2);
			}
		}
		for (PlacementActPlannedOutDto place : servicePackageFormDto.getPlacementActPlannedList()) {
			if (null == place) {
				List<PlacementActPlannedOutDto> place2 = new ArrayList<PlacementActPlannedOutDto>();
				servicePackageFormDto.setPlacementActPlannedList(place2);
			}
		}

		/**
		 * If equals set the prefill data for group cfa2005 and cfa2003
		 */
		if (FormConstants.EMPTY_STRING != servicePackageFormDto.getPersonDto().getCdPersonSuffix()
				&& FormConstants.EMPTY_STRING != servicePackageFormDto.getEmployeePersPhNameDto().getCdNameSuffix()) {
			FormDataGroupDto tempChildCommaDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			FormDataGroupDto tempChildCommaDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_1,
					FormConstants.EMPTY_STRING);
			if (!ObjectUtils.isEmpty(servicePackageFormDto.getPersonDto().getCdPersonSuffix())) {
				formDataGroupList.add(tempChildCommaDto);
			}
			if (!ObjectUtils.isEmpty(servicePackageFormDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
				formDataGroupList.add(tempChildCommaDto2);
			}

		}

		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		BookmarkDto bookmarkReqDate = createBookmark(BookmarkConstants.REQUEST_DATE,
				DateUtils.stringDt(servicePackageFormDto.getStageCaseDtlDto().getCurrentDate()));
		bookmarkNonFrmGrpList.add(bookmarkReqDate);

		BookmarkDto bookmarkTitleCaseNm = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				servicePackageFormDto.getStageCaseDtlDto().getNmCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNm);

		BookmarkDto bookmarkTitleCaseNbr = createBookmark(BookmarkConstants.TITLE_CASE_ID,
				servicePackageFormDto.getStageCaseDtlDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNbr);

		BookmarkDto bookmarkWorkerPhnNbr = createBookmark(BookmarkConstants.WORKER_PHONE_NBR,
				servicePackageFormDto.getEmployeePersPhNameDto().getNbrPhone());
		bookmarkNonFrmGrpList.add(bookmarkWorkerPhnNbr);

		if(!ObjectUtils.isEmpty(servicePackageFormDto.getEmployeePersPhNameDto())
		     && servicePackageFormDto.getEmployeePersPhNameDto().getCdEmpBjnEmp().equals("SSCC")) {
			BookmarkDto bookmarkWorkerEmail = createBookmark(BookmarkConstants.WORKER_EMAIL,
					servicePackageFormDto.getEmployeePersPhNameDto().getTxtEmail());
			bookmarkNonFrmGrpList.add(bookmarkWorkerEmail);
		}else{
			BookmarkDto bookmarkWorkerEmail = createBookmark(BookmarkConstants.WORKER_EMAIL,
					servicePackageFormDto.getEmployeePersPhNameDto().getTxtemployeeEmail());
			bookmarkNonFrmGrpList.add(bookmarkWorkerEmail);
		}

		BookmarkDto bookmarkWorkerPhnExt = createBookmark(BookmarkConstants.WORKER_PHONE_EXT,
				servicePackageFormDto.getEmployeePersPhNameDto().getNbrMailCodePhoneExt());
		bookmarkNonFrmGrpList.add(bookmarkWorkerPhnExt);

		if (!ObjectUtils.isEmpty(servicePackageFormDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
			BookmarkDto bookmarkWorkerSuff = createBookmark(BookmarkConstants.WORKER_SUFFIX, lookupDao
					.decode(ServiceConstants.CSUFFIX2, servicePackageFormDto.getEmployeePersPhNameDto().getCdNameSuffix()));
			bookmarkNonFrmGrpList.add(bookmarkWorkerSuff);
		}

		BookmarkDto bookmarkWorkerFst = createBookmark(BookmarkConstants.WORKER_FIRST,
				servicePackageFormDto.getEmployeePersPhNameDto().getNmNameFirst());
		bookmarkNonFrmGrpList.add(bookmarkWorkerFst);

		BookmarkDto bookmarkWorkerMid = createBookmark(BookmarkConstants.WORKER_MIDDLE,
				servicePackageFormDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(bookmarkWorkerMid);

		BookmarkDto bookmarkWorkerLst = createBookmark(BookmarkConstants.WORKER_LAST,
				servicePackageFormDto.getEmployeePersPhNameDto().getNmNameLast());
		bookmarkNonFrmGrpList.add(bookmarkWorkerLst);

		LegalStatusPersonMaxStatusDtOutDto legalStatusPersonMaxStatusDtOutDto=null;
		if(CollectionUtils.isEmpty(servicePackageFormDto.getLegalStatusPersonMaxList())) {
			legalStatusPersonMaxStatusDtOutDto=new LegalStatusPersonMaxStatusDtOutDto();
		}
		else {
			 legalStatusPersonMaxStatusDtOutDto=servicePackageFormDto.getLegalStatusPersonMaxList().get(0);
		}

		BookmarkDto bookmarkChildCntyCvs = createBookmark(BookmarkConstants.CHILD_CNTY_CVS, lookupDao.decode(
				ServiceConstants.CCOUNT, legalStatusPersonMaxStatusDtOutDto.getCdLegalStatCnty()));
		bookmarkNonFrmGrpList.add(bookmarkChildCntyCvs);

		BookmarkDto bookmarkChildRegCvs = createBookmark(BookmarkConstants.CHILD_REGION_CVS,
				lookupDao.decode(ServiceConstants.CCNTYREG,
						legalStatusPersonMaxStatusDtOutDto.getCdLegalStatCnty()));
		bookmarkNonFrmGrpList.add(bookmarkChildRegCvs);

		BookmarkDto bookmarkChildCvsStatus = createBookmark(BookmarkConstants.CHILD_CVS_STATUS,
				lookupDao.decode(ServiceConstants.CLEGSTAT,
						legalStatusPersonMaxStatusDtOutDto.getCdLegalStatStatus()));
		bookmarkNonFrmGrpList.add(bookmarkChildCvsStatus);

		BookmarkDto bookmarkChildCurrLoc = createBookmark(BookmarkConstants.CHILD_CURRENT_LOC,
				lookupDao.decode(ServiceConstants.CATHPLOC, servicePackageFormDto.getPersonLocDto().getCdPlocChild()));
		bookmarkNonFrmGrpList.add(bookmarkChildCurrLoc);

		PlacementActPlannedOutDto placementActPlannedOutDto=null;
		if(CollectionUtils.isEmpty(servicePackageFormDto.getPlacementActPlannedList())) {
			placementActPlannedOutDto=new PlacementActPlannedOutDto();
		}
		else {
			placementActPlannedOutDto=servicePackageFormDto.getPlacementActPlannedList().get(0);
		}
		BookmarkDto bookmarkFaclityNm = createBookmark(BookmarkConstants.FACILITY_NAME,
				placementActPlannedOutDto.getNmPlcmtFacil());
		bookmarkNonFrmGrpList.add(bookmarkFaclityNm);

		Long idFacilNumber = placementActPlannedOutDto.getIdRsrcFacil();
		idFacilNumber = ObjectUtils.isEmpty(idFacilNumber) ? 0l : idFacilNumber;
		BookmarkDto bookmarkFacilityNbr = createBookmark(BookmarkConstants.FACILITY_NBR, idFacilNumber);
		bookmarkNonFrmGrpList.add(bookmarkFacilityNbr);

		BookmarkDto bookmarkChildSex = createBookmark(BookmarkConstants.CHILD_SEX,
				servicePackageFormDto.getPersonDto().getCdPersonSex());
		bookmarkNonFrmGrpList.add(bookmarkChildSex);

		SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd-yyyy");
		String dateString2 = "";
		dateString2 = sdf2.format(servicePackageFormDto.getPersonDto().getDtPersonBirth());
		BookmarkDto bookmarkTitleChildDOB = createBookmark(BookmarkConstants.TITLE_CHILD_DOB, dateString2);
		bookmarkNonFrmGrpList.add(bookmarkTitleChildDOB);

		if (!ObjectUtils.isEmpty(servicePackageFormDto.getPersonDto().getCdPersonSuffix())) {
			BookmarkDto bookmarkTitleChildSuff = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_SUFFIX,
					lookupDao.decode(ServiceConstants.CSUFFIX2, servicePackageFormDto.getPersonDto().getCdPersonSuffix()));
			bookmarkNonFrmGrpList.add(bookmarkTitleChildSuff);
		}
		BookmarkDto bookmarkTitleChildEthn = createBookmark(BookmarkConstants.CHILD_ETHNICITY,
				lookupDao.decode(ServiceConstants.CETHNIC, servicePackageFormDto.getPersonDto().getCdPersonEthnicGroup()));
		bookmarkNonFrmGrpList.add(bookmarkTitleChildEthn);

		BookmarkDto bookmarkTitleChildNmFst = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_FIRST,
				servicePackageFormDto.getPersonDto().getNmPersonFirst());
		bookmarkNonFrmGrpList.add(bookmarkTitleChildNmFst);

		BookmarkDto bookmarkTitleChildNmMid = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_MIDDLE,
				servicePackageFormDto.getPersonDto().getNmPersonMiddle());
		bookmarkNonFrmGrpList.add(bookmarkTitleChildNmMid);

		BookmarkDto bookmarkTitleChildNmLst = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_LAST,
				servicePackageFormDto.getPersonDto().getNmPersonLast());
		bookmarkNonFrmGrpList.add(bookmarkTitleChildNmLst);

		BookmarkDto bookmarkChildPrsnId = createBookmark(BookmarkConstants.CHILD_PERSON_ID,
				servicePackageFormDto.getPersonDto().getIdPerson());
		bookmarkNonFrmGrpList.add(bookmarkChildPrsnId);

		if (!ObjectUtils.isEmpty(servicePackageFormDto.getPersonIdDto())) {
			if (!ObjectUtils.isEmpty(servicePackageFormDto.getPersonIdDto().getPersonIdNumber())) {
				BookmarkDto bookmarkMedcdNbr = createBookmark(BookmarkConstants.CHILD_MEDICAID_NBR,
						servicePackageFormDto.getPersonIdDto().getPersonIdNumber());
				bookmarkNonFrmGrpList.add(bookmarkMedcdNbr);
			}
		}

		if(!ObjectUtils.isEmpty(servicePackageFormDto.getServicePackageDtlDto())){
			if(!ObjectUtils.isEmpty(servicePackageFormDto.getServicePackageDtlDto().getSvcPkgCd())){
				BookmarkDto bookmarkRecommendPKG = createBookmark(BookmarkConstants.RECOMMENDED_PACKAGE,
						lookupDao.decode(ServiceConstants.CSVCCODE, servicePackageFormDto.getServicePackageDtlDto().getSvcPkgCd()));
				bookmarkNonFrmGrpList.add(bookmarkRecommendPKG);
			}

		}


		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		preFillData.setFormDataGroupList(formDataGroupList);
		return preFillData;
	}

}
