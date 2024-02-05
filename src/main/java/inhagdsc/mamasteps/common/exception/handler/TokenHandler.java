package inhagdsc.mamasteps.common.exception.handler;


import inhagdsc.mamasteps.common.code.BaseErrorCode;
import inhagdsc.mamasteps.common.exception.GeneralException;

public class TokenHandler extends GeneralException {
    public TokenHandler(BaseErrorCode code) {
        super(code);
    }
}
