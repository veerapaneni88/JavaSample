package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceFetchStageDtlDao;
import us.tx.state.dfps.service.casepackage.dto.RtrvStageInDto;
import us.tx.state.dfps.service.casepackage.dto.RtrvStageOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceFetchStageDtlDaoImpl Feb 7, 2018- 5:50:45 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class CaseMaintenanceFetchStageDtlDaoImpl implements CaseMaintenanceFetchStageDtlDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMaintenanceFetchPersonDaoImpl.strCSES71D_CURSORQuery}")
	private transient String strCSES71D_CURSORQuery;

	private static final Logger log = Logger.getLogger(CaseMaintenanceFetchStageDtlDaoImpl.class);

	/**
	 * Method Name: fetchStageDtl Method Description:This Method is used to
	 * fetch stage dtl DAM: CSES71D
	 *
	 * @param rtrvStageInDto
	 * @param rtrvStageOutDto
	 * @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void fetchStageDtl(RtrvStageInDto rtrvStageInDto, RtrvStageOutDto rtrvStageOutDto) {
		log.debug("Entering method fetchStageDtl in CaseMaintenanceFetchStageDtlDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCSES71D_CURSORQuery)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStageType", StandardBasicTypes.STRING).addScalar("idUnit", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idSituation", StandardBasicTypes.LONG)
				.addScalar("dtStageClose", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStageClassification", StandardBasicTypes.STRING)
				.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageInitialPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING)
				.addScalar("indStageClose", StandardBasicTypes.STRING)
				.addScalar("cdStageCnty", StandardBasicTypes.STRING).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("cdStageRegion", StandardBasicTypes.STRING)
				.addScalar("dtStageStart", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("txtStageClosureCmnts", StandardBasicTypes.STRING)
				.addScalar("txtStagePriorityCmnts", StandardBasicTypes.STRING)
				.setParameter("idStage", rtrvStageInDto.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(RtrvStageOutDto.class)));

		List<RtrvStageOutDto> rtrvStageOutDtos = new ArrayList<>();
		rtrvStageOutDtos = (List<RtrvStageOutDto>) sQLQuery1.list();

		if (!CollectionUtils.isEmpty(rtrvStageOutDtos)) {
			BeanUtils.copyProperties(rtrvStageOutDtos.get(0), rtrvStageOutDto);
		}
		log.debug("Exiting method fetchStageDtl in CaseMaintenanceFetchStageDtlDaoImpl");
	}

}
