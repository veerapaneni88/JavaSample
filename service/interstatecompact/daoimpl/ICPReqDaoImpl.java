package us.tx.state.dfps.service.interstatecompact.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.icpforms.dto.IcpPersonDto;
import us.tx.state.dfps.service.icpforms.dto.IcpRsrcEntityDto;
import us.tx.state.dfps.service.interstatecompact.dao.ICPReqDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:dao class
 * for Interstate Compact Placement Request May 11, 2018- 10:00:05 AM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class ICPReqDaoImpl implements ICPReqDao {

	@Value("${ICPformsDaoImpl.getRequest}")
	private transient String getRequestSql;

	@Value("${ICPformsDaoImpl.getPerson}")
	private transient String getPersonSql;

	@Value("${ICPformsDaoImpl.getEntity}")
	private transient String getEntitySql;

	@Value("${ICPformsDaoImpl.getPersonRace}")
	private transient String getPersonRaceSql;

	@Value("${ICPformsDaoImpl.getResource}")
	private transient String getResourceSql;

	@Value("${ICPformsDaoImpl.getEnclosure}")
	private transient String getEnclosureSql;

	@Value("${ICPformsDaoImpl.getPersonAddressReq}")
	private transient String getPersonAddressReqSql;

	@Value("${ICPformsDaoImpl.getPlacement}")
	private transient String getPlacementSql;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(ICPReqDaoImpl.class);

	/**
	 * Dam Name: CSESG1D Method Name: getRequest Method Description: Retrieves
	 * data based on id
	 * 
	 * @param idReq
	 * @return IcpRsrcEntityDto
	 */
	@Override
	public IcpRsrcEntityDto getRequest(Long idReq) {
		IcpRsrcEntityDto icpRsrcEntityDto = new IcpRsrcEntityDto();
		icpRsrcEntityDto = (IcpRsrcEntityDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getRequestSql).setParameter("idReq", idReq))
						.addScalar("idIcpcSub", StandardBasicTypes.LONG)
						.addScalar("idIcpcPortReq", StandardBasicTypes.LONG)
						.addScalar("idCreatedPers", StandardBasicTypes.LONG)
						.addScalar("cdReqType", StandardBasicTypes.STRING)
						.addScalar("cdRecievingState", StandardBasicTypes.STRING)
						.addScalar("cdSendingState", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(IcpRsrcEntityDto.class)).uniqueResult();
		return icpRsrcEntityDto;
	}

	/**
	 * Dam Name: CLSCH0D Method Name: getPerson Method Description: Retrieves
	 * data based on id
	 * 
	 * @param idEvent
	 * @return IcpPersonDto
	 */
	@Override
	public List<IcpPersonDto> getPerson(Long idEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersonSql)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("cdPersonType", StandardBasicTypes.STRING)
				.addScalar("nbrPersonId", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(IcpPersonDto.class));
		List<IcpPersonDto> list = (List<IcpPersonDto>) query.list();
		return list;
	}

	/**
	 * Dam Name: CLSCH1D Method Name: getEntity Method Description: Retrieves
	 * data based on id
	 * 
	 * @param idEvent
	 * @return IcpRsrcEntityDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IcpRsrcEntityDto> getEntity(Long idEvent) {
		List<IcpRsrcEntityDto> icpRsrcEntityDto = new ArrayList<IcpRsrcEntityDto>();
		icpRsrcEntityDto = (ArrayList<IcpRsrcEntityDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getEntitySql).setParameter("idEvent", idEvent))
						.addScalar("nmEntity", StandardBasicTypes.STRING)
						.addScalar("addrStLn1", StandardBasicTypes.STRING)
						.addScalar("addrStLn2", StandardBasicTypes.STRING)
						.addScalar("addrCity", StandardBasicTypes.STRING)
						.addScalar("cdState", StandardBasicTypes.STRING).addScalar("addrZip", StandardBasicTypes.STRING)
						.addScalar("nbrPhone", StandardBasicTypes.LONG).addScalar("cdType", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(IcpRsrcEntityDto.class)).list();
		return icpRsrcEntityDto;
	}

	/**
	 * Dam Name: CSECF5D Method Name: getPersonRace Method Description:
	 * Retrieves data based on id
	 * 
	 * @param idEvent
	 * @return IcpPersonDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IcpPersonDto getPersonRace(Long idEvent) {
		List<IcpPersonDto> icpPersonDtos = new ArrayList<IcpPersonDto>();
		icpPersonDtos = (ArrayList<IcpPersonDto>) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonRaceSql)
				.setParameter("idEvent", idEvent)).addScalar("nmPersonFull", StandardBasicTypes.STRING)
						.addScalar("cdPersonSex", StandardBasicTypes.STRING)
						.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
						.addScalar("nbrPersonId", StandardBasicTypes.STRING)
						.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
						.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdRace", StandardBasicTypes.STRING)
						.addScalar("cdEthnicity", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(IcpPersonDto.class)).list();
		if(!CollectionUtils.isEmpty(icpPersonDtos)){
			return icpPersonDtos.get(0);
		}
		return new IcpPersonDto();
	}

	/**
	 * Dam Name: CSECF6D Method Name: getResource Method Description: Retrieves
	 * data based on id
	 * 
	 * @param idEvent
	 * @return IcpRsrcEntityDto
	 */
	@Override
	public IcpRsrcEntityDto getResource(Long idEvent) {
		IcpRsrcEntityDto icpRsrcEntityDto = new IcpRsrcEntityDto();
		icpRsrcEntityDto = (IcpRsrcEntityDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getResourceSql).setParameter("idEvent", idEvent))
						.addScalar("nmResource", StandardBasicTypes.STRING)
						.addScalar("addrRsrcStLn1", StandardBasicTypes.STRING)
						.addScalar("addrRsrcStLn2", StandardBasicTypes.STRING)
						.addScalar("addrRsrcCity", StandardBasicTypes.STRING)
						.addScalar("addrRsrcState", StandardBasicTypes.STRING)
						.addScalar("addrRsrcZip", StandardBasicTypes.STRING)
						.addScalar("nbrRsrcPhone", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(IcpRsrcEntityDto.class)).uniqueResult();
		return icpRsrcEntityDto;
	}

	/**
	 * Dam Name: CLSSC6D Method Name: getEnclosure Method Description: Retrieves
	 * data based on id
	 * 
	 * @param idEvent
	 * @return String
	 */
	@Override
	public List<String> getEnclosure(Long idEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getEnclosureSql).setParameter("idEvent",
				idEvent);
		List<String> list = (List<String>) query.list();
		return list;
	}

	/**
	 * Dam Name: CLSCH5D Method Name: getPersonAddressReq Method Description:
	 * Retrieves data based on id
	 * 
	 * @param idEvent
	 * @return IcpPersonDto
	 */
	@Override
	public List<IcpPersonDto> getPersonAddressReq(Long idEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersonAddressReqSql)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("cdPersonType", StandardBasicTypes.STRING)
				.addScalar("addrPersStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersStLn2", StandardBasicTypes.STRING)
				.addScalar("addrPersCity", StandardBasicTypes.STRING)
				.addScalar("addrPersState", StandardBasicTypes.STRING)
				.addScalar("addrPersZip", StandardBasicTypes.STRING)
				.addScalar("nbrPersPhone", StandardBasicTypes.STRING)
				.addScalar("idIcpcRequestPersonLink", StandardBasicTypes.LONG)
				.addScalar("cdPlacementType", StandardBasicTypes.STRING)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(IcpPersonDto.class));
		List<IcpPersonDto> list = (List<IcpPersonDto>) query.list();
		return list;
	}

	/**
	 * Dam Name: CSECF1D Method Name: getPlacement Method Description: Retrieves
	 * data based on id
	 * 
	 * @param idEvent
	 * @return IcpPersonDto
	 */
	@Override
	public IcpPersonDto getPlacement(Long idEvent) {
		IcpPersonDto icpPersonDto = new IcpPersonDto();
		icpPersonDto = (IcpPersonDto) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPlacementSql)
				.setParameter("idEvent", idEvent)).addScalar("indEligible", StandardBasicTypes.STRING)
						.addScalar("indTitle", StandardBasicTypes.STRING)
						.addScalar("cdCareType", StandardBasicTypes.STRING)
						.addScalar("cdReceivingState", StandardBasicTypes.STRING)
						.addScalar("cdSendingState", StandardBasicTypes.STRING)
						.addScalar("cdLegalStatus", StandardBasicTypes.STRING)
						.addScalar("cdInitReport", StandardBasicTypes.STRING)
						.addScalar("cdSprvSrvc", StandardBasicTypes.STRING)
						.addScalar("cdSprvRpt", StandardBasicTypes.STRING)
						.addScalar("cdDecision", StandardBasicTypes.STRING)
						.addScalar("txtPlacementRemarks", StandardBasicTypes.STRING)
						.addScalar("cdViolation", StandardBasicTypes.STRING)
						.addScalar("cdSubsidy", StandardBasicTypes.STRING)
						.addScalar("otrTypeOfCare", StandardBasicTypes.STRING)
						.addScalar("indPublicOrPrivate", StandardBasicTypes.STRING)
						.addScalar("otrLegalStatus", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(IcpPersonDto.class)).uniqueResult();
		return icpPersonDto;
	}

}
