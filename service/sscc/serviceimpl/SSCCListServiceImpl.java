package us.tx.state.dfps.service.sscc.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.Option;
import us.tx.state.dfps.common.dto.UserProfileDto;
import us.tx.state.dfps.service.casepackage.dao.CaseDao;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefPlcmtDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.SSCCListReq;
import us.tx.state.dfps.service.common.response.SSCCListRes;
import us.tx.state.dfps.service.common.util.CodesTableViewLookupUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.lookup.dto.CodeAttributes;
import us.tx.state.dfps.service.sscc.dao.SSCCListDao;
import us.tx.state.dfps.service.sscc.dao.SSCCRefDao;
import us.tx.state.dfps.service.sscc.service.SSCCListService;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.SSCCListHeaderDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:SSCCListServiceImpl Oct 26, 2017- 3:56:03 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class SSCCListServiceImpl implements SSCCListService {

	@Autowired
	private CaseDao caseDao;

	@Autowired
	private StageDao stageDao;

	@Autowired
	SSCCListDao ssccListDao;

	@Autowired
	SSCCRefDao ssccRefDao;

	@Autowired
	PlacementDao placementDao;

	@Autowired
	private CodesTableViewLookupUtils codesTableViewLookupUtils;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-SSCCListServiceLog");

	/**
	 * Method Name: fetchSSCCListResults Method Description:Fetches the SSCC
	 * LIST Search Results and sets it into the PaginationResultsBean object as
	 * an array list.
	 * 
	 * @param ssccListHeaderDto
	 * @param userDto
	 * @return PaginationResultDto
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCListRes fetchSSCCListResults(SSCCListHeaderDto ssccListHeaderDto) {
		SSCCListRes ssccListRes = new SSCCListRes();
		List<SSCCListDto> ssccSearchResultsList;
		ssccSearchResultsList = ssccListDao.getSSCCListResults(ssccListHeaderDto);
		if (!TypeConvUtil.isNullOrEmpty(ssccSearchResultsList)) {
			for (SSCCListDto ssccListDto : ssccSearchResultsList) {
				StageValueBeanDto stageDto = stageDao.retrieveStageInfo(ssccListDto.getIdStage());
				if (!ObjectUtils.isEmpty(stageDto) && !ObjectUtils.isEmpty(stageDto.getCdStage())) {
					ssccListDto.setCdStage(stageDto.getCdStage());
				}
				ssccListDto.setIndUserHasSensitiveCaseAccess(ServiceConstants.FALSEVAL);
				SSCCRefDto ssccRefDto = ssccListDto.getSsccRefDto();
				SSCCRefPlcmtDto ssccRefPlcmtDto = ssccListDto.getSsccRefDto().getSsccRefPlcmtDto();
				// Set flag to determine if logged user is SSCC or DFPS
				ssccListDto.setIsUserSSCC(ssccRefDao.isUserSSCCExternal(ssccListHeaderDto.getIdUser(),
						ssccRefDto.getSsccResourceDto().getCdSSCCCatchment()));

				// Check if current referral is in a sensitive case
				CapsCaseDto capsCaseDto = caseDao.selectCaseInfo(ssccRefDto.getIdCase());

				if (ServiceConstants.Y.equals(capsCaseDto.getIndCaseSensitive())) {
					ssccListDto.setIndSensitiveCase(ServiceConstants.TRUEVAL);
				}

				// If user has no stage access and does not have Case sensitive
				// user rights then
				// do not display the following information for that referral
				if (ssccListHeaderDto.getIndCaseSensitiveAccess()
						|| ssccListDao.hasStageAccess(ssccRefDto.getIdStage(), ssccListHeaderDto.getIdUser())) {
					ssccListDto.setIndUserHasSensitiveCaseAccess(ServiceConstants.TRUEVAL);
				}

				// The following information shall only be displayed for
				// sensitive cases 1)
				// Stage Name 2) Person Id 3) Case Id
				// No other information should not be displayed
				if (ServiceConstants.Y.equals(capsCaseDto.getIndCaseSensitive())
						&& !ssccListDto.getIndUserHasSensitiveCaseAccess()) {

					ssccRefDto.setCdSSCCRefType(ServiceConstants.EMPTY_STRING);
					ssccRefDto.setIndLinkedSvcAuthData(ServiceConstants.EMPTY_STRING);
					ssccRefDto.setDtDischargeActual(null);
					ssccRefDto.setDtRecorded(null);
					ssccListDto.setEfcFY(0l);
					ssccListDto.setEfcTotal(0l);
					ssccListDto.setCdPlcmtOptionStatus(ServiceConstants.EMPTY_STRING);
					ssccListDto.setCdPlcmtCircStatus(ServiceConstants.EMPTY_STRING);
					ssccListDto.setCdSvcAuthStatus(ServiceConstants.EMPTY_STRING);
					ssccListDto.setIndDaycareValidated(ServiceConstants.N);
					ssccListDto.setDtChildPlanInitiated(null);
					ssccRefPlcmtDto.setCdLegalCounty(ServiceConstants.EMPTY_STRING);
					ssccRefDto.setIdStage(null);
					ssccListDto.setDtReferral(null);
					ssccListDto.setNmPlcmtResource(ServiceConstants.EMPTY_STRING);
					ssccListDto.setIndNonssccSvcAuth(ServiceConstants.N);
					ssccListDto.setIndSsccDaycare(ServiceConstants.N);
					ssccListDto.setSsccReferralAlerts(new ArrayList<>());
					ssccListDto.setSsccplcmtAlerts(new ArrayList<>());
					ssccListDto.setSsccChildPlanAlerts(new ArrayList<>());
				}
				/**
				 * The following data is available only for Child Placement
				 * Referrals. Set EMPTY STRING where referral type is 'Family
				 * Service Referrals'
				 */
				if (ServiceConstants.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {
					ssccListDto.setCdPlcmtOptionStatus(ServiceConstants.EMPTY_STRING);
					ssccListDto.setCdPlcmtCircStatus(ServiceConstants.EMPTY_STRING);
					ssccListDto.setEfcFY(0l);
					ssccListDto.setEfcTotal(0l);
					ssccListDto.setIndDaycareValidated(ServiceConstants.N);
					ssccListDto.setIndSsccDaycare(ServiceConstants.N);
					ssccListDto.setDtChildPlanInitiated(null);
					ssccRefDto.getSsccRefPlcmtDto().setCdLegalCounty(ServiceConstants.EMPTY_STRING);
					ssccListDto.setNmPlcmtResource(ServiceConstants.EMPTY_STRING);
					ssccRefDto.setIdPerson(ServiceConstants.ZERO_VAL);
				}
				if(!ObjectUtils.isEmpty(ssccListDto.getIdStage()) 
						&& ObjectUtils.isEmpty(ssccListDto.getIdPlcmtEvent())) {
					PlacementDto plcmntDto = fetchLatestPlacement(ssccListDto.getIdStage());
					if(!ObjectUtils.isEmpty(plcmntDto)) {
						ssccListDto.setIdPlcmtEvent(plcmntDto.getIdPlcmtEvent());
					}
				}
				ssccListRes.setSsccList(ssccSearchResultsList);
				ssccListRes.setSsccListDto(ssccListDto);
				ssccListRes.getSsccListDto().setSsccRefDto(ssccRefDto);
				ssccListRes.getSsccListDto().getSsccRefDto().setSsccRefPlcmtDto(ssccRefPlcmtDto);
			}
		}
		ssccListRes.setSsccHeaderDto(ssccListHeaderDto);
		return ssccListRes;
	}

	/**
	 * 
	 * Method Name: fetchRegionUnit Method Description:Returns an arraylist of
	 * Unit Region Option objects
	 * 
	 * @return List<Option>
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Option> fetchRegionUnit() {
		return ssccListDao.fetchRegionUnit();

	}

	/**
	 * Method Name: fetchValidSSCCRegion Method Description:Returns an arraylist
	 * of valid SSCC Regions
	 * 
	 * @param userDto
	 * @return List<String>
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<String> fetchValidSSCCRegion(Long idUser, String cdRegion) {
		List<String> ssccRegionList;
		if (ssccRefDao.isUserSSCCExternal(idUser, cdRegion)) {
			ssccRegionList = ssccListDao.fetchValidUnitRegionforSSCCUser(idUser);
		} else {
			ssccRegionList = ssccListDao.fetchValidSSCCRegion();
		}
		return ssccRegionList;
	}

	/**
	 * 
	 * Method Name: computeExcludeOptions Method Description: Returns a List of
	 * codes that need to be excluded from the Region dropdown
	 * 
	 * @param validRegionList
	 * @return List<String>
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<String> computeExcludeOptions(List<String> validRegionList) {
		LOG.debug("Entering method computeExcludeOptions in SSCCListService");
		List<String> excludeOptions = new ArrayList<>();
		List<CodeAttributes> codesArray = codesTableViewLookupUtils.getCodesTableView(ServiceConstants.CREGIONS,
				ServiceConstants.CODE);

		if (!TypeConvUtil.isNullOrEmpty(codesArray)) {
			codesArray.forEach(codeAttribute -> {
				String code = codeAttribute.getCode();
				if (!validRegionList.contains(code)) {
					excludeOptions.add(code);
				}
			});
		}
		return excludeOptions;
	}

	/**
	 * 
	 * Method Name: computeExcludeOptions Method Description: Returns a List of
	 * codes that need to be excluded from the Catchment dropdown
	 * 
	 * @param validRegionList
	 * @return List<String>
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<String> fetchExcludeOptionsforCatchment(List<String> validCatchmentList) {
		LOG.debug("Entering method computeExcludeOptions in SSCCListService");
		List<String> excludeOptions = new ArrayList<>();
		List<CodeAttributes> codesArray = codesTableViewLookupUtils.getCodesTableView(ServiceConstants.CSSCCTCH,
				ServiceConstants.CODE);

		if (!TypeConvUtil.isNullOrEmpty(codesArray)) {
			codesArray.forEach(codeAttribute -> {
				String code = codeAttribute.getCode();
				if (!validCatchmentList.contains(code)) {
					excludeOptions.add(code);
				}
			});
		}
		return excludeOptions;
	}

	/**
	 * Method Name: fetchSSCCList Method Description:Fetches a row from the
	 * SSCC_LIST table using Referral Id
	 * 
	 * @param idSSCCReferral
	 * @return SSCCListDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SSCCListDto> fetchSSCCList(Long idSSCCReferral) {
		return ssccListDao.fetchSSCCList(idSSCCReferral);
	}

	/**
	 * Method Name: fetchLatestPlacement Method Description: This method
	 * retrieves Latest Placement for the given Stage
	 * 
	 * @param idStage
	 * @return PlacementDto
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PlacementDto fetchLatestPlacement(Long idStage) {
		return placementDao.selectLatestPlacement(idStage);
	}

	/**
	 * Method Name: saveSSCCList Method Description:Inserts a row into the
	 * SSCC_LIST table
	 * 
	 * @param ssccListDto
	 * @return SSCCListDto
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCListDto saveSSCCList(SSCCListDto ssccListDto) {
		return ssccListDao.insertSSCCList(ssccListDto);
	}

	/**
	 * Method Name: updateSSCCList Method Description: Updates a row into the
	 * SSCC_LIST table
	 * 
	 * @param ssccListDto
	 * @return Long
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateSSCCList(SSCCListDto ssccListDto) {
		return ssccListDao.updateSSCCList(ssccListDto);
	}

	/**
	 * Method Name: userHasAccessToSSCCListPage Method Description:Returns true
	 * if the region is a valid SSCC Catchment region
	 * 
	 * @param userDto
	 * @return Boolean
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean userHasAccessToSSCCListPage(UserProfileDto userDto) {
		return ssccListDao.isValidSSCCCatchmentRegion(userDto.getUserRegion());
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCListDto fetchCdCatchmentFromIdCatchment(SSCCListDto ssccListDto) {
		ssccListDto.setCdSsccCatchment(ssccListDao.fetchCdCatchmentFromIdCatchment(ssccListDto.getIdSSCCCatchment()));
		return ssccListDto;
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isUserSSCCExternal(SSCCListDto ssccListDto) {
		return ssccRefDao.isUserSSCCExternal(ssccListDto.getIdPerson(), ssccListDto.getCdSsccCatchment());
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean userHasSSCCCatchmentAccess(SSCCListDto ssccListDto) {
		return ssccRefDao.userHasSSCCCatchmentAccess(ssccListDto.getIdPerson(), ssccListDto.getCdSsccCatchment());
	}

	@Override
	public SSCCListRes displaySSCCListBean(SSCCListReq ssccListReq) {
		SSCCListHeaderDto ssccListHeaderDto = ObjectUtils.isEmpty(ssccListReq.getSsccListHeaderDto())
				? new SSCCListHeaderDto() : ssccListReq.getSsccListHeaderDto();
		if (!ssccListHeaderDto.getIndSearchEnabled()) {
			ssccListHeaderDto = populateDisplayRequest(ssccListHeaderDto);
		}
		ssccListHeaderDto.setValidSSCCRegionList(
				fetchValidSSCCRegion(ssccListHeaderDto.getIdUser(), ssccListHeaderDto.getCdRegion()));
		String ssccCatchment = ssccListDao.fetchDefaultCatchmentForSSCCUser(ssccListHeaderDto.getIdUser());

		// This block of code is not used any where. written as per legacy. will
		// remove later
		List<String> catchments = new ArrayList<>();
		if (!ObjectUtils.isEmpty(ssccListHeaderDto.getValidSSCCRegionList())) {
			ssccListHeaderDto.getValidSSCCRegionList().forEach(validSSCCRegion -> {
				List<String> catchmentsForRegion = ssccListDao.fetchCatchmentsForRegion(validSSCCRegion);
				catchments.addAll(catchmentsForRegion);
			});

		}
		// Setting Exclude Options for Catchment
		if (ssccRefDao.isUserSSCCExternal(ssccListHeaderDto.getIdUser(), ssccCatchment)) {
			List<String> optionalCatchment = new ArrayList<>();
			optionalCatchment.add(ssccCatchment);
			ssccListHeaderDto.setExcludeOptionSetCatchment(fetchExcludeOptionsforCatchment(optionalCatchment));
		}

		// Condition for Search Results (Enter Key Press)
		if (ssccListHeaderDto.getIndSearchEnabled()) {
			ssccListHeaderDto
					.setIdCatchment(ssccListDao.fetchIdCatchmentFromCdCatchment(ssccListHeaderDto.getCdCatchment()));
		} else { // display results
			if (!ObjectUtils.isEmpty(ssccCatchment)) {
				ssccListHeaderDto.setCdCatchment(ssccCatchment);
				ssccListHeaderDto.setIdCatchment(
						ssccListDao.fetchIdCatchmentFromCdCatchment(ssccListHeaderDto.getCdCatchment()));
			} else {
				ssccListHeaderDto.setIdCatchment(
						ssccListDao.fetchDefaultSSCCCatchmentForDFPSUser(ssccListHeaderDto.getIdUser()));
				ssccListHeaderDto.setCdCatchment(
						ssccListDao.fetchCdCatchmentFromIdCatchment(ssccListHeaderDto.getIdCatchment()));
			}
		}
		return fetchSSCCListResults(ssccListHeaderDto);
	}

	private SSCCListHeaderDto populateDisplayRequest(SSCCListHeaderDto ssccListHeaderDto) {
		ssccListHeaderDto.setIndAssignedTo(ServiceConstants.Y);
		ssccListHeaderDto.setCdUnit(ServiceConstants.EMPTY_STRING);
		ssccListHeaderDto.setIndDisplayChildPlcmtRef(ServiceConstants.Y);
		ssccListHeaderDto.setIndDisplayFamRef(ServiceConstants.Y);
		ssccListHeaderDto.setIndDisplayRefWithAlertsOnly(ServiceConstants.N);
		ssccListHeaderDto.setIndIncludeDischargedRef(ServiceConstants.N);
		ssccListHeaderDto.setIndIncludeRescindRef(ServiceConstants.N);
		if (!ObjectUtils.isEmpty(ssccListHeaderDto.getCdRegion()) && ssccListHeaderDto.getCdRegion().length() == 3
				&& ssccListHeaderDto.getCdRegion().charAt(0) == '0')
			ssccListHeaderDto.setCdRegion(ssccListHeaderDto.getCdRegion().substring(1));
		else {
			ssccListHeaderDto.setCdRegion(ssccListHeaderDto.getCdRegion());
		}
		if (ObjectUtils.isEmpty(ssccListHeaderDto.getDtBegin())) {
			ssccListHeaderDto.setDtBegin(new Date());
		}
		if (ObjectUtils.isEmpty(ssccListHeaderDto.getDtEnd())) {
			ssccListHeaderDto.setDtEnd(new Date());
		}
		return ssccListHeaderDto;
	}
}
