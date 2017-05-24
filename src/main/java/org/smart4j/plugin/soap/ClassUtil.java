package org.smart4j.plugin.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.util.StringUtil;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 开发一个类加载器,类操作工具类
 * Created by Administrator on 2017/2/22 0022.
 */
public final class ClassUtil {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(org.smart4j.framework.util.ClassUtil.class);
    /**
     * 获取类加载器
     */
    public static ClassLoader getClassLoader(){
        //获取当前线程中的ClassLoader
        return Thread.currentThread().getContextClassLoader();
    }
    /**
     * 加载类
     */
    public static Class<?> loadClass(String className,boolean isInitialized){
        Class<?> cls;
        try {
            cls = Class.forName(className,isInitialized,getClassLoader());
        }catch (ClassNotFoundException e){
            LOGGER.error("load class failure",e);
            throw new RuntimeException(e);
        }
        return cls;
    }
    /**
     * 获取指定包名下的所有类
     */
    public static Set<Class<?>> getClassSet(String packageName){
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        try {
            //1,通过包名获取资源,使用类加载器方法
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".","/"));
            while (urls.hasMoreElements()){
                URL url = urls.nextElement();
                if (url!=null){
                    String propocol = url.getProtocol();//获取原型
                    if (propocol.equals("file")){//如果是文件
                        String packagePath = url.getPath().replace("%20"," ");//截取路径
                        addClass(classSet,packagePath,packageName);
                    }else if(propocol.equals("jar")){
                        //如果是jar文件,则需要通过JarUrlConnection的方法获取资源
                        JarURLConnection jarURLConnection = (JarURLConnection)url.openConnection();
                        if (jarURLConnection!=null){
                            JarFile jarFile = jarURLConnection.getJarFile();
                            if (jarFile!=null){
                                Enumeration<JarEntry> jarEntries = jarFile.entries();
                                //获取到jar里面的文件,逐个文件添加到类加载器.
                                while (jarEntries.hasMoreElements()){
                                    JarEntry jarEntry = jarEntries.nextElement();
                                    String jarEntryName = jarEntry.getName();
                                    if (jarEntryName.endsWith(".class")){
                                        String className = jarEntryName.substring(0,jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                        doAddClass(classSet,className);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            LOGGER.error("get class set failure",e);
            throw new RuntimeException(e);
        }
        //返回加载后的类型集合对象.
        return classSet;
    }

    /**
     *
     * @param classSet
     * @param packagePath
     * @param packageName
     */
    private static void addClass(Set<Class<?>> classSet,String packagePath,String packageName){
        //判断是否为class文件
        File [] files = new File(packagePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.isFile()&&file.getName().endsWith(".class"))||
                file.isDirectory();
            }
        });
        for (File file:files){
            String fileName = file.getName();
            if (file.isFile()){
                String className = fileName.substring(0,fileName.lastIndexOf("."));
                if (StringUtil.isNotEmpty(packageName)){
                    className = packageName + "." + className;
                }
                doAddClass(classSet,className);
            }else {
                String subPackagePath = fileName;
                if (StringUtil.isNotEmpty(packagePath)){
                    //如果不是文件则找它下面的子文件
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                String subPackageName = fileName;
                if (StringUtil.isNotEmpty(packageName)){
                    subPackageName = packageName + "." + subPackageName;
                }
                //递归调用,遍历当前路径下的所有文件
                addClass(classSet,subPackagePath,subPackageName);
            }
        }
    }

    private static void doAddClass(Set<Class<?>>classSet,String className){
        Class<?> cls = loadClass(className,false);
        classSet.add(cls);
    }
}
