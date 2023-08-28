package com.bookmyshow.hystrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@SpringBootApplication
@RestController
@EnableHystrix
public class EnableHystrixBookMyShowServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnableHystrixBookMyShowServiceApplication.class, args);
	}
	
	
	
	
	@Autowired(required=true)
	@Lazy // lazy is used to resolve circular dependecny issue
	private RestTemplate template;

	@GetMapping("/bookNow")
	@HystrixCommand(groupKey="hystrics",commandKey="hystrics",fallbackMethod="bookMyShowFallBack")
	public String bookshow()
	{
		String emailServiceResponse=template.getForObject("http://localhost:7070/email", String.class);
		String paymentServiceResponse=template.getForObject("http://localhost:7071/paytm", String.class);
		
		return emailServiceResponse + " \n" + paymentServiceResponse;
	}
	
	//make sure to ensure that both emailservice and paytmservice are running
	@GetMapping("/bookNowWithOutHystricx")
	public String bookshowWithOutHystrics()
	{
		String emailServiceResponse=template.getForObject("http://localhost:7070/email", String.class);
		String paymentServiceResponse=template.getForObject("http://localhost:7071/paytm", String.class);
		
		
		return emailServiceResponse + " \n" + paymentServiceResponse;
	}
	
	public String bookMyShowFallBack()
	{
		return "Sever is slow please try later";
	}
	
	@Bean
	public RestTemplate template()
	{
		return new RestTemplate();
	}

}
