package com.example.contentrange;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpRange;
import org.springframework.web.util.ContentCachingResponseWrapper;

@WebFilter("*")
public class AddResponseHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        var rangeHeader = httpServletRequest.getHeader("Range");

        // if there is no range request in the header than do the "normal" filtering
        if (rangeHeader == null) {
            chain.doFilter(request, response);
            return;
        }

        HttpRange range = HttpRange.parseRanges(rangeHeader).get(0);

        ContentCachingResponseWrapper responseWrapper =
                new ContentCachingResponseWrapper((HttpServletResponse) response);

        try {
            chain.doFilter(request, responseWrapper);
        } finally {
            byte[] copy = responseWrapper.getContentAsByteArray();
            int size = responseWrapper.getContentSize();
            int lower = (int) range.getRangeStart(size);
            int upper = (int) range.getRangeEnd(size);
            if (lower <= size) {
                responseWrapper.setStatus(SC_PARTIAL_CONTENT);
                byte[] subArray = Arrays.copyOfRange(copy, lower, upper + 1);
                String newContent = new String(subArray, UTF_8);
                responseWrapper.reset();
                responseWrapper.setHeader(
                        "Content-Range", String.format("bytes %d-%d/%d", lower, upper, size));
                responseWrapper.setContentLength(newContent.length());
                responseWrapper.getWriter().write(newContent);
                responseWrapper.getWriter().flush();
                responseWrapper.flushBuffer();
                responseWrapper.copyBodyToResponse();
            } else {
                responseWrapper.setStatus(SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            }
        }
    }
}
