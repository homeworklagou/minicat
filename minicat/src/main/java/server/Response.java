package server;

import java.io.IOException;
import java.io.OutputStream;

public class Response {

    private OutputStream outputStream;

    public Response() {
    }

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void outPut(String content) throws IOException {
        outputStream.write(content.getBytes());
    }
}
