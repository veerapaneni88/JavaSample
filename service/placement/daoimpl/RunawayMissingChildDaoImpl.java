/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Feb 15, 2018- 3:21:12 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.placement.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.MsngChildDtl;
import us.tx.state.dfps.common.domain.MsngChildRcvryDtl;
import us.tx.state.dfps.common.domain.MsngChildRcvryNotifctn;
import us.tx.state.dfps.common.domain.MsngChildRnwyRsn;
import us.tx.state.dfps.service.admin.dto.LegalStatusInDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.placement.dao.RunawayMissingChildDao;
import us.tx.state.dfps.service.placement.dto.*;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 15, 2018- 3:21:12 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
@SuppressWarnings("unchecked")
public class RunawayMissingChildDaoImpl implements RunawayMissingChildDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${RunawayMissingChildDaoImpl.fetchRunawayMissingList}")
	private transient String fetchRunawayMissingList;

	@Value("${RunawayMissingChildDaoImpl.fetchChildRecoveryId}")
	private transient String fetchChildRecoveryId;

	@Value("${RunawayMissingChildDaoImpl.fetchRecordsForValidation}")
	private transient String fetchRecordsForValidation;

	@Value("${RunawayMissingChildDaoImpl.joinSubQuery}")
	private transient String joinSubQuery;

	@Value("${RunawayMissingChildDaoImpl.joinORDERClause}")
	private transient String joinORDERClause;

	@Value("${RunawayMissingChildDaoImpl.childRecoveryExist}")
	private transient String childRecoveryExistSql;

	@Value("${RunawayMissingChildDaoImpl.isChildRecoveryExistsSql}")
	private transient String isChildRecoveryExistsSql;
	
	@Value("${RunawayMissingChildDaoImpl.fetchPrimaryPerson}")
	private transient String fetchPrimaryPersonSql;
	
	@Value("${RunawayMissingChildDaoImpl.getRecoveryDate}")
	private transient String getRecoveryDate;

	@Value("${RunawayMissingChildDaoImpl.fetchMissingChildIds}")
	private transient String fetchMissingChildIds;

	@Value("${RunawayMissingChildDaoImpl.fetchChildRecoveryIds}")
	private transient String fetchChildRecoveryIds;

	@Value("${RunawayMissingChildDaoImpl.fetchDtRemovalSql}")
	private transient String fetchDtRemovalSql;



	/**
	 * 
	 * Method Name: fetchRunawayMissingList Method Description: retrieves a full
	 * row from CHILD_MSNG_DTL and CHILD_RECOVERY_DTL table for given case and
	 * stage
	 * 
	 * @param commonHelperReq
	 * @return List<RunawayMissingDto>
	 */
	@Override
	public List<RunawayMissingDto> fetchRunawayMissingList(CommonHelperReq commonHelperReq) {
		List<RunawayMissingDto> runawayMsngDtoList = new ArrayList<>();
    Query query =
        sessionFactory
            .getCurrentSession()
            .createSQLQuery(fetchRunawayMissingList)
            .addScalar("idChldMsngDtl", StandardBasicTypes.LONG)
            .addScalar("idChldMsngEvent", StandardBasicTypes.LONG)
            .addScalar("idChldRCVRYDtl", StandardBasicTypes.LONG)
            .addScalar("idChldRcvryEvent", StandardBasicTypes.LONG)
            .addScalar("dtMsngChld", StandardBasicTypes.DATE)
            .addScalar("dtRetrndChld", StandardBasicTypes.DATE)
            .addScalar("dtEnd", StandardBasicTypes.DATE)
            .addScalar("cdMissingEventType", StandardBasicTypes.STRING)
            .addScalar("cdMissingEventStatus", StandardBasicTypes.STRING)
            .addScalar("cdRecoveryEventStatus", StandardBasicTypes.STRING)
            .addScalar("cdReasonNotRtrnd", StandardBasicTypes.STRING)
            .addScalar("indChildReturn", StandardBasicTypes.STRING)
            .setResultTransformer(Transformers.aliasToBean(RunawayMissingDto.class));
		query.setParameter("idCase", commonHelperReq.getIdCase());
		query.setParameter("idStage", commonHelperReq.getIdStage());
		runawayMsngDtoList = query.list();

		return runawayMsngDtoList;
	}

	/**
	 * 
	 * Method Name: fetchMissingChildDetail Method Description: Retrieves
	 * MissingChildDetail from CHILD_MSNG_DTL table for given ID_CHILD_MSNG_DTL
	 * 
	 * @param idChldMsngDtl
	 * @return MissingChildDetailDto
	 *
	 */
	@Override
	public MissingChildDetailDto fetchMissingChildDetail(Long idChldMsngDtl, Long idEvent) {
		MissingChildDetailDto msngChldDtlDto = new MissingChildDetailDto();
		MsngChildDtl msngChldDtl = new MsngChildDtl();
		if (!ObjectUtils.isEmpty(idChldMsngDtl) && idChldMsngDtl > ServiceConstants.ZERO_VAL) {
			msngChldDtl = (MsngChildDtl) sessionFactory.getCurrentSession().get(MsngChildDtl.class, idChldMsngDtl);
		} else if (!ObjectUtils.isEmpty(idEvent) && idEvent > ServiceConstants.ZERO_VAL) {
			msngChldDtl = (MsngChildDtl) sessionFactory.getCurrentSession().createCriteria(MsngChildDtl.class)
					.add(Restrictions.eq("idEvent", idEvent)).uniqueResult();
		}
		if (!ObjectUtils.isEmpty(msngChldDtl) && !ObjectUtils.isEmpty(msngChldDtl.getIdChldMsngDtl())) {
			BeanUtils.copyProperties(msngChldDtl, msngChldDtlDto);
		}
		return msngChldDtlDto;
	}

	/**
	 * 
	 * Method Name: fetchNotificationDetail Method Description: Retrieves
	 * NotificationPartiesDetail from NOTIFCTN_PARTIES table for given ID_EVENT
	 * 
	 * @param idEvent
	 * @return NotificationPartiesDto
	 *
	 */
	@Override
	public NotificationPartiesDto fetchNotificationDetail(Long idEvent) {
		NotificationPartiesDto notificationPartiesDto = new NotificationPartiesDto();
		MsngChildRcvryNotifctn msngNotify = (MsngChildRcvryNotifctn) sessionFactory.getCurrentSession()
				.createCriteria(MsngChildRcvryNotifctn.class).add(Restrictions.eq("idEvent", idEvent)).uniqueResult();
		if (!ObjectUtils.isEmpty(msngNotify) && !ObjectUtils.isEmpty(msngNotify.getIdNoficationParty())) {
			BeanUtils.copyProperties(msngNotify, notificationPartiesDto);
		}
		return notificationPartiesDto;
	}

	/**
	 * 
	 * Method Name: fetchChildRecoveryDetail Method Description: Retrieves Child
	 * Recovery detail from CHILD_RECOVERY_DTL table for given ID_CHILD_MSNG_DTL
	 * 
	 * @param idChldMsngDtl
	 * @return ChildRecoveryDetailDto
	 *
	 */
	@Override
	public ChildRecoveryDetailDto fetchChildRecoveryDetail(Long idChldMsngDtl, Long idEvent) {
		ChildRecoveryDetailDto childRecoveryDetail = new ChildRecoveryDetailDto();
		MsngChildRcvryDtl msngChldRcvryDtl = new MsngChildRcvryDtl();
		if (!ObjectUtils.isEmpty(idChldMsngDtl) && idChldMsngDtl > ServiceConstants.ZERO_VAL) {
			Query query = sessionFactory.getCurrentSession().createSQLQuery(fetchChildRecoveryId)
					.addScalar("idChldRecoveryDtl", StandardBasicTypes.LONG)
					.setResultTransformer(Transformers.aliasToBean(ChildRecoveryDetailDto.class));
			query.setParameter("idChldMsngDtl", idChldMsngDtl);
			ChildRecoveryDetailDto childRecoveryDtl = (ChildRecoveryDetailDto) query.uniqueResult();

			if (!ObjectUtils.isEmpty(childRecoveryDtl) && !ObjectUtils.isEmpty(childRecoveryDtl.getIdChldRecoveryDtl())
					&& ServiceConstants.ZERO_VAL != childRecoveryDtl.getIdChldRecoveryDtl()) {
				msngChldRcvryDtl = (MsngChildRcvryDtl) sessionFactory.getCurrentSession().get(MsngChildRcvryDtl.class,
						childRecoveryDtl.getIdChldRecoveryDtl());

			}
		} else if (!ObjectUtils.isEmpty(idEvent) && idEvent > ServiceConstants.ZERO_VAL) {
			msngChldRcvryDtl = (MsngChildRcvryDtl) sessionFactory.getCurrentSession()
					.createCriteria(MsngChildRcvryDtl.class).add(Restrictions.eq("idEvent", idEvent)).uniqueResult();
		}
		if (!ObjectUtils.isEmpty(msngChldRcvryDtl)) {
			BeanUtils.copyProperties(msngChldRcvryDtl, childRecoveryDetail);
		}
		if (!ObjectUtils.isEmpty(msngChldRcvryDtl) && !ObjectUtils.isEmpty(msngChldRcvryDtl.getMsngChildDtl())
				&& !ObjectUtils.isEmpty(msngChldRcvryDtl.getMsngChildDtl().getIdChldMsngDtl())
				&& msngChldRcvryDtl.getMsngChildDtl().getIdChldMsngDtl() > ServiceConstants.ZERO_VAL) {
			childRecoveryDetail.setIdChldMsngDtl(msngChldRcvryDtl.getMsngChildDtl().getIdChldMsngDtl());
		}
		return childRecoveryDetail;
	}

	/**
	 * 
	 * Method Name: fetchChildAbsenceReason Method Description:Fetch the Child
	 * Confirmed Reason for Absence detail for idChldRecoveryDtl from
	 * CHILD_CNFRMD_RSN_ABSENCE table from NOTIFCTN_PARTIES table through
	 * DaoImpl
	 * 
	 * @param idChldRecoveryDtl
	 * @return List<ChildAbsenceReasonDto>
	 */
	@Override
	public List<ChildAbsenceReasonDto> fetchChildAbsenceReason(Long idChldRecoveryDtl) {
		List<ChildAbsenceReasonDto> chldAbsReasonList = new ArrayList<ChildAbsenceReasonDto>();
		List<MsngChildRnwyRsn> msngChildRnwyRsnList = new ArrayList<>();
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(MsngChildRnwyRsn.class)
				.add(Restrictions.eq("msngChildRcvryDtl.idChldRecoveryDtl", idChldRecoveryDtl));
		msngChildRnwyRsnList = (List<MsngChildRnwyRsn>) cr.list();
		if (!ObjectUtils.isEmpty(msngChildRnwyRsnList)) {
			msngChildRnwyRsnList.forEach(runawayRsn -> {

				if (!ObjectUtils.isEmpty(runawayRsn)) {
					ChildAbsenceReasonDto runawayReason = new ChildAbsenceReasonDto();
					BeanUtils.copyProperties(runawayRsn, runawayReason);
					chldAbsReasonList.add(runawayReason);
				}
			});
		}
		return chldAbsReasonList;
	}

	/**
	 * 
	 * Method Name: saveMissingChildDetail Method Description: This method used
	 * to save missing child detail into MSGN_CHLD_DTL
	 * 
	 * @param msngChildDetailDto
	 * @param cReqFun
	 * @return MissingChildDetailDto
	 */
	@Override
	public MissingChildDetailDto saveMissingChildDetail(MissingChildDetailDto msngChildDetailDto, String cReqFun) {
		MsngChildDtl msngChldDtl = new MsngChildDtl();
		if (!ObjectUtils.isEmpty(msngChildDetailDto.getIdChldMsngDtl())) {
		// Artifact: artf162914 - ALM Defect#15379 : Load the saved entity before updating to retain DT_created value.
		// Removed setting dtCreated and idCreatedPerson from update case.
			msngChldDtl = (MsngChildDtl) sessionFactory.getCurrentSession().load(MsngChildDtl.class, msngChildDetailDto.getIdChldMsngDtl());
		}
		msngChldDtl.setCdMissingFrom(msngChildDetailDto.getCdMissingFrom());
		msngChldDtl.setMsngFromOther(msngChildDetailDto.getMsngFromOther());
		msngChldDtl.setIndChildReturn(msngChildDetailDto.getIndChildReturn());
		msngChldDtl.setCdReasonNotRtrnd(msngChildDetailDto.getCdReasonNotRtrnd());
		msngChldDtl.setCdSuspectedAbsence(msngChildDetailDto.getCdSuspectedAbsence());
		msngChldDtl.setIdEvent(msngChildDetailDto.getIdEvent());
		msngChldDtl.setDtChildMissing(msngChildDetailDto.getDtChildMissing());
		msngChldDtl.setDtEnd(msngChildDetailDto.getDtEnd());
		msngChldDtl.setDtWorkerNotified(msngChildDetailDto.getDtWorkerNotified());
		msngChldDtl.setIndAmberAlrtIsud(msngChildDetailDto.getIndAmberAlrtIsud());
		msngChldDtl.setIndNCMECPublctn(msngChildDetailDto.getIndNCMECPublctn());
		msngChldDtl.setIndCscalIsud(msngChildDetailDto.getIndCscalIsud());
		msngChldDtl.setIdPerson(msngChildDetailDto.getIdPerson());
		msngChldDtl.setIndMsngPriorToRmvl(msngChildDetailDto.getIndMsngPriorToRmvl());
		msngChldDtl.setNcicNumber(msngChildDetailDto.getNcicNumber());
		msngChldDtl.setNcmecNumber(msngChildDetailDto.getNcmecNumber());
		msngChldDtl.setDtLastUpdate(new Date());
		msngChldDtl.setIndMsngPriorToRmvl(msngChildDetailDto.getIndMsngPriorToRmvl());
		switch (cReqFun) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			msngChldDtl.setIdCreatedPerson(msngChildDetailDto.getIdCreatedPerson());
			msngChldDtl.setDtCreated(new Date());
			msngChldDtl.setIdLastUpdatePerson(msngChildDetailDto.getIdCreatedPerson());
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			msngChldDtl.setIdLastUpdatePerson(msngChildDetailDto.getIdCreatedPerson());
			break;
		default:
			break;
		}
		sessionFactory.getCurrentSession().saveOrUpdate(msngChldDtl);
		msngChildDetailDto.setIdChldMsngDtl(msngChldDtl.getIdChldMsngDtl());
		msngChildDetailDto.setDtLastUpdate(msngChldDtl.getDtLastUpdate());
		msngChildDetailDto.setDtCreated(msngChldDtl.getDtCreated());
		msngChildDetailDto.setIdLastUpdatePerson(msngChldDtl.getIdLastUpdatePerson());

		return msngChildDetailDto;
	}

	@Override
	public void deleteMissingChildDetail(MissingChildDetailDto msngChildDetailDto) {
		MsngChildDtl msngChldDtl = new MsngChildDtl();
		if (!ObjectUtils.isEmpty(msngChildDetailDto.getIdChldMsngDtl())) {
			// Artifact: artf162914 - ALM Defect#15379 : Load the saved entity before updating to retain DT_created value.
			// Removed setting dtCreated and idCreatedPerson from update case.
			msngChldDtl = (MsngChildDtl) sessionFactory.getCurrentSession().load(MsngChildDtl.class, msngChildDetailDto.getIdChldMsngDtl());
			if(msngChldDtl !=null ) {
				sessionFactory.getCurrentSession().delete(msngChldDtl);
			}
		}
		return;
	}

	/**
	 * 
	 * Method Name: saveMissingChildDetail Method Description: This method used
	 * to save Notification Party detail into MSNG_CHILD_RCVRY_NOTIFCTN
	 * 
	 * @param notificationPatiesDto
	 * @param cReqFun
	 * @return NotificationPartiesDto
	 */
	@Override
	public NotificationPartiesDto saveNotificationDetail(NotificationPartiesDto notificationPatiesDto, String cReqFun) {
		MsngChildRcvryNotifctn msngNotify = new MsngChildRcvryNotifctn();
		if (!ObjectUtils.isEmpty(notificationPatiesDto.getIdNoficationParty())) {
		// Artifact: artf162914 - ALM Defect#15379 : Load the saved entity before updating to retain DT_created value.
		// Removed setting dtCreated and idCreatedPerson from update case. Remove setting dtPurged, as we never set dtPurge in Java code.
			msngNotify = (MsngChildRcvryNotifctn) sessionFactory.getCurrentSession().load(MsngChildRcvryNotifctn.class, notificationPatiesDto.getIdNoficationParty());
		}
		msngNotify.setIdEvent(notificationPatiesDto.getIdEvent());
		msngNotify.setDtLENotified(notificationPatiesDto.getDtLENotified());
		msngNotify.setDtCourtNotified(notificationPatiesDto.getDtCourtNotified());
		msngNotify.setDtAtrnyNotified(notificationPatiesDto.getDtAtrnyNotified());
		msngNotify.setIndAtrnyNA(notificationPatiesDto.getIndAtrnyNA());
		msngNotify.setDtCasaNotified(notificationPatiesDto.getDtCasaNotified());
		msngNotify.setIndCasaNA(notificationPatiesDto.getIndCasaNA());
		msngNotify.setDtGrdnNotified(notificationPatiesDto.getDtGrdnNotified());
		msngNotify.setIndGrdnNA(notificationPatiesDto.getIndGrdnNA());
		msngNotify.setDtJuvnleNotified(notificationPatiesDto.getDtJuvnleNotified());
		msngNotify.setIndJuvnleNA(notificationPatiesDto.getIndJuvnleNA());
		msngNotify.setDtNCMECNotified(notificationPatiesDto.getDtNCMECNotified());
		msngNotify.setIndNcmecNA(notificationPatiesDto.getIndNcmecNA());
		msngNotify.setDtPrntsatrnyNotified(notificationPatiesDto.getDtPrntsatrnyNotified());
		msngNotify.setIndPrntsatrnyNA(notificationPatiesDto.getIndPrntsatrnyNA());
		msngNotify.setDtPrntsNotified(notificationPatiesDto.getDtPrntsNotified());
		msngNotify.setIndPrntsNA(notificationPatiesDto.getIndPrntsNA());
		msngNotify.setDtSpclINVNTFD(notificationPatiesDto.getDtSpclINVNTFD());
		msngNotify.setDtLastUpdate(new Date());
		switch (cReqFun) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			msngNotify.setIdCreatedPerson(notificationPatiesDto.getIdCreatedPerson());
			msngNotify.setDtCreated(new Date());
			msngNotify.setIdLastUpdatePerson(notificationPatiesDto.getIdCreatedPerson());
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			//artf198170 this can happen because fixer can change childReturn flag from yes to no and then back to yes
			if(msngNotify.getIdCreatedPerson() == null){
				msngNotify.setIdCreatedPerson(notificationPatiesDto.getIdCreatedPerson());
				msngNotify.setDtCreated(new Date());
			}
			msngNotify.setIdLastUpdatePerson(notificationPatiesDto.getIdCreatedPerson());
			break;
		default:
			break;
		}
		sessionFactory.getCurrentSession().saveOrUpdate(msngNotify);
		notificationPatiesDto.setIdNoficationParty(msngNotify.getIdNoficationParty());
		notificationPatiesDto.setDtLastUpdate(msngNotify.getDtLastUpdate());
		notificationPatiesDto.setDtCreated(msngNotify.getDtCreated());
		notificationPatiesDto.setIdLastUpdatePerson(msngNotify.getIdLastUpdatePerson());
		return notificationPatiesDto;
	}

	/**
	 *
	 * Method Name: saveMissingChildDetail Method Description: This method used
	 * to save Notification Party detail into MSNG_CHILD_RCVRY_NOTIFCTN
	 *
	 * @param notificationPatiesDto
	 * @return
	 */
	@Override
	public void deleteNotificationDetail(NotificationPartiesDto notificationPatiesDto) {
		MsngChildRcvryNotifctn msngNotify = new MsngChildRcvryNotifctn();
		if (!ObjectUtils.isEmpty(notificationPatiesDto.getIdNoficationParty())) {
			msngNotify = (MsngChildRcvryNotifctn) sessionFactory.getCurrentSession().load(MsngChildRcvryNotifctn.class, notificationPatiesDto.getIdNoficationParty());
			if(msngNotify != null) {
				sessionFactory.getCurrentSession().delete(msngNotify);
			}
		}
		return;
	}
		/**
         *
         * Method Name: saveChildRecoveryDetail Method Description: This method used
         * to save Notification Party detail into MSNG_CHILD_RCVRY_DTL
         *
         * @param childRecoveryDetail
         * @param cReqFun
         * @return
         */
	@Override
	public ChildRecoveryDetailDto saveChildRecoveryDetail(ChildRecoveryDetailDto childRecoveryDetail, String cReqFun,
			MissingChildDetailDto msngChildDetailDto) {
		MsngChildRcvryDtl msngChldRcvryDtl = new MsngChildRcvryDtl();
		MsngChildDtl msngChldDtl = (MsngChildDtl) sessionFactory.getCurrentSession().get(MsngChildDtl.class,
				(childRecoveryDetail.getIdChldMsngDtl()));
		if (!ObjectUtils.isEmpty(childRecoveryDetail.getIdChldRecoveryDtl())) {
			// Artifact: artf162914 - ALM Defect#15379 : Load the saved entity before updating to retain DT_created value.
			// Removed setting dtCreated and idCreatedPerson from update case. Remove setting dtPurged, as we never set dtPurge in Java code.
			msngChldRcvryDtl = (MsngChildRcvryDtl) sessionFactory.getCurrentSession().load(MsngChildRcvryDtl.class, childRecoveryDetail.getIdChldRecoveryDtl());
		}
		msngChldRcvryDtl.setCdChildRetrnd(childRecoveryDetail.getCdChildRetrnd());
		if (!StringUtils.isEmpty(childRecoveryDetail.getIndRcvryIntrvwCndctd())
				&& !ServiceConstants.Y.equals(childRecoveryDetail.getIndRcvryIntrvwCndctd())) {
			msngChldRcvryDtl.setCdRsnNotIntrvwd(childRecoveryDetail.getCdRsnNotIntrvwd());
		}
		msngChldRcvryDtl.setTxtRsnNotIntrvwd(childRecoveryDetail.getTxtRsnNotIntrvwd());
		msngChldRcvryDtl.setCdSuspectedReason(childRecoveryDetail.getCdSuspectedReason());
		msngChldRcvryDtl.setChildRetrndOther(childRecoveryDetail.getChildRetrndOther());
		msngChldRcvryDtl.setDtChldRetrnd(childRecoveryDetail.getDtChldRetrnd());
		msngChldRcvryDtl.setDtWorkerNotified(childRecoveryDetail.getDtWorkerNotified());
		msngChldRcvryDtl.setDtRecoveryInterviewed(childRecoveryDetail.getDtRecoveryInterviewed());
		msngChldRcvryDtl.setIdEvent(childRecoveryDetail.getIdEvent());
		msngChldRcvryDtl.setIndRcvryIntrvwCndctd(childRecoveryDetail.getIndRcvryIntrvwCndctd());
		msngChldRcvryDtl.setMsngChildDtl(msngChldDtl);
		msngChldRcvryDtl.setIndRcvryIntrvwCndctd(childRecoveryDetail.getIndRcvryIntrvwCndctd());
		msngChldRcvryDtl.setIndVctmztnLbtr(childRecoveryDetail.getIndVctmztnLbtr());
		msngChldRcvryDtl.setIndVctmztnOther(childRecoveryDetail.getIndVctmztnOther());
		msngChldRcvryDtl.setTxtVctmOther(childRecoveryDetail.getTxtVctmOther());
		msngChldRcvryDtl.setIndVctmztnPhab(childRecoveryDetail.getIndVctmztnPhab());
		msngChldRcvryDtl.setIndVctmztnSxab(childRecoveryDetail.getIndVctmztnSxab());
		msngChldRcvryDtl.setIndVctmztnSxtr(childRecoveryDetail.getIndVctmztnSxtr());
		msngChldRcvryDtl.setDtLastUpdated(new Date());
		switch (cReqFun) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			msngChldRcvryDtl.setIdCreatedPerson(childRecoveryDetail.getIdCreatedPerson());
			msngChldRcvryDtl.setDtCreated(new Date());
			msngChldRcvryDtl.setIdLastUpdatePerson(childRecoveryDetail.getIdCreatedPerson());
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			msngChldRcvryDtl.setIdLastUpdatePerson(childRecoveryDetail.getIdCreatedPerson());
			break;
		default:
			break;
		}
		sessionFactory.getCurrentSession().saveOrUpdate(msngChldRcvryDtl);
		childRecoveryDetail.setIdChldRecoveryDtl(msngChldRcvryDtl.getIdChldRecoveryDtl());
		childRecoveryDetail.setDtLastUpdated(msngChldRcvryDtl.getDtLastUpdated());
		childRecoveryDetail.setDtCreated(msngChldRcvryDtl.getDtCreated());
		childRecoveryDetail.setIdLastUpdatePerson(msngChldRcvryDtl.getIdLastUpdatePerson());
		return childRecoveryDetail;
	}

	@Override
	public void deleteChildRecoveryDetail(ChildRecoveryDetailDto childRecoveryDetail,
														  MissingChildDetailDto msngChildDetailDto) {
		if (!ObjectUtils.isEmpty(childRecoveryDetail.getIdChldRecoveryDtl())) {
			MsngChildRcvryDtl msngChldRcvryDtl = (MsngChildRcvryDtl) sessionFactory.getCurrentSession().load(MsngChildRcvryDtl.class, childRecoveryDetail.getIdChldRecoveryDtl());
			if(msngChldRcvryDtl != null){
				sessionFactory.getCurrentSession().delete(msngChldRcvryDtl);
			}
		}

		return;
	}

	/**
	 * 
	 * Method Name: saveConfirmedRsnAbsDetail 
	 * Method Description: This method saves runaway reasons, 
	 * If Recovery Interview conducted is Yes and Confirmed Reason for absence is
	 * RunAway, else delete the runaway reasons if they exists.
	 * 
	 * @param chldAbsenceRsnList
	 * @param childRecoveryDetailDto
	 */
	@Override
	public void saveConfirmedRsnAbsDetail(List<ChildAbsenceReasonDto> chldAbsenceRsnList,
			ChildRecoveryDetailDto childRecoveryDetailDto) {
		if (!ObjectUtils.isEmpty(chldAbsenceRsnList)) {
			if (ServiceConstants.Y.equalsIgnoreCase(childRecoveryDetailDto.getIndRcvryIntrvwCndctd())
					&& CodesConstant.ABSNCRSN_RNAW.equalsIgnoreCase(childRecoveryDetailDto.getCdSuspectedReason())) {
				// If recovery interview conducted and confirmed  reason is runaway.
				MsngChildRcvryDtl msngChldRcvryDtl = (MsngChildRcvryDtl) sessionFactory.getCurrentSession()
						.get(MsngChildRcvryDtl.class, childRecoveryDetailDto.getIdChldRecoveryDtl());
				chldAbsenceRsnList.stream().forEach(chldAbsenceRsn -> {
					if (!StringUtils.isEmpty(chldAbsenceRsn.getCdRwnyReason())
							&& (StringUtils.isEmpty(chldAbsenceRsn.getIdChldRwnyRsn())
									|| ServiceConstants.ZERO_VAL.equals(chldAbsenceRsn.getIdChldRwnyRsn()))) {
						// This is the scenario when a new reason needs to be
						// added.
						MsngChildRnwyRsn msngChldRwnRsn = new MsngChildRnwyRsn();
						msngChldRwnRsn.setCdRwnyReason(chldAbsenceRsn.getCdRwnyReason());
						msngChldRwnRsn.setDtLastUpdate(new Date());
						msngChldRwnRsn.setTxtOtherComments(chldAbsenceRsn.getTxtOtherComments());
						msngChldRwnRsn.setMsngChildRcvryDtl(msngChldRcvryDtl);
						msngChldRwnRsn.setIdCreatedPerson(childRecoveryDetailDto.getIdCreatedPerson());
						msngChldRwnRsn.setIdLastUpdatePerson(childRecoveryDetailDto.getIdCreatedPerson());
						sessionFactory.getCurrentSession().save(msngChldRwnRsn);
					} else if (StringUtils.isEmpty(chldAbsenceRsn.getCdRwnyReason())
							&& !StringUtils.isEmpty(chldAbsenceRsn.getIdChldRwnyRsn())) {
						// Scenario when a previously selected reason is
						// de-selected and save is clicked.
						sessionFactory.getCurrentSession().delete(sessionFactory.getCurrentSession()
								.load(MsngChildRnwyRsn.class, chldAbsenceRsn.getIdChldRwnyRsn()));
					}
				});
			} else {
				// Scenario when a previously reason was selected but the
				// interview question is changed to No or confirmed reason is changed from runaway to missing/abducted. 
				// All previously selected reason must be cleared.
				chldAbsenceRsnList.stream().forEach(chldAbsenceRsn -> {
					if (!StringUtils.isEmpty(chldAbsenceRsn.getIdChldRwnyRsn())) {
						sessionFactory.getCurrentSession().delete(sessionFactory.getCurrentSession()
								.load(MsngChildRnwyRsn.class, chldAbsenceRsn.getIdChldRwnyRsn()));
					}
				});
			}
		}
	}

	/**
	 *
	 * Method Name: saveConfirmedRsnAbsDetail
	 * Method Description: This method saves runaway reasons,
	 * If Recovery Interview conducted is Yes and Confirmed Reason for absence is
	 * RunAway, else delete the runaway reasons if they exists.
	 *
	 * @param chldAbsenceRsnList
	 * @param childRecoveryDetailDto
	 */
	@Override
	public void deleteConfirmedRsnAbsDetail(List<ChildAbsenceReasonDto> chldAbsenceRsnList,
										  ChildRecoveryDetailDto childRecoveryDetailDto) {
		if (!ObjectUtils.isEmpty(chldAbsenceRsnList)) {
			if (ServiceConstants.Y.equalsIgnoreCase(childRecoveryDetailDto.getIndRcvryIntrvwCndctd())
					&& CodesConstant.ABSNCRSN_RNAW.equalsIgnoreCase(childRecoveryDetailDto.getCdSuspectedReason())) {
				// If recovery interview conducted and confirmed  reason is runaway.
//				MsngChildRcvryDtl msngChldRcvryDtl = (MsngChildRcvryDtl) sessionFactory.getCurrentSession()
//						.get(MsngChildRcvryDtl.class, childRecoveryDetailDto.getIdChldRecoveryDtl());
				chldAbsenceRsnList.stream().forEach(chldAbsenceRsn -> {
					if (!StringUtils.isEmpty(chldAbsenceRsn.getIdChldRwnyRsn())) {
						sessionFactory.getCurrentSession().delete(sessionFactory.getCurrentSession()
								.load(MsngChildRnwyRsn.class, chldAbsenceRsn.getIdChldRwnyRsn()));
					}
				});
			}
		}
	}


	/**
	 * 
	 * Method Name: fetchDetailForValidation Method Description: This method
	 * will fetch Detail from Person table , CNSRVTRSHP_REMOVAL table Legal
	 * Status table for validation in Missing Child Detail and Child Recovery
	 * Detail page.
	 * 
	 * @param idPerson
	 * @param idCase
	 * @return RunawayMsngRcvryDto
	 */
	@Override
	public RunawayMsngRcvryDto fetchDetailForValidation(Long idPerson, Long idCase) {
		RunawayMsngRcvryDto runawayMsngDto = new RunawayMsngRcvryDto();
		Query fetchLegalStatusQuery = sessionFactory.getCurrentSession().createSQLQuery(joinSubQuery)
				.addScalar("idLegalStatEvent", StandardBasicTypes.LONG)
				.addScalar("cdLegalStatus", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(LegalStatusInDto.class));
		fetchLegalStatusQuery.setParameter("idPerson", idPerson);
		fetchLegalStatusQuery.setParameter("idCase", idCase);
		LegalStatusInDto legalEventDto = (LegalStatusInDto) fetchLegalStatusQuery.uniqueResult();
		StringBuilder dynamicRunawayListSql = new StringBuilder();
		dynamicRunawayListSql = dynamicRunawayListSql.append(fetchRecordsForValidation);

		Query query = sessionFactory.getCurrentSession().createSQLQuery(dynamicRunawayListSql.toString())
				.addScalar("dtRemoval", StandardBasicTypes.DATE).addScalar("dtPersonDeath", StandardBasicTypes.DATE)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE).addScalar("age", StandardBasicTypes.INTEGER)
				.setResultTransformer(Transformers.aliasToBean(RunawayMsngRcvryDto.class));
		query.setParameter("idPerson", idPerson);
		query.setParameter("idCase", idCase);
		List<RunawayMsngRcvryDto> runawaymsngLst = (List<RunawayMsngRcvryDto>) query.list();
		if(!ObjectUtils.isEmpty(runawaymsngLst)) {
			runawayMsngDto = runawaymsngLst.get(0);
		}
		// Modified the code to calculate the person age from person date of
		// birth instead of getting age from NBR_PERSON_AGE for warranty defect 12259 
		if (!ObjectUtils.isEmpty(runawayMsngDto) && !ObjectUtils.isEmpty(runawayMsngDto.getDtPersonBirth())) {
			runawayMsngDto.setAge(DateUtils.getAge(runawayMsngDto.getDtPersonBirth()));
		}
		if (!ObjectUtils.isEmpty(legalEventDto) && !ObjectUtils.isEmpty(legalEventDto.getIdLegalStatEvent())) {
			runawayMsngDto.setCdLegalStatus(legalEventDto.getCdLegalStatus());
		}
		return runawayMsngDto;
	}

	@Override
	public Boolean childRecoveryExist(Long idMsngChildDtl) {
		Long queryResult = 0L;
		Boolean isChildRecoveryExist = Boolean.FALSE;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(childRecoveryExistSql)
				.setParameter("idMsngDtl", idMsngChildDtl);
		if (!ObjectUtils.isEmpty(sqlQuery.uniqueResult()) && ((BigDecimal) sqlQuery.uniqueResult()).longValue() > 0) {
			queryResult = ((BigDecimal) sqlQuery.uniqueResult()).longValue();
		}
		if (queryResult > 0) {
			isChildRecoveryExist = Boolean.TRUE;
		}
		return isChildRecoveryExist;
	}

	@Override
	public Boolean isChildRecoveryExists(Long idMsngChildDtl) {
		Long queryResult = 0L;
		Boolean isChildRecoveryExist = Boolean.FALSE;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isChildRecoveryExistsSql)
				.setParameter("idMsngDtl", idMsngChildDtl);
		if (!ObjectUtils.isEmpty(sqlQuery.uniqueResult()) && ((BigDecimal) sqlQuery.uniqueResult()).longValue() > 0) {
			queryResult = ((BigDecimal) sqlQuery.uniqueResult()).longValue();
		}
		if (queryResult > 0) {
			isChildRecoveryExist = Boolean.TRUE;
		}
		return isChildRecoveryExist;
	}

	@Override
	public Long fetchPrimaryPerson(Long idStage) {
		BigDecimal idPerson = null;
		if (!ObjectUtils.isEmpty(idStage)) {
			SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchPrimaryPersonSql)
					.setParameter(ServiceConstants.IDSTAGE, idStage));
			idPerson = ((BigDecimal) query.uniqueResult());
		}
		return !ObjectUtils.isEmpty(idPerson)?idPerson.longValue():0l;
	}
	
	@Override
	public Date getRecoveryDate(Long idMsngChildDtl) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getRecoveryDate)
				.addScalar("dtChldRetrnd", StandardBasicTypes.DATE)
				.setParameter("idMsngChildDtl", idMsngChildDtl)
				.setResultTransformer(Transformers.aliasToBean(MsngChildRcvryDtl.class));
        return sqlQuery.uniqueResult()!=null?((MsngChildRcvryDtl)(sqlQuery.uniqueResult())).getDtChldRetrnd():null;
	}

	@Override
	public RunawayMissingIdsDto fetchMissingChildIds(Long idEvent) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchMissingChildIds)
				.addScalar("idNoficationParty", StandardBasicTypes.LONG)
				.addScalar("idChldMsngDtl", StandardBasicTypes.LONG)
				.addScalar("indChildReturn", StandardBasicTypes.STRING)
				.addScalar("cdReasonNotRtrnd", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(RunawayMissingIdsDto.class));
		return sqlQuery.uniqueResult()!=null?((RunawayMissingIdsDto)(sqlQuery.uniqueResult())):null;
	}

	@Override
	public RunawayChildRecoveryIdsDto fetchChildRecoveryIds(Long idEvent) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchChildRecoveryIds)
				.addScalar("idNoficationParty", StandardBasicTypes.LONG)
				.addScalar("idChldMsngDtl", StandardBasicTypes.LONG)
				.addScalar("idChldMsngRcvryDtl", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdated", StandardBasicTypes.DATE)
				.addScalar("indRcvryIntrvwCndctd", StandardBasicTypes.STRING)
				.addScalar("cdSuspectedReason", StandardBasicTypes.STRING)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(RunawayChildRecoveryIdsDto.class));
		return sqlQuery.uniqueResult()!=null?((RunawayChildRecoveryIdsDto)(sqlQuery.uniqueResult())):null;
	}

	@Override
	public RunawayMsngDtRemovalDto fetchDtRemoval(Long idStage) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchDtRemovalSql)
				.addScalar("dtRemoval", StandardBasicTypes.DATE)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(RunawayMsngDtRemovalDto.class));
		return sqlQuery.uniqueResult()!=null?((RunawayMsngDtRemovalDto)(sqlQuery.uniqueResult())):null;
	}

}
