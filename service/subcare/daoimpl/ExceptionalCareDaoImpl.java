package us.tx.state.dfps.service.subcare.daoimpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Placement;
import us.tx.state.dfps.common.domain.SsccExceptCare;
import us.tx.state.dfps.common.dto.SSCCExceptCareDesignationDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ExceptionalCareReq;
import us.tx.state.dfps.service.common.response.ExceptionalCareRes;
import us.tx.state.dfps.service.subcare.dao.ExceptionalCareDao;
import us.tx.state.dfps.service.subcare.dto.ExceptionalCareDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This is the
 * Dao Impl class for Exceptional Care. Apr 12, 2018- 6:01:20 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class ExceptionalCareDaoImpl implements ExceptionalCareDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ExceptionalCareDaoImpl.getExceptCareList}")
	private String getExceptCareList;

	@Value("${ExceptionalCareDaoImpl.getPlacementDates}")
	private String getPlacementDates;
	@Value("${ExceptionalCareDaoImpl.getActiveChildPlcmtReferral}")
	private String getActiveChildPlcmtReferral;
	@Value("${ExceptionalCareDaoImpl.getExcpCareDays}")
	private String getExcpCareDays;

	private static final Logger log = Logger.getLogger(ExceptionalCareDaoImpl.class);

	/**
	 * Method Name: saveExceptionalCare Method Description:Gets placement start
	 * and end dates
	 * 
	 * @param exceptionalCareVB
	 * @return
	 */
	@Override
	public ExceptionalCareDto saveExceptionalCare(ExceptionalCareDto exceptionalCareVB) {
		log.debug("Entering method saveExceptionalCare in ExceptionalCareDaoImpl");

		SsccExceptCare ssccExceptCare = new SsccExceptCare();
		// ssccExceptCare.setIdSsccExceptCare(exceptionalCareVB.getIdExceptCare().intValue());
		ssccExceptCare.setIdLastUpdatePerson(exceptionalCareVB.getExceptCarelastUpdatedPersonId());
		ssccExceptCare.setIdCreatedPerson(exceptionalCareVB.getExceptCareCreatedPersonId());
		Placement placement = (Placement) sessionFactory.getCurrentSession().get(Placement.class,
				exceptionalCareVB.getIdPlcmntEvent());
		ssccExceptCare.setPlacement(placement);
		ssccExceptCare.setDtStart(exceptionalCareVB.getDtStart());
		ssccExceptCare.setDtEnd(exceptionalCareVB.getDtEnd());
		ssccExceptCare.setTxtComment(exceptionalCareVB.getTxtComment());
		ssccExceptCare.setNbrDays(exceptionalCareVB.getNbrDays());

		sessionFactory.getCurrentSession().save(ssccExceptCare);

		log.debug("Exiting method saveExceptionalCare in ExceptionalCareDaoImpl");
		return exceptionalCareVB;
	}

	/**
	 * Method Name: displayExceptCareList Method Description:Insert a new record
	 * into SSCC_EXCEPTIONAL_CARE or Updates a record from the same table.
	 * 
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ExceptionalCareDto> displayExceptCareList(ExceptionalCareReq req) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getExceptCareList)
				.addScalar("idExceptCare", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPlcmntEvent", StandardBasicTypes.LONG).addScalar("dtStart", StandardBasicTypes.DATE)
				.addScalar("dtEnd", StandardBasicTypes.DATE).addScalar("txtComment", StandardBasicTypes.STRING)
				.addScalar("nbrDays", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE).addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
				.setParameter("idPlcmtEvent", req.getIdPlcmtEvent())
				.setResultTransformer(Transformers.aliasToBean(ExceptionalCareDto.class));
		return (List<ExceptionalCareDto>) query.list();

	}

	@Override
	public ExceptionalCareRes updateSsccExceptCare(ExceptionalCareReq req) {
		List<ExceptionalCareDto> exceptionalCareDtoList = this.displayExceptCareList(req);
		ExceptionalCareRes res = new ExceptionalCareRes();
		
		if (!CollectionUtils.isEmpty(exceptionalCareDtoList) && exceptionalCareDtoList.size() > 0) {
			
			ExceptionalCareDto exceptionalCareDto = exceptionalCareDtoList.get(0);
			SsccExceptCare ssccExceptCare = (SsccExceptCare) sessionFactory.getCurrentSession()
					.get(SsccExceptCare.class, (exceptionalCareDto.getIdExceptCare()));
			if (!ObjectUtils.isEmpty(ssccExceptCare)) {
				ssccExceptCare.setDtEnd(req.getDtEnd());
				if (!ObjectUtils.isEmpty(ssccExceptCare.getIdSsccExceptCare())) {
					ssccExceptCare.setIdSsccExceptCare(ssccExceptCare.getIdSsccExceptCare());
				}
				ssccExceptCare.setDtLastUpdate(new Date());
				if (!ObjectUtils.isEmpty(req.getTxtComment())) {
					ssccExceptCare.setTxtComment(req.getTxtComment());
				}
				if (!ObjectUtils.isEmpty(req.getNbrDays())) {
					ssccExceptCare.setNbrDays(req.getNbrDays());
				}
				sessionFactory.getCurrentSession().saveOrUpdate(ssccExceptCare);
				res.setMessage(ServiceConstants.SUCCESS);
			}
		}
		return res;
	}

	@Override
	public ExceptionalCareRes updateSsccECStartDate(ExceptionalCareReq req) {
		ExceptionalCareRes res = new ExceptionalCareRes();
		SsccExceptCare ssccExceptCare = (SsccExceptCare) sessionFactory.getCurrentSession().get(SsccExceptCare.class,
				(req.getIdSsccExceptCare()));
		if (!ObjectUtils.isEmpty(ssccExceptCare)) {

			if (!ObjectUtils.isEmpty(req.getDtStart())) {
				ssccExceptCare.setDtEnd(ssccExceptCare.getDtStart());
			}
			if (!ObjectUtils.isEmpty(ssccExceptCare.getIdSsccExceptCare())) {
				ssccExceptCare.setIdSsccExceptCare(ssccExceptCare.getIdSsccExceptCare());
			}
			ssccExceptCare.setDtLastUpdate(new Date());
			if (!ObjectUtils.isEmpty(req.getTxtComment())) {
				ssccExceptCare.setTxtComment(req.getTxtComment());
			}
			if (!ObjectUtils.isEmpty(req.getNbrDays())) {
				ssccExceptCare.setNbrDays(req.getNbrDays());
			}
			sessionFactory.getCurrentSession().saveOrUpdate(ssccExceptCare);
			res.setMessage(ServiceConstants.SUCCESS);
		}
		return res;
	}

	@Override
	public ExceptionalCareRes updateSsccECStartEndDates(ExceptionalCareReq req) {
		ExceptionalCareRes res = new ExceptionalCareRes();
		SsccExceptCare ssccExceptCare = (SsccExceptCare) sessionFactory.getCurrentSession().get(SsccExceptCare.class,
				(req.getIdSsccExceptCare()));
		if (!ObjectUtils.isEmpty(ssccExceptCare)) {
			if (!ObjectUtils.isEmpty(req.getDtEnd())) {
				ssccExceptCare.setDtEnd(req.getDtEnd());
			}
			if (!ObjectUtils.isEmpty(ssccExceptCare.getIdSsccExceptCare())) {
				ssccExceptCare.setIdSsccExceptCare(ssccExceptCare.getIdSsccExceptCare());
			}
			ssccExceptCare.setDtLastUpdate(new Date());
			if (!ObjectUtils.isEmpty(req.getTxtComment())) {
				ssccExceptCare.setTxtComment(req.getTxtComment());
			}
			if (!ObjectUtils.isEmpty(req.getNbrDays())) {
				ssccExceptCare.setNbrDays(req.getNbrDays());
			}
			sessionFactory.getCurrentSession().saveOrUpdate(ssccExceptCare);
			res.setMessage(ServiceConstants.SUCCESS);
		}
		return res;
	}

	/**
	 * Method Name: getPlacementDates Method Description:Gets placement start
	 * and end dates
	 * 
	 * @param idPlcmtEvent
	 * @return
	 */
	@Override
	public ExceptionalCareDto getPlacementDates(Long idPlcmtEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPlacementDates)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE).addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
				.setParameter("idPlcmtEvent", idPlcmtEvent)
				.setResultTransformer(Transformers.aliasToBean(ExceptionalCareDto.class));
		ExceptionalCareDto exceptionalCareDto = (ExceptionalCareDto) query.uniqueResult();
		return exceptionalCareDto;
	}

	/**
	 * Method Name: saveExceptionalCare Method Description: Insert a new record
	 * into SSCC_EXCEPTIONAL_CARE or Updates a record from the same table.
	 * 
	 * @param exceptionalCareDto
	 * @param cdSavetype
	 */
	@Override
	public ExceptionalCareRes SaveExceptionalCare(ExceptionalCareDto exceptionalCareDto, String cdSaveType) {
		ExceptionalCareRes res = new ExceptionalCareRes();
		Placement placement = null;
		ExceptionalCareDto exceptionalCare = new ExceptionalCareDto();
		res.setMessage(ServiceConstants.FAIL);
		SsccExceptCare ssccExceptCare = new SsccExceptCare();
		switch (cdSaveType) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			ssccExceptCare.setIdLastUpdatePerson(exceptionalCareDto.getExceptCarelastUpdatedPersonId());
			ssccExceptCare.setIdCreatedPerson(exceptionalCareDto.getExceptCarelastUpdatedPersonId());
			ssccExceptCare.setDtCreated(new Date());
			ssccExceptCare.setDtStart(exceptionalCareDto.getDtStart());
			if (ObjectUtils.isEmpty(exceptionalCareDto.getDtEnd())) {
				ssccExceptCare.setDtEnd(ServiceConstants.GENERIC_END_DATE);
			} else {
				ssccExceptCare.setDtEnd(exceptionalCareDto.getDtEnd());
			}
			ssccExceptCare.setTxtComment(exceptionalCareDto.getTxtComment());
			ssccExceptCare.setNbrDays((long) exceptionalCareDto.getNbrDays());
			ssccExceptCare.setDtLastUpdate(Calendar.getInstance().getTime());
			placement = (Placement) sessionFactory.getCurrentSession().get(Placement.class,
					exceptionalCareDto.getIdPlcmntEvent());
			ssccExceptCare.setPlacement(placement);
			Long idExceptCare = (Long) sessionFactory.getCurrentSession().save(ssccExceptCare);
			exceptionalCare.setIdExceptCare(idExceptCare);
			res.setExceptionalCareDto(exceptionalCare);
			res.setMessage(ServiceConstants.SUCCESS);
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			if (!ObjectUtils.isEmpty(exceptionalCareDto.getIdExceptCare())
					&& exceptionalCareDto.getIdExceptCare() != 0l) {
				ssccExceptCare = (SsccExceptCare) sessionFactory.getCurrentSession().get(SsccExceptCare.class,
						exceptionalCareDto.getIdExceptCare());
			}
			ssccExceptCare.setIdLastUpdatePerson(exceptionalCareDto.getExceptCarelastUpdatedPersonId());
			ssccExceptCare.setDtStart(exceptionalCareDto.getDtStart());
			if (ObjectUtils.isEmpty(exceptionalCareDto.getDtEnd())) {
				ssccExceptCare.setDtEnd(ServiceConstants.GENERIC_END_DATE);
			} else {
				ssccExceptCare.setDtEnd(exceptionalCareDto.getDtEnd());
			}
			ssccExceptCare.setTxtComment(exceptionalCareDto.getTxtComment());
			ssccExceptCare.setNbrDays((long) exceptionalCareDto.getNbrDays());
			ssccExceptCare.setDtLastUpdate(Calendar.getInstance().getTime());
			placement = (Placement) sessionFactory.getCurrentSession().get(Placement.class,
					exceptionalCareDto.getIdPlcmntEvent());
			ssccExceptCare.setPlacement(placement);
			sessionFactory.getCurrentSession().saveOrUpdate(ssccExceptCare);
			exceptionalCare.setIdExceptCare(exceptionalCareDto.getIdExceptCare());
			res.setExceptionalCareDto(exceptionalCare);
			res.setMessage(ServiceConstants.SUCCESS);
			break;

		}
		return res;

	}

	/**
	 * Method Name: getActiveChildPlcmtReferral Method Description:Gets an
	 * active child sscc referral from the SSCC_REFERRAL table
	 * 
	 * @param idStage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SSCCExceptCareDesignationDto getActiveChildPlcmtReferral(Long idStage) {
		List<SSCCExceptCareDesignationDto> ssccExceptCareDesignationDtoLst = new ArrayList<SSCCExceptCareDesignationDto>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getActiveChildPlcmtReferral)
				.addScalar("idSsccReferral", StandardBasicTypes.LONG).addScalar("count", StandardBasicTypes.INTEGER)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(SSCCExceptCareDesignationDto.class));
		// Query return more than one record, so change the return type to list
		// and getting the first element from the result - Added this change for
		// warranty defect 12569
		ssccExceptCareDesignationDtoLst = (List<SSCCExceptCareDesignationDto>) query.list();
		if (!ObjectUtils.isEmpty(ssccExceptCareDesignationDtoLst)
				&& ssccExceptCareDesignationDtoLst.size() > ServiceConstants.Zero_INT) {
			return ssccExceptCareDesignationDtoLst.get(ServiceConstants.Zero_INT);
		}
		return new SSCCExceptCareDesignationDto();
	}

	/**
	 * Method Name: getExcpCareDays Method Description: Gets numbers of
	 * exceptional care days in a contract period
	 * 
	 * @param exceptionalCareDto
	 * @return
	 */
	@Override
	public Integer getExcpCareDays(ExceptionalCareDto exceptionalCareDto) {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		String dtStart = dateFormat.format(exceptionalCareDto.getDtStart());
		Query nbrDays = sessionFactory.getCurrentSession().createSQLQuery(getExcpCareDays)
				.addScalar("nbrTotalBudgetDays", StandardBasicTypes.INTEGER)
				.setParameter("idssccReferral", exceptionalCareDto.getIdSsccReferral())
				.setParameter("dtStart", dtStart);
		Integer nbrOfDays = (Integer) nbrDays.uniqueResult();
		return nbrOfDays;

	}

}
