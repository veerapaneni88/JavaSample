package us.tx.state.dfps.service.conservatorship.daoimpl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dao.StageStagePersonLinkStgRoleDao;
import us.tx.state.dfps.service.cvs.dto.StageStagePersonLinkStgRoleInDto;
import us.tx.state.dfps.service.cvs.dto.StageStagePersonLinkStgRoleOutDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * fetches the stage ID details Aug 12, 2017- 2:44:31 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class StageStagePersonLinkStgRoleDaoImpl implements StageStagePersonLinkStgRoleDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StageStagePersonLinkStgRoleDaoImpl.getStageDtl}")
	private transient String getStageID;

	private static final Logger log = Logger.getLogger(StageStagePersonLinkStgRoleDaoImpl.class);

	/**
	 * Method Name:getStageID Method desc:This method fetches the stage ID
	 * details Legacy Name:csub84dQUERYdam
	 * 
	 * @param stageStagePersonLinkStgRoleInDto
	 * @param pOutputDataRec
	 * @return List<Csub84doDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StageStagePersonLinkStgRoleOutDto> getStageID(
			StageStagePersonLinkStgRoleInDto stageStagePersonLinkStgRoleInDto) {
		log.debug("Entering method getStageID in StageStagePersonLinkStgRoleDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getStageID)
				.setResultTransformer(Transformers.aliasToBean(StageStagePersonLinkStgRoleOutDto.class)));
		sQLQuery1.addScalar("idStage", StandardBasicTypes.LONG);
		sQLQuery1.setParameter("idPriorStage", stageStagePersonLinkStgRoleInDto.getIdPriorStage());
		sQLQuery1.setParameter("cdStage", stageStagePersonLinkStgRoleInDto.getCdStage());
		sQLQuery1.setParameter("idPerson", stageStagePersonLinkStgRoleInDto.getIdPerson());
		sQLQuery1.setParameter("cdStagePersRole", stageStagePersonLinkStgRoleInDto.getCdStagePersRole());
		List<StageStagePersonLinkStgRoleOutDto> liCsub84doDto = (List<StageStagePersonLinkStgRoleOutDto>) sQLQuery1
				.list();
		if (TypeConvUtil.isNullOrEmpty(liCsub84doDto) || liCsub84doDto.size() == 0) {
			throw new DataNotFoundException(messageSource.getMessage("getStageID.not.found.ulIdCase", null, Locale.US));
		}
		log.debug("Exiting method getStageID in StageStagePersonLinkStgRoleDaoImpl");
		return liCsub84doDto;
	}
}
