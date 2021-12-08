package server;

public class LagouServlet extends HttpServlet {
    @Override
    public void doGet(Request request, Response response) throws Exception {
        response.outPut(HttpProtocolUtil.get200Response("<h1>demo1 LagouServlet Get</h1>"));
    }

    @Override
    public void doPost(Request request, Response response) throws Exception {
        response.outPut(HttpProtocolUtil.get200Response("<h1>demo1 LagouServlet Post</h1>"));
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void destroy() throws Exception {

    }
}
