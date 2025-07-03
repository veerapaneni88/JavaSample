package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.investigation.dto.LicensingInvReportDto;
import us.tx.state.dfps.service.investigation.dto.LicensingInvstDtlDto;
import us.tx.state.dfps.service.person.dto.AllegationWithVicDto;
import us.tx.state.dfps.service.person.dto.PersonListAlleDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.common.dto.AgencyHomeInfoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Generates
 * prefill string used to populate form CFIV5000 Apr 16, 2018- 9:26:03 AM © 2017
 * Texas Department of Family and Protective Services
 */
@Component
public class LicensingInvReportPrefillData extends DocumentServiceUtil {

	
	
	
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		LicensingInvReportDto prefillDto = (LicensingInvReportDto) parentDtoobj;
		
		boolean isAbuse=false;
		boolean isNeglect=false;
		boolean isRuledOut=false;
		
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		if (ObjectUtils.isEmpty(prefillDto.getPersonListAlleDtoList())) {
			prefillDto.setPersonListAlleDtoList(new ArrayList<PersonListAlleDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getPersonAllVictimAllegationList())) {
			prefillDto.setPersonAllVictimAllegationList(new ArrayList<PersonListAlleDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getVctmPrepetratorAllegationDtl())) {
			prefillDto.setVctmPrepetratorAllegationDtl(new ArrayList<AllegationWithVicDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getEmployeePersPhNameDto())) {
			prefillDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto())) {
			prefillDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getLicensingInvstDtlDto())) {
			prefillDto.setLicensingInvstDtlDto(new LicensingInvstDtlDto());
		}
		// Non-group bookmarks
		List<BookmarkDto> bookmarkNonGroupList = new ArrayList<BookmarkDto>();

		bookmarkNonGroupList.add(createBookmark(BookmarkConstants.CLASS_OPERATION_NUMBER, new StringBuilder()
				.append(!ObjectUtils.isEmpty(prefillDto.getLicensingInvstDtlDto().getNbrAcclaim()) ? prefillDto.getLicensingInvstDtlDto().getNbrAcclaim() + FormConstants.SPACE : FormConstants.EMPTY_STRING)
				.append(!ObjectUtils.isEmpty(prefillDto.getLicensingInvstDtlDto().getNbrAgency()) ? prefillDto.getLicensingInvstDtlDto().getNbrAgency() + FormConstants.SPACE : FormConstants.EMPTY_STRING)
				.append(!ObjectUtils.isEmpty(prefillDto.getLicensingInvstDtlDto().getNbrBranch()) ? prefillDto.getLicensingInvstDtlDto().getNbrBranch() : FormConstants.EMPTY_STRING).toString()));

		bookmarkNonGroupList.add(createBookmark(BookmarkConstants.CLASS_OPERATION_NAME,
				prefillDto.getLicensingInvstDtlDto().getNmResource()));

		// CSEC02D
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.CASE_NAME,
				prefillDto.getGenericCaseInfoDto().getNmCase());
		bookmarkNonGroupList.add(bookmarkNmCase);
		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.CASE_ID,
				prefillDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonGroupList.add(bookmarkIdCase);

		// CSEC01D
		BookmarkDto bookmarkNmCaseworkerFirst = createBookmark(BookmarkConstants.NM_CASEWORKER_FIRST,
				prefillDto.getEmployeePersPhNameDto().getNmNameFirst());
		bookmarkNonGroupList.add(bookmarkNmCaseworkerFirst);
		BookmarkDto bookmarkNmCaseworkerLast = createBookmark(BookmarkConstants.NM_CASEWORKER_LAST,
				prefillDto.getEmployeePersPhNameDto().getNmNameLast());
		bookmarkNonGroupList.add(bookmarkNmCaseworkerLast);
		BookmarkDto bookmarkNmCaseworkerMiddle = createBookmark(BookmarkConstants.NM_CASEWORKER_MIDDLE,
				prefillDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bookmarkNonGroupList.add(bookmarkNmCaseworkerMiddle);

		// parent group cfiv5009
		for (PersonListAlleDto vicitimAllegationHist : prefillDto.getPersonAllVictimAllegationList()) {
			FormDataGroupDto victimGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_VICTIM,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> victimGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkVictimList = new ArrayList<BookmarkDto>();
			if (!ObjectUtils.isEmpty(vicitimAllegationHist.getCdPersonSuffix())) {
				BookmarkDto bookmarkVicNmSuffix = createBookmarkWithCodesTable(BookmarkConstants.NM_VICTIM_SUFFIX,
						vicitimAllegationHist.getCdPersonSuffix(), CodesConstant.CSUFFIX);
				bookmarkVictimList.add(bookmarkVicNmSuffix);
			}
			if (!ObjectUtils.isEmpty(vicitimAllegationHist.getNmPersonFirst())) {
				BookmarkDto bookmarkVicNmFirst = createBookmark(BookmarkConstants.NM_VICTIM_FIRST,
						vicitimAllegationHist.getNmPersonFirst());
				bookmarkVictimList.add(bookmarkVicNmFirst);
			}
			if (!ObjectUtils.isEmpty(vicitimAllegationHist.getNmPersonLast())) {
				BookmarkDto bookmarkVicNmLast = createBookmark(BookmarkConstants.NM_VICTIM_LAST,
						vicitimAllegationHist.getNmPersonLast());
				bookmarkVictimList.add(bookmarkVicNmLast);
			}
			if (!ObjectUtils.isEmpty(vicitimAllegationHist.getNmPersonMiddle())) {
				BookmarkDto bookmarkVicNmMiddle = createBookmark(BookmarkConstants.NM_VICTIM_MIDDLE,
						vicitimAllegationHist.getNmPersonMiddle());
				bookmarkVictimList.add(bookmarkVicNmMiddle);
			}

			// sub group cfiv5004
			if (!ObjectUtils.isEmpty(vicitimAllegationHist.getCdPersonSuffix())) {
				FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_1,
						FormGroupsConstants.TMPLAT_NM_VICTIM);
				victimGroupList.add(commaGroupDto);
			}
			victimGroupDto.setBookmarkDtoList(bookmarkVictimList);
			victimGroupDto.setFormDataGroupList(victimGroupList);
			formDataGroupList.add(victimGroupDto);
		}
		// parent group cfiv5002
		for (PersonListAlleDto personListAlleDto : prefillDto.getPersonListAlleDtoList()) {
			FormDataGroupDto allegPerpGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_AP,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> allegPerpGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkAllegPerpList = new ArrayList<BookmarkDto>();
			if (!ObjectUtils.isEmpty(personListAlleDto.getCdPersonSuffix())) {
				BookmarkDto bookmarkApNmSuffix = createBookmarkWithCodesTable(BookmarkConstants.NM_AP_SUFFIX,
						personListAlleDto.getCdPersonSuffix(), CodesConstant.CSUFFIX);
				bookmarkAllegPerpList.add(bookmarkApNmSuffix);
			}
			if (!ObjectUtils.isEmpty(personListAlleDto.getNmPersonFirst())) {
				BookmarkDto bookmarkApNmFirst = createBookmark(BookmarkConstants.NM_AP_FIRST,
						personListAlleDto.getNmPersonFirst());
				bookmarkAllegPerpList.add(bookmarkApNmFirst);
			}
			if (!ObjectUtils.isEmpty(personListAlleDto.getNmPersonLast())) {
				BookmarkDto bookmarkApNmLast = createBookmark(BookmarkConstants.NM_AP_LAST,
						personListAlleDto.getNmPersonLast());
				bookmarkAllegPerpList.add(bookmarkApNmLast);
			}
			if (!ObjectUtils.isEmpty(personListAlleDto.getNmPersonMiddle())) {
				BookmarkDto bookmarkApNmMiddle = createBookmark(BookmarkConstants.NM_AP_MIDDLE,
						personListAlleDto.getNmPersonMiddle());
				bookmarkAllegPerpList.add(bookmarkApNmMiddle);
			}
			// sub group cfiv5003
			if (!ObjectUtils.isEmpty(personListAlleDto.getCdPersonSuffix())) {
				FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_2,
						FormGroupsConstants.TMPLAT_AP);
				allegPerpGroupList.add(commaGroupDto);
			}

			allegPerpGroupDto.setBookmarkDtoList(bookmarkAllegPerpList);
			allegPerpGroupDto.setFormDataGroupList(allegPerpGroupList);
			formDataGroupList.add(allegPerpGroupDto);
		}

		// parent group cfiv5001
		for (AllegationWithVicDto vctmPrepetratorAllegationDto : prefillDto.getVctmPrepetratorAllegationDtl()) {
			if (!ObjectUtils.isEmpty(vctmPrepetratorAllegationDto.getaCdAllegDisposition())) {
				FormDataGroupDto allegGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> allegGroupList = new ArrayList<FormDataGroupDto>();
				List<BookmarkDto> bookmarkAllegList = new ArrayList<BookmarkDto>();
				if (!ObjectUtils.isEmpty(vctmPrepetratorAllegationDto.getaCdAllegDisposition())) {
					BookmarkDto bookmarkAllegDisp = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DISP,
							vctmPrepetratorAllegationDto.getaCdAllegDisposition(), CodesConstant.CDISPSTN);
					bookmarkAllegList.add(bookmarkAllegDisp);
				}
				BookmarkDto bookmarkAllegType = createBookmark(BookmarkConstants.ALLEG_TYPE,
						vctmPrepetratorAllegationDto.getaCdAllegType());
				bookmarkAllegList.add(bookmarkAllegType);
				BookmarkDto bookmarkAllegFatality = createBookmark(BookmarkConstants.ALLEG_CHLD_FATALITY,
						vctmPrepetratorAllegationDto.getaIndFatality());
				bookmarkAllegList.add(bookmarkAllegFatality);
				BookmarkDto bookmarkApSuffix = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_AP_SUFFIX,
						vctmPrepetratorAllegationDto.getcCdPersonSuffix(), CodesConstant.CSUFFIX);
				bookmarkAllegList.add(bookmarkApSuffix);
				BookmarkDto bookmarkVicSuffix = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_VIC_SUFFIX,
						vctmPrepetratorAllegationDto.getbCdPersonSuffix(), CodesConstant.CSUFFIX);
				bookmarkAllegList.add(bookmarkVicSuffix);
				BookmarkDto bookmarkApFirst = createBookmark(BookmarkConstants.ALLEG_AP_FIRST,
						vctmPrepetratorAllegationDto.getcNmPersonFirst());
				bookmarkAllegList.add(bookmarkApFirst);
				BookmarkDto bookmarkVicFirst = createBookmark(BookmarkConstants.ALLEG_VIC_FIRST,
						vctmPrepetratorAllegationDto.getbNmPersonFirst());
				bookmarkAllegList.add(bookmarkVicFirst);
				BookmarkDto bookmarkApLast = createBookmark(BookmarkConstants.ALLEG_AP_LAST,
						vctmPrepetratorAllegationDto.getcNmPersonLast());
				bookmarkAllegList.add(bookmarkApLast);
				BookmarkDto bookmarkVicLast = createBookmark(BookmarkConstants.ALLEG_VIC_LAST,
						vctmPrepetratorAllegationDto.getbNmPersonLast());
				bookmarkAllegList.add(bookmarkVicLast);
				BookmarkDto bookmarkApMiddle = createBookmark(BookmarkConstants.ALLEG_AP_MIDDLE,
						vctmPrepetratorAllegationDto.getcNmPersonMiddle());
				bookmarkAllegList.add(bookmarkApMiddle);
				BookmarkDto bookmarkVicMiddle = createBookmark(BookmarkConstants.ALLEG_VIC_MIDDLE,
						vctmPrepetratorAllegationDto.getbNmPersonMiddle());
				bookmarkAllegList.add(bookmarkVicMiddle);

				// sub group cfiv5010
				if (StringUtils.isNotBlank(vctmPrepetratorAllegationDto.getbCdPersonSuffix())) {
					FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_3,
							FormGroupsConstants.TMPLAT_ALLEGATION);
					allegGroupList.add(commaGroupDto);
				}

				// sub group cfiv5030
				if (ObjectUtils.isEmpty(vctmPrepetratorAllegationDto.getbDtPersonDeath()) || ServiceConstants.MAX_DATE
						.toString().equals(vctmPrepetratorAllegationDto.getbDtPersonDeath().toString())) {
					FormDataGroupDto noFatalityGroupDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_ALG_CHILD_FATALITY_WITHOUT_DOD,
							FormGroupsConstants.TMPLAT_ALLEGATION);
					allegGroupList.add(noFatalityGroupDto);
				}

				// sub group cfiv5020
				if (!ObjectUtils.isEmpty(vctmPrepetratorAllegationDto.getbDtPersonDeath())) {
					FormDataGroupDto fatalityGroupDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_ALG_CHILD_FATALITY_WITH_DOD,
							FormGroupsConstants.TMPLAT_ALLEGATION);
					List<BookmarkDto> bookmarkFatalityList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkFatality = createBookmark(BookmarkConstants.ALG_CHILD_FATALITY,
							vctmPrepetratorAllegationDto.getaIndFatality());
					bookmarkFatalityList.add(bookmarkFatality);
					fatalityGroupDto.setBookmarkDtoList(bookmarkFatalityList);
					allegGroupList.add(fatalityGroupDto);
				}

				// sub group cfiv5011
				if (StringUtils.isNotBlank(vctmPrepetratorAllegationDto.getcCdPersonSuffix())) {
					FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_4,
							FormGroupsConstants.TMPLAT_ALLEGATION);
					allegGroupList.add(commaGroupDto);
				}

				allegGroupDto.setBookmarkDtoList(bookmarkAllegList);
				allegGroupDto.setFormDataGroupList(allegGroupList);
				formDataGroupList.add(allegGroupDto);
			}

			// parent group cfiv5006		
			
			// Neglect Allegation
			if (CodesConstant.CABALTYP_NEGL.equals(vctmPrepetratorAllegationDto.getaCdAllegType())
					|| CodesConstant.CABALTYP_PHNG.equals(vctmPrepetratorAllegationDto.getaCdAllegType())
					|| CodesConstant.CABALTYP_NSUP.equals(vctmPrepetratorAllegationDto.getaCdAllegType())
					|| CodesConstant.CABALTYP_MDNG.equals(vctmPrepetratorAllegationDto.getaCdAllegType())
					|| CodesConstant.CABALTYP_MHNG.equals(vctmPrepetratorAllegationDto.getaCdAllegType())) 			
				
			{
				
				if (!CodesConstant.CDISPSTN_ADM.equals(vctmPrepetratorAllegationDto.getaCdAllegDisposition())
						&& !CodesConstant.CDISPSTN_UTD.equals(vctmPrepetratorAllegationDto.getaCdAllegDisposition())
						&& !CodesConstant.CDISPSTN_RO.equals(vctmPrepetratorAllegationDto.getaCdAllegDisposition()))
				{
					isNeglect=true;
				}
				
			}
			
			// Abuse Allegation
			if(CodesConstant.CAPSALLG_PHAB.equals(vctmPrepetratorAllegationDto.getaCdAllegType())
					|| CodesConstant.CAPSALLG_SXAB.equals(vctmPrepetratorAllegationDto.getaCdAllegType())
					|| CodesConstant.CABALTYP_EMAB.equals(vctmPrepetratorAllegationDto.getaCdAllegType())) 
			{
				
				if (!CodesConstant.CDISPSTN_ADM.equals(vctmPrepetratorAllegationDto.getaCdAllegDisposition())
					&& !CodesConstant.CDISPSTN_UTD.equals(vctmPrepetratorAllegationDto.getaCdAllegDisposition())
					&& !CodesConstant.CDISPSTN_RO.equals(vctmPrepetratorAllegationDto.getaCdAllegDisposition()))
			{
				isAbuse=true;
			} 
			}
			
			// R/O,ADM,UTD disposition 
			if (CodesConstant.CDISPSTN_ADM.equals(vctmPrepetratorAllegationDto.getaCdAllegDisposition())
					|| CodesConstant.CDISPSTN_UTD.equals(vctmPrepetratorAllegationDto.getaCdAllegDisposition())
					|| CodesConstant.CDISPSTN_RO.equals(vctmPrepetratorAllegationDto.getaCdAllegDisposition()))
			{
				isRuledOut=true;
			}		
		}
		
		if(isAbuse)
		{
		FormDataGroupDto abuseGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ABUSE,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkAbuseList = new ArrayList<BookmarkDto>();
		/*BookmarkDto bookmarkIdAlleg = createBookmark(BookmarkConstants.UE_GROUPID,
				vctmPrepetratorAllegationDto.getaIdAllegation());
		bookmarkAbuseList.add(bookmarkIdAlleg); 
		abuseGroupDto.setBookmarkDtoList(bookmarkAbuseList); */
		formDataGroupList.add(abuseGroupDto);
		}

		if(isNeglect)
		{
		FormDataGroupDto neglectGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NEGLECT,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkNeglectList = new ArrayList<BookmarkDto>();
		/*BookmarkDto bookmarkIdVic = createBookmark(BookmarkConstants.UE_GROUPID,
				vctmPrepetratorAllegationDto.getaIdVictivm());
		bookmarkNeglectList.add(bookmarkIdVic);
		neglectGroupDto.setBookmarkDtoList(bookmarkNeglectList); */
		formDataGroupList.add(neglectGroupDto);
		}
		
		if(isRuledOut)
		{
		FormDataGroupDto admGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RO_UTD_AC,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkAdmList = new ArrayList<BookmarkDto>();
		/*BookmarkDto bookmarkIdAp = createBookmark(BookmarkConstants.UE_GROUPID,
				vctmPrepetratorAllegationDto.getaIdAllegedPerpetrator());
		bookmarkAdmList.add(bookmarkIdAp);
		admGroupDto.setBookmarkDtoList(bookmarkAdmList); */
		formDataGroupList.add(admGroupDto);
		}
		
		
		// Populate prefill object
		PreFillDataServiceDto prefillData = new PreFillDataServiceDto();
		prefillData.setBookmarkDtoList(bookmarkNonGroupList);
		prefillData.setFormDataGroupList(formDataGroupList);
		return prefillData;

	}

}
