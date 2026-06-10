package com.alpacaflow.meditrack.meditrackgateway.infrastructure.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
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
    private static final String CLINICAL_BREAKER = "clinicalCircuitBreaker";
    private static final String RELATIVES_BREAKER = "relativesCircuitBreaker";
    private static final String IAM_FALLBACK = "forward:/fallback/iam";
    private static final String ORGANIZATION_FALLBACK = "forward:/fallback/organization";
    private static final String CLINICAL_FALLBACK = "forward:/fallback/clinical";
    private static final String RELATIVES_FALLBACK = "forward:/fallback/relatives";

    /**
     * Browser CORS is handled by the gateway. Strip CORS request headers before proxying to
     * Organization so its CorsFilter does not reject allowed frontend origins twice.
     */
    private GatewayFilterSpec organizationFilters(GatewayFilterSpec filters) {
        return filters
                .removeRequestHeader("Origin")
                .removeRequestHeader("Access-Control-Request-Method")
                .removeRequestHeader("Access-Control-Request-Headers")
                .circuitBreaker(c -> c
                        .setName(ORGANIZATION_BREAKER)
                        .setFallbackUri(ORGANIZATION_FALLBACK));
    }

    private GatewayFilterSpec relativesFilters(GatewayFilterSpec filters) {
        return filters
                .removeRequestHeader("Origin")
                .removeRequestHeader("Access-Control-Request-Method")
                .removeRequestHeader("Access-Control-Request-Headers")
                .circuitBreaker(c -> c
                        .setName(RELATIVES_BREAKER)
                        .setFallbackUri(RELATIVES_FALLBACK));
    }

    private GatewayFilterSpec clinicalFilters(GatewayFilterSpec filters) {
        return filters
                .removeRequestHeader("Origin")
                .removeRequestHeader("Access-Control-Request-Method")
                .removeRequestHeader("Access-Control-Request-Headers")
                .circuitBreaker(c -> c
                        .setName(CLINICAL_BREAKER)
                        .setFallbackUri(CLINICAL_FALLBACK));
    }

    @Bean
    public RouteLocator meditrackRoutes(
            RouteLocatorBuilder builder,
            @Value("${services.iam.url}") String iamUrl,
            @Value("${services.organization.url}") String organizationUrl,
            @Value("${services.relatives.url}") String relativesUrl,
            @Value("${services.clinical.url}") String clinicalUrl) {
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
                        .filters(this::organizationFilters)
                        .uri(organizationUrl))
                .route("organization-admins", r -> r
                        .path("/api/v1/admins/**")
                        .filters(this::organizationFilters)
                        .uri(organizationUrl))
                .route("organization-doctors", r -> r
                        .path("/api/v1/doctors/**")
                        .filters(this::organizationFilters)
                        .uri(organizationUrl))
                .route("organization-caregivers", r -> r
                        .path("/api/v1/caregivers/**")
                        .filters(this::organizationFilters)
                        .uri(organizationUrl))
                .route("organization-senior-citizens", r -> r
                        .path("/api/v1/senior-citizens/**")
                        .filters(this::organizationFilters)
                        .uri(organizationUrl))
                .route("relatives", r -> r
                        .path("/api/v1/relatives/**")
                        .filters(this::relativesFilters)
                        .uri(relativesUrl))
                .route("clinical-medical-records", r -> r
                        .path("/api/v1/medical-records/**")
                        .filters(this::clinicalFilters)
                        .uri(clinicalUrl))
                .route("clinical-patient-thresholds", r -> r
                        .path("/api/v1/patient-thresholds/**")
                        .filters(this::clinicalFilters)
                        .uri(clinicalUrl))
                .build();
    }
}
