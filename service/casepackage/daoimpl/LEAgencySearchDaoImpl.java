package us.tx.state.dfps.service.casepackage.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.ResourceEmail;
import us.tx.state.dfps.service.casepackage.dao.LEAgencySearchDao;
import us.tx.state.dfps.service.casepackage.dto.LEAgencySearchDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.LEAgencySearchReq;
import us.tx.state.dfps.service.common.service.CommonService;

@Repository
public class LEAgencySearchDaoImpl implements LEAgencySearchDao {

	public static final String LAW_ENFORCEMENT_NOTIFICATION = "leNotification";

	@Autowired
	CommonService commonService;

	@Autowired
	public SessionFactory sessionFactory;

	@Value("${LEAgencySearch.getLawEnforcementAgencyList}")
	private String getLawEnforcementAgencyList;

	private static final Logger LOG = Logger.getLogger(LEAgencySearchDaoImpl.class);

	public LEAgencySearchDaoImpl() {

	}

	/**
	 * 
	 * Method Description: This Method will retrieve rows from the CAPS_RESOURCE
	 * table given CD_RSRC_TYPE as 'Law Enforcement Agency' CD_RSRC_STATUS as
	 * 'Active' CD_RSRC_STATE as 'Taxes' And one of the following search
	 * criteria nm_resource, addr_rsrc_city or cd_rsrc_cnty
	 * 
	 * @param leAgencySearchReq
	 * @return List<LEAgencySearchDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LEAgencySearchDto> getLawEnforcementAgencyList(LEAgencySearchReq leAgencySearchReq) {
		String resourceName = null;
		if(LAW_ENFORCEMENT_NOTIFICATION.equalsIgnoreCase(leAgencySearchReq.getSearchType())){
			resourceName = leAgencySearchReq.getNmResource();
		}else{
			resourceName = leAgencySearchReq.getSearchResourceName();
		}
		List<LEAgencySearchDto> leAgencySearchDtoList = ((List<LEAgencySearchDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getLawEnforcementAgencyList).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("nmResource", StandardBasicTypes.STRING)
				.addScalar("cdResourceStatus", StandardBasicTypes.STRING)
				.addScalar("cdResourceType", StandardBasicTypes.STRING)
				.addScalar("cdJurisdiction", StandardBasicTypes.STRING)
				.addScalar("cdFacilityType", StandardBasicTypes.STRING)
				.addScalar("cdResourceState", StandardBasicTypes.STRING)
				.addScalar("addrResourceCity", StandardBasicTypes.STRING)
				.addScalar("cdResourceCnty", StandardBasicTypes.STRING)
				.addScalar("addrRsrcStLn1", StandardBasicTypes.STRING).addScalar("rsrcPhn", StandardBasicTypes.STRING)
				.addScalar("rsrcPhoneExt", StandardBasicTypes.STRING)
				.addScalar("indRsrcContracted", StandardBasicTypes.CHARACTER)
				.addScalar("cdPrefrdContact", StandardBasicTypes.STRING)
				.setParameter("nmResource", "%" + resourceName + "%")
				.setParameter("addrRsrcCity", leAgencySearchReq.getAddrResourceCity())
				.setParameter("cdRsrcCnty", leAgencySearchReq.getCdResourceCnty())
				.setParameter("idResource", leAgencySearchReq.getSearchIdResource())
				.setResultTransformer(Transformers.aliasToBean(LEAgencySearchDto.class)).list());
		if(LAW_ENFORCEMENT_NOTIFICATION.equalsIgnoreCase(leAgencySearchReq.getSearchType())){
			List<String> emailList;
			LEAgencySearchReq leAgencySearchReq1 = new LEAgencySearchReq();
			for (LEAgencySearchDto lEAgencySearchDto : leAgencySearchDtoList) {
				leAgencySearchReq1.setIdResource(lEAgencySearchDto.getIdResource());
				emailList = new ArrayList<String>();
				emailList = getResourceEmail(leAgencySearchReq1);
				lEAgencySearchDto.setEmailList(emailList);
			}
		}
		return leAgencySearchDtoList;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve resource email from
	 * RESOURCE_Email table given id_Resource
	 * 
	 * @param leAgencySearchReq
	 * @return List<LEAgencySearchDto> @
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getResourceEmail(LEAgencySearchReq leAgencySearchReq) {

		List<String> emailList = (List<String>) sessionFactory.getCurrentSession().createCriteria(ResourceEmail.class)
				.setProjection(Projections.projectionList().add(Projections.property("txtEmailAddress")))
				.add(Restrictions.eq("idResource", leAgencySearchReq.getIdResource()))
				.add(Restrictions.eq("indPrimary", ServiceConstants.YES))
				.add(Restrictions.eq("dtEnd", ServiceConstants.GENERIC_END_DATE)).list();

		LOG.debug("TransactionId :" + leAgencySearchReq.getTransactionId());

		return emailList;
	}

}
