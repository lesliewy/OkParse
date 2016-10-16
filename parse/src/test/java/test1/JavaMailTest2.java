/**
 * 
 */
package test1;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author leslie
 *
 */
public class JavaMailTest2 {
    public static void main(String[] args) throws MessagingException {  
    	String host = "smtp.gmail.com";
    	String from = "leslie.wy87@gmail.com";
    	String pass = "jslygpmnw";
    	Properties props = System.getProperties();
    	props.put("mail.smtp.starttls.enable", "true"); // 在本行添加
    	props.put("mail.smtp.host", host);
    	props.put("mail.smtp.user", from);
    	props.put("mail.smtp.password", pass);
    	props.put("mail.smtp.port", "587");
    	props.put("mail.smtp.auth", "true");
    	String[] to = {"leslie_wangyang@163.com"}; // 在本行添加
    	Session session = Session.getDefaultInstance(props, null);
    	MimeMessage message = new MimeMessage(session);
    	message.setFrom(new InternetAddress(from));
    	InternetAddress[] toAddress = new InternetAddress[to.length];
    	// 获取地址的array
    	for( int i=0; i < to.length; i++ ) { // 从while循环更改而成
    	toAddress[i] = new InternetAddress(to[i]);
    	}
    	System.out.println(Message.RecipientType.TO);
    	for( int i=0; i < toAddress.length; i++) { // 从while循环更改而成
    	message.addRecipient(Message.RecipientType.TO, toAddress[i]);
    	}
    	message.setSubject("sending in a group");
    	message.setText("Welcome to JavaMail");
    	Transport transport = session.getTransport("smtp");
    	transport.connect(host, from, pass);
    	transport.sendMessage(message, message.getAllRecipients());
    	transport.close(); 
    }
}
