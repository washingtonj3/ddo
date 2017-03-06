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

package org.sakaiproject.ddo.tool.models;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.ddo.logic.ProjectLogic;
import org.sakaiproject.ddo.model.Submission;

/**
 * @author David P. Bauer (dbauer1@udayton.edu)
 */
public class DetachableSubmissionModel extends LoadableDetachableModel<Submission> {

    @SpringBean(name="org.sakaiproject.ddo.logic.ProjectLogic")
    protected ProjectLogic projectLogic;

    private final long id;

    /**
     * @param s
     */
    public DetachableSubmissionModel(Submission s) {
        Injector.get().inject(this);

        this.id = s.getSubmissionId();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    /**
     * used for dataview with ReuseIfModelsEqualStrategy item reuse strategy
     *
     * @see org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj instanceof DetachableSubmissionModel) {
            DetachableSubmissionModel other = (DetachableSubmissionModel) obj;
            return other.id == id;
        }
        return false;
    }

    /**
     * @see org.apache.wicket.model.LoadableDetachableModel#load()
     */
    protected Submission load() {

        // get the submission
        return projectLogic.getSubmission(id);
    }
}