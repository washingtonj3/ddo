package org.sakaiproject.ddo.tool.pages;

import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.util.io.IClusterable;

public class RemoveBean implements IClusterable {
    @Getter
    @Setter
    private String userId;
}