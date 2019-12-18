package com.zq.kyb.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * User: Joey Timestamp: 2006-7-7 Time: 16:04:04 封装java反射相关的操作
 */
public class ReflectionUtils {

    public static Annotation getAnnotation(Class<?> source, String name, Class c) {
        Annotation a = null;
        try {
            Field field = source.getDeclaredField(name);
            if (field != null) {
                field.setAccessible(true);
                a = field.getAnnotation(c);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // e.printStackTrace();
        }
        return a;
    }

    /**
     * 取得一个类，包括父类的字段。 javaIOC的一些操作
     * 
     * @param aClass
     *            带查找的类
     * @return List 字段的列表
     */
    public static Collection<Field> getFieldsForClass(Class<?> aClass) {
        Map<String, Field> li = new TreeMap<String, Field>();
        while (aClass != null) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (li.get(field.getName()) == null) {
                    li.put(field.getName(), field);
                }

            }
            aClass = aClass.getSuperclass();
        }
        return li.values();
    }

    /**
     * 在一个对象中从一个类或它的父类中找一个字段
     * 
     * @param object
     *            当前对象
     * @param fieldName
     *            字段名称
     * @return Field 找到的字段
     * @throws ClassNotFoundException
     */
    public static Field getFieldForObject(Object object, String fieldName) {
        Class<? extends Object> aClass = object.getClass();

        return getFieldForClass(aClass, fieldName);
    }

    /**
     * 在一个对象中设置一个字段的值
     * 
     * @param object
     *            当前对象
     * @param fieldName
     *            字段名称
     * @return Field 找到的字段
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws ParseException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    public static void setFieldValue(Object object, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, ParseException, InstantiationException,
            InvocationTargetException {
        Field fieldForObject = getFieldForObject(object, fieldName);
        if (fieldForObject == null)
            return;
        fieldForObject.setAccessible(true);
        Class type = fieldForObject.getType();
        String name = type.getName();

        if (value == null || value instanceof JSONNull) {
            value = null;
        } else if ("java.lang.Long".equals(name) || "java.lang.Integer".equals(name) || "java.lang.Float".equals(name) || "java.lang.Double".equals(name) || "java.lang.Boolean".equals(name)) {
            value = ReflectionUtils.stringConverBaseType(String.valueOf(value), type);
        } else if (type.isEnum()) {
            if (value != null && value instanceof String) {
                value = Enum.valueOf(type, String.valueOf(value));
            }
        } else if (instanceOf(type, Timestamp.class)) {
            Long time = null;
            if (value instanceof Long) {
                time = (Long) value;
            } else if (value instanceof String) {
                if (value != null) {
                    time = Long.valueOf((String) value);
                }
            }
            if (time != null) {
                value = new Timestamp(time);
            }
        } else if (instanceOf(type, List.class)) {
            if (value instanceof JSONArray) {
                ArrayList a = new ArrayList();
                Class<?> genericClass = ReflectionUtils.getGenericClass(fieldForObject);
                JSONArray jsonArray = (JSONArray) value;
                for (Object object2 : jsonArray) {
                    if (genericClass != null && object2 instanceof JSONObject) {
                        Object obj = genericClass.newInstance();
                        a.add(populate(obj, (JSONObject) object2));
                    } else {
                        a.add(object2);
                    }
                }
                value = a;
            }
        } else if (instanceOf(type, String.class)) {
            if (!(value instanceof String)) {
                value = value.toString();
            }
        } else {
            if (value instanceof String) {
                value = ReflectionUtils.stringConverBaseType((String) value, type);
            }
        }
        fieldForObject.set(object, value);
    }

    /**
     * 类似javascript,将string自动转换为其他基础类型数据
     * 
     * @param type
     * @return
     * @throws ParseException
     */
    public static Object stringConverBaseType(String fieldValue, Class<?> type) throws ParseException {
        String name = type.getName();
        Object value = fieldValue;
        if ("long".equals(name) || "java.lang.Long".equals(name)) {
            fieldValue = org.apache.commons.lang.StringUtils.isEmpty(fieldValue) ? "0" : fieldValue;
            value = Long.parseLong(fieldValue);
        } else if ("int".equals(name) || "java.lang.Integer".equals(name)) {
            fieldValue = org.apache.commons.lang.StringUtils.isEmpty(fieldValue) ? "0" : fieldValue;
            value = Integer.parseInt(fieldValue);
        } else if ("double".equals(name) || "java.lang.Double".equals(name)) {
            fieldValue = org.apache.commons.lang.StringUtils.isEmpty(fieldValue) ? "0.0" : fieldValue;
            value = Double.parseDouble(fieldValue);
        } else if ("float".equals(name) || "java.lang.Float".equals(name)) {
            fieldValue = org.apache.commons.lang.StringUtils.isEmpty(fieldValue) ? "0.0" : fieldValue;
            value = Float.parseFloat(fieldValue);
        } else if ("boolean".equals(name) || "java.lang.Boolean".equals(name)) {
            fieldValue = org.apache.commons.lang.StringUtils.isEmpty(fieldValue) ? "false" : fieldValue;
            if ("1".equals(fieldValue)) {
                value = true;
            } else if ("0".equals(fieldValue)) {
                value = false;
            } else {
                value = Boolean.parseBoolean(fieldValue);
            }
        } else if ("java.sql.Timestamp".equals(name)) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            value = new Timestamp(df.parse(fieldValue).getTime());
        }
        return value;
    }

    /**
     * 获取一个字段的泛形的类
     *
     * @param fieldForObject
     * @return
     */
    public static Class<?> getGenericClass(Field fieldForObject) {
        Type mapMainType = fieldForObject.getGenericType();
        Class<?> genericClass = null;
        if (mapMainType instanceof ParameterizedType) {
            // 执行强制类型转换
            ParameterizedType parameterizedType = (ParameterizedType) mapMainType;
            // 获取基本类型信息，即Map
            // Type basicType = parameterizedType.getRawType();
            // System.out.println("基本类型为：" + basicType);
            Type[] types = parameterizedType.getActualTypeArguments();
            if (types != null && types.length > 0) {
                genericClass = (Class<?>) types[0];
            }
        }
        return genericClass;
    }

    public static Object populate(Object bean, Map properties) throws IllegalAccessException, InvocationTargetException, IllegalArgumentException, ParseException, InstantiationException {
        if ((bean == null) || (properties == null)) {
            return null;
        }
        Iterator entries = properties.entrySet().iterator();
        while (entries.hasNext()) {
            // Identify the property name and value(s) to be assigned
            Map.Entry entry = (Map.Entry) entries.next();
            String name = (String) entry.getKey();
            if (name == null) {
                continue;
            }
            setFieldValue(bean, name, entry.getValue());
        }
        return bean;
    }

    /**
     * 在一个类中从一个类或它的父类中找一个字段
     *
     * @param aClass
     *            当前类
     * @param fieldName
     *            字段名称
     * @return Field 找到的字段
     */
    public static Field getFieldForClass(Class<? extends Object> aClass, String fieldName) {
        Field declaredField = null;
        while (aClass != null) {
            declaredField = getFields(aClass, fieldName);
            if (declaredField != null) {
                break;
            }
            aClass = aClass.getSuperclass();
        }
        return declaredField;
    }

    private static Field getFields(Class<? extends Object> entityType, String str) {
        Field s = null;
        try {
            s = entityType.getDeclaredField(str);
        } catch (NoSuchFieldException e) {
            // e.printStackTrace();
        }
        return s;
    }

    /**
     * 判断objClass 是否为extendClass或extendClass的子类
     *
     * @param subClass
     *            需要判断的类
     * @param parentClass
     *            类
     * @return 成立:true
     */
    public static boolean instanceOf(Class subClass, Class parentClass) {
        boolean s = false;
        if (subClass != null && parentClass != null) {
            Class k = subClass;
            if (subClass == parentClass)
                return true;
            while (k.getSuperclass() != null && !s) {
                if (k == parentClass)
                    return true;
                k = k.getSuperclass();
            }
            s = getInter(s, subClass, parentClass);
        }
        return s;
    }

    private static boolean getInter(boolean isEquel, Class subClass, Class parentClass) {
        Class[] ins = subClass.getInterfaces();
        for (Class in : ins) {
            if (in == parentClass) {
                isEquel = true;
                break;
            }
            isEquel = getInter(isEquel, in, parentClass);
        }
        return isEquel;
    }

    /**
     * 得到某个对象的公共属性
     *
     * @param owner
     *            当前对象
     * @param fieldName
     *            字段名
     * @return 该属性对象
     * @throws IllegalAccessException
     *             字段访问异常
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws NoSuchFieldException
     *             字段每找到异常
     */
    public static Object getProperty(Object owner, String fieldName) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Field field = getFieldForObject(owner, fieldName);
        Object obj = null;
        if (field != null) {
            int indexOf = owner.getClass().getName().indexOf("$$EnhancerByCGLIB$$");
            if (indexOf > -1) {
                String name = field.getType().getName();
                String methodName = "get" + EntityUtils.getEntityName(fieldName);
                if ("boolean".equals(name) || "java.lang.Boolean".equals(name)) {
                    methodName = "is" + EntityUtils.getEntityName(fieldName);
                }
                try {
                    Method method = owner.getClass().getMethod(methodName);
                    obj = method.invoke(owner);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                field.setAccessible(true);
                obj = field.get(owner);
            }
        }
        return obj;
    }

    /**
     * 拷贝字段
     *
     * @param targetObj
     * @param sourceObj
     * @throws IllegalAccessException
     */
    public static void copyProperties(Object targetObj, Object sourceObj) throws IllegalAccessException {
        Collection<Field> li = getFieldsForClass(targetObj.getClass());
        for (Field field : li) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            try {
                field.set(targetObj, getProperty(sourceObj, field.getName()));
            } catch (Exception e) {
                Logger.getLogger(ReflectionUtils.class.getName()).info(e.getMessage());
                // e.printStackTrace();
            }
        }
    }

    /**
     * 得到某类的静态公共属性
     *
     * @param className
     *            类名
     * @param fieldName
     *            属性名
     * @return 该属性对象
     * @throws IllegalAccessException
     *             异常
     * @throws NoSuchFieldException
     *             异常
     * @throws ClassNotFoundException
     *             异常
     */
    public static Object getStaticProperty(String className, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class ownerClass = Class.forName(className);

        Field field = ownerClass.getField(fieldName);

        return field.get(ownerClass);
    }

    /**
     * 执行某类的静态方法
     *
     * @param className
     *            类名
     * @param methodName
     *            方法名
     * @param args
     *            参数数组
     * @return 执行方法返回的结果
     * @throws NoSuchMethodException
     *             异常抛出
     * @throws IllegalAccessException
     *             异常抛出
     * @throws InvocationTargetException
     *             异常抛出
     * @throws ClassNotFoundException
     *             异常抛出
     */
    public Object invokeStaticMethod(String className, String methodName, Object[] args) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Class ownerClass = Class.forName(className);

        Class[] argsClass = new Class[args.length];

        for (int i = 0, j = args.length; i < j; i++) {
            argsClass[i] = args[i].getClass();
        }

        Method method = ownerClass.getMethod(methodName, argsClass);

        return method.invoke(null, args);
    }

    /**
     * 新建实例
     *
     * @param className
     *            类名
     * @param args
     *            构造函数的参数
     * @return 新建的实例
     * @throws NoSuchMethodException
     *             异常
     * @throws IllegalAccessException
     *             异常
     * @throws InvocationTargetException
     *             异常
     * @throws InstantiationException
     *             异常
     * @throws ClassNotFoundException
     *             异常
     */
    public Object newInstance(String className, Object[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> newoneClass = Class.forName(className);

        Class[] argsClass = new Class[args.length];

        for (int i = 0, j = args.length; i < j; i++) {
            argsClass[i] = args[i].getClass();
        }

        Constructor<?> cons = newoneClass.getConstructor(argsClass);

        return cons.newInstance(args);

    }

    /**
     * 是不是某个类的实例
     * 
     * @param obj
     *            实例
     * @param cls
     *            类
     * @return 如果 obj 是此类的实例，则返回 true
     */
    public boolean isInstance(Object obj, Class cls) {
        return cls.isInstance(obj);
    }

    /**
     * 得到数组中的某个元素
     * 
     * @param array
     *            数组
     * @param index
     *            索引
     * @return 返回指定数组对象中索引组件的值
     */
    public Object getByArray(Object array, int index) {
        return Array.get(array, index);
    }

    /**
     * 获得包中所有类，必须在类的搜索路径中
     * 
     * @param packageName
     *            包名称
     *            要查找的类名称
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static List<Class> getPackageClasses(String packageName) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        List<Class> li = new ArrayList<Class>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File[] file = new File(resource.getFile()).listFiles();
            for (File file2 : file) {
                String canonicalPath = file2.getCanonicalPath();
                if (canonicalPath.endsWith(".class")) {
                    String className = path + "/" + file2.getName();
                    className = className.split("\\.")[0].replace("/", ".");
                    Class<?> c = Class.forName(className);
                    Logger.getLogger(ReflectionUtils.class.getName()).info("load class:" + c);
                    li.add(c);
                }
            }
        }
        return li;
    }

}
