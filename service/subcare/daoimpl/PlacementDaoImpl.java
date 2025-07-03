package us.tx.state.dfps.service.subcare.daoimpl;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.common.dto.*;
import us.tx.state.dfps.phoneticsearch.IIRHelper.DateHelper;
import us.tx.state.dfps.service.admin.dto.ResourceServiceInDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PlacementReq;
import us.tx.state.dfps.service.common.request.SavePlacementDetailReq;
import us.tx.state.dfps.service.common.response.CommonCountRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.fce.EligibilityDto;
import us.tx.state.dfps.service.hmm.dto.HeightenedMonitoringDto;
import us.tx.state.dfps.service.kin.dto.KinChildDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.placement.dao.ContractDao;
import us.tx.state.dfps.service.placement.dto.AlertPlacementLsDto;
import us.tx.state.dfps.service.placement.dto.PlacementAUDDto;
import us.tx.state.dfps.service.placement.dto.PlacementDtlGpDto;
import us.tx.state.dfps.service.placement.dto.TemporaryAbsenceDto;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.subcare.dto.ChildBillOfRightsDto;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.EventDto;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static us.tx.state.dfps.service.common.ServiceConstants.CHAR_IND_Y;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:PlacementDao
 * performs some of the database activities related to Placement Page/table. Oct
 * 10, 2017- 1:43:33 PM © 2017 Texas Department of Family and Protective
 * Services
 */

@Repository
@SuppressWarnings("unchecked")
public class PlacementDaoImpl implements PlacementDao {

	private static final String TFC_SVC_SERVICE = "63U";
	private static final String TEP_SVC_SERVICE = "60W";

	@Value("${PlacementDaoImpl.checkLegalAction}")
	private String checkLegalAction;

	@Value("${placementDaoimpl.getEligibility}")
	private String getEligibibility;

	@Value("${PlacementDaoImpl.selectLatestPlacement}")
	private String selectLatestPlacement;

	@Value("${PlacementDaoImpl.findActivePlacements}")
	private String findActivePlacements;

	@Value("${PlacementDaoImpl.checkPlcmtDateRange}")
	private String checkPlcmtDateRange;

	@Value("${PlacementDaoImpl.findAllPlacementsForStage}")
	private String findAllPlacementsForStage;

	@Value("${PlacementDaoImpl.findAllQRTPPlacementsForChild}")
	private String findAllQRTPPlacementsForChild;


	@Value("${PlacementDaoImpl.getActiveSilContract}")
	private String getActiveSilContract;

	@Value("${PlacementDaoImpl.getAllContractPeriods}")
	private String getAllContractPeriods;

	@Value("${PlacementDaoImpl.getSILRsrsSvc}")
	private String getSILRsrsSvc;

	@Value("${PlacementDaoImpl.getCorrespondingPlacement}")
	private String getCorrespondingPlacement;

	@Value("${PlacementDaoImpl.getContractCounty_sscc}")
	private String getContractCounty_sscc;

	@Value("${PlacementDaoImpl.getContractCounty_sil}")
	private String getContractCounty_sil;

	@Value("${PlacementDaoImpl.getAddCntInSilRsrc}")
	private String getAddCntInSilRsrc;

	@Value("${PlacementDaoImpl.isSSCCPlacement}")
	private String isSSCCPlacement;

	@Value("${PlacementDaoImpl.getExeptCareCreaters}")
	private String getExeptCareCreaters;

	@Value("${PlacementDaoImpl.isActSsccCntrctExist}")
	private String isActSsccCntrctExist;

	@Value("${PlacementDaoImpl.getActiveChildPlcmtReferral}")
	private String getActiveChildPlcmtReferral;

	@Value("${PlacementDaoImpl.getActiveSSCCReferral}")
	private String getActiveSSCCReferral;

	@Value("${PlacementDaoImpl.childLastestPlcmtSSCC}")
	private String childLastestPlcmtSSCC;

	@Value("${PlacementDaoImpl.getChildPlanInitiateInfo}")
	private String getChildPlanInitiateInfo;

	@Value("${PlacementDaoImpl.getExcpCareDaysUsed}")
	private String getExcpCareDaysUsed;

	@Value("${PlacementDaoImpl.findActivePlacementsForEligibilty}")
	private String findActivePlacementsForEligibiltySql;

	@Value("${PlacementDaoImpl.findPlacementSql}")
	private String findPlacementSql;

	@Value("${PlacementDaoImpl.findRecentPlacements}")
	private String findRecentPlacementsSql;

	@Value("${PlacementDaoImpl.findOtherChildInPlacementCount}")
	private String findOtherChildInPlacementCount;

	@Value("${PlacementDaoImpl.getPlacementsByChildId}")
	private String getPlacementsByChildId;

	@Value("${PlacementDaoImpl.getPlacementDetailsByChildId}")
	private String getPlacementDetailsByChildId;

	@Value("${PlacementDaoImpl.getDistinctService}")
	private String getDistinctServiceSql;

	@Value("${PlacementDaoImpl.getContractDtl}")
	private String getContractDtlSql;

	@Value("${PlacementDaoImpl.getContractSignedOrNot}")
	private String getContractSignedOrNotSql;

	@Value("${PlacementDaoImpl.getDtActiveADO}")
	private String getDtActiveADOSql;

	@Value("${PlacementDaoImpl.getEventStageCount}")
	private String getEventStageCountSql;

	@Value("${PlacementDaoImpl.checkRecIdPerson}")
	private String checkRecIdPersonSql;

	@Value("${PlacementDaoImpl.checkOpenPlacementChld}")
	private String checkOpenPlacementChldSql;

	@Value("${PlacementDaoImpl.OpenPlacementChldDiffCases}")
	private String OpenPlacementChldDiffCasesSql;

	@Value("${PlacementDaoImpl.checkNewRecOverlaps}")
	private String checkNewRecOverlapsSql;

	@Value("${PlacementDaoImpl.checkOpenPlcmtOpenStage}")
	private String checkOpenPlcmtOpenStageSql;

	@Value("${PlacementDaoImpl.checkOpenPlcmtCloseStage}")
	private String checkOpenPlcmtCloseStageSql;

	@Value("${PlacementDaoImpl.checkNewRecOverlapsOtherRec}")
	private String checkNewRecOverlapsOtherRecSql;

	@Value("${PlacementDaoImpl.checkNewRecIdentical}")
	private String checkNewRecIdenticalSql;

	@Value("${PlacementDaoImpl.checkDtPlcmtStartOneDayMore}")
	private String checkDtPlcmtStartOneDayMoreSql;

	@Value("${PlacementDaoImpl.checkDtPlcmtStartRightOneDayMore}")
	private String checkDtPlcmtStartRightOneDayMoreSql;

	@Value("${PlacementDaoImpl.checkRecordPrimaryKey}")
	private String checkRecordPrimaryKeySql;

	@Value("${PlacementDaoImpl.checkDatesAbs}")
	private String checkDatesAbsSql;

	@Value("${PlacementDaoImpl.checkLeftOverlap}")
	private String checkLeftOverlapSql;

	@Value("${PlacementDaoImpl.checkForSUBStageCount}")
	private String checkForSUBStageCountSql;

	@Value("${PlacementDaoImpl.checkRightOverlap}")
	private String checkRightOverlapSql;

	@Value("${PlacementDaoImpl.checkDtPlcmtStartLeftOneDayMore}")
	private String checkDtPlcmtStartLeftOneDayMoreSql;

	@Value("${PlacementDaoImpl.checkDtPlcmtStartRightOneDayMore}")
	private String checkDtPlcmtStartOneDayMoreRightSql;

	@Value("${PlacementDaoImpl.checkOverlapPlacementinDiffCases}")
	private String checkOverlapPlacementinDiffCasesSql;

	@Value("${PlacementDaoImpl.updateKinship}")
	private String updateKinshipSql;

	@Value("${PlacementDaoImpl.checkIdStage}")
	private String checkIdStageSql;

	@Value("${PlacementDaoImpl.checkRecForIdStageNIdEvent}")
	private String checkRecForIdStageNIdEventSql;

	@Value("${PlacementDaoImpl.checkNewRecOverlapsLeft}")
	private String checkNewRecOverlapsLeftSql;

	@Value("${PlacementDaoImpl.checkNewRecOverlapsRight}")
	private String checkNewRecOverlapsRightSql;

	@Value("${PlacementDaoImpl.checkNewRecIden}")
	private String checkNewRecIdenSql;

	@Value("${PlacementDaoImpl.checkDtPlcmtStartLeftOneDayBigger}")
	private String checkDtPlcmtStartLeftOneDayBiggerSql;

	@Value("${PlacementDaoImpl.checkDtPlcmtStartRightOneDayBigger}")
	private String checkDtPlcmtStartRightOneDayBiggerSql;

	@Value("${PlacementDaoImpl.updateKinshipIndYes}")
	private String updateKinshipIndYesSql;

	@Value("${PlacementDaoImpl.countADOSubcareStage}")
	private String countADOSubcareStageSql;

	@Value("${PlacementDaoImpl.countOtherSubcareStage}")
	private String countOtherSubcareStageSql;

	@Value("${PlacementDaoImpl.getPriorPlacementsById}")
	private String getPriorPlacementsByIdSql;

	@Value("${PlacementDaoImpl.getIndChildSibing1}")
	private String getIndChildSibing1Sql;

	@Value("${PlacementDaoImpl.getActualPlacementByStageId}")
	private String getActualPlacementByStageIdSql;

	@Value("${PlacementDaoImpl.getMostRecentPlacement}")
	private String getMostRecentPlacementSql;

	@Value("${PlacementDaoImpl.getMostRecentLinvingArrangement}")
	private String getMostRecentLinvingArrangementSql;

	@Value("${PlacementDaoImpl.approversSql}")
	private String approversSql;

	@Value("${PlacementDaoImpl.alertForPlacementTriggerOne}")
	private String alertForPlacementTriggerOne;

	@Value("${PlacementDaoImpl.alertForPlacementTriggerTwo}")
	private String alertForPlacementTriggerTwo;

	@Value("${PlacementDaoImpl.alertForLSTriggerOne}")
	private String alertForLSTriggerOne;

	@Value("${PlacementDaoImpl.alertForLSTriggerTwo}")
	private String alertForLSTriggerTwo;

	@Value("${PlacementDaoImpl.getActiveTepContract}")
	private String getActiveTepContract;

	@Value("${PlacementDaoImpl.getCountOfActiveTfcPlmnts}")
	private String getCountOfActiveTfcPlmnts;

	@Value("${PlacementDaoImpl.getCountOfAllPlacements}")
	private String getCountOfAllPlacements;

	@Value("${PlacementDaoImpl.getPersonlocEventId}")
	private String getPersonlocEventId;

	@Value("${PlacementDaoImpl.getCountyRegion}")
	private String getCountyRegion;

	@Value("${PlacementDaoImpl.getOpenPlacementCountForCase}")
	private String getOpenPlacementCountForCase;

	@Value("${PlacementDaoImpl.getBillOfRightsDatesByChildId}")
	private String getBillOfRightsDatesByChildIdSql;

	@Value("${PlacementDaoImpl.getCountOfAllPlacementsByChildId}")
	private String getCountOfAllPlacementsByChildIdSql;

	@Value("${PlacementDaoImpl.getBillOfRightsDatesByPlcmtId}")
	private String getBillOfRightsDatesByPlcmtIdSql;

	@Value("${PlacementDaoImpl.getEarliestReviewBillOfRights}")
	private String getEarliestReviewBillOfRightsSql;

	@Value("${PlacementDaoImpl.getTemporaryAbsenceList}")
	private String getTemporaryAbsenceListSql;

	@Value("${PlacementDaoImpl.getAllChildPlacementsId}")
	private String getAllChildPlacementsIdSql;

	@Value("${PlacementDaoImpl.getPlacementChildInfo}")
	private String getPlacementChildInfoSql;

	@Value("${PlacementDaoImpl.getApprovedPlacementForAChild}")
	private String getApprovedPlacementForAChildSql;

	@Value("${PlacementDaoImpl.getPlacementsInfo}")
	private String getPlacementsInfoSql;

	@Value("${PlacementDaoImpl.getPlacementsAdultId}")
	private String getPlacementsAdultIdSql;

	@Value("${PlacementDaoImpl.getPlacementLegalStatusInfo}")
	private String getPlacementLegalStatusInfoSql;

	@Value("${PlacementDaoImpl.getChildPlcmtReferrals}")
	private String getChildPlcmtReferrals;

    @Value("${PlacementDaoImpl.chkValidFPSContractRsrcSql}")
	private String chkValidFPSContractRsrcSql;

	@Value("${PlacementDaoImpl.getChildPlacement}")
	private String getChildPlacementSql;

	@Value("${PlacementDaoImpl.getSvcPkgAddonForTAF}")
	private String getSvcPkgAddonForTAFSql;

	@Value("${PlacementDaoImpl.latestPlacement}")
	private String latestPlacement;

	@Value("${PlacementDaoImpl.getParentPlacementSql}")
	private String getParentPlacementSql;
	@Value("${PlacementDaoImpl.getCountCPBPlcmntsForYouthParentSql}")
	private String getCountCPBPlcmntsForYouthParentSql;

	@Value("${ServicePackageDaoImpl.getValidContract}")
	private String getValidContract;


	@Value("${PlacementDaoImpl.checkAlocBlocForNonT3cPlcmtSql}")
	private String checkAlocBlocForNonT3cPlcmtSql;
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Autowired
	PersonDao personDao;

	@Autowired
	ContractDao contractDao;

	@Autowired
	CapsResourceDao capsResourceDao;

