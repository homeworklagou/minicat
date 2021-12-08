package server;

import java.util.ArrayList;
import java.util.List;

public class Mapper {
    private List<Host> hosts = new ArrayList<>();

    public List<Host> getHosts() {
        return hosts;
    }

    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }

    public HttpServlet getServlet(Request request) {
        for (int i = 0; i < hosts.size(); i++) {
            Host host = hosts.get(i);
            // /demo1/lagou
            if (request.getHost().equals(host.getName())) {
                String url = request.getUrl();
                List<Context> contextList = host.getContextList();
                for (int j = 0; j < contextList.size(); j++) {
                    Context context = contextList.get(j);
                    // /demo1
                    if (url.indexOf(context.getPath()) == 0) {
                        List<Wrapper> wrapperList = context.getWrapperList();
                        for (int k = 0; k < wrapperList.size(); k++) {
                            Wrapper wrapper = wrapperList.get(k);
                            // /lagou
                            if (url.substring(context.getPath().length()).indexOf(wrapper.getUrl()) == 0) {
                                return wrapper.getServlet();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
