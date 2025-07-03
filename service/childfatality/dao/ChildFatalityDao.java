/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jul 2, 2017- 5:12:49 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.childfatality.dao;

import java.text.ParseException;
import java.util.List;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.childfatality.dto.ChildFatalityDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ChildFatalityDao Performs ChildFatality related retrieve into the
 * data base. Aug 17, 2017- 5:14:41 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public interface ChildFatalityDao {

	/**
	 * This method searches person records with Child Fatality Search Criteria.
	 * The list is iterated and duplicate is removed.
	 *
	 * @param childFatalityDto
	 *            the child fatality dto
	 * @return List<Person> - results of person search records. @ the service
	 * exception
	 * @throws ParseException
	 *             the parse exception
	 */
	public List<Person> searchChild(ChildFatalityDto childFatalityDto) throws ParseException;

	/**
	 * This method searches person records with Child Fatality Search Criteria.
	 * The list is iterated and duplicate is removed.
	 *
	 * @param childFatalityDto
	 *            the child fatality dto
	 * @return List<Person> - results of person search records. @ the service
	 * exception
	 * @throws ParseException
	 *             the parse exception
	 */
	public List<Person> searchChildDOD(ChildFatalityDto childFatalityDto) throws ParseException;

	/**
	 * This method searches person records with Child Fatality Search Criteria -
	 * Only Person ID. The list is iterated and duplicate is removed.
	 *
	 * @param childFatalityDto
	 *            the child fatality dto
	 * @return List<Person> - results of person search records. @ the service
	 * exception
	 * @throws ParseException
	 *             the parse exception
	 */
	public List<Person> searchChildID(ChildFatalityDto childFatalityDto) throws ParseException;
}
