/**
 * 
 */
package com.wy.okooo.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author leslie
 *
 */
public class FileNameSelector implements FilenameFilter{
	 String prefix = "";
	 String post = "";
	 
	 public FileNameSelector(String fileNamePrefix, String fileNamePost)
	 {
		 prefix += fileNamePrefix;
		 post += fileNamePost;
	 }
	 
	public boolean accept(File dir, String name) {
		return name.startsWith(prefix) && name.endsWith(post);
	}
	 
}
