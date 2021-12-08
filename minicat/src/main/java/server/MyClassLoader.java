package server;

import java.io.*;
import java.util.Objects;

public class MyClassLoader extends ClassLoader {
    private String classPath;

    public MyClassLoader(String classPath) {
        this.classPath = classPath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class aClass = null;
        byte[] data = getData(name);
        if (Objects.nonNull(data)) {
            aClass = defineClass(name, data, 0, data.length);
        } else {
            throw new ClassNotFoundException(name + "文件取值为空");
        }
        return aClass;
    }

    //返回类的字节码
    private byte[] getData(String className) {
        InputStream in = null;
        ByteArrayOutputStream out = null;
        String path = classPath + File.separatorChar +
                className.replace('.', File.separatorChar) + ".class";
        try {
            in = new FileInputStream(path);
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
