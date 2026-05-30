package com.alpacaflow.meditrack.meditrackgateway.infrastructure.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway routes with Resilience4j circuit breakers per downstream bounded context.
 */
@Configuration
public class GatewayRoutesConfiguration {

    private static final String IAM_BREAKER = "iamCircuitBreaker";
    private static final String ORGANIZATION_BREAKER = "organizationCircuitBreaker";
    private static final String IAM_FALLBACK = "forward:/fallback/iam";
    private static final String ORGANIZATION_FALLBACK = "forward:/fallback/organization";

    @Bean
    public RouteLocator meditrackRoutes(
            RouteLocatorBuilder builder,
            @Value("${services.iam.url}") String iamUrl,
            @Value("${services.organization.url}") String organizationUrl) {
        return builder.routes()
                .route("iam-authentication", r -> r
                        .path("/api/v1/authentication/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName(IAM_BREAKER)
                                .setFallbackUri(IAM_FALLBACK)))
                        .uri(iamUrl))
                .route("iam-mock-users", r -> r
                        .path("/temp-api/v1/users/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName(IAM_BREAKER)
                                .setFallbackUri(IAM_FALLBACK)))
                        .uri(iamUrl))
                .route("iam-staff-users", r -> r
                        .path("/api/v1/users/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName(IAM_BREAKER)
                                .setFallbackUri(IAM_FALLBACK)))
                        .uri(iamUrl))
                .route("organization-organizations", r -> r
                        .path("/api/v1/organizations/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName(ORGANIZATION_BREAKER)
                                .setFallbackUri(ORGANIZATION_FALLBACK)))
                        .uri(organizationUrl))
                .route("organization-admins", r -> r
                        .path("/api/v1/admins/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName(ORGANIZATION_BREAKER)
                                .setFallbackUri(ORGANIZATION_FALLBACK)))
                        .uri(organizationUrl))
                .route("organization-doctors", r -> r
                        .path("/api/v1/doctors/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName(ORGANIZATION_BREAKER)
                                .setFallbackUri(ORGANIZATION_FALLBACK)))
                        .uri(organizationUrl))
                .route("organization-caregivers", r -> r
                        .path("/api/v1/caregivers/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName(ORGANIZATION_BREAKER)
                                .setFallbackUri(ORGANIZATION_FALLBACK)))
                        .uri(organizationUrl))
                .route("organization-senior-citizens", r -> r
                        .path("/api/v1/senior-citizens/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName(ORGANIZATION_BREAKER)
                                .setFallbackUri(ORGANIZATION_FALLBACK)))
                        .uri(organizationUrl))
                .build();
    }
}
