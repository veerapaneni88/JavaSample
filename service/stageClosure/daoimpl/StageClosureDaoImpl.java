package us.tx.state.dfps.service.stageClosure.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.stageClosure.dao.StageClosureDao;
import us.tx.state.dfps.service.subcare.dto.LglAcnByEvntStageDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.service.servicedlvryclosure.dto.ServiceDlvryClosureDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:StageClosureDaoImpl Sep 6, 2017- 6:32:10 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class StageClosureDaoImpl implements StageClosureDao {

	@Autowired
	SessionFactory sessionFactory;

	@Value("${StageClosureDaoImpl.getPersonIdInFDTCSql}")
	private transient String getPersonIdInFDTCSql;

	@Value("${StageClosureDaoImpl.getMostRecentFDTCSubtype}")
	private transient String getMostRecentFDTCSubtypeSql;

	@Value("${StageClosureDaoImpl.getRunAwayStatus}")
	private String getRunAwayStatusSql;

	/**
	 * Method Name: getPersonIdInFDTC Method Description:This method returns the
	 * list of person that have legal actions of type FDTC for a given case
	 * 
	 * @param caseId
	 * @return HashMap<Integer, String>
	 */
	@Override
	public HashMap<Long, String> getPersonIdInFDTC(Long caseId) {
		HashMap<Long, String> resultHashMap = new HashMap<>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersonIdInFDTCSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING);
		query.setParameter("caseId", caseId);
		query.setParameter("legAct", ServiceConstants.CLEGCPS_CFDT)
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class));

		List<PersonDto> personList = query.list();
		if (personList.size() > ServiceConstants.Zero && personList != null) {

			resultHashMap = new HashMap<Long, String>();

			for (PersonDto personDto : personList) {

				resultHashMap.put(personDto.getIdPerson(), personDto.getNmPersonFull());

			}
		}
		return resultHashMap;

	}

	/**
	 * Method Name: getMostRecentFDTCSubtype Method Description:Returns the most
	 * recent FDTC Subtype and Outcome date for a Person in a given case id
	 * 
	 * @param personId
	 * @return HashMap
	 */
	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, String> getMostRecentFDTCSubtype(Long personId) {
		HashMap<String, String> hashMap = new HashMap<>();
		List<String> legalActionSubtype = new ArrayList<>();
		legalActionSubtype.add(ServiceConstants.CFDT_010);
		legalActionSubtype.add(ServiceConstants.CFDT_020);
		legalActionSubtype.add(ServiceConstants.CFDT_030);
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getMostRecentFDTCSubtypeSql)
				.addScalar("legalActActnSubtype", StandardBasicTypes.STRING)
				.addScalar("idEventStage", StandardBasicTypes.STRING);
		query.setParameter("idPerson", personId);
		query.setParameterList("legalActionSubtype", legalActionSubtype);
		query.setParameter("legalAct", ServiceConstants.CLEGCPS_CFDT)
				.setResultTransformer(Transformers.aliasToBean(ServiceDlvryClosureDto.class));
		// Defect 11338 - Use correct DTO for transformation and retrieve the result
		List<ServiceDlvryClosureDto> serviceDlvryClosure = query.list();
		if (serviceDlvryClosure.size() > ServiceConstants.Zero){
			hashMap.put(ServiceConstants.CD_LEGAL_ACT_ACTN_SUBTYPE, serviceDlvryClosure.get(0).getLegalActActnSubtype());
			hashMap.put(ServiceConstants.ID_EVENT_STAGE, serviceDlvryClosure.get(0).getIdEventStage());
		}
		return hashMap;
	}

	/**
	 * Method Name: getRunAwayStatus Method Description: This method is to check
	 * runAway status
	 * 
	 * @param idPerson
	 * @return
	 */
	@Override
	public boolean getRunAwayStatus(Long idPerson) {
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getRunAwayStatusSql)
				.setParameter("idPerson", idPerson));
		if (null != query.uniqueResult() && ((BigDecimal) query.uniqueResult()).longValue() > 0) {
			return ServiceConstants.TRUE_VALUE;
		}
		return ServiceConstants.FALSEVAL;
	}
}
