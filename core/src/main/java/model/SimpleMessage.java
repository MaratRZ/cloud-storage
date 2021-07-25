package model;

public class SimpleMessage extends AbstractMessage {
    private String message;

    public SimpleMessage(String message) {
        this.message = message;
    }

    @Override
    public MessageType messageType() {
        return MessageType.SIMPLE;
    }

    public String getMessage() {
        return message;
    }
}
