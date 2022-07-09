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
        Address address = createAddress(event);

        return persistOrUpdate(address)
                .onFailure().transform(f->  new BusinessException(createErrorMessage(f, "Error creatingAddress"), f)) ;

    }

    private Address createAddress(Event event) {
        return Address.builder()
                .addressLine_1(event.getAddressLine_1())
                .addressLine_2(event.getAddressLine_2())
                .floorNumber(event.getFloorNumber())
                .suiteNumber(event.getSuiteNumber())
                .city(event.getCity())
                .state(event.getState())
                .roomNumber(event.getRoomNumber())
                .zipCode(event.getZipCode())
                .countryCode(event.getCountryCode())
                .build();
    }
}
