package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.ResourceAddressRsrcLinkDao;
import us.tx.state.dfps.service.admin.dto.ResourceAddressRsrcLinkInDto;
import us.tx.state.dfps.service.admin.dto.ResourceAddressRsrcLinkOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Retrieves
 * resource name, ID, Region, county, of all subcontractors of a parent
 * resource. For facilities, also retrieves facility type, and acclaim id. Feb
 * 9, 2018- 5:17:12 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class ResourceAddressRsrcLinkDaoImpl implements ResourceAddressRsrcLinkDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ResourceAddressRsrcLinkDaoImpl.searchParentResource}")
	private transient String searchParentResource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.admin.dao.ResourceAddressRsrcLinkDao#
	 * searchSubContractor(us.tx.state.dfps.service.admin.dto.
	 * ResourceAddressRsrcLinkInDto)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceAddressRsrcLinkOutDto> searchSubContractor(ResourceAddressRsrcLinkInDto pInputDataRec) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(searchParentResource)
				.addScalar("idResource", StandardBasicTypes.LONG).addScalar("nmResource", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcFacilAcclaim", StandardBasicTypes.LONG)
				.addScalar("cdRsrcSvcCnty", StandardBasicTypes.STRING)
				.setParameter("idResource", pInputDataRec.getIdResource())
				.setParameter("cdRsrcPhoneType", ServiceConstants.PRIMARY_ADDRESS_TYPE)
				.setParameter("cdRsrcLinkType", ServiceConstants.AGENCY_LINK_TYPE)
				.setResultTransformer(Transformers.aliasToBean(ResourceAddressRsrcLinkOutDto.class)));
		List<ResourceAddressRsrcLinkOutDto> liCres02doDto = (List<ResourceAddressRsrcLinkOutDto>) sQLQuery1.list();
		return liCres02doDto;
	}
}
