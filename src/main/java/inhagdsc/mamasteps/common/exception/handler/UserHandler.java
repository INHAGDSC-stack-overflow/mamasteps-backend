package inhagdsc.mamasteps.common.exception.handler;


import inhagdsc.mamasteps.common.code.BaseErrorCode;
import inhagdsc.mamasteps.common.exception.GeneralException;

public class UserHandler extends GeneralException {
    public UserHandler(BaseErrorCode code) {
        super(code);
    }
}
