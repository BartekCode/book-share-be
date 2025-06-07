module core {
    requires transitive com.fasterxml.jackson.annotation;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive org.slf4j;
    requires org.aspectj.weaver;
    requires spring.context;
    requires spring.aop;

    exports com.example.core.services.log;
    exports com.example.core.config;

    opens com.example.core.config to spring.core, spring.beans, spring.context;
}
