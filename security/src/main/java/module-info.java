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

}
