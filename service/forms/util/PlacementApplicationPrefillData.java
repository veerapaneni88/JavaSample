package us.tx.state.dfps.service.forms.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;
import us.tx.state.dfps.service.casepackage.dto.CSAEpisodeDto;
import us.tx.state.dfps.service.casepackage.dto.CsaEpisodesIncdntDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.pca.dto.PlacementDtlDto;
import us.tx.state.dfps.service.person.dto.TraffickingDto;
import us.tx.state.dfps.service.person.dto.UnitDto;
import us.tx.state.dfps.service.placement.dto.CPAdoptionDto;
import us.tx.state.dfps.service.placement.dto.CommonApplicationDto;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.placement.dto.TemporaryAbsenceDto;
import us.tx.state.dfps.service.placement.dto.VisitationPlanInfoDtlsDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimIncidentDto;

/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:* PlacementApplicationPrefillData will implemented returnPrefillData operation
 * defined in DocumentServiceUtil Interface to populate the prefill data for
 * form csc11o00.
 *OCT 11, 2018- 2:19:28 PM
 *© 2017 Texas Department of Family and Protective Services 
 *  * **********  Change History *********************************
 * 10/21/2019 thompswa artf128758 : FCL Project - Add trafficking and sexual victimization histories
 * 12/11/2019 thompswa artf131169 Confirmed sx vctmztn hist question should not populate 'No' when the sexual victimization page question has not been answered
 * 03/03/2022 suris PPM 65015 removing any code to show CSA_EPISODE data and only showing CSA_EPISODE_INCDNT data, the bookmark still exists for CSA_EPISODE on the template. This was done so no new template is required
 */
@Component
public class PlacementApplicationPrefillData  extends DocumentServiceUtil{
	

	public static final String DT_INCDNT= "Date of Incident:  ";
	public static final String INCDNT_DESCRPTN= "Incident Description:  ";
	public static final String INCDNT_DESCRPTNS= "Incident Description(s):";
	public static final String DT_EPISODE_START= "Episode Start Date:  ";
	public static final String DT_EPISODE_END= "Episode End Date:  ";
	public static final String APPROXIMATE_DATE= "Approximate Date";
	private static final Logger logger = Logger.getLogger(PlacementApplicationPrefillData.class);
	
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
		CommonApplicationDto commonApplicationDto = (CommonApplicationDto) parentDtoobj;

		if (ObjectUtils.isEmpty(commonApplicationDto.getEmployeePersCsWrkrDto()))
			commonApplicationDto.setEmployeePersCsWrkrDto(new EmployeePersPhNameDto());
		if (ObjectUtils.isEmpty(commonApplicationDto.getEmployeePersSuprvsrDto()))
			commonApplicationDto.setEmployeePersSuprvsrDto(new EmployeePersPhNameDto());
		if (ObjectUtils.isEmpty(commonApplicationDto.getUnitDto()))
			commonApplicationDto.setUnitDto(new UnitDto());
		if (ObjectUtils.isEmpty(commonApplicationDto.getNameDetailDto()))
			commonApplicationDto.setNameDetailDto(new NameDetailDto());
		if (ObjectUtils.isEmpty(commonApplicationDto.getFceApplicationDto()))
			commonApplicationDto.setFceApplicationDto(new FceApplicationDto());
		if (ObjectUtils.isEmpty(commonApplicationDto.getFceEligibilityDto()))
			commonApplicationDto.setFceEligibilityDto(new FceEligibilityDto());
		if (ObjectUtils.isEmpty(commonApplicationDto.getCsaEpisodesIncdntDtoLst())) {
			List<CsaEpisodesIncdntDto> csaEpisodesIncdntDtoLst = new ArrayList<CsaEpisodesIncdntDto>();
			CsaEpisodesIncdntDto csaEpisodesIncdntDto = new CsaEpisodesIncdntDto();
			csaEpisodesIncdntDtoLst.add(csaEpisodesIncdntDto);
			commonApplicationDto.setCsaEpisodesIncdntDtoLst(csaEpisodesIncdntDtoLst);
		}
		if (ObjectUtils.isEmpty(commonApplicationDto.getStagePersonLinkCaseDto()))
			commonApplicationDto.setStagePersonLinkCaseDto(new StagePersonLinkCaseDto());
		if (ObjectUtils.isEmpty(commonApplicationDto.getPersonDto()))
			commonApplicationDto.setPersonDto(new PersonDto());
		if (ObjectUtils.isEmpty(commonApplicationDto.getTrfckngDtoLst())) {
			List<TraffickingDto> trfckngDtoLst = new ArrayList<TraffickingDto>();
			TraffickingDto trfckngDto = new TraffickingDto();
			trfckngDtoLst.add(trfckngDto);
			commonApplicationDto.setTrfckngDtoLst(trfckngDtoLst);
		}

