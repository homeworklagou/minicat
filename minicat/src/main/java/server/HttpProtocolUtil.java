package server;

public class HttpProtocolUtil {

    public static String get200Response(String content) {
        return "HTTP/1.1 200 OK \n" +
                "Content-Type: text/html \n" +
                "Content-Length: " + content.getBytes().length + " \n"
                + "\r\n" + content;
    }


    public static String getHttpHeader404() {
        String str404 = "<h1>404 not found<h1>";
        return "HTTP/1.1 404 Not Found \n" +
                "Content-Type: text/html \n" +
                "Content-Length: " + str404.getBytes().length + " \n"
                + "\r\n" + str404;
    }
}
