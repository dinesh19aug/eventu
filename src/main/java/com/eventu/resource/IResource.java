package com.eventu.resource;

import com.eventu.vo.AStatus;
import com.eventu.vo.EventError;

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
}
