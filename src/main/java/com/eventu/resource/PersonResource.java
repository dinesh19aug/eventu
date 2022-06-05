package com.eventu.resource;

import com.eventu.repository.PersonRepository;
import com.eventu.vo.AStatus;
import com.eventu.vo.EventError;
import com.eventu.vo.Person;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/person")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {
    @Inject
    PersonRepository repository;

    @POST
    public Uni<Response> create(Person person){
        return repository.create(person). map(r ->
                Response.ok(r).build())
                .onFailure()
                .recoverWithItem(f-> {
                    AStatus status =  createErrorStatus(f.getMessage());
                    return Response.serverError().entity(status).build();
                }) ;
    }

    @GET
    @Path("/{email}")
    public Uni<Response> get(@PathParam("email") String email){
        Response response=null;
        return repository.getPersonByEmail(email).onItem().transform(person -> {
            if(person==null){
                person = new Person();
                person.setStatus("No Records found");
            }
            return person;
        }).map(
                resp-> Response.ok().entity(resp).build()
        );
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
