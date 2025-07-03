package us.tx.state.dfps.service.sscc.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.SsccList;
import us.tx.state.dfps.common.domain.SsccReferral;
import us.tx.state.dfps.common.domain.Unit;
import us.tx.state.dfps.common.dto.Option;
import us.tx.state.dfps.common.dto.UserProfileDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCParameterDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefPlcmtDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCResourceDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.sscc.dao.SSCCListDao;
import us.tx.state.dfps.service.workload.dto.SSCCListHeaderDto;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: SSCC
 * EJB Class Description: SSCCRefDao Mar 26, 2017 - 8:58:10 PM
 * 
 *
 */
@Repository
public class SSCCListDaoImpl implements SSCCListDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${SSCCListDaoImpl.hasStageAccess}")
	private String sqlHasStageAccess;

	@Value("${SSCCListDaoImpl.fetchValidSSCCRegion}")
	private String fetchValidSSCCRegion;

	@Value("${SSCCListDaoImpl.isValidSSCCCatchmentRegion}")
	private String isValidSSCCCatchmentRegion;

	@Value("${SSCCListDaoImpl.fetchValidUnitRegionforSSCCUser}")
	private String fetchValidUnitRegionforSSCCUser;

	@Value("${SSCCListDaoImpl.sqlFetchSsccMonitoringList}")
	private String sqlFetchSsccMonitoringList;

	@Value("${SSCCListDaoImpl.sqlCaseForPlcmtStatus}")
	private String sqlCaseForPlcmtStatus;

	@Value("${SSCCListDaoImpl.sqlCaseForPlcmtStatusDecode}")
	private String sqlCaseForPlcmtStatusDecode;

	@Value("${SSCCListDaoImpl.sqlCaseForReferralDate}")
	private String sqlCaseForReferralDate;

	@Value("${SSCCListDaoImpl.sqlCaseRefIndicator}")
	private String sqlCaseRefIndicator;

	@Value("${SSCCListDaoImpl.sqlFetchEfcTot}")
	private String sqlFetchEfcTot;

	@Value("${SSCCListDaoImpl.sqlFetchEfcFy}")
	private String sqlFetchEfcFy;

	@Value("${SSCCListDaoImpl.sqlCaseFetchLegalCnty}")
	private String sqlCaseFetchLegalCnty;

	@Value("${SSCCListDaoImpl.sqlSsccMonitoringListFromClauseAssignToUser}")
	private String sqlSsccMonitoringListFromClauseAssignToUser;

	@Value("${SSCCListDaoImpl.sqlSsccMonitoringListFromClauseAssignToAll}")
	private String sqlSsccMonitoringListFromClauseAssignToAll;

	@Value("${SSCCListDaoImpl.sqlSsccMonitoringListWhereClause}")
	private String sqlSsccMonitoringListWhereClause;

	@Value("${SSCCListDaoImpl.sqlSsccMonitoringListIncludeOnlyAssignedToUser}")
	private String sqlSsccMonitoringListIncludeOnlyAssignedToUser;

	@Value("${SSCCListDaoImpl.sqlSsccMonitoringListWhereClauseWithDates}")
	private String sqlSsccMonitoringListWhereClauseWithDates;

	@Value("${SSCCListDaoImpl.sqlSsccMonitoringListIncludeAll}")
	private String sqlSsccMonitoringListIncludeAll;

	@Value("${SSCCListDaoImpl.sqlSsccMonitoringListIncludeDischarged}")
	private String sqlSsccMonitoringListIncludeDischarged;

	@Value("${SSCCListDaoImpl.sqlSsccMonitoringListIncludeRescinded}")
	private String sqlSsccMonitoringListIncludeRescinded;

	@Value("${SSCCListDaoImpl.sqlSsccMonitoringListIncludeActiveOnly}")
	private String sqlSsccMonitoringListIncludeActiveOnly;

	@Value("${SSCCListDaoImpl.sqlSsccMonitoringListIncludePlcmtRefOnly}")
	private String sqlSsccMonitoringListIncludePlcmtRefOnly;

	@Value("${SSCCListDaoImpl.sqlSsccMonitoringListIncludeFamRefOnly}")
	private String sqlSsccMonitoringListIncludeFamRefOnly;

	@Value("${SSCCListDaoImpl.sqlSsccMonitoringLinkResource}")
	private String sqlSsccMonitoringLinkResource;

	@Value("${SSCCListDaoImpl.fetchCdCatchmentFromIdCatchment}")
	private String sqlFetchCdCatchmentFromIdCatchment;

	@Value("${SSCCListDaoImpl.fetchIdCatchmentFromCdCatchment}")
	private String sqlFetchIdCatchmentFromCdCatchment;

	@Value("${SSCCListDaoImpl.fetchDefaultCatchmentForSSCCUser}")
	private String sqlFetchDefaultCatchmentForSSCCUser;

	@Value("${SSCCListDaoImpl.fetchDefaultSSCCCatchmentForDFPSUser}")
	private String sqlFetchDefaultSSCCCatchmentForDFPSUser;

	@Value("${SSCCListDaoImpl.fetchCatchmentsForRegion}")
	private String sqlFetchCatchmentsForRegion;

	@Override
	public long saveSSCCList(SSCCListDto ssccListDto) {

		SsccList ssccList = new SsccList();

		ssccList.setDtCreated(new Date());
		ssccList.setIdCreatedPerson((long) ssccListDto.getIdCreatedPerson());
		ssccList.setIdLastUpdatePerson((long) ssccListDto.getIdLastUpdatePerson());
		SsccReferral ssccReferral = new SsccReferral();
		ssccReferral.setIdSSCCReferral((long) ssccListDto.getIdSSCCReferral());
		ssccList.setSsccReferral(ssccReferral);
		ssccList.setIndNew(ssccListDto.getIndNew());
		ssccList.setIndLegalStatusMissing(ssccListDto.getIndLegalStatusMissing());
		if (ssccListDto.getDtFamilyMemberUpdate() != null) {
			ssccList.setDtFamilyMemberUpdate(ssccListDto.getDtFamilyMemberUpdate());
		}
		if (ssccListDto.getIndPlcmtSscc() != null) {
			ssccList.setIndPlcmtSscc(ssccListDto.getIndPlcmtSscc());
		}
		if (ssccListDto.getIdPlcmtRsrc() != null) {
			ssccList.setIdPlcmtRsrc((long) ssccListDto.getIdPlcmtRsrc());
		}
		if (!ObjectUtils.isEmpty(ssccListDto.getIdPlcmtEvent())) {
			ssccList.setIdPlcmtEvent((long) ssccListDto.getIdPlcmtEvent());
		}
		ssccList.setIndEfc(ssccListDto.getIndEfc());
		ssccList.setIndEfcActive(ssccListDto.getIndEfcActive());
		if (ssccListDto.getCdPlcmtOptionType() != null) {
			ssccList.setCdPlcmtOptionType(ssccListDto.getCdPlcmtOptionType());
		}
		if (ssccListDto.getCdPlcmtOptionStatus() != null) {
			ssccList.setCdPlcmtOptionStatus(ssccListDto.getCdPlcmtOptionStatus());
		}
		if (ssccListDto.getDtPlcmtOptionStatus() != null) {
			ssccList.setDtPlcmtOptionStatus(ssccListDto.getDtPlcmtOptionStatus());
		}
		if (ssccListDto.getDtPlcmtOptionRecorded() != null) {
			ssccList.setDtPlcmtOptionRecorded(ssccListDto.getDtPlcmtOptionRecorded());
		}
		if (ssccListDto.getCdExceptCareStatus() != null) {
			ssccList.setCdExceptCareStatus(ssccListDto.getCdExceptCareStatus());
		}
		if (ssccListDto.getCdPlcmtCircStatus() != null) {
			ssccList.setCdPlcmtCircStatus(ssccListDto.getCdPlcmtCircStatus());
		}
		if (ssccListDto.getCdPlcmtCircStatus() != null) {
			ssccList.setCdPlcmtCircStatus(ssccListDto.getCdPlcmtCircStatus());
		}
		if (ssccListDto.getDtPlcmtCircStart() != null) {
			ssccList.setDtPlcmtCircStart(ssccListDto.getDtPlcmtCircStart());
		}
		if (ssccListDto.getDtPlcmtCircExpire() != null) {
			ssccList.setDtPlcmtCircExpire(ssccListDto.getDtPlcmtCircExpire());
		}
		if (ssccListDto.getCdSvcAuthStatus() != null) {
			ssccList.setCdSvcAuthStatus(ssccListDto.getCdSvcAuthStatus());
		}
		if (ssccListDto.getDtSvcAuthStatus() != null) {
			ssccList.setDtSvcAuthStatus(ssccListDto.getDtSvcAuthStatus());
		}
		ssccList.setIndNonssccSvcAuth(ssccListDto.getIndNonssccSvcAuth());
		ssccList.setIndDaycareValidated(ssccListDto.getIndDaycareValidated());
		if (ssccListDto.getDtDaycareValidated() != null) {
			ssccList.setDtDaycareValidated(ssccListDto.getDtDaycareValidated());
		}
		ssccList.setIndSsccDaycare(ssccListDto.getIndSsccDaycare());
		if (ssccListDto.getDtSsccDaycare() != null) {
			ssccList.setDtSsccDaycare(ssccListDto.getDtSsccDaycare());
		}
		if (ssccListDto.getDtChildPlanInitiated() != null) {
			ssccList.setDtChildPlanInitiated(ssccListDto.getDtChildPlanInitiated());
		}
		if (ssccListDto.getDtChildPlanDue() != null) {
			ssccList.setDtChildPlanDue(ssccListDto.getDtChildPlanDue());
		}
		if (ssccListDto.getCdChildPlanDue() != null) {
			ssccList.setCdChildPlanDue(ssccListDto.getCdChildPlanDue());
		}
		ssccList.setIndCpContentPropose(ssccListDto.getIndCpContentPropose());
		if (ssccListDto.getDtCpContentPropose() != null) {
			ssccList.setDtCpContentPropose(ssccListDto.getDtCpContentPropose());
		}
		ssccList.setIndCpContentReject(ssccListDto.getIndCpContentReject());
		if (ssccListDto.getDtCpContentReject() != null) {
			ssccList.setDtCpContentReject(ssccListDto.getDtCpContentReject());
		}
		ssccList.setIndCpContentApprove(ssccListDto.getIndCpContentApprove());
		if (ssccListDto.getDtCpContentApprove() != null) {
			ssccList.setDtCpContentApprove(ssccListDto.getDtCpContentApprove());
		}
		ssccList.setIndCpContentUnlock(ssccListDto.getIndCpContentUnlock());
		if (ssccListDto.getDtCpContentUnlock() != null) {
			ssccList.setDtCpContentUnlock(ssccListDto.getDtCpContentUnlock());
		}
		ssccList.setIndCpContentRepropose(ssccListDto.getIndCpContentRepropose());
		if (ssccListDto.getDtCpContentRepropose() != null) {
			ssccList.setDtCpContentRepropose(ssccListDto.getDtCpContentRepropose());
		}
		if (ssccListDto.getDtChildPlanAprv() != null) {
			ssccList.setDtChildPlanAprv(ssccListDto.getDtChildPlanAprv());
		}
		ssccList.setDtLastUpdate(new Date());

		return (long) sessionFactory.getCurrentSession().save(ssccList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public long updateSSCCList(SSCCListDto ssccListDto) {

		long updatedResult = ServiceConstants.ZERO_VAL;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccList.class);

		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", (long) ssccListDto.getIdSSCCReferral()));

		List<SsccList> ssccLists = criteria.list();
		for (SsccList ssccList : ssccLists) {

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIdLastUpdatePerson())) {
				ssccList.setIdLastUpdatePerson((long) ssccListDto.getIdLastUpdatePerson());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndNew())) {
				ssccList.setIndNew(ssccListDto.getIndNew());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndLegalStatusMissing())) {
				ssccList.setIndLegalStatusMissing(ssccListDto.getIndLegalStatusMissing());

			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtFamilyMemberUpdate())) {
				ssccList.setDtFamilyMemberUpdate(ssccListDto.getDtFamilyMemberUpdate());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndPlcmtSscc())) {
				ssccList.setIndPlcmtSscc(ssccListDto.getIndPlcmtSscc());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIdPlcmtRsrc())) {
				ssccList.setIdPlcmtRsrc((long) ssccListDto.getIdPlcmtRsrc());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIdPlcmtEvent())) {
				ssccList.setIdPlcmtEvent((long) ssccListDto.getIdPlcmtEvent());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndEfc())) {
				ssccList.setIndEfc(ssccListDto.getIndEfc());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndEfcActive())) {
				ssccList.setIndEfcActive(ssccListDto.getIndEfcActive());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getCdPlcmtOptionType())) {
				ssccList.setCdPlcmtOptionType(ssccListDto.getCdPlcmtOptionType());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getCdPlcmtOptionStatus())) {
				ssccList.setCdPlcmtOptionStatus(ssccListDto.getCdPlcmtOptionStatus());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtPlcmtOptionStatus())) {
				ssccList.setDtPlcmtOptionStatus(ssccListDto.getDtPlcmtOptionStatus());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtPlcmtOptionRecorded())) {
				ssccList.setDtPlcmtOptionRecorded(ssccListDto.getDtPlcmtOptionRecorded());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getCdExceptCareStatus())) {
				ssccList.setCdExceptCareStatus(ssccListDto.getCdExceptCareStatus());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getCdPlcmtCircStatus())) {
				ssccList.setCdPlcmtCircStatus(ssccListDto.getCdPlcmtCircStatus());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtPlcmtCircStatus())) {
				ssccList.setDtPlcmtCircStatus(ssccListDto.getDtPlcmtCircStatus());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtPlcmtCircStart())) {
				ssccList.setDtPlcmtCircStart(ssccListDto.getDtPlcmtCircStart());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtPlcmtCircExpire())) {
				ssccList.setDtPlcmtCircExpire(ssccListDto.getDtPlcmtCircExpire());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getCdSvcAuthStatus())) {
				ssccList.setCdSvcAuthStatus(ssccListDto.getCdSvcAuthStatus());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtSvcAuthStatus())) {
				ssccList.setDtSvcAuthStatus(ssccListDto.getDtSvcAuthStatus());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndNonssccSvcAuth())) {
				ssccList.setIndNonssccSvcAuth(ssccListDto.getIndNonssccSvcAuth());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndDaycareValidated())) {
				ssccList.setIndDaycareValidated(ssccListDto.getIndDaycareValidated());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtDaycareValidated())) {
				ssccList.setDtDaycareValidated(ssccListDto.getDtDaycareValidated());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndSsccDaycare())) {
				ssccList.setIndSsccDaycare(ssccListDto.getIndSsccDaycare());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtSsccDaycare())) {
				ssccList.setDtSsccDaycare(ssccListDto.getDtSsccDaycare());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtChildPlanInitiated())) {
				ssccList.setDtChildPlanInitiated(ssccListDto.getDtChildPlanInitiated());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtChildPlanDue())) {
				ssccList.setDtChildPlanDue(ssccListDto.getDtChildPlanDue());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getCdChildPlanDue())) {
				ssccList.setCdChildPlanDue(ssccListDto.getCdChildPlanDue());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndCpContentPropose())) {
				ssccList.setIndCpContentPropose(ssccListDto.getIndCpContentPropose());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtCpContentPropose())) {
				ssccList.setDtCpContentPropose(ssccListDto.getDtCpContentPropose());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndCpContentReject())) {
				ssccList.setIndCpContentReject(ssccListDto.getIndCpContentReject());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtCpContentReject())) {
				ssccList.setDtCpContentReject(ssccListDto.getDtCpContentReject());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndCpContentApprove())) {
				ssccList.setIndCpContentApprove(ssccListDto.getIndCpContentApprove());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtCpContentApprove())) {
				ssccList.setDtCpContentApprove(ssccListDto.getDtCpContentApprove());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndCpContentUnlock())) {
				ssccList.setIndCpContentUnlock(ssccListDto.getIndCpContentUnlock());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtCpContentUnlock())) {
				ssccList.setDtCpContentUnlock(ssccListDto.getDtCpContentUnlock());

			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndCpContentRepropose())) {
				ssccList.setIndCpContentRepropose(ssccListDto.getIndCpContentRepropose());
			}

			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtCpContentRepropose())) {
				ssccList.setDtCpContentRepropose(ssccListDto.getDtCpContentRepropose());
			}
			if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtChildPlanAprv())) {
				ssccList.setDtChildPlanAprv(ssccListDto.getDtChildPlanAprv());
			}
			sessionFactory.getCurrentSession().saveOrUpdate(ssccList);
			updatedResult++;

		}
		if (updatedResult < 1) {
			throw new DataLayerException(messageSource.getMessage("SSCCListImpl.updateSSCCList", null, Locale.US));
		}

		return updatedResult;
	}

	@Override
	public SSCCListDto populateSSCCListValueBeanForFixerUpdate(SSCCRefDto ssccRefDto, SSCCListDto ssccListDto,
			UserProfileDto userProfileDto) {
		if (!ServiceConstants.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())
				&& ServiceConstants.CSSCCSUB_SA.equals(ssccRefDto.getCdSSCCRefSubtype())) {
			ssccListDto.setCdChildPlanDue(ServiceConstants.CSSCCCPP_10);
			if (!ServiceConstants.EMPTY_STRING.equals(ssccRefDto.getSsccRefPlcmtDto().getDtCnsrvtrshpRmvl())
					&& null != ssccRefDto.getSsccRefPlcmtDto().getDtCnsrvtrshpRmvl()) {

				ssccListDto.setDtChildPlanDue(DateUtils.addToDate(ssccRefDto.getSsccRefPlcmtDto().getDtCnsrvtrshpRmvl(),
						new Integer(0).toString(), new Integer(0).toString(), new Integer(40).toString()));
			}
		} else if (!ServiceConstants.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())
				&& !ServiceConstants.CSSCCSUB_SA.equals(ssccRefDto.getCdSSCCRefSubtype())) {
			ssccListDto.setCdChildPlanDue(ServiceConstants.CSSCCCPP_20);

			if (ssccRefDto.getIndPriorComm().equalsIgnoreCase(ServiceConstants.TRUE)) {
				ssccListDto.setDtChildPlanDue(DateUtils.addToDate(ssccRefDto.getDtRecordedDfps(),
						new Integer(0).toString(), new Integer(0).toString(), new Integer(30).toString()));
			} else {
				ssccListDto.setDtChildPlanDue(DateUtils.addToDate(ssccRefDto.getDtRecorded(), new Integer(0).toString(),
						new Integer(0).toString(), new Integer(30).toString()));
			}
		}

		return ssccListDto;
	}

	/**
	 * 
	 * Method Name: insertSSCCList Method Description:Method inserts a row into
	 * the SSCC_LIST table
	 * 
	 * @param ssccListDto
	 * @return SSCCListDto
	 * @throws DataNotFoundException
	 */
	@Override
	public SSCCListDto insertSSCCList(SSCCListDto ssccListDto) throws DataNotFoundException {

		SsccList sscclist = new SsccList();

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtCreated())) {
			sscclist.setDtCreated(ssccListDto.getDtCreated());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIdCreatedPerson())) {
			sscclist.setIdCreatedPerson(ssccListDto.getIdCreatedPerson());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIdLastUpdatePerson())) {
			sscclist.setIdLastUpdatePerson(ssccListDto.getIdLastUpdatePerson());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIdSSCCReferral())) {
			SsccReferral ssccReferral = new SsccReferral();
			ssccReferral.setIdSSCCReferral(ssccListDto.getIdSSCCReferral());
			sscclist.setSsccReferral(ssccReferral);
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndNew())) {
			sscclist.setIndNew(ssccListDto.getIndNew());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndLegalStatusMissing())) {
			sscclist.setIndLegalStatusMissing(ssccListDto.getIndLegalStatusMissing());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtFamilyMemberUpdate())) {
			sscclist.setDtFamilyMemberUpdate(ssccListDto.getDtFamilyMemberUpdate());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndPlcmtSscc())) {
			sscclist.setIndPlcmtSscc(ssccListDto.getIndPlcmtSscc());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIdPlcmtRsrc())) {
			sscclist.setIdPlcmtRsrc(ssccListDto.getIdPlcmtRsrc());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIdPlcmtEvent())) {
			sscclist.setIdPlcmtEvent(ssccListDto.getIdPlcmtEvent());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndEfc())) {
			sscclist.setIndEfc(ssccListDto.getIndEfc());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndEfcActive())) {
			sscclist.setIndEfcActive(ssccListDto.getIndEfcActive());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getCdPlcmtOptionType())) {
			sscclist.setCdPlcmtOptionType(ssccListDto.getCdPlcmtOptionType());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getCdPlcmtOptionStatus())) {
			sscclist.setCdPlcmtOptionStatus(ssccListDto.getCdPlcmtOptionStatus());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtPlcmtOptionStatus())) {
			sscclist.setDtPlcmtOptionStatus(ssccListDto.getDtPlcmtOptionStatus());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtPlcmtOptionRecorded())) {
			sscclist.setDtPlcmtOptionRecorded(ssccListDto.getDtPlcmtOptionRecorded());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getCdExceptCareStatus())) {
			sscclist.setCdExceptCareStatus(ssccListDto.getCdExceptCareStatus());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getCdPlcmtCircStatus())) {
			sscclist.setCdPlcmtCircStatus(ssccListDto.getCdPlcmtCircStatus());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtPlcmtCircStatus())) {
			sscclist.setDtPlcmtCircStatus(ssccListDto.getDtPlcmtCircStatus());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtPlcmtCircStart())) {
			sscclist.setDtPlcmtCircStart(ssccListDto.getDtPlcmtCircStart());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtPlcmtCircExpire())) {
			sscclist.setDtPlcmtCircExpire(ssccListDto.getDtPlcmtCircExpire());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getCdSvcAuthStatus())) {
			sscclist.setCdSvcAuthStatus(ssccListDto.getCdSvcAuthStatus());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtSvcAuthStatus())) {
			sscclist.setDtSvcAuthStatus(ssccListDto.getDtSvcAuthStatus());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndNonssccSvcAuth())) {
			sscclist.setIndNonssccSvcAuth(ssccListDto.getIndNonssccSvcAuth());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndDaycareValidated())) {
			sscclist.setIndDaycareValidated(ssccListDto.getIndDaycareValidated());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtDaycareValidated())) {
			sscclist.setDtDaycareValidated(ssccListDto.getDtDaycareValidated());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndSsccDaycare())) {
			sscclist.setIndSsccDaycare(ssccListDto.getIndSsccDaycare());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtSsccDaycare())) {
			sscclist.setDtSsccDaycare(ssccListDto.getDtSsccDaycare());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtChildPlanInitiated())) {
			sscclist.setDtChildPlanInitiated(ssccListDto.getDtChildPlanInitiated());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtChildPlanDue())) {
			sscclist.setDtChildPlanDue(ssccListDto.getDtChildPlanDue());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getCdChildPlanDue())) {
			sscclist.setCdChildPlanDue(ssccListDto.getCdChildPlanDue());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndCpContentPropose())) {
			sscclist.setIndCpContentPropose(ssccListDto.getIndCpContentPropose());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtCpContentPropose())) {
			sscclist.setDtCpContentPropose(ssccListDto.getDtCpContentPropose());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndCpContentReject())) {
			sscclist.setIndCpContentReject(ssccListDto.getIndCpContentReject());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtCpContentReject())) {
			sscclist.setDtCpContentReject(ssccListDto.getDtCpContentReject());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndCpContentApprove())) {
			sscclist.setIndCpContentApprove(ssccListDto.getIndCpContentApprove());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtCpContentApprove())) {
			sscclist.setDtCpContentApprove(ssccListDto.getDtCpContentApprove());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndCpContentUnlock())) {
			sscclist.setIndCpContentUnlock(ssccListDto.getIndCpContentUnlock());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtCpContentUnlock())) {
			sscclist.setDtCpContentUnlock(ssccListDto.getDtCpContentUnlock());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getIndCpContentRepropose())) {
			sscclist.setIndCpContentRepropose(ssccListDto.getIndCpContentRepropose());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtCpContentRepropose())) {
			sscclist.setDtCpContentRepropose(ssccListDto.getDtCpContentRepropose());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtChildPlanAprv())) {
			sscclist.setDtChildPlanAprv(ssccListDto.getDtChildPlanAprv());
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccListDto.getDtLastUpdate())) {
			sscclist.setDtLastUpdate(ssccListDto.getDtLastUpdate());
		}

		Long primaryKey = (Long) sessionFactory.getCurrentSession().save(sscclist);

		if (TypeConvUtil.isNullOrEmpty(primaryKey)) {
			throw new DataNotFoundException(
					messageSource.getMessage("SSCCListDao.insertSSCCList.notInserted", null, Locale.US));
		}
		return ssccListDto;
	}

	/**
	 * Method fetches a record from the SSCC_LIST table using Primary Key
	 * 
	 * @param idSSCCReferral
	 * @return SSCCListDto
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<SSCCListDto> fetchSSCCList(long idSSCCReferral) {
		List<SsccList> ssccList;
		List<SSCCListDto> ssccListDto = new ArrayList<>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccList.class)
				.add(Restrictions.eq("ssccReferral.idSSCCReferral", idSSCCReferral));
		ssccList = (List<SsccList>) criteria.list();
		if (CollectionUtils.isNotEmpty(ssccList) && !ObjectUtils.isEmpty(ssccList)) {
			ssccList.forEach(ssccLst -> {
				if (!ObjectUtils.isEmpty(ssccLst)) {
					SSCCListDto ssccDto = new SSCCListDto();
					BeanUtils.copyProperties(ssccLst, ssccDto);
					ssccDto.setIdSSCCReferral(idSSCCReferral);
					ssccListDto.add(ssccDto);
				}
			});
		}
		return ssccListDto;
	}

	/**
	 * 
	 * Method Name: getSSCCListResults Method Description:Fetches the SSCC LIST
	 * Search Results and sets it into the PaginationResultsBean object.
	 * 
	 * @param ssccListHeaderDto
	 * 
	 * @return PaginationResultDto
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SSCCListDto> getSSCCListResults(SSCCListHeaderDto ssccListHeaderDto) {

		List<SSCCListDto> ssccListResults = new ArrayList<>();
		Long loggedinUserId = ServiceConstants.LongZero;
		if (!ObjectUtils.isEmpty(ssccListHeaderDto.getIndAssignedTo())
				&& ServiceConstants.Y.equals(ssccListHeaderDto.getIndAssignedTo())) {
			loggedinUserId = ssccListHeaderDto.getIdUser();
		}

		SQLQuery sQLQuery1 = sessionFactory.getCurrentSession().createSQLQuery(getSearchSQL(ssccListHeaderDto));
		sQLQuery1.addScalar("nmStage", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("nmPlcmtResource", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdSsccCatchment", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtDischargeActual", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("idSsccList", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("dtCreated", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("idCreatedPerson", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idSSCCReferral", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("indLegalStatusMissing", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtFamilyMemberUpdate", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("indPlcmtSscc", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("idPlcmtRsrc", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idPlcmtEvent", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("indEfc", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indEfcActive", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdPlcmtOptionType", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtPlcmtOptionStatus", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtPlcmtOptionRecorded", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("cdExceptCareStatus", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdPlcmtCircStatus", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtPlcmtCircStart", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtPlcmtCircExpire", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtPlcmtCircStatus", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("cdSvcAuthStatus", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtSvcAuthStatus", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("indNonssccSvcAuth", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtDaycareValidated", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("indSsccDaycare", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtSsccDaycare", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtChildPlanInitiated", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtChildPlanDue", StandardBasicTypes.DATE);
		sQLQuery1.addScalar(ServiceConstants.IDPERSON, StandardBasicTypes.LONG);
		sQLQuery1.addScalar(ServiceConstants.IDSTAGE, StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idCase", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("indLinkedSvcAuthData", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdSSCCRefType", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtRecorded", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("cdSSCCRefSubtype", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdRefStatus", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdRefSubStatus", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtLinkedSvcAuthData", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("indRefAcknowledge", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtDischargePlanned", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("cdChildPlanDue", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indCpContentPropose", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtCpContentPropose", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("indCpContentReject", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtCpContentReject", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtCpContentApprove", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("indCpContentApprove", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indCpContentUnlock", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtCpContentUnlock", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("indCpContentRepropose", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtCpContentRepropose", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtChildPlanAprv", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("cdPlcmtOptionStatus", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdPlcmtOptionType", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtReferral", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("indNew", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("efcTotal", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("efcFY", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("cdLegalCounty", StandardBasicTypes.STRING);

		sQLQuery1.setParameter("idSsccCatchment", ssccListHeaderDto.getIdCatchment());
		if (!TypeConvUtil.isNullOrEmpty(ssccListHeaderDto.getIndAssignedTo())
				&& "ALL".equalsIgnoreCase(ssccListHeaderDto.getIndAssignedTo())) {
			sQLQuery1.setParameter("dtBegin", ssccListHeaderDto.getDtBegin());
			sQLQuery1.setParameter("dtEnd", ssccListHeaderDto.getDtEnd());
			sQLQuery1.setParameter("cdUnit", ssccListHeaderDto.getCdUnit());
		}
		if (loggedinUserId > 0) {
			sQLQuery1.setParameter("loggedinUserId", loggedinUserId);
		}
		sQLQuery1.setResultTransformer(Transformers.aliasToBean(SSCCListDto.class));
		if (!ObjectUtils.isEmpty(sQLQuery1) && CollectionUtils.isNotEmpty(sQLQuery1.list())) {
			ssccListResults = sQLQuery1.list();
			ssccListResults.forEach(ssccListDto -> {
				if (!ObjectUtils.isEmpty(ssccListDto)) {
					SSCCRefDto ssccRefDto = new SSCCRefDto();
					BeanUtils.copyProperties(ssccListDto, ssccRefDto);
					ssccListDto.setSsccRefDto(ssccRefDto);
					if (!ObjectUtils.isEmpty(ssccRefDto)) {
						SSCCResourceDto ssccResourceDto = new SSCCResourceDto();
						BeanUtils.copyProperties(ssccListDto, ssccResourceDto);
						ssccRefDto.setSsccResourceDto(ssccResourceDto);
						SSCCRefPlcmtDto ssccRefPlcmtDto = new SSCCRefPlcmtDto();
						BeanUtils.copyProperties(ssccListDto, ssccRefPlcmtDto);
						ssccRefDto.setSsccRefPlcmtDto(ssccRefPlcmtDto);
					}

				}
			});
		}
		return ssccListResults;
	}

	/**
	 * Method Name: getSearchSQL Method Description:returns String of sql query
	 * 
	 * @param ssccListHeaderDto
	 * @return String
	 */
	private String getSearchSQL(SSCCListHeaderDto ssccListHeaderDto) {
		StringBuilder selectSsccList = new StringBuilder(sqlFetchSsccMonitoringList);
		List<String> selectSSCCSql = Arrays.asList(sqlCaseForPlcmtStatus, sqlCaseForPlcmtStatusDecode,
				sqlCaseForReferralDate, sqlCaseRefIndicator, sqlFetchEfcTot, sqlFetchEfcFy, sqlCaseFetchLegalCnty);
		selectSsccList = selectSsccList.append(String.join(ServiceConstants.SPACE, selectSSCCSql));

		if (ServiceConstants.Y.equals(ssccListHeaderDto.getIndAssignedTo())) {
			selectSsccList = selectSsccList.append(ServiceConstants.SPACE);
			selectSsccList = selectSsccList.append(sqlSsccMonitoringListFromClauseAssignToUser);
		} else {
			selectSsccList = selectSsccList.append(ServiceConstants.SPACE);
			selectSsccList = selectSsccList.append(sqlSsccMonitoringListFromClauseAssignToAll);
		}
		selectSsccList = selectSsccList.append(ServiceConstants.SPACE);
		selectSsccList = selectSsccList.append(sqlSsccMonitoringListWhereClause);
		if (ServiceConstants.Y.equals(ssccListHeaderDto.getIndAssignedTo())) {
			selectSsccList = selectSsccList.append(ServiceConstants.SPACE);
			selectSsccList = selectSsccList.append(sqlSsccMonitoringListIncludeOnlyAssignedToUser);
		} else {
			selectSsccList = selectSsccList.append(ServiceConstants.SPACE);
			selectSsccList = selectSsccList.append(sqlSsccMonitoringListWhereClauseWithDates);
		}
		if (ServiceConstants.Y.equals(ssccListHeaderDto.getIndIncludeDischargedRef())
				&& ServiceConstants.Y.equals(ssccListHeaderDto.getIndIncludeRescindRef())) {
			selectSsccList = selectSsccList.append(ServiceConstants.SPACE);
			selectSsccList = selectSsccList.append(sqlSsccMonitoringListIncludeAll);
		} else {
			if (ServiceConstants.Y.equals(ssccListHeaderDto.getIndIncludeDischargedRef())) {
				selectSsccList = selectSsccList.append(ServiceConstants.SPACE);
				selectSsccList = selectSsccList.append(sqlSsccMonitoringListIncludeDischarged);
			} else if (ServiceConstants.Y.equals(ssccListHeaderDto.getIndIncludeRescindRef())) {
				selectSsccList = selectSsccList.append(ServiceConstants.SPACE);
				selectSsccList = selectSsccList.append(sqlSsccMonitoringListIncludeRescinded);
			} else {
				selectSsccList = selectSsccList.append(ServiceConstants.SPACE);
				selectSsccList = selectSsccList.append(sqlSsccMonitoringListIncludeActiveOnly);
			}
		}
		if ((ServiceConstants.N.equals(ssccListHeaderDto.getIndDisplayChildPlcmtRef())
				|| ServiceConstants.N.equals(ssccListHeaderDto.getIndDisplayFamRef()))
				&& (ServiceConstants.Y.equals(ssccListHeaderDto.getIndDisplayChildPlcmtRef())
						|| ServiceConstants.Y.equals(ssccListHeaderDto.getIndDisplayFamRef()))) {
			if (ServiceConstants.Y.equals(ssccListHeaderDto.getIndDisplayChildPlcmtRef())) {
				selectSsccList = selectSsccList.append(ServiceConstants.SPACE);
				selectSsccList = selectSsccList.append(sqlSsccMonitoringListIncludePlcmtRefOnly);
			} else if (ServiceConstants.Y.equals(ssccListHeaderDto.getIndDisplayFamRef())) {
				selectSsccList = selectSsccList.append(ServiceConstants.SPACE);
				selectSsccList = selectSsccList.append(sqlSsccMonitoringListIncludeFamRefOnly);
			}
		}
		selectSsccList = selectSsccList.append(ServiceConstants.SPACE);
		selectSsccList = selectSsccList.append(sqlSsccMonitoringLinkResource);
		
		selectSsccList = selectSsccList.append(ServiceConstants.SQL_ORDER_BY_DEFAULT);
		selectSsccList = selectSsccList.append(ServiceConstants.SPACE);
		
		return selectSsccList.toString();
	}

	/**
	 * 
	 * Method Name: fetchRegionUnit Method Description:Fetch all the units from
	 * the unit table and return a list.
	 * 
	 * @return List<Option>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Option> fetchRegionUnit() {
		List<Option> unitStringList = new ArrayList<>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Unit.class);
		criteria.add(Restrictions.eq("cdUnitProgram", ServiceConstants.CPS_PROGRAM));
		criteria.addOrder(Order.asc("nbrUnit"));

		List<Unit> unitList = criteria.list();
		if (!TypeConvUtil.isNullOrEmpty(unitList)) {
			unitList.forEach(unit -> {
				Option option = new Option();
				option.setCode(unit.getCdUnitRegion() + ServiceConstants.ZIP_CODE + unit.getNbrUnit());
				option.setDecode(unit.getNbrUnit());
				unitStringList.add(option);
			});
		}
		return unitStringList;
	}

	/**
	 * 
	 * Method Name: isValidSSCCCatchmentRegion Method Description:Returns true
	 * if the region is a valid SSCC Catchment region
	 * 
	 * @param cdSSCCCntrctRegion
	 * @return Boolean
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Boolean isValidSSCCCatchmentRegion(String cdSSCCCntrctRegion) {
		Boolean isValidCatchmentReqion = Boolean.FALSE;
		String cdSSCCRegion = null;
		if (!ObjectUtils.isEmpty(cdSSCCCntrctRegion) && cdSSCCCntrctRegion.length() == 3
				&& cdSSCCCntrctRegion.charAt(0) == '0') {
			cdSSCCRegion = cdSSCCCntrctRegion.substring(1);
		}
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isValidSSCCCatchmentRegion)
				.addScalar("cdCntrctRegion", StandardBasicTypes.STRING).setParameter("cdCntrctRegion", cdSSCCRegion)
				.setResultTransformer(Transformers.aliasToBean(SSCCParameterDto.class));
		List<SSCCParameterDto> liSsccParameters = sQLQuery1.list();
		if (!TypeConvUtil.isNullOrEmpty(liSsccParameters)) {
			isValidCatchmentReqion = Boolean.TRUE;
		}
		return isValidCatchmentReqion;
	}

	/**
	 * Method Name: fetchValidUnitRegionforSSCCUser Method Description:Fetches a
	 * list of valid Regions for a SSCC user
	 * 
	 * @param userID
	 * @return List<String>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> fetchValidUnitRegionforSSCCUser(Long userID) {
		List<String> validRegionsForSSCCUser = new ArrayList<>();
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(fetchValidUnitRegionforSSCCUser).setParameter("userID", userID);
		List<String> validRegionforSSCCUser = sqlQuery.list();
		if (!TypeConvUtil.isNullOrEmpty(validRegionforSSCCUser)) {
			validRegionforSSCCUser.forEach(cdRegion -> {
				if (!ObjectUtils.isEmpty(cdRegion) && cdRegion.length() == 3 && cdRegion.charAt(0) == '0') {
					cdRegion = cdRegion.substring(1);
				}
				validRegionsForSSCCUser.add(cdRegion);
			});
		}
		return validRegionsForSSCCUser;
	}

	/**
	 * Method Name: fetchValidSSCCRegion Method Description:Fetches a list of
	 * valid SSCC Regions to be displayed in the Region dropdown
	 * 
	 * @return List<String>
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> fetchValidSSCCRegion() {
		List<String> validSSCCRegion;
		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(fetchValidSSCCRegion);
		validSSCCRegion = sqlQuery.list();
		if (TypeConvUtil.isNullOrEmpty(validSSCCRegion)) {
			throw new DataNotFoundException(
					messageSource.getMessage("SSCCListDao.fetchValidSSCCRegion.NotFound", null, Locale.US));
		} else {
			validSSCCRegion = validSSCCRegion.stream().filter(cdRegion -> (!ObjectUtils.isEmpty(cdRegion)))
					.collect(Collectors.toList());
		}
		return validSSCCRegion;

	}

	@Override
	public String fetchCdCatchmentFromIdCatchment(Long idSsccCatchment) {
		String cdCatchment = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(sqlFetchCdCatchmentFromIdCatchment).setParameter("idSsccCatchment", idSsccCatchment);

		if (!ObjectUtils.isEmpty(sqlQuery.uniqueResult())) {
			cdCatchment = (String) sqlQuery.uniqueResult();
		}
		return cdCatchment;

	}

	@Override
	public Long fetchIdCatchmentFromCdCatchment(String cdCatchment) {
		Long idCatchment = 0l;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(sqlFetchIdCatchmentFromCdCatchment).setParameter("cdCatchment", cdCatchment);
		if (!ObjectUtils.isEmpty(sqlQuery.uniqueResult())) {
			idCatchment = ((BigDecimal) sqlQuery.uniqueResult()).longValue();
		}
		return idCatchment;
	}

	@Override
	public String fetchDefaultCatchmentForSSCCUser(Long idPerson) {
		String cdCatchment = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(sqlFetchDefaultCatchmentForSSCCUser).setParameter("idPerson", idPerson);
		if (!ObjectUtils.isEmpty(sqlQuery.uniqueResult())) {
			cdCatchment = (String) sqlQuery.uniqueResult();
		}
		return cdCatchment;
	}

	/**
	 * Method Name: fetchDefaultSSCCCatchmentForDFPSUser Method Description:
	 * Fetch Default SSCC Catchment for DFPS User from SSCC_REFERRAL
	 * 
	 * @param idWkldPerson
	 * @return
	 */
	@Override
	public Long fetchDefaultSSCCCatchmentForDFPSUser(Long idWkldPerson) {
		Long idSSCCCatchment = 0l;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(sqlFetchDefaultSSCCCatchmentForDFPSUser).setParameter("idWkldPerson", idWkldPerson);
		/*if (!ObjectUtils.isEmpty(sqlQuery.uniqueResult()) && ((BigDecimal) sqlQuery.uniqueResult()).longValue() > 0) {
			idSSCCCatchment = ((BigDecimal) sqlQuery.uniqueResult()).longValue();
		}*/
		if (!ObjectUtils.isEmpty(sqlQuery.list())){
			List<BigDecimal> ssccCatchmentList = sqlQuery.list();
			idSSCCCatchment = ssccCatchmentList.get(0).longValue();
		}
		return idSSCCCatchment;
	}

	/**
	 * Method Name: fetchCatchmentsForRegion Method Description: fetches
	 * catchments from sscc_parameters based on region
	 * 
	 * @param cdContractRegion
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> fetchCatchmentsForRegion(String cdContractRegion) {
		List<String> ssccCatchments = new ArrayList<>();
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlFetchCatchmentsForRegion)
				.setParameter("cdContractRegion", cdContractRegion);
		if (!ObjectUtils.isEmpty(sqlQuery.list())) {
			ssccCatchments = sqlQuery.list();
		}
		return ssccCatchments;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean hasStageAccess(Long ulIdStage, Long idUser) {
		Boolean hasStageAccess = Boolean.FALSE;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlHasStageAccess);
		query.setParameter("idStage", ulIdStage);
		query.setParameter("idPerson", idUser);
		List<Long> results = query.list();
		if (!ObjectUtils.isEmpty(results.size())) {
			hasStageAccess = Boolean.TRUE;
		}
		return hasStageAccess;
	}

}
