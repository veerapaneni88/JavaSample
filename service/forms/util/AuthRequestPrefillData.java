/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jan 23, 2018- 10:47:20 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.dto.SubcareLOCFormDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;
import us.tx.state.dfps.service.person.dto.PersonPhoneDto;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.dto.PersonLocPersonOutDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 23, 2018- 10:47:20 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class AuthRequestPrefillData extends DocumentServiceUtil {

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
		SubcareLOCFormDto subcareLOCFormDto = (SubcareLOCFormDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		if (null == subcareLOCFormDto.getPersonDto()) {
			subcareLOCFormDto.setPersonDto(new PersonDto());
		}
		if (null == subcareLOCFormDto.getStageCaseDtlDto()) {
			subcareLOCFormDto.setStageCaseDtlDto(new StageCaseDtlDto());
		}
		if (null == subcareLOCFormDto.getEmployeePersPhNameDto()) {
			subcareLOCFormDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (null == subcareLOCFormDto.getPersonPhoneDto()) {
			subcareLOCFormDto.setPersonPhoneDto(new PersonPhoneDto());
		}
		if (null == subcareLOCFormDto.getStagePersonDto()) {
			subcareLOCFormDto.setStagePersonDto(new StagePersonDto());
		}
		if (null == subcareLOCFormDto.getPersonLocDto()) {
			subcareLOCFormDto.setPersonLocDto(new PersonLocDto());
		}
		if (null == subcareLOCFormDto.getPersonLocPersonOutDto()) {
			subcareLOCFormDto.setPersonLocPersonOutDto(new PersonLocPersonOutDto());
		}
		if (null == subcareLOCFormDto.getPersonDto()) {
			subcareLOCFormDto.setPersonDto(new PersonDto());
		}
		for (LegalStatusPersonMaxStatusDtOutDto Legal : subcareLOCFormDto.getLegalStatusPersonMaxList()) {
			if (null == Legal) {
				List<LegalStatusPersonMaxStatusDtOutDto> legal2 = new ArrayList<LegalStatusPersonMaxStatusDtOutDto>();
				subcareLOCFormDto.setLegalStatusPersonMaxList(legal2);
			}
		}
		for (PlacementActPlannedOutDto place : subcareLOCFormDto.getPlacementActPlannedList()) {
			if (null == place) {
				List<PlacementActPlannedOutDto> place2 = new ArrayList<PlacementActPlannedOutDto>();
				subcareLOCFormDto.setPlacementActPlannedList(place2);
			}
		}

		/**
		 * If equals set the prefill data for group cfa2005 and cfa2003
		 */
		if (FormConstants.EMPTY_STRING != subcareLOCFormDto.getPersonDto().getCdPersonSuffix()
				&& FormConstants.EMPTY_STRING != subcareLOCFormDto.getEmployeePersPhNameDto().getCdNameSuffix()) {
			FormDataGroupDto tempChildCommaDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			FormDataGroupDto tempChildCommaDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_1,
					FormConstants.EMPTY_STRING);
			if (!ObjectUtils.isEmpty(subcareLOCFormDto.getPersonDto().getCdPersonSuffix())) {
				formDataGroupList.add(tempChildCommaDto);
			}
			if (!ObjectUtils.isEmpty(subcareLOCFormDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
				formDataGroupList.add(tempChildCommaDto2);
			}

		}

		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		BookmarkDto bookmarkReqDate = createBookmark(BookmarkConstants.REQUEST_DATE,
				DateUtils.stringDt(subcareLOCFormDto.getStageCaseDtlDto().getCurrentDate()));
		bookmarkNonFrmGrpList.add(bookmarkReqDate);

		BookmarkDto bookmarkTitleCaseNm = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				subcareLOCFormDto.getStageCaseDtlDto().getNmCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNm);

		BookmarkDto bookmarkTitleCaseNbr = createBookmark(BookmarkConstants.TITLE_CASE_ID,
				subcareLOCFormDto.getStageCaseDtlDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNbr);

		BookmarkDto bookmarkWorkerPhnNbr = createBookmark(BookmarkConstants.WORKER_PHONE_NBR,
				subcareLOCFormDto.getEmployeePersPhNameDto().getNbrPhone());
		bookmarkNonFrmGrpList.add(bookmarkWorkerPhnNbr);

		BookmarkDto bookmarkWorkerPhnExt = createBookmark(BookmarkConstants.WORKER_PHONE_EXT,
				subcareLOCFormDto.getEmployeePersPhNameDto().getNbrMailCodePhoneExt());
		bookmarkNonFrmGrpList.add(bookmarkWorkerPhnExt);

		BookmarkDto bookmarkWorkerMailCd = createBookmark(BookmarkConstants.WORKER_MAIL_CODE,
				subcareLOCFormDto.getEmployeePersPhNameDto().getCdOfficeMail());
		bookmarkNonFrmGrpList.add(bookmarkWorkerMailCd);

		BookmarkDto bookmarkWorkerMailCdCity = createBookmark(BookmarkConstants.WORKER_CITY,
				subcareLOCFormDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
		bookmarkNonFrmGrpList.add(bookmarkWorkerMailCdCity);

		if (!ObjectUtils.isEmpty(subcareLOCFormDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
			BookmarkDto bookmarkWorkerSuff = createBookmark(BookmarkConstants.WORKER_SUFFIX, lookupDao
					.decode(ServiceConstants.CSUFFIX2, subcareLOCFormDto.getEmployeePersPhNameDto().getCdNameSuffix()));
			bookmarkNonFrmGrpList.add(bookmarkWorkerSuff);
		}

		BookmarkDto bookmarkWorkerFst = createBookmark(BookmarkConstants.WORKER_FIRST,
				subcareLOCFormDto.getEmployeePersPhNameDto().getNmNameFirst());
		bookmarkNonFrmGrpList.add(bookmarkWorkerFst);

		BookmarkDto bookmarkWorkerMid = createBookmark(BookmarkConstants.WORKER_MIDDLE,
				subcareLOCFormDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(bookmarkWorkerMid);

		BookmarkDto bookmarkWorkerLst = createBookmark(BookmarkConstants.WORKER_LAST,
				subcareLOCFormDto.getEmployeePersPhNameDto().getNmNameLast());
		bookmarkNonFrmGrpList.add(bookmarkWorkerLst);

		BookmarkDto bookmarkReqLOC = createBookmark(BookmarkConstants.REQUEST_LOC, lookupDao
				.decode(ServiceConstants.CLOCFORM, subcareLOCFormDto.getPersonLocPersonOutDto().getCdPLOCChild()));
		bookmarkNonFrmGrpList.add(bookmarkReqLOC);

		BookmarkDto bookmarkWorkerfaxNbr = createBookmark(BookmarkConstants.WORKER_FAX_NBR,
				subcareLOCFormDto.getPersonPhoneDto().getIdPersonPhone());
		bookmarkNonFrmGrpList.add(bookmarkWorkerfaxNbr);
		LegalStatusPersonMaxStatusDtOutDto legalStatusPersonMaxStatusDtOutDto=null;
		if(CollectionUtils.isEmpty(subcareLOCFormDto.getLegalStatusPersonMaxList())) {
			legalStatusPersonMaxStatusDtOutDto=new LegalStatusPersonMaxStatusDtOutDto();
		}
		else {
			 legalStatusPersonMaxStatusDtOutDto=subcareLOCFormDto.getLegalStatusPersonMaxList().get(0);
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
				lookupDao.decode(ServiceConstants.CATHPLOC, subcareLOCFormDto.getPersonLocDto().getCdPlocChild()));
		bookmarkNonFrmGrpList.add(bookmarkChildCurrLoc);

		PlacementActPlannedOutDto placementActPlannedOutDto=null;
		if(CollectionUtils.isEmpty(subcareLOCFormDto.getPlacementActPlannedList())) {
			placementActPlannedOutDto=new PlacementActPlannedOutDto();
		}
		else {
			placementActPlannedOutDto=subcareLOCFormDto.getPlacementActPlannedList().get(0);
		}
		BookmarkDto bookmarkFaclityNm = createBookmark(BookmarkConstants.FACILITY_NAME,
				placementActPlannedOutDto.getNmPlcmtFacil());
		bookmarkNonFrmGrpList.add(bookmarkFaclityNm);

		Long idFacilNumber = placementActPlannedOutDto.getIdRsrcFacil();
		idFacilNumber = ObjectUtils.isEmpty(idFacilNumber) ? 0l : idFacilNumber;
		BookmarkDto bookmarkFacilityNbr = createBookmark(BookmarkConstants.FACILITY_NBR, idFacilNumber);
		bookmarkNonFrmGrpList.add(bookmarkFacilityNbr);

		BookmarkDto bookmarkChildSex = createBookmark(BookmarkConstants.CHILD_SEX,
				subcareLOCFormDto.getPersonDto().getCdPersonSex());
		bookmarkNonFrmGrpList.add(bookmarkChildSex);

		SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd-yyyy");
		String dateString2 = "";
		dateString2 = sdf2.format(subcareLOCFormDto.getPersonDto().getDtPersonBirth());
		BookmarkDto bookmarkTitleChildDOB = createBookmark(BookmarkConstants.TITLE_CHILD_DOB, dateString2);
		bookmarkNonFrmGrpList.add(bookmarkTitleChildDOB);

		if (!ObjectUtils.isEmpty(subcareLOCFormDto.getPersonDto().getCdPersonSuffix())) {
			BookmarkDto bookmarkTitleChildSuff = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_SUFFIX,
					lookupDao.decode(ServiceConstants.CSUFFIX2, subcareLOCFormDto.getPersonDto().getCdPersonSuffix()));
			bookmarkNonFrmGrpList.add(bookmarkTitleChildSuff);
		}
		BookmarkDto bookmarkTitleChildEthn = createBookmark(BookmarkConstants.CHILD_ETHNICITY,
				lookupDao.decode(ServiceConstants.CETHNIC, subcareLOCFormDto.getPersonDto().getCdPersonEthnicGroup()));
		bookmarkNonFrmGrpList.add(bookmarkTitleChildEthn);

		BookmarkDto bookmarkTitleChildNmFst = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_FIRST,
				subcareLOCFormDto.getPersonDto().getNmPersonFirst());
		bookmarkNonFrmGrpList.add(bookmarkTitleChildNmFst);

		BookmarkDto bookmarkTitleChildNmMid = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_MIDDLE,
				subcareLOCFormDto.getPersonDto().getNmPersonMiddle());
		bookmarkNonFrmGrpList.add(bookmarkTitleChildNmMid);

		BookmarkDto bookmarkTitleChildNmLst = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_LAST,
				subcareLOCFormDto.getPersonDto().getNmPersonLast());
		bookmarkNonFrmGrpList.add(bookmarkTitleChildNmLst);

		BookmarkDto bookmarkChildPrsnId = createBookmark(BookmarkConstants.CHILD_PERSON_ID,
				subcareLOCFormDto.getPersonDto().getIdPerson());
		bookmarkNonFrmGrpList.add(bookmarkChildPrsnId);

		if (!ObjectUtils.isEmpty(subcareLOCFormDto.getPersonIdDto())) {
			if (!ObjectUtils.isEmpty(subcareLOCFormDto.getPersonIdDto().getPersonIdNumber())) {
				BookmarkDto bookmarkMedcdNbr = createBookmark(BookmarkConstants.CHILD_MEDICAID_NBR,
						subcareLOCFormDto.getPersonIdDto().getPersonIdNumber());
				bookmarkNonFrmGrpList.add(bookmarkMedcdNbr);
			}
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		preFillData.setFormDataGroupList(formDataGroupList);
		return preFillData;
	}

}
