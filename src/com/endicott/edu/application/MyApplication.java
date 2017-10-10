package com.endicott.edu.application;

import com.endicott.edu.service.*;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

//Defines the base URI for all resource URIs.
@ApplicationPath("/")
//The java class declares root resource and provider classes
public class MyApplication extends Application{
    //The method returns a non-empty collection with classes, that must be included in the published JAX-RS application
    @SuppressWarnings("unchecked")
    @Override
    public Set<Class<?>> getClasses() {
        HashSet h = new HashSet<Class<?>>();
        h.add(DormServices.class);
        h.add( CollegeService.class );
        h.add( NewsFeedService.class );
        h.add(StudentServices.class);
        h.add(FacultyService.class);
        h.add(SportService.class);
        h.add( org.glassfish.jersey.moxy.json.MoxyJsonFeature.class);  // This enables JSON binding support
        h.add( JsonMoxyConfigurationContextResolver.class);
        return h;
    }
}