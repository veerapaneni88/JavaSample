package us.tx.state.dfps.service.kin.daoimpl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.service.kin.dao.KinPlacementInfoDao;
import us.tx.state.dfps.service.kin.dto.KinPlacementInfoValueDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This is the
 * Bean class used for Placement Information. The Person is checked if he is the
 * Primary Kin Caregiver, If yes, then the resourceId and the resourceName are
 * retrieved. Sep 6, 2017- 4:00:00 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class KinPlacementInfoDaoImpl implements KinPlacementInfoDao {

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Gets the Resource Details based on the personId if the person is Primary
	 * Kinship Caregiver.
	 * 
	 * @param personId
	 * @return KinPlacementInfoValueDto @
	 */

	@SuppressWarnings("unchecked")
	@Override
	public KinPlacementInfoValueDto getResourceDetails(long personId) {
		KinPlacementInfoValueDto kinPlacementInfoValueDto = new KinPlacementInfoValueDto();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
		criteria.add(Restrictions.eq("indKinPrCaregiver", "Y"));
		criteria.add(Restrictions.eq("idPerson", personId));
		List<StagePersonLink> stagePersonLinks = (List<StagePersonLink>) criteria.list();
		if (!ObjectUtils.isEmpty(stagePersonLinks)) {
			StagePersonLink stagePersonLink = stagePersonLinks.get(0);
			criteria = sessionFactory.getCurrentSession().createCriteria(CapsResource.class);
			Stage stage = new Stage();
			stage.setIdStage(stagePersonLink.getIdStage());
			criteria.add(Restrictions.eq("stage", stage));
			List<CapsResource> capsResources = criteria.list();
			if (capsResources.size() > 0) {
				CapsResource capsResource = capsResources.get(0);
				kinPlacementInfoValueDto.setIdResource(capsResource.getIdResource());
				kinPlacementInfoValueDto.setResourceName(capsResource.getNmResource());
				kinPlacementInfoValueDto.setHomeStatus(capsResource.getCdRsrcFaHomeStatus());
				return kinPlacementInfoValueDto;
			}
		}
		return null;
	}

	/**
	 * Gets the status of the home based on the resourceId
	 * 
	 * @param resourceId
	 * @return KinPlacementInfoValueDto @
	 */

	@Override
	public KinPlacementInfoValueDto getHomeStatus(long resourceId) {
		KinPlacementInfoValueDto kinPlacementInfoValueDto = new KinPlacementInfoValueDto();
		CapsResource capsResource = (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class,
				resourceId);
		if (capsResource != null) {
			kinPlacementInfoValueDto.setHomeStatus(capsResource.getCdRsrcFaHomeStatus());
			return kinPlacementInfoValueDto;
		}
		return null;
	}

}
