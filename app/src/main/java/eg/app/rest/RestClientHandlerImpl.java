package eg.app.rest;

import eg.app.rest.dto.MainUser;
import eg.app.rest.dto.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestClientHandlerImpl implements RestClientHandler {

    @Override
    public void getUserAddressHandle(User user, MainUser mainUser, BusinessLogicException ex0) {
        log.info("Got exception : {} ---", String.valueOf(ex0));
    }
}
