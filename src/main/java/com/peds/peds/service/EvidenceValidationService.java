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

        boolean claimPresent =
                claim != null && !claim.trim().isEmpty();

        boolean sourceNamePresent =
                sourceName != null && !sourceName.trim().isEmpty();

        boolean urlFormatValid = false;

        if (sourceUrl != null && !sourceUrl.trim().isEmpty()) {
            try {
                URI uri = URI.create(sourceUrl.trim());

                urlFormatValid =
                        uri.isAbsolute()
                        && ("https".equalsIgnoreCase(uri.getScheme())
                            || "http".equalsIgnoreCase(uri.getScheme()))
                        && uri.getHost() != null
                        && uri.getUserInfo() == null;
            } catch (IllegalArgumentException e) {
                urlFormatValid = false;
            }
        }

        boolean fieldsValid =
                claimPresent && sourceNamePresent && urlFormatValid;

        result.put("claimPresent", claimPresent);
        result.put("sourceNamePresent", sourceNamePresent);
        result.put("urlFormatValid", urlFormatValid);
        result.put("fieldsValid", fieldsValid);
        result.put(
                "message",
                fieldsValid
                        ? "Evidence fields are valid. Source content has not been verified."
                        : "Evidence fields are missing or invalid."
        );

        return result;
    }

    
public Map<String, Object> checkSourceAvailability(String sourceUrl) {
    Map<String, Object> result = new LinkedHashMap<>();

    result.put("sourceUrl", sourceUrl);
    result.put("serverResponded", false);
    result.put("reachable", false);
    result.put("pageAccessible", false);
    result.put("httpStatus", null);

    if (sourceUrl == null || sourceUrl.trim().isEmpty()) {
        result.put("message", "Source URL is missing.");
        return result;
    }

    try {
        URI uri = URI.create(sourceUrl.trim());
        String host = uri.getHost();

        if (!"https".equalsIgnoreCase(uri.getScheme())
                || host == null
                || uri.getUserInfo() != null
                || (uri.getPort() != -1 && uri.getPort() != 443)) {
            result.put("message", "Only standard HTTPS URLs are allowed.");
            return result;
        }

        

        String normalizedHost = host.toLowerCase(Locale.ROOT);

        boolean allowed = ALLOWED_DOMAINS.stream().anyMatch(
        domain -> normalizedHost.equals(domain)
                || normalizedHost.endsWith("." + domain)
);

        if (!allowed) {
            result.put(
                    "message",
                    "This domain is not in the approved source list."
            );
            return result;
        }

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(5))
                .header("User-Agent", "PEDS-EvidenceChecker/1.0")
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<Void> response = client.send(
                request,
                HttpResponse.BodyHandlers.discarding()
        );

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
            result.put(
                    "message",
                    "Server responded with a redirect. The destination was not checked."
            );
        } else {
            result.put(
                    "message",
                    "Server responded with HTTP status " + status + "."
            );
        }

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        result.put("message", "Source check was interrupted.");
    } catch (IllegalArgumentException e) {
        result.put("message", "The source URL is invalid.");
    } catch (Exception e) {
        result.put(
                "message",
                "No usable HTTP response was received. The source may be unavailable or the request may have been blocked."
        );
    }

    return result;
}

}
