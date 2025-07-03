package us.tx.state.dfps.service.casemanagement.daoimpl;

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

import us.tx.state.dfps.service.casemanagement.dao.FetchNameResourceDao;
import us.tx.state.dfps.service.casepackage.dto.RetrieveNmResourceInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveNmResourceOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 22, 2018- 10:40:22 PM © 2017 Texas Department of
 * Family and Protective Services
 */

// Ccmni0dDaoImpl
@Repository
public class FetchNameResourceDaoImpl implements FetchNameResourceDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FetchNameResourceDaoImpl.strCCMNI0DCURSORQuery}")
	private String strCCMNI0DCURSORQuery;

	private static final Logger log = Logger.getLogger(FetchNameResourceDaoImpl.class);

	/**
	 * Method Name: fetchNameResource Method Description:This Method is used to
	 * fetch the information of NameResource
	 * 
	 * @param retrieveNmResourceInputDto
	 * @param retrieveNmResourceOutputDto
	 * @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void fetchNameResource(RetrieveNmResourceInputDto retrieveNmResourceInputDto,
			RetrieveNmResourceOutputDto retrieveNmResourceOutputDto) {
		log.debug("Entering method fetchNameResource in FetchNameResourceDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCCMNI0DCURSORQuery)
				.addScalar("nmResourceInvstFacility", StandardBasicTypes.STRING)
				.setParameter("idStage", retrieveNmResourceInputDto.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(RetrieveNmResourceOutputDto.class)));

		List<RetrieveNmResourceOutputDto> retrieveNmResourceOutputDtos = new ArrayList<>();
		retrieveNmResourceOutputDtos = (List<RetrieveNmResourceOutputDto>) sQLQuery1.list();

		if (!CollectionUtils.isEmpty(retrieveNmResourceOutputDtos)) {
			retrieveNmResourceOutputDto
					.setNmResourceInvstFacility(retrieveNmResourceOutputDtos.get(0).getNmResourceInvstFacility());
		}
		log.debug("Exiting method fetchNameResource in FetchNameResourceDaoImpl");
	}

}
