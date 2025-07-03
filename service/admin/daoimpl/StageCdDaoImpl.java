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

import us.tx.state.dfps.service.admin.dao.StageCdDao;
import us.tx.state.dfps.service.admin.dto.StageCdInDto;
import us.tx.state.dfps.service.admin.dto.StageCdOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO Impl
 * for fetching stage details> Aug 4, 2017- 3:18:59 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class StageCdDaoImpl implements StageCdDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(StageCdDaoImpl.class);

	@Value("${StageCdDaoImpl.getStageDtlsByDate}")
	private transient String getStageDtlsByDate;

	public StageCdDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getStageDtlsByDate Method Description: This method will get
	 * Stage details by Date.
	 * 
	 * @param pInputDataRec
	 * @return List<StageCdOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StageCdOutDto> getStageDtlsByDate(StageCdInDto pInputDataRec) {
		log.debug("Entering method StageCdQUERYdam in StageCdDaoImpl");
		String maxDate = ServiceConstants.MAX_DATE_STRING;
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getStageDtlsByDate)
				.setResultTransformer(Transformers.aliasToBean(StageCdOutDto.class)));
		sQLQuery1.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdStage", StandardBasicTypes.STRING)
				.setParameter("maxDate", maxDate).setParameter("hI_ulIdCase", pInputDataRec.getIdCase());
		List<StageCdOutDto> liCcmnf6doDto = new ArrayList<>();
		liCcmnf6doDto = (List<StageCdOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCcmnf6doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Ccmnf6dDaoImpl.not.found.stage", null, Locale.US));
		}
		log.debug("Exiting method StageCdQUERYdam in StageCdDaoImpl");
		return liCcmnf6doDto;
	}
}
