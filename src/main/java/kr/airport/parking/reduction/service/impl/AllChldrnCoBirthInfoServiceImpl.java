package kr.airport.parking.reduction.service.impl;

import javax.annotation.Resource;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import kr.airport.parking.reduction.service.AllChldrnCoBirthInfoService;
import kr.airport.parking.reduction.service.CommonHeaderService;
import kr.airport.parking.reduction.service.CommunicationService;
import kr.airport.parking.reduction.service.SoapService;
import kr.airport.parking.reduction.vo.message.AllChldrnCoBirthInfoRequestBody;
import kr.airport.parking.reduction.vo.message.AllChldrnCoBirthInfoResponseBody;
import kr.airport.parking.reduction.vo.message.CommonHeader;
import kr.airport.parking.reduction.vo.table.AllChldrnCoBirthInfo;
import kr.airport.parking.util.common.CommonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AllChldrnCoBirthInfoServiceImpl implements AllChldrnCoBirthInfoService{
	
	private static final Logger LOG = LoggerFactory.getLogger(AllChldrnCoBirthInfoService.class);
	
	
	@Resource
	CommonHeaderService commonHeaderService;
	
	@Resource
	CommunicationService communicationService;
	
	@Resource
	SoapService soapService;
	

	
	@Value("#{appConfig['useEncrypt']}")	
	private String useEncrypt;
	
	@Value("#{appConfig['useSystemCode']}")	
	private String useSystemCode;
	
	@Value("#{appConfig['userName']}")	
	private String userName;
	
	@Value("#{appConfig['userDeptCode']}")	
	private String userDeptCode;
	
	
			
	@Value("#{appConfig['allChldrnCoBirthInfo.schedulerYn']}")	
	private String schedulerYn;
	
	@Value("#{appConfig['allChldrnCoBirthInfo.URI']}")	
	private String uri;	
	
	@Value("#{appConfig['allChldrnCoBirthInfo.certServerId']}")	
	private String certServerId;	
		
	@Value("#{appConfig['allChldrnCoBirthInfo.methodName']}")	
	private String methodName;	
	
	@Value("#{appConfig['allChldrnCoBirthInfo.serviceName']}")	
	private String serviceName;
	
	@Value("#{appConfig['allChldrnCoBirthInfo.nameSpace']}")	
	private String nameSpace;
	
	@Value("#{appConfig['allChldrnCoBirthInfo.targetServerId']}")	
	private String targetServerId;
	
	@Value("#{appConfig['allChldrnCoBirthInfo.body.orgCode']}")	
	private String orgCode;

	/*
	 * ?????? ?????? ????????? ??????????????? ???????????? ????????? ???????????? ???.
	 */
	@Override
	public AllChldrnCoBirthInfo proc(AllChldrnCoBirthInfo allChldrnCoBirthInfo,Boolean dbProc,AllChldrnCoBirthInfo beforeAllChldrnCoBirthInfo) {
	
		String requestXml;
		String responseXml;
		
		String airportCode = allChldrnCoBirthInfo.getAirportCode();
		String id = allChldrnCoBirthInfo.getId();
		String name = allChldrnCoBirthInfo.getName();
		
		
		beforeAllChldrnCoBirthInfo = allChldrnCoBirthInfo;
		
		
		try {
			
			SOAPMessage soapMsg = soapService.makeSoapMassage();
			SOAPHeader header = soapMsg.getSOAPHeader();
			
			/*
			 * SOAP XML HEADER ?????? ????????? ??????
			 */
			CommonHeader commonHeader = new CommonHeader();
			commonHeader.setServiceName(serviceName);
			commonHeader.setMethodName(methodName);
			commonHeader.setServerId(certServerId);
			commonHeader.setNameSpace(nameSpace);
			commonHeader.setUserDeptCode(userDeptCode);
			commonHeader.setUserName(userName);
			commonHeader.setUseSystemCode(useSystemCode);
			
			LOG.info("========CommonHeader===========================================================");
			LOG.info("["+id+"] AllChldrnCoBirthInfo serviceName " + serviceName);
			LOG.info("["+id+"] AllChldrnCoBirthInfo methodName " + methodName);
			LOG.info("["+id+"] AllChldrnCoBirthInfo certServerId " + certServerId);
			LOG.info("["+id+"] AllChldrnCoBirthInfo nameSpace " + nameSpace);
			LOG.info("["+id+"] AllChldrnCoBirthInfo userDeptCode " + userDeptCode);
			LOG.info("["+id+"] AllChldrnCoBirthInfo userName " + userName);
			LOG.info("["+id+"] AllChldrnCoBirthInfo useSystemCode " + useSystemCode);
			LOG.info("===================================================================");
			
			commonHeader.setTransactionUniqueId(commonHeaderService.getTransactionUniqueIdFromTime());
			header = commonHeaderService.makeCommonHeaderToSOAPHeader(header,commonHeader);
			LOG.info("["+id+"] ReductionTsCarInfo TransactionUniqueId :" + commonHeader.getTransactionUniqueId());
		
			
			/*
			 * SOAP XML BODY ?????? ??????
			 */
			LOG.info("["+id+"] AllChldrnCoBirthInfo body make Start");
			AllChldrnCoBirthInfoRequestBody body = new AllChldrnCoBirthInfoRequestBody();
			body.setOrgCode(orgCode);
			body.setId(allChldrnCoBirthInfo.getId());
			body.setName(allChldrnCoBirthInfo.getName());
			
			
			
			LOG.info("===================================================================");
			LOG.info("["+id+"] AllChldrnCoBirthInfo methodName " + methodName);
			LOG.info("["+id+"] AllChldrnCoBirthInfo nameSpace " + nameSpace);
			LOG.info("===================================================================");
			
			SOAPPart part = soapMsg.getSOAPPart();
			SOAPBodyElement soapBodyElement = soapService.makeBodyElement(part.getEnvelope(),methodName, nameSpace);
			soapBodyElement = makeAllChldrnCoBirthInfoBody(soapBodyElement,body);
			soapMsg.saveChanges();
			LOG.debug("["+id+"] AllChldrnCoBirthInfo soapMsg [" + soapMsg + "]");
			
			/*
			 * SOAP XML Object -> String ??????
			 */
			requestXml = soapService.getXmlStringFromSoapMessageObject(soapMsg);
			requestXml = requestXml.replace("#userName#", userName );
			requestXml = requestXml.replace("#name#", body.getName() );
			LOG.debug("["+id+"] AllChldrnCoBirthInfo requestXml [" + requestXml + "]");
			
			allChldrnCoBirthInfo.setReqSoapXml(requestXml);
			
			
			/*
			 * XML ????????? Body ?????? ????????? ??????
			 */
			if("Y".equals(useEncrypt)) {
				requestXml = soapService.requestMessageEncrypt(methodName, nameSpace, requestXml, targetServerId);
				LOG.debug("["+id+"] AllChldrnCoBirthInfo EnCrypt requestXml [" + requestXml + "]");
			}
			
			
			//??????????????? ??????
			allChldrnCoBirthInfo.setIfTime(CommonUtil.getIfTime());
			allChldrnCoBirthInfo.setIfDt(CommonUtil.getIfDate());
			
			/*
			 * Http Client ???????????? ???????????? ??? ?????? Xml ??????
			 */
			responseXml = communicationService.communicate(uri, requestXml, commonHeader.getTransactionUniqueId(), serviceName, airportCode, id);
			LOG.debug("["+id+"] AllChldrnCoBirthInfo responseXml [" + responseXml + "]");
			
			if(null == responseXml) {
				beforeAllChldrnCoBirthInfo.setReqSoapXml(requestXml);
				
				beforeAllChldrnCoBirthInfo.setIfRsltYn("N");			
				beforeAllChldrnCoBirthInfo.setIfTime(allChldrnCoBirthInfo.getIfTime());
				beforeAllChldrnCoBirthInfo.setIfDt(allChldrnCoBirthInfo.getIfDt());
				beforeAllChldrnCoBirthInfo.setIfType("01");	
				beforeAllChldrnCoBirthInfo.setErrorChk(Boolean.TRUE);
				
				
				LOG.info("["+id+"] AllChldrnCoBirthInfo responseXml null");
				return beforeAllChldrnCoBirthInfo;
			}
			
			/*
			 * XML ????????? Body ?????? ????????? ??????
			 */
			if("Y".equals(useEncrypt)) {
				responseXml = soapService.responseMessageDecrypt(methodName, nameSpace, responseXml);
				LOG.debug("["+id+"] AllChldrnCoBirthInfo responseXml Decrypt [" + responseXml + "]");
			}
			allChldrnCoBirthInfo.setResSoapXml(responseXml);
			
			LOG.info("AllChldrnCoBirthInforesponseXml [" + responseXml + "]");
			
			/*
			 * XML ??????????????? Body VO ?????? ??????
			 */
			AllChldrnCoBirthInfoResponseBody allChldrnCoBirthInfoResponseBody = parsingResultFromResponseXml(responseXml, airportCode, name, id);
		
	
			/*
			 * ?????? ?????? 
			 * orgCode 						???????????? 		
			 * id							??????????????????
			 * name							??????
			 * childrenCnt					?????????
			 * birthDay						?????????????????? ( ??? : 19900101|19500101 )
			 * serviceResult				?????????????????????  	1:??????, 2:?????????????????? ??????, 3:?????? ??????, 4:????????? ??????, 5:???????????????(?????????,?????????), 9:???????????????, 80:??????????????????????????????
			 */
			
			LOG.info(" AllChldrnCoBirthInfo ResponseBody =======================================");
			LOG.info(" allChldrnCoBirthInfoResponseBody  orgCode : " + allChldrnCoBirthInfoResponseBody.getOrgCode());
			LOG.info(" allChldrnCoBirthInfoResponseBody  id : " + allChldrnCoBirthInfoResponseBody.getId());
			LOG.info(" allChldrnCoBirthInfoResponseBody  name : " + allChldrnCoBirthInfoResponseBody.getName());
			LOG.info(" allChldrnCoBirthInfoResponseBody  childrenCnt : " + allChldrnCoBirthInfoResponseBody.getChildrenCnt());
			LOG.info(" allChldrnCoBirthInfoResponseBody  birthDay : " + allChldrnCoBirthInfoResponseBody.getBirthDay());
			LOG.info(" allChldrnCoBirthInfoResponseBody  serviceResult : " + allChldrnCoBirthInfoResponseBody.getServiceResult());
			LOG.info(" =================================================================");
			
			if("1".equals(allChldrnCoBirthInfoResponseBody.getServiceResult())) {
				
				if(Integer.parseInt(allChldrnCoBirthInfoResponseBody.getChildrenCnt()) > 2){
					allChldrnCoBirthInfo.setReductionYn("Y");
				}
				else{
					allChldrnCoBirthInfo.setReductionYn("N");
				}
				
				allChldrnCoBirthInfo.setChildrenCnt(allChldrnCoBirthInfoResponseBody.getChildrenCnt());
				allChldrnCoBirthInfo.setBirthDay(allChldrnCoBirthInfoResponseBody.getBirthDay());
				allChldrnCoBirthInfo.setServiceResult(allChldrnCoBirthInfoResponseBody.getServiceResult());
				
				allChldrnCoBirthInfo.setIfRsltYn("Y");
				allChldrnCoBirthInfo.setErrorChk(Boolean.FALSE);
			}
			else {
				allChldrnCoBirthInfo.setErrorChk(Boolean.TRUE);
				allChldrnCoBirthInfo.setReductionYn("N");
				
				allChldrnCoBirthInfo.setIfRsltYn("Y");
				allChldrnCoBirthInfo.setServiceResult(allChldrnCoBirthInfoResponseBody.getServiceResult());
			}
		} catch (SOAPException soapException){
			LOG.error("["+airportCode+"]["+allChldrnCoBirthInfo.getId()+"] " + soapException.getMessage());
			allChldrnCoBirthInfo.setReductionYn("N");
			
		} catch (NullPointerException nullPointerException){
			LOG.error("["+airportCode+"]["+allChldrnCoBirthInfo.getId()+"] " + nullPointerException.getMessage());
			allChldrnCoBirthInfo.setReductionYn("N");
		} catch (ArrayIndexOutOfBoundsException e) {
			LOG.error("["+airportCode+"]["+allChldrnCoBirthInfo.getId()+"] " + e.getMessage());
			allChldrnCoBirthInfo.setReductionYn("N");
		} catch (Exception e){	
			LOG.error("["+airportCode+"]["+allChldrnCoBirthInfo.getId()+"] " + e.getMessage());
			allChldrnCoBirthInfo.setReductionYn("N");
		
		} 
			
		
		return allChldrnCoBirthInfo;
	}
	
	private SOAPBodyElement makeAllChldrnCoBirthInfoBody(SOAPBodyElement element, AllChldrnCoBirthInfoRequestBody body) throws SOAPException,NullPointerException,Exception{
		element.addChildElement("orgCode").addTextNode(body.getOrgCode());
		element.addChildElement("id").addTextNode(body.getId());
		element.addChildElement("name").addTextNode("#name#");
		return element;
	}
	
	private AllChldrnCoBirthInfoResponseBody parsingResultFromResponseXml(String responseXml, String airportCode, String name, String id) throws Exception{
		
		AllChldrnCoBirthInfoResponseBody allChldrnCoBirthInfoResponseBody = new AllChldrnCoBirthInfoResponseBody();
		try {
			String serviceResultCode = responseXml.split("<serviceResult>")[1].split("</serviceResult>")[0];
			allChldrnCoBirthInfoResponseBody.setServiceResult(serviceResultCode);
			
			if (responseXml.contains("<orgCode/>")) {
				allChldrnCoBirthInfoResponseBody.setOrgCode("");
			} else {
				allChldrnCoBirthInfoResponseBody.setOrgCode(responseXml.split("<orgCode>")[1].split("</orgCode>")[0]);
			}
			
			if (responseXml.contains("<id/>")) {
				allChldrnCoBirthInfoResponseBody.setId(id);
			} else {
				allChldrnCoBirthInfoResponseBody.setId(responseXml.split("<id>")[1].split("</id>")[0]);
			}
			
			if (responseXml.contains("<name/>")) {
				allChldrnCoBirthInfoResponseBody.setName(name);
			} else {
				allChldrnCoBirthInfoResponseBody.setName(responseXml.split("<name>")[1].split("</name>")[0]);
			}
			
			// ???????????? ?????????
			if("1".equals(serviceResultCode)) {			
				if (responseXml.contains("<childrenCnt/>")) {
					allChldrnCoBirthInfoResponseBody.setChildrenCnt("0");
				} else {
					allChldrnCoBirthInfoResponseBody.setChildrenCnt(responseXml.split("<childrenCnt>")[1].split("</childrenCnt>")[0]);
				}
				
				if (responseXml.contains("<birthDay/>")) {
					allChldrnCoBirthInfoResponseBody.setBirthDay("");
				} else {
					allChldrnCoBirthInfoResponseBody.setBirthDay(responseXml.split("<birthDay>")[1].split("</birthDay>")[0]);
				}
			}else {
				allChldrnCoBirthInfoResponseBody.setChildrenCnt("0");
				allChldrnCoBirthInfoResponseBody.setBirthDay("");
			}
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {				
			allChldrnCoBirthInfoResponseBody.setServiceResult("9");
			allChldrnCoBirthInfoResponseBody.setId(id);
			allChldrnCoBirthInfoResponseBody.setName(name);
			LOG.error("["+airportCode+"]["+id+"]["+name+"] " + e.getMessage());
			
		} catch (Exception ex) {				
			allChldrnCoBirthInfoResponseBody.setServiceResult("9");
			allChldrnCoBirthInfoResponseBody.setId(id);
			allChldrnCoBirthInfoResponseBody.setName(name);
			LOG.error("["+airportCode+"]["+id+"]["+name+"] " + ex.getMessage());
			
		}
	
		return allChldrnCoBirthInfoResponseBody;		
	}
	

}
