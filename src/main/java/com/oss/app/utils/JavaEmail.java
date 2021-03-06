package com.oss.app.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang.StringUtils;

public class JavaEmail {

	private MimeMessage message;
    private Session session;
    private Transport transport;

    private String mailHost = "";
    private String sender_username = "";
    private String sender_password = "";
    private String mail_receiver = "";//接收者
    private String mail_cc = "";//抄送给

    private Properties properties = new Properties();
    
    public JavaEmail(boolean debug){
    	//new FileInputStream("conf/mailserver.properties");
   	 try {
   		 	InputStream in = new FileInputStream("conf/mailserver.properties");//JavaEmail.class.getResourceAsStream("mailserver.properties");
			properties.load(in);
			this.mailHost = properties.getProperty("mail.smtp.host");
			this.sender_username = properties.getProperty("mail.sender.username");
			this.sender_password = properties.getProperty("mail.sender.password");
			this.mail_receiver = properties.getProperty("mail.receiver");
			this.mail_cc = properties.getProperty("mail.cc");
   	 } catch (IOException e) {
   		 e.printStackTrace();
   	 }
   	 	session = Session.getInstance(properties);
        session.setDebug(debug);//开启后有调试信息
        message = new MimeMessage(session);
   }
    //发送给多人
    public void doSendHtmlEmailToMultiplayer(String subject, String sendHtml){
    	String[] receiver = this.mail_receiver.split(",");
 	   for (String re : receiver) {
 		  doSendHtmlEmail(subject, sendHtml, re);
 	   }
    }
    
   public void doSendHtmlEmail(String subject, String sendHtml,String receiver){
   	   try {
              // 发件人
              //InternetAddress from = new InternetAddress(sender_username);
              // 下面这个是设置发送人的Nick name
              InternetAddress from = new InternetAddress(MimeUtility.encodeWord("幻影")+" <"+sender_username+">");
              message.setFrom(from);
              
              // 收件人
              InternetAddress to = new InternetAddress(receiver);
              message.setRecipient(Message.RecipientType.TO, to);//还可以有CC、BCC
              //设置抄送人
              if(StringUtils.isNotBlank(mail_cc)){
            	  String[] cc = mail_cc.split(",");
            	  InternetAddress[] addresses = new InternetAddress[cc.length];
            	  for (int i = 0; i < cc.length; i++) {
            		  addresses[i] = new InternetAddress(cc[i]);
				}
            	 message.setRecipients(Message.RecipientType.CC, addresses);
              }
              
              // 邮件主题
              message.setSubject(subject);
              
              String content = sendHtml.toString();
              // 邮件内容,也可以使纯文本"text/plain"
              message.setContent(content, "text/html;charset=UTF-8");
              
              // 保存邮件
              message.saveChanges();
              
              transport = session.getTransport("smtp");
              // smtp验证，就是你用来发邮件的邮箱用户名密码
              transport.connect(mailHost, sender_username, sender_password);
              // 发送
              transport.sendMessage(message, message.getAllRecipients());
              //System.out.println("send success!");
          } catch (Exception e) {
              e.printStackTrace();
          }finally {
              if(transport!=null){
                  try {
                      transport.close();
                  } catch (MessagingException e) {
                      e.printStackTrace();
                  }
              }
          }
   }
   
 //发送给多人
   public void doSendHtmlEmailWithAttachmentToMultiplayer(String subject,String sendHtml, File attachment){
	   String[] receiver = this.mail_receiver.split(",");
	   for (String re : receiver) {
		doSendHtmlEmailWithAttachment(subject, sendHtml, attachment, re);
	   }
   }
   
   public void doSendHtmlEmailWithAttachment(String subject, String sendHtml, File attachment,String receiver) {
   	 try {
            // 发件人
            InternetAddress from = new InternetAddress(sender_username);
            message.setFrom(from);

            // 收件人
            InternetAddress to = new InternetAddress(receiver);
            message.setRecipient(Message.RecipientType.TO, to);
            //设置抄送人
            if(StringUtils.isNotBlank(mail_cc)){
          	  String[] cc = mail_cc.split(",");
          	  InternetAddress[] addresses = new InternetAddress[cc.length];
          	  for (int i = 0; i < cc.length; i++) {
          		  addresses[i] = new InternetAddress(cc[i]);
				}
          	 message.setRecipients(Message.RecipientType.CC, addresses);
            }

            // 邮件主题
            message.setSubject(subject);

            // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();
            
            // 添加邮件正文
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(sendHtml, "text/html;charset=UTF-8");
            multipart.addBodyPart(contentPart);
            
            // 添加附件的内容
            if (attachment != null) {
                BodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachment);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                
                // 网上流传的解决文件名乱码的方法，其实用MimeUtility.encodeWord就可以很方便的搞定
                // 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
                //sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
                //messageBodyPart.setFileName("=?GBK?B?" + enc.encode(attachment.getName().getBytes()) + "?=");
                
                //MimeUtility.encodeWord可以避免文件名乱码
                attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment.getName()));
                multipart.addBodyPart(attachmentBodyPart);
            }
            
            // 将multipart对象放到message中
            message.setContent(multipart);
            // 保存邮件
            message.saveChanges();

            transport = session.getTransport("smtp");
            // smtp验证，就是你用来发邮件的邮箱用户名密码
            transport.connect(mailHost, sender_username, sender_password);
            // 发送
            transport.sendMessage(message, message.getAllRecipients());

            System.out.println("send success!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
   }
}
