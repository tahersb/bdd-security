/*******************************************************************************
 *    BDD-Security, application security testing framework
 * 
 * Copyright (C) `2012 Stephen de Vries`
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see `<http://www.gnu.org/licenses/>`.
 ******************************************************************************/
package net.continuumsecurity.runner;

import java.util.List;

import net.continuumsecurity.web.steps.AutomatedScanningSteps;
import net.continuumsecurity.web.steps.WebApplicationSteps;

import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;

public class ConfigurationStoryRunner extends BaseStoryRunner {
	List<String> filters;
	boolean automatedScan;
	
	public ConfigurationStoryRunner(List<String> filters,boolean automatedScan) {
		this.filters = filters;
		this.automatedScan = automatedScan;
		configuredEmbedder().useMetaFilters(filters);
		configuredEmbedder().embedderControls().doIgnoreFailureInStories(false);
	}
	
	@Override
	public List<String> storyPaths() {
		configuredEmbedder().useMetaFilters(filters);
		return new StoryFinder().findPaths(CodeLocations.codeLocationFromURL(storyUrl), "**/configuration_story.story","");
	}

	@Override
	public InjectableStepsFactory stepsFactory() {
		WebApplicationSteps ws = new WebApplicationSteps();
		if (automatedScan) {
			return new InstanceStepsFactory(configuration(),
				ws,
				new AutomatedScanningSteps());
		} else {
			return new InstanceStepsFactory(configuration(),
					ws);
		}
			
	}
	
}
