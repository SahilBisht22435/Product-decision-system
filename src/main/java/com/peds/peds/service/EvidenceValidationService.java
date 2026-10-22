
package com.peds.peds.service;

import com.peds.peds.model.Evidence;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class EvidenceValidationService {

    private static final Set<String> ALLOWED_DOMAINS = Set.of(
            "example.com",
            "samsung.com",
            "apple.com",
            "lenovo.com",
            "hp.com",
            "dell.com",
            "asus.com",
            "acer.com",
            "marutisuzuki.com"
    );

    private static final HttpClient HTTP_CLIENT =
            HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(3))
                    .followRedirects(HttpClient.Redirect.NEVER)
                    .build();

    /**
     * Validates required evidence fields and the source URL.
     * This does not establish factual accuracy.
     */
    public Map<String, Object> validateEvidence(Evidence evidence) {

        Map<String, Object> result = new LinkedHashMap<>();

        if (evidence == null) {
            result.put("claimPresent", false);
            result.put("sourceNamePresent", false);
            result.put("urlFormatValid", false);
            result.put("fieldsValid", false);
            result.put("message", "Evidence cannot be null.");
            return result;
        }

        String claim = evidence.getClaim();
        String sourceName = evidence.getSourceName();
        String sourceUrl = evidence.getSourceUrl();

        boolean claimPresent = claim != null && !claim.isBlank();
        boolean sourceNamePresent =
                sourceName != null && !sourceName.isBlank();
        boolean urlFormatValid = isApprovedHttpsUrl(sourceUrl);

        boolean fieldsValid =
                claimPresent && sourceNamePresent && urlFormatValid;

        result.put("claimPresent", claimPresent);
        result.put("sourceNamePresent", sourceNamePresent);
        result.put("urlFormatValid", urlFormatValid);
        result.put("fieldsValid", fieldsValid);
        result.put(
                "message",
                fieldsValid
                        ? "Evidence fields are valid. Source content and claim accuracy have not been verified."
                        : "Evidence fields are missing or invalid, or the source URL is not approved."
        );

        return result;
    }

    /**
     * Checks source availability using HEAD, with a GET fallback
     * when HEAD is unsupported. Redirects are never followed.
     */
    public Map<String, Object> checkSourceAvailability(String sourceUrl) {

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("sourceUrl", sourceUrl);
        result.put("serverResponded", false);
        result.put("reachable", false);
        result.put("pageAccessible", false);
        result.put("httpStatus", null);

        if (sourceUrl == null || sourceUrl.isBlank()) {
            result.put("message", "Source URL is missing.");
            return result;
        }

        if (!isApprovedHttpsUrl(sourceUrl)) {
            result.put(
                    "message",
                    "Only approved HTTPS source URLs on standard ports are allowed."
            );
            return result;
        }

        URI uri;

        try {
            uri = URI.create(sourceUrl.trim());
        } catch (IllegalArgumentException e) {
            result.put("message", "The source URL is invalid.");
            return result;
        }

        try {
            HttpRequest headRequest = buildRequest(uri, "HEAD");

            HttpResponse<Void> response = HTTP_CLIENT.send(
                    headRequest,
                    HttpResponse.BodyHandlers.discarding()
            );

            // Some websites do not support HEAD requests.
            if (response.statusCode() == 405
                    || response.statusCode() == 501) {

                HttpRequest getRequest = buildRequest(uri, "GET");

                response = HTTP_CLIENT.send(
                        getRequest,
                        HttpResponse.BodyHandlers.discarding()
                );
            }

            int status = response.statusCode();
            boolean successful = status >= 200 && status < 300;

            result.put("serverResponded", true);
            result.put("httpStatus", status);
            result.put("reachable", successful);
            result.put("pageAccessible", successful);

            if (successful) {
                result.put(
                        "message",
                        "Source returned a successful HTTP response. Claim accuracy has not been verified."
                );
            } else if (status == 403) {
                result.put(
                        "message",
                        "Server responded, but access was denied. Page accessibility is inconclusive."
                );
            } else if (status == 404) {
                result.put(
                        "message",
                        "Server responded, but the requested page was not found."
                );
            } else if (status >= 300 && status < 400) {
                inspectRedirect(uri, response, result);
            } else {
                result.put(
                        "message",
                        "Server responded with HTTP status " + status + "."
                );
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            result.put("message", "Source check was interrupted.");
        } catch (Exception e) {
            result.put(
                    "message",
                    "No usable HTTP response was received. The source may be unavailable or the request may have been blocked."
            );
        }

        return result;
    }

    private HttpRequest buildRequest(URI uri, String method) {

        return HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(5))
                .header("User-Agent", "PEDS-EvidenceChecker/1.0")
                .method(method, HttpRequest.BodyPublishers.noBody())
                .build();
    }

    /**
     * Checks the redirect destination without requesting it.
     */
    private void inspectRedirect(
            URI originalUri,
            HttpResponse<?> response,
            Map<String, Object> result) {

        String location = response.headers()
                .firstValue("Location")
                .orElse(null);

        result.put("redirectUrl", location);

        if (location == null || location.isBlank()) {
            result.put("redirectDestinationApproved", false);
            result.put(
                    "message",
                    "Server returned a redirect without a destination."
            );
            return;
        }

        try {
            URI redirectUri = originalUri.resolve(location);

            boolean approved =
                    isApprovedHttpsUrl(redirectUri.toString());

            result.put("redirectDestinationApproved", approved);
            result.put(
                    "message",
                    approved
                            ? "Redirect destination is approved but has not been checked."
                            : "Redirect destination is not an approved HTTPS URL."
            );

        } catch (IllegalArgumentException e) {
            result.put("redirectDestinationApproved", false);
            result.put(
                    "message",
                    "The redirect destination is invalid."
            );
        }
    }

    /**
     * Validates HTTPS, host, port, credentials and approved domain.
     */
    private boolean isApprovedHttpsUrl(String sourceUrl) {

        if (sourceUrl == null || sourceUrl.isBlank()) {
            return false;
        }

        // Reject whitespace and control characters before parsing.
        for (int i = 0; i < sourceUrl.length(); i++) {
            char character = sourceUrl.charAt(i);

            if (Character.isWhitespace(character)
                    || Character.isISOControl(character)) {
                return false;
            }
        }

        try {
            URI uri = URI.create(sourceUrl.trim());

            if (!uri.isAbsolute()
                    || !"https".equalsIgnoreCase(uri.getScheme())
                    || uri.getHost() == null
                    || uri.getHost().isBlank()
                    || uri.getUserInfo() != null
                    || uri.getFragment() != null
                    || (uri.getPort() != -1 && uri.getPort() != 443)) {
                return false;
            }

            String host = uri.getHost().toLowerCase(Locale.ROOT);

            return ALLOWED_DOMAINS.stream().anyMatch(
                    domain -> host.equals(domain)
                            || host.endsWith("." + domain)
            );

        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
