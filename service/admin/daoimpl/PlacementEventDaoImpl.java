package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.PlacementEventDao;
import us.tx.state.dfps.service.admin.dto.PlacementEventInDto;
import us.tx.state.dfps.service.admin.dto.PlacementEventOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * determines whether a given person is in pl for a given stage Aug 6, 2017-
 * 10:23:55 AM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class PlacementEventDaoImpl implements PlacementEventDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PlacementEventDaoImpl.hasPLforStage}")
	private String hasPLforStage;

	private static final Logger log = Logger.getLogger(PlacementEventDaoImpl.class);

	public PlacementEventDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: hasPLforStage Method Description: This method will give data
	 * from PLACEMENT and EVENT table. Cinve9d
	 * 
	 * @param placementEventInDto
	 * @return List<PlacementEventOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PlacementEventOutDto> hasPLforStage(PlacementEventInDto placementEventInDto) {
		log.debug("Entering method hasPLforStage in PlacementEventDaoImpl");
		if (placementEventInDto.getIdStage() == null) {
			placementEventInDto.setIdStage(0L);
		}
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(hasPLforStage)
				.addScalar("idPlcmtEvent", StandardBasicTypes.STRING)
				.addScalar("idEventStage", StandardBasicTypes.STRING)
				.setParameter("hI_ulIdStage", placementEventInDto.getIdStage())
				.setParameter("hI_ulIdPerson", placementEventInDto.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(PlacementEventOutDto.class)));
		List<PlacementEventOutDto> placementEventOutDtos = (List<PlacementEventOutDto>) sQLQuery1.list();
		log.debug("Exiting method hasPLforStage in PlacementEventDaoImpl");
		return placementEventOutDtos;
	}
}
