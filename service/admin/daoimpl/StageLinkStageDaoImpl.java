package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
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

import us.tx.state.dfps.service.admin.dao.StageLinkStageDao;
import us.tx.state.dfps.service.admin.dto.StageLinkStageInDto;
import us.tx.state.dfps.service.admin.dto.StageLinkStageOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO Impl
 * for getting Prior Stage from Stage_Link> Aug 8, 2017- 3:53:12 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class StageLinkStageDaoImpl implements StageLinkStageDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StageLinkStageDaoImpl.getPriorStage}")
	private transient String getPriorStage;

	private static final Logger log = Logger.getLogger(StageLinkStageDaoImpl.class);

	public StageLinkStageDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getPriorStage Method Description: This method will get
	 * IdPrior Stage from Stage Person Link and Stage table. DAM Name: cseca8d
	 * 
	 * @param pInputDataRec
	 * @return List<StageLinkStageOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StageLinkStageOutDto> getPriorStage(StageLinkStageInDto pInputDataRec) {
		log.debug("Entering method StageLinkStageQUERYdam in StageLinkStageDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPriorStage)
				.setResultTransformer(Transformers.aliasToBean(StageLinkStageOutDto.class)));
		sQLQuery1.addScalar("idPriorStage", StandardBasicTypes.LONG).setParameter("hI_ulIdStage",
				pInputDataRec.getIdStage());
		List<StageLinkStageOutDto> liCseca8doDto = new ArrayList<>();
		liCseca8doDto = (List<StageLinkStageOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCseca8doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cseca8dDaoImpl.not.found.prioritystage", null, Locale.US));
		}
		log.debug("Exiting method StageLinkStageQUERYdam in StageLinkStageDaoImpl");
		return liCseca8doDto;
	}
}
