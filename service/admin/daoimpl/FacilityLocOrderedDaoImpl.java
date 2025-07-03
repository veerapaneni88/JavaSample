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

import us.tx.state.dfps.service.admin.dao.FacilityLocOrderedDao;
import us.tx.state.dfps.service.admin.dto.FacilityLocOrderedInDto;
import us.tx.state.dfps.service.admin.dto.FacilityLocOrderedOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Gets data
 * from Cres07dDao and returns liCres07doDto Aug 22, 2017- 10:58:02 AM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class FacilityLocOrderedDaoImpl implements FacilityLocOrderedDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ResourceChrctrRtrvDaoImpl.getResourceCharacter}")
	private transient String getResourceCharacter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.admin.dao.FacilityLocOrderedDao#
	 * getFacilityLocOrderedOutDtoList(us.tx.state.dfps.service.admin.dto.
	 * FacilityLocOrderedInDto)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FacilityLocOrderedOutDto> getFacilityLocOrderedOutDtoList(
			FacilityLocOrderedInDto facilityLocOrderedInDto) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getResourceCharacter)
				.addScalar("idFloc", StandardBasicTypes.LONG).addScalar("dtFlocEffect", StandardBasicTypes.DATE)
				.addScalar("dtFlocEnd", StandardBasicTypes.DATE).addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("nbrFlocLevelsOfCare", StandardBasicTypes.SHORT)
				.addScalar("cdFlocStatus1", StandardBasicTypes.STRING)
				.addScalar("cdFlocStatus2", StandardBasicTypes.STRING)
				.addScalar("cdFlocStatus3", StandardBasicTypes.STRING)
				.addScalar("cdFlocStatus4", StandardBasicTypes.STRING)
				.addScalar("cdFlocStatus5", StandardBasicTypes.STRING)
				.addScalar("cdFlocStatus6", StandardBasicTypes.STRING)
				.addScalar("cdFlocStatus7", StandardBasicTypes.STRING)
				.addScalar("cdFlocStatus8", StandardBasicTypes.STRING).addScalar("cdFlocStatus9")
				.addScalar("cdFlocStatus10", StandardBasicTypes.STRING)
				.addScalar("cdFlocStatus11", StandardBasicTypes.STRING).addScalar("cdFlocStatus12")
				.addScalar("cdFlocStatus13", StandardBasicTypes.STRING)
				.addScalar("cdFlocStatus14", StandardBasicTypes.STRING)
				.addScalar("cdFlocStatus15", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(FacilityLocOrderedOutDto.class)));
		sQLQuery1.setParameter("idResource", facilityLocOrderedInDto.getIdResource());
		List<FacilityLocOrderedOutDto> facilityLocOrderedOutDtoList = (List<FacilityLocOrderedOutDto>) sQLQuery1.list();

		return facilityLocOrderedOutDtoList;
	}
}
