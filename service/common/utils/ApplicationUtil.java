package us.tx.state.dfps.service.common.utils;

import org.hibernate.SessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.FceApplication;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;

@Repository
public class ApplicationUtil {

	@Autowired
	private static SessionFactory sessionFactory;

	public FceApplicationDto findApplication(Long idFceApplication) {
		FceApplicationDto fceApplicationDto = new FceApplicationDto();
		FceApplication fceApplication = (FceApplication) sessionFactory.getCurrentSession().get(FceApplication.class,
				idFceApplication);
		BeanUtils.copyProperties(fceApplication, fceApplicationDto);
		return fceApplicationDto;
	}


}
