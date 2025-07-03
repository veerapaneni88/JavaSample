/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Utility Class for SSCC Placement Option and Circum
 *Services.
 *Aug 23, 2018- 10:40:24 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.sscc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.service.casepackage.dto.SSCCTimelineDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.FormattingUtils;
import us.tx.state.dfps.service.errorWarning.ErrorListDto;
import us.tx.state.dfps.service.errorWarning.ErrorListGroupDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dto.MedicaidUpdateDto;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;
import us.tx.state.dfps.service.placement.dto.PlacementAUDDto;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.sscc.dao.SSCCPlcmtOptCircumDao;
import us.tx.state.dfps.service.sscc.dto.MedCnsntrWarngDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtMedCnsntrDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtOptCircumDto;
import us.tx.state.dfps.service.sscc.service.SSCCPlcmtOptCircumService;
import us.tx.state.dfps.service.subcare.dto.ExceptionalCareDto;
import us.tx.state.dfps.service.subcare.dto.ResourceAddressDto;
import us.tx.state.dfps.service.subcare.dto.ResourcePhoneDto;

@Service
@Transactional
public class SSCCPlcmtOptCircumUtil {

	private static final Logger log = Logger.getLogger(SSCCPlcmtOptCircumUtil.class);

	@Autowired
	LookupDao lookupDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	SSCCPlcmtOptCircumDao ssccPlcmtOptCircumDao;

	@Autowired
	SSCCPlcmtOptCircumService ssccPlcmtOptCircumService;

	@Autowired
	FormattingUtils formattingUtil;

	// Local Constants For the Service.
	public static final List<String> CPB_PARENT_LIST = Arrays.asList(CodesConstant.CRPTRINT_PA,
			CodesConstant.CRPTRINT_PB, CodesConstant.CRPTRINT_PD, CodesConstant.CRPTRINT_ST, CodesConstant.CRPTRINT_AP,
			CodesConstant.CRPTRINT_PG);
	public static final List<String> CPA_FOSTER_OR_PRE_CONSUM_PRNTS = Arrays.asList(CodesConstant.CFACTYP2_31,
			CodesConstant.CFACTYP2_32, CodesConstant.CFACTYP2_33, CodesConstant.CFACTYP2_34, CodesConstant.CFACTYP2_35,
			CodesConstant.CFACTYP2_36, CodesConstant.CFACTYP2_37, CodesConstant.CFACTYP2_38, CodesConstant.CFACTYP2_40,
			CodesConstant.CFACTYP2_41, CodesConstant.CFACTYP2_51, CodesConstant.CFACTYP2_52, CodesConstant.CFACTYP2_53,
			CodesConstant.CFACTYP2_54, CodesConstant.CFACTYP2_55, CodesConstant.CFACTYP2_56, CodesConstant.CFACTYP2_57,
			CodesConstant.CFACTYP2_58, CodesConstant.CFACTYP2_59, CodesConstant.CFACTYP2_71, CodesConstant.CFACTYP2_72,
			CodesConstant.CFACTYP2_73, CodesConstant.CFACTYP2_74, CodesConstant.CFACTYP2_75);
	public static final List<String> GRO_CHILD_CARE = Arrays.asList(CodesConstant.CLARES_68);
	public static final List<String> GRO_TRT_SRVCS_INTL_DIS = Arrays.asList(CodesConstant.CLARES_82,
			CodesConstant.CLARES_83);
	public static final List<String> GRO_TRT_SRVCS_EMOT_DIS = Arrays.asList(CodesConstant.CLARES_64,
			CodesConstant.CLARES_65, CodesConstant.CLARES_81);
	public static final List<String> GRO_EMER_CARE_SVCS = Arrays.asList(CodesConstant.CLARES_67);

	public static final String DT_PLCMT_CIRC_START = "DT_PLCMT_CIRC_START";
	public static final String DT_PLCMT_CIRC_EXPIRE = "DT_PLCMT_CIRC_EXPIRE";
	public static final String CD_PLCMT_OPTION_STATUS = "CD_PLCMT_OPTION_STATUS";
	public static final String CD_PLCMT_CIRC_STATUS = "CD_PLCMT_CIRC_STATUS";
	public static final String CD_PLCMT_OPTION_TYPE = "CD_PLCMT_OPTION_TYPE";
	public static final String DT_PLCMT_OPTION_RECORDED = "DT_PLCMT_OPTION_RECORDED";
	public static final String ID_PLCMT_RSRC = "ID_PLCMT_RSRC";
	public static final String ID_PLCMT_EVENT = "ID_PLCMT_EVENT";
	public static final String IND_EFC = "IND_EFC";
	public static final String IND_PLCMT_SSCC = "IND_PLCMT_SSCC";
	public static final String OPTION_TYPE = "Option Type";
	public static final String IND_PRIOR_COMM = "Prior Communication Date/Time:";
	public static final String DT_REC_SSCC = "Date/Time Option(SSCC)";
	public static final String DT_REC_DFPS = "Date/Time Option(DFPS)";
	private static final String EXISTING = "Existing";
	private static final String PROPOSED = "Proposed";
	public static final String EDIT_SECTION_OPT_NAME = "editPlcmtName";
	public static final String EDIT_SECTION_OPT_MED = "editMedCnsntr";
	public static final String EDIT_SECTION_PLCMT_INFO = "editPlcmtInfo";
	public static final String EDIT_SECTION_CIRCUM = "editPlcmtCircum";

