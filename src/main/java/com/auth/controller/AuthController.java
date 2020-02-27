package com.auth.controller;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.auth.model.embeddedid.AuthResultKey;
import com.auth.model.entity.AuthResult;
import com.auth.model.repository.AuthApplyRepository;
import com.auth.model.repository.AuthResultRepository;
import com.google.common.hash.Hashing;

@Controller
@RequestMapping("/nice")
public class AuthController {

	@Value("${nice.site.code}")
	private String sSiteCode; // NICE로부터 부여받은 사이트 코드

	@Value("${nice.site.password}")
	private String sSitePassword; // NICE로부터 부여받은 사이트 패스워드

	@Value("${jwt.header.name}")
	private String JWT_HEADER;
	
	@Value("${jwt.refresh.name}")
	private String JWT_REFRESH;

	@Autowired
	private AuthResultRepository authResultRepository;

	@Autowired
	private AuthApplyRepository authApplyRepository;

	@Autowired
	private SimpMessagingTemplate template;

	/**
	 * 본인인증 서비스 호출 페이지
	 * 
	 * @param session
	 * @param model
	 * @return
	 */
	@CrossOrigin("*")
	@ResponseBody
	@PostMapping("/")
	public ResponseEntity<Map<String, String>> auth(HttpServletRequest request, HttpSession session,
			@RequestBody Map<String, String> requestMap, HttpServletResponse response) {

//		String tokenString = request.getHeader(JWT_HEADER);

		String userId = requestMap.get("applyUserId");
		String serialNum = requestMap.get("serialNumber");

//		Map<String, String> tokenMap = new HashMap<String, String>();
//		try {
//			tokenMap = refresh(tokenString, serialNum).getBody();
//		} catch (Exception e) {
//			Map<String, String> resultMap = new HashMap<String, String>();
//			return new ResponseEntity<Map<String, String>>(resultMap, HttpStatus.UNAUTHORIZED);
//		}

		String sMessage = "";
		String sEncData = "";

		NiceID.Check.CPClient niceCheck = new NiceID.Check.CPClient();

		String sRequestNumber = "REQ0000000001"; // 요청 번호, 이는 성공/실패후에 같은 값으로 되돌려주게 되므로
		sRequestNumber = niceCheck.getRequestNO(sSiteCode);
		sRequestNumber = sRequestNumber + ":" + userId + ":" + serialNum;

		String sAuthType = ""; // 없으면 기본 선택화면, M: 핸드폰, C: 신용카드, X: 공인인증서
		String popgubun = "Y"; // Y : 취소버튼 있음 / N : 취소버튼 없음
		String customize = ""; // 없으면 기본 웹페이지 / Mobile : 모바일페이지
		String sGender = ""; // 없으면 기본 선택 값, 0 : 여자, 1 : 남자

		// CheckPlus(본인인증) 처리 후, 결과 데이타를 리턴 받기위해 다음예제와 같이 http부터 입력합니다.
		// 리턴url은 인증 전 인증페이지를 호출하기 전 url과 동일해야 합니다. ex) 인증 전 url : http://www.~ 리턴 url :
		// http://www.~
//		String sReturnUrl = "https://www.skhappycampus.com/auth/nice/success";
//		String sErrorUrl = "https://www.skhappycampus.com/auth/nice/fail";
		String sReturnUrl = "http://192.168.0.8:8081/nice/success";
		String sErrorUrl = "http://192.168.0.8:8081/nice/fail";

		// 입력될 plain 데이타를 만든다.
		String sPlainData = "7:REQ_SEQ" + sRequestNumber.getBytes().length + ":" + sRequestNumber + "8:SITECODE"
				+ sSiteCode.getBytes().length + ":" + sSiteCode + "9:AUTH_TYPE" + sAuthType.getBytes().length + ":"
				+ sAuthType + "7:RTN_URL" + sReturnUrl.getBytes().length + ":" + sReturnUrl + "7:ERR_URL"
				+ sErrorUrl.getBytes().length + ":" + sErrorUrl + "11:POPUP_GUBUN" + popgubun.getBytes().length + ":"
				+ popgubun + "9:CUSTOMIZE" + customize.getBytes().length + ":" + customize + "6:GENDER"
				+ sGender.getBytes().length + ":" + sGender;

		int iReturn = niceCheck.fnEncode(sSiteCode, sSitePassword, sPlainData);
		if (iReturn == 0) {
			sEncData = niceCheck.getCipherData();
		} else if (iReturn == -1) {
			sMessage = "암호화 시스템 에러입니다.";
		} else if (iReturn == -2) {
			sMessage = "암호화 처리오류입니다.";
		} else if (iReturn == -3) {
			sMessage = "암호화 데이터 오류입니다.";
		} else if (iReturn == -9) {
			sMessage = "입력 데이터 오류입니다.";
		} else {
			sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
		}

//		response.addHeader(JWT_REFRESH, tokenMap.get(JWT_REFRESH));
//		response.addHeader("userid", tokenMap.get("userid"));
//		response.addHeader("usertype", tokenMap.get("usertype"));
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("message", sMessage);
		resultMap.put("encData", sEncData);
		return new ResponseEntity<Map<String, String>>(resultMap, HttpStatus.OK);
	}

