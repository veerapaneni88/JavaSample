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
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.casemanagement.dao.FetchFacilityNameOfStageDao;
import us.tx.state.dfps.service.casepackage.dto.FacilityNameInputDto;
import us.tx.state.dfps.service.casepackage.dto.FacilityNameOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 22, 2018- 10:37:35 PM © 2017 Texas Department of
 * Family and Protective Services
 */

// Ccmna3dDaoImpl
@Repository
public class FetchFacilityNameOfStageDaoImpl implements FetchFacilityNameOfStageDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FetchFacilityNameOfStageDaoImpl.strCCMNA3DCURSORQuery}")
	private String strCCMNA3DCURSORQuery;

	private static final Logger log = Logger.getLogger(FetchFacilityNameOfStageDaoImpl.class);

	/**
	 * Method Name: retrieveFacilityName Method Description:This Method is used
	 * to retireve the information of all
	 * 
	 * @param facilityNameInputDto
	 * @param facilityNameOutputDto
	 * @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void retrieveFacilityName(FacilityNameInputDto facilityNameInputDto,
			FacilityNameOutputDto facilityNameOutputDto) {
		log.debug("Entering method retrieveFacilityName in FetchFacilityNameOfStageDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCCMNA3DCURSORQuery)
				.addScalar("nmFacilInvstFacility", StandardBasicTypes.STRING)
				.setParameter("idStage", facilityNameInputDto.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(FacilityNameOutputDto.class)));

		List<FacilityNameOutputDto> facilityNameOutputDtos = new ArrayList<>();
		facilityNameOutputDtos = (List<FacilityNameOutputDto>) sQLQuery1.list();
		if (!CollectionUtils.isEmpty(facilityNameOutputDtos)) {
			facilityNameOutputDto.setNmFacilInvstFacility(facilityNameOutputDtos.get(0).getNmFacilInvstFacility());
		}

		log.debug("Exiting method retrieveFacilityName in FetchFacilityNameOfStageDaoImpl");
	}

}
