package com.eventu.resource.event;

import com.eventu.repository.AddressRepository;
import com.eventu.repository.EventRepository;
import com.eventu.repository.PersonRepository;
import com.eventu.vo.AStatus;
import com.eventu.vo.Event;
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
public class EventCreate {

    @Inject
    EventRepository eventRepository;

    @Inject
    PersonRepository personRepository;

    @Inject
    AddressRepository addressRepository;

    @POST
    @Path("/{person_id}/event")
    public Uni<Response> create(Event event, @PathParam("person_id") String personId){
        //Check if personId exist.
        Uni<Person> uniPerson = personRepository.getPersonById(personId);

        return uniPerson.onItem()
                .transformToUni(person ->  {
                    if( person != null){
                        return createEvent(event, person);
                }else {
                        AStatus status =  createErrorStatus("Person does not exist");
                        return Uni.createFrom().item( Response.serverError().entity(status).build());
                    }

                }).onFailure()
                .recoverWithItem(failure -> {
                    AStatus status =  createErrorStatus(failure.getMessage());
                    return Response.serverError().entity(status).build();
                });

    }

    private Uni<Response> createEvent(Event event, Person person) {
        return eventRepository.create(event, person.getId())
                .onItem().transformToUni(e ->
                            addAddressToEvent(e)
                );
    }

    private Uni<Response> addAddressToEvent(Event e) {
        return addressRepository.create(e).onItem()
                        .transformToUni(address ->
                                eventRepository.addAddress(e,address).onItem()
                                        .transform(updatedEvent -> Response.ok().entity(updatedEvent).build() )

                        );
    }


    private AStatus createErrorStatus(String statusDesc){

        AStatus status = new AStatus();
        status.setStatus("Error");
        status.setError(
                EventError.builder()
                        .errorDesc(statusDesc)
                        .build()
        );
        return status;
    }

}
