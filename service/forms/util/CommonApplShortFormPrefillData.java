package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CaExSubstanceUse;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.CommonAppShortFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.pca.dto.PlacementDtlDto;
import us.tx.state.dfps.service.person.dto.TraffickingDto;
import us.tx.state.dfps.service.placement.dto.CommonApplShortFormPrintDto;
import us.tx.state.dfps.service.placement.dto.CommonApplicationShortFormDto;
import us.tx.state.dfps.service.placement.dto.ShortFormCsaEpisodeIncdntsDto;
import us.tx.state.dfps.service.placement.dto.ShortFormEducationSrvDto;
import us.tx.state.dfps.service.placement.dto.ShortFormMedicationDto;
import us.tx.state.dfps.service.placement.dto.ShortFormPsychiatricDto;
import us.tx.state.dfps.service.placement.dto.ShortFormRtnRunawayDto;
import us.tx.state.dfps.service.placement.dto.ShortFormSiblingsDto;
import us.tx.state.dfps.service.placement.dto.ShortFormSpecialProgrammingDto;
import us.tx.state.dfps.service.placement.dto.ShortFormTherapyDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimIncidentDto;

/**
 *service-business- INTEROPERABILITY PROJECT
 *Class Description:* CommonApplShortFormPrefillData will implement returnPrefillData operation
 * defined in DocumentServiceUtil Interface to populate the prefill data for
 * form csc11o00shortform.
 *JAN 11, 2020 10:49:28 AM
 *© 2020 Texas Department of Family and Protective Services
 *********************************  Change History *********************************
 * 01/11/2020 thompswa artf113241 Interop Project initial. Pre-release - artf141314, artf142789, artf146712, artf146731, artf146754, artf146789, artf146801, artf146886, artf146922, artf146772
 */
@Component
public class CommonApplShortFormPrefillData  extends DocumentServiceUtil{

	private static final Logger logger = Logger.getLogger(CommonApplShortFormPrefillData.class);
	private static final String APPROXIMATE_DATE= "Approximate Date";

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

		CommonApplShortFormPrintDto commonApplShortFormPrintDto = (CommonApplShortFormPrintDto) parentDtoobj;

		CommonAppShortFormRes commonAppShortFormRes = commonApplShortFormPrintDto.getCommonAppShortFormRes();
		List<CaExSubstanceUse> substanceList = commonApplShortFormPrintDto.getSubstanceList();

