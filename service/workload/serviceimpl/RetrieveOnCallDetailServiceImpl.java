package us.tx.state.dfps.service.workload.serviceimpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.tx.state.dfps.service.admin.dao.EmpOnCallLinkPersonPhoneDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.workload.dto.EmpOnCallLinkPersonPhoneInDto;
import us.tx.state.dfps.service.workload.dto.EmpOnCallLinkPersonPhoneOutDto;
import us.tx.state.dfps.service.workload.dto.RetrieveOnCallDetailDto;
import us.tx.state.dfps.service.workload.service.RetrieveOnCallDetailService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<The purpose
 * of this class is to retrieve details from EMP_ON_CALL_LINK table using
 * ulIdOnCall> Aug 2, 2017- 8:41:47 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Service
@Transactional
public class RetrieveOnCallDetailServiceImpl implements RetrieveOnCallDetailService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	EmpOnCallLinkPersonPhoneDao empOnCallLinkPersonPhoneDao;

	private static final Logger log = Logger.getLogger(RetrieveOnCallDetailServiceImpl.class);

	/**
	 * 
	 * Method Name: callRetrieveOnCallDetailService Method Description: This
	 * service invokes callRetrieveOnCallDetailService method and retrieves
	 * OnCall details.
	 * 
	 * @param retrieveOnCallDetailiDto
	 * @return List<RetrieveOnCallDetailoDto>
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public List<RetrieveOnCallDetailDto> callRetrieveOnCallDetailService(
			RetrieveOnCallDetailDto retrieveOnCallDetailDto) {
		log.debug("Entering method callRetrieveOnCallDetailService in RetrieveOnCallDetailServiceImpl");
		DateFormat dateFormat = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_WITH_TIME);
		Date date = new Date();
		String dtWCDDtSystemDate = dateFormat.format(date);
		EmpOnCallLinkPersonPhoneInDto empOnCallLinkPersonPhoneInDto = new EmpOnCallLinkPersonPhoneInDto();
		List<RetrieveOnCallDetailDto> employeeOnCallList = null;
		empOnCallLinkPersonPhoneInDto.setIdOnCall(retrieveOnCallDetailDto.getIdOnCall());
		// Invoking data access object method to get the employee list from the
		// EMP_ON_CALL_LINK table
		List<EmpOnCallLinkPersonPhoneOutDto> employeeList = empOnCallLinkPersonPhoneDao
				.getEmployeeOnCallList(empOnCallLinkPersonPhoneInDto);
		if (!TypeConvUtil.isNullOrEmpty(employeeList)) {
			employeeOnCallList = new ArrayList<RetrieveOnCallDetailDto>();
			// Iterate over the output from the dao layer and populate the dto
			// in the
			// response
			employeeOnCallList = processCallDetailDto(dtWCDDtSystemDate, employeeOnCallList, employeeList);
		}
		log.debug("Exiting method callRetrieveOnCallDetailService in RetrieveOnCallDetailServiceImpl");
		return employeeOnCallList;
	}

	/**
	 * Method Name: processCallDetailDto Method Description:This method sets the
	 * RetrieveOnCall details into list of service dto RetrieveOnCallDetailoDto.
	 * 
	 * @param dtWCDDtSystemDate
	 * @param liRetrieveOnCallDetailoDto
	 * @param employeeList
	 * @return liRetrieveOnCallDetailoDto
	 */
	private List<RetrieveOnCallDetailDto> processCallDetailDto(String dtWCDDtSystemDate,
			List<RetrieveOnCallDetailDto> liRetrieveOnCallDetailoDto,
			List<EmpOnCallLinkPersonPhoneOutDto> employeeList) {
		ObjectMapper mapper = new ObjectMapper();
		Long contactOrder = null;
		// Iterating over the employee list fetched and setting the values in
		// the dto to
		// be returned to the web layer
		for (EmpOnCallLinkPersonPhoneOutDto employee : employeeList) {
			RetrieveOnCallDetailDto callDetailoDto = new RetrieveOnCallDetailDto();
			if (employee.getEmpOnCallCntctOrd() != contactOrder) {
				BeanUtils.copyProperties(employee, callDetailoDto);
				callDetailoDto.setDtWCDDtSystem(dtWCDDtSystemDate);
				callDetailoDto.setPhone(employee.getPhoneNumber());
				liRetrieveOnCallDetailoDto.add(callDetailoDto);
				contactOrder = employee.getEmpOnCallCntctOrd();
				// If the last update date is not empty , converting the date
				// into a json string
				// to store in the front end.
				if (!ObjectUtils.isEmpty(employee.getDtLastUpdate())) {
					try {
						callDetailoDto.setDtLastUpdateStr(mapper.writeValueAsString(employee.getDtLastUpdate()));
					} catch (JsonProcessingException e) {
						log.info("Error occured while parsing the date object to json string");
					}
				}
			}
		}
		return liRetrieveOnCallDetailoDto;
	}
}
