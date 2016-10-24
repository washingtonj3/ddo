package org.sakaiproject.ddo.tool.pages;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.sakaiproject.ddo.logic.SakaiProxy;
import org.sakaiproject.ddo.tool.beans.AddBean;
import org.sakaiproject.ddo.tool.beans.ClosedBean;
import org.sakaiproject.ddo.tool.beans.RemoveBean;
import org.sakaiproject.ddo.utils.DDOConstants;

import java.util.ArrayList;
import java.util.List;

public class AdminPage extends BasePage {

    public AdminPage() {
        disableLink(adminPageLink);

        final WebMarkupContainer staffContainer = new WebMarkupContainer("staffContainer");
        staffContainer.setOutputMarkupId(true);
        add(staffContainer);

        ListDataProvider<String> studentWorkerList = new ListDataProvider<String>() {
            @Override
            protected List<String> getData() {
                return new ArrayList<String>(sakaiProxy.getStudentWorkerIds());
            }
        };
        DataView staffDataView = new DataView<String>("ddoStaff", studentWorkerList) {
            @Override
            protected void populateItem(Item item) {
                final String userId = (String) item.getModelObject();
                item.add(new Label("staff-name", sakaiProxy.getUserSortName(userId)));
                item.add(new Label("staff-username", sakaiProxy.getUserDisplayId(userId)));
                item.add(new TextField<String>("staff-userId", Model.of(userId)));
            }
        };

        staffContainer.add(staffDataView);

        final AddBean addStaffBean = new AddBean();

        final Form<AddBean> addStaffForm = new Form<AddBean>("addStaffForm", new CompoundPropertyModel<AddBean>(addStaffBean));

        FormComponent formComponent;

        formComponent = new RequiredTextField<String>("userName");
        formComponent.setLabel(new ResourceModel("form.addstaff"));

        addStaffForm.add(formComponent);
        addStaffForm.add(new SimpleFormComponentLabel("add-staff-label", formComponent));

        addStaffForm.add(new AjaxButton("ajax-add-staff", addStaffForm) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                String userId = sakaiProxy.getUserIdForEid(addStaffBean.getUserName());
                boolean success = sakaiProxy.addUserToDDO(userId, DDOConstants.DDO_STAFF_ROLE);
                if(success) {
                    info("User " + addStaffBean.getUserName() + " added to DDO Staff.");
                    //refresh list
                    target.add(staffContainer);
                } else {
                    error("Failed to add user " + addStaffBean.getUserName() + " to DDO Staff.");
                }
                target.add(feedbackPanel);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        add(addStaffForm);

        final RemoveBean removeStaffBean = new RemoveBean();
        final Form<RemoveBean> removeStaffForm = new Form<RemoveBean>("removeStaffForm", new CompoundPropertyModel<RemoveBean>(removeStaffBean));

        FormComponent fc;

        fc = new RequiredTextField<String>("userId");

        removeStaffForm.add(fc);

        removeStaffForm.add(new AjaxButton("ajax-remove-staff", removeStaffForm) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                boolean success = sakaiProxy.removeUserFromDDO(removeStaffBean.getUserId());
                if(success) {
                    info("User " + sakaiProxy.getUserDisplayId(removeStaffBean.getUserId()) + " removed from DDO Staff.");
                    //refresh list
                    target.add(staffContainer);
                } else {
                    error("Failed to remove user " + sakaiProxy.getUserDisplayId(removeStaffBean.getUserId()) + " from DDO Staff.");
                }
                target.add(feedbackPanel);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        add(removeStaffForm);

        Link<Void> refreshPage = new Link<Void>("refreshPage") {
            public void onClick() {
                setResponsePage(new AdminPage());
            }
        };
        add(refreshPage);

        final WebMarkupContainer closedContainer = new WebMarkupContainer("closedContainer");
        closedContainer.setOutputMarkupId(true);
        add(closedContainer);

        final ClosedBean closedBean = new ClosedBean();
        final Form<ClosedBean> closedForm = new Form<>("closedForm", new CompoundPropertyModel<>(closedBean));

        FormComponent closedValue = new CheckBox("closed");
        FormComponent closedMessage = new TextArea<String>("message");

        closedForm.add(closedValue);
        closedForm.add(closedMessage);

        closedForm.add(new AjaxButton("ajax-close-ddo", closedForm) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                boolean closeDDO = closedBean.isClosed();
                sakaiProxy.setDDORealmProperty(DDOConstants.PROP_CLOSED, closeDDO);
                sakaiProxy.setDDORealmProperty(DDOConstants.PROP_CLOSED_MESSAGE, closedBean.getMessage());

                info(closeDDO ? getString("ddo.closed.true") : getString("ddo.closed.false"));
                target.add(closedContainer);
                target.add(feedbackPanel);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        closedForm.add(new Label("closed-section-label", getString("ddo.closed.header")));

        closedContainer.add(closedForm);

        add(new Label("closedSectionHeader", getString("ddo.closed.section.header")));
    }
}

