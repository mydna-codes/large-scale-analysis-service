package codes.mydna.clients.grpc.models;


import codes.mydna.lib.enums.Status;

public class CheckedEntity<E> {

    private E entity;
    private Status status;

    public E getEntity() {
        return entity;
    }

    public void setEntity(E entity) {
        this.entity = entity;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
