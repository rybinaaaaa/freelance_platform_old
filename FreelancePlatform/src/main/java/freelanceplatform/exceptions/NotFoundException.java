package freelanceplatform.exceptions;

public class NotFoundException extends BaseException{
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a NotFoundException with the specified resource name and identifier.
     *
     * @param resourceName The name of the resource.
     * @param identifier   The identifier of the resource.
     * @return NotFoundException with a message indicating the resource was not found.
     */
    public static NotFoundException create(String resourceName, Object identifier) {
        return new NotFoundException(resourceName + " identified by " + identifier + " not found.");
    }
}
