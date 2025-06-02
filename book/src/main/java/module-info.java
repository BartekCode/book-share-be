module book {
    requires spring.web;
    requires spring.context;
    requires core;
    requires db;

    exports com.example.book.controllers;
    exports com.example.book.model;
    exports com.example.book.configuration;
    exports com.example.book.service;

    opens com.example.book.configuration to spring.core, spring.beans, spring.context;
}