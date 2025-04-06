package com.codemaniac.authenticationservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class Action implements Serializable {
    @Column(name = "CAN_READ")
    private boolean read;
    @Column(name = "CAN_CREATE")
    private boolean create;
    @Column(name = "CAN_UPDATE")
    private boolean update;
    @Column(name = "CAN_DELETE")
    private boolean delete;

}
