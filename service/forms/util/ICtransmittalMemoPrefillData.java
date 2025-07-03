package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.icpforms.dto.IcpFormsDto;
import us.tx.state.dfps.service.icpforms.dto.IcpcChildDetailsDto;
import us.tx.state.dfps.service.icpforms.dto.IcpcTransmittalDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Prefill
 * data class for Interstate Compact Transmittal Memo> May 9, 2018- 4:12:06 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Component
public class ICtransmittalMemoPrefillData extends DocumentServiceUtil {

	/**
	 * Method Name: returnPrefillData Method Description: prefill data from
	 * ICPformsServiceImpl for interstate compact Transimittal Memo form.
	 * 
	 * @param parentDtoobj
	 * @return PreFillDataServiceDto
	 */

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		IcpFormsDto icpcFormsDto = (IcpFormsDto) parentDtoobj;

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();		
		
		if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcTransmittalDto())) {

			// Group 2083
			if (!ServiceConstants.CSTATE_TX.equals(icpcFormsDto.getIcpcTransmittalDto().getCdSendingState())) {
				FormDataGroupDto sendingStateGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SENDING_STATE,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> sendingStateGroupbookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto trnsmttlst = createBookmark(BookmarkConstants.TRNSMTTL_SENDING_ST,
						icpcFormsDto.getIcpcTransmittalDto().getCdSendingState());
				sendingStateGroupbookmarkList.add(trnsmttlst);
				sendingStateGroup.setBookmarkDtoList(sendingStateGroupbookmarkList);
				formDataGroupList.add(sendingStateGroup);
			}

			// Group 2084
			if (!ServiceConstants.CSTATE_TX.equals(icpcFormsDto.getIcpcTransmittalDto().getCdReceivingState())) {
				FormDataGroupDto receivingStateGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RECEIVING_STATE,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> recStateGroupbookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto trnsmttlst = createBookmark(BookmarkConstants.TRNSMTTL_RECEIVING_ST,
						icpcFormsDto.getIcpcTransmittalDto().getCdReceivingState());
				recStateGroupbookmarkList.add(trnsmttlst);
				receivingStateGroup.setBookmarkDtoList(recStateGroupbookmarkList);
				formDataGroupList.add(receivingStateGroup);
			}
		}

		if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcTransmittalDtoList())) {
			for (IcpcTransmittalDto dto : icpcFormsDto.getIcpcTransmittalDtoList()) {
				// Group2200
				if (ServiceConstants.STRING_IND_Y.equalsIgnoreCase(dto.getIndTransmittal())
						&& ServiceConstants.CCOR_170.equalsIgnoreCase(dto.getCdTransmittalType())) {
					FormDataGroupDto priRegGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIORITY_REG_CHECKED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(priRegGroup);
				}

				// Group2210
				if (ServiceConstants.STRING_IND_Y.equalsIgnoreCase(dto.getIndTransmittal())
						&& ServiceConstants.CCOR_200.equalsIgnoreCase(dto.getCdTransmittalType())) {
					FormDataGroupDto urgentGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_URGENT_CHECKED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(urgentGroup);
				}

				// Group2220
				if (ServiceConstants.ONE_TWENTY_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.EIGHTY_A.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto custody = createFormDataGroup(FormGroupsConstants.TMPLAT_HOME_OR_CUSTODY_ATTACHED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(custody);
				}

				// Group2230
				if (ServiceConstants.ONE_TWENTY_R.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.EIGHTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto custodyReq = createFormDataGroup(
							FormGroupsConstants.TMPLAT_HOME_OR_CUSTODY_REQUESTED, FormConstants.EMPTY_STRING);
					formDataGroupList.add(custodyReq);
				}

				// Group2235
				if (ServiceConstants.ONE_TWENTY_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.ONE_TWENTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto homeEval = createFormDataGroup(FormGroupsConstants.TMPLAT_HOMEEVAL_CHECKED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(homeEval);
				}

				// Group2240
				if (ServiceConstants.EIGHTY_R.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.EIGHTY_A.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto cusChecked = createFormDataGroup(FormGroupsConstants.TMPLAT_CUSTODY_CHECKED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(cusChecked);
				}

				// Group2245
				if (ServiceConstants.TEN_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.TWENTY_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.THIRTY_A.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto att = createFormDataGroup(FormGroupsConstants.TMPLAT_ICPC100A_ATTACHED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(att);
				}

				// Group2250
				if (ServiceConstants.TEN_R.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.TWENTY_R.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.THIRTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto req = createFormDataGroup(FormGroupsConstants.TMPLAT_ICPC100A_REQUESTED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(req);
				}

				// Group2251
				if (ServiceConstants.TWENTY_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.TWENTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto considCkd = createFormDataGroup(
							FormGroupsConstants.TMPLAT_100A_CONSIDERATION_CHECKED, FormConstants.EMPTY_STRING);
					formDataGroupList.add(considCkd);
				}

				// Group2252
				if (ServiceConstants.TEN_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.TEN_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto apprvlCkd = createFormDataGroup(FormGroupsConstants.TMPLAT_100A_APPROVAL_CHECKED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(apprvlCkd);
				}

				// Group2253
				if (ServiceConstants.THIRTY_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.THIRTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto denialCkd = createFormDataGroup(FormGroupsConstants.TMPLAT_100A_DENIAL_CHECKED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(denialCkd);
				}

				// Group2254
				if (ServiceConstants.ONE_EIGHTY_A.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto sumAttached = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SOCIAL_SUMMARY_ATTACHED, FormConstants.EMPTY_STRING);
					formDataGroupList.add(sumAttached);
				}

				// Group2255
				if (ServiceConstants.ONE_EIGHTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto sumReq = createFormDataGroup(FormGroupsConstants.TMPLAT_SOCIAL_SUMMARY_REQUESTED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(sumReq);
				}

				// Group2256
				if (ServiceConstants.ONE_THIRTY_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.ONE_FORTY_A.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto tprAttached = createFormDataGroup(
							FormGroupsConstants.TMPLAT_LGL_WRDSHP_TPR_ATTACHED, FormConstants.EMPTY_STRING);
					formDataGroupList.add(tprAttached);
				}

				// Group2257
				if (ServiceConstants.ONE_THIRTY_R.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.ONE_FORTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto tprReq = createFormDataGroup(FormGroupsConstants.TMPLAT_LGL_WRDSHP_TPR_REQUESTED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(tprReq);
				}

				// Group2258
				if (ServiceConstants.ONE_THIRTY_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.ONE_THIRTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto wardCkd = createFormDataGroup(FormGroupsConstants.TMPLAT_WARDSHIP_CHECKED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(wardCkd);
				}

				// Group2259
				if (ServiceConstants.ONE_FORTY_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.ONE_FORTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto tprOrders = createFormDataGroup(FormGroupsConstants.TMPLAT_TPRORDERS_CHECKED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(tprOrders);
				}

				// Group2260
				if (ServiceConstants.FORTY_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.FIFTY_A.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto tprOrders = createFormDataGroup(FormGroupsConstants.TMPLAT_100BCONFIRMING_ATTACHED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(tprOrders);
				}

				// Group2261
				if (ServiceConstants.FORTY_R.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.FIFTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto tprOrders = createFormDataGroup(
							FormGroupsConstants.TMPLAT_100BCONFIRMING_REQUESTED, FormConstants.EMPTY_STRING);
					formDataGroupList.add(tprOrders);
				}

				// Group2262
				if (ServiceConstants.FIFTY_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.FIFTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto tprOrders = createFormDataGroup(FormGroupsConstants.TMPLAT_PLCMNT_DATE_CHECKED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(tprOrders);
				}

				// Group2264
				if (ServiceConstants.FORTY_R.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.FORTY_A.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto chng = createFormDataGroup(FormGroupsConstants.TMPLAT_PLCMNT_CHNG_CLOSURE_CHECKED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(chng);
				}

				// Group2265
				if (ServiceConstants.SEVENTY_A.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto casePlan = createFormDataGroup(FormGroupsConstants.TMPLAT_CASE_PLAN_ATTACHED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(casePlan);
				}

				// Group2266
				if (ServiceConstants.SEVENTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto planReq = createFormDataGroup(FormGroupsConstants.TMPLAT_CASE_PLAN_REQUESTED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(planReq);
				}

				// Group2267
				if (ServiceConstants.ONE_FIFTY_A.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto infoAttched = createFormDataGroup(FormGroupsConstants.TMPLAT_MEDICAL_INFO_ATTACHED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(infoAttched);
				}

				// Group2268
				if (ServiceConstants.ONE_FIFTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto medInfo = createFormDataGroup(FormGroupsConstants.TMPLAT_MEDICAL_INFO_REQUESTED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(medInfo);
				}

				// Group2269
				if (ServiceConstants.ONE_HUNDRED_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.ONE_HUNDRED_TEN_A.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto medAtt = createFormDataGroup(FormGroupsConstants.TMPLAT_FIN_MED_YESNO_ATTACHED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(medAtt);
				}

				// Group2270
				if (ServiceConstants.ONE_HUNDRED_R.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.ONE_HUNDRED_TEN_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto medReq = createFormDataGroup(FormGroupsConstants.TMPLAT_FIN_MED_YESNO_REQUESTED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(medReq);
				}

				// Group2071
				if (ServiceConstants.ONE_HUNDRED_TEN_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.ONE_HUNDRED_TEN_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto yesChecked = createFormDataGroup(
							FormGroupsConstants.TMPLAT_FIN_MEDICAL_YES_CHECKED, FormConstants.EMPTY_STRING);
					formDataGroupList.add(yesChecked);
				}

				// Group2072
				if (ServiceConstants.ONE_HUNDRED_R.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.ONE_HUNDRED_A.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto noChecked = createFormDataGroup(FormGroupsConstants.TMPLAT_FIN_MEDICAL_NO_CHECKED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(noChecked);
				}

				// Group2073
				if (ServiceConstants.ONE_NINTY_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.NINTY_A.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto famAttached = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SUPERVISORY_FAMILY_ATTACHED, FormConstants.EMPTY_STRING);
					formDataGroupList.add(famAttached);
				}

				// Group2074
				if (ServiceConstants.ONE_NINTY_R.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.NINTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto famReq = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SUPERVISORY_FAMILY_REQUESTED, FormConstants.EMPTY_STRING);
					formDataGroupList.add(famReq);
				}

				// Group2075
				if (ServiceConstants.ONE_NINTY_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.ONE_NINTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto rptCkd = createFormDataGroup(FormGroupsConstants.TMPLAT_SUPERVISORY_RPT_CHECKED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(rptCkd);
				}

				// Group2276
				if (ServiceConstants.NINTY_A.equalsIgnoreCase(dto.getConcatTypeInd())
						|| ServiceConstants.NINTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto reunionCkd = createFormDataGroup(FormGroupsConstants.TMPLAT_FAMILY_REUNION_CHECKED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(reunionCkd);
				}

				// Group2277
				if (ServiceConstants.SIXTY_A.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto infoAttched = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDTNL_INFO_ATTACHED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(infoAttched);
				}

				// Group2078
				if (ServiceConstants.SIXTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto infoAttched = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDTNL_INFO_REQUESTED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(infoAttched);
				}

				// Group2079
				if (ServiceConstants.ONE_SIXTY_A.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto infoAttched = createFormDataGroup(FormGroupsConstants.TMPLAT_OTHER_ATTACHED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(infoAttched);
				}

				// Group2080
				if (ServiceConstants.ONE_SIXTY_R.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto otherReq = createFormDataGroup(FormGroupsConstants.TMPLAT_OTHER_REQUESTED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(otherReq);
				}

				// Group2081
				if (ServiceConstants.TWO_TEN_Y.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto otherReq = createFormDataGroup(FormGroupsConstants.TMPLAT_100A_HELD_CHECKED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(otherReq);
				}

				// Group2082
				if (ServiceConstants.TWO_TWENTY_Y.equalsIgnoreCase(dto.getConcatTypeInd())) {
					FormDataGroupDto svcs = createFormDataGroup(FormGroupsConstants.TMPLAT_INTERSTATE_SVCS_CHECKED,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(svcs);
				}
			}
		}

		// Group 2297
		if (!ObjectUtils.isEmpty(icpcFormsDto.getTitleIVE())
				&& StringUtils.isNotBlank(icpcFormsDto.getTitleIVE().getCdCareType())) {
			FormDataGroupDto typeGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PLACEMENTB_TYPE,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> typeBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto type100b = createBookmarkWithCodesTable(BookmarkConstants.PLACEMENT_TYPE_100B,
					icpcFormsDto.getTitleIVE().getCdCareType(), CodesConstant.ICPCCRTP);
			typeBookmarkList.add(type100b);
			typeGroup.setBookmarkDtoList(typeBookmarkList);
			formDataGroupList.add(typeGroup);
		}

		// Group 2285
		if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcNameAndAddr())) {
			FormDataGroupDto nameAddrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PLCMT_RSRC,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> typeBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto rsrcName = createBookmark(BookmarkConstants.PLCMT_RSRC_NAME,
					icpcFormsDto.getIcpcNameAndAddr().getNmResource());
			typeBookmarkList.add(rsrcName);
			nameAddrGroup.setBookmarkDtoList(typeBookmarkList);
			formDataGroupList.add(nameAddrGroup);

			// Group2287
			if (StringUtils.isBlank(icpcFormsDto.getIcpcNameAndAddr().getNbrPersonPhone())) {
				FormDataGroupDto rsrcAddrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RSRC_ADDR,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> addrBookmarkList = new ArrayList<BookmarkDto>();
				List<FormDataGroupDto> addrGroupList = new ArrayList<FormDataGroupDto>();
				BookmarkDto city = createBookmark(BookmarkConstants.RSRC_ADDR_CITY,
						icpcFormsDto.getIcpcNameAndAddr().getAddrCity());
				BookmarkDto zip = createBookmark(BookmarkConstants.RSRC_ADDR_ZIP,
						icpcFormsDto.getIcpcNameAndAddr().getAddrZip());
				BookmarkDto addr1 = createBookmark(BookmarkConstants.RSRC_ADDR_1,
						icpcFormsDto.getIcpcNameAndAddr().getAddrStLn1());
				BookmarkDto state = createBookmark(BookmarkConstants.RSRC_ADDR_STATE,
						icpcFormsDto.getIcpcNameAndAddr().getAddrState());
				addrBookmarkList.add(city);
				addrBookmarkList.add(zip);
				addrBookmarkList.add(addr1);
				addrBookmarkList.add(state);

				// Group2290
				if (StringUtils.isNotBlank(icpcFormsDto.getIcpcNameAndAddr().getAddrStLn2())) {
					FormDataGroupDto rsrcSubAddrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RSRC_ADDR_LN,
							FormGroupsConstants.TMPLAT_RSRC_ADDR);
					List<BookmarkDto> addrSubBookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto addr2 = createBookmark(BookmarkConstants.RSRC_ADDR_LN_2,
							icpcFormsDto.getIcpcNameAndAddr().getAddrStLn2());
					addrSubBookmarkList.add(addr2);
					rsrcSubAddrGroup.setBookmarkDtoList(addrBookmarkList);
					addrGroupList.add(rsrcSubAddrGroup);
				}
				rsrcAddrGroup.setFormDataGroupList(addrGroupList);
				rsrcAddrGroup.setBookmarkDtoList(addrBookmarkList);
				formDataGroupList.add(rsrcAddrGroup);
			}
		}

		// Group2286
		if (!ObjectUtils.isEmpty(icpcFormsDto.getPersonDetailByIdReqDtoList())) {
			for (IcpcChildDetailsDto dto : icpcFormsDto.getPersonDetailByIdReqDtoList()) {
				if (ServiceConstants.CD_MEMBER.equalsIgnoreCase(dto.getCdPersonType())) {

					FormDataGroupDto personGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PLCMT_PERSON,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> addrBookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto fname = createBookmark(BookmarkConstants.PLCMT_PERSON_FNAME, dto.getNmNameFirst());
					BookmarkDto lname = createBookmark(BookmarkConstants.PLCMT_PERSON_LNAME, dto.getNmNameLast());
					addrBookmarkList.add(fname);
					addrBookmarkList.add(lname);
					personGroup.setBookmarkDtoList(addrBookmarkList);
					formDataGroupList.add(personGroup);

					// Group2288
					FormDataGroupDto personAddrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSON_ADDR,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> personAddrBookmarkList = new ArrayList<BookmarkDto>();
					List<FormDataGroupDto> personAddrGroupList = new ArrayList<FormDataGroupDto>();
					BookmarkDto zip = createBookmark(BookmarkConstants.PERSON_ADDR_ZIP, dto.getAddrZip());
					BookmarkDto city = createBookmark(BookmarkConstants.PERSON_ADDR_CITY, dto.getAddrCity());
					BookmarkDto addr1 = createBookmark(BookmarkConstants.PERSON_ADDR_1, dto.getAddrStLn1());
					BookmarkDto state = createBookmark(BookmarkConstants.PERSON_ADDR_STATE, dto.getAddrState());
					personAddrBookmarkList.add(zip);
					personAddrBookmarkList.add(city);
					personAddrBookmarkList.add(addr1);
					personAddrBookmarkList.add(state);
					personAddrGroup.setBookmarkDtoList(personAddrBookmarkList);

					// Group2289
					if (StringUtils.isNotBlank(dto.getAddrStLn2())) {
						FormDataGroupDto rsrcSubAddrGroup = createFormDataGroup(
								FormGroupsConstants.TMPLAT_PERSON_ADDR_LN, FormGroupsConstants.TMPLAT_PERSON_ADDR);
						personAddrGroupList.add(rsrcSubAddrGroup);
						personAddrGroup.setFormDataGroupList(personAddrGroupList);
					}

					formDataGroupList.add(personAddrGroup);
				}
			}
		}

		// Group 2291
		if (!ObjectUtils.isEmpty(icpcFormsDto.getAddressForAgency())
				&& !ServiceConstants.CO.equals(icpcFormsDto.getAddressForAgency().getAddrState())
				&& !ServiceConstants.CA.equals(icpcFormsDto.getAddressForAgency().getAddrState())
				&& !ServiceConstants.OH.equals(icpcFormsDto.getAddressForAgency().getAddrState())) {
			FormDataGroupDto agencyGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ICPC_ADDR,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> agencyBookmarkList = new ArrayList<BookmarkDto>();
			List<FormDataGroupDto> agencyGroupList = new ArrayList<FormDataGroupDto>();
			BookmarkDto city = createBookmark(BookmarkConstants.ICPC_ADDR_CITY,
					icpcFormsDto.getAddressForAgency().getAddrCity());
			BookmarkDto ln1 = createBookmark(BookmarkConstants.ICPC_ADDR_1,
					icpcFormsDto.getAddressForAgency().getAddrStLn1());
			BookmarkDto zip = createBookmark(BookmarkConstants.ICPC_ADDR_ZIP,
					icpcFormsDto.getAddressForAgency().getAddrZip());
			BookmarkDto state = createBookmark(BookmarkConstants.ICPC_ADDR_STATE,
					icpcFormsDto.getAddressForAgency().getAddrState());
			agencyBookmarkList.add(city);
			agencyBookmarkList.add(ln1);
			agencyBookmarkList.add(zip);
			agencyBookmarkList.add(state);
			agencyGroup.setBookmarkDtoList(agencyBookmarkList);

			// Group2293
			if (StringUtils.isNotBlank(icpcFormsDto.getAddressForAgency().getAddrStLn2())) {
				FormDataGroupDto rsrcSubAddrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ICPC_ADDR_LN,
						FormGroupsConstants.TMPLAT_ICPC_ADDR);
				List<BookmarkDto> ln2BookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto ln2 = createBookmark(BookmarkConstants.ICPC_ADDR_LN_2,
						icpcFormsDto.getAddressForAgency().getAddrStLn2());
				ln2BookmarkList.add(ln2);
				rsrcSubAddrGroup.setBookmarkDtoList(ln2BookmarkList);
				agencyGroupList.add(rsrcSubAddrGroup);
				agencyGroup.setFormDataGroupList(agencyGroupList);
			}
			formDataGroupList.add(agencyGroup);
		}

		// Group 2292
		if (!ObjectUtils.isEmpty(icpcFormsDto.getCountyAddressDto())
				&& ServiceConstants.CCH_20.equals(icpcFormsDto.getCountyAddressDto().getCdType())) {
			FormDataGroupDto cntyAddrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ICPC_CNTY_ADDR,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> agencyBookmarkList = new ArrayList<BookmarkDto>();
			List<FormDataGroupDto> agencyGroupList = new ArrayList<FormDataGroupDto>();
			BookmarkDto city = createBookmark(BookmarkConstants.ICPC_CNTY_ADDR_CITY,
					icpcFormsDto.getCountyAddressDto().getAddrCity());
			BookmarkDto ln1 = createBookmark(BookmarkConstants.ICPC_CNTY_ADDR_1,
					icpcFormsDto.getCountyAddressDto().getAddrStLn1());
			BookmarkDto zip = createBookmark(BookmarkConstants.ICPC_CNTY_ADDR_ZIP,
					icpcFormsDto.getCountyAddressDto().getAddrZip());
			BookmarkDto state = createBookmark(BookmarkConstants.ICPC_CNTY_ADDR_STATE,
					icpcFormsDto.getCountyAddressDto().getAddrState());
			agencyBookmarkList.add(city);
			agencyBookmarkList.add(ln1);
			agencyBookmarkList.add(zip);
			agencyBookmarkList.add(state);
			cntyAddrGroup.setBookmarkDtoList(agencyBookmarkList);

			// Group2294
			if (StringUtils.isNotBlank(icpcFormsDto.getCountyAddressDto().getAddrStLn2())) {
				FormDataGroupDto cntySubAddrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ICPC_CNTY_ADDR_LN,
						FormGroupsConstants.TMPLAT_ICPC_CNTY_ADDR);
				List<BookmarkDto> ln2BookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto ln2 = createBookmark(BookmarkConstants.ICPC_CNTY_ADDR_LN_2,
						icpcFormsDto.getCountyAddressDto().getAddrStLn2());
				ln2BookmarkList.add(ln2);
				cntySubAddrGroup.setBookmarkDtoList(ln2BookmarkList);
				agencyGroupList.add(cntySubAddrGroup);
				cntyAddrGroup.setFormDataGroupList(agencyGroupList);
			}
			formDataGroupList.add(cntyAddrGroup);
		}

		// Group 2295
		if (!ObjectUtils.isEmpty(icpcFormsDto.getChildrenNameList())) {
			for (IcpcChildDetailsDto dto : icpcFormsDto.getChildrenNameList()) {
				FormDataGroupDto childGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILDREN,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> childrenBookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto child = createBookmark(BookmarkConstants.CHILD_TRNSMTTL, dto.getNmPersonFull());
				childrenBookmarkList.add(child);
				childGroup.setBookmarkDtoList(childrenBookmarkList);
				formDataGroupList.add(childGroup);
			}
		}

		// Group 2296
		if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcDetailsDto())
				&& StringUtils.isNotBlank(icpcFormsDto.getIcpcDetailsDto().getCdCareType())) {
			FormDataGroupDto childGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PLACEMENTA_TYPE,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> childrenBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto child = createBookmarkWithCodesTable(BookmarkConstants.PLACEMENT_TYPE_100A,
					icpcFormsDto.getIcpcDetailsDto().getCdCareType(), CodesConstant.ICPCCRTP);
			childrenBookmarkList.add(child);
			childGroup.setBookmarkDtoList(childrenBookmarkList);
			formDataGroupList.add(childGroup);
		}

		// Populating the non form group data into prefill data. !!bookmarks
		if (!ObjectUtils.isEmpty(icpcFormsDto.getStagePersonLinkCaseDto())) {
			BookmarkDto childNmStage = createBookmark(BookmarkConstants.CHILD_NM_STAGE,
					icpcFormsDto.getStagePersonLinkCaseDto().getNmStage());
			bookmarkNonFrmGrpList.add(childNmStage);
			BookmarkDto idCase = createBookmark(BookmarkConstants.CASE_NUMBER,
					icpcFormsDto.getStagePersonLinkCaseDto().getIdCase());
			bookmarkNonFrmGrpList.add(idCase);
		}

		if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcTransmittalDto())) {
			BookmarkDto dateSent = createBookmark(BookmarkConstants.DATE_SENT,
					DateUtils.stringDt(icpcFormsDto.getIcpcTransmittalDto().getDtSent()));
			BookmarkDto transType = createBookmarkWithCodesTable(BookmarkConstants.TYPE_OF_TRANSMITTAL,
					icpcFormsDto.getIcpcTransmittalDto().getCdTransmittalType(), CodesConstant.ICPCTRTP);
			BookmarkDto nmAttn = createBookmark(BookmarkConstants.NM_ATTN,
					icpcFormsDto.getIcpcTransmittalDto().getNmAttn());

			bookmarkNonFrmGrpList.add(dateSent);
			bookmarkNonFrmGrpList.add(transType);
			bookmarkNonFrmGrpList.add(nmAttn);
		}

		if (!ObjectUtils.isEmpty(icpcFormsDto.getCaseNumberList())) {
			for (IcpcTransmittalDto dto : icpcFormsDto.getCaseNumberList()) {
				BookmarkDto addtnlInfo = createBookmarkWithCodesTable(BookmarkConstants.ADDTNL_INFO,
						dto.getCdAddtnlInfo(), CodesConstant.ICPCTRAI);
				BookmarkDto otherName = createBookmark(BookmarkConstants.OTHER_CASE_NAME, dto.getNbrCaseOtherState());
				BookmarkDto txtComment = createBookmark(BookmarkConstants.TXT_COMMENT, dto.getTxtComment());
				BookmarkDto txtOther = createBookmark(BookmarkConstants.TXT_OTHER, dto.getTxtOther());
				bookmarkNonFrmGrpList.add(otherName);
				bookmarkNonFrmGrpList.add(addtnlInfo);
				bookmarkNonFrmGrpList.add(txtComment);
				bookmarkNonFrmGrpList.add(txtOther);
			}
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}

}
