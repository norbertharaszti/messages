package edu.progmatic.messageapp.filters;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.util.Arrays;

@Component
public class RequestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //servletRequest.getParameterMap().forEach((k, v) -> System.out.println(k + ": " + Arrays.toString(v)));
        filterChain.doFilter(servletRequest, servletResponse);


    }
}
