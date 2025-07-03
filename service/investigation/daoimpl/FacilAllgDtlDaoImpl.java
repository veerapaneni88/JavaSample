
package us.tx.state.dfps.service.investigation.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.Allegation;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.FacilAlleg;
import us.tx.state.dfps.common.domain.FacilityInvstDtl;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.GetFacilAllegDetailReq;
import us.tx.state.dfps.service.common.request.UpdtFacilAllegMultiDtlReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.investigation.dao.AllegtnDao;
import us.tx.state.dfps.service.investigation.dao.FacilAllgDtlDao;
import us.tx.state.dfps.service.investigation.dto.AllegtnPrsnDto;
import us.tx.state.dfps.service.investigation.dto.FacilAllegDetailDto;
import us.tx.state.dfps.service.investigation.dto.FacilAllegInjuryDto;
import us.tx.state.dfps.service.investigation.dto.FacilInvstFacilDto;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:
 * CINV07S,CINV08S,CINV10S Class Description: This class is to
 * retrieves,saves,updates,multi update Facility Allegation Detail page.
 */
@Repository
public class FacilAllgDtlDaoImpl implements FacilAllgDtlDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${FacilAllgDtlDaoImpl.getAllegationDtlByIdStage}")
	private transient String getAllegationDtlByIdStage;

	@Value("${FacilAllgDtlDaoImpl.getPriorStageId}")
	private transient String getPriorStageId;

	@Value("${FacilAllgDtlDaoImpl.getIncomingCallDtl}")
	private transient String getIncomingCallDtl;

	@Value("${FacilAllgDtlDaoImpl.getFacilAllgDt}")
	private transient String getFacilAllgDt;

	@Value("${FacilAllgDtlDaoImpl.getInjuryDtl}")
	private transient String getInjuryDtl;

	@Value("${FacilAllgDtlDaoImpl.getFacilitysInvCnclsnList}")
	private transient String getFacilitysInvCnclsnList;

	@Value("${FacilAllgDtlDaoImpl.getFacilityInvCnclsn}")
	private transient String getFacilityInvCnclsn;

	@Value("${FacilAllgDtlDaoImpl.CallBlankOverallDispositionFACsql}")
	private transient String CallBlankOverallDispositionFACsql;

	@Value("${FacilAllgDtlDaoImpl.deleteFacilAllegSql}")
	private transient String deleteFacilAllegSql;

	@Value("${CaseMerge.getCaseMergeByIdCaseMergeTo}")
	private transient String getCaseMergeByIdCaseMergeToSql;

	@Autowired
	AllegtnDao allegtnDao;

	@Autowired
	CaseSummaryDao caseSummaryDao;

	public FacilAllgDtlDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Description: Populates the Allegation List for Facility
	 * Allegations. legacy DAM name - CINV70S
	 * 
	 * @param idAllegation
	 * @return @
	 */
	@Override
	public FacilAllegDetailDto getallegtnlist(GetFacilAllegDetailReq getFacilAllegDetailReq) {
		FacilAllegDetailDto resObj;
		Query getFacilAllegDtlQry = sessionFactory.getCurrentSession().createSQLQuery(getFacilAllgDt)
				.addScalar("cdAllegType", StandardBasicTypes.STRING)
				.addScalar("cdAllegDisposition", StandardBasicTypes.STRING)
				.addScalar("cdAllegIncidentStage", StandardBasicTypes.STRING)
				.addScalar("idVictim", StandardBasicTypes.LONG).addScalar("idAllegation", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idAllegedPerpetrator", StandardBasicTypes.LONG)
				.addScalar("dtFacilAllegIncident", StandardBasicTypes.DATE)
				.addScalar("cdFacilAllegNeglType", StandardBasicTypes.STRING)
				.addScalar("cdFacilAllegEventLoc", StandardBasicTypes.STRING)
				.addScalar("cdFacilAllegInjSer", StandardBasicTypes.STRING)
				.addScalar("facilAllegMHMR", StandardBasicTypes.STRING)
				.addScalar("indFacilAllegAbOffGr", StandardBasicTypes.STRING)
				.addScalar("indFacilAllegSupvd", StandardBasicTypes.STRING)
				.addScalar("cdFacilAllegSrc", StandardBasicTypes.STRING)
				.addScalar("cdFacilAllegSrcSupr", StandardBasicTypes.STRING)
				.addScalar("cdFacilAllegDispSupr", StandardBasicTypes.STRING)
				.addScalar("dtFacilAllegInvstgtr", StandardBasicTypes.DATE)
				.addScalar("dtFacilAllegSuprReply", StandardBasicTypes.DATE)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdFacilAllegInvClass", StandardBasicTypes.STRING)
				.addScalar("cdFacilAllegClssSupr", StandardBasicTypes.STRING)
				.addScalar("facilAllegCmnts", StandardBasicTypes.STRING)
				.setParameter("idAllegation", getFacilAllegDetailReq.getIdAllegation())
				.setResultTransformer(Transformers.aliasToBean(FacilAllegDetailDto.class));
		resObj = (FacilAllegDetailDto) getFacilAllegDtlQry.uniqueResult();
		//Fix for defect 14210. Removed the minimum time check as the user is not able to see 12:00 am selected in UI page.
		if (!ObjectUtils.isEmpty(resObj.getDtFacilAllegIncident()) ) {
			resObj.setIncmgCallTime(DateUtils.getTime(resObj.getDtFacilAllegIncident()));
		}
		return resObj;
	}

	/**
	 * 
	 * Method Description:This DAM retrieves data from the Facility Injury table
	 * to populate the Injury List/Detail window. legacy DAM name - CINV08D
	 * 
	 * @param idAllegation
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FacilAllegInjuryDto> getInjuryDtl(GetFacilAllegDetailReq getFacilAllegDetailReq) {
		List<FacilAllegInjuryDto> resObj1;
		Query getFacilAllegDtlQry = sessionFactory.getCurrentSession().createSQLQuery(getInjuryDtl)
				.addScalar("cdFacilInjuryBody").addScalar("cdFacilInjuryCause")
				.addScalar("dtFacilInjuryDtrmntn", StandardBasicTypes.DATE).addScalar("cdFacilInjurySide")
				.addScalar("cdFacilInjuryType").addScalar("txtFacilInjuryCmnts")
				.addScalar("idFacilityInjury", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.setParameter("idAllegation", getFacilAllegDetailReq.getIdAllegation())
				.setResultTransformer(Transformers.aliasToBean(FacilAllegInjuryDto.class));
		resObj1 = (List<FacilAllegInjuryDto>) getFacilAllegDtlQry.list();
		return resObj1;
	}

	/**
	 * 
	 * Method Description: Retrieves data from PERSON and STAGE_PERSON_LINK for
	 * Allegation Detail and Facility Allegation Detail. legacy DAM name -
	 * CINVF8D
	 * 
	 * @param getFacilAllegDetailReq
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AllegtnPrsnDto> getFacilAllegDtl(GetFacilAllegDetailReq getFacilAllegDetailReq) {
		List<AllegtnPrsnDto> allegationList = new ArrayList<>();
		Query getFacilAllegDtlQry = sessionFactory.getCurrentSession().createSQLQuery(getAllegationDtlByIdStage)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdStagePersRole").addScalar("tsLastUpdate")
				.addScalar("nmPersonFull").addScalar("cdPersonSuffix").addScalar("cdPersonMaritalStatus")
				.addScalar("dtPersonBirth").addScalar("dtPersonDeath")
				.setParameter("idStage", getFacilAllegDetailReq.getIdStage())
				.setParameter("typePrn", ServiceConstants.CPRSNALL_PRN)
				.setResultTransformer(Transformers.aliasToBean(AllegtnPrsnDto.class));
		allegationList = (List<AllegtnPrsnDto>) getFacilAllegDtlQry.list();
		return allegationList;
	}

	/**
	 * 
	 * Method Description: Gets the most recently closed previous ID STAGE for a
	 * given ID STAGE. Retrieves DtIncomingCall from Incoming Detail table.
	 * legacy DAM name - CSEC54D,CCMNB5D
	 * 
	 * @param idStage
	 * @return @
	 */
	public Date getdtIncCall(GetFacilAllegDetailReq getFacilAllegDetailReq) {
		Date dtIncomingCall = null;
		SelectStageDto stageDto = caseSummaryDao.getStage(getFacilAllegDetailReq.getIdStage(),
				ServiceConstants.STAGE_PRIOR);
		if (!ObjectUtils.isEmpty(stageDto) && ServiceConstants.CSTAGES_INT.equals(stageDto.getCdStage())) {
			// CCMNB5D
			Query query1 = sessionFactory.getCurrentSession().createSQLQuery(getIncomingCallDtl)
					.addScalar("dtIncomingCall", StandardBasicTypes.DATE)
					.setParameter("idStage", stageDto.getIdStage());
			dtIncomingCall = (Date) query1.uniqueResult();
		}
		return dtIncomingCall;
	}

	/**
	 * 
	 * Method Description: Get's the list of facilities associated with the
	 * investigation legacy EJB Service name - Cinv07sEJB
	 * 
	 * @param getFacilAllegDetailReq
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	public List<FacilInvstFacilDto> getFacilitysInvCnclsnList(GetFacilAllegDetailReq getFacilAllegDetailReq) {
		List<FacilInvstFacilDto> res;
		Query getFacilityInvCnclsnListQry = sessionFactory.getCurrentSession().createSQLQuery(getFacilitysInvCnclsnList)
				.addScalar("idFacilResource", StandardBasicTypes.LONG).addScalar("nmRsrc", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
				.addScalar("cdMhmrCompCode", StandardBasicTypes.STRING)
				.setParameter("idStage", getFacilAllegDetailReq.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(FacilInvstFacilDto.class));
		res = (List<FacilInvstFacilDto>) getFacilityInvCnclsnListQry.list();
		return res;
	}

	/**
	 * 
	 * Method Description:Retrieve the facility associated with the allegation
	 * legacy EJB Service name - Cinv07sEJB
	 * 
	 * @param getFacilAllegDetailReq
	 * @return @
	 */
	public FacilInvstFacilDto getFacilityInvCnclsn(GetFacilAllegDetailReq getFacilAllegDetailReq) {
		Query getFacilityInvCnclsnQry = sessionFactory.getCurrentSession().createSQLQuery(getFacilityInvCnclsn)
				.addScalar("idFacilResource", StandardBasicTypes.LONG).addScalar("nmRsrc", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
				.addScalar("cdMhmrCompCode", StandardBasicTypes.STRING)
				.setParameter("idAllegation", getFacilAllegDetailReq.getIdAllegation())
				.setResultTransformer(Transformers.aliasToBean(FacilInvstFacilDto.class));
		return (FacilInvstFacilDto) getFacilityInvCnclsnQry.uniqueResult();
	}

	/**
	 * 
	 * Method Description: Retrieves blank overall Disposition FAC.
	 * 
	 * @param idStage
	 * @
	 */
	@Override
	public void callBlankOverallDispositionFAC(Long idStage) {
		sessionFactory.getCurrentSession().createQuery(CallBlankOverallDispositionFACsql).setParameter("idStage",
				idStage);
	}

	/**
	 * 
	 * Method Description: Deletes facil alleg details.
	 * 
	 * @param idAllegation
	 * @
	 */
	@Override
	public void deleteFacilAlleg(Long idAllegation) {
		sessionFactory.getCurrentSession().createQuery(deleteFacilAllegSql).setParameter("idAllegation", idAllegation);
		Allegation allegation = allegtnDao.getAllegationById(idAllegation);
		allegtnDao.updateAllegation(allegation, ServiceConstants.REQ_FUNC_CD_DELETE, false);
	}

	/**
	 * 
	 * Method Description: Loads facil alleg details.
	 * 
	 * @param idAllegation
	 * @
	 */
	@Override
	public FacilAlleg loadFacilAllegation(Long idAllegation) {
		FacilAlleg facilAlleg = (FacilAlleg) sessionFactory.getCurrentSession().load(FacilAlleg.class, idAllegation);

		return facilAlleg;
	}

	/**
	 * 
	 * Method Description: updates facil alleg details.
	 * 
	 * @param facilAlleg
	 * @param operation
	 * @param indFlush
	 * @
	 */
	@Override
	public Long updateFacilAlleg(FacilAlleg facilAlleg, String operation, boolean indFlush) {
		Long idAllegation = null;
		if (operation.equals(ServiceConstants.REQ_FUNC_CD_ADD))
			sessionFactory.getCurrentSession().persist(facilAlleg);
		else if (operation.equals(ServiceConstants.REQ_FUNC_CD_UPDATE))
			sessionFactory.getCurrentSession().saveOrUpdate(facilAlleg);
		else if (operation.equals(ServiceConstants.REQ_FUNC_CD_DELETE))
			sessionFactory.getCurrentSession()
					.delete(sessionFactory.getCurrentSession().load(FacilAlleg.class, facilAlleg.getIdAllegation()));
		idAllegation = facilAlleg.getIdAllegation();
		if (indFlush)
			sessionFactory.getCurrentSession().flush();
		return idAllegation;
	}

	/**
	 * 
	 * Method Description: This Method will changes the Event Status of a given
	 * event regardless of timestamp although timestamp is updated Dam Name:
	 * CCMN62D
	 * 
	 * @param ServiceReqHeaderDto
	 * @param eventDto
	 * @return ServiceResHeaderDto @
	 */
	public String getEventDetailsUpdate(Long idEvent, String cdEventStatus) {
		String retMsg = "";
		Date systemDate = new Date();
		if (!TypeConvUtil.isNullOrEmpty(idEvent)) {
			Event eventEntity = (Event) sessionFactory.getCurrentSession().get(Event.class, idEvent);
			eventEntity.setCdEventStatus(cdEventStatus);
			eventEntity.setDtEventOccurred(systemDate);
			sessionFactory.getCurrentSession().update(eventEntity);
			retMsg = ServiceConstants.SUCCESS;
		} else {
			throw new DataLayerException(ServiceConstants.NOAUDDOP);
		}
		return retMsg;
	}

	/**
	 * 
	 * Method Description: Updates multiple facility allegation records with the
	 * same disposition and findings. Dam Name: cinv76d
	 * 
	 * @param facilAllegDetailDto
	 * @param cdReqFunc
	 * @
	 */
	public void updateMultiFacilAllgWithDisp(FacilAllegDetailDto facilAllegDetailDto, String cdReqFunc) {
		switch (cdReqFunc) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			Criteria criteriaSqlObj = sessionFactory.getCurrentSession().createCriteria(FacilAlleg.class);
			criteriaSqlObj.add(Restrictions.eq("idAllegation", facilAllegDetailDto.getIdAllegation()));

			FacilAlleg facilAllegEntity = (FacilAlleg) criteriaSqlObj.uniqueResult();
			if (!StringUtils.isEmpty(facilAllegEntity)) {
				facilAllegEntity.setTxtFacilAllegCmnts(facilAllegDetailDto.getFacilAllegCmnts());
				sessionFactory.getCurrentSession().saveOrUpdate(facilAllegEntity);
			}
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Allegation.class);
			criteria.add(Restrictions.eq("idAllegation", facilAllegDetailDto.getIdAllegation()));

			Allegation allegationEntity = (Allegation) criteria.uniqueResult();
			if (!StringUtils.isEmpty(allegationEntity)) {
				allegationEntity.setCdAllegDisposition(facilAllegDetailDto.getCdAllegDisposition());
				sessionFactory.getCurrentSession().saveOrUpdate(allegationEntity);

				criteria = sessionFactory.getCurrentSession().createCriteria(FacilAlleg.class);
				criteria.add(Restrictions.eq("idAllegation", facilAllegDetailDto.getIdAllegation()));
				FacilAlleg facilAlleg = (FacilAlleg) criteria.uniqueResult();

				facilAlleg.setCdFacilAllegClss(facilAllegDetailDto.getCdFacilAllegInvClass());
				facilAlleg.setCdFacilAllegSrc(facilAllegDetailDto.getCdFacilAllegSrc());
				facilAlleg.setDtFacilAllegInvstgtr(facilAllegDetailDto.getDtFacilAllegInvstgtr());
				sessionFactory.getCurrentSession().saveOrUpdate(facilAlleg);
			}
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			Criteria criteriaSql = sessionFactory.getCurrentSession().createCriteria(FacilAlleg.class);
			criteriaSql.add(Restrictions.eq("idAllegation", facilAllegDetailDto.getIdAllegation()));

			FacilAlleg facilAllegEntityObj = (FacilAlleg) criteriaSql.uniqueResult();
			if (!StringUtils.isEmpty(facilAllegEntityObj)) {
				facilAllegEntityObj.setCdFacilAllegClssSupr(facilAllegDetailDto.getCdFacilAllegClssSupr());
				facilAllegEntityObj.setCdFacilAllegDispSupr(facilAllegDetailDto.getCdFacilAllegDispSupr());
				facilAllegEntityObj.setCdFacilAllegSrcSupr(facilAllegDetailDto.getCdFacilAllegSrcSupr());
				facilAllegEntityObj.setDtFacilAllegSuprReply(facilAllegDetailDto.getDtFacilAllegSuprReply());
				sessionFactory.getCurrentSession().saveOrUpdate(facilAllegEntityObj);
			}
			break;
		}
	}

	/**
	 * 
	 * Method Description: Retrieves ID_CASE from Stage table given ID_STAGE.
	 * Dam Name: ccmnb6d
	 * 
	 * @param updtFacilAllegMultiDtlReq
	 * @
	 */
	@Override
	public FacilAllegDetailDto retriveIdCase(UpdtFacilAllegMultiDtlReq updtFacilAllegMultiDtlReq) {
		FacilAllegDetailDto res = new FacilAllegDetailDto();
		Stage stage = (Stage) sessionFactory.getCurrentSession().load(Stage.class,
				updtFacilAllegMultiDtlReq.getFacilAllegDetailDto().getIdStage());
		res.setIdCase(stage.getCapsCase().getIdCase());
		res.setIndFormalScreened(stage.getIndScreened());
		return res;
	}

	/**
	 * 
	 * Method Description: updates overall Disposition FAC.
	 * 
	 * @param idStage
	 * @param cdDispositon
	 * @
	 */
	@Override
	public void updateOverallDispositionFAC(Long idStage, String cdDispositon) {
		FacilityInvstDtl facilityInvstDtl = (FacilityInvstDtl) sessionFactory.getCurrentSession()
				.createCriteria(FacilityInvstDtl.class).add(Restrictions.eq(ServiceConstants.STAGE_IDSTAGE, idStage))
				.list().stream().findAny().orElse(null);
		if (!ObjectUtils.isEmpty(facilityInvstDtl)) {
			facilityInvstDtl.setCdFacilInvstOvrallDis(cdDispositon);
			facilityInvstDtl.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(facilityInvstDtl);
		}

	}

}
