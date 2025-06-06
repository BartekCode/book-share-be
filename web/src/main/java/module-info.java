module web {
    requires spring.web;
    requires spring.context;
    requires core;
    requires db;
    requires spring.security.crypto;
    requires jakarta.annotation;

    exports com.example.web.controllers;
    exports com.example.web.model;
    exports com.example.web.configuration;
    exports com.example.web.service;
    exports com.example.web.model.user.request;
    exports com.example.web.model.user.response;
    exports com.example.web.utils;

    opens com.example.web.configuration to spring.core, spring.beans, spring.context;
}