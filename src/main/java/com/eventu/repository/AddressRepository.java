package com.eventu.repository;

import com.eventu.exception.BusinessException;
import com.eventu.vo.Address;
import com.eventu.vo.Event;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AddressRepository implements ReactivePanacheMongoRepository<Address>, IRepository{
    public Uni<Address> create(Event event){
        Address address = event.getAddress();

        return persistOrUpdate(address)
                .onFailure().transform(f->  new BusinessException(createErrorMessage(f, "Error creatingAddress"), f)) ;

    }

}
