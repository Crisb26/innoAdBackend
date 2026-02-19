package com.innoad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.innoad")
@EntityScan(basePackages = {
    "com.innoad.config.auditoria",
    "com.innoad.hardware.model",
    "com.innoad.mantenimiento",
    "com.innoad.modules.admin.domain",
    "com.innoad.modules.auth.domain",
    "com.innoad.modules.campaigns.domain",
    "com.innoad.modules.chat.dominio",
    "com.innoad.modules.content.domain",
    "com.innoad.modules.ia.domain",
    "com.innoad.modules.mantenimiento.dominio",
    "com.innoad.modules.pagos.dominio",
    "com.innoad.modules.publicaciones.model",
    "com.innoad.modules.reportes.modelo",
    "com.innoad.modules.screens.domain",
    "com.innoad.modules.ubicaciones.model",
    "com.innoad.roles"
})
@EnableJpaRepositories(basePackages = "com.innoad")
@EnableCaching
@EnableAsync
@EnableScheduling
public class InnoAdApplication {

    public static void main(String[] args) {
        SpringApplication.run(InnoAdApplication.class, args);
    }
}
