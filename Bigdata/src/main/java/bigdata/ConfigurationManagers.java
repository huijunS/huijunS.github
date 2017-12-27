package bigdata;


import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManagers {

    private static Properties prop = new Properties();

static {
        try {
        //操作系统类型
        String osName = System.getProperty("os.name");
        //配置文件目录
        String proFilePath = "huijun.properties";
        InputStream in = ConfigurationManagers.class
                    .getClassLoader().getResourceAsStream(proFilePath);

                            prop.load(in);

                            } catch (Exception e) {
                            e.printStackTrace();
                            }
                            }
public static String getProperty(String key) {
        return prop.getProperty(key);
        }
        }
