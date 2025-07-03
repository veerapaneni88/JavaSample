package us.tx.state.dfps.service.arreport.controller;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.arreport.dto.ArPrincipalsHistoryDto;
import us.tx.state.dfps.service.arreport.dto.ArReportDto;
import us.tx.state.dfps.service.arreport.service.ArReportService;
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvContactSdmSafetyAssessDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvReportMergedDto;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.forms.util.ArReportPrefillData;
import us.tx.state.dfps.service.workload.dto.ContactDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Makes call
 * to service and sends form data to forms server Apr 4, 2018- 4:42:13 PM © 2017
 * Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("arreport")
public class ArReportController {

	@Autowired
	private ArReportService arReportService;

	@Autowired
	ArReportPrefillData prefillData;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(ArReportController.class);

	/**
	 * Method Name: getArReport Method Description: Gets information about AR
	 * report from database and returns prefill string
	 * 
	 * @param req
	 * @return PreFillDataServiceDto
	 * 
	 */
	@RequestMapping(value = "/getreport", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getReport(@RequestBody CommonApplicationReq request) {
		log.debug("Entering method arreport getReport in ArReportController");
		if (TypeConvUtil.isNullOrEmpty(request)) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		ArReportDto arReportDto = arReportService.getGenericCaseInfo(request.getIdStage());
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		String mergedStages = arReportDto.getMergedStages();
		
		Future future1 = executorService.submit(new Callable<List>() {
			@Override
			public List call() throws Exception {
				return arReportService.getContactList(request.getIdStage(), mergedStages);
			}
		});

		List<CpsInvReportMergedDto> cpsInvReportMergedDtoList = arReportDto.getCpsInvReportMergedDtoList();
		Future future2 = executorService.submit(new Callable<List>() {
			@Override
			public List call() throws Exception {
				return arReportService.getContacts(cpsInvReportMergedDtoList);
			}
		});

		arReportDto = arReportService.getArReport(request, arReportDto);
		List<ContactDto> contactList = null;
		List<CpsInvContactSdmSafetyAssessDto> getContactsp1List = null;
		List<ArPrincipalsHistoryDto> arPrincipalsHistoryDtoList = null;
		try {
			contactList = (List<ContactDto>) future1.get();
			arReportDto.setContactList(contactList);
			getContactsp1List = (List<CpsInvContactSdmSafetyAssessDto>) future2.get();
			arReportDto.setGetContactsp1List(getContactsp1List);
			arReportDto = arReportService.getSDMCareGiverList(arReportDto);
		} catch (InterruptedException | ExecutionException e) {
			ServiceLayerException serviceException = new ServiceLayerException(e.getMessage());
			serviceException.initCause(e);
			throw serviceException;
		}finally{
			executorService.shutdownNow();
		}
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(arReportService.returnPrefillData(arReportDto)));

		return commonFormRes;
	}

}
