module web {
    requires spring.web;
    requires spring.context;
    requires core;
    requires db;
    requires spring.security.crypto;
    requires jakarta.annotation;
    requires spring.beans;
    requires spring.tx;
    requires jakarta.validation;
    requires liquibase.core;
    requires spring.boot.autoconfigure;

    exports com.example.web.controllers;
    exports com.example.web.model.book.dto.response;
    exports com.example.web.model.book.dto.request;
    exports com.example.web.model.user.dto.response;
    exports com.example.web.model.user.dto.request;
    exports com.example.web.model.common.enums;
    exports com.example.web.configuration;
    exports com.example.web.service.user;
    exports com.example.web.service.book;
    exports com.example.web.service.like;

    opens com.example.web.configuration to spring.core, spring.beans, spring.context;
    opens com.example.web.controllers to spring.core, spring.beans, spring.context;
    opens com.example.web.service.book to spring.beans, spring.context, spring.core;
    opens com.example.web.service.user to spring.beans, spring.context, spring.core;
    opens com.example.web.service.like to spring.beans, spring.context, spring.core;
}