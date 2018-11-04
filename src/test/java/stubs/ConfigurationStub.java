package stubs;

import org.junit.platform.engine.ConfigurationParameters;

import java.util.Optional;

public class ConfigurationStub implements ConfigurationParameters {
    @Override
    public Optional<String> get(String key) {
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
        return Optional.empty();
    }

    @Override
    public int size() {
        return 0;
    }
}
