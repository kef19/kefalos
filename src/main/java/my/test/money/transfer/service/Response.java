package my.test.money.transfer.service;

public class Response {

    Object entity;
    String status;
    String message;

    public Response(Object entity, String status, String message) {
        this.status = status;
        this.message = message;
        this.entity = entity;
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}