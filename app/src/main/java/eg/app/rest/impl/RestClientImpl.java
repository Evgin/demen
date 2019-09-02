package eg.app.rest.impl;

import eg.app.rest.BusinessLogicException;
import eg.app.rest.RestClient;
import eg.app.rest.dto.Address;
import eg.app.rest.dto.MainUser;
import eg.app.rest.dto.User;

public class RestClientImpl implements RestClient {

    @Override
    public Address getUserAddress(User user, MainUser mainUser) throws BusinessLogicException {

        if (user == null) {
            throw new BusinessLogicException();
        }

        return new Address();
    }
}
