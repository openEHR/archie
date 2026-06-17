package org.openehr.odin.jackson3;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openehr.odin.jackson3.serializers.*;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.cfg.MapperBuilderState;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.nedap.archie.base.Interval;
import com.nedap.archie.base.terminology.TerminologyCode;

import java.net.URI;
import java.net.URL;

/**
 * Jackson 3 port of {@link org.openehr.odin.jackson.ODINMapper}.
 */
public class ODINMapper3 extends ObjectMapper {

    protected ODINMapper3(Builder b) {
        super(b);
    }

    public static Builder builder() {
        return new Builder(new ODINFactory3());
    }

    public static class Builder extends MapperBuilder<ODINMapper3, Builder> {

        public Builder(ODINFactory3 f) {
            super(f);
            propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
            enable(SerializationFeature.INDENT_OUTPUT);
            defaultPrettyPrinter(new ODINPrettyPrinter3());
            changeDefaultPropertyInclusion(v -> JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.ALWAYS));
            activateDefaultTyping(
                    BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build(),
                    DefaultTyping.JAVA_LANG_OBJECT);

            SimpleModule odinModule = new SimpleModule("odin-module");
            odinModule.addKeySerializer(String.class, new OdinStringMapKeySerializer());
            odinModule.addKeySerializer(Integer.class, new OdinIntegerMapKeySerializer());
            odinModule.addKeySerializer(Long.class, new OdinLongMapKeySerializer());
            odinModule.addSerializer(Interval.class, new OdinIntervalSerializer());
            odinModule.addSerializer(URI.class, new OdinURISerializer());
            odinModule.addSerializer(URL.class, new OdinURLSerializer());
            odinModule.addSerializer(TerminologyCode.class, new TerminologyCodeSerializer());
            addModule(odinModule);
        }

        public Builder(StateImpl state) {
            super(state);
        }

        @Override
        protected MapperBuilderState _saveState() {
            return new StateImpl(this);
        }

        @Override
        public ODINMapper3 build() {
            return new ODINMapper3(this);
        }

        protected static class StateImpl extends MapperBuilderState implements java.io.Serializable {
            private static final long serialVersionUID = 1L;

            public StateImpl(Builder src) {
                super(src);
            }

            @Override
            protected Object readResolve() {
                return new Builder(this).build();
            }
        }
    }

    public ODINMapper3 configure(ODINGenerator3.Feature f, boolean state) {
        return state ? enable(f) : disable(f);
    }

    public ODINMapper3 enable(ODINGenerator3.Feature f) {
        ((ODINFactory3) tokenStreamFactory()).enable(f);
        return this;
    }

    public ODINMapper3 disable(ODINGenerator3.Feature f) {
        ((ODINFactory3) tokenStreamFactory()).disable(f);
        return this;
    }

    public final ODINFactory3 getFactory() {
        return (ODINFactory3) tokenStreamFactory();
    }
}
