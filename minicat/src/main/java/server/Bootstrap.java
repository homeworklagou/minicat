package server;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

/**
 * minicat主类
 */
public class Bootstrap {

    private static final int port = 8080;

    private Mapper mapper = new Mapper();

    private class RequestProcessor implements Runnable {
        private Socket socket;

        public RequestProcessor(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                Request request = new Request(inputStream);
                Response response = new Response(socket.getOutputStream());
                HttpServlet httpServlet = mapper.getServlet(request);
                if (Objects.nonNull(httpServlet)) {
                    httpServlet.service(request, response);
                } else {
                    response.outPut(HttpProtocolUtil.getHttpHeader404());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * 程序启动入口
     * */
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void start() throws Exception {

        loadConfig();

        // 定义一个线程池
        int corePoolSize = 10;
        int maximumPoolSize = 50;
        long keepAliveTime = 100L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();


        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler
        );
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket socket = serverSocket.accept();
            threadPoolExecutor.execute(new RequestProcessor(socket));
        }
    }

    private void loadConfig() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("server.xml");
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            List<Element> hostElementList = rootElement.selectNodes("//Host");
            for (int i = 0; i < hostElementList.size(); i++) {
                Element hostElement = hostElementList.get(i);
                Host host = new Host();
                host.setName(hostElement.attributeValue("name"));
                //D:\homework\code\webapps
                String appBase = hostElement.attributeValue("appBase");
                host.setAppBase(appBase);
                mapper.getHosts().add(host);
                List<Element> contextElements = hostElement.selectNodes("Context");
                for (int j = 0; j < contextElements.size(); j++) {
                    Element contextElement = contextElements.get(j);
                    Context context = new Context();
                    context.setPath(contextElement.attributeValue("path"));
                    //\demo1
                    String docBase = contextElement.attributeValue("docBase");
                    context.setDocBase(docBase);
                    //D:\homework\code\webapps\demo1
                    String appPath = appBase + docBase;
                    File file = new File(appPath, "web.xml");
                    if (file.exists()) {
                        List<Wrapper> wrapperList = loadServlet(file);
                        if (Objects.nonNull(wrapperList) && !wrapperList.isEmpty()) {
                            context.getWrapperList().addAll(wrapperList);
                        }
                    } else {
                        throw new RuntimeException("配置的路径未找到！");
                    }
                    host.getContextList().add(context);
                }
            }
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //加载解析web.xml 初始化Servlet
    private List<Wrapper> loadServlet(File file) throws FileNotFoundException {
        List<Wrapper> wrapperList = new ArrayList<>();
        InputStream resourceAsStream = new FileInputStream(file);
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            List<Element> list = rootElement.selectNodes("//servlet");
            for (int i = 0; i < list.size(); i++) {
                Element element = list.get(i);
                Wrapper wrapper = new Wrapper();
                Element servletNameElement = (Element) element.selectSingleNode("servlet-name");
                String servletName = servletNameElement.getStringValue();
                Element servletClassElement = (Element) element.selectSingleNode("servlet-class");
                String servletClass = servletClassElement.getStringValue();
                //根据servlet-name的值找到url-pattern
                Element servletMappingElement = (Element) element.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
                String urlPattern = servletMappingElement.selectSingleNode("url-pattern").getStringValue();
                wrapper.setUrl(urlPattern);
                //加载类并实例化
                MyClassLoader classLoader = new MyClassLoader(file.getParent());
                Class<?> servletClazz = classLoader.loadClass(servletClass);
                wrapper.setServlet((HttpServlet) servletClazz.newInstance());
                wrapperList.add(wrapper);
            }
        } catch (DocumentException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return wrapperList;
    }

//    private HttpServlet queryServletByRequest(Request request){
//        for (int i = 0; i < hosts.size(); i++) {
//            Host host = hosts.get(i);
//        }
//    }
}
