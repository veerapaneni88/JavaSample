/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jan 4, 2018- 2:16:35 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.resourcehistory.daoimpl;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ResourceHistory;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.resource.dto.ResourceHistoryDto;
import us.tx.state.dfps.service.resource.dto.ResourceHistoryInDto;
import us.tx.state.dfps.service.resourcehistory.dao.ResourceHistoryDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * is used to perform CRUD operations on ResourceHistory Jan 4, 2018- 2:16:35 PM
 * © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class ResourceHistoryDaoImpl implements ResourceHistoryDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ResourceHistoryDaoImpl.getResourceHistoryById}")
	private String getResourceHistoryById;

	@Value("${ResourceHistoryDaoImpl.getResourceHistoryStageId}")
	private String getResourceHistoryStageId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.resourcehistory.dao.ResourceHistoryDao#
	 * getRsrcHistoryByIdAndDate(us.tx.state.dfps.service.resource.dto.
	 * ResourceHistoryInDto)
	 */
	@Override
	public ResourceHistoryDto getRsrcHistory(ResourceHistoryInDto resourceHistoryInDto) {

		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getResourceHistoryById)
				.addScalar("idResourceHistory", StandardBasicTypes.LONG)
				.addScalar("idResource", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("dtRshsEffective", StandardBasicTypes.DATE).addScalar("dtRshsClose", StandardBasicTypes.DATE)
				.addScalar("dtRshsCert", StandardBasicTypes.DATE).addScalar("dtRshsMarriage", StandardBasicTypes.DATE)
				.addScalar("dtRshsEnd", StandardBasicTypes.DATE).addScalar("addrRshsStLn1", StandardBasicTypes.STRING)
				.addScalar("addrRshsStLn2", StandardBasicTypes.STRING)
				.addScalar("addrRshsCity", StandardBasicTypes.STRING)
				.addScalar("cdRshsState", StandardBasicTypes.STRING).addScalar("addrRshsZip", StandardBasicTypes.STRING)
				.addScalar("addrRshsAttn", StandardBasicTypes.STRING).addScalar("cdRshsCnty", StandardBasicTypes.STRING)
				.addScalar("cdRshsInvolClosure", StandardBasicTypes.STRING)
				.addScalar("cdRshsClosureRsn", StandardBasicTypes.STRING)
				.addScalar("cdRshsType", StandardBasicTypes.STRING)
				.addScalar("cdRshsCampusType", StandardBasicTypes.STRING)
				.addScalar("cdRshsSourceInquiry", StandardBasicTypes.STRING)
				.addScalar("cdRshsMaintainer", StandardBasicTypes.STRING)
				.addScalar("cdRshsSchDist", StandardBasicTypes.STRING)
				.addScalar("cdRshsOwnership", StandardBasicTypes.STRING)
				.addScalar("cdRshsStatus", StandardBasicTypes.STRING)
				.addScalar("cdRshsFacilType", StandardBasicTypes.STRING)
				.addScalar("cdRshsCertBy", StandardBasicTypes.STRING)
				.addScalar("cdRshsOperBy", StandardBasicTypes.STRING)
				.addScalar("cdRshsSetting", StandardBasicTypes.STRING)
				.addScalar("cdRshsPayment", StandardBasicTypes.STRING)
				.addScalar("cdRshsCategory", StandardBasicTypes.STRING)
				.addScalar("cdRshsEthnicity", StandardBasicTypes.STRING)
				.addScalar("cdRshsLanguage", StandardBasicTypes.STRING)
				.addScalar("cdRshsMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("cdRshsRecmndReopen", StandardBasicTypes.STRING)
				.addScalar("cdRshsRegion", StandardBasicTypes.STRING)
				.addScalar("cdRshsReligion", StandardBasicTypes.STRING)
				.addScalar("cdRshsRespite", StandardBasicTypes.STRING)
				.addScalar("cdRshsFaHomeStatus", StandardBasicTypes.STRING)
				.addScalar("cdRshsFaHomeType1", StandardBasicTypes.STRING)
				.addScalar("cdRshsFaHomeType2", StandardBasicTypes.STRING)
				.addScalar("cdRshsFaHomeType3", StandardBasicTypes.STRING)
				.addScalar("cdRshsFaHomeType4", StandardBasicTypes.STRING)
				.addScalar("cdRshsFaHomeType5", StandardBasicTypes.STRING)
				.addScalar("cdRshsFaHomeType6", StandardBasicTypes.STRING)
				.addScalar("cdRshsFaHomeType7", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("indRshsCareProv", StandardBasicTypes.STRING)
				.addScalar("indRshsInactive", StandardBasicTypes.STRING)
				.addScalar("indRshsTransport", StandardBasicTypes.STRING)
				.addScalar("indRshsIndivStudy", StandardBasicTypes.STRING)
				.addScalar("indRshsNonprs ", StandardBasicTypes.STRING)
				.addScalar("indRshsEmergPlace ", StandardBasicTypes.STRING)
				.addScalar("nmRshsResource", StandardBasicTypes.STRING)
				.addScalar("nmRshsContact", StandardBasicTypes.STRING)
				.addScalar("nmRshsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("nbrRshsVid", StandardBasicTypes.LONG).addScalar("nbrRshsPhn", StandardBasicTypes.LONG)
				.addScalar("nbrRshsFacilCapacity", StandardBasicTypes.LONG)
				.addScalar("nbrRshsFacilAcclaim", StandardBasicTypes.LONG)
				.addScalar("nbrRshsPhoneExt", StandardBasicTypes.LONG)
				.addScalar("nbrRshsCampusNbr", StandardBasicTypes.LONG)
				.addScalar("nbrRshsAnnualIncome", StandardBasicTypes.LONG)
				.addScalar("nbrRshsFmAgeMax", StandardBasicTypes.LONG)
				.addScalar("nbrRshsFmAgeMin", StandardBasicTypes.LONG)
				.addScalar("nbrRshsMaAgeMax", StandardBasicTypes.LONG)
				.addScalar("nbrRshsMaAgeMin", StandardBasicTypes.LONG)
				.addScalar("nbrRshsIntChildren", StandardBasicTypes.LONG)
				.addScalar("nbrRshsIntFeAgeMax", StandardBasicTypes.LONG)
				.addScalar("nbrRshsIntFeAgeMin", StandardBasicTypes.LONG)
				.addScalar("nbrRshsIntMaAgeMax", StandardBasicTypes.LONG)
				.addScalar("nbrRshsIntMaAgeMin", StandardBasicTypes.LONG)
				.addScalar("txtRshsAddrCmnts", StandardBasicTypes.STRING)
				.addScalar("txtRshsComments", StandardBasicTypes.STRING));
		sQLQuery.setParameter("idRsrc", resourceHistoryInDto.getIdRsrc());
		sQLQuery.setParameter("dtPlacementStart", resourceHistoryInDto.getDtPlacementStart());
		sQLQuery.setResultTransformer(Transformers.aliasToBean(ResourceHistoryDto.class));
		List<ResourceHistoryDto> resourceHistoryDtoList = sQLQuery.list();
		if (!ObjectUtils.isEmpty(resourceHistoryDtoList)) {
			return resourceHistoryDtoList.get(0);
		}

		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.resourcehistory.dao.ResourceHistoryDao#
	 * getRsrcHistoryByIdAndDate(us.tx.state.dfps.service.resource.dto.
	 * ResourceHistoryInDto)
	 */
	@Override
	public ResourceHistory getRsrcHistoryByIdAndDate(ResourceHistoryInDto resourceHistoryInDto) {
		java.util.Date dtStart = resourceHistoryInDto.getDtPlacementStart();
		Calendar cal = Calendar.getInstance();
		cal.setTime(dtStart);
		cal.add(Calendar.DATE, 1); // add 1 days
		dtStart = cal.getTime();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceHistory.class);
		criteria.add(Restrictions.eq("idResource", resourceHistoryInDto.getIdRsrc()));
		criteria.add(Restrictions.lt("dtRshsEffective", dtStart));
		criteria.add(Restrictions.gt("dtRshsEnd", resourceHistoryInDto.getDtPlacementStart()));
		criteria.addOrder(Order.desc("dtRshsEffective"));
		
		List<ResourceHistory> resourceHistoryList = (List<ResourceHistory>) criteria.list();
		if (!ObjectUtils.isEmpty(resourceHistoryList)) {
			return resourceHistoryList.get(0);
		}
		return null;

	}

	@Override
	public List<ResourceHistory>  getRsrcHistoryByStageId(ResourceDto resourceDto ) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceHistory.class);
		criteria.add(Restrictions.eq("stage.idStage", resourceDto.getIdStage()));
		criteria.addOrder(Order.desc("dtRshsEffective"));
		criteria.addOrder(Order.desc("idResourceHistory"));
		List<ResourceHistory> resourceHistoryList = (List<ResourceHistory>) criteria.list();
		return resourceHistoryList;
	}

}
