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

import us.tx.state.dfps.service.admin.dao.StagePersonLinkMergeViewDao;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkMergeViewInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkMergeViewOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This joins
 * the Stage Person Link and the stage Table. to determine all of the active
 * programs and stages that a that a person is involved in Aug 5, 2017- 11:59:43
 * AM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class StagePersonLinkMergeViewDaoImpl implements StagePersonLinkMergeViewDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StagePersonLinkMergeViewDaoImpl.getActive_Prog_Stage_PID}")
	private String getActive_Prog_Stage_PID;

	private static final Logger log = Logger.getLogger(StagePersonLinkMergeViewDaoImpl.class);

	public StagePersonLinkMergeViewDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getActive_Prog_Stage_PID Method Description: This method
	 * will get data from STAGE ,STAGE_PERSON_LINK and PERSON_MERGE_VIEW tables.
	 * Cinv33d
	 * 
	 * @param stagePersonLinkMergeViewInDto
	 * @return List<StagePersonLinkMergeViewOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StagePersonLinkMergeViewOutDto> getActiveProgStagePID(
			StagePersonLinkMergeViewInDto stagePersonLinkMergeViewInDto) {
		log.debug("Entering method getActiveProgStagePID in StagePersonLinkMergeViewDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getActive_Prog_Stage_PID)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("idStage", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(StagePersonLinkMergeViewOutDto.class)));
		sQLQuery1.setParameter("hI_ulIdPerson", stagePersonLinkMergeViewInDto.getIdPerson());
		List<StagePersonLinkMergeViewOutDto> stagePersonLinkMergeViewOutDtos = (List<StagePersonLinkMergeViewOutDto>) sQLQuery1
				.list();
		log.debug("Exiting method getActiveProgStagePID in StagePersonLinkMergeViewDaoImpl");
		return stagePersonLinkMergeViewOutDtos;
	}
}
