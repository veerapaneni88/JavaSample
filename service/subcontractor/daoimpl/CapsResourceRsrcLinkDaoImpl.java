package us.tx.state.dfps.service.subcontractor.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.subcontractor.dao.CapsResourceRsrcLinkDao;
import us.tx.state.dfps.service.subcontractor.dto.CapsResourceRsrcLinkInDto;
import us.tx.state.dfps.service.subcontractor.dto.CapsResourceRsrcLinkOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * 
 * Class Description:Implmentation for CapsResourceRsrcLinkDao
 * 
 * Aug 2, 2017- 8:29:40 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class CapsResourceRsrcLinkDaoImpl implements CapsResourceRsrcLinkDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CapsResourceRsrcLinkDaoImpl.getResource}")
	private String getResource;

	/**
	 * 
	 * Method Name: getResource
	 * 
	 * Method Description:This method will get data from CAPS_RESOURCE and
	 * RSRC_LINK tables.
	 * 
	 * @param pInputDataRec
	 * @return List<CapsResourceRsrcLinkOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CapsResourceRsrcLinkOutDto> getResource(CapsResourceRsrcLinkInDto pInputDataRec) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getResource)
				.addScalar("nmResource", StandardBasicTypes.STRING).addScalar("idRsrcLink", StandardBasicTypes.LONG)
				.addScalar("idRsrcLinkChild", StandardBasicTypes.LONG)
				.addScalar("cdRsrcLinkService", StandardBasicTypes.STRING)
				.addScalar("rsrcLinkLastUpdate", StandardBasicTypes.TIMESTAMP)
				.setParameter("idRsrcLinkParent", pInputDataRec.getIdRsrcLinkParent())
				.setResultTransformer(Transformers.aliasToBean(CapsResourceRsrcLinkOutDto.class)));
		return (List<CapsResourceRsrcLinkOutDto>) sQLQuery1.list();
	}
}
