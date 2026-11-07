package com.abhishek.github.tinylink.constant;

public class ApiErrorMessages {
    public static final String successMessage = "success";

    public static final String unknownErrorMessage = "Unknown Error";

    public static final String prefixBelongsToOtherUser = "Prefix Belongs To Other User";

    public static final String validationFailed = "Validation failed";
    public static final String tinyCodeAlreadyTaken = "This tinyCode is already taken, please try with some other value";
    public static final String userNotFound = "User not found";
    public static final String tinyLinkNotFound = "Tiny Link Not Found";
    public static final String tinyLinkNotFoundOrAccessDenied = "Tiny Link not found or access denied";
    public static final String noUrlWithTinyCode = "No URL with this tinyCode";
    public static final String authTokenGenerationFailed = "Auth done but token gen failed";
    public static final String selfReferencingUrl = "URL cannot point back to this service";
    public static final String internalUrlForbidden = "Internal network URLs are not allowed";
    public static final String invalidUrlProtocol = "Only the following protocols are allowed : %s";
    public static final String invalidUrlHost = "URL must contain a valid host";
    public static final String invalidDomain = "Invalid domain format - must contain a valid TLD (e.g., .com, .org, .net)";
    public static final String malformedUrl = "Malformed URL provided";
    public static final String tinyCodeEmpty = "Tiny Code is empty";
    public static final String tinyCodeLength = "Tiny code must be between %d and %d characters";
    public static final String tinyLinkUpdatedSuccessfully = "Tiny Link Updated Successfully";
}
