# spring-mvc-controllers


### Table Of Contents

#### [Requirements](#requirements-1)

##### [Annotation Driven Controller](#annotation-driven-controller-1)

##### [Custom Controller](#custom-controller-1)

##### [MultiActionController](#multiactioncontroller-1)

##### [ParameterizableViewController](#parameterizableviewcontroller-1)

##### [ServletForwardingController](#servletforwardingcontroller-1)

##### [ServletWrappingController](#servletwrappingcontroller-1)

##### [UrlFilenameViewController](#urlfilenameviewcontroller-1)


---


## Requirements

* Maven 3.3.x
* JDK 1.7+
* Web Container (Tomcat/Jetty/Others)


---


## Annotation Driven Controller

Annotation-driven controller are typically used in combination with annotated handler methods based on the [@RequestMapping](http://docs.spring.io/spring-framework/docs/4.3.5.RELEASE/javadoc-api/org/springframework/web/bind/annotation/RequestMapping.html) annotation.

Example:

GreetingController.java

```java
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
```

Bean declaration:

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
	http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context-4.3.xsd">

	<bean id="viewResolver"	class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<property name="prefix">
			<value>/WEB-INF/annotation-driven/</value>
		</property>
		<property name="suffix">
			<value>.jsp</value>
		</property>
	</bean>

	<context:component-scan	base-package="com.aviskar.sample.controller.annotation.driven" />
</beans>
```

Now, [GreetingController](/src/main/java/com/aviskar/sample/controller/annotation/driven/GreetingController.java) can serves at two different URLs, _[http://localhost:8080/spring-mvc-controllers/annotation-driven/greeting/morning.htm](http://localhost:8080/spring-mvc-controllers/annotation-driven/greeting/morning.htm)_ & _[http://localhost:8080/spring-mvc-controllers/annotation-driven/greeting/night.htm](http://localhost:8080/spring-mvc-controllers/annotation-driven/greeting/night.htm)_, performing different tasks, where *http://localhost:8080/spring-mvc-controllers/annotation-driven/* is entry point for DispatcherServlet.


## Custom Controller

The more simple way to write own controller is by extending [AbstractController](http://docs.spring.io/spring-framework/docs/4.3.5.RELEASE/javadoc-api/org/springframework/web/servlet/mvc/AbstractController.html) abstract class.

Example:

HelloController.java

```java
public class HelloController extends AbstractController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return new ModelAndView("page", "msg", "Hello World!!");
	}
}
```

Bean declaration:

```xml
<bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
	<property name="mappings">
		<props>
			<prop key="/">helloController</prop>
		</props>
	</property>
</bean>

<bean id="helloController" class="com.aviskar.sample.controller.custom.HelloController" />
```

Now, [HelloController](/src/main/java/com/aviskar/sample/controller/custom/HelloController.java) serves at _[http://localhost:8080/spring-mvc-controllers/custom/](http://localhost:8080/spring-mvc-controllers/custom/)_, where *http://localhost:8080/spring-mvc-controllers/custom/* is entry point for DispatcherServlet.


## MultiActionController

Deprecated, as of 4.3, in favor of annotation-driven controller.

[MultiActionController](http://docs.spring.io/spring-framework/docs/4.3.5.RELEASE/javadoc-api/org/springframework/web/servlet/mvc/multiaction/MultiActionController.html) is a implementation of [Controller](http://docs.spring.io/spring-framework/docs/4.3.5.RELEASE/javadoc-api/org/springframework/web/servlet/mvc/Controller.html) that allows multiple request types to be handled by the same class. Subclasses of this class can handle several different types of request with methods of the form:

```java
public (ModelAndView | Map | String | void) actionName(HttpServletRequest request, HttpServletResponse response, [,HttpSession] [,AnyObject]);
```
	
Example:

CustomerController.java

```java
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
```	

Bean declaration:

```xml
<bean class="org.springframework.web.servlet.mvc.support.ControllerClassNameHandlerMapping" />

