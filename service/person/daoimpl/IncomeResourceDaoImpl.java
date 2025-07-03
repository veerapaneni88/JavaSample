package us.tx.state.dfps.service.person.daoimpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.IncomeAndResources;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.person.dao.IncomeResourceDao;
import us.tx.state.dfps.service.person.dto.IncomeAndResourceDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Dao Impl
 * class for Income and Resouce> May 7, 2018- 10:47:02 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class IncomeResourceDaoImpl implements IncomeResourceDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${IncomeResourceDaoImpl.getIncomeAndResourceList}")
	private String getIncomeAndResourceList;

	/**
	 * Method Name: getLatestActiveStartDate Method Description: get
	 * Latest(Date) Active Income & Resource for person id.
	 * 
	 * @param idPerson
	 * @return Date
	 * @throws ParseException
	 */
	@Override
	public Date getLatestActiveStartDate(Long idPerson) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IncomeAndResources.class);
		Criterion condition1 = Restrictions.isNotNull("dtIncRsrcFrom");
		Date date = DateUtils.getMaxJavaDate();
		Criterion condition2 = Restrictions.neOrIsNotNull("dtIncRsrcFrom", date);

		Criterion condition3 = Restrictions.isNull("dtIncRsrcTo");

		Criterion condition4 = Restrictions.ge("dtIncRsrcTo", date);

		Criterion condition5 = Restrictions.eq("personByIdPerson.idPerson", idPerson);

		criteria.add(Restrictions.or(condition3, condition4));
		criteria.add(Restrictions.and(condition1, condition2));
		criteria.add(Restrictions.and(condition5));

		criteria.setProjection(Projections.max("dtIncRsrcFrom"));

		Date maxDate = (Date) criteria.uniqueResult();
		return maxDate;
	}

	/**
	 * 
	 * Method Name: getIncomeAndResourceList Method Description:Fetches the
	 * person income and resources list from snapshot table
	 * (SS_INCOME_AND_RESOURCES) ( For example: This method is used for
	 * displaying the Select Forward person details in post person merge page)
	 * 
	 * @param idPerson
	 * @param sortBy
	 * @param activeFlag
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @return
	 */
	@Override
	public ArrayList<IncomeAndResourceDto> getIncomeAndResourceList(Long idPerson, String sortBy, Boolean activeFlag,
			Long idReferenceData, String cdActionType, String cdSnapshotType) {

		StringBuilder builder = new StringBuilder();
		builder.append(getIncomeAndResourceList);
		if (activeFlag) {
			builder.append(ServiceConstants.ACTIVE_RECORDS);
		}
		if (sortBy != null) {
			builder.append(ServiceConstants.ORDER_BY1 + sortBy);
		}
		Query query1 = sessionFactory.getCurrentSession().createSQLQuery(builder.toString())
				.addScalar("idIncRsrc", StandardBasicTypes.LONG).addScalar("idIncRsrcWorker", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("amount", StandardBasicTypes.DOUBLE).addScalar("type", StandardBasicTypes.STRING)
				.addScalar("incomeOrResource", StandardBasicTypes.STRING)
				.addScalar("effectiveFrom", StandardBasicTypes.DATE).addScalar("effectiveTo", StandardBasicTypes.DATE)
				.addScalar("notAccessible", StandardBasicTypes.STRING).addScalar("source", StandardBasicTypes.STRING)
				.addScalar("verfMethod", StandardBasicTypes.STRING).addScalar("incRsrcDesc", StandardBasicTypes.STRING)
				.setParameter("idReferenceData", idReferenceData).setParameter("cdSnapshotType", cdSnapshotType)
				.setParameter("cdActionType", cdActionType).setParameter("idObject", idPerson)
				.setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(IncomeAndResourceDto.class));
		ArrayList<IncomeAndResourceDto> incomeResourceDtoList = (ArrayList<IncomeAndResourceDto>) query1.list();
		return incomeResourceDtoList;
	}

	/**
	 * Method Name: getIncomeAndResourceList Method Description: This dao will
	 * fetch IncomeAndResourceDtoList.
	 * 
	 * @param idPerson
	 * @param sortBy
	 * @param activeFlag
	 * @return List<IncomeAndResourceDto>
	 * @throws ParseException
	 */
	@Override
	public ArrayList<IncomeAndResourceDto> getIncomeAndResourceList(long idPerson, String sortBy, boolean activeFlag) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IncomeAndResources.class);
		criteria.add(Restrictions.eq("personByIdPerson.idPerson", new Long(idPerson)));

		if (activeFlag) {
			Criterion condition1 = Restrictions.isNull("dtIncRsrcTo");
			Date date = DateUtils.getMaxJavaDate();
			Criterion condition2 = Restrictions.ge("dtIncRsrcTo", date);
			criteria.add(Restrictions.or(condition1, condition2));
		}
		if (sortBy != null) {
			criteria.addOrder(Order.desc(sortBy));
		}
		ArrayList<IncomeAndResourceDto> incomeAndResourceDtoList = new ArrayList<IncomeAndResourceDto>();
		IncomeAndResourceDto incomeAndResourceDto = new IncomeAndResourceDto();
		List<IncomeAndResources> incomeAndResources = criteria.list();
		for (IncomeAndResources incomeAndResourcesiter : incomeAndResources) {

			if (!TypeConvUtil.isNullOrEmpty(incomeAndResourcesiter.getIdIncRsrc())) {
				incomeAndResourceDto.setIdIncRsrc(incomeAndResourcesiter.getIdIncRsrc());
			}
			if (!TypeConvUtil.isNullOrEmpty(incomeAndResourcesiter.getPersonByIdPerson().getIdPerson())) {
				incomeAndResourceDto.setIdPerson(incomeAndResourcesiter.getPersonByIdPerson().getIdPerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(incomeAndResourcesiter.getPersonByIdIncRsrcWorker().getIdPerson())) {
				incomeAndResourceDto
						.setIdIncRsrcWorker(incomeAndResourcesiter.getPersonByIdIncRsrcWorker().getIdPerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(incomeAndResourcesiter.getDtPurged())) {
				incomeAndResourceDto.setScrDtLastUpdate(incomeAndResourcesiter.getDtPurged());
			}
			if (!TypeConvUtil.isNullOrEmpty(incomeAndResourcesiter.getAmtIncRsrc().doubleValue())) {
				incomeAndResourceDto.setAmount(incomeAndResourcesiter.getAmtIncRsrc().doubleValue());
			}
			if (!TypeConvUtil.isNullOrEmpty(incomeAndResourcesiter.getCdIncRsrcType())) {
				incomeAndResourceDto.setType(incomeAndResourcesiter.getCdIncRsrcType());
			}
			if (!TypeConvUtil.isNullOrEmpty(incomeAndResourcesiter.getCdIncRsrcIncome())) {
				incomeAndResourceDto.setIncomeOrResource(incomeAndResourcesiter.getCdIncRsrcIncome());
			}
			if (!TypeConvUtil.isNullOrEmpty(incomeAndResourcesiter.getDtIncRsrcFrom())) {
				incomeAndResourceDto.setEffectiveFrom(incomeAndResourcesiter.getDtIncRsrcFrom());
			}
			if (!TypeConvUtil.isNullOrEmpty(incomeAndResourcesiter.getDtIncRsrcTo())) {
				incomeAndResourceDto.setEffectiveTo(incomeAndResourcesiter.getDtIncRsrcTo());
			}
			if (!TypeConvUtil.isNullOrEmpty(incomeAndResourcesiter.getIndIncRsrcNotAccess().toString())) {
				incomeAndResourceDto.setNotAccessible(incomeAndResourcesiter.getIndIncRsrcNotAccess().toString());
			}
			if (!TypeConvUtil.isNullOrEmpty(incomeAndResourcesiter.getSdsIncRsrcSource())) {
				incomeAndResourceDto.setSource(incomeAndResourcesiter.getSdsIncRsrcSource());
			}
			if (!TypeConvUtil.isNullOrEmpty(incomeAndResourcesiter.getSdsIncRsrcVerfMethod())) {
				incomeAndResourceDto.setVerfMethod(incomeAndResourcesiter.getSdsIncRsrcVerfMethod());
			}
			if (!TypeConvUtil.isNullOrEmpty(incomeAndResourcesiter.getTxtIncRsrcDesc())) {
				incomeAndResourceDto.setIncRsrcDesc(incomeAndResourcesiter.getTxtIncRsrcDesc());
			}
			if (!TypeConvUtil.isNullOrEmpty(incomeAndResourcesiter.getDtLastUpdate())) {
				incomeAndResourceDto.setDtLastUpdate(incomeAndResourcesiter.getDtLastUpdate());
			}

			incomeAndResourceDtoList.add(incomeAndResourceDto);
		}
		return incomeAndResourceDtoList;
	}

	/**
	 * 
	 * Method Name: getIncomeAndResource Method Description:
	 * 
	 * @param idIncomeAndResource
	 * @return
	 */
	@Override
	public IncomeAndResources getIncomeAndResource(int idIncomeAndResource) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IncomeAndResources.class);
		criteria.add(Restrictions.eq("idIncRsrc", (long)idIncomeAndResource));
		return (IncomeAndResources) criteria.uniqueResult();
	}

	@Override
	public void updateIncomeAndResource(IncomeAndResources incomeAndResources) {
		sessionFactory.getCurrentSession().saveOrUpdate(incomeAndResources);
	}

	@Override
	public void saveIncomeAndResource(IncomeAndResources incomeAndResources) {
		sessionFactory.getCurrentSession().save(incomeAndResources);
	}
}
