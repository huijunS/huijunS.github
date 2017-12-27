package SparkTest;


import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.net.SMTPAppender;

public class TestLog4jSendMail {
    public static Logger logger = Logger.getLogger(TestLog4jSendMail.class);

    SMTPAppender appender = new SMTPAppender();

    public TestLog4jSendMail() {
        try {
            appender.setSMTPUsername("15821280021@163.com");
            appender.setSMTPPassword("shjshj123");
            appender.setTo("15821280021@163.com");
            appender.setFrom("15821280021@163.com");
            // SMTP服务器 smtp.163.com
            appender.setSMTPHost("smtp.163.com");
            appender.setLocationInfo(true);
            appender.setSubject("Test Mail From Log4J");
            appender.setLayout(new PatternLayout());
            appender.activateOptions();
            logger.addAppender(appender);
            logger.error("Hello World");
            System.out.println("发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Printing ERROR Statements", e);
        }
    }

    public static void main(String args[]) {
        new TestLog4jSendMail();
    }

}
