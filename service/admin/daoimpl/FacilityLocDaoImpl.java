package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Date;
import java.util.Iterator;
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
import us.tx.state.dfps.common.domain.FacilityLoc;
import us.tx.state.dfps.service.admin.dao.FacilityLocDao;
import us.tx.state.dfps.service.admin.dto.FacilityLocDto;
import us.tx.state.dfps.service.admin.dto.FacilityLocInDto;
import us.tx.state.dfps.service.admin.dto.FacilityLocOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.FacilityDetailSaveReq;
import us.tx.state.dfps.service.common.response.FacilityDetailRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Retrieves
 * up to 9 levels of care for each facility in the output message. Aug 9, 2017-
 * 7:40:14 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class FacilityLocDaoImpl implements FacilityLocDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FacilityLocDaoImpl.getFacilityCare}")
	private transient String getFacilityCare;

	@Value("${FacilityLocDaoImpl.getFacilityLocByResource}")
	private String getFacilityLocByResource;

	@Value("${FacilityLocDaoImpl.getFacilityLocByRsrcId}")
	private String getFacilityLocByRsrcId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.admin.dao.FacilityLocDao#getFacilityCare(us.tx.
	 * state.dfps.service.admin.dto.FacilityLocInDto)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FacilityLocOutDto> getFacilityCare(FacilityLocInDto facilityLocInDto) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFacilityCare)
				.addScalar("nbrFlocLevelsOfCare", StandardBasicTypes.SHORT)
				.addScalar("dtFlocEffect", StandardBasicTypes.DATE).addScalar("cdFlocStatus1")
				.addScalar("cdFlocStatus2").addScalar("cdFlocStatus3").addScalar("cdFlocStatus4")
				.addScalar("cdFlocStatus5").addScalar("cdFlocStatus6").addScalar("cdFlocStatus7")
				.addScalar("cdFlocStatus8").addScalar("cdFlocStatus9").addScalar("cdFlocStatus10")
				.addScalar("cdFlocStatus11").addScalar("cdFlocStatus12").addScalar("cdFlocStatus13")
				.addScalar("cdFlocStatus14").addScalar("cdFlocStatus15")
				.setParameter("idResource", facilityLocInDto.getIdResource())
				.setResultTransformer(Transformers.aliasToBean(FacilityLocOutDto.class)));
		List<FacilityLocOutDto> facilityLocOutDtoList = (List<FacilityLocOutDto>) sQLQuery1.list();

		return facilityLocOutDtoList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.admin.dao.FacilityLocDao#
	 * getFclityLocByResourceId(us.tx.state.dfps.service.admin.dto.
	 * FacilityLocInDto)
	 */
	@Override
	public List<FacilityLocDto> getFclityLocByResourceId(FacilityLocInDto facilityLocInDto) {
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFacilityLocByResource)
				.addScalar("idFloc", StandardBasicTypes.LONG).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("nbrFlocLevelsOfCare", StandardBasicTypes.LONG)
				.addScalar("dtFlocEffect", StandardBasicTypes.DATE).addScalar("dtFlocEnd", StandardBasicTypes.DATE)
				.addScalar("indFlocCancelHist", StandardBasicTypes.STRING).addScalar("cdFlocStatus1")
				.addScalar("cdFlocStatus2").addScalar("cdFlocStatus3").addScalar("cdFlocStatus4")
				.addScalar("cdFlocStatus5").addScalar("cdFlocStatus6").addScalar("cdFlocStatus7")
				.addScalar("cdFlocStatus8").addScalar("cdFlocStatus9").addScalar("cdFlocStatus10")
				.addScalar("cdFlocStatus11").addScalar("cdFlocStatus12").addScalar("cdFlocStatus13")
				.addScalar("cdFlocStatus14").addScalar("cdFlocStatus15")
				.setParameter("idResource", facilityLocInDto.getIdResource())
				.setParameter("dtPlacementStart", facilityLocInDto.getPlcmtStartDate())
				.setResultTransformer(Transformers.aliasToBean(FacilityLocDto.class)));
		return (List<FacilityLocDto>) sQLQuery.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.admin.dao.FacilityLocDao#getFclityLocByRsrcId(us
	 * .tx.state.dfps.service.admin.dto.FacilityLocInDto)
	 */
	@Override
	public List<FacilityLocDto> getFclityLocByRsrcId(FacilityLocInDto facilityLocInDto) {
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFacilityLocByRsrcId)
				.addScalar("idFloc", StandardBasicTypes.LONG).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("nbrFlocLevelsOfCare", StandardBasicTypes.LONG)
				.addScalar("dtFlocEffect", StandardBasicTypes.DATE).addScalar("dtFlocEnd", StandardBasicTypes.DATE)
				.addScalar("indFlocCancelHist", StandardBasicTypes.STRING).addScalar("cdFlocStatus1")
				.addScalar("cdFlocStatus2").addScalar("cdFlocStatus3").addScalar("cdFlocStatus4")
				.addScalar("cdFlocStatus5").addScalar("cdFlocStatus6").addScalar("cdFlocStatus7")
				.addScalar("cdFlocStatus8").addScalar("cdFlocStatus9").addScalar("cdFlocStatus10")
				.addScalar("cdFlocStatus11").addScalar("cdFlocStatus12").addScalar("cdFlocStatus13")
				.addScalar("cdFlocStatus14").addScalar("cdFlocStatus15")
				.setParameter("idResource", facilityLocInDto.getIdResource())
				.setParameter("dtPlacementStart", facilityLocInDto.getPlcmtStartDate())
				.setResultTransformer(Transformers.aliasToBean(FacilityLocDto.class)));
		return (List<FacilityLocDto>) sQLQuery.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.admin.dao.FacilityLocDao#updateFacilityLoc(us.tx
	 * .state.dfps.service.common.request.FacilityDetailSaveReq,
	 * us.tx.state.dfps.service.common.response.FacilityDetailRes)
	 */
	@Override
	public FacilityDetailRes updateFacilityLoc(FacilityDetailSaveReq facilityDetailSaveReq,
			FacilityDetailRes facilityDetailRes) {
		List<FacilityLocDto> facilityLocList = facilityDetailSaveReq.getFacilityLocList();

		for (Iterator<FacilityLocDto> iterator = facilityLocList.iterator(); iterator.hasNext();) {
			FacilityLocDto facilityLocDto = (FacilityLocDto) iterator.next();

			switch (facilityLocDto.getCdScrDataAction()) {
			case ServiceConstants.REQ_FUNC_CD_ADD:

				FacilityLoc newFacilityLocEntity = new FacilityLoc();

				Criteria crCapsResource = sessionFactory.getCurrentSession().createCriteria(CapsResource.class)
						.add(Restrictions.eq("idResource", facilityLocDto.getIdResource()));
				CapsResource capsResource = (CapsResource) crCapsResource.uniqueResult();

				if (!ObjectUtils.isEmpty(capsResource))
					newFacilityLocEntity.setCapsResource(capsResource);

				newFacilityLocEntity.setDtFlocEffect(facilityLocDto.getDtFlocEffect());
				newFacilityLocEntity.setDtFlocEnd(facilityLocDto.getDtFlocEnd());
				newFacilityLocEntity.setCdFlocStatus1(facilityLocDto.getCdFlocStatus1());
				newFacilityLocEntity.setCdFlocStatus2(facilityLocDto.getCdFlocStatus2());
				newFacilityLocEntity.setCdFlocStatus3(facilityLocDto.getCdFlocStatus3());
				newFacilityLocEntity.setCdFlocStatus4(facilityLocDto.getCdFlocStatus4());
				newFacilityLocEntity.setCdFlocStatus5(facilityLocDto.getCdFlocStatus5());
				newFacilityLocEntity.setCdFlocStatus6(facilityLocDto.getCdFlocStatus6());
				newFacilityLocEntity.setCdFlocStatus7(facilityLocDto.getCdFlocStatus7());
				newFacilityLocEntity.setCdFlocStatus8(facilityLocDto.getCdFlocStatus8());
				newFacilityLocEntity.setCdFlocStatus9(facilityLocDto.getCdFlocStatus9());
				newFacilityLocEntity.setCdFlocStatus10(facilityLocDto.getCdFlocStatus10());
				newFacilityLocEntity.setCdFlocStatus11(facilityLocDto.getCdFlocStatus11());
				newFacilityLocEntity.setCdFlocStatus12(facilityLocDto.getCdFlocStatus12());
				newFacilityLocEntity.setCdFlocStatus13(facilityLocDto.getCdFlocStatus13());
				newFacilityLocEntity.setCdFlocStatus14(facilityLocDto.getCdFlocStatus14());
				newFacilityLocEntity.setCdFlocStatus15(facilityLocDto.getCdFlocStatus15());
				newFacilityLocEntity.setIndFlocCancelHist(facilityLocDto.getIndFlocCancelHist());
				newFacilityLocEntity
						.setNbrFlocLevelsOfCare(!ObjectUtils.isEmpty(facilityLocDto.getNbrFlocLevelsOfCare())
								? facilityLocDto.getNbrFlocLevelsOfCare().byteValue() : null);
				newFacilityLocEntity.setDtLastUpdate(new Date());

				sessionFactory.getCurrentSession().save(newFacilityLocEntity);

				facilityDetailRes.setIdFloc(newFacilityLocEntity.getIdFloc());

				break;

			case ServiceConstants.REQ_FUNC_CD_UPDATE:

				// TODO lastUpdate check for concurrency
				Criteria crFacilityLoc = sessionFactory.getCurrentSession().createCriteria(FacilityLoc.class)
						.add(Restrictions.eq("idFloc", facilityLocDto.getIdFloc()));

				FacilityLoc facilityLocEntity = (FacilityLoc) crFacilityLoc.uniqueResult();

				if (!ObjectUtils.isEmpty(facilityLocEntity)) {

					facilityLocEntity.setDtFlocEnd(facilityLocDto.getDtFlocEnd());
					facilityLocEntity.setCdFlocStatus1(facilityLocDto.getCdFlocStatus1());
					facilityLocEntity.setCdFlocStatus2(facilityLocDto.getCdFlocStatus2());
					facilityLocEntity.setCdFlocStatus3(facilityLocDto.getCdFlocStatus3());
					facilityLocEntity.setCdFlocStatus4(facilityLocDto.getCdFlocStatus4());
					facilityLocEntity.setCdFlocStatus5(facilityLocDto.getCdFlocStatus5());
					facilityLocEntity.setCdFlocStatus6(facilityLocDto.getCdFlocStatus6());
					facilityLocEntity.setCdFlocStatus7(facilityLocDto.getCdFlocStatus7());
					facilityLocEntity.setCdFlocStatus8(facilityLocDto.getCdFlocStatus8());
					facilityLocEntity.setCdFlocStatus9(facilityLocDto.getCdFlocStatus9());
					facilityLocEntity.setCdFlocStatus10(facilityLocDto.getCdFlocStatus10());
					facilityLocEntity.setCdFlocStatus11(facilityLocDto.getCdFlocStatus11());
					facilityLocEntity.setCdFlocStatus12(facilityLocDto.getCdFlocStatus12());
					facilityLocEntity.setCdFlocStatus13(facilityLocDto.getCdFlocStatus13());
					facilityLocEntity.setCdFlocStatus14(facilityLocDto.getCdFlocStatus14());
					facilityLocEntity.setCdFlocStatus15(facilityLocDto.getCdFlocStatus15());

					sessionFactory.getCurrentSession().saveOrUpdate(facilityLocEntity);

				}
				break;

			default:
				break;
			}
		}
		return facilityDetailRes;
	}
}
