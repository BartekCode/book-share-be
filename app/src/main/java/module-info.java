module app {
    requires spring.core;
    requires spring.context;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires core;
    requires web;

    exports com.example.app;
}