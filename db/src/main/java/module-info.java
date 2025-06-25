module db {
    requires core;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires liquibase.core;
    requires java.sql;
    requires spring.jdbc;
    requires java.management;
    requires spring.security.core;
    requires spring.tx;

    exports com.example.db.dao.book;
    exports com.example.db.dao.user;
    exports com.example.db.dao.like;
    exports com.example.db.model.book;
    exports com.example.db.model.token;
    exports com.example.db.configuration;

    opens com.example.db.configuration to spring.core, spring.beans, spring.context;
    opens com.example.db.model.book to spring.beans, spring.context, spring.core;
    opens com.example.db.dao.book to spring.beans, spring.context, spring.core;
    opens com.example.db.dao.user to spring.beans, spring.context, spring.core;
    opens com.example.db.dao.like to spring.beans, spring.context, spring.core;
    exports com.example.db.dao.role;
    exports com.example.db.dao.token;
    exports com.example.db.exceptions;
}
