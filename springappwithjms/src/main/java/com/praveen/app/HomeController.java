package com.praveen.app;


import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@EnableJms
public class HomeController 
{
	@Autowired
	private JmsTemplate jmsTemplate;

	@Value("${springjms.myJmsReqQueue}")
	private String queue;

	@Value("${springjms.myJmsReplyQueue}")
	private String replyQueue;

	@RequestMapping(value = "home")
	public String homePage(HttpServletRequest request,HttpServletResponse response)
	{
		return "login.jsp";
	}

	@RequestMapping(value = "returnHome")
	public String returnHomePage(HttpServletRequest request,HttpServletResponse response) throws JMSException
	{
		getReply(jmsTemplate,replyQueue);
		return "login.jsp";
	}

	@RequestMapping(value = "login")
	public String login(HttpServletRequest request,HttpServletResponse response)
	{
		String user=request.getParameter("uname");
		String pwd=request.getParameter("pwd");
		if(user.equals("admin") && pwd.equals("admin"))
		{
			return "home.jsp";
		}
		return "fail.jsp";
	}

	@RequestMapping(value = "sendMsg")
	public String sendMsg(HttpServletRequest request,HttpServletResponse response)
	{
		try
		{
			String msg=request.getParameter("msg");
			String s=HomeController.sendMsgMethod(msg,queue,jmsTemplate,replyQueue);
			if(s.equals("true"))
			{
				return "check.jsp";
			}
			else
			{
				return "failure.jsp";
			}
		}
		catch (Exception e) 
		{
		}
		return "check.jsp";
	}

	private static String sendMsgMethod(String msg,String queueVal,JmsTemplate jmsTemplateVal,String replyQueue) 
	{
		System.out.println("inside sendMsgMethod methods ====> "+msg+" ====> "+queueVal);
		try
		{
			jmsTemplateVal.send(queueVal,new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException 
				{
					TextMessage createTextMessage = session.createTextMessage(msg);
					createTextMessage.setJMSReplyTo(session.createQueue(replyQueue));
					//createTextMessage.setJMSPriority(9);
					return createTextMessage;
				}
			});
			return "true";
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return "false";
		}
	}

	private static void getReply(JmsTemplate jmsTemplateVal,String replyQueue) throws JMSException 
	{
		System.out.println("calling get reply");
		TextMessage textMessage = (TextMessage) jmsTemplateVal.receive(replyQueue);
		System.out.println("msg received is ==> "+textMessage.getText());
	}

}
