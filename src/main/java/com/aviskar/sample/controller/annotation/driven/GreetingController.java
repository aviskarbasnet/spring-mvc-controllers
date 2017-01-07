package com.aviskar.sample.controller.annotation.driven;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GreetingController {

	@RequestMapping("/greeting/morning.htm")
	public ModelAndView goodMorning(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("page", "msg", "Good Morning!!");
	}

	@RequestMapping("/greeting/night.htm")
	public ModelAndView goodNight(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("page", "msg", "Good Night!!");
	}
}
