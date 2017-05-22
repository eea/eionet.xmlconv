package eionet.gdem.services.projects.export;

import eionet.gdem.data.projects.Project;

import java.io.File;

/**
 *
 *
 */
public interface ProjectExporter {

    File export(Project project);
}
