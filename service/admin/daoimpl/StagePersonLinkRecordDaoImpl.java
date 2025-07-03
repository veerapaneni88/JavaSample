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

import us.tx.state.dfps.service.admin.dao.StagePersonLinkRecordDao;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This DAM
 * retrieves a full row of the Stage Person Link Table using Id Stage and Id
 * Person Aug 5, 2017- 8:39:12 AM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class StagePersonLinkRecordDaoImpl implements StagePersonLinkRecordDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StagePersonLinkRecordDaoImpl.getStagePersonLinkRecord}")
	private String getStagePersonLinkRecord;

	@Value("${StagePersonLinkRecordDaoImpl.getStagePersonLinkCount}")
	private String getStagePersonLinkCount;

	private static final Logger log = Logger.getLogger(StagePersonLinkRecordDaoImpl.class);

	public StagePersonLinkRecordDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getStagePersonLinkRecord Method Description: This method
	 * will get data from Stage Person Link table.
	 * 
	 * @param stagePersonLinkRecordInDto
	 * @return List<StagePersonLinkRecordOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StagePersonLinkRecordOutDto> getStagePersonLinkRecord(
			StagePersonLinkRecordInDto stagePersonLinkRecordInDto) {
		log.debug("Entering method StagePersonLinkRecordQUERYdam in StagePersonLinkRecordDaoImpl");
		if (stagePersonLinkRecordInDto.getIdStage() == null)
			stagePersonLinkRecordInDto.setIdStage(0L);
		if (stagePersonLinkRecordInDto.getIdPerson() == null)
			stagePersonLinkRecordInDto.setIdPerson(0L);
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getStagePersonLinkRecord)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("indCdStagePersSearch", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("dtStagePersLink", StandardBasicTypes.TIMESTAMP)
				.addScalar("idStagePerson", StandardBasicTypes.LONG)
				.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("stagePersNotes", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("indCaringAdult", StandardBasicTypes.STRING)
				.addScalar("indNytdDesgContact", StandardBasicTypes.STRING)
				.addScalar("indNytdPrimary", StandardBasicTypes.STRING)
				.addScalar("dtLasteUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(StagePersonLinkRecordOutDto.class)));
		sQLQuery1.setParameter("hI_ulIdStage", stagePersonLinkRecordInDto.getIdStage());
		sQLQuery1.setParameter("hI_ulIdPerson", stagePersonLinkRecordInDto.getIdPerson());
		List<StagePersonLinkRecordOutDto> liCinv39doDto = (List<StagePersonLinkRecordOutDto>) sQLQuery1.list();
		log.debug("Exiting method StagePersonLinkRecordQUERYdam in StagePersonLinkRecordDaoImpl");
		return liCinv39doDto;
	}

	/**
	 * Method Name: getStagePersonLinkCount
	 * Method description : gets the stage person link count for a given person id
	 * @param stagePersonLinkRecordInDto
	 * @return
	 */
	@Override
	public Integer getStagePersonLinkCount(StagePersonLinkRecordInDto stagePersonLinkRecordInDto) {
		SQLQuery query= (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getStagePersonLinkCount)
				.addScalar("stageCount",StandardBasicTypes.INTEGER)
				.setParameter("idPerson", stagePersonLinkRecordInDto.getIdPerson());
		Integer count= (Integer) query.uniqueResult();
		return count;
	}


}
