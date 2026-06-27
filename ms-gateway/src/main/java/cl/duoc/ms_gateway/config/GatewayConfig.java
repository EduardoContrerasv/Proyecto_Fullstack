package cl.duoc.ms_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Value("${services.user}")        private String userUri;
    @Value("${services.character}")   private String characterUri;
    @Value("${services.item}")        private String itemUri;
    @Value("${services.inventory}")   private String inventoryUri;
    @Value("${services.currency}")    private String currencyUri;
    @Value("${services.shop}")        private String shopUri;
    @Value("${services.quest}")       private String questUri;
    @Value("${services.combat}")      private String combatUri;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("ms-user",      r -> r.path("/api/v1/user/**").uri(userUri))
                .route("ms-character", r -> r.path("/api/v1/character/**").uri(characterUri))
                .route("ms-item",      r -> r.path("/api/v1/item/**").uri(itemUri))
                .route("ms-inventory", r -> r.path("/api/v1/inventory/**").uri(inventoryUri))
                .route("ms-currency",  r -> r.path("/api/v1/currency/**").uri(currencyUri))
                .route("ms-shop",      r -> r.path("/api/v1/shop/**").uri(shopUri))
                .route("ms-quest",     r -> r.path("/api/v1/quest/**").uri(questUri))
                .route("ms-combat",    r -> r.path("/api/v1/combats/**").uri(combatUri))
                .build();
    }
}
