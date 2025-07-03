/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 29, 2018- 10:32:01 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.prt.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.prt.dto.PRTConnectionDto;
import us.tx.state.dfps.service.prt.dto.PRTPermGoalDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 29, 2018- 10:32:01 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class PRTActionPlanUtils {

	@Autowired
	private LookupDao lookupDao;

	/**
	 * Method Name: isPersonInConnList Method Description:This method checks if
	 * the given Person Id is in the connection list.
	 * 
	 * @param prtConnections
	 * @param idPerson
	 * @return Boolean
	 */
	public Boolean isPersonInConnList(List<PRTConnectionDto> prtConnections, Long idPerson) {
		boolean existConn = ServiceConstants.MOBILE_IMPACT;

		for (PRTConnectionDto prtConnectionValueDto : prtConnections) {
			if (prtConnectionValueDto.getIdPerson().equals(idPerson)) {
				existConn = ServiceConstants.SERVER_IMPACT;
				break;
			}
		}

		return existConn;
	}

	/**
	 * Method Name: validateChildPlanGoals Method Description:This method runs
	 * the validations for Child Plan Goals.
	 * 
	 * @param child
	 * @return boolean
	 */
	public boolean validateChildPlanGoals(PRTPersonLinkDto child) {

		boolean isValid = ServiceConstants.SERVER_IMPACT;
		String cpPrimaryGoal = getPrimaryGoalForChild(child);
		String cpConcurrentGoals = getConcurrentGoalsForChild(child);

		if (!StringUtil.isValid(cpPrimaryGoal)
				|| ((!StringUtil.isValid(cpConcurrentGoals) && !ServiceConstants.Y.equals(child.getIndNoConGoal()))
						&& !ServiceConstants.CCPPLNTP_ADP.equals(child.getChildPlanType()))) {
			isValid = ServiceConstants.MOBILE_IMPACT;
		}

		return isValid;
	}

	/**
	 * Method Name: getConcurrentGoalsForChild Method Description:This method
	 * returns Concurrent Child Plan Goal of the Child.
	 * 
	 * @param child
	 * @return String
	 */
	private String getConcurrentGoalsForChild(PRTPersonLinkDto child) {
		StringBuilder concurrentGoals = new StringBuilder();

		for (PRTPermGoalDto goal : child.getPrtPermGoalValueDtoList()) {
			if (ServiceConstants.CPRMGLTY_20.equals(goal.getCdType())) {
				String goalDecode = lookupDao.simpleDecodeSafe(ServiceConstants.CCPPRMGL, goal.getCdGoal());
				concurrentGoals.append(goalDecode).append(",");
			}
		}

		if (concurrentGoals.length() > 0) {
			concurrentGoals.setLength(concurrentGoals.length() - 1);
		}

		return concurrentGoals.toString();

	}

	/**
	 * Method Name: getPrimaryGoalForChild Method Description:This method
	 * returns Primary Child Plan Goal of the Child.
	 * 
	 * @param child
	 * @return String
	 */
	private String getPrimaryGoalForChild(PRTPersonLinkDto child) {

		String primaryGoal = getPrimaryGoalForChildCode(child);

		return lookupDao.simpleDecodeSafe(ServiceConstants.CCPPRMGL, primaryGoal);

	}

	/**
	 * Method Name: getPrimaryGoalForChildCode Method Description:This method
	 * returns Primary Child Plan Goal Code of the Child.
	 * 
	 * @param child
	 * @return String
	 */
	private String getPrimaryGoalForChildCode(PRTPersonLinkDto child) {
		String primaryGoal = "";

		List<PRTPermGoalDto> prtPermGoalValueDtos = child.getPrtPermGoalValueDtoList();
		for (PRTPermGoalDto goal : prtPermGoalValueDtos) {
			if (ServiceConstants.CPRMGLTY_10.equals(goal.getCdType())) {
				primaryGoal = goal.getCdGoal();
				break;
			}
		}

		return primaryGoal;

	}
}
