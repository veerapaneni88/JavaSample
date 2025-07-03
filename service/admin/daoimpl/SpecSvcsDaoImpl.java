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
import us.tx.state.dfps.common.domain.SpecSvcs;
import us.tx.state.dfps.service.admin.dao.SpecSvcsDao;
import us.tx.state.dfps.service.admin.dto.SpecSvcDto;
import us.tx.state.dfps.service.admin.dto.SpecSvcsInDto;
import us.tx.state.dfps.service.admin.dto.SpecSvcsOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.FacilityDetailSaveReq;
import us.tx.state.dfps.service.common.response.FacilityDetailRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Gets data
 * from Cres06dDao and returns liCres06doDto Aug 21, 2017- 6:37:47 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class SpecSvcsDaoImpl implements SpecSvcsDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	@Value("${SpecSvcsDaoImpl.getResourceCharacter}")
	private transient String getResourceCharacter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.admin.dao.SpecSvcsDao#getSpecSvcsOutDtoList(us.
	 * tx.state.dfps.service.admin.dto.SpecSvcsInDto)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SpecSvcsOutDto> getSpecSvcsOutDtoList(SpecSvcsInDto specSvcsInDto) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getResourceCharacter)
				.addScalar("idSpecSvc", StandardBasicTypes.LONG).addScalar("idSpecSvcRsrc", StandardBasicTypes.LONG)
				.addScalar("cdSpecSvcs", StandardBasicTypes.STRING).addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(SpecSvcsOutDto.class)));
		sQLQuery1.setParameter("idSpecSvcRsrc", specSvcsInDto.getIdSpecSvcRsrc());
		List<SpecSvcsOutDto> specSvcsOutDtoList = (List<SpecSvcsOutDto>) sQLQuery1.list();

		return specSvcsOutDtoList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.admin.dao.SpecSvcsDao#specSvcsAud(us.tx.state.
	 * dfps.service.common.request.FacilityDetailSaveReq,
	 * us.tx.state.dfps.service.common.response.FacilityDetailRes)
	 */
	@Override
	public FacilityDetailRes specSvcsAud(FacilityDetailSaveReq facilityDetailSaveReq,
			FacilityDetailRes facilityDetailRes) {
		List<SpecSvcDto> specSvcList = facilityDetailSaveReq.getSpecSvcList();

		for (Iterator<SpecSvcDto> iterator = specSvcList.iterator(); iterator.hasNext();) {
			SpecSvcDto specSvcDto = (SpecSvcDto) iterator.next();

			switch (specSvcDto.getCdScrDataAction()) {
			case ServiceConstants.REQ_FUNC_CD_ADD:

				Criteria crCapsResource = sessionFactory.getCurrentSession().createCriteria(CapsResource.class)
						.add(Restrictions.eq("idResource", specSvcDto.getIdSpecSvcRsrc()));
				CapsResource capsResource = (CapsResource) crCapsResource.uniqueResult();

				// TODO lastUpdate check for concurrency
				SpecSvcs specSvcsEntity = new SpecSvcs();
				if (!ObjectUtils.isEmpty(capsResource))
					specSvcsEntity.setCapsResource(capsResource);
				specSvcsEntity.setCdSpecSvcs(specSvcDto.getCdSpecSvcs());
				specSvcsEntity.setDtLastUpdate(new Date());

				sessionFactory.getCurrentSession().save(specSvcsEntity);

				facilityDetailRes.setIdSpecSvc(specSvcsEntity.getIdSpecSvc());

				break;
			case ServiceConstants.REQ_FUNC_CD_DELETE:
				Criteria crSpecSvcs = sessionFactory.getCurrentSession().createCriteria(SpecSvcs.class)
						.add(Restrictions.eq("idSpecSvc", specSvcDto.getIdSpecSvc()))
						.add(Restrictions.eq("dtLastUpdate", specSvcDto.getTsLastUpdate()));
				SpecSvcs specSvcsEntityForDelete = (SpecSvcs) crSpecSvcs.uniqueResult();

				if (!ObjectUtils.isEmpty(specSvcsEntityForDelete))
					sessionFactory.getCurrentSession().delete(specSvcsEntityForDelete);

				break;
			default:
				break;
			}

		}
		return facilityDetailRes;

	}

}
