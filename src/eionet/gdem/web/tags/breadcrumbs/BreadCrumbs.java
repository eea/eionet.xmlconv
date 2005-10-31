/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is Web Dashboards Service
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 * 
 * Contributors(s):
 *    Original code: Dusan Popovic (ED) 
 */

package eionet.gdem.web.tags.breadcrumbs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BreadCrumbs {

	private List trail = new ArrayList();

	public BreadCrumbs() {
	}

	public void addToTrail(String referer, BreadCrumb bc, int level) {
		if (level == 0 &&  trail.size() > 0)
			return;
		
		if (level > 0 && level < trail.size()) {
			BreadCrumb tmp = (BreadCrumb) trail.get(level);
			trail = trail.subList(0, level);
		}
		trail.add(bc);
	}

	public Iterator iterateTrail() {
		return trail.iterator();
	}

}
