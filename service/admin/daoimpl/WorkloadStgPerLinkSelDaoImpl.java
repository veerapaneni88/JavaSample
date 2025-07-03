package us.tx.state.dfps.service.admin.daoimpl;

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

import us.tx.state.dfps.service.admin.dao.WorkloadStgPerLinkSelDao;
import us.tx.state.dfps.service.admin.dto.WorkloadStgPerLinkSelInDto;
import us.tx.state.dfps.service.admin.dto.WorkloadStgPerLinkSelOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:Cinv51dDaoImpl Aug 9, 2017- 1:21:28 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class WorkloadStgPerLinkSelDaoImpl implements WorkloadStgPerLinkSelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${WorkloadStgPerLinkSelDaoImpl.getWorkloadOnRole}")
	private String getWorkloadOnRole;

	@Value("${WorkloadStgPerLinkSelDaoImpl.getWorkload}")
	private String getWorkload;

	private static final Logger log = Logger.getLogger(WorkloadStgPerLinkSelDaoImpl.class);

	public WorkloadStgPerLinkSelDaoImpl() {
		super();
	}

	/**
	 * Cinv51d
	 * 
	 * @param workloadStgPerLinkSelInDto
	 * @return List<WorkloadStgPerLinkSelOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<WorkloadStgPerLinkSelOutDto> getWorkLoad(WorkloadStgPerLinkSelInDto workloadStgPerLinkSelInDto) {
		log.debug("Entering method WorkloadStgPerLinkSelQUERYdam in WorkloadStgPerLinkSelDaoImpl");
		List<WorkloadStgPerLinkSelOutDto> workloadStgPerLinkSelOutDtos;
		if (workloadStgPerLinkSelInDto.getCdStagePersRole().equalsIgnoreCase(ServiceConstants.PRIMARY_ROLE)
				|| (workloadStgPerLinkSelInDto.getCdStagePersRole()
						.equalsIgnoreCase(ServiceConstants.SECONDARY_ROLE))) {
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getWorkloadOnRole)
					.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
					.setParameter("hI_ulIdStage", workloadStgPerLinkSelInDto.getIdStage())
					.setParameter("hI_szCdStagePersRole", workloadStgPerLinkSelInDto.getCdStagePersRole())
					.setResultTransformer(Transformers.aliasToBean(WorkloadStgPerLinkSelOutDto.class)));
			workloadStgPerLinkSelOutDtos = (List<WorkloadStgPerLinkSelOutDto>) sQLQuery1.list();
		} else {
			SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getWorkload)
					.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
					.setParameter("hI_ulIdStage", workloadStgPerLinkSelInDto.getIdStage())
					.setParameter("hI_szCdStagePersRole", workloadStgPerLinkSelInDto.getCdStagePersRole())
					.setResultTransformer(Transformers.aliasToBean(WorkloadStgPerLinkSelOutDto.class)));
			workloadStgPerLinkSelOutDtos = (List<WorkloadStgPerLinkSelOutDto>) sQLQuery2.list();
		}
		if (TypeConvUtil.isNullOrEmpty(workloadStgPerLinkSelOutDtos)) {
			throw new DataNotFoundException(messageSource.getMessage("person.not.found.role", null, Locale.US));
		}
		log.debug("Exiting method WorkloadStgPerLinkSelQUERYdam in WorkloadStgPerLinkSelDaoImpl");
		return workloadStgPerLinkSelOutDtos;
	}
}
