/**
 * 
 */
package com.wy.okooo.util;

import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 邮件 Utils
 * 
 * @author leslie
 * 
 */
public class MailUtils {
	private static final String FROM_MAIL = "leslie_wangyang@163.com";

	private static final String ADDRESSEE = "leslie.wy87@gmail.com";

	private static final String FROM_MAIL_USERNAME = "leslie_wangyang";

	private static final String FROM_MAIL_PASSWORD = "Lesliewy1987";

	public static void sendMail(String content, String jobType)
			throws MessagingException {
		Properties props = new Properties();
		// 开启debug调试
		props.setProperty("mail.debug", "true");
		// 发送服务器需要身份验证
		props.setProperty("mail.smtp.auth", "true");
		// 设置邮件服务器主机名
		props.setProperty("mail.host", "smtp.163.com");
		// 发送邮件协议名称
		props.setProperty("mail.transport.protocol", "smtp");

		// 设置环境信息
		Session session = Session.getInstance(props);

		// 创建邮件对象
		Message msg = new MimeMessage(session);
		msg.setSubject("Okooo lottery - " + jobType);
		// 设置邮件内容
		msg.setText(content);
		// 设置发件人
		msg.setFrom(new InternetAddress(FROM_MAIL));

		Transport transport = session.getTransport();
		// 连接邮件服务器
		transport.connect(FROM_MAIL_USERNAME, FROM_MAIL_PASSWORD);
		// 发送邮件
		transport.sendMessage(msg, new Address[] { new InternetAddress(
				ADDRESSEE) });
		// 关闭连接
		transport.close();
	}

	public static void sendMailTo163(String content, Map<String, String> map)
			throws MessagingException {
		Properties props = System.getProperties();
		
//		String host = "smtp.gmail.com";
//		String from = "leslie.wy87@gmail.com";
//		String pass = "jslygpmnw";
//		props.put("mail.smtp.port", "587");
		
//		String host = "smtp.qq.com";
//		String from = "498865821@qq.com";
//		String pass = "JSLYGPMNW147258";
//		props.put("mail.smtp.port", "25");
//		props.setProperty("mail.transport.protocol", "smtp");
		
		String host = "smtp.163.com";
		String from = "leslie_wangyang@163.com";
		String pass = "Lesliewy1987";
		
		props.put("mail.smtp.starttls.enable", "true"); // 在本行添加
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", from);
		props.put("mail.smtp.password", pass);
		props.put("mail.smtp.auth", "true");
		
		String[] to = { "leslie_wangyang@163.com" }; // 在本行添加
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		InternetAddress[] toAddress = new InternetAddress[to.length];
		// 获取地址的array
		for (int i = 0; i < to.length; i++) { // 从while循环更改而成
			toAddress[i] = new InternetAddress(to[i]);
		}
		for (int i = 0; i < toAddress.length; i++) { // 从while循环更改而成
			message.addRecipient(Message.RecipientType.TO, toAddress[i]);
		}
		String subject = "Okooo - " + map.get("okUrlDate") + " " + map.get("jobType") + " " + map.get("beginMatchSeq") + "-" + 
				map.get("endMatchSeq");
		message.setSubject(subject);
		message.setText(content);
		Transport transport = session.getTransport("smtp");
		transport.connect(host, from, pass);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}
}
