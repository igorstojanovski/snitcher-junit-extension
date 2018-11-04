package extensions;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class WiremockExtension implements AfterEachCallback, ParameterResolver {

    ThreadLocal<WireMockServer> wiremockLocal = new ThreadLocal<>();

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        wiremockLocal.get().stop();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().isAnnotationPresent(Wiremock.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        WireMockServer wireMockServer = new WireMockServer(options().dynamicPort().notifier(new ConsoleNotifier(true))); //No-args constructor will start on port 8080, no HTTPS
        wiremockLocal.set(wireMockServer);
        wireMockServer.start();

        return wireMockServer;
    }
}
