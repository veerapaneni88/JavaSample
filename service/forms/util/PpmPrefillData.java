package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PpmDto;
import us.tx.state.dfps.service.forms.dto.PptDetailsOutDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dto.AddressDto;
import us.tx.state.dfps.service.person.dto.PPTParticipantDto;
import us.tx.state.dfps.service.person.dto.PersonGenderSpanishDto;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Populates
 * prefill data for form CSC36O00 Feb 20, 2018- 10:20:01 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Component
public class PpmPrefillData extends DocumentServiceUtil {

	public static final String COMMA_SPACE_STRING = ", ";
	public static final char SPACE_STRING = ' ';

	@Autowired
	LookupDao lookupDao;

	/**
	 * Method Description: This method is used to prefill the data from the
	 * different Dao by passing Dao output Dtos and bookmark and form group
	 * bookmark Dto as objects as input request
	 * 
	 * @param parentDtoobj
	 * @return PreFillData
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		PpmDto ppmDto = (PpmDto) parentDtoobj;

		// initialize null dtos
		if (TypeConvUtil.isNullOrEmpty(ppmDto.getStagePersonLinkCase())) {
			ppmDto.setStagePersonLinkCase(new StagePersonLinkCaseDto());
		}
		if (TypeConvUtil.isNullOrEmpty(ppmDto.getNameDetail())) {
			ppmDto.setNameDetail(new NameDetailDto());
		}
		if (TypeConvUtil.isNullOrEmpty(ppmDto.getCodesTables())) {
			ppmDto.setCodesTables(new CodesTablesDto());
		}
		if (TypeConvUtil.isNullOrEmpty(ppmDto.getpPtDetailsOut())) {
			ppmDto.setpPtDetailsOut(new PptDetailsOutDto());
		}
		if (TypeConvUtil.isNullOrEmpty(ppmDto.getpPTParticipant())) {
			ppmDto.setpPTParticipant(new PPTParticipantDto());
		}
		if (TypeConvUtil.isNullOrEmpty(ppmDto.getPersonGenderSpanish())) {
			ppmDto.setPersonGenderSpanish(new PersonGenderSpanishDto());
		}
		if (TypeConvUtil.isNullOrEmpty(ppmDto.getPartsNameDetail())) {
			ppmDto.setPartsNameDetail(new NameDetailDto());
		}
		if (TypeConvUtil.isNullOrEmpty(ppmDto.getAddressList())) {
			ppmDto.setAddressList(new ArrayList<AddressDto>());
		}
		if (TypeConvUtil.isNullOrEmpty(ppmDto.getStagePerson())) {
			ppmDto.setStagePerson(new StagePersonDto());
		}
		if (TypeConvUtil.isNullOrEmpty(ppmDto.getEmployeePersPhName())) {
			ppmDto.setEmployeePersPhName(new EmployeePersPhNameDto());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// parent group csc06o01
		if(!ObjectUtils.isEmpty(ppmDto) && !ObjectUtils.isEmpty(ppmDto.getAddressList())){
		//for (AddressDto addressDto : ppmDto.getAddressList()) {
			AddressDto addressDto = ppmDto.getAddressList().get(ServiceConstants.Zero);
			if (ServiceConstants.Y.equalsIgnoreCase(addressDto.getIndPersAddrLinkPrimary())
					&& ServiceConstants.N.equalsIgnoreCase(addressDto.getIndPersAddrLinkInvalid())
					&& (!TypeConvUtil.isNullOrEmpty(addressDto.getDtPersAddrLinkEnd())
							|| addressDto.getDtPersAddrLinkEnd().compareTo(new Date(86560635600000L)) == 0)) {
				FormDataGroupDto addressGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDRESS,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkAddressList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkZip = createBookmark(BookmarkConstants.DP_ZIP, addressDto.getAddrZip());
				BookmarkDto bookmarkCity = createBookmark(BookmarkConstants.DP_CITY, addressDto.getAddrCity());
				BookmarkDto bookmarkStreet1 = createBookmark(BookmarkConstants.DP_STREET1,
						addressDto.getAddrPersAddrStLn1());
				BookmarkDto bookmarkState = createBookmark(BookmarkConstants.DP_STATE, addressDto.getCdAddrState());
				bookmarkAddressList.add(bookmarkState);
				bookmarkAddressList.add(bookmarkStreet1);
				bookmarkAddressList.add(bookmarkCity);
				bookmarkAddressList.add(bookmarkZip);
				addressGroupDto.setBookmarkDtoList(bookmarkAddressList);

				// sub group cfzz1701
				if (!TypeConvUtil.isNullOrEmpty(addressDto.getAddrPersAddrStLn2())
						&& (TypeConvUtil.isNullOrEmpty(addressDto.getDtPersAddrLinkEnd())
								|| addressDto.getDtPersAddrLinkEnd().compareTo(new Date(86560635600000L)) == 0)) {
					List<FormDataGroupDto> addressGroupDtoList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto addressLn2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_STREET2,
							FormGroupsConstants.TMPLAT_ADDRESS);
					List<BookmarkDto> bookmarkStreet2List = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkStreet2 = createBookmark(BookmarkConstants.STREET_LN_2,
							addressDto.getAddrPersAddrStLn2());
					bookmarkStreet2List.add(bookmarkStreet2);
					addressLn2GroupDto.setBookmarkDtoList(bookmarkStreet2List);

					addressGroupDto.setFormDataGroupList(addressGroupDtoList);
				}

				formDataGroupList.add(addressGroupDto);
			}
		}

		// parent group csc3aa
		if (ServiceConstants.UNDER_SIXTEEN.equalsIgnoreCase(ppmDto.getStagePersonLinkCase().getCdPersonDeath())) {
			FormDataGroupDto pcUnder16GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PC_UNDER_16,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(pcUnder16GroupDto);
		}

		// parent group csc36ab
		else if (ServiceConstants.SIXTEEN_AND_OVER
				.equalsIgnoreCase(ppmDto.getStagePersonLinkCase().getCdPersonDeath())) {
			FormDataGroupDto pcOver16GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PC_16_OR_OLDER,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(pcOver16GroupDto);
		}

		// parent group cfzco50
		if (!TypeConvUtil.isNullOrEmpty(ppmDto.getEmployeePersPhName().getCdNameSuffix())) {
			FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(commaGroupDto);
		}

		// parent group csc36ac
		if (!TypeConvUtil.isNullOrEmpty(ppmDto.getEmployeePersPhName().getMailCodePhoneExt())) {
			FormDataGroupDto pptWorkerPhoneExtGroupDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_PPT_WORKER_PHONE_EXT, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkPptWorkerPhoneExtList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkExtension = createBookmark(BookmarkConstants.PPT_WORKER_PHONE_EXT_EXTENSION,
					ppmDto.getEmployeePersPhName().getNbrPhoneExtension());
			bookmarkPptWorkerPhoneExtList.add(bookmarkExtension);
			pptWorkerPhoneExtGroupDto.setBookmarkDtoList(bookmarkPptWorkerPhoneExtList);
			formDataGroupList.add(pptWorkerPhoneExtGroupDto);
		}

		// parent group cfzz0601
		if (ppmDto.getCodesTables().getaCode().compareTo(FormConstants.SYSCODE) == 0) {
			FormDataGroupDto tempHeaderDirectorFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_HEADER_DIRECTOR, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkHeaderDirectorList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkSysDecodeDto = createBookmark(BookmarkConstants.HEADER_DIRECTOR_TITLE,
					ppmDto.getCodesTables().getaDecode());
			BookmarkDto bookmarkSysDecode2Dto = createBookmark(BookmarkConstants.HEADER_DIRECTOR_NAME,
					ppmDto.getCodesTables().getbDecode());
			bookmarkHeaderDirectorList.add(bookmarkSysDecodeDto);
			bookmarkHeaderDirectorList.add(bookmarkSysDecode2Dto);

			tempHeaderDirectorFrmDataGrpDto.setBookmarkDtoList(bookmarkHeaderDirectorList);
			formDataGroupList.add(tempHeaderDirectorFrmDataGrpDto);
		}

		// parent group cfzz0501
		else if (ppmDto.getCodesTables().getaCode().compareTo(FormConstants.SYSCODE) > 0) {
			FormDataGroupDto tempHeaderBoardFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER_BOARD,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkHeaderBoardList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkSysDecodeDto = createBookmark(BookmarkConstants.HEADER_BOARD_CITY,
					ppmDto.getCodesTables().getaDecode());
			BookmarkDto bookmarkSysDecode2Dto = createBookmark(BookmarkConstants.HEADER_BOARD_NAME,
					ppmDto.getCodesTables().getbDecode());
			bookmarkHeaderBoardList.add(bookmarkSysDecodeDto);
			bookmarkHeaderBoardList.add(bookmarkSysDecode2Dto);

			tempHeaderBoardFrmDataGrpDto.setBookmarkDtoList(bookmarkHeaderBoardList);
			formDataGroupList.add(tempHeaderBoardFrmDataGrpDto);
		}

		// parent groupcfzco72
		if (!TypeConvUtil.isNullOrEmpty(ppmDto.getNameDetail().getCdNameSuffix())) {
			FormDataGroupDto comma2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_2,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(comma2GroupDto);
		}

		// parent groups cfzco83, cfzco84
		if (!TypeConvUtil.isNullOrEmpty(ppmDto.getPartsNameDetail().getCdNameSuffix())) {
			FormDataGroupDto comma3GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_3,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(comma3GroupDto);

			FormDataGroupDto comma4GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_4,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(comma4GroupDto);
		}

		// parent group cfzz92
		if (ServiceConstants.FEMALE.equalsIgnoreCase(ppmDto.getPersonGenderSpanish().getCdPersonSex())) {
			FormDataGroupDto salutation2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SALUTATION_2,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(salutation2GroupDto);
		}

		// parent group cfzz91
		else {
			FormDataGroupDto salutation1GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SALUTATION_1,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(salutation1GroupDto);
		}

		// orphan bookmarks
		List<BookmarkDto> bookmarkNonFormGroupList = new ArrayList<BookmarkDto>();

		// DAM CSES40D
		BookmarkDto bookmarkHdName = createBookmark(BookmarkConstants.HD_NAME,
				ppmDto.getpPTParticipant().getNmPptPartFull());
		bookmarkNonFormGroupList.add(bookmarkHdName);
		BookmarkDto bookmarkDpName = createBookmark(BookmarkConstants.DP_NAME,
				ppmDto.getpPTParticipant().getNmPptPartFull());
		bookmarkNonFormGroupList.add(bookmarkDpName);

		// DAM CSEC15D
		BookmarkDto bookmarkCurrentDate = createBookmark(BookmarkConstants.CURRENT_DATE,
				DateUtils.stringDt(ppmDto.getStagePersonLinkCase().getDtCurrent()));
		bookmarkNonFormGroupList.add(bookmarkCurrentDate);
		BookmarkDto bookmarkChildAge = createBookmark(BookmarkConstants.PPT_CHILD_AGE,
				ppmDto.getStagePersonLinkCase().getNbrPersonAge());
		bookmarkNonFormGroupList.add(bookmarkChildAge);

		// DAM CSES14D
		BookmarkDto bookmarkDate = createBookmark(BookmarkConstants.PPT_DATE,
				DateUtils.stringDt(ppmDto.getpPtDetailsOut().getDtPptDate()));
		bookmarkNonFormGroupList.add(bookmarkDate);
		BookmarkDto bookmarkCity = createBookmark(BookmarkConstants.PPT_CITY,
				ppmDto.getpPtDetailsOut().getAddrPptCity());
		bookmarkNonFormGroupList.add(bookmarkCity);
		BookmarkDto bookmarkStreet1 = createBookmark(BookmarkConstants.PPT_STREET1,
				ppmDto.getpPtDetailsOut().getAddrPptStLn1());
		bookmarkNonFormGroupList.add(bookmarkStreet1);
		BookmarkDto bookmarkState = createBookmarkWithCodesTable(BookmarkConstants.PPT_STATE,
				ppmDto.getpPtDetailsOut().getAddrPptState(), CodesConstant.CSTATE);
		bookmarkNonFormGroupList.add(bookmarkState);
		BookmarkDto bookmarkZip = createBookmark(BookmarkConstants.PPT_ZIP, ppmDto.getpPtDetailsOut().getAddrPptZip());
		bookmarkNonFormGroupList.add(bookmarkZip);
		BookmarkDto bookmarkTime = createBookmark(BookmarkConstants.PPT_TIME,
				ppmDto.getpPtDetailsOut().getTmScrTmGeneric1());
		bookmarkNonFormGroupList.add(bookmarkTime);

		// DAM CSEC01D
		BookmarkDto bookmarkWorkerSuffix = createBookmarkWithCodesTable(BookmarkConstants.PPT_WORKER_SUFFIX,
				ppmDto.getEmployeePersPhName().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFormGroupList.add(bookmarkWorkerSuffix);
		BookmarkDto bookmarkWorkerPhone = createBookmark(BookmarkConstants.PPT_WORKER_PHONE,
				TypeConvUtil.formatPhone(ppmDto.getEmployeePersPhName().getNbrPhone()));
		bookmarkNonFormGroupList.add(bookmarkWorkerPhone);
		BookmarkDto bookmarkWorkerFirst = createBookmark(BookmarkConstants.PPT_WORKER_FIRST,
				ppmDto.getEmployeePersPhName().getNmNameFirst());
		bookmarkNonFormGroupList.add(bookmarkWorkerFirst);
		BookmarkDto bookmarkWorkerLast = createBookmark(BookmarkConstants.PPT_WORKER_LAST,
				ppmDto.getEmployeePersPhName().getNmNameLast());
		bookmarkNonFormGroupList.add(bookmarkWorkerLast);
		BookmarkDto bookmarkWorkerMiddle = createBookmark(BookmarkConstants.PPT_WORKER_MIDDLE,
				ppmDto.getEmployeePersPhName().getNmNameMiddle());
		bookmarkNonFormGroupList.add(bookmarkWorkerMiddle);

		// DAM CSEC35D
		BookmarkDto bookmarkChildSuffix = createBookmarkWithCodesTable(BookmarkConstants.PPT_CHILD_SUFFIX,
				ppmDto.getNameDetail().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFormGroupList.add(bookmarkChildSuffix);
		BookmarkDto bookmarkChildFirst = createBookmark(BookmarkConstants.PPT_CHILD_FIRST,
				ppmDto.getNameDetail().getNmNameFirst());
		bookmarkNonFormGroupList.add(bookmarkChildFirst);
		BookmarkDto bookmarkChildLast = createBookmark(BookmarkConstants.PPT_CHILD_LAST,
				ppmDto.getNameDetail().getNmNameLast());
		bookmarkNonFormGroupList.add(bookmarkChildLast);
		BookmarkDto bookmarkChildMiddle = createBookmark(BookmarkConstants.PPT_CHILD_MIDDLE,
				ppmDto.getNameDetail().getNmNameMiddle());
		bookmarkNonFormGroupList.add(bookmarkChildMiddle);
		if (StringUtils.isNotBlank(ppmDto.getPartsNameDetail().getCdNameSuffix())) {
			BookmarkDto bookmarkHdSuffix = createBookmarkWithCodesTable(BookmarkConstants.HD_SUFFIX_NAME,
					ppmDto.getPartsNameDetail().getCdNameSuffix(), CodesConstant.CSUFFIX2);
			bookmarkNonFormGroupList.add(bookmarkHdSuffix);
			BookmarkDto bookmarkDpSuffix = createBookmarkWithCodesTable(BookmarkConstants.DP_SUFFIX_NAME,
					ppmDto.getPartsNameDetail().getCdNameSuffix(), CodesConstant.CSUFFIX2);
			bookmarkNonFormGroupList.add(bookmarkDpSuffix);
		}
		if (StringUtils.isNotBlank(ppmDto.getPartsNameDetail().getNmNameLast())) {
			BookmarkDto bookmarkHdLast = createBookmark(BookmarkConstants.HD_LAST_NAME,
					ppmDto.getPartsNameDetail().getNmNameLast());
			bookmarkNonFormGroupList.add(bookmarkHdLast);

			BookmarkDto bookmarkDpLast = createBookmark(BookmarkConstants.DP_LAST_NAME,
					ppmDto.getPartsNameDetail().getNmNameLast());
			bookmarkNonFormGroupList.add(bookmarkDpLast);
		}
		if (StringUtils.isNotBlank(ppmDto.getPartsNameDetail().getNmNameMiddle())) {
			BookmarkDto bookmarkDpMiddle = createBookmark(BookmarkConstants.DP_MIDDLE_NAME,
					ppmDto.getPartsNameDetail().getNmNameMiddle());
			bookmarkNonFormGroupList.add(bookmarkDpMiddle);
			BookmarkDto bookmarkHdMiddle = createBookmark(BookmarkConstants.HD_MIDDLE_NAME,
					ppmDto.getPartsNameDetail().getNmNameMiddle());
			bookmarkNonFormGroupList.add(bookmarkHdMiddle);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGroupList);
		return preFillData;
	}
}