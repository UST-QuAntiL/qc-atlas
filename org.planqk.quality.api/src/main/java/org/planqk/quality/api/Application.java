package org.planqk.quality.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.planqk.quality")
public class Application {

    final private static Logger LOG = LoggerFactory.getLogger(Application.class);

    @Value("${service.message}")
    public static String message;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        logReadyMessage();
    }

    private static void logReadyMessage() {
        final String readyMessage = "\n===================================================\n" +
                "QUALITY IS READY TO USE!\n" +
                "===================================================";
        LOG.info(readyMessage);
    }
}
