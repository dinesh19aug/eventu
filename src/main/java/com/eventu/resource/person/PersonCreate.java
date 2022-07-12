package com.eventu.resource.person;

import com.eventu.repository.PersonRepository;
import com.eventu.vo.AStatus;
import com.eventu.vo.EventError;
import com.eventu.vo.Person;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/party/person")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class PersonCreate {
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