		SimpleDateFormat sdfDtExit = new SimpleDateFormat("yyyy-MM-dd");
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		// Populate Case Name value
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				commonApplicationDto.getStagePersonLinkCaseDto().getNmCase());
		bookmarkNonFrmGrpList.add(bookmarkNmCase);
		// Populate Case Id value
		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_CASE_NBR,
				commonApplicationDto.getStagePersonLinkCaseDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkIdCase);
		// Populate Child Name value
		BookmarkDto bookmarkChildNm = createBookmark(BookmarkConstants.TITLE_CHILD_NAME,
				TypeConvUtil.formatFullName(commonApplicationDto.getStagePersonLinkCaseDto().getNmPersonFirst(),
						commonApplicationDto.getStagePersonLinkCaseDto().getNmPersonMiddle(),
						commonApplicationDto.getStagePersonLinkCaseDto().getNmPersonLast()));
		bookmarkNonFrmGrpList.add(bookmarkChildNm);
		// Populate the Child Date of Birth value.
		BookmarkDto bookmarkDtpersonBirth = createBookmark(BookmarkConstants.TITLE_DATE_OF_BIRTH,
				TypeConvUtil.formDateFormat(commonApplicationDto.getStagePersonLinkCaseDto().getDtPersonBirth()));
		bookmarkNonFrmGrpList.add(bookmarkDtpersonBirth);
		// Populate the Form Date Completed value
		BookmarkDto bookmarkFormDtComp = createBookmark(BookmarkConstants.TITLE_DATE_COMP,
				TypeConvUtil.formDateFormat(new Date()));
		bookmarkNonFrmGrpList.add(bookmarkFormDtComp);

		// Start - Populate DFPS Case Worker Information - Section 1
		// Populate Primary Assignment Case Worker Name
		BookmarkDto bookmarkCsWrkrNm = createBookmark(BookmarkConstants.TXT_CSWRKR_NAME,
				TypeConvUtil.formatFullName(commonApplicationDto.getEmployeePersCsWrkrDto().getNmNameFirst(),
						commonApplicationDto.getEmployeePersCsWrkrDto().getNmNameMiddle(),
						commonApplicationDto.getEmployeePersCsWrkrDto().getNmNameLast()));
		bookmarkNonFrmGrpList.add(bookmarkCsWrkrNm);
		// Populate Primary Assignment Case Worker Unit
		BookmarkDto bookmarkUnitnbr = createBookmark(BookmarkConstants.TXT_CSWRKR_UNIT,
				commonApplicationDto.getUnitDto().getNbrUnit());
		bookmarkNonFrmGrpList.add(bookmarkUnitnbr);
		// Populate Primary Assignment Case Worker Supervisor Name
		BookmarkDto bookmarkSuprVsrNm = createBookmark(BookmarkConstants.TXT_SUPRVSR_NAME,
				TypeConvUtil.formatFullName(commonApplicationDto.getEmployeePersSuprvsrDto().getNmNameFirst(),
						commonApplicationDto.getEmployeePersSuprvsrDto().getNmNameMiddle(),
						commonApplicationDto.getEmployeePersSuprvsrDto().getNmNameLast()));
		bookmarkNonFrmGrpList.add(bookmarkSuprVsrNm);
		// End - Populate DFPS Case Worker Information - Section 1
		// Start - Child's Information - Section 2
		// Populate the Child Full Name
		BookmarkDto bookmarkNameFirstNm = createBookmark(BookmarkConstants.TXT_CHILD_NAME,
				TypeConvUtil.formatFullName(commonApplicationDto.getNameDetailDto().getNmNameFirst(),
						commonApplicationDto.getNameDetailDto().getNmNameMiddle(),
						commonApplicationDto.getNameDetailDto().getNmNameLast()));
		bookmarkNonFrmGrpList.add(bookmarkNameFirstNm);
		// Populate the Child Date of Birth
		BookmarkDto bookmarkChilddateOfBirth = createBookmark(BookmarkConstants.TXT_DATE_OF_BIRTH,
				TypeConvUtil.formDateFormat(commonApplicationDto.getPersonDto().getDtPersonBirth()));
		bookmarkNonFrmGrpList.add(bookmarkChilddateOfBirth);
		// Populate the Child PersonID
		BookmarkDto bookmarkChildPersonId = createBookmark(BookmarkConstants.TXT_CHILD_IDPERSON,
				commonApplicationDto.getPersonDto().getIdPerson());
		bookmarkNonFrmGrpList.add(bookmarkChildPersonId);
		// Populate Child Gender Value
		if (!ObjectUtils.isEmpty(commonApplicationDto.getPersonDto().getCdPersonSex())
				&& ServiceConstants.MALE.equalsIgnoreCase(commonApplicationDto.getPersonDto().getCdPersonSex())) {
			BookmarkDto bookmarkChildMaleGender = createBookmark(BookmarkConstants.CHK_CHILD_GNDR_MALE,
					ServiceConstants.CHECKED);
			bookmarkNonFrmGrpList.add(bookmarkChildMaleGender);
		}
		if (!ObjectUtils.isEmpty(commonApplicationDto.getPersonDto().getCdPersonSex())
				&& ServiceConstants.FEMALE.equalsIgnoreCase(commonApplicationDto.getPersonDto().getCdPersonSex())) {
			BookmarkDto bookmarkChildFeMaleGender = createBookmark(BookmarkConstants.CHK_CHILD_GNDR_FEMALE,
					ServiceConstants.CHECKED);
			bookmarkNonFrmGrpList.add(bookmarkChildFeMaleGender);
		}
		// Populate the Child Legal Region Value
		BookmarkDto bookmarkChildLegalRegion = createBookmark(BookmarkConstants.TXT_CHILD_LEGAL_REGN,
				commonApplicationDto.getStagePersonLinkCaseDto().getCdStageRegion());
		bookmarkNonFrmGrpList.add(bookmarkChildLegalRegion);
		// Populate the Child Country of Citizen Value
		BookmarkDto bookmarkChildCitznCntry = createBookmarkWithCodesTable(BookmarkConstants.TXT_CHILD_CNTRY_CTZN,
				commonApplicationDto.getFceEligibilityDto().getCdPersonCitizenship(), CodesConstant.CCTZNSTA);
		bookmarkNonFrmGrpList.add(bookmarkChildCitznCntry);
		// Populate the Child Ethinicity Value
		BookmarkDto bookmarkCdPersEthnicGrp = createBookmark(BookmarkConstants.TXT_CHILD_ETHNCTY,
				commonApplicationDto.getEthinicity());
		bookmarkNonFrmGrpList.add(bookmarkCdPersEthnicGrp);
		// Populate the Child Race Value
		BookmarkDto bookmarkCdPersRaceGrp = createBookmark(BookmarkConstants.TXT_txtCHILD_RACE,
				commonApplicationDto.getRace());
		bookmarkNonFrmGrpList.add(bookmarkCdPersRaceGrp);
		// Populate the child Primary Language
		BookmarkDto bookmarkCdPersLang = createBookmarkWithCodesTable(BookmarkConstants.TXT_CHILD_PRMRY_LANG,
				commonApplicationDto.getPersonDto().getCdPersonLanguage(), CodesConstant.CLANG);
		bookmarkNonFrmGrpList.add(bookmarkCdPersLang);
		// Populate the Child Removal Address
		if (!ObjectUtils.isEmpty(commonApplicationDto.getFceApplicationDto())) {
			String rmvlAddr = !ObjectUtils.isEmpty(commonApplicationDto.getFceApplicationDto().getAddrRemovalStLn1())
					? commonApplicationDto.getFceApplicationDto().getAddrRemovalStLn1()
					: ServiceConstants.EMPTY_STRING.concat(
							!ObjectUtils.isEmpty(commonApplicationDto.getFceApplicationDto().getAddrRemovalStLn2())
									? commonApplicationDto.getFceApplicationDto().getAddrRemovalStLn2()
									: ServiceConstants.EMPTY_STRING)
							.concat(!ObjectUtils
									.isEmpty(commonApplicationDto.getFceApplicationDto().getAddrRemovalCity())
											? commonApplicationDto.getFceApplicationDto().getAddrRemovalCity()
											: ServiceConstants.EMPTY_STRING)
							.concat(!ObjectUtils.isEmpty(commonApplicationDto.getFceApplicationDto().getCdState())
									? commonApplicationDto.getFceApplicationDto().getCdState()
									: ServiceConstants.EMPTY_STRING)
							.concat(!ObjectUtils
									.isEmpty(commonApplicationDto.getFceApplicationDto().getAddrRemovalAddrZip())
											? commonApplicationDto.getFceApplicationDto().getAddrRemovalAddrZip()
											: ServiceConstants.EMPTY_STRING);
			BookmarkDto bookmarkChildRmvlAddr = createBookmark(BookmarkConstants.TXT_CHILD_RMVL_ADDR, rmvlAddr);
			bookmarkNonFrmGrpList.add(bookmarkChildRmvlAddr);
		}
		// Populate Child Permanency Plan Value - Start
		if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto())) {
			if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())
					&& CodesConstant.CCPPRMGL_AFM
							.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())) {
				BookmarkDto bookmarkChildPermPlanAfm = createBookmark(BookmarkConstants.CHK_FMLY_REUNFCTN,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkChildPermPlanAfm);
			}
			if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())
					&& CodesConstant.CCPPRMGL_FFC
							.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())) {
				BookmarkDto bookmarkChildPermPlanFfc = createBookmark(BookmarkConstants.CHK_FSTR_FMLY_CVS,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkChildPermPlanFfc);
			}
			if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())
					&& CodesConstant.CCPPRMGL_ARA
							.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())) {
				BookmarkDto bookmarkChildPermPlanAra = createBookmark(BookmarkConstants.CHK_FMLY_ADOPTN,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkChildPermPlanAra);
			}
			if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())
					&& CodesConstant.CCPPRMGL_GPC
							.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())) {
				BookmarkDto bookmarkChildPermPlanGpc = createBookmark(BookmarkConstants.CHK_OTHR_FMLY_CVS,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkChildPermPlanGpc);
			}
			if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())
					&& CodesConstant.CCPPRMGL_BPR
							.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())) {
				BookmarkDto bookmarkChildPermPlanBpr = createBookmark(BookmarkConstants.CHK_FMLY_CVS,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkChildPermPlanBpr);
			}
			if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())
					&& CodesConstant.CCPPRMGL_HIL
							.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())) {
				BookmarkDto bookmarkChildPermPlanHil = createBookmark(BookmarkConstants.CHK_INDPNDNT_LVNG,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkChildPermPlanHil);
			}
			if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())
					&& CodesConstant.CCPPRMGL_CAD
							.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())) {
				BookmarkDto bookmarkChildPermPlanCad = createBookmark(BookmarkConstants.CHK_FMLY_UNRELTD_ADOPTN,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkChildPermPlanCad);
			}
			if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())
					&& CodesConstant.CCPPRMGL_ILC
							.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())) {
				BookmarkDto bookmarkChildPermPlanIlc = createBookmark(BookmarkConstants.CHK_CMNTY_CARE,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkChildPermPlanIlc);
			}
			if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())
					&& CodesConstant.CCPPRMGL_DTC
							.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdCspPlanPermGoal())) {
				BookmarkDto bookmarkChildPermPlanDtc = createBookmark(BookmarkConstants.CHK_FMLY_UNRELTD_CVS,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkChildPermPlanDtc);
			}
		}
		// Populate Child Permanency Plan Value - End
		// Populate Child Legal Status Value - Start
		if (!ObjectUtils.isEmpty(commonApplicationDto.getChildLegalStatusDtoList())
				&& commonApplicationDto.getChildLegalStatusDtoList().size() > ServiceConstants.Zero) {
			//Fixed Warranty Defect#12004 Issue to fix duplicate bookmark creation
				LegalStatusPersonMaxStatusDtOutDto childLegalStatusDto = commonApplicationDto.getChildLegalStatusDtoList().stream()
								.max(Comparator.comparing(LegalStatusPersonMaxStatusDtOutDto::getTsLastUpdate)).get();
				
				if (!ObjectUtils.isEmpty(childLegalStatusDto.getCdLegalStatStatus())
						&& CodesConstant.CLEGSTAT_020.equalsIgnoreCase(childLegalStatusDto.getCdLegalStatStatus())) {
					BookmarkDto bookmarkChildLglStatTmc = createBookmark(BookmarkConstants.CHK_LGL_TMC,
							ServiceConstants.CHECKED);
					bookmarkNonFrmGrpList.add(bookmarkChildLglStatTmc);
				}
				if (!ObjectUtils.isEmpty(childLegalStatusDto.getCdLegalStatStatus())
						&& CodesConstant.CLEGSTAT_030.equalsIgnoreCase(childLegalStatusDto.getCdLegalStatStatus())) {
					BookmarkDto bookmarkChildLglStatPmc = createBookmark(BookmarkConstants.CHK_LGL_PMC,
							ServiceConstants.CHECKED);
					bookmarkNonFrmGrpList.add(bookmarkChildLglStatPmc);
				}
				if (!ObjectUtils.isEmpty(childLegalStatusDto.getCdLegalStatStatus())
						&& CodesConstant.CLEGSTAT_080.equalsIgnoreCase(childLegalStatusDto.getCdLegalStatStatus())) {
					BookmarkDto bookmarkChildLglStatJmc = createBookmark(BookmarkConstants.CHK_LGL_JMC,
							ServiceConstants.CHECKED);
					bookmarkNonFrmGrpList.add(bookmarkChildLglStatJmc);
				}
				if (!ObjectUtils.isEmpty(childLegalStatusDto.getCdLegalStatStatus())
						&& CodesConstant.CLEGSTAT_010.equalsIgnoreCase(childLegalStatusDto.getCdLegalStatStatus())) {
					BookmarkDto bookmarkChildLglStatCcc = createBookmark(BookmarkConstants.CHK_LGL_CCC,
							ServiceConstants.CHECKED);
					bookmarkNonFrmGrpList.add(bookmarkChildLglStatCcc);
				}
				if (!ObjectUtils.isEmpty(childLegalStatusDto.getCdLegalStatStatus())
						&& CodesConstant.CLEGSTAT_120.equalsIgnoreCase(childLegalStatusDto.getCdLegalStatStatus())) {
					BookmarkDto bookmarkChildLglStatPrs = createBookmark(BookmarkConstants.CHK_LGL_PRS_TERMTD,
							ServiceConstants.CHECKED);
					bookmarkNonFrmGrpList.add(bookmarkChildLglStatPrs);
				}
				// Populate the Child Legal County
				if (!ObjectUtils.isEmpty(childLegalStatusDto.getCdLegalStatCnty())) {
					BookmarkDto bookmarkChildLglStatCnty = createBookmarkWithCodesTable(
							BookmarkConstants.TXT_CHILD_LGL_CNTY, childLegalStatusDto.getCdLegalStatCnty(),
							CodesConstant.CCOUNT);
					bookmarkNonFrmGrpList.add(bookmarkChildLglStatCnty);
				}
		}
		// Populate Child Legal Status Value - End
		// artf128758 populate history of sexual victimization		
		if (!ObjectUtils.isEmpty(commonApplicationDto.getSexVicHistDto()) &&  // artf131169 ...
				!TypeConvUtil.isNullOrEmpty(commonApplicationDto.getSexVicHistDto().getIndChildHasSxVictimHistory())) {
			bookmarkNonFrmGrpList.add( createBookmarkWithCodesTable(BookmarkConstants.SEXVICTIM, 
					commonApplicationDto.getSexVicHistDto().getIndChildHasSxVictimHistory(), CodesConstant.CINVACAN));
		}

		if (!ObjectUtils.isEmpty(commonApplicationDto.getSexVicHistDto()) &&  // artf131169 ...
				!TypeConvUtil.isNullOrEmpty(commonApplicationDto.getSexVicHistDto().getIndUnconfirmedVictimHistory())) {
			bookmarkNonFrmGrpList.add( createBookmarkWithCodesTable(BookmarkConstants.UNCFSEXVICTIM,
					commonApplicationDto.getSexVicHistDto().getIndUnconfirmedVictimHistory(), CodesConstant.CINVACAN));
			if(commonApplicationDto.getSexVicHistDto().getIndUnconfirmedVictimHistory().equals("N")){
				bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.UNCFSEXVICTIM_YES,
						ServiceConstants.HIDDEN_TRUE));
			}else{
				bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.UNCFSEXVICTIM_YES,
						ServiceConstants.HIDDEN_FALSE));
			}
		}
		// populate Child sexually aggressive behavior
		if (commonApplicationDto.getIsAfterFclRelease()) {
			// post fcl creldate artf128758
			if (!ObjectUtils.isEmpty(commonApplicationDto.getChildCSABhvr())) {
				bookmarkNonFrmGrpList.add( createBookmark(BookmarkConstants.SEXAGGRESSION, TypeConvUtil.getYesNoBoolean( commonApplicationDto.getChildCSABhvr())));
			}
		} else {
			// prior to fcl creldate artf128758
			if (commonApplicationDto.getChildCSABhvr()) {
				BookmarkDto bookmarkChildSxBhvrYes = createBookmark(BookmarkConstants.CHK_CHILD_SEX_AGGR_BHVR_YES,
					ServiceConstants.CHECKED_TRUE);
			    bookmarkNonFrmGrpList.add(bookmarkChildSxBhvrYes);
		    } else {
			    BookmarkDto bookmarkChildSxBhvrNo = createBookmark(BookmarkConstants.CHK_CHILD_SEX_AGGR_BHVR_NO,
					ServiceConstants.CHECKED_TRUE);
			    bookmarkNonFrmGrpList.add(bookmarkChildSxBhvrNo);
		    } 
		} // END artf128758 
		// Populate Child Placement Address Info
		if (!ObjectUtils.isEmpty(commonApplicationDto.getPlacementDtlDto())) {
			String plcmntAddr = !ObjectUtils.isEmpty(commonApplicationDto.getPlacementDtlDto().getAddrPlcmtLn1())
					? commonApplicationDto.getPlacementDtlDto().getAddrPlcmtLn1()
					: ServiceConstants.EMPTY_STRING
							.concat(!ObjectUtils.isEmpty(commonApplicationDto.getPlacementDtlDto().getAddrPlcmtLn2())
									? commonApplicationDto.getPlacementDtlDto().getAddrPlcmtLn2()
									: ServiceConstants.EMPTY_STRING);
			BookmarkDto bookmarkChildPlcmntAddr = createBookmark(BookmarkConstants.TXT_CHILD_PCMNT_ADDR, plcmntAddr);
			bookmarkNonFrmGrpList.add(bookmarkChildPlcmntAddr);
			BookmarkDto bookmarkChildPlcmntAddrCity = createBookmark(BookmarkConstants.TXT_CHILD_PCMNT_CITY,
					commonApplicationDto.getPlacementDtlDto().getAddrPlcmtCity());
			bookmarkNonFrmGrpList.add(bookmarkChildPlcmntAddrCity);
			BookmarkDto bookmarkChildPlcmntAddrState = createBookmarkWithCodesTable(
					BookmarkConstants.TXT_CHILD_PCMNT_STATE, commonApplicationDto.getPlacementDtlDto().getAddrPlcmtSt(),
					CodesConstant.CSTATE);
			bookmarkNonFrmGrpList.add(bookmarkChildPlcmntAddrState);
			BookmarkDto bookmarkChildPlcmntAddrZip = createBookmark(BookmarkConstants.TXT_CHILD_PCMNT_ZIP,
					commonApplicationDto.getPlacementDtlDto().getAddrPlcmtZip());
			bookmarkNonFrmGrpList.add(bookmarkChildPlcmntAddrZip);
		}

		// artf263992 : BR 15.4 Form - Common Application for Placement of Children in Residential Care

		if(!TypeConvUtil.isNullOrEmpty(commonApplicationDto.getRmdServicePackageDtlDto())){
			FormDataGroupDto rmdServicePackage = createFormDataGroup(FormGroupsConstants.TMPLAT_RMDSERVICEPKG, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRmdSvcPkgList = new ArrayList<>();
			// Populate child Current Recommended Service Package
			BookmarkDto bookmarkChildrecmdServPkg = createBookmarkWithCodesTable(BookmarkConstants.TXT_CHILD_RMD_SVCS_PKG,
					commonApplicationDto.getRmdServicePackageDtlDto().getSvcPkgCd(), CodesConstant.CSVCCODE);
			bookmarkRmdSvcPkgList.add(bookmarkChildrecmdServPkg);
			BookmarkDto bookmarkChildrecmdServPkgStartDt = createBookmark(BookmarkConstants.TXT_CHILD_RMD_SVCS_PKG_START_DT,
					sdfDtExit.format(commonApplicationDto.getRmdServicePackageDtlDto().getDtSvcStart()));
			bookmarkRmdSvcPkgList.add(bookmarkChildrecmdServPkgStartDt);
			BookmarkDto bookmarkChildrecmdServPkgEndDt = createBookmark(BookmarkConstants.TXT_CHILD_RMD_SVCS_PKG_END_DT,
					sdfDtExit.format(commonApplicationDto.getRmdServicePackageDtlDto().getDtSvcEnd()));
			bookmarkRmdSvcPkgList.add(bookmarkChildrecmdServPkgEndDt);
			rmdServicePackage.setBookmarkDtoList(bookmarkRmdSvcPkgList);
			formDataGroupList.add(rmdServicePackage);
		} else {
			// Populate child Current Authorized Service level of Care info
			if(!ObjectUtils.isEmpty(commonApplicationDto.getServicelvlPersonLocDto())) {
				FormDataGroupDto servicePackage = createFormDataGroup(FormGroupsConstants.TMPLAT_SERVICEPKG, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSvcPkgList = new ArrayList<>();
				BookmarkDto bookmarkChildCurrLOC = createBookmarkWithCodesTable(BookmarkConstants.TXT_CHILD_SVCS_LVL_CARE,
						commonApplicationDto.getServicelvlPersonLocDto().getCdPlocChild(), CodesConstant.CLVOFCR2);
				bookmarkSvcPkgList.add(bookmarkChildCurrLOC);
				BookmarkDto bookmarkChildCurrLOCStartDt = createBookmark(BookmarkConstants.TXT_CHILD_SVCS_LVL_START_DT,
						commonApplicationDto.getServicelvlPersonLocDto().getDtPlocStart());
				bookmarkSvcPkgList.add(bookmarkChildCurrLOCStartDt);
				BookmarkDto bookmarkChildCurrLOCEndDt = createBookmark(BookmarkConstants.TXT_CHILD_SVCS_LVL_END_DT,
						commonApplicationDto.getServicelvlPersonLocDto().getDtPlocEnd());
				bookmarkSvcPkgList.add(bookmarkChildCurrLOCEndDt);
				servicePackage.setBookmarkDtoList(bookmarkSvcPkgList);
				formDataGroupList.add(servicePackage);
			}
		}

		// End - Child's Information - Section 2
		// Section 3 - Trauma History 
		// artf128758 populate sexual victimization incident description
		if ( !ObjectUtils.isEmpty(commonApplicationDto.getSexVicHistDto().getSvhIncidents())
				&& commonApplicationDto.getSexVicHistDto().getSvhIncidents().size() > ServiceConstants.Zero) {
			
			for (SexualVictimIncidentDto sexVicIncdntDto : commonApplicationDto.getSexVicHistDto().getSvhIncidents()) {
				FormDataGroupDto sexVicIncdntFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SEXVICTIMHIST, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSexVicIncdntFrmGrpList = new ArrayList<BookmarkDto>();
				bookmarkSexVicIncdntFrmGrpList.add( createBookmark(BookmarkConstants.SEXVICTIMHIST_DTINCIDENTLABEL, DT_INCDNT));
				bookmarkSexVicIncdntFrmGrpList.add( createBookmark(BookmarkConstants.SEXVICTIMHIST_DTINCIDENT, TypeConvUtil.formDateFormat(sexVicIncdntDto.getDtIncident())));
				if (!TypeConvUtil.isNullOrEmpty(sexVicIncdntDto.getIndApproxDate()) && !ServiceConstants.N.equals(sexVicIncdntDto.getIndApproxDate())) {
					bookmarkSexVicIncdntFrmGrpList.add( createBookmark(BookmarkConstants.SEXVICTIMHIST_DTAPPROXIMATE, APPROXIMATE_DATE));
				}
				bookmarkSexVicIncdntFrmGrpList.add( createBookmark(BookmarkConstants.SEXVICTIMHIST_DESCR,
						!TypeConvUtil.isNullOrEmpty(sexVicIncdntDto.getVictimComments())
						? sexVicIncdntDto.getVictimComments() : ServiceConstants.EMPTY_STRING));
				sexVicIncdntFrmDataGrpDto.setBookmarkDtoList(bookmarkSexVicIncdntFrmGrpList);
				formDataGroupList.add(sexVicIncdntFrmDataGrpDto);
			}			
	    }// artf128758 end
		// Start - Trafficking History - Section 4
		// Populate Sex & Labor Trafficking Informations
		if (!commonApplicationDto.getIsAfterFclRelease()) { // prior to fcl creldate artf128758
			if (commonApplicationDto.getIndChildSuspdSxTrfckng()) {
				BookmarkDto bookmarkSuspVctmYes = createBookmark(BookmarkConstants.CHK_TRFCKNG_SUSP_VCTM_YES,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkSuspVctmYes);
			} else {
				BookmarkDto bookmarkSuspVctmNo = createBookmark(BookmarkConstants.CHK_TRFCKNG_SUSP_VCTM_NO,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkSuspVctmNo);
			}
			if (commonApplicationDto.getIndChildCnfrmdSxTrfckng()) {
				BookmarkDto bookmarkCnfrmdVctmYes = createBookmark(BookmarkConstants.CHK_TRFCKNG_CNFRMD_VCTM_YES,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkCnfrmdVctmYes);
			} else {
				BookmarkDto bookmarkCnfrmdVctmNo = createBookmark(BookmarkConstants.CHK_TRFCKNG_CNFRMD_VCTM_NO,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkCnfrmdVctmNo);
			}
			if (commonApplicationDto.getIndChildSuspdLbrTrfckng()) {
				BookmarkDto bookmarkSuspLbrVctmYes = createBookmark(BookmarkConstants.CHK_LBR_TRFCKNG_SUSP_VCTM_YES,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkSuspLbrVctmYes);
			} else {
				BookmarkDto bookmarkSuspLbrVctmNo = createBookmark(BookmarkConstants.CHK_LBR_TRFCKNG_SUSP_VCTM_NO,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkSuspLbrVctmNo);
			}
			if (commonApplicationDto.getIndChildCnfrmdLbrTrfckng()) {
				BookmarkDto bookmarkCnfrmdLbrVctmYes = createBookmark(BookmarkConstants.CHK_LBR_TRFCKNG_CNFRMD_VCTM_YES,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkCnfrmdLbrVctmYes);
			} else {
				BookmarkDto bookmarkCnfrmdLbrVctmNo = createBookmark(BookmarkConstants.CHK_LBR_TRFCKNG_CNFRMD_VCTM_NO,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkCnfrmdLbrVctmNo);
			}
		} else {
			// Populate Sex & Labor Trafficking Informations post fcl creldate artf128758
			bookmarkNonFrmGrpList.add( createBookmark(BookmarkConstants.SEXTRAFFICKINGSUSP, TypeConvUtil.getYesNoBoolean( commonApplicationDto.getIndChildSuspdSxTrfckng())));
			bookmarkNonFrmGrpList.add( createBookmark(BookmarkConstants.SEXTRAFFICKINGCONF, TypeConvUtil.getYesNoBoolean( commonApplicationDto.getIndChildCnfrmdSxTrfckng())));
			bookmarkNonFrmGrpList.add( createBookmark(BookmarkConstants.LABORTRAFFICKINGSUSP, TypeConvUtil.getYesNoBoolean( commonApplicationDto.getIndChildSuspdLbrTrfckng())));
			bookmarkNonFrmGrpList.add( createBookmark(BookmarkConstants.LABORTRAFFICKINGCONF, TypeConvUtil.getYesNoBoolean( commonApplicationDto.getIndChildCnfrmdLbrTrfckng())));
		} // end Populate Sex & Labor Trafficking Informations post fcl creldate artf128758
		if (!ObjectUtils.isEmpty(commonApplicationDto.getCpEmtnlThrptcDtlDto())) {
			BookmarkDto bookmarkTrfckngAddrIssue = createBookmark(BookmarkConstants.TXT_TRFCKNG_ADDR_SPCFC_ISSUE,
					commonApplicationDto.getCpEmtnlThrptcDtlDto().getTxtSpcfcSvcsForChild());
			bookmarkNonFrmGrpList.add(bookmarkTrfckngAddrIssue);
		}
		// artf128758 populate sexual trafficking incident description
		if ( !ObjectUtils.isEmpty(commonApplicationDto.getTrfckngDtoLst()) && ServiceConstants.Zero < commonApplicationDto.getTrfckngDtoLst().size()) {
			for (TraffickingDto traffingDto : commonApplicationDto.getTrfckngDtoLst()) {
				FormDataGroupDto trafIncdntFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_TRFCKNGABUSE, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkTrafIncdntFrmGrpList = new ArrayList<BookmarkDto>();
				if ( !TypeConvUtil.isNullOrEmpty(traffingDto.getDtOfIncdnt())) {
					bookmarkTrafIncdntFrmGrpList.add( createBookmark(BookmarkConstants.TRFCKNGABUSE_DTINCIDENTLABEL, DT_INCDNT));
					bookmarkTrafIncdntFrmGrpList.add( createBookmark(BookmarkConstants.TRFCKNGABUSE_DTINCIDENT, TypeConvUtil.formDateFormat(traffingDto.getDtOfIncdnt())));
				}
				bookmarkTrafIncdntFrmGrpList.add( createBookmark(BookmarkConstants.TRFCKNGABUSE_DTAPPROXIMATE,
						!TypeConvUtil.isNullOrEmpty(traffingDto.getIndApproxDate()) ? APPROXIMATE_DATE : ServiceConstants.EMPTY_STRING));
				bookmarkTrafIncdntFrmGrpList.add( createBookmark(BookmarkConstants.TRFCKNGABUSE_INFO,
						!TypeConvUtil.isNullOrEmpty(traffingDto.getTxtVictimizationComments())
						? traffingDto.getTxtVictimizationComments() : ServiceConstants.EMPTY_STRING));
				trafIncdntFrmDataGrpDto.setBookmarkDtoList(bookmarkTrafIncdntFrmGrpList);
				formDataGroupList.add(trafIncdntFrmDataGrpDto);
			}
		}// artf128758 end
		// End - Trafficking History - Section 4
		// Start - Health Care Summary - Section 5
		if (!ObjectUtils.isEmpty(commonApplicationDto.getCpHealthCareSummaryDto())) {
			// Populate Child Last Health Medical Care Exam value
			BookmarkDto bookmarkHlthCareMedExam = createBookmark(BookmarkConstants.TXT_HLTH_CARE_MED_EXAM,
					TypeConvUtil.formDateFormat(commonApplicationDto.getCpHealthCareSummaryDto().getDtLastTxMedChkp()));
			bookmarkNonFrmGrpList.add(bookmarkHlthCareMedExam);
			// Populate the number of Authorized Nursing Hours value
			BookmarkDto bookmarkTotNursngHrs = createBookmark(BookmarkConstants.TXT_NO_NURSNG_HRS,
					commonApplicationDto.getCpHealthCareSummaryDto().getTxtNursingHrs());
			bookmarkNonFrmGrpList.add(bookmarkTotNursngHrs);
			// Populate the Home Health Agency and Contact Information
			BookmarkDto bookmarkNmHmeHlthAgncy = createBookmark(BookmarkConstants.TXT_NM_HME_HLTH_AGNCY,
					commonApplicationDto.getCpHealthCareSummaryDto().getTxtHmeHlthCnctInfo());
			bookmarkNonFrmGrpList.add(bookmarkNmHmeHlthAgncy);
			// Populate the List Durable Medical Equipment (DME) Supplies
			BookmarkDto bookmarkLstDmeSupls = createBookmark(BookmarkConstants.TXT_LST_DME_SUPLS,
					commonApplicationDto.getCpHealthCareSummaryDto().getTxtLstDmeSupls());
			bookmarkNonFrmGrpList.add(bookmarkLstDmeSupls);
			// Populate the child need to be transported by ambulance value
			if (ServiceConstants.YES
					.equalsIgnoreCase(commonApplicationDto.getCpHealthCareSummaryDto().getIndAmbulnceTrnsprt())) {
				BookmarkDto bookmarkNeedAmbulanceYes = createBookmark(BookmarkConstants.CHK_CHILD_NEED_AMBULANCE_YES,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkNeedAmbulanceYes);
			} else {
				BookmarkDto bookmarkNeedAmbulanceNo = createBookmark(BookmarkConstants.CHK_CHILD_NEED_AMBULANCE_NO,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkNeedAmbulanceNo);
			}
			// Populate Do Not Resuscitate (DNR) value
			if (ServiceConstants.YES.equalsIgnoreCase(commonApplicationDto.getCpHealthCareSummaryDto().getIndDnr())) {
				BookmarkDto bookmarkDnrOrdrYes = createBookmark(BookmarkConstants.CHK_DNR_ORDR_YES,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkDnrOrdrYes);
			} else {
				BookmarkDto bookmarkDnrOrdrNo = createBookmark(BookmarkConstants.CHK_DNR_ORDR_NO,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkDnrOrdrNo);
			}
		}
		if (!ObjectUtils.isEmpty(commonApplicationDto.getCpEmtnlThrptcDtlDto())) {
			// Populate the child or youth’s emotional strengths and needs value
			BookmarkDto bookmarkEmtnlStrength = createBookmark(BookmarkConstants.TXT_EMTNL_STRENGTH,
					commonApplicationDto.getCpEmtnlThrptcDtlDto().getTxtEmtnlNeeds());
			bookmarkNonFrmGrpList.add(bookmarkEmtnlStrength);
			// Populate therapist impressions and/or diagnosis value
			BookmarkDto bookmarkThrpstDiagnsis = createBookmark(BookmarkConstants.TXT_THRPST_DIAGNSIS,
					commonApplicationDto.getCpEmtnlThrptcDtlDto().getTxtTherapistDiagnsis());
			bookmarkNonFrmGrpList.add(bookmarkThrpstDiagnsis);
			// Populate the date of CANS assessment value
			BookmarkDto bookmarkDtCansAssmnt = createBookmark(BookmarkConstants.TXT_DT_CANS_ASSMNT,
					TypeConvUtil.formDateFormat(commonApplicationDto.getCpEmtnlThrptcDtlDto().getDtCansAssmt()));
			bookmarkNonFrmGrpList.add(bookmarkDtCansAssmnt);
		}
		// End - Health Care Summary - Section 5
		// Section 6 - Substance Abuse - No Prefill data for this section
		// Section 7 - Youth Parntg - No Prefill data for this section
		// Section 8 - Risk Bhvr - No Prefill data for this section
		// Start -Sexualized Behaviors - Section 9
		// Populate Child sexually aggressive behavior current episodes before fcl release artf128758	
		if (!commonApplicationDto.getIsAfterFclRelease() && commonApplicationDto.getChildCSABhvr()) {
			BookmarkDto bookmarkChildSxBhvrYes = createBookmark(BookmarkConstants.CHK_SEX_AGGRESS_BHVR_YES,
					ServiceConstants.CHECKED_TRUE);
			bookmarkNonFrmGrpList.add(bookmarkChildSxBhvrYes);
		} else {
			BookmarkDto bookmarkChildSxBhvrNo = createBookmark(BookmarkConstants.CHK_SEX_AGGRESS_BHVR_NO,
					ServiceConstants.CHECKED_TRUE);
			bookmarkNonFrmGrpList.add(bookmarkChildSxBhvrNo);
		}
		// Populate Child sexually behavior Episode start Date  ( before fcl release artf128758 )
		if (!commonApplicationDto.getIsAfterFclRelease() && !ObjectUtils.isEmpty(commonApplicationDto.getCsaEpisodeDto()) && commonApplicationDto.getChildCSABhvr()) {
			BookmarkDto bookmarkEpsodeStartDt = createBookmark(BookmarkConstants.TXT_SXBHVR_EPSODE_START_DT,
					TypeConvUtil.formDateFormat(commonApplicationDto.getCsaEpisodeDto().getDtEpisodeStart()));
			bookmarkNonFrmGrpList.add(bookmarkEpsodeStartDt);
		}	
		// Populate Child sexually behavior Incident start Date and Description  (before fcl release)
		if (!commonApplicationDto.getIsAfterFclRelease() && commonApplicationDto.getChildCSABhvr() && !ObjectUtils.isEmpty(commonApplicationDto.getCsaEpisodesIncdntDtoLst())
				&& commonApplicationDto.getCsaEpisodesIncdntDtoLst().size() > ServiceConstants.Zero) {			
			for (CsaEpisodesIncdntDto csaEpisodesIncdntDto : commonApplicationDto.getCsaEpisodesIncdntDtoLst()) {
				FormDataGroupDto tempSexBhvrIncdntFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SXBHVR_INICIDENT, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarksexBhvrIncdntFrmGrpList = new ArrayList<>();
				String incdntDtls = DT_INCDNT.concat(TypeConvUtil.formDateFormat(csaEpisodesIncdntDto.getDtIncdnt()))
						.concat(ServiceConstants.EMPTY_LINE).concat(INCDNT_DESCRPTN)
						.concat(!ObjectUtils.isEmpty(csaEpisodesIncdntDto.getTxtIncdntDesc())
								? csaEpisodesIncdntDto.getTxtIncdntDesc() : ServiceConstants.EMPTY_STRING)
						.concat(ServiceConstants.EMPTY_LINE).concat(ServiceConstants.EMPTY_LINE);
				BookmarkDto bookmarkIncdntDescrp = createBookmark(BookmarkConstants.TXT_SXBHVR_INICIDENT_DESCRPTN,
						incdntDtls);
				bookmarksexBhvrIncdntFrmGrpList.add(bookmarkIncdntDescrp);
				tempSexBhvrIncdntFrmDataGrpDto.setBookmarkDtoList(bookmarksexBhvrIncdntFrmGrpList);
				formDataGroupList.add(tempSexBhvrIncdntFrmDataGrpDto);
			}			
		} // end current sex aggress bhvr before fcl release
		
		// Populate Child sexually aggressive behavior	after fcl release
		if (commonApplicationDto.getIsAfterFclRelease() && !ObjectUtils.isEmpty(commonApplicationDto.getChildCSABhvr())) {
			bookmarkNonFrmGrpList.add( createBookmark(BookmarkConstants.SEXAGGRBHR, TypeConvUtil.getYesNoBoolean( commonApplicationDto.getChildCSABhvr())));
		}
		// Populate Child sexually behavior Episode start Date ( after fcl release artf128758 )
		if (commonApplicationDto.getIsAfterFclRelease() && !ObjectUtils.isEmpty(commonApplicationDto.getCsaEpisodeDto()) && commonApplicationDto.getChildCSABhvr()
				 && !ObjectUtils.isEmpty(commonApplicationDto.getCsaEpisodesIncdntDtoLst())
					&& commonApplicationDto.getCsaEpisodesIncdntDtoLst().size() > ServiceConstants.Zero) {
			BookmarkDto bookmarkEpsodeStartDt = createBookmark(BookmarkConstants.TXT_SXBHVR_EPSODE_START_DT,
					DT_EPISODE_START.concat(TypeConvUtil.formDateFormat(commonApplicationDto.getCsaEpisodeDto().getDtEpisodeStart())));
			bookmarkNonFrmGrpList.add(bookmarkEpsodeStartDt);
		}	
		// Populate Child sexually behavior Incident start Date and Description ( after fcl release artf128758 )
		if ( commonApplicationDto.getIsAfterFclRelease() && commonApplicationDto.getChildCSABhvr() && !ObjectUtils.isEmpty(commonApplicationDto.getCsaEpisodesIncdntDtoLst())
				&& commonApplicationDto.getCsaEpisodesIncdntDtoLst().size() > ServiceConstants.Zero) {	
			BookmarkDto bookmarkSexAggIncdntsLabel = createBookmark(BookmarkConstants.SEXAGGRBHRLABEL, INCDNT_DESCRPTNS);
			bookmarkNonFrmGrpList.add(bookmarkSexAggIncdntsLabel);
			for (CsaEpisodesIncdntDto csaEpisodesIncdntDto : commonApplicationDto.getCsaEpisodesIncdntDtoLst()) {
				FormDataGroupDto tempSexBhvrIncdntFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SEXAGGRBHR, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarksexBhvrIncdntFrmGrpList = new ArrayList<>();
				bookmarksexBhvrIncdntFrmGrpList.add( createBookmark(BookmarkConstants.SEXAGGRBHR_DTINCDNT, 
						DT_INCDNT.concat(TypeConvUtil.formDateFormat(csaEpisodesIncdntDto.getDtIncdnt()))));
				bookmarksexBhvrIncdntFrmGrpList.add( createBookmark(BookmarkConstants.SEXAGGRBHR_DESCR, 
						INCDNT_DESCRPTN.concat(!ObjectUtils.isEmpty(csaEpisodesIncdntDto.getTxtIncdntDesc())
								? csaEpisodesIncdntDto.getTxtIncdntDesc() : ServiceConstants.EMPTY_STRING)));
				tempSexBhvrIncdntFrmDataGrpDto.setBookmarkDtoList(bookmarksexBhvrIncdntFrmGrpList);
				formDataGroupList.add(tempSexBhvrIncdntFrmDataGrpDto);
			}			
		} // end current sex aggress bhvr after fcl release
		
		/*
		 *  Populate the child or youth have any problematic sexual behavior
		 *  value before artf128758
		 */
		if (!commonApplicationDto.getIsAfterFclRelease() && !ObjectUtils.isEmpty(commonApplicationDto.getIndChildPrblmSexualBhvr())
				&& commonApplicationDto.getIndChildPrblmSexualBhvr()) {
			//Warranty defect 11633 - Setting the Checked true Attribute
			BookmarkDto bookmarkChildprblmSxBhvrYes = createBookmark(BookmarkConstants.CHK_PRBLMTC_SEX_BHVR_YES,
					ServiceConstants.CHECKED_TRUE);
			bookmarkNonFrmGrpList.add(bookmarkChildprblmSxBhvrYes);
		} else{
			//Warranty defect 11633 - Setting the Checked true Attribute
			BookmarkDto bookmarkChildprblmSxBhvrNo = createBookmark(BookmarkConstants.CHK_PRBLMTC_SEX_BHVR_NO,
					ServiceConstants.CHECKED_TRUE);
			bookmarkNonFrmGrpList.add(bookmarkChildprblmSxBhvrNo);
		}
		// Populate the description behavior, when it happened, and how it was
		// managed
		if (!commonApplicationDto.getIsAfterFclRelease() && !ObjectUtils.isEmpty(commonApplicationDto.getCsaEpisodesClosedIncdntDtoLst())
				&& !ObjectUtils.isEmpty(commonApplicationDto.getCsaEpisodeClosedDtoLst())
				&& commonApplicationDto.getCsaEpisodeClosedDtoLst().size() > ServiceConstants.Zero
				&& commonApplicationDto.getCsaEpisodesClosedIncdntDtoLst().size() > ServiceConstants.Zero) {			
			for (CSAEpisodeDto csaEpisodeClosedDto : commonApplicationDto.getCsaEpisodeClosedDtoLst()) {
				List<CsaEpisodesIncdntDto> csaEpisodesClosedIncdntLst = new ArrayList<>();
				csaEpisodesClosedIncdntLst = commonApplicationDto.getCsaEpisodesClosedIncdntDtoLst().stream()
						.filter(child -> child.getIdCsaEpisodes().equals(csaEpisodeClosedDto.getIdCsaEpisodes()))
						.collect(Collectors.toList());
				for (CsaEpisodesIncdntDto csaEpisodesClosedIncdntDto : csaEpisodesClosedIncdntLst) {
					FormDataGroupDto tempDescrbSexBhvrFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_DESCRB_PRBLMTC_SXBHVR,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkDescrbSexBhvrFrmGrpList = new ArrayList<>();
					String incdntDtls = DT_EPISODE_START
							.concat(TypeConvUtil.formDateFormat(csaEpisodeClosedDto.getDtEpisodeStart()))
							.concat(ServiceConstants.EMPTY_LINE).concat(DT_EPISODE_END)
							.concat(TypeConvUtil.formDateFormat(csaEpisodeClosedDto.getDtEpisodeEnd()))
							.concat(ServiceConstants.EMPTY_LINE).concat(DT_INCDNT)
							.concat(TypeConvUtil.formDateFormat(csaEpisodesClosedIncdntDto.getDtIncdnt()))
							.concat(ServiceConstants.EMPTY_LINE).concat(INCDNT_DESCRPTN)
							.concat(!ObjectUtils.isEmpty(csaEpisodesClosedIncdntDto.getTxtIncdntDesc())
									? csaEpisodesClosedIncdntDto.getTxtIncdntDesc() : ServiceConstants.EMPTY_STRING)
							.concat(ServiceConstants.EMPTY_LINE).concat(ServiceConstants.EMPTY_LINE);
					BookmarkDto bookmarkEpispdeIncdntDtl = createBookmark(BookmarkConstants.TXT_DESCRB_PRBLMTC_SEX_BHVR,
							incdntDtls);
					bookmarkDescrbSexBhvrFrmGrpList.add(bookmarkEpispdeIncdntDtl);
					tempDescrbSexBhvrFrmDataGrpDto.setBookmarkDtoList(bookmarkDescrbSexBhvrFrmGrpList);
					formDataGroupList.add(tempDescrbSexBhvrFrmDataGrpDto);
				}
			}
		} // end before fcl release - code can be removed when all older versions are in approved event status

		/*
		 *  Populate the child or youth have any problematic sexual behavior value after artf128758
		 */

		if (!ObjectUtils.isEmpty(commonApplicationDto.getSexVicHistDto()) &&  // artf131169 ...
				!TypeConvUtil.isNullOrEmpty(commonApplicationDto.getSexVicHistDto().getIndSexualBehaviorProblem())) {
			bookmarkNonFrmGrpList.add( createBookmarkWithCodesTable(BookmarkConstants.PRBLSXBHVR,
					commonApplicationDto.getSexVicHistDto().getIndSexualBehaviorProblem(), CodesConstant.CINVACAN));
			if(!ObjectUtils.isEmpty(commonApplicationDto.getSexVicHistDto().getTxtBehaviorProblems())) {
			    if(commonApplicationDto.getSexVicHistDto().getIndSexualBehaviorProblem().equals("Y")) {
                    String description = commonApplicationDto.getSexVicHistDto().getTxtBehaviorProblems();
                    bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.PRBLSXBHVR_INCDNT_DESCR, description));
                }
			} else {
			    if (commonApplicationDto.getCsaEpisodeClosedDtoLst() != null) {
                    for (CSAEpisodeDto csaEpisodeClosedDto : commonApplicationDto.getCsaEpisodeClosedDtoLst()) {
                        List<CsaEpisodesIncdntDto> csaEpisodesClosedIncdntLst = new ArrayList<>();
                        csaEpisodesClosedIncdntLst = commonApplicationDto.getCsaEpisodesClosedIncdntDtoLst().stream()
                            .filter(child -> child.getIdCsaEpisodes().equals(csaEpisodeClosedDto.getIdCsaEpisodes()))
                            .collect(Collectors.toList());
                        for (CsaEpisodesIncdntDto csaEpisodesClosedIncdntDto : csaEpisodesClosedIncdntLst) {
                            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.PRBLSXBHVR_INCDNT_DESCR,
                                INCDNT_DESCRPTN.concat(!ObjectUtils.isEmpty(csaEpisodesClosedIncdntDto.getTxtIncdntDesc())
                                    ? csaEpisodesClosedIncdntDto.getTxtIncdntDesc() : ServiceConstants.EMPTY_STRING)));
                        }
                    }
                }
			}

		}

		// Populate the description behavior, when it happened, and how it was  managed
		if (commonApplicationDto.getIsAfterFclRelease()
				&& !ObjectUtils.isEmpty(commonApplicationDto.getSexVicHistDto())
				&& !ObjectUtils.isEmpty(commonApplicationDto.getCsaEpisodeClosedDtoLst())
				&& commonApplicationDto.getCsaEpisodeClosedDtoLst().size() > ServiceConstants.Zero
				&& commonApplicationDto.getCsaEpisodesClosedIncdntDtoLst().size() > ServiceConstants.Zero) {
			for (CSAEpisodeDto csaEpisodeClosedDto : commonApplicationDto.getCsaEpisodeClosedDtoLst()) {
				FormDataGroupDto episodeSexBhvrFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PRBLSXBHVR, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkEpisodeSexBhvrFrmGrpList = new ArrayList<>();
				bookmarkEpisodeSexBhvrFrmGrpList.add( createBookmark(BookmarkConstants.PRBLSXBHVR_DTEPSDSTART,
						DT_EPISODE_START.concat(TypeConvUtil.formDateFormat(csaEpisodeClosedDto.getDtEpisodeStart()))));
				bookmarkEpisodeSexBhvrFrmGrpList.add( createBookmark(BookmarkConstants.PRBLSXBHVR_DTEPSDEND,
						DT_EPISODE_END.concat(TypeConvUtil.formDateFormat(csaEpisodeClosedDto.getDtEpisodeEnd()))));
				episodeSexBhvrFrmDataGrpDto.setBookmarkDtoList(bookmarkEpisodeSexBhvrFrmGrpList);
				
				List<FormDataGroupDto> sexAggIncdntGroupList = new ArrayList<>();
				List<CsaEpisodesIncdntDto> csaEpisodesClosedIncdntLst = new ArrayList<>();
				csaEpisodesClosedIncdntLst = commonApplicationDto.getCsaEpisodesClosedIncdntDtoLst().stream()
						.filter(child -> child.getIdCsaEpisodes().equals(csaEpisodeClosedDto.getIdCsaEpisodes()))
						.collect(Collectors.toList());
				for (CsaEpisodesIncdntDto csaEpisodesClosedIncdntDto : csaEpisodesClosedIncdntLst) {
						FormDataGroupDto incdntSexBhvrFrmDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_PRBLSXBHVR_INCDNT, FormGroupsConstants.TMPLAT_PRBLSXBHVR);
						List<BookmarkDto> bookmarkIncdntSexBhvrFrmGrpList = new ArrayList<>();
						bookmarkIncdntSexBhvrFrmGrpList.add( createBookmark(BookmarkConstants.PRBLSXBHVR_INCDNT_DT,
								DT_INCDNT.concat(TypeConvUtil.formDateFormat(csaEpisodesClosedIncdntDto.getDtIncdnt()))));
						bookmarkIncdntSexBhvrFrmGrpList.add( createBookmark(BookmarkConstants.PRBLSXBHVR_INCDNT_DESCR,
								INCDNT_DESCRPTN.concat(!ObjectUtils.isEmpty(commonApplicationDto.getSexVicHistDto().getTxtBehaviorProblems())
										? commonApplicationDto.getSexVicHistDto().getTxtBehaviorProblems() : ServiceConstants.EMPTY_STRING)));
						incdntSexBhvrFrmDataGrpDto.setBookmarkDtoList(bookmarkIncdntSexBhvrFrmGrpList);
						sexAggIncdntGroupList.add(incdntSexBhvrFrmDataGrpDto);
						episodeSexBhvrFrmDataGrpDto.setFormDataGroupList(sexAggIncdntGroupList);
				}
				formDataGroupList.add(episodeSexBhvrFrmDataGrpDto);
			}
		} // end after fcl release artf128758
		
		// End -Sexualized Behaviors - Section 9
		// Start -Education - Section 10
		if (!ObjectUtils.isEmpty(commonApplicationDto.getEducationHistoryDto())) {
			BookmarkDto bookmarkSchoolName = createBookmark(BookmarkConstants.TXT_EDU_SCH_NM,
					commonApplicationDto.getEducationHistoryDto().getNmEdHistSchool());
			bookmarkNonFrmGrpList.add(bookmarkSchoolName);
			BookmarkDto bookmarkSchWithDrawnDt = createBookmark(BookmarkConstants.TXT_EDU_WITHDRAWN_DT,
					commonApplicationDto.getEducationHistoryDto().getDtEdHistWithdrawn());
			bookmarkNonFrmGrpList.add(bookmarkSchWithDrawnDt);
			BookmarkDto bookmarkSchCurrGrd = createBookmarkWithCodesTable(BookmarkConstants.TXT_EDU_CURR_GRADE,
					commonApplicationDto.getEducationHistoryDto().getCdEdHistEnrollGrade(), CodesConstant.CSCHGRAD);
			bookmarkNonFrmGrpList.add(bookmarkSchCurrGrd);
			BookmarkDto bookmarkSchLastAdmDt = createBookmark(BookmarkConstants.TXT_EDU_SCH_LAST_ADMISSION_DT,
					TypeConvUtil.formDateFormat(commonApplicationDto.getEducationHistoryDto().getDtLastArdiep()));
			bookmarkNonFrmGrpList.add(bookmarkSchLastAdmDt);
		}
		if (!ObjectUtils.isEmpty(commonApplicationDto.getCpEducationDto())) {
			if (ServiceConstants.YES.equalsIgnoreCase(commonApplicationDto.getCpEducationDto().getIndGrdLvl())) {
				BookmarkDto bookmarkEduGrdlvlYes = createBookmark(BookmarkConstants.CHK_EDU_GRADE_LVL_YES,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkEduGrdlvlYes);
			} else {
				BookmarkDto bookmarkEduGrdlvlNo = createBookmark(BookmarkConstants.CHK_EDU_GRADE_LVL_NO,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkEduGrdlvlNo);
			}
			BookmarkDto bookmarkEduExtraActv = createBookmark(BookmarkConstants.TXT_EDU_EXTRA_ACTV,
					commonApplicationDto.getCpEducationDto().getTxtLstExtraActv());
			bookmarkNonFrmGrpList.add(bookmarkEduExtraActv);
			if (ServiceConstants.YES
					.equalsIgnoreCase(commonApplicationDto.getCpEducationDto().getIndChildEnrlldSch())) {
				BookmarkDto bookmarkEnrlldSchYes = createBookmark(BookmarkConstants.CHK_EDU_CURR_ENRLLD_SCH_YES,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkEnrlldSchYes);
			} else {
				BookmarkDto bookmarkEnrlldSchNo = createBookmark(BookmarkConstants.CHK_EDU_CURR_ENRLLD_SCH_NO,
						ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkEnrlldSchNo);
			}
			BookmarkDto bookmarkDescrbEduStrength = createBookmark(BookmarkConstants.TXT_DESCRB_EDU_STRENGTH,
					commonApplicationDto.getCpEducationDto().getTxtEductnStrength());
			bookmarkNonFrmGrpList.add(bookmarkDescrbEduStrength);
			BookmarkDto bookmarkDescrbEduNeeds = createBookmark(BookmarkConstants.TXT_DESCRB_ADTNL_EDU_NEEDS,
					commonApplicationDto.getCpEducationDto().getTxtSpeclEductnSvcs());
			bookmarkNonFrmGrpList.add(bookmarkDescrbEduNeeds);
		}
		String schoolAddr = ServiceConstants.EMPTY_STRING;
		String schoolCity = ServiceConstants.EMPTY_STRING;
		String schoolState = ServiceConstants.EMPTY_STRING;
		
		if (!ObjectUtils.isEmpty(commonApplicationDto.getRsrcAddrPhoneDto())) {
			schoolAddr = !ObjectUtils.isEmpty(commonApplicationDto.getRsrcAddrPhoneDto().getAddrRsrcAddrStLn1())
					? commonApplicationDto.getRsrcAddrPhoneDto().getAddrRsrcAddrStLn1()
					: ServiceConstants.EMPTY_STRING.concat(
							!ObjectUtils.isEmpty(commonApplicationDto.getRsrcAddrPhoneDto().getAddrRsrcAddrStLn1())
									? commonApplicationDto.getRsrcAddrPhoneDto().getAddrRsrcAddrStLn1()
									: ServiceConstants.EMPTY_STRING);
			schoolCity = !ObjectUtils.isEmpty(commonApplicationDto.getRsrcAddrPhoneDto().getAddrRsrcAddrCity())
					? commonApplicationDto.getRsrcAddrPhoneDto().getAddrRsrcAddrCity() : ServiceConstants.EMPTY_STRING;
			schoolState = !ObjectUtils.isEmpty(commonApplicationDto.getRsrcAddrPhoneDto().getCdRsrcAddrState())
					? commonApplicationDto.getRsrcAddrPhoneDto().getCdRsrcAddrState() : ServiceConstants.EMPTY_STRING;
		} else {
			if (!ObjectUtils.isEmpty(commonApplicationDto.getEducationHistoryDto())) {
				schoolAddr = !ObjectUtils
						.isEmpty(commonApplicationDto.getEducationHistoryDto().getAddrEdHistStreetLn1())
								? commonApplicationDto.getEducationHistoryDto().getAddrEdHistStreetLn1()
								: ServiceConstants.EMPTY_STRING.concat(!ObjectUtils
										.isEmpty(commonApplicationDto.getEducationHistoryDto().getAddrEdHistStreetLn2())
												? commonApplicationDto.getEducationHistoryDto().getAddrEdHistStreetLn2()
												: ServiceConstants.EMPTY_STRING);
				schoolCity = !ObjectUtils.isEmpty(commonApplicationDto.getEducationHistoryDto().getAddrEdHistCity())
						? commonApplicationDto.getEducationHistoryDto().getAddrEdHistCity()
						: ServiceConstants.EMPTY_STRING;
				schoolState = !ObjectUtils.isEmpty(commonApplicationDto.getEducationHistoryDto().getAddrEdHistState())
						? commonApplicationDto.getEducationHistoryDto().getAddrEdHistState() : CodesConstant.CSTATE_TX;
			}
		}
		BookmarkDto bookmarkAddr = createBookmark(BookmarkConstants.TXT_EDU_SCH_ADDR, schoolAddr);
		bookmarkNonFrmGrpList.add(bookmarkAddr);
		BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.TXT_EDU_SCH_CITY, schoolCity);
		bookmarkNonFrmGrpList.add(bookmarkAddrCity);
		BookmarkDto bookmarkAddrState = createBookmarkWithCodesTable(BookmarkConstants.TXT_EDU_SCH_STATE, schoolState,
				CodesConstant.CSTATE);
		bookmarkNonFrmGrpList.add(bookmarkAddrState);
		if (!ObjectUtils.isEmpty(commonApplicationDto.getPrevSchoolAddr())) {
			BookmarkDto bookmarkPrevSchAddr = createBookmark(BookmarkConstants.TXT_EDU_PREV_SCH_ADDR,
					commonApplicationDto.getPrevSchoolAddr());
			bookmarkNonFrmGrpList.add(bookmarkPrevSchAddr);
		}
		if (!ObjectUtils.isEmpty(commonApplicationDto.getPrevSchoolCntct())) {
			BookmarkDto bookmarkPrevSchCntct = createBookmark(BookmarkConstants.TXT_EDU_PREV_SCH_CNCT,
					commonApplicationDto.getPrevSchoolCntct());
			bookmarkNonFrmGrpList.add(bookmarkPrevSchCntct);
		}
		
		// End -Education - Section 10
		// Start - TRANSITION PLANNING FOR A SUCCESSFUL ADULTHOOD (PAL) -
		// Section 11
		if (!ObjectUtils.isEmpty(commonApplicationDto.getCpTransAdltAbvDtlDto())) {
			BookmarkDto bookmarkDescrbLifeSkills = createBookmark(BookmarkConstants.TXT_DESCRB_LIFE_SKILLLS_STRENGTH,
					commonApplicationDto.getCpTransAdltAbvDtlDto().getTxtSkillChlng());
			bookmarkNonFrmGrpList.add(bookmarkDescrbLifeSkills);
			if (ServiceConstants.YES
					.equalsIgnoreCase(commonApplicationDto.getCpTransAdltAbvDtlDto().getIndYouthSkillAssmnt())) {
				BookmarkDto bookmarkLifeSkillAssmntYes = createBookmark(
						BookmarkConstants.CHK_COMP_LIFE_SKILLLS_ASSMNT_YES, ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkLifeSkillAssmntYes);
			} else {
				BookmarkDto bookmarkLifeSkillAssmntNo = createBookmark(
						BookmarkConstants.CHK_COMP_LIFE_SKILLLS_ASSMNT_NO, ServiceConstants.CHECKED);
				bookmarkNonFrmGrpList.add(bookmarkLifeSkillAssmntNo);
			}
			BookmarkDto bookmarkDescrbFstrCare = createBookmark(BookmarkConstants.TXT_DESCRB_YOUTH_STILL_FSTRCARE,
					commonApplicationDto.getCpTransAdltAbvDtlDto().getTxtExtnFostercare());
			bookmarkNonFrmGrpList.add(bookmarkDescrbFstrCare);
		}
		// End - TRANSITION PLANNING FOR A SUCCESSFUL ADULTHOOD (PAL) - Section
		// 11
		// Start - JUVENILE JUSTICE INVOLVEMENT - Section 12
		if (!ObjectUtils.isEmpty(commonApplicationDto.getCpAdtnlSctnDtlDto()) && ServiceConstants.YES
				.equalsIgnoreCase(commonApplicationDto.getCpAdtnlSctnDtlDto().getIndJuvnleJustcInvlvmnt())) {
			BookmarkDto bookmarkJuvnleHistYes = createBookmark(BookmarkConstants.CHK_JUVNLE_JUSTICE_HIST_YES,
					ServiceConstants.CHECKED);
			bookmarkNonFrmGrpList.add(bookmarkJuvnleHistYes);
		} else {
			BookmarkDto bookmarkJuvnleHistNo = createBookmark(BookmarkConstants.CHK_JUVNLE_JUSTICE_HIST_NO,
					ServiceConstants.CHECKED);
			bookmarkNonFrmGrpList.add(bookmarkJuvnleHistNo);
		}
		// End - JUVENILE JUSTICE INVOLVEMENT - Section 12
		// Start -FAMILY HISTORY - Section 13
		// Populate the Child Parents informations.
		int idCountParnt = ServiceConstants.One;
		if (!ObjectUtils.isEmpty(commonApplicationDto.getCaseInfoDtolist())
				&& commonApplicationDto.getCaseInfoDtolist().size() > ServiceConstants.Zero) {
			for (CaseInfoDto caseInfoDto : commonApplicationDto.getCaseInfoDtolist()) {
				FormDataGroupDto tempFmlyHistParntInfoFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_FMLY_HIST_PARNT_INFO, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkFmlyHistParntInfoFrmGrpList = new ArrayList<BookmarkDto>();
				if (ServiceConstants.PARENT_RELTN.contains(caseInfoDto.getCdStagePersRelInt())) {
					BookmarkDto bookmarkParntNm = createBookmark(BookmarkConstants.TXT_FMLY_HIST_NAME,
							TypeConvUtil.formatFullName(caseInfoDto.getNmNameFirst(), caseInfoDto.getNmNameMiddle(),
									caseInfoDto.getNmNameLast()));
					bookmarkFmlyHistParntInfoFrmGrpList.add(bookmarkParntNm);
					BookmarkDto bookmarkParntReltn = createBookmarkWithCodesTable(BookmarkConstants.TXT_FMLY_HIST_RELTN,
							caseInfoDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
					bookmarkFmlyHistParntInfoFrmGrpList.add(bookmarkParntReltn);
					BookmarkDto bookmarkParntDob = createBookmark(BookmarkConstants.TXT_FMLY_HIST_DOB,
							TypeConvUtil.formDateFormat(caseInfoDto.getDtPersonBirth()));
					bookmarkFmlyHistParntInfoFrmGrpList.add(bookmarkParntDob);
					BookmarkDto bookmarkParntDod = createBookmark(BookmarkConstants.TXT_FMLY_HIST_DOD,
							TypeConvUtil.formDateFormat(caseInfoDto.getDtPersonDeath()));
					bookmarkFmlyHistParntInfoFrmGrpList.add(bookmarkParntDod);
					BookmarkDto bookmarkParntPhNo = createBookmark(BookmarkConstants.TXT_FMLY_HIST_PH_NO,
							TypeConvUtil.formatPhone(caseInfoDto.getNbrPersonPhone()));
					bookmarkFmlyHistParntInfoFrmGrpList.add(bookmarkParntPhNo);
					String parntAddr = !ObjectUtils.isEmpty(caseInfoDto.getAddrPersAddrStLn1())
							? caseInfoDto.getAddrPersAddrStLn1()
							: ServiceConstants.EMPTY_STRING.concat(
									!ObjectUtils.isEmpty(caseInfoDto.getAddrPersAddrStLn2())
											? caseInfoDto.getAddrPersAddrStLn2()
											: ServiceConstants.EMPTY_STRING)
									.concat(!ObjectUtils
											.isEmpty(caseInfoDto.getAddrPersonAddrCity())
													? caseInfoDto.getAddrPersonAddrCity()
													: ServiceConstants.EMPTY_STRING)
									.concat(!ObjectUtils.isEmpty(caseInfoDto.getCdPersonAddrState())
											? caseInfoDto.getCdPersonAddrState()
											: ServiceConstants.EMPTY_STRING)
									.concat(!ObjectUtils
											.isEmpty(caseInfoDto.getAddrPersonAddrZip())
													? caseInfoDto.getAddrPersonAddrZip()
													: ServiceConstants.EMPTY_STRING);
					BookmarkDto bookmarkParntAddr = createBookmark(BookmarkConstants.TXT_FMLY_HIST_ADDR, parntAddr);
					bookmarkFmlyHistParntInfoFrmGrpList.add(bookmarkParntAddr);
					BookmarkDto bookmarkParntPrmyLang = createBookmarkWithCodesTable(BookmarkConstants.TXT_FMLY_HIST_PRMYLANG,
							caseInfoDto.getCdPersonLanguage(), CodesConstant.CLANG);
					bookmarkFmlyHistParntInfoFrmGrpList.add(bookmarkParntPrmyLang);
					BookmarkDto bookmarkFmlyParntGroupId = createBookmark(BookmarkConstants.UE_GROUPID,
							caseInfoDto.getIdPerson());
					bookmarkFmlyHistParntInfoFrmGrpList.add(bookmarkFmlyParntGroupId);
					idCountParnt = idCountParnt + ServiceConstants.One;
					tempFmlyHistParntInfoFrmDataGrpDto.setBookmarkDtoList(bookmarkFmlyHistParntInfoFrmGrpList);
					formDataGroupList.add(tempFmlyHistParntInfoFrmDataGrpDto);
				}
			}
		}
		// Populate Balance Rows for the table with empty records
		for (int parntbalRowsAddng = idCountParnt
				- ServiceConstants.One; parntbalRowsAddng < ServiceConstants.FIVEININT; parntbalRowsAddng++) {
			FormDataGroupDto tempFmlyHistParntInfoFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_FMLY_HIST_PARNT_INFO, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkFmlyHistParntInfoFrmGrpList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkParntNm = createBookmark(BookmarkConstants.TXT_FMLY_HIST_NAME,
					ServiceConstants.EMPTY_STRING);
			bookmarkFmlyHistParntInfoFrmGrpList.add(bookmarkParntNm);
			BookmarkDto bookmarkParntReltn = createBookmark(BookmarkConstants.TXT_FMLY_HIST_RELTN,
					ServiceConstants.EMPTY_STRING);
			bookmarkFmlyHistParntInfoFrmGrpList.add(bookmarkParntReltn);
			BookmarkDto bookmarkParntDob = createBookmark(BookmarkConstants.TXT_FMLY_HIST_RELTN,
					ServiceConstants.EMPTY_STRING);
			bookmarkFmlyHistParntInfoFrmGrpList.add(bookmarkParntDob);
			BookmarkDto bookmarkParntDod = createBookmark(BookmarkConstants.TXT_FMLY_HIST_DOD,
					ServiceConstants.EMPTY_STRING);
			bookmarkFmlyHistParntInfoFrmGrpList.add(bookmarkParntDod);
			BookmarkDto bookmarkParntPhNo = createBookmark(BookmarkConstants.TXT_FMLY_HIST_PH_NO,
					ServiceConstants.EMPTY_STRING);
			bookmarkFmlyHistParntInfoFrmGrpList.add(bookmarkParntPhNo);
			BookmarkDto bookmarkParntAddr = createBookmark(BookmarkConstants.TXT_FMLY_HIST_ADDR,
					ServiceConstants.EMPTY_STRING);
			bookmarkFmlyHistParntInfoFrmGrpList.add(bookmarkParntAddr);
			BookmarkDto bookmarkParntPrmyLang = createBookmark(BookmarkConstants.TXT_FMLY_HIST_PRMYLANG,
					ServiceConstants.EMPTY_STRING);
			bookmarkFmlyHistParntInfoFrmGrpList.add(bookmarkParntPrmyLang);
			BookmarkDto bookmarkParntInfoGroupId = createBookmark(BookmarkConstants.UE_GROUPID, parntbalRowsAddng);
			bookmarkFmlyHistParntInfoFrmGrpList.add(bookmarkParntInfoGroupId);
			tempFmlyHistParntInfoFrmDataGrpDto.setBookmarkDtoList(bookmarkFmlyHistParntInfoFrmGrpList);
			formDataGroupList.add(tempFmlyHistParntInfoFrmDataGrpDto);
		}
		// Populate the Child Siblings informations.
		
		int idCountSib = ServiceConstants.One;
		if (!ObjectUtils.isEmpty(commonApplicationDto.getCaseInfoDtolist())
				&& commonApplicationDto.getCaseInfoDtolist().size() > ServiceConstants.Zero) {			
			for (CaseInfoDto caseInfoDto : commonApplicationDto.getCaseInfoDtolist()) {
				if (CodesConstant.CRPTRINT_SB.equalsIgnoreCase(caseInfoDto.getCdStagePersRelInt())) {
					FormDataGroupDto tempFmlyHistSiblngInfoFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_FMLY_HIST_SIB_INFO, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkFmlyHistSiblngInfoList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkSibNm = createBookmark(BookmarkConstants.TXT_FMLY_HIST_SIB,
							TypeConvUtil.formatFullName(caseInfoDto.getNmNameFirst(), caseInfoDto.getNmNameMiddle(),
									caseInfoDto.getNmNameLast()));
					bookmarkFmlyHistSiblngInfoList.add(bookmarkSibNm);							
					BookmarkDto bookmarkSibDob = createBookmark(BookmarkConstants.TXT_FMLY_HIST_SIB_DOB,
							TypeConvUtil.formDateFormat(caseInfoDto.getDtPersonBirth()));
					bookmarkFmlyHistSiblngInfoList.add(bookmarkSibDob);
					String SiblingAddr = !ObjectUtils.isEmpty(caseInfoDto.getAddrPersAddrStLn1())
							? caseInfoDto.getAddrPersAddrStLn1()
							: ServiceConstants.EMPTY_STRING
									.concat(!ObjectUtils.isEmpty(caseInfoDto.getAddrPersAddrStLn2())
											? caseInfoDto.getAddrPersAddrStLn2() : ServiceConstants.EMPTY_STRING)
									.concat(!ObjectUtils.isEmpty(caseInfoDto.getAddrPersonAddrCity())
											? caseInfoDto.getAddrPersonAddrCity() : ServiceConstants.EMPTY_STRING)
									.concat(!ObjectUtils.isEmpty(caseInfoDto.getCdPersonAddrState())
											? caseInfoDto.getCdPersonAddrState() : ServiceConstants.EMPTY_STRING)
									.concat(!ObjectUtils.isEmpty(caseInfoDto.getAddrPersonAddrZip())
											? caseInfoDto.getAddrPersonAddrZip() : ServiceConstants.EMPTY_STRING);
					BookmarkDto bookmarkSibAddr = createBookmark(BookmarkConstants.TXT_FMLY_HIST_SIB_ADDR, SiblingAddr);
					bookmarkFmlyHistSiblngInfoList.add(bookmarkSibAddr);
					BookmarkDto bookmarkSibGroupId = createBookmark(BookmarkConstants.UE_GROUPID,
							caseInfoDto.getIdPerson());
					bookmarkFmlyHistSiblngInfoList.add(bookmarkSibGroupId);
					idCountSib = idCountSib + ServiceConstants.One;
					tempFmlyHistSiblngInfoFrmDataGrpDto.setBookmarkDtoList(bookmarkFmlyHistSiblngInfoList);
					formDataGroupList.add(tempFmlyHistSiblngInfoFrmDataGrpDto);
				}
			}			
		}
		for (int sibbalRowsAddng = idCountSib
				- ServiceConstants.One; sibbalRowsAddng < ServiceConstants.TEN; sibbalRowsAddng++) {
			FormDataGroupDto tempFmlyHistSiblngInfoFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_FMLY_HIST_SIB_INFO, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkFmlyHistSiblngInfoList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkSibNm = createBookmark(BookmarkConstants.TXT_FMLY_HIST_SIB,
					ServiceConstants.EMPTY_STRING);
			bookmarkFmlyHistSiblngInfoList.add(bookmarkSibNm);
			BookmarkDto bookmarkSibDob = createBookmark(BookmarkConstants.TXT_FMLY_HIST_SIB_DOB,
					ServiceConstants.EMPTY_STRING);
			bookmarkFmlyHistSiblngInfoList.add(bookmarkSibDob);
			BookmarkDto bookmarkSibAddr = createBookmark(BookmarkConstants.TXT_FMLY_HIST_SIB_ADDR,
					ServiceConstants.EMPTY_STRING);
			bookmarkFmlyHistSiblngInfoList.add(bookmarkSibAddr);
			BookmarkDto bookmarkSibGroupId = createBookmark(BookmarkConstants.UE_GROUPID, sibbalRowsAddng);
			bookmarkFmlyHistSiblngInfoList.add(bookmarkSibGroupId);
			tempFmlyHistSiblngInfoFrmDataGrpDto.setBookmarkDtoList(bookmarkFmlyHistSiblngInfoList);
			formDataGroupList.add(tempFmlyHistSiblngInfoFrmDataGrpDto);
		}
		
		// Populate the Child Plan for Visitation and Contacts With Family
		// informations.
		int idCountPlanVisit = ServiceConstants.One;
		if (!ObjectUtils.isEmpty(commonApplicationDto.getVisitationPlanInfoDtlsDtoLst())
				&& commonApplicationDto.getVisitationPlanInfoDtlsDtoLst().size() > ServiceConstants.Zero) {
			for (VisitationPlanInfoDtlsDto visitationPlanInfoDtlsDto : commonApplicationDto
					.getVisitationPlanInfoDtlsDtoLst()) {
				FormDataGroupDto tempFmlyPlanVisitinfoFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PLAN_VISIT_CNTCT_INFO, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkFmlyPlanVisitinfoFrmGrpList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkFmlyHistPlanPrsnNm = createBookmark(BookmarkConstants.TXT_FMLY_HIST_VISIT_PARTCPNT,
						visitationPlanInfoDtlsDto.getNmPersonFull());
				bookmarkFmlyPlanVisitinfoFrmGrpList.add(bookmarkFmlyHistPlanPrsnNm);
				BookmarkDto bookmarkFmlyHistPlanReltnNm = createBookmarkWithCodesTable(
						BookmarkConstants.TXT_FMLY_HIST_VISIT_RELTN, visitationPlanInfoDtlsDto.getCdStagePersRelInt(),
						CodesConstant.CRPTRINT);
				bookmarkFmlyPlanVisitinfoFrmGrpList.add(bookmarkFmlyHistPlanReltnNm);
				BookmarkDto bookmarkFmlyHistPlanLngthNm = createBookmark(BookmarkConstants.TXT_FMLY_HIST_VISIT_LNGTH,
						TypeConvUtil.removeAccentSpecialChars(visitationPlanInfoDtlsDto.getTxtLngthOfVisit()));
				bookmarkFmlyPlanVisitinfoFrmGrpList.add(bookmarkFmlyHistPlanLngthNm);
				BookmarkDto bookmarkFmlyHistPlanFreqNm = createBookmark(BookmarkConstants.TXT_FMLY_HIST_VISIT_FREQ,
						TypeConvUtil.removeAccentSpecialChars(visitationPlanInfoDtlsDto.getTxtVisitFreq()));
				bookmarkFmlyPlanVisitinfoFrmGrpList.add(bookmarkFmlyHistPlanFreqNm);
				BookmarkDto bookmarkFmlyHistPlanDaysNm = createBookmark(BookmarkConstants.TXT_FMLY_HIST_VISIT_DAYS,
						TypeConvUtil.removeAccentSpecialChars(visitationPlanInfoDtlsDto.getTxtDayAndTime()));
				bookmarkFmlyPlanVisitinfoFrmGrpList.add(bookmarkFmlyHistPlanDaysNm);
				BookmarkDto bookmarkFmlyHistPlanLoctnNm = createBookmark(BookmarkConstants.TXT_FMLY_HIST_VISIT_LOCTN,
						TypeConvUtil.removeAccentSpecialChars(visitationPlanInfoDtlsDto.getTxtVisitLoctn()));
				bookmarkFmlyPlanVisitinfoFrmGrpList.add(bookmarkFmlyHistPlanLoctnNm);
				//artf148785 : ALM ID : 14793 : UE_GROUP value should be unique for each participant but idVisitPlan is same for all participants.
				// since only one participant is saved until now from this section
				// we can use idVisitPlan for first record and Participant idPerson for the rest.
				BookmarkDto bookmarkFmlyHistGroupId = createBookmark(BookmarkConstants.UE_GROUPID,
					idCountPlanVisit == 1 ? visitationPlanInfoDtlsDto.getIdVisitPlan() : visitationPlanInfoDtlsDto.getIdPerson());

				bookmarkFmlyPlanVisitinfoFrmGrpList.add(bookmarkFmlyHistGroupId);
				idCountPlanVisit += ServiceConstants.One;
				tempFmlyPlanVisitinfoFrmDataGrpDto.setBookmarkDtoList(bookmarkFmlyPlanVisitinfoFrmGrpList);
				formDataGroupList.add(tempFmlyPlanVisitinfoFrmDataGrpDto);
			}
		}

		//artf148785 : ALM ID : 14793 : The UE_GROUP for this section should not be changed
		// so reinitializing it to match previous value
		idCountPlanVisit = ServiceConstants.One;
		for (int visitPlanbalRowsAddng = idCountPlanVisit
				- ServiceConstants.One; visitPlanbalRowsAddng < ServiceConstants.FIVEININT; visitPlanbalRowsAddng++) {
			FormDataGroupDto tempFmlyPlanVisitinfoFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_PLAN_VISIT_CNTCT_INFO, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkFmlyPlanVisitinfoFrmGrpList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkFmlyHistPlanPrsnNm = createBookmark(BookmarkConstants.TXT_FMLY_HIST_VISIT_PARTCPNT,
					ServiceConstants.EMPTY_STRING);
			bookmarkFmlyPlanVisitinfoFrmGrpList.add(bookmarkFmlyHistPlanPrsnNm);
			BookmarkDto bookmarkFmlyHistPlanReltnNm = createBookmark(BookmarkConstants.TXT_FMLY_HIST_VISIT_RELTN,
					ServiceConstants.EMPTY_STRING);
			bookmarkFmlyPlanVisitinfoFrmGrpList.add(bookmarkFmlyHistPlanReltnNm);
			BookmarkDto bookmarkFmlyHistPlanLngthNm = createBookmark(BookmarkConstants.TXT_FMLY_HIST_VISIT_LNGTH,
					ServiceConstants.EMPTY_STRING);
			bookmarkFmlyPlanVisitinfoFrmGrpList.add(bookmarkFmlyHistPlanLngthNm);
			BookmarkDto bookmarkFmlyHistPlanFreqNm = createBookmark(BookmarkConstants.TXT_FMLY_HIST_VISIT_FREQ,
					ServiceConstants.EMPTY_STRING);
			bookmarkFmlyPlanVisitinfoFrmGrpList.add(bookmarkFmlyHistPlanFreqNm);
			BookmarkDto bookmarkFmlyHistPlanDaysNm = createBookmark(BookmarkConstants.TXT_FMLY_HIST_VISIT_DAYS,
					ServiceConstants.EMPTY_STRING);
			bookmarkFmlyPlanVisitinfoFrmGrpList.add(bookmarkFmlyHistPlanDaysNm);
			BookmarkDto bookmarkFmlyHistPlanLoctnNm = createBookmark(BookmarkConstants.TXT_FMLY_HIST_VISIT_LOCTN,
					ServiceConstants.EMPTY_STRING);
			bookmarkFmlyPlanVisitinfoFrmGrpList.add(bookmarkFmlyHistPlanLoctnNm);
			BookmarkDto bookmarkFmlyHistGroupId = createBookmark(BookmarkConstants.UE_GROUPID, visitPlanbalRowsAddng);
			bookmarkFmlyPlanVisitinfoFrmGrpList.add(bookmarkFmlyHistGroupId);
			tempFmlyPlanVisitinfoFrmDataGrpDto.setBookmarkDtoList(bookmarkFmlyPlanVisitinfoFrmGrpList);
			formDataGroupList.add(tempFmlyPlanVisitinfoFrmDataGrpDto);
		}
		// End -FAMILY HISTORY - Section 13
		// Start -PLACEMENT HISTORY - Section 14
		// Populate child or youth been previously adopted domestically
		if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto()) && ServiceConstants.ONE
				.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdChildAdoptdDmstc())) {
			BookmarkDto bookmarkAdoptDmstcYes = createBookmark(BookmarkConstants.CHK_PREV_ADO_DMSTC_YES,
					ServiceConstants.CHECKED);
			bookmarkNonFrmGrpList.add(bookmarkAdoptDmstcYes);
		} else if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto()) && ServiceConstants.TWO
				.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdChildAdoptdDmstc())) {
			BookmarkDto bookmarkAdoptDmstcNo = createBookmark(BookmarkConstants.CHK_PREV_ADO_DMSTC_NO,
					ServiceConstants.CHECKED);
			bookmarkNonFrmGrpList.add(bookmarkAdoptDmstcNo);
		} else if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto()) && ServiceConstants.UNKNOWN1
				.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdChildAdoptdDmstc())) {
			BookmarkDto bookmarkAdoptDmstcUnDtrmn = createBookmark(BookmarkConstants.CHK_PREV_ADO_DMSTC_UNDTRMN,
					ServiceConstants.CHECKED);
			bookmarkNonFrmGrpList.add(bookmarkAdoptDmstcUnDtrmn);
		}
		if (!ObjectUtils.isEmpty(commonApplicationDto.getCpAdoptionDto())
				&& commonApplicationDto.getCpAdoptionDto().size() > ServiceConstants.Zero) {
			String decodedValue = "";
			for (CPAdoptionDto cpAdoptionDto : commonApplicationDto.getCpAdoptionDto()) {
				if (CodesConstant.CPADOTYP_D.equalsIgnoreCase(cpAdoptionDto.getCdChildAdoptdType())) {
					/***ALM defect# 15099 - Unable to Create Common Application*
					 * Changed the code to avoid duplicate bookmark creation for 
					 * duplicate rows in Cp_Adoptn_Dtl table**/
					decodedValue += " " + (String) getDecodedValue(cpAdoptionDto.getCdChildAdoptdType(),CodesConstant.CCOUNTRY);
				}
			}
			BookmarkDto bookmarkWrAdoptDmstcCnsmted = createBookmark(BookmarkConstants.TXT_WR_ADOPTN_CNSMTED_DMSTC, decodedValue!=null?decodedValue.trim():null);
			bookmarkNonFrmGrpList.add(bookmarkWrAdoptDmstcCnsmted);
		}
		// Populate child or youth been previously adopted internationally
		if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto()) && ServiceConstants.ONE
				.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdChildAdoptdIntnl())) {
			BookmarkDto bookmarkAdoptIntrntnlYes = createBookmark(BookmarkConstants.CHK_PREV_ADO_INTRNTNL_YES,
					ServiceConstants.CHECKED);
			bookmarkNonFrmGrpList.add(bookmarkAdoptIntrntnlYes);
		} else if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto()) && ServiceConstants.TWO
				.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdChildAdoptdIntnl())) {
			BookmarkDto bookmarkAdoptIntrntnlNo = createBookmark(BookmarkConstants.CHK_PREV_ADO_INTRNTNL_NO,
					ServiceConstants.CHECKED);
			bookmarkNonFrmGrpList.add(bookmarkAdoptIntrntnlNo);
		} else if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto()) && ServiceConstants.UNKNOWN1
				.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdChildAdoptdIntnl())) {
			BookmarkDto bookmarkAdoptIntrntnlUnDtrmn = createBookmark(BookmarkConstants.CHK_PREV_ADO_INTRNTNL_UNDTRMN,
					ServiceConstants.CHECKED);
			bookmarkNonFrmGrpList.add(bookmarkAdoptIntrntnlUnDtrmn);
		}
		if (!ObjectUtils.isEmpty(commonApplicationDto.getCpAdoptionDto())
				&& commonApplicationDto.getCpAdoptionDto().size() > ServiceConstants.Zero) {
			String decodedValue = "";
			for (CPAdoptionDto cpAdoptionDto : commonApplicationDto.getCpAdoptionDto()) {
				if (CodesConstant.CPADOTYP_I.equalsIgnoreCase(cpAdoptionDto.getCdChildAdoptdType())) {
					/***ALM defect# 15099 - Unable to Create Common Application*
					 * Changed the code to avoid duplicate bookmark creation for 
					 * duplicate rows in Cp_Adoptn_Dtl table**/
					decodedValue += " " + (String) getDecodedValue(cpAdoptionDto.getCdCntryChildAdoptd(),CodesConstant.CCOUNTRY);
				}
			}
			BookmarkDto bookmarkWrAdoptIntrntnlCnsmted = createBookmark(
					BookmarkConstants.TXT_WR_ADOPTN_CNSMTED_INTRNTNL,  decodedValue!=null?decodedValue.trim():null);
			bookmarkNonFrmGrpList.add(bookmarkWrAdoptIntrntnlCnsmted);

		}
		// Populate the parents previously had legal custody of the child or
		// youth?
		if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto()) && ServiceConstants.ONE
				.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdChildLglGrdnship())) {
			BookmarkDto bookmarkPrevlglCstdyYes = createBookmark(BookmarkConstants.CHK_PREV_LGL_CSTDY_YES,
					ServiceConstants.CHECKED);
			bookmarkNonFrmGrpList.add(bookmarkPrevlglCstdyYes);
		} else if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto()) && ServiceConstants.TWO
				.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdChildLglGrdnship())) {
			BookmarkDto bookmarkPrevlglCstdyNo = createBookmark(BookmarkConstants.CHK_PREV_LGL_CSTDY_NO,
					ServiceConstants.CHECKED);
			bookmarkNonFrmGrpList.add(bookmarkPrevlglCstdyNo);
		} else if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto()) && ServiceConstants.UNKNOWN1
				.equalsIgnoreCase(commonApplicationDto.getEventChildPlanDto().getCdChildLglGrdnship())) {
			BookmarkDto bookmarkPrevlglCstdyUnDtrmn = createBookmark(BookmarkConstants.CHK_PREV_LGL_CSTDY_UNDTRMN,
					ServiceConstants.CHECKED);
			bookmarkNonFrmGrpList.add(bookmarkPrevlglCstdyUnDtrmn);
		}
		if (!ObjectUtils.isEmpty(commonApplicationDto.getCpLegalGrdnshpDto())) {
			BookmarkDto bookmarkDescrbPrevLglCstdy = createBookmark(BookmarkConstants.TXT_DESCRB_PREV_LGL_CSTDY,
					commonApplicationDto.getCpLegalGrdnshpDto().getNmPrvLglGrdnship());
			bookmarkNonFrmGrpList.add(bookmarkDescrbPrevLglCstdy);
		}
		int idCountPlcmntLog = ServiceConstants.One;
		if (!ObjectUtils.isEmpty(commonApplicationDto.getPlcmntLogDtlLst())
				&& commonApplicationDto.getPlcmntLogDtlLst().size() > ServiceConstants.Zero) {
			for (PlacementDtlDto placementDtlDto : commonApplicationDto.getPlcmntLogDtlLst()) {
				FormDataGroupDto tempPlcmntHistLogFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PLCMNT_HIST_LOG, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPlcmntHistLogFrmGrpList = new ArrayList<BookmarkDto>();				
				BookmarkDto bookmarkPlcmntNm = createBookmark(BookmarkConstants.TXT_PLCMNT_NAME,
						placementDtlDto.getNmPlcmtFacil());
				bookmarkPlcmntHistLogFrmGrpList.add(bookmarkPlcmntNm);
				BookmarkDto bookmarkPlcmntType = null;
				switch (placementDtlDto.getCdPlcmtType()) {
				case ServiceConstants.CPLMNTYP_010:
					bookmarkPlcmntType = createBookmarkWithCodesTable(BookmarkConstants.TXT_PLCMNT_TYPE,
							placementDtlDto.getCdPlcmtLivArr(), CodesConstant.CLANCP);
					break;
				case ServiceConstants.CPLMNTYP_020:
					bookmarkPlcmntType = createBookmarkWithCodesTable(BookmarkConstants.TXT_PLCMNT_TYPE,
							placementDtlDto.getCdPlcmtLivArr(), CodesConstant.CLAPRSFA);
					break;
				case ServiceConstants.CPLMNTYP_040:
					if (ServiceConstants.CFACTYP2_93.equalsIgnoreCase(placementDtlDto.getCdRsrcFacilType())) {
						bookmarkPlcmntType = createBookmarkWithCodesTable(BookmarkConstants.TXT_PLCMNT_TYPE,
								placementDtlDto.getCdPlcmtLivArr(), CodesConstant.CLASIL);					
					} else if (ServiceConstants.PCA_STG
							.equalsIgnoreCase(commonApplicationDto.getStagePersonLinkCaseDto().getCdStage())) {
						bookmarkPlcmntType = createBookmarkWithCodesTable(BookmarkConstants.TXT_PLCMNT_TYPE,
								placementDtlDto.getCdPlcmtLivArr(), CodesConstant.CLAPCA);						
					} else {
						bookmarkPlcmntType = createBookmarkWithCodesTable(BookmarkConstants.TXT_PLCMNT_TYPE,
								placementDtlDto.getCdPlcmtLivArr(), CodesConstant.CPLCMT);					
					}
					break;
				case ServiceConstants.CPLMNTYP_080:
					bookmarkPlcmntType = createBookmarkWithCodesTable(BookmarkConstants.TXT_PLCMNT_TYPE,
							placementDtlDto.getCdPlcmtLivArr(), CodesConstant.CLAUNA);
					break;
				case ServiceConstants.CPLMNTYP_090:
					bookmarkPlcmntType = createBookmarkWithCodesTable(BookmarkConstants.TXT_PLCMNT_TYPE,
							placementDtlDto.getCdPlcmtLivArr(), CodesConstant.CLAKNL);
					break;
				default:
					if (ServiceConstants.CPLMNTYP_100.equalsIgnoreCase(placementDtlDto.getCdPlcmtType())) {
						bookmarkPlcmntType = createBookmarkWithCodesTable(BookmarkConstants.TXT_PLCMNT_TYPE,
								placementDtlDto.getCdPlcmtLivArr(), CodesConstant.CLAPCA);
					} else if (ServiceConstants.CPLMNTYP_030.equalsIgnoreCase(placementDtlDto.getCdPlcmtType())) {
						if (!ServiceConstants.CFACTYP2_93.equalsIgnoreCase(placementDtlDto.getCdRsrcFacilType())) {
							bookmarkPlcmntType = createBookmarkWithCodesTable(BookmarkConstants.TXT_PLCMNT_TYPE,
									placementDtlDto.getCdPlcmtLivArr(), CodesConstant.CFACTYP2);
						} else if (ServiceConstants.CFACTYP2_93
								.equalsIgnoreCase(placementDtlDto.getCdRsrcFacilType())) {
							bookmarkPlcmntType = createBookmarkWithCodesTable(BookmarkConstants.TXT_PLCMNT_TYPE,
									placementDtlDto.getCdPlcmtLivArr(), CodesConstant.CLASIL);
						} else {
							bookmarkPlcmntType = createBookmarkWithCodesTable(BookmarkConstants.TXT_PLCMNT_TYPE,
									placementDtlDto.getCdPlcmtLivArr(), CodesConstant.CFACTYP2);
						}
					} else {
						bookmarkPlcmntType = createBookmarkWithCodesTable(BookmarkConstants.TXT_PLCMNT_TYPE,
								placementDtlDto.getCdPlcmtLivArr(), CodesConstant.CFACTYP2);
					}
				}				
				bookmarkPlcmntHistLogFrmGrpList.add(bookmarkPlcmntType);
				BookmarkDto bookmarkPlcmntStartDt = createBookmark(BookmarkConstants.TXT_PLCMNT_START_DT,
						TypeConvUtil.formDateFormat(placementDtlDto.getDtPlcmtStart()));
				bookmarkPlcmntHistLogFrmGrpList.add(bookmarkPlcmntStartDt);
				// Warranty Defect Fix - 10996 To prevent the Default Placement End Date to be printed from Form
				// in case the placement is not end-dated
				String placementEndDate=TypeConvUtil.formDateFormat(placementDtlDto.getDtPlcmtEnd());
				if(!ServiceConstants.STAGE_OPEN_DT.equals(placementEndDate))
				{
				BookmarkDto bookmarkPlcmntEndDt = createBookmark(BookmarkConstants.TXT_PLCMNT_END_DT,
						TypeConvUtil.formDateFormat(placementDtlDto.getDtPlcmtEnd()));
				bookmarkPlcmntHistLogFrmGrpList.add(bookmarkPlcmntEndDt);
				}
				idCountPlcmntLog = idCountPlcmntLog + ServiceConstants.One;
				tempPlcmntHistLogFrmDataGrpDto.setBookmarkDtoList(bookmarkPlcmntHistLogFrmGrpList);
				formDataGroupList.add(tempPlcmntHistLogFrmDataGrpDto);
			}
		}

		for (int plcmntLogbalRowsAddng = idCountPlcmntLog
				- ServiceConstants.One; plcmntLogbalRowsAddng < ServiceConstants.TEN; plcmntLogbalRowsAddng++) {
			FormDataGroupDto tempPlcmntHistLogFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_PLCMNT_HIST_LOG, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkPlcmntHistLogFrmGrpList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkPlcmntNm = createBookmark(BookmarkConstants.TXT_PLCMNT_NAME,
					ServiceConstants.FORM_SPACE);
			bookmarkPlcmntHistLogFrmGrpList.add(bookmarkPlcmntNm);
			BookmarkDto bookmarkPlcmntType = createBookmark(BookmarkConstants.TXT_PLCMNT_TYPE,
					ServiceConstants.FORM_SPACE);
			bookmarkPlcmntHistLogFrmGrpList.add(bookmarkPlcmntType);
			BookmarkDto bookmarkPlcmntStartDt = createBookmark(BookmarkConstants.TXT_PLCMNT_START_DT,
					ServiceConstants.FORM_SPACE);
			bookmarkPlcmntHistLogFrmGrpList.add(bookmarkPlcmntStartDt);
			BookmarkDto bookmarkPlcmntEndDt = createBookmark(BookmarkConstants.TXT_PLCMNT_END_DT,
					ServiceConstants.FORM_SPACE);
			bookmarkPlcmntHistLogFrmGrpList.add(bookmarkPlcmntEndDt);
			tempPlcmntHistLogFrmDataGrpDto.setBookmarkDtoList(bookmarkPlcmntHistLogFrmGrpList);
			formDataGroupList.add(tempPlcmntHistLogFrmDataGrpDto);

		}

		// End -PLACEMENT HISTORY - Section 14
		//TEMPORARY ABSENCE LIST

		if (!ObjectUtils.isEmpty(commonApplicationDto.getTempAbsenceList())
				&& commonApplicationDto.getTempAbsenceList().size() > ServiceConstants.Zero) {
			FormDataGroupDto taGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_TEMP_ABSENCE,FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> taList = new ArrayList<FormDataGroupDto>();
			for (TemporaryAbsenceDto tempAbsenceDto : commonApplicationDto.getTempAbsenceList()) {
					FormDataGroupDto tempTALogFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_TEMP_ABSENCE_LOG, FormGroupsConstants.TMPLAT_TEMP_ABSENCE);
					List<BookmarkDto> bookmarkTAFrmGrpList = new ArrayList<BookmarkDto>();

					BookmarkDto bookmarkTAType = null;
					switch (tempAbsenceDto.getTemporaryAbsenceType()) {
						case ServiceConstants.CCTATYPE_01:
							bookmarkTAType = createBookmarkWithCodesTable(BookmarkConstants.TXT_TEMP_ABSENCE_TYPE,
									tempAbsenceDto.getTemporaryAbsenceType(), CodesConstant.CCTATYPE);
							break;
						case ServiceConstants.CCTATYPE_02:
							bookmarkTAType = createBookmarkWithCodesTable(BookmarkConstants.TXT_TEMP_ABSENCE_TYPE,
									tempAbsenceDto.getTemporaryAbsenceType(), CodesConstant.CCTATYPE);
							break;
						case ServiceConstants.CCTATYPE_03:
							bookmarkTAType = createBookmarkWithCodesTable(BookmarkConstants.TXT_TEMP_ABSENCE_TYPE,
									tempAbsenceDto.getTemporaryAbsenceType(), CodesConstant.CCTATYPE);
							break;
						case ServiceConstants.CCTATYPE_04:
							bookmarkTAType = createBookmarkWithCodesTable(BookmarkConstants.TXT_TEMP_ABSENCE_TYPE,
									tempAbsenceDto.getTemporaryAbsenceType(), CodesConstant.CCTATYPE);
							break;
						case ServiceConstants.CCTATYPE_05:
							bookmarkTAType = createBookmarkWithCodesTable(BookmarkConstants.TXT_TEMP_ABSENCE_TYPE,
									tempAbsenceDto.getTemporaryAbsenceType(), CodesConstant.CCTATYPE);
							break;
						case ServiceConstants.CCTATYPE_06:
							bookmarkTAType = createBookmarkWithCodesTable(BookmarkConstants.TXT_TEMP_ABSENCE_TYPE,
									tempAbsenceDto.getTemporaryAbsenceType(), CodesConstant.CCTATYPE);
							break;
						case ServiceConstants.CCTATYPE_07:
							bookmarkTAType = createBookmarkWithCodesTable(BookmarkConstants.TXT_TEMP_ABSENCE_TYPE,
									tempAbsenceDto.getTemporaryAbsenceType(), CodesConstant.CCTATYPE);
							break;

					}
					bookmarkTAFrmGrpList.add(bookmarkTAType);
					BookmarkDto bookmarkLnkdPlcmnt = createBookmark(BookmarkConstants.TXT_LNKD_PLCMT,
							tempAbsenceDto.getLinkedPlacementDesc());
					bookmarkTAFrmGrpList.add(bookmarkLnkdPlcmnt);
					BookmarkDto bookmarkTAStartDt = createBookmark(BookmarkConstants.TXT_TA_START_DT,
							TypeConvUtil.formDateFormat(tempAbsenceDto.getDtTemporaryAbsenceStart()));
					bookmarkTAFrmGrpList.add(bookmarkTAStartDt);
					BookmarkDto bookmarkTAEndDt = createBookmark(BookmarkConstants.TXT_TA_END_DT,
							TypeConvUtil.formDateFormat(tempAbsenceDto.getDtTemporaryAbsenceEnd()));
					bookmarkTAFrmGrpList.add(bookmarkTAEndDt);

					tempTALogFrmDataGrpDto.setBookmarkDtoList(bookmarkTAFrmGrpList);
					taList.add(tempTALogFrmDataGrpDto);
				}
			taGrpDto.setFormDataGroupList(taList);
			formDataGroupList.add(taGrpDto);
		}

		//END TEMPORARY ABSENCE LIST
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		preFillData.setFormDataGroupList(formDataGroupList);
		return preFillData;
	}
}
