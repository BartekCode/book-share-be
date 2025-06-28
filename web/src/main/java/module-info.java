open module web {
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
    requires spring.security.core;
    requires security;
    requires jakarta.mail;
    requires java.sql;

    exports com.example.web.controllers;
    exports com.example.web.model.book.dto.response;
    exports com.example.web.model.book.dto.request;
    exports com.example.web.model.user.dto.response;
    exports com.example.web.model.user.dto.request;
    exports com.example.web.model.common.enums;
    exports com.example.web.model.excpetion;
    exports com.example.web.configuration;
    exports com.example.web.service.user;
    exports com.example.web.service.book;
    exports com.example.web.service.like;
}