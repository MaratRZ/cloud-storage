package model;

import lombok.Data;

import java.io.Serializable;

public abstract class AbstractMessage implements Serializable {
    public abstract MessageType messageType();
}
