package server;

import java.io.IOException;
import java.io.InputStream;

public class Request {
    private String method;

    private String url;

    private InputStream inputStream;

    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Request() {
    }

    public Request(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        int count = 0;
        while (count == 0) {
            count = inputStream.available();
        }
        byte[] bytes = new byte[count];
        inputStream.read(bytes);
        String inputStr = new String(bytes);
        System.out.println("========>请求信息" + inputStr);

        String firstLine = inputStr.split("\\n")[0];
        String[] strings = firstLine.split(" ");
        this.method = strings[0];
        this.url = strings[1];

        String hostLine = inputStr.split("\\n")[1];
        String[] hostInfos = hostLine.split(":");
        this.host = hostInfos[1].trim();
        System.out.println("========>method" + method);

        System.out.println("========>url" + url);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;

    }
}
