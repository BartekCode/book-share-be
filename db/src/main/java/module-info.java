module db {
    requires core;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires liquibase.core;
    requires java.sql;
    requires spring.jdbc;

    exports com.example.db.repository;
    exports com.example.db.model;
    exports com.example.db.configuration;

    opens com.example.db.repository to spring.core, spring.beans, spring.context;
    opens com.example.db.configuration to spring.core, spring.beans, spring.context;
}
