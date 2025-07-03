package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dto.CrimHistDto;
import us.tx.state.dfps.service.admin.dto.EmpNameDto;
import us.tx.state.dfps.service.common.*;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.dpscrimhistres.dto.DPSCrimHistResDto;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.daoimpl.NameDaoImpl;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FbiFingerprintHistoryPrefillData will implement returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data as for Form fbifingerprinthistory Feb 5, 2021- 07:23:14 AM Â© 2021 Texas Department of
 * Family and Protective Services
 *********************************  Change History *********************************
 * 02/05/2020 thompswa artf172715 BR 21.01 Support Manual Entry of Results from DPS SecureSite
 * 04/12/2021 nairl    artf179500: Selecting Narrative button in criminal history result page is not loading rap sheet(Form) in IMPACT 2.0
 * 08/24/2023 kannir   artf251083 load DATE_COMPLETED with CrimHistDto().getDtResultsPosted()
 * 08/24/2023 thompswa artf251083 move getNameWithSuffix to DPSCrimHistResServiceImpl.
 */
@Component
public class FbiFingerprintHistoryPrefillData extends DocumentServiceUtil {
	private static final Logger log = Logger.getLogger(FbiFingerprintHistoryPrefillData.class);

	public FbiFingerprintHistoryPrefillData() {
		super();
	}

	/**
	 * Method Description: returnPrefillData method is used to prefill the data from the
	 * NameDao by passing Dao output to Dtos to FbiFingerprintHistoryServiceImpl to bookmark 
	 * Dto list
	 * 
	 * @param parentDtoobj
	 * @param bookmarkDtoObj
	 * @return PreFillData
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		DPSCrimHistResDto dPSCrimHistResDto = (DPSCrimHistResDto) parentDtoobj;
		
		if (ObjectUtils.isEmpty(dPSCrimHistResDto.getCrimHistDto()))
			dPSCrimHistResDto.setCrimHistDto(new CrimHistDto());
		
		if (ObjectUtils.isEmpty(dPSCrimHistResDto.getEmpNameDtoPerson()))
			dPSCrimHistResDto.setEmpNameDtoPerson(new EmpNameDto());
		
		if (ObjectUtils.isEmpty(dPSCrimHistResDto.getEmpNameDtoReq()))
			dPSCrimHistResDto.setEmpNameDtoReq(new EmpNameDto());

		// bookmarks without groups
		List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();
		
		bookmarkNonFormGrpList.addAll(Arrays.asList(createBookmark(PERSONNAME_THEAD, dPSCrimHistResDto.getEmpNameDtoPerson().getEmpNameAction())
				, createBookmark(PERSONID_THEAD, dPSCrimHistResDto.getEmpNameDtoPerson().getIdPerson())
				, createBookmarkWithCodesTable(RECCHECK_CHECKTYPE, dPSCrimHistResDto.getCrimHistDto().getCdRecCheckType(), CodesConstant.CCHKTYPE)
				, createBookmark(PERSON_NAME, dPSCrimHistResDto.getEmpNameDtoPerson().getEmpNameAction())
				, createBookmark(PERSON_ID, dPSCrimHistResDto.getEmpNameDtoPerson().getIdPerson())
				, createBookmark(REQUESTER_NAME, dPSCrimHistResDto.getEmpNameDtoReq().getEmpNameAction())
				, createBookmark(REQUESTER_ID, dPSCrimHistResDto.getEmpNameDtoReq().getIdPerson())
				, createBookmark(REQUEST_DATE, DateUtils.stringDt(dPSCrimHistResDto.getCrimHistDto().getDtRecCheckReq()))
				, createBookmark(DATE_COMPLETED, DateUtils.stringDt(dPSCrimHistResDto.getCrimHistDto().getDtResultsPosted()))
				, createBookmark(NAME_RETURNED, dPSCrimHistResDto.getCrimHistDto().getNmCrimHistReturned())
				, createBookmark(COMMENTS, dPSCrimHistResDto.getCrimHistDto().getTxtCrimHistCmnts())
		));

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);

		return preFillData;
	}
	
	  // bookmarks for populateJavaSection
	  
	  private static final String PERSONNAME_THEAD = "PERSONNAME_THEAD";
	  private static final String PERSONID_THEAD = "PERSONID_THEAD";
	  private static final String RECCHECK_CHECKTYPE = "RECCHECK_CHECKTYPE";
	  private static final String PERSON_NAME = "PERSON_NAME";
	  private static final String PERSON_ID = "PERSON_ID";
	  private static final String REQUESTER_NAME = "REQUESTER_NAME";
	  private static final String REQUESTER_ID = "REQUESTER_ID";
	  private static final String REQUEST_DATE = "REQUEST_DATE";
	  private static final String DATE_COMPLETED = "DATE_COMPLETED";
	  private static final String NAME_RETURNED = "NAME_RETURNED";
	  private static final String COMMENTS = "COMMENTS";
	  private static final String CRIMHIST_NARR = "CRIMHIST_NARR";
}
