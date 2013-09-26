package gr.media24.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * generates escenic content-type file, 
 * depending on the escenic folders and files structure!
 * - webapp - ... - escenic - content-type 
 * 				  - 24media - basic-content-items.xml
 * 
 * @goal content
 * 
 * @phase generate-resources
 */
public class ContentEscenicMojo extends AbstractMojo {
	
	private static final String CONTENT_TYPE_ECE_PATH = "/src/main/webapp/META-INF/escenic/publication-resources/escenic/content-type"; 
	
	/**
	 * Maven Project.
	 * @parameter expression="${project.basedir}"
	 */
	private File projectDirectory;
	
	/**
	 * @parameter generic publication resources
	 * @required
	 */
	private File publicationResources;

	/**
	 * @parameter widgets folder path
	 * @required
	 */
	private File widgetsDirectory;
	
	public ContentEscenicMojo() {
		projectDirectory = new File("");
	}

	@SuppressWarnings("resource")
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (!publicationResources.exists()) {
			throw new MojoFailureException("Publication resources, file doesn't exist");
		}
		if (!widgetsDirectory.exists()) {
			throw new MojoFailureException("Widget directory doesn't exist");
		}
		// input/output file names
		String outputFileName = projectDirectory.getAbsolutePath() + CONTENT_TYPE_ECE_PATH;
		File outputFile = new File(outputFileName);
		try {
			//create output file if not exist
			outputFile.createNewFile();
			// Create FileReader Object
			FileReader inputFileReader = new FileReader(publicationResources);
			FileWriter outputFileReader = new FileWriter(outputFileName);

			// Create Buffered/PrintWriter Objects
			BufferedReader inputStream = new BufferedReader(inputFileReader);
			PrintWriter outputStream = new PrintWriter(outputFileReader);

			// open xml
			outputStream.println("<content-types xmlns=\"http://xmlns.escenic.com/2008/content-type\" xmlns:ui=\"http://xmlns.escenic.com/2008/interface-hints\" ");
			outputStream.println("xmlns:geocode=\"http://xmlns.escenic.com/2009/studio/plugin/geocode\" xmlns:rep=\"http://xmlns.escenic.com/2009/representations\" version=\"4\">");

			outputStream.println();

			String inLine = null;

			getLog().info("Adding Publication Resources");
			while ((inLine = inputStream.readLine()) != null) {
				outputStream.println(inLine);
			}

			outputStream.println();

			List<File> widgetsFolders = Arrays.asList(widgetsDirectory.listFiles());

			Collections.sort(widgetsFolders, new Comparator<File>() {
				public int compare(File file1, File file2) {
					String file1Name = file1.getName();
					String file2Name = file2.getName();
					return file1Name.compareTo(file2Name);
				}
			});

			// write widget content-types
			for (File widget : widgetsFolders) {
				if (!widget.isDirectory()) {
					continue;
				}

				// 24 MEDIA assertion
				File widgetContentType = new File(widget.getAbsolutePath() + "/resources/" + widget.getName() + ".content-type.xml");
				
				if (!widgetContentType.exists()) {
					throw new MojoFailureException("No content-type defined for the widget " + widget.getName());
				}
				inputFileReader = new FileReader(widgetContentType);
				// Create Buffered/PrintWriter Objects
				inputStream = new BufferedReader(inputFileReader);
				getLog().info("Adding " + widget.getName() + " widget resources");
				while ((inLine = inputStream.readLine()) != null) {
					outputStream.println(inLine);
				}
			}

			// create the widget group
			getLog().info("Adding Widgets group");
			outputStream.println("<ui:group name=\"widgets\">");
			outputStream.println("<ui:label>Widgets</ui:label>");
			for (File widget : widgetsFolders) {
				if (!widget.isDirectory()) {
					continue;
				}
				outputStream.println("<ui:ref-content-type name=\"widget_" + widget.getName() + "\"/>");
			}
			outputStream.println("</ui:group>");

			// close xml
			outputStream.println();
			outputStream.println("</content-types>");

			//close streams
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			throw new MojoExecutionException(" Error generating content-type ", e);
		}

	}

	public File getPublicationResources() {
		return publicationResources;
	}

	public void setPublicationResources(File publicationResources) {
		this.publicationResources = publicationResources;
	}

	public File getWidgetsDirectory() {
		return widgetsDirectory;
	}

	public void setWidgetsDirectory(File widgetsDirectory) {
		this.widgetsDirectory = widgetsDirectory;
	}
}
