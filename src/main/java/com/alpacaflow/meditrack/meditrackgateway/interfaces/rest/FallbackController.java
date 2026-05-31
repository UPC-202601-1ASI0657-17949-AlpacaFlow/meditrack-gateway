package com.alpacaflow.meditrack.meditrackgateway.interfaces.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fallback responses when a downstream microservice is unavailable and the circuit breaker opens.
 */
@RestController
@RequestMapping(value = "/fallback", produces = MediaType.APPLICATION_JSON_VALUE)
public class FallbackController {

    @RequestMapping(
            value = "/iam",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH}
    )
    public ResponseEntity<Map<String, Object>> iamFallback() {
        return serviceUnavailable(
                "IAM",
                "Identity and authentication service is temporarily unavailable. Please try again later."
        );
    }

    @RequestMapping(
            value = "/organization",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH}
    )
    public ResponseEntity<Map<String, Object>> organizationFallback() {
        return serviceUnavailable(
                "Organization",
                "Organization service is temporarily unavailable. Please try again later."
        );
    }

    @RequestMapping(
            value = "/relatives",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH}
    )
    public ResponseEntity<Map<String, Object>> relativesFallback() {
        return serviceUnavailable(
                "Relatives",
                "Relatives service is temporarily unavailable. Please try again later."
        );
    }

    private static ResponseEntity<Map<String, Object>> serviceUnavailable(String service, String detail) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "about:blank");
        body.put("title", "Service Unavailable");
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("detail", detail);
        body.put("service", service);
        body.put("code", "CIRCUIT_BREAKER_OPEN");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}
