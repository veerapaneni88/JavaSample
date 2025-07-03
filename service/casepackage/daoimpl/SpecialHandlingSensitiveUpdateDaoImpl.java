package us.tx.state.dfps.service.casepackage.daoimpl;

import java.util.ArrayList;
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
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.casepackage.dao.SpecialHandlingSensitiveUpdateDao;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingSensitiveUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingSensitiveUpdateOutDto;

@Repository
public class SpecialHandlingSensitiveUpdateDaoImpl implements SpecialHandlingSensitiveUpdateDao {
	@Autowired
	MessageSource messageSource;

	@Value("${SpecialHandlingSensitiveUpdateDaoImpl.specialHandlingSensitive}")
	private String specialHandlingSensitive;

	@Value("${SpecialHandlingSensitiveUpdateDaoImpl.specialHandlingSensitiveUpdate1}")
	private String specialHandlingSensitiveUpdate1;

	@Value("${SpecialHandlingSensitiveUpdateDaoImpl.specialHandlingSensitiveUpdate2}")
	private String specialHandlingSensitiveUpdate2;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger("ServiceBusiness-EmployeeDaoLog");

	/**
	 * Method Name: specialHandlingStageDetailFetch Method Description: fetch
	 * count of ID_PRIOR_STAGE from stage_link and update the
	 * IND_INCMG_SENSITIVE in INCOMING_DETAIL, DAM cinve7d
	 * 
	 * @param specialHandlingSensitiveUpdateInDto
	 * @param specialHandlingSensitiveUpdateOutDto
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void specialHandlingSensitiveUpdate(SpecialHandlingSensitiveUpdateInDto specialHandlingSensitiveUpdateInDto,
			SpecialHandlingSensitiveUpdateOutDto specialHandlingSensitiveUpdateOutDto) {
		log.debug("Entering method specialHandlingSensitiveUpdate in SpecialHandlingSensitiveUpdateDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(specialHandlingSensitive)
				.addScalar("totalRecCount", StandardBasicTypes.LONG)
				.setParameter("idCase", specialHandlingSensitiveUpdateInDto.getIdCase())
				.setResultTransformer(Transformers.aliasToBean(SpecialHandlingSensitiveUpdateOutDto.class)));
		List<SpecialHandlingSensitiveUpdateOutDto> liCinve7doDto = new ArrayList<>();
		liCinve7doDto = (List<SpecialHandlingSensitiveUpdateOutDto>) sQLQuery1.list();

		Long hO_ldPreviousStageCount = 0L;

		if (!CollectionUtils.isEmpty(liCinve7doDto)) {
			hO_ldPreviousStageCount = liCinve7doDto.get(0).getTotalRecCount();
		}

		if (hO_ldPreviousStageCount == 0) {
			SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(specialHandlingSensitiveUpdate1)
					.setParameter("indCaseSensitive", specialHandlingSensitiveUpdateInDto.getIndCaseSensitive())
					.setParameter("idCase", specialHandlingSensitiveUpdateInDto.getIdCase()));
			sQLQuery2.executeUpdate();
		} else if (hO_ldPreviousStageCount > 0) {
			SQLQuery sQLQuery3 = ((SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(specialHandlingSensitiveUpdate2)
					.setParameter("cdStageType", specialHandlingSensitiveUpdateInDto.getCdStageType())
					.setParameter("indCaseSensitive", specialHandlingSensitiveUpdateInDto.getIndCaseSensitive())
					.setParameter("idCase", specialHandlingSensitiveUpdateInDto.getIdCase()));
			sQLQuery3.executeUpdate();
		}

		log.debug("Exiting method specialHandlingSensitiveUpdate in SpecialHandlingSensitiveUpdateDaoImpl");
	}

}
