package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.notiftolawenforcement.dto.FacilInvDtlDto;
import us.tx.state.dfps.notiftolawenforcement.dto.MultiAddressDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.sslccommchklst.dto.SslcCommChklstDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Creates
 * prefill string to populate form Mar 16, 2018- 11:00:01 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Component
public class SslcCommChklstPrefillData extends DocumentServiceUtil {

	/**
	 * Method Description: This method is used to prefill the data from the
	 * different Dao by passing Dao output Dtos and bookmark and form group
	 * bookmark Dto as objects as input request
	 * 
	 * @param parentDtoobj
	 * @param bookmarkDtoObj
	 * @return PreFillData
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		SslcCommChklstDto prefillDto = (SslcCommChklstDto) parentDtoobj;
		// initialize empty dtos if null
		if (ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto())) {
			prefillDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}

		if (ObjectUtils.isEmpty(prefillDto.getFacilInvDtlDto())) {
			prefillDto.setFacilInvDtlDto(new FacilInvDtlDto());
		}

		List<BookmarkDto> orphanBookmarks = new ArrayList<BookmarkDto>();
		
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// Group-less bookmarks
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.NM_CASE,
				prefillDto.getGenericCaseInfoDto().getNmCase());
		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.ID_CASE,
				prefillDto.getGenericCaseInfoDto().getIdCase());
		orphanBookmarks.add(bookmarkIdCase);
		orphanBookmarks.add(bookmarkNmCase);

		// for form cfiv2300
		if (prefillDto.getIsApsReferral()) {
			BookmarkDto bookmarkDtIntake = createBookmark(BookmarkConstants.DT_INTAKE,
					DateUtils.stringDt(prefillDto.getFacilInvDtlDto().getDtFacilInvstIntake()));
			BookmarkDto bookmarkTmIntake = createBookmark(BookmarkConstants.TM_INTAKE,
					DateUtils.getTime(prefillDto.getFacilInvDtlDto().getDtFacilInvstIntake()));
			orphanBookmarks.add(bookmarkDtIntake);
			orphanBookmarks.add(bookmarkTmIntake);
		}

		
	
		
		
		
		// for form cfiv4300
		if (ServiceConstants.ADULT_PROTECTIVE_SERVICES.equalsIgnoreCase(prefillDto.getApsRefForm())) {
			BookmarkDto bookmarkDtIntake = createBookmark(BookmarkConstants.DT_INTAKE,
					DateUtils.stringDt(prefillDto.getFacilInvDtlDto().getDtFacilInvstIntake()));
			BookmarkDto bookmarkTmIntake = createBookmark(BookmarkConstants.TM_INTAKE,
					DateUtils.getTime(prefillDto.getFacilInvDtlDto().getDtFacilInvstIntake()));			
			BookmarkDto bookmarkMHMRCode = createBookmark(BookmarkConstants.MHMR_CODE,
					prefillDto.getFacilInvDtlDto().getTxtFacilInvstComments());
			orphanBookmarks.add(bookmarkDtIntake);
			orphanBookmarks.add(bookmarkTmIntake);			
			orphanBookmarks.add(bookmarkMHMRCode);
			
			if(!ObjectUtils.isEmpty(prefillDto.MultiAddressDto()))
			{
			for(MultiAddressDto multiAddressDto:prefillDto.MultiAddressDto())
			{
			FormDataGroupDto tempFacilityFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FACILITY,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkProviderNameList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarFacilityName = createBookmark(BookmarkConstants.FACILITY_NAME,
					multiAddressDto.getaCpNmResource());
			bookmarkProviderNameList.add(bookmarFacilityName);
			tempFacilityFrmDataGrpDto.setBookmarkDtoList(bookmarkProviderNameList);	
			formDataGroupList.add(tempFacilityFrmDataGrpDto);
			
			}	
			}
			
			
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(orphanBookmarks);
		preFillData.setFormDataGroupList(formDataGroupList);		

		return preFillData;
	}

}
