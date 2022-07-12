package com.eventu.resource;

import com.eventu.vo.AStatus;
import com.eventu.vo.EventError;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.core.Response;

public interface IResource {
    default AStatus createErrorStatus(String statusDesc){

        AStatus status = new AStatus();
        status.setStatus("Error");
        status.setError(
                EventError.builder()
                        .errorDesc(statusDesc)
                        .build()
        );
        return status;
    }

    default Uni<Response> getErrorResponse(String message) {
        AStatus status =  createErrorStatus(message);
        return Uni.createFrom().item( Response.serverError().entity(status).build());
    }
}