		if (ObjectUtils.isEmpty(commonApplShortFormPrintDto.getCommonAppShortFormRes()))
			commonApplShortFormPrintDto.setCommonAppShortFormRes(new CommonAppShortFormRes());
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto()))
			commonAppShortFormRes.setCommonApplicationShortFormDto(new CommonApplicationShortFormDto());
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getStagePersonLinkCaseDto()))
			commonAppShortFormRes.setStagePersonLinkCaseDto(new StagePersonLinkCaseDto());
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getSplProgDto()))
			commonAppShortFormRes.setSplProgDto(new ShortFormSpecialProgrammingDto());
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getPsychiatricList())) {
			List<ShortFormPsychiatricDto> psychiatricList = new ArrayList<ShortFormPsychiatricDto>();
			ShortFormPsychiatricDto hsptlztn = new ShortFormPsychiatricDto();
			psychiatricList.add(hsptlztn);
			commonAppShortFormRes.setPsychiatricList(psychiatricList);
		}
		if (ObjectUtils.isEmpty(commonApplShortFormPrintDto.getSubstanceList())) {
			List<CaExSubstanceUse> caExSubstanceList = new ArrayList<CaExSubstanceUse>();
			CaExSubstanceUse substanceUse = new CaExSubstanceUse();
			caExSubstanceList.add(substanceUse);
			commonApplShortFormPrintDto.setSubstanceList(caExSubstanceList);
		}

		if (ObjectUtils.isEmpty(commonAppShortFormRes.getMedList())) {
			List<ShortFormMedicationDto> medDtoLst = new ArrayList<ShortFormMedicationDto>();
			ShortFormMedicationDto medDto = new ShortFormMedicationDto();
			medDtoLst.add(medDto);
			commonAppShortFormRes.setMedList(medDtoLst);
		}
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getTherapyList())) {
			List<ShortFormTherapyDto> thrpDtoLst = new ArrayList<ShortFormTherapyDto>();
			ShortFormTherapyDto thrpDto = new ShortFormTherapyDto();
			thrpDtoLst.add(thrpDto);
			commonAppShortFormRes.setTherapyList(thrpDtoLst);
		}
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getSexualVictimList())) {
			List<SexualVictimIncidentDto> sexualVictimList = new ArrayList<>();
			SexualVictimIncidentDto sexualVictimIncident = new SexualVictimIncidentDto();
			sexualVictimList.add(sexualVictimIncident);
			commonAppShortFormRes.setSexualVictimList(sexualVictimList);
		}
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getTraffickingLst())) {
			List<TraffickingDto> trfckngDtoLst = new ArrayList<TraffickingDto>();
			TraffickingDto trfckngDto = new TraffickingDto();
			trfckngDtoLst.add(trfckngDto);
			commonAppShortFormRes.setTraffickingLst(trfckngDtoLst);
		}
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getSfTraffickingLst())) {
			List<TraffickingDto> trfckngDtoLst = new ArrayList<TraffickingDto>();
			TraffickingDto trfckngDto = new TraffickingDto();
			trfckngDtoLst.add(trfckngDto);
			commonAppShortFormRes.setTraffickingLst(trfckngDtoLst);
		}
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getSfEpisodeIncdntDtls())) {
			List<ShortFormCsaEpisodeIncdntsDto> csaEpisodesIncdntDtoLst = new ArrayList<ShortFormCsaEpisodeIncdntsDto>();
			ShortFormCsaEpisodeIncdntsDto csaEpisodesIncdntDto = new ShortFormCsaEpisodeIncdntsDto();
			csaEpisodesIncdntDtoLst.add(csaEpisodesIncdntDto);
			commonAppShortFormRes.setSfEpisodeIncdntDtls(csaEpisodesIncdntDtoLst);
		}
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getRtnRunaway())) {
			commonAppShortFormRes.setRtnRunaway(new ShortFormRtnRunawayDto());
		}
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getEduSrv())) {
			commonAppShortFormRes.setEduSrv(new ShortFormEducationSrvDto());
		}
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getSiblingList())) {
			List<ShortFormSiblingsDto> siblingLst = new ArrayList<ShortFormSiblingsDto>();
			ShortFormSiblingsDto sibling = new ShortFormSiblingsDto();
			siblingLst.add(sibling);
			commonAppShortFormRes.setSiblingList(siblingLst);
		}

		/**
		 * List to add all the group bookmarks to set into prefill data
		 */
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		/**
		 * List to add all the non-group bookmark data to set into prefill data
		 */
		List<BookmarkDto> bookmarkNoGroupList = new ArrayList<BookmarkDto>();


		/*
		 * TITLE SECTION
		 */
		bookmarkNoGroupList.add(createBookmarkWithCodesTable(HEADER_DIRECTOR_TITLE, CodesConstant.CBRDTTLE_001, CodesConstant.CBRDTTLE));
		bookmarkNoGroupList.add(createBookmarkWithCodesTable(HEADER_DIRECTOR_NAME, CodesConstant.CBRDNAME_001, CodesConstant.CBRDNAME));
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getNmCase()));
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TITLE_CASE_NBR,
				commonAppShortFormRes.getStagePersonLinkCaseDto().getIdCase()));
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TITLE_CHILD_NAME,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getNmChild()));
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TITLE_DATE_OF_BIRTH,
				TypeConvUtil.formDateFormat(commonAppShortFormRes.getCommonApplicationShortFormDto().getDtChildBirth())));
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TITLE_DATE_COMP,
				TypeConvUtil.formDateFormat(commonAppShortFormRes.getCommonApplicationShortFormDto().getDtCompletedApplication())));

		/*
		 * DFPS CASEWORKER INFORMATION SECTION
		 */
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_CSWRKR_NAME,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getNmDfpsCaseWorker()));
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_CSWRKR_UNIT,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getCdEmpUnit()));
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_SUPRVSR_NAME,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getNmDfpsSupervisor()));


		/*
		 * CHILDS INFORMATION SECTION
		 */
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_CHILD_NAME,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getNmChild()));
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_DATE_OF_BIRTH,
				TypeConvUtil.formDateFormat(commonAppShortFormRes.getCommonApplicationShortFormDto().getDtChildBirth())));
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_CHILD_IDPERSON,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getIdPerson()));
		// Populate Child Gender Value
		if (!ObjectUtils.isEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getCdPersonSex())
				&& ServiceConstants.MALE.equalsIgnoreCase(commonAppShortFormRes.getCommonApplicationShortFormDto().getCdPersonSex())) {
			bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_CHILD_GNDR_MALE,
					ServiceConstants.CHECKED));
		}
		if (!ObjectUtils.isEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getCdPersonSex())
				&& ServiceConstants.FEMALE.equalsIgnoreCase(commonAppShortFormRes.getCommonApplicationShortFormDto().getCdPersonSex())) {
			bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_CHILD_GNDR_FEMALE,
					ServiceConstants.CHECKED));
		}
		// Populate Other Child Gender Identification Value
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_CHILD_GNDR_OTHER,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtGenderIdentification()));
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_CHILD_LEGAL_REGN,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getCdLegalRegion()));
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_CHILD_CNTRY_CTZN,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getCdPersonCitizenship()));
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_CHILD_ETHNCTY,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtEthnicity()));
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_txtCHILD_RACE,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtRace()));
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_CHILD_PRMRY_LANG,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getCdPersonLanguage()));
		bookmarkNoGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.TXT_CHILD_LGL_CNTY,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getCdLegalCounty(), CodesConstant.CCOUNT));
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_CHILD_SVCS_LVL_CARE,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtLevlOfCare()));

		if(!TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getCdServicePackage()))
		{
			bookmarkNoGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.TXT_CHILD_RMD_SVCS_PKG,
					commonAppShortFormRes.getCommonApplicationShortFormDto().getCdServicePackage(), CodesConstant.CSVCCODE));

		}

		//code added for 61082
		StringBuilder remAddr = new StringBuilder();
		if(commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtRemovalAddress()==null || commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtRemovalAddress().length()==0){
			remAddr.append(commonAppShortFormRes.getCommonApplicationShortFormDto().getRemovalAddrStLn1());
			remAddr.append(commonAppShortFormRes.getCommonApplicationShortFormDto().getRemovalAddrStLn2()!=null?" "+commonAppShortFormRes.getCommonApplicationShortFormDto().getRemovalAddrStLn2():"");
			remAddr.append(" ");
			remAddr.append(commonAppShortFormRes.getCommonApplicationShortFormDto().getRemovalAddrCity());
			remAddr.append(" ");
			remAddr.append(commonAppShortFormRes.getCommonApplicationShortFormDto().getCdRemovalAddrState());
			remAddr.append(" ");
			remAddr.append(commonAppShortFormRes.getCommonApplicationShortFormDto().getAddrRemovalAddrZip());
		}else{
			remAddr.append(commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtRemovalAddress());
		}
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_CHILD_RMVL_ADDR, remAddr.toString()));
		bookmarkNoGroupList.add(createBookmark(CHILD_RMVLDATE,
				TypeConvUtil.formDateFormat(commonAppShortFormRes.getCommonApplicationShortFormDto().getRemovalDt())));
		if (!ObjectUtils.isEmpty(commonAppShortFormRes.getSplProgDto().getPregnant())) {
			bookmarkNoGroupList.add(createBookmark(SPCPRGNT,
					ServiceConstants.CHECKED));
		}
		if (!ObjectUtils.isEmpty(commonAppShortFormRes.getSplProgDto().getParent())) {
			bookmarkNoGroupList.add(createBookmark(SPCPRNTNG,
					ServiceConstants.CHECKED));
		}
		if (!ObjectUtils.isEmpty(commonAppShortFormRes.getSplProgDto().getNone())) {
			bookmarkNoGroupList.add(createBookmark(SPCNONE,
					ServiceConstants.CHECKED));
		}


		/*
		 * TRAUMA HISTORY SECTION
		 */
		// set the sxvctmztn radio
		if (TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getSexualVictim())) {
			;// no default response
		} else {
			bookmarkNoGroupList.add(createBookmark(SXVCTMZTN_RMVL, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtTraumaAbuseNeglect()));
			bookmarkNoGroupList.add(createBookmark(SXVCTMZTN_HIST, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtTraumaOthAbuseNeglect()));
			bookmarkNoGroupList.add(createBookmark(SXVCTMZTN_OTHR, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtTraumaOthTraumaticExp()));

			if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getUnconfirmedSexualVictim())) {
				bookmarkNoGroupList.add(createBookmark(UNCSXVCTMZTNN, ServiceConstants.CHECKED));
			} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getUnconfirmedSexualVictim())) {
				bookmarkNoGroupList.add(createBookmark(UNCSXVCTMZTNY, ServiceConstants.CHECKED));
			}

			if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getSexualVictim())) {
				bookmarkNoGroupList.add(createBookmark(SXVCTMZTNN, ServiceConstants.CHECKED));
			} else {
				bookmarkNoGroupList.add(createBookmark(SXVCTMZTNY, ServiceConstants.CHECKED));

				// populate victimization list
				if (null != commonAppShortFormRes.getSexualVictimList()
						&& ServiceConstants.Zero < commonAppShortFormRes.getSexualVictimList().size()) {
					FormDataGroupDto vctmztnGroup = createFormDataGroup(TMPLAT_SXVCTMZTN, FormConstants.EMPTY_STRING);
					// populate sexual victimization incident date and description
					List<FormDataGroupDto> vicGrpList = new ArrayList<>();
					for (SexualVictimIncidentDto sexVicIncdntDto : commonAppShortFormRes.getSexualVictimList()) {
						FormDataGroupDto sexVicIncdntFrmDataGrpDto = createFormDataGroup(TMPLAT_SXVCTMZTN_HIST, TMPLAT_SXVCTMZTN);
						List<BookmarkDto> bookmarkSexVicIncdntFrmGrpList = new ArrayList<>();
						bookmarkSexVicIncdntFrmGrpList.add(createBookmark(SXVCTMZTN_HIST_DT,
								TypeConvUtil.formDateFormat(sexVicIncdntDto.getDtIncident())));
						if (!TypeConvUtil.isNullOrEmpty(sexVicIncdntDto.getIndApproxDate())
								&& !ServiceConstants.N.equals(sexVicIncdntDto.getIndApproxDate())) {
							bookmarkSexVicIncdntFrmGrpList.add(createBookmark(SXVCTMZTN_HIST_DTAPPROX, APPROXIMATE_DATE));
						}
						bookmarkSexVicIncdntFrmGrpList.add(createBookmark(SXVCTMZTN_HIST_DESCR, sexVicIncdntDto.getVictimComments()));
						sexVicIncdntFrmDataGrpDto.setBookmarkDtoList(bookmarkSexVicIncdntFrmGrpList);
						vicGrpList.add(sexVicIncdntFrmDataGrpDto);
					}
					vctmztnGroup.setFormDataGroupList(vicGrpList);
					formDataGroupList.add(vctmztnGroup);
				}
			}
		}


		/*
		 * TRAFFICKING HISTORY SECTION
		 */
		// populate suspected sex trafficking radio
		if (TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSuspVictimSexTrfckng())){
			; // no default response
		} else {
			if (ServiceConstants.UNKNOWN1.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSuspVictimSexTrfckng())) {
				bookmarkNoGroupList.add(createBookmark(SXTRSUSPUNK, ServiceConstants.CHECKED ));
			} else
			if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSuspVictimSexTrfckng())) {
				bookmarkNoGroupList.add(createBookmark(SXTRSUSPN, ServiceConstants.CHECKED ));
			} else {
				bookmarkNoGroupList.add(createBookmark(SXTRSUSPY, ServiceConstants.CHECKED ));
			}
		}
		// populate suspected labor trafficking radio
		if (TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSuspVictimLaborTrfckng())){
			; // no default response
		} else {
			if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSuspVictimLaborTrfckng())) {
				bookmarkNoGroupList.add(createBookmark(LBTRSUSPY, ServiceConstants.CHECKED ));
			} else
			if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSuspVictimLaborTrfckng())) {
				bookmarkNoGroupList.add(createBookmark(LBTRSUSPN, ServiceConstants.CHECKED ));
			}
		}
		// populate confirmed labor trafficking radio
		if (TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCnfrmVictimLaborTrfckng())){
			; // no default response
		} else {
			if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCnfrmVictimLaborTrfckng())) {
				bookmarkNoGroupList.add(createBookmark(LBTRCONFY, ServiceConstants.CHECKED ));
			} else if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCnfrmVictimLaborTrfckng())) {
				bookmarkNoGroupList.add(createBookmark(LBTRCONFN, ServiceConstants.CHECKED ));
			}
		}
		// artf146789 the answer to trafficking question should be displayed
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_TRFCKNG_ADDR_SPCFC_ISSUE,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtTrafficking()));
		// set the confirmed sex trafficking radio
		if (TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCnfrmVictimSexTrfckng())) {
			; // no default response
		} else { //artf146801 display Unknown radio
			if (ServiceConstants.UNKNOWN1.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCnfrmVictimSexTrfckng())) {
				bookmarkNoGroupList.add(createBookmark(SXTRCONFUNK, ServiceConstants.CHECKED ));
			} else
			if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCnfrmVictimSexTrfckng())) {
				bookmarkNoGroupList.add(createBookmark(SXTRCONFN, ServiceConstants.CHECKED ));
			} else {
				bookmarkNoGroupList.add(createBookmark(SXTRCONFY, ServiceConstants.CHECKED));

				// populate sexual trafficking incident description
				if (!ObjectUtils.isEmpty(commonAppShortFormRes.getSfTraffickingLst())
						&& ServiceConstants.Zero < commonAppShortFormRes.getSfTraffickingLst().size()) {
					FormDataGroupDto trafGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_TRFCKNGABUSE,
							FormConstants.EMPTY_STRING);
					// get confirmed sex trafficking incidents
					List<FormDataGroupDto> sxTrfckngList = new ArrayList<FormDataGroupDto>();
					if (!ObjectUtils.isEmpty(commonAppShortFormRes.getSfTraffickingLst())
							&& ServiceConstants.Zero < commonAppShortFormRes.getSfTraffickingLst().size()) {
						for (TraffickingDto sxtrConfDto : commonAppShortFormRes.getSfTraffickingLst()) {
							FormDataGroupDto trafIncdntFrmDataGrpDto = createFormDataGroup(TMPLAT_TRFCKNGABUSE_SXCONF,
									FormGroupsConstants.TMPLAT_TRFCKNGABUSE);
							List<BookmarkDto> bookmarkSxTrafIncdntList = new ArrayList<BookmarkDto>();
							if (!TypeConvUtil.isNullOrEmpty(sxtrConfDto.getDtOfIncdnt())) {
								bookmarkSxTrafIncdntList.add(createBookmark(BookmarkConstants.TRFCKNGABUSE_DTINCIDENT,
										TypeConvUtil.formDateFormat(sxtrConfDto.getDtOfIncdnt())));
							}
							// artf146731 only show approximate date indicator if Y
							if (!TypeConvUtil.isNullOrEmpty(sxtrConfDto.getIndApproxDate())
									&& !ServiceConstants.N.equals(sxtrConfDto.getIndApproxDate())) {
								bookmarkSxTrafIncdntList.add(createBookmark(BookmarkConstants.TRFCKNGABUSE_DTAPPROXIMATE, APPROXIMATE_DATE));
							}
							bookmarkSxTrafIncdntList.add(createBookmark(BookmarkConstants.TRFCKNGABUSE_INFO,
									!TypeConvUtil.isNullOrEmpty(sxtrConfDto.getTxtVictimizationComments())
											? sxtrConfDto.getTxtVictimizationComments() : ServiceConstants.EMPTY_STRING));
							trafIncdntFrmDataGrpDto.setBookmarkDtoList(bookmarkSxTrafIncdntList);
							sxTrfckngList.add(trafIncdntFrmDataGrpDto);
						}
					}
					trafGrpDto.setFormDataGroupList(sxTrfckngList);
					formDataGroupList.add(trafGrpDto);
				}
			}
		}


		/*
		 * HEALTH CARE SUMMARY SECTION - MEDICAL INFORMATION SUBSECTION
		 */
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_HLTH_CARE_MED_EXAM,
				TypeConvUtil.formDateFormat(commonAppShortFormRes.getCommonApplicationShortFormDto().getDtHealthStepsMedExam())));
		bookmarkNoGroupList.add(createBookmark(HLTHCAREDENTEXAM,
				TypeConvUtil.formDateFormat(commonAppShortFormRes.getCommonApplicationShortFormDto().getDtHealthStepsDentalExam())));
		bookmarkNoGroupList.add(createBookmark(HLTHCARETBTEST,
				TypeConvUtil.formDateFormat(commonAppShortFormRes.getCommonApplicationShortFormDto().getDtLastTbTest())));
		bookmarkNoGroupList.add(createBookmark(HLTHCAREALLERGIES,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtAllergies()));
		bookmarkNoGroupList.add(createBookmark(HLTHCAREMEDCTN,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtMedicationReactions()));
		bookmarkNoGroupList.add(createBookmarkWithCodesTable(HLTHCAREINDMEDS,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCurrentlyMedications(), CodesConstant.CINVACAN));
		// populate current medications list
		if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCurrentlyMedications())) {
			FormDataGroupDto currentMedsGroup = createFormDataGroup(TMPLAT_MEDINFO, FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> medsGroupsList = new ArrayList<FormDataGroupDto>();
			if ( !ObjectUtils.isEmpty(commonAppShortFormRes.getMedList())
					&& ServiceConstants.Zero < commonAppShortFormRes.getMedList().size()) {
				for (ShortFormMedicationDto medDto : commonAppShortFormRes.getMedList()) {
					FormDataGroupDto medsGroup = createFormDataGroup(TMPLAT_MEDINFO_MEDS, TMPLAT_MEDINFO);
					List<BookmarkDto> bookmarkMedsList = new ArrayList<BookmarkDto>();
					bookmarkMedsList.add(createBookmark(MEDINFO_MEDS_MEDCTNNAME, medDto.getMedName()));
					bookmarkMedsList.add(createBookmark(MEDINFO_MEDS_DOSAGE, medDto.getDosage()));
					bookmarkMedsList.add(createBookmark(MEDINFO_MEDS_FREQ, medDto.getFrequency()));
					bookmarkMedsList.add(createBookmark(MEDINFO_MEDS_DTPRESCRIBED, TypeConvUtil.formDateFormat(medDto.getDtPrescribed())));
					bookmarkMedsList.add(createBookmark(MEDINFO_MEDS_TREATNGCNDTN, medDto.getCondition()));
					medsGroup.setBookmarkDtoList(bookmarkMedsList);
					medsGroupsList.add(medsGroup);
				}
				// Add group and bookmarks to current med group
				currentMedsGroup.setFormDataGroupList(medsGroupsList);
				formDataGroupList.add(currentMedsGroup);
			}
		}
		// set the current immunizations radio
		if (TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndImmunizationsCurrent())) {
			; // no default response
		}else {
			if (ServiceConstants.UNKNOWN1.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndImmunizationsCurrent())) {
				bookmarkNoGroupList.add(createBookmark(IMMNZTNCURRENTUNK, ServiceConstants.CHECKED ));
			} else
			if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndImmunizationsCurrent())) {
				bookmarkNoGroupList.add(createBookmark(IMMNZTNCURRENTY, ServiceConstants.CHECKED ));
			} else {
				bookmarkNoGroupList.add(createBookmark(IMMNZTNCURRENTN, ServiceConstants.CHECKED ));
			}
		}
		// set the medical consenter radio
		if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSelfMedicalConsenter())) {
			bookmarkNoGroupList.add(createBookmark(OWNMEDCNSTRY, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSelfMedicalConsenter())) {
			bookmarkNoGroupList.add(createBookmark(OWNMEDCNSTRN, ServiceConstants.CHECKED ));
		}


		/*
		 * HEALTH CARE SUMMARY SECTION - PHYSICAL HEALTH SUBSECTION
		 */
		bookmarkNoGroupList.add(createBookmark(SUSPPHYSCLHLTHCNDTN,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtDiagPhyHlthConditions()));
		bookmarkNoGroupList.add(createBookmark(MEDSPECLSTTREATNG,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtSpecialistAppointments()));
		bookmarkNoGroupList.add(createBookmark(CHILDDVLPMNTLHIST,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtDevelopmentalFunctioning()));
		// set the eci radio
		if (TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndEciServicesReceived())) {
			// no default response
		} else {
			if (ServiceConstants.UNKNOWN1.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndEciServicesReceived())) {
				bookmarkNoGroupList.add(createBookmark(CHILDRCVDECIUNK, ServiceConstants.CHECKED ));
			} else
			if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndEciServicesReceived())) {
				bookmarkNoGroupList.add(createBookmark(CHILDRCVDECIN, ServiceConstants.CHECKED ));
			} else {
				bookmarkNoGroupList.add(createBookmark(CHILDRCVDECIY, ServiceConstants.CHECKED ));
				FormDataGroupDto eciGroup = createFormDataGroup(TMPLAT_ECI, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkEciList = new ArrayList<BookmarkDto>();
				bookmarkEciList.add(createBookmark(ECI_SVCSPRVDED, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtEciServicesReceived()));
				eciGroup.setBookmarkDtoList(bookmarkEciList);
				formDataGroupList.add(eciGroup);
			}
		}
		// set the therapy radio
		if (TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndOtPtStTherapy())) {
			; // no default response
		} else {
			if (ServiceConstants.UNKNOWN1.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndOtPtStTherapy())) {
				bookmarkNoGroupList.add(createBookmark(CHILDRCVNGTHRPYUNK, ServiceConstants.CHECKED ));
			} else
			if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndOtPtStTherapy())) {
				bookmarkNoGroupList.add(createBookmark(CHILDRCVNGTHRPYN, ServiceConstants.CHECKED ));
			} else {
				bookmarkNoGroupList.add(createBookmark(CHILDRCVNGTHRPYY, ServiceConstants.CHECKED ));
				FormDataGroupDto therapyGroup = createFormDataGroup(TMPLAT_THRP, FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> thrpGroupsList = new ArrayList<FormDataGroupDto>();
				if ( !ObjectUtils.isEmpty(commonAppShortFormRes.getTherapyList())
						&& ServiceConstants.Zero < commonAppShortFormRes.getTherapyList().size()) {
					for (ShortFormTherapyDto thrpDto : commonAppShortFormRes.getTherapyList()) {
						FormDataGroupDto thrpSvcsGroup = createFormDataGroup(TMPLAT_THRP_SVCS, TMPLAT_THRP);
						List<BookmarkDto> bookmarkThrpList = new ArrayList<BookmarkDto>();
						bookmarkThrpList.add(createBookmark(THRP_SVCS_AGNCY, thrpDto.getAgencyName()));
						if (CAETHRPY_OTH.equals(thrpDto.getTherapy())) {
							bookmarkThrpList.add(createBookmark(THRP_SVCS_TYPE, thrpDto.getOtherTxt()));
						} else {
							bookmarkThrpList.add(createBookmark(THRP_SVCS_TYPE, getTherapy(thrpDto.getTherapy())));
						}
						bookmarkThrpList.add(createBookmark(THRP_SVCS_STARTDT, TypeConvUtil.formDateFormat(thrpDto.getDtStart())));
						bookmarkThrpList.add(createBookmark(THRP_SVCS_FREQ, thrpDto.getFrequency()));
						thrpSvcsGroup.setBookmarkDtoList(bookmarkThrpList);
						thrpGroupsList.add(thrpSvcsGroup);
					}
					// Add group and bookmarks to therapy group
					therapyGroup.setFormDataGroupList(thrpGroupsList);
					formDataGroupList.add(therapyGroup);
				}
			}
		}
		// set the encopresis radio
		if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndEncopresisPast90days())) {
			bookmarkNoGroupList.add(createBookmark(CHILDENCOPRESISHISTY, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndEncopresisPast90days())) {
			bookmarkNoGroupList.add(createBookmark(CHILDENCOPRESISHISTN, ServiceConstants.CHECKED ));
		}
		// set the enuresis radio
		if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndEnuresisPast90days())) {
			bookmarkNoGroupList.add(createBookmark(CHILDENURESISHISTY, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndEnuresisPast90days())) {
			bookmarkNoGroupList.add(createBookmark(CHILDENURESISHISTN, ServiceConstants.CHECKED ));
		}
		// populate enuresis history
		if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndEnuresisPast90days())
				// artf146772 display text data for Yes encopresis as well as Yes enuresis
				|| ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndEncopresisPast90days())) {
			FormDataGroupDto enurHistGroup = createFormDataGroup(TMPLAT_ENURHIST, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkEnurHistList = new ArrayList<BookmarkDto>();
			bookmarkEnurHistList.add(createBookmark(ENURHIST, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtEncopresisEnuresis()));
			enurHistGroup.setBookmarkDtoList(bookmarkEnurHistList);
			formDataGroupList.add(enurHistGroup);
		}

		/*
		 * HEALTH CARE SUMMARY SECTION - PRIMARY MEDICAL NEEDS SUBSECTION
		 */
		// set the pmn radio
		if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndPmn())) {
			bookmarkNoGroupList.add(createBookmark(CHILDPRMMEDNEDN, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndPmn())) {
			bookmarkNoGroupList.add(createBookmark(CHILDPRMMEDNEDY, ServiceConstants.CHECKED ));
			FormDataGroupDto pmnGroup = createFormDataGroup(TMPLAT_PMN, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkPmnList = new ArrayList<BookmarkDto>();
			bookmarkPmnList.add(createBookmark(PMN_DIAGNSISTREATMNT, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtPmnDiagnosis()));
			bookmarkPmnList.add(createBookmark(PMN_MEDSPECLSTCNTCT, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtPmnMedSpecialist()));
			bookmarkPmnList.add(createBookmark(PMN_TREATNGHSPTL, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtPmnPrimaryHospital()));
			bookmarkPmnList.add(createBookmark(PMN_NUMNURSNGHRS, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtNursingHours()));
			bookmarkPmnList.add(createBookmark(PMN_NMHMEHLTHAGNCY, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtHomeHealthAgency()));
			pmnGroup.setBookmarkDtoList(bookmarkPmnList);
			formDataGroupList.add(pmnGroup);
		}
		// set the dme radio
		if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndDmeRequired())) {
			bookmarkNoGroupList.add(createBookmark(CHILDREQRDDMEN, ServiceConstants.CHECKED ));
			// artf146712 else if Y
		} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndDmeRequired())) {
			bookmarkNoGroupList.add(createBookmark(CHILDREQRDDMEY, ServiceConstants.CHECKED ));
			FormDataGroupDto dmeGroup = createFormDataGroup(TMPLAT_DME, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkDmeList = new ArrayList<BookmarkDto>();
			bookmarkDmeList.add(createBookmark(DME_LSTDMESUPLS, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtDme()));
			dmeGroup.setBookmarkDtoList(bookmarkDmeList);
			formDataGroupList.add(dmeGroup);
		}
		// set the ambulance radio
		if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndAmbulance())) {
			bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_CHILD_NEED_AMBULANCE_YES, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndAmbulance())) {
			bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_CHILD_NEED_AMBULANCE_NO, ServiceConstants.CHECKED ));
		}
		// set the dnr radio
		if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndDnr())) {
			bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_DNR_ORDR_YES, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndDnr())) {
			bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_DNR_ORDR_NO, ServiceConstants.CHECKED ));
		}
		// set the deaf radio
		if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndDeafHearingImpaired())) {
			bookmarkNoGroupList.add(createBookmark(CHILDDEAFN, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndDeafHearingImpaired())) {
			bookmarkNoGroupList.add(createBookmark(CHILDDEAFY, ServiceConstants.CHECKED ));
			FormDataGroupDto deafGroup = createFormDataGroup(TMPLAT_CHILDDEAF, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkDeafList = new ArrayList<BookmarkDto>();
			bookmarkDeafList.add(createBookmark(CHILDDEAF_CMNCT, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtCommunicationType()));
			deafGroup.setBookmarkDtoList(bookmarkDeafList);
			formDataGroupList.add(deafGroup);
		}
		// set the blind radio
		if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndBlindVisuallyImpaired())) {
			bookmarkNoGroupList.add(createBookmark(CHILDBLINDN, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndBlindVisuallyImpaired())) {
			bookmarkNoGroupList.add(createBookmark(CHILDBLINDY, ServiceConstants.CHECKED ));
			FormDataGroupDto blindGroup = createFormDataGroup(TMPLAT_CHILDBLIND, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkBlindList = new ArrayList<BookmarkDto>();
			bookmarkBlindList.add(createBookmark(CHILDBLIND_SVCS, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtBlindVisuallyImpaired()));
			blindGroup.setBookmarkDtoList(bookmarkBlindList);
			formDataGroupList.add(blindGroup);
		}

		/*
		 * HEALTH CARE SUMMARY SECTION - BEHAVIORAL HEALTH SUBSECTION
		 */
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_EMTNL_STRENGTH, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtEmotionalStrengths()));
		bookmarkNoGroupList.add(createBookmark(TRUMATRGG, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtTraumaTriggers()));
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_THRPST_DIAGNSIS, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtTherapistImpressions()));
		bookmarkNoGroupList.add(createBookmark(MNTLBHVRNEEDS, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtMhBhServices()));
		// set the cans radio
		if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCansAssessment())) {
			bookmarkNoGroupList.add(createBookmark(CHILDCANSASSMNTN, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCansAssessment())) {
			bookmarkNoGroupList.add(createBookmark(CHILDCANSASSMNTY, ServiceConstants.CHECKED ));
			FormDataGroupDto cansGroup = createFormDataGroup(TMPLAT_CHILDCANSASSMNT, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkCansList = new ArrayList<BookmarkDto>();
			bookmarkCansList.add(createBookmark(BookmarkConstants.TXT_DT_CANS_ASSMNT, TypeConvUtil.formDateFormat(commonAppShortFormRes.getCommonApplicationShortFormDto().getDtCansAssessment())));
			bookmarkCansList.add(createBookmark(CHILDCANSASSMNT_RECMNDTN, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtCansRecommendations()));
			cansGroup.setBookmarkDtoList(bookmarkCansList);
			formDataGroupList.add(cansGroup);
		}
		bookmarkNoGroupList.add(createBookmark(DTPSYCHOLOGICALEVALTN, TypeConvUtil.formDateFormat(commonAppShortFormRes.getCommonApplicationShortFormDto().getDtPsychologicalEvaluation())));
		bookmarkNoGroupList.add(createBookmark(DTPSYCHIATRICEVALTN, TypeConvUtil.formDateFormat(commonAppShortFormRes.getCommonApplicationShortFormDto().getDtPsychiatricEvaluation())));
		bookmarkNoGroupList.add(createBookmark(CURRNTDIAGNSIS, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtCurrentDiagnosis()));
		// set the mental crisis radio
		if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndMhCrisisPast6months())) {
			bookmarkNoGroupList.add(createBookmark(CHILDMNTLCRISISN, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndMhCrisisPast6months())) {
			bookmarkNoGroupList.add(createBookmark(CHILDMNTLCRISISY, ServiceConstants.CHECKED ));
			FormDataGroupDto mhCrisisGroup = createFormDataGroup(TMPLAT_CHILDMNTLCRISIS, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkMhCrisisList = new ArrayList<BookmarkDto>();
			bookmarkMhCrisisList.add(createBookmark(CHILDMNTLCRISIS_DESCRB, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtMhCrisis()));
			mhCrisisGroup.setBookmarkDtoList(bookmarkMhCrisisList);
			formDataGroupList.add(mhCrisisGroup);
		}
		// set the hospitalization radio
		if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndPsychiatricHospital())) {
			bookmarkNoGroupList.add(createBookmark(PSYHSPTLN, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndPsychiatricHospital())) {
			bookmarkNoGroupList.add(createBookmark(PSYHSPTLY, ServiceConstants.CHECKED ));
			FormDataGroupDto psyHsplGroup = createFormDataGroup(TMPLAT_PSYHSPTL, FormConstants.EMPTY_STRING);
			if (!ObjectUtils.isEmpty(commonAppShortFormRes.getPsychiatricList())
					&& ServiceConstants.Zero < commonAppShortFormRes.getPsychiatricList().size()) {
				List<FormDataGroupDto> hsptlztnGroupsList = new ArrayList<FormDataGroupDto>();
				for (ShortFormPsychiatricDto hsptlztn : commonAppShortFormRes.getPsychiatricList()) {
					FormDataGroupDto hsptlztnGroup = createFormDataGroup(TMPLAT_PSYHSPTL_HIST, TMPLAT_PSYHSPTL);
					List<BookmarkDto> bookmarkHsptlztnList = new ArrayList<BookmarkDto>();
					bookmarkHsptlztnList.add(createBookmark(PSYHSPTL_DTHSPTLZTN, TypeConvUtil.formDateFormat(hsptlztn.getDtHospitalized())));
					bookmarkHsptlztnList.add(createBookmark(PSYHSPTL_LNGTHHSPTLZTN, hsptlztn.getLenHospitalized()));
					bookmarkHsptlztnList.add(createBookmark(PSYHSPTL_BHVRCHILDADMIT, hsptlztn.getBehvAdmitted()));
					hsptlztnGroup.setBookmarkDtoList(bookmarkHsptlztnList);
					hsptlztnGroupsList.add(hsptlztnGroup);
				}
				psyHsplGroup.setFormDataGroupList(hsptlztnGroupsList);
				formDataGroupList.add(psyHsplGroup);
			}



		}

		/*
		 * SUBSTANCE USE OR ABUSE SECTION
		 */
		// set the substance use or abuse radio
		if (TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSubstanceUse())) {
			; // no default response
		} else {
			if (ServiceConstants.UNKNOWN1.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSubstanceUse())) {
				bookmarkNoGroupList.add(createBookmark(SUBSTNCABUSEUNK, ServiceConstants.CHECKED ));
			} else // artf146754 populate No checkbox bookmark from indicator substance use
				if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSubstanceUse())) {
					bookmarkNoGroupList.add(createBookmark(SUBSTNCABUSEN, ServiceConstants.CHECKED ));
				} else {
					bookmarkNoGroupList.add(createBookmark(SUBSTNCABUSEY, ServiceConstants.CHECKED ));
					FormDataGroupDto substanceGroup = createFormDataGroup(TMPLAT_SUBSTNCABUSE, FormConstants.EMPTY_STRING);
					if (!ObjectUtils.isEmpty(substanceList) && ServiceConstants.Zero < substanceList.size()) {

						List<FormDataGroupDto> substanceGroupsList = new ArrayList<FormDataGroupDto>();
						for (CaExSubstanceUse substanceUse : commonApplShortFormPrintDto.getSubstanceList()) {
							FormDataGroupDto substHistGroup = createFormDataGroup(TMPLAT_SUBSTNCABUSE_HIST, TMPLAT_SUBSTNCABUSE);
							List<BookmarkDto> bookmarkSubstanceList = new ArrayList<BookmarkDto>();
							bookmarkSubstanceList.add(createBookmark(SUBSTNCABUSE_HIST_SUBSTANCE, getSubstance(substanceUse.getCdSubstanceType())));
							// set the substance use or abuse hist radio
							if (TypeConvUtil.isNullOrEmpty(substanceUse.getIndSubstanceUse())
									|| ServiceConstants.UNKNOWN1.equals(substanceUse.getIndSubstanceUse())) {
								substanceUse.setIndSubstanceUse(ServiceConstants.UNKNOWN1);
								bookmarkSubstanceList.add(createBookmark(SUBSTNCABUSE_HIST_USE, getYesOrNoText(substanceUse.getIndSubstanceUse())));
							} else {
								bookmarkSubstanceList.add(createBookmark(SUBSTNCABUSE_HIST_USE, getYesOrNoText(substanceUse.getIndSubstanceUse())));
							}
							bookmarkSubstanceList.add(createBookmark(SUBSTNCABUSE_HIST_AGE, substanceUse.getNbrAgeFirstUse()));
							bookmarkSubstanceList.add(createBookmark(SUBSTNCABUSE_HIST_FREQ, substanceUse.getTxtFrequencyOfUse()));
							bookmarkSubstanceList.add(createBookmark(SUBSTNCABUSE_HIST_APPROXDT, TypeConvUtil.formDateFormat(substanceUse.getDtUseLast())));
							substHistGroup.setBookmarkDtoList(bookmarkSubstanceList);
							substanceGroupsList.add(substHistGroup);
						}
						// Add group and bookmarks to therapy group
						substanceGroup.setFormDataGroupList(substanceGroupsList);
						formDataGroupList.add(substanceGroup);
					}
				}
		}

		/*
		 * YOUTH WHO ARE PREGNANT OR PARENTING SECTION
		 */
		// set the pregnant radio
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCurrentPregnant())) {
			; // no default response
		} else
		if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCurrentPregnant())) {
			bookmarkNoGroupList.add(createBookmark(YOUTHPREGNANTN, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCurrentPregnant())) {
			bookmarkNoGroupList.add(createBookmark(YOUTHPREGNANTY, ServiceConstants.CHECKED ));
			FormDataGroupDto pregnantGroup = createFormDataGroup(TMPLAT_YOUTHPREGNANT, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkPregnantList = new ArrayList<BookmarkDto>();
			bookmarkPregnantList.add(createBookmark(YOUTHPREGNANT_BABYDUE, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtBabyDue()));
			bookmarkPregnantList.add(createBookmark(YOUTHPREGNANT_BABYPLAN, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtPlanForBaby()));
			pregnantGroup.setBookmarkDtoList(bookmarkPregnantList);
			formDataGroupList.add(pregnantGroup);
		}
		// set the currently parent radio
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCurrentParent())) {
			; // no default response
		} else
		if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCurrentParent())) {
			bookmarkNoGroupList.add(createBookmark(YOUTHPARENTN, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCurrentParent())) {
			bookmarkNoGroupList.add(createBookmark(YOUTHPARENTY, ServiceConstants.CHECKED ));
			FormDataGroupDto crrntParentGroup = createFormDataGroup(TMPLAT_YOUTHPARENT, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkCrrntParentList = new ArrayList<BookmarkDto>();
			bookmarkCrrntParentList.add(createBookmark(YOUTHPARENT_RESIDE, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtChildReside()));
			bookmarkCrrntParentList.add(createBookmark(YOUTHPARENT_ROLE, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtParentingRole()));
			crrntParentGroup.setBookmarkDtoList(bookmarkCrrntParentList);
			formDataGroupList.add(crrntParentGroup);
		}

		/*
		 * RISK BEHAVIOR SECTION - SELF HARM/SUICIDE HISTORY SUBSECTION
		 */
		// set the self harm radio
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSelfHarmingBehavior())) {
			; // no default response
		} else
		if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSelfHarmingBehavior())) {
			bookmarkNoGroupList.add(createBookmark(RISKBHVRSELFHARMN, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSelfHarmingBehavior())) {
			bookmarkNoGroupList.add(createBookmark(RISKBHVRSELFHARMY, ServiceConstants.CHECKED ));
			FormDataGroupDto riskBhvrGroup = createFormDataGroup(TMPLAT_RISKBHVR, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRiskBhvrList = new ArrayList<BookmarkDto>();
			bookmarkRiskBhvrList.add(createBookmark(RISKBHVR_DESCRB, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtSelfHarmingBehavior()));
			riskBhvrGroup.setBookmarkDtoList(bookmarkRiskBhvrList);
			formDataGroupList.add(riskBhvrGroup);
		}
		// set the suicidal ideations radio
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSuicidalAttempts())) {
			; // no default response
		} else
		if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSuicidalAttempts())) {
			bookmarkNoGroupList.add(createBookmark(RISKBHVRSUICDEATTMPN, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSuicidalAttempts())) {
			bookmarkNoGroupList.add(createBookmark(RISKBHVRSUICDEATTMPY, ServiceConstants.CHECKED ));
		}

		/*
		 * RISK BEHAVIOR SECTION - RUNAWAY HISTORY SUBSECTION
		 */
		// set the runaway radio
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndHistRunaway())) {
			; // no default response
		} else
		if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndHistRunaway())) {
			bookmarkNoGroupList.add(createBookmark(RUNAWAYN, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndHistRunaway())) {
			bookmarkNoGroupList.add(createBookmark(RUNAWAYY, ServiceConstants.CHECKED ));
			FormDataGroupDto runawayGroup = createFormDataGroup(TMPLAT_RUNAWAY, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRunawayList = new ArrayList<BookmarkDto>();
			bookmarkRunawayList.add(createBookmark(RUNAWAY_HIST, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtHistRunaway()));
			bookmarkRunawayList.add(createBookmark(RUNAWAY_LAST, TypeConvUtil.formDateFormat(commonAppShortFormRes.getCommonApplicationShortFormDto().getDtLastRunaway())));
			bookmarkRunawayList.add(createBookmark(RUNAWAY_EPISODE, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtHistRunaway()));
			// artf141314 populate not null runaway return values
			bookmarkRunawayList.add(createBookmark(RUNAWAY_VLNTRY, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getRtnRunaway().getVoluntary())
					? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
			bookmarkRunawayList.add(createBookmark(RUNAWAY_TYCSTF, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getRtnRunaway().getTycStaff())
					? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
			bookmarkRunawayList.add(createBookmark(RUNAWAY_FCLTYSTF, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getRtnRunaway().getFacilityStaff())
					? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
			bookmarkRunawayList.add(createBookmark(RUNAWAY_JPCSTF, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getRtnRunaway().getJpcStaff())
					? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
			bookmarkRunawayList.add(createBookmark(RUNAWAY_CPSSTF, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getRtnRunaway().getCpsStaff())
					? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
			bookmarkRunawayList.add(createBookmark(RUNAWAY_ICJSTF, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getRtnRunaway().getIcjStaff())
					? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
			bookmarkRunawayList.add(createBookmark(RUNAWAY_LOCALLE, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getRtnRunaway().getLocalLawEnforcement())
					? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
			bookmarkRunawayList.add(createBookmark(RUNAWAY_OTHR, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getRtnRunaway().getOtherRunaway())
					? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
			bookmarkRunawayList.add(createBookmark(RUNAWAY_OTHRSPEC, commonAppShortFormRes.getRtnRunaway().getTxtRunaway()));
			runawayGroup.setBookmarkDtoList(bookmarkRunawayList);
			formDataGroupList.add(runawayGroup);
		}

		/*
		 * RISK BEHAVIOR SECTION - OTHER SIGNIFICANT PROBLEMS AND/OR BEHAVIORS SUBSECTION
		 */
		// set the setting fires radio
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndHistSettingFires())) {
			; // no default response
		} else
		if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndHistSettingFires())) {
			bookmarkNoGroupList.add(createBookmark(SETTGFIREHISTN, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndHistSettingFires())) {
			bookmarkNoGroupList.add(createBookmark(SETTGFIREHISTY, ServiceConstants.CHECKED ));
			FormDataGroupDto riskBhvrGroup = createFormDataGroup(TMPLAT_SETTINGFIRE, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRiskBhvrList = new ArrayList<BookmarkDto>();
			bookmarkRiskBhvrList.add(createBookmark(SETTINGFIRE_DESCRB, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtHistSettingFires()));
			riskBhvrGroup.setBookmarkDtoList(bookmarkRiskBhvrList);
			formDataGroupList.add(riskBhvrGroup);
		}
		// set the animal cruelty radio
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndHistCrueltyToAnimals())) {
			; // no default response
		} else
		if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndHistCrueltyToAnimals())) {
			bookmarkNoGroupList.add(createBookmark(ANIMALCRULHISTN, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndHistCrueltyToAnimals())) {
			bookmarkNoGroupList.add(createBookmark(ANIMALCRULHISTY, ServiceConstants.CHECKED ));
			FormDataGroupDto riskBhvrGroup = createFormDataGroup(TMPLAT_ANIMALCRULHIST, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRiskBhvrList = new ArrayList<BookmarkDto>();
			bookmarkRiskBhvrList.add(createBookmark(ANIMALCRULHIST_DESCRB, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtHistCrueltyToAnimals()));
			riskBhvrGroup.setBookmarkDtoList(bookmarkRiskBhvrList);
			formDataGroupList.add(riskBhvrGroup);
		}
		// set the other significant problems radio
		if (ObjectUtils.isEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndOtherBehaviorProblems())) {
			; // no default response
		} else
		if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndOtherBehaviorProblems())) {
			bookmarkNoGroupList.add(createBookmark(SIGNFCNTPRBMN, ServiceConstants.CHECKED ));
		} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndOtherBehaviorProblems())) {
			bookmarkNoGroupList.add(createBookmark(SIGNFCNTPRBMY, ServiceConstants.CHECKED ));
			FormDataGroupDto riskBhvrGroup = createFormDataGroup(TMPLAT_SIGNFCNTPRBM, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRiskBhvrList = new ArrayList<BookmarkDto>();
			bookmarkRiskBhvrList.add(createBookmark(SIGNFCNTPRBM_DESCRB, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtOtherBehaviorProblems()));
			riskBhvrGroup.setBookmarkDtoList(bookmarkRiskBhvrList);
			formDataGroupList.add(riskBhvrGroup);
		}

		/*
		 * SEXUALIZED BEHAVIOURS  SECTION
		 */
		// set the sxaggrssn radio
		if (TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSexualAggresBehavior())) {
			; // no default response
		} else {
			if (ServiceConstants.UNKNOWN1.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSexualAggresBehavior())) {
				bookmarkNoGroupList.add(createBookmark(SXAGGRSSNUNK, ServiceConstants.CHECKED ));
			} else if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSexualAggresBehavior())) {
				bookmarkNoGroupList.add(createBookmark(SXAGGRSSNN, ServiceConstants.CHECKED ));
			} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSexualAggresBehavior())) {
				bookmarkNoGroupList.add(createBookmark(SXAGGRSSNY, ServiceConstants.CHECKED ));
				// populate sxaggrssn description
				FormDataGroupDto sxaggrssnGroup = createFormDataGroup(TMPLAT_SXAGGRSSN, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSxaggrssn = new ArrayList<BookmarkDto>();
				bookmarkSxaggrssn.add(createBookmark(SXAGGRSSN_DESCR, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtSexualAggresBehavior()));
				sxaggrssnGroup.setBookmarkDtoList(bookmarkSxaggrssn);
				// populate sxaggrssn list
				if (!ObjectUtils.isEmpty(commonAppShortFormRes.getSfEpisodeIncdntDtls())
						&& ServiceConstants.Zero < commonAppShortFormRes.getSfEpisodeIncdntDtls().size()){
					// populate sxaggrssn episode and incident date and description
					List<FormDataGroupDto> sxaggrssnHistList = new ArrayList<FormDataGroupDto>();
					for (ShortFormCsaEpisodeIncdntsDto sxaggrssnIncdntDto : commonAppShortFormRes.getSfEpisodeIncdntDtls()) {
						FormDataGroupDto sxAggIncdntGroup = createFormDataGroup(TMPLAT_SXAGGRSSN_HIST, TMPLAT_SXAGGRSSN);
						List<BookmarkDto> bookmarkSxAggIncdntList = new ArrayList<BookmarkDto>();
						bookmarkSxAggIncdntList.add(createBookmark(SXAGGRSSN_HIST_EPSTDT, TypeConvUtil.formDateFormat(sxaggrssnIncdntDto.getDtEpisodeSt())));
						bookmarkSxAggIncdntList.add(createBookmark(SXAGGRSSN_HIST_EPENDT, TypeConvUtil.formDateFormat(sxaggrssnIncdntDto.getDtEpisodeEnd())));
						bookmarkSxAggIncdntList.add(createBookmark(SXAGGRSSN_HIST_INCDDT, TypeConvUtil.formDateFormat(sxaggrssnIncdntDto.getDtIncdnt())));
						if (!TypeConvUtil.isNullOrEmpty(sxaggrssnIncdntDto.getIndAppxDt())
								&& !ServiceConstants.N.equals(sxaggrssnIncdntDto.getIndAppxDt())) {
							bookmarkSxAggIncdntList.add(createBookmark(SXAGGRSSN_HIST_DTAPPROX, APPROXIMATE_DATE));
						}
						bookmarkSxAggIncdntList.add(createBookmark(SXAGGRSSN_HIST_DESCR,
								!TypeConvUtil.isNullOrEmpty(sxaggrssnIncdntDto.getTxtIncdntDesc())
										? sxaggrssnIncdntDto.getTxtIncdntDesc() : ServiceConstants.EMPTY_STRING));
						sxAggIncdntGroup.setBookmarkDtoList(bookmarkSxAggIncdntList);
						sxaggrssnHistList.add(sxAggIncdntGroup);
					}
					sxaggrssnGroup.setFormDataGroupList(sxaggrssnHistList);
					formDataGroupList.add(sxaggrssnGroup);
				}
			}
		}

		if (!TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSexualBehaviorProblem())) {
			if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSexualBehaviorProblem())) {
				bookmarkNoGroupList.add(createBookmark(SXBHVRN, ServiceConstants.CHECKED ));
				bookmarkNoGroupList.add(createBookmark(SXBHVPROBTXTHIDDEN, "visuallyhidden"));
			} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndSexualBehaviorProblem())) {
				bookmarkNoGroupList.add(createBookmark(SXBHVRY, ServiceConstants.CHECKED));
				bookmarkNoGroupList.add(createBookmark(SXBHVPROBTXT, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtSexualBehaviorProblem()));
			}

		}
		/*
		 * EDUCATION SECTION
		 */
		if (ServiceConstants.N.equalsIgnoreCase(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCurrentlyEnrolSchool())) {
			bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_EDU_CURR_ENRLLD_SCH_NO, ServiceConstants.CHECKED));
			// populate education not currently enrolled
			FormDataGroupDto eduNotCurrentGroup = createFormDataGroup(TMPLAT_EDULASTSCH, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkEduNotCurrent = new ArrayList<>();
			bookmarkEduNotCurrent.add(createBookmark(EDULASTSCH_NM, commonAppShortFormRes.getCommonApplicationShortFormDto().getNmLastSchoolAttended()));
			bookmarkEduNotCurrent.add(createBookmark(EDULASTSCH_ADDR, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtLastSchoolAddress()));
			bookmarkEduNotCurrent.add(createBookmark(EDULASTSCH_CONTACT, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtLastSchoolContact()));
			eduNotCurrentGroup.setBookmarkDtoList(bookmarkEduNotCurrent);
			formDataGroupList.add(eduNotCurrentGroup);
		} else if (ServiceConstants.Y.equalsIgnoreCase(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCurrentlyEnrolSchool())) {
			bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_EDU_CURR_ENRLLD_SCH_YES, ServiceConstants.CHECKED));
			// populate education currently enrolled
			FormDataGroupDto eduGroup = createFormDataGroup(TMPLAT_EDU, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkEdu = new ArrayList<>();
			bookmarkEdu.add(createBookmark(EDU_SCHNM, commonAppShortFormRes.getCommonApplicationShortFormDto().getNmSchool()));
			bookmarkEdu.add(createBookmark(EDU_SCHADDR, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtAddressSchool()));
			bookmarkEdu.add(createBookmark(EDU_SCHCITY, commonAppShortFormRes.getCommonApplicationShortFormDto().getNmCitySchool()));
			bookmarkEdu.add(createBookmark(EDU_SCHSTATE, commonAppShortFormRes.getCommonApplicationShortFormDto().getNmStateSchool()));
			bookmarkEdu.add(createBookmark(EDU_WITHDRAWNDT, TypeConvUtil.formDateFormat(commonAppShortFormRes.getCommonApplicationShortFormDto().getDtWithdrawn())));
			bookmarkEdu.add(createBookmark(EDU_CNCTNM, commonAppShortFormRes.getCommonApplicationShortFormDto().getNmSchoolContact()));
			// artf146886 null check contact phone
			if (!TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getNbrSchoolContactPhone())) {
				StringBuilder contactPhone = new StringBuilder(TypeConvUtil.formatPhone(commonAppShortFormRes.getCommonApplicationShortFormDto().getNbrSchoolContactPhone().toString()));
				if (!TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getNbrSchoolContactPhoneExt())) {
					contactPhone.append(" Ext ").append(commonAppShortFormRes.getCommonApplicationShortFormDto().getNbrSchoolContactPhoneExt());
				} // artf146922 Extension sh/be Ext
				bookmarkEdu.add(createBookmark(EDU_CNCTPH, contactPhone.toString()));
			}
			bookmarkEdu.add(createBookmark(EDU_CNCTEMAIL, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtSchoolContactEmail()));
			bookmarkEdu.add(createBookmark(EDU_CURRGRADE, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtCurrentGrade()));
			bookmarkEdu.add(createBookmark(EDU_GRADEASDT, TypeConvUtil.formDateFormat(commonAppShortFormRes.getCommonApplicationShortFormDto().getDtCurrentGrade())));
			if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndOnGradeLevel())) {
				bookmarkEdu.add(createBookmark(EDU_GRADELVLYES, ServiceConstants.CHECKED));
			} else if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndOnGradeLevel())) {
				bookmarkEdu.add(createBookmark(EDU_GRADELVLNO, ServiceConstants.CHECKED));
			}
			eduGroup.setBookmarkDtoList(bookmarkEdu);
			formDataGroupList.add(eduGroup);
		}

		// populate education services
		bookmarkNoGroupList.add(createBookmark(EDUSVCSDTRGLR, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getEduSrv().getRegularClass())
				? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
		bookmarkNoGroupList.add(createBookmark(EDUSVCSDTBILANG, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getEduSrv().getBilingualESL())
				? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
		bookmarkNoGroupList.add(createBookmark(EDUSVCSDTGIFT, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getEduSrv().getGiftedTalented())
				? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
		bookmarkNoGroupList.add(createBookmark(EDUSVCSDTSELFCONTND, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getEduSrv().getSelfContained())
				? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
		bookmarkNoGroupList.add(createBookmark(EDUSVCSDTSPLTRANS, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getEduSrv().getSplTransport())
				? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
		bookmarkNoGroupList.add(createBookmark(EDUSVCSDTCREDREC, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getEduSrv().getCreditRecovery())
				? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
		bookmarkNoGroupList.add(createBookmark(EDUSVCSDTVOCTNL, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getEduSrv().getVocational())
				? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
		bookmarkNoGroupList.add(createBookmark(EDUSVCSDTCONSEL, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getEduSrv().getCounselingSrv())
				? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
		bookmarkNoGroupList.add(createBookmark(EDUSVCSDTADVNCPLCMNT, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getEduSrv().getAdvPlacement())
				? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
		bookmarkNoGroupList.add(createBookmark(EDUSVCSDTSPLEDUCTN, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getEduSrv().getSplEducation())
				? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
		bookmarkNoGroupList.add(createBookmark(EDUSVCSDTDAEP, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getEduSrv().getDaepJjaep())
				? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
		bookmarkNoGroupList.add(createBookmark(EDUSERDTOTHR, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getEduSrv().getOtherSpec())
				? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
		bookmarkNoGroupList.add(createBookmark(EDUSERDTOTHRSPEC, commonAppShortFormRes.getEduSrv().getOtherTxt()));
		bookmarkNoGroupList.add(createBookmark(EDUSVCSDTSELFPACED, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getEduSrv().getSelfPaced())
				? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
		bookmarkNoGroupList.add(createBookmark(EDUSVCSDT504, !TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getEduSrv().getModification504())
				? ServiceConstants.CHECKED : ServiceConstants.EMPTY_STRING));
		// populate truancy radio
		if (TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndHistoryOfTruancy())) {
			;  // no default response
		} else {
			if (ServiceConstants.UNKNOWN1.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndHistoryOfTruancy())) {
				bookmarkNoGroupList.add(createBookmark(EDUTRUANCYHISTUNK, ServiceConstants.CHECKED ));
			} else
			if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndHistoryOfTruancy())) {
				bookmarkNoGroupList.add(createBookmark(EDUTRUANCYHISTN, ServiceConstants.CHECKED ));
			} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndHistoryOfTruancy())) {
				bookmarkNoGroupList.add(createBookmark(EDUTRUANCYHISTY, ServiceConstants.CHECKED ));
			}
		}

		/*
		 * TRANSITION PLANNING FOR A SUCCESSFUL ADULTHOOD (PAL) SECTION
		 */
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_DESCRB_LIFE_SKILLLS_STRENGTH,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtLifeSkillProgress()));
		if (ServiceConstants.Y.equalsIgnoreCase(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndLifeSkillAssessment())) {
			bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_COMP_LIFE_SKILLLS_ASSMNT_YES, ServiceConstants.CHECKED));
		} else if (ServiceConstants.N.equalsIgnoreCase(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndLifeSkillAssessment())) {
			bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_COMP_LIFE_SKILLLS_ASSMNT_NO, ServiceConstants.CHECKED));
		}
		if (TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndPalSkillTraining())) {
			; // no default response
		} else {
			if (ServiceConstants.UNKNOWN1.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndPalSkillTraining())) {
				bookmarkNoGroupList.add(createBookmark(COMPPALTRNGSKILLUNK, ServiceConstants.CHECKED ));
			} else
			if (ServiceConstants.Y.equalsIgnoreCase(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndPalSkillTraining())) {
				bookmarkNoGroupList.add(createBookmark(COMPPALTRNGSKILLY, ServiceConstants.CHECKED));
			} else if (ServiceConstants.N.equalsIgnoreCase(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndPalSkillTraining())) {
				bookmarkNoGroupList.add(createBookmark(COMPPALTRNGSKILLN, ServiceConstants.CHECKED));
			}
		}
		bookmarkNoGroupList.add(createBookmark(DESCRBREGNLPALLSTF,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtRegionalPalStaff()));
		if (ServiceConstants.Y.equalsIgnoreCase(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCircleOfSupport())) {
			bookmarkNoGroupList.add(createBookmark(CHILDCIRCLSUPRTY, ServiceConstants.CHECKED));
		} else if (ServiceConstants.N.equalsIgnoreCase(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndCircleOfSupport())) {
			bookmarkNoGroupList.add(createBookmark(CHILDCIRCLSUPRTN, ServiceConstants.CHECKED));
		}
		bookmarkNoGroupList.add(createBookmark(BookmarkConstants.TXT_DESCRB_YOUTH_STILL_FSTRCARE,
				commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtExtendedFcOptions()));

		/*
		 * JUVENILE JUSTICE INVOLVEMENT SECTION
		 */
		if (ServiceConstants.N.equalsIgnoreCase(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndJjInvolvement())) {
			bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_JUVNLE_JUSTICE_HIST_NO, ServiceConstants.CHECKED));
		} else if (ServiceConstants.Y.equalsIgnoreCase(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndJjInvolvement())) {
			bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_JUVNLE_JUSTICE_HIST_YES, ServiceConstants.CHECKED));
			// artf142789 description text needs to be in conditional group
			FormDataGroupDto JjInvGroup = createFormDataGroup(TMPLAT_JUVNLE, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkJjInvList = new ArrayList<>();
			bookmarkJjInvList.add(createBookmark(JUVNLE_INVOLVEMENT, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtJjInvolvement()));
			JjInvGroup.setBookmarkDtoList(bookmarkJjInvList);
			formDataGroupList.add(JjInvGroup);
		}


		/*
		 * FAMILY HISTORY SECTION
		 */
		if (!ObjectUtils.isEmpty(commonAppShortFormRes.getSiblingList())
				&& 0 < commonAppShortFormRes.getSiblingList().size()) {
			for (ShortFormSiblingsDto sibling : commonAppShortFormRes.getSiblingList()) {
				FormDataGroupDto siblingGroup = createFormDataGroup(TMPLAT_FAMILY, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSiblingList = new ArrayList<>();
				bookmarkSiblingList.add(createBookmark(FAMILY_SBLNGS, sibling.getSiblingsName()));
				bookmarkSiblingList.add(createBookmark(FAMILY_INCARE, getYesOrNoText(sibling.getDfpsCare())));
				bookmarkSiblingList.add(createBookmark(FAMILY_DATEOB, TypeConvUtil.formDateFormat(sibling.getDob())));
				bookmarkSiblingList.add(createBookmark(FAMILY_ADDRES, sibling.getAddress()));
				siblingGroup.setBookmarkDtoList(bookmarkSiblingList);
				formDataGroupList.add(siblingGroup);
			}
		}

		/*
		 * PLACEMENT HISTORY SECTION
		 */
		// set child or youth been previously placed out of the home radio
		if (TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndPlacedOutOfHome())) {
			; // no default response
		} else {
			if (ServiceConstants.UNKNOWN1.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndPlacedOutOfHome())) {
				bookmarkNoGroupList.add(createBookmark(PLCMNTPLCDHMEOUTUNK, ServiceConstants.CHECKED));
			} else if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndPlacedOutOfHome())) {
				bookmarkNoGroupList.add(createBookmark(PLCMNTPLCDHMEOUTN, ServiceConstants.CHECKED));
			} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndPlacedOutOfHome())) {
				bookmarkNoGroupList.add(createBookmark(PLCMNTPLCDHMEOUTY, ServiceConstants.CHECKED));
				// number of previous out-of-home placements
				FormDataGroupDto placedOutHomeGroup = createFormDataGroup(TMPLAT_NOPREVHOME, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPlacedOut = new ArrayList<>();
				bookmarkPlacedOut.add(createBookmark(NOPREVHOME_OUTPLCMNT, commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtPlacedOutOfHome()));
				placedOutHomeGroup.setBookmarkDtoList(bookmarkPlacedOut);
				formDataGroupList.add(placedOutHomeGroup);
			}
		}

		// set child or youth been previously adopted domestically radio
		if (TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndAdoptedDomestically())) {
			; // no default response
		} else {
			if (ServiceConstants.UNKNOWN1.equalsIgnoreCase(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndAdoptedDomestically())) {
				bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_PREV_ADO_DMSTC_UNDTRMN, ServiceConstants.CHECKED));
			} else
			if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndAdoptedDomestically())) {
				bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_PREV_ADO_DMSTC_NO, ServiceConstants.CHECKED));
			} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndAdoptedDomestically())) {
				bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_PREV_ADO_DMSTC_YES, ServiceConstants.CHECKED));
				// where was the domestic adoption consummated
				FormDataGroupDto whrCnsmtdGroup = createFormDataGroup(TMPLAT_DMSTC, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkWhrConsmtd = new ArrayList<>();
				bookmarkWhrConsmtd.add(createBookmark(BookmarkConstants.TXT_WR_ADOPTN_CNSMTED_DMSTC,
						commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtDomesticConsummated()));
				whrCnsmtdGroup.setBookmarkDtoList(bookmarkWhrConsmtd);
				formDataGroupList.add(whrCnsmtdGroup);
			}
		}

		// set child or youth been previously adopted internationally radio
		if (TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndAdoptedInternationally())) {
			; // no default response
		} else {
			if (ServiceConstants.UNKNOWN1.equalsIgnoreCase(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndAdoptedInternationally())) {
				bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_PREV_ADO_INTRNTNL_UNDTRMN, ServiceConstants.CHECKED));
			} else
			if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndAdoptedInternationally())) {
				bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_PREV_ADO_INTRNTNL_NO, ServiceConstants.CHECKED));
			} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndAdoptedInternationally())) {
				bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_PREV_ADO_INTRNTNL_YES, ServiceConstants.CHECKED));
				// where was the int'l adoption consummated
				FormDataGroupDto intlGroup = createFormDataGroup(TMPLAT_INTRNTNL, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkIntl = new ArrayList<>();
				bookmarkIntl.add(createBookmark(BookmarkConstants.TXT_WR_ADOPTN_CNSMTED_INTRNTNL,
						commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtInternConsummated()));
				intlGroup.setBookmarkDtoList(bookmarkIntl);
				formDataGroupList.add(intlGroup);
			}
		}

		// set the parents previously had legal custody of the child radio
		if (TypeConvUtil.isNullOrEmpty(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndOtherLegalCustody())) {
			; // no default response
		} else {
			if (ServiceConstants.UNKNOWN1.equalsIgnoreCase(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndOtherLegalCustody())) {
				bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_PREV_LGL_CSTDY_UNDTRMN, ServiceConstants.CHECKED));
			} else
			if (ServiceConstants.N.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndOtherLegalCustody())) {
				bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_PREV_LGL_CSTDY_NO, ServiceConstants.CHECKED));
			} else if (ServiceConstants.Y.equals(commonAppShortFormRes.getCommonApplicationShortFormDto().getIndOtherLegalCustody())) {
				bookmarkNoGroupList.add(createBookmark(BookmarkConstants.CHK_PREV_LGL_CSTDY_YES, ServiceConstants.CHECKED));
				// if yes, who and when
				FormDataGroupDto intlGroup = createFormDataGroup(TMPLAT_PRERLGLCUSTD, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkIntl = new ArrayList<>();
				bookmarkIntl.add(createBookmark(BookmarkConstants.TXT_DESCRB_PREV_LGL_CSTDY,
						commonAppShortFormRes.getCommonApplicationShortFormDto().getTxtOtherLegalCustody()));
				intlGroup.setBookmarkDtoList(bookmarkIntl);
				formDataGroupList.add(intlGroup);
			}
		}
		// placement log
		if (!ObjectUtils.isEmpty(commonAppShortFormRes.getPlcmtLog())
				&& 0 < commonAppShortFormRes.getPlcmtLog().size()) {
			for (PlacementDtlDto plcmt : commonAppShortFormRes.getPlcmtLog()) {
				FormDataGroupDto plcmtGroup = createFormDataGroup(TMPLAT_PLCMNT, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPlcmtList = new ArrayList<>();
				// set placement name as facility name or placement person name
				bookmarkPlcmtList.add(createBookmark(BookmarkConstants.TXT_PLCMNT_NAME, !TypeConvUtil.isNullOrEmpty(plcmt.getNmPlcmtFacil())
						? plcmt.getNmPlcmtFacil() : plcmt.getNmPlcmtPersonFull()));
				// set placement type according to living arrangement and facility, etc.
				bookmarkPlcmtList.add(createBookmarkWithCodesTable(BookmarkConstants.TXT_PLCMNT_TYPE,
						plcmt.getCdPlcmtLivArr(), CodesConstant.CPLLAFRM));
				bookmarkPlcmtList.add(createBookmark(PLCMNT_STARTDT, TypeConvUtil.formDateFormat(plcmt.getDtPlcmtStart())));
				// prevent the Default Placement End Date to be printed in case the placement is not end-dated
				String placementEndDate = TypeConvUtil.formDateFormat(plcmt.getDtPlcmtEnd());
				if(!ServiceConstants.STAGE_OPEN_DT.equals(placementEndDate))
				{
					bookmarkPlcmtList.add(createBookmark(PLCMNT_ENDDT, TypeConvUtil.formDateFormat(plcmt.getDtPlcmtEnd())));
				}

				plcmtGroup.setBookmarkDtoList(bookmarkPlcmtList);
				formDataGroupList.add(plcmtGroup);
			}
		}
		// End -PLACEMENT HISTORY


		// return prefilldata
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkNoGroupList);
		preFillData.setFormDataGroupList(formDataGroupList);
		return preFillData;
	}


	/**
	 * get Yes or No or Unknown map.
	 */
	private String getYesOrNoText(String indicator) {
		Map<String, String> ind = new HashMap<String, String>();

		ind.put("Y", "Yes");
		ind.put("N", "No");
		ind.put("U", "Unknown");
		return ind.get(indicator);
	}

	/**
	 * get substance map.
	 */
	private String getSubstance(String substance) {
		Map<String, String> sbstnc = new HashMap<String, String>();

		sbstnc.put("ALC", "Alcohol");
		sbstnc.put("MRJ", "Marijuana");
		sbstnc.put("INH", "Inhalants");
		sbstnc.put("COC", "Cocaine/Crack");
		sbstnc.put("HRN", "Heroin");
		sbstnc.put("MTH", "Methamphetamines");
		sbstnc.put("OPI", "Opioids");
		sbstnc.put("ECS", "Ecstasy");
		sbstnc.put("DRG", "Prescription Drugs");
		return sbstnc.get(substance);
	}

	/**
	 * get therapy map.
	 */
	private String getTherapy(String therapy) {
		Map<String, String> thrp = new HashMap<String, String>();

		thrp.put("ST", "Speech-Language Therapy");
		thrp.put("OT", "Occupational Therapy");
		thrp.put("PT", "Physical Therapy");
		return thrp.get(therapy);
	}

	/*
	 * TITLE SECTION BOOKMARKS
	 */
	private static final String HEADER_DIRECTOR_TITLE = "HEADER_DIRECTOR_TITLE";
	private static final String HEADER_DIRECTOR_NAME = "HEADER_DIRECTOR_NAME";

	/*
	 * DFPS CASEWORKER INFORMATION SECTION BOOKMARKS
	 */
	private static final String CSWRKR_NAME = "CSWRKR_NAME";

	/*
	 * CHILDS INFORMATION SECTION BOOKMARKS
	 */
	private static final String CHILD_RMVLDATE = "CHILD_RMVLDATE";
	private static final String SPCPRGNT = "SPCPRGNT";
	private static final String SPCPRNTNG = "SPCPRNTNG";
	private static final String SPCNONE = "SPCNONE";

	/*
	 * TRAUMA HISTORY SECTION BOOKMARKS
	 */
	private static final String SXVCTMZTNY = "SXVCTMZTNY";
	private static final String SXVCTMZTNN = "SXVCTMZTNN";
	private static final String UNCSXVCTMZTNY = "UNCSXVCTMZTNY";
	private static final String UNCSXVCTMZTNN = "UNCSXVCTMZTNN";
	private static final String SXVCTMZTNUNK = "SXVCTMZTNYUNK";
	private static final String TMPLAT_SXVCTMZTN = "TMPLAT_SXVCTMZTN";
	private static final String SXVCTMZTN_RMVL = "SXVCTMZTN_RMVL";
	private static final String SXVCTMZTN_HIST = "SXVCTMZTN_HIST";
	private static final String SXVCTMZTN_OTHR = "SXVCTMZTN_OTHR";
	private static final String TMPLAT_SXVCTMZTN_HIST = "TMPLAT_SXVCTMZTN_HIST";
	private static final String SXVCTMZTN_HIST_DT = "SXVCTMZTN_HIST_DT";
	private static final String SXVCTMZTN_HIST_DTAPPROX = "SXVCTMZTN_HIST_DTAPPROX";
	private static final String SXVCTMZTN_HIST_DESCR = "SXVCTMZTN_HIST_DESCR";

	/*
	 * TRAFFICKING HISTORY SECTION BOOKMARKS
	 */
	private static final String SXTRSUSPY = "SXTRSUSPY";
	private static final String SXTRSUSPN = "SXTRSUSPN";
	private static final String SXTRSUSPUNK = "SXTRSUSPUNK";
	private static final String SXTRCONFY = "SXTRCONFY";
	private static final String SXTRCONFN = "SXTRCONFN";
	private static final String SXTRCONFUNK = "SXTRCONFUNK";
	private static final String LBTRSUSPY = "LBTRSUSPY";
	private static final String LBTRSUSPN = "LBTRSUSPN";
	private static final String LBTRCONFY = "LBTRCONFY";
	private static final String LBTRCONFN = "LBTRCONFN";
	private static final String TMPLAT_TRFCKNGABUSE_SXCONF = "TMPLAT_TRFCKNGABUSE_SXCONF";

	/*
	 * HEALTH CARE SUMMARY SECTION BOOKMARKS - MEDICAL INFORMATION SUBSECTION
	 */
	private static final String HLTHCAREDENTEXAM = "HLTHCAREDENTEXAM";
	private static final String HLTHCARETBTEST = "HLTHCARETBTEST";
	private static final String HLTHCAREALLERGIES = "HLTHCAREALLERGIES";
	private static final String HLTHCAREMEDCTN = "HLTHCAREMEDCTN";
	private static final String HLTHCAREINDMEDS = "HLTHCAREINDMEDS";
	private static final String TMPLAT_MEDINFO = "TMPLAT_MEDINFO";
	private static final String TMPLAT_MEDINFO_MEDS = "TMPLAT_MEDINFO_MEDS";
	private static final String MEDINFO_MEDS_MEDCTNNAME = "MEDINFO_MEDS_MEDCTNNAME";
	private static final String MEDINFO_MEDS_DOSAGE = "MEDINFO_MEDS_DOSAGE";
	private static final String MEDINFO_MEDS_FREQ = "MEDINFO_MEDS_FREQ";
	private static final String MEDINFO_MEDS_DTPRESCRIBED = "MEDINFO_MEDS_DTPRESCRIBED";
	private static final String MEDINFO_MEDS_TREATNGCNDTN = "MEDINFO_MEDS_TREATNGCNDTN";
	private static final String IMMNZTNCURRENTY = "IMMNZTNCURRENTY";
	private static final String IMMNZTNCURRENTN = "IMMNZTNCURRENTN";
	private static final String IMMNZTNCURRENTUNK = "IMMNZTNCURRENTUNK";
	private static final String OWNMEDCNSTRY = "OWNMEDCNSTRY";
	private static final String OWNMEDCNSTRN = "OWNMEDCNSTRN";

	/*
	 * HEALTH CARE SUMMARY SECTION BOOKMARKS - PHYSICAL HEALTH SUBSECTION
	 */
	private static final String SUSPPHYSCLHLTHCNDTN = "SUSPPHYSCLHLTHCNDTN";
	private static final String MEDSPECLSTTREATNG = "MEDSPECLSTTREATNG";
	private static final String CHILDDVLPMNTLHIST = "CHILDDVLPMNTLHIST";
	private static final String CHILDRCVDECIY = "CHILDRCVDECIY";
	private static final String CHILDRCVDECIN = "CHILDRCVDECIN";
	private static final String CHILDRCVDECIUNK = "CHILDRCVDECIUNK";
	private static final String TMPLAT_ECI = "TMPLAT_ECI";
	private static final String ECI_SVCSPRVDED = "ECI_SVCSPRVDED";
	private static final String CHILDRCVNGTHRPYY = "CHILDRCVNGTHRPYY";
	private static final String CHILDRCVNGTHRPYN = "CHILDRCVNGTHRPYN";
	private static final String CHILDRCVNGTHRPYUNK = "CHILDRCVNGTHRPYUNK";
	private static final String TMPLAT_THRP = "TMPLAT_THRP";
	private static final String TMPLAT_THRP_SVCS = "TMPLAT_THRP_SVCS";
	private static final String THRP_SVCS_AGNCY = "THRP_SVCS_AGNCY";
	private static final String THRP_SVCS_TYPE = "THRP_SVCS_TYPE";
	private static final String THRP_SVCS_STARTDT = "THRP_SVCS_STARTDT";
	private static final String THRP_SVCS_FREQ = "THRP_SVCS_FREQ";
	private static final String CAETHRPY_OTH = "OTH"; // code_type CAETHRPY code OTH decode Other
	private static final String CHILDENCOPRESISHISTY = "CHILDENCOPRESISHISTY";
	private static final String CHILDENCOPRESISHISTN = "CHILDENCOPRESISHISTN";
	private static final String CHILDENURESISHISTY = "CHILDENURESISHISTY";
	private static final String CHILDENURESISHISTN = "CHILDENURESISHISTN";
	private static final String TMPLAT_ENURHIST = "TMPLAT_ENURHIST";
	private static final String ENURHIST = "ENURHIST";

	/*
	 * HEALTH CARE SUMMARY SECTION BOOKMARKS - PRIMARY MEDICAL NEEDS SUBSECTION
	 */
	private static final String CHILDPRMMEDNEDY = "CHILDPRMMEDNEDY";
	private static final String CHILDPRMMEDNEDN = "CHILDPRMMEDNEDN";
	private static final String TMPLAT_PMN = "TMPLAT_PMN";
	private static final String PMN_DIAGNSISTREATMNT = "PMN_DIAGNSISTREATMNT";
	private static final String PMN_MEDSPECLSTCNTCT = "PMN_MEDSPECLSTCNTCT";
	private static final String PMN_TREATNGHSPTL = "PMN_TREATNGHSPTL";
	private static final String PMN_NUMNURSNGHRS = "PMN_NUMNURSNGHRS";
	private static final String PMN_NMHMEHLTHAGNCY = "PMN_NMHMEHLTHAGNCY";
	private static final String CHILDREQRDDMEY = "CHILDREQRDDMEY";
	private static final String CHILDREQRDDMEN = "CHILDREQRDDMEN";
	private static final String TMPLAT_DME = "TMPLAT_DME";
	private static final String DME_LSTDMESUPLS = "DME_LSTDMESUPLS";
	private static final String CHILDDEAFY = "CHILDDEAFY";
	private static final String CHILDDEAFN = "CHILDDEAFN";
	private static final String TMPLAT_CHILDDEAF = "TMPLAT_CHILDDEAF";
	private static final String CHILDDEAF_CMNCT = "CHILDDEAF_CMNCT";
	private static final String CHILDBLINDY = "CHILDBLINDY";
	private static final String CHILDBLINDN = "CHILDBLINDN";
	private static final String TMPLAT_CHILDBLIND = "TMPLAT_CHILDBLIND";
	private static final String CHILDBLIND_SVCS = "CHILDBLIND_SVCS";

	/*
	 * HEALTH CARE SUMMARY SECTION BOOKMARKS - BEHAVIORAL HEALTH SUBSECTION
	 */
	private static final String TRUMATRGG = "TRUMATRGG";
	private static final String MNTLBHVRNEEDS = "MNTLBHVRNEEDS";
	private static final String CHILDCANSASSMNTY = "CHILDCANSASSMNTY";
	private static final String CHILDCANSASSMNTN = "CHILDCANSASSMNTN";
	private static final String TMPLAT_CHILDCANSASSMNT = "TMPLAT_CHILDCANSASSMNT";
	private static final String CHILDCANSASSMNT_RECMNDTN = "CHILDCANSASSMNT_RECMNDTN";
	private static final String DTPSYCHOLOGICALEVALTN = "DTPSYCHOLOGICALEVALTN";
	private static final String DTPSYCHIATRICEVALTN = "DTPSYCHIATRICEVALTN";
	private static final String CURRNTDIAGNSIS = "CURRNTDIAGNSIS";
	private static final String CHILDMNTLCRISISY = "CHILDMNTLCRISISY";
	private static final String CHILDMNTLCRISISN = "CHILDMNTLCRISISN";
	private static final String TMPLAT_CHILDMNTLCRISIS = "TMPLAT_CHILDMNTLCRISIS";
	private static final String CHILDMNTLCRISIS_DESCRB = "CHILDMNTLCRISIS_DESCRB";
	private static final String PSYHSPTLY = "PSYHSPTLY";
	private static final String PSYHSPTLN = "PSYHSPTLN";
	private static final String TMPLAT_PSYHSPTL = "TMPLAT_PSYHSPTL";
	private static final String TMPLAT_PSYHSPTL_HIST = "TMPLAT_PSYHSPTL_HIST";
	private static final String PSYHSPTL_DTHSPTLZTN = "PSYHSPTL_DTHSPTLZTN";
	private static final String PSYHSPTL_LNGTHHSPTLZTN = "PSYHSPTL_LNGTHHSPTLZTN";
	private static final String PSYHSPTL_BHVRCHILDADMIT = "PSYHSPTL_BHVRCHILDADMIT";

	/*
	 * SUBSTANCE USE OR ABUSE SECTION BOOKMARKS
	 */
	private static final String SUBSTNCABUSEY = "SUBSTNCABUSEY";
	private static final String SUBSTNCABUSEN = "SUBSTNCABUSEN";
	private static final String SUBSTNCABUSEUNK = "SUBSTNCABUSEUNK";
	private static final String TMPLAT_SUBSTNCABUSE = "TMPLAT_SUBSTNCABUSE";
	private static final String TMPLAT_SUBSTNCABUSE_HIST = "TMPLAT_SUBSTNCABUSE_HIST";
	private static final String SUBSTNCABUSE_HIST_SUBSTANCE = "SUBSTNCABUSE_HIST_SUBSTANCE";
	private static final String SUBSTNCABUSE_HIST_USE = "SUBSTNCABUSE_HIST_USE";
	private static final String SUBSTNCABUSE_HIST_AGE = "SUBSTNCABUSE_HIST_AGE";
	private static final String SUBSTNCABUSE_HIST_FREQ = "SUBSTNCABUSE_HIST_FREQ";
	private static final String SUBSTNCABUSE_HIST_APPROXDT = "SUBSTNCABUSE_HIST_APPROXDT";

	/*
	 * YOUTH WHO ARE PREGNANT OR PARENTING SECTION BOOKMARKS
	 */
	private static final String YOUTHPREGNANTY = "YOUTHPREGNANTY";
	private static final String YOUTHPREGNANTN = "YOUTHPREGNANTN";
	private static final String TMPLAT_YOUTHPREGNANT = "TMPLAT_YOUTHPREGNANT";
	private static final String YOUTHPREGNANT_BABYDUE = "YOUTHPREGNANT_BABYDUE";
	private static final String YOUTHPREGNANT_BABYPLAN = "YOUTHPREGNANT_BABYPLAN";
	private static final String YOUTHPARENTY = "YOUTHPARENTY";
	private static final String YOUTHPARENTN = "YOUTHPARENTN";
	private static final String TMPLAT_YOUTHPARENT = "TMPLAT_YOUTHPARENT";
	private static final String YOUTHPARENT_RESIDE = "YOUTHPARENT_RESIDE";
	private static final String YOUTHPARENT_ROLE = "YOUTHPARENT_ROLE";

	/*
	 * RISK BEHAVIOR SECTION BOOKMARKS - SELF HARM/SUICIDE HISTORY SUBSECTION
	 */
	private static final String RISKBHVRSELFHARMY = "RISKBHVRSELFHARMY";
	private static final String RISKBHVRSELFHARMN = "RISKBHVRSELFHARMN";
	private static final String TMPLAT_RISKBHVR = "TMPLAT_RISKBHVR";
	private static final String RISKBHVR_DESCRB = "RISKBHVR_DESCRB";
	private static final String RISKBHVRSUICDEATTMPY = "RISKBHVRSUICDEATTMPY";
	private static final String RISKBHVRSUICDEATTMPN = "RISKBHVRSUICDEATTMPN";

	/*
	 * RISK BEHAVIOR SECTION BOOKMARKS - RUNAWAY HISTORY SUBSECTION
	 */
	private static final String RUNAWAYY = "RUNAWAYY";
	private static final String RUNAWAYN = "RUNAWAYN";
	private static final String TMPLAT_RUNAWAY = "TMPLAT_RUNAWAY";
	private static final String RUNAWAY_HIST = "RUNAWAY_HIST";
	private static final String RUNAWAY_LAST = "RUNAWAY_LAST";
	private static final String RUNAWAY_EPISODE = "RUNAWAY_EPISODE";
	private static final String RUNAWAY_VLNTRY = "RUNAWAY_VLNTRY";
	private static final String RUNAWAY_TYCSTF = "RUNAWAY_TYCSTF";
	private static final String RUNAWAY_FCLTYSTF = "RUNAWAY_FCLTYSTF";
	private static final String RUNAWAY_JPCSTF = "RUNAWAY_JPCSTF";
	private static final String RUNAWAY_CPSSTF = "RUNAWAY_CPSSTF";
	private static final String RUNAWAY_ICJSTF = "RUNAWAY_ICJSTF";
	private static final String RUNAWAY_LOCALLE = "RUNAWAY_LOCALLE";
	private static final String RUNAWAY_OTHR = "RUNAWAY_OTHR";
	private static final String RUNAWAY_OTHRSPEC = "RUNAWAY_OTHRSPEC";

	/*
	 * RISK BEHAVIOR SECTION BOOKMARKS - OTHER SIGNIFICANT PROBLEMS AND/OR BEHAVIORS SUBSECTION
	 */
	private static final String SETTGFIREHISTY = "SETTGFIREHISTY";
	private static final String SETTGFIREHISTN = "SETTGFIREHISTN";
	private static final String TMPLAT_SETTINGFIRE = "TMPLAT_SETTINGFIRE";
	private static final String SETTINGFIRE_DESCRB = "SETTINGFIRE_DESCRB";
	private static final String ANIMALCRULHISTY = "ANIMALCRULHISTY";
	private static final String ANIMALCRULHISTN = "ANIMALCRULHISTN";
	private static final String TMPLAT_ANIMALCRULHIST = "TMPLAT_ANIMALCRULHIST";
	private static final String ANIMALCRULHIST_DESCRB = "ANIMALCRULHIST_DESCRB";
	private static final String SIGNFCNTPRBMY = "SIGNFCNTPRBMY";
	private static final String SIGNFCNTPRBMN = "SIGNFCNTPRBMN";
	private static final String TMPLAT_SIGNFCNTPRBM = "TMPLAT_SIGNFCNTPRBM";
	private static final String SIGNFCNTPRBM_DESCRB = "SIGNFCNTPRBM_DESCRB";

	/*
	 * SEXUALIZED BEHAVIOURS  SECTION BOOKMARKS
	 */
	private static final String SXAGGRSSNY = "SXAGGRSSNY";
	private static final String SXAGGRSSNN = "SXAGGRSSNN";
	private static final String SXAGGRSSNUNK = "SXAGGRSSNUNK";
	private static final String SXBHVRY = "SXBHVRY";
	private static final String SXBHVRN = "SXBHVRN";
	private static final String SXBHVPROBTXT = "SXBHVPROBTXT";
	private static final String SXBHVPROBTXTHIDDEN ="SXBHVPROBTXTHIDDEN";
	private static final String TMPLAT_SXAGGRSSN = "TMPLAT_SXAGGRSSN";
	private static final String SXAGGRSSN_DESCR = "SXAGGRSSN_DESCR";
	private static final String TMPLAT_SXAGGRSSN_HIST = "TMPLAT_SXAGGRSSN_HIST";
	private static final String SXAGGRSSN_HIST_EPSTDT = "SXAGGRSSN_HIST_EPSTDT";
	private static final String SXAGGRSSN_HIST_EPENDT = "SXAGGRSSN_HIST_EPENDT";
	private static final String SXAGGRSSN_HIST_INCDDT = "SXAGGRSSN_HIST_INCDDT";
	private static final String SXAGGRSSN_HIST_DTAPPROX = "SXAGGRSSN_HIST_DTAPPROX";
	private static final String SXAGGRSSN_HIST_DESCR = "SXAGGRSSN_HIST_DESCR";

	/*
	 * EDUCATION SECTION BOOKMARKS
	 */
	private static final String TMPLAT_EDULASTSCH = "TMPLAT_EDULASTSCH";
	private static final String EDULASTSCH_NM = "EDULASTSCH_NM";
	private static final String EDULASTSCH_ADDR = "EDULASTSCH_ADDR";
	private static final String EDULASTSCH_CONTACT = "EDULASTSCH_CONTACT";
	private static final String TMPLAT_EDU = "TMPLAT_EDU";
	private static final String EDU_SCHNM = "EDU_SCHNM";
	private static final String EDU_SCHADDR = "EDU_SCHADDR";
	private static final String EDU_SCHCITY = "EDU_SCHCITY";
	private static final String EDU_SCHSTATE = "EDU_SCHSTATE";
	private static final String EDU_WITHDRAWNDT = "EDU_WITHDRAWNDT";
	private static final String EDU_CNCTNM = "EDU_CNCTNM";
	private static final String EDU_CNCTPH = "EDU_CNCTPH";
	private static final String EDU_CNCTEMAIL = "EDU_CNCTEMAIL";
	private static final String EDU_CURRGRADE = "EDU_CURRGRADE";
	private static final String EDU_GRADEASDT = "EDU_GRADEASDT";
	private static final String EDU_GRADELVLYES = "EDU_GRADELVLYES";
	private static final String EDU_GRADELVLNO = "EDU_GRADELVLNO";
	private static final String EDUSVCSDTRGLR = "EDUSVCSDTRGLR";
	private static final String EDUSVCSDTBILANG = "EDUSVCSDTBILANG";
	private static final String EDUSVCSDTGIFT = "EDUSVCSDTGIFT";
	private static final String EDUSVCSDTSELFCONTND = "EDUSVCSDTSELFCONTND";
	private static final String EDUSVCSDTSPLTRANS = "EDUSVCSDTSPLTRANS";
	private static final String EDUSVCSDTCREDREC = "EDUSVCSDTCREDREC";
	private static final String EDUSVCSDTVOCTNL = "EDUSVCSDTVOCTNL";
	private static final String EDUSVCSDTCONSEL = "EDUSVCSDTCONSEL";
	private static final String EDUSVCSDTADVNCPLCMNT = "EDUSVCSDTADVNCPLCMNT";
	private static final String EDUSVCSDTSPLEDUCTN = "EDUSVCSDTSPLEDUCTN";
	private static final String EDUSVCSDTDAEP = "EDUSVCSDTDAEP";
	private static final String EDUSERDTOTHR = "EDUSERDTOTHR";
	private static final String EDUSERDTOTHRSPEC = "EDUSERDTOTHRSPEC";
	private static final String EDUSVCSDTSELFPACED = "EDUSVCSDTSELFPACED";
	private static final String EDUSVCSDT504 = "EDUSVCSDT504";
	private static final String EDUTRUANCYHISTY = "EDUTRUANCYHISTY";
	private static final String EDUTRUANCYHISTN = "EDUTRUANCYHISTN";
	private static final String EDUTRUANCYHISTUNK = "EDUTRUANCYHISTUNK";

	/*
	 * TRANSITION PLANNING FOR A SUCCESSFUL ADULTHOOD (PAL) SECTION BOOKMARKS
	 */
	private static final String COMPPALTRNGSKILLY = "COMPPALTRNGSKILLY";
	private static final String COMPPALTRNGSKILLN = "COMPPALTRNGSKILLN";
	private static final String COMPPALTRNGSKILLUNK = "COMPPALTRNGSKILLUNK";
	private static final String DESCRBREGNLPALLSTF ="DESCRBREGNLPALLSTF";
	private static final String CHILDCIRCLSUPRTY ="CHILDCIRCLSUPRTY";
	private static final String CHILDCIRCLSUPRTN ="CHILDCIRCLSUPRTN";

	/*
	 * JUVENILE JUSTICE INVOLVEMENT SECTION BOOKMARKS
	 */
	private static final String TMPLAT_JUVNLE = "TMPLAT_JUVNLE";
	private static final String JUVNLE_INVOLVEMENT = "JUVNLE_INVOLVEMENT";

	/*
	 * FAMILY HISTORY SECTION BOOKMARKS
	 */
	private static final String TMPLAT_FAMILY = "TMPLAT_FAMILY";
	private static final String FAMILY_SBLNGS = "FAMILY_SBLNGS";
	private static final String FAMILY_INCARE = "FAMILY_INCARE";
	private static final String FAMILY_DATEOB = "FAMILY_DATEOB";
	private static final String FAMILY_ADDRES = "FAMILY_ADDRES";


	/*
	 * PLACEMENT HISTORY SECTION
	 */
	private static final String PLCMNTPLCDHMEOUTY = "PLCMNTPLCDHMEOUTY";
	private static final String PLCMNTPLCDHMEOUTN = "PLCMNTPLCDHMEOUTN";
	private static final String PLCMNTPLCDHMEOUTUNK = "PLCMNTPLCDHMEOUTUNK";
	private static final String TMPLAT_NOPREVHOME = "TMPLAT_NOPREVHOME";
	private static final String NOPREVHOME_OUTPLCMNT = "NOPREVHOME_OUTPLCMNT";
	private static final String TMPLAT_DMSTC = "TMPLAT_DMSTC";
	private static final String TMPLAT_INTRNTNL = "TMPLAT_INTRNTNL";
	private static final String TMPLAT_PRERLGLCUSTD = "TMPLAT_PRERLGLCUSTD";
	private static final String TMPLAT_PLCMNT = "TMPLAT_PLCMNT";
	private static final String PLCMNT_STARTDT = "PLCMNT_STARTDT";
	private static final String PLCMNT_ENDDT = "PLCMNT_ENDDT";

}
