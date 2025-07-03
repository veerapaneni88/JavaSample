package us.tx.state.dfps.service.person.serviceimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.dto.EmailDetailDto;
import us.tx.state.dfps.common.web.bean.EmailDetailBean;
import us.tx.state.dfps.service.common.request.EmailDetailReq;
import us.tx.state.dfps.service.common.request.EmailReq;
import us.tx.state.dfps.service.common.request.PersonEmailReq;
import us.tx.state.dfps.service.common.response.EmailRes;
import us.tx.state.dfps.service.common.response.PersonEmailRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.person.dao.PersonEmailDao;
import us.tx.state.dfps.service.person.service.PersonEmailService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jun 17, 2017- 8:09:40 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class PersonEmailServiceImpl implements PersonEmailService {

	@Autowired
	PersonEmailDao personEmailDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.person.service.PersonEmailService#
	 * getPersonEmailAddress(us.tx.state.dfps.service.common.request.EmailReq)
	 */
	@Override
	public EmailRes getPersonEmailAddress(EmailReq emailReq) {
		EmailRes emailResponse = new EmailRes();
		String emailAddress = personEmailDao.getPersonEmailAddress(emailReq);
		emailResponse.setEmailAddress(emailAddress);
		return emailResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.person.service.PersonEmailService#fetchEmailList
	 * (us.tx.state.dfps.service.common.request.EmailReq)
	 */
	@Override
	public EmailRes fetchEmailList(EmailReq request) {
		EmailRes emailResponse = new EmailRes();
		List<EmailDetailBean> emailList = personEmailDao.fetchEmailList(request);
		emailResponse.setEmailList(emailList);
		return emailResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.person.service.PersonEmailService#
	 * updatePersonEmail(us.tx.state.dfps.service.common.request.EmailDetailReq)
	 */
	@Override
	public void updatePersonEmail(EmailDetailReq request) {

		personEmailDao.updatePersonEmail(request);
	}

	/**
	 * Method Name: updateEmail Method Description: this method will update
	 * email address
	 * 
	 * @param request
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public void updateEmail(EmailDetailReq request){
		personEmailDao.updateEmail(request);
	}

	/**
	 * Method Name: getEmailList Method Description: this method will return
	 * list of email address for a giving person id
	 * 
	 * @param request
	 * @return PersonEmailRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PersonEmailRes getEmailList(PersonEmailReq request){
		PersonEmailRes emailResponse = new PersonEmailRes();
		List<EmailDetailDto> emailList = personEmailDao.getEmailList(request);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		Date formatDate = new Date();
		try {
			for (EmailDetailDto emailDetail : emailList) {
				if (!TypeConvUtil.isNullOrEmpty(emailDetail.getDtEnd())) {
					formatDate = format.parse(emailDetail.getDtEnd());
					emailDetail.setDtEnd(formatDate.toString());
				}
				if (!TypeConvUtil.isNullOrEmpty(emailDetail.getDtStart())) {
					formatDate = format.parse(emailDetail.getDtStart());
					emailDetail.setDtStart(formatDate.toString());
				}
			}
		}catch (ParseException e) {
			throw new ServiceLayerException(e.getMessage());
		}

		emailResponse.setEmailList(emailList);
		return emailResponse;
	}
}
