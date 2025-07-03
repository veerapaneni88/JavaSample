package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.admin.dto.CrimHistDto;
import us.tx.state.dfps.service.admin.dto.EmpNameDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.dpscrimhistres.dto.DPSCrimHistResDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:DPSCrimHistResPrefillData will implement returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for Form ccf12o00 Apr 9, 2018- 10:53:14 AM © 2017 Texas Department of
 * Family and Protective Services
 *********************************  Change History *********************************
 * 08/23/2023 thompswa artf245974 refactor
 */
@Component
public class DPSCrimHistResPrefillData extends DocumentServiceUtil {

	public DPSCrimHistResPrefillData() {
		super();
	}

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

		DPSCrimHistResDto dPSCrimHistResDto = (DPSCrimHistResDto) parentDtoobj;

		if (ObjectUtils.isEmpty(dPSCrimHistResDto.getCrimHistDto()))
			dPSCrimHistResDto.setCrimHistDto(new CrimHistDto());

		if (ObjectUtils.isEmpty(dPSCrimHistResDto.getEmpNameDtoPerson()))
			dPSCrimHistResDto.setEmpNameDtoPerson(new EmpNameDto());

		if (ObjectUtils.isEmpty(dPSCrimHistResDto.getEmpNameDtoReq()))
			dPSCrimHistResDto.setEmpNameDtoReq(new EmpNameDto());


		List<FormDataGroupDto> formDataGroupList = new ArrayList<>();
		List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();
		String crimHistDate = ServiceConstants.EMPTY_STRING;

		if(Arrays.asList(CodesConstant.CCHKTYPE_80, CodesConstant.CCHKTYPE_81)
				.contains(dPSCrimHistResDto.getCrimHistDto().getCdRecCheckType()))
		{
			formDataGroupList.add(createFormDataGroup(TMPLAT_CRIMHIST_DATERESULTS, ServiceConstants.EMPTY_STRING));
			crimHistDate = DateUtils.stringDt(dPSCrimHistResDto.getCrimHistDto().getDtResultsPosted());
		} else {
			formDataGroupList.add(createFormDataGroup(TMPLAT_CRIMHIST_DATECOMPLETED, ServiceConstants.EMPTY_STRING));
			crimHistDate = DateUtils.stringDt(dPSCrimHistResDto.getCrimHistDto().getDtRecCheckCompl());
		}

		bookmarkNonFormGrpList.addAll(Arrays.asList(createBookmark(PERSONNAME_THEAD, dPSCrimHistResDto.getEmpNameDtoPerson().getEmpNameAction())
				, createBookmark(PERSONID_THEAD, dPSCrimHistResDto.getEmpNameDtoPerson().getIdPerson())
				, createBookmarkWithCodesTable(RECCHECK_CHECKTYPE, dPSCrimHistResDto.getCrimHistDto().getCdRecCheckType(), CodesConstant.CCHKTYPE)
				, createBookmark(PERSON_NAME, dPSCrimHistResDto.getEmpNameDtoPerson().getEmpNameAction())
				, createBookmark(PERSON_ID, dPSCrimHistResDto.getEmpNameDtoPerson().getIdPerson())
				, createBookmark(REQUESTER_NAME, dPSCrimHistResDto.getEmpNameDtoReq().getEmpNameAction())
				, createBookmark(REQUESTER_ID, dPSCrimHistResDto.getEmpNameDtoReq().getIdPerson())
				, createBookmark(REQUEST_DATE, DateUtils.stringDt(dPSCrimHistResDto.getCrimHistDto().getDtRecCheckReq()))
				, createBookmark(CRIMHIST_DATE, crimHistDate)
				, createBookmark(BookmarkConstants.NAME_RETURNED, dPSCrimHistResDto.getCrimHistDto().getNmCrimHistReturned())
				, createBookmark(COMMENTS, dPSCrimHistResDto.getCrimHistDto().getTxtCrimHistCmnts())
				));

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);
		preFillData.setBlobDataDtoList(Arrays.asList(createBlobData(CRIMHIST_NARR,
				CodesConstant.CRIMINAL_HIST_NARR_VIEW, dPSCrimHistResDto.getCrimHistDto().getIdCrimHist().toString())));

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
	private static final String COMMENTS = "COMMENTS";
	private static final String CRIMHIST_NARR = "CRIMHIST_NARR";
	private static final String CRIMHIST_DATE = "CRIMHIST_DATE";
	private static final String TMPLAT_CRIMHIST_DATECOMPLETED = "TMPLAT_CRIMHIST_DATECOMPLETED";
	private static final String TMPLAT_CRIMHIST_DATERESULTS = "TMPLAT_CRIMHIST_DATERESULTS";
}

