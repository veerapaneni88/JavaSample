package us.tx.state.dfps.service.admin.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.FacilityServiceType;
import us.tx.state.dfps.service.admin.dao.FacilityServiceTypeDao;
import us.tx.state.dfps.service.admin.dto.FacilityServiceTypeAudDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.facilityservicetype.dto.FacilityServiceTypeInDto;
import us.tx.state.dfps.service.facilityservicetype.dto.FacilityServiceTypeOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Gets data
 * from Aug 21, 2017- 6:13:17 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class FacilityServiceTypeDaoImpl implements FacilityServiceTypeDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FacilityServiceTypeDaoImpl.getResourceCharacter}")
	private transient String getResourceCharacter;

	@Value("${FacilityServiceTypeDaoImpl.getEdFacilityServiceTypeCountRTC}")
	private transient String getEdFacilityServiceTypeCountRtc;

	@Value("${FacilityServiceTypeDaoImpl.getEdFacilityServiceTypeCountNotRTC}")
	private transient String getEdFacilityServiceTypeCountNotRtc;

	@Value("${FacilityServiceTypeDaoImpl.getFacilityServiceType}")
	private String getFacilityServiceType;

	public static final String RSRC_TYP_RTC_EXIST = "Y";
	public static final String RSRC_TYP_RTC_NOT_EXIST = "N";

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.admin.dao.FacilityServiceTypeDao#
	 * getFacilityServiceTypeOutDtoList(us.tx.state.dfps.service.
	 * facilityservicetype.dto.FacilityServiceTypeInDto)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FacilityServiceTypeOutDto> getFacilityServiceTypeOutDtoList(
			FacilityServiceTypeInDto facilityServiceTypeInDto) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getResourceCharacter)
				.addScalar("idFacilSvcType", StandardBasicTypes.LONG).addScalar("dtEffective", StandardBasicTypes.DATE)
				.addScalar("dtEnd", StandardBasicTypes.DATE)
				.addScalar("cdFacilSvcType", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(FacilityServiceTypeOutDto.class)));
		sQLQuery1.setParameter("idResource", facilityServiceTypeInDto.getIdResource());
		List<FacilityServiceTypeOutDto> facilityServiceTypeOutDtoList = new ArrayList<>();
		facilityServiceTypeOutDtoList = (List<FacilityServiceTypeOutDto>) sQLQuery1.list();
		return facilityServiceTypeOutDtoList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.admin.dao.FacilityServiceTypeDao#csesb1dQUERYdam
	 * (us.tx.state.dfps.service.admin.dto.FacilityServiceTypeInDto)
	 */
	@Override
	public List<FacilityServiceTypeOutDto> getFacilityServiceType(FacilityServiceTypeInDto pInputDataRec) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFacilityServiceType)
				.addScalar("idFacilSvcType", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idResource", StandardBasicTypes.LONG).addScalar("cdFacilSvcType", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING).addScalar("dtEffective", StandardBasicTypes.DATE)
				.addScalar("dtEnd", StandardBasicTypes.DATE)
				.setResultTransformer(Transformers.aliasToBean(FacilityServiceTypeOutDto.class)));
		sQLQuery1.setParameter("idResource", pInputDataRec.getIdResource());
		sQLQuery1.setParameter("dtPlacementStart", pInputDataRec.getDtPlacementStart());
		List<FacilityServiceTypeOutDto> liCres59doDto = (List<FacilityServiceTypeOutDto>) sQLQuery1.list();
		return liCres59doDto;
	}

	@Override
	public Integer getEdFacilityServiceTypeCount(FacilityServiceTypeInDto facilityServiceTypeInDto) {
		String indMaxEndDate = facilityServiceTypeInDto.getIndMaxEndDate();
		Integer svcTypeCount = 0;
		SQLQuery sqlQuery = null;
		switch (indMaxEndDate) {
		case RSRC_TYP_RTC_EXIST:
			sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getEdFacilityServiceTypeCountRtc)
					.addScalar("facSvcCount").setParameter("idResource", facilityServiceTypeInDto.getIdResource())
					.setParameter("cdFacilSvcType", facilityServiceTypeInDto.getCdFacilSvcType()));

			svcTypeCount = ((BigDecimal) sqlQuery.uniqueResult()).intValue();

			break;
		case RSRC_TYP_RTC_NOT_EXIST:
			sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(getEdFacilityServiceTypeCountNotRtc).addScalar("facSvcCount")
					.setParameter("idResource", facilityServiceTypeInDto.getIdResource())
					.setParameter("cdFacilSvcType", facilityServiceTypeInDto.getCdFacilSvcType()));

			svcTypeCount = ((BigDecimal) sqlQuery.uniqueResult()).intValue();

			break;
		default:
			break;
		}

		return svcTypeCount;
	}

	@Override
	public void facilityServiceTypeAud(FacilityServiceTypeAudDto facilityServiceTypeAudDto) {
		String cdReqFunc = facilityServiceTypeAudDto.getReqFuncCd();

		switch (cdReqFunc) {
		case ServiceConstants.REQ_FUNC_CD_ADD:

			FacilityServiceType facilityServiceTypeEntity = new FacilityServiceType();

			Criteria crCapsResource = sessionFactory.getCurrentSession().createCriteria(CapsResource.class)
					.add(Restrictions.eq("idResource", facilityServiceTypeAudDto.getIdResource()));
			CapsResource capsResource = (CapsResource) crCapsResource.uniqueResult();
			//artf276237 : Child Specific Contract Rsrc Creation -START
			if (!ObjectUtils.isEmpty(capsResource))
				facilityServiceTypeEntity.setCapsResource(capsResource);
			facilityServiceTypeEntity.setCdPlcmtLivArr(facilityServiceTypeAudDto.getCdPlcmtLivArr());
			facilityServiceTypeEntity.setDtEffective(facilityServiceTypeAudDto.getDtEffective());
			//artf276237 : In FacDetailPage if user doesn't give any closeDt, apply the default
			//MaxEndDt as the DtEnd otherwise DB populates from request.
			if (ObjectUtils.isEmpty(facilityServiceTypeAudDto.getDtEnd()))
				facilityServiceTypeEntity.setDtEnd(ServiceConstants.GENERIC_END_DATE);
			else
				facilityServiceTypeEntity.setDtEnd(facilityServiceTypeAudDto.getDtEnd());
			facilityServiceTypeEntity.setCdFacilSvcType(facilityServiceTypeAudDto.getCdFacilSvcType());
			//FacilityServiceType shows DtLastUpdate as Non-Nullable
			//so ensuring to set the date to New Date to avoid any null before
			//saving the entity for first time.
			facilityServiceTypeEntity.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().save(facilityServiceTypeEntity);
			

			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:

			Criteria crFacilityServiceType = sessionFactory.getCurrentSession()
					.createCriteria(FacilityServiceType.class)
					.add(Restrictions.eq("capsResource.idResource", facilityServiceTypeAudDto.getIdResource()))
					.add(Restrictions.eq("cdFacilSvcType", facilityServiceTypeAudDto.getCdFacilSvcType()));

			FacilityServiceType facilityServiceType = (FacilityServiceType) crFacilityServiceType.uniqueResult();

			if (!ObjectUtils.isEmpty(facilityServiceType)) {
				facilityServiceType.setDtEffective(facilityServiceTypeAudDto.getDtEffective());
				if (ObjectUtils.isEmpty(facilityServiceTypeAudDto.getDtEnd()))
					facilityServiceType.setDtEnd(ServiceConstants.GENERIC_END_DATE);
				else
					facilityServiceType.setDtEnd(facilityServiceTypeAudDto.getDtEnd());
				//FacilityServiceType shows DtLastUpdate as Non-Nullable
				//so ensuring to set the date to New Date to avoid any null before
				//persisting or updating the entity.
				facilityServiceType.setDtLastUpdate(new Date());
				sessionFactory.getCurrentSession().saveOrUpdate(facilityServiceType);
                //artf276237 : Child Specific Contract Rsrc Creation -END
			}

			break;
		default:
			break;
		}
	}



}
