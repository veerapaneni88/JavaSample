package us.tx.state.dfps.service.fce.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.domiciledeprivation.dto.PrinciplesDto;
import us.tx.state.dfps.service.fce.dao.DomicileDeprivationDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: his class
 * implements DomicileDeprivationDao methods Mar 15, 2018- 12:15:29 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class DomicileDeprivationDaoImpl implements DomicileDeprivationDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${DomicileDeprivationDaoImpl.findPrinciples}")
	private String findPrinciplesSql;

	/**
	 * Method Name: findPrinciples Method Description:This method fetches the
	 * PrinciplesList from Database based on idFceEligibility
	 * 
	 * @param idFceEligibility
	 * @return List<PrinciplesListDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PrinciplesDto> findPrinciples(Long idFceEligibility) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(findPrinciplesSql)
				.addScalar("idFcePerson", StandardBasicTypes.LONG)
				.addScalar("idFceEligibility", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("cdRelInt", StandardBasicTypes.STRING)
				.addScalar("indCertifiedGroup", StandardBasicTypes.STRING)
				.addScalar("indPersonHmRemoval", StandardBasicTypes.STRING)
				.addScalar("indDobApprox", StandardBasicTypes.STRING).addScalar("dtBirth", StandardBasicTypes.DATE)
				.addScalar("nbrAge", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("addrPersonStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersonCity", StandardBasicTypes.STRING)
				.addScalar("addrPersonZip", StandardBasicTypes.STRING)
				.addScalar("cdPersonState", StandardBasicTypes.STRING)
				.addScalar("cdPersonCounty", StandardBasicTypes.STRING)
				.setParameter("idFceEligibility", idFceEligibility)
				.setResultTransformer(Transformers.aliasToBean(PrinciplesDto.class));

		List<PrinciplesDto> principlesListDtos = sqlQuery.list();
		for (PrinciplesDto principlesListDto : principlesListDtos) {
			synchronizeBirthDays(principlesListDto);
		}
		return principlesListDtos;
	}

	/**
	 * Method Name: synchronizeBirthDays Method Description:This method
	 * synchronizes the birthdays present PrinciplesList
	 * 
	 * @param principlesListDto
	 * @return @
	 */
	private void synchronizeBirthDays(PrinciplesDto principlesListDto) {
		principlesListDto.setDtPersonBirth(principlesListDto.getDtBirth());
		principlesListDto.setIndPersonDobApprox(principlesListDto.getIndDobApprox());
	}

}