package com.ebay.soap.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.Properties;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "ebay")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema ebaySchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("EbayServicePort");
        wsdl11Definition.setLocationUri("/ws/ebay");
        wsdl11Definition.setTargetNamespace("http://soap.ebay.com/service");
        wsdl11Definition.setSchema(ebaySchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema ebaySchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/ebay-service.xsd"));
    }
    
    @Bean
    public SoapFaultMappingExceptionResolver exceptionResolver() {
        SoapFaultMappingExceptionResolver resolver = new SoapFaultMappingExceptionResolver();
        
        SoapFaultDefinition faultDefinition = new SoapFaultDefinition();
        faultDefinition.setFaultCode(SoapFaultDefinition.SERVER);
        resolver.setDefaultFault(faultDefinition);
        
        Properties errorMappings = new Properties();
        errorMappings.setProperty(Exception.class.getName(), SoapFaultDefinition.SERVER.toString());
        errorMappings.setProperty(IllegalArgumentException.class.getName(), SoapFaultDefinition.CLIENT.toString());
        errorMappings.setProperty("com.ebay.soap.exception.EbayServiceException", SoapFaultDefinition.CLIENT.toString());
        resolver.setExceptionMappings(errorMappings);
        resolver.setOrder(1);
        
        return resolver;
    }
}
