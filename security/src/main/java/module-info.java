module security {
    requires core;
    requires spring.security.web;
    requires spring.web;
    requires spring.context;
    requires spring.security.config;
    requires spring.security.core;
    requires org.apache.tomcat.embed.core;
    requires db;
    requires jjwt.api;
    requires spring.beans;
    requires jakarta.validation;

    exports com.example.security.configuration;
    exports com.example.security.service;
    exports com.example.security.filter;

    opens com.example.security.service to spring.core, spring.beans, spring.context;
    opens com.example.security.configuration to spring.core, spring.beans, spring.context;
}
