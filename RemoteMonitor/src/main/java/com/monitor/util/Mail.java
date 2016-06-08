package com.monitor.util;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail {

	private static String username = "devaraj1992@gmail.com";
	private static String password = "falcon1994";
	
	private static String buildMailMessageLamp(ArrayList<String> messageList){
		StringBuilder mailMessage  = new StringBuilder();
	    mailMessage.append("Hello Technician/Admin,");
	    mailMessage.append("\n");
	    mailMessage.append("\n");
	    mailMessage.append("The Following street lamps need to be replaced/repaired");
	    mailMessage.append("\n");
	    mailMessage.append("\n");
		for(String message:messageList){
			String[] splittedString = message.split("+");
			mailMessage.append("Address: "+splittedString[0]);
			mailMessage.append("  ");
			mailMessage.append("Pincode: "+splittedString[1]);
			mailMessage.append("  ");
			mailMessage.append("LampId:  "+splittedString[2]);
			mailMessage.append("\n");
		}
		return mailMessage.toString();
	}
	
	private static String buildMailMessageLoad(String message){
		StringBuilder mailMessage = new StringBuilder();
	    mailMessage.append("Hello Technician/Admin,");
	    mailMessage.append("\n");
	    mailMessage.append("\n");
	    mailMessage.append("The Following Transformer Experienced Heavy Load Recently:");
	    mailMessage.append("\n");
	    mailMessage.append("\n");
		String[] splittedString = message.split("+");
		mailMessage.append("Address: "+splittedString[0]);
		mailMessage.append("  ");
		mailMessage.append("Pincode: "+splittedString[1]);
		mailMessage.append("  ");
		mailMessage.append("TransfomerId:  "+splittedString[2]);
		mailMessage.append("  ");
		mailMessage.append("Value:  "+splittedString[3]);
		mailMessage.append("\n");
		return mailMessage.toString();
	}
	
	public static void sendMailLamp(ArrayList<String> messageList){
		sendMail(buildMailMessageLamp(messageList));
	}
	
	public static void sendMailLoad(String message){
		sendMail(buildMailMessageLoad(message));
	}
	
	private static void sendMail(String mailMessage) {
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("devaraj1992@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("devaraj1992@gmail.com"));
			message.setSubject("Remote Monitor-Alert");
			message.setText(mailMessage.toString());

			Transport.send(message);
			
			System.out.println("Following Mail Message is sent:");
			System.out.println(message);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

}
