package us.tx.state.dfps.service.sscc.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.SsccExceptCareDesig;
import us.tx.state.dfps.common.dto.SSCCExceptCareDesignationDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.sscc.dao.SSCCExceptCareDesignationDao;
import us.tx.state.dfps.service.subcare.dto.ExceptionalCareDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 15, 2018- 3:21:12 PM © 2017 Texas Department of
 * Family and Protective Services
 */

@Repository
public class SSCCExceptCareDesignationDaoImpl implements SSCCExceptCareDesignationDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${SSCCExceptCareDesignationDaoImpl.getEligibilityPlcmtInfo}")
	private String getEligibilityPlcmtInfoSql;

	@Value("${SSCCExceptCareDesignationDaoImpl.getExistECDesig}")
	private String getExistECDesigSql;

	@Value("${SSCCExceptCareDesignationDaoImpl.getSSCCExceptCareList}")
	private String SSCCExpCareList;

	@Value("${SSCCExceptCareDesignationDaoImpl.getExcpCareOnSaveAndApprove}")
	private String getExcpCareOnSaveAndApproveSql;

	@Value("${SSCCExceptCareDesignationDaoImpl.getSSCCTimelineList}")
	private String getSSCCTimelineListSql;

	@Value("${SSCCExceptCareDesignationDaoImpl.getActiveSsccPlcmt}")
	private String activeSsccPlcmt;

	@Value("${SSCCExceptCareDesignationDaoImpl.getActiveChildPlcmtRefferal}")
	private String activeChildPlcmtRefferal;

	@Value("${SSCCExceptCareDesignationDaoImpl.getUpdateSSCCExceptCareDesigStatus}")
	private String updateSSCCExceptCareDesigStatus;

	@Value("${SSCCExceptCareDesignationDaoImpl.getUpdateCdExceptCareStatus}")
	private String updateCdExceptCareStatus;

	@Value("${SSCCExceptCareDesignationDaoImpl.getExceptionalCareList}")
	private String getExceptionalCareListSql;

	@Autowired
	MessageSource messageSource;

	@Value("${SSCCExceptCareDesignationDaoImpl.getSsccRsrcContractInfo}")
	private String getSsccRsrcContractInfoSql;

	@Value("${SSCCExceptCareDesignationDaoImpl.getSSCCExceptCareDays}")
	private String sSCCExceptCareDays;

	/**
	 * 
	 * Method Description: Gets child SSCC eligible placement for stage id.
	 * Service Name: getEligibilityPlcmtInfo
	 * 
	 * @param idStage
	 * @return ssccExcpCareDesList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SSCCExceptCareDesignationDto> getEligibilityPlcmtInfo(Long idStage) {
		List<SSCCExceptCareDesignationDto> ssccExcpCareDesList = (List<SSCCExceptCareDesignationDto>) sessionFactory
				.getCurrentSession().createSQLQuery(getEligibilityPlcmtInfoSql)
				.addScalar("idResourceSSCC", StandardBasicTypes.LONG)
				.addScalar("txtEventDescr", StandardBasicTypes.STRING)
				.addScalar("idPlcmntEvent", StandardBasicTypes.LONG).addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtEnd", StandardBasicTypes.DATE).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(SSCCExceptCareDesignationDto.class)).list();
		return ssccExcpCareDesList;
	}

	/**
	 * 
	 * Method Description: Gets an existed exceptional care designation record
	 * Service Name: getExistECDesig
	 * 
	 * @param idECDesig
	 * @return ssccExcpCareDesDto
	 */
	@Override
	public SSCCExceptCareDesignationDto getExistECDesig(Long idECDesig) {
		SSCCExceptCareDesignationDto ssccExcpCareDesDto = (SSCCExceptCareDesignationDto) sessionFactory
				.getCurrentSession().createSQLQuery(getExistECDesigSql)
				.addScalar("idPlcmntEvent", StandardBasicTypes.LONG)
				.addScalar("idExceptCareDesig", StandardBasicTypes.LONG).addScalar("dtStart", StandardBasicTypes.DATE)
				.addScalar("dtEndDate", StandardBasicTypes.DATE).addScalar("txtComment", StandardBasicTypes.STRING)
				.addScalar("cdStatus", StandardBasicTypes.STRING).addScalar("idSsccReferral", StandardBasicTypes.LONG)
				.addScalar("idSsccExceptCare", StandardBasicTypes.LONG).addScalar("idEvent",StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.setParameter("idECDesig", idECDesig)
				.setResultTransformer(Transformers.aliasToBean(SSCCExceptCareDesignationDto.class)).uniqueResult();
		return ssccExcpCareDesDto;
	}

	/**
	 * Method Description: Gets SSCC Resource and Contract information for stage
	 * id Service Name: getSsccRsrcContractInfo
	 *
	 * @param idStage
	 * @param idPlcmntEvent
	 * @return ssccExcpCareDesDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SSCCExceptCareDesignationDto getSsccRsrcContractInfo(Long idStage, Long idPlcmntEvent) {
		String getSsccRsrcContractInfo = getSsccRsrcContractInfoSql;
		try {
			List<SSCCExceptCareDesignationDto> ssccExcpCareDesDtoLst = new ArrayList<SSCCExceptCareDesignationDto>();


			if(!ObjectUtils.isEmpty(idPlcmntEvent)){
				getSsccRsrcContractInfo = getSsccRsrcContractInfo
						.concat(" AND D.ID_PLCMT_EVENT = ")
						.concat(String.valueOf(idPlcmntEvent));
			}else{
				getSsccRsrcContractInfo = getSsccRsrcContractInfo
						.concat(" AND Trunc(Sysdate) between trunc(A.DT_START) and trunc(A.DT_END) ");
			}
			SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSsccRsrcContractInfo)
					.addScalar("idResourceSSCC", StandardBasicTypes.LONG).addScalar("rsrcName", StandardBasicTypes.STRING)
					.addScalar("cdRegion", StandardBasicTypes.STRING).addScalar("idContract", StandardBasicTypes.LONG)
					.addScalar("cdCatchment", StandardBasicTypes.STRING).setParameter("idStage", idStage)
					.setResultTransformer(Transformers.aliasToBean(SSCCExceptCareDesignationDto.class));


			ssccExcpCareDesDtoLst = (List<SSCCExceptCareDesignationDto>) query.list();
			// Query return more than one record, so change the return type to list
			// and getting the last element from the result - Added this change for
			// warranty defect 12372
			if (!ObjectUtils.isEmpty(ssccExcpCareDesDtoLst) && ssccExcpCareDesDtoLst.size() > 0) {
				int lstSize = ssccExcpCareDesDtoLst.size();
				return ssccExcpCareDesDtoLst.get(lstSize - ServiceConstants.One);
			}
		}catch(Exception e){
			
		}
		return new SSCCExceptCareDesignationDto();
	}

	/**
	 * 
	 * Method Description: Gets SSCC Exceptional Care Designation list for a
	 * person or stage id Service Name: getSSCCExceptCareList
	 * 
	 * @param idCase
	 * @param idStage
	 * @return ssccExceptCareList
	 */

	@Override
	public List<SSCCExceptCareDesignationDto> getSSCCExceptCareList(Long idCase, Long idStage) {
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(SSCCExpCareList)
				.addScalar("idExceptCareDesig", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPlcmntEvent", StandardBasicTypes.LONG)
				.addScalar("dtStart", StandardBasicTypes.DATE).addScalar("dtEndDate", StandardBasicTypes.DATE)
				.addScalar("txtComment", StandardBasicTypes.STRING).addScalar("cdStatus", StandardBasicTypes.STRING)
				.addScalar("dtRecorded", StandardBasicTypes.DATE).addScalar("idSsccExceptCare", StandardBasicTypes.LONG)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("nmPersonProposed", StandardBasicTypes.STRING)
				.addScalar("nmPersonEvaluated", StandardBasicTypes.STRING)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE).addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
				.addScalar("txtEventDescr", StandardBasicTypes.STRING)
				.addScalar("idSsccReferral", StandardBasicTypes.LONG).setParameter("idCase", idCase)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(SSCCExceptCareDesignationDto.class)));

		List<SSCCExceptCareDesignationDto> ssccExceptCareList = (List<SSCCExceptCareDesignationDto>) query.list();

		return ssccExceptCareList;
	}

	/**
	 * 
	 * Method Description: Save OR Updates a record in the
	 * SSCC_EXCEPT_CARE_DESIG Table Service Name: saveOrUpdateExceptCareDesig
	 * 
	 * @param sSCCExceptCareDesignationDto
	 * @param action
	 * @return sSCCExceptCareDesignationDto
	 */
	@Override
	public SSCCExceptCareDesignationDto saveOrUpdateExceptCareDesig(
			SSCCExceptCareDesignationDto sSCCExceptCareDesignationDto, String action) {
		sSCCExceptCareDesignationDto.setMessage(ServiceConstants.FAIL);
		SsccExceptCareDesig ssccExceptCareDesig = new SsccExceptCareDesig();
		switch (action) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			ssccExceptCareDesig.setIdLastUpdatePerson(sSCCExceptCareDesignationDto.getIdLastUpdatedPerson());
			ssccExceptCareDesig.setIdCreatedPerson(sSCCExceptCareDesignationDto.getIdCreatedPerson());
			ssccExceptCareDesig.setIdPlcmtEvent(sSCCExceptCareDesignationDto.getIdPlcmntEvent());
			ssccExceptCareDesig.setCdStatus(sSCCExceptCareDesignationDto.getCdStatus());
			if (!ObjectUtils.isEmpty(sSCCExceptCareDesignationDto.getDtRecorded())) {
				ssccExceptCareDesig.setDtDatetimeRecorded(sSCCExceptCareDesignationDto.getDtRecorded());
			} else {
				ssccExceptCareDesig.setDtDatetimeRecorded(new Date());
			}
			ssccExceptCareDesig.setDtStart(sSCCExceptCareDesignationDto.getDtStart());
			if (ObjectUtils.isEmpty(sSCCExceptCareDesignationDto.getDtEndDate())) {
				ssccExceptCareDesig.setDtEnd(ServiceConstants.GENERIC_END_DATE);
			} else {
				ssccExceptCareDesig.setDtEnd(sSCCExceptCareDesignationDto.getDtEndDate());
			}
			ssccExceptCareDesig.setTxtComment(sSCCExceptCareDesignationDto.getTxtComment());
			ssccExceptCareDesig.setIdSsccReferral(sSCCExceptCareDesignationDto.getIdSsccReferral());
			ssccExceptCareDesig.setDtCreated(new Date());
			ssccExceptCareDesig.setDtLastUpdate(new Date());
			ssccExceptCareDesig.setIdEvent(sSCCExceptCareDesignationDto.getIdEvent());
			sSCCExceptCareDesignationDto
					.setIdExceptCareDesig((Long) sessionFactory.getCurrentSession().save(ssccExceptCareDesig));
			sSCCExceptCareDesignationDto.setMessage(ServiceConstants.SUCCESS);
			break;

		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			ssccExceptCareDesig = (SsccExceptCareDesig) sessionFactory.getCurrentSession()
					.get(SsccExceptCareDesig.class, sSCCExceptCareDesignationDto.getIdExceptCareDesig());
			ssccExceptCareDesig.setIdLastUpdatePerson(sSCCExceptCareDesignationDto.getIdLastUpdatedPerson());
			ssccExceptCareDesig.setCdStatus(sSCCExceptCareDesignationDto.getCdStatus());
			ssccExceptCareDesig.setDtStart(sSCCExceptCareDesignationDto.getDtStart());
			if (ObjectUtils.isEmpty(sSCCExceptCareDesignationDto.getDtEndDate())) {
				ssccExceptCareDesig.setDtEnd(ServiceConstants.GENERIC_END_DATE);
			} else {
				ssccExceptCareDesig.setDtEnd(sSCCExceptCareDesignationDto.getDtEndDate());
			}
			ssccExceptCareDesig.setTxtComment(sSCCExceptCareDesignationDto.getTxtComment());
			ssccExceptCareDesig.setIdSsccExceptCare(sSCCExceptCareDesignationDto.getIdSsccExceptCare());
			sessionFactory.getCurrentSession().saveOrUpdate(ssccExceptCareDesig);
			sSCCExceptCareDesignationDto.setMessage(ServiceConstants.SUCCESS);
			break;
		}

		return sSCCExceptCareDesignationDto;
	}

	/**
	 * 
	 * Method Description: * Gets existing linked exceptional care record on
	 * save and approve of the sscc exceptional care designation Service Name:
	 * getExcpCareOnSaveAndApprove
	 * 
	 * @param idECDesig
	 * @return ssccExcpCareDesDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SSCCExceptCareDesignationDto getExcpCareOnSaveAndApprove(Long idECDesig) {
		SSCCExceptCareDesignationDto ssccExcpCareDesDto = null;
		List<SSCCExceptCareDesignationDto> ssccExcpCareDesList = (List<SSCCExceptCareDesignationDto>) sessionFactory
				.getCurrentSession().createSQLQuery(getExcpCareOnSaveAndApproveSql)
				.addScalar("dtStart", StandardBasicTypes.DATE).addScalar("dtEndDate", StandardBasicTypes.DATE).addScalar("idSsccExceptCare",StandardBasicTypes.LONG)
				.setParameter("idECDesig", idECDesig)
				.setResultTransformer(Transformers.aliasToBean(SSCCExceptCareDesignationDto.class)).list();
		if (!CollectionUtils.isEmpty(ssccExcpCareDesList)) {
			ssccExcpCareDesDto = ssccExcpCareDesList.get(0);
		}
		return ssccExcpCareDesDto;
	}

	/**
	 * 
	 * Method Name: getActiveChildPlcmtRefferal Method Description: This method
	 * gets active child placement referral for the stage id
	 * 
	 * @param idStage
	 * @return ssccExcpCareDesDto
	 * 
	 */

	@Override
	public SSCCExceptCareDesignationDto getActiveChildPlcmtRefferal(Long idStage) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(activeChildPlcmtRefferal)

				.addScalar("idSsccReferral", StandardBasicTypes.LONG).addScalar("cdRegion", StandardBasicTypes.STRING)
				.addScalar("count", StandardBasicTypes.INTEGER).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(SSCCExceptCareDesignationDto.class));
		SSCCExceptCareDesignationDto ssccExcpCareDesDto = (SSCCExceptCareDesignationDto) query.uniqueResult();

		return ssccExcpCareDesDto;
	}

	/**
	 * 
	 * Method Name: getActiveSsccPlcmt Method Description: This method used for
	 * Gets list of child sscc placement for stage id
	 * 
	 * @param idStage
	 * @return ssccExceptCareList @
	 */

	@Override
	public List<SSCCExceptCareDesignationDto> getActiveSsccPlcmt(Long idStage) {
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(activeSsccPlcmt)
				.addScalar("idResourceSSCC", StandardBasicTypes.LONG)
				.addScalar("idPlcmntEvent", StandardBasicTypes.LONG).addScalar("idPlcmntChild", StandardBasicTypes.LONG)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE).addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(SSCCExceptCareDesignationDto.class)));

		List<SSCCExceptCareDesignationDto> ssccExceptCareList = (List<SSCCExceptCareDesignationDto>) query.list();

		return ssccExceptCareList;
	}

	/**
	 * 
	 * Method Name: getUpdateSSCCExceptCareDesigStatus Method Description: This
	 * method used for Updates exceptional care designation status in the
	 * SSCC_EXCEPT_CARE_DESIG table
	 * 
	 * @param cdStatus
	 * @param idExceptCareDesig
	 * @return ssccExcpCareDesDto @
	 */

	@Override
	public Boolean getUpdateSSCCExceptCareDesigStatus(String cdStatus, Long idExceptCareDesig) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateSSCCExceptCareDesigStatus)
				.setParameter("cdStatus", cdStatus).setParameter("idExceptCareDesig", idExceptCareDesig);
		int noOfRowsUpdated = query.executeUpdate();
		if (noOfRowsUpdated > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * Method Name: getUpdateCdExceptCareStatus Method Description: This method
	 * used for Updates CD_EXCEPT_CARE_STATUS in the SSCC_LIST table
	 * 
	 * @param cdExceptCareStatus
	 * @param idSsccReferral
	 * @return ssccExcpCareDesDto @
	 */
	@Override
	public Boolean getUpdateCdExceptCareStatus(String cdExceptCareStatus, Long idSsccReferral) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateCdExceptCareStatus)
				.setParameter("cdExceptCareStatus", cdExceptCareStatus).setParameter("idSsccReferral", idSsccReferral);
		int noOfRowsUpdated = query.executeUpdate();
		if (noOfRowsUpdated > 0) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public SSCCExceptCareDesignationDto getSSCCExceptCareDays(Long idSsccReferral) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sSCCExceptCareDays)

				.addScalar("nbrTotalBudgetDays", StandardBasicTypes.INTEGER)

				.setParameter("idSsccReferral", idSsccReferral)
				.setResultTransformer(Transformers.aliasToBean(SSCCExceptCareDesignationDto.class));
		SSCCExceptCareDesignationDto ssccExcpCareDesDto = (SSCCExceptCareDesignationDto) query.uniqueResult();

		return ssccExcpCareDesDto;
	}

	/**
	 * Method Name: displayExceptCareList Method Description:Gets list of all
	 * exceptional care records that linked to an sscc placement.
	 * 
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ExceptionalCareDto> getExceptCareList(Long idCase, Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getExceptionalCareListSql)
				.addScalar("idExceptCare", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPlcmntEvent", StandardBasicTypes.LONG).addScalar("dtStart", StandardBasicTypes.DATE)
				.addScalar("dtEnd", StandardBasicTypes.DATE).addScalar("txtComment", StandardBasicTypes.STRING)
				.addScalar("nbrDays", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE).addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
				.setParameter("idCase", idCase).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(ExceptionalCareDto.class));
		return (List<ExceptionalCareDto>) query.list();

	}

}