	public final static int UNKNOWN_STATE = 0;
	public final static int NEW_STATE = 1;
	public final static int PROPOSE_SSCC_STATE = 2;
	public final static int PROPOSE_DFPS_STATE = 3;
	public final static int RESCIND_STATE = 4;
	public final static int PROPOSE_SSCC_EDIT_STATE = 5;
	public final static int ACK_DFPS_STATE = 6;
	public final static int PROPOSE_DFPS_EDIT_STATE = 7;
	public final static int EVAL_STATE = 8;
	public final static int APPROVE_STATE = 9;
	public final static int APPROVE_WITH_MOD_STATE = 10;
	public final static int REJECT_STATE = 11;
	public final static int APPROVE_SSCC_STATE = 12;
	public final static int APPROVE_DFPS_STATE = 13;
	public final static int PLACED_SSCC_STATE = 14;
	public final static int PLACED_DFPS_STATE = 15;
	public final static int VERSION_CHECK_STATE = 16;
	public final static int APPROVE_WITH_SAVE_STATE = 17;
	// new states for FCR Phase III start
	public final static int SAVED_CONTENT_SSCC_STATE = 18;
	public final static int SAVED_CONTENT_DFPS_STATE = 19;
	public final static int SAVED_CONTENT_FIXER_STATE = 20;
	public final static int APPROVE_WITHOUT_SAVE_STATE = 21;
	public final static int PLACED_FIXER_STATE = 22;
	public final static int PLACED_DFPSFIXER_STATE = 23;
	public final static int SAVED_CONTENT_DFPSFIXER_STATE = 24;
	public final static int VALIDATE_WITH_ERRORS = 25;
	public final static int VALIDATE_WITHOUT_ERRORS = 26;
	public final static int REVALIDATE_WITH_ERRORS = 27;
	public final static int REVALIDATE_WITHOUT_ERRORS = 28;
	public final static int REJECT_DFPSFIXER_STATE = 29;
	public final static int APPROVE_WITHOUT_SAVE_DFPSFIXER_STATE = 30;
	public final static int APPROVE_WITH_SAVE_DFPSFIXER_STATE = 31;
	public final static int REJECT_FIXER_STATE = 32;
	public final static int APPROVE_WITHOUT_SAVE_FIXER_STATE = 33;
	public final static int APPROVE_WITH_SAVE_FIXER_STATE = 34;

