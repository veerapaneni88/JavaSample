package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Name;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.admin.dao.NamePersonDao;
import us.tx.state.dfps.service.admin.dto.NamePersonInDto;
import us.tx.state.dfps.service.admin.dto.NamePersonOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * fetches the Name Details of a Person Using PersonID Aug 7, 2017- 4:42:33 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class NamePersonDaoImpl implements NamePersonDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${NamePersonDaoImpl.PrsnDtls}")
	private String prsnDtls;

	private static final Logger log = Logger.getLogger("ServiceBusiness-NamePersonDao");

	/**
	 * 
	 * Method Name: PrsnDtls Method Description:This method retrieves data from
	 * NAME table.
	 * 
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 * @return List<NamePersonOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NamePersonOutDto> prsnDtls(NamePersonInDto pInputDataRec, NamePersonOutDto pOutputDataRec) {
		List<NamePersonOutDto> namePersonOutDtoList = new ArrayList<>();
		List<Name> nameList = (List<Name>) sessionFactory.getCurrentSession().createCriteria(Name.class)
				.add(Restrictions.eq("person",
						(Person) sessionFactory.getCurrentSession().get(Person.class, pInputDataRec.getIdPerson())))
				.add(Restrictions.eq("indNamePrimary", ServiceConstants.Y))
				.add(Restrictions.eqOrIsNull("dtNameEndDate", ServiceConstants.GENERIC_END_DATE)).list();
		if (!ObjectUtils.isEmpty(nameList)) {
			nameList.stream().forEach(name -> {
				NamePersonOutDto personOut = new NamePersonOutDto();
				personOut.setIdName(name.getIdName());
				personOut.setTsLastUpdate(DateUtils.dateString(name.getDtLastUpdate()));
				personOut.setIdPerson(name.getPerson().getIdPerson());
				personOut.setIndNameInvalid(name.getIndNameInvalid());
				personOut.setNmNameFirst(name.getNmNameFirst());
				personOut.setNmNameMiddle(name.getNmNameMiddle());
				personOut.setNmNameLast(name.getNmNameLast());
				personOut.setIndNamePrimary(name.getIndNamePrimary());
				personOut.setCdNameSuffix(name.getCdNameSuffix());
				personOut.setDtNameStartDate(name.getDtNameStartDate());
				personOut.setDtNameEndDate(name.getDtNameEndDate());
				namePersonOutDtoList.add(personOut);
			});
		}
		return namePersonOutDtoList;
	}
}
