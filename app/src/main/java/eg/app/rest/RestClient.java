package eg.app.rest;

import eg.ann.Handler;
import eg.app.rest.dto.Address;
import eg.app.rest.dto.MainUser;
import eg.app.rest.dto.User;

public interface RestClient {

    @Handler
    Address getUserAddress(User user, MainUser mainUser) throws BusinessLogicException;
}
