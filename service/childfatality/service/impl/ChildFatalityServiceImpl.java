package us.tx.state.dfps.service.childfatality.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.childfatality.dao.ChildFatalityDao;
import us.tx.state.dfps.service.childfatality.dto.ChildFatalityDto;
import us.tx.state.dfps.service.childfatality.service.ChildFatalityService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ChildFatalityReq;
import us.tx.state.dfps.service.common.response.ChildFatalityRes;
import us.tx.state.dfps.service.exception.ServiceLayerException;

@Service
@Transactional
public class ChildFatalityServiceImpl implements ChildFatalityService {

	@Autowired
	ChildFatalityDao childFatalityDao;

	private static final Logger log = Logger.getLogger(ChildFatalityServiceImpl.class);

	/**
	 * This method searches person records with all values.
	 *
	 * @param childFatalityReq
	 *            the child fatality req
	 * @return the child fatality res
	 * @throws ParseException
	 */
	public ChildFatalityRes searchChild(ChildFatalityReq childFatalityReq) {
		ChildFatalityRes childFatalityRes = new ChildFatalityRes();
		ChildFatalityDto childFatalityDto = childFatalityReq.getChildFatalityDto();
		List<Long> searchResults = null;
		if (StringUtils.isNotBlank(childFatalityDto.getProgram())
				|| StringUtils.isNotBlank(childFatalityDto.getRsrcRegion())
				|| StringUtils.isNotBlank(childFatalityDto.getAddrCounty())
				|| StringUtils.isNotBlank(childFatalityDto.getGender()) || childFatalityDto.getMinDob() != null
				|| childFatalityDto.getMaxDob() != null) {
			log.info("First Condition ------");
			SimpleDateFormat format = new SimpleDateFormat(ServiceConstants.DATE_FORMAT);
			if (childFatalityDto.getMinDob() != null) {
				childFatalityDto.setMinStrDob(format.format(childFatalityDto.getMinDob()));
			}
			if (childFatalityDto.getMaxDob() != null) {
				childFatalityDto.setMaxStrDob(format.format(childFatalityDto.getMaxDob()));
			}
			List<Person> personList;
			try {
				personList = childFatalityDao.searchChild(childFatalityDto);
			} catch (ParseException e) {
				ServiceLayerException serviceLayerException = new ServiceLayerException(e.getMessage());
				serviceLayerException.initCause(e);
				throw serviceLayerException;
			}
			if (personList != null && personList.size() > 0) {
				searchResults = new ArrayList();
				for (Person person : personList) {
					searchResults.add(person.getIdPerson());
				}
				childFatalityRes.setPersonIds(searchResults);
				childFatalityRes.setRecordsAvailable(true);
			}
		} else // To
		if (childFatalityDto.getDtFrom() != null && childFatalityDto.getDtTo() != null) {
			log.info("Second Condition ------ Date of Death");
			List<Person> personList;
			try {
				personList = childFatalityDao.searchChildDOD(childFatalityDto);
			} catch (ParseException e) {
				ServiceLayerException serviceLayerException = new ServiceLayerException(e.getMessage());
				serviceLayerException.initCause(e);
				throw serviceLayerException;
			}
			if (personList != null && personList.size() > 0) {
				searchResults = new ArrayList();
				for (Person person : personList) {
					searchResults.add(person.getIdPerson());
				}
				childFatalityRes.setPersonIds(searchResults);
				childFatalityRes.setRecordsAvailable(true);
			}
		} else // If Person ID is entered then search query based on Person ID
		if (childFatalityDto.getIdPerson() != 0) {
			log.info("Third Condition ------ person id");
			List<Person> personList;
			try {
				personList = childFatalityDao.searchChildID(childFatalityDto);
			} catch (ParseException e) {
				ServiceLayerException serviceLayerException = new ServiceLayerException(e.getMessage());
				serviceLayerException.initCause(e);
				throw serviceLayerException;
			}
			if (personList != null && personList.size() > 0) {
				searchResults = new ArrayList();
				for (Person person : personList) {
					searchResults.add(person.getIdPerson());
				}
				childFatalityRes.setPersonIds(searchResults);
				childFatalityRes.setRecordsAvailable(true);
			}
		} else // If Case ID is entered then search query based on Case ID
		if (childFatalityDto.getIdCase() != 0) {
			log.info("Fourth Condition ------ case id");
			List<Person> personList;
			try {
				personList = childFatalityDao.searchChildID(childFatalityDto);
			} catch (ParseException e) {
				ServiceLayerException serviceLayerException = new ServiceLayerException(e.getMessage());
				serviceLayerException.initCause(e);
				throw serviceLayerException;
			}
			if (personList != null && personList.size() > 0) {
				searchResults = new ArrayList();
				for (Person person : personList) {
					searchResults.add(person.getIdPerson());
				}
				childFatalityRes.setPersonIds(searchResults);
				childFatalityRes.setRecordsAvailable(true);
			}
		}
		return childFatalityRes;
	}
}