	@CrossOrigin("*")
	@ResponseBody
	private ResponseEntity<Map<String,String>> refresh(String token, String serialNum) {

		final String url = "https://www.skhappycampus.com/api/authNice/refreshNice/"+serialNum;

		HttpHeaders headers = new HttpHeaders();
		headers.set(JWT_HEADER, token);
		HttpEntity entity = new HttpEntity(headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		
		String newToken = response.getHeaders().get(JWT_REFRESH).get(0);	
		String userId = response.getHeaders().get("userid").get(0);	
		String userType = response.getHeaders().get("usertype").get(0);	
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put(JWT_REFRESH, newToken);
		resultMap.put("userid", userId);
		resultMap.put("usertype", userType);
		return new ResponseEntity<Map<String,String>>(resultMap, HttpStatus.OK);
	}

	/**
	 * 성공시 페이지
	 * 
	 * @param request
	 * @param session
	 * @return
	 */
	@CrossOrigin("*")
	@PostMapping("/success")
	public String success(HttpServletRequest request, Model model) {

		NiceID.Check.CPClient niceCheck = new NiceID.Check.CPClient();
		String sEncodeData = requestReplace(request.getParameter("EncodeData"), "encodeData");
		String sCipherTime = ""; // 복호화한 시간
		String sRequestNumber = ""; // 요청 번호
		String sResponseNumber = ""; // 인증 고유번호
		String sAuthType = ""; // 인증 수단
		String sName = ""; // 성명
		String sDupInfo = ""; // 중복가입 확인값 (DI_64 byte)
		String sConnInfo = ""; // 연계정보 확인값 (CI_88 byte)
		String sBirthDate = ""; // 생년월일(YYYYMMDD)
		String sGender = ""; // 성별
		String sNationalInfo = ""; // 내/외국인정보 (개발가이드 참조)
		String sMobileNo = ""; // 휴대폰번호
		String sMobileCo = ""; // 통신사
		String sMessage = "";
		String sPlainData = "";
		String dupYn = "N";
		String userId = "";
		String serialNum = "";

		String noticeMessage = "F";

		int iReturn = niceCheck.fnDecode(sSiteCode, sSitePassword, sEncodeData);

		if (iReturn == 0) {

			noticeMessage = "S";

			sPlainData = niceCheck.getPlainData();
			sCipherTime = niceCheck.getCipherDateTime();

			// 데이타를 추출합니다.
			java.util.HashMap mapresult = niceCheck.fnParse(sPlainData);

			sRequestNumber = (String) mapresult.get("REQ_SEQ");
			userId = sRequestNumber.split(":")[1];
			serialNum = sRequestNumber.split(":")[2];
			sRequestNumber = sRequestNumber.split(":")[0];
			sResponseNumber = (String) mapresult.get("RES_SEQ");
			sAuthType = (String) mapresult.get("AUTH_TYPE");
			sName = (String) mapresult.get("NAME");
			// sName = (String)mapresult.get("UTF8_NAME"); //charset utf8 사용시 주석 해제 후 사용
			sBirthDate = (String) mapresult.get("BIRTHDATE");
			sGender = (String) mapresult.get("GENDER");
			sNationalInfo = (String) mapresult.get("NATIONALINFO");
			sDupInfo = (String) mapresult.get("DI");
			sConnInfo = (String) mapresult.get("CI");
			sMobileNo = (String) mapresult.get("MOBILE_NO");
			sMobileCo = (String) mapresult.get("MOBILE_CO");

			AuthResult authResult = new AuthResult();
			AuthResultKey authResultKey = new AuthResultKey();
			authResultKey.setSerialNumber(serialNum);
			authResultKey.setApplyUserId(userId);

			if (authResultRepository.findById(authResultKey).isEmpty()) {
				// 본인인증 중복 확인
				if (authResultRepository.findByDupInfoAndSerialNumber(sDupInfo, serialNum) > 0) {
					dupYn = "Y";
					noticeMessage = "I";
				}

				// 만 14세 미만 사용자 제외
				if (sBirthDate != null && sBirthDate.length() == 8) {

					try {
						int birthYear = Integer.parseInt(sBirthDate.substring(0, 4));
						int birthMonth = Integer.parseInt(sBirthDate.substring(4, 6));
						int birthDay = Integer.parseInt(sBirthDate.substring(6, 8));
						int age = getAge(birthYear, birthMonth, birthDay);

						// 나이제한 : 만 14세 미만 만 34세 초과 인증
						if (age < 14 || age > 34) {
							noticeMessage = "A";
						} else {

							// 국적제한 : 대한민국만 가능
							if ("1".equals(sNationalInfo)) {
								noticeMessage = "N";
							} else {
								// 본인인증 정보 등록
								authResult.setAuthResultKey(authResultKey);
								authResult.setCipherTime(sCipherTime);
								authResult.setRequestNumber(sRequestNumber);
								authResult.setResponseNumber(sResponseNumber);
								authResult.setAuthType(sAuthType);
								authResult.setName(sName);
								authResult.setDupInfo(sDupInfo);
								authResult.setBirthDate(sBirthDate);
								authResult.setGender(sGender);
								authResult.setNationalInfo(sNationalInfo);
								authResult.setDupYn(dupYn);
								authResultRepository.save(authResult);

								// 지원 정보 업데이트
								if ("N".equals(dupYn)) {
									String gender = "0".equals(sGender) ? "여" : "남";
									String nationality = "1".equals(sNationalInfo) ? "외국인" : "대한민국";
									authApplyRepository.updateApply(sName, sBirthDate, gender, nationality, serialNum,
											userId);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						noticeMessage = "F";
					}

				} else {
					noticeMessage = "F";
				}

			} else {
				noticeMessage = "D";
			}

		} else if (iReturn == -1) {
			sMessage = "복호화 시스템 에러입니다.";
		} else if (iReturn == -4) {
			sMessage = "복호화 처리오류입니다.";
		} else if (iReturn == -5) {
			sMessage = "복호화 해쉬 오류입니다.";
		} else if (iReturn == -6) {
			sMessage = "복호화 데이터 오류입니다.";
		} else if (iReturn == -9) {
			sMessage = "입력 데이터 오류입니다.";
		} else if (iReturn == -12) {
			sMessage = "사이트 패스워드 오류입니다.";
		} else {
			sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
		}

		String sha256 = Hashing.sha256().hashString(userId, StandardCharsets.UTF_8).toString();
		this.template.convertAndSend("/topic/greetings/" + sha256, noticeMessage);

		return "success";
	}

	/**
	 * 실패 페이지
	 * 
	 * @param request
	 * @param session
	 * @return
	 */
	@CrossOrigin("*")
	@PostMapping("/fail")
	public String fail(HttpServletRequest request) {
		NiceID.Check.CPClient niceCheck = new NiceID.Check.CPClient();

		String sEncodeData = requestReplace(request.getParameter("EncodeData"), "encodeData");
		String sCipherTime = ""; // 복호화한 시간
		String sRequestNumber = ""; // 요청 번호
		String sErrorCode = ""; // 인증 결과코드
		String sAuthType = ""; // 인증 수단
		String sMessage = "";
		String sPlainData = "";

		int iReturn = niceCheck.fnDecode(sSiteCode, sSitePassword, sEncodeData);

		if (iReturn == 0) {
			sPlainData = niceCheck.getPlainData();
			sCipherTime = niceCheck.getCipherDateTime();

			// 데이타를 추출합니다.
			java.util.HashMap mapresult = niceCheck.fnParse(sPlainData);

			sRequestNumber = (String) mapresult.get("REQ_SEQ");
			sErrorCode = (String) mapresult.get("ERR_CODE");
			sAuthType = (String) mapresult.get("AUTH_TYPE");
		} else if (iReturn == -1) {
			sMessage = "복호화 시스템 에러입니다.";
		} else if (iReturn == -4) {
			sMessage = "복호화 처리오류입니다.";
		} else if (iReturn == -5) {
			sMessage = "복호화 해쉬 오류입니다.";
		} else if (iReturn == -6) {
			sMessage = "복호화 데이터 오류입니다.";
		} else if (iReturn == -9) {
			sMessage = "입력 데이터 오류입니다.";
		} else if (iReturn == -12) {
			sMessage = "사이트 패스워드 오류입니다.";
		} else {
			sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
		}

		return "fail";
	}

	/**
	 * @param paramValue
	 * @param gubun
	 * @return
	 */
	public String requestReplace(String paramValue, String gubun) {
		String result = "";

		if (paramValue != null) {

			paramValue = paramValue.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

			paramValue = paramValue.replaceAll("\\*", "");
			paramValue = paramValue.replaceAll("\\?", "");
			paramValue = paramValue.replaceAll("\\[", "");
			paramValue = paramValue.replaceAll("\\{", "");
			paramValue = paramValue.replaceAll("\\(", "");
			paramValue = paramValue.replaceAll("\\)", "");
			paramValue = paramValue.replaceAll("\\^", "");
			paramValue = paramValue.replaceAll("\\$", "");
			paramValue = paramValue.replaceAll("'", "");
			paramValue = paramValue.replaceAll("@", "");
			paramValue = paramValue.replaceAll("%", "");
			paramValue = paramValue.replaceAll(";", "");
			paramValue = paramValue.replaceAll(":", "");
			paramValue = paramValue.replaceAll("-", "");
			paramValue = paramValue.replaceAll("#", "");
			paramValue = paramValue.replaceAll("--", "");
			paramValue = paramValue.replaceAll("-", "");
			paramValue = paramValue.replaceAll(",", "");

			if (gubun != "encodeData") {
				paramValue = paramValue.replaceAll("\\+", "");
				paramValue = paramValue.replaceAll("/", "");
				paramValue = paramValue.replaceAll("=", "");
			}

			result = paramValue;

		}
		return result;
	}

	/**
	 * 만 나이 계산
	 * 
	 * @param birthYear
	 * @param birthMonth
	 * @param birthDay
	 * @return
	 */
	public int getAge(int birthYear, int birthMonth, int birthDay) {
		Calendar current = Calendar.getInstance();
		int currentYear = current.get(Calendar.YEAR);
		int currentMonth = current.get(Calendar.MONTH) + 1;
		int currentDay = current.get(Calendar.DAY_OF_MONTH);

		int age = currentYear - birthYear;
		// 생일 안 지난 경우 -1
		if (birthMonth * 100 + birthDay > currentMonth * 100 + currentDay)
			age--;

		return age;
	}
}
