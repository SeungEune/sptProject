package egovframework.com.cmm.exception.custom;

import egovframework.com.cmm.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author : 김언중
 * @since : 2023. 11. 15.
 * <p>
 * == 수정사항 ==
 * ---------------------------------------
 * 2023. 11. 15.  김언중 최초 생성
 */
@Getter
public class UnauthorizedException extends HttpClientErrorException {

    private final ErrorCode code = ErrorCode.UNAUTHORIZED;

    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage());
    }

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