	/**
	 *
	 * Method Name: selectPlacement Method Description:This method retrieves
	 * Placement Details from the database using idPlcmtEvent.
	 *
	 * @param idPlcmtEvent
	 * @return PlacementDto
	 */
	@Override
	public Placement selectPlacement(Long idPlcmtEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Placement.class);
		criteria.add(Restrictions.eq("idPlcmtEvent", idPlcmtEvent));
		Placement placement = (Placement) criteria.uniqueResult();
		return placement;
	}

	/**
	 *
	 * Method Name: selectLatestPlacement Method Description:This method
	 * retrieves Latest Placement for the given
	 *
	 * @param idStage
	 * @return PlacementDto @
	 */
	@Override
	public PlacementDto selectLatestPlacement(Long idStage) {
		PlacementDto placementDto = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(selectLatestPlacement)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtEnd", StandardBasicTypes.DATE).addScalar("caseId", StandardBasicTypes.LONG)
				.addScalar("idRsrcAgency", StandardBasicTypes.LONG).addScalar("idRsrcFacil", StandardBasicTypes.LONG)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtRemovalRsn", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtActPlanned", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtService", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo1", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo2", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo3", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo4", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo5", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo6", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo7", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo8", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo9", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo10", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo11", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo12", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo13", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo14", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo15", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo16", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo17", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo18", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo19", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo20", StandardBasicTypes.STRING)
				.addScalar("indCongregateCare", StandardBasicTypes.STRING)
			.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		placementDto = (PlacementDto) query.uniqueResult();
		return placementDto;
	}

	/**
	 * Method Name: checkLegalAction Method Description:This method Checks for
	 * the proper Legal Action
	 *
	 * @param placementValueDto
	 * @return Boolean @
	 */

	@Override
	public Boolean checkLegalAction(PlacementValueDto placementValueDto) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(checkLegalAction)
				.addScalar("cdLegalActAction", StandardBasicTypes.STRING)
				.setParameter("idPerson", placementValueDto.getIdPerson())
				.setParameter("idCase", placementValueDto.getIdCase())
				.setParameter("cdLegalActnSubStartCode", placementValueDto.getCdLegalActnSubStartCode())
				.setParameter("dtPlcmtStart", placementValueDto.getDtPlcmtStart())
				.setParameter("cdLegalActnSubEndCode", placementValueDto.getCdLegalActnSubEndCode())
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));

		List<PlacementValueDto> placementValueDtoList = query.list();
		if (ObjectUtils.isEmpty(placementValueDtoList)) {
			return ServiceConstants.FALSEVAL;
		} else {
			return ServiceConstants.TRUEVAL;
		}
	}

	/**
	 * Method Name: findActivePlacements Method Description:Fetches the most
	 * recent open Active Placement for the idPerson
	 *
	 * @param idPerson
	 * @return List<PlacementValueDto>
	 */

	@Override
	public List<PlacementValueDto> findActivePlacements(Long idPerson) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(findActivePlacements)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtEnd", StandardBasicTypes.DATE).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idRsrcAgency", StandardBasicTypes.LONG).addScalar("idRsrcFacil", StandardBasicTypes.LONG)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtRemovalRsn", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtActPlanned", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtService", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo1", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo2", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo3", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo4", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo5", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo6", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo7", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo8", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo9", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo10", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo11", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo12", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo13", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo14", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo15", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo16", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo17", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo18", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo19", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo20", StandardBasicTypes.STRING)
				.addScalar("idPlcmtAdult", StandardBasicTypes.LONG).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		return (List<PlacementValueDto>) query.list();

	}

	/**
	 * Method Name: checkPlcmtDateRange Method Description:This method checks if
	 * there is any Placement for the Child in the Range of Placement Start
	 * Date.
	 *
	 * @param idPerson
	 * @param dtPlcmtStart
	 * @return List<PlacementValueDto>
	 */
	@Override
	public List<PlacementValueDto> checkPlcmtDateRange(Long idPerson, Date dtPlcmtStart) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(checkPlcmtDateRange)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtEnd", StandardBasicTypes.DATE).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idRsrcAgency", StandardBasicTypes.LONG).addScalar("idRsrcFacil", StandardBasicTypes.LONG)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtRemovalRsn", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtActPlanned", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtService", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo1", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo2", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo3", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo4", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo5", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo6", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo7", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo8", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo9", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo10", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo11", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo12", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo13", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo14", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo15", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo16", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo17", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo18", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo19", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo20", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setParameter("dtPlcmtStart", dtPlcmtStart)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		return (List<PlacementValueDto>) query.list();
	}

	/**
	 * Method Name: findAllPlacementsForStage Method Description:This method
	 * returns all the placements for the given Stage
	 *
	 * @param stageId
	 * @return List<PlacementValueDto> @
	 */

	@Override
	public List<PlacementValueDto> findAllPlacementsForStage(Long stageId) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(findAllPlacementsForStage)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("dtParentPlcmtStart", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtEnd", StandardBasicTypes.DATE).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idRsrcAgency", StandardBasicTypes.LONG).addScalar("idRsrcFacil", StandardBasicTypes.LONG)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtRemovalRsn", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtActPlanned", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtService", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo1", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo2", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo3", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo4", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo5", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo6", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo7", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo8", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo9", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo10", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo11", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo12", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo13", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo14", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo15", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo16", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo17", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo18", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo19", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo20", StandardBasicTypes.STRING)
				.addScalar("indCongregateCare", StandardBasicTypes.STRING).setParameter("idStage", stageId)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		Object obj = query.list();
		if(ObjectUtils.isEmpty(obj)) {
			return Collections.emptyList();
		}
		return (List<PlacementValueDto>) obj;
	}

	@Override
	public List<PlacementValueDto> findAllQTRPPlacements(Long childID) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(findAllQRTPPlacementsForChild)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtEnd", StandardBasicTypes.DATE).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idRsrcAgency", StandardBasicTypes.LONG).addScalar("idRsrcFacil", StandardBasicTypes.LONG)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtRemovalRsn", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtActPlanned", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtService", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo1", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo2", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo3", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo4", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo5", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo6", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo7", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo8", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo9", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo10", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo11", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo12", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo13", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo14", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo15", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo16", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo17", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo18", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo19", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo20", StandardBasicTypes.STRING)
				.setParameter("childID", childID)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		Object obj = query.list();
		if(ObjectUtils.isEmpty(obj)) {
			return Collections.emptyList();
		}
		return (List<PlacementValueDto>) obj;
	}

	/**
	 * Method Name: getActiveSilContract Method Description:This method returns
	 * an active SIL
	 *
	 * @param idResource
	 * @return List<PlacementValueDto>
	 */

	@Override
	public List<PlacementValueDto> getActiveSilContract(Long idResource) {
		List<PlacementValueDto> placementValueDtoList = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getActiveSilContract)
				.addScalar("idRsrcSSCC", StandardBasicTypes.LONG).addScalar("idContract", StandardBasicTypes.LONG)
				.addScalar("cdContractRegion", StandardBasicTypes.STRING)
				.addScalar("cdContractStatus", StandardBasicTypes.STRING)
				.addScalar("cdContractService", StandardBasicTypes.STRING)
				.addScalar("dtContractStart", StandardBasicTypes.DATE)
				.addScalar("dtContractTerm", StandardBasicTypes.DATE).setParameter("idRsrcSSCC", idResource)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		placementValueDtoList = (List<PlacementValueDto>) query.list();
		return placementValueDtoList;

	}

	/**
	 * Method Name: getAllContractPeriods Method Description:This method returns
	 * all the contract periods
	 *
	 * @param idResource
	 * @return List<PlacementValueDto>
	 */

	@Override
	public List<PlacementValueDto> getAllContractPeriods(Long idResource) {
		List<PlacementValueDto> placementValueDtoList = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAllContractPeriods)
				.addScalar("idRsrcSSCC", StandardBasicTypes.LONG).addScalar("idContract", StandardBasicTypes.LONG)
				.addScalar("cdContractRegion", StandardBasicTypes.STRING)
				.addScalar("cdContractStatus", StandardBasicTypes.STRING)
				.addScalar("cdContractService", StandardBasicTypes.STRING)
				.addScalar("dtContractStart", StandardBasicTypes.DATE)
				.addScalar("dtContractTerm", StandardBasicTypes.DATE).setParameter("idRsrcSSCC", idResource)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		placementValueDtoList = (List<PlacementValueDto>) query.list();
		return placementValueDtoList;

	}

	/**
	 * Method Name: getSILRsrsSvc Method Description:This method returns List of
	 * SIL resource services.
	 *
	 * @param idResource
	 * @return List<PlacementValueDto>
	 */

	@Override
	public List<PlacementValueDto> getSILRsrsSvc(Long idResource) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSILRsrsSvc)
				.addScalar("idRsrcSSCC", StandardBasicTypes.LONG)
				.addScalar("cdResourceService", StandardBasicTypes.STRING).setParameter("idRsrcSSCC", idResource)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		return (List<PlacementValueDto>) query.list();
	}

	/**
	 * Method Name: getCorrespondingPlacement Method Description:This method
	 * returns List corresponding parent placement for the child within a stage
	 *
	 * @param stageId
	 * @return List<PlacementValueDto>
	 */

	@Override
	public List<PlacementValueDto> getCorrespondingPlacement(Long stageId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getCorrespondingPlacement)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE).addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
				.addScalar("idRsrcFacil", StandardBasicTypes.LONG).addScalar("idRsrcSSCC", StandardBasicTypes.LONG)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING).setParameter("idStage", stageId)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		return (List<PlacementValueDto>) query.list();
	}

	/**
	 * Method Name: getContractCounty Method Description:This method gets
	 * address county in SIL Contract services
	 *
	 * @param placementValueDto
	 * @return List<PlacementValueDto> @
	 */

	@Override
	public List<PlacementValueDto> getContractCounty(PlacementValueDto placementValueDto) {

		String selectSql = ServiceConstants.EMPTY_STR;
		if (placementValueDto.getQualifySscc().equals(ServiceConstants.Y))
			selectSql = getContractCounty_sscc;
		else {
			selectSql = getContractCounty_sil;
		}
		Query query = sessionFactory.getCurrentSession().createSQLQuery(selectSql)
				.addScalar("dtContractTerm", StandardBasicTypes.DATE)
				.addScalar("dtContractStart", StandardBasicTypes.DATE).addScalar("county", StandardBasicTypes.STRING)
				.addScalar("cdContractRegion", StandardBasicTypes.STRING)
				.setParameter("idRsrcFacil", placementValueDto.getIdRsrcFacil())
				.setParameter("county", placementValueDto.getCounty())
				.setParameter("cdPlcmtLivArr", placementValueDto.getCdPlcmtLivArr())
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		return (List<PlacementValueDto>) query.list();
	}

	/**
	 * Method Name: getAddCntInSilRsrc Method Description:This method gets
	 * address county in SIL resource services
	 *
	 * @param idResource
	 * @param szCountyCode
	 * @param livArr
	 * @return List<PlacementValueDto>
	 */

	@Override
	public List<PlacementValueDto> getAddCntInSilRsrc(Long idResource, String szCountyCode, String livArr) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAddCntInSilRsrc)
				.addScalar("idRsrcFacil", StandardBasicTypes.LONG).addScalar("county", StandardBasicTypes.STRING)
				.addScalar("cdContractRegion", StandardBasicTypes.STRING).setParameter("idRsrcFacil", idResource)
				.setParameter("county", szCountyCode).setParameter("cdPlcmtLivArr", livArr)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		return (List<PlacementValueDto>) query.list();
	}

	/**
	 * Method Name: isSSCCPlacement Method Description:This method checks to see
	 * if the placement is a SSCC placement
	 *
	 * @param idPlcmtEvent
	 * @return Boolean
	 */
	@Override
	public Boolean isSSCCPlacement(Long idPlcmtEvent) {
		Boolean result = ServiceConstants.TRUEVAL;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(isSSCCPlacement)
				.addScalar("idRsrcSSCC", StandardBasicTypes.LONG).setParameter("idPlcmtEvent", idPlcmtEvent)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		PlacementValueDto placementValueDto = (PlacementValueDto) query.uniqueResult();
		if (ObjectUtils.isEmpty(placementValueDto) || placementValueDto.getIdRsrcSSCC() == 0) {
			result = ServiceConstants.FALSEVAL;
		}
		return result;
	}

	/**
	 * Method Name: getExeptCareCreaters Method Description:This method to
	 * create an alert to do to staff who created Exception Care
	 *
	 * @param idEvent
	 * @return Long @
	 */
	@Override
	public Long getExeptCareCreaters(Long idEvent) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getExeptCareCreaters)
				.setParameter("idPlcmtEvent", idEvent);

		BigDecimal creatorsValue = (BigDecimal) query.uniqueResult();
		if (ObjectUtils.isEmpty(creatorsValue)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}

		return creatorsValue.longValue();
	}

	/**
	 * Method Name: isActSsccCntrctExist Method Description:This method gets
	 * active SSCC contract services for the catchment area
	 *
	 * @param placeReq
	 * @return PlacementValueDto
	 */

	@Override
	public PlacementValueDto isActSsccCntrctExist(PlacementReq placeReq) {
		List<PlacementValueDto> placementValueDtoList = null;
		PlacementValueDto placementValueDto1 = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(isActSsccCntrctExist)
				.addScalar("resourceName", StandardBasicTypes.STRING).addScalar("idRsrcSSCC", StandardBasicTypes.LONG)
				.addScalar("dtBatchSSCCStart", StandardBasicTypes.DATE)
				.addScalar("dtBatchSSCCEnd", StandardBasicTypes.DATE).setParameter("idStage", placeReq.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		placementValueDtoList = (List<PlacementValueDto>) query.list();
		if (!ObjectUtils.isEmpty(placementValueDtoList)) {
			placementValueDto1 = placementValueDtoList.get(0);
		}
		return placementValueDto1;
	}

	/**
	 * Method Name: getActiveChildPlcmtReferral Method Description:This method
	 * gets valid child placement referral information for the stage id (an
	 * active referral here is not base on the status, it's base on the referral
	 * recorded and discharged dates.
	 *
	 * @param stageId
	 * @return PlacementValueDto @
	 */

	@Override
	public PlacementValueDto getActiveChildPlcmtReferral(Long stageId) {
		PlacementValueDto placementValueDto = null;
		List<PlacementValueDto> placementValueDtoList = null;

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getActiveChildPlcmtReferral)
				.addScalar("idRsrcSSCC", StandardBasicTypes.LONG)
				.addScalar("cdContractRegion", StandardBasicTypes.STRING)
				.addScalar("resourceName", StandardBasicTypes.STRING).addScalar("idContract", StandardBasicTypes.LONG)
				.addScalar("dtDischargeActual", StandardBasicTypes.DATE)
				.addScalar("dtRecorded", StandardBasicTypes.DATE).addScalar("dtRecordedSSCC", StandardBasicTypes.DATE)
				.addScalar("dtRecordedDFPS", StandardBasicTypes.DATE)
				.addScalar("indPriorCommunication", StandardBasicTypes.STRING)
				.addScalar("idSsccReferral", StandardBasicTypes.LONG)
				.addScalar("dtExpectedPlcmt",StandardBasicTypes.DATE).setParameter("idStage", stageId)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));

		placementValueDtoList = (List<PlacementValueDto>) sQLQuery1.list();

		if (!ObjectUtils.isEmpty(placementValueDtoList)) {
			placementValueDto = placementValueDtoList.get(ServiceConstants.Zero);
			if (ServiceConstants.Y.equalsIgnoreCase(placementValueDto.getIndPriorCommunication())) {
				if (ObjectUtils.isEmpty(placementValueDto.getDtRecordedSSCC())) {
					placementValueDto.setDtRecordedSSCC(placementValueDto.getDtRecorded());
				}
				if (ObjectUtils.isEmpty(placementValueDto.getDtRecordedDFPS())) {
					placementValueDto.setDtRecordedDFPS(placementValueDto.getDtRecorded());
				}

			} else {
				placementValueDto.setDtRecordedSSCC(placementValueDto.getDtRecorded());
				placementValueDto.setDtRecordedDFPS(placementValueDto.getDtRecorded());
			}

			if (((placementValueDto.getDtRecorded() != null) && (placementValueDto.getDtRecordedSSCC() != null)
					&& (placementValueDto.getDtRecordedDFPS() != null))
					&& (DateUtils.isBefore(placementValueDto.getDtRecorded(), placementValueDto.getDtRecordedSSCC())
							|| placementValueDto.getDtRecorded().equals(placementValueDto.getDtRecordedSSCC()))
					&& (DateUtils.isBefore(placementValueDto.getDtRecorded(), placementValueDto.getDtRecordedDFPS())
							|| placementValueDto.getDtRecorded().equals(placementValueDto.getDtRecordedDFPS()))) {
				placementValueDto.setDtReferralDate(placementValueDto.getDtRecorded());
			} else if (((placementValueDto.getDtRecorded() != null) && (placementValueDto.getDtRecordedSSCC() != null)
					&& (placementValueDto.getDtRecordedDFPS() != null))
					&& (DateUtils.isBefore(placementValueDto.getDtRecordedSSCC(), placementValueDto.getDtRecorded())
							|| placementValueDto.getDtRecordedSSCC().equals(placementValueDto.getDtRecorded()))
					&& (DateUtils.isBefore(placementValueDto.getDtRecordedSSCC(), placementValueDto.getDtRecordedDFPS())
							|| placementValueDto.getDtRecordedSSCC().equals(placementValueDto.getDtRecordedDFPS()))) {
				placementValueDto.setDtReferralDate(placementValueDto.getDtRecordedSSCC());
			} else {
				placementValueDto.setDtReferralDate(placementValueDto.getDtRecordedDFPS());
			}
			if (ObjectUtils.isEmpty(placementValueDto.getDtExpectedPlcmt())) {
				placementValueDto.setDtExpectedPlcmt(placementValueDto.getDtRecorded());
			}
		}
		return placementValueDto;

	}

	/**
	 * Method Name: updateIndPlcmtSSCC Method Description:This method updates
	 * indicator placement sscc
	 *
	 * @param placementValueDto
	 * @return PlacementValueDto @
	 */

	@Override
	public PlacementValueDto updateIndPlcmtSSCC(PlacementValueDto placementValueDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccList.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", placementValueDto.getIdSsccReferral()));
		List<SsccList> ssccLists = criteria.list();

		if (ObjectUtils.isEmpty(ssccLists)) {
			throw new DataNotFoundException(
					messageSource.getMessage("PlacementDao.getIndPlcmtSSCC.NotFound", null, Locale.US));
		}
		for (SsccList ssccList : ssccLists) {
			ssccList.setIndPlcmtSscc(placementValueDto.getIndPlcmtSSCC());
			sessionFactory.getCurrentSession().saveOrUpdate(ssccList);
		}
		return placementValueDto;
	}

	/**
	 * Method Name: updateIdPlcmtSSCC Method Description:This method updates id
	 * placement sscc
	 *
	 * @param placementValueDto
	 * @return PlacementValueDto @
	 */

	@Override
	public PlacementValueDto updateIdPlcmtSSCC(PlacementValueDto placementValueDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccList.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", placementValueDto.getIdSsccReferral()));
		List<SsccList> ssccLists = criteria.list();

		if (ObjectUtils.isEmpty(ssccLists)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		for (SsccList ssccList : ssccLists) {
			ssccList.setIdPlcmtRsrc(placementValueDto.getIdPlcmtRsrc());
			ssccList.setIdPlcmtEvent(placementValueDto.getIdPlcmtEvent());
			sessionFactory.getCurrentSession().saveOrUpdate(ssccList);
		}
		return placementValueDto;

	}

	/**
	 * Method Name: updateChildPlanDue Method Description:This method updates id
	 * placement sscc
	 *
	 * @param placementValueDto
	 * @return PlacementValueDto @
	 */

	@Override
	public PlacementValueDto updateChildPlanDue(PlacementValueDto placementValueDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccList.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", placementValueDto.getIdSsccReferral()));
		List<SsccList> ssccLists = criteria.list();

		if (CollectionUtils.isEmpty(ssccLists)) {
			return placementValueDto;
		}
		for (SsccList ssccList : ssccLists) {
			ssccList.setCdChildPlanDue(placementValueDto.getCdChildPlanDue());
			ssccList.setDtChildPlanDue(placementValueDto.getDtChildPlanDue());
			ssccList.setDtChildPlanInitiated(placementValueDto.getDtChildPlanInitiated());
			sessionFactory.getCurrentSession().saveOrUpdate(ssccList);
		}
		return placementValueDto;

	}

	/**
	 * Method Name: updateIndEfcActive Method Description:This method updates
	 * indicator EFC Active
	 *
	 * @param placementValueDto
	 * @return PlacementValueDto @
	 */

	@Override
	public PlacementValueDto updateIndEfcActive(PlacementValueDto placementValueDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccList.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", placementValueDto.getIdSsccReferral()));
		List<SsccList> ssccLists = criteria.list();
		if (ObjectUtils.isEmpty(ssccLists)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		for (SsccList ssccList : ssccLists) {
			ssccList.setIndEfcActive(placementValueDto.getIndEfcActive());
			sessionFactory.getCurrentSession().saveOrUpdate(ssccList);
		}
		return placementValueDto;
	}

	/**
	 * Method Name: updateIndEfc Method Description:This method updates
	 * indicator EFC
	 *
	 * @param placementValueDto
	 * @return PlacementValueDto @
	 */

	@Override
	public PlacementValueDto updateIndEfc(PlacementValueDto placementValueDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccList.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", placementValueDto.getIdSsccReferral()));
		List<SsccList> ssccLists = criteria.list();

		if (ObjectUtils.isEmpty(ssccLists)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		for (SsccList ssccList : ssccLists) {
			ssccList.setIndEfc(placementValueDto.getIndEfc());
			sessionFactory.getCurrentSession().saveOrUpdate(ssccList);
		}
		return placementValueDto;

	}

	/**
	 * Method Name: updateIndLinkedPlcmtData Method Description: This method
	 * updates indicator linked placement data
	 *
	 * @param placementValueDto
	 * @return PlacementValueDto @
	 */

	@Override
	public PlacementValueDto updateIndLinkedPlcmtData(PlacementValueDto placementValueDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccReferral.class);
		criteria.add(Restrictions.eq("idSSCCReferral", placementValueDto.getIdSsccReferral()));
		List<SsccReferral> ssccReferralLists = criteria.list();

		if (ObjectUtils.isEmpty(ssccReferralLists)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		for (SsccReferral ssccReferral : ssccReferralLists) {
			ssccReferral.setIndLinkedPlcmtData(placementValueDto.getIndPlcmtLinkedData());

			sessionFactory.getCurrentSession().saveOrUpdate(ssccReferral);
		}
		return placementValueDto;

	}

	/**
	 * Method Name: getIndPlcmtSSCC Method Description:This method gets indicate
	 * placement sscc
	 *
	 * @param idReferral
	 * @return PlacementValueDto @
	 */

	@Override
	public PlacementValueDto getIndPlcmtSSCC(Long idReferral) {
		PlacementValueDto placementValueDto = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccList.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", idReferral));
		List<SsccList> ssccLists = criteria.list();
		if (!ObjectUtils.isEmpty(ssccLists)) {
			placementValueDto = new PlacementValueDto();
			placementValueDto.setIndPlcmtSSCC(ssccLists.get(0).getIndPlcmtSscc());
		}

		if (ObjectUtils.isEmpty(placementValueDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("PlacementDao.getIndPlcmtSSCC.NotFound", null, Locale.US));
		}
		return placementValueDto;
	}

	/**
	 * Method Name: getActiveSSCCReferral Method Description:This method gets
	 * active sscc referral for stage id
	 *
	 * @param stageId
	 * @return PlacementValueDto @
	 */

	@Override
	public List<PlacementValueDto> getActiveSSCCReferral(Long stageId) {
		List<PlacementValueDto> placementValueDtoList = null;
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getActiveSSCCReferral)
				.addScalar("cdLegalStatus", StandardBasicTypes.STRING)
				.addScalar("idSsccReferral", StandardBasicTypes.LONG).setParameter("idStage", stageId)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));

		placementValueDtoList = (List<PlacementValueDto>) sQLQuery1.list();

		if (ObjectUtils.isEmpty(placementValueDtoList)) {
			placementValueDtoList = new ArrayList<>();
		}

		return placementValueDtoList;
	}

	/**
	 * Method Name: childLastestPlcmtSSCC Method Description:This method gets
	 * child latest placement sscc
	 *
	 * @param stageId
	 * @param dtPlcmtStart
	 * @return PlacementValueDto @
	 */

	@Override
	public PlacementValueDto childLastestPlcmtSSCC(Long stageId, Date dtPlcmtStart) {

		List<PlacementValueDto> placementValueDtoList = null;
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(childLastestPlcmtSSCC)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).addScalar("idRsrcAgency", StandardBasicTypes.LONG)
				.addScalar("idRsrcFacil", StandardBasicTypes.LONG).setParameter("idStage", stageId)
				.setParameter("dtPlcmtEnd", dtPlcmtStart)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));

		placementValueDtoList = (List<PlacementValueDto>) sQLQuery1.list();

		if (ObjectUtils.isEmpty(placementValueDtoList)) {
			PlacementValueDto placementValueDto = new PlacementValueDto();
			return placementValueDto;
		}
		return placementValueDtoList.get(ServiceConstants.Zero);
	}

	/**
	 * Method Name: getChildPlanInitiateInfo Method Description:This method gets
	 * the latest child plan initiate info within a stage at the time of
	 * approval of an sscc placement
	 *
	 * @param idReferral
	 * @return PlacementValueDto
	 */

	@Override
	public PlacementValueDto getChildPlanInitiateInfo(Long idReferral) {

		List<PlacementValueDto> placementValueDtoList = null;
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getChildPlanInitiateInfo)
				.addScalar("dtChildPlanInitiated", StandardBasicTypes.DATE)
				.addScalar("dtChildPlanDue", StandardBasicTypes.DATE)
				.addScalar("cdChildPlanDue", StandardBasicTypes.STRING).setParameter("idSsccReferral", idReferral)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));

		placementValueDtoList = (List<PlacementValueDto>) sQLQuery1.list();

		/*
		 * if (ObjectUtils.isEmpty(placementValueDtoList)) { throw new
		 * DataNotFoundException( messageSource.getMessage(
		 * "PlacementDao.getChildPlanInitiateInfo.NotFound", null, Locale.US));
		 * }
		 */
		return placementValueDtoList.get(ServiceConstants.Zero);
	}

	/**
	 * Method Name: getExcpCareDaysUsed Method Description:This method gets the
	 * number of exceptional care used in a contract period
	 *
	 * @param idReferral
	 * @return Long @
	 */
	@Override
	public Long getExcpCareDaysUsed(Long idReferral) {

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getExcpCareDaysUsed)
				.setParameter("idSsccReferral", idReferral);

		BigDecimal expCareDaysUsed = (BigDecimal) sQLQuery1.uniqueResult();
		return !TypeConvUtil.isNullOrEmptyBdDecm(expCareDaysUsed) ? expCareDaysUsed.longValue()
				: ServiceConstants.ZERO_VAL;
	}

	/**
	 * Method Name: findActivePlacementsForEligibilty Method Description:Fetches
	 * the most recent open Active Placement for the idPerson
	 *
	 * @param idPerson
	 * @return List<PlacementDto>
	 */
	@Override
	public PlacementDto findActivePlacementsForEligibilty(Long idPerson) {
		List<PlacementDto> placementValueDtoList = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(findActivePlacementsForEligibiltySql)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
				.addScalar("indPlcmtEmerg", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		placementValueDtoList = (List<PlacementDto>) query.list();
		if (ObjectUtils.isEmpty(placementValueDtoList)) {
			return null;
		}
		return placementValueDtoList.get(0);
	}

	/**
	 * Method Name: findActivePlacementsForFosterCare Method Description: This
	 * method is used to find ActivePlacements For FosterCare
	 *
	 * @param idPerson
	 * @return List<PlacementDto>
	 */

	@Override
	public PlacementValueDto findActivePlacement(Long idPerson) {

		List<PlacementValueDto> resultList = sessionFactory.getCurrentSession().createSQLQuery(findPlacementSql)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
				.addScalar("indPlcmtEmerg", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setParameter("idPlcmtActPlanned", ServiceConstants.CD_PLCMT_ACT_PLANNED)
				.setParameter("idStageClosed", ServiceConstants.N)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class)).list();
		if (!CollectionUtils.isEmpty(resultList)) {
			return resultList.get(0);
		}
		return null;
	}

	/**
	 * Method Name: findRecentPlacements Method Description: This method is used
	 * to find Recent Placements
	 *
	 * @param idStage
	 * @return List<PlacementDto>
	 */
	@Override
	public List<PlacementDto> findRecentPlacements(Long idStage) {

		List<PlacementDto> placementValueDtoList = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(findRecentPlacementsSql)
				.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
				.addScalar("dtEventCreated", StandardBasicTypes.DATE)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("txtEventDescr", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		placementValueDtoList = (List<PlacementDto>) query.list();
		if (ObjectUtils.isEmpty(placementValueDtoList)) {
			return null;
		}
		return placementValueDtoList;

	}

	/**
	 * Method Name: findActivePlacementsForFosterCare Method Description: This
	 * method is used to find ActivePlacements For FosterCare
	 *
	 * @param idPerson
	 * @return List<PlacementDto>
	 */
	@Override
	public List<PlacementDto> findActivePlacementsForFosterCare(Long idPerson) {
		List<PlacementDto> placementValueDtoList = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(findActivePlacementsForEligibiltySql)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
				.addScalar("indPlcmtEmerg", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		placementValueDtoList = (List<PlacementDto>) query.list();
		if (ObjectUtils.isEmpty(placementValueDtoList)) {
			return Collections.EMPTY_LIST;
		}
		return placementValueDtoList;

	}

	/**
	 *
	 * Method Name: findOtherChildInPlacementCount (DAm Name : CLSS01D ) Method
	 * Description:This Dam returns the count of other children with palcements
	 * for a given idRsrcFacil and idPlcmtChild
	 *
	 * @param resourceServiceInDto
	 * @return
	 */
	@Override
	public CommonCountRes findOtherChildInPlacementCount(ResourceServiceInDto resourceServiceInDto) {
		CommonCountRes resp = new CommonCountRes();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(findOtherChildInPlacementCount)
				.addScalar("count", StandardBasicTypes.LONG)
				.setParameter("idRsrcFacil", resourceServiceInDto.getIdRsrcFacil())
				.setParameter("idPlcmtChild", resourceServiceInDto.getIdPlcmtChild());
		Long count = (Long) query.uniqueResult();
		resp.setCount(count);
		return resp;
	}

	/**
	 * Method Name: getPlacementsByChildId Method Description: This method is
	 * used to getPlacements By ChildId
	 *
	 * @param resourceServiceInDto
	 * @return List<PlacementDto>
	 */
	@Override
	public List<PlacementDto> getPlacementsByChildId(ResourceServiceInDto resourceServiceInDto) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPlacementsByChildId)
				.addScalar("idPlcmtChild", StandardBasicTypes.LONG).addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("dtPlacementLastUpdate", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE).addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtRemovalRsn", StandardBasicTypes.STRING).addScalar("stageId", StandardBasicTypes.LONG)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("dtStageClosed", StandardBasicTypes.DATE)
				.addScalar("personId", StandardBasicTypes.LONG).addScalar("txtEventDescr", StandardBasicTypes.STRING)
				.addScalar("cdEventType", StandardBasicTypes.STRING).addScalar("cdTask", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("dtEventLastUpdate", StandardBasicTypes.DATE)
				.setParameter("idPlcmtChild", resourceServiceInDto.getIdPlcmtChild())
				.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		return (List<PlacementDto>) query.list();

	}

	@Override
	public List<PlacementDto> getPlacementDetailsByChildId(Long childId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPlacementDetailsByChildId)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("nmPlcmtFacil", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtPersonFull", StandardBasicTypes.STRING)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE).addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING)
				.setParameter("idPlcmtChild", childId)
				.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		return (List<PlacementDto>) query.list();
	}

	// CLSCE9D
	/**
	 * Method Name: getDistinctService Method Description: This method is used
	 * to getDistinctService
	 *
	 * @param placementDtlGpDto
	 * @return String
	 */
	@Override
	public String getDistinctService(PlacementDtlGpDto placementDtlGpDto) {

		String cntyService = null;

		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getDistinctServiceSql)
				.addScalar("cntyService", StandardBasicTypes.STRING)
				.setParameter("idContract", placementDtlGpDto.getIdContract())
				.setParameter("cdCncntyCounty", placementDtlGpDto.getAddrPlcmtCnty())
				.setParameter("dtScrDtCurrentDate", placementDtlGpDto.getDtPlcmtStart())
				.setParameter("cdCnperStatusAct", ServiceConstants.CNPER_STATUS_ACT)
				.setParameter("cdCnperStatusCls", ServiceConstants.CNPER_STATUS_CLS)
				.setParameter("cdCnperStatusClt", ServiceConstants.CNPER_STATUS_CLT)
				.setParameter("cdCnperStatusPnt", ServiceConstants.CNPER_STATUS_PNT)
				.setParameter("cdCnperStatusPyh", ServiceConstants.CNPER_STATUS_PYH)
				.setParameter("cdCnperStatusSvh", ServiceConstants.CNPER_STATUS_SVH));

		cntyService = sQLQuery.toString();
		return cntyService;
	}

	/**
	 * Method Name: getContractDtl Method Description: Retrieves CONTRACT IDs
	 * for the passed Resource ID
	 *
	 * DAM Name: CLSS67D Service Name: CSUB26S
	 *
	 * @param idResourse
	 *            - The resource for which the Contacts should be fetched
	 * @return List<ContractDto> - The List of Contracts for the Resource
	 */
	// CLSS67D
	@Override
	public List<ContractDto> getContractDtl(Long idResourse) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getContractDtlSql)
				.addScalar("idContract", StandardBasicTypes.LONG).addScalar("idContractWkr", StandardBasicTypes.LONG)
				.addScalar("idContractMngr", StandardBasicTypes.LONG).addScalar("idResourse", StandardBasicTypes.LONG)
				.addScalar("idRsrcAddress", StandardBasicTypes.LONG)
				.addScalar("cdCntrctFuncType", StandardBasicTypes.STRING)
				.addScalar("cdCntrctProgramType", StandardBasicTypes.STRING)
				.addScalar("cdCntrctProcureType", StandardBasicTypes.STRING)
				.addScalar("cdCntrctRegion", StandardBasicTypes.STRING)
				.addScalar("indCntrctBudgLimit", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).setParameter("idResourse", idResourse)
				.setResultTransformer(Transformers.aliasToBean(ContractDto.class));
		return (List<ContractDto>) query.list();
	}

	/**
	 * Method Name: getContractPeriodByIdContract MEthod Description: Retrieves
	 * CONTRACT based on id_contract from CONTRACT_PERIOD table
	 *
	 * DAM Name: CSES80D Service Name: CCMN35S
	 *
	 * @param idContract
	 * @return List<ContractPeriod>
	 */

	@Override
	public List<ContractPeriodDto> getContractPeriodByIdContract(Long idContract) {
		List<ContractPeriodDto> contractPeriodList = null;
		contractPeriodList = (List<ContractPeriodDto>) sessionFactory.getCurrentSession()
				.createCriteria(ContractPeriod.class)
				.add(Restrictions.eq("id.idContract", idContract))
				.add(Restrictions.ge("dtCnperClosure", Calendar.getInstance().getTime()))
				.setProjection(Projections.projectionList().add(Projections.property("id.idContract").as("idContract"))
						.add(Projections.property("id.nbrCnperPeriod").as("idContractPeriod"))
						.add(Projections.property("person.idPerson").as("idContractWorker"))
						.add(Projections.property("contract.idContract").as("idContract"))
						.add(Projections.property("cdCnperStatus").as("cdCnperStatus"))
						.add(Projections.property("dtCnperStart").as("dtCnperStart"))
						.add(Projections.property("dtCnperTerm").as("dtCnperTerm"))
						.add(Projections.property("dtCnperClosure").as("dtCnperClosure"))
						.add(Projections.property("indCnperRenewal").as("indCnperRenewal"))
						.add(Projections.property("indCnperSigned").as("indCnperSigned"))
						.add(Projections.property("dtLastUpdate").as("dtLastUpdate"))
						.add(Projections.property("nbrLegalIdentifier").as("nbrLegalIdentifier"))
						.add(Projections.property("txtProcureNbr").as("txtProcureNbr"))
						.add(Projections.property("indCnperPriorClos").as("indCnperPriorClos")))
				.setResultTransformer(Transformers.aliasToBean(ContractPeriodDto.class)).list();
		return contractPeriodList;
	}

	// CLSSB1D
	/**
	 * Method Name: getContractSignedOrNot Method Description: This method is
	 * used to check whether Contract is Signed or Not
	 *
	 * @param placementDtlGpDto
	 * @return Long
	 */
	@Override
	public Long getContractSignedOrNot(PlacementDtlGpDto placementDtlGpDto) {
		Long idContract = 0L;

		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContractSignedOrNotSql)
				.addScalar("ID_CONTRACT", StandardBasicTypes.LONG)
				.setParameter("idContract", placementDtlGpDto.getIdContract())
				.setParameter("dtPlcmtStart", placementDtlGpDto.getDtPlcmtStart())
				.setParameter("cdCnperStatusAct", ServiceConstants.CNPER_STATUS_ACT)
				.setParameter("cdCnperStatusCls", ServiceConstants.CNPER_STATUS_CLS)
				.setParameter("cdCnperStatusClt", ServiceConstants.CNPER_STATUS_CLT)
				.setParameter("cdCnperStatusPnt", ServiceConstants.CNPER_STATUS_PNT)
				.setParameter("cdCnperStatusPyh", ServiceConstants.CNPER_STATUS_PYH)
				.setParameter("cdCnperStatusSvh", ServiceConstants.CNPER_STATUS_SVH));

		idContract = (Long) sQLQuery.uniqueResult();
		return idContract;
	}

	// CSES16D
	/**
	 * Method Name: getEligibilityDtl Method Description: This method is used to
	 * get Eligibility Detail
	 *
	 * @param idEvent
	 * @return EligibilityDto
	 */
	@Override
	public EligibilityDto getEligibilityDtl(Long idEvent) {
		EligibilityDto eligibilityDto = (EligibilityDto) sessionFactory.getCurrentSession()
				.createCriteria(Eligibility.class, "eligibility").add(Restrictions.eq("idEligEvent", idEvent))
				.setProjection(Projections.projectionList().add(Projections.property("idEligEvent").as("idEligEvent"))
						.add(Projections.property("personByIdPerson.idPerson").as("idPerson"))
						.add(Projections.property("personByIdPersonUpdate.idPerson").as("idPersonUpdate"))
						.add(Projections.property("cdEligActual").as("cdEligActual"))
						.add(Projections.property("cdEligCsupQuest1").as("cdEligCsupQuest1"))
						.add(Projections.property("cdEligCsupQuest2").as("cdEligCsupQuest2"))
						.add(Projections.property("cdEligCsupQuest3").as("cdEligCsupQuest3"))
						.add(Projections.property("cdEligCsupQuest4").as("cdEligCsupQuest4"))
						.add(Projections.property("cdEligCsupQuest5").as("cdEligCsupQuest5"))
						.add(Projections.property("cdEligCsupQuest6").as("cdEligCsupQuest6"))
						.add(Projections.property("cdEligCsupQuest7").as("cdEligCsupQuest7"))
						.add(Projections.property("cdEligMedEligGroup").as("cdEligMedEligGroup"))
						.add(Projections.property("cdEligSelected").as("cdEligSelected"))
						.add(Projections.property("dtEligCsupReferral").as("dtEligCsupReferral"))
						.add(Projections.property("dtEligEnd").as("dtEligEnd"))
						.add(Projections.property("dtEligReview").as("dtEligReview"))
						.add(Projections.property("dtEligStart").as("dtEligStart"))
						.add(Projections.property("indEligCsupSend").as("indEligCsupSend"))
						.add(Projections.property("indEligWriteHistory").as("indEligWriteHistory"))
						.add(Projections.property("txtEligComment").as("txtEligComment")))
				.setResultTransformer(Transformers.aliasToBean(EligibilityDto.class)).uniqueResult();

		return eligibilityDto;
	}

	// CSEC86D
	/**
	 * Method Name: getWorkloadDtl Method Description: This method is used to
	 * getWorkloadDtl
	 *
	 * @param idStage
	 * @return WorkloadDto
	 */
	@Override
	public WorkloadDto getWorkloadDtl(Long idStage) {
		WorkloadDto workloadDto = null;
		workloadDto = (WorkloadDto) sessionFactory.getCurrentSession().createCriteria(Workload.class, "workload")
				.add(Restrictions.eq("id.idWkldStage", idStage))
				.setProjection(
						Projections.projectionList().add(Projections.property("id.idWkldPerson").as("idWkldPerson"))
								.add(Projections.property("id.idWkldStage").as("idWkldStage"))
								.add(Projections.property("id.dtLastUpdate").as("dtLastUpdate"))
								.add(Projections.property("id.idWkldCase").as("idWkldCase"))
								.add(Projections.property("id.cdWkldStagePersRole").as("cdWkldStagePersRole"))
								.add(Projections.property("id.dtWkldStagePersLink").as("dtWkldStagePersLink"))
								.add(Projections.property("id.indWkldStagePersNew").as("indWkldStagePersNew"))
								.add(Projections.property("id.nmWkldStage").as("nmWkldStage"))
								.add(Projections.property("id.cdWkldStage").as("cdWkldStage"))
								.add(Projections.property("id.cdWkldStageCnty").as("cdWkldStageCnty"))
								.add(Projections.property("id.cdWkldStageType").as("cdWkldStageType"))
								.add(Projections.property("id.cdWkldStageRegion").as("cdWkldStageRegion"))
								.add(Projections.property("id.cdWkldStageRsnCls").as("cdWkldStageRsnCls"))
								.add(Projections.property("id.cdWkldStageProgram").as("cdWkldStageProgram"))
								.add(Projections.property("id.idWkldUnit").as("idWkldUnit"))
								.add(Projections.property("id.nbrWkldUnit").as("wkldUnit"))
								.add(Projections.property("id.nmWkldCase").as("nmWkldCase"))
								.add(Projections.property("id.indWkldCaseSensitive").as("indWkldCaseSensitive")))
				.setResultTransformer(Transformers.aliasToBean(WorkloadDto.class)).uniqueResult();

		return workloadDto;
	}

	/**
	 * Method Name: getDtActiveADO Method Description: This method will retrieve
	 * a date of active ADO from the ADoptionSUB (DAM CSUB89D from legacy Tuxedo
	 * Code)
	 *
	 * @param idEvent
	 * @return PlacementDtlGpDto
	 */
	@Override
	public PlacementDtlGpDto getDtActiveADO(Long idEvent) {
		PlacementDtlGpDto placementDtlGpDto = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getDtActiveADOSql)
				.addScalar("dtAdptSubEnd", StandardBasicTypes.DATE)
				.addScalar("dtAdptSubEffective", StandardBasicTypes.DATE).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(PlacementDtlGpDto.class));
		placementDtlGpDto = (PlacementDtlGpDto) query.uniqueResult();
		return placementDtlGpDto;
	}

	// CSUB87D
	/**
	 * Method Name: updateOpenDDPlacement Method Description: This method
	 * Updates PLACEMENT table to close an open DA/DD placement by populating
	 * the placement closure reason and placement end date.
	 *
	 * @param placementDtoList
	 * @param idPerson
	 * @return String
	 */
	@Override
	public String updateOpenDDPlacement(List<PlacementDto> placementDtoList, Long idPerson) {
		String message = "";
		for (PlacementDto placementDto : placementDtoList) {
			Placement placement = (Placement) sessionFactory.getCurrentSession().load(Placement.class,
					(placementDto.getIdPlcmtEvent()));
			placement.setCdPlcmtRemovalRsn(placementDto.getCdPlcmtRemovalRsn());
			placement.setDtPlcmtEnd(placementDto.getDtPlcmtEnd());
			placement.setIdLastUpdatePerson(idPerson);
			sessionFactory.getCurrentSession().saveOrUpdate(placement);
		}
		message = ServiceConstants.SUCCESS;
		return message;
	}

	/**
	 * Method Name: getStageCount Method Description: This method is used to
	 * getStageCount
	 *
	 * @param idStage
	 * @return Long
	 */

	@Override
	public Long getStageCount(Long idStage) {

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getEventStageCountSql)
				.setParameter("idEventStage", idStage);
		Long count = ((BigDecimal) query.uniqueResult()).longValue();
		return count;
	}

	/**
	 * Method Name: getPlacementEventList Method Description: This method is
	 * used to get Placement EventList
	 *
	 * @param idPlacementChild
	 * @param cdPlacementActPlanned
	 * @return List<Long>
	 */
	@Override
	public List<Long> getPlacementEventList(Long idPlacementChild, String cdPlacementActPlanned) {

		Query queryIdPerson = (Query) sessionFactory.getCurrentSession().createSQLQuery(checkRecIdPersonSql)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).setParameter("idPlcmtChild", idPlacementChild)
				.setParameter("cdPlcmtActPlanned", cdPlacementActPlanned);
		return queryIdPerson.list();

	}

	/**
	 * Method Name: getEventStageCount Method Description: This method is used
	 * to getEventStageCount
	 *
	 * @param idStage
	 * @return Long
	 */
	@Override
	public Long getEventStageCount(Long idStage) {
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getEventStageCountSql)
				.setParameter("idEventStage", idStage);
		Long count = ((BigDecimal) query.uniqueResult()).longValue();

		return count;
	}

	/**
	 * Method Name: checkOpenPlacement Method Description: This method is used
	 * to checkOpenPlacement
	 *
	 * @param placementAUDDto
	 * @return Long
	 */
	@Override
	public Long checkOpenPlacement(PlacementAUDDto placementAUDDto) {
		Query queryOpenPlacementChld = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkOpenPlacementChldSql)
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned()));
		Long openPlacementChldCount = ((BigDecimal) queryOpenPlacementChld.uniqueResult()).longValue();
		return openPlacementChldCount;
	}

	/**
	 * Method Name: checkOtherOpenPlacement Method Description: This method is
	 * used to checkOtherOpenPlacement
	 *
	 * @param placementAUDDto
	 * @return Long
	 */
	@Override
	public Long checkOtherOpenPlacement(PlacementAUDDto placementAUDDto) {
		Query queryOpenPlacementChldDiffCases = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(OpenPlacementChldDiffCasesSql).setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned()));
		Long openPlacementChldDiffCasesCount = ((BigDecimal) queryOpenPlacementChldDiffCases.uniqueResult())
				.longValue();
		return openPlacementChldDiffCasesCount;
	}

	/**
	 * Method Name: checkLeftOverlaps Method Description: This method is used to
	 * checkLeftOverlaps
	 *
	 * @param placementAUDDto
	 * @return List<Long>
	 */
	@Override
	public List<Long> checkLeftOverlaps(PlacementAUDDto placementAUDDto) {
		Query queryNewRecOverlaps = ((Query) sessionFactory.getCurrentSession().createSQLQuery(checkNewRecOverlapsSql)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned())
				.setParameter("dtPlcmtStart", placementAUDDto.getDtPlcmtStart())
				.setParameter("dtPlcmtEnd", placementAUDDto.getDtPlcmtEnd()));

		return (List<Long>) queryNewRecOverlaps.list();

	}

	/**
	 * Method Name: getOpenPlacementInOpenStages Method Description: This method
	 * is used to getOpenPlacementInOpenStages
	 *
	 * @param idPlacemntEvent
	 * @return Long
	 */
	@Override
	public Long getOpenPlacementInOpenStages(Long idPlacemntEvent) {
		Query queryOpenPlcmtOpenStage = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkOpenPlcmtOpenStageSql).setParameter("idPlcmtEvent", idPlacemntEvent));
		Long openPlacementsCount = ((BigDecimal) queryOpenPlcmtOpenStage.uniqueResult()).longValue();
		return openPlacementsCount;
	}

	/**
	 * Method Name: getOpenPlacementInClosedStages Method Description: This
	 * method is used to getOpenPlacementInClosedStages
	 *
	 * @param idPlacemntEvent
	 * @return Long
	 */
	@Override
	public Long getOpenPlacementInClosedStages(Long idPlacemntEvent) {
		Query queryOpenPlcmtCloseStage = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkOpenPlcmtCloseStageSql).setParameter("idPlcmtEvent", idPlacemntEvent));
		Long openPlcmtCloseStageCount = ((BigDecimal) queryOpenPlcmtCloseStage.uniqueResult()).longValue();
		return openPlcmtCloseStageCount;
	}

	/**
	 * Method Name: getOverlapingRecords Method Description: This method is used
	 * to getOverlapingRecords
	 *
	 * @param placementAUDDto
	 * @return List<Long>
	 */
	@Override
	public List<Long> checkRightOverlaps(PlacementAUDDto placementAUDDto) {
		Query queryNewRecOverlapsOtherRec = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkNewRecOverlapsOtherRecSql).addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned())
				.setParameter("dtPlcmtStart", placementAUDDto.getDtPlcmtStart())
				.setParameter("dtPlcmtEnd", placementAUDDto.getDtPlcmtEnd()));
		List<Long> idPlacmntOverlapsOtherRec = (List<Long>) queryNewRecOverlapsOtherRec.list();
		return idPlacmntOverlapsOtherRec;
	}

	/**
	 * Method Name: getIdenticalRecords Method Description: This method is used
	 * to getIdenticalRecords
	 *
	 * @param placementAUDDto
	 * @return List<Long>
	 */
	@Override
	public List<Long> getIdenticalRecords(PlacementAUDDto placementAUDDto) {
		Query queryNewRecIdentical = ((Query) sessionFactory.getCurrentSession().createSQLQuery(checkNewRecIdenticalSql)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned())
				.setParameter("dtPlcmtStart", placementAUDDto.getDtPlcmtStart())
				.setParameter("dtPlcmtEnd", placementAUDDto.getDtPlcmtEnd()));
		List<Long> idPlacmntNewRecIdentical = (List<Long>) queryNewRecIdentical.list();
		return idPlacmntNewRecIdentical;
	}

	/**
	 * Method Name: getPlacmentsStartingNextDay Method Description: This method
	 * is used to getPlacmentsStartingNextDay
	 *
	 * @param placementAUDDto
	 * @return List<PlacementDto>
	 */
	@Override
	public List<PlacementDto> checkLeftGaps(PlacementAUDDto placementAUDDto) {
		Query queryDtPlcmtStartOneDayMore = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkDtPlcmtStartOneDayMoreSql).addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("dtPlcmtEnd", StandardBasicTypes.DATE).addScalar("plcmtDiffDate", StandardBasicTypes.INTEGER)
				.setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned())
				.setDate("dtPlcmtStart", placementAUDDto.getDtPlcmtStart()))
						.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		List<PlacementDto> placementsWithLeftGap = ((List<PlacementDto>) queryDtPlcmtStartOneDayMore.list());
		return placementsWithLeftGap;
	}

	/**
	 * Method Name: getPlacmentsStartingPreviousDay Method Description: This
	 * method is used to getPlacmentsStartingPreviousDay
	 *
	 * @param placementAUDDto
	 * @return List<PlacementDto>
	 */
	@Override
	public List<PlacementDto> checkRightGaps(PlacementAUDDto placementAUDDto) {
		Query queryDtPlcmtStartRightOneDayMore = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkDtPlcmtStartRightOneDayMoreSql).addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("dtPlcmtEnd", StandardBasicTypes.DATE).addScalar("plcmtDiffDate", StandardBasicTypes.INTEGER)
				.setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned())
				.setDate("dtPlcmtEnd", placementAUDDto.getDtPlcmtEnd()))
						.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		List<PlacementDto> placementsWithRightGap = (List<PlacementDto>) queryDtPlcmtStartRightOneDayMore.list();
		return placementsWithRightGap;
	}

	/**
	 * Method Name: savePlacement Method Description: This method is used to
	 * savePlacement
	 *
	 * @param placementAUDDto
	 * @return Long
	 */
	@Override
	public Long savePlacement(PlacementAUDDto placementAUDDto) {
		Placement placement = new Placement();

		if (!ObjectUtils.isEmpty(placementAUDDto)) {
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtPermEff())) {
				placement.setDtPlcmtPermEff(placementAUDDto.getDtPlcmtPermEff());
			}

			if (!ObjectUtils.isEmpty(placementAUDDto.getIndTrashBags())) {
				placement.setIndTrashBags(placementAUDDto.getIndTrashBags());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getTxtTrashBags())) {
				placement.setTxtTrashBags(placementAUDDto.getTxtTrashBags());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIdPlcmtEvent())) {
				placement.setIdPlcmtEvent(placementAUDDto.getIdPlcmtEvent());
			}
			placement.setDtLastUpdate(new Date());
			if (!StringUtils.isEmpty(placementAUDDto.getIdPlcmtAdult())) {
				Person personByAdult = personDao.getPersonEntity(placementAUDDto.getIdPlcmtAdult());
				placement.setPersonByIdPlcmtAdult(personByAdult);
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIdPlcmtChild())) {
				Person personByChild = personDao.getPersonEntity(placementAUDDto.getIdPlcmtChild());
				if (!ObjectUtils.isEmpty(personByChild)) {
					placement.setPersonByIdPlcmtChild(personByChild);
				}
			}

			if (!ObjectUtils.isEmpty(placementAUDDto.getIdContract())) {
				Contract idContact = contractDao.getContractById(placementAUDDto.getIdContract());

				if (!ObjectUtils.isEmpty(idContact)) {
					placement.setContract(idContact);
				}
			}

			if (!ObjectUtils.isEmpty(placementAUDDto.getIdRsrcAgency())) {
				CapsResource idCapsResource = capsResourceDao.getCapsResourceById(placementAUDDto.getIdRsrcAgency());
				if (!ObjectUtils.isEmpty(idCapsResource)) {
					placement.setCapsResourceByIdRsrcAgency(idCapsResource);
				}
			} else {
				placement.setCapsResourceByIdRsrcAgency(null);
			}

			if (!ObjectUtils.isEmpty(placementAUDDto.getIdRsrcFacil())) {
				CapsResource idCapsResourceFacil = capsResourceDao
						.getCapsResourceById(placementAUDDto.getIdRsrcFacil());

				if (!ObjectUtils.isEmpty(idCapsResourceFacil)) {
					placement.setCapsResourceByIdRsrcFacil(idCapsResourceFacil);
				}
			} else {
				placement.setCapsResourceByIdRsrcFacil(null);
			}

			if (!ObjectUtils.isEmpty(placementAUDDto.getAddrPlcmtCity())) {
				placement.setAddrPlcmtCity(placementAUDDto.getAddrPlcmtCity());
			}
			placement.setDtPlcmtLastPrebill(new Date());
			if (!ObjectUtils.isEmpty(placementAUDDto.getAddrPlcmtCnty())) {
				placement.setAddrPlcmtCnty(placementAUDDto.getAddrPlcmtCnty());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getAddrPlcmtLn1())) {
				placement.setAddrPlcmtLn1(placementAUDDto.getAddrPlcmtLn1());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getAddrPlcmtLn2())) {
				placement.setAddrPlcmtLn2(placementAUDDto.getAddrPlcmtLn2());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getAddrPlcmtSt())) {
				placement.setAddrPlcmtSt(placementAUDDto.getAddrPlcmtSt());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getAddrPlcmtZip())) {
				placement.setAddrPlcmtZip(placementAUDDto.getAddrPlcmtZip());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo1())) {
				placement.setCdPlcmtInfo1(placementAUDDto.getCdPlcmtInfo1());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo2())) {
				placement.setCdPlcmtInfo2(placementAUDDto.getCdPlcmtInfo2());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo3())) {
				placement.setCdPlcmtInfo3(placementAUDDto.getCdPlcmtInfo3());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo4())) {
				placement.setCdPlcmtInfo4(placementAUDDto.getCdPlcmtInfo4());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo5())) {
				placement.setCdPlcmtInfo5(placementAUDDto.getCdPlcmtInfo5());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo6())) {
				placement.setCdPlcmtInfo6(placementAUDDto.getCdPlcmtInfo6());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo7())) {
				placement.setCdPlcmtInfo7(placementAUDDto.getCdPlcmtInfo7());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo8())) {
				placement.setCdPlcmtInfo8(placementAUDDto.getCdPlcmtInfo8());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo9())) {
				placement.setCdPlcmtInfo9(placementAUDDto.getCdPlcmtInfo9());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo10())) {
				placement.setCdPlcmtInfo10(placementAUDDto.getCdPlcmtInfo10());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo11())) {
				placement.setCdPlcmtInfo11(placementAUDDto.getCdPlcmtInfo11());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo12())) {
				placement.setCdPlcmtInfo12(placementAUDDto.getCdPlcmtInfo12());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo13())) {
				placement.setCdPlcmtInfo13(placementAUDDto.getCdPlcmtInfo13());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo14())) {
				placement.setCdPlcmtInfo14(placementAUDDto.getCdPlcmtInfo14());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo15())) {
				placement.setCdPlcmtInfo15(placementAUDDto.getCdPlcmtInfo15());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo16())) {
				placement.setCdPlcmtInfo16(placementAUDDto.getCdPlcmtInfo16());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo17())) {
				placement.setCdPlcmtInfo17(placementAUDDto.getCdPlcmtInfo17());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo18())) {
				placement.setCdPlcmtInfo18(placementAUDDto.getCdPlcmtInfo18());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo19())) {
				placement.setCdPlcmtInfo19(placementAUDDto.getCdPlcmtInfo19());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo20())) {
				placement.setCdPlcmtInfo20(placementAUDDto.getCdPlcmtInfo20());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtLivArr())) {
				placement.setCdPlcmtLivArr(placementAUDDto.getCdPlcmtLivArr());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtRemovalRsn())) {
				placement.setCdPlcmtRemovalRsn(placementAUDDto.getCdPlcmtRemovalRsn());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtActPlanned())) {
				placement.setCdPlcmtActPlanned(placementAUDDto.getCdPlcmtActPlanned());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtType())) {
				placement.setCdPlcmtType(placementAUDDto.getCdPlcmtType());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtService())) {
				placement.setCdPlcmtService(placementAUDDto.getCdPlcmtService());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtCaregvrDiscuss())) {
				placement.setDtPlcmtCaregvrDiscuss(placementAUDDto.getDtPlcmtCaregvrDiscuss());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtChildDiscuss())) {
				placement.setDtPlcmtChildDiscuss(placementAUDDto.getDtPlcmtChildDiscuss());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtChildPlan())) {
				placement.setDtPlcmtChildPlan(placementAUDDto.getDtPlcmtChildPlan());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtEducLog())) {
				placement.setDtPlcmtEducLog(placementAUDDto.getDtPlcmtEducLog());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtEnd())) {
				placement.setDtPlcmtEnd(placementAUDDto.getDtPlcmtEnd());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtSxVctmztnHistoryDiscuss())) {
				placement.setDtSxVctmztnHistoryDiscuss(placementAUDDto.getDtSxVctmztnHistoryDiscuss());
			}
			// artf176932 - Add NA checkbox for Sexual History Attachment A
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndSxVctmztnHistoryDiscuss())) {
				placement.setIndSxVctmztnHistoryDiscuss(placementAUDDto.getIndSxVctmztnHistoryDiscuss());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtMeddevHistory())) {
				placement.setDtPlcmtMeddevHistory(placementAUDDto.getDtPlcmtMeddevHistory());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtParentsNotif())) {
				placement.setDtPlcmtParentsNotif(placementAUDDto.getDtPlcmtParentsNotif());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtPreplaceVisit())) {
				placement.setDtPlcmtPreplaceVisit(placementAUDDto.getDtPlcmtPreplaceVisit());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtSchoolRecords())) {
				placement.setDtPlcmtSchoolRecords(placementAUDDto.getDtPlcmtSchoolRecords());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtStart())) {
				placement.setDtPlcmtStart(placementAUDDto.getDtPlcmtStart());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndPlcmtContCntct())) {
				placement.setIndPlcmtContCntct(placementAUDDto.getIndPlcmtContCntct());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndPlcmtEducLog())) {
				placement.setIndPlcmtEducLog(placementAUDDto.getIndPlcmtEducLog());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndPlcmetEmerg())) {
				placement.setIndPlcmtEmerg(placementAUDDto.getIndPlcmetEmerg());
			}
			// artf255991 : BR 4.13 T3C Placement Indicator Logic - New Placement Save
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndT3CPlcmet())) {
				placement.setIndT3CPlcmt(placementAUDDto.getIndT3CPlcmet());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndPlcmtNotApplic())) {
				placement.setIndPlcmtNotApplic(placementAUDDto.getIndPlcmtNotApplic());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndPlcmtSchoolDoc())) {
				placement.setIndPlcmtSchoolDoc(placementAUDDto.getIndPlcmtSchoolDoc());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getPlcmtPhoneExt())) {
				placement.setNbrPlcmtPhoneExt(placementAUDDto.getPlcmtPhoneExt());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getPlcmtTelephone())) {
				placement.setNbrPlcmtTelephone(placementAUDDto.getPlcmtTelephone());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getPlcmtAgency())) {
				placement.setNmPlcmtAgency(placementAUDDto.getPlcmtAgency());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getPlcmtContact())) {
				placement.setNmPlcmtContact(placementAUDDto.getPlcmtContact());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getPlcmtFacil())) {
				placement.setNmPlcmtFacil(placementAUDDto.getPlcmtFacil());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getPlcmtPersonFull())) {
				placement.setNmPlcmtPersonFull(placementAUDDto.getPlcmtPersonFull());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndPlcmtWriteHistory())) {
				placement.setIndPlcmtWriteHistory(placementAUDDto.getIndPlcmtWriteHistory());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getTxtPlcmtAddrComment())) {
				placement.setTxtPlcmtAddrComment(placementAUDDto.getTxtPlcmtAddrComment());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getTxtPlcmtDiscussion())) {
				placement.setTxtPlcmtDiscussion(placementAUDDto.getTxtPlcmtDiscussion());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getTxtPlcmtDocuments())) {
				placement.setTxtPlcmtDocuments(placementAUDDto.getTxtPlcmtDocuments());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getTxtPlcmtRemovalRsn())) {
				placement.setTxtPlcmtRemovalRsn(placementAUDDto.getTxtPlcmtRemovalRsn());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtRemovalRsnSubtype())) {
				placement.setCdRmvlRsnSubtype(placementAUDDto.getCdPlcmtRemovalRsnSubtype());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIdLastUpdatePerson())) {
				placement.setIdLastUpdatePerson(placementAUDDto.getIdLastUpdatePerson());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIdCreatedPerson())) {
				placement.setIdCreatedPerson(placementAUDDto.getIdCreatedPerson());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIdRsrcSSCC())) {
				placement.setIdRsrcSscc(placementAUDDto.getIdRsrcSSCC());
				if (ServiceConstants.ZERO.equals(placementAUDDto.getIdRsrcSSCC()))
					placement.setIdRsrcSscc(null);
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getNmPlcmtSSCC())) {
				placement.setNmPlcmtSscc(placementAUDDto.getNmPlcmtSSCC());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndPlcmtStartEndDtDiff())) {
				placement.setIndPlcmtLessThan24Hrs(placementAUDDto.getIndPlcmtStartEndDtDiff());
			}
			// FFPSA: BR 138 : Set CongregateCare Indicator
			placement.setIndCongregateCare(placementAUDDto.getIndCongregateCare());
		}
		Long idPlacement = (Long) sessionFactory.getCurrentSession().save(placement);

		return idPlacement;

	}

	/**
	 * Method Name: getPlacementForUpdate Method Description: This method is
	 * used to getPlacementForUpdate
	 *
	 * @param placementAUDDto
	 * @return PlacementDto
	 */
	@Override
	public PlacementDto getPlacementForUpdate(PlacementAUDDto placementAUDDto) {
		PlacementDto placementDto = new PlacementDto();
		Query queryRecordPrimaryKey = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkRecordPrimaryKeySql).addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("currPlcmtStart", StandardBasicTypes.DATE).addScalar("currPlcmtEnd", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE).addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
				.setParameter("idPlcmtEvent", placementAUDDto.getIdPlcmtEvent())
				.setParameter("dtPlcmtStart", placementAUDDto.getDtPlcmtStart())
				.setParameter("dtPlcmtEnd", placementAUDDto.getDtPlcmtEnd()))
						.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		placementDto = (PlacementDto) queryRecordPrimaryKey.uniqueResult();

		return placementDto;
	}

	/**
	 * Method Name: getAbsDates Method Description: This method is used to
	 * getAbsDates
	 *
	 * @param placementDto
	 * @return PlacementDto
	 */
	@Override
	public PlacementDto getAbsDates(PlacementDto placementDto) {
		PlacementDto checkDatesAbs = new PlacementDto();
		Query querycheckDatesAbs = ((Query) sessionFactory.getCurrentSession().createSQLQuery(checkDatesAbsSql)
				.addScalar("absStartDate", StandardBasicTypes.INTEGER)
				.addScalar("absEndDate", StandardBasicTypes.INTEGER)
				.setParameter("currPlcmtStart", placementDto.getCurrPlcmtStart())
				.setParameter("dtPlcmtStart", placementDto.getDtPlcmtStart())
				.setParameter("currPlcmtEnd", placementDto.getCurrPlcmtEnd())
				.setParameter("dtPlcmtEnd", placementDto.getDtPlcmtEnd()))
						.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		checkDatesAbs = ((PlacementDto) querycheckDatesAbs.uniqueResult());

		return checkDatesAbs;
	}

	/**
	 * Method Name: checkLeftOverlapForUpdate Method Description: This method is
	 * used to checkLeftOverlapForUpdate
	 *
	 * @param placementAUDDto
	 * @param placementDto
	 * @return List<Long>
	 */
	@Override
	public List<Long> checkLeftOverlapForUpdate(PlacementAUDDto placementAUDDto, PlacementDto placementDto) {
		Query querycheckLeftOverlap = ((Query) sessionFactory.getCurrentSession().createSQLQuery(checkLeftOverlapSql)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned())
				.setParameter("currPlcmtStart", placementDto.getCurrPlcmtStart())
				.setParameter("dtPlcmtStart", placementDto.getDtPlcmtStart())
				.setParameter("idPlcmtEvent", placementAUDDto.getIdPlcmtEvent()));

		List<Long> checkLeftOverlap = (List<Long>) querycheckLeftOverlap.list();

		return checkLeftOverlap;
	}

	/**
	 * Method Name: getOpenSubStageCount Method Description: This method is used
	 * to getOpenSubStageCount
	 *
	 * @param idPlacementEvent
	 * @return Long
	 */
	@Override
	public Long getOpenSubStageCount(Long idPlacementEvent) {
		Query queryCheckForSUBStageCount = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkForSUBStageCountSql).setParameter("idPlcmtEvent", idPlacementEvent));
		Long openSubStageCount = ((BigDecimal) queryCheckForSUBStageCount.uniqueResult()).longValue();

		return openSubStageCount;
	}

	/**
	 * Method Name: checkRightOverlapForUpdate Method Description: This method
	 * is used to checkRightOverlapForUpdate
	 *
	 * @param placementAUDDto
	 * @param placementDto
	 * @return List<Long>
	 */
	@Override
	public List<Long> checkRightOverlapForUpdate(PlacementAUDDto placementAUDDto, PlacementDto placementDto) {
		Query querycheckRightOverlap = ((Query) sessionFactory.getCurrentSession().createSQLQuery(checkRightOverlapSql)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned())
				.setParameter("currPlcmtEnd", placementDto.getCurrPlcmtEnd())
				.setParameter("dtPlcmtStart", placementDto.getDtPlcmtEnd())
				.setParameter("idPlcmtEvent", placementAUDDto.getIdPlcmtEvent()));
		List<Long> checkRightOverlap = ((List<Long>) querycheckRightOverlap.list());

		return checkRightOverlap;
	}

	/**
	 * Method Name: getPlacmentsStartingNextDayForUpdate Method Description:
	 * This method is used to getPlacmentsStartingNextDayForUpdate
	 *
	 * @param placementAUDDto
	 * @param placementDto
	 * @return List<PlacementDto>
	 */
	@Override
	public List<PlacementDto> checkLeftGapForUpdate(PlacementAUDDto placementAUDDto, PlacementDto placementDto) {
		Query queryCheckDtPlcmtStartLeftOneDayMore = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkDtPlcmtStartLeftOneDayMoreSql).addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("dtPlcmtEnd", StandardBasicTypes.DATE).addScalar("plcmtDiffDate", StandardBasicTypes.INTEGER)
				.setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned())
				.setParameter("currPlcmtStart", placementDto.getDtPlcmtStart())
				.setDate("dtPlcmtStart", placementAUDDto.getDtPlcmtStart()))
						.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		List<PlacementDto> plcmtStartLeftOneDayMoreRec = ((List<PlacementDto>) queryCheckDtPlcmtStartLeftOneDayMore
				.list());
		return plcmtStartLeftOneDayMoreRec;
	}

	/**
	 * Method Name: getPlacmentsStartingNextDayForUpdate Method Description:
	 * This method is used to getPlacmentsStartingNextDayForUpdate
	 *
	 * @param placementAUDDto
	 * @param placementDto
	 * @return List<PlacementDto>
	 */
	@Override
	public List<PlacementDto> checkRightGapForUpdate(PlacementAUDDto placementAUDDto, PlacementDto placementDto) {
		Query queryCheckDtPlcmtStartRightOneDayMore = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkDtPlcmtStartOneDayMoreRightSql).addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
				.addScalar("plcmtDiffDate", StandardBasicTypes.INTEGER)
				.setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned())
				.setDate("dtPlcmtEnd", placementDto.getDtPlcmtStart()))
						.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		List<PlacementDto> plcmtStartRightOneDayMoreRec = ((List<PlacementDto>) queryCheckDtPlcmtStartRightOneDayMore
				.list());
		return plcmtStartRightOneDayMoreRec;
	}

	/**
	 * Method Name: checkPlacementsOverlapingInDifferenctCases Method
	 * Description: This method is used to
	 * checkPlacementsOverlapingInDifferenctCases
	 *
	 * @param placementAUDDto
	 * @param placementDto
	 * @return Long
	 */
	@Override
	public Long checkPlacementsOverlapingInDifferenctCases(PlacementAUDDto placementAUDDto, PlacementDto placementDto) {
		Query queryCheckOverlapPlacementinDiffCasesSql = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkOverlapPlacementinDiffCasesSql).setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned())
				.setParameter("dtPlcmtEnd", placementDto.getDtPlcmtEnd())
				.setParameter("dtPlcmtStart", placementDto.getDtPlcmtStart()));
		Long checkOverlapPlacementinDiffCases = ((BigDecimal) queryCheckOverlapPlacementinDiffCasesSql.uniqueResult())
				.longValue();
		return checkOverlapPlacementinDiffCases;
	}

	/**
	 * Method Name: updatePlacement Method Description: This method is used to
	 * updatePlacement
	 *
	 * @param placementAUDDto
	 */
	@Override
	public void updatePlacement(PlacementAUDDto placementAUDDto) {
		Placement placement = new Placement();
		if (!ObjectUtils.isEmpty(placementAUDDto)) {

			if (!ObjectUtils.isEmpty(placementAUDDto.getIdPlcmtEvent())) {
				placement = (Placement) sessionFactory.getCurrentSession().get(Placement.class,
						Long.valueOf(placementAUDDto.getIdPlcmtEvent()));

				if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtPermEff())) {
					placement.setDtPlcmtPermEff(placementAUDDto.getDtPlcmtPermEff());
				}
				if (!ObjectUtils.isEmpty(placementAUDDto.getIndTrashBags())) {
					placement.setIndTrashBags(placementAUDDto.getIndTrashBags());
				}
				if (!ObjectUtils.isEmpty(placementAUDDto.getTxtTrashBags())) {
					placement.setTxtTrashBags(placementAUDDto.getTxtTrashBags());
				}
				placement.setDtLastUpdate(new Date());

				if (!ObjectUtils.isEmpty(placementAUDDto.getIdPlcmtAdult())) {
					Person personByAdult = (Person) sessionFactory.getCurrentSession().get(Person.class,
							Long.valueOf(placementAUDDto.getIdPlcmtAdult()));
					if (!ObjectUtils.isEmpty(personByAdult)) {
						Set<EventPersonLink> eventPersonLinks = personByAdult.getEventPersonLinks();
						personByAdult.setEventPersonLinks(eventPersonLinks);
						placement.setPersonByIdPlcmtAdult(personByAdult);
					}
				}
				if (!ObjectUtils.isEmpty(placementAUDDto.getIdPlcmtChild())) {
					Person personByChild = (Person) sessionFactory.getCurrentSession().get(Person.class,
							Long.valueOf(placementAUDDto.getIdPlcmtChild()));
					if (!ObjectUtils.isEmpty(personByChild)) {
						Set<EventPersonLink> eventPersonLinks = personByChild.getEventPersonLinks();
						personByChild.setEventPersonLinks(eventPersonLinks);
						placement.setPersonByIdPlcmtChild(personByChild);
					}
				}

				if (!ObjectUtils.isEmpty(placementAUDDto.getIdContract())) {
					Contract idContact = (Contract) sessionFactory.getCurrentSession().get(Contract.class,
							Long.valueOf(placementAUDDto.getIdContract()));
					if (!ObjectUtils.isEmpty(idContact)) {
						placement.setContract(idContact);
					}
				}else {
					placement.setContract(null);
				}

				if (!ObjectUtils.isEmpty(placementAUDDto.getIdRsrcAgency())) {
					CapsResource idCapsResource = (CapsResource) sessionFactory.getCurrentSession()
							.get(CapsResource.class, Long.valueOf(placementAUDDto.getIdRsrcAgency()));
					if (!ObjectUtils.isEmpty(idCapsResource)) {
						placement.setCapsResourceByIdRsrcAgency(idCapsResource);
					}
				} else {
					placement.setCapsResourceByIdRsrcAgency(null);
				}

				if (!ObjectUtils.isEmpty(placementAUDDto.getIdRsrcFacil())) {
					CapsResource idCapsResourceFacil = (CapsResource) sessionFactory.getCurrentSession()
							.get(CapsResource.class, Long.valueOf(placementAUDDto.getIdRsrcFacil()));
					if (!ObjectUtils.isEmpty(idCapsResourceFacil)) {
						placement.setCapsResourceByIdRsrcFacil(idCapsResourceFacil);
					}
				} else {
					placement.setCapsResourceByIdRsrcFacil(null);
				}
				placement.setAddrPlcmtCity(placementAUDDto.getAddrPlcmtCity());
				placement.setAddrPlcmtCnty(placementAUDDto.getAddrPlcmtCnty());
				placement.setAddrPlcmtLn1(placementAUDDto.getAddrPlcmtLn1());
				placement.setAddrPlcmtLn2(placementAUDDto.getAddrPlcmtLn2());
				placement.setAddrPlcmtSt(placementAUDDto.getAddrPlcmtSt());
				placement.setAddrPlcmtZip(placementAUDDto.getAddrPlcmtZip());
				placement.setCdPlcmtInfo1(placementAUDDto.getCdPlcmtInfo1());
				placement.setCdPlcmtInfo2(placementAUDDto.getCdPlcmtInfo2());
				placement.setCdPlcmtInfo3(placementAUDDto.getCdPlcmtInfo3());
				placement.setCdPlcmtInfo4(placementAUDDto.getCdPlcmtInfo4());
				placement.setCdPlcmtInfo5(placementAUDDto.getCdPlcmtInfo5());
				placement.setCdPlcmtInfo6(placementAUDDto.getCdPlcmtInfo6());
				placement.setCdPlcmtInfo7(placementAUDDto.getCdPlcmtInfo7());
				placement.setCdPlcmtInfo8(placementAUDDto.getCdPlcmtInfo8());
				placement.setCdPlcmtInfo9(placementAUDDto.getCdPlcmtInfo9());
				placement.setCdPlcmtInfo10(placementAUDDto.getCdPlcmtInfo10());
				placement.setCdPlcmtInfo11(placementAUDDto.getCdPlcmtInfo11());
				placement.setCdPlcmtInfo12(placementAUDDto.getCdPlcmtInfo12());
				placement.setCdPlcmtInfo13(placementAUDDto.getCdPlcmtInfo13());
				placement.setCdPlcmtInfo14(placementAUDDto.getCdPlcmtInfo14());
				placement.setCdPlcmtInfo15(placementAUDDto.getCdPlcmtInfo15());
				placement.setCdPlcmtInfo16(placementAUDDto.getCdPlcmtInfo16());
				placement.setCdPlcmtInfo17(placementAUDDto.getCdPlcmtInfo17());
				placement.setCdPlcmtInfo18(placementAUDDto.getCdPlcmtInfo18());
				placement.setCdPlcmtInfo19(placementAUDDto.getCdPlcmtInfo19());
				placement.setCdPlcmtInfo20(placementAUDDto.getCdPlcmtInfo20());
				placement.setCdPlcmtLivArr(placementAUDDto.getCdPlcmtLivArr());
				placement.setCdPlcmtRemovalRsn(placementAUDDto.getCdPlcmtRemovalRsn());
				placement.setCdPlcmtActPlanned(placementAUDDto.getCdPlcmtActPlanned());
				placement.setCdPlcmtType(placementAUDDto.getCdPlcmtType());
				placement.setCdPlcmtService(placementAUDDto.getCdPlcmtService());
				placement.setDtPlcmtCaregvrDiscuss(placementAUDDto.getDtPlcmtCaregvrDiscuss());
				placement.setDtPlcmtChildDiscuss(placementAUDDto.getDtPlcmtChildDiscuss());
				placement.setDtPlcmtChildPlan(placementAUDDto.getDtPlcmtChildPlan());
				placement.setDtPlcmtEducLog(placementAUDDto.getDtPlcmtEducLog());
				if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtEnd())) {
					placement.setDtPlcmtEnd(placementAUDDto.getDtPlcmtEnd());
				}
				placement.setDtPlcmtMeddevHistory(placementAUDDto.getDtPlcmtMeddevHistory());
				placement.setDtSxVctmztnHistoryDiscuss(placementAUDDto.getDtSxVctmztnHistoryDiscuss());
				// artf176932 - Add NA checkbox for Sexual History Attachment A
				placement.setIndSxVctmztnHistoryDiscuss(placementAUDDto.getIndSxVctmztnHistoryDiscuss());
				placement.setDtPlcmtParentsNotif(placementAUDDto.getDtPlcmtParentsNotif());
				placement.setDtPlcmtPreplaceVisit(placementAUDDto.getDtPlcmtPreplaceVisit());
				placement.setDtPlcmtSchoolRecords(placementAUDDto.getDtPlcmtSchoolRecords());
				placement.setDtPlcmtStart(placementAUDDto.getDtPlcmtStart());
				placement.setIndPlcmtContCntct(placementAUDDto.getIndPlcmtContCntct());
				placement.setIndPlcmtEducLog(placementAUDDto.getIndPlcmtEducLog());
				placement.setIndPlcmtEmerg(placementAUDDto.getIndPlcmetEmerg());
				// artf255991 : BR 4.13 T3C Placement Indicator Logic - New Placement Save
				placement.setIndT3CPlcmt(placementAUDDto.getIndT3CPlcmet());
				placement.setIndPlcmtNotApplic(placementAUDDto.getIndPlcmtNotApplic());
				placement.setIndPlcmtSchoolDoc(placementAUDDto.getIndPlcmtSchoolDoc());
				placement.setNbrPlcmtPhoneExt(placementAUDDto.getPlcmtPhoneExt());
				placement.setNbrPlcmtTelephone(placementAUDDto.getPlcmtTelephone());
				placement.setNmPlcmtAgency(placementAUDDto.getPlcmtAgency());
				placement.setNmPlcmtContact(placementAUDDto.getPlcmtContact());
				placement.setNmPlcmtFacil(placementAUDDto.getPlcmtFacil());
				placement.setNmPlcmtPersonFull(placementAUDDto.getPlcmtPersonFull());
				placement.setIndPlcmtWriteHistory(placementAUDDto.getIndPlcmtWriteHistory());
				placement.setTxtPlcmtAddrComment(placementAUDDto.getTxtPlcmtAddrComment());
				placement.setTxtPlcmtDiscussion(placementAUDDto.getTxtPlcmtDiscussion());
				placement.setTxtPlcmtDocuments(placementAUDDto.getTxtPlcmtDocuments());
				placement.setTxtPlcmtRemovalRsn(placementAUDDto.getTxtPlcmtRemovalRsn());
				placement.setCdRmvlRsnSubtype(placementAUDDto.getCdPlcmtRemovalRsnSubtype());
				if (!ObjectUtils.isEmpty(placementAUDDto.getIdLastUpdatePerson())) {
					placement.setIdLastUpdatePerson(placementAUDDto.getIdLastUpdatePerson());
				}
				placement.setIdRsrcSscc(placementAUDDto.getIdRsrcSSCC());
				if (ServiceConstants.ZERO.equals(placementAUDDto.getIdRsrcSSCC()))
					placement.setIdRsrcSscc(null);
				placement.setNmPlcmtSscc(placementAUDDto.getNmPlcmtSSCC());
				placement.setIndPlcmtLessThan24Hrs(placementAUDDto.getIndPlcmtStartEndDtDiff());
				if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtLastPrebill())) {
					placement.setDtPlcmtLastPrebill(placementAUDDto.getDtPlcmtLastPrebill());
				}
				if (!ObjectUtils.isEmpty(placementAUDDto.getTxtTrashBags())) {
					placement.setTxtTrashBags(placementAUDDto.getTxtTrashBags());
				}
				if (!ObjectUtils.isEmpty(placementAUDDto.getIndTrashBags())) {
					placement.setIndTrashBags(placementAUDDto.getIndTrashBags());
					if(ServiceConstants.NO.equals(placementAUDDto.getIndTrashBags())){
						placement.setTxtTrashBags(ServiceConstants.EMPTY_STRING);
					}
				}
				// FFPSA: BR 138 : Set CongregateCare Indicator
				placement.setIndCongregateCare(placementAUDDto.getIndCongregateCare());
				sessionFactory.getCurrentSession().saveOrUpdate(placement);
			}
		}

	}

	/**
	 * Method Name: updateKinshipRecord Method Description: This method is used
	 * to updateKinshipRecord
	 *
	 * @param placementAUDDto
	 */
	@Override
	public void updateKinshipRecord(PlacementAUDDto placementAUDDto) {
		Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(updateKinshipSql);
		queryUpdate.setParameter("idPlcmtEvent", placementAUDDto.getIdPlcmtEvent());
		queryUpdate.setDate("currDate", new Date());
		queryUpdate.executeUpdate();
	}

	/**
	 * Method Name: checkIdStage Method Description: This method is used to
	 * checkIdStage
	 *
	 * @param placementAUDDto
	 * @return Long
	 */
	@Override
	public Long checkIdStage(PlacementAUDDto placementAUDDto) {
		Query queryIdStage = ((Query) sessionFactory.getCurrentSession().createSQLQuery(checkIdStageSql)
				.setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild()));
		Long idStageCount = ((BigDecimal) queryIdStage.uniqueResult()).longValue();
		return idStageCount;
	}

	/**
	 * Method Name: getPlacementEventForChild Method Description: This method is
	 * used to getPlacementEventForChild
	 *
	 * @param placementAUDDto
	 * @return Long
	 */
	@Override
	public List<Long> getPlacementEventForChild(PlacementAUDDto placementAUDDto) {
		Query queryRecForIdStageNIdEvent = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkRecForIdStageNIdEventSql).setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned()));
		List<Long> placementEventList = (List<Long>)queryRecForIdStageNIdEvent.list();
		return placementEventList;
	}

	/**
	 * Method Name: checkLeftOverlapForNewRecords Method Description: This
	 * method is used to checkLeftOverlapForNewRecords
	 *
	 * @param placementAUDDto
	 * @return List<Long>
	 */
	@Override
	public List<Long> checkLeftOverlapForNewRecords(PlacementAUDDto placementAUDDto) {
		Query queryNewRecOverlapsLeft = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkNewRecOverlapsLeftSql).addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned())
				.setParameter("dtPlcmtStart", placementAUDDto.getDtPlcmtStart())
				.setParameter("dtPlcmtEnd", placementAUDDto.getDtPlcmtEnd()));
		List<Long> newRecOverlapsLeftCount = (List<Long>) queryNewRecOverlapsLeft.list();

		return newRecOverlapsLeftCount;
	}

	/**
	 * Method Name: checkRightOverlapForNewRecords Method Description: This
	 * method is used to checkRightOverlapForNewRecords
	 *
	 * @param placementAUDDto
	 * @return List<Long>
	 */
	@Override
	public List<Long> checkRightOverlapForNewRecords(PlacementAUDDto placementAUDDto) {
		Query queryNewRecOverlapsRight = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkNewRecOverlapsRightSql).addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned())
				.setParameter("dtPlcmtStart", placementAUDDto.getDtPlcmtStart())
				.setParameter("dtPlcmtEnd", placementAUDDto.getDtPlcmtEnd()));

		List<Long> newRecOverlapsRightCount = ((List<Long>) queryNewRecOverlapsRight.list());

		return newRecOverlapsRightCount;
	}

	/**
	 * Method Name: getIdenticalNewRecords Method Description: This method is
	 * used to getIdenticalNewRecords
	 *
	 * @param placementAUDDto
	 * @return List<Long>
	 */
	@Override
	public List<Long> getIdenticalNewRecords(PlacementAUDDto placementAUDDto) {
		Query queryNewRecIdentical = ((Query) sessionFactory.getCurrentSession().createSQLQuery(checkNewRecIdenSql)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned())
				.setParameter("dtPlcmtStart", placementAUDDto.getDtPlcmtStart())
				.setParameter("dtPlcmtEnd", placementAUDDto.getDtPlcmtEnd()));
		List<Long> idPlacmntNewRecIdentical = ((List<Long>) queryNewRecIdentical.list());

		return idPlacmntNewRecIdentical;
	}

	/**
	 * Method Name: getNewRecordsHavingLeftGapMoreThanOneDay Method Description:
	 * This method is used to getNewRecordsHavingLeftGapMoreThanOneDay
	 *
	 * @param placementAUDDto
	 * @return List<PlacementDto>
	 */
	@Override
	public List<PlacementDto> getNewRecordsHavingLeftGapMoreThanOneDay(PlacementAUDDto placementAUDDto) {
		Query queryNewRecOverlapsLeft = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkDtPlcmtStartLeftOneDayBiggerSql).addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("dtPlcmtEnd", StandardBasicTypes.DATE).addScalar("plcmtDiffDate", StandardBasicTypes.INTEGER)
				.setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned())
				.setDate("dtPlcmtStart", placementAUDDto.getDtPlcmtStart()))
						.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		List<PlacementDto> newRecOverlapsLeftCount = ((List<PlacementDto>) queryNewRecOverlapsLeft.list());
		return newRecOverlapsLeftCount;
	}

	/**
	 * Method Name: getNewRecordsHavingRightGapMoreThanOneDay Method
	 * Description: This method is used to
	 * getNewRecordsHavingRightGapMoreThanOneDay
	 *
	 * @param placementAUDDto
	 * @return List<PlacementDto>
	 */
	@Override
	public List<PlacementDto> getNewRecordsHavingRightGapMoreThanOneDay(PlacementAUDDto placementAUDDto) {
		Query queryNewRecOverlapsRight = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkDtPlcmtStartRightOneDayBiggerSql)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
				.addScalar("plcmtDiffDate", StandardBasicTypes.INTEGER)
				.setParameter("idCase", placementAUDDto.getIdCase())
				.setParameter("idPlcmtChild", placementAUDDto.getIdPlcmtChild())
				.setParameter("cdPlcmtActPlanned", placementAUDDto.getCdPlcmtActPlanned())
				.setDate("dtPlcmtStart", placementAUDDto.getDtPlcmtStart()))
						.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		List<PlacementDto> newRecOverlapsRightCount = ((List<PlacementDto>) queryNewRecOverlapsRight.list());

		return newRecOverlapsRightCount;
	}

	/**
	 * Method Name: saveAndClosePlacement Method Description: This method is
	 * used to saveAndClosePlacement
	 *
	 * @param placementAUDDto
	 */
	@Override
	public void saveAndClosePlacement(PlacementAUDDto placementAUDDto) {

		Placement placement = new Placement();
		if (!ObjectUtils.isEmpty(placementAUDDto)) {
			if (!ObjectUtils.isEmpty(placementAUDDto.getIdPlcmtEvent())) {
				placement = (Placement) sessionFactory.getCurrentSession().get(Placement.class,
						new Long(placementAUDDto.getIdPlcmtEvent()));
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtPermEff())) {
				placement.setDtPlcmtPermEff(placementAUDDto.getDtPlcmtPermEff());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIdPlcmtEvent())) {
				placement.setIdPlcmtEvent(placementAUDDto.getIdPlcmtEvent());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtLastUpdate())) {
				placement.setDtLastUpdate(placementAUDDto.getDtLastUpdate());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIdPlcmtAdult())) {
				Person personByAdult = (Person) sessionFactory.getCurrentSession().get(Person.class,
						new Long(placementAUDDto.getIdPlcmtAdult()));
				placement.setPersonByIdPlcmtAdult(personByAdult);
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIdPlcmtChild())) {
				Person personByChild = (Person) sessionFactory.getCurrentSession().get(Person.class,
						new Long(placementAUDDto.getIdPlcmtChild()));
				if (!ObjectUtils.isEmpty(personByChild)) {
					placement.setPersonByIdPlcmtChild(personByChild);
				}
			}

			if (!ObjectUtils.isEmpty(placementAUDDto.getIdContract())) {
				Contract idContact = (Contract) sessionFactory.getCurrentSession().get(Contract.class,
						new Long(placementAUDDto.getIdContract()));
				if (!ObjectUtils.isEmpty(idContact)) {
					placement.setContract(idContact);
				}
			}

			if (!ObjectUtils.isEmpty(placementAUDDto.getIdRsrcAgency())) {
				CapsResource idCapsResource = (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class,
						new Long(placementAUDDto.getIdRsrcAgency()));
				if (!ObjectUtils.isEmpty(idCapsResource)) {
					placement.setCapsResourceByIdRsrcAgency(idCapsResource);
				}
			}

			if (!ObjectUtils.isEmpty(placementAUDDto.getIdRsrcFacil())) {
				CapsResource idCapsResourceFacil = (CapsResource) sessionFactory.getCurrentSession()
						.get(CapsResource.class, new Long(placementAUDDto.getIdRsrcFacil()));
				if (!ObjectUtils.isEmpty(idCapsResourceFacil)) {
					placement.setCapsResourceByIdRsrcAgency(idCapsResourceFacil);
				}
			}

			if (!ObjectUtils.isEmpty(placementAUDDto.getAddrPlcmtCity())) {
				placement.setAddrPlcmtCity(placementAUDDto.getAddrPlcmtCity());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getAddrPlcmtCnty())) {
				placement.setAddrPlcmtCnty(placementAUDDto.getAddrPlcmtCnty());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getAddrPlcmtLn1())) {
				placement.setAddrPlcmtLn1(placementAUDDto.getAddrPlcmtLn1());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getAddrPlcmtLn2())) {
				placement.setAddrPlcmtLn2(placementAUDDto.getAddrPlcmtLn2());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getAddrPlcmtSt())) {
				placement.setAddrPlcmtSt(placementAUDDto.getAddrPlcmtSt());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getAddrPlcmtZip())) {
				placement.setAddrPlcmtZip(placementAUDDto.getAddrPlcmtZip());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo1())) {
				placement.setCdPlcmtInfo1(placementAUDDto.getCdPlcmtInfo1());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo2())) {
				placement.setCdPlcmtInfo2(placementAUDDto.getCdPlcmtInfo2());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo3())) {
				placement.setCdPlcmtInfo3(placementAUDDto.getCdPlcmtInfo3());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo4())) {
				placement.setCdPlcmtInfo4(placementAUDDto.getCdPlcmtInfo4());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo5())) {
				placement.setCdPlcmtInfo5(placementAUDDto.getCdPlcmtInfo5());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo6())) {
				placement.setCdPlcmtInfo6(placementAUDDto.getCdPlcmtInfo6());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo7())) {
				placement.setCdPlcmtInfo7(placementAUDDto.getCdPlcmtInfo7());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo8())) {
				placement.setCdPlcmtInfo8(placementAUDDto.getCdPlcmtInfo8());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo9())) {
				placement.setCdPlcmtInfo9(placementAUDDto.getCdPlcmtInfo9());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo10())) {
				placement.setCdPlcmtInfo10(placementAUDDto.getCdPlcmtInfo10());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo11())) {
				placement.setCdPlcmtInfo11(placementAUDDto.getCdPlcmtInfo11());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo12())) {
				placement.setCdPlcmtInfo12(placementAUDDto.getCdPlcmtInfo12());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo13())) {
				placement.setCdPlcmtInfo13(placementAUDDto.getCdPlcmtInfo13());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo14())) {
				placement.setCdPlcmtInfo14(placementAUDDto.getCdPlcmtInfo14());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo15())) {
				placement.setCdPlcmtInfo15(placementAUDDto.getCdPlcmtInfo15());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo16())) {
				placement.setCdPlcmtInfo16(placementAUDDto.getCdPlcmtInfo16());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo17())) {
				placement.setCdPlcmtInfo17(placementAUDDto.getCdPlcmtInfo17());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo18())) {
				placement.setCdPlcmtInfo18(placementAUDDto.getCdPlcmtInfo18());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo19())) {
				placement.setCdPlcmtInfo19(placementAUDDto.getCdPlcmtInfo19());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtInfo20())) {
				placement.setCdPlcmtInfo20(placementAUDDto.getCdPlcmtInfo20());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtLivArr())) {
				placement.setCdPlcmtLivArr(placementAUDDto.getCdPlcmtLivArr());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtRemovalRsn())) {
				placement.setCdPlcmtRemovalRsn(placementAUDDto.getCdPlcmtRemovalRsn());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtActPlanned())) {
				placement.setCdPlcmtActPlanned(placementAUDDto.getCdPlcmtActPlanned());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtType())) {
				placement.setCdPlcmtType(placementAUDDto.getCdPlcmtType());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtService())) {
				placement.setCdPlcmtService(placementAUDDto.getCdPlcmtService());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtCaregvrDiscuss())) {
				placement.setDtPlcmtCaregvrDiscuss(placementAUDDto.getDtPlcmtCaregvrDiscuss());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtChildDiscuss())) {
				placement.setDtPlcmtChildDiscuss(placementAUDDto.getDtPlcmtChildDiscuss());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtChildPlan())) {
				placement.setDtPlcmtChildPlan(placementAUDDto.getDtPlcmtChildPlan());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtEducLog())) {
				placement.setDtPlcmtEducLog(placementAUDDto.getDtPlcmtEducLog());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtEnd())) {
				placement.setDtPlcmtEnd(placementAUDDto.getDtPlcmtEnd());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtMeddevHistory())) {
				placement.setDtPlcmtMeddevHistory(placementAUDDto.getDtPlcmtMeddevHistory());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtParentsNotif())) {
				placement.setDtPlcmtParentsNotif(placementAUDDto.getDtPlcmtParentsNotif());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtPreplaceVisit())) {
				placement.setDtPlcmtPreplaceVisit(placementAUDDto.getDtPlcmtPreplaceVisit());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtSchoolRecords())) {
				placement.setDtPlcmtSchoolRecords(placementAUDDto.getDtPlcmtSchoolRecords());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getDtPlcmtStart())) {
				placement.setDtPlcmtStart(placementAUDDto.getDtPlcmtStart());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndPlcmtContCntct())) {
				placement.setIndPlcmtContCntct(placementAUDDto.getIndPlcmtContCntct());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndPlcmtEducLog())) {
				placement.setIndPlcmtEducLog(placementAUDDto.getIndPlcmtEducLog());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndPlcmetEmerg())) {
				placement.setIndPlcmtEmerg(placementAUDDto.getIndPlcmetEmerg());
			}
			// artf255991 : BR 4.13 T3C Placement Indicator Logic - New Placement Save
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndT3CPlcmet())) {
				placement.setIndT3CPlcmt(placementAUDDto.getIndT3CPlcmet());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndPlcmtNotApplic())) {
				placement.setIndPlcmtNotApplic(placementAUDDto.getIndPlcmtNotApplic());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndPlcmtSchoolDoc())) {
				placement.setIndPlcmtSchoolDoc(placementAUDDto.getIndPlcmtSchoolDoc());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getPlcmtPhoneExt())) {
				placement.setNbrPlcmtPhoneExt(placementAUDDto.getPlcmtPhoneExt());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getPlcmtTelephone())) {
				placement.setNbrPlcmtTelephone(placementAUDDto.getPlcmtTelephone());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getPlcmtAgency())) {
				placement.setNmPlcmtAgency(placementAUDDto.getPlcmtAgency());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getPlcmtContact())) {
				placement.setNmPlcmtContact(placementAUDDto.getPlcmtContact());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getPlcmtFacil())) {
				placement.setNmPlcmtFacil(placementAUDDto.getPlcmtFacil());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getPlcmtPersonFull())) {
				placement.setNmPlcmtPersonFull(placementAUDDto.getPlcmtPersonFull());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndPlcmtWriteHistory())) {
				placement.setIndPlcmtWriteHistory(placementAUDDto.getIndPlcmtWriteHistory());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getTxtPlcmtAddrComment())) {
				placement.setTxtPlcmtAddrComment(placementAUDDto.getTxtPlcmtAddrComment());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getTxtPlcmtDiscussion())) {
				placement.setTxtPlcmtDiscussion(placementAUDDto.getTxtPlcmtDiscussion());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getTxtPlcmtDocuments())) {
				placement.setTxtPlcmtDocuments(placementAUDDto.getTxtPlcmtDocuments());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getTxtPlcmtRemovalRsn())) {
				placement.setTxtPlcmtRemovalRsn(placementAUDDto.getTxtPlcmtRemovalRsn());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getCdPlcmtRemovalRsnSubtype())) {
				placement.setCdRmvlRsnSubtype(placementAUDDto.getCdPlcmtRemovalRsnSubtype());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIdLastUpdatePerson())) {
				placement.setIdLastUpdatePerson(placementAUDDto.getIdLastUpdatePerson());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIdRsrcSSCC())) {
				placement.setIdRsrcSscc(placementAUDDto.getIdRsrcSSCC());
				if (ServiceConstants.Zero.equals(placementAUDDto.getIdRsrcSSCC()))
					placement.setIdRsrcSscc(null);
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getNmPlcmtSSCC())) {
				placement.setNmPlcmtSscc(placementAUDDto.getNmPlcmtSSCC());
			}
			if (!ObjectUtils.isEmpty(placementAUDDto.getIndPlcmtStartEndDtDiff())) {
				placement.setIndPlcmtLessThan24Hrs(placementAUDDto.getIndPlcmtStartEndDtDiff());
			}
			// IND_PLCMT_LESS_THAN_24HRS Left
			// table

			// FFPSA: BR 138 : Set CongregateCare Indicator
			placement.setIndCongregateCare(placementAUDDto.getIndCongregateCare());
		}

		sessionFactory.getCurrentSession().saveOrUpdate(placement);

	}

	/**
	 * Method Name: updateKinshipInd Method Description: This method is used to
	 * update KinshipInd
	 *
	 * @param placementAUDDto
	 */
	@Override
	public void updateKinshipInd(PlacementAUDDto placementAUDDto) {
		Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(updateKinshipIndYesSql);
		queryUpdate.setParameter("idPlcmtEvent", placementAUDDto.getIdPlcmtEvent());
		queryUpdate.setDate("currDate", new Date());
		queryUpdate.executeUpdate();
	}

	// CMSC09d
	/**
	 * Method Name: getCountSubcareStage Method Description: This method will
	 * return the count of the rows that meet the requirements
	 *
	 * @param commonDto
	 * @return Long
	 */
	@Override
	public Long getCountSubcareStage(CommonDto commonDto) {
		Long nbrStagesOpen = 0L;
		if (!commonDto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_ADO)) { // Defect 11067 - Negate the condition to match tux code
			Query queryCountADOSubcareStageSql = ((Query) sessionFactory.getCurrentSession()
					.createSQLQuery(countADOSubcareStageSql).setParameter("idCase", commonDto.getIdCase())
					.setParameter("idStage", commonDto.getIdStage()).setParameter("cdStage", commonDto.getCdStage())
					.setParameter("cdRemRsn", commonDto.getCdRemRsn())
					.setParameter("cdLivArr", commonDto.getCdLivArr()));
			nbrStagesOpen = ((BigDecimal) queryCountADOSubcareStageSql.uniqueResult()).longValue();
		}

		else {
			Query queryCountADOSubcareStageSql = ((Query) sessionFactory.getCurrentSession()
					.createSQLQuery(countOtherSubcareStageSql).setParameter("idCase", commonDto.getIdCase())
					.setParameter("idStage", commonDto.getIdStage()).setParameter("cdStage", commonDto.getCdStage()));
			nbrStagesOpen = ((BigDecimal) queryCountADOSubcareStageSql.uniqueResult()).longValue();
		}
		return nbrStagesOpen;
	}

	/**
	 * Method Name: getPriorPlacementsById Method Description: This method
	 * returns prior placement list based on idPlacementEvent
	 *
	 * @param idPriorPlacementEvent
	 * @return idPlacementEvents
	 */

	public List<Long> getPriorPlacementsById(Long idPriorPlacementEvent) {

		List<Long> idPlacementEvents = new ArrayList<>();

		idPlacementEvents = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPriorPlacementsByIdSql)
				.setParameter("idPriorPlacement", idPriorPlacementEvent))
						.addScalar("idPlacement", StandardBasicTypes.LONG).list();

		return idPlacementEvents;
	}

	/**
	 * Method Name: getIndChildSibling1 Method Description: This method returns
	 * prior Sibling person in the pca application.
	 *
	 * @param idPerson
	 * @return indChildSibling1
	 */

	public String getIndChildSibling1(Long idPerson) {

		String indChildSibling1 = "";

		indChildSibling1 = (String) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getIndChildSibing1Sql)
				.setParameter("idPerson", idPerson)).addScalar("indChildSibling1", StandardBasicTypes.STRING)
						.uniqueResult();

		return indChildSibling1;
	}

	/**
	 * Method Name: getEligiblityEvent Method Description: This method returns
	 * eligibility of a person.
	 *
	 * @param idPerson
	 * @return eligibility
	 */

	public EligibilityDto getEligibilityEvent(Long idPerson) {

		EligibilityDto eligibility = new EligibilityDto();
		eligibility = (EligibilityDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getEligibibility).setParameter("idPerson", idPerson))
						.addScalar("idEligEvent", StandardBasicTypes.LONG)
						.addScalar("cdEligActual", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(EligibilityDto.class)).uniqueResult();
		return eligibility;
	}

	/**
	 *
	 * Method Name: getActualPlacement Method Description:CLSS84D
	 *
	 * @param idStage
	 * @return
	 */
	public List<PlacementDto> getActualPlacement(Long idStage) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getActualPlacementByStageIdSql)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPlcmtAdult", StandardBasicTypes.LONG).addScalar("idPlcmtChild", StandardBasicTypes.LONG)
				.addScalar("idRsrcAgency", StandardBasicTypes.LONG).addScalar("idRsrcFacil", StandardBasicTypes.LONG)
				.addScalar("addrPlcmtCity", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtCnty", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtLn1", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtLn2", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtSt", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtZip", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo1", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo2", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo3", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo4", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo5", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo6", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo7", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtRemovalRsn", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtActPlanned", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtService", StandardBasicTypes.STRING)
				.addScalar("dtPlcmtCaregvrDiscuss", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtChildDiscuss", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtChildPlan", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtEducLog", StandardBasicTypes.DATE).addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
				.addScalar("dtSxVctmztnHistoryDiscuss", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtMeddevHistory", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtParentsNotif", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtPreplaceVisit", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtSchoolRecords", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
				/*
				 * .addScalar("indPlcmtEducLog", StandardBasicTypes.STRING)
				 * .addScalar("indPlcmtEmerg", StandardBasicTypes.STRING)
				 * .addScalar("indPlcmtNotApplic", StandardBasicTypes.STRING)
				 * .addScalar("indPlcmtSchoolDoc", StandardBasicTypes.STRING)
				 * .addScalar("indPlcmtWriteHistory", StandardBasicTypes.STRING)
				 * .addScalar("indSxVctmztnHistoryDiscuss", StandardBasicTypes.STRING)
				 */
				.addScalar("nbrPlcmtPhoneExt", StandardBasicTypes.STRING)
				.addScalar("nbrPlcmtTelephone", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtAgency", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtContact", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtFacil", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtPersonFull", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtAddrComment", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtDiscussion", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtDocuments", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtRemovalRsn", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		return (List<PlacementDto>) query.list();

	}

	/**
	 *
	 * Method Name: getMostRecentPlacement Method Description: CSES44D
	 *
	 * @param idPlcmtChild
	 * @return
	 */

	public List<PlacementDto> getMostRecentPlacement(Long idPlcmtChild) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getMostRecentPlacementSql)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPlcmtAdult", StandardBasicTypes.LONG).addScalar("idPlcmtChild", StandardBasicTypes.LONG)
				.addScalar("idRsrcAgency", StandardBasicTypes.LONG).addScalar("idRsrcFacil", StandardBasicTypes.LONG)
				.addScalar("addrPlcmtCity", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtCnty", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtLn1", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtLn2", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtSt", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtZip", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo1", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo2", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo3", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo4", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo5", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo6", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo7", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtRemovalRsn", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtActPlanned", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtService", StandardBasicTypes.STRING)
				.addScalar("dtPlcmtCaregvrDiscuss", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtChildDiscuss", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtChildPlan", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtEducLog", StandardBasicTypes.DATE).addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtMeddevHistory", StandardBasicTypes.DATE)
				.addScalar("dtSxVctmztnHistoryDiscuss", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtParentsNotif", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtPreplaceVisit", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtSchoolRecords", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
				/*
				 * .addScalar("indPlcmtEducLog", StandardBasicTypes.STRING)
				 * .addScalar("indPlcmtEmerg", StandardBasicTypes.STRING)
				 * .addScalar("indPlcmtNotApplic", StandardBasicTypes.STRING)
				 * .addScalar("indPlcmtSchoolDoc", StandardBasicTypes.STRING)
				 * .addScalar("indPlcmtWriteHistory", StandardBasicTypes.STRING)
				 * .addScalar("indSxVctmztnHistoryDiscuss", StandardBasicTypes.STRING)
				 */
				.addScalar("nbrPlcmtPhoneExt", StandardBasicTypes.STRING)
				.addScalar("nbrPlcmtTelephone", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtAgency", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtContact", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtFacil", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtPersonFull", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtAddrComment", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtDiscussion", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtDocuments", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtRemovalRsn", StandardBasicTypes.STRING).setParameter("idPlcmtChild", idPlcmtChild)
				.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		return (List<PlacementDto>) query.list();

	}

	/**
	 *
	 * Method Name: getMostRecentPlacement Method Description: CSES44D
	 *
	 * @param idPlcmtChild
	 * @return
	 */

	public List<PlacementDto> getMostRecentLinvingArrangement(Long idPlcmtChild, String cdTask) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getMostRecentLinvingArrangementSql)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING).addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE).setParameter("idPlcmtChild", idPlcmtChild)
				.setParameter("cdTask", cdTask).setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		return (List<PlacementDto>) query.list();

	}

	/**
	 *
	 * Method Name: getApprovers Method Description:
	 *
	 * @param idEvent
	 * @return
	 */

	@Override
	public List<Long> getApprovers(Long idEvent) {
		return (List<Long>) sessionFactory.getCurrentSession().createSQLQuery(approversSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).setParameter("idEvent", idEvent).list();
	}

	/**
	 * This DAM will retreive a Relative Placement from the PLACEMENT table
	 * where ID PERSON = the host and Dt Plcmt Strt <= input date and input date
	 * =< Max and IND PLCMT ACT PLANNED = true CSECC1D
	 *
	 * @param idPlcmtChild
	 * @param dtSvcAuthEff
	 * @return
	 */
	@Override
	public PlacementDto retrieveRelativePlacement(Long idPlcmtChild, Date dtSvcAuthEff) {
		PlacementDto placementDto = new PlacementDto();
		// This criteria is used to get the placement information which has
		// maximum idPlacementEvent.
		Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(Placement.class)
				.setProjection(Projections.max("idPlcmtEvent"))
				.add(Restrictions.eq("personByIdPlcmtChild.idPerson", idPlcmtChild))
				.add(Restrictions.le("dtPlcmtStart", dtSvcAuthEff)).add(Restrictions.ne("dtPlcmtStart", "dtPlcmtEnd"))
				.add(Restrictions.eq("cdPlcmtLivArr", ServiceConstants.PLCMT_LIV_ARR_PRIMARY))
				.add(Restrictions.eq("cdPlcmtActPlanned", ServiceConstants.PLCMT_ACTUAL_TYPE));
		Placement placementByidEvent = (Placement) criteria1.uniqueResult();

		// This criteria is used to get the placement which has maximum
		// placement start date
		Criteria criteria2 = sessionFactory.getCurrentSession().createCriteria(Placement.class)
				.setProjection(Projections.max("dtPlcmtStart"))
				.add(Restrictions.eq("personByIdPlcmtChild.idPerson", idPlcmtChild))
				.add(Restrictions.le("dtPlcmtStart", dtSvcAuthEff)).add(Restrictions.ne("dtPlcmtStart", "dtPlcmtEnd"))
				.add(Restrictions.eq("cdPlcmtLivArr", ServiceConstants.PLCMT_LIV_ARR_PRIMARY))
				.add(Restrictions.eq("cdPlcmtActPlanned", ServiceConstants.PLCMT_ACTUAL_TYPE));
		Placement placementByStartDt = (Placement) criteria2.uniqueResult();

		// This criteria gets the placement with maximum placement id and
		// maximum placement start date
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Placement.class)
				.add(Restrictions.eq("personByIdPlcmtChild.idPerson", idPlcmtChild))
				.add(Restrictions.le("dtPlcmtStart", dtSvcAuthEff)).add(Restrictions.ne("dtPlcmtStart", "dtPlcmtEnd"))
				.add(Restrictions.eq("cdPlcmtLivArr", ServiceConstants.PLCMT_LIV_ARR_PRIMARY))
				.add(Restrictions.eq("cdPlcmtActPlanned", ServiceConstants.PLCMT_ACTUAL_TYPE))
				.add(Restrictions.eq("idPlcmtEvent", placementByidEvent.getIdPlcmtEvent()))
				.add(Restrictions.eq("dtPlcmtStart", placementByStartDt.getDtPlcmtStart()));
		Placement placement = (Placement) criteria.uniqueResult();

		BeanUtils.copyProperties(placementDto, placement);
		return placementDto;
	}

	/**
	 *
	 * Method Name: retrievePlacementByEventId Method Description: get placement
	 * by eventId
	 *
	 * @param idEvent
	 * @return
	 */
	@Override
	public Placement retrievePlacementByEventId(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Placement.class)
				.add(Restrictions.eq("idPlcmtEvent", idEvent));
		Placement placement = (Placement) criteria.uniqueResult();
		return placement;
	}

	/**
	 * Method Name: alertPlacementReferral Method Description: This method is
	 * called in save method of placement detail an legal status page to create
	 * alert for the primary assigned caseworker to complete the 2077 Referral
	 * within 7 days of when a child Placement is entered and saved.
	 *
	 * @param alertPlacementLsDto
	 */
	@Override
	public void alertPlacementReferral(AlertPlacementLsDto alertPlacementLsDto) {
		Long placemntCnt = 0l;
		Date dtLegalStatusCnt = null;
		if (alertPlacementLsDto.getCheckFlag().equals(ServiceConstants.PLACEMENT)) {
			// Query to get the child’s Legal Status Region.
			Query queryAlertForPlacementTriggerOne = sessionFactory.getCurrentSession()
					.createSQLQuery(alertForPlacementTriggerOne).addScalar("cdCntyRegion", StandardBasicTypes.STRING)
					.setParameter("idStage", alertPlacementLsDto.getIdStage())
					.setResultTransformer(Transformers.aliasToBean(AlertPlacementLsDto.class));
			AlertPlacementLsDto plcmntdtoOne = (AlertPlacementLsDto) queryAlertForPlacementTriggerOne.uniqueResult();
			if (!ObjectUtils.isEmpty(plcmntdtoOne)) {
				//Region of the child's Legal County is not equal to the Region of the Placement Address.
				if (plcmntdtoOne.getCdCntyRegion() != alertPlacementLsDto.getCdCntyRegion()) {
					placemntCnt = 1L;
					// Legal County on the Legal Status record is equal to the Region of the Placement Address.
				} else if (plcmntdtoOne.getCdCntyRegion() == alertPlacementLsDto.getCdCntyRegion()) {
					// Query to get the Region of the County of any parent
					Query queryAlertForPlacementTriggerTwo = sessionFactory.getCurrentSession()
							.createSQLQuery(alertForPlacementTriggerTwo)
							.addScalar("cdCntyRegion", StandardBasicTypes.STRING)
							.setParameter("idStage", alertPlacementLsDto.getIdStage())
							.setResultTransformer(Transformers.aliasToBean(AlertPlacementLsDto.class));
					List<AlertPlacementLsDto> plcmntdtoTwoList = (List<AlertPlacementLsDto>) queryAlertForPlacementTriggerTwo
							.list();
					if (!CollectionUtils.isEmpty(plcmntdtoTwoList)) {
						// The Region of the County of any parent is not equal
						// to the Legal Region of the child.
						for (AlertPlacementLsDto alertPlacementLsDto2 : plcmntdtoTwoList) {
							if (alertPlacementLsDto2.getCdCntyRegion() != plcmntdtoOne.getCdCntyRegion()) {
								 placemntCnt = 1L;
							}
						}
					}
				}
			}
		} else {
			// legal region and placement region does not match & should the
			// first legal status record for the stage
			Query queryAlertForLSTriggerOne = sessionFactory.getCurrentSession().createSQLQuery(alertForLSTriggerOne)
					.addScalar("dtStart", StandardBasicTypes.DATE)
					.setParameter("idStage", alertPlacementLsDto.getIdStage())
					.setParameter("idEvent", alertPlacementLsDto.getIdEvent())
					.setResultTransformer(Transformers.aliasToBean(AlertPlacementLsDto.class));
			AlertPlacementLsDto dtoOne = (AlertPlacementLsDto) queryAlertForLSTriggerOne.uniqueResult();
			if (!ObjectUtils.isEmpty(dtoOne)) {
				dtLegalStatusCnt = dtoOne.getDtStart();
			}

			// legal region and placement region does match & should the first
			// legal status record for the stage
			if (ObjectUtils.isEmpty(dtoOne)) {
				Query queryAlertForLSTriggerTwo = sessionFactory.getCurrentSession()
						.createSQLQuery(alertForLSTriggerTwo).addScalar("dtStart", StandardBasicTypes.DATE)
						.setParameter("idStage", alertPlacementLsDto.getIdStage())
						.setParameter("idEvent", alertPlacementLsDto.getIdEvent())
						.setResultTransformer(Transformers.aliasToBean(AlertPlacementLsDto.class));
				AlertPlacementLsDto dtoTwo = (AlertPlacementLsDto) queryAlertForLSTriggerTwo.uniqueResult();
				if (!ObjectUtils.isEmpty(dtoTwo)) {
					dtLegalStatusCnt = dtoTwo.getDtStart();
				}
			}
		}
		if (placemntCnt > 0) {
			// create alert in Todo Detail for placement Information Page.
			Placement placement = (Placement) sessionFactory.getCurrentSession().get(Placement.class,
					alertPlacementLsDto.getIdEvent());
			if (!ObjectUtils.isEmpty(placement)) {
				Date dtPlcmtStart = placement.getDtPlcmtStart();
				if (!ObjectUtils.isEmpty(dtPlcmtStart)) {
					Calendar c = Calendar.getInstance();
					c.setTime(dtPlcmtStart);
					c.add(Calendar.DAY_OF_MONTH, 7);
					Date dtDue = c.getTime();
					createTodoEntity(dtDue, alertPlacementLsDto);
				}
			}
		}
		if (!ObjectUtils.isEmpty(dtLegalStatusCnt)) {
			// create alert in Todo Detail for Legal Status Page.
			Date dtPlcmtStart = dtLegalStatusCnt;
			if (!ObjectUtils.isEmpty(dtPlcmtStart)) {
				Calendar c = Calendar.getInstance();
				c.setTime(dtPlcmtStart);
				c.add(Calendar.DAY_OF_MONTH, 7);
				Date dtDue = c.getTime();
				createTodoEntity(dtDue, alertPlacementLsDto);
			}
		}
	}

	/**
	 * Method Name: createTodoEntity Method Description: This method is used to
	 * create alert in the Todo Detail page
	 *
	 * @param dtDue
	 * @param alertPlacementLsDto
	 */
	public void createTodoEntity(Date dtDue, AlertPlacementLsDto alertPlacementLsDto) {
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		Todo todoEntity = new Todo();
		todoEntity.setCdTodoType(ServiceConstants.ALERT_TODO);
		todoEntity.setDtTodoDue(dtDue);
		todoEntity.setCdTodoTask(ServiceConstants.PLCMT_LIST_TASK_CODE_SUB);
		todoEntity.setDtLastUpdate(currentDate);
		todoEntity.setDtTodoCreated(currentDate);
		SimpleDateFormat sdf = new SimpleDateFormat(ServiceConstants.DATE_FORMAT);
		String dueDt = sdf.format(dtDue.getTime());
		String todoDesc = ServiceConstants.ALERT_TODO_DESC + dueDt;
		todoEntity.setTxtTodoDesc(todoDesc);
		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, alertPlacementLsDto.getIdStage());
		if (!ObjectUtils.isEmpty(stage))
			todoEntity.setStage(stage);

		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, alertPlacementLsDto.getIdEvent());
		if (!ObjectUtils.isEmpty(event))
			todoEntity.setEvent(event);

		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
				alertPlacementLsDto.getIdPerson());
		if (!ObjectUtils.isEmpty(person))
			todoEntity.setPersonByIdTodoPersAssigned(person);
		    todoEntity.setPersonByIdTodoPersWorker(person);
		CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class,
				alertPlacementLsDto.getIdCase());
		if (!ObjectUtils.isEmpty(capsCase))
			todoEntity.setCapsCase(capsCase);
		sessionFactory.getCurrentSession().saveOrUpdate(todoEntity);
	}

	/**
	 * Method Name: getLatestPlcmntEvent Method Description: This method is to
	 * retrieve the latest placement event.
	 *
	 * @param idStage
	 * @param cdEventType
	 * @return EventDto
	 */

	@Override
	public EventDto getLatestPlcmntEvent(Long idEvent, Long idStage, String cdEventType) {
		EventDto eventDto = new EventDto();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class, "event");
		criteria.add(Restrictions.eq("event.stage.idStage", idStage));
		criteria.add(Restrictions.eq("cdEventType", cdEventType));
		if (!(0l == idEvent)) {
			criteria.add(Restrictions.ne("idEvent", idEvent));
		}
		criteria.addOrder(Order.desc("dtLastUpdate"));
		criteria.setProjection(Projections.property("idEvent"));
		List<Long> idEvents = (List<Long>) criteria.list();
		if (!CollectionUtils.isEmpty(idEvents)) {
			eventDto.setIdEvent(idEvents.get(0));
		}
		return eventDto;
	}


	/**
	 * Method Name: getActiveTEPContract
	 * Method Description:This method returns
	 * an active idcontract
	 *
	 * @param placementReq
	 * @return PlacementValueDto
	 * */

	@Override
	public PlacementValueDto getActiveTepContract(PlacementReq placementReq) {
		PlacementValueDto placementValueDto = null;
		List<PlacementValueDto> placementValueDtoList = null;
		Long idResource = placementReq.getIdResource();
		String svcService = TEP_SVC_SERVICE;
		if (Boolean.TRUE.equals(placementReq.getTfcPlacement())) {
			svcService = TFC_SVC_SERVICE;
		}
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getActiveTepContract)
				.addScalar("idRsrcSSCC", StandardBasicTypes.LONG)
				.addScalar("idContract", StandardBasicTypes.LONG)
				.addScalar("cdContractRegion", StandardBasicTypes.STRING)
				.addScalar("cdContractStatus", StandardBasicTypes.STRING)
				.addScalar("cdContractService", StandardBasicTypes.STRING)
				.addScalar("dtContractStart", StandardBasicTypes.DATE)
				.addScalar("dtContractTerm", StandardBasicTypes.DATE).
				setParameter("idRsrcContractTep", idResource).setParameter("svcService", svcService)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		placementValueDtoList = (List<PlacementValueDto>) query.list();
		if (!ObjectUtils.isEmpty(placementValueDtoList)) {
			placementValueDto = placementValueDtoList.get(0);
		} else {
			placementValueDto = new PlacementValueDto();
			placementValueDto.setIdContract(0L);

		}

		return placementValueDto;

}

	/**
	 *
	 * Method Name: getCountOfActiveTfcPlmnts Method
	 * Description:This Dam returns the count of other children with open tfc palcements
	 * for a given idRsrcFacil and idPlcmtChild
	 *
	 * @param placementReq
	 * @return
	 */
	@Override
	public CommonCountRes getCountOfActiveTfcPlmnts(PlacementReq placementReq) {
		CommonCountRes resp = new CommonCountRes();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getCountOfActiveTfcPlmnts)
				.addScalar("count", StandardBasicTypes.LONG)
				.setParameter("idRsrcFacil", placementReq.getIdResource())
				.setParameter("idPlcmtChild", placementReq.getIdPerson());
		Long count = (Long) query.uniqueResult();
		resp.setCount(count);
		return resp;
	}

	/**
	 *
	 * Method Name: getCountOfAllPlacements Method
	 * Description:This Dam returns the count of other children with open tfc palcements
	 * for a given idRsrcFacil and idPlcmtChild
	 *
	 * @param placementReq
	 * @return
	 */
	@Override
	public CommonCountRes getCountOfAllPlacements(PlacementReq placementReq) {
		CommonCountRes resp = new CommonCountRes();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getCountOfAllPlacements)
				.addScalar("countAll", StandardBasicTypes.LONG)
				.setParameter("idRsrcFacil", placementReq.getIdResource())
				.setParameter("idPlcmtChild", placementReq.getIdPerson());
		Long countAll = (Long) query.uniqueResult();
		resp.setCount(countAll);
		return resp;
	}

	/**
	 *
	 * Method Name: getCountyRegion Method
	 * Description:This Method returns the region based on county.
	 *
	 * @param cdCounty
	 * @return String
	 */
	@Override
	public String getCountyRegion(String cdCounty) {
		String cdCntyRegion = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getCountyRegion)
				.addScalar("cdCntyRegion", StandardBasicTypes.STRING)
				.setParameter("cdCounty", cdCounty)
				.setResultTransformer(Transformers.aliasToBean(AlertPlacementLsDto.class));

		AlertPlacementLsDto placementLsDto = (AlertPlacementLsDto) query.uniqueResult();
		if(!ObjectUtils.isEmpty(placementLsDto)){
			cdCntyRegion = placementLsDto.getCdCntyRegion();
		}
		return cdCntyRegion;
	}

	@Override
	public int getOpenPlacementCountForCase(Long idCase) {

		Query query = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getOpenPlacementCountForCase).setParameter("idCase", idCase));
		int openPlacementCount = ((BigDecimal) query.uniqueResult()).intValue();

		return openPlacementCount;
	}

	@Override
	public Long getOpenEligibilityEvent(Long idStage) {

		Long idEligibilityEvent = null;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Eligibility.class);
		criteria.add(Restrictions.eq("idStage", idStage));
		criteria.add(Restrictions.eq("dtEligEnd", ServiceConstants.MAX_DATE));
		List<Eligibility> eligibility =  criteria.list();

		if(!CollectionUtils.isEmpty(eligibility)) {
			idEligibilityEvent = eligibility.get(0).getIdEligEvent();
		}

		return idEligibilityEvent;
	}

	//PPM 70054(FCL) - IMPACT Date Requirement for RCYFC Notification

	/**
	 * @param childBillOfRightsDto
	 * @param isPlacementApproved
	 * @param indNewActualPlcmt
	 */
	@Override
	public void saveToChildBillOfRightsHistory(ChildBillOfRightsDto childBillOfRightsDto, boolean isPlacementApproved, String indNewActualPlcmt) {
		ChildBillOfRightsHistory childBillOfRightsHistory = null;

		if(!isPlacementApproved && (!ObjectUtils.isEmpty(indNewActualPlcmt) && !"Y".equals(indNewActualPlcmt))){
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildBillOfRightsHistory.class);
			criteria.add(Restrictions.eq("idPlcmtEvent",childBillOfRightsDto.getIdPlcmtEvent()));
			criteria.add(Restrictions.eq("cdBillOfRightsType", childBillOfRightsDto.getCdBillOfRightsType()));
			criteria.addOrder(Order.desc("dtBillOfRights")).setMaxResults(1);
			childBillOfRightsHistory = (ChildBillOfRightsHistory)criteria.uniqueResult();
		}
		if(ObjectUtils.isEmpty(childBillOfRightsHistory) || isPlacementApproved){
				childBillOfRightsHistory = new ChildBillOfRightsHistory();
				childBillOfRightsHistory.setDtCreated(new Date());
				if (!ObjectUtils.isEmpty(childBillOfRightsDto.getIdCreatedPerson())) {
					childBillOfRightsHistory.setIdCreatedPerson(childBillOfRightsDto.getIdCreatedPerson());
				}
			}

		if (!ObjectUtils.isEmpty(childBillOfRightsDto.getIdPlcmtEvent())) {
			childBillOfRightsHistory.setIdPlcmtEvent(childBillOfRightsDto.getIdPlcmtEvent());
		}
		if (!ObjectUtils.isEmpty(childBillOfRightsDto.getDtBillOfRights())) {
			childBillOfRightsHistory.setDtBillOfRights(childBillOfRightsDto.getDtBillOfRights());

		}

		if(!ObjectUtils.isEmpty(childBillOfRightsDto.getCdBillOfRightsType())){
			childBillOfRightsHistory.setCdBillOfRightsType(childBillOfRightsDto.getCdBillOfRightsType());
		}

		if (!ObjectUtils.isEmpty(childBillOfRightsDto.getIdLastUpdatePerson())) {
			childBillOfRightsHistory.setIdLastUpdatePerson(childBillOfRightsDto.getIdLastUpdatePerson());
		}

		childBillOfRightsHistory.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().save(childBillOfRightsHistory);
	}

	/**
	 * @param idPlcmtChild
	 * @return
	 */
	@Override
	public List<ChildBillOfRightsDto> getBillOfRightsDatesByChildId(Long idPlcmtChild){
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getBillOfRightsDatesByChildIdSql)
				.addScalar("dtBillOfRights", StandardBasicTypes.DATE)
				.addScalar("cdBillOfRightsType", StandardBasicTypes.STRING)
				.addScalar("indDisableInitialBor", StandardBasicTypes.STRING)
				.setParameter("idPlcmtChild", idPlcmtChild).setResultTransformer(Transformers.aliasToBean(ChildBillOfRightsDto.class));
		return (List<ChildBillOfRightsDto>) query.list();
	}

	/**
	 * @param idPlcmtChild
	 * @return
	 */
	@Override
	public Long getCountOfAllPlacementsByChildId(Long idPlcmtChild) {
		Long countAll = 0L;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getCountOfAllPlacementsByChildIdSql)
				.addScalar("countAll", StandardBasicTypes.LONG)
				.setParameter("idPlcmtChild", idPlcmtChild);
		countAll = (Long) query.uniqueResult();
		return countAll;
	}

	/**
	 * @param idPlcmtEvent
	 * @param idPlcmtChild
	 * @return
	 */
	@Override
	public List<ChildBillOfRightsDto> getBillOfRightsDates(Long idPlcmtEvent, Long idPlcmtChild){
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getBillOfRightsDatesByPlcmtIdSql)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("cdBillOfRightsType", StandardBasicTypes.STRING)
				.addScalar("dtBillOfRights", StandardBasicTypes.DATE)
				.addScalar("indDisableInitialBor", StandardBasicTypes.STRING)
				.setParameter("idPlcmtEvent", idPlcmtEvent)
				.setParameter("idPlcmtChild", idPlcmtChild).setResultTransformer(Transformers.aliasToBean(ChildBillOfRightsDto.class));
		return (List<ChildBillOfRightsDto>) query.list();
	}

	/**
	 * @param idPlcmtEvent
	 */
	@Override
	public void setInitialBorDisableInd(Long idPlcmtEvent) {
		if (!ObjectUtils.isEmpty(idPlcmtEvent)) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildBillOfRightsHistory.class);
			criteria.add(Restrictions.eq("idPlcmtEvent", idPlcmtEvent));
			criteria.add(Restrictions.eq("cdBillOfRightsType", ServiceConstants.CHILD_BILL_OF_RIGHTS_TYPE_INITIAL));
			criteria.addOrder(Order.desc("dtBillOfRights")).setMaxResults(1);
			ChildBillOfRightsHistory childBillOfRightsHistory = (ChildBillOfRightsHistory) criteria.uniqueResult();
			if (!ObjectUtils.isEmpty(childBillOfRightsHistory)) {
				if (ObjectUtils.isEmpty(childBillOfRightsHistory.getIndDisableInitialBor())) {
					childBillOfRightsHistory.setIndDisableInitialBor(ServiceConstants.STRING_IND_Y);
					childBillOfRightsHistory.setDtLastUpdate(new Date());
					sessionFactory.getCurrentSession().save(childBillOfRightsHistory);
				}
			}
		}
	}

	@Override
	public List<TemporaryAbsenceDto> getTemporaryAbsenceList(Long placementEventId){
			Query query = sessionFactory.getCurrentSession().createSQLQuery(getTemporaryAbsenceListSql)
					.addScalar("idPlacementTa", StandardBasicTypes.LONG)
					.addScalar("temporaryAbsenceType", StandardBasicTypes.STRING)
					.addScalar("dtTemporaryAbsenceStart", StandardBasicTypes.DATE)
					.addScalar("dtTemporaryAbsenceEnd", StandardBasicTypes.DATE)
					.setParameter("idLinkedPlcmtEvent", placementEventId)
					.setResultTransformer(Transformers.aliasToBean(TemporaryAbsenceDto.class));
			List<TemporaryAbsenceDto> temporaryAbsenceDtoList = (List<TemporaryAbsenceDto>) query.list();

			return temporaryAbsenceDtoList;
	}


	@Override
	public void saveIntialBillOfRights(ChildBillOfRightsDto childBillOfRightsDto){
		ChildBillOfRightsHistory childBillOfRightsHistory = null;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildBillOfRightsHistory.class);
		criteria.add(Restrictions.eq("idPlcmtEvent",childBillOfRightsDto.getIdPlcmtEvent()));
		criteria.add(Restrictions.eq("cdBillOfRightsType", childBillOfRightsDto.getCdBillOfRightsType()));
		criteria.addOrder(Order.desc("dtBillOfRights")).setMaxResults(1);
		childBillOfRightsHistory = (ChildBillOfRightsHistory)criteria.uniqueResult();

		if(ObjectUtils.isEmpty(childBillOfRightsHistory)){
			childBillOfRightsHistory = new ChildBillOfRightsHistory();
			childBillOfRightsHistory.setDtCreated(new Date());
			if (!ObjectUtils.isEmpty(childBillOfRightsDto.getIdCreatedPerson())) {
				childBillOfRightsHistory.setIdCreatedPerson(childBillOfRightsDto.getIdCreatedPerson());
			}
		}

		if (!ObjectUtils.isEmpty(childBillOfRightsDto.getIdPlcmtEvent())) {
			childBillOfRightsHistory.setIdPlcmtEvent(childBillOfRightsDto.getIdPlcmtEvent());
		}
		if (!ObjectUtils.isEmpty(childBillOfRightsDto.getDtBillOfRights())) {
			childBillOfRightsHistory.setDtBillOfRights(childBillOfRightsDto.getDtBillOfRights());

		}

		if(!ObjectUtils.isEmpty(childBillOfRightsDto.getCdBillOfRightsType())){
			childBillOfRightsHistory.setCdBillOfRightsType(childBillOfRightsDto.getCdBillOfRightsType());
		}

		if (!ObjectUtils.isEmpty(childBillOfRightsDto.getIdLastUpdatePerson())) {
			childBillOfRightsHistory.setIdLastUpdatePerson(childBillOfRightsDto.getIdLastUpdatePerson());
		}

		childBillOfRightsHistory.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().save(childBillOfRightsHistory);
	}

	@Override
	public List<KinChildDto> getAllChildPlacementsId(Long homeResourceId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getTemporaryAbsenceListSql);
		List<Long> idPlacementIds = new ArrayList<>();
		List<KinChildDto> kinChildDtos = new ArrayList<>();

		idPlacementIds.addAll(((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAllChildPlacementsIdSql)
				.setParameter("resourceId", homeResourceId))
				.addScalar("placementChildId", StandardBasicTypes.LONG).list());

		if(!CollectionUtils.isEmpty(idPlacementIds)){
			idPlacementIds.stream().forEach(e -> {
				KinChildDto kinChildDto = new KinChildDto();
				kinChildDto.setChildId(e);
				kinChildDtos.add(kinChildDto);
			});
		}

		return kinChildDtos;
	}

	public KinChildDto getPlacementChildInfo(Long childId, Long resourceId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPlacementChildInfoSql)
				.addScalar("placementEventId", StandardBasicTypes.LONG)
				.addScalar("birthDate", StandardBasicTypes.DATE)
				.setParameter("childId", childId)
				.setParameter("resourceId", resourceId)
				.setResultTransformer(Transformers.aliasToBean(KinChildDto.class));

		KinChildDto kinChildDto = (KinChildDto) query.uniqueResult();
		if (!ObjectUtils.isEmpty(kinChildDto) && !ObjectUtils.isEmpty(kinChildDto.getBirthDate())) {
			kinChildDto.setAge(DateHelper.getAge(kinChildDto.getBirthDate()));
		}

		return kinChildDto;
	}

	public boolean getHasApprovedPlacementForAChild(Long resourceId, Long placementChildId) {
		Query query = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getApprovedPlacementForAChildSql)
				.setParameter("resourceId", resourceId)
				.setParameter("placementChildId", placementChildId));

		return CHAR_IND_Y == (char) query.uniqueResult();
	}

	public KinChildDto getPlacementInfo(Long placementChildId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPlacementsInfoSql)
				.addScalar("childId", StandardBasicTypes.LONG)
				.addScalar("caseId", StandardBasicTypes.LONG)
				.addScalar("livingArrangement", StandardBasicTypes.STRING)
				.addScalar("placementEventId", StandardBasicTypes.LONG)
				.addScalar("placementTypeCode", StandardBasicTypes.STRING)
				.addScalar("placementStartDate", StandardBasicTypes.DATE)
				.addScalar("placementEndDate", StandardBasicTypes.DATE)
				.addScalar("birthDate", StandardBasicTypes.DATE)
				.addScalar("dateOfDeath", StandardBasicTypes.DATE)
				.addScalar("childFullName", StandardBasicTypes.STRING)
				.setParameter("placementChildId", placementChildId)
				.setResultTransformer(Transformers.aliasToBean(KinChildDto.class));

		KinChildDto kinChildDto = (KinChildDto) query.uniqueResult();
		if (!ObjectUtils.isEmpty(kinChildDto) && !ObjectUtils.isEmpty(kinChildDto.getBirthDate())) {
			kinChildDto.setAge(DateHelper.getAge(kinChildDto.getBirthDate()));
		}
		return kinChildDto;
	}

	public KinChildDto getPlacementLegalStatusInfo(Long placementChildId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPlacementLegalStatusInfoSql)
				.addScalar("legalStatusStatDate", StandardBasicTypes.DATE)
				.addScalar("legalStatusDismissDate", StandardBasicTypes.DATE)
				.addScalar("cdLegalStatus", StandardBasicTypes.STRING)
				.setParameter("placementChildId", placementChildId)
				.setResultTransformer(Transformers.aliasToBean(KinChildDto.class));
		KinChildDto kinChildDto = null;
		List<KinChildDto> kinChildDtoList = (List<KinChildDto>) query.list();
		if(!ObjectUtils.isEmpty(kinChildDtoList)) {
			kinChildDto = kinChildDtoList.get(0);
		}
		if (!ObjectUtils.isEmpty(kinChildDto) && !ObjectUtils.isEmpty(kinChildDto.getBirthDate())) {
			kinChildDto.setAge(DateHelper.getAge(kinChildDto.getBirthDate()));
		}

		return kinChildDto;
	}


	@Override
	public Long getPlacementsAdultId(Long resourceId) {

		Query query = ((Query) sessionFactory.getCurrentSession().createSQLQuery(getPlacementsAdultIdSql)
				.setParameter("resourceId", resourceId));
		try{
			return ((BigDecimal) query.uniqueResult()).longValue();
		} catch (Exception ex){
			return null;
		}
	}

	@Override
	public List<PlacementValueDto> getChildPlcmtReferrals(Long stageId) {
		List<PlacementValueDto> placementValueDtoLst = new ArrayList<>();

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getChildPlcmtReferrals)
				.addScalar("idRsrcSSCC", StandardBasicTypes.LONG)
				.addScalar("cdContractRegion", StandardBasicTypes.STRING)
				.addScalar("resourceName", StandardBasicTypes.STRING).addScalar("idContract", StandardBasicTypes.LONG)
				.addScalar("dtDischargeActual", StandardBasicTypes.DATE)
				.addScalar("dtRecorded", StandardBasicTypes.DATE).addScalar("dtRecordedSSCC", StandardBasicTypes.DATE)
				.addScalar("dtRecordedDFPS", StandardBasicTypes.DATE)
				.addScalar("indPriorCommunication", StandardBasicTypes.STRING)
				.addScalar("idSsccReferral", StandardBasicTypes.LONG)
				.addScalar("dtExpectedPlcmt",StandardBasicTypes.DATE).setParameter("idStage", stageId)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));

		List<PlacementValueDto> placementValueDtoList = (List<PlacementValueDto>) sQLQuery1.list();

		if (!ObjectUtils.isEmpty(placementValueDtoList)) {
			for (PlacementValueDto placementValueDto : placementValueDtoList) {

				if (ServiceConstants.Y.equalsIgnoreCase(placementValueDto.getIndPriorCommunication())) {
					if (ObjectUtils.isEmpty(placementValueDto.getDtRecordedSSCC())) {
						placementValueDto.setDtRecordedSSCC(placementValueDto.getDtRecorded());
					}
					if (ObjectUtils.isEmpty(placementValueDto.getDtRecordedDFPS())) {
						placementValueDto.setDtRecordedDFPS(placementValueDto.getDtRecorded());
					}

				} else {
					placementValueDto.setDtRecordedSSCC(placementValueDto.getDtRecorded());
					placementValueDto.setDtRecordedDFPS(placementValueDto.getDtRecorded());
				}

				if (((placementValueDto.getDtRecorded() != null) && (placementValueDto.getDtRecordedSSCC() != null)
						&& (placementValueDto.getDtRecordedDFPS() != null))
						&& (DateUtils.isBefore(placementValueDto.getDtRecorded(), placementValueDto.getDtRecordedSSCC())
						|| placementValueDto.getDtRecorded().equals(placementValueDto.getDtRecordedSSCC()))
						&& (DateUtils.isBefore(placementValueDto.getDtRecorded(), placementValueDto.getDtRecordedDFPS())
						|| placementValueDto.getDtRecorded().equals(placementValueDto.getDtRecordedDFPS()))) {
					placementValueDto.setDtReferralDate(placementValueDto.getDtRecorded());
				} else if (((placementValueDto.getDtRecorded() != null) && (placementValueDto.getDtRecordedSSCC() != null)
						&& (placementValueDto.getDtRecordedDFPS() != null))
						&& (DateUtils.isBefore(placementValueDto.getDtRecordedSSCC(), placementValueDto.getDtRecorded())
						|| placementValueDto.getDtRecordedSSCC().equals(placementValueDto.getDtRecorded()))
						&& (DateUtils.isBefore(placementValueDto.getDtRecordedSSCC(), placementValueDto.getDtRecordedDFPS())
						|| placementValueDto.getDtRecordedSSCC().equals(placementValueDto.getDtRecordedDFPS()))) {
					placementValueDto.setDtReferralDate(placementValueDto.getDtRecordedSSCC());
				} else {
					placementValueDto.setDtReferralDate(placementValueDto.getDtRecordedDFPS());
				}
				if (ObjectUtils.isEmpty(placementValueDto.getDtExpectedPlcmt())) {
					placementValueDto.setDtExpectedPlcmt(placementValueDto.getDtRecorded());
				}
				placementValueDtoLst.add(placementValueDto);
			}
		}
		return placementValueDtoLst;

	}


	@Override
	public List<Date> getEarliestReviewBillOfRights(Long idPlcmtChild){
		return sessionFactory.getCurrentSession().createSQLQuery(getEarliestReviewBillOfRightsSql)
				.addScalar("dtBillOfRightsReview", StandardBasicTypes.DATE).setParameter("idPlcmtChild", idPlcmtChild)
				.list();
	}

	/**
	 * This method retrieves the Resource Facility Id of the child placement record that matches the following conditions:
	 * 1. The child has a relationship of Son/Daugther with the idPerson
	 * 2. The child's stage type is C-PB
	 * 3. The child placement type is '040' (Non-FPS Paid)
	 * @param idPerson
	 * @param idStage
	 * @return
	 */
	@Override
	public  List<PlacementValueDto> getChildPlacement(Long idPerson, Long idStage){
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getChildPlacementSql)
				.addScalar("idRsrcFacil", StandardBasicTypes.LONG)
				.addScalar("startDate", StandardBasicTypes.TIMESTAMP)
				.setParameter("idStage", idStage)
				.setParameter("idPerson", idPerson).setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		return (List<PlacementValueDto>) query.list();
	}

	/**
	 * This method checks if the 'Pregnant and Parenting Support Services' Add-on exists for the person and stage id,
	 * and returns true if the add-on exists.
	 * @param idPerson
	 * @param idStage
	 * @return
	 */
	@Override
	public 	Boolean getAddOnSvcPkg(Long idPerson, Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSvcPkgAddonForTAFSql)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.setParameter("idStage", idStage).setParameter("idPerson", idPerson).
				setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		List<PlacementValueDto> list =  (List<PlacementValueDto>) query.list();
		return !list.isEmpty();
	}

	@Override
	public PlacementDto getLatestPlacement(Long idPlcmntChild, Long idCase) {
		PlacementDto placementDto = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(latestPlacement)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtEnd", StandardBasicTypes.DATE).addScalar("caseId", StandardBasicTypes.LONG)
				.addScalar("idRsrcAgency", StandardBasicTypes.LONG).addScalar("idRsrcFacil", StandardBasicTypes.LONG)
				.addScalar("indT3CPlcmt", StandardBasicTypes.STRING)
				.setParameter("idPlcmntChild", idPlcmntChild)
				.setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		List<PlacementDto> placements = query.list();
		if(!CollectionUtils.isEmpty(placements)){
			placementDto = placements.get(0);
		}
		return placementDto;
	}


	/**
	 * This method retrieves the Resource Facility Id of the child placement record that matches the following conditions:
	 * 1. The child has a relationship of Son/Daugther with the idPerson
	 * 2. The child's stage type is C-PB
	 * 3. The child placement type is '040' (Non-FPS Paid)
	 * @param idPerson
	 * @param idStage
	 * @return
	 */
	@Override
	public  List<PlacementValueDto> getParentPlacement(Long idPerson, Long idStage){
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getParentPlacementSql)
				.addScalar("idRsrcFacil", StandardBasicTypes.LONG)
				.addScalar("dtPlcmtStart", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(PlacementValueDto.class));
		return (List<PlacementValueDto>) query.list();
	}
    @Override
	public boolean chkValidFPSContractRsrc(Long idResource, Long idRsrcSscc) {
		Query query = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(chkValidFPSContractRsrcSql)
				.setParameter("idResource", idResource)
				.setParameter("idRsrcSscc", idRsrcSscc));
		Object result=query.uniqueResult();
		return (null != result) ? ((BigDecimal) result).longValue() > 0 : false;

	}
	@Override
	public CommonCountRes getCountCPBPlcmntsForYouthParent(Long idPerson, Long idStage, Date placementStartDate) {
		CommonCountRes resp = new CommonCountRes();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getCountCPBPlcmntsForYouthParentSql)
				.addScalar("COUNT", StandardBasicTypes.LONG)
				.setParameter("placementStartDate", placementStartDate)
				.setParameter("idStage", idStage)
				.setParameter("idPerson", idPerson);
		Long countAll = (Long) query.uniqueResult();
		resp.setCount(countAll);
		return resp;
	}

	@Override
	public List<String> getContractServices(PlacementDtlGpDto placementDtlGpDto, List<String> servicePackages) {



		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getValidContract)
				.addScalar("cntyService", StandardBasicTypes.STRING)
				.setParameter("idContract", placementDtlGpDto.getIdContract())
				.setParameter("dtScrDtCurrentDate", placementDtlGpDto.getDtPlcmtStart())
				.setParameter("cdCncntyCounty", placementDtlGpDto.getAddrPlcmtCnty())
				.setParameter("cdCnperStatusAct", ServiceConstants.CNPER_STATUS_ACT)
				.setParameter("cdCnperStatusCls", ServiceConstants.CNPER_STATUS_CLS)
				.setParameter("cdCnperStatusClt", ServiceConstants.CNPER_STATUS_CLT)
				.setParameter("cdCnperStatusPnt", ServiceConstants.CNPER_STATUS_PNT)
				.setParameter("cdCnperStatusPyh", ServiceConstants.CNPER_STATUS_PYH)
				.setParameter("cdCnperStatusSvh", ServiceConstants.CNPER_STATUS_SVH)
				.setParameterList("cdCnCntyService", servicePackages));
      	System.out.println("getValidContract==> " + getValidContract);
      	System.out.println("cdCnCntyService==> " + servicePackages);
		System.out.println("idContract==> " +  placementDtlGpDto.getIdContract());
		System.out.println("dtScrDtCurrentDate==> " + placementDtlGpDto.getDtPlcmtStart());
		System.out.println("cdCncntyCounty==> " + placementDtlGpDto.getAddrPlcmtCnty());
    

		List<String> serviceCodes = sQLQuery.list();

		return serviceCodes;
	}

	@Override
	public Boolean checkAlocBlocForNonT3cPlcmt(Long idCase, Date dtPlcmtStart) {
		Query query = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(checkAlocBlocForNonT3cPlcmtSql)
				.setParameter("idCase", idCase)
				.setParameter("dtPlcmtStart", dtPlcmtStart));
		Object result=query.uniqueResult();
		return null != result && ((BigDecimal) result).longValue() > 0;
	}


}
