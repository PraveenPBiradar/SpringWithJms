package com.praveen.app;

import java.io.IOException;
import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.JmsMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@EnableJms
public class RecieverController {

	@Autowired
	private JmsTemplate jmsTemplate;

	@Value("${springjms.myJmsReqQueue}")
	private String queue;

	@RequestMapping(value = "receiveMsg")
	public String getMsg(String message,HttpServletRequest request,HttpServletResponse response,ModelMap modelMap) throws NamingException, JMSException, IOException, InterruptedException 
	{
		System.out.println("queueu is "+queue);
		final TextMessage textMessage = (TextMessage) jmsTemplate.receive(queue);
		System.out.println(textMessage.getText());
		modelMap.addAttribute("message", textMessage.getBody(String.class));
		jmsTemplate.send(textMessage.getJMSReplyTo(), new MessageCreator() 
		{
			@Override
			public Message createMessage(Session session) throws JMSException 
			{
				TextMessage createTextMessage = session.createTextMessage("received the msg thank you");
				createTextMessage.setJMSCorrelationID(textMessage.getJMSMessageID());
				return createTextMessage;
			}
		});;
		System.out.println("sent bck reply");
		return "final.jsp";
	}

	//jms listener code
//	@JmsListener(destination = "${springjms.myAppTopic3}")
//	private void getMsg1(String msg) throws JMSException 
//	{
//		System.out.println("recived in second ===> "+msg);
//	}
}
