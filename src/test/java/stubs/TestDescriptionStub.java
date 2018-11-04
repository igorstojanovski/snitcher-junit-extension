package stubs;

import org.junit.jupiter.engine.descriptor.ClassTestDescriptor;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.UniqueId;

public class TestDescriptionStub extends ClassTestDescriptor {
    public TestDescriptionStub(UniqueId uniqueId, Class<?> testClass, ConfigurationParameters configurationParameters) {
        super(uniqueId, testClass, configurationParameters);
    }
}
