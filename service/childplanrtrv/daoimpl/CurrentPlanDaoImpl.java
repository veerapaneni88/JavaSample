package us.tx.state.dfps.service.childplanrtrv.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.childplan.dto.ChildPlanLegacyDto;
import us.tx.state.dfps.service.childplan.dto.CurrentlySelectedPlanDto;
import us.tx.state.dfps.service.childplanrtrv.dao.CurrentPlanDao;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * implements the methods declared in CurrentPlanDao Oct 11, 2017- 5:38:53 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class CurrentPlanDaoImpl implements CurrentPlanDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CurrentPlanDaoImpl.getCurrentlySelectedPlans}")
	private transient String getCurrentlySelectedPlans;

	/**
	 * 
	 * Method Name: getCurrentlySelectedPlans Method Description:This methods
	 * returns the list of topics applicable for current selected plan
	 * 
	 * @param childPlanDetailsDto
	 * @return childPlnDto @
	 */
	@Override
	public void getCurrentlySelectedPlans(Long idChildPlanEvent, String cdCspPlanType,
			ChildPlanLegacyDto childPlanLegacyDto) {

		if (!ObjectUtils.isEmpty(cdCspPlanType)) {

			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(getCurrentlySelectedPlans)
					.setResultTransformer(Transformers.aliasToBean(CurrentlySelectedPlanDto.class)));

			sQLQuery1.addScalar("cdCpTopic", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cpTopicTable", StandardBasicTypes.STRING);

			sQLQuery1.setParameter("codeType1", ServiceConstants.CCPTPTBL);
			sQLQuery1.setParameter("codeType2", cdCspPlanType);

			List<CurrentlySelectedPlanDto> currentlySelectedPlanDtoList = (List<CurrentlySelectedPlanDto>) sQLQuery1
					.list();

			if (!ObjectUtils.isEmpty(currentlySelectedPlanDtoList)) {
				currentlySelectedPlanDtoList.forEach(currentlySelectedPlanDto -> {
					String dynamicSQL = ServiceConstants.TOPIC_DT_LAST + currentlySelectedPlanDto.getCpTopicTable();
					dynamicSQL = dynamicSQL + ServiceConstants.WHERE_CLAUSE;
					SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(dynamicSQL)
							.setResultTransformer(Transformers.aliasToBean(CurrentlySelectedPlanDto.class)));

					sQLQuery2.addScalar("dtScrLastUpdate", StandardBasicTypes.DATE);
					sQLQuery2.addScalar("dtNewUsed", StandardBasicTypes.DATE);

					sQLQuery2.setParameter("idChildPlanEvent", idChildPlanEvent);

					CurrentlySelectedPlanDto planDto = (CurrentlySelectedPlanDto) sQLQuery2.uniqueResult();
					if (!ObjectUtils.isEmpty(planDto)) {
						currentlySelectedPlanDto.setDtScrLastUpdate(planDto.getDtScrLastUpdate());
						currentlySelectedPlanDto.setDtNewUsed(planDto.getDtNewUsed());
					}

				});
			}

			childPlanLegacyDto.setCurrentlySelectedPlanDtoList(currentlySelectedPlanDtoList);
		}

	}

}
