package com.eventu.resource.person;

import com.eventu.repository.PersonRepository;
import com.eventu.vo.AStatus;
import com.eventu.vo.EventError;
import com.eventu.vo.Person;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/party/person")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class PersonRead {
    @Inject
    PersonRepository repository;

    @GET
    @Path("/{email}")
    public Uni<Response> get(@PathParam("email") String email){
        Response response=null;
        return repository.getPersonByEmail(email).onItem().transform(person -> {
            if(person==null){
                AStatus aStatus= new AStatus();
                aStatus.setStatus("No Records found");
                return Response.serverError().entity(aStatus).build();
            }else{
                return Response.ok().entity(person).build();
            }

        });
    }


    private Person createErrorStatus(String statusDesc){

        Person person = Person.builder().build();
        person.setStatus("Error");
                person.setError(
                        EventError.builder()
                                .errorDesc(statusDesc)
                                .build()
                );
        return person;
    }

}
