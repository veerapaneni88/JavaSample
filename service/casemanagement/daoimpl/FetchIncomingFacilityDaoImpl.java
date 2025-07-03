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

import us.tx.state.dfps.service.casemanagement.dao.FetchIncomingFacilityDao;
import us.tx.state.dfps.service.casepackage.dto.RetreiveIncomingFacilityInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetreiveIncomingFacilityOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 22, 2018- 10:40:53 PM © 2017 Texas Department of
 * Family and Protective Services
 */

// Cint09dDaoImpl
@Repository
public class FetchIncomingFacilityDaoImpl implements FetchIncomingFacilityDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FetchIncomingFacilityDaoImpl.strCINT09DCURSORQuery}")
	private String strCINT09DCURSORQuery;

	private static final Logger log = Logger.getLogger(FetchIncomingFacilityDaoImpl.class);

	/**
	 * Method Name: Method Description:This Method is used to fetch the
	 * information of IncomingFacility
	 * 
	 * @param retreiveIncomingFacilityInputDto
	 * @param retreiveIncomingFacilityOutputDto
	 * @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void fetchIncomingFacility(RetreiveIncomingFacilityInputDto retreiveIncomingFacilityInputDto,
			RetreiveIncomingFacilityOutputDto retreiveIncomingFacilityOutputDto) {
		log.debug("Entering method fetchIncomingFacility in Cint09dDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCINT09DCURSORQuery)
				.addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("indIncmgOnGrnds", StandardBasicTypes.STRING)
				.addScalar("nmIncmgFacilName", StandardBasicTypes.STRING)
				.addScalar("nmIncmgFacilSuprtdant", StandardBasicTypes.STRING)
				.addScalar("cdIncFacilOperBy", StandardBasicTypes.STRING)
				.addScalar("nmIncmgFacilAffiliated", StandardBasicTypes.STRING)
				.addScalar("indIncmgFacilSearch", StandardBasicTypes.STRING)
				.addScalar("indIncmgFacilAbSupvd", StandardBasicTypes.STRING)
				.addScalar("cdIncmgFacilType", StandardBasicTypes.STRING)
				.addScalar("addrIncmgFacilStLn1", StandardBasicTypes.STRING)
				.addScalar("addrIncmgFacilStLn2", StandardBasicTypes.STRING)
				.addScalar("cdIncmgFacilState", StandardBasicTypes.STRING)
				.addScalar("cdIncmgFacilCnty", StandardBasicTypes.STRING)
				.addScalar("addrIncmgFacilCity", StandardBasicTypes.STRING)
				.addScalar("addrIncmgFacilZip", StandardBasicTypes.STRING)
				.addScalar("nbrIncmgFacilPhone", StandardBasicTypes.STRING)
				.addScalar("nbrIncmgFacilPhoneExt", StandardBasicTypes.STRING)
				.addScalar("nmUnitWard", StandardBasicTypes.STRING)
				.addScalar("txtFacilCmnts", StandardBasicTypes.STRING)
				.addScalar("tsLastUpdate", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.setParameter("idStage", retreiveIncomingFacilityInputDto.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(RetreiveIncomingFacilityOutputDto.class)));

		List<RetreiveIncomingFacilityOutputDto> retreiveIncomingFacilityOutputDtos = new ArrayList<>();
		retreiveIncomingFacilityOutputDtos = (List<RetreiveIncomingFacilityOutputDto>) sQLQuery1.list();
		if (!CollectionUtils.isEmpty(retreiveIncomingFacilityOutputDtos)) {
			BeanUtils.copyProperties(retreiveIncomingFacilityOutputDtos.get(0), retreiveIncomingFacilityOutputDto);// retreiveIncomingFacilityOutputDto
																													// =
																													// retreiveIncomingFacilityOutputDtos.get(0);
		}
		log.debug("Exiting method fetchIncomingFacility in FetchIncomingFacilityDaoImpl");
	}

}
