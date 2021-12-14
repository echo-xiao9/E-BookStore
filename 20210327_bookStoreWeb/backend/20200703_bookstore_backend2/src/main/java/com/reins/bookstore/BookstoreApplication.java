package com.reins.bookstore;



import javax.jms.ConnectionFactory;


import com.reins.bookstore.entity.BookType;
import com.reins.bookstore.repository.BookTypeRepository;
import com.reins.bookstore.utils.lucene.IndexFiles;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.Arrays;
import java.util.List;


@SpringBootApplication
@EnableJms
public class BookstoreApplication {
    private static IndexFiles indexFiles;

    private final static Logger log = LoggerFactory.getLogger(BookstoreApplication.class);

    @Bean
    public JmsListenerContainerFactory<?> myFactory(@Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory,
                                                    DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all boot's default to this factory, including the message converter
        configurer.configure(factory, connectionFactory);
        // You could still override some of Boot's default if necessary.
        return factory;
    }

    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }


    public static void main(String[] args) {
        // Launch the application
        ConfigurableApplicationContext context = SpringApplication.run(BookstoreApplication.class, args);
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        indexFiles = new IndexFiles();
        indexFiles.updateIndexFiles();
    }

@Bean
public Connector connector() {
    Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    connector.setScheme("http");
    connector.setPort(8787);
    connector.setSecure(false);
    connector.setRedirectPort(9091);
    return connector;
}

    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory(Connector connector) {
        TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint=new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection=new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcatServletWebServerFactory.addAdditionalTomcatConnectors(connector);
        return tomcatServletWebServerFactory;
    }

    @Bean
    CommandLineRunner demo(BookTypeRepository bookTypeRepository) {
        return args -> {

            bookTypeRepository.deleteAll();

            BookType novel = new BookType("novel");
            BookType children = new BookType("children");
            BookType biographies = new BookType("biographies");
            BookType autobiographies = new BookType("autobiographies");
            BookType science = new BookType("science");
            List<BookType> team = Arrays.asList(novel, children, biographies, autobiographies,science);

//            log.info("Before linking up with Neo4j...");

//            team.stream().forEach(bookType -> log.info("\t" + bookType.toString()));

            bookTypeRepository.save(novel);
            bookTypeRepository.save(children);
            bookTypeRepository.save(biographies);
            bookTypeRepository.save(autobiographies);
            bookTypeRepository.save(science);

            novel = bookTypeRepository.findByName(novel.getName());
            novel.recommend(children);
            novel.recommend(biographies);
            novel.recommend(science);
            bookTypeRepository.save(novel);

            children = bookTypeRepository.findByName(children.getName());
            bookTypeRepository.save(children);

            science = bookTypeRepository.findByName(science.getName());
            science.recommend(novel);
            bookTypeRepository.save(science);

            autobiographies = bookTypeRepository.findByName(autobiographies.getName());
            autobiographies.recommend(science);
            autobiographies.recommend(biographies);
            bookTypeRepository.save(autobiographies);

            biographies = bookTypeRepository.findByName(biographies.getName());
            biographies.recommend(autobiographies);
            biographies.recommend(science);
            bookTypeRepository.save(biographies);


            // We already know craig works with roy and greg

//            log.info("Lookup each person by name...");
//            team.stream().forEach(person -> log.info(
//                    "\t" + bookTypeRepository.findByName(person.getName()).toString()));
//
//            List<BookType> teammates = bookTypeRepository.findByRecommendsName(novel.getName());
//            log.info("The following have novel as a recommend...");
//            teammates.stream().forEach(person -> log.info("\t" + person.getName()));
//            log.info("novel recommends it:");
//            log.info( bookTypeRepository.findByName(novel.getName()).toString());

        };
    }





}












