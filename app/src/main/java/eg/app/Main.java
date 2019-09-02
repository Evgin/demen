package eg.app;

import eg.app.rest.BusinessLogicException;
import eg.app.rest.RestClient;
import eg.app.rest.dto.MainUser;
import eg.app.rest.impl.RestClientImpl;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
public class Main {


    public static void main(String[] args) {

        MainUser user = new MainUser();
        user = null;

        RestClient restClient = createRestClient();

        try {
            log.info("Address: {}", restClient.getUserAddress(null, null));
        } catch (BUSINESSLOGICEXCEPTION e) {
            log.error("BusinessLogicException", e);
        } catch (Throwable t) {
            log.error("Throwable", t);
        }
    }


    private static RestClient createRestClient() {
        Class<?>[] interfaces = new Class<?>[1];
        interfaces[0] = RestClient.class;

        return (RestClient) Proxy.newProxyInstance(
                Main.class.getClassLoader(),
                interfaces,
                new InvocationHandler() {
                    private RestClientImpl restClientImpl = new RestClientImpl();

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        try {
                            return method.invoke(restClientImpl, args);
                        } catch (Throwable e) {
                            Class<?> handlerClass = Class.forName(RestClient.class.getCanonicalName() + "HandlerImpl");
                            Class<?>[] methodParamTypes = Stream.concat(
                                    Arrays.stream(method.getParameterTypes()),
                                    Arrays.stream(method.getExceptionTypes()))
                                    .toArray(Class<?>[]::new);
                            Method handleMethod = handlerClass.getDeclaredMethod(method.getName() + "Handle", methodParamTypes);

                            Object[] params = Arrays.copyOf(args, methodParamTypes.length);
                            params[methodParamTypes.length - 1] = e.getCause();
                            handleMethod.invoke(handlerClass.newInstance(), params);
                            throw e.getCause();
                        }
                    }
                }
        );
    }

}
