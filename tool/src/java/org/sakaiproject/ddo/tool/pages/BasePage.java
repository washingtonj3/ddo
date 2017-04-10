/*
 *  Copyright (c) 2016, University of Dayton
 *
 *  Licensed under the Educational Community License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *              http://opensource.org/licenses/ecl2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.sakaiproject.ddo.tool.pages;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import org.sakaiproject.component.cover.ServerConfigurationService;
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
	Link<Void> archivePageLink;
	Link<Void> statisticsPageLink;
	
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

		//Archive Page link
		archivePageLink = new Link<Void>("archivePageLink") {
			private static final long serialVersionUID = 1L;
			public void onClick() {
				setResponsePage(new ArchivePage());
			}
		};
		archivePageLink.add(new Label("archivePageLinkLabel", new ResourceModel("link.archivepage")).setRenderBodyOnly(true));
		archivePageLink.add(new AttributeModifier("title", new ResourceModel("link.archivepage.tooltip")));
		menu.add(archivePageLink);

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

		//Statistics Page Link
		statisticsPageLink = new Link<Void>("statsPageLink") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick() {
				setResponsePage(new StatisticsPage());
			}
			@Override
			public boolean isVisible() {
				return sakaiProxy.isDDOAdmin();
			}
		};
		statisticsPageLink.add(new Label("statisticsPageLinkLabel", new ResourceModel("link.statspage")).setRenderBodyOnly(true));
		statisticsPageLink.add(new AttributeModifier("title", new ResourceModel("link.statspage.tooltip")));
		menu.add(statisticsPageLink);

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
		super.renderHead(response);

		final String version = ServerConfigurationService.getString("portal.cdn.version", "");

		// get the Sakai skin header fragment from the request attribute
		final HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();

		response.render(new PriorityHeaderItem(JavaScriptHeaderItem
				.forReference(getApplication().getJavaScriptLibrarySettings().getJQueryReference())));

		response.render(StringHeaderItem.forString((String) request.getAttribute("sakai.html.head")));
		response.render(OnLoadHeaderItem.forScript("setMainFrameHeight( window.name )"));

		// Tool additions (at end so we can override if required)
		response.render(StringHeaderItem
				.forString("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"));

		// jQueryUI
		response.render(JavaScriptHeaderItem
				.forUrl(String.format("/library/webjars/jquery-ui/1.11.3/jquery-ui.min.js?version=%s", version)));

		// Include Sakai Date Picker
		response.render(JavaScriptHeaderItem
				.forUrl(String.format("/library/js/lang-datepicker/lang-datepicker.js?version=%s", version)));

		// tablesorter
		response.render(CssHeaderItem
				.forUrl(String.format("/library/js/jquery/tablesorter/2.1.17/css/theme.bootstrap.css?version=%s", version)));
		response.render(JavaScriptHeaderItem
				.forUrl(String.format("/library/js/jquery/tablesorter/2.1.17/jquery.tablesorter.min.js?version=%s", version)));
		response.render(JavaScriptHeaderItem
				.forUrl(String.format("/library/js/jquery/tablesorter/2.1.17/jquery.tablesorter.widgets.min.js?version=%s", version)));

		// DDO specific styles and behaviour
		response.render(CssHeaderItem
				.forUrl(String.format("/ddo-tool/styles/ddo-shared.css?version=%s", version)));
		response.render(JavaScriptHeaderItem
				.forUrl(String.format("/ddo-tool/scripts/ddo.js?version=%s", version)));
	}
	
	
	/** 
	 * Helper to disable a link. Add the Sakai class 'current'.
	 */
	protected void disableLink(Link<Void> l) {
		l.add(new AttributeAppender("class", new Model<String>("current"), " "));
		l.setEnabled(false);
	}
	
	
	
}
