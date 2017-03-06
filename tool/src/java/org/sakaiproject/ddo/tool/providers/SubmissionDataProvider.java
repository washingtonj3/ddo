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

package org.sakaiproject.ddo.tool.providers;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.ddo.logic.ProjectLogic;
import org.sakaiproject.ddo.model.Submission;
import org.sakaiproject.ddo.tool.models.DetachableSubmissionModel;
import org.sakaiproject.ddo.utils.SubmissionListType;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * DataProvider to manage our submission list
 *
 * @author David P. Bauer (dbauer1@udayton.edu)
 */
public class SubmissionDataProvider implements IDataProvider<Submission> {

    @SpringBean(name="org.sakaiproject.ddo.logic.ProjectLogic")
    protected ProjectLogic projectLogic;

    private List<Submission> list;

    private SubmissionListType listType;

    public SubmissionDataProvider(SubmissionListType listType) {
        Injector.get().inject(this);

        this.listType = listType;
    }

    private List<Submission> getData() {
        if(this.list == null) {
            switch (this.listType) {

                case FOR_CURRENT_USER:
                    this.list = projectLogic.getSubmissionsForCurrentUser();
                    break;

                case ALL_ARCHIVED:
                    this.list = projectLogic.getAllArchivedSubmissions();
                    break;

                case ALL_AWAITING:
                    this.list = projectLogic.getAllWaitingSubmissions();
                    break;

                case ALL_REVIEWED:
                    this.list = projectLogic.getAllReviewedSubmissions();
                    break;
            }

            Collections.sort(this.list, Submission.submissionComparator);

            // If we are getting submissions that are awaiting review
            // we should reverse the list so the oldest submissions are
            // at the top.
            if (this.listType == SubmissionListType.ALL_AWAITING) {
                Collections.reverse(this.list);
            }
        }
        return this.list;
    }


    @Override
    public Iterator<Submission> iterator(long first, long count){
        int f = (int) first; //not ideal but ok for demo
        int c = (int) count; //not ideal but ok for demo
        return getData().subList(f, f + c).iterator();
    }

    @Override
    public long size(){
        return getData().size();
    }

    @Override
    public IModel<Submission> model(Submission object){
        return new DetachableSubmissionModel(object);
    }

    @Override
    public void detach(){
        this.list = null;
    }
}
