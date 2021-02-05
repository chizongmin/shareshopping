package base

class SystemException extends RuntimeException {

    int status
    int errorCode
    String message

    SystemException(String message) {
        super(message)
        this.message = message
    }

    SystemException(int status, String message) {
        super(message)
        this.status = status
        this.message = message
    }

    SystemException(int status, int errorCode) {
        this.status = status
        this.errorCode = errorCode
    }

    SystemException(int status, int errorCode, String message) {
        super(message)
        this.status = status
        this.errorCode = errorCode
        this.message = message
    }
    SystemException() {
    }
}