	/**
	 * Method Name: readAddressPhone Method Description: Method check if the
	 * resource address and phone should be read from Orginal Table or Entity
	 * Table.
	 * 
	 * @param ssccPlcmtHeader
	 * @return returnval
	 */
	public static boolean readAddressPhone(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("Method readAddressPhone of SSCCPlcmtOptCircumUtil : Execution Started");
		boolean readOrginal = true;
		if (ssccPlcmtOptCircumDto.getIsOption()) {
			readOrginal = !(CodesConstant.CSSCCSTA_90
					.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdStatus())
					|| CodesConstant.CSSCCSTA_120.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdStatus()));
		} else {
			readOrginal = !(CodesConstant.CSSCCSTA_90
					.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdStatus())
					|| CodesConstant.CSSCCSTA_70.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdStatus())
					|| CodesConstant.CSSCCSTA_100.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdStatus()));
		}
		log.info("Method readAddressPhone of SSCCPlcmtOptCircumUtil : Return Response " + readOrginal);
		return readOrginal;
	}

	/**
	 * Method Name: checkGroResCodes Method Description: The method checks the
	 * input code exists in GRO Residence Living Arrangement Code List
	 * 
	 * @param checkCode
	 * @return boolean
	 */
	public static boolean checkGroResCodes(String checkCode) {
		log.info("Method checkGroResCodes of SSCCPlcmtOptCircumUtil : Execution Started");
		List<String> allowedOptions = new ArrayList<>();
		allowedOptions.addAll(GRO_EMER_CARE_SVCS);
		allowedOptions.addAll(GRO_CHILD_CARE);
		allowedOptions.addAll(GRO_TRT_SRVCS_INTL_DIS);
		allowedOptions.addAll(GRO_TRT_SRVCS_EMOT_DIS);
		log.info("Method checkGroResCodes of SSCCPlcmtOptCircumUtil : Returning Response");
		return allowedOptions.contains(checkCode);
	}

	/**
	 * Method Name: getCpaAgncyHmsCodes Method Description: Method returns CPA
	 * Agency Homes Living Arr Codes.
	 * 
	 * @return List<String>
	 */
	public List<String> getCpaAgncyHmsCodes() {
		List<String> excludeOptions = lookupDao.getCategoryListingDecode(CodesConstant.CFACTYP2);
		excludeOptions.removeAll(CPA_FOSTER_OR_PRE_CONSUM_PRNTS);
		return excludeOptions;
	}

	/**
	 * Method Name: getDefaultCodes Method Description: get Default Living
	 * Arrangement codes for Placement Options.
	 * 
	 * @return
	 */
	public List<String> getDefaultCodes() {
		List<String> excludeOptions = lookupDao.getCategoryListingDecode(CodesConstant.CFACTYP2);
		List<String> allowedOptions = new ArrayList<>();

		allowedOptions.addAll(GRO_EMER_CARE_SVCS);
		allowedOptions.addAll(GRO_CHILD_CARE);
		allowedOptions.addAll(GRO_TRT_SRVCS_INTL_DIS);
		allowedOptions.addAll(GRO_TRT_SRVCS_EMOT_DIS);
		allowedOptions.addAll(CPA_FOSTER_OR_PRE_CONSUM_PRNTS);
		excludeOptions.removeAll(allowedOptions);
		return excludeOptions;
	}

	/**
	 * Method Name: findResourcePrimaryAddress Method Description: Method finds
	 * the primary address for the given resource.
	 * 
	 * @param resourceDto
	 * @return ResourceAddressDto
	 */
	public static ResourceAddressDto findResourcePrimaryAddress(ResourceDto resourceDto) {
		ResourceAddressDto primaryResourceAddr = null;
		if (!ObjectUtils.isEmpty(resourceDto.getResourceAddressLists())) {
			primaryResourceAddr = resourceDto.getResourceAddressLists().stream()
					.filter(addr -> CodesConstant.CRSCADDR_01.equals(addr.getCdRsrcAddrType())).findAny().orElse(null);
		}
		return primaryResourceAddr;
	}

	/**
	 * Method Name: findResourcePrimaryPhone Method Description: Method finds
	 * the primary Phone for the give resource.
	 * 
	 * @param resourceDto
	 * @return ResourcePhoneDto
	 */
	public static ResourcePhoneDto findResourcePrimaryPhone(ResourceDto resourceDto) {
		ResourcePhoneDto primaryResourcePhone = null;
		if (!ObjectUtils.isEmpty(resourceDto.getResourcePhoneLists())) {
			primaryResourcePhone = resourceDto.getResourcePhoneLists().stream()
					.filter(phone -> CodesConstant.CRSCPHON_01.equals(phone.getCdRsrcPhoneType())).findAny()
					.orElse(null);
		}
		return primaryResourcePhone;
	}

	/**
	 * Method Name: checkIfFixer Method Description: method sets the role if the
	 * user accessing the Placement Option circum page is fixer.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 */
	public void checkIfFixer(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		if (ServiceConstants.ROLEDFPS.equals(ssccPlcmtOptCircumDto.getRole())
				&& (!StringUtils.isEmpty(ssccPlcmtOptCircumDto.getIdActiveRef())
						&& !ServiceConstants.ZERO_VAL.equals(ssccPlcmtOptCircumDto.getIdActiveRef()))) {
			if (ssccPlcmtOptCircumDto.getIsFixer()) {
				// if Fixer has Stage Access then user is given both DFPS and
				// Fixer Role
				if (stageDao.hasStageAccess(ssccPlcmtOptCircumDto.getIdStage(), ssccPlcmtOptCircumDto.getIdUser())) {
					ssccPlcmtOptCircumDto.setRole(ServiceConstants.ROLEDFPSFIXER);
				} else {
					ssccPlcmtOptCircumDto.setRole(ServiceConstants.ROLEFIXER);
				}
				// Make the page Editable only for Fixer.
				ssccPlcmtOptCircumDto.setPageMode(ServiceConstants.PAGE_MODE_MODIFY);
			}
		}
	}

	/**
	 * Method Name: setInformationMsg Method Description: This method is used to
	 * set all the Information Attention message.
	 * 
	 * @param errorCode
	 * @param ssccPlcmtOptCircumDto
	 */
	public void setInformationMsg(int errorCode, SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		List<String> infoMsgLst = ssccPlcmtOptCircumDto.getInfoMsgList();
		if (ObjectUtils.isEmpty(infoMsgLst)) {
			infoMsgLst = new ArrayList<>();
		}
		String infoMsg = lookupDao.getMessage(errorCode);
		if (!infoMsgLst.contains(infoMsg)) {
			infoMsgLst.add(lookupDao.getMessage(errorCode));
		}
		ssccPlcmtOptCircumDto.setInfoMsgList(infoMsgLst);
	}

	/**
	 * Method Name: setErrorMessage Method Description: This method is used to
	 * set all the error messages.
	 * 
	 * @param errorCode
	 * @param ssccPlcmtOptCircumDto
	 */
	public void setErrorMessage(int errorCode, SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		List<String> errorMsgList = ssccPlcmtOptCircumDto.getErrorMsgList();
		if (ObjectUtils.isEmpty(errorMsgList)) {
			errorMsgList = new ArrayList<>();
		}
		String errMsg = lookupDao.getMessage(errorCode);
		if (!errorMsgList.contains(errMsg)) {
			errorMsgList.add(lookupDao.getMessage(errorCode));
		}
		ssccPlcmtOptCircumDto.setErrorMsgList(errorMsgList);
	}

	/**
	 * Method Name: populateSSCCTimelineValue Method Description:
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @param txtDescription
	 * @return
	 * @throws Exception
	 */
	public SSCCTimelineDto populateSSCCTimelineValue(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto,
			String txtDescription) {
		SSCCTimelineDto ssccTimelineDto = new SSCCTimelineDto();
		ssccTimelineDto.setDtLastUpdate(new Date());
		ssccTimelineDto.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
		ssccTimelineDto.setDtCreated(new Date());
		ssccTimelineDto.setIdCreatedPerson(ssccPlcmtOptCircumDto.getIdUser());
		ssccTimelineDto.setCdTimelineTableName(CodesConstant.CSSCCTBL_20);
		ssccTimelineDto.setTxtTimelineDesc(txtDescription);
		ssccTimelineDto.setDtRecorded(new Date());
		ssccTimelineDto.setIdSsccReferral(ssccPlcmtOptCircumDto.getIdActiveRef());
		if (ssccPlcmtOptCircumDto.getRole().equals(ServiceConstants.ROLESSCC)) {
			ssccTimelineDto.setIdRsrcSscc(ssccPlcmtOptCircumDto.getSsccResourceDto().getIdSSCCResource());
		}
		ssccTimelineDto.setIdReference(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader());

		return ssccTimelineDto;
	}

	public static boolean isNonZeroLong(Long val) {
		return !StringUtils.isEmpty(val) && !ServiceConstants.ZERO_VAL.equals(val);
	}

	/**
	 * Method Name: setErrorWarningGroup Method Description: This method
	 * validates the SSCCPlcmtMedCnsntr Section when Validate button is clicked
	 * before save. and sets the List of Errors and Warning to be shown to the
	 * user.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	public SSCCPlcmtOptCircumDto setErrorWarningGroup(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		List<ErrorListGroupDto> errMsgGoupList = new ArrayList<>();
		List<ErrorListGroupDto> warnMsgGroupList = new ArrayList<>();
		ssccPlcmtOptCircumDto.setValidateFlag(Boolean.TRUE);
		ErrorListGroupDto placementErrorGrp = getPlacementErrorList(ssccPlcmtOptCircumDto);
		if (!ObjectUtils.isEmpty(placementErrorGrp) && !ObjectUtils.isEmpty(placementErrorGrp.getErrorMessageList())
				&& placementErrorGrp.getErrorMessageList().size() > ServiceConstants.ZERO_SHORT) {
			errMsgGoupList.add(placementErrorGrp);
		}
		ErrorListGroupDto medCnsntrErrGrp = getMedCnsntrErrorList(ssccPlcmtOptCircumDto);
		if (!ObjectUtils.isEmpty(medCnsntrErrGrp) && !ObjectUtils.isEmpty(medCnsntrErrGrp.getErrorMessageList())
				&& medCnsntrErrGrp.getErrorMessageList().size() > ServiceConstants.ZERO_SHORT) {
			errMsgGoupList.add(medCnsntrErrGrp);
		}
		ErrorListGroupDto placementWrnGrp = getPlacmentWarningList(ssccPlcmtOptCircumDto);
		if (!ObjectUtils.isEmpty(placementWrnGrp) && !ObjectUtils.isEmpty(placementWrnGrp.getErrorMessageList())
				&& placementWrnGrp.getErrorMessageList().size() > ServiceConstants.ZERO_SHORT) {
			warnMsgGroupList.add(placementWrnGrp);
		}
		ErrorListGroupDto medCnsntrWrnGrp = getMedCnsntrWarningList(ssccPlcmtOptCircumDto);
		if (!ObjectUtils.isEmpty(medCnsntrWrnGrp) && !ObjectUtils.isEmpty(medCnsntrWrnGrp.getErrorMessageList())
				&& medCnsntrWrnGrp.getErrorMessageList().size() > ServiceConstants.ZERO_SHORT) {
			warnMsgGroupList.add(medCnsntrWrnGrp);
		}
		if (!ObjectUtils.isEmpty(errMsgGoupList)) {
			ssccPlcmtOptCircumDto.setErrorMsgGrp(errMsgGoupList);
		} else {
			ssccPlcmtOptCircumDto.setValidateFlag(Boolean.FALSE);
		}
		if (!ObjectUtils.isEmpty(warnMsgGroupList)) {
			ssccPlcmtOptCircumDto.setWarnMsgGrp(warnMsgGroupList);
		}
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: getPlacementErrorList Method Description: Returns List of
	 * Errors for Placement when Validate Button is clicked before saving
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return ErrorListGroupDto
	 */
	public ErrorListGroupDto getPlacementErrorList(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("Method getPlacementErrorList of SSCCPlcmtOptCircumUtil : Execution Started");
		List<ErrorListDto> placementErrList = new ArrayList<>();
		Date maxPlcmtDtEnd = ssccPlcmtOptCircumDao.getMaxDtPlcmtEnd(ssccPlcmtOptCircumDto.getIdActiveRef());

		if (!StringUtils.isEmpty(maxPlcmtDtEnd)
				&& maxPlcmtDtEnd.after(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart())) {
			placementErrList
					.add(new ErrorListDto(lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_OVERLAP)));
		}
		if (isNonZeroLong(ssccPlcmtOptCircumDao.getActivePlcmtCnt(ssccPlcmtOptCircumDto.getIdActiveRef()))) {
			placementErrList
					.add(new ErrorListDto(lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_NOT_ENDED)));
		}
		if (ssccPlcmtOptCircumDao.excpCareBudgetDaysExceeded(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto(), true)
				&& ServiceConstants.Y.equals(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getIndExceptCare())) {
			placementErrList.add(
					new ErrorListDto(lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_EXP_CARE_LIMIT)));
		}
		if (isNonZeroLong(ssccPlcmtOptCircumDao.getSSCCPlcmtInPlcmtCnt(ssccPlcmtOptCircumDto))) {
			placementErrList.add(new ErrorListDto(lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_EXST)));
		}
		log.info("Method getPlacementErrorList of SSCCPlcmtOptCircumUtil : Returning Error list Size :"
				+ placementErrList.size());
		ErrorListGroupDto plcmtErrorGrp = new ErrorListGroupDto("Placement", placementErrList);
		return plcmtErrorGrp;
	}

	/**
	 * Method Name: getMedCnsntrErrorList Method Description: Returns List of
	 * Errors for Med consenters when Validate Button is clicked before saving
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	public ErrorListGroupDto getMedCnsntrErrorList(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("Method getMedCnsntrErrorList of SSCCPlcmtOptCircumUtil : Execution Started");
		List<ErrorListDto> mcConsenterErrList = new ArrayList<>();
		ErrorListGroupDto medCnsntrErrorGrp = new ErrorListGroupDto();
		HashMap<String, MedicalConsenterDto> impactMedConsenters = ssccPlcmtOptCircumDao
				.getActiveMedCnsntr(ssccPlcmtOptCircumDto.getIdActiveRef());
		HashMap<String, SSCCPlcmtMedCnsntrDto> ssccMedCnsntr = ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap();

		HashMap<String, Long> impactMedConPairs = new HashMap<>();
		HashMap<String, Long> ssccMedConPairs = new HashMap<>();
		HashSet<Integer> errorMgsProcessed = new HashSet<>();

		if (!ObjectUtils.isEmpty(impactMedConsenters)) {
			impactMedConsenters.entrySet().stream().forEach(entry -> {
				impactMedConPairs.put(entry.getKey(), entry.getValue().getIdMedConsenterPerson());
			});

			ssccMedCnsntr.entrySet().stream().forEach(entry -> {
				if(!ServiceConstants.Y.equals(entry.getValue().getIndBypass())){
					ssccMedConPairs.put(entry.getKey(), entry.getValue().getIdMedConsenterPerson());
				}
			});
			impactMedConPairs.entrySet().stream().forEach(entry -> {
				Long ssccMedvalue = ssccMedConPairs.get(entry.getKey());
				if (!errorMgsProcessed.contains(ServiceConstants.MSG_SSCC_PLCMT_MED_CNSNTR_MULT)
						&& !ssccMedConPairs.containsKey(entry.getKey())
						&& ssccMedConPairs.containsValue(entry.getValue())) {
					mcConsenterErrList.add(new ErrorListDto(
							lookupDao.getMessage(ServiceConstants.MSG_SSCC_PLCMT_MED_CNSNTR_MULT.intValue())));
					errorMgsProcessed.add(ServiceConstants.MSG_SSCC_PLCMT_MED_CNSNTR_MULT);
				}
				if (!errorMgsProcessed.contains(ServiceConstants.MSG_SSCC_PLCMT_MED_CNSNTR_TYPE_DUP)
						&& (!StringUtils.isEmpty(ssccMedvalue) && ssccMedvalue.equals(entry.getValue()))) {
					mcConsenterErrList.add(new ErrorListDto(
							lookupDao.getMessage(ServiceConstants.MSG_SSCC_PLCMT_MED_CNSNTR_TYPE_DUP.intValue())));
					errorMgsProcessed.add(ServiceConstants.MSG_SSCC_PLCMT_MED_CNSNTR_TYPE_DUP);
				}
				if (!errorMgsProcessed.contains(ServiceConstants.MSG_SSCC_PLCMT_MED_CNSNTR_COURT_ORD)
						&& !StringUtils.isEmpty(ssccMedvalue)) {
					MedicalConsenterDto impactMedCnsntr = impactMedConsenters.get(entry.getKey());
					if (!ObjectUtils.isEmpty(impactMedCnsntr) && !StringUtils.isEmpty(impactMedCnsntr.getCdCourtAuth())
							&& CodesConstant.CCRTAUTH_OIND.equals(impactMedCnsntr.getCdCourtAuth())) {
						mcConsenterErrList.add(new ErrorListDto(
								lookupDao.getMessage(ServiceConstants.MSG_SSCC_PLCMT_MED_CNSNTR_COURT_ORD.intValue())));
						errorMgsProcessed.add(ServiceConstants.MSG_SSCC_PLCMT_MED_CNSNTR_COURT_ORD);
					}
				}					
				if (!errorMgsProcessed.contains(ServiceConstants.MSG_SSCC_PLCMT_MED_CNSNTR_REPLACE)
						&& !StringUtils.isEmpty(ssccMedvalue)) {
					MedicalConsenterDto impactMedCnsntr = impactMedConsenters.get(entry.getKey());
					if (!ObjectUtils.isEmpty(impactMedCnsntr) && !StringUtils.isEmpty(impactMedCnsntr.getCdCourtAuth())
							&& (CodesConstant.CCRTAUTH_YALL.equals(impactMedCnsntr.getCdCourtAuth())
									|| CodesConstant.CCRTAUTH_YSME.equals(impactMedCnsntr.getCdCourtAuth()))) {
						mcConsenterErrList.add(new ErrorListDto(
								lookupDao.getMessage(ServiceConstants.MSG_SSCC_PLCMT_MED_CNSNTR_REPLACE.intValue())));
						errorMgsProcessed.add(ServiceConstants.MSG_SSCC_PLCMT_MED_CNSNTR_REPLACE);
					}

				}
			});
		}
		log.info("Method getMedCnsntrErrorList of SSCCPlcmtOptCircumUtil : Returning Error List size "
				+ mcConsenterErrList.size());
		medCnsntrErrorGrp = new ErrorListGroupDto("Medical Consenter", mcConsenterErrList);
		return medCnsntrErrorGrp;
	}

	/**
	 * Method Name: getPlacmentWarningList Method Description: Returns List of
	 * Warnings for Placement when Validate Button is clicked before saving
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	public ErrorListGroupDto getPlacmentWarningList(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("Method getPlacmentWarningList of SSCCPlcmtOptCircumUtil : Execution Started");
		List<ErrorListDto> plcmtWrnGrp = new ArrayList<>();
		Date maxPlcmtDtEnd = ssccPlcmtOptCircumDao.getMaxDtPlcmtEnd(ssccPlcmtOptCircumDto.getIdActiveRef());

		if (!StringUtils.isEmpty(maxPlcmtDtEnd)
				&& !maxPlcmtDtEnd.after(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart())
				&& !maxPlcmtDtEnd.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart())) {
			plcmtWrnGrp.add(new ErrorListDto(lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_GAP)));
		}

		log.info("Method getPlacmentWarningList of SSCCPlcmtOptCircumUtil : Returning Error list Size :"
				+ plcmtWrnGrp.size());
		ErrorListGroupDto plcmtWrnGrpDto = new ErrorListGroupDto("Placement", plcmtWrnGrp);
		return plcmtWrnGrpDto;
	}

	/**
	 * Method Name: getMedCnsntrWarningList Method Description: Returns List of
	 * Warnings in Table format for Med Consenters when Validate Button is
	 * clicked before saving
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	public ErrorListGroupDto getMedCnsntrWarningList(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("Method getMedCnsntrWarningList of SSCCPlcmtOptCircumUtil : Execution Started");
		List<ErrorListDto> medCnsntrWrngLst = new ArrayList<>();
		HashMap<String, MedicalConsenterDto> impactMedConsenters = ssccPlcmtOptCircumDao
				.getActiveMedCnsntr(ssccPlcmtOptCircumDto.getIdActiveRef());
		SSCCPlcmtMedCnsntrDto ssccMedCnsntr = null;
		MedicalConsenterDto impactMedCnsntr = null;
		final String indCourtAuth = (!ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEnableCourtAuth()
				|| ServiceConstants.Y.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIndCourtAuth()))
						? ServiceConstants.Y : ServiceConstants.N;
		// Populate the MedCnsntrWarning Dto List.
		List<MedCnsntrWarngDto> medCnstrWrnDtoLst = new ArrayList<>();
		// Set Primary
		impactMedCnsntr = impactMedConsenters.get(CodesConstant.CMCTYPE_FPRI);
		ssccMedCnsntr = ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().get(CodesConstant.CMCTYPE_FPRI);
		formMedCnsntrWrnList(medCnstrWrnDtoLst, impactMedCnsntr, ssccMedCnsntr, indCourtAuth,
				CodesConstant.CMCTYPE_FPRI, ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart());
		// Set Secondary
		impactMedCnsntr = impactMedConsenters.get(CodesConstant.CMCTYPE_SPRI);
		ssccMedCnsntr = ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().get(CodesConstant.CMCTYPE_SPRI);
		formMedCnsntrWrnList(medCnstrWrnDtoLst, impactMedCnsntr, ssccMedCnsntr, indCourtAuth,
				CodesConstant.CMCTYPE_SPRI, ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart());
		// Set Backup
		impactMedCnsntr = impactMedConsenters.get(CodesConstant.CMCTYPE_FBUP);
		ssccMedCnsntr = ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().get(CodesConstant.CMCTYPE_FBUP);
		formMedCnsntrWrnList(medCnstrWrnDtoLst, impactMedCnsntr, ssccMedCnsntr, indCourtAuth,
				CodesConstant.CMCTYPE_FBUP, ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart());
		// Set Second Backup
		impactMedCnsntr = impactMedConsenters.get(CodesConstant.CMCTYPE_SBUP);
		ssccMedCnsntr = ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().get(CodesConstant.CMCTYPE_SBUP);
		formMedCnsntrWrnList(medCnstrWrnDtoLst, impactMedCnsntr, ssccMedCnsntr, indCourtAuth,
				CodesConstant.CMCTYPE_SBUP, ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart());
		// Set the Error Message List Dto.
		medCnsntrWrngLst.add(
				new ErrorListDto(lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_MED_CNSNTR_UPDATES)));
		ssccPlcmtOptCircumDto.setMedCnsntrLst(medCnstrWrnDtoLst);
		log.info("Method getMedCnsntrWarningList of SSCCPlcmtOptCircumUtil : Returning Error List size "
				+ medCnsntrWrngLst.size());
		ErrorListGroupDto medCnsntrErrorGrp = new ErrorListGroupDto("Medical Consenter", medCnsntrWrngLst);
		return medCnsntrErrorGrp;
	}

	/**
	 * Method Name: formMedCnsntrWrnList Method Description: Method creates the
	 * List of Existing Med Consenter and Proposed Med Consenters.
	 * 
	 * @param medCnstrWrnDtoLst
	 * @param impactMedCnsntr
	 * @param ssccMedCnsntr
	 * @param indCourtAuth
	 * @param cnsntrType
	 */
	private void formMedCnsntrWrnList(List<MedCnsntrWarngDto> medCnstrWrnDtoLst, MedicalConsenterDto impactMedCnsntr,
			SSCCPlcmtMedCnsntrDto ssccMedCnsntr, String indCourtAuth, String cnsntrType, Date dtPlcmtStart) {
		// Create the Existing and Propsed row with the details.
		MedCnsntrWarngDto existMedCnsntrWrnDto = new MedCnsntrWarngDto(EXISTING,
				lookupDao.decode(CodesConstant.CMCTYPE, cnsntrType));
		MedCnsntrWarngDto propsedMedCnsntrWrnDto = new MedCnsntrWarngDto(PROPOSED,
				lookupDao.decode(CodesConstant.CMCTYPE, cnsntrType));
		if (!ObjectUtils.isEmpty(impactMedCnsntr)) {
			existMedCnsntrWrnDto.setName(isNonZeroLong(impactMedCnsntr.getIdMedConsenterPerson())
					? formattingUtil.formatName(impactMedCnsntr.getIdMedConsenterPerson())
					: ServiceConstants.EMPTY_STRING);
			existMedCnsntrWrnDto
					.setCourtAuth(lookupDao.decode(CodesConstant.CCRTAUTH, impactMedCnsntr.getCdCourtAuth()));
			existMedCnsntrWrnDto
					.setDesigDFPS(lookupDao.decode(CodesConstant.CFPSDESG, impactMedCnsntr.getCdDfpsDesig()));
			existMedCnsntrWrnDto.setDtStrat(impactMedCnsntr.getDtMedConsStart());
			if (!ObjectUtils.isEmpty(ssccMedCnsntr) && !ServiceConstants.Y.equals(ssccMedCnsntr.getIndBypass())) {
				existMedCnsntrWrnDto.setDtEnd(dtPlcmtStart);
			}
		}
		if (!ObjectUtils.isEmpty(ssccMedCnsntr) && !ServiceConstants.Y.equals(ssccMedCnsntr.getIndBypass())) {
			ssccMedCnsntr = evaluateCourtAuthDFPSDesig(ssccMedCnsntr, indCourtAuth);
			propsedMedCnsntrWrnDto.setName(isNonZeroLong(ssccMedCnsntr.getIdMedConsenterPerson())
					? formattingUtil.formatName(ssccMedCnsntr.getIdMedConsenterPerson())
					: ServiceConstants.EMPTY_STRING);
			propsedMedCnsntrWrnDto
					.setCourtAuth(lookupDao.decode(CodesConstant.CCRTAUTH, ssccMedCnsntr.getCdCourtAuth()));
			propsedMedCnsntrWrnDto
					.setDesigDFPS(lookupDao.decode(CodesConstant.CFPSDESG, ssccMedCnsntr.getCdDfpsDesig()));
			propsedMedCnsntrWrnDto.setDtStrat(dtPlcmtStart);
		}
		medCnstrWrnDtoLst.add(existMedCnsntrWrnDto);
		medCnstrWrnDtoLst.add(propsedMedCnsntrWrnDto);
	}

	/**
	 * Method Name: populatePlacementInfoDto Method Description: This method
	 * sets the Placement Information to create the Placement record from
	 * SSCCPlcmntOptCircum Details.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return plcmtInfo
	 */
	public static PlacementAUDDto populatePlacementInfoDto(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		PlacementAUDDto plcmtInfo = new PlacementAUDDto();
		plcmtInfo.setIdPlcmtEvent(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdPlcmtEvent());
		plcmtInfo.setIdPlcmtChild(ssccPlcmtOptCircumDto.getIdPrimaryChild());
		plcmtInfo.setIdCase(ssccPlcmtOptCircumDto.getIdCase());
		plcmtInfo.setIdContract(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdPlcmtContract());
		plcmtInfo.setIdRsrcAgency(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcAgency());
		plcmtInfo.setIdRsrcFacil(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcFacil());
		plcmtInfo.setAddrPlcmtCity(ssccPlcmtOptCircumDto.getEntityAddressDto().getAddrCity());
		plcmtInfo.setAddrPlcmtCnty(ssccPlcmtOptCircumDto.getEntityAddressDto().getCdCounty());
		plcmtInfo.setAddrPlcmtLn1(ssccPlcmtOptCircumDto.getEntityAddressDto().getAddrStLn1());
		plcmtInfo.setAddrPlcmtLn2(ssccPlcmtOptCircumDto.getEntityAddressDto().getAddrStLn2());
		plcmtInfo.setAddrPlcmtSt(ssccPlcmtOptCircumDto.getEntityAddressDto().getCdState());
		plcmtInfo.setAddrPlcmtZip(ssccPlcmtOptCircumDto.getEntityAddressDto().getAddrZip());
		plcmtInfo.setCdPlcmtInfo1(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getCdPlcmtInfo1());
		plcmtInfo.setCdPlcmtInfo2(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getCdPlcmtInfo2());
		plcmtInfo.setCdPlcmtInfo3(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getCdPlcmtInfo3());
		plcmtInfo.setCdPlcmtInfo5(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getCdPlcmtInfo5());
		plcmtInfo.setCdPlcmtLivArr(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdPlcmtLivArr());
		plcmtInfo.setCdPlcmtActPlanned(CodesConstant.CPLCMTAC_A);
		plcmtInfo.setCdPlcmtType(CodesConstant.CPLMNTYP_030);
		plcmtInfo.setDtPlcmtCaregvrDiscuss(ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto().getDtPlcmtCaregvrDiscuss());
		plcmtInfo.setDtPlcmtChildDiscuss(ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto().getDtPlcmtChildDiscuss());
		plcmtInfo.setDtPlcmtChildPlan(ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto().getDtPlcmtChildPlan());
		plcmtInfo.setDtPlcmtEducLog(ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto().getDtPlcmtEducLog());
		plcmtInfo.setDtPlcmtMeddevHistory(ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto().getDtPlcmtMeddevHistory());
		plcmtInfo.setDtPlcmtParentsNotif(ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto().getDtPlcmtParentsNotif());
		plcmtInfo.setDtPlcmtPreplaceVisit(ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto().getDtPlcmtPreplaceVisit());
		plcmtInfo.setDtPlcmtSchoolRecords(ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto().getDtPlcmtSchoolRecords());
		plcmtInfo.setDtPlcmtStart(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart());
		plcmtInfo.setIndPlcmtEducLog(ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto().getIndPlcmtEducLog());
		plcmtInfo.setIndPlcmetEmerg(ServiceConstants.N);
		plcmtInfo.setIndPlcmtNotApplic(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getIndPlcmtNotApplic());
		plcmtInfo.setIndPlcmtSchoolDoc(ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto().getIndPlcmtSchoolDocs());
		plcmtInfo
				.setPlcmtPhoneExt(!StringUtils.isEmpty(ssccPlcmtOptCircumDto.getEntityPhoneDto().getNbrPhoneExtension())
						? ssccPlcmtOptCircumDto.getEntityPhoneDto().getNbrPhoneExtension().toString()
						: ServiceConstants.EMPTY_STRING);
		plcmtInfo.setPlcmtTelephone(!StringUtils.isEmpty(ssccPlcmtOptCircumDto.getEntityPhoneDto().getNbrPhone())
				? ssccPlcmtOptCircumDto.getEntityPhoneDto().getNbrPhone().toString() : ServiceConstants.EMPTY_STRING);
		plcmtInfo.setPlcmtAgency(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getNmRsrcAgency());
		plcmtInfo.setPlcmtContact(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getNmPlcmtContact());
		plcmtInfo.setPlcmtFacil(ssccPlcmtOptCircumDto.getEntityDto().getNmEntity());
		plcmtInfo.setTxtPlcmtAddrComment(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getTxtPlcmtAddrComment());
		plcmtInfo.setTxtPlcmtDiscussion(ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto().getTxtPlcmtDiscussion());
		plcmtInfo.setTxtPlcmtDocuments(ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto().getTxtPlcmtDocuments());
		plcmtInfo.setDtPlcmtPermEff(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getDtPlcmtPermEff());
		plcmtInfo.setCdPlcmtInfo18(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getCdPlcmtInfo18());
		plcmtInfo.setCdPlcmtInfo19(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getCdPlcmtInfo19());
		plcmtInfo.setCdPlcmtInfo20(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getCdPlcmtInfo20());
		plcmtInfo.setUserId(ssccPlcmtOptCircumDto.getIdUser().toString());
		plcmtInfo.setIdRsrcSSCC(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcSSCC());
		plcmtInfo.setNmPlcmtSSCC(ssccPlcmtOptCircumDto.getSsccResourceDto().getNmSSCCResource());
		return plcmtInfo;
	}

	/**
	 * Method Name: populateMedicaidForAdd Method Description:This method sets
	 * the MedicaidUpdate Record to create the MedicaidUpdate from
	 * SSCCPlcmntOptCircum Details.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	public static MedicaidUpdateDto populateMedicaidForAdd(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		MedicaidUpdateDto medicaidUpdateDto = new MedicaidUpdateDto();
		medicaidUpdateDto.setIdMedUpdPerson(ssccPlcmtOptCircumDto.getIdPrimaryChild());
		medicaidUpdateDto.setIdMedUpdStage(ssccPlcmtOptCircumDto.getIdStage());
		medicaidUpdateDto.setIdMedUpdRecord(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdPlcmtEvent());
		medicaidUpdateDto.setCdMedUpdType(CodesConstant.CEVNTTYP_PLA);
		medicaidUpdateDto.setCdMedUpdTransType(CodesConstant.CMEDUPTR_SUS);
		medicaidUpdateDto.setIdCase(ssccPlcmtOptCircumDto.getIdCase());
		medicaidUpdateDto.setReqFuncCd(ServiceConstants.ADD);
		return medicaidUpdateDto;
	}

	/**
	 * Method Name: populateExcepCareDto Method Description: This method sets
	 * the Excep Care Record to create the Exception Care from
	 * SSCCPlcmtOptCircum Placement.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	public static ExceptionalCareDto populateExcepCareDto(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		ExceptionalCareDto excpCareDto = new ExceptionalCareDto();
		excpCareDto.setExceptCareCreatedPersonId(ssccPlcmtOptCircumDto.getIdUser());
		excpCareDto.setExceptCarelastUpdatedPersonId(ssccPlcmtOptCircumDto.getIdUser());
		excpCareDto.setDtStart(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart());
		excpCareDto.setDtEnd(DateUtils.getMaxJavaDate());
		excpCareDto.setIdPlcmntEvent(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdPlcmtEvent());
		excpCareDto.setNbrDays(ServiceConstants.ZERO);
		excpCareDto.setTxtComment(ServiceConstants.SSCC_EXCP_CARE_COMMENT);
		return excpCareDto;
	}

	/**
	 * Method Name: evaluateCourtAuthDFPSDesig Method Description: This method
	 * Updates the SSCCPlcmtMedCnsntr Object after creating the SSCC Placement.
	 * 
	 * @param ssccPlcmtMedCnsntrDto
	 * @param indCourtAuth
	 * @return
	 */
	public static SSCCPlcmtMedCnsntrDto evaluateCourtAuthDFPSDesig(SSCCPlcmtMedCnsntrDto ssccPlcmtMedCnsntrDto,
			String indCourtAuth) {
		switch (ssccPlcmtMedCnsntrDto.getCdMedCnsntrSelectType()) {
		case CodesConstant.CSSCCMDC_60:
			ssccPlcmtMedCnsntrDto.setCdCourtAuth(CodesConstant.CCRTAUTH_YALL);
			break;
		case CodesConstant.CSSCCMDC_70:
			ssccPlcmtMedCnsntrDto.setCdCourtAuth(CodesConstant.CCRTAUTH_TEMO);
			break;
		case CodesConstant.CSSCCMDC_10:
			ssccPlcmtMedCnsntrDto.setCdDfpsDesig(CodesConstant.CFPSDESG_ESEM);
			ssccPlcmtMedCnsntrDto.setCdCourtAuth(ServiceConstants.Y.equals(indCourtAuth) ? CodesConstant.CCRTAUTH_DFPS
					: CodesConstant.CCRTAUTH_CHAR);
			break;
		case CodesConstant.CSSCCMDC_80:
			ssccPlcmtMedCnsntrDto.setCdDfpsDesig(CodesConstant.CFPSDESG_ESEM);
			ssccPlcmtMedCnsntrDto.setCdCourtAuth(ServiceConstants.Y.equals(indCourtAuth) ? CodesConstant.CCRTAUTH_DFPS
					: CodesConstant.CCRTAUTH_CHAR);
			break;
		case CodesConstant.CSSCCMDC_20:
			ssccPlcmtMedCnsntrDto.setCdCourtAuth(ServiceConstants.Y.equals(indCourtAuth) ? CodesConstant.CCRTAUTH_DFPS
					: CodesConstant.CCRTAUTH_CHAR);
			ssccPlcmtMedCnsntrDto
					.setCdDfpsDesig((CodesConstant.CMCTYPE_FPRI.equals(ssccPlcmtMedCnsntrDto.getCdMedConsenterType())
							|| CodesConstant.CMCTYPE_SPRI.equals(ssccPlcmtMedCnsntrDto.getCdMedConsenterType()))
									? CodesConstant.CFPSDESG_LINC : CodesConstant.CFPSDESG_CPAE);
			break;
		case CodesConstant.CSSCCMDC_90:
			ssccPlcmtMedCnsntrDto.setCdCourtAuth(ServiceConstants.Y.equals(indCourtAuth) ? CodesConstant.CCRTAUTH_DFPS
					: CodesConstant.CCRTAUTH_CHAR);
			ssccPlcmtMedCnsntrDto
					.setCdDfpsDesig((CodesConstant.CMCTYPE_FPRI.equals(ssccPlcmtMedCnsntrDto.getCdMedConsenterType())
							|| CodesConstant.CMCTYPE_SPRI.equals(ssccPlcmtMedCnsntrDto.getCdMedConsenterType()))
									? CodesConstant.CFPSDESG_LINC : CodesConstant.CFPSDESG_CPAE);
			break;
		case CodesConstant.CSSCCMDC_30:
			ssccPlcmtMedCnsntrDto.setCdDfpsDesig(CodesConstant.CFPSDESG_LINC);
			ssccPlcmtMedCnsntrDto.setCdCourtAuth(ServiceConstants.Y.equals(indCourtAuth) ? CodesConstant.CCRTAUTH_DFPS
					: CodesConstant.CCRTAUTH_CHAR);
			break;
		case CodesConstant.CSSCCMDC_100:
			ssccPlcmtMedCnsntrDto.setCdDfpsDesig(CodesConstant.CFPSDESG_LINC);
			ssccPlcmtMedCnsntrDto.setCdCourtAuth(ServiceConstants.Y.equals(indCourtAuth) ? CodesConstant.CCRTAUTH_DFPS
					: CodesConstant.CCRTAUTH_CHAR);
			break;
		case CodesConstant.CSSCCMDC_40:
			ssccPlcmtMedCnsntrDto.setCdDfpsDesig(CodesConstant.CFPSDESG_FPSE);
			ssccPlcmtMedCnsntrDto.setCdCourtAuth(ServiceConstants.Y.equals(indCourtAuth) ? CodesConstant.CCRTAUTH_DFPS
					: CodesConstant.CCRTAUTH_CHAR);
			break;
		case CodesConstant.CSSCCMDC_50:
			ssccPlcmtMedCnsntrDto.setCdDfpsDesig(CodesConstant.CFPSDESG_FPSE);
			ssccPlcmtMedCnsntrDto.setCdCourtAuth(ServiceConstants.Y.equals(indCourtAuth) ? CodesConstant.CCRTAUTH_DFPS
					: CodesConstant.CCRTAUTH_CHAR);
			break;
		default:
			break;
		}
		return ssccPlcmtMedCnsntrDto;
	}

	/**
	 * Method Name: checkFieldChanged Method Description: Method checks the
	 * input objects if the value is changed.
	 * 
	 * @param oldVal
	 * @param newVal
	 * @return
	 */
	public static boolean checkFieldChanged(Object oldVal, Object newVal) {
		return (StringUtils.isEmpty(oldVal) && !StringUtils.isEmpty(newVal))
				|| (!StringUtils.isEmpty(oldVal) && StringUtils.isEmpty(newVal))
				|| (!StringUtils.isEmpty(oldVal) && !StringUtils.isEmpty(newVal) && !oldVal.equals(newVal));
	}
}