<bean class="com.aviskar.sample.controller.multi.action.CustomerController" />
```

Now, _http://localhost:8080/spring-mvc-controllers/multi-action/customer/{name}\[.*]_ executes *{name}()* method of [CustomerController](/src/main/java/com/aviskar/sample/controller/multi/action/CustomerController.java), where *http://localhost:8080/spring-mvc-controllers/multi-action/* is entry point for DispatcherServlet.

E.g.:

* _[http://localhost:8080/spring-mvc-controllers/multi-action/customer/add](http://localhost:8080/spring-mvc-controllers/multi-action/customer/add) -> add() method_
* _[http://localhost:8080/spring-mvc-controllers/multi-action/customer/add.htm](http://localhost:8080/spring-mvc-controllers/multi-action/customer/add.htm) -> add() method_
* _[http://localhost:8080/spring-mvc-controllers/multi-action/customer/add.anything](http://localhost:8080/spring-mvc-controllers/multi-action/customer/add.anything) -> add() method_

The type of methodNameResolver determines the method to be executed. Default methodNameResolver is InternalPathMethodNameResolver with prefix as "" & suffix as "". There are three possible built-in candidates for methodNameResolver & one of them can be configured explicitly.

1. [InternalPathMethodNameResolver](#internalpathmethodnameresolver)
2. [PropertiesMethodNameResolver](#propertiesmethodnameresolver)
3. [ParameterMethodNameResolver](#parametermethodnameresolver)

#### InternalPathMethodNameResolver

```xml
<bean class="com.aviskar.sample.controller.multi.action.CustomerController">
	<property name="methodNameResolver">
		<bean class="org.springframework.web.servlet.mvc.multiaction.InternalPathMethodNameResolver">
			<property name="prefix" value="test" />
			<property name="suffix" value="customer" />
		</bean>
	</property>
</bean>
```

* _/customer/{name}[.*] –> test{name}customer() method_

#### PropertiesMethodNameResolver

```xml
<bean class="com.aviskar.sample.controller.multi.action.CustomerController">
	<property name="methodNameResolver">
		<bean class="org.springframework.web.servlet.mvc.multiaction.PropertiesMethodNameResolver">
			<property name="mappings">
				<props>
					<prop key="/customer/a.htm">add</prop>
					<prop key="/customer/b.htm">add</prop>
				</props>
			</property>
		</bean>
	</property>
</bean>
```

* _/customer/a.htm –> add() method_
* _/customer/b.htm –> add() method_

#### ParameterMethodNameResolver

```xml
<bean class="com.aviskar.sample.controller.multi.action.CustomerController">
	<property name="methodNameResolver">
		<bean class="org.springframework.web.servlet.mvc.multiaction.ParameterMethodNameResolver">
			<property name="paramName" value="action"/>
		</bean>
	</property>
</bean>
```

* _/customer/*.htm?action={name} –> {name}() method_


## ParameterizableViewController
[ParameterizableViewController](http://docs.spring.io/spring-framework/docs/4.3.5.RELEASE/javadoc-api/org/springframework/web/servlet/mvc/ParameterizableViewController.html) is a trivial controller that always returns a pre-configured view and optionally sets the response status code. The view and status can be configured using the provided configuration properties.

Example:

```xml
<bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
	<property name="mappings">
		<props>
			<prop key="/">parameterizableViewController</prop>
		</props>
	</property>
</bean>

<bean id="parameterizableViewController" class="org.springframework.web.servlet.mvc.ParameterizableViewController">
	<property name="viewName" value="static" />
</bean>
```
	
Now, *[http://localhost:8080/spring-mvc-controllers/parameterizable-view/](http://localhost:8080/spring-mvc-controllers/servlet-wrapping/)* returns *[/WEB-INF/parameterizable-view/static.jsp](/src/main/webapp/WEB-INF/parameterizable-view/static.jsp)* page, where *http://localhost:8080/spring-mvc-controllers/parameterizable-view/* is entry point for DispatcherServlet.	


## ServletForwardingController

[ServletForwardingController](http://docs.spring.io/spring-framework/docs/4.3.5.RELEASE/javadoc-api/org/springframework/web/servlet/mvc/ServletForwardingController.html) is a implementation of [Controller](http://docs.spring.io/spring-framework/docs/4.3.5.RELEASE/javadoc-api/org/springframework/web/servlet/mvc/Controller.html) that forwards to a named servlet, i.e. the "servlet-name" in web.xml rather than a URL path mapping. A target servlet doesn't even need a "servlet-mapping" in web.xml in the first place: A "servlet" declaration is sufficient.

It useful to invoke an existing servlet via Spring's dispatching infrastructure, for example to apply Spring HandlerInterceptors to its requests. This will work even in a minimal Servlet container that does not support Servlet filters.

Example:

Servlet declaration in web.xml.

```xml
<servlet>
	<servlet-name>hello-servlet</servlet-name>
	<servlet-class>com.aviskar.sample.controller.servlet.forwarding.HelloServlet</servlet-class>
