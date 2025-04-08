package hirjanfabian.ccframework.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {
    // Definește cheia API așteptată pentru endpoint-urile API
    private static final String API_KEY = "123";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Exclude rutele Swagger (dacă le folosești) și orice rută care nu începe cu /api
        // (presupunând că doar endpoint-urile API vor fi protejate)
        if (!path.startsWith("/api") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Dacă este o cerere de tip OPTIONS, continuă fără verificare
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Verifică API key doar pentru endpoint-urile care încep cu /api
        String apiKey = request.getHeader("X-API-Key");
        if (!API_KEY.equals(apiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
