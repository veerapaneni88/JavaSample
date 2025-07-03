package us.tx.state.dfps.service.resourcedetail.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.resource.detail.dto.RtrvCpaNameParentResIdOutDto;
import us.tx.state.dfps.service.resourcedetail.dao.RtrvParentRsrcIdDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Gets
 * records from Resource and RsrcLink Jan 30, 2018- 12:09:45 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class RtrvParentRsrcIdDaoImpl implements RtrvParentRsrcIdDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${RtrvParentRsrcIdDaoImpl.getCapsResourceRsrcLink}")
	private String getCapsResourceRsrcLink;

	private static final Logger log = Logger.getLogger(RtrvParentRsrcIdDaoImpl.class);

	/**
	 * Method Description: This method is used to retrieve the parent resourceId
	 * based on idResource and cdRsrcLinkType Dam Name: CSECE9D
	 * 
	 * @param idResource
	 * @param cdRsrcLinkType
	 * @return rtrvCpaNameParentResIdOutDtoList @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RtrvCpaNameParentResIdOutDto> getCapsRsrcLink(Long idResource, String cdRsrcLinkType) {
		log.debug("Entering method getCapsResourceRsrcLink in RtrvCpaNameParentResIdDaoImpl");
		List<RtrvCpaNameParentResIdOutDto> rtrvCpaNameParentResIdOutDtoList = new ArrayList<>();

		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCapsResourceRsrcLink)
				.addScalar("idRsrcLinkParent", StandardBasicTypes.LONG)
				.addScalar("nmRsrcLinkParent", StandardBasicTypes.STRING)
				.addScalar("idRsrcLink", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.setParameter("idResource", idResource).setParameter("cdRsrcLinkType", cdRsrcLinkType)
				.setResultTransformer(Transformers.aliasToBean(RtrvCpaNameParentResIdOutDto.class)));
		rtrvCpaNameParentResIdOutDtoList = (List<RtrvCpaNameParentResIdOutDto>) sQLQuery.list();
		// In case No data found for the above query, The Linked Resource is
		// Added first time. Instantiate the output DTO and return the list.
		if (ObjectUtils.isEmpty(rtrvCpaNameParentResIdOutDtoList)) {
			RtrvCpaNameParentResIdOutDto outputDto = new RtrvCpaNameParentResIdOutDto();
			rtrvCpaNameParentResIdOutDtoList.add(outputDto);
		}
		log.debug("Exiting method getCapsResourceRsrcLink in RtrvCpaNameParentResIdDaoImpl");
		return rtrvCpaNameParentResIdOutDtoList;
	}
}
