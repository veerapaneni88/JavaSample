package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.StagePersonUpdByRoleDao;
import us.tx.state.dfps.service.admin.dto.StagePersonUpdByRoleInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonUpdByRoleOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV16S Aug
 * 9, 2017- 4:42:50 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class StagePersonUpdByRoleDaoImpl implements StagePersonUpdByRoleDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StagePersonUpdByRoleDaoImpl.updateStagePersonDetails}")
	private String updateStagePersonDetails;

	private static final Logger log = Logger.getLogger(StagePersonUpdByRoleDaoImpl.class);

	public StagePersonUpdByRoleDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: updateStagePersonDetails Method Description: This method
	 * will update StagePersonLink table.
	 * 
	 * @param pInputDataRec
	 * @return StagePersonUpdByRoleOutDto @
	 */
	@Override
	public StagePersonUpdByRoleOutDto updateStagePersonDetails(StagePersonUpdByRoleInDto pInputDataRec) {
		log.debug("Entering method StagePersonUpdByRoleQUERYdam in StagePersonUpdByRoleDaoImpl");
		StagePersonUpdByRoleOutDto Caude6diDto = new StagePersonUpdByRoleOutDto();
		Query queryIncomeDtls = sessionFactory.getCurrentSession().createSQLQuery(updateStagePersonDetails);
		queryIncomeDtls.setParameter("hI_szCdStagePersRole2", pInputDataRec.getCdStagePersRole2());
		queryIncomeDtls.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		queryIncomeDtls.setParameter("hI_szCdStagePersType", pInputDataRec.getCdStagePersType());
		queryIncomeDtls.setParameter("hI_szCdStagePersRole", pInputDataRec.getCdStagePersRole());
		int rowCount = queryIncomeDtls.executeUpdate();
		if (TypeConvUtil.isNullOrEmpty(rowCount)) {
			throw new DataNotFoundException(
					messageSource.getMessage("stagepersonlink.not.found.attributes", null, Locale.US));
		}
		log.debug("Exiting method StagePersonUpdByRoleQUERYdam in StagePersonUpdByRoleDaoImpl");
		return Caude6diDto;
	}
}
