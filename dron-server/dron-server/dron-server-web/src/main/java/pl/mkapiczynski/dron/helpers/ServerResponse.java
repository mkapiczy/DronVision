package pl.mkapiczynski.dron.helpers;

public enum ServerResponse {
    OK(200, "OK"), 
    FILE_NOT_FOUND(204, "ERROR_FILE_NOT_FOUND"), 
    NOT_AUTHORIZED(403, "ERROR_NOT_AUTHORIZED"), 
    METHOD_NOT_ALLOWED(405, "METHOD_NOT_ALLOWED"), 
    REQUIRED_FIELD_MISSING(412, "ERROR_REQUIRED_FIELD_MISSING"),
    PRECONDITION_FAILED(412, "PRECONDITION_FAILED"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR");


    private int code;
    private String content;

    ServerResponse(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public String getContent() {
        return content;
    }


}