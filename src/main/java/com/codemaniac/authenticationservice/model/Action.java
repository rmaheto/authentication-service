package com.codemaniac.authenticationservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class Action implements Serializable {
    @Column(name = "CAN_READ")
    private Boolean read;
    @Column(name = "CAN_CREATE")
    private Boolean create;
    @Column(name = "CAN_UPDATE")
    private Boolean update;
    @Column(name = "CAN_DELETE")
    private Boolean delete;

}
