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

package org.sakaiproject.ddo.tool.beans;

import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.util.io.IClusterable;

/**
 * @author David P. Bauer (dbauer1@udayton.edu)
 */
public class AddBean implements IClusterable {
    @Getter
    @Setter
    private String userName;
}
