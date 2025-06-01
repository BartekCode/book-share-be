module app {
    requires spring.core;
    requires spring.context;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires core;
    requires db;

    exports com.example.app;
}