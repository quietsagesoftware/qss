package com.quietsage.db;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * This implementation is based on work found on:
 * https://sites.google.com/a/athaydes.com/renato-athaydes//posts/jersey_guice_rest_api 
 */
public class DbModule extends GuiceServletContextListener {

	 @Override
	 protected Injector getInjector() {
		 return Guice.createInjector( new ServletModule() {
			 @Override
			 protected void configureServlets() {
				 bind(ConnectionManager.class).to(MongoConnectionManager.class);

                 ResourceConfig rc = new PackagesResourceConfig( "com.olafrye.resource" );
                 for ( Class<?> resource : rc.getClasses() ) {
                	 bind( resource );
                 }

                 serve( "/resource/*" ).with( GuiceContainer.class );
			 }
		 } );
	 }	
}
