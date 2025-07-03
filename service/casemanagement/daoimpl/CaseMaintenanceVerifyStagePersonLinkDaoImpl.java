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
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceVerifyStagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.StagePersonLinkRtrvInDto;
import us.tx.state.dfps.service.casepackage.dto.StagePersonLinkRtrvOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceVerifyStagePersonLinkDaoImpl Feb 7, 2018- 5:52:45
 * PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class CaseMaintenanceVerifyStagePersonLinkDaoImpl implements CaseMaintenanceVerifyStagePersonLinkDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMaintenanceVerifyStagePersonLinkDaoImpl.strCLSCF1D_CURSORQuery}")
	private transient String strCLSCF1D_CURSORQuery;

	private static final Logger log = Logger.getLogger(CaseMaintenanceVerifyStagePersonLinkDaoImpl.class);

	/**
	 * Method Name: verifyStagePersonLink Method Description:This Method is used
	 * to verify the StagePersonLink DAM: Clscf1d
	 * 
	 * @param stagePersonLinkRtrvInDto
	 * @param stagePersonLinkRtrvOutDto
	 * @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void verifyStagePersonLink(StagePersonLinkRtrvInDto stagePersonLinkRtrvInDto,
			StagePersonLinkRtrvOutDto stagePersonLinkRtrvOutDto) {
		log.debug("Entering method verifyStagePersonLink in CaseMaintenanceVerifyStagePersonLinkDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCLSCF1D_CURSORQuery)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.setParameter("idStage", stagePersonLinkRtrvInDto.getIdStage())
				.setParameter("idPerson", stagePersonLinkRtrvInDto.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(StagePersonLinkRtrvOutDto.class)));

		List<StagePersonLinkRtrvOutDto> stagePersonLinkRtrvOutDtos = new ArrayList<>();
		stagePersonLinkRtrvOutDtos = (List<StagePersonLinkRtrvOutDto>) sQLQuery1.list();

		if (!CollectionUtils.isEmpty(stagePersonLinkRtrvOutDtos)) {
			BeanUtils.copyProperties(stagePersonLinkRtrvOutDtos.get(0), stagePersonLinkRtrvOutDto);
		}
		log.debug("Exiting method verifyStagePersonLink in CaseMaintenanceVerifyStagePersonLinkDaoImpl");
	}

}
