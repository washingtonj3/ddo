package org.sakaiproject.ddo.tool.pages;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.sakaiproject.ddo.logic.SakaiProxy;

import java.util.ArrayList;
import java.util.List;

public class AdminPage extends BasePage {

    public AdminPage() {
        disableLink(adminPageLink);

        List<String> ddoAdminIds = new ArrayList<String>(sakaiProxy.getDDOAdminIds());

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
                String userId = (String) item.getModelObject();
                item.add(new Label("staff-name", sakaiProxy.getUserDisplayName(userId)));
                item.add(new Label("staff-username", sakaiProxy.getUserDisplayId(userId)));
            }
        };

        staffContainer.add(staffDataView);

        add(new ListView<String>("ddoAdmins", ddoAdminIds) {
            @Override
            protected void populateItem(ListItem<String> listItem) {
                String userId = listItem.getModelObject();
                listItem.add(new Label("admin-name", sakaiProxy.getUserDisplayName(userId)));
                listItem.add(new Label("admin-username", sakaiProxy.getUserDisplayId(userId)));
            }
        });

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
                boolean success = sakaiProxy.addUserToDDO(userId, SakaiProxy.DDO_STAFF_ROLE);
                if(success) {
                    success("User added to DDO Staff.");
                } else {
                    error("Failed to add user to DDO Staff.");
                }
                target.add(feedbackPanel);
                //refresh list
                target.add(staffContainer);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        add(addStaffForm);

    }
}

