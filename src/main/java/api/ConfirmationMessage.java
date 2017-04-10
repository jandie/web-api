package api;

/**
 * Used for feedback in the web API.
 *
 * Created by Jandie on 13-3-2017.
 */
public class ConfirmationMessage {
    /**
     * The status of the action.
     */
    private StatusType status;

    /**
     * The message of the action.
     */
    private String message;

    /**
     * The object that has been added.
     */
    private Object returnObject;

    /**
     * Creates a new instance of ConfirmationMessage.
     * @param status The status of the action.
     * @param message The message of the action.
     * @param returnObject The object that has been added.
     */
    public ConfirmationMessage(StatusType status, String message, Object returnObject) {
        this.status = status;
        this.message = message;
        this.returnObject = returnObject;
    }

    /**
     * Gets the status.
     * @return The status.
     */
    public StatusType getStatus() {
        return status;
    }

    /**
     * Gets the message.
     * @return The message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the return object.
     * @return The return object.
     */
    public Object getReturnObject() {
        return returnObject;
    }

    public enum StatusType {
        SUCCES,
        ERROR
    }
}
