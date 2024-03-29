spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://{{.Env.POSTGRES_HOSTNAME}}:{{.Env.POSTGRES_PORT}}/{{.Env.POSTGRES_DB}}
spring.datasource.username={{.Env.POSTGRES_USER}}
spring.datasource.password={{.Env.POSTGRES_PASSWORD}}
spring.datasource.sql-script-encoding=UTF-8
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true
spring.jpa.properties.hibernate.event.merge.entity_copy_observer=allow
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.hibernate.ddl-auto=update
spring.liquibase.change-log=classpath:/db/changelog-master.xml

logging.level.org.planqk.atlas=DEBUG
springdoc.swagger-ui.path=/swagger-ui
springdoc.api-docs.groups.enabled=true
springdoc.swagger-ui.config-url=/atlas/v3/api-docs/swagger-config
springdoc.swagger-ui.url=/atlas/v3/api-docs/
springdoc.swagger-ui.operationsSorter=alpha
springdoc.default-produces-media-type=application/hal+json
spring.jackson.deserialization.adjust-dates-to-context-time-zone=false
spring.jackson.serialization.write-dates-as-timestamps=false

# Embedded Tomcat
server.servlet.contextPath=/atlas
# Max file upload size
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size={{ default .Env.UPLOAD_FILE_SIZE "150MB"}}
spring.servlet.multipart.max-request-size={{ default .Env.UPLOAD_FILE_SIZE "150MB"}}

# Winery
org.planqk.atlas.winery.protocol={{ default .Env.WINERY_PROTOCOL "http" }}
org.planqk.atlas.winery.hostname={{ default .Env.WINERY_HOSTNAME "localhost" }}
org.planqk.atlas.winery.port={{ default .Env.WINERY_PORT "8080" }}
