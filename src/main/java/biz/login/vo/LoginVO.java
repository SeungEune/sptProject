package biz.login.vo;

import java.io.Serializable;

import javax.validation.constraints.Email;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @Class Name : LoginVO.java
 * @Description : Login VO class
 * @Modification Information
 * @
 * @  수정일         수정자                   수정내용
 * @ -------    --------    ---------------------------
 * @ 2009.03.03    박지욱          최초 생성
 *
 *  @author 공통서비스 개발팀 박지욱
 *  @since 2009.03.03
 *  @version 1.0
 *  @see
 *  
 */
@Getter
@Setter
public class LoginVO implements Serializable{
	
	private static final long serialVersionUID = -8274004534207618049L;
	
	// 아이디
	private String userId;
	
	// 이름
	private String userNm;

	// 카카오프로필 닉네임
	private String nickname;

	// 로그인 타입 (COM: 일반, SNS: 소셜)
	private String loginType;

	// 주민등록번호
	private String ihidNum;
	
	// 이메일주소
	@Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
	private String email;
	
	// 비밀번호
	private String password;
	
	// 비밀번호 힌트
	private String passwordHint;
	
	// 비밀번호 정답
	private String passwordCnsr;
	
	// 사용자 구분 (GNR: 일반, ENT: 기업, USR: 사용자)
	private String userSe;

	// 사용자 구분명
	private String userSeNm;

	// 조직(부서)ID
	private String orgnztId;
	
	// 조직(부서)명
	private String orgnztNm;
	
	// 고유아이디
	private String uniqId;
	
	// 로그인 후 이동할 페이지
	private String url;
	
	// 사용자 IP정보
	private String ip;
	
	// GPKI인증 DN
	private String dn;
}