</servlet>
```

Bean declaration.

```xml
<bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
	<property name="mappings">
		<props>
			<prop key="/">servletForwardingController</prop>
		</props>
	</property>
</bean>

<bean id="servletForwardingController" class="org.springframework.web.servlet.mvc.ServletForwardingController">
	<property name="servletName">
		<value>hello-servlet</value>
	</property>
</bean>
```
	
Now, *[http://localhost:8080/spring-mvc-controllers/servlet-forwarding/](http://localhost:8080/spring-mvc-controllers/servlet-forwarding/)* forwards request to [HelloServlet](/src/main/java/com/aviskar/sample/controller/servlet/forwarding/HelloServlet.java)'s instance, where *http://localhost:8080/spring-mvc-controllers/servlet-forwarding/* is entry point for DispatcherServlet.


## ServletWrappingController
[ServletWrappingController](http://docs.spring.io/spring-framework/docs/4.3.5.RELEASE/javadoc-api/org/springframework/web/servlet/mvc/ServletWrappingController.html) is a implementation of [Controller](http://docs.spring.io/spring-framework/docs/4.3.5.RELEASE/javadoc-api/org/springframework/web/servlet/mvc/Controller.html) that wraps a servlet instance which it manages internally. Such a wrapped servlet is not known outside of this controller.

It is useful to invoke an existing servlet via Spring's dispatching infrastructure, for example to apply Spring HandlerInterceptors to its requests.

Example:

```xml
<bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
	<property name="mappings">
		<props>
			<prop key="/">servletWrappingController</prop>
		</props>
	</property>
</bean>

<bean id="servletWrappingController" class="org.springframework.web.servlet.mvc.ServletWrappingController">
	<property name="servletClass">
		<value>
			com.aviskar.sample.controller.servlet.wrapping.WelcomeServlet
		</value>
	</property>
	<!-- Default servlet-name is the bean name of this controller. -->
	<!-- Servlet-name & initial parameters can also be configured. -->
</bean>
```
  
Now, *[http://localhost:8080/spring-mvc-controllers/servlet-wrapping/](http://localhost:8080/spring-mvc-controllers/servlet-wrapping/)* serves [WelcomeServlet](/src/main/java/com/aviskar/sample/controller/servlet/wrapping/WelcomeServlet.java)'s instance which is managed by Spring MVC, where *http://localhost:8080/spring-mvc-controllers/servlet-wrapping/* is entry point for DispatcherServlet.


## UrlFilenameViewController

[UrlFilenameViewController](http://docs.spring.io/spring-framework/docs/4.3.5.RELEASE/javadoc-api/org/springframework/web/servlet/mvc/UrlFilenameViewController.html) is a simple [Controller](http://docs.spring.io/spring-framework/docs/4.3.5.RELEASE/javadoc-api/org/springframework/web/servlet/mvc/Controller.html) implementation that transforms the virtual path of a URL into a view name and returns that view.

Example:

```xml
<bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
	<property name="mappings">
		<props>
			<prop key="/static">urlFilenameViewController</prop>
		</props>
	</property>
</bean>

<bean id="urlFilenameViewController" class="org.springframework.web.servlet.mvc.UrlFilenameViewController" />
```

Now, *[http://localhost:8080/spring-mvc-controllers/url-filename-view/static](http://localhost:8080/spring-mvc-controllers/url-filename-view/static)* returns *[/WEB-INF/url-filename-view/static.jsp](/src/main/webapp/WEB-INF/url-filename-view/static.jsp)* page.

In above example, *http://localhost:8080/spring-mvc-controllers/url-filename-view/* is entry point for DispatcherServlet & */static* is virtual path. And thus, the view with the name *"static"* is created & returned. And then, *viewResolver* resolves the returned view.

Similarly,

* *"/index" -> "index"*
* *"/index.html" -> "index"*
* *"/products/view.html" -> "products/view"*

Optionally *prefix* and/or *suffix* can be configured to build the view-name from the URL filename.

For example:

* *"/index.html" + prefix "pre_" and suffix "_suf" -> "pre_index_suf"*
