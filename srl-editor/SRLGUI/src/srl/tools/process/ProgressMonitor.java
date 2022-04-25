/*
 * Copyright (c) 2008, National Institute of Informatics
 *
 * This file is part of SRL, and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June 1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://www.fsf.org/licensing/licenses/info/GPLv2.html.
 */
package srl.tools.process;

/**
 * Monitor the progress of some process
 * @author John McCrae, National Institute of Informatics
 */
public interface ProgressMonitor {
    /** A message about the current operation of the process */
    public void setMessageVal(String s);
    /** The processes progress (between 0 and 1) */
    public void setProgressVal(float f);
}
