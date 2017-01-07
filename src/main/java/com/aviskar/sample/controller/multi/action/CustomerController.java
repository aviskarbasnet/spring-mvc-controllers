package com.aviskar.sample.controller.multi.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

@SuppressWarnings("deprecation")
public class CustomerController extends MultiActionController {

	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("page", "msg", "add() method");
	}

	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("page", "msg", "delete() method");
	}

	public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("page", "msg", "update() method");
	}

	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("page", "msg", "list() method");
	}
}
