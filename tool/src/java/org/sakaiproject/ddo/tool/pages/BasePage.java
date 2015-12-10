package org.sakaiproject.ddo.tool.pages;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.head.StringHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import org.sakaiproject.ddo.logic.ProjectLogic;
import org.sakaiproject.ddo.logic.SakaiProxy;


/**
 * This is the base page for Digital Drop-Off. It sets up the containing markup and top navigation.
 * All top level pages should extend from this page so as to keep the same navigation. The content for those pages will
 * be rendered in the main area below the top nav.
 * 
 * <p>It also allows us to setup the API injection and any other common methods, which are then made available in the other pages.
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 * @author David P. Bauer (dbauer1@udayton.edu)
 *
 */
public class BasePage extends WebPage implements IHeaderContributor {

	private static final Logger log = Logger.getLogger(BasePage.class); 
	
	@SpringBean(name="org.sakaiproject.ddo.logic.SakaiProxy")
	protected SakaiProxy sakaiProxy;
	
	@SpringBean(name="org.sakaiproject.ddo.logic.ProjectLogic")
	protected ProjectLogic projectLogic;

	FeedbackPanel feedbackPanel;

	Link<Void> studentOverviewLink;
	Link<Void> staffOverviewLink;
	Link<Void> adminPageLink;
	
	public BasePage() {
		
		log.debug("BasePage()");

		WebMarkupContainer menu = new WebMarkupContainer("menu") {
			@Override
			public boolean isVisible() {
				return (sakaiProxy.isStudentWorker() || sakaiProxy.isDDOAdmin());
			}
		};

		//Student Overview link
		studentOverviewLink = new Link<Void>("studentOverviewLink") {
			private static final long serialVersionUID = 1L;
			public void onClick() {
				setResponsePage(new StudentOverview());
			}
		};
		studentOverviewLink.add(new Label("studentOverviewLinkLabel", new ResourceModel("link.studentoverview")).setRenderBodyOnly(true));
		studentOverviewLink.add(new AttributeModifier("title", new ResourceModel("link.studentoverview.tooltip")));
		menu.add(studentOverviewLink);

		//Staff Overview link
		staffOverviewLink = new Link<Void>("staffOverviewLink") {
			private static final long serialVersionUID = 1L;
			public void onClick() {
				setResponsePage(new StaffOverview());
			}
		};
		staffOverviewLink.add(new Label("staffOverviewLinkLabel", new ResourceModel("link.staffoverview")).setRenderBodyOnly(true));
		staffOverviewLink.add(new AttributeModifier("title", new ResourceModel("link.staffoverview.tooltip")));
		menu.add(staffOverviewLink);

		//Admin Page link
		adminPageLink = new Link<Void>("adminPageLink") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick() {
				setResponsePage(new AdminPage());
			}
			@Override
			public boolean isVisible() {
				return sakaiProxy.isDDOAdmin();
			}
		};
		adminPageLink.add(new Label("adminPageLinkLabel", new ResourceModel("link.adminpage")).setRenderBodyOnly(true));
		adminPageLink.add(new AttributeModifier("title", new ResourceModel("link.adminpage.tooltip")));
		menu.add(adminPageLink);

		add(menu);

		// Add a FeedbackPanel for displaying our messages
        feedbackPanel = new FeedbackPanel("feedback"){
        	
        	@Override
        	protected Component newMessageDisplayComponent(final String id, final FeedbackMessage message) {
        		final Component newMessageDisplayComponent = super.newMessageDisplayComponent(id, message);

        		if(message.getLevel() == FeedbackMessage.ERROR ||
        			message.getLevel() == FeedbackMessage.DEBUG ||
        			message.getLevel() == FeedbackMessage.FATAL ||
        			message.getLevel() == FeedbackMessage.WARNING){
        			add(AttributeModifier.replace("class", "alertMessage"));
        		} else if(message.getLevel() == FeedbackMessage.INFO){
        			add(AttributeModifier.replace("class", "success"));
        		} 

        		return newMessageDisplayComponent;
        	}
        };
		feedbackPanel.setOutputMarkupPlaceholderTag(true);
        add(feedbackPanel); 
		
    }
	
	/**
	 * Helper to clear the feedbackpanel display.
	 * @param f	FeedBackPanel
	 */
	public void clearFeedback(FeedbackPanel f) {
		if(!f.hasFeedbackMessage()) {
			f.add(AttributeModifier.replace("class", ""));
		}
	}

	/**
	 * This block adds the required wrapper markup to style it like a Sakai tool. 
	 * Add to this any additional CSS or JS references that you need.
	 * 
	 */
	public void renderHead(IHeaderResponse response) {
		//get the Sakai skin header fragment from the request attribute
		HttpServletRequest request = (HttpServletRequest)getRequest().getContainerRequest();
		
		response.render(StringHeaderItem.forString((String)request.getAttribute("sakai.html.head")));
		response.render(OnLoadHeaderItem.forScript("setMainFrameHeight( window.name )"));
		
		
		//Tool additions (at end so we can override if required)
		response.render(StringHeaderItem.forString("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"));
		//response.renderCSSReference("css/my_tool_styles.css");
		//response.renderJavascriptReference("js/my_tool_javascript.js");
	}
	
	
	/** 
	 * Helper to disable a link. Add the Sakai class 'current'.
	 */
	protected void disableLink(Link<Void> l) {
		l.add(new AttributeAppender("class", new Model<String>("current"), " "));
		l.setEnabled(false);
	}
	
	
	
}
