module core {
    requires transitive com.fasterxml.jackson.annotation;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive org.slf4j;
    requires org.aspectj.weaver;
    requires spring.context;
    requires spring.aop;
    requires spring.security.core;
    requires spring.security.crypto;
    requires spring.context.support;
    requires thymeleaf.spring6;
    requires jakarta.mail;
    requires thymeleaf;
    requires spring.beans;

    exports com.example.core.model.role;
    exports com.example.core.model.user;
    exports com.example.core.services.encoder;
    exports com.example.core.services.log;
    exports com.example.core.config;
    exports com.example.core.services.email;

    opens com.example.core.config to spring.core, spring.beans, spring.context;
    exports com.example.core.model.email;
}
