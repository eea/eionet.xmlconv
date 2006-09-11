/**
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
 * The Original Code is "GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA).
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 * Created on 23.08.2006
 */

package eionet.gdem.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	static final int BUFFER = 2048;
	static final String MIMETYPE_FILE = "mimetype";

	public static void zipDir(String dir2zip, ZipOutputStream outZip)
			throws IOException {

		zipDir(dir2zip, outZip, dir2zip);
	}

	/**
	 * Function zips all the files in directory and subdirectories and adds these into zip file
	 *
	 * @param dir2zip - directory that will be zipped
	 * @param outZip - ZipOutputStream represents the zip file, where the files will be placed
	 * @param sourceDir - root directory, where the zipping started
	 * @throws IOException
	 */
	public static void zipDir(String dir2zip, ZipOutputStream outZip,
			String sourceDir) throws IOException {
		// create a new File object based on the directory we have to zip
		File zipDir = new File(dir2zip);
		// get a listing of the directory content
		String[] dirList = zipDir.list();

		// Set the compression ratio
		outZip.setLevel(Deflater.DEFAULT_COMPRESSION);

		//if directory contains mimetype file, then start with it
		File mimetype_file = new File(dir2zip, MIMETYPE_FILE);
		if (mimetype_file.exists()){
			zipFile(mimetype_file,outZip,sourceDir);
		}

		// loop through dirList, and zip the files
		for (int i = 0; i < dirList.length; i++) {
			// Do not zip mimetype file anymore
			if (dirList[i].equals(MIMETYPE_FILE))continue;

			File f = new File(zipDir, dirList[i]);
			if (f.isDirectory()) {
				// if the File object is a directory, call this
				// function again to add its content recursively
				String filePath = f.getPath();
				zipDir(filePath, outZip, sourceDir);
				// loop again
				continue;
			}
			//if we reached herem the f is not directory
			zipFile(f,outZip,sourceDir);
		}
	}
	/**
	 *
	 * @param f  - File that will be zipped
	 * @param outZip - ZipOutputStream represents the zip file, where the files will be placed
	 * @param sourceDir - root directory, where the zipping started
	 * @throws IOException
	 */
	public static void zipFile(File f, ZipOutputStream outZip, String sourceDir) throws IOException {

		byte[] readBuffer = new byte[BUFFER];
		int bytesIn = 0;
		// create a FileInputStream on top of file f
		FileInputStream fis = new FileInputStream(f);
		// create a new zip entry
		String strAbsPath = f.getPath();
		String strZipEntryName = strAbsPath.substring(
				sourceDir.length() + 1, strAbsPath.length());

		//make ut work on windows
		strZipEntryName = Utils.Replace(strZipEntryName, File.separator,
				"/");
		ZipEntry anEntry = new ZipEntry(strZipEntryName);
		// place the zip entry in the ZipOutputStream object
		outZip.putNextEntry(anEntry);
		// now write the content of the file to the ZipOutputStream
		while ((bytesIn = fis.read(readBuffer)) != -1) {
			outZip.write(readBuffer, 0, bytesIn);
		}
		outZip.flush();
		// Close the current entry
		outZip.closeEntry();
		// close the Stream

		fis.close();

	}
	public static void unzip(String inZip, String outDir) throws IOException {

		File sourceZipFile = new File(inZip);
		File unzipDestinationDirectory = new File(outDir);

		// Open Zip file for reading
		ZipFile zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);

		// Create an enumeration of the entries in the zip file
		Enumeration zipFileEntries = zipFile.entries();

		// Process each entry
		while (zipFileEntries.hasMoreElements()) {
			// grab a zip file entry
			ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();

			String currentEntry = entry.getName();
			//System.out.println("Extracting: " + entry);

			File destFile = new File(unzipDestinationDirectory, currentEntry);

			// grab file's parent directory structure
			File destinationParent = destFile.getParentFile();

			// create the parent directory structure if needed
			destinationParent.mkdirs();

			// extract file if not a directory
			if (!entry.isDirectory()) {
				BufferedInputStream is = new BufferedInputStream(zipFile
						.getInputStream(entry));
				int currentByte;
				// establish buffer for writing file
				byte data[] = new byte[BUFFER];

				// write the current file to disk
				FileOutputStream fos = new FileOutputStream(destFile);
				BufferedOutputStream dest = new BufferedOutputStream(fos,
						BUFFER);

				// read and write until last byte is encountered
				while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, currentByte);
				}
				dest.flush();
				dest.close();
				is.close();
			}
		}
		zipFile.close();
	}
	private static boolean getDirContainsMimeFile(String dir){
		File file = new File(dir, MIMETYPE_FILE);
		if (file.exists())
			return true;
		else
			return false;
	}
}
